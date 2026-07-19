package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2596;
import net.minecraft.class_7439;

public class AutoFuckModule
extends Module {
    private final Random random = new Random();
    private final SettingGroup sgMode = this.settings.createGroup("\u6a21\u5f0f");
    private final SettingGroup sgPlayer = this.settings.createGroup("\u76ee\u6807\u73a9\u5bb6");
    private final SettingGroup sgPhrases = this.settings.createGroup("\u8bed\u5f55");
    private final SettingGroup sgTypo = this.settings.createGroup("\u9519\u5b57");
    private final Setting<TriggerMode> triggerMode = this.sgMode.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u89e6\u53d1\u6a21\u5f0f")).description("\u968f\u673a\u95f4\u9694 / \u7b49\u5f85\u5bf9\u65b9\u6d88\u606f")).defaultValue((Object)TriggerMode.RANDOM_INTERVAL)).build());
    private final Setting<Double> minIntervalSec = this.sgMode.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u6700\u5c0f\u95f4\u9694")).description("\u968f\u673a\u53d1\u9001\u7684\u6700\u5c0f\u95f4\u9694\uff08\u79d2\uff09")).defaultValue(5.0).min(1.0).max(30.0).sliderRange(1.0, 30.0).visible(() -> this.triggerMode.get() == TriggerMode.RANDOM_INTERVAL)).build());
    private final Setting<Double> maxIntervalSec = this.sgMode.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u6700\u5927\u95f4\u9694")).description("\u968f\u673a\u53d1\u9001\u7684\u6700\u5927\u95f4\u9694\uff08\u79d2\uff09")).defaultValue(15.0).min(1.0).max(30.0).sliderRange(1.0, 30.0).visible(() -> this.triggerMode.get() == TriggerMode.RANDOM_INTERVAL)).build());
    private final Setting<Integer> waitMsgCountMin = this.sgMode.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u7b49\u5f85\u6d88\u606f\u6570(\u6700\u5c0f)")).description("\u7b49\u5f85\u5bf9\u65b9\u53d1\u591a\u5c11\u6761\u6d88\u606f\u540e\u89e6\u53d1")).defaultValue((Object)2)).min(1).max(50).sliderRange(1, 20).visible(() -> this.triggerMode.get() == TriggerMode.WAIT_FOR_MESSAGE)).build());
    private final Setting<Integer> waitMsgCountMax = this.sgMode.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u7b49\u5f85\u6d88\u606f\u6570(\u6700\u5927)")).description("\u968f\u673a\u6700\u5927\u7b49\u5f85\u6d88\u606f\u6570\uff08\u7b49\u4e8e\u6700\u5c0f\u503c\u5219\u56fa\u5b9a\uff09")).defaultValue((Object)5)).min(1).max(50).sliderRange(1, 20).visible(() -> this.triggerMode.get() == TriggerMode.WAIT_FOR_MESSAGE)).build());
    private final Setting<SendMode> sendMode = this.sgMode.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u53d1\u9001\u6a21\u5f0f")).description("\u5355\u53d1 / \u8fde\u53d1")).defaultValue((Object)SendMode.SINGLE)).build());
    private final Setting<Integer> burstCountMin = this.sgMode.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u8fde\u53d1\u6570\u91cf(\u6700\u5c0f)")).defaultValue((Object)2)).min(1).max(20).sliderRange(1, 10).visible(() -> this.sendMode.get() == SendMode.BURST)).build());
    private final Setting<Integer> burstCountMax = this.sgMode.add((Setting)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u8fde\u53d1\u6570\u91cf(\u6700\u5927)")).defaultValue((Object)5)).min(1).max(20).sliderRange(1, 10).visible(() -> this.sendMode.get() == SendMode.BURST)).build());
    private final Setting<Double> burstIntervalMin = this.sgMode.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u8fde\u53d1\u95f4\u9694(\u6700\u5c0f)")).description("\u8fde\u53d1\u6bcf\u6761\u4e4b\u95f4\u7684\u95f4\u9694\uff08\u79d2\uff09")).defaultValue(0.5).min(0.1).max(5.0).sliderRange(0.1, 5.0).visible(() -> this.sendMode.get() == SendMode.BURST)).build());
    private final Setting<Double> burstIntervalMax = this.sgMode.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u8fde\u53d1\u95f4\u9694(\u6700\u5927)")).description("\u8fde\u53d1\u6bcf\u6761\u4e4b\u95f4\u7684\u968f\u673a\u6700\u5927\u95f4\u9694\uff08\u79d2\uff09")).defaultValue(1.5).min(0.1).max(5.0).sliderRange(0.1, 5.0).visible(() -> this.sendMode.get() == SendMode.BURST)).build());
    private final Setting<PlayerMode> playerMode = this.sgPlayer.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u76ee\u6807\u9009\u62e9")).description("\u6700\u8fd1\u73a9\u5bb6 / \u968f\u673a\u73a9\u5bb6 / \u56fa\u5b9a\u73a9\u5bb6")).defaultValue((Object)PlayerMode.NEAREST)).build());
    private final Setting<String> fixedPlayer = this.sgPlayer.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u56fa\u5b9a\u73a9\u5bb6\u540d")).defaultValue((Object)"")).visible(() -> this.playerMode.get() == PlayerMode.FIXED)).build());
    private final Setting<String> customCommand = this.sgPlayer.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u81ea\u5b9a\u4e49\u6307\u4ee4")).description("\u4e3a\u7a7a\u5219\u516c\u804a\u53d1\u9001\uff0c\u586b\u5199\u5219\u7528\u6307\u4ee4\u53d1\u9001\u3002{player}=\u76ee\u6807\u540d {fuck}=\u9a82\u4eba\u6587\u672c")).defaultValue((Object)"")).build());
    private final Setting<List<String>> phraseList = this.sgPhrases.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u8bed\u5f55\u5217\u8868")).description("\u53f3\u952e\u5217\u8868\u6253\u5f00\u7f16\u8f91\u754c\u9762\u3002\u7528 [\u7ec4\u540d] \u5f00\u5934\u5efa\u7acb\u5206\u7ec4\uff0c{player} = \u76ee\u6807\u73a9\u5bb6\u540d")).defaultValue(new String[]{"[\u9ed8\u8ba4]", "\u4f60\u83dc\u5f97\u6263\u811a {player}", "{player} \u4f60\u597d\u83dc\u554a", "ez {player}"}).build());
    private final Setting<Boolean> typoEnabled = this.sgTypo.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u542f\u7528\u9519\u5b57")).description("\u53d1\u9001\u65f6\u968f\u673a\u6253\u4e71\u5b57\u6bcd\u5927\u5c0f\u5199\u3001\u6807\u70b9\u7b26\u53f7\u3001\u968f\u673a\u5220\u9664\u5b57\u7b26")).defaultValue((Object)false)).build());
    private final Setting<Double> typoFrequency = this.sgTypo.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u9519\u5b57\u9891\u7387")).description("\u6bcf\u6761\u6d88\u606f\u89e6\u53d1\u9519\u5b57\u7684\u6982\u7387\uff080=\u4ece\u4e0d, 1=\u603b\u662f\uff09")).defaultValue(0.3).min(0.0).max(1.0).sliderRange(0.0, 1.0).visible(() -> this.typoEnabled.get())).build());
    private final Setting<Double> typoStrength = this.sgTypo.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u9519\u5b57\u5f3a\u5ea6")).description("\u9519\u5b57\u7a0b\u5ea6\uff080=\u51e0\u4e4e\u4e0d\u53d8, 1=\u9762\u76ee\u5168\u975e\uff09")).defaultValue(0.5).min(0.0).max(1.0).sliderRange(0.0, 1.0).visible(() -> this.typoEnabled.get())).build());
    private final Setting<List<String>> typoTable = this.sgTypo.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u9519\u5b57\u66ff\u6362\u8868")).description("\u683c\u5f0f\uff1a\u539f\u8bcd -> \u66ff\u6362\u8bcd\uff08\u6bcf\u884c\u4e00\u6761\u6620\u5c04\uff09")).defaultValue(new String[]{"ni hao -> n1 h4o", "hello -> he110"}).visible(() -> this.typoEnabled.get())).build());
    private int tickCounter;
    private int nextTriggerTick;
    private int waitedMsgCount;
    private int needMsgCount;
    private int burstRemaining;
    private int burstDelayTicks;

    public AutoFuckModule() {
        super(MaceKillAddon.CATEGORY, "AutoFuck", "\u81ea\u52a8\u9a82\u4eba");
    }

    public void onActivate() {
        this.resetState();
    }

    public void onDeactivate() {
        this.resetState();
    }

    private void resetState() {
        this.tickCounter = 0;
        this.nextTriggerTick = this.randomTriggerDelay();
        this.waitedMsgCount = 0;
        this.needMsgCount = this.randomRange((Integer)this.waitMsgCountMin.get(), (Integer)this.waitMsgCountMax.get());
        this.burstRemaining = 0;
        this.burstDelayTicks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
            return;
        }
        if (this.burstRemaining > 0) {
            if (this.burstDelayTicks > 0) {
                --this.burstDelayTicks;
                return;
            }
            this.doSend();
            --this.burstRemaining;
            if (this.burstRemaining > 0) {
                this.burstDelayTicks = this.randomBurstDelayTicks();
            }
            return;
        }
        ++this.tickCounter;
        if (this.triggerMode.get() == TriggerMode.RANDOM_INTERVAL && this.tickCounter >= this.nextTriggerTick) {
            this.trigger();
            this.tickCounter = 0;
            this.nextTriggerTick = this.randomTriggerDelay();
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            return;
        }
        if (this.triggerMode.get() != TriggerMode.WAIT_FOR_MESSAGE) {
            return;
        }
        class_2596 class_25962 = event.packet;
        if (!(class_25962 instanceof class_7439)) {
            return;
        }
        class_7439 packet = (class_7439)class_25962;
        String myName = this.mc.field_1724.method_5477().getString();
        String sender = this.getMessageAuthor(packet);
        if (sender.isEmpty()) {
            return;
        }
        if (sender.equals(myName)) {
            return;
        }
        String target = this.getTargetName();
        if (!target.isEmpty() && !sender.equals(target)) {
            return;
        }
        ++this.waitedMsgCount;
        if (this.waitedMsgCount >= this.needMsgCount) {
            this.trigger();
            this.waitedMsgCount = 0;
            this.needMsgCount = this.randomRange((Integer)this.waitMsgCountMin.get(), (Integer)this.waitMsgCountMax.get());
        }
    }

    private void trigger() {
        String target = this.getTargetName();
        if (target.isEmpty()) {
            return;
        }
        List<String> phrases = this.collectPhrases();
        if (phrases.isEmpty()) {
            return;
        }
        if (this.sendMode.get() == SendMode.BURST) {
            this.burstRemaining = this.randomRange((Integer)this.burstCountMin.get(), (Integer)this.burstCountMax.get());
            this.burstDelayTicks = 0;
        } else {
            this.doSend();
        }
    }

    private void doSend() {
        String cmd;
        if (this.mc.method_1562() == null || this.mc.field_1724 == null) {
            return;
        }
        List<String> phrases = this.collectPhrases();
        if (phrases.isEmpty()) {
            return;
        }
        String target = this.getTargetName();
        if (target.isEmpty()) {
            return;
        }
        String msg = phrases.get(this.random.nextInt(phrases.size()));
        msg = msg.replace("{player}", target);
        if (((Boolean)this.typoEnabled.get()).booleanValue() && this.random.nextDouble() < (Double)this.typoFrequency.get()) {
            msg = this.applyTypo(msg);
        }
        if (!(cmd = (String)this.customCommand.get()).isEmpty()) {
            msg = cmd.replace("{player}", target).replace("{fuck}", msg);
        }
        this.mc.method_1562().method_45729(msg);
    }

    private List<String> collectPhrases() {
        ArrayList<String> result = new ArrayList<String>();
        for (String line : (List)this.phraseList.get()) {
            if (line.isEmpty() || line.startsWith("[") && line.endsWith("]")) continue;
            result.add(line);
        }
        return result;
    }

    private String getTargetName() {
        return switch (((PlayerMode)((Object)this.playerMode.get())).ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.getNearestPlayerName();
            case 1 -> this.getRandomPlayerName();
            case 2 -> (String)this.fixedPlayer.get();
        };
    }

    private String getNearestPlayerName() {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            return "";
        }
        class_1657 nearest = null;
        double best = Double.MAX_VALUE;
        for (class_1657 p : this.mc.field_1687.method_18456()) {
            double d;
            if (p == this.mc.field_1724 || !((d = this.mc.field_1724.method_5858((class_1297)p)) < best)) continue;
            best = d;
            nearest = p;
        }
        return nearest != null ? nearest.method_5477().getString() : "";
    }

    private String getRandomPlayerName() {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            return "";
        }
        ArrayList<class_1657> others = new ArrayList<class_1657>();
        for (class_1657 p : this.mc.field_1687.method_18456()) {
            if (p == this.mc.field_1724) continue;
            others.add(p);
        }
        if (others.isEmpty()) {
            return "";
        }
        return ((class_1657)others.get(this.random.nextInt(others.size()))).method_5477().getString();
    }

    private String getMessageAuthor(class_7439 packet) {
        int end;
        String text = packet.comp_763().getString();
        if (text.startsWith("<") && (end = text.indexOf(">")) > 1) {
            return text.substring(1, end);
        }
        return "";
    }

    private String applyTypo(String text) {
        double strength = (Double)this.typoStrength.get();
        int ops = Math.max(1, (int)Math.ceil((double)text.length() * strength * 0.3));
        for (int i = 0; i < ops; ++i) {
            int r = this.random.nextInt(3);
            text = switch (r) {
                case 0 -> this.swapCaseRandom(text);
                case 1 -> this.swapPunctuation(text);
                case 2 -> this.deleteRandomChar(text);
                default -> text;
            };
        }
        for (String mapping : (List)this.typoTable.get()) {
            String[] parts = mapping.split("->");
            if (parts.length != 2) continue;
            String from = parts[0].trim();
            String to = parts[1].trim();
            if (!(this.random.nextDouble() < strength)) continue;
            text = text.replace(from, to);
        }
        return text;
    }

    private String swapCaseRandom(String text) {
        if (text.isEmpty()) {
            return text;
        }
        int idx = this.random.nextInt(text.length());
        char c = text.charAt(idx);
        if (Character.isUpperCase(c)) {
            c = Character.toLowerCase(c);
        } else if (Character.isLowerCase(c)) {
            c = Character.toUpperCase(c);
        } else {
            return text;
        }
        return text.substring(0, idx) + c + text.substring(idx + 1);
    }

    private String swapPunctuation(String text) {
        if (text.isEmpty()) {
            return text;
        }
        String[] targets = new String[]{"\uff0c", "\u3002", "\uff01", "\uff1f", ",", ".", "!", "?"};
        String[] replacements = new String[]{",", ".", "!", "?", "\uff0c", "\u3002", "\uff01", "\uff1f"};
        for (int i = 0; i < targets.length; ++i) {
            if (!text.contains(targets[i])) continue;
            int pair = i < 4 ? i + 4 : i - 4;
            text = text.replace(targets[i], replacements[pair]);
            return text;
        }
        return text;
    }

    private String deleteRandomChar(String text) {
        if (text.length() <= 1) {
            return text;
        }
        int idx = this.random.nextInt(text.length());
        return text.substring(0, idx) + text.substring(idx + 1);
    }

    private int randomTriggerDelay() {
        double sec = (Double)this.minIntervalSec.get();
        double max = (Double)this.maxIntervalSec.get();
        if (max > sec) {
            sec += this.random.nextDouble() * (max - sec);
        }
        return (int)(sec * 20.0);
    }

    private int randomBurstDelayTicks() {
        double sec = (Double)this.burstIntervalMin.get();
        double max = (Double)this.burstIntervalMax.get();
        if (max > sec) {
            sec += this.random.nextDouble() * (max - sec);
        }
        return (int)(sec * 20.0);
    }

    private int randomRange(int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + this.random.nextInt(max - min + 1);
    }

    public String getInfoString() {
        String target = this.getTargetName();
        return target.isEmpty() ? "\u65e0\u76ee\u6807" : target;
    }

    private static enum TriggerMode {
        RANDOM_INTERVAL,
        WAIT_FOR_MESSAGE;

    }

    private static enum SendMode {
        SINGLE,
        BURST;

    }

    private static enum PlayerMode {
        NEAREST,
        RANDOM,
        FIXED;

    }
}
