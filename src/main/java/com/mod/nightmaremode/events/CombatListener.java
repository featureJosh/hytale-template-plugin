package com.mod.nightmaremode.events;

import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.player.Player;
import com.hypixel.hytale.server.core.event.events.EntityDamageEvent;
import com.hypixel.hytale.event.EventHandler;

public class CombatListener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity attacker = event.getAttacker();
        Entity target = event.getTarget();

        if (attacker instanceof Player) {
            String targetName = target.getName();
            player.sendMessage(player.getName() + " hit " + targetName);
        }

        if (target instanceof Player) {
            Player player = (Player) target;
            String attackerName = attacker.getName();
            player.sendMessage(attackerName + " hit " + player.getName());
        }
    }
}
