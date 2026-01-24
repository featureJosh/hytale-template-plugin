package org.featureJosh.plugin;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class DodgePacketHandler implements PlayerPacketFilter {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

   public boolean test(@Nonnull PlayerRef playerRef, @Nonnull Packet packet) {
      if (packet instanceof SyncInteractionChains) {
         SyncInteractionChains syncPacket = (SyncInteractionChains)packet;
         SyncInteractionChain[] var4 = syncPacket.updates;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            SyncInteractionChain chain = var4[var6];
            boolean isAbility2 = chain.interactionType == InteractionType.Ability2;
            if (isAbility2 && chain.initial) {
               this.handleDodgeTrigger(playerRef);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void handleDodgeTrigger(PlayerRef playerRef) {
      Ref<EntityStore> entityRef = playerRef.getReference();
      if (entityRef != null && entityRef.isValid()) {
         Store<EntityStore> store = entityRef.getStore();
         World world = ((EntityStore)store.getExternalData()).getWorld();
         world.execute(() -> {
            DodgeComponent dodge = (DodgeComponent)store.getComponent(entityRef, SoulsDodge.getDodgeComponentType());
            if (dodge != null) {
               dodge.setQueuedDodge(true);
            }

         });
      }
   }
}
