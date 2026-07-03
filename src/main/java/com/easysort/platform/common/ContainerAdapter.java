package com.easysort.platform.common;

import com.easysort.core.config.SortConfig;
import com.easysort.core.model.SortableItem;
import com.easysort.core.sort.SortEngine;
import com.easysort.core.sort.SortResult;
import com.easysort.core.transfer.TransferEngine;
import com.easysort.core.transfer.TransferResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bridges a live vanilla {@link Container} to {@code core.sort}/{@code core.transfer}
 * and back. Pure vanilla Minecraft API (no Fabric imports) - this is expected
 * to be identical under a future NeoForge module, since Container is a
 * vanilla type.
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
		Map<VariantKey, ItemStack> templates = new HashMap<>();
		List<SortableItem> slots = toSortableItems(container, fromSlot, toSlotExclusive, templates);

		SortResult result = SortEngine.sort(slots, config);

		writeBack(container, fromSlot, toSlotExclusive, result.items(), templates);
	}

	/**
	 * Moves everything in [sourceFrom, sourceToExclusive) of {@code source}
	 * that {@code destination} already contains into destination. See
	 * {@link TransferEngine#quickStack}.
	 *
	 * @return how many items moved
	 */
	public static int quickStack(Container source, int sourceFrom, int sourceToExclusive, Container destination) {
		Map<VariantKey, ItemStack> templates = new HashMap<>();
		List<SortableItem> sourceItems = toSortableItems(source, sourceFrom, sourceToExclusive, templates);
		List<SortableItem> destinationItems = toSortableItems(destination, 0, destination.getContainerSize(), templates);

		TransferResult result = TransferEngine.quickStack(sourceItems, destinationItems);

		writeBack(source, sourceFrom, sourceToExclusive, result.source(), templates);
		writeBack(destination, 0, destination.getContainerSize(), result.destination(), templates);
		return result.itemsMoved();
	}

	/**
	 * Tops up destination's existing partial stacks in
	 * [destinationFrom, destinationToExclusive) from {@code source}. See
	 * {@link TransferEngine#restock}.
	 *
	 * @return how many items moved
	 */
	public static int restock(Container source, Container destination, int destinationFrom, int destinationToExclusive) {
		Map<VariantKey, ItemStack> templates = new HashMap<>();
		List<SortableItem> sourceItems = toSortableItems(source, 0, source.getContainerSize(), templates);
		List<SortableItem> destinationItems = toSortableItems(destination, destinationFrom, destinationToExclusive, templates);

		TransferResult result = TransferEngine.restock(sourceItems, destinationItems);

		writeBack(source, 0, source.getContainerSize(), result.source(), templates);
		writeBack(destination, destinationFrom, destinationToExclusive, result.destination(), templates);
		return result.itemsMoved();
	}

	private static List<SortableItem> toSortableItems(Container container, int fromSlot, int toSlotExclusive,
			Map<VariantKey, ItemStack> templates) {
		List<SortableItem> slots = new ArrayList<>(toSlotExclusive - fromSlot);
		for (int i = fromSlot; i < toSlotExclusive; i++) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) {
				slots.add(null);
				continue;
			}
			String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
			long variantKey = stack.getComponents().hashCode();
			templates.putIfAbsent(new VariantKey(itemId, variantKey), stack);
			slots.add(new SortableItem(itemId, stack.getHoverName().getString(), stack.getCount(),
					container.getMaxStackSize(stack), i - fromSlot, variantKey));
		}
		return slots;
	}

	private static void writeBack(Container container, int fromSlot, int toSlotExclusive, List<SortableItem> items,
			Map<VariantKey, ItemStack> templates) {
		int rangeSize = toSlotExclusive - fromSlot;
		for (int i = 0; i < rangeSize; i++) {
			int slotIndex = fromSlot + i;
			SortableItem item = i < items.size() ? items.get(i) : null;
			if (item == null) {
				container.setItem(slotIndex, ItemStack.EMPTY);
			} else {
				ItemStack template = templates.get(new VariantKey(item.itemId(), item.variantKey()));
				container.setItem(slotIndex, template.copyWithCount(item.count()));
			}
		}
		container.setChanged();
	}

	private record VariantKey(String itemId, long variantKey) {
	}
}
