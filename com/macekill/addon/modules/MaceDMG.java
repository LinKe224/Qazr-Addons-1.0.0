package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import java.lang.reflect.Field;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1802;
import net.minecraft.class_1934;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class MaceDMG
extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> fakeHeight;
    private final Setting<Integer> attackInterval;
    private final Setting<Boolean> globalDetection;
    private final Setting<Boolean> autoSwitch;
    private final Setting<Integer> normalPackets;
    private final Setting<Boolean> chatInfo;
    private long lastAttackTime;
    private float lastCooldown;

    public MaceDMG() {
        super(MaceKillAddon.CATEGORY, "\u5e73\u5730\u91cd\u9524", "\u624b\u6301\u91cd\u9524\u653b\u51fb\u65f6\u4f2a\u9020\u9ad8\u5ea6\u6253\u51fa\u6ee1\u4f24\u5bb3.\n\u652f\u6301\u5168\u5c40\u653b\u51fb\u68c0\u6d4b(\u81ea\u52a8\u52ab\u6301\u653b\u51fb\u4e8b\u4ef6)\u6216\u624b\u52a8\u89e6\u53d1.");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.fakeHeight = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u4f2a\u9020\u9ad8\u5ea6")).description("MaceDMG\u53d1\u9001\u7684\u9ad8\u7a7a\u4f4d\u7f6e\u9ad8\u5ea6(sqrt\u503c,\u5982\u221a500\u224822.36)")).defaultValue(22.36).min(1.0).max(50.0).sliderMax(35.0).build());
        this.attackInterval = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u653b\u51fb\u95f4\u9694")).description("\u653b\u51fb\u95f4\u9694(\u6beb\u79d2, \u6700\u5c0f25ms)")).defaultValue((Object)55)).min(25).max(2000).sliderMax(500).build());
        this.globalDetection = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u5168\u5c40\u653b\u51fb\u68c0\u6d4b")).description("ON:\u62e6\u622a\u6240\u6709\u653b\u51fb\u4e8b\u4ef6\u81ea\u52a8\u89e6\u53d1 | OFF:\u4ec5\u73a9\u5bb6\u624b\u52a8\u5de6\u952e\u653b\u51fb\u65f6\u89e6\u53d1")).defaultValue((Object)true)).build());
        this.autoSwitch = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u52a8\u5207\u6362\u91cd\u9524")).description("\u80cc\u5305\u4e2d\u6709\u91cd\u9524\u65f6\u81ea\u52a8\u5207\u6362\u5230\u4e3b\u624b")).defaultValue((Object)true)).build());
        this.normalPackets = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6b63\u5e38\u5305\u6570\u91cf")).description("\u9ad8\u7a7a\u5305\u4e4b\u524d\u53d1\u9001\u7684\u6b63\u5e38\u4f4d\u7f6e\u5305\u6570\u91cf(\u53c2\u8003Wurst:4)")).defaultValue((Object)4)).min(1).max(10).sliderMax(8).build());
        this.chatInfo = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u804a\u5929\u4fe1\u606f")).description("\u5728\u804a\u5929\u680f\u663e\u793a\u89e6\u53d1\u4fe1\u606f")).defaultValue((Object)false)).build());
        this.autoSubscribe = true;
    }

    public void onActivate() {
        this.lastAttackTime = 0L;
        this.lastCooldown = 0.0f;
        if (((Boolean)this.chatInfo.get()).booleanValue()) {
            this.info("\u00a7a\u5e73\u5730\u91cd\u9524\u5df2\u542f\u52a8! \u4f2a\u9020\u9ad8\u5ea6=" + String.format("%.1f", this.fakeHeight.get()) + " \u5168\u5c40\u68c0\u6d4b=" + ((Boolean)this.globalDetection.get() != false ? "ON" : "OFF"));
        }
    }

    public void onDeactivate() {
        if (((Boolean)this.chatInfo.get()).booleanValue()) {
            this.info("\u00a7c\u5e73\u5730\u91cd\u9524\u5df2\u5173\u95ed!");
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        long elapsed;
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null || mc.field_1687 == null) {
            return;
        }
        if (mc.field_1761.method_2920() == class_1934.field_9219) {
            return;
        }
        if (((Boolean)this.autoSwitch.get()).booleanValue() && !this.isHoldingMace((class_1657)player)) {
            this.switchToMace(mc);
        }
        if (!this.isHoldingMace((class_1657)player)) {
            return;
        }
        boolean shouldAttack = false;
        if (((Boolean)this.globalDetection.get()).booleanValue()) {
            float currentCooldown = player.method_7261(0.0f);
            if (this.lastCooldown > 0.5f && currentCooldown < 0.01f) {
                shouldAttack = true;
            }
            this.lastCooldown = currentCooldown;
        } else if (mc.field_1690.field_1886.method_1436()) {
            shouldAttack = true;
        }
        if (!shouldAttack) {
            return;
        }
        long now = System.currentTimeMillis();
        if (this.lastAttackTime == 0L) {
            this.lastAttackTime = now;
        }
        if ((elapsed = now - this.lastAttackTime) < (long)((Integer)this.attackInterval.get()).intValue()) {
            return;
        }
        this.lastAttackTime = now;
        this.performMaceDMG(mc);
    }

    private void performMaceDMG(class_310 mc) {
        class_746 player = mc.field_1724;
        double height = (Double)this.fakeHeight.get();
        for (int i = 0; i < (Integer)this.normalPackets.get(); ++i) {
            this.sendFakeY(mc, 0.0);
        }
        this.sendFakeY_air(mc, Math.sqrt(height));
        mc.field_1724.field_6017 = 0.0;
        if (((Boolean)this.chatInfo.get()).booleanValue()) {
            this.info("\u00a7b\u89e6\u53d1MaceDMG | height=" + String.format("%.1f", Math.sqrt(height)));
        }
    }

    private void sendFakeY(class_310 mc, double offset) {
        mc.field_1724.field_3944.method_52787((class_2596)new class_2828.class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318() + offset, mc.field_1724.method_23321(), true, mc.field_1724.field_5976));
    }

    private void sendFakeY_air(class_310 mc, double offset) {
        mc.field_1724.field_3944.method_52787((class_2596)new class_2828.class_2829(mc.field_1724.method_23317(), mc.field_1724.method_23318() + offset, mc.field_1724.method_23321(), false, mc.field_1724.field_5976));
    }

    private boolean isHoldingMace(class_1657 player) {
        return player.method_6047().method_31574(class_1802.field_49814) || player.method_6079().method_31574(class_1802.field_49814);
    }

    private void switchToMace(class_310 mc) {
        int i;
        class_746 player = mc.field_1724;
        if (player.method_6047().method_31574(class_1802.field_49814)) {
            return;
        }
        if (player.method_6079().method_31574(class_1802.field_49814)) {
            return;
        }
        for (i = 0; i < 9; ++i) {
            if (!player.method_31548().method_5438(i).method_31574(class_1802.field_49814)) continue;
            this.setSelectedSlot((class_1657)player, i);
            return;
        }
        for (i = 9; i < 36; ++i) {
            if (!player.method_31548().method_5438(i).method_31574(class_1802.field_49814)) continue;
            int syncId = player.field_7498.field_7763;
            int selected = this.getSelectedSlot((class_1657)player);
            mc.field_1761.method_2906(syncId, i, selected, class_1713.field_7791, (class_1657)player);
            return;
        }
    }

    private void setSelectedSlot(class_1657 player, int slot) {
        try {
            Field f = class_1661.class.getDeclaredField("selectedSlot");
            f.setAccessible(true);
            f.setInt(player.method_31548(), slot);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private int getSelectedSlot(class_1657 player) {
        try {
            Field f = class_1661.class.getDeclaredField("selectedSlot");
            f.setAccessible(true);
            return f.getInt(player.method_31548());
        }
        catch (Exception exception) {
            return 0;
        }
    }

    private void info(String msg) {
        ChatUtils.sendMsg((class_2561)class_2561.method_43470((String)("\u00a78[\u00a7b\u5e73\u5730\u91cd\u9524\u00a78] \u00a7f" + msg)));
    }
}
