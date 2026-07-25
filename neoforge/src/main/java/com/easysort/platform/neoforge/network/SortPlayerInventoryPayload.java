package com.easysort.platform.neoforge.network;

import com.easysort.core.sort.SortKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * "Sort my own inventory" - unlike {@link SortContainerPayload}, this never
 * needs to be checked against a specific open container, since it only ever
 * touches the requesting player's own inventory regardless of what screen
 * (if any) is open.
 */
public record SortPlayerInventoryPayload(SortKey primarySortKey) {

	public void send() {
		NetworkSetup.CHANNEL.sendToServer(this);
	}

	static void encode(SortPlayerInventoryPayload payload, FriendlyByteBuf buf) {
		SortKeyCodec.encode(buf, payload.primarySortKey());
	}

	static SortPlayerInventoryPayload decode(FriendlyByteBuf buf) {
		return new SortPlayerInventoryPayload(SortKeyCodec.decode(buf));
	}

	public static void register(SimpleChannel channel, int id, BiConsumer<SortPlayerInventoryPayload, ServerPlayer> handler) {
		channel.registerMessage(id, SortPlayerInventoryPayload.class, SortPlayerInventoryPayload::encode,
				SortPlayerInventoryPayload::decode, (payload, contextSupplier) -> handle(payload, contextSupplier, handler));
	}

	private static void handle(SortPlayerInventoryPayload payload, Supplier<NetworkEvent.Context> contextSupplier,
			BiConsumer<SortPlayerInventoryPayload, ServerPlayer> handler) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handler.accept(payload, context.getSender()));
		context.setPacketHandled(true);
	}
}
