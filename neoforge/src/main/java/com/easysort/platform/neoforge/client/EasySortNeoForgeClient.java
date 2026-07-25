package com.easysort.platform.neoforge.client;

import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.neoforge.client.config.EasySortClientConfig;
import com.easysort.platform.neoforge.network.SortContainerPayload;
import com.easysort.platform.neoforge.network.SortPlayerInventoryPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only setup. Only ever touched from a Dist.CLIENT-gated branch in
 * EasySortNeoForge's constructor, so this class (and the client-only
 * NeoForge/vanilla classes it references) never loads on a dedicated server.
 */
public final class EasySortNeoForgeClient {

	// 1.20.1 predates KeyMapping.Category (a later addition) - the category
	// is just a plain translation-key string here, matching vanilla's own
	// "key.categories.*" convention.
	private static final String CATEGORY = "key.categories.easysort";

	private static KeyMapping sortInventoryKey;
	private static KeyMapping sortContainerKey;

	private EasySortNeoForgeClient() {
	}

	public static void init(IEventBus modEventBus) {
		EasySortClientConfig.load();
		modEventBus.addListener(EasySortNeoForgeClient::registerKeyMappings);
		MinecraftForge.EVENT_BUS.addListener(EasySortNeoForgeClient::onScreenKeyPressed);
	}

	private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		sortInventoryKey = new KeyMapping("key.easy-sort.sort_inventory", GLFW.GLFW_KEY_R, CATEGORY);
		event.register(sortInventoryKey);

		// Unbound by default - "S" is movement, and there's no other obviously
		// safe default key for "sort the container I'm looking at".
		sortContainerKey = new KeyMapping("key.easy-sort.sort_container", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
		event.register(sortContainerKey);
	}

	// Both only fire while a container-type screen is open, matching where the
	// in-screen sort buttons live - not a fully global hotkey (yet).
	//
	// KeyMapping.consumeClick() (the previous approach here) is backed by
	// KeyMapping.click(), which vanilla only calls when Minecraft.screen ==
	// null - it never fires while any Screen is open, so it could never work
	// for these. Hooking the screen keyboard event instead and matching key
	// presses directly against the KeyMapping actually works while a
	// container screen is open.
	private static void onScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
		if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen)) {
			return;
		}
		if (sortInventoryKey.matches(event.getKeyCode(), event.getScanCode())) {
			new SortPlayerInventoryPayload(EasySortClientConfig.getPrimarySortKey()).send();
		}
		if (sortContainerKey.matches(event.getKeyCode(), event.getScanCode()) && MenuContainers.isSupported(containerScreen.getMenu())) {
			new SortContainerPayload(containerScreen.getMenu().containerId, EasySortClientConfig.getPrimarySortKey()).send();
		}
	}
}
