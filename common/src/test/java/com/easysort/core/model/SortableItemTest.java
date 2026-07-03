package com.easysort.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SortableItemTest {

	@Test
	void modIdIsDerivedFromNamespace() {
		SortableItem item = new SortableItem("modb:widget", "Widget", 1, 64, 0, 0);

		assertEquals("modb", item.modId());
	}

	@Test
	void modIdFallsBackToWholeIdWithoutNamespace() {
		SortableItem item = new SortableItem("widget", "Widget", 1, 64, 0, 0);

		assertEquals("widget", item.modId());
	}

	@Test
	void rejectsNonPositiveCount() {
		assertThrows(IllegalArgumentException.class, () -> new SortableItem("minecraft:dirt", "Dirt", 0, 64, 0, 0));
	}

	@Test
	void rejectsBlankItemId() {
		assertThrows(IllegalArgumentException.class, () -> new SortableItem(" ", "Dirt", 1, 64, 0, 0));
	}
}
