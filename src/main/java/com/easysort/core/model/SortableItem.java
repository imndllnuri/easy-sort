package com.easysort.core.model;

import java.util.Objects;

/**
 * A loader-agnostic view of one item stack, used only by {@code core.sort}.
 * {@code variantKey} distinguishes stacks that share an itemId but must not
 * merge (e.g. differently enchanted books) - it is an opaque identity token
 * supplied by the platform layer, not interpreted here.
 */
public record SortableItem(String itemId, int count, int maxStackSize, int stableIndex, long variantKey) {

	public SortableItem {
		Objects.requireNonNull(itemId, "itemId");
		if (itemId.isBlank()) {
			throw new IllegalArgumentException("itemId must not be blank");
		}
		if (count <= 0) {
			throw new IllegalArgumentException("count must be positive");
		}
		if (maxStackSize <= 0) {
			throw new IllegalArgumentException("maxStackSize must be positive");
		}
		if (stableIndex < 0) {
			throw new IllegalArgumentException("stableIndex must not be negative");
		}
	}

	public String modId() {
		int separator = itemId.indexOf(':');
		return separator >= 0 ? itemId.substring(0, separator) : itemId;
	}
}
