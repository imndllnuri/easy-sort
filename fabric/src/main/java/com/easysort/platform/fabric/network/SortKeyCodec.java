package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import net.minecraft.network.FriendlyByteBuf;

final class SortKeyCodec {

	private SortKeyCodec() {
	}

	static void encode(FriendlyByteBuf buf, SortKey key) {
		buf.writeVarInt(key.ordinal());
	}

	static SortKey decode(FriendlyByteBuf buf) {
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
