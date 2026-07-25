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
public class ContainerAdapterGameTests {

	// FabricGameTest.EMPTY_STRUCTURE - these tests build their own scene via
	// setBlock()/makeMockPlayer(), so no pre-made structure template exists
	// for the (nonexistent) .snbt file the vanilla default would otherwise
	// look up by test name.
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void sortsAndMergesChestContents(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		// GameTestHelper.getBlockEntity(BlockPos, Class) (the cast-included
		// overload) is a later addition - 1.20.1 only has the plain-BlockPos
		// overload, so the cast is done by hand here.
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));

		chest.setItem(0, new ItemStack(Items.DIRT, 10));
		chest.setItem(1, new ItemStack(Items.STONE, 5));
		chest.setItem(2, new ItemStack(Items.DIRT, 20));

		ContainerAdapter.sort(chest, SortConfig.defaultConfig());

		helper.assertTrue(chest.getItem(0).is(Items.DIRT) && chest.getItem(0).getCount() == 30,
				"expected a single merged 30-dirt stack first (dirt < stone alphabetically)");
		helper.assertTrue(chest.getItem(1).is(Items.STONE) && chest.getItem(1).getCount() == 5,
				"expected the stone stack second");
		helper.assertTrue(chest.getItem(2).isEmpty(), "expected the freed slot to be empty");

		helper.succeed();
	}

	// FabricGameTest.EMPTY_STRUCTURE - these tests build their own scene via
	// setBlock()/makeMockPlayer(), so no pre-made structure template exists
	// for the (nonexistent) .snbt file the vanilla default would otherwise
	// look up by test name.
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void mergeRespectsMaxStackSizeInARealChest(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));

		chest.setItem(0, new ItemStack(Items.DIRT, 40));
		chest.setItem(1, new ItemStack(Items.DIRT, 40));

		ContainerAdapter.sort(chest, SortConfig.defaultConfig());

		int total = chest.getItem(0).getCount() + chest.getItem(1).getCount();
		helper.assertTrue(chest.getItem(0).getCount() <= 64 && chest.getItem(1).getCount() <= 64,
				"no resulting stack should exceed the item's max stack size");
		helper.assertTrue(total == 80, "total item count must be conserved across the merge");

		helper.succeed();
	}

	// FabricGameTest.EMPTY_STRUCTURE - these tests build their own scene via
	// setBlock()/makeMockPlayer(), so no pre-made structure template exists
	// for the (nonexistent) .snbt file the vanilla default would otherwise
	// look up by test name.
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void sortingPlayerInventoryNeverTouchesEquippedArmor(GameTestHelper helper) {
		// GameTestHelper.makeMockPlayer(GameType) (choosing a game mode) is a
		// later addition - 1.20.1 only has the no-arg overload.
		Player player = helper.makeMockPlayer();
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));

		player.getInventory().setItem(5, new ItemStack(Items.DIRT, 3));
		player.getInventory().setItem(2, new ItemStack(Items.DIRT, 10));

		ContainerAdapter.sort(player.getInventory(), 0, Inventory.INVENTORY_SIZE, SortConfig.defaultConfig());

		helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).is(Items.DIAMOND_HELMET),
				"equipped helmet must not be touched by an inventory sort");
		helper.assertTrue(player.getInventory().getItem(0).is(Items.DIRT) && player.getInventory().getItem(0).getCount() == 13,
				"expected the merged 13-dirt stack in the main inventory grid");

		helper.succeed();
	}

	// FabricGameTest.EMPTY_STRUCTURE - these tests build their own scene via
	// setBlock()/makeMockPlayer(), so no pre-made structure template exists
	// for the (nonexistent) .snbt file the vanilla default would otherwise
	// look up by test name.
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void sortingPlayerInventoryNeverTouchesHotbar(GameTestHelper helper) {
		Player player = helper.makeMockPlayer();

		// Hotbar (slots 0-8) - deliberately unsorted, must stay untouched.
		player.getInventory().setItem(0, new ItemStack(Items.STONE, 1));
		player.getInventory().setItem(1, new ItemStack(Items.DIRT, 1));

		// Main storage (slots 9-35) - out of order, should get sorted.
		player.getInventory().setItem(15, new ItemStack(Items.STONE, 1));
		player.getInventory().setItem(9, new ItemStack(Items.DIRT, 1));

		// Inventory.SELECTION_SIZE (the hotbar size) is private at 1.20.1 - it
		// was made public in a later version. 9 hotbar slots is a stable
		// vanilla invariant.
		ContainerAdapter.sort(player.getInventory(), 9, Inventory.INVENTORY_SIZE,
				SortConfig.defaultConfig());

		helper.assertTrue(player.getInventory().getItem(0).is(Items.STONE),
				"hotbar slot 0 must be untouched by an inventory sort");
		helper.assertTrue(player.getInventory().getItem(1).is(Items.DIRT),
				"hotbar slot 1 must be untouched by an inventory sort");
		helper.assertTrue(player.getInventory().getItem(9).is(Items.DIRT),
				"expected dirt sorted first in the main storage grid");

		helper.succeed();
	}
}
