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
 */
public record SortPlayerInventoryPayload(SortKey primarySortKey) {

	public static final ResourceLocation CHANNEL = new ResourceLocation(EasySort.MOD_ID, "sort_player_inventory");

	public static void encode(SortPlayerInventoryPayload payload, FriendlyByteBuf buf) {
		SortKeyCodec.encode(buf, payload.primarySortKey());
	}

	public static SortPlayerInventoryPayload decode(FriendlyByteBuf buf) {
		return new SortPlayerInventoryPayload(SortKeyCodec.decode(buf));
	}
}
