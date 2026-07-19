/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  meteordevelopment.meteorclient.events.world.TickEvent$Post
 *  meteordevelopment.meteorclient.settings.BoolSetting$Builder
 *  meteordevelopment.meteorclient.settings.ColorSetting$Builder
 *  meteordevelopment.meteorclient.settings.EnumSetting$Builder
 *  meteordevelopment.meteorclient.settings.IntSetting$Builder
 *  meteordevelopment.meteorclient.settings.Setting
 *  meteordevelopment.meteorclient.settings.SettingGroup
 *  meteordevelopment.meteorclient.settings.StringListSetting$Builder
 *  meteordevelopment.meteorclient.settings.StringSetting$Builder
 *  meteordevelopment.meteorclient.systems.modules.Module
 *  meteordevelopment.meteorclient.utils.render.color.SettingColor
 *  meteordevelopment.orbit.EventHandler
 *  net.minecraft.Formatting
 *  net.minecraft.StatusEffect
 *  net.minecraft.StatusEffectInstance
 *  net.minecraft.Item
 *  net.minecraft.ItemStack
 *  net.minecraft.Items
 *  net.minecraft.PotionContentsComponent
 *  net.minecraft.ItemConvertible
 *  net.minecraft.Registry
 *  net.minecraft.Text
 *  net.minecraft.Style
 *  net.minecraft.Identifier
 *  net.minecraft.MinecraftClient
 *  net.minecraft.MutableText
 *  net.minecraft.TextColor
 *  net.minecraft.RegistryEntry
 *  net.minecraft.RegistryKeys
 *  net.minecraft.DataComponentTypes
 */
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
import net.minecraft.util.Formatting;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.component.DataComponentTypes;

public class CustomPotionModule
extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final SettingGroup sg = this.settings.getDefaultGroup();
    private final Setting<PotionType> potionType = this.sg.add(((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u836f\u6c34\u7c7b\u578b")).defaultValue(PotionType.SPLASH)).build());
    private final Setting<Integer> count = this.sg.add(((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6570\u91cf")).defaultValue(1)).min(1).max(64).sliderMax(64).build());
    private final Setting<List<String>> potionEffects = this.sg.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u6548\u679c\u5217\u8868")).description("\u6bcf\u884c\u4e00\u4e2a\uff0c\u683c\u5f0f: \u6548\u679cID|\u7b49\u7ea7(0-255)|\u6301\u7eed\u65f6\u95f4(\u79d2)\n\u53ef\u7701\u7565 minecraft: \u524d\u7f00\n\u793a\u4f8b:\n  instant_health|125|1    (\u77ac\u95f4\u6cbb\u7597125\u7ea7)\n  strength|10|300        (\u529b\u91cf10\u7ea7 5\u5206\u949f)\n  regeneration|5|30      (\u518d\u751f5\u7ea7 30\u79d2)\n  speed|2|600            (\u901f\u5ea62\u7ea7 10\u5206\u949f)\n\u5e38\u7528: instant_health, instant_damage, strength, regeneration, speed, slowness, jump_boost, resistance, fire_resistance, invisibility, night_vision, water_breathing, absorption, health_boost, saturation, glowing, levitation, slow_falling, luck, unluck, bad_omen, hero_of_the_village, darkness, weaving, oozing, infested, wind_charged, raid_omen, trial_omen")).defaultValue(List.of("instant_health|125|1"))).build());
    private final Setting<Boolean> customColor = this.sg.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u5b9a\u4e49\u989c\u8272")).defaultValue(false)).build());
    private final Setting<SettingColor> potionColor = this.sg.add(((ColorSetting.Builder)((ColorSetting.Builder)new ColorSetting.Builder().name("\u836f\u6c34\u989c\u8272")).description("\u70b9\u51fb\u6253\u5f00 HSV \u989c\u8272\u9009\u62e9\u5668, \u9009\u5b9a\u540e\u5e94\u7528\u5230\u836f\u6c34\u7684\u7c92\u5b50\u989c\u8272")).defaultValue(new SettingColor(255, 0, 0)).build());
    private final Setting<Boolean> customName = this.sg.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u5b9a\u4e49\u540d\u79f0")).defaultValue(false)).build());
    private final Setting<String> nameText = this.sg.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u540d\u79f0\u6587\u672c")).description("\u652f\u6301\u989c\u8272\u4ee3\u7801: &6\u91d1\u8272 &c\u7ea2\u8272 &a\u7eff\u8272 &b\u6d45\u84dd &9\u84dd &d\u7c89 &e\u9ec4 &f\u767d &0\u9ed1 \u7b49\n&l=\u7c97\u4f53 &o=\u659c\u4f53 &n=\u4e0b\u5212\u7ebf &m=\u5220\u9664\u7ebf &k=\u6df7\u6dc6")).defaultValue("&c&lQazr1234 &6&l\u6740\u622e\u836f\u6c34")).build());
    private final Setting<Boolean> continuous = this.sg.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8fde\u7eed\u751f\u6210")).defaultValue(false)).build());

    public CustomPotionModule() {
        super(MaceKillAddon.CATEGORY, "\u836f\u6c34\u751f\u6210\u5668", "\u521b\u9020\u6a21\u5f0f\u81ea\u5b9a\u4e49\u836f\u6c34\u751f\u6210\n\u6548\u679c\u5217\u8868: \u6bcf\u884c\u4e00\u4e2a, \u683c\u5f0f \u6548\u679cID|\u7b49\u7ea7|\u6301\u7eed\u79d2\u6570\n\u793a\u4f8b: instant_health|125|1   (\u77ac\u95f4\u6cbb\u7597125\u7ea7)\n\u53ef\u7701\u7565 minecraft: \u524d\u7f00, \u6301\u7eed\u65f6\u95f4\u6700\u592760\u5206\u949f");
    }

    public void onActivate() {
        if (this.mc.player == null || this.mc.world == null) {
            this.toggle();
            return;
        }
        if (!this.mc.player.getAbilities().creativeMode) {
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
        if (!((Boolean)this.continuous.get()).booleanValue() || this.mc.player == null) {
            return;
        }
        if (!this.mc.player.getAbilities().creativeMode) {
            this.toggle();
            return;
        }
        this.generateAndGive();
    }

    private void generateAndGive() {
        PotionType type = (PotionType)(this.potionType.get());
        Item baseItem = switch (type.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> Items.POTION;
            case 1 -> Items.SPLASH_POTION;
            case 2 -> Items.LINGERING_POTION;
        };
        ItemStack stack = new ItemStack((ItemConvertible)baseItem, ((Integer)this.count.get()).intValue());
        ArrayList<StatusEffectInstance> effects = new ArrayList<StatusEffectInstance>();
        for (String entry : (List<String>)this.potionEffects.get()) {
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
                Identifier effId = Identifier.tryParse((String)(idStr.contains(":") ? idStr : "minecraft:" + idStr));
                if (effId == null) {
                    CreativeGiveUtil.warn("\u6548\u679cID\u9519\u8bef: " + idStr);
                    continue;
                }
                Optional ref = this.statusEffectRegistry().getEntry(effId);
                if (ref.isEmpty()) {
                    CreativeGiveUtil.warn("\u6548\u679c\u672a\u627e\u5230: " + idStr);
                    continue;
                }
                effects.add(new StatusEffectInstance((RegistryEntry)ref.get(), durationTicks, amplifier, false, true));
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
        PotionContentsComponent contents = new PotionContentsComponent(Optional.empty(), color, effects, Optional.empty());
        stack.set(DataComponentTypes.POTION_CONTENTS, contents);
        if (((Boolean)this.customName.get()).booleanValue()) {
            stack.set(DataComponentTypes.CUSTOM_NAME, this.parseName((String)this.nameText.get()));
        }
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u5df2\u751f\u6210 " + String.valueOf(type) + " \u836f\u6c34 x" + stack.getCount() + " (\u6548\u679c\u6570: " + effects.size() + ")");
        }
    }

    private Text parseName(String raw) {
        if (raw == null || raw.isEmpty()) {
            return Text.literal((String)"");
        }
        MutableText out = Text.empty();
        StringBuilder buf = new StringBuilder();
        Style curStyle = Style.EMPTY.withItalic(Boolean.valueOf(false));
        for (int i = 0; i < raw.length(); ++i) {
            char c = raw.charAt(i);
            if ((c == '&' || c == '\u00a7') && i + 1 < raw.length()) {
                char code;
                Formatting f;
                if (buf.length() > 0) {
                    out.append((Text)Text.literal((String)buf.toString()).setStyle(curStyle));
                    buf.setLength(0);
                }
                if ((f = Formatting.byCode((char)(code = Character.toLowerCase(raw.charAt(++i))))) == null) continue;
                if (f == Formatting.BOLD) {
                    curStyle = curStyle.withBold(Boolean.valueOf(!curStyle.isBold()));
                    continue;
                }
                if (f == Formatting.ITALIC) {
                    curStyle = curStyle.withItalic(Boolean.valueOf(!curStyle.isItalic()));
                    continue;
                }
                if (f == Formatting.UNDERLINE) {
                    curStyle = curStyle.withUnderline(Boolean.valueOf(!curStyle.isUnderlined()));
                    continue;
                }
                if (f == Formatting.STRIKETHROUGH) {
                    curStyle = curStyle.withStrikethrough(Boolean.valueOf(!curStyle.isStrikethrough()));
                    continue;
                }
                if (f == Formatting.OBFUSCATED) {
                    curStyle = curStyle.withObfuscated(Boolean.valueOf(!curStyle.isObfuscated()));
                    continue;
                }
                if (!f.isColor()) continue;
                curStyle = curStyle.withColor(TextColor.fromFormatting((Formatting)f));
                continue;
            }
            buf.append(c);
        }
        if (buf.length() > 0) {
            out.append((Text)Text.literal((String)buf.toString()).setStyle(curStyle));
        }
        return out;
    }

    private Registry<StatusEffect> statusEffectRegistry() {
        return (Registry)this.mc.world.getRegistryManager().getOptional(RegistryKeys.STATUS_EFFECT).orElseThrow();
    }

    public static enum PotionType {
        REGULAR,
        SPLASH,
        LINGERING;

    }
}

