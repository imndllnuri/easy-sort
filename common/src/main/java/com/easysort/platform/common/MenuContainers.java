package com.easysort.platform.common;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
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

	/**
	 * Whether this player may change inventories through Easy Sort's requests.
	 * Vanilla's container-click handling (ServerGamePacketListenerImpl) ignores
	 * every slot click from a spectator, who is allowed to open a chest to look
	 * inside but not to touch it. Sort/Quick Stack/Restock move items just like
	 * those clicks do, so they must follow the same rule - otherwise a spectator
	 * could take items from a chest with Restock. Every server handler calls this
	 * first.
	 */
	public static boolean canPlayerInteract(Player player) {
		return !player.isSpectator();
	}
}
