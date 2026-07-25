package com.easysort.platform.fabric.network;

import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/** "Move matching items from my inventory into this open container." */
public record QuickStackPayload(int containerId) {

	public static final ResourceLocation CHANNEL = new ResourceLocation(EasySort.MOD_ID, "quick_stack");

	public static void encode(QuickStackPayload payload, FriendlyByteBuf buf) {
		buf.writeVarInt(payload.containerId());
	}

	public static QuickStackPayload decode(FriendlyByteBuf buf) {
		return new QuickStackPayload(buf.readVarInt());
	}
}
