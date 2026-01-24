package org.featureJosh.plugin;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class PlayerJoinDodgeAdder extends RefSystem<EntityStore> {
   private final ComponentType<EntityStore, DodgeComponent> dodgeComponentType;

   public PlayerJoinDodgeAdder(ComponentType<EntityStore, DodgeComponent> dodgeComponentType) {
      this.dodgeComponentType = dodgeComponentType;
   }

   @Nonnull
   public Query<EntityStore> getQuery() {
      return PlayerRef.getComponentType();
   }

   public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
      if (commandBuffer.getComponent(ref, this.dodgeComponentType) == null) {
         commandBuffer.addComponent(ref, this.dodgeComponentType, new DodgeComponent());
      }

   }

   public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
   }
}
