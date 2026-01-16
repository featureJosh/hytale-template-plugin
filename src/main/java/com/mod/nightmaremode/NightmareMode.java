package com.mod.nightmaremode;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.player.Player;
import com.hypixel.hytale.server.core.event.events.EntitySpawnEvent;
import com.hypixel.hytale.event.SubscribeEvent;

public class NightmareMode extends JavaPlugin {

    private static NightmareMode instance;

    public NightmareMode(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    public void setup() {
        getEventRegistry().register(this);
    }

    @Override
    public void start() {
        getLogger().info("Nightmare Mode enabled!");
    }

    @Override
    public void shutdown() {
        getLogger().info("Nightmare Mode disabled!");
    }

    @SubscribeEvent
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            double healthMultiplier = 4;
            double baseHealth = entity.getMaxHealth();
            entity.setMaxHealth(baseHealth * healthMultiplier);
            entity.setHealth(entity.getMaxHealth());
        }
    }

    public static NightmareMode getInstance() {
        return instance;
    }
}
