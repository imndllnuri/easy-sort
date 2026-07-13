package com.easysort.platform.neoforge.network;

import com.easysort.platform.neoforge.EasySortNeoForge;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** "Top up my partial stacks from this open container." */
public record RestockPayload(int containerId) implements CustomPacketPayload {

	public static final Type<RestockPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(EasySortNeoForge.MOD_ID, "restock"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RestockPayload> CODEC =
			StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, RestockPayload::containerId, RestockPayload::new);

	public static void register(PayloadRegistrar registrar, IPayloadHandler<RestockPayload> handler) {
		registrar.playToServer(TYPE, CODEC, handler);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
