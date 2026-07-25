package com.easysort.platform.fabric.network;

import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * "Top up my partial stacks from this open container."
 *
 * <p>Fabric's typed CustomPayload/PayloadTypeRegistry API is a 1.20.5+
 * addition - on 1.19.2 (fabric-networking-api-v1 1.2.x), channels are plain
 * ResourceLocation-keyed raw FriendlyByteBuf packets instead.
 */
public record RestockPayload(int containerId) {

	public static final ResourceLocation ID = new ResourceLocation(EasySort.MOD_ID, "restock");

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(containerId);
	}

	public static RestockPayload read(FriendlyByteBuf buf) {
		return new RestockPayload(buf.readVarInt());
	}
}
