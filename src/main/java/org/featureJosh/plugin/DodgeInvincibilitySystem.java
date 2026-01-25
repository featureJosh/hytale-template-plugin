package org.featureJosh.plugin;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.packets.world.SpawnParticleSystem;
import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
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

        if (elapsed > SoulsDodgeSettings.get().invincibility) return;

        event.setCancelled(true);
        event.setAmount(0.0F);
        event.putMetaObject(Damage.KNOCKBACK_COMPONENT, null);

        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        commandBuffer.tryRemoveComponent(ref, KnockbackComponent.getComponentType());

        TransformComponent transform = chunk.getComponent(index, TransformComponent.getComponentType());
        if (transform != null) {
            Vector3d pos = transform.getPosition();
            Position particlePos = new Position(pos.x, pos.y + 1.5, pos.z);
            SpawnParticleSystem packet = new SpawnParticleSystem(
                "Daggers_Dash_Straight",
                particlePos,
                new Direction(0.0f, 0.0f, 0.0f),
                2.0f,
                new Color((byte)255, (byte)100, (byte)0)
            );
            PlayerUtil.forEachPlayerThatCanSeeEntity(ref, (entityRef, playerRef, accessor) -> {
                playerRef.getPacketHandler().write(packet);
            }, commandBuffer);
        }
    }
}
