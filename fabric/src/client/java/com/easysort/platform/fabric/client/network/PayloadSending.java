package com.easysort.platform.fabric.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Tiny helper around Fabric's raw-channel client networking API. The typed
 * CustomPayload/PayloadTypeRegistry API used on this project's later
 * unobfuscated 26.x-era branches is a Minecraft 1.20.5+ addition - on 1.19.2
 * (fabric-networking-api-v1 1.2.x), a channel packet is just a plain
 * ResourceLocation name plus a raw FriendlyByteBuf the caller fills in.
 */
public final class PayloadSending {

	private PayloadSending() {
	}

	public static void sendToServer(ResourceLocation channel, Consumer<FriendlyByteBuf> writer) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		writer.accept(buf);
		ClientPlayNetworking.send(channel, buf);
	}
}
