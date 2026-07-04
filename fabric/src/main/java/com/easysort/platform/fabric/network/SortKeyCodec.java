package com.easysort.platform.fabric.network;

import com.easysort.core.sort.SortKey;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

final class SortKeyCodec {

	static final StreamCodec<ByteBuf, SortKey> CODEC = ByteBufCodecs.VAR_INT.map(SortKeyCodec::byOrdinal, SortKey::ordinal);

	private SortKeyCodec() {
	}

	private static SortKey byOrdinal(int ordinal) {
		SortKey[] values = SortKey.values();
		if (ordinal < 0 || ordinal >= values.length) {
			throw new IllegalArgumentException("Invalid SortKey ordinal: " + ordinal);
		}
		return values[ordinal];
	}
}
