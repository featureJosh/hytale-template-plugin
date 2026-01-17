package com.mod.nightmaremode;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.mod.nightmaremode.events.CombatListener;

public class NightmareMode extends JavaPlugin {

    private static NightmareMode instance;

    public NightmareMode(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    public void setup() {
        getEventRegistry().register(new CombatListener());
    }

    @Override
    public void start() {
        getLogger().info("Nightmare Mode enabled!");
    }

    @Override
    public void shutdown() {
        getLogger().info("Nightmare Mode disabled!");
    }

    public static NightmareMode getInstance() {
        return instance;
    }
}
