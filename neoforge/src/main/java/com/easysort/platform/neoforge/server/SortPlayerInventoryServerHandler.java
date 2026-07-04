package com.easysort.platform.neoforge.server;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.neoforge.network.SortPlayerInventoryPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class SortPlayerInventoryServerHandler {

	private SortPlayerInventoryServerHandler() {
	}

	public static void handle(SortPlayerInventoryPayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
		// Sorts only the 27 main storage slots - never the hotbar (reordering
		// it mid-use would be jarring/dangerous, e.g. moving your held tool)
		// and never armor/offhand, which sit past index INVENTORY_SIZE.
		ContainerAdapter.sort(player.getInventory(), Inventory.SELECTION_SIZE, Inventory.INVENTORY_SIZE,
				SortConfig.withPrimary(payload.primarySortKey()));
		player.containerMenu.broadcastFullState();
	}
}
