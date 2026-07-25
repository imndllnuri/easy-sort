package com.easysort.gametest;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * Exercises ContainerAdapter against real block entities/registries, not the
 * hand-built SortableItem test doubles core.sort's own unit tests use - this
 * is what would have caught the equipment-slot bug from manual testing
 * automatically, instead of relying on a human noticing it in-game.
 */
public class ContainerAdapterGameTests implements FabricGameTest {

	@GameTest(template = EMPTY_STRUCTURE)
	public void sortsAndMergesChestContents(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));

		chest.setItem(0, new ItemStack(Items.DIRT, 10));
		chest.setItem(1, new ItemStack(Items.STONE, 5));
		chest.setItem(2, new ItemStack(Items.DIRT, 20));

		ContainerAdapter.sort(chest, SortConfig.defaultConfig());

		GameTestAssertions.assertTrue(helper, chest.getItem(0).is(Items.DIRT) && chest.getItem(0).getCount() == 30,
				"expected a single merged 30-dirt stack first (dirt < stone alphabetically)");
		GameTestAssertions.assertTrue(helper, chest.getItem(1).is(Items.STONE) && chest.getItem(1).getCount() == 5,
				"expected the stone stack second");
		GameTestAssertions.assertTrue(helper, chest.getItem(2).isEmpty(), "expected the freed slot to be empty");

		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void mergeRespectsMaxStackSizeInARealChest(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));

		chest.setItem(0, new ItemStack(Items.DIRT, 40));
		chest.setItem(1, new ItemStack(Items.DIRT, 40));

		ContainerAdapter.sort(chest, SortConfig.defaultConfig());

		int total = chest.getItem(0).getCount() + chest.getItem(1).getCount();
		GameTestAssertions.assertTrue(helper, chest.getItem(0).getCount() <= 64 && chest.getItem(1).getCount() <= 64,
				"no resulting stack should exceed the item's max stack size");
		GameTestAssertions.assertTrue(helper, total == 80, "total item count must be conserved across the merge");

		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void sortingPlayerInventoryNeverTouchesEquippedArmor(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));

		player.getInventory().setItem(5, new ItemStack(Items.DIRT, 3));
		player.getInventory().setItem(2, new ItemStack(Items.DIRT, 10));

		ContainerAdapter.sort(player.getInventory(), 0, Inventory.INVENTORY_SIZE, SortConfig.defaultConfig());

		GameTestAssertions.assertTrue(helper, player.getItemBySlot(EquipmentSlot.HEAD).is(Items.DIAMOND_HELMET),
				"equipped helmet must not be touched by an inventory sort");
		GameTestAssertions.assertTrue(helper,
				player.getInventory().getItem(0).is(Items.DIRT) && player.getInventory().getItem(0).getCount() == 13,
				"expected the merged 13-dirt stack in the main inventory grid");

		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void sortingPlayerInventoryNeverTouchesHotbar(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();

		// Hotbar (slots 0-8) - deliberately unsorted, must stay untouched.
		player.getInventory().setItem(0, new ItemStack(Items.STONE, 1));
		player.getInventory().setItem(1, new ItemStack(Items.DIRT, 1));

		// Main storage (slots 9-35) - out of order, should get sorted.
		player.getInventory().setItem(15, new ItemStack(Items.STONE, 1));
		player.getInventory().setItem(9, new ItemStack(Items.DIRT, 1));

		ContainerAdapter.sort(player.getInventory(), Inventory.getSelectionSize(), Inventory.INVENTORY_SIZE,
				SortConfig.defaultConfig());

		GameTestAssertions.assertTrue(helper, player.getInventory().getItem(0).is(Items.STONE),
				"hotbar slot 0 must be untouched by an inventory sort");
		GameTestAssertions.assertTrue(helper, player.getInventory().getItem(1).is(Items.DIRT),
				"hotbar slot 1 must be untouched by an inventory sort");
		GameTestAssertions.assertTrue(helper, player.getInventory().getItem(9).is(Items.DIRT),
				"expected dirt sorted first in the main storage grid");

		helper.succeed();
	}
}
