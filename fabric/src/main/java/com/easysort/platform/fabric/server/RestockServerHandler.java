package com.easysort.platform.fabric.server;

import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.fabric.network.RestockPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class RestockServerHandler {

	private RestockServerHandler() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(RestockPayload.ID, (server, player, handler, buf, responseSender) -> {
			// The buf is only valid for the duration of this callback (it runs on
			// netty's event loop, not the server thread), so it must be fully read
			// here, before scheduling the actual handling on the server thread.
			RestockPayload payload = RestockPayload.read(buf);
			server.execute(() -> handle(payload, player));
		});
	}

	private static void handle(RestockPayload payload, ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != payload.containerId()) {
			return;
		}
		if (!MenuContainers.isSupported(menu)) {
			return;
		}

		ContainerAdapter.restock(MenuContainers.extractContainer(menu), player.getInventory(), 0, Inventory.INVENTORY_SIZE);
		menu.broadcastFullState();
	}
}
