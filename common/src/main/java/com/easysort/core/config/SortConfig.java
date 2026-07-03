package com.easysort.core.config;

import com.easysort.core.model.SortableItem;
import com.easysort.core.sort.SortKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SortConfig(List<SortKey> order) {

	public SortConfig {
		order = List.copyOf(order);
	}

	public static SortConfig defaultConfig() {
		return new SortConfig(List.of(SortKey.MOD_ID, SortKey.ITEM_ID));
	}

	/**
	 * The user's chosen primary sort key first, then every other key as a
	 * tiebreaker in declaration order - so choosing a primary never loses the
	 * multi-key ordering the default config provides.
	 */
	public static SortConfig withPrimary(SortKey primary) {
		List<SortKey> order = new ArrayList<>();
		order.add(primary);
		for (SortKey key : SortKey.values()) {
			if (key != primary) {
				order.add(key);
			}
		}
		return new SortConfig(order);
	}

	/**
	 * stableIndex is always the final tiebreaker so the result is fully
	 * deterministic even with an empty order or a non-total-ordering config.
	 */
	public Comparator<SortableItem> comparator() {
		Comparator<SortableItem> comparator = (a, b) -> 0;
		for (SortKey key : order) {
			comparator = comparator.thenComparing(key.comparator());
		}
		return comparator.thenComparingInt(SortableItem::stableIndex);
	}
}
