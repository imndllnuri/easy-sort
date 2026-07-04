package com.easysort.platform.neoforge.network;

import com.easysort.platform.neoforge.server.QuickStackServerHandler;
import com.easysort.platform.neoforge.server.RestockServerHandler;
import com.easysort.platform.neoforge.server.SortContainerServerHandler;
import com.easysort.platform.neoforge.server.SortPlayerInventoryServerHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class NetworkSetup {

	private NetworkSetup() {
	}

	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		SortContainerPayload.register(registrar, SortContainerServerHandler::handle);
		SortPlayerInventoryPayload.register(registrar, SortPlayerInventoryServerHandler::handle);
		QuickStackPayload.register(registrar, QuickStackServerHandler::handle);
		RestockPayload.register(registrar, RestockServerHandler::handle);
	}
}
