package com.easysort.platform.neoforge.mixin.client;

import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.neoforge.client.config.EasySortClientConfig;
import com.easysort.platform.neoforge.client.screen.EasySortConfigScreen;
import com.easysort.platform.neoforge.client.widget.MiniButton;
import com.easysort.platform.neoforge.network.QuickStackPayload;
import com.easysort.platform.neoforge.network.RestockPayload;
import com.easysort.platform.neoforge.network.SortContainerPayload;
import com.easysort.platform.neoforge.network.SortPlayerInventoryPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the sort/settings/restock/quick-stack button row to any screen backed
 * by a supported storage menu (see MenuContainers) - chest, double chest,
 * barrel, ender chest, minecart with chest, and shulker box today. No clean
 * NeoForge event exists for adding a widget to an existing vanilla screen, so
 * this is one of the few mixins the project needs - see ARCHITECTURE.md.
 *
 * Mirrors the Fabric platform's AbstractContainerScreenMixin exactly in
 * intent; the "I" button's Creative-tab visibility is re-checked via a
 * second inject at render() HEAD (scoped to this screen instance, no global
 * listener/leak) instead of Fabric API's per-screen ScreenEvents.beforeRender,
 * since AbstractWidget.render() itself is final and can't be overridden to
 * self-recompute visibility.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

	private static final int BUTTON_SIZE = 10;
	private static final int BUTTON_GAP = 1;
	private static final int RIGHT_MARGIN = 7;

	@Shadow
	@Final
	protected AbstractContainerMenu menu;

	@Shadow
	protected int leftPos;

	@Shadow
	protected int topPos;

	@Shadow
	protected int imageWidth;

	@Shadow
	protected int imageHeight;

	@Shadow
	protected int titleLabelY;

	@Unique
	private MiniButton easysort$inventoryButton;

	private AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void easysort$addSortButton(CallbackInfo ci) {
		// Player-inventory sort is available on every container screen, since
		// the player's own inventory grid is always shown somewhere in one.
		// Derived from imageHeight ourselves rather than trusting the vanilla
		// inventoryLabelY field directly: ShulkerBoxScreen bumps imageHeight
		// by 1 in its own constructor (after the base AbstractContainerScreen
		// constructor already computed inventoryLabelY from the un-bumped
		// value) without recomputing it, unlike ContainerScreen (chest) which
		// does - so on shulker boxes that field is stale by a pixel.
		int inventoryLabelY = this.imageHeight - 94;
		int inventoryButtonY = this.topPos + inventoryLabelY - (BUTTON_SIZE - 8) / 2 - 1;
		easysort$inventoryButton = easysort$button("I", "easy-sort.button.sort_inventory", 0, inventoryButtonY,
				true, () -> ClientPacketDistributor.sendToServer(
						new SortPlayerInventoryPayload(EasySortClientConfig.getPrimarySortKey())));
		this.addRenderableWidget(easysort$inventoryButton);

		if (!MenuContainers.isSupported(this.menu)) {
			return;
		}

		// Vertically centered on the title row rather than a separate row,
		// matching the compact inline-with-title reference style.
		int buttonY = this.topPos + this.titleLabelY - (BUTTON_SIZE - 8) / 2 - 1;

		// Left-to-right reading order: S, Q, R, G (indexFromRight counts from
		// the right edge, so the rightmost button - G - is index 0).
		this.addRenderableWidget(easysort$button("G", "easy-sort.button.settings", 0, buttonY, true,
				() -> this.minecraft.setScreen(new EasySortConfigScreen((Screen) (Object) this))));
		this.addRenderableWidget(easysort$button("R", "easy-sort.button.restock", 1, buttonY, true,
				() -> ClientPacketDistributor.sendToServer(new RestockPayload(this.menu.containerId))));
		this.addRenderableWidget(easysort$button("Q", "easy-sort.button.quick_stack", 2, buttonY, true,
				() -> ClientPacketDistributor.sendToServer(new QuickStackPayload(this.menu.containerId))));
		this.addRenderableWidget(easysort$button("S", "easy-sort.button.sort", 3, buttonY, true,
				() -> ClientPacketDistributor.sendToServer(new SortContainerPayload(this.menu.containerId,
						EasySortClientConfig.getPrimarySortKey()))));
	}

	// CreativeModeInventoryScreen reuses this same screen/widgets across all
	// of its item-browsing tabs (switching tabs doesn't call init() again),
	// so only the one "Inventory" tab that shows real player items should
	// keep this button visible - re-checked every frame since tab changes
	// don't fire any event we can hook once.
	@Inject(method = "render", at = @At("HEAD"))
	private void easysort$updateInventoryButtonVisibility(GuiGraphics graphics, int mouseX, int mouseY,
			float partialTick, CallbackInfo ci) {
		easysort$inventoryButton.visible = !((Object) this instanceof CreativeModeInventoryScreen)
				|| CreativeModeInventoryScreenAccessor.easysort$getSelectedTab().getType() == CreativeModeTab.Type.INVENTORY;
	}

	private MiniButton easysort$button(String glyph, String tooltipKey, int indexFromRight, int buttonY,
			boolean enabled, Runnable onPress) {
		MiniButton button = new MiniButton(easysort$buttonX(indexFromRight), buttonY, BUTTON_SIZE,
				Component.literal(glyph), onPress);
		button.setTooltip(Tooltip.create(Component.translatable(tooltipKey)));
		button.active = enabled;
		return button;
	}

	// indexFromRight 0 is the rightmost button; higher indices sit further left,
	// so additional buttons can be prepended without touching this one's position.
	private int easysort$buttonX(int indexFromRight) {
		return this.leftPos + this.imageWidth - RIGHT_MARGIN - (indexFromRight + 1) * (BUTTON_SIZE + BUTTON_GAP);
	}
}
