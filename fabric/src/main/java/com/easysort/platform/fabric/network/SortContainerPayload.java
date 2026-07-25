package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Fabric's typed CustomPayload/PayloadTypeRegistry API is a 1.20.5+ addition -
 * on 1.19.2 (fabric-networking-api-v1 1.2.x), channels are plain
 * ResourceLocation-keyed raw FriendlyByteBuf packets instead.
 */
public record SortContainerPayload(int containerId, SortKey primarySortKey) {

	public static final ResourceLocation ID = new ResourceLocation(EasySort.MOD_ID, "sort_container");

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(containerId);
		SortKeyCodec.write(buf, primarySortKey);
	}

	public static SortContainerPayload read(FriendlyByteBuf buf) {
		int containerId = buf.readVarInt();
		SortKey primarySortKey = SortKeyCodec.read(buf);
		return new SortContainerPayload(containerId, primarySortKey);
	}
}
