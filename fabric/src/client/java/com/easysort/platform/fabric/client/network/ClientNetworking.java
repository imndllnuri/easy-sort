package com.easysort.platform.fabric.client.network;

import com.easysort.platform.fabric.network.QuickStackPayload;
import com.easysort.platform.fabric.network.RestockPayload;
import com.easysort.platform.fabric.network.SortContainerPayload;
import com.easysort.platform.fabric.network.SortPlayerInventoryPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Sends payloads to the server. Deliberately kept in the client-only source
 * set (unlike the payload records themselves, which live in `main` and stay
 * free of client-only Fabric API types like ClientPlayNetworking) - see
 * network.SortContainerPayload and friends.
 */
public final class ClientNetworking {

	private ClientNetworking() {
	}

	public static void send(SortContainerPayload payload) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		SortContainerPayload.encode(payload, buf);
		ClientPlayNetworking.send(SortContainerPayload.CHANNEL, buf);
	}

	public static void send(SortPlayerInventoryPayload payload) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		SortPlayerInventoryPayload.encode(payload, buf);
		ClientPlayNetworking.send(SortPlayerInventoryPayload.CHANNEL, buf);
	}

	public static void send(QuickStackPayload payload) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		QuickStackPayload.encode(payload, buf);
		ClientPlayNetworking.send(QuickStackPayload.CHANNEL, buf);
	}

	public static void send(RestockPayload payload) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		RestockPayload.encode(payload, buf);
		ClientPlayNetworking.send(RestockPayload.CHANNEL, buf);
	}
}
