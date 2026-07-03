package com.easysort.core.transfer;

import com.easysort.core.model.SortableItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferEngineTest {

	private static SortableItem item(String itemId, int count, int stableIndex) {
		return new SortableItem(itemId, itemId, count, 64, stableIndex, 0);
	}

	@Test
	void quickStackMovesOnlyItemTypesAlreadyInDestination() {
		List<SortableItem> source = Arrays.asList(item("minecraft:dirt", 20, 0), item("minecraft:stone", 10, 1));
		List<SortableItem> destination = Arrays.asList(item("minecraft:dirt", 5, 0), null);

		TransferResult result = TransferEngine.quickStack(source, destination);

		assertEquals(25, result.destination().get(0).count(), "dirt should merge into the existing stack");
		assertNull(result.source().get(0), "source dirt slot should be fully drained");
		assertEquals(10, result.source().get(1).count(), "stone was never in destination, so it must not move");
		assertNull(result.destination().get(1), "no stone should have landed in the empty destination slot");
	}

	@Test
	void quickStackFillsExistingStackBeforeUsingEmptySlots() {
		List<SortableItem> source = List.of(item("minecraft:dirt", 50, 0));
		List<SortableItem> destination = Arrays.asList(item("minecraft:dirt", 54, 0), null, null);

		TransferResult result = TransferEngine.quickStack(source, destination);

		assertEquals(64, result.destination().get(0).count(), "existing stack should be topped up to max first");
		assertEquals(40, result.destination().get(1).count(), "overflow should land in an empty slot as a new stack");
		assertNull(result.destination().get(2), "unused empty slot should stay empty");
		assertNull(result.source().get(0));
	}

	@Test
	void quickStackNewStacksRespectMaxStackSize() {
		List<SortableItem> source = List.of(item("minecraft:dirt", 100, 0));
		List<SortableItem> destination = Arrays.asList(item("minecraft:dirt", 64, 0), null, null);

		TransferResult result = TransferEngine.quickStack(source, destination);

		assertTrue(result.destination().stream().filter(java.util.Objects::nonNull)
				.allMatch(i -> i.count() <= 64));
	}

	@Test
	void quickStackLeavesOverflowInSourceWhenDestinationHasNoRoom() {
		List<SortableItem> source = List.of(item("minecraft:dirt", 30, 0));
		List<SortableItem> destination = List.of(item("minecraft:dirt", 64, 0));

		TransferResult result = TransferEngine.quickStack(source, destination);

		assertEquals(30, result.source().get(0).count(), "nothing could move, destination stack is already full");
		assertEquals(64, result.destination().get(0).count());
		assertEquals(0, result.itemsMoved());
	}

	@Test
	void restockOnlyFillsPartialStacksNeverEmptySlots() {
		List<SortableItem> source = List.of(item("minecraft:arrow", 64, 0));
		List<SortableItem> destination = Arrays.asList(item("minecraft:arrow", 5, 0), null);

		TransferResult result = TransferEngine.restock(source, destination);

		assertEquals(64, result.destination().get(0).count(), "existing partial stack should be topped up to max");
		assertNull(result.destination().get(1), "restock must never place a new stack in an empty slot");
	}

	@Test
	void restockDoesNotIntroduceNewItemTypes() {
		List<SortableItem> source = List.of(item("minecraft:golden_apple", 10, 0));
		List<SortableItem> destination = Arrays.asList((SortableItem) null, null);

		TransferResult result = TransferEngine.restock(source, destination);

		assertTrue(result.destination().stream().allMatch(java.util.Objects::isNull));
		assertEquals(10, result.source().get(0).count(), "nothing should have moved");
	}

	@Test
	void transferConservesTotalItemCount() {
		List<SortableItem> source = Arrays.asList(item("minecraft:dirt", 40, 0), item("minecraft:stone", 20, 1));
		List<SortableItem> destination = Arrays.asList(item("minecraft:dirt", 10, 0), null, item("minecraft:stone", 64, 1));

		int before = sum(source) + sum(destination);

		TransferResult result = TransferEngine.quickStack(source, destination);

		int after = sum(result.source()) + sum(result.destination());
		assertEquals(before, after);
	}

	@Test
	void transferIsNoOpWhenNoMatchingItems() {
		List<SortableItem> source = List.of(item("minecraft:dirt", 10, 0));
		List<SortableItem> destination = List.of(item("minecraft:stone", 10, 0));

		TransferResult result = TransferEngine.quickStack(source, destination);

		assertEquals(10, result.source().get(0).count());
		assertEquals(10, result.destination().get(0).count());
		assertEquals(0, result.itemsMoved());
	}

	@Test
	void quickStackResultStaysPositional() {
		List<SortableItem> source = new ArrayList<>(List.of(item("minecraft:dirt", 5, 0)));
		List<SortableItem> destination = new ArrayList<>(Arrays.asList(item("minecraft:dirt", 5, 0), null, null));

		TransferResult result = TransferEngine.quickStack(source, destination);

		assertEquals(source.size(), result.source().size());
		assertEquals(destination.size(), result.destination().size());
	}

	private static int sum(List<SortableItem> items) {
		return items.stream().filter(java.util.Objects::nonNull).mapToInt(SortableItem::count).sum();
	}
}
