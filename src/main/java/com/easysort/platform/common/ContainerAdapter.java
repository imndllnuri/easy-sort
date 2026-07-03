package com.easysort.platform.common;

import com.easysort.core.config.SortConfig;
import com.easysort.core.model.SortableItem;
import com.easysort.core.sort.SortEngine;
import com.easysort.core.sort.SortResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bridges a live vanilla {@link Container} to {@code core.sort} and back.
 * Pure vanilla Minecraft API (no Fabric imports) - this is expected to be
 * identical under a future NeoForge module, since Container is a vanilla type.
 */
public final class ContainerAdapter {

	private ContainerAdapter() {
	}

	public static void sort(Container container, SortConfig config) {
		sort(container, 0, container.getContainerSize(), config);
	}

	/**
	 * Sorts only slots [fromSlot, toSlotExclusive) of the container, leaving
	 * everything outside that range untouched. Needed for the player's own
	 * Inventory: its Container view exposes armor/offhand past index
	 * Inventory.INVENTORY_SIZE, and those must never be reordered into the
	 * main inventory grid.
	 */
	public static void sort(Container container, int fromSlot, int toSlotExclusive, SortConfig config) {
		int rangeSize = toSlotExclusive - fromSlot;
		List<SortableItem> slots = new ArrayList<>(rangeSize);
		Map<VariantKey, ItemStack> templates = new HashMap<>();

		for (int i = fromSlot; i < toSlotExclusive; i++) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) {
				slots.add(null);
				continue;
			}
			String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
			long variantKey = stack.getComponents().hashCode();
			templates.putIfAbsent(new VariantKey(itemId, variantKey), stack);
			slots.add(new SortableItem(itemId, stack.getCount(), container.getMaxStackSize(stack), i - fromSlot, variantKey));
		}

		SortResult result = SortEngine.sort(slots, config);
		List<SortableItem> sorted = result.items();

		for (int i = 0; i < rangeSize; i++) {
			int slotIndex = fromSlot + i;
			if (i < sorted.size()) {
				SortableItem item = sorted.get(i);
				ItemStack template = templates.get(new VariantKey(item.itemId(), item.variantKey()));
				container.setItem(slotIndex, template.copyWithCount(item.count()));
			} else {
				container.setItem(slotIndex, ItemStack.EMPTY);
			}
		}
		container.setChanged();
	}

	private record VariantKey(String itemId, long variantKey) {
	}
}
