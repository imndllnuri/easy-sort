package com.easysort.gametest;

import com.easysort.platform.common.MenuContainers;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * ShulkerBoxMenu has no public getter for its backing Container (unlike
 * ChestMenu), so MenuContainers reads it off the menu's own slot list
 * instead. This verifies that actually works against a real menu instance,
 * not just against the ChestMenu case it was designed around.
 */
public class MenuContainersGameTests implements FabricGameTest {

	@GameTest(template = EMPTY_STRUCTURE)
	public void extractsBackingContainerFromShulkerBoxMenu(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		SimpleContainer shulkerContainer = new SimpleContainer(27);
		shulkerContainer.setItem(0, new ItemStack(Items.DIAMOND, 3));
		ShulkerBoxMenu menu = new ShulkerBoxMenu(1, player.getInventory(), shulkerContainer);

		GameTestAssertions.assertTrue(helper, MenuContainers.isSupported(menu), "ShulkerBoxMenu should be a supported menu");
		GameTestAssertions.assertTrue(helper, MenuContainers.extractContainer(menu) == shulkerContainer,
				"extracted container should be the exact same instance backing the menu");

		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void extractsBackingContainerFromChestMenu(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		SimpleContainer chestContainer = new SimpleContainer(27);
		ChestMenu menu = ChestMenu.threeRows(1, player.getInventory(), chestContainer);

		GameTestAssertions.assertTrue(helper, MenuContainers.isSupported(menu), "ChestMenu should be a supported menu");
		GameTestAssertions.assertTrue(helper, MenuContainers.extractContainer(menu) == chestContainer,
				"slot-based extraction should agree with ChestMenu.getContainer()");

		helper.succeed();
	}
}
