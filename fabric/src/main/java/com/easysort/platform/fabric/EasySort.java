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
		// Unlike this project's later unobfuscated 26.x-era branches, 1.19.2's
		// raw-channel networking API (fabric-networking-api-v1 1.2.x) has no
		// separate payload-type registration step - registerGlobalReceiver()
		// on the server handler is the only registration needed.
		SortContainerServerHandler.register();
		SortPlayerInventoryServerHandler.register();
		QuickStackServerHandler.register();
		RestockServerHandler.register();
	}
}
