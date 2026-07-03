package com.easysort.platform.fabric.mixin.client;

import com.easysort.platform.fabric.network.SortContainerPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
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

	private static final int BUTTON_SIZE = 18;
	private static final int BUTTON_GAP = 2;

	@Shadow
	@Final
	protected AbstractContainerMenu menu;

	@Shadow
	protected int leftPos;

	@Shadow
	protected int topPos;

	@Shadow
	protected int imageWidth;

	private AbstractContainerScreenMixin(Component title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void easysort$addSortButton(CallbackInfo ci) {
		if (!(this.menu instanceof ChestMenu)) {
			return;
		}

		// Inside the panel, just under the top border - previously this sat
		// above the window entirely. Exact position/order is a placeholder
		// pending a reference image.
		int buttonY = this.topPos + BUTTON_GAP;

		this.addRenderableWidget(Button.builder(Component.literal("S"), button ->
						ClientPlayNetworking.send(new SortContainerPayload(this.menu.containerId)))
				.tooltip(Tooltip.create(Component.translatable("easy-sort.button.sort")))
				.bounds(easysort$buttonX(0), buttonY, BUTTON_SIZE, BUTTON_SIZE)
				.build());

		this.addRenderableWidget(easysort$placeholderButton("G", "easy-sort.button.settings", 1, buttonY));
		this.addRenderableWidget(easysort$placeholderButton("R", "easy-sort.button.restock", 2, buttonY));
		this.addRenderableWidget(easysort$placeholderButton("Q", "easy-sort.button.quick_stack", 3, buttonY));
	}

	// Not wired to any behavior yet - disabled so it reads as "coming soon"
	// rather than a button that silently does nothing when clicked.
	private Button easysort$placeholderButton(String glyph, String tooltipKey, int indexFromRight, int buttonY) {
		Button button = Button.builder(Component.literal(glyph), button2 -> {
					})
				.tooltip(Tooltip.create(Component.translatable(tooltipKey)))
				.bounds(easysort$buttonX(indexFromRight), buttonY, BUTTON_SIZE, BUTTON_SIZE)
				.build();
		button.active = false;
		return button;
	}

	// indexFromRight 0 is the rightmost button; higher indices sit further left,
	// so additional buttons can be prepended without touching this one's position.
	private int easysort$buttonX(int indexFromRight) {
		return this.leftPos + this.imageWidth - (indexFromRight + 1) * (BUTTON_SIZE + BUTTON_GAP);
	}
}
