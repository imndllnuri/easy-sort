package com.easysort.core.transfer;

import com.easysort.core.model.SortableItem;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Moves items between two slot lists (nulls are empty slots, same convention
 * as {@code SortEngine}). Both outputs stay positional - same length and
 * slot-for-slot correspondence as the inputs - since these represent real
 * container slots being written back individually, unlike a sort's compacted
 * result.
 */
public final class TransferEngine {

	private TransferEngine() {
	}

	/**
	 * Player inventory -> container: moves everything in {@code source} that
	 * {@code destination} already has at least one stack of, filling existing
	 * stacks first and then destination's empty slots for the rest. Never
	 * introduces an item type destination didn't already contain.
	 */
	public static TransferResult quickStack(List<SortableItem> source, List<SortableItem> destination) {
		return transfer(source, destination, true);
	}

	/**
	 * Container -> player inventory: tops up destination's existing partial
	 * stacks from source. Never creates a new stack in an empty destination
	 * slot, so it can't introduce item types the player wasn't already
	 * carrying.
	 */
	public static TransferResult restock(List<SortableItem> source, List<SortableItem> destination) {
		return transfer(source, destination, false);
	}

	private static TransferResult transfer(List<SortableItem> source, List<SortableItem> destination, boolean allowNewStacksInDestination) {
		List<SortableItem> newDestination = new ArrayList<>(destination);
		int[] sourceRemaining = new int[source.size()];
		for (int i = 0; i < source.size(); i++) {
			SortableItem item = source.get(i);
			sourceRemaining[i] = item == null ? 0 : item.count();
		}

		// Item types destination already contains, in first-seen slot order -
		// this is both the processing order and, for quick-stack, the gate on
		// which types are allowed to land in a previously-empty slot.
		Set<MatchKey> presentInDestination = new LinkedHashSet<>();
		for (SortableItem item : destination) {
			if (item != null) {
				presentInDestination.add(MatchKey.of(item));
			}
		}

		int itemsMoved = 0;

		for (MatchKey key : presentInDestination) {
			for (int di = 0; di < newDestination.size(); di++) {
				SortableItem destItem = newDestination.get(di);
				if (destItem == null || !MatchKey.of(destItem).equals(key)) {
					continue;
				}
				int headroom = destItem.maxStackSize() - destItem.count();
				if (headroom <= 0) {
					continue;
				}
				int pulled = pull(source, sourceRemaining, key, headroom);
				if (pulled > 0) {
					newDestination.set(di, withCount(destItem, destItem.count() + pulled));
					itemsMoved += pulled;
				}
			}

			if (allowNewStacksInDestination) {
				int remaining = remainingSupply(source, sourceRemaining, key);
				if (remaining > 0) {
					int maxStackSize = maxStackSizeOf(source, key);
					for (int di = 0; di < newDestination.size() && remaining > 0; di++) {
						if (newDestination.get(di) != null) {
							continue;
						}
						int amount = Math.min(remaining, maxStackSize);
						int pulled = pull(source, sourceRemaining, key, amount);
						if (pulled > 0) {
							newDestination.set(di, newStack(source, key, pulled, di));
							itemsMoved += pulled;
							remaining -= pulled;
						}
					}
				}
			}
		}

		List<SortableItem> newSource = new ArrayList<>(source.size());
		for (int i = 0; i < source.size(); i++) {
			SortableItem original = source.get(i);
			boolean nowEmpty = original == null || sourceRemaining[i] <= 0;
			newSource.add(nowEmpty ? null : withCount(original, sourceRemaining[i]));
		}

		return new TransferResult(newSource, newDestination, itemsMoved);
	}

	private static int pull(List<SortableItem> source, int[] sourceRemaining, MatchKey key, int amount) {
		int pulled = 0;
		for (int i = 0; i < source.size() && pulled < amount; i++) {
			SortableItem item = source.get(i);
			if (item == null || sourceRemaining[i] <= 0 || !MatchKey.of(item).equals(key)) {
				continue;
			}
			int take = Math.min(amount - pulled, sourceRemaining[i]);
			sourceRemaining[i] -= take;
			pulled += take;
		}
		return pulled;
	}

	private static int remainingSupply(List<SortableItem> source, int[] sourceRemaining, MatchKey key) {
		int total = 0;
		for (int i = 0; i < source.size(); i++) {
			SortableItem item = source.get(i);
			if (item != null && sourceRemaining[i] > 0 && MatchKey.of(item).equals(key)) {
				total += sourceRemaining[i];
			}
		}
		return total;
	}

	private static int maxStackSizeOf(List<SortableItem> source, MatchKey key) {
		for (SortableItem item : source) {
			if (item != null && MatchKey.of(item).equals(key)) {
				return item.maxStackSize();
			}
		}
		throw new IllegalStateException("No source item found for " + key);
	}

	private static SortableItem newStack(List<SortableItem> source, MatchKey key, int count, int stableIndex) {
		for (SortableItem item : source) {
			if (item != null && MatchKey.of(item).equals(key)) {
				return new SortableItem(item.itemId(), item.displayName(), count, item.maxStackSize(), stableIndex, item.variantKey());
			}
		}
		throw new IllegalStateException("No source item found for " + key);
	}

	private static SortableItem withCount(SortableItem item, int count) {
		return new SortableItem(item.itemId(), item.displayName(), count, item.maxStackSize(), item.stableIndex(), item.variantKey());
	}

	private record MatchKey(String itemId, long variantKey) {
		static MatchKey of(SortableItem item) {
			return new MatchKey(item.itemId(), item.variantKey());
		}
	}
}
