package com.easysort.core.sort;

import com.easysort.core.model.SortableItem;

import java.util.List;

public record SortResult(List<SortableItem> items, int slotsFreed) {

	public SortResult {
		items = List.copyOf(items);
	}
}
