package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * "Sort my own inventory" - unlike {@link SortContainerPayload}, this never
 * needs to be checked against a specific open container, since it only ever
 * touches the requesting player's own inventory regardless of what screen
 * (if any) is open.
 *
 * <p>Fabric's typed CustomPayload/PayloadTypeRegistry API is a 1.20.5+
 * addition - on 1.19.2 (fabric-networking-api-v1 1.2.x), channels are plain
 * ResourceLocation-keyed raw FriendlyByteBuf packets instead.
 */
public record SortPlayerInventoryPayload(SortKey primarySortKey) {

	public static final ResourceLocation ID = new ResourceLocation(EasySort.MOD_ID, "sort_player_inventory");

	public void write(FriendlyByteBuf buf) {
		SortKeyCodec.write(buf, primarySortKey);
	}

	public static SortPlayerInventoryPayload read(FriendlyByteBuf buf) {
		return new SortPlayerInventoryPayload(SortKeyCodec.read(buf));
	}
}
