package com.branders.spawnermod.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

/** Assertion helpers so the test bodies stay the same across GameTest API versions. */
final class Check {

    private Check() {
    }

    static void isTrue(GameTestHelper helper, boolean condition, String message) {
        helper.assertTrue(condition, Component.literal(message));
    }

    static void isFalse(GameTestHelper helper, boolean condition, String message) {
        helper.assertFalse(condition, Component.literal(message));
    }
}
