package com.easysort.platform.neoforge.network;

import com.easysort.platform.neoforge.EasySortNeoForge;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** "Move matching items from my inventory into this open container." */
public record QuickStackPayload(int containerId) implements CustomPacketPayload {

	public static final Type<QuickStackPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(EasySortNeoForge.MOD_ID, "quick_stack"));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuickStackPayload> CODEC =
			StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, QuickStackPayload::containerId, QuickStackPayload::new);

	public static void register(PayloadRegistrar registrar, IPayloadHandler<QuickStackPayload> handler) {
		registrar.playToServer(TYPE, CODEC, handler);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
