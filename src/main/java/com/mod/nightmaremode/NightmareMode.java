package com.mod.nightmaremode;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.mod.nightmaremode.events.CombatListener;
import java.util.logging.Level;

public class NightmareMode extends JavaPlugin {

    private static NightmareMode instance;

    public NightmareMode(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    public void setup() {
        CombatListener combatListener = new CombatListener();
        combatListener.register(getEventRegistry());
    }

    @Override
    public void start() {
        getLogger().at(Level.INFO).log("Nightmare Mode enabled!");
    }

    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("Nightmare Mode disabled!");
    }

    public static NightmareMode getInstance() {
        return instance;
    }
}