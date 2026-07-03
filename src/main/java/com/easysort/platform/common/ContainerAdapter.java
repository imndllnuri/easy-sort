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
		int size = container.getContainerSize();
		List<SortableItem> slots = new ArrayList<>(size);
		Map<VariantKey, ItemStack> templates = new HashMap<>();

		for (int i = 0; i < size; i++) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) {
				slots.add(null);
				continue;
			}
			String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
			long variantKey = stack.getComponents().hashCode();
			templates.putIfAbsent(new VariantKey(itemId, variantKey), stack);
			slots.add(new SortableItem(itemId, stack.getCount(), container.getMaxStackSize(stack), i, variantKey));
		}

		SortResult result = SortEngine.sort(slots, config);
		List<SortableItem> sorted = result.items();

		for (int i = 0; i < size; i++) {
			if (i < sorted.size()) {
				SortableItem item = sorted.get(i);
				ItemStack template = templates.get(new VariantKey(item.itemId(), item.variantKey()));
				container.setItem(i, template.copyWithCount(item.count()));
			} else {
				container.setItem(i, ItemStack.EMPTY);
			}
		}
		container.setChanged();
	}

	private record VariantKey(String itemId, long variantKey) {
	}
}
