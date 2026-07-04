package com.easysort.gametest;

import com.easysort.core.config.SortConfig;
import com.easysort.platform.common.ContainerAdapter;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/**
 * Exercises ContainerAdapter against real block entities/registries, not the
 * hand-built SortableItem test doubles core.sort's own unit tests use - this
 * is what would have caught the equipment-slot bug from manual testing
 * automatically, instead of relying on a human noticing it in-game.
 */
public class ContainerAdapterGameTests {

	@GameTest
	public void sortsAndMergesChestContents(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = helper.getBlockEntity(new BlockPos(1, 1, 1), ChestBlockEntity.class);

		chest.setItem(0, new ItemStack(Items.DIRT, 10));
		chest.setItem(1, new ItemStack(Items.STONE, 5));
		chest.setItem(2, new ItemStack(Items.DIRT, 20));

		ContainerAdapter.sort(chest, SortConfig.defaultConfig());

		helper.assertTrue(chest.getItem(0).is(Items.DIRT) && chest.getItem(0).getCount() == 30,
				Component.literal("expected a single merged 30-dirt stack first (dirt < stone alphabetically)"));
		helper.assertTrue(chest.getItem(1).is(Items.STONE) && chest.getItem(1).getCount() == 5,
				Component.literal("expected the stone stack second"));
		helper.assertTrue(chest.getItem(2).isEmpty(), Component.literal("expected the freed slot to be empty"));

		helper.succeed();
	}

	@GameTest
	public void mergeRespectsMaxStackSizeInARealChest(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = helper.getBlockEntity(new BlockPos(1, 1, 1), ChestBlockEntity.class);

		chest.setItem(0, new ItemStack(Items.DIRT, 40));
		chest.setItem(1, new ItemStack(Items.DIRT, 40));

		ContainerAdapter.sort(chest, SortConfig.defaultConfig());

		int total = chest.getItem(0).getCount() + chest.getItem(1).getCount();
		helper.assertTrue(chest.getItem(0).getCount() <= 64 && chest.getItem(1).getCount() <= 64,
				Component.literal("no resulting stack should exceed the item's max stack size"));
		helper.assertTrue(total == 80, Component.literal("total item count must be conserved across the merge"));

		helper.succeed();
	}

	@GameTest
	public void sortingPlayerInventoryNeverTouchesEquippedArmor(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));

		player.getInventory().setItem(5, new ItemStack(Items.DIRT, 3));
		player.getInventory().setItem(2, new ItemStack(Items.DIRT, 10));

		ContainerAdapter.sort(player.getInventory(), 0, Inventory.INVENTORY_SIZE, SortConfig.defaultConfig());

		helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).is(Items.DIAMOND_HELMET),
				Component.literal("equipped helmet must not be touched by an inventory sort"));
		helper.assertTrue(player.getInventory().getItem(0).is(Items.DIRT) && player.getInventory().getItem(0).getCount() == 13,
				Component.literal("expected the merged 13-dirt stack in the main inventory grid"));

		helper.succeed();
	}

	@GameTest
	public void sortingPlayerInventoryNeverTouchesHotbar(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);

		// Hotbar (slots 0-8) - deliberately unsorted, must stay untouched.
		player.getInventory().setItem(0, new ItemStack(Items.STONE, 1));
		player.getInventory().setItem(1, new ItemStack(Items.DIRT, 1));

		// Main storage (slots 9-35) - out of order, should get sorted.
		player.getInventory().setItem(15, new ItemStack(Items.STONE, 1));
		player.getInventory().setItem(9, new ItemStack(Items.DIRT, 1));

		ContainerAdapter.sort(player.getInventory(), Inventory.SELECTION_SIZE, Inventory.INVENTORY_SIZE,
				SortConfig.defaultConfig());

		helper.assertTrue(player.getInventory().getItem(0).is(Items.STONE),
				Component.literal("hotbar slot 0 must be untouched by an inventory sort"));
		helper.assertTrue(player.getInventory().getItem(1).is(Items.DIRT),
				Component.literal("hotbar slot 1 must be untouched by an inventory sort"));
		helper.assertTrue(player.getInventory().getItem(9).is(Items.DIRT),
				Component.literal("expected dirt sorted first in the main storage grid"));

		helper.succeed();
	}
}
