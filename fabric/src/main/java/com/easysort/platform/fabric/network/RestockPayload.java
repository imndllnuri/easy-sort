package com.easysort.platform.fabric.network;

import com.easysort.platform.fabric.EasySort;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** "Top up my partial stacks from this open container." */
public record RestockPayload(int containerId) implements CustomPacketPayload {

	public static final Type<RestockPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(EasySort.MOD_ID, "restock"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RestockPayload> CODEC =
			StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, RestockPayload::containerId, RestockPayload::new);

	public static void register() {
		PayloadTypeRegistry.playC2S().register(TYPE, CODEC);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
