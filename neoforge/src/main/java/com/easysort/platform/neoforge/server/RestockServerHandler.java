package com.easysort.platform.neoforge.server;

import com.easysort.platform.common.ContainerAdapter;
import com.easysort.platform.common.MenuContainers;
import com.easysort.platform.neoforge.network.RestockPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class RestockServerHandler {

	private RestockServerHandler() {
	}

	public static void handle(RestockPayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
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
