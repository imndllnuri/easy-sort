package com.easysort.gametest;

import com.easysort.platform.common.MenuContainers;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

/**
 * ShulkerBoxMenu has no public getter for its backing Container (unlike
 * ChestMenu), so MenuContainers reads it off the menu's own slot list
 * instead. This verifies that actually works against a real menu instance,
 * not just against the ChestMenu case it was designed around.
 */
public class MenuContainersGameTests {

	@GameTest
	public void extractsBackingContainerFromShulkerBoxMenu(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		SimpleContainer shulkerContainer = new SimpleContainer(27);
		shulkerContainer.setItem(0, new ItemStack(Items.DIAMOND, 3));
		ShulkerBoxMenu menu = new ShulkerBoxMenu(1, player.getInventory(), shulkerContainer);

		helper.assertTrue(MenuContainers.isSupported(menu), Component.literal("ShulkerBoxMenu should be a supported menu"));
		helper.assertTrue(MenuContainers.extractContainer(menu) == shulkerContainer,
				Component.literal("extracted container should be the exact same instance backing the menu"));

		helper.succeed();
	}

	@GameTest
	public void extractsBackingContainerFromChestMenu(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		SimpleContainer chestContainer = new SimpleContainer(27);
		ChestMenu menu = ChestMenu.threeRows(1, player.getInventory(), chestContainer);

		helper.assertTrue(MenuContainers.isSupported(menu), Component.literal("ChestMenu should be a supported menu"));
		helper.assertTrue(MenuContainers.extractContainer(menu) == chestContainer,
				Component.literal("slot-based extraction should agree with ChestMenu.getContainer()"));

		helper.succeed();
	}
}
