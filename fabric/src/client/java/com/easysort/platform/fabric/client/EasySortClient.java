package com.easysort.platform.fabric.client;

import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.fabric.client.config.EasySortClientConfig;
import com.easysort.platform.fabric.client.network.PayloadSending;
import com.easysort.platform.fabric.network.SortContainerPayload;
import com.easysort.platform.fabric.network.SortPlayerInventoryPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.lwjgl.glfw.GLFW;

public final class EasySortClient implements ClientModInitializer {

	// KeyMapping.Category is a Minecraft 1.21.9+ addition - on 1.19.2 a
	// KeyMapping's category is just a plain string, auto-registered the first
	// time a KeyMapping using it is constructed, and used directly as the
	// translation key (see the existing "key.category.easy-sort.easy_sort"
	// entry in lang/en_us.json).
	private static final String CATEGORY = "key.category.easy-sort.easy_sort";

	private static final KeyMapping SORT_INVENTORY_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyMapping("key.easy-sort.sort_inventory", GLFW.GLFW_KEY_R, CATEGORY));

	// Unbound by default - "S" is movement, and there's no other obviously
	// safe default key for "sort the container I'm looking at".
	private static final KeyMapping SORT_CONTAINER_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyMapping("key.easy-sort.sort_container", GLFW.GLFW_KEY_UNKNOWN, CATEGORY));

	@Override
	public void onInitializeClient() {
		EasySortClientConfig.load();

		// Both only fire while a container-type screen is open, matching where
		// the in-screen sort buttons live - not a fully global hotkey (yet).
		//
		// KeyMapping.consumeClick() (the previous approach here) is backed by
		// KeyMapping.click(), which vanilla only calls when Minecraft.screen
		// == null - it never fires while any Screen is open, so it could
		// never work for these. Hooking the per-screen keyboard event instead
		// and matching key presses directly against the KeyMapping actually
		// works while a container screen is open.
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
				return;
			}
			ScreenKeyboardEvents.afterKeyPress(screen).register((s, key, scancode, modifiers) -> {
				if (SORT_INVENTORY_KEY.matches(key, scancode)) {
					PayloadSending.sendToServer(SortPlayerInventoryPayload.ID,
							new SortPlayerInventoryPayload(EasySortClientConfig.getPrimarySortKey())::write);
				}
				if (SORT_CONTAINER_KEY.matches(key, scancode) && MenuContainers.isSupported(containerScreen.getMenu())) {
					PayloadSending.sendToServer(SortContainerPayload.ID,
							new SortContainerPayload(containerScreen.getMenu().containerId,
									EasySortClientConfig.getPrimarySortKey())::write);
				}
			});
		});
	}
}
