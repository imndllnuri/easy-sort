package com.easysort.platform.neoforge.network;

import com.easysort.platform.neoforge.EasySortNeoForge;
import com.easysort.platform.neoforge.server.QuickStackServerHandler;
import com.easysort.platform.neoforge.server.RestockServerHandler;
import com.easysort.platform.neoforge.server.SortContainerServerHandler;
import com.easysort.platform.neoforge.server.SortPlayerInventoryServerHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 1.20.1's NeoForge (still a straight fork of Forge 47.1.x at this point -
 * see gradle.properties) predates vanilla's CustomPacketPayload/StreamCodec
 * (added 1.20.5) and NeoForge's own later payload-registration API, so this
 * uses the classic Forge SimpleChannel instead.
 */
public final class NetworkSetup {

	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(EasySortNeoForge.MOD_ID, "main"))
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.simpleChannel();

	private NetworkSetup() {
	}

	public static void register() {
		int id = 0;
		SortContainerPayload.register(CHANNEL, id++, SortContainerServerHandler::handle);
		SortPlayerInventoryPayload.register(CHANNEL, id++, SortPlayerInventoryServerHandler::handle);
		QuickStackPayload.register(CHANNEL, id++, QuickStackServerHandler::handle);
		RestockPayload.register(CHANNEL, id++, RestockServerHandler::handle);
	}
}
