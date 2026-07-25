package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Raw {@link FriendlyByteBuf} read/write helpers for {@link SortKey}, shared
 * by the payloads that carry one. Fabric API's typed CustomPayload/StreamCodec
 * pair is a Minecraft 1.20.5+ addition - on 1.19.2, channel packets are just
 * raw FriendlyByteBuf reads/writes keyed by a plain ResourceLocation channel
 * name (see fabric-networking-api-v1's ServerPlayNetworking/ClientPlayNetworking).
 */
final class SortKeyCodec {

	private SortKeyCodec() {
	}

	static void write(FriendlyByteBuf buf, SortKey key) {
		buf.writeVarInt(key.ordinal());
	}

	static SortKey read(FriendlyByteBuf buf) {
		return byOrdinal(buf.readVarInt());
	}

	private static SortKey byOrdinal(int ordinal) {
		SortKey[] values = SortKey.values();
		if (ordinal < 0 || ordinal >= values.length) {
			throw new IllegalArgumentException("Invalid SortKey ordinal: " + ordinal);
		}
		return values[ordinal];
	}
}
