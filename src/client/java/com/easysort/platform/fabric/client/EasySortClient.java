package com.easysort.platform.fabric.client;

import com.easysort.platform.fabric.network.SortPlayerInventoryPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.lwjgl.glfw.GLFW;

public final class EasySortClient implements ClientModInitializer {

	private static final KeyMapping SORT_INVENTORY_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyMapping("key.easy-sort.sort_inventory", GLFW.GLFW_KEY_R, KeyMapping.Category.INVENTORY));

	@Override
	public void onInitializeClient() {
		// Only fires while a container-type screen is open, matching where the
		// in-screen sort buttons live - not a fully global hotkey (yet).
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (SORT_INVENTORY_KEY.consumeClick()) {
				if (client.screen instanceof AbstractContainerScreen<?>) {
					ClientPlayNetworking.send(new SortPlayerInventoryPayload());
				}
			}
		});
	}
}
