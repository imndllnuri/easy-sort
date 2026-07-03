package com.easysort.platform.fabric.mixin.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * CreativeModeInventoryScreen.selectedTab is private and static with no
 * public getter - needed to tell the item-browsing tabs apart from the one
 * "Inventory" tab that actually shows the player's own items.
 */
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeModeInventoryScreenAccessor {

	@Accessor("selectedTab")
	static CreativeModeTab easysort$getSelectedTab() {
		throw new AssertionError("Mixin injection failed");
	}
}
