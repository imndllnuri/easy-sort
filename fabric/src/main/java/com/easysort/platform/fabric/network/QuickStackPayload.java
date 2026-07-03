package com.easysort.platform.fabric.network;

import com.easysort.platform.fabric.EasySort;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** "Move matching items from my inventory into this open container." */
public record QuickStackPayload(int containerId) implements CustomPacketPayload {

	public static final Type<QuickStackPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(EasySort.MOD_ID, "quick_stack"));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuickStackPayload> CODEC =
			StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, QuickStackPayload::containerId, QuickStackPayload::new);

	public static void register() {
		PayloadTypeRegistry.playC2S().register(TYPE, CODEC);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
