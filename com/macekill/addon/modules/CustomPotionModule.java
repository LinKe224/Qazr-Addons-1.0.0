package com.macekill.addon.modules;

import com.macekill.addon.MaceKillAddon;
import com.macekill.addon.utils.CreativeGiveUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_1935;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5251;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_9334;

public class CustomPotionModule
extends Module {
    private final class_310 mc = class_310.method_1551();
    private final SettingGroup sg = this.settings.getDefaultGroup();
    private final Setting<PotionType> potionType = this.sg.add((Setting)((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u836f\u6c34\u7c7b\u578b")).defaultValue((Object)PotionType.SPLASH)).build());
    private final Setting<Integer> count = this.sg.add((Setting)((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6570\u91cf")).defaultValue((Object)1)).min(1).max(64).sliderMax(64).build());
    private final Setting<List<String>> potionEffects = this.sg.add((Setting)((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u6548\u679c\u5217\u8868")).description("\u6bcf\u884c\u4e00\u4e2a\uff0c\u683c\u5f0f: \u6548\u679cID|\u7b49\u7ea7(0-255)|\u6301\u7eed\u65f6\u95f4(\u79d2)\n\u53ef\u7701\u7565 minecraft: \u524d\u7f00\n\u793a\u4f8b:\n  instant_health|125|1    (\u77ac\u95f4\u6cbb\u7597125\u7ea7)\n  strength|10|300        (\u529b\u91cf10\u7ea7 5\u5206\u949f)\n  regeneration|5|30      (\u518d\u751f5\u7ea7 30\u79d2)\n  speed|2|600            (\u901f\u5ea62\u7ea7 10\u5206\u949f)\n\u5e38\u7528: instant_health, instant_damage, strength, regeneration, speed, slowness, jump_boost, resistance, fire_resistance, invisibility, night_vision, water_breathing, absorption, health_boost, saturation, glowing, levitation, slow_falling, luck, unluck, bad_omen, hero_of_the_village, darkness, weaving, oozing, infested, wind_charged, raid_omen, trial_omen")).defaultValue(List.of("instant_health|125|1"))).build());
    private final Setting<Boolean> customColor = this.sg.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u5b9a\u4e49\u989c\u8272")).defaultValue((Object)false)).build());
    private final Setting<SettingColor> potionColor = this.sg.add((Setting)((ColorSetting.Builder)((ColorSetting.Builder)new ColorSetting.Builder().name("\u836f\u6c34\u989c\u8272")).description("\u70b9\u51fb\u6253\u5f00 HSV \u989c\u8272\u9009\u62e9\u5668, \u9009\u5b9a\u540e\u5e94\u7528\u5230\u836f\u6c34\u7684\u7c92\u5b50\u989c\u8272")).defaultValue(new SettingColor(255, 0, 0)).build());
    private final Setting<Boolean> customName = this.sg.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u5b9a\u4e49\u540d\u79f0")).defaultValue((Object)false)).build());
    private final Setting<String> nameText = this.sg.add((Setting)((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u540d\u79f0\u6587\u672c")).description("\u652f\u6301\u989c\u8272\u4ee3\u7801: &6\u91d1\u8272 &c\u7ea2\u8272 &a\u7eff\u8272 &b\u6d45\u84dd &9\u84dd &d\u7c89 &e\u9ec4 &f\u767d &0\u9ed1 \u7b49\n&l=\u7c97\u4f53 &o=\u659c\u4f53 &n=\u4e0b\u5212\u7ebf &m=\u5220\u9664\u7ebf &k=\u6df7\u6dc6")).defaultValue((Object)"&c&lQazr1234 &6&l\u6740\u622e\u836f\u6c34")).build());
    private final Setting<Boolean> continuous = this.sg.add((Setting)((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8fde\u7eed\u751f\u6210")).defaultValue((Object)false)).build());

    public CustomPotionModule() {
        super(MaceKillAddon.CATEGORY, "\u836f\u6c34\u751f\u6210\u5668", "\u521b\u9020\u6a21\u5f0f\u81ea\u5b9a\u4e49\u836f\u6c34\u751f\u6210\n\u6548\u679c\u5217\u8868: \u6bcf\u884c\u4e00\u4e2a, \u683c\u5f0f \u6548\u679cID|\u7b49\u7ea7|\u6301\u7eed\u79d2\u6570\n\u793a\u4f8b: instant_health|125|1   (\u77ac\u95f4\u6cbb\u7597125\u7ea7)\n\u53ef\u7701\u7565 minecraft: \u524d\u7f00, \u6301\u7eed\u65f6\u95f4\u6700\u592760\u5206\u949f");
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
        PotionType type = (PotionType)((Object)this.potionType.get());
        class_1792 baseItem = switch (type.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> class_1802.field_8574;
            case 1 -> class_1802.field_8436;
            case 2 -> class_1802.field_8150;
        };
        class_1799 stack = new class_1799((class_1935)baseItem, ((Integer)this.count.get()).intValue());
        ArrayList<class_1293> effects = new ArrayList<class_1293>();
        for (String entry : (List)this.potionEffects.get()) {
            String s;
            if (entry == null || (s = entry.trim()).isEmpty()) continue;
            String[] parts = s.split("\\|");
            if (parts.length < 1) {
                CreativeGiveUtil.warn("\u6548\u679c\u683c\u5f0f\u9519\u8bef: " + s + " (\u5e94\u4e3a id|\u7b49\u7ea7|\u79d2)");
                continue;
            }
            String idStr = parts[0].trim();
            int amplifier = 0;
            int durationTicks = 600;
            if (parts.length >= 2) {
                try {
                    amplifier = Integer.parseInt(parts[1].trim());
                }
                catch (Exception e) {
                    CreativeGiveUtil.warn("\u6548\u679c\u7b49\u7ea7\u89e3\u6790\u9519\u8bef: " + parts[1]);
                }
            }
            if (parts.length >= 3) {
                try {
                    int seconds = Integer.parseInt(parts[2].trim());
                    durationTicks = Math.min(seconds * 20, 72000);
                }
                catch (Exception e) {
                    CreativeGiveUtil.warn("\u6548\u679c\u6301\u7eed\u65f6\u95f4\u89e3\u6790\u9519\u8bef: " + parts[2]);
                }
            }
            try {
                class_2960 effId = class_2960.method_12829((String)(idStr.contains(":") ? idStr : "minecraft:" + idStr));
                if (effId == null) {
                    CreativeGiveUtil.warn("\u6548\u679cID\u9519\u8bef: " + idStr);
                    continue;
                }
                Optional ref = this.statusEffectRegistry().method_10223(effId);
                if (ref.isEmpty()) {
                    CreativeGiveUtil.warn("\u6548\u679c\u672a\u627e\u5230: " + idStr);
                    continue;
                }
                effects.add(new class_1293((class_6880)ref.get(), durationTicks, amplifier, false, true));
            }
            catch (Exception e) {
                CreativeGiveUtil.warn("\u6548\u679c\u5931\u8d25 [" + idStr + "]: " + e.getMessage());
            }
        }
        if (effects.isEmpty()) {
            CreativeGiveUtil.warn("\u672a\u6dfb\u52a0\u4efb\u4f55\u6548\u679c!");
            return;
        }
        SettingColor sc = (SettingColor)this.potionColor.get();
        int rgb = sc.r << 16 | sc.g << 8 | sc.b;
        Optional color = (Boolean)this.customColor.get() != false ? Optional.of(rgb) : Optional.empty();
        class_1844 contents = new class_1844(Optional.empty(), color, effects, Optional.empty());
        stack.method_57379(class_9334.field_49651, (Object)contents);
        if (((Boolean)this.customName.get()).booleanValue()) {
            stack.method_57379(class_9334.field_49631, (Object)this.parseName((String)this.nameText.get()));
        }
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u5df2\u751f\u6210 " + String.valueOf((Object)type) + " \u836f\u6c34 x" + stack.method_7947() + " (\u6548\u679c\u6570: " + effects.size() + ")");
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

    private class_2378<class_1291> statusEffectRegistry() {
        return (class_2378)this.mc.field_1687.method_30349().method_46759(class_7924.field_41208).orElseThrow();
    }

    public static enum PotionType {
        REGULAR,
        SPLASH,
        LINGERING;

    }
}
