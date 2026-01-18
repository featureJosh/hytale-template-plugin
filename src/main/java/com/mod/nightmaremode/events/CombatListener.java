package com.mod.nightmaremode.events;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.event.EventRegistry;

public class CombatListener {

    public void register(EventRegistry eventRegistry) {
        eventRegistry.register(PlayerConnectEvent.class, this::onPlayerConnect);
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            player.sendMessage(Message.raw(player.getDisplayName() + " connected!"));
        }
    }
}
