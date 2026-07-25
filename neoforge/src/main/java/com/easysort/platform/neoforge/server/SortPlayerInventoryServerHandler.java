package com.easysort.platform.neoforge.server;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.neoforge.network.SortPlayerInventoryPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public final class SortPlayerInventoryServerHandler {

	// Inventory.SELECTION_SIZE (the hotbar size) is private at 1.20.1 - it was
	// made public in a later version. The value itself (9 hotbar slots) is a
	// stable vanilla invariant.
	private static final int HOTBAR_SIZE = 9;

	private SortPlayerInventoryServerHandler() {
	}

	public static void handle(SortPlayerInventoryPayload payload, ServerPlayer player) {
		// Sorts only the 27 main storage slots - never the hotbar (reordering
		// it mid-use would be jarring/dangerous, e.g. moving your held tool)
		// and never armor/offhand, which sit past index INVENTORY_SIZE.
		ContainerAdapter.sort(player.getInventory(), HOTBAR_SIZE, Inventory.INVENTORY_SIZE,
				SortConfig.withPrimary(payload.primarySortKey()));
		player.containerMenu.broadcastFullState();
	}
}
