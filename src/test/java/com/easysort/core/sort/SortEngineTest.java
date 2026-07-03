package com.easysort.core.sort;

import com.easysort.core.config.SortConfig;
import com.easysort.core.model.SortableItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortEngineTest {

	private static SortableItem item(String itemId, int count, int maxStackSize, int stableIndex, long variantKey) {
		return new SortableItem(itemId, count, maxStackSize, stableIndex, variantKey);
	}

	@Test
	void emptyInputProducesEmptyResult() {
		SortResult result = SortEngine.sort(List.of(), SortConfig.defaultConfig());

		assertTrue(result.items().isEmpty());
		assertEquals(0, result.slotsFreed());
	}

	@Test
	void nullSlotsAreTreatedAsEmpty() {
		List<SortableItem> slots = new ArrayList<>();
		slots.add(null);
		slots.add(item("minecraft:dirt", 10, 64, 0, 0));
		slots.add(null);

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		assertEquals(1, result.items().size());
		assertEquals(10, result.items().get(0).count());
	}

	@Test
	void mergesPartialStacksOfTheSameItemAndVariant() {
		List<SortableItem> slots = List.of(
				item("minecraft:dirt", 10, 64, 0, 0),
				item("minecraft:dirt", 20, 64, 1, 0));

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		assertEquals(1, result.items().size());
		assertEquals(30, result.items().get(0).count());
		assertEquals(1, result.slotsFreed());
	}

	@Test
	void mergeRespectsMaxStackSizeAndSplitsOverflow() {
		List<SortableItem> slots = List.of(
				item("minecraft:dirt", 40, 64, 0, 0),
				item("minecraft:dirt", 40, 64, 1, 0));

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		assertEquals(2, result.items().size());
		int total = result.items().stream().mapToInt(SortableItem::count).sum();
		assertEquals(80, total);
		assertTrue(result.items().stream().allMatch(i -> i.count() <= 64));
	}

	@Test
	void doesNotMergeDifferentVariantsOfTheSameItem() {
		List<SortableItem> slots = List.of(
				item("minecraft:enchanted_book", 1, 1, 0, 111L),
				item("minecraft:enchanted_book", 1, 1, 1, 222L));

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		assertEquals(2, result.items().size());
		assertEquals(0, result.slotsFreed());
	}

	@Test
	void neverMergesUnstackableItemsEvenWithMatchingVariant() {
		List<SortableItem> slots = List.of(
				item("minecraft:diamond_sword", 1, 1, 0, 0),
				item("minecraft:diamond_sword", 1, 1, 1, 0));

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		assertEquals(2, result.items().size());
		assertTrue(result.items().stream().allMatch(i -> i.count() == 1));
	}

	@Test
	void sortsByModIdThenItemIdByDefault() {
		List<SortableItem> slots = List.of(
				item("modb:widget", 1, 64, 0, 0),
				item("minecraft:dirt", 1, 64, 1, 0),
				item("minecraft:apple", 1, 64, 2, 0));

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		List<String> order = result.items().stream().map(SortableItem::itemId).toList();
		assertEquals(List.of("minecraft:apple", "minecraft:dirt", "modb:widget"), order);
	}

	@Test
	void sortsByCountDescendingWhenConfigured() {
		List<SortableItem> slots = List.of(
				item("minecraft:dirt", 5, 64, 0, 0),
				item("minecraft:stone", 40, 64, 1, 0));

		SortResult result = SortEngine.sort(slots, new SortConfig(List.of(SortKey.COUNT)));

		assertEquals("minecraft:stone", result.items().get(0).itemId());
		assertEquals("minecraft:dirt", result.items().get(1).itemId());
	}

	@Test
	void emptyOrderFallsBackToOriginalFirstSeenOrder() {
		List<SortableItem> slots = List.of(
				item("modb:widget", 1, 64, 0, 0),
				item("minecraft:dirt", 1, 64, 1, 0));

		SortResult result = SortEngine.sort(slots, new SortConfig(List.of()));

		List<String> order = result.items().stream().map(SortableItem::itemId).toList();
		assertEquals(List.of("modb:widget", "minecraft:dirt"), order);
	}

	@Test
	void isDeterministicRegardlessOfInputSlotOrder() {
		List<SortableItem> slots = List.of(
				item("minecraft:stone", 3, 64, 0, 0),
				item("minecraft:dirt", 12, 64, 1, 0),
				item("minecraft:apple", 1, 64, 2, 0),
				item("minecraft:dirt", 40, 64, 3, 0));

		List<SortableItem> shuffled = new ArrayList<>(slots);
		Collections.shuffle(shuffled, new Random(42));

		SortResult first = SortEngine.sort(slots, SortConfig.defaultConfig());
		SortResult second = SortEngine.sort(shuffled, SortConfig.defaultConfig());

		assertEquals(first.items(), second.items());
	}

	@Test
	void isIdempotentWhenReSortingItsOwnOutput() {
		List<SortableItem> slots = List.of(
				item("minecraft:stone", 3, 64, 0, 0),
				item("minecraft:dirt", 12, 64, 1, 0),
				item("minecraft:dirt", 40, 64, 3, 0));

		SortResult first = SortEngine.sort(slots, SortConfig.defaultConfig());
		SortResult second = SortEngine.sort(first.items(), SortConfig.defaultConfig());

		assertEquals(first.items(), second.items());
		assertEquals(0, second.slotsFreed());
	}

	@Test
	void conservesTotalItemCount() {
		List<SortableItem> slots = List.of(
				item("minecraft:stone", 3, 64, 0, 0),
				item("minecraft:dirt", 12, 64, 1, 0),
				item("minecraft:dirt", 40, 64, 3, 0),
				item("minecraft:diamond_sword", 1, 1, 4, 0));

		SortResult result = SortEngine.sort(slots, SortConfig.defaultConfig());

		int before = slots.stream().mapToInt(SortableItem::count).sum();
		int after = result.items().stream().mapToInt(SortableItem::count).sum();
		assertEquals(before, after);
	}
}
