package com.easysort.platform.common;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;

/**
 * Identifies "generic storage" menus (chest, shulker box, ...) that sorting
 * and transfer buttons should appear on, and extracts their backing
 * Container. ShulkerBoxMenu has no public getter for it (unlike ChestMenu),
 * so this reads it off the menu's own slot list instead: every such menu's
 * own slots come first and all share one Container reference, distinct from
 * the player inventory slots that follow - true for any vanilla container
 * menu, not just the two handled today.
 */
public final class MenuContainers {

	private MenuContainers() {
	}

	public static boolean isSupported(AbstractContainerMenu menu) {
		return menu instanceof ChestMenu || menu instanceof ShulkerBoxMenu;
	}

	public static Container extractContainer(AbstractContainerMenu menu) {
		return menu.slots.get(0).container;
	}
}
