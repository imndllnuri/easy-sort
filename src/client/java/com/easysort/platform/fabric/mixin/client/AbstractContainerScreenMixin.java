package com.easysort.platform.fabric.mixin.client;

import com.easysort.platform.fabric.client.config.EasySortClientConfig;
import com.easysort.platform.fabric.client.screen.EasySortConfigScreen;
import com.easysort.platform.fabric.client.widget.MiniButton;
import com.easysort.platform.fabric.network.SortContainerPayload;
import com.easysort.platform.fabric.network.SortPlayerInventoryPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a sort button to any chest-like container screen (chest, double chest,
 * barrel, ender chest all share ChestMenu). No clean Fabric API hook exists
 * for adding a widget to an existing vanilla screen, so this is one of the
 * few mixins the project needs - see ARCHITECTURE.md.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

	private static final int BUTTON_SIZE = 12;
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
	protected int titleLabelY;

	@Shadow
	protected int inventoryLabelY;

	private AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void easysort$addSortButton(CallbackInfo ci) {
		// Player-inventory sort is available on every container screen, since
		// the player's own inventory grid is always shown somewhere in one.
		int inventoryButtonY = this.topPos + this.inventoryLabelY - (BUTTON_SIZE - 8) / 2 - 1;
		MiniButton inventoryButton = easysort$button("I", "easy-sort.button.sort_inventory", 0, inventoryButtonY,
				true, () -> ClientPlayNetworking.send(
						new SortPlayerInventoryPayload(EasySortClientConfig.getPrimarySortKey())));
		this.addRenderableWidget(inventoryButton);

		// CreativeModeInventoryScreen reuses this same screen/widgets across all
		// of its item-browsing tabs (switching tabs doesn't call init() again),
		// so only the one "Inventory" tab that shows real player items should
		// keep this button visible - re-checked every frame since tab changes
		// don't fire any event we can hook once.
		ScreenEvents.beforeRender((Screen) (Object) this).register((screen, graphics, mouseX, mouseY, tickDelta) ->
				inventoryButton.visible = !(screen instanceof CreativeModeInventoryScreen)
						|| CreativeModeInventoryScreenAccessor.easysort$getSelectedTab().getType() == CreativeModeTab.Type.INVENTORY);

		if (!(this.menu instanceof ChestMenu)) {
			return;
		}

		// Vertically centered on the title row rather than a separate row,
		// matching the compact inline-with-title reference style.
		int buttonY = this.topPos + this.titleLabelY - (BUTTON_SIZE - 8) / 2 - 1;

		this.addRenderableWidget(easysort$button("S", "easy-sort.button.sort", 0, buttonY, true,
				() -> ClientPlayNetworking.send(new SortContainerPayload(this.menu.containerId,
						EasySortClientConfig.getPrimarySortKey()))));
		this.addRenderableWidget(easysort$button("G", "easy-sort.button.settings", 1, buttonY, true,
				() -> this.minecraft.setScreen(new EasySortConfigScreen((Screen) (Object) this))));
		this.addRenderableWidget(easysort$button("R", "easy-sort.button.restock", 2, buttonY, false, () -> {
		}));
		this.addRenderableWidget(easysort$button("Q", "easy-sort.button.quick_stack", 3, buttonY, false, () -> {
		}));
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
