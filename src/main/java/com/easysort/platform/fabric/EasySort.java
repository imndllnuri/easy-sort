package com.easysort.platform.fabric;

import com.easysort.platform.fabric.network.QuickStackPayload;
import com.easysort.platform.fabric.network.RestockPayload;
import com.easysort.platform.fabric.network.SortContainerPayload;
import com.easysort.platform.fabric.network.SortPlayerInventoryPayload;
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
		SortContainerPayload.register();
		SortContainerServerHandler.register();
		SortPlayerInventoryPayload.register();
		SortPlayerInventoryServerHandler.register();
		QuickStackPayload.register();
		QuickStackServerHandler.register();
		RestockPayload.register();
		RestockServerHandler.register();
	}
}
