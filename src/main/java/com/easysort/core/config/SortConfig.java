package com.easysort.core.config;

import com.easysort.core.model.SortableItem;
import com.easysort.core.sort.SortKey;

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
