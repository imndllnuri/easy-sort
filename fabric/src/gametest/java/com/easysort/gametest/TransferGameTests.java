package com.easysort.gametest;

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
 * Exercises ContainerAdapter.quickStack/restock against a real player and
 * chest, mirroring ContainerAdapterGameTests but for the transfer-between-
 * two-containers operations rather than sorting a single one.
 */
public class TransferGameTests {

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void quickStackMovesMatchingItemsFromPlayerIntoChest(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		// GameTestHelper.getBlockEntity(BlockPos, Class) (the cast-included
		// overload) is a later addition - 1.20.1 only has the plain-BlockPos
		// overload, so the cast is done by hand here.
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
		chest.setItem(0, new ItemStack(Items.DIRT, 10));

		// GameTestHelper.makeMockPlayer(GameType) is a later addition - 1.20.1
		// only has the no-arg overload.
		Player player = helper.makeMockPlayer();
		player.getInventory().setItem(0, new ItemStack(Items.DIRT, 20));
		player.getInventory().setItem(1, new ItemStack(Items.GOLD_INGOT, 5));

		int moved = ContainerAdapter.quickStack(player.getInventory(), 0, Inventory.INVENTORY_SIZE, chest);

		helper.assertTrue(moved == 20, "expected all 20 dirt to move");
		helper.assertTrue(chest.getItem(0).is(Items.DIRT) && chest.getItem(0).getCount() == 30,
				"chest's existing dirt stack should be topped up");
		helper.assertTrue(player.getInventory().getItem(0).isEmpty(),
				"player's dirt slot should be fully drained");
		helper.assertTrue(player.getInventory().getItem(1).is(Items.GOLD_INGOT) && player.getInventory().getItem(1).getCount() == 5,
				"gold was never in the chest, so it must not move");

		helper.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void quickStackNeverTouchesEquippedArmor(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
		chest.setItem(0, new ItemStack(Items.DIRT, 1));

		Player player = helper.makeMockPlayer();
		player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
		player.getInventory().setItem(0, new ItemStack(Items.DIRT, 5));

		ContainerAdapter.quickStack(player.getInventory(), 0, Inventory.INVENTORY_SIZE, chest);

		helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).is(Items.DIAMOND_HELMET),
				"equipped helmet must not be touched by quick-stack");

		helper.succeed();
	}

	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void restockToppedUpFromChestNeverCreatesNewStacks(GameTestHelper helper) {
		helper.setBlock(1, 1, 1, Blocks.CHEST);
		ChestBlockEntity chest = (ChestBlockEntity) helper.getBlockEntity(new BlockPos(1, 1, 1));
		chest.setItem(0, new ItemStack(Items.ARROW, 64));
		chest.setItem(1, new ItemStack(Items.EMERALD, 10));

		Player player = helper.makeMockPlayer();
		player.getInventory().setItem(0, new ItemStack(Items.ARROW, 5));

		int moved = ContainerAdapter.restock(chest, player.getInventory(), 0, Inventory.INVENTORY_SIZE);

		helper.assertTrue(moved == 59, "expected the partial arrow stack topped up to 64");
		helper.assertTrue(player.getInventory().getItem(0).is(Items.ARROW) && player.getInventory().getItem(0).getCount() == 64,
				"player's arrow stack should be full");
		helper.assertTrue(player.getInventory().getItem(1).isEmpty(),
				"restock must never place emeralds in an empty slot - player never carried any");

		helper.succeed();
	}
}
