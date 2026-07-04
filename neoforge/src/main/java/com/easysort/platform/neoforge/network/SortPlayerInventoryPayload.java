package com.easysort.platform.neoforge.network;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.neoforge.EasySortNeoForge;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * "Sort my own inventory" - unlike {@link SortContainerPayload}, this never
 * needs to be checked against a specific open container, since it only ever
 * touches the requesting player's own inventory regardless of what screen
 * (if any) is open.
 */
public record SortPlayerInventoryPayload(SortKey primarySortKey) implements CustomPacketPayload {

	public static final Type<SortPlayerInventoryPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(EasySortNeoForge.MOD_ID, "sort_player_inventory"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SortPlayerInventoryPayload> CODEC = StreamCodec.composite(
			SortKeyCodec.CODEC, SortPlayerInventoryPayload::primarySortKey,
			SortPlayerInventoryPayload::new);

	public static void register(PayloadRegistrar registrar, IPayloadHandler<SortPlayerInventoryPayload> handler) {
		registrar.playToServer(TYPE, CODEC, handler);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
