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
		ServerPlayNetworking.registerGlobalReceiver(SortPlayerInventoryPayload.ID, (server, player, handler, buf, responseSender) -> {
			// The buf is only valid for the duration of this callback (it runs on
			// netty's event loop, not the server thread), so it must be fully read
			// here, before scheduling the actual handling on the server thread.
			SortPlayerInventoryPayload payload = SortPlayerInventoryPayload.read(buf);
			server.execute(() -> handle(payload, player));
		});
	}

	private static void handle(SortPlayerInventoryPayload payload, ServerPlayer player) {
		// Sorts only the 27 main storage slots - never the hotbar (reordering
		// it mid-use would be jarring/dangerous, e.g. moving your held tool)
		// and never armor/offhand, which sit past index INVENTORY_SIZE.
		ContainerAdapter.sort(player.getInventory(), Inventory.getSelectionSize(), Inventory.INVENTORY_SIZE,
				SortConfig.withPrimary(payload.primarySortKey()));
		player.containerMenu.broadcastFullState();
	}
}
