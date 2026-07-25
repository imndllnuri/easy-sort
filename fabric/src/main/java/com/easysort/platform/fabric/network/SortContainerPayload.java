package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * 1.20.1 predates vanilla's CustomPacketPayload/StreamCodec (added 1.20.5), so
 * this uses Fabric API's classic channel-based networking instead: a fixed
 * channel id plus hand-written FriendlyByteBuf encode/decode. Deliberately
 * has no client-only dependency (e.g. ClientPlayNetworking) - this class
 * lives in the shared `main` source set, not the client-only one; see
 * client.network.ClientNetworking for the actual send-from-client call.
 */
public record SortContainerPayload(int containerId, SortKey primarySortKey) {

	public static final ResourceLocation CHANNEL = new ResourceLocation(EasySort.MOD_ID, "sort_container");

	public static void encode(SortContainerPayload payload, FriendlyByteBuf buf) {
		buf.writeVarInt(payload.containerId());
		SortKeyCodec.encode(buf, payload.primarySortKey());
	}

	public static SortContainerPayload decode(FriendlyByteBuf buf) {
		int containerId = buf.readVarInt();
		SortKey key = SortKeyCodec.decode(buf);
		return new SortContainerPayload(containerId, key);
	}
}
