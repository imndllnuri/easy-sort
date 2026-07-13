package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.fabric.EasySort;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SortContainerPayload(int containerId, SortKey primarySortKey) implements CustomPacketPayload {

	public static final Type<SortContainerPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(EasySort.MOD_ID, "sort_container"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SortContainerPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.CONTAINER_ID, SortContainerPayload::containerId,
			SortKeyCodec.CODEC, SortContainerPayload::primarySortKey,
			SortContainerPayload::new);

	public static void register() {
		PayloadTypeRegistry.playC2S().register(TYPE, CODEC);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
