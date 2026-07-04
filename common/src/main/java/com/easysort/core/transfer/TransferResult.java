package com.easysort.core.transfer;

import com.easysort.core.model.SortableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Both lists are always exactly as long as the inputs given to
 * {@link TransferEngine} - unlike {@code SortResult}, slots are never
 * compacted, since each index still corresponds to a real container slot.
 */
public record TransferResult(List<SortableItem> source, List<SortableItem> destination, int itemsMoved) {

	public TransferResult {
		// List.copyOf() rejects null elements; these lists legitimately
		// contain nulls for empty slots, so wrap instead of copying via that.
		source = Collections.unmodifiableList(new ArrayList<>(source));
		destination = Collections.unmodifiableList(new ArrayList<>(destination));
	}
}
