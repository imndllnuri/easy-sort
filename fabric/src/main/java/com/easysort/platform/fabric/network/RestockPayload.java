package com.easysort.platform.fabric.network;

import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/** "Top up my partial stacks from this open container." */
public record RestockPayload(int containerId) {

	public static final ResourceLocation CHANNEL = new ResourceLocation(EasySort.MOD_ID, "restock");

	public static void encode(RestockPayload payload, FriendlyByteBuf buf) {
		buf.writeVarInt(payload.containerId());
	}

	public static RestockPayload decode(FriendlyByteBuf buf) {
		return new RestockPayload(buf.readVarInt());
	}
}
