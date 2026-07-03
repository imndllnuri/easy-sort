package com.easysort.core.sort;

import com.easysort.core.model.SortableItem;

import java.util.Comparator;

public enum SortKey {
	MOD_ID(Comparator.comparing(SortableItem::modId)),
	ITEM_ID(Comparator.comparing(SortableItem::itemId)),
	COUNT(Comparator.comparingInt(SortableItem::count).reversed());

	private final Comparator<SortableItem> comparator;

	SortKey(Comparator<SortableItem> comparator) {
		this.comparator = comparator;
	}

	public Comparator<SortableItem> comparator() {
		return comparator;
	}
}
