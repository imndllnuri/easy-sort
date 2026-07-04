package com.easysort.platform.neoforge.server;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.neoforge.network.SortContainerPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Handles the client's "sort this open container" request. Never trusts the
 * client's claim about which container is open - always re-checks against the
 * player's actual currently-open menu server-side.
 */
public final class SortContainerServerHandler {

	private SortContainerServerHandler() {
	}

	public static void handle(SortContainerPayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
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
