package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2374;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2868;

public class MaceKillModule
extends Module {
    private static Field selectedSlotField;
    private final SettingGroup sgGeneral;
    private final SettingGroup sgKill;
    private final SettingGroup sgDestory;
    private final Setting<Double> range;
    private final Setting<Double> moveDistance;
    private final Setting<Boolean> swingHand;
    private final Setting<Boolean> requireFullCooldown;
    private final Setting<Boolean> playerOnly;
    private final Setting<Integer> teleportDelay;
    private final Setting<Boolean> spamRotations;
    private final Setting<Boolean> autoTotem;
    private final Setting<Boolean> kehd;
    private final Setting<Boolean> predict;
    private final Setting<Integer> predictTicks;
    private final Setting<Boolean> enableArmorDestroy;
    private final Setting<Integer> ignoreArmorValue;
    private final Setting<List<String>> destroyHeights;
    private final Setting<List<String>> killHeights;
    private Phase phase;
    private int delayTicks;
    private class_243 originalPos;
    private class_243 targetPos;
    private class_1309 target;
    private boolean isArmorDestroyed;

    public MaceKillModule() {
        super(MaceKillAddon.CATEGORY, "macemiss", "\u77ac\u79fb\u5230\u76ee\u6807\u65c1\uff0cVClip\u8d77\u8df3\u653b\u51fb");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgKill = this.settings.createGroup("\u51fb\u6740\u9ad8\u5ea6");
        this.sgDestory = this.settings.createGroup("\u7834\u7532\u9ad8\u5ea6");
        this.range = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u8303\u56f4")).description("\u68c0\u6d4b\u5468\u56f4\u751f\u7269\u7684\u8ddd\u79bb")).defaultValue(20.0).min(1.0).max(200.0).sliderRange(1.0, 128.0).build());
        this.moveDistance = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u79fb\u52a8\u6b65\u957f")).description("\u6bcf\u4e2a\u79fb\u52a8\u5305\u7684\u6700\u5927\u8ddd\u79bb")).defaultValue(20.0).min(1.0).max(128.0).sliderRange(1.0, 128.0).build());
        this.swingHand = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6325\u624b")).description("\u653b\u51fb\u65f6\u5ba2\u6237\u7aef\u6325\u624b")).defaultValue((Object)false)).build());
        this.requireFullCooldown = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u9700\u8981\u6ee1\u51b7\u5374")).description("\u653b\u51fb\u9700\u8981\u6ee1\u51b7\u5374\u624d\u6267\u884c")).defaultValue((Object)false)).build());
        this.playerOnly = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u4ec5\u73a9\u5bb6")).description("\u4ec5\u5bf9\u73a9\u5bb6\u751f\u6548\uff0c\u5ffd\u7565\u5176\u4ed6\u751f\u7269")).defaultValue((Object)false)).build());
        this.teleportDelay = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u4f20\u9001\u5ef6\u8fdf")).description("\u4f20\u9001\u540e\u7b49\u5f85\u7684tick\u6570\uff0c0\u4e3a\u65e0\u5ef6\u8fdf")).defaultValue((Object)5)).range(0, 20).build());
        this.spamRotations = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u5783\u573e\u65cb\u8f6c\u5305")).description("\u5728\u7b2c\u4e00\u6b21\u4f20\u9001\u524d\u53d1\u90014\u4e2a\u65cb\u8f6c\u5305")).defaultValue((Object)false)).build());
        this.autoTotem = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u52a8\u5207\u56fe\u817e")).description("\u653b\u51fb\u7ed3\u675f\u540e\u81ea\u52a8\u6362\u56de\u56fe\u817e")).defaultValue((Object)false)).build());
        this.kehd = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("kehud")).description("\u540c\u65f6\u66f4\u65b0\u5ba2\u6237\u7aef\u4f4d\u7f6e")).defaultValue((Object)false)).build());
        this.predict = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u9884\u6d4b\u4f4d\u7f6e")).description("\u6839\u636e\u76ee\u6807\u901f\u5ea6\u9884\u6d4b\u5176\u4f4d\u7f6e")).defaultValue((Object)true)).build());
        this.predictTicks = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u9884\u6d4btick")).description("\u9884\u6d4b\u7684tick\u6570")).defaultValue((Object)5)).min(1).sliderMax(20).visible(() -> this.predict.get())).build());
        this.enableArmorDestroy = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u542f\u7528\u7834\u7532")).description("\u5148\u4f7f\u7528\u7834\u7532\u9ad8\u5ea6\u653b\u51fb\u76f4\u5230\u76ee\u6807\u62a4\u7532\u4e0d\u8db3")).defaultValue((Object)false)).build());
        this.ignoreArmorValue = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u7834\u7532\u9608\u503c")).description("\u76ee\u6807\u5269\u4f59\u62a4\u7532\u2264\u6b64\u503c\u65f6\u6539\u7528\u51fb\u6740\u9ad8\u5ea6")).defaultValue((Object)0)).min(0).max(4).sliderMax(4).visible(() -> this.enableArmorDestroy.get())).build());
        this.destroyHeights = this.sgDestory.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u7834\u7532\u9ad8\u5ea6\u5217\u8868")).description("\u7834\u574f\u62a4\u7532\u65f6\u4f7f\u7528\u7684\u9ad8\u5ea6")).defaultValue(new String[]{"30", "60"}).visible(() -> this.enableArmorDestroy.get())).build());
        this.killHeights = this.sgKill.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u51fb\u6740\u9ad8\u5ea6\u5217\u8868")).description("\u51fb\u6740\u65f6\u4f7f\u7528\u7684\u9ad8\u5ea6")).defaultValue(new String[]{"10", "20", "30"}).build());
        this.phase = Phase.IDLE;
    }

    public void onDeactivate() {
        this.phase = Phase.IDLE;
        this.delayTicks = 0;
        this.target = null;
        this.isArmorDestroyed = false;
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
            this.phase = Phase.IDLE;
            return;
        }
        if (((Boolean)this.autoTotem.get()).booleanValue()) {
            this.ensureTotem();
        }
        if (((Boolean)this.requireFullCooldown.get()).booleanValue() && this.mc.field_1724.method_7261(0.0f) < 1.0f) {
            return;
        }
        switch (this.phase.ordinal()) {
            case 0: {
                this.tickIdle();
                break;
            }
            case 1: {
                this.tickStartDelay();
                break;
            }
            case 2: {
                this.tickReturnDelay();
            }
        }
    }

    private void tickIdle() {
        this.target = this.findNearestTarget();
        if (this.target == null) {
            return;
        }
        this.originalPos = new class_243(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321());
        this.targetPos = this.predictPosition(this.target);
        if (((Boolean)this.spamRotations.get()).booleanValue()) {
            this.sendRotations(4);
        }
        this.doTpTo(this.targetPos);
        int delay = (Integer)this.teleportDelay.get();
        if (delay > 0) {
            this.phase = Phase.START_DELAY;
            this.delayTicks = delay;
        } else {
            this.executeAndReturn();
        }
    }

    private void tickStartDelay() {
        this.doTpTo(this.targetPos);
        if (((Boolean)this.kehd.get()).booleanValue()) {
            this.mc.field_1724.method_30634(this.targetPos.field_1352, this.targetPos.field_1351, this.targetPos.field_1350);
        }
        --this.delayTicks;
        if (this.delayTicks <= 0) {
            if (this.target == null || !this.target.method_5805() || this.mc.field_1724.method_5858((class_1297)this.target) > (Double)this.range.get() * (Double)this.range.get()) {
                this.doReturn();
            } else {
                this.executeAndReturn();
            }
        }
    }

    private void tickReturnDelay() {
        this.doReturn();
        --this.delayTicks;
        if (this.delayTicks <= 0) {
            this.phase = Phase.IDLE;
            this.target = null;
        }
    }

    private void executeAndReturn() {
        this.executeAttack();
        this.doReturn();
        int delay = (Integer)this.teleportDelay.get();
        if (delay > 0) {
            this.phase = Phase.RETURN_DELAY;
            this.delayTicks = delay;
        } else {
            this.phase = Phase.IDLE;
            this.target = null;
        }
    }

    private class_2338 findVclipHole(class_243 vec, double vclip) {
        class_2338 pos = class_2338.method_49638((class_2374)vec);
        if (vclip > 0.0) {
            int targetY;
            for (int y = targetY = pos.method_10264() + (int)vclip; y >= pos.method_10264(); --y) {
                class_2338 p1 = new class_2338(pos.method_10263(), y, pos.method_10260());
                class_2338 p2 = p1.method_10084();
                if (!this.isSafeBlock(p1) || !this.isSafeBlock(p2)) continue;
                return p1;
            }
        }
        return pos;
    }

    private boolean isSafeBlock(class_2338 pos) {
        return this.mc.field_1687.method_8320(pos).method_26215() && this.mc.field_1687.method_8316(pos).method_15769() && !this.mc.field_1687.method_8320(pos).method_27852(class_2246.field_10343);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void executeAttack() {
        List rawHeights;
        if (this.target == null || this.mc.method_1562() == null) {
            return;
        }
        if (((Boolean)this.enableArmorDestroy.get()).booleanValue() && this.needsArmorDestroy(this.target)) {
            rawHeights = (List)this.destroyHeights.get();
            this.isArmorDestroyed = false;
        } else {
            rawHeights = (List)this.killHeights.get();
            this.isArmorDestroyed = true;
        }
        List<Double> heights = this.parseHeights(rawHeights);
        if (heights.isEmpty()) {
            heights.add(20.0);
        }
        int oldSlot = this.switchToMace();
        try {
            if (oldSlot == -1) {
                return;
            }
            double baseX = this.mc.field_1724.method_23317();
            double baseY = this.mc.field_1724.method_23318();
            double baseZ = this.mc.field_1724.method_23321();
            for (double h : heights) {
                class_2338 vclipHole = this.findVclipHole(new class_243(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321()), h);
                class_243 vclipPos = class_243.method_24955((class_2382)vclipHole);
                this.doTpTo(vclipPos);
                this.sendMovePacket(this.targetPos.field_1352, this.targetPos.field_1351 + 0.5, this.targetPos.field_1350);
                this.sendMovePacket(baseX, baseY, baseZ);
                if (((Boolean)this.swingHand.get()).booleanValue()) {
                    this.mc.field_1724.method_6104(class_1268.field_5808);
                }
                this.attackEntity((class_1297)this.target);
            }
        }
        finally {
            if (oldSlot != -1) {
                if (((Boolean)this.autoTotem.get()).booleanValue()) {
                    int totemSlot = this.findTotemSlot();
                    if (totemSlot != -1) {
                        this.setSelectedSlot(this.mc.field_1724.method_31548(), totemSlot);
                        this.sendSlotPacket(totemSlot);
                    } else {
                        this.switchBack(oldSlot);
                    }
                } else {
                    this.switchBack(oldSlot);
                }
            }
        }
    }

    private void doTpTo(class_243 to) {
        class_243 from = new class_243(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23318(), this.mc.field_1724.method_23321());
        double dist = from.method_1022(to);
        double maxStep = (Double)this.moveDistance.get();
        if (!Double.isFinite(maxStep) || maxStep <= 0.0) {
            maxStep = 1.0;
        }
        int steps = (int)Math.ceil(dist / maxStep);
        for (int i = 1; i < steps; ++i) {
            double progress = (double)i / (double)steps;
            double x = from.field_1352 + (to.field_1352 - from.field_1352) * progress;
            double y = from.field_1351 + (to.field_1351 - from.field_1351) * progress;
            double z = from.field_1350 + (to.field_1350 - from.field_1350) * progress;
            this.sendMovePacket(x, y, z);
        }
        this.sendMovePacket(to.field_1352, to.field_1351, to.field_1350);
        if (((Boolean)this.kehd.get()).booleanValue()) {
            this.mc.field_1724.method_30634(to.field_1352, to.field_1351, to.field_1350);
        }
    }

    private void doReturn() {
        if (this.originalPos == null) {
            return;
        }
        this.doTpTo(this.originalPos);
    }

    private class_243 predictPosition(class_1309 entity) {
        if (!((Boolean)this.predict.get()).booleanValue()) {
            return new class_243(entity.method_23317(), entity.method_23318(), entity.method_23321());
        }
        class_243 pos = new class_243(entity.method_23317(), entity.method_23318(), entity.method_23321());
        class_243 vel = entity.method_18798();
        if (vel.field_1352 * vel.field_1352 + vel.field_1351 * vel.field_1351 + vel.field_1350 * vel.field_1350 < 1.0E-4) {
            return pos;
        }
        int ticks = (Integer)this.predictTicks.get();
        for (int i = 0; i < ticks; ++i) {
            class_2338 next = class_2338.method_49637((double)(pos.field_1352 + vel.field_1352), (double)pos.field_1351, (double)(pos.field_1350 + vel.field_1350));
            if (!this.isSafeBlock(next)) {
                vel = new class_243(0.0, vel.field_1351, 0.0);
            }
            pos = pos.method_1031(vel.field_1352, 0.0, vel.field_1350);
        }
        return pos;
    }

    private boolean needsArmorDestroy(class_1309 entity) {
        if (!(entity instanceof class_1657)) {
            return false;
        }
        class_1657 player = (class_1657)entity;
        int count = 0;
        for (int i = 36; i <= 39; ++i) {
            if (player.method_31548().method_5438(i).method_7960()) continue;
            ++count;
        }
        return count > (Integer)this.ignoreArmorValue.get();
    }

    private List<Double> parseHeights(List<String> raw) {
        ArrayList<Double> list = new ArrayList<Double>();
        for (String s : raw) {
            try {
                double v = Double.parseDouble(s.trim());
                if (!(v > 0.0)) continue;
                list.add(v);
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return list;
    }

    private void sendRotations(int count) {
        if (this.mc.method_1562() == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            this.mc.method_1562().method_52787((class_2596)new class_2828.class_2831(this.mc.field_1724.method_36454(), this.mc.field_1724.method_36455(), this.mc.field_1724.method_24828(), false));
        }
    }

    private void sendMovePacket(double x, double y, double z) {
        if (this.mc.method_1562() == null) {
            return;
        }
        this.mc.method_1562().method_52787((class_2596)new class_2828.class_2829(x, y, z, false, false));
    }

    private void sendSlotPacket(int slot) {
        if (this.mc.method_1562() == null) {
            return;
        }
        this.mc.method_1562().method_52787((class_2596)new class_2868(slot));
    }

    private void attackEntity(class_1297 target) {
        if (this.mc.method_1562() == null) {
            return;
        }
        this.mc.method_1562().method_52787((class_2596)class_2824.method_34206((class_1297)target, (boolean)this.mc.field_1724.method_5715()));
    }

    private class_1309 findNearestTarget() {
        if (this.mc.field_1687 == null) {
            return null;
        }
        class_1309 nearest = null;
        double best = (Double)this.range.get() * (Double)this.range.get();
        for (class_1297 e : this.mc.field_1687.method_18112()) {
            double d;
            class_1657 player;
            class_1309 living;
            if (!(e instanceof class_1309) || (living = (class_1309)e) == this.mc.field_1724 || !living.method_5805() || ((Boolean)this.playerOnly.get()).booleanValue() && !(living instanceof class_1657) || living instanceof class_1657 && ((player = (class_1657)living).method_68878() || player.method_7325() || !Friends.get().shouldAttack(player)) || !((d = this.mc.field_1724.method_5858((class_1297)living)) < best)) continue;
            best = d;
            nearest = living;
        }
        return nearest;
    }

    private int switchToMace() {
        if (this.mc.field_1724 == null) {
            return -1;
        }
        for (int i = 0; i < 9; ++i) {
            if (this.mc.field_1724.method_31548().method_5438(i).method_7909() != class_1802.field_49814) continue;
            int cur = this.getSelectedSlot();
            if (i != cur) {
                this.setSelectedSlot(this.mc.field_1724.method_31548(), i);
                this.sendSlotPacket(i);
            }
            return cur;
        }
        return -1;
    }

    private void switchBack(int slot) {
        if (this.mc.field_1724 == null) {
            return;
        }
        this.setSelectedSlot(this.mc.field_1724.method_31548(), slot);
        this.sendSlotPacket(slot);
    }

    private int findTotemSlot() {
        if (this.mc.field_1724 == null) {
            return -1;
        }
        for (int i = 0; i < 9; ++i) {
            if (this.mc.field_1724.method_31548().method_5438(i).method_7909() != class_1802.field_8288) continue;
            return i;
        }
        return -1;
    }

    private void ensureTotem() {
        if (this.mc.field_1724 == null || this.mc.method_1562() == null) {
            return;
        }
        int cur = this.getSelectedSlot();
        if (this.mc.field_1724.method_31548().method_5438(cur).method_7909() == class_1802.field_8288) {
            return;
        }
        int t = this.findTotemSlot();
        if (t != -1) {
            this.setSelectedSlot(this.mc.field_1724.method_31548(), t);
            this.sendSlotPacket(t);
        }
    }

    private int getSelectedSlot() {
        try {
            if (selectedSlotField == null) {
                selectedSlotField = class_1661.class.getDeclaredField("selectedSlot");
                selectedSlotField.setAccessible(true);
            }
            return selectedSlotField.getInt(this.mc.field_1724.method_31548());
        }
        catch (Exception e) {
            return 0;
        }
    }

    private void setSelectedSlot(class_1661 inv, int slot) {
        try {
            if (selectedSlotField == null) {
                selectedSlotField = class_1661.class.getDeclaredField("selectedSlot");
                selectedSlotField.setAccessible(true);
            }
            selectedSlotField.setInt(inv, slot);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public String getInfoString() {
        int count = 0;
        for (String s : (List)this.killHeights.get()) {
            try {
                if (!(Double.parseDouble(s.trim()) > 0.0)) continue;
                ++count;
            }
            catch (NumberFormatException numberFormatException) {}
        }
        return count > 0 ? count + "\u6b21\u653b\u51fb" : "\u5f85\u914d\u7f6e";
    }

    private static enum Phase {
        IDLE,
        START_DELAY,
        RETURN_DELAY;

    }
}
