package com.macekill.addon.utils;

import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2873;
import net.minecraft.class_310;

public class CreativeGiveUtil {
    private static final class_310 mc = class_310.method_1551();
    private static String lastError = "";

    public static boolean give(class_1799 stack) {
        if (CreativeGiveUtil.mc.field_1724 == null || mc.method_1562() == null) {
            return false;
        }
        int slot = CreativeGiveUtil.mc.field_1724.method_31548().method_7376();
        if (slot < 0) {
            if (CreativeGiveUtil.mc.field_1724 != null) {
                CreativeGiveUtil.mc.field_1724.method_7353(class_2561.method_30163((String)"\u00a7c[Qazr] \u80cc\u5305\u5df2\u6ee1\uff0c\u65e0\u6cd5\u751f\u6210"), false);
            }
            return false;
        }
        CreativeGiveUtil.mc.field_1724.method_31548().method_5447(slot, stack);
        int networkSlot = slot < 9 ? slot + 36 : slot;
        mc.method_1562().method_52787((class_2596)new class_2873(networkSlot, stack));
        return true;
    }

    public static void warn(String msg) {
        if (CreativeGiveUtil.mc.field_1724 != null && !msg.equals(lastError)) {
            lastError = msg;
            CreativeGiveUtil.mc.field_1724.method_7353(class_2561.method_30163((String)("\u00a7c[Qazr] " + msg)), false);
        }
    }

    public static void info(String msg) {
        if (CreativeGiveUtil.mc.field_1724 != null) {
            CreativeGiveUtil.mc.field_1724.method_7353(class_2561.method_30163((String)("\u00a7a[Qazr] " + msg)), true);
        }
    }

    public static void resetError() {
        lastError = "";
    }
}
