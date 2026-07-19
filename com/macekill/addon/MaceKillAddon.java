package com.macekill.addon;

import com.macekill.addon.modules.AutoFuckModule;
import com.macekill.addon.modules.AutoGGModule;
import com.macekill.addon.modules.AutoMineModule;
import com.macekill.addon.modules.CreativeGiveModule;
import com.macekill.addon.modules.CustomPotionModule;
import com.macekill.addon.modules.MaceDMG;
import com.macekill.addon.modules.MaceKillModule;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaceKillAddon
extends MeteorAddon {
    public static final Logger LOG = LoggerFactory.getLogger("Qazr-Addons");
    public static final Category CATEGORY = new Category("Qazr1234");

    public void onInitialize() {
        LOG.info("Qazr Addons initializing...");
        Modules.get().add((Module)new AutoFuckModule());
        Modules.get().add((Module)new AutoGGModule());
        Modules.get().add((Module)new MaceKillModule());
        Modules.get().add((Module)new MaceDMG());
        Modules.get().add((Module)new AutoMineModule());
        Modules.get().add((Module)new CreativeGiveModule());
        Modules.get().add((Module)new CustomPotionModule());
        LOG.info("Qazr Addons initialized successfully!");
    }

    public void onRegisterCategories() {
        Modules.registerCategory((Category)CATEGORY);
    }

    public String getPackage() {
        return "com.macekill.addon";
    }
}
