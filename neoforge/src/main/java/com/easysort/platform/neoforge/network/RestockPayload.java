package com.easysort.platform.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** "Top up my partial stacks from this open container." */
public record RestockPayload(int containerId) {

	public void send() {
		NetworkSetup.CHANNEL.sendToServer(this);
	}

	static void encode(RestockPayload payload, FriendlyByteBuf buf) {
		buf.writeVarInt(payload.containerId());
	}

	static RestockPayload decode(FriendlyByteBuf buf) {
		return new RestockPayload(buf.readVarInt());
	}

	public static void register(SimpleChannel channel, int id, BiConsumer<RestockPayload, ServerPlayer> handler) {
		channel.registerMessage(id, RestockPayload.class, RestockPayload::encode, RestockPayload::decode,
				(payload, contextSupplier) -> handle(payload, contextSupplier, handler));
	}

	private static void handle(RestockPayload payload, Supplier<NetworkEvent.Context> contextSupplier,
			BiConsumer<RestockPayload, ServerPlayer> handler) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handler.accept(payload, context.getSender()));
		context.setPacketHandled(true);
	}
}
