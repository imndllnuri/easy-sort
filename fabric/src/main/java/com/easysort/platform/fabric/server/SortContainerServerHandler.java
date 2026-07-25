package com.easysort.platform.fabric.server;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.fabric.network.SortContainerPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Handles the client's "sort this open container" request. Never trusts the
 * client's claim about which container is open - always re-checks against the
 * player's actual currently-open menu server-side.
 */
public final class SortContainerServerHandler {

	private SortContainerServerHandler() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(SortContainerPayload.ID, (server, player, handler, buf, responseSender) -> {
			// The buf is only valid for the duration of this callback (it runs on
			// netty's event loop, not the server thread), so it must be fully read
			// here, before scheduling the actual handling on the server thread.
			SortContainerPayload payload = SortContainerPayload.read(buf);
			server.execute(() -> handle(payload, player));
		});
	}

	private static void handle(SortContainerPayload payload, ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != payload.containerId()) {
			return;
		}
		if (!MenuContainers.isSupported(menu)) {
			return;
		}

		ContainerAdapter.sort(MenuContainers.extractContainer(menu), SortConfig.withPrimary(payload.primarySortKey()));
		menu.broadcastFullState();
	}
}
