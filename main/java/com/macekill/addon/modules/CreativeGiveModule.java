/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  meteordevelopment.meteorclient.events.world.TickEvent$Post
 *  meteordevelopment.meteorclient.settings.BoolSetting$Builder
 *  meteordevelopment.meteorclient.settings.EnumSetting$Builder
 *  meteordevelopment.meteorclient.settings.IntSetting$Builder
 *  meteordevelopment.meteorclient.settings.Setting
 *  meteordevelopment.meteorclient.settings.SettingGroup
 *  meteordevelopment.meteorclient.settings.StringListSetting$Builder
 *  meteordevelopment.meteorclient.settings.StringSetting$Builder
 *  meteordevelopment.meteorclient.systems.modules.Module
 *  meteordevelopment.orbit.EventHandler
 *  net.minecraft.Formatting
 *  net.minecraft.EntityAttribute
 *  net.minecraft.EntityAttributeModifier
 *  net.minecraft.EntityAttributeModifier$class_1323
 *  net.minecraft.Item
 *  net.minecraft.ItemStack
 *  net.minecraft.Items
 *  net.minecraft.Enchantment
 *  net.minecraft.ItemConvertible
 *  net.minecraft.Registry
 *  net.minecraft.Text
 *  net.minecraft.Style
 *  net.minecraft.Identifier
 *  net.minecraft.MinecraftClient
 *  net.minecraft.MutableText
 *  net.minecraft.TextColor
 *  net.minecraft.RegistryEntry
 *  net.minecraft.Registries
 *  net.minecraft.RegistryKeys
 *  net.minecraft.AttributeModifierSlot
 *  net.minecraft.AttributeModifiersComponent
 *  net.minecraft.ItemEnchantmentsComponent
 *  net.minecraft.ItemEnchantmentsComponent$class_9305
 *  net.minecraft.DataComponentTypes
 */
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
import net.minecraft.util.Formatting;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.TextColor;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.DataComponentTypes;

public class CreativeGiveModule
extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final SettingGroup sg = this.settings.getDefaultGroup();
    private final Setting<Preset> preset = this.sg.add(((EnumSetting.Builder)((EnumSetting.Builder)new EnumSetting.Builder().name("\u5feb\u6377\u9884\u8bbe")).defaultValue(Preset.NONE)).build());
    private final Setting<String> itemId = this.sg.add(((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u7269\u54c1ID")).defaultValue("minecraft:diamond_sword")).build());
    private final Setting<Integer> count = this.sg.add(((IntSetting.Builder)((IntSetting.Builder)new IntSetting.Builder().name("\u6570\u91cf")).defaultValue(1)).min(1).max(64).sliderMax(64).build());
    private final Setting<Boolean> enableName = this.sg.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u81ea\u5b9a\u4e49\u540d\u79f0")).defaultValue(false)).build());
    private final Setting<String> customName = this.sg.add(((StringSetting.Builder)((StringSetting.Builder)((StringSetting.Builder)new StringSetting.Builder().name("\u540d\u79f0\u6587\u672c")).description("\u652f\u6301\u989c\u8272\u4ee3\u7801: \u8f93\u5165 &6\u91d1\u8272 &c\u7ea2\u8272 &a\u7eff\u8272 &b\u6d45\u84dd &9\u84dd &d\u7c89 &e\u9ec4 &f\u767d &0\u9ed1 \u7b49\n& \u52a0\u4e00\u4e2a\u5b57\u6bcd = \u989c\u8272, &l=\u7c97\u4f53 &o=\u659c\u4f53 &n=\u4e0b\u5212\u7ebf &m=\u5220\u9664\u7ebf &k=\u6df7\u6dc6\n\u793a\u4f8b: &6&lQazr1234 &c&l\u795e\u5251")).defaultValue("&6&lQazr1234 &c&l\u6d4b\u8bd5\u5251")).build());
    private final Setting<List<String>> enchantments = this.sg.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u9644\u9b54\u5217\u8868")).description("\u6bcf\u884c\u4e00\u4e2a\uff0c\u683c\u5f0f: \u9644\u9b54ID:\u7b49\u7ea7 (\u9644\u9b54ID\u53ef\u7701\u7565 minecraft: \u524d\u7f00, \u7b49\u7ea7\u652f\u6301\u4efb\u610f64\u4f4d\u6574\u6570)")).defaultValue(List.of("minecraft:sharpness:10"))).build());
    private final Setting<List<String>> attributes = this.sg.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)new StringListSetting.Builder().name("\u5c5e\u6027\u5217\u8868")).description("\u6bcf\u884c\u4e00\u4e2a\uff0c\u683c\u5f0f: \u5c5e\u6027ID|\u503c|\u69fd\u4f4d\n\u5c5e\u6027ID\u53ef\u7701\u7565 minecraft: \u524d\u7f00\n\u69fd\u4f4d: MAINHAND, OFFHAND, HAND, HEAD, CHEST, LEGS, FEET, ARMOR, ANY")).defaultValue(List.of("generic.attack_damage|1000|MAINHAND"))).build());
    private final Setting<Boolean> continuous = this.sg.add(((BoolSetting.Builder)((BoolSetting.Builder)new BoolSetting.Builder().name("\u8fde\u7eed\u751f\u6210")).defaultValue(false)).build());

    public CreativeGiveModule() {
        super(MaceKillAddon.CATEGORY, "\u7269\u54c1\u751f\u6210\u5668", "\u521b\u9020\u6a21\u5f0f\u901a\u7528\u7269\u54c1\u751f\u6210 - \u7c7b\u578b\u5316\u7ec4\u4ef6\n\u9644\u9b54\u5217\u8868\u683c\u5f0f: \u9644\u9b54ID:\u7b49\u7ea7 (\u5982 minecraft:sharpness:10)\n\u5c5e\u6027\u5217\u8868\u683c\u5f0f: \u5c5e\u6027ID|\u503c|\u69fd\u4f4d (\u5982 generic.attack_damage|1000|MAINHAND)\n\u69fd\u4f4d: MAINHAND, OFFHAND, HAND, HEAD, CHEST, LEGS, FEET, ARMOR, ANY\n\u63d0\u793a: \u836f\u6c34\u8bf7\u4f7f\u7528\u300c\u836f\u6c34\u751f\u6210\u5668\u300d\u6a21\u5757");
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
        Preset p = (Preset)(this.preset.get());
        if (p != Preset.NONE) {
            this.genPreset(p);
            return;
        }
        Item item = this.resolveItem((String)this.itemId.get());
        if (item == Items.AIR) {
            CreativeGiveUtil.warn("\u672a\u627e\u5230\u7269\u54c1: " + (String)this.itemId.get());
            return;
        }
        ItemStack stack = new ItemStack((ItemConvertible)item, ((Integer)this.count.get()).intValue());
        if (((Boolean)this.enableName.get()).booleanValue()) {
            stack.set(DataComponentTypes.CUSTOM_NAME, this.parseName((String)this.customName.get()));
        }
        for (String entry : (List<String>)this.enchantments.get()) {
            this.applyEnchantFromString(stack, entry);
        }
        for (String entry : (List<String>)this.attributes.get()) {
            this.applyAttrFromString(stack, entry);
        }
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u5df2\u751f\u6210 " + stack.getCount() + "x " + (String)this.itemId.get());
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

    private void applyEnchantFromString(ItemStack stack, String entry) {
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
            Identifier enchId = Identifier.tryParse((String)(id.contains(":") ? id : "minecraft:" + id));
            if (enchId == null) {
                CreativeGiveUtil.warn("\u9644\u9b54ID\u9519\u8bef: " + id);
                return;
            }
            Optional ref = this.enchantmentRegistry().getEntry(enchId);
            if (ref.isEmpty()) {
                CreativeGiveUtil.warn("\u9644\u9b54\u672a\u627e\u5230: " + id);
                return;
            }
            int safeLevel = (int)Math.min(level, Integer.MAX_VALUE);
            ItemEnchantmentsComponent current = (ItemEnchantmentsComponent)stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
            ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(current);
            builder.add((RegistryEntry)ref.get(), safeLevel);
            stack.set(DataComponentTypes.ENCHANTMENTS, builder.build());
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u9644\u9b54\u5931\u8d25 [" + id + "]: " + e.getMessage());
        }
    }

    private void applyAttrFromString(ItemStack stack, String entry) {
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
            Identifier attrId = Identifier.tryParse((String)(id.contains(":") ? id : "minecraft:" + id));
            if (attrId == null) {
                CreativeGiveUtil.warn("\u5c5e\u6027ID\u9519\u8bef: " + id);
                return;
            }
            Optional ref = this.attributeRegistry().getEntry(attrId);
            if (ref.isEmpty()) {
                CreativeGiveUtil.warn("\u5c5e\u6027\u672a\u627e\u5230: " + id);
                return;
            }
            EntityAttributeModifier modifier = new EntityAttributeModifier(Identifier.of((String)"qazr1234", (String)("mod_" + System.nanoTime())), amount, EntityAttributeModifier.Operation.ADD_VALUE);
            AttributeModifierSlot slot = this.parseSlot(slotStr);
            AttributeModifiersComponent current = (AttributeModifiersComponent)stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            AttributeModifiersComponent updated = current.with((RegistryEntry)ref.get(), modifier, slot);
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, updated);
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u5c5e\u6027\u5931\u8d25 [" + id + "]: " + e.getMessage());
        }
    }

    private AttributeModifierSlot parseSlot(String s) {
        try {
            return AttributeModifierSlot.valueOf((String)s);
        }
        catch (Exception e) {
            CreativeGiveUtil.warn("\u672a\u77e5\u69fd\u4f4d: " + s + " (\u4f7f\u7528ANY)");
            return AttributeModifierSlot.ANY;
        }
    }

    private Registry<Enchantment> enchantmentRegistry() {
        return (Registry)this.mc.world.getRegistryManager().getOptional(RegistryKeys.ENCHANTMENT).orElseThrow();
    }

    private Registry<EntityAttribute> attributeRegistry() {
        return (Registry)this.mc.world.getRegistryManager().getOptional(RegistryKeys.ATTRIBUTE).orElseThrow();
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
        ItemStack stack = new ItemStack((ItemConvertible)Items.NETHERITE_SWORD, 1);
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal((String)"Qazr1234 \u4f24\u5bb3\u795e\u5251").formatted(new Formatting[]{Formatting.YELLOW, Formatting.BOLD}));
        Optional dmgAttr = this.attributeRegistry().getEntry(Identifier.of((String)"generic.attack_damage"));
        if (dmgAttr.isPresent()) {
            EntityAttributeModifier mod = new EntityAttributeModifier(Identifier.of((String)"qazr1234", (String)"dmg"), 137891.0, EntityAttributeModifier.Operation.ADD_VALUE);
            AttributeModifiersComponent current = (AttributeModifiersComponent)stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, current.with((RegistryEntry)dmgAttr.get(), mod, AttributeModifierSlot.MAINHAND));
        }
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u4f24\u5bb3\u795e\u5251(137891)\u5df2\u751f\u6210");
        }
    }

    private void genDamageArmor() {
        Item[] pieces = new Item[]{Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS};
        AttributeModifierSlot[] groups = new AttributeModifierSlot[]{AttributeModifierSlot.HEAD, AttributeModifierSlot.CHEST, AttributeModifierSlot.LEGS, AttributeModifierSlot.FEET};
        Optional armor = this.attributeRegistry().getEntry(Identifier.of((String)"generic.armor"));
        Optional toughness = this.attributeRegistry().getEntry(Identifier.of((String)"generic.armor_toughness"));
        Optional kb = this.attributeRegistry().getEntry(Identifier.of((String)"generic.knockback_resistance"));
        for (int i = 0; i < pieces.length; ++i) {
            ItemStack stack = new ItemStack((ItemConvertible)pieces[i], 1);
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal((String)"Qazr1234 \u4f24\u5bb3\u795e\u7532").formatted(new Formatting[]{Formatting.LIGHT_PURPLE, Formatting.BOLD}));
            AttributeModifiersComponent comp = (AttributeModifiersComponent)stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
            if (armor.isPresent()) {
                comp = comp.with((RegistryEntry)armor.get(), new EntityAttributeModifier(Identifier.of((String)"qazr1234", (String)("armor" + i)), 137891.0, EntityAttributeModifier.Operation.ADD_VALUE), groups[i]);
            }
            if (toughness.isPresent()) {
                comp = comp.with((RegistryEntry)toughness.get(), new EntityAttributeModifier(Identifier.of((String)"qazr1234", (String)("tough" + i)), 137891.0, EntityAttributeModifier.Operation.ADD_VALUE), groups[i]);
            }
            if (kb.isPresent()) {
                comp = comp.with((RegistryEntry)kb.get(), new EntityAttributeModifier(Identifier.of((String)"qazr1234", (String)("kb" + i)), 1.0, EntityAttributeModifier.Operation.ADD_VALUE), groups[i]);
            }
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, comp);
            CreativeGiveUtil.give(stack);
        }
        CreativeGiveUtil.info("\u4f24\u5bb3\u795e\u7532\u5168\u5957(137891)\u5df2\u751f\u6210");
    }

    private void genTotem() {
        ItemStack stack = new ItemStack((ItemConvertible)Items.TOTEM_OF_UNDYING, 1);
        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal((String)"Qazr1234 \u4e0d\u6b7b\u56fe\u817e").formatted(new Formatting[]{Formatting.GOLD, Formatting.BOLD}));
        if (CreativeGiveUtil.give(stack)) {
            CreativeGiveUtil.info("\u4e0d\u6b7b\u56fe\u817e\u5df2\u751f\u6210");
        }
    }

    private Item resolveItem(String id) {
        Object fullId = id.contains(":") ? id : "minecraft:" + id;
        Identifier identifier = Identifier.tryParse((String)fullId);
        return identifier != null ? (Item)Registries.ITEM.get(identifier) : Items.AIR;
    }

    public static enum Preset {
        NONE,
        DAMAGE_SWORD,
        DAMAGE_ARMOR,
        TOTEM;

    }
}

