package com.easysort.platform.fabric.mixin.client;

import com.easysort.platform.fabric.network.SortContainerPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.components.Button;
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

		int buttonSize = 18;
		int buttonX = this.leftPos + this.imageWidth - buttonSize;
		int buttonY = this.topPos - buttonSize - 2;

		this.addRenderableWidget(Button.builder(Component.translatable("easy-sort.button.sort"), button ->
						ClientPlayNetworking.send(new SortContainerPayload(this.menu.containerId)))
				.bounds(buttonX, buttonY, buttonSize, buttonSize)
				.build());
	}
}
