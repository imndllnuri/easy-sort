package com.easysort.platform.fabric.server;

import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.fabric.network.QuickStackPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;

public final class QuickStackServerHandler {

	private QuickStackServerHandler() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(QuickStackPayload.TYPE, (payload, context) ->
				context.server().execute(() -> handle(payload, context.player())));
	}

	private static void handle(QuickStackPayload payload, ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != payload.containerId()) {
			return;
		}
		if (!(menu instanceof ChestMenu chestMenu)) {
			return;
		}

		ContainerAdapter.quickStack(player.getInventory(), 0, Inventory.INVENTORY_SIZE, chestMenu.getContainer());
		menu.broadcastFullState();
	}
}
