package com.easysort.gametest;

import net.minecraft.gametest.framework.GameTestHelper;

/**
 * {@code GameTestHelper.assertTrue(boolean, Component)} is a convenience
 * addition from this project's later unobfuscated 26.x-era branches - on
 * 1.19.2, GameTestHelper only offers narrowly-typed assertXxx methods (block/
 * entity/container presence, ...) plus a plain {@link GameTestHelper#fail(String)}.
 * This is the direct equivalent: fail with the given message if the condition
 * doesn't hold.
 */
final class GameTestAssertions {

	private GameTestAssertions() {
	}

	static void assertTrue(GameTestHelper helper, boolean condition, String message) {
		if (!condition) {
			helper.fail(message);
		}
	}
}
