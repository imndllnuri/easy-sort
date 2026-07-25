package com.easysort.platform.fabric;

import com.easysort.platform.fabric.server.QuickStackServerHandler;
import com.easysort.platform.fabric.server.RestockServerHandler;
import com.easysort.platform.fabric.server.SortContainerServerHandler;
import com.easysort.platform.fabric.server.SortPlayerInventoryServerHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EasySort implements ModInitializer {
	public static final String MOD_ID = "easy-sort";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Easy Sort initializing");
		// 1.20.1's classic channel-based Fabric networking (unlike the
		// CustomPacketPayload-based API from 1.20.5+) has no separate
		// "payload type" registration step - only the server-side listener
		// registration below is needed.
		SortContainerServerHandler.register();
		SortPlayerInventoryServerHandler.register();
		QuickStackServerHandler.register();
		RestockServerHandler.register();
	}
}
