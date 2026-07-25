package com.easysort.platform.fabric.network;

import com.easysort.platform.fabric.EasySort;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * "Move matching items from my inventory into this open container."
 *
 * <p>Fabric's typed CustomPayload/PayloadTypeRegistry API is a 1.20.5+
 * addition - on 1.19.2 (fabric-networking-api-v1 1.2.x), channels are plain
 * ResourceLocation-keyed raw FriendlyByteBuf packets instead.
 */
public record QuickStackPayload(int containerId) {

	public static final ResourceLocation ID = new ResourceLocation(EasySort.MOD_ID, "quick_stack");

	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(containerId);
	}

	public static QuickStackPayload read(FriendlyByteBuf buf) {
		return new QuickStackPayload(buf.readVarInt());
	}
}
