package com.easysort.core.sort;

import com.easysort.core.config.SortConfig;
import com.easysort.core.model.SortableItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Stateless: merges partial stacks of the same item/variant (capped at each
 * item's maxStackSize), then sorts the result. Null entries in {@code slots}
 * represent empty container slots and are dropped from the output entirely -
 * callers are responsible for placing the returned items back into slots.
 */
public final class SortEngine {

	private SortEngine() {
	}

	public static SortResult sort(List<SortableItem> slots, SortConfig config) {
		Objects.requireNonNull(slots, "slots");
		Objects.requireNonNull(config, "config");

		Map<MergeKey, MergeGroup> groups = new LinkedHashMap<>();
		int presentCount = 0;
		for (SortableItem item : slots) {
			if (item == null) {
				continue;
			}
			presentCount++;
			groups.computeIfAbsent(mergeKeyOf(item), key -> new MergeGroup(item)).add(item);
		}

		List<SortableItem> merged = new ArrayList<>();
		for (MergeGroup group : groups.values()) {
			merged.addAll(group.toStacks());
		}

		merged.sort(config.comparator());

		return new SortResult(merged, presentCount - merged.size());
	}

	private static MergeKey mergeKeyOf(SortableItem item) {
		// Items that cap at 1 per stack (tools, armor, unique items) must never
		// be merged into each other, even when itemId/variantKey match.
		int distinguisher = item.maxStackSize() <= 1 ? item.stableIndex() : 0;
		return new MergeKey(item.itemId(), item.variantKey(), distinguisher);
	}

	private record MergeKey(String itemId, long variantKey, int distinguisher) {
	}

	private static final class MergeGroup {
		private final String itemId;
		private final long variantKey;
		private final int maxStackSize;
		private int stableIndex;
		private int totalCount;

		MergeGroup(SortableItem first) {
			this.itemId = first.itemId();
			this.variantKey = first.variantKey();
			this.maxStackSize = first.maxStackSize();
			this.stableIndex = first.stableIndex();
		}

		// The merged stack's stableIndex is the minimum across its sources, so
		// the result never depends on which source item happened to be visited
		// first while iterating the input slots.
		void add(SortableItem item) {
			totalCount += item.count();
			stableIndex = Math.min(stableIndex, item.stableIndex());
		}

		List<SortableItem> toStacks() {
			List<SortableItem> stacks = new ArrayList<>();
			int remaining = totalCount;
			while (remaining > 0) {
				int stackCount = Math.min(remaining, maxStackSize);
				stacks.add(new SortableItem(itemId, stackCount, maxStackSize, stableIndex, variantKey));
				remaining -= stackCount;
			}
			return stacks;
		}
	}
}
