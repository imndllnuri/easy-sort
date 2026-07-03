package com.easysort.core.config;

import com.easysort.core.model.SortableItem;
import com.easysort.core.sort.SortKey;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortConfigTest {

	@Test
	void defaultConfigOrdersByModIdThenItemId() {
		assertEquals(List.of(SortKey.MOD_ID, SortKey.ITEM_ID), SortConfig.defaultConfig().order());
	}

	@Test
	void emptyOrderComparatorFallsBackToStableIndex() {
		SortConfig config = new SortConfig(List.of());
		SortableItem first = new SortableItem("minecraft:zombie_head", "Zombie Head", 1, 64, 0, 0);
		SortableItem second = new SortableItem("minecraft:apple", "Apple", 1, 64, 1, 0);

		assertEquals(-1, Integer.signum(config.comparator().compare(first, second)));
	}
}
