package com.easysort.platform.fabric.server;

import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.fabric.network.RestockPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;

public final class RestockServerHandler {

	private RestockServerHandler() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(RestockPayload.TYPE, (payload, context) ->
				context.server().execute(() -> handle(payload, context.player())));
	}

	private static void handle(RestockPayload payload, ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != payload.containerId()) {
			return;
		}
		if (!(menu instanceof ChestMenu chestMenu)) {
			return;
		}

		ContainerAdapter.restock(chestMenu.getContainer(), player.getInventory(), 0, Inventory.INVENTORY_SIZE);
		menu.broadcastFullState();
	}
}
