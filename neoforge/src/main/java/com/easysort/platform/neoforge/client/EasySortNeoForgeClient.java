package com.easysort.platform.neoforge.client;

import com.easysort.platform.neoforge.client.config.EasySortClientConfig;
import com.easysort.platform.neoforge.network.SortPlayerInventoryPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

/**
 * Client-only setup. Only ever touched from a Dist.CLIENT-gated branch in
 * EasySortNeoForge's constructor, so this class (and the client-only
 * NeoForge/vanilla classes it references) never loads on a dedicated server.
 */
public final class EasySortNeoForgeClient {

	private static KeyMapping sortInventoryKey;

	private EasySortNeoForgeClient() {
	}

	public static void init(IEventBus modEventBus) {
		EasySortClientConfig.load();
		modEventBus.addListener(EasySortNeoForgeClient::registerKeyMappings);
		NeoForge.EVENT_BUS.addListener(EasySortNeoForgeClient::onClientTick);
	}

	private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		sortInventoryKey = new KeyMapping("key.easy-sort.sort_inventory", GLFW.GLFW_KEY_R, KeyMapping.Category.INVENTORY);
		event.register(sortInventoryKey);
	}

	// Only fires while a container-type screen is open, matching where the
	// in-screen sort buttons live - not a fully global hotkey (yet).
	private static void onClientTick(ClientTickEvent.Post event) {
		Minecraft client = Minecraft.getInstance();
		while (sortInventoryKey.consumeClick()) {
			if (client.screen instanceof AbstractContainerScreen<?>) {
				ClientPacketDistributor.sendToServer(new SortPlayerInventoryPayload(EasySortClientConfig.getPrimarySortKey()));
			}
		}
	}
}
