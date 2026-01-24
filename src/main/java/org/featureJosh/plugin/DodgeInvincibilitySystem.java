package org.featureJosh.plugin;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class DodgeInvincibilitySystem extends DamageEventSystem {
    private final ComponentType<EntityStore, DodgeComponent> dodgeComponentType;

    public DodgeInvincibilitySystem(ComponentType<EntityStore, DodgeComponent> dodgeComponentType) {
        this.dodgeComponentType = dodgeComponentType;
    }

    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getFilterDamageGroup();
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.dodgeComponentType;
    }

    public void handle(int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Damage event) {
        DodgeComponent dodge = chunk.getComponent(index, this.dodgeComponentType);
        if (dodge == null || !dodge.isDodging) return;

        TimeResource timeResource = store.getResource(TimeResource.getResourceType());
        long nowMs = timeResource.getNow().toEpochMilli();
        long elapsed = nowMs - dodge.dodgeStartTimeMs;

        if (elapsed > DodgeConfig.get().iFrameDurationMs) return;

        event.setCancelled(true);
        event.setAmount(0.0F);
        event.putMetaObject(Damage.KNOCKBACK_COMPONENT, null);

        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());
    }
}
