package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import com.macekill.addon.utils.CreativeGiveUtil;
import java.util.List;
import java.util.Optional;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1887;
import net.minecraft.class_1935;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.class_9274;
import net.minecraft.class_9285;
import net.minecraft.class_9304;
import net.minecraft.class_9334;

public class CreativeGiveModule
extends Module {
    private final class_310 mc = class_310.method_1551();
    private final SettingGroup sg = this.settings.getDefaultGroup();
    private final Setting<Preset> preset = this.sg.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u5feb\u6377\u9884\u8bbe")).defaultValue((Object)Preset.NONE)).build());
    private final Setting<String> itemId = this.sg.add((Setting)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u7269\u54c1ID")).defaultValue((Object)"minecraft:diamond_sword")).build());
    private final Setting<Integer> count = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6570\u91cf")).defaultValue((Object)1)).min(1).max(64).sliderMax(64).build());
    private final Setting<Boolean> enableName = this.sg.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u5b9a\u4e49\u540d\u79f0")).defaultValue((Object)false)).build());
    private final Setting<String> customName = this.sg.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u540d\u79f0\u6587\u672c")).description("\u652f\u6301\u989c\u8272\u4ee3\u7801: \u8f93\u5165 &6\u91d1\u8272 &c\u7ea2\u8272 &a\u7eff\u8272 &b\u6d45\u84dd &9\u84dd &d\u7c89 &e\u9ec4 &f\u767d &0\u9ed1 \u7b49\n& \u52a0\u4e00\u4e2a\u5b57\u6bcd = \u989c\u8272, &l=\u7c97\u4f53 &o=\u659c\u4f53 &n=\u4e0b\u5212\u7ebf &m=\u5220\u9664\u7ebf &k=\u6df7\u6dc6\n\u793a\u4f8b: &6&lQazr1234 &c&l\u795e\u5251")).defaultValue((Object)"&6&lQazr1234 &c&l\u6d4b\u8bd5\u5251")).build());
    private final Setting<List<String>> enchantments = this.sg.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u9644\u9b54\u5217\u8868")).description("\u6bcf\u884c\u4e00\u4e2a\uff0c\u683c\u5f0f: \u9644\u9b54ID:\u7b49\u7ea7 (\u9644\u9b54ID\u53ef\u7701\u7565 minecraft: \u524d\u7f00, \u7b49\u7ea7\u652f\u6301\u4efb\u610f64\u4f4d\u6574\u6570)")).defaultValue(List.of("minecraft:sharpness:10"))).build());
    private final Setting<List<String>> attributes = this.sg.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u5c5e\u6027\u5217\u8868")).description("\u6bcf\u884c\u4e00\u4e2a\uff0c\u683c\u5f0f: \u5c5e\u6027ID|\u503c|\u69fd\u4f4d\n\u5c5e\u6027ID\u53ef\u7701\u7565 minecraft: \u524d\u7f00\n\u69fd\u4f4d: MAINHAND, OFFHAND, HAND, HEAD, CHEST, LEGS, FEET, ARMOR, ANY")).defaultValue(List.of("generic.attack_damage|1000|MAINHAND"))).build());
    private final Setting<Boolean> continuous = this.sg.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8fde\u7eed\u751f\u6210")).defaultValue((Object)false)).build());

    public CreativeGiveModule() {
        super(MaceKillAddon.CATEGORY, "\u7269\u54c1\u751f\u6210\u5668", "\u521b\u9020\u6a21\u5f0f\u901a\u7528\u7269\u54c1\u751f\u6210 - \u7c7b\u578b\u5316\u7ec4\u4ef6\n\u9644\u9b54\u5217\u8868\u683c\u5f0f: \u9644\u9b54ID:\u7b49\u7ea7 (\u5982 minecraft:sharpness:10)\n\u5c5e\u6027\u5217\u8868\u683c\u5f0f: \u5c5e\u6027ID|\u503c|\u69fd\u4f4d (\u5982 generic.attack_damage|1000|MAINHAND)\n\u69fd\u4f4d: MAINHAND, OFFHAND, HAND, HEAD, CHEST, LEGS, FEET, ARMOR, ANY\n\u63d0\u793a: \u836f\u6c34\u8bf7\u4f7f\u7528\u300c\u836f\u6c34\u751f\u6210\u5668\u300d\u6a21\u5757");
    }

    public void onActivate() {
        if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
            this.toggle();
            return;
        }
        if (!this.mc.field_1724.method_31549().field_7477) {
            CreativeGiveUtil.warn("\u9700\u8981\u521b\u9020\u6a21\u5f0f!");
            this.toggle();
            return;
        }
        CreativeGiveUtil.resetError();
        this.generateAndGive();
        if (!((Boolean)this.continuous.get()).booleanValue()) {
            this.toggle();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!((Boolean)this.continuous.get()).booleanValue() || this.mc.field_1724 == null) {
            return;
        }
        if (!this.mc.field_1724.method_31549().field_7477) {
            this.toggle();
            return;
        }
        this.generateAndGive();
    }

    private void generateAndGive() {
        Preset p = (Preset)((Object)this.preset.get());
        if (p != Preset.NONE) {
            this.genPreset(p);
            return;
        }
        class_1792 item = this.resolveItem((String)this.itemId.get());
        if (item == class_1802.field_8162) {
            CreativeGiveUtil.warn("\u672a\u627e\u5230\u7269\u54c1: " + (String)this.itemId.get());
            return;
        }
        class_1799 stack = new class_1799((class_1935)item, ((Integer)this.count.get()).intValue());
        if (((Boolean)this.enableName.get()).booleanValue()) {
            stack.method_57379(class_9334.field_49631, (Object)this.parseName((String)this.customName.get()));
        }
        for (String entry : (List)this.enchantments.get()) {
            this.applyEnchantFromString(stack, entry);
        }
        for (String entry : (List)this.attributes.get()) {
            this.applyAttrFromString(stack, entry);
        }
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u5df2\u751f\u6210 " + stack.method_7947() + "x " + (String)this.itemId.get());
        }
    }

    private class_2561 parseName(String raw) {
        if (raw == null || raw.isEmpty()) {
            return class_2561.method_43470((String)"");
        }
        class_5250 out = class_2561.method_43473();
        StringBuilder buf = new StringBuilder();
        class_2583 curStyle = class_2583.field_24360.method_10978(Boolean.valueOf(false));
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if ((c == '&' || c == '\u00a7') && i + 1 < raw.length()) {
                char code;
                class_124 f;
                if (buf.length() > 0) {
                    out.method_10852((class_2561)class_2561.method_43470((String)buf.toString()).method_10862(curStyle));
                    buf.setLength(0);
                }
                if ((f = class_124.method_544((char)(code = Character.toLowerCase(raw.charAt(++i))))) == null) continue;
                if (f == class_124.field_1067) {
                    curStyle = curStyle.method_10982(Boolean.valueOf(!curStyle.method_10984()));
                    continue;
                }
                if (f == class_124.field_1056) {
                    curStyle = curStyle.method_10978(Boolean.valueOf(!curStyle.method_10966()));
                    continue;
                }
                if (f == class_124.field_1073) {
                    curStyle = curStyle.method_30938(Boolean.valueOf(!curStyle.method_10965()));
                    continue;
                }
                if (f == class_124.field_1055) {
                    curStyle = curStyle.method_36140(Boolean.valueOf(!curStyle.method_10986()));
                    continue;
                }
                if (f == class_124.field_1051) {
                    curStyle = curStyle.method_36141(Boolean.valueOf(!curStyle.method_10987()));
                    continue;
                }
                if (!f.method_543()) continue;
                curStyle = curStyle.method_27703(class_5251.method_27718((class_124)f));
                continue;
            }
            buf.append(c);
        }
        if (buf.length() > 0) {
            out.method_10852((class_2561)class_2561.method_43470((String)buf.toString()).method_10862(curStyle));
        }
        return out;
    }

    private void applyEnchantFromString(class_1799 stack, String entry) {
        long level;
        if (entry == null) {
            return;
        }
        String s = entry.trim();
        if (s.isEmpty()) {
            return;
        }
        int idx = s.lastIndexOf(58);
        if (idx <= 0) {
            CreativeGiveUtil.warn("\u9644\u9b54\u683c\u5f0f\u9519\u8bef: " + s + " (\u5e94\u4e3a id:level)");
            return;
        }
        String id = s.substring(0, idx);
        String levelStr = s.substring(idx + 1);
        try {
            level = Long.parseLong(levelStr.trim());
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u9644\u9b54\u7b49\u7ea7\u89e3\u6790\u9519\u8bef: " + levelStr);
            return;
        }
        try {
            class_2960 enchId = class_2960.method_12829((String)(id.contains(":") ? id : "minecraft:" + id));
            if (enchId == null) {
                CreativeGiveUtil.warn("\u9644\u9b54ID\u9519\u8bef: " + id);
                return;
            }
            Optional ref = this.enchantmentRegistry().method_10223(enchId);
            if (ref.isEmpty()) {
                CreativeGiveUtil.warn("\u9644\u9b54\u672a\u627e\u5230: " + id);
                return;
            }
            int safeLevel = (int)Math.min(level, Integer.MAX_VALUE);
            class_9304 current = (class_9304)stack.method_58695(class_9334.field_49633, (Object)class_9304.field_49385);
            class_9304.class_9305 builder = new class_9304.class_9305(current);
            builder.method_57550((class_6880)ref.get(), safeLevel);
            stack.method_57379(class_9334.field_49633, (Object)builder.method_57549());
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u9644\u9b54\u5931\u8d25 [" + id + "]: " + e.getMessage());
        }
    }

    private void applyAttrFromString(class_1799 stack, String entry) {
        double amount;
        if (entry == null) {
            return;
        }
        String s = entry.trim();
        if (s.isEmpty()) {
            return;
        }
        String[] parts = s.split("\\|");
        if (parts.length < 2) {
            CreativeGiveUtil.warn("\u5c5e\u6027\u683c\u5f0f\u9519\u8bef: " + s + " (\u5e94\u4e3a id|\u503c|\u69fd\u4f4d)");
            return;
        }
        String id = parts[0].trim();
        String amountStr = parts[1].trim();
        String slotStr = parts.length >= 3 ? parts[2].trim().toUpperCase() : "ANY";
        try {
            amount = Double.parseDouble(amountStr);
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u5c5e\u6027\u503c\u89e3\u6790\u9519\u8bef: " + amountStr);
            return;
        }
        try {
            class_2960 attrId = class_2960.method_12829((String)(id.contains(":") ? id : "minecraft:" + id));
            if (attrId == null) {
                CreativeGiveUtil.warn("\u5c5e\u6027ID\u9519\u8bef: " + id);
                return;
            }
            Optional ref = this.attributeRegistry().method_10223(attrId);
            if (ref.isEmpty()) {
                CreativeGiveUtil.warn("\u5c5e\u6027\u672a\u627e\u5230: " + id);
                return;
            }
            class_1322 modifier = new class_1322(class_2960.method_60655((String)"qazr1234", (String)("mod_" + System.nanoTime())), amount, class_1322.class_1323.field_6328);
            class_9274 slot = this.parseSlot(slotStr);
            class_9285 current = (class_9285)stack.method_58695(class_9334.field_49636, (Object)class_9285.field_49326);
            class_9285 updated = current.method_57484((class_6880)ref.get(), modifier, slot);
            stack.method_57379(class_9334.field_49636, (Object)updated);
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u5c5e\u6027\u5931\u8d25 [" + id + "]: " + e.getMessage());
        }
    }

    private class_9274 parseSlot(String s) {
        try {
            return class_9274.valueOf((String)s);
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u672a\u77e5\u69fd\u4f4d: " + s + " (\u4f7f\u7528ANY)");
            return class_9274.field_49216;
        }
    }

    private class_2378<class_1887> enchantmentRegistry() {
        return (class_2378)this.mc.field_1687.method_30349().method_46759(class_7924.field_41265).orElseThrow();
    }

    private class_2378<class_1320> attributeRegistry() {
        return (class_2378)this.mc.field_1687.method_30349().method_46759(class_7924.field_41251).orElseThrow();
    }

    private void genPreset(Preset p) {
        switch (p.ordinal()) {
            case 1: {
                this.genDamageSword();
                break;
            }
            case 2: {
                this.genDamageArmor();
                break;
            }
            case 3: {
                this.genTotem();
            }
        }
    }

    private void genDamageSword() {
        class_1799 stack = new class_1799((class_1935)class_1802.field_22022, 1);
        stack.method_57379(class_9334.field_49631, (Object)class_2561.method_43470((String)"Qazr1234 \u4f24\u5bb3\u795e\u5251").method_27695(new class_124[]{class_124.field_1054, class_124.field_1067}));
        Optional dmgAttr = this.attributeRegistry().method_10223(class_2960.method_60654((String)"generic.attack_damage"));
        if (dmgAttr.isPresent()) {
            class_1322 mod = new class_1322(class_2960.method_60655((String)"qazr1234", (String)"dmg"), 137891.0, class_1322.class_1323.field_6328);
            class_9285 current = (class_9285)stack.method_58695(class_9334.field_49636, (Object)class_9285.field_49326);
            stack.method_57379(class_9334.field_49636, (Object)current.method_57484((class_6880)dmgAttr.get(), mod, class_9274.field_49217));
        }
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u4f24\u5bb3\u795e\u5251(137891)\u5df2\u751f\u6210");
        }
    }

    private void genDamageArmor() {
        class_1792[] pieces = new class_1792[]{class_1802.field_22027, class_1802.field_22028, class_1802.field_22029, class_1802.field_22030};
        class_9274[] groups = new class_9274[]{class_9274.field_49223, class_9274.field_49222, class_9274.field_49221, class_9274.field_49220};
        Optional armor = this.attributeRegistry().method_10223(class_2960.method_60654((String)"generic.armor"));
        Optional toughness = this.attributeRegistry().method_10223(class_2960.method_60654((String)"generic.armor_toughness"));
        Optional kb = this.attributeRegistry().method_10223(class_2960.method_60654((String)"generic.knockback_resistance"));
        for (int i = 0; i < pieces.length; ++i) {
            class_1799 stack = new class_1799((class_1935)pieces[i], 1);
            stack.method_57379(class_9334.field_49631, (Object)class_2561.method_43470((String)"Qazr1234 \u4f24\u5bb3\u795e\u7532").method_27695(new class_124[]{class_124.field_1076, class_124.field_1067}));
            class_9285 comp = (class_9285)stack.method_58695(class_9334.field_49636, (Object)class_9285.field_49326);
            if (armor.isPresent()) {
                comp = comp.method_57484((class_6880)armor.get(), new class_1322(class_2960.method_60655((String)"qazr1234", (String)("armor" + i)), 137891.0, class_1322.class_1323.field_6328), groups[i]);
            }
            if (toughness.isPresent()) {
                comp = comp.method_57484((class_6880)toughness.get(), new class_1322(class_2960.method_60655((String)"qazr1234", (String)("tough" + i)), 137891.0, class_1322.class_1323.field_6328), groups[i]);
            }
            if (kb.isPresent()) {
                comp = comp.method_57484((class_6880)kb.get(), new class_1322(class_2960.method_60655((String)"qazr1234", (String)("kb" + i)), 1.0, class_1322.class_1323.field_6328), groups[i]);
            }
            stack.method_57379(class_9334.field_49636, (Object)comp);
            CreativeGiveUtil.give(stack);
        }
        CreativeGiveUtil.info("\u4f24\u5bb3\u795e\u7532\u5168\u5957(137891)\u5df2\u751f\u6210");
    }

    private void genTotem() {
        class_1799 stack = new class_1799((class_1935)class_1802.field_8288, 1);
        stack.method_57379(class_9334.field_49631, (Object)class_2561.method_43470((String)"Qazr1234 \u4e0d\u6b7b\u56fe\u817e").method_27695(new class_124[]{class_124.field_1065, class_124.field_1067}));
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u4e0d\u6b7b\u56fe\u817e\u5df2\u751f\u6210");
        }
    }

    private class_1792 resolveItem(String id) {
        Object fullId = id.contains(":") ? id : "minecraft:" + id;
        class_2960 identifier = class_2960.method_12829((String)fullId);
        return identifier != null ? (class_1792)class_7923.field_41178.method_63535(identifier) : class_1802.field_8162;
    }

    public static enum Preset {
        NONE,
        DAMAGE_SWORD,
        DAMAGE_ARMOR,
        TOTEM;

    }
}
