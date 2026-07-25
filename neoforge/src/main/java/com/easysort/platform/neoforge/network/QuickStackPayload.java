package com.easysort.platform.neoforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/** "Move matching items from my inventory into this open container." */
public record QuickStackPayload(int containerId) {

	public void send() {
		NetworkSetup.CHANNEL.sendToServer(this);
	}

	static void encode(QuickStackPayload payload, FriendlyByteBuf buf) {
		buf.writeVarInt(payload.containerId());
	}

	static QuickStackPayload decode(FriendlyByteBuf buf) {
		return new QuickStackPayload(buf.readVarInt());
	}

	public static void register(SimpleChannel channel, int id, BiConsumer<QuickStackPayload, ServerPlayer> handler) {
		channel.registerMessage(id, QuickStackPayload.class, QuickStackPayload::encode, QuickStackPayload::decode,
				(payload, contextSupplier) -> handle(payload, contextSupplier, handler));
	}

	private static void handle(QuickStackPayload payload, Supplier<NetworkEvent.Context> contextSupplier,
			BiConsumer<QuickStackPayload, ServerPlayer> handler) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handler.accept(payload, context.getSender()));
		context.setPacketHandled(true);
	}
}
