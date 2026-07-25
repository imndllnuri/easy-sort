package com.easysort.platform.neoforge.network;

import com.easysort.core.sort.SortKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public record SortContainerPayload(int containerId, SortKey primarySortKey) {

	public void send() {
		NetworkSetup.CHANNEL.sendToServer(this);
	}

	static void encode(SortContainerPayload payload, FriendlyByteBuf buf) {
		buf.writeVarInt(payload.containerId());
		SortKeyCodec.encode(buf, payload.primarySortKey());
	}

	static SortContainerPayload decode(FriendlyByteBuf buf) {
		int containerId = buf.readVarInt();
		SortKey key = SortKeyCodec.decode(buf);
		return new SortContainerPayload(containerId, key);
	}

	public static void register(SimpleChannel channel, int id, BiConsumer<SortContainerPayload, ServerPlayer> handler) {
		channel.registerMessage(id, SortContainerPayload.class, SortContainerPayload::encode, SortContainerPayload::decode,
				(payload, contextSupplier) -> handle(payload, contextSupplier, handler));
	}

	private static void handle(SortContainerPayload payload, Supplier<NetworkEvent.Context> contextSupplier,
			BiConsumer<SortContainerPayload, ServerPlayer> handler) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handler.accept(payload, context.getSender()));
		context.setPacketHandled(true);
	}
}
