package com.easysort.platform.neoforge.network;

import com.easysort.core.sort.SortKey;
import com.easysort.platform.neoforge.EasySortNeoForge;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public record SortContainerPayload(int containerId, SortKey primarySortKey) implements CustomPacketPayload {

	public static final Type<SortContainerPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(EasySortNeoForge.MOD_ID, "sort_container"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SortContainerPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.CONTAINER_ID, SortContainerPayload::containerId,
			SortKeyCodec.CODEC, SortContainerPayload::primarySortKey,
			SortContainerPayload::new);

	public static void register(PayloadRegistrar registrar, IPayloadHandler<SortContainerPayload> handler) {
		registrar.playToServer(TYPE, CODEC, handler);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
