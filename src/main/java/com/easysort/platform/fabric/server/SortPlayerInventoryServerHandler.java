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
		// Inventory.getContainerSize() includes armor/offhand past index
		// INVENTORY_SIZE - bound the sort to the main 36 hotbar+storage slots
		// only, or equipped gear would get scrambled into the grid.
		ContainerAdapter.sort(player.getInventory(), 0, Inventory.INVENTORY_SIZE,
				SortConfig.withPrimary(payload.primarySortKey()));
		player.containerMenu.broadcastFullState();
	}
}
