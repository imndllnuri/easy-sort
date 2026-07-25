package com.easysort.platform.fabric.server;

import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.fabric.network.QuickStackPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class QuickStackServerHandler {

	private QuickStackServerHandler() {
	}

	public static void register() {
		ServerPlayNetworking.registerGlobalReceiver(QuickStackPayload.ID, (server, player, handler, buf, responseSender) -> {
			// The buf is only valid for the duration of this callback (it runs on
			// netty's event loop, not the server thread), so it must be fully read
			// here, before scheduling the actual handling on the server thread.
			QuickStackPayload payload = QuickStackPayload.read(buf);
			server.execute(() -> handle(payload, player));
		});
	}

	private static void handle(QuickStackPayload payload, ServerPlayer player) {
		AbstractContainerMenu menu = player.containerMenu;
		if (menu.containerId != payload.containerId()) {
			return;
		}
		if (!MenuContainers.isSupported(menu)) {
			return;
		}

		ContainerAdapter.quickStack(player.getInventory(), 0, Inventory.INVENTORY_SIZE, MenuContainers.extractContainer(menu));
		menu.broadcastFullState();
	}
}
