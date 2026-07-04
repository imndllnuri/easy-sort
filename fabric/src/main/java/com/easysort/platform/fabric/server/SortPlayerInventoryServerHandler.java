package com.easysort.platform.fabric.server;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.fabric.network.SortPlayerInventoryPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public final class SortPlayerInventoryServerHandler {

	private SortPlayerInventoryServerHandler() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(SortPlayerInventoryPayload.TYPE, (payload, context) ->
				context.server().execute(() -> handle(payload, context.player())));
	}

	private static void handle(SortPlayerInventoryPayload payload, ServerPlayer player) {
		// Sorts only the 27 main storage slots - never the hotbar (reordering
		// it mid-use would be jarring/dangerous, e.g. moving your held tool)
		// and never armor/offhand, which sit past index INVENTORY_SIZE.
		ContainerAdapter.sort(player.getInventory(), Inventory.SELECTION_SIZE, Inventory.INVENTORY_SIZE,
				SortConfig.withPrimary(payload.primarySortKey()));
		player.containerMenu.broadcastFullState();
	}
}
