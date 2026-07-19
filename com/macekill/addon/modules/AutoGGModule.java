package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_7439;

public class AutoGGModule
extends Module {
    private static boolean skipChatModify;
    private final SettingGroup sgGeneral;
    private final Setting<List<String>> messageList;
    private final Setting<Double> range;
    private final Setting<String> chatPrefix;
    private final Setting<String> chatSuffix;
    private final List<String> nearbyPlayers;
    private int scanCooldown;
    private int delayTicks;
    private String pendingVictimName;
    private final Random random;
    private boolean processing;

    public AutoGGModule() {
        super(MaceKillAddon.CATEGORY, "\u81ea\u52a8GG", "\u51fb\u6740\u540e\u81ea\u52a8\u53d1\u9001\u6d88\u606f + \u804a\u5929\u524d\u540e\u7f00");
        this.sgGeneral = this.settings.getDefaultGroup();
        this.messageList = this.sgGeneral.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u6d88\u606f\u5217\u8868")).description("\u51fb\u6740\u540e\u968f\u673a\u9009\u62e9\u4e00\u6761\u53d1\u9001\uff0c{player} \u5c06\u88ab\u66ff\u6362\u4e3a\u76ee\u6807\u73a9\u5bb6\u540d")).defaultValue(new String[]{"gg {player}", "ez {player}", "L + ratio {player}"}).build());
        this.range = this.sgGeneral.add((Setting)((DoubleSetting.Builder)((DoubleSetting.Builder)new DoubleSetting.Builder().name("\u68c0\u6d4b\u8303\u56f4")).description("\u9644\u8fd1\u73a9\u5bb6\u7684\u68c0\u6d4b\u8ddd\u79bb\uff08\u683c\uff09")).defaultValue(16.0).min(1.0).max(128.0).sliderMax(64.0).build());
        this.chatPrefix = this.sgGeneral.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u804a\u5929\u524d\u7f00")).description("\u53d1\u9001\u804a\u5929\u6d88\u606f\u65f6\u5728\u524d\u9762\u81ea\u52a8\u63d2\u5165\u7684\u6587\u672c\uff08&\u4ee3\u8868\u00a7\u6362\u8272\u7b26\uff09")).defaultValue((Object)"&7[&bQazr&7]&r ")).build());
        this.chatSuffix = this.sgGeneral.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u804a\u5929\u540e\u7f00")).description("\u53d1\u9001\u804a\u5929\u6d88\u606f\u65f6\u5728\u540e\u9762\u81ea\u52a8\u63d2\u5165\u7684\u6587\u672c\uff08&\u4ee3\u8868\u00a7\u6362\u8272\u7b26\uff09")).defaultValue((Object)"")).build());
        this.nearbyPlayers = new ArrayList<String>();
        this.random = new Random();
    }

    public void onDeactivate() {
        this.nearbyPlayers.clear();
        this.scanCooldown = 0;
        this.delayTicks = 0;
        this.pendingVictimName = null;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            return;
        }
        if (this.scanCooldown <= 0) {
            this.scanCooldown = 10;
            this.updateNearbyPlayers();
        }
        --this.scanCooldown;
        if (this.delayTicks > 0) {
            --this.delayTicks;
            if (this.delayTicks <= 0 && this.pendingVictimName != null) {
                this.sendGGMessage(this.pendingVictimName);
                this.pendingVictimName = null;
            }
        }
    }

    private void updateNearbyPlayers() {
        this.nearbyPlayers.clear();
        double rangeSq = (Double)this.range.get() * (Double)this.range.get();
        for (class_1657 player : this.mc.field_1687.method_18456()) {
            if (player == this.mc.field_1724 || this.mc.field_1724.method_5858((class_1297)player) > rangeSq) continue;
            this.nearbyPlayers.add(player.method_5477().getString());
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (this.processing || skipChatModify) {
            return;
        }
        if (this.mc.field_1724 == null || this.mc.method_1562() == null) {
            return;
        }
        class_2596 class_25962 = event.packet;
        if (class_25962 instanceof class_2797) {
            class_2797 chatPacket = (class_2797)class_25962;
            String prefix = (String)this.chatPrefix.get();
            String suffix = (String)this.chatSuffix.get();
            if (prefix.isEmpty() && suffix.isEmpty()) {
                return;
            }
            String original = chatPacket.comp_945();
            String modified = AutoGGModule.parseColors(prefix) + original + AutoGGModule.parseColors(suffix);
            this.processing = true;
            event.cancel();
            this.mc.method_1562().method_45729(modified);
            this.processing = false;
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
            return;
        }
        if (this.nearbyPlayers.isEmpty()) {
            return;
        }
        class_2596 class_25962 = event.packet;
        if (class_25962 instanceof class_7439) {
            class_7439 packet = (class_7439)class_25962;
            String message = packet.comp_763().getString();
            String myName = this.mc.field_1724.method_5477().getString();
            for (String name : this.nearbyPlayers) {
                String rest;
                if (!message.startsWith(name) || !(rest = message.substring(name.length())).contains(myName)) continue;
                this.triggerKill(name);
                return;
            }
        }
    }

    private void triggerKill(String playerName) {
        if (this.pendingVictimName != null) {
            return;
        }
        this.pendingVictimName = playerName;
        this.delayTicks = this.random.nextInt(21) + 4;
    }

    private void sendGGMessage(String playerName) {
        List messages = (List)this.messageList.get();
        String msg = messages.isEmpty() ? "gg {player}" : (String)messages.get(this.random.nextInt(messages.size()));
        msg = msg.replace("{player}", playerName);
        skipChatModify = true;
        if (this.mc.field_1705 != null) {
            this.mc.field_1705.method_34004((class_2561)class_2561.method_43470((String)("\u00a7c\u51fb\u6740 \u00a7e" + playerName)));
        }
        if (this.mc.field_1724 != null) {
            this.mc.field_1724.method_7353((class_2561)class_2561.method_43470((String)("\u00a7e[AutoGG] \u00a7a" + msg)), false);
        }
        if (this.mc.method_1562() != null) {
            this.mc.method_1562().method_45729(msg);
        }
        skipChatModify = false;
    }

    private static String parseColors(String text) {
        return text.replace('&', '\u00a7');
    }
}
