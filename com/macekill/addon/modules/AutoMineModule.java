package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2356;
import net.minecraft.class_2374;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2521;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_3486;
import net.minecraft.class_638;

public class AutoMineModule
extends Module {
    private final SettingGroup sgGeneral;
    private final Setting<Double> scanRange;
    private final Setting<Boolean> autoMine;
    private final Setting<Integer> minY;
    private final Setting<Integer> stuckTime;
    private final Setting<Integer> abandonThreshold;
    private final Setting<Integer> safeDistance;
    private final Setting<Boolean> avoidCaves;
    private final Setting<Boolean> filterDangerOres;
    private final Setting<Boolean> chatInfo;
    private final SettingGroup sgOres;
    private final Setting<Boolean> mineCoal;
    private final Setting<Boolean> mineIron;
    private final Setting<Boolean> mineCopper;
    private final Setting<Boolean> mineGold;
    private final Setting<Boolean> mineRedstone;
    private final Setting<Boolean> mineLapis;
    private final Setting<Boolean> mineDiamond;
    private final Setting<Boolean> mineEmerald;
    private final Setting<Boolean> mineNetherQuartz;
    private final Setting<Boolean> mineNetherGold;
    private final Setting<Boolean> mineAncientDebris;
    private final SettingGroup sgRender;
    private final Setting<Boolean> renderOres;
    private final Setting<SettingColor> oreColor;
    private final Setting<SettingColor> targetColor;
    private final Setting<SettingColor> pathColor;
    private final Set<class_2338> orePositions;
    private class_2338 currentTarget;
    private final List<class_2338> waypoints;
    private int wpIndex;
    private Phase phase;
    private int scanCooldown;
    private int tickCounter;
    private class_2338 breakingPos;
    private int breakTicks;
    private int stuckTicks;
    private boolean isStuck;
    private class_2338 lastPos;
    private int targetLockTime;
    private int tunnelY;
    private boolean wasFlying;
    private Method getCpuLoadMethod;
    private int cpuCheckTick;
    private long perfPauseUntil;
    private final Map<class_2338, Long> abandonBlacklist;
    private final Map<class_2338, Long> dugPositions;
    private long lastOreMinedTime;
    private static Field selectedSlotField;

    public AutoMineModule() {
        super(MaceKillAddon.CATEGORY, "\u77ff\u7269\u8ffd\u8e2a", "Wurst TunnelHack\u98ce\u683c: \u6298\u7ebf\u5bfb\u8def+\u98de\u884c\u5782\u76f4+\u81ea\u52a8\u6316\u6398");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.scanRange = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u626b\u63cf\u8303\u56f4")).description("\u641c\u7d22\u77ff\u77f3\u7684\u6700\u5927\u534a\u5f84(\u683c)")).defaultValue(64.0).min(8.0).max(256.0).sliderMax(128.0).build());
        this.autoMine = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u52a8\u6316\u77ff")).description("\u81ea\u52a8\u5bfb\u8def+\u6316\u6398")).defaultValue((Object)true)).build());
        this.minY = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6700\u4f4eY\u5c42")).description("\u6b64\u5c42\u6570\u53ca\u4ee5\u4e0b\u7684\u65b9\u5757\u4e0d\u4f1a\u88ab\u5411\u4e0b\u6316\u6398")).defaultValue((Object)-64)).min(-64).max(320).build());
        this.stuckTime = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u5361\u4f4f\u5224\u5b9a")).description("\u591a\u4e45\u4e0d\u52a8\u7b97\u5361\u4f4f(tick)")).defaultValue((Object)60)).min(10).max(200).build());
        this.abandonThreshold = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u653e\u5f03\u9608\u503c")).description("\u4e0a\u4e00\u4e2a\u77ff\u6316\u5b8c\u540e\u8d85\u65f6\u672a\u5230\u8fbe\u5219\u9ed1\u540d\u53553\u5206\u949f(tick)")).defaultValue((Object)200)).min(20).max(1200).build());
        this.safeDistance = this.sgGeneral.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u5b89\u5168\u8ddd\u79bb")).description("\u8eb2\u907f\u6d41\u4f53\u7684\u6700\u5c0f\u8ddd\u79bb(\u683c)")).defaultValue((Object)5)).min(2).max(15).build());
        this.avoidCaves = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8eb2\u907f\u6d1e\u7a74")).description("\u96a7\u9053\u907f\u5f00\u5929\u7136\u7a7a\u6c14/\u6d1e\u7a74(\u4f1a\u964d\u4f4e\u5bfb\u8def\u6210\u529f\u7387)")).defaultValue((Object)false)).build());
        this.filterDangerOres = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6392\u9664\u5371\u9669\u533a\u77ff\u7269")).description("\u4e0d\u6316\u6398\u5b89\u5168\u8ddd\u79bb\u5185\u9760\u8fd1\u6d41\u4f53/\u6d1e\u7a74\u7684\u77ff\u7269")).defaultValue((Object)true)).build());
        this.chatInfo = this.sgGeneral.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u804a\u5929\u4fe1\u606f")).description("\u5728\u804a\u5929\u680f\u8f93\u51fa\u72b6\u6001")).defaultValue((Object)false)).build());
        this.sgOres = this.settings.createGroup("\u77ff\u7269\u5217\u8868");
        this.mineCoal = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u7164\u77ff")).defaultValue((Object)true)).build());
        this.mineIron = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u94c1\u77ff")).defaultValue((Object)true)).build());
        this.mineCopper = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u94dc\u77ff")).defaultValue((Object)true)).build());
        this.mineGold = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u91d1\u77ff")).defaultValue((Object)true)).build());
        this.mineRedstone = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u7ea2\u77f3\u77ff")).defaultValue((Object)true)).build());
        this.mineLapis = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u9752\u91d1\u77f3")).defaultValue((Object)true)).build());
        this.mineDiamond = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u94bb\u77f3\u77ff")).defaultValue((Object)true)).build());
        this.mineEmerald = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u7eff\u5b9d\u77f3")).defaultValue((Object)true)).build());
        this.mineNetherQuartz = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u4e0b\u754c\u77f3\u82f1")).defaultValue((Object)true)).build());
        this.mineNetherGold = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u4e0b\u754c\u91d1\u77ff")).defaultValue((Object)true)).build());
        this.mineAncientDebris = this.sgOres.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8fdc\u53e4\u6b8b\u9ab8")).defaultValue((Object)true)).build());
        this.sgRender = this.settings.createGroup("\u6e32\u67d3");
        this.renderOres = this.sgRender.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6e32\u67d3\u77ff\u77f3")).defaultValue((Object)true)).build());
        this.oreColor = this.sgRender.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)new ColorSetting.Builder().name("\u77ff\u77f3\u989c\u8272")).defaultValue(new SettingColor(255, 255, 0, 200)).visible(() -> this.renderOres.get())).build());
        this.targetColor = this.sgRender.add((Setting)((ColorSetting.Builder)new ColorSetting.Builder().name("\u76ee\u6807\u989c\u8272")).defaultValue(new SettingColor(0, 255, 0, 200)).build());
        this.pathColor = this.sgRender.add((Setting)((ColorSetting.Builder)new ColorSetting.Builder().name("\u8def\u5f84\u989c\u8272")).defaultValue(new SettingColor(100, 200, 255, 180)).build());
        this.orePositions = new HashSet<class_2338>();
        this.waypoints = new ArrayList<class_2338>();
        this.phase = Phase.IDLE;
        this.scanCooldown = 60;
        this.abandonBlacklist = new HashMap<class_2338, Long>();
        this.dugPositions = new HashMap<class_2338, Long>();
        this.autoSubscribe = true;
    }

    public void onActivate() {
        this.orePositions.clear();
        this.currentTarget = null;
        this.waypoints.clear();
        this.wpIndex = 0;
        this.phase = Phase.IDLE;
        this.scanCooldown = 60;
        this.tickCounter = 0;
        this.breakingPos = null;
        this.breakTicks = 0;
        this.stuckTicks = 0;
        this.isStuck = false;
        this.targetLockTime = 0;
        this.abandonBlacklist.clear();
        this.dugPositions.clear();
        this.lastOreMinedTime = 0L;
        this.lastPos = this.mc.field_1724 != null ? this.mc.field_1724.method_24515() : null;
        this.wasFlying = this.mc.field_1724 != null && this.mc.field_1724.method_31549().field_7479;
        try {
            this.getCpuLoadMethod = ManagementFactory.getOperatingSystemMXBean().getClass().getMethod("getProcessCpuLoad", new Class[0]);
        }
        catch (Exception ignored) {
            this.getCpuLoadMethod = null;
        }
    }

    public void onDeactivate() {
        this.orePositions.clear();
        this.waypoints.clear();
        this.releaseControls();
        if (!this.wasFlying) {
            this.mc.field_1724.method_31549().field_7479 = false;
            this.mc.field_1724.method_31549().field_7478 = false;
        }
    }

    public String getInfoString() {
        if (this.isStuck) {
            return "\u6e05\u7406...";
        }
        if (this.phase == Phase.WALK_XZ && this.currentTarget != null) {
            return "\u2192" + this.currentTarget.method_23854();
        }
        if (this.phase == Phase.FLY_UP) {
            return "\u98de\u884c\u4e0a\u5347...";
        }
        if (this.phase == Phase.DIG_DOWN) {
            return "\u9636\u68af\u4e0b\u964d...";
        }
        if (this.phase == Phase.MINE_ORE) {
            return "\u6316\u6398\u77ff\u77f3...";
        }
        if (this.phase == Phase.RETURN_TO_TUNNEL) {
            return "\u8fd4\u56de\u96a7\u9053...";
        }
        return this.orePositions.size() + " ore(s)";
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        long elapsed;
        class_2680 ts;
        class_2338 nearest;
        if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
            return;
        }
        if (this.mc.field_1724.method_29504() || this.mc.field_1724.method_6032() <= 0.0f) {
            this.toggle();
            return;
        }
        ++this.tickCounter;
        if (this.tickCounter % 100 == 0) {
            long now = System.currentTimeMillis();
            this.abandonBlacklist.entrySet().removeIf(e -> (Long)e.getValue() < now);
            this.dugPositions.entrySet().removeIf(e -> (Long)e.getValue() < now);
        }
        if (this.scanCooldown-- <= 0) {
            this.scanCooldown = 60;
            this.scan();
        }
        this.orePositions.removeIf(p -> {
            class_2680 s = this.mc.field_1687.method_8320(p);
            return !this.isTargetOre(s.method_26204()) || s.method_26215();
        });
        class_2338 class_23382 = nearest = this.orePositions.isEmpty() ? null : (class_2338)this.orePositions.stream().filter(p -> !this.abandonBlacklist.containsKey(p)).filter(p -> !this.dugPositions.containsKey(p)).min(Comparator.comparingDouble(p -> p.method_19770((class_2374)this.mc.field_1724.method_33571()))).orElse(null);
        if (this.currentTarget != null && (!this.isTargetOre((ts = this.mc.field_1687.method_8320(this.currentTarget)).method_26204()) || ts.method_26215())) {
            this.lastOreMinedTime = System.currentTimeMillis();
            this.currentTarget = null;
            this.waypoints.clear();
            this.wpIndex = 0;
            this.phase = Phase.IDLE;
            this.targetLockTime = 0;
            this.breakingPos = null;
            this.breakTicks = 0;
            this.returnIfFlying();
            this.notify("\u77ff\u77f3\u5df2\u91c7\u6398\u5b8c\u6bd5");
        }
        if (this.currentTarget != null && this.lastOreMinedTime > 0L && (elapsed = System.currentTimeMillis() - this.lastOreMinedTime) > (long)((Integer)this.abandonThreshold.get()).intValue() * 50L) {
            this.blacklistWithNeighbors(this.currentTarget);
            this.orePositions.remove(this.currentTarget);
            this.currentTarget = null;
            this.waypoints.clear();
            this.wpIndex = 0;
            this.phase = Phase.IDLE;
            this.lastOreMinedTime = 0L;
            this.returnIfFlying();
            this.notify("\u653e\u5f03\u76ee\u6807,\u9ed1\u540d\u53553\u5206\u949f");
        }
        if (this.currentTarget == null && nearest != null) {
            this.currentTarget = nearest;
            this.targetLockTime = 0;
            this.planWaypoints();
            if (this.waypoints.isEmpty()) {
                this.currentTarget = null;
            } else if (((Boolean)this.autoMine.get()).booleanValue()) {
                this.wpIndex = 0;
                int pY = this.mc.field_1724.method_24515().method_10264();
                if (Math.abs(this.currentTarget.method_10264() - pY) <= 1) {
                    this.phase = Phase.WALK_XZ;
                } else if (this.currentTarget.method_10264() != pY) {
                    this.phase = Phase.WALK_XZ;
                }
            }
        }
        class_2338 curPos = this.mc.field_1724.method_24515();
        if (this.lastPos != null && curPos.equals((Object)this.lastPos)) {
            ++this.stuckTicks;
        } else {
            this.stuckTicks = 0;
            this.isStuck = false;
        }
        this.lastPos = curPos;
        if (this.stuckTicks >= (Integer)this.stuckTime.get()) {
            this.isStuck = true;
        }
        if (this.isStuck) {
            this.doClearing();
            return;
        }
        if (!((Boolean)this.autoMine.get()).booleanValue() || this.currentTarget == null) {
            return;
        }
        if (this.perfPauseUntil > 0L) {
            if (System.currentTimeMillis() < this.perfPauseUntil) {
                return;
            }
            this.perfPauseUntil = 0L;
            this.cpuCheckTick = 0;
            this.notify("\u5bfb\u8def\u5df2\u6062\u590d");
        }
        if (this.getCpuLoadMethod != null && ++this.cpuCheckTick >= 100) {
            this.cpuCheckTick = 0;
            try {
                double cpuLoad = (Double)this.getCpuLoadMethod.invoke((Object)ManagementFactory.getOperatingSystemMXBean(), new Object[0]);
                if (cpuLoad > 0.8) {
                    if (this.currentTarget != null) {
                        this.blacklistWithNeighbors(this.currentTarget);
                        this.orePositions.remove(this.currentTarget);
                    }
                    this.currentTarget = null;
                    this.waypoints.clear();
                    this.wpIndex = 0;
                    this.phase = Phase.IDLE;
                    this.targetLockTime = 0;
                    this.lastOreMinedTime = 0L;
                    this.breakingPos = null;
                    this.breakTicks = 0;
                    this.mc.field_1761.method_2925();
                    this.perfPauseUntil = System.currentTimeMillis() + 5000L;
                    this.notify(String.format("CPU\u8fc7\u8f7d(%.0f%%),\u6682\u505c5\u79d2", cpuLoad * 100.0));
                    return;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (++this.targetLockTime > 1200) {
            this.blacklistWithNeighbors(this.currentTarget);
            this.orePositions.remove(this.currentTarget);
            this.currentTarget = null;
            this.waypoints.clear();
            this.wpIndex = 0;
            this.phase = Phase.IDLE;
            this.targetLockTime = 0;
            this.lastOreMinedTime = 0L;
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
            this.notify("\u65e0\u6cd5\u63a5\u8fd1,\u9ed1\u540d\u53553\u5206\u949f");
            return;
        }
        switch (this.phase.ordinal()) {
            case 1: {
                this.disableFlight();
                this.walkXZPhase();
                break;
            }
            case 2: {
                this.enableFlight();
                this.flyUpPhase();
                break;
            }
            case 3: {
                this.disableFlight();
                this.digDownPhase();
                break;
            }
            case 4: {
                this.disableFlight();
                this.mineOrePhase();
                break;
            }
            case 5: {
                this.returnToTunnelPhase();
            }
        }
    }

    private void planWaypoints() {
        this.waypoints.clear();
        class_2338 from = this.mc.field_1724.method_24515();
        class_2338 to = this.currentTarget;
        to = this.findAirAdjacent(to);
        this.tunnelY = from.method_10264();
        class_2338 hTarget = new class_2338(to.method_10263(), this.tunnelY, to.method_10260());
        List<class_2338> hPath = this.findHorizontalPath(from, hTarget);
        this.waypoints.addAll(hPath);
        this.waypoints.removeIf(wp -> this.isDangerZone((class_2338)wp, this.tunnelY));
        if (to.method_10264() != this.tunnelY) {
            this.waypoints.add(to);
        }
        if (!this.waypoints.isEmpty() && this.waypoints.get(0).equals((Object)from)) {
            this.waypoints.remove(0);
        }
        if (this.waypoints.isEmpty()) {
            this.blacklistAndNext("\u65e0\u6cd5\u89c4\u5212\u5b89\u5168\u8def\u5f84");
            return;
        }
        this.wpIndex = 0;
    }

    private List<class_2338> findHorizontalPath(class_2338 from, class_2338 to) {
        int y = from.method_10264();
        int[][] dirs = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        HashMap<Long, Float> gScore = new HashMap<Long, Float>();
        HashMap<Long, Long> parent = new HashMap<Long, Long>();
        PriorityQueue<Node> open = new PriorityQueue<Node>();
        long startKey = this.key(from);
        long goalKey = this.key(to);
        gScore.put(startKey, Float.valueOf(0.0f));
        open.offer(new Node(startKey, 0.0f, this.heuristic(from, to)));
        int maxExpand = 6000;
        int expanded = 0;
        long goalFound = -1L;
        while (!open.isEmpty() && expanded < maxExpand) {
            Node cur = (Node)open.poll();
            if (cur.key == goalKey) {
                goalFound = cur.key;
                break;
            }
            ++expanded;
            class_2338 cp = this.unkey(cur.key, y);
            if (!cp.equals((Object)from) && this.isDangerZone(cp, y)) continue;
            for (int[] d : dirs) {
                float tentativeG;
                class_2338 np = cp.method_10069(d[0], 0, d[1]);
                long nk = this.key(np);
                if (this.isDangerZone(np, y) || !((tentativeG = gScore.getOrDefault(cur.key, Float.valueOf(Float.MAX_VALUE)).floatValue() + 1.0f) < gScore.getOrDefault(nk, Float.valueOf(Float.MAX_VALUE)).floatValue())) continue;
                gScore.put(nk, Float.valueOf(tentativeG));
                parent.put(nk, cur.key);
                open.offer(new Node(nk, tentativeG, tentativeG + this.heuristic(np, to)));
            }
        }
        ArrayList<class_2338> path = new ArrayList<class_2338>();
        if (goalFound < 0L) {
            return this.fallbackPath(from, to, y);
        }
        long pKey = goalFound;
        while (pKey != startKey) {
            class_2338 bp = this.unkey(pKey, y);
            path.add(bp);
            pKey = parent.getOrDefault(pKey, startKey);
        }
        path.add(from);
        Collections.reverse(path);
        return this.compressPath(path, y);
    }

    private List<class_2338> compressPath(List<class_2338> path, int y) {
        if (path.size() <= 2) {
            return path;
        }
        ArrayList<class_2338> result = new ArrayList<class_2338>();
        result.add(path.get(0));
        int anchor = 0;
        for (int i = 2; i < path.size(); ++i) {
            if (this.canWalkStraight(path.get(anchor), path.get(i), y)) continue;
            result.add(path.get(i - 1));
            anchor = i - 1;
        }
        result.add(path.get(path.size() - 1));
        return result;
    }

    private boolean canWalkStraight(class_2338 a, class_2338 b, int y) {
        int dx = Integer.signum(b.method_10263() - a.method_10263());
        int dz = Integer.signum(b.method_10260() - a.method_10260());
        if (dx != 0 && dz != 0) {
            return false;
        }
        if (dx == 0 && dz == 0) {
            return true;
        }
        int steps = dx != 0 ? Math.abs(b.method_10263() - a.method_10263()) : Math.abs(b.method_10260() - a.method_10260());
        for (int i = 1; i <= steps; ++i) {
            if (!this.isDangerZone(a.method_10069(dx * i, 0, dz * i), y)) continue;
            return false;
        }
        return true;
    }

    private List<class_2338> fallbackPath(class_2338 from, class_2338 to, int y) {
        ArrayList<class_2338> path = new ArrayList<class_2338>();
        path.add(from);
        int dx = Integer.signum(to.method_10263() - from.method_10263());
        int dz = Integer.signum(to.method_10260() - from.method_10260());
        int stepsX = Math.abs(to.method_10263() - from.method_10263());
        int stepsZ = Math.abs(to.method_10260() - from.method_10260());
        block0: for (int i = 1; i <= stepsX; ++i) {
            class_2338 p = from.method_10069(dx * i, 0, 0);
            if (!this.isDangerZone(p, y)) {
                path.add(p);
                continue;
            }
            for (int s = 1; s <= 4; ++s) {
                class_2338 det = p.method_10069(0, 0, s);
                if (!this.isDangerZone(det, y)) {
                    path.add(det);
                    path.add(det.method_10069(dx, 0, 0));
                    continue block0;
                }
                det = p.method_10069(0, 0, -s);
                if (this.isDangerZone(det, y)) continue;
                path.add(det);
                path.add(det.method_10069(dx, 0, 0));
                continue block0;
            }
        }
        class_2338 afterX = new class_2338(to.method_10263(), y, from.method_10260());
        block2: for (int i = 1; i <= stepsZ; ++i) {
            class_2338 p = afterX.method_10069(0, 0, dz * i);
            if (!this.isDangerZone(p, y)) {
                path.add(p);
                continue;
            }
            for (int s = 1; s <= 4; ++s) {
                class_2338 det = p.method_10069(s, 0, 0);
                if (!this.isDangerZone(det, y)) {
                    path.add(det);
                    path.add(det.method_10069(0, 0, dz));
                    continue block2;
                }
                det = p.method_10069(-s, 0, 0);
                if (this.isDangerZone(det, y)) continue;
                path.add(det);
                path.add(det.method_10069(0, 0, dz));
                continue block2;
            }
        }
        return path;
    }

    private boolean isDangerZone(class_2338 pos, int yLevel) {
        int safeR = (Integer)this.safeDistance.get();
        int minY = (Integer)this.minY.get();
        boolean checkCaves = (Boolean)this.avoidCaves.get();
        for (int dx = -safeR; dx <= safeR; ++dx) {
            for (int dy = -1; dy <= 2; ++dy) {
                for (int dz = -safeR; dz <= safeR; ++dz) {
                    class_2338 check = pos.method_10069(dx, yLevel + dy, dz);
                    if (check.method_10264() <= minY) continue;
                    class_2680 s = this.mc.field_1687.method_8320(check);
                    if (this.isFluid(s)) {
                        return true;
                    }
                    if (!checkCaves || !s.method_26215()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private long key(class_2338 p) {
        return (long)p.method_10263() << 32 | (long)p.method_10260() & 0xFFFFFFFFL;
    }

    private class_2338 unkey(long k, int y) {
        return new class_2338((int)(k >> 32), y, (int)k);
    }

    private float heuristic(class_2338 a, class_2338 b) {
        return Math.abs(a.method_10263() - b.method_10263()) + Math.abs(a.method_10260() - b.method_10260());
    }

    private class_2338 findAirAdjacent(class_2338 ore) {
        class_2338 best = ore;
        double bestDist = Double.MAX_VALUE;
        class_243 eye = this.mc.field_1724 != null ? this.mc.field_1724.method_33571() : class_243.field_1353;
        for (class_2350 d : class_2350.values()) {
            double dist;
            class_2338 adj = ore.method_10093(d);
            if (!this.mc.field_1687.method_8320(adj).method_26215() && !this.isReplaceable(this.mc.field_1687.method_8320(adj)) || !((dist = adj.method_19770((class_2374)eye)) < bestDist)) continue;
            bestDist = dist;
            best = adj;
        }
        if (bestDist == Double.MAX_VALUE) {
            for (class_2350 d : class_2350.values()) {
                double dist = ore.method_10093(d).method_19770((class_2374)eye);
                if (!(dist < bestDist)) continue;
                bestDist = dist;
                best = ore.method_10093(d);
            }
        }
        return best;
    }

    private void walkXZPhase() {
        boolean frontDanger;
        boolean zSeg;
        boolean atX;
        if (this.wpIndex >= this.waypoints.size()) {
            int pY = this.mc.field_1724.method_24515().method_10264();
            int tY = this.currentTarget.method_10264();
            if (tY > pY + 1) {
                this.phase = Phase.FLY_UP;
                this.enableFlight();
                return;
            }
            if (tY < pY - 1) {
                this.phase = Phase.DIG_DOWN;
                return;
            }
            this.phase = Phase.MINE_ORE;
            return;
        }
        class_2338 wp = this.waypoints.get(this.wpIndex);
        class_2338 feet = this.mc.field_1724.method_24515();
        boolean atZ = feet.method_10260() == wp.method_10260();
        boolean bl = atX = feet.method_10263() == wp.method_10263();
        if (atX && atZ) {
            ++this.wpIndex;
            if (this.wpIndex < this.waypoints.size() && this.isDangerZone(this.waypoints.get(this.wpIndex), feet.method_10264())) {
                this.planWaypoints();
                if (this.waypoints.isEmpty() || this.wpIndex >= this.waypoints.size()) {
                    this.blacklistAndNext("\u4e0b\u4e00\u8282\u70b9\u4e0d\u5b89\u5168");
                    return;
                }
                this.wpIndex = 0;
            }
            return;
        }
        boolean xSeg = wp.method_10263() != feet.method_10263() && wp.method_10260() == feet.method_10260();
        boolean bl2 = zSeg = wp.method_10260() != feet.method_10260() && wp.method_10263() == feet.method_10263();
        if (!xSeg && !zSeg) {
            ++this.wpIndex;
            return;
        }
        int stepX = xSeg ? Integer.signum(wp.method_10263() - feet.method_10263()) : 0;
        int stepZ = zSeg ? Integer.signum(wp.method_10260() - feet.method_10260()) : 0;
        class_243 faceTarget = new class_243((double)(feet.method_10263() + stepX) + 0.5, (double)feet.method_10264() + 0.5, (double)(feet.method_10260() + stepZ) + 0.5);
        this.face(faceTarget);
        class_2338 frontLow = feet.method_10069(stepX, 0, stepZ);
        class_2338 frontHigh = frontLow.method_10084();
        if (frontLow.method_10264() <= (Integer)this.minY.get() || frontHigh.method_10264() <= (Integer)this.minY.get()) {
            this.blacklistAndNext("\u5230\u8fbe\u6700\u4f4eY\u5c42\u9650\u5236");
            return;
        }
        ArrayList<class_2338> toDig = new ArrayList<class_2338>();
        if (this.isBlocking(this.mc.field_1687.method_8320(frontLow))) {
            toDig.add(frontLow);
        }
        if (this.isBlocking(this.mc.field_1687.method_8320(frontHigh))) {
            toDig.add(frontHigh);
        }
        if (frontDanger = this.isDangerZone(frontLow, feet.method_10264())) {
            if (this.isBlocking(this.mc.field_1687.method_8320(frontLow))) {
                this.blacklistBlock(frontLow);
            }
            this.planWaypoints();
            if (this.waypoints.isEmpty() || this.wpIndex >= this.waypoints.size()) {
                this.blacklistAndNext("\u8def\u5f84\u8fdb\u5165\u5371\u9669\u533a");
                return;
            }
            this.wpIndex = 0;
            this.mc.field_1690.field_1894.method_23481(false);
            return;
        }
        if (this.breakingPos != null) {
            toDig.clear();
            toDig.add(this.breakingPos);
        }
        if (!toDig.isEmpty()) {
            class_2338 dig = (class_2338)toDig.get(0);
            if (toDig.size() == 2) {
                double dHigh;
                double dLow = this.mc.field_1724.method_33571().method_1025(class_243.method_24953((class_2382)frontLow));
                dig = dLow < (dHigh = this.mc.field_1724.method_33571().method_1025(class_243.method_24953((class_2382)frontHigh))) ? frontLow : frontHigh;
            }
            this.doDigBlock(dig);
        } else {
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
            this.mc.field_1690.field_1894.method_23481(true);
        }
    }

    private void flyUpPhase() {
        class_2338 feet = this.mc.field_1724.method_24515();
        int tY = this.currentTarget.method_10264();
        if (feet.method_10264() >= tY - 1) {
            this.disableFlight();
            this.phase = Phase.MINE_ORE;
            return;
        }
        for (int dy = 1; dy <= tY - feet.method_10264() + 1; ++dy) {
            class_2338 check = new class_2338(feet.method_10263(), feet.method_10264() + dy, feet.method_10260());
            if (check.method_10264() <= (Integer)this.minY.get()) {
                this.blacklistAndNext("\u5230\u8fbe\u6700\u4f4eY\u5c42");
                return;
            }
            if (!this.isBlocking(this.mc.field_1687.method_8320(check))) continue;
            this.face(class_243.method_24953((class_2382)check));
            this.doDigBlock(check);
            this.enableFlight();
            return;
        }
        this.enableFlight();
        class_243 vel = this.mc.field_1724.method_18798();
        this.mc.field_1724.method_18800(vel.field_1352, 0.5, vel.field_1350);
    }

    private void digDownPhase() {
        class_2338 feet = this.mc.field_1724.method_24515();
        int tY = this.currentTarget.method_10264();
        if (feet.method_10264() <= tY + 1) {
            this.phase = Phase.MINE_ORE;
            return;
        }
        for (int dy = 1; dy <= feet.method_10264() - tY; ++dy) {
            class_2338 check = new class_2338(feet.method_10263(), feet.method_10264() - dy, feet.method_10260());
            if (check.method_10264() <= (Integer)this.minY.get()) {
                this.blacklistAndNext("\u5230\u8fbe\u6700\u4f4eY\u5c42");
                return;
            }
            if (!this.isBlocking(this.mc.field_1687.method_8320(check))) continue;
            this.face(class_243.method_24953((class_2382)check));
            this.doDigBlock(check);
            return;
        }
        this.mc.field_1690.field_1894.method_23481(true);
        this.mc.field_1690.field_1832.method_23481(true);
    }

    private void mineOrePhase() {
        class_2338 ore = this.currentTarget;
        if (ore == null) {
            this.phase = Phase.IDLE;
            return;
        }
        class_2680 s = this.mc.field_1687.method_8320(ore);
        if (!this.isTargetOre(s.method_26204()) || s.method_26215()) {
            this.lastOreMinedTime = System.currentTimeMillis();
            this.currentTarget = null;
            this.waypoints.clear();
            this.wpIndex = 0;
            this.targetLockTime = 0;
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
            this.phase = Phase.RETURN_TO_TUNNEL;
            return;
        }
        if (this.breakingPos != null && this.breakingPos.equals((Object)ore)) {
            if (this.breakTicks++ > 200) {
                this.blacklistBlock(ore);
                this.breakingPos = null;
                this.breakTicks = 0;
                this.mc.field_1761.method_2925();
                this.planWaypoints();
                return;
            }
            this.mc.field_1761.method_2902(ore, this.bestFace(ore));
            this.face(class_243.method_24953((class_2382)ore));
            return;
        }
        this.face(class_243.method_24953((class_2382)ore));
        this.switchToPick();
        this.doDigBlock(ore);
    }

    private void returnToTunnelPhase() {
        int pY = this.mc.field_1724.method_24515().method_10264();
        if (Math.abs(pY - this.tunnelY) <= 1) {
            this.disableFlight();
            this.phase = Phase.IDLE;
            return;
        }
        if (pY < this.tunnelY) {
            this.enableFlight();
            class_243 vel = this.mc.field_1724.method_18798();
            this.mc.field_1724.method_18800(vel.field_1352, 0.5, vel.field_1350);
        } else {
            this.disableFlight();
            class_2338 below = this.mc.field_1724.method_24515().method_10074();
            if (this.isBlocking(this.mc.field_1687.method_8320(below))) {
                this.face(class_243.method_24953((class_2382)below));
                this.doDigBlock(below);
            } else {
                this.mc.field_1690.field_1894.method_23481(true);
            }
        }
    }

    private void doDigBlock(class_2338 pos) {
        class_2680 s = this.mc.field_1687.method_8320(pos);
        if (s.method_26215() || this.isReplaceable(s) || this.isUnbreakable(s.method_26204()) || this.isFluid(s)) {
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
            return;
        }
        if (this.isDangerZone(pos, pos.method_10264())) {
            this.blacklistBlock(pos);
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
            this.waypoints.clear();
            this.planWaypoints();
            if (this.waypoints.size() > 0) {
                this.wpIndex = 0;
            }
            return;
        }
        if (this.breakingPos != null && !this.breakingPos.equals((Object)pos)) {
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
        }
        this.breakingPos = pos;
        this.dugPositions.put(pos, System.currentTimeMillis() + 60000L);
        this.face(class_243.method_24953((class_2382)pos));
        this.switchToPick();
        class_2350 bestSide = this.bestFace(pos);
        if (this.breakTicks > 300) {
            this.blacklistBlock(pos);
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.field_1761.method_2925();
            this.waypoints.clear();
            this.wpIndex = 0;
            this.planWaypoints();
            if (!this.waypoints.isEmpty()) {
                this.wpIndex = 1;
            }
            return;
        }
        if (this.breakTicks == 0) {
            this.mc.field_1761.method_2910(pos, bestSide);
        }
        this.mc.field_1761.method_2902(pos, bestSide);
        this.mc.field_1724.method_6104(class_1268.field_5808);
        ++this.breakTicks;
    }

    private void doClearing() {
        class_2338 pp = this.mc.field_1724.method_24515();
        ArrayList<class_2338> nearby = new ArrayList<class_2338>();
        int minYLev = (Integer)this.minY.get();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 2; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    class_2680 s;
                    class_2338 pos;
                    if (dx == 0 && dy == 0 && dz == 0 || (pos = pp.method_10069(dx, dy, dz)).method_10264() <= minYLev || (s = this.mc.field_1687.method_8320(pos)).method_26215() || !(s.method_26204().method_36555() >= 0.0f) || this.isUnbreakable(s.method_26204()) || this.isFluid(s)) continue;
                    nearby.add(pos);
                }
            }
        }
        if (nearby.isEmpty()) {
            this.isStuck = false;
            this.stuckTicks = 0;
            return;
        }
        nearby.sort(Comparator.comparingDouble(p -> p.method_19770((class_2374)this.mc.field_1724.method_33571())));
        class_2338 dig = (class_2338)nearby.get(0);
        this.face(class_243.method_24953((class_2382)dig));
        this.switchToPick();
        this.mc.field_1761.method_2902(dig, this.bestFace(dig));
        this.mc.field_1724.method_6104(class_1268.field_5808);
        if (this.mc.field_1687.method_8320(dig).method_26215()) {
            this.isStuck = false;
            this.stuckTicks = 0;
        }
    }

    private void scan() {
        class_638 world = this.mc.field_1687;
        class_2338 pp = this.mc.field_1724.method_24515();
        int r = (int)Math.ceil((Double)this.scanRange.get());
        int rSq = r * r;
        int minYLev = (Integer)this.minY.get();
        boolean filterDanger = (Boolean)this.filterDangerOres.get();
        class_2338.class_2339 m = new class_2338.class_2339();
        for (int dx = -r; dx <= r; ++dx) {
            int dx2 = dx * dx;
            for (int dy = -r; dy <= r; ++dy) {
                int dxy2 = dx2 + dy * dy;
                if (dxy2 > rSq) continue;
                for (int dz = -r; dz <= r; ++dz) {
                    class_2680 s;
                    if (dxy2 + dz * dz > rSq) continue;
                    m.method_10103(pp.method_10263() + dx, pp.method_10264() + dy, pp.method_10260() + dz);
                    if (m.method_10264() <= minYLev || this.abandonBlacklist.containsKey(m) || this.dugPositions.containsKey(m) || !this.isTargetOre((s = world.method_8320((class_2338)m)).method_26204()) || s.method_26214((class_1922)world, (class_2338)m) < 0.0f || filterDanger && this.isOreNearFluid((class_2338)m)) continue;
                    this.orePositions.add(m.method_10062());
                }
            }
        }
    }

    private boolean isOreNearFluid(class_2338 ore) {
        int safeR = (Integer)this.safeDistance.get();
        for (int dx = -safeR; dx <= safeR; ++dx) {
            for (int dy = -safeR; dy <= safeR; ++dy) {
                for (int dz = -safeR; dz <= safeR; ++dz) {
                    class_2338 check;
                    if (dx == 0 && dy == 0 && dz == 0 || !this.isFluid(this.mc.field_1687.method_8320(check = ore.method_10069(dx, dy, dz)))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTargetOre(class_2248 b) {
        if (b == class_2246.field_10418 || b == class_2246.field_29219) {
            return (Boolean)this.mineCoal.get();
        }
        if (b == class_2246.field_10212 || b == class_2246.field_29027) {
            return (Boolean)this.mineIron.get();
        }
        if (b == class_2246.field_27120 || b == class_2246.field_29221) {
            return (Boolean)this.mineCopper.get();
        }
        if (b == class_2246.field_10571 || b == class_2246.field_29026) {
            return (Boolean)this.mineGold.get();
        }
        if (b == class_2246.field_10080 || b == class_2246.field_29030) {
            return (Boolean)this.mineRedstone.get();
        }
        if (b == class_2246.field_10090 || b == class_2246.field_29028) {
            return (Boolean)this.mineLapis.get();
        }
        if (b == class_2246.field_10442 || b == class_2246.field_29029) {
            return (Boolean)this.mineDiamond.get();
        }
        if (b == class_2246.field_10013 || b == class_2246.field_29220) {
            return (Boolean)this.mineEmerald.get();
        }
        if (b == class_2246.field_10213) {
            return (Boolean)this.mineNetherQuartz.get();
        }
        if (b == class_2246.field_23077) {
            return (Boolean)this.mineNetherGold.get();
        }
        if (b == class_2246.field_22109) {
            return (Boolean)this.mineAncientDebris.get();
        }
        return false;
    }

    private boolean isUnbreakable(class_2248 b) {
        return b == class_2246.field_9987 || b == class_2246.field_10499 || b == class_2246.field_10525 || b == class_2246.field_10395 || b == class_2246.field_10263 || b == class_2246.field_10465 || b == class_2246.field_16540 || b == class_2246.field_10369;
    }

    private void face(class_243 t) {
        class_243 e = this.mc.field_1724.method_33571();
        double dx = t.field_1352 - e.field_1352;
        double dy = t.field_1351 - e.field_1351;
        double dz = t.field_1350 - e.field_1350;
        double h = Math.sqrt(dx * dx + dz * dz);
        this.mc.field_1724.method_36456((float)Math.toDegrees(Math.atan2(-dx, dz)));
        this.mc.field_1724.method_36457((float)(-Math.toDegrees(Math.atan2(dy, h))));
    }

    private class_2350 bestFace(class_2338 pos) {
        class_243 eye = this.mc.field_1724.method_33571();
        class_243 c = class_243.method_24953((class_2382)pos);
        class_243 d = c.method_1020(eye).method_1029();
        double best = -2.0;
        class_2350 r = class_2350.field_11036;
        for (class_2350 dir : class_2350.values()) {
            double dot = d.method_1026(class_243.method_24954((class_2382)dir.method_62675()));
            if (!(dot > best)) continue;
            best = dot;
            r = dir;
        }
        return r;
    }

    private boolean isBlocking(class_2680 s) {
        if (s.method_26215() || s.method_45474() || this.isFluid(s)) {
            return false;
        }
        return s.method_51367() || s.method_26204().method_36555() >= 0.0f;
    }

    private boolean isReplaceable(class_2680 s) {
        return s.method_45474() || s.method_26204() instanceof class_2521 || s.method_26204() instanceof class_2356;
    }

    private boolean isFluid(class_2680 s) {
        return s.method_26227().method_15767(class_3486.field_15518) || s.method_26227().method_15767(class_3486.field_15517);
    }

    private void blacklistAndNext(String reason) {
        if (this.currentTarget != null) {
            this.blacklistWithNeighbors(this.currentTarget);
            this.orePositions.remove(this.currentTarget);
        }
        this.currentTarget = null;
        this.waypoints.clear();
        this.wpIndex = 0;
        this.phase = Phase.IDLE;
        this.targetLockTime = 0;
        this.breakingPos = null;
        this.breakTicks = 0;
        this.returnIfFlying();
        if (((Boolean)this.chatInfo.get()).booleanValue()) {
            this.notify(reason + ", \u9ed1\u540d\u53553\u5206\u949f");
        }
    }

    private void blacklistBlock(class_2338 pos) {
        this.abandonBlacklist.put(pos, System.currentTimeMillis() + 180000L);
    }

    private void blacklistWithNeighbors(class_2338 center) {
        long expire = System.currentTimeMillis() + 180000L;
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dy = -2; dy <= 2; ++dy) {
                for (int dz = -2; dz <= 2; ++dz) {
                    class_2338 pos;
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 2 || !this.orePositions.contains(pos = center.method_10069(dx, dy, dz))) continue;
                    this.abandonBlacklist.put(pos, expire);
                }
            }
        }
    }

    private void enableFlight() {
        this.mc.field_1724.method_31549().field_7479 = true;
        this.mc.field_1724.method_31549().field_7478 = true;
    }

    private void disableFlight() {
        if (!this.wasFlying) {
            this.mc.field_1724.method_31549().field_7479 = false;
            this.mc.field_1724.method_31549().field_7478 = false;
        }
    }

    private void returnIfFlying() {
        if (this.phase == Phase.FLY_UP || this.phase == Phase.RETURN_TO_TUNNEL) {
            this.phase = Phase.RETURN_TO_TUNNEL;
        }
    }

    private void notify(String msg) {
        if (this.mc.field_1724 != null) {
            this.mc.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u00a78[\u00a76\u77ff\u7269\u8ffd\u8e2a\u00a78] \u00a7f" + msg)), true);
        }
    }

    private void releaseControls() {
        this.mc.field_1690.field_1894.method_23481(false);
        this.mc.field_1690.field_1881.method_23481(false);
        this.mc.field_1690.field_1913.method_23481(false);
        this.mc.field_1690.field_1849.method_23481(false);
        this.mc.field_1690.field_1903.method_23481(false);
        this.mc.field_1690.field_1832.method_23481(false);
        this.mc.field_1690.field_1886.method_23481(false);
    }

    private void switchToPick() {
        if (this.isPickaxe(this.mc.field_1724.method_6047().method_7909())) {
            return;
        }
        for (int i = 0; i < 9; ++i) {
            if (!this.isPickaxe(this.mc.field_1724.method_31548().method_5438(i).method_7909())) continue;
            this.setSelectedSlot(this.mc.field_1724.method_31548(), i);
            return;
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

    private boolean isPickaxe(class_1792 i) {
        return i == class_1802.field_8647 || i == class_1802.field_8387 || i == class_1802.field_8403 || i == class_1802.field_8335 || i == class_1802.field_8377 || i == class_1802.field_22024;
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (this.mc.field_1687 == null) {
            return;
        }
        if (((Boolean)this.renderOres.get()).booleanValue()) {
            for (class_2338 p : this.orePositions) {
                if (!this.isTargetOre(this.mc.field_1687.method_8320(p).method_26204())) continue;
                event.renderer.box(p, (Color)this.oreColor.get(), (Color)this.oreColor.get(), ShapeMode.Both, 0);
            }
        }
        if (this.currentTarget != null && this.isTargetOre(this.mc.field_1687.method_8320(this.currentTarget).method_26204())) {
            event.renderer.box(this.currentTarget, (Color)this.targetColor.get(), (Color)this.targetColor.get(), ShapeMode.Both, 0);
        }
        if (!this.waypoints.isEmpty()) {
            for (int i = this.wpIndex; i < this.waypoints.size(); ++i) {
                class_2338 p;
                p = this.waypoints.get(i);
                event.renderer.box(p, (Color)this.pathColor.get(), (Color)this.pathColor.get(), ShapeMode.Lines, 0);
                if (i <= 0) continue;
                class_243 a = class_243.method_24953((class_2382)((class_2382)this.waypoints.get(i - 1)));
                class_243 b = class_243.method_24953((class_2382)p);
                event.renderer.line(a.field_1352, a.field_1351, a.field_1350, b.field_1352, b.field_1351, b.field_1350, (Color)this.pathColor.get());
            }
        }
    }

    private static enum Phase {
        IDLE,
        WALK_XZ,
        FLY_UP,
        DIG_DOWN,
        MINE_ORE,
        RETURN_TO_TUNNEL;

    }

    private static class Node
    implements Comparable<Node> {
        long key;
        float g;
        float f;

        Node(long key, float g, float f) {
            this.key = key;
            this.g = g;
            this.f = f;
        }

        @Override
        public int compareTo(Node o) {
            return Float.compare(this.f, o.f);
        }
    }
}
