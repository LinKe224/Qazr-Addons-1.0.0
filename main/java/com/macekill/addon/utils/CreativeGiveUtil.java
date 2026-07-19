/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ItemStack
 *  net.minecraft.Text
 *  net.minecraft.Packet
 *  net.minecraft.CreativeInventoryActionC2SPacket
 *  net.minecraft.MinecraftClient
 */
package com.macekill.addon.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.client.MinecraftClient;

public class CreativeGiveUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static String lastError = "";

    public static boolean give(ItemStack stack) {
        if (CreativeGiveUtil.mc.player == null || mc.getNetworkHandler() == null) {
            return false;
        }
        int slot = CreativeGiveUtil.mc.player.getInventory().getEmptySlot();
        if (slot < 0) {
            if (CreativeGiveUtil.mc.player != null) {
                CreativeGiveUtil.mc.player.sendMessage(Text.of((String)"\u00a7c[Qazr] \u80cc\u5305\u5df2\u6ee1\uff0c\u65e0\u6cd5\u751f\u6210"), false);
            }
            return false;
        }
        CreativeGiveUtil.mc.player.getInventory().setStack(slot, stack);
        int networkSlot = slot < 9 ? slot + 36 : slot;
        mc.getNetworkHandler().sendPacket((Packet)new CreativeInventoryActionC2SPacket(networkSlot, stack));
        return true;
    }

    public static void warn(String msg) {
        if (CreativeGiveUtil.mc.player != null && !msg.equals(lastError)) {
            lastError = msg;
            CreativeGiveUtil.mc.player.sendMessage(Text.of((String)("\u00a7c[Qazr] " + msg)), false);
        }
    }

    public static void info(String msg) {
        if (CreativeGiveUtil.mc.player != null) {
            CreativeGiveUtil.mc.player.sendMessage(Text.of((String)("\u00a7a[Qazr] " + msg)), true);
        }
    }

    public static void resetError() {
        lastError = "";
    }
}

