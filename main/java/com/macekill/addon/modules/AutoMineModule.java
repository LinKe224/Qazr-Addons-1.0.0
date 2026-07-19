/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  meteordevelopment.meteorclient.events.render.Render3DEvent
 *  meteordevelopment.meteorclient.events.world.TickEvent$Post
 *  meteordevelopment.meteorclient.renderer.ShapeMode
 *  meteordevelopment.meteorclient.settings.BoolSetting$Builder
 *  meteordevelopment.meteorclient.settings.ColorSetting$Builder
 *  meteordevelopment.meteorclient.settings.DoubleSetting$Builder
 *  meteordevelopment.meteorclient.settings.IntSetting$Builder
 *  meteordevelopment.meteorclient.settings.Setting
 *  meteordevelopment.meteorclient.settings.SettingGroup
 *  meteordevelopment.meteorclient.systems.modules.Module
 *  meteordevelopment.meteorclient.utils.render.color.Color
 *  meteordevelopment.meteorclient.utils.render.color.SettingColor
 *  meteordevelopment.orbit.EventHandler
 *  net.minecraft.Hand
 *  net.minecraft.PlayerInventory
 *  net.minecraft.Item
 *  net.minecraft.Items
 *  net.minecraft.BlockView
 *  net.minecraft.Blocks
 *  net.minecraft.Block
 *  net.minecraft.BlockPos
 *  net.minecraft.BlockPos$class_2339
 *  net.minecraft.Direction
 *  net.minecraft.FlowerBlock
 *  net.minecraft.Position
 *  net.minecraft.Vec3i
 *  net.minecraft.Vec3d
 *  net.minecraft.TallFlowerBlock
 *  net.minecraft.Text
 *  net.minecraft.BlockState
 *  net.minecraft.FluidTags
 *  net.minecraft.ClientWorld
 */
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
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.world.BlockView;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.FlowerBlock;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.text.Text;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.client.world.ClientWorld;

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
    private final Set<BlockPos> orePositions;
    private BlockPos currentTarget;
    private final List<BlockPos> waypoints;
    private int wpIndex;
    private Phase phase;
    private int scanCooldown;
    private int tickCounter;
    private BlockPos breakingPos;
    private int breakTicks;
    private int stuckTicks;
    private boolean isStuck;
    private BlockPos lastPos;
    private int targetLockTime;
    private int tunnelY;
    private boolean wasFlying;
    private Method getCpuLoadMethod;
    private int cpuCheckTick;
    private long perfPauseUntil;
    private final Map<BlockPos, Long> abandonBlacklist;
    private final Map<BlockPos, Long> dugPositions;
    private long lastOreMinedTime;
    private static Field selectedSlotField;

    public AutoMineModule() {
        super(MaceKillAddon.CATEGORY, "\u77ff\u7269\u8ffd\u8e2a", "Wurst TunnelHack\u98ce\u683c: \u6298\u7ebf\u5bfb\u8def+\u98de\u884c\u5782\u76f4+\u81ea\u52a8\u6316\u6398");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.scanRange = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u626b\u63cf\u8303\u56f4")).description("\u641c\u7d22\u77ff\u77f3\u7684\u6700\u5927\u534a\u5f84(\u683c)")).defaultValue(64.0).min(8.0).max(256.0).sliderMax(128.0).build());
        this.autoMine = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u52a8\u6316\u77ff")).description("\u81ea\u52a8\u5bfb\u8def+\u6316\u6398")).defaultValue(true)).build());
        this.minY = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6700\u4f4eY\u5c42")).description("\u6b64\u5c42\u6570\u53ca\u4ee5\u4e0b\u7684\u65b9\u5757\u4e0d\u4f1a\u88ab\u5411\u4e0b\u6316\u6398")).defaultValue(-64)).min(-64).max(320).build());
        this.stuckTime = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u5361\u4f4f\u5224\u5b9a")).description("\u591a\u4e45\u4e0d\u52a8\u7b97\u5361\u4f4f(tick)")).defaultValue(60)).min(10).max(200).build());
        this.abandonThreshold = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u653e\u5f03\u9608\u503c")).description("\u4e0a\u4e00\u4e2a\u77ff\u6316\u5b8c\u540e\u8d85\u65f6\u672a\u5230\u8fbe\u5219\u9ed1\u540d\u53553\u5206\u949f(tick)")).defaultValue(200)).min(20).max(1200).build());
        this.safeDistance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u5b89\u5168\u8ddd\u79bb")).description("\u8eb2\u907f\u6d41\u4f53\u7684\u6700\u5c0f\u8ddd\u79bb(\u683c)")).defaultValue(5)).min(2).max(15).build());
        this.avoidCaves = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8eb2\u907f\u6d1e\u7a74")).description("\u96a7\u9053\u907f\u5f00\u5929\u7136\u7a7a\u6c14/\u6d1e\u7a74(\u4f1a\u964d\u4f4e\u5bfb\u8def\u6210\u529f\u7387)")).defaultValue(false)).build());
        this.filterDangerOres = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6392\u9664\u5371\u9669\u533a\u77ff\u7269")).description("\u4e0d\u6316\u6398\u5b89\u5168\u8ddd\u79bb\u5185\u9760\u8fd1\u6d41\u4f53/\u6d1e\u7a74\u7684\u77ff\u7269")).defaultValue(true)).build());
        this.chatInfo = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u804a\u5929\u4fe1\u606f")).description("\u5728\u804a\u5929\u680f\u8f93\u51fa\u72b6\u6001")).defaultValue(false)).build());
        this.sgOres = this.settings.createGroup("\u77ff\u7269\u5217\u8868");
        this.mineCoal = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u7164\u77ff")).defaultValue(true)).build());
        this.mineIron = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u94c1\u77ff")).defaultValue(true)).build());
        this.mineCopper = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u94dc\u77ff")).defaultValue(true)).build());
        this.mineGold = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u91d1\u77ff")).defaultValue(true)).build());
        this.mineRedstone = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u7ea2\u77f3\u77ff")).defaultValue(true)).build());
        this.mineLapis = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u9752\u91d1\u77f3")).defaultValue(true)).build());
        this.mineDiamond = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u94bb\u77f3\u77ff")).defaultValue(true)).build());
        this.mineEmerald = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u7eff\u5b9d\u77f3")).defaultValue(true)).build());
        this.mineNetherQuartz = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u4e0b\u754c\u77f3\u82f1")).defaultValue(true)).build());
        this.mineNetherGold = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u4e0b\u754c\u91d1\u77ff")).defaultValue(true)).build());
        this.mineAncientDebris = this.sgOres.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8fdc\u53e4\u6b8b\u9ab8")).defaultValue(true)).build());
        this.sgRender = this.settings.createGroup("\u6e32\u67d3");
        this.renderOres = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u6e32\u67d3\u77ff\u77f3")).defaultValue(true)).build());
        this.oreColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)new ColorSetting.Builder().name("\u77ff\u77f3\u989c\u8272")).defaultValue(new SettingColor(255, 255, 0, 200)).visible(() -> this.renderOres.get())).build());
        this.targetColor = this.sgRender.add(((ColorSetting.Builder)new ColorSetting.Builder().name("\u76ee\u6807\u989c\u8272")).defaultValue(new SettingColor(0, 255, 0, 200)).build());
        this.pathColor = this.sgRender.add(((ColorSetting.Builder)new ColorSetting.Builder().name("\u8def\u5f84\u989c\u8272")).defaultValue(new SettingColor(100, 200, 255, 180)).build());
        this.orePositions = new HashSet<BlockPos>();
        this.waypoints = new ArrayList<BlockPos>();
        this.phase = Phase.IDLE;
        this.scanCooldown = 60;
        this.abandonBlacklist = new HashMap<BlockPos, Long>();
        this.dugPositions = new HashMap<BlockPos, Long>();
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
        this.lastPos = this.mc.player != null ? this.mc.player.getBlockPos() : null;
        this.wasFlying = this.mc.player != null && this.mc.player.getAbilities().flying;
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
            this.mc.player.getAbilities().flying = false;
            this.mc.player.getAbilities().allowFlying = false;
        }
    }

    public String getInfoString() {
        if (this.isStuck) {
            return "\u6e05\u7406...";
        }
        if (this.phase == Phase.WALK_XZ && this.currentTarget != null) {
            return "\u2192" + this.currentTarget.toShortString();
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
        BlockState ts;
        BlockPos nearest;
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (this.mc.player.isDead() || this.mc.player.getHealth() <= 0.0f) {
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
            BlockState s = this.mc.world.getBlockState(p);
            return !this.isTargetOre(s.getBlock()) || s.isAir();
        });
        BlockPos nearestOre = nearest = this.orePositions.isEmpty() ? null : (BlockPos)this.orePositions.stream().filter(p -> !this.abandonBlacklist.containsKey(p)).filter(p -> !this.dugPositions.containsKey(p)).min(Comparator.comparingDouble(p -> p.getSquaredDistance((Position)this.mc.player.getEyePos()))).orElse(null);
        if (this.currentTarget != null && (!this.isTargetOre((ts = this.mc.world.getBlockState(this.currentTarget)).getBlock()) || ts.isAir())) {
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
                int pY = this.mc.player.getBlockPos().getY();
                if (Math.abs(this.currentTarget.getY() - pY) <= 1) {
                    this.phase = Phase.WALK_XZ;
                } else if (this.currentTarget.getY() != pY) {
                    this.phase = Phase.WALK_XZ;
                }
            }
        }
        BlockPos curPos = this.mc.player.getBlockPos();
        if (this.lastPos != null && curPos.equals(this.lastPos)) {
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
                double cpuLoad = (Double)this.getCpuLoadMethod.invoke(ManagementFactory.getOperatingSystemMXBean(), new Object[0]);
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
                    this.mc.interactionManager.cancelBlockBreaking();
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
            this.mc.interactionManager.cancelBlockBreaking();
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
        BlockPos from = this.mc.player.getBlockPos();
        BlockPos to = this.currentTarget;
        to = this.findAirAdjacent(to);
        this.tunnelY = from.getY();
        BlockPos hTarget = new BlockPos(to.getX(), this.tunnelY, to.getZ());
        List<BlockPos> hPath = this.findHorizontalPath(from, hTarget);
        this.waypoints.addAll(hPath);
        this.waypoints.removeIf(wp -> this.isDangerZone((BlockPos)wp, this.tunnelY));
        if (to.getY() != this.tunnelY) {
            this.waypoints.add(to);
        }
        if (!this.waypoints.isEmpty() && this.waypoints.get(0).equals(from)) {
            this.waypoints.remove(0);
        }
        if (this.waypoints.isEmpty()) {
            this.blacklistAndNext("\u65e0\u6cd5\u89c4\u5212\u5b89\u5168\u8def\u5f84");
            return;
        }
        this.wpIndex = 0;
    }

    private List<BlockPos> findHorizontalPath(BlockPos from, BlockPos to) {
        int y = from.getY();
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
            BlockPos cp = this.unkey(cur.key, y);
            if (!cp.equals(from) && this.isDangerZone(cp, y)) continue;
            for (int[] d : dirs) {
                float tentativeG;
                BlockPos np = cp.add(d[0], 0, d[1]);
                long nk = this.key(np);
                if (this.isDangerZone(np, y) || !((tentativeG = gScore.getOrDefault(cur.key, Float.valueOf(Float.MAX_VALUE)).floatValue() + 1.0f) < gScore.getOrDefault(nk, Float.valueOf(Float.MAX_VALUE)).floatValue())) continue;
                gScore.put(nk, Float.valueOf(tentativeG));
                parent.put(nk, cur.key);
                open.offer(new Node(nk, tentativeG, tentativeG + this.heuristic(np, to)));
            }
        }
        ArrayList<BlockPos> path = new ArrayList<BlockPos>();
        if (goalFound < 0L) {
            return this.fallbackPath(from, to, y);
        }
        long pKey = goalFound;
        while (pKey != startKey) {
            BlockPos bp = this.unkey(pKey, y);
            path.add(bp);
            pKey = parent.getOrDefault(pKey, startKey);
        }
        path.add(from);
        Collections.reverse(path);
        return this.compressPath(path, y);
    }

    private List<BlockPos> compressPath(List<BlockPos> path, int y) {
        if (path.size() <= 2) {
            return path;
        }
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
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

    private boolean canWalkStraight(BlockPos a, BlockPos b, int y) {
        int dx = Integer.signum(b.getX() - a.getX());
        int dz = Integer.signum(b.getZ() - a.getZ());
        if (dx != 0 && dz != 0) {
            return false;
        }
        if (dx == 0 && dz == 0) {
            return true;
        }
        int steps = dx != 0 ? Math.abs(b.getX() - a.getX()) : Math.abs(b.getZ() - a.getZ());
        for (int i = 1; i <= steps; ++i) {
            if (!this.isDangerZone(a.add(dx * i, 0, dz * i), y)) continue;
            return false;
        }
        return true;
    }

    private List<BlockPos> fallbackPath(BlockPos from, BlockPos to, int y) {
        ArrayList<BlockPos> path = new ArrayList<BlockPos>();
        path.add(from);
        int dx = Integer.signum(to.getX() - from.getX());
        int dz = Integer.signum(to.getZ() - from.getZ());
        int stepsX = Math.abs(to.getX() - from.getX());
        int stepsZ = Math.abs(to.getZ() - from.getZ());
        block0: for (int i = 1; i <= stepsX; ++i) {
            BlockPos p = from.add(dx * i, 0, 0);
            if (!this.isDangerZone(p, y)) {
                path.add(p);
                continue;
            }
            for (int s = 1; s <= 4; ++s) {
                BlockPos det = p.add(0, 0, s);
                if (!this.isDangerZone(det, y)) {
                    path.add(det);
                    path.add(det.add(dx, 0, 0));
                    continue block0;
                }
                det = p.add(0, 0, -s);
                if (this.isDangerZone(det, y)) continue;
                path.add(det);
                path.add(det.add(dx, 0, 0));
                continue block0;
            }
        }
        BlockPos afterX = new BlockPos(to.getX(), y, from.getZ());
        block2: for (int i = 1; i <= stepsZ; ++i) {
            BlockPos p = afterX.add(0, 0, dz * i);
            if (!this.isDangerZone(p, y)) {
                path.add(p);
                continue;
            }
            for (int s = 1; s <= 4; ++s) {
                BlockPos det = p.add(s, 0, 0);
                if (!this.isDangerZone(det, y)) {
                    path.add(det);
                    path.add(det.add(0, 0, dz));
                    continue block2;
                }
                det = p.add(-s, 0, 0);
                if (this.isDangerZone(det, y)) continue;
                path.add(det);
                path.add(det.add(0, 0, dz));
                continue block2;
            }
        }
        return path;
    }

    private boolean isDangerZone(BlockPos pos, int yLevel) {
        int safeR = (Integer)this.safeDistance.get();
        int minY = (Integer)this.minY.get();
        boolean checkCaves = (Boolean)this.avoidCaves.get();
        for (int dx = -safeR; dx <= safeR; ++dx) {
            for (int dy = -1; dy <= 2; ++dy) {
                for (int dz = -safeR; dz <= safeR; ++dz) {
                    BlockPos check = pos.add(dx, yLevel + dy, dz);
                    if (check.getY() <= minY) continue;
                    BlockState s = this.mc.world.getBlockState(check);
                    if (this.isFluid(s)) {
                        return true;
                    }
                    if (!checkCaves || !s.isAir()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private long key(BlockPos p) {
        return (long)p.getX() << 32 | (long)p.getZ() & 0xFFFFFFFFL;
    }

    private BlockPos unkey(long k, int y) {
        return new BlockPos((int)(k >> 32), y, (int)k);
    }

    private float heuristic(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getZ() - b.getZ());
    }

    private BlockPos findAirAdjacent(BlockPos ore) {
        BlockPos best = ore;
        double bestDist = Double.MAX_VALUE;
        Vec3d eye = this.mc.player != null ? this.mc.player.getEyePos() : Vec3d.ZERO;
        for (Direction d : Direction.values()) {
            double dist;
            BlockPos adj = ore.offset(d);
            if (!this.mc.world.getBlockState(adj).isAir() && !this.isReplaceable(this.mc.world.getBlockState(adj)) || !((dist = adj.getSquaredDistance((Position)eye)) < bestDist)) continue;
            bestDist = dist;
            best = adj;
        }
        if (bestDist == Double.MAX_VALUE) {
            for (Direction d : Direction.values()) {
                double dist = ore.offset(d).getSquaredDistance((Position)eye);
                if (!(dist < bestDist)) continue;
                bestDist = dist;
                best = ore.offset(d);
            }
        }
        return best;
    }

    private void walkXZPhase() {
        boolean frontDanger;
        boolean zSeg;
        boolean atX;
        if (this.wpIndex >= this.waypoints.size()) {
            int pY = this.mc.player.getBlockPos().getY();
            int tY = this.currentTarget.getY();
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
        BlockPos wp = this.waypoints.get(this.wpIndex);
        BlockPos feet = this.mc.player.getBlockPos();
        boolean atZ = feet.getZ() == wp.getZ();
        boolean bl = atX = feet.getX() == wp.getX();
        if (atX && atZ) {
            ++this.wpIndex;
            if (this.wpIndex < this.waypoints.size() && this.isDangerZone(this.waypoints.get(this.wpIndex), feet.getY())) {
                this.planWaypoints();
                if (this.waypoints.isEmpty() || this.wpIndex >= this.waypoints.size()) {
                    this.blacklistAndNext("\u4e0b\u4e00\u8282\u70b9\u4e0d\u5b89\u5168");
                    return;
                }
                this.wpIndex = 0;
            }
            return;
        }
        boolean xSeg = wp.getX() != feet.getX() && wp.getZ() == feet.getZ();
        boolean bl2 = zSeg = wp.getZ() != feet.getZ() && wp.getX() == feet.getX();
        if (!xSeg && !zSeg) {
            ++this.wpIndex;
            return;
        }
        int stepX = xSeg ? Integer.signum(wp.getX() - feet.getX()) : 0;
        int stepZ = zSeg ? Integer.signum(wp.getZ() - feet.getZ()) : 0;
        Vec3d faceTarget = new Vec3d((double)(feet.getX() + stepX) + 0.5, (double)feet.getY() + 0.5, (double)(feet.getZ() + stepZ) + 0.5);
        this.face(faceTarget);
        BlockPos frontLow = feet.add(stepX, 0, stepZ);
        BlockPos frontHigh = frontLow.up();
        if (frontLow.getY() <= (Integer)this.minY.get() || frontHigh.getY() <= (Integer)this.minY.get()) {
            this.blacklistAndNext("\u5230\u8fbe\u6700\u4f4eY\u5c42\u9650\u5236");
            return;
        }
        ArrayList<BlockPos> toDig = new ArrayList<BlockPos>();
        if (this.isBlocking(this.mc.world.getBlockState(frontLow))) {
            toDig.add(frontLow);
        }
        if (this.isBlocking(this.mc.world.getBlockState(frontHigh))) {
            toDig.add(frontHigh);
        }
        if (frontDanger = this.isDangerZone(frontLow, feet.getY())) {
            if (this.isBlocking(this.mc.world.getBlockState(frontLow))) {
                this.blacklistBlock(frontLow);
            }
            this.planWaypoints();
            if (this.waypoints.isEmpty() || this.wpIndex >= this.waypoints.size()) {
                this.blacklistAndNext("\u8def\u5f84\u8fdb\u5165\u5371\u9669\u533a");
                return;
            }
            this.wpIndex = 0;
            this.mc.options.forwardKey.setPressed(false);
            return;
        }
        if (this.breakingPos != null) {
            toDig.clear();
            toDig.add(this.breakingPos);
        }
        if (!toDig.isEmpty()) {
            BlockPos dig = (BlockPos)toDig.get(0);
            if (toDig.size() == 2) {
                double dHigh;
                double dLow = this.mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter((Vec3i)frontLow));
                dig = dLow < (dHigh = this.mc.player.getEyePos().squaredDistanceTo(Vec3d.ofCenter((Vec3i)frontHigh))) ? frontLow : frontHigh;
            }
            this.doDigBlock(dig);
        } else {
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.interactionManager.cancelBlockBreaking();
            this.mc.options.forwardKey.setPressed(true);
        }
    }

    private void flyUpPhase() {
        BlockPos feet = this.mc.player.getBlockPos();
        int tY = this.currentTarget.getY();
        if (feet.getY() >= tY - 1) {
            this.disableFlight();
            this.phase = Phase.MINE_ORE;
            return;
        }
        for (int dy = 1; dy <= tY - feet.getY() + 1; ++dy) {
            BlockPos check = new BlockPos(feet.getX(), feet.getY() + dy, feet.getZ());
            if (check.getY() <= (Integer)this.minY.get()) {
                this.blacklistAndNext("\u5230\u8fbe\u6700\u4f4eY\u5c42");
                return;
            }
            if (!this.isBlocking(this.mc.world.getBlockState(check))) continue;
            this.face(Vec3d.ofCenter((Vec3i)check));
            this.doDigBlock(check);
            this.enableFlight();
            return;
        }
        this.enableFlight();
        Vec3d vel = this.mc.player.getVelocity();
        this.mc.player.setVelocity(vel.x, 0.5, vel.z);
    }

    private void digDownPhase() {
        BlockPos feet = this.mc.player.getBlockPos();
        int tY = this.currentTarget.getY();
        if (feet.getY() <= tY + 1) {
            this.phase = Phase.MINE_ORE;
            return;
        }
        for (int dy = 1; dy <= feet.getY() - tY; ++dy) {
            BlockPos check = new BlockPos(feet.getX(), feet.getY() - dy, feet.getZ());
            if (check.getY() <= (Integer)this.minY.get()) {
                this.blacklistAndNext("\u5230\u8fbe\u6700\u4f4eY\u5c42");
                return;
            }
            if (!this.isBlocking(this.mc.world.getBlockState(check))) continue;
            this.face(Vec3d.ofCenter((Vec3i)check));
            this.doDigBlock(check);
            return;
        }
        this.mc.options.forwardKey.setPressed(true);
        this.mc.options.sneakKey.setPressed(true);
    }

    private void mineOrePhase() {
        BlockPos ore = this.currentTarget;
        if (ore == null) {
            this.phase = Phase.IDLE;
            return;
        }
        BlockState s = this.mc.world.getBlockState(ore);
        if (!this.isTargetOre(s.getBlock()) || s.isAir()) {
            this.lastOreMinedTime = System.currentTimeMillis();
            this.currentTarget = null;
            this.waypoints.clear();
            this.wpIndex = 0;
            this.targetLockTime = 0;
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.interactionManager.cancelBlockBreaking();
            this.phase = Phase.RETURN_TO_TUNNEL;
            return;
        }
        if (this.breakingPos != null && this.breakingPos.equals(ore)) {
            if (this.breakTicks++ > 200) {
                this.blacklistBlock(ore);
                this.breakingPos = null;
                this.breakTicks = 0;
                this.mc.interactionManager.cancelBlockBreaking();
                this.planWaypoints();
                return;
            }
            this.mc.interactionManager.updateBlockBreakingProgress(ore, this.bestFace(ore));
            this.face(Vec3d.ofCenter((Vec3i)ore));
            return;
        }
        this.face(Vec3d.ofCenter((Vec3i)ore));
        this.switchToPick();
        this.doDigBlock(ore);
    }

    private void returnToTunnelPhase() {
        int pY = this.mc.player.getBlockPos().getY();
        if (Math.abs(pY - this.tunnelY) <= 1) {
            this.disableFlight();
            this.phase = Phase.IDLE;
            return;
        }
        if (pY < this.tunnelY) {
            this.enableFlight();
            Vec3d vel = this.mc.player.getVelocity();
            this.mc.player.setVelocity(vel.x, 0.5, vel.z);
        } else {
            this.disableFlight();
            BlockPos below = this.mc.player.getBlockPos().down();
            if (this.isBlocking(this.mc.world.getBlockState(below))) {
                this.face(Vec3d.ofCenter((Vec3i)below));
                this.doDigBlock(below);
            } else {
                this.mc.options.forwardKey.setPressed(true);
            }
        }
    }

    private void doDigBlock(BlockPos pos) {
        BlockState s = this.mc.world.getBlockState(pos);
        if (s.isAir() || this.isReplaceable(s) || this.isUnbreakable(s.getBlock()) || this.isFluid(s)) {
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.interactionManager.cancelBlockBreaking();
            return;
        }
        if (this.isDangerZone(pos, pos.getY())) {
            this.blacklistBlock(pos);
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.interactionManager.cancelBlockBreaking();
            this.waypoints.clear();
            this.planWaypoints();
            if (this.waypoints.size() > 0) {
                this.wpIndex = 0;
            }
            return;
        }
        if (this.breakingPos != null && !this.breakingPos.equals(pos)) {
            this.breakTicks = 0;
            this.mc.interactionManager.cancelBlockBreaking();
        }
        this.breakingPos = pos;
        this.dugPositions.put(pos, System.currentTimeMillis() + 60000L);
        this.face(Vec3d.ofCenter((Vec3i)pos));
        this.switchToPick();
        Direction bestSide = this.bestFace(pos);
        if (this.breakTicks > 300) {
            this.blacklistBlock(pos);
            this.breakingPos = null;
            this.breakTicks = 0;
            this.mc.interactionManager.cancelBlockBreaking();
            this.waypoints.clear();
            this.wpIndex = 0;
            this.planWaypoints();
            if (!this.waypoints.isEmpty()) {
                this.wpIndex = 1;
            }
            return;
        }
        if (this.breakTicks == 0) {
            this.mc.interactionManager.attackBlock(pos, bestSide);
        }
        this.mc.interactionManager.updateBlockBreakingProgress(pos, bestSide);
        this.mc.player.swingHand(Hand.MAIN_HAND);
        ++this.breakTicks;
    }

    private void doClearing() {
        BlockPos pp = this.mc.player.getBlockPos();
        ArrayList<BlockPos> nearby = new ArrayList<BlockPos>();
        int minYLev = (Integer)this.minY.get();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 2; ++dy) {
                for (int dz = -1; dz <= 1; ++dz) {
                    BlockState s;
                    BlockPos pos;
                    if (dx == 0 && dy == 0 && dz == 0 || (pos = pp.add(dx, dy, dz)).getY() <= minYLev || (s = this.mc.world.getBlockState(pos)).isAir() || !(s.getBlock().getHardness() >= 0.0f) || this.isUnbreakable(s.getBlock()) || this.isFluid(s)) continue;
                    nearby.add(pos);
                }
            }
        }
        if (nearby.isEmpty()) {
            this.isStuck = false;
            this.stuckTicks = 0;
            return;
        }
        nearby.sort(Comparator.comparingDouble(p -> p.getSquaredDistance((Position)this.mc.player.getEyePos())));
        BlockPos dig = (BlockPos)nearby.get(0);
        this.face(Vec3d.ofCenter((Vec3i)dig));
        this.switchToPick();
        this.mc.interactionManager.updateBlockBreakingProgress(dig, this.bestFace(dig));
        this.mc.player.swingHand(Hand.MAIN_HAND);
        if (this.mc.world.getBlockState(dig).isAir()) {
            this.isStuck = false;
            this.stuckTicks = 0;
        }
    }

    private void scan() {
        ClientWorld world = this.mc.world;
        BlockPos pp = this.mc.player.getBlockPos();
        int r = (int)Math.ceil((Double)this.scanRange.get());
        int rSq = r * r;
        int minYLev = (Integer)this.minY.get();
        boolean filterDanger = (Boolean)this.filterDangerOres.get();
        BlockPos.Mutable m = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; ++dx) {
            int dx2 = dx * dx;
            for (int dy = -r; dy <= r; ++dy) {
                int dxy2 = dx2 + dy * dy;
                if (dxy2 > rSq) continue;
                for (int dz = -r; dz <= r; ++dz) {
                    BlockState s;
                    if (dxy2 + dz * dz > rSq) continue;
                    m.set(pp.getX() + dx, pp.getY() + dy, pp.getZ() + dz);
                    if (m.getY() <= minYLev || this.abandonBlacklist.containsKey(m) || this.dugPositions.containsKey(m) || !this.isTargetOre((s = world.getBlockState((BlockPos)m)).getBlock()) || s.getHardness((BlockView)world, (BlockPos)m) < 0.0f || filterDanger && this.isOreNearFluid((BlockPos)m)) continue;
                    this.orePositions.add(m.toImmutable());
                }
            }
        }
    }

    private boolean isOreNearFluid(BlockPos ore) {
        int safeR = (Integer)this.safeDistance.get();
        for (int dx = -safeR; dx <= safeR; ++dx) {
            for (int dy = -safeR; dy <= safeR; ++dy) {
                for (int dz = -safeR; dz <= safeR; ++dz) {
                    BlockPos check;
                    if (dx == 0 && dy == 0 && dz == 0 || !this.isFluid(this.mc.world.getBlockState(check = ore.add(dx, dy, dz)))) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTargetOre(Block b) {
        if (b == Blocks.COAL_ORE || b == Blocks.DEEPSLATE_COAL_ORE) {
            return (Boolean)this.mineCoal.get();
        }
        if (b == Blocks.IRON_ORE || b == Blocks.DEEPSLATE_IRON_ORE) {
            return (Boolean)this.mineIron.get();
        }
        if (b == Blocks.COPPER_ORE || b == Blocks.DEEPSLATE_COPPER_ORE) {
            return (Boolean)this.mineCopper.get();
        }
        if (b == Blocks.GOLD_ORE || b == Blocks.DEEPSLATE_GOLD_ORE) {
            return (Boolean)this.mineGold.get();
        }
        if (b == Blocks.REDSTONE_ORE || b == Blocks.DEEPSLATE_REDSTONE_ORE) {
            return (Boolean)this.mineRedstone.get();
        }
        if (b == Blocks.LAPIS_ORE || b == Blocks.DEEPSLATE_LAPIS_ORE) {
            return (Boolean)this.mineLapis.get();
        }
        if (b == Blocks.DIAMOND_ORE || b == Blocks.DEEPSLATE_DIAMOND_ORE) {
            return (Boolean)this.mineDiamond.get();
        }
        if (b == Blocks.EMERALD_ORE || b == Blocks.DEEPSLATE_EMERALD_ORE) {
            return (Boolean)this.mineEmerald.get();
        }
        if (b == Blocks.NETHER_QUARTZ_ORE) {
            return (Boolean)this.mineNetherQuartz.get();
        }
        if (b == Blocks.NETHER_GOLD_ORE) {
            return (Boolean)this.mineNetherGold.get();
        }
        if (b == Blocks.ANCIENT_DEBRIS) {
            return (Boolean)this.mineAncientDebris.get();
        }
        return false;
    }

    private boolean isUnbreakable(Block b) {
        return b == Blocks.BEDROCK || b == Blocks.BARRIER || b == Blocks.COMMAND_BLOCK || b == Blocks.CHAIN_COMMAND_BLOCK || b == Blocks.REPEATING_COMMAND_BLOCK || b == Blocks.STRUCTURE_BLOCK || b == Blocks.JIGSAW || b == Blocks.STRUCTURE_VOID;
    }

    private void face(Vec3d t) {
        Vec3d e = this.mc.player.getEyePos();
        double dx = t.x - e.x;
        double dy = t.y - e.y;
        double dz = t.z - e.z;
        double h = Math.sqrt(dx * dx + dz * dz);
        this.mc.player.setYaw((float)Math.toDegrees(Math.atan2(-dx, dz)));
        this.mc.player.setPitch((float)(-Math.toDegrees(Math.atan2(dy, h))));
    }

    private Direction bestFace(BlockPos pos) {
        Vec3d eye = this.mc.player.getEyePos();
        Vec3d c = Vec3d.ofCenter((Vec3i)pos);
        Vec3d d = c.subtract(eye).normalize();
        double best = -2.0;
        Direction r = Direction.UP;
        for (Direction dir : Direction.values()) {
            double dot = d.dotProduct(Vec3d.of((Vec3i)dir.getVector()));
            if (!(dot > best)) continue;
            best = dot;
            r = dir;
        }
        return r;
    }

    private boolean isBlocking(BlockState s) {
        if (s.isAir() || s.isReplaceable() || this.isFluid(s)) {
            return false;
        }
        return s.isSolid() || s.getBlock().getHardness() >= 0.0f;
    }

    private boolean isReplaceable(BlockState s) {
        return s.isReplaceable() || s.getBlock() instanceof TallFlowerBlock || s.getBlock() instanceof FlowerBlock;
    }

    private boolean isFluid(BlockState s) {
        return s.getFluidState().isIn(FluidTags.LAVA) || s.getFluidState().isIn(FluidTags.WATER);
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

    private void blacklistBlock(BlockPos pos) {
        this.abandonBlacklist.put(pos, System.currentTimeMillis() + 180000L);
    }

    private void blacklistWithNeighbors(BlockPos center) {
        long expire = System.currentTimeMillis() + 180000L;
        for (int dx = -2; dx <= 2; ++dx) {
            for (int dy = -2; dy <= 2; ++dy) {
                for (int dz = -2; dz <= 2; ++dz) {
                    BlockPos pos;
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 2 || !this.orePositions.contains(pos = center.add(dx, dy, dz))) continue;
                    this.abandonBlacklist.put(pos, expire);
                }
            }
        }
    }

    private void enableFlight() {
        this.mc.player.getAbilities().flying = true;
        this.mc.player.getAbilities().allowFlying = true;
    }

    private void disableFlight() {
        if (!this.wasFlying) {
            this.mc.player.getAbilities().flying = false;
            this.mc.player.getAbilities().allowFlying = false;
        }
    }

    private void returnIfFlying() {
        if (this.phase == Phase.FLY_UP || this.phase == Phase.RETURN_TO_TUNNEL) {
            this.phase = Phase.RETURN_TO_TUNNEL;
        }
    }

    private void notify(String msg) {
        if (this.mc.player != null) {
            this.mc.player.sendMessage((Text)Text.literal((String)("\u00a78[\u00a76\u77ff\u7269\u8ffd\u8e2a\u00a78] \u00a7f" + msg)), true);
        }
    }

    private void releaseControls() {
        this.mc.options.forwardKey.setPressed(false);
        this.mc.options.backKey.setPressed(false);
        this.mc.options.leftKey.setPressed(false);
        this.mc.options.rightKey.setPressed(false);
        this.mc.options.jumpKey.setPressed(false);
        this.mc.options.sneakKey.setPressed(false);
        this.mc.options.attackKey.setPressed(false);
    }

    private void switchToPick() {
        if (this.isPickaxe(this.mc.player.getMainHandStack().getItem())) {
            return;
        }
        for (int i = 0; i < 9; ++i) {
            if (!this.isPickaxe(this.mc.player.getInventory().getStack(i).getItem())) continue;
            this.setSelectedSlot(this.mc.player.getInventory(), i);
            return;
        }
    }

    private void setSelectedSlot(PlayerInventory inv, int slot) {
        try {
            if (selectedSlotField == null) {
                selectedSlotField = PlayerInventory.class.getDeclaredField("selectedSlot");
                selectedSlotField.setAccessible(true);
            }
            selectedSlotField.setInt(inv, slot);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private boolean isPickaxe(Item i) {
        return i == Items.WOODEN_PICKAXE || i == Items.STONE_PICKAXE || i == Items.IRON_PICKAXE || i == Items.GOLDEN_PICKAXE || i == Items.DIAMOND_PICKAXE || i == Items.NETHERITE_PICKAXE;
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (this.mc.world == null) {
            return;
        }
        if (((Boolean)this.renderOres.get()).booleanValue()) {
            for (BlockPos p : this.orePositions) {
                if (!this.isTargetOre(this.mc.world.getBlockState(p).getBlock())) continue;
                event.renderer.box(p, (Color)this.oreColor.get(), (Color)this.oreColor.get(), ShapeMode.Both, 0);
            }
        }
        if (this.currentTarget != null && this.isTargetOre(this.mc.world.getBlockState(this.currentTarget).getBlock())) {
            event.renderer.box(this.currentTarget, (Color)this.targetColor.get(), (Color)this.targetColor.get(), ShapeMode.Both, 0);
        }
        if (!this.waypoints.isEmpty()) {
            for (int i = this.wpIndex; i < this.waypoints.size(); ++i) {
                BlockPos p;
                p = this.waypoints.get(i);
                event.renderer.box(p, (Color)this.pathColor.get(), (Color)this.pathColor.get(), ShapeMode.Lines, 0);
                if (i <= 0) continue;
                Vec3d a = Vec3d.ofCenter((Vec3i)((Vec3i)this.waypoints.get(i - 1)));
                Vec3d b = Vec3d.ofCenter((Vec3i)p);
                event.renderer.line(a.x, a.y, a.z, b.x, b.y, b.z, (Color)this.pathColor.get());
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

