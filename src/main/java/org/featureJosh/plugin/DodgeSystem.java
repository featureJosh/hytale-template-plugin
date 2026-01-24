package org.featureJosh.plugin;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.packets.entities.PlayAnimation;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.stamina.SprintStaminaRegenDelay;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.PlayerUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class DodgeSystem extends EntityTickingSystem<EntityStore> {
    private static final int STAMINA_INDEX = 9;
    private final ComponentType<EntityStore, DodgeComponent> dodgeComponentType;
    private final Vector3d tempVelocity = new Vector3d();

    public DodgeSystem(ComponentType<EntityStore, DodgeComponent> dodgeComponentType) {
        this.dodgeComponentType = dodgeComponentType;
    }

    @Nonnull
    public Query<EntityStore> getQuery() {
        return this.dodgeComponentType;
    }

    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        DodgeComponent dodge = chunk.getComponent(index, this.dodgeComponentType);
        if (dodge == null) return;

        DodgeConfig config = DodgeConfig.get();
        TimeResource timeResource = store.getResource(TimeResource.getResourceType());
        long nowMs = timeResource.getNow().toEpochMilli();

        if (dodge.isDodging) {
            long elapsed = nowMs - dodge.dodgeStartTimeMs;
            if (elapsed > config.iFrameDurationMs) {
                dodge.isDodging = false;
                removeEffect(chunk.getReferenceTo(index), store, commandBuffer);
            } else {
                enforceVelocity(index, chunk, dodge);
            }
        }

        if (!dodge.isQueuedDodge()) return;
        dodge.setQueuedDodge(false);

        if (nowMs - dodge.lastDodgeTimeMs < config.dodgeCooldownMs) return;

        if (!config.allowAirDash) {
            MovementStatesComponent stateComp = chunk.getComponent(index, MovementStatesComponent.getComponentType());
            if (stateComp != null) {
                MovementStates states = stateComp.getMovementStates();
                if (!states.onGround) return;
            }
        }

        EntityStatMap statMap = chunk.getComponent(index, EntityStatMap.getComponentType());
        if (statMap != null) {
            EntityStatValue staminaStat = statMap.get(STAMINA_INDEX);
            if (staminaStat != null) {
                float currentStamina = staminaStat.get();
                if (currentStamina < config.staminaCost) return;

                statMap.setStatValue(STAMINA_INDEX, currentStamina - config.staminaCost);
                SprintStaminaRegenDelay delayConfig = store.getResource(SprintStaminaRegenDelay.getResourceType());
                if (delayConfig.hasDelay()) {
                    statMap.setStatValue(delayConfig.getIndex(), delayConfig.getValue());
                }
            }
        }

        dodge.lastDodgeTimeMs = nowMs;
        dodge.dodgeStartTimeMs = nowMs;
        dodge.isDodging = true;

        performDodge(index, chunk, config, dodge, commandBuffer, store);
    }

    private void enforceVelocity(int index, ArchetypeChunk<EntityStore> chunk, DodgeComponent dodge) {
        Velocity velocityComp = chunk.getComponent(index, Velocity.getComponentType());
        if (velocityComp == null) return;

        Vector3d currentVel = velocityComp.getClientVelocity();

        tempVelocity.x = dodge.dodgeVelocityX;
        tempVelocity.y = currentVel.y;
        tempVelocity.z = dodge.dodgeVelocityZ;

        velocityComp.getInstructions().clear();
        velocityComp.addInstruction(tempVelocity, (VelocityConfig) null, ChangeVelocityType.Set);

        TransformComponent transformComp = chunk.getComponent(index, TransformComponent.getComponentType());
        if (transformComp != null) {
            Vector3f currentRot = transformComp.getRotation();
            transformComp.setRotation(new Vector3f(currentRot.x, dodge.dodgeYaw, currentRot.z));
        }
    }

    private void performDodge(int index, ArchetypeChunk<EntityStore> chunk, DodgeConfig config, DodgeComponent dodge, CommandBuffer<EntityStore> commandBuffer, Store<EntityStore> store) {
        Velocity velocityComp = chunk.getComponent(index, Velocity.getComponentType());
        TransformComponent transformComp = chunk.getComponent(index, TransformComponent.getComponentType());
        if (velocityComp == null || transformComp == null) return;

        Vector3d currentVel = velocityComp.getClientVelocity();
        Vector3f rotation = transformComp.getRotation();

        double dirX, dirZ;
        double speedSq = currentVel.x * currentVel.x + currentVel.z * currentVel.z;
        if (speedSq > 0.01) {
            double invSpeed = 1.0 / Math.sqrt(speedSq);
            dirX = currentVel.x * invSpeed;
            dirZ = currentVel.z * invSpeed;
        } else {
            dirX = Math.sin(rotation.y);
            dirZ = Math.cos(rotation.y);
        }

        double velX = dirX * config.dodgeVelocity;
        double velY = config.verticalHop;
        double velZ = dirZ * config.dodgeVelocity;

        dodge.setDodgeVelocity(velX, velY, velZ);

        float dodgeYaw = (float) Math.atan2(dirX, dirZ);
        dodge.dodgeYaw = dodgeYaw;

        transformComp.setRotation(new Vector3f(rotation.x, dodgeYaw, rotation.z));

        tempVelocity.x = velX;
        tempVelocity.y = velY;
        tempVelocity.z = velZ;

        velocityComp.getInstructions().clear();
        velocityComp.addInstruction(tempVelocity, (VelocityConfig) null, ChangeVelocityType.Set);

        broadcastAnimation(index, chunk, commandBuffer);

        Ref<EntityStore> entityRef = chunk.getReferenceTo(index);
        applyEffect(entityRef, store, commandBuffer, config.iFrameDurationMs / 1000.0f);
    }

    private void broadcastAnimation(int index, ArchetypeChunk<EntityStore> chunk, CommandBuffer<EntityStore> commandBuffer) {
        NetworkId netId = chunk.getComponent(index, NetworkId.getComponentType());
        if (netId == null) return;

        PlayAnimation packet = new PlayAnimation(netId.getId(), null, "Roll", AnimationSlot.Action);
        PlayerUtil.forEachPlayerThatCanSeeEntity(chunk.getReferenceTo(index), (ref, playerRef, accessor) -> {
            playerRef.getPacketHandler().write(packet);
        }, commandBuffer);
    }

    private static void applyEffect(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer, float duration) {
        EffectControllerComponent effectController = store.getComponent(entityRef, EffectControllerComponent.getComponentType());
        if (effectController == null) return;

        EntityEffect dodgeEffect = EntityEffect.getAssetMap().getAsset("PP_Dodge_Effect");
        if (dodgeEffect != null) {
            effectController.addEffect(entityRef, dodgeEffect, duration, OverlapBehavior.OVERWRITE, commandBuffer);
        }
    }

    private static void removeEffect(Ref<EntityStore> entityRef, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        EffectControllerComponent effectController = store.getComponent(entityRef, EffectControllerComponent.getComponentType());
        if (effectController == null) return;

        int effectIndex = EntityEffect.getAssetMap().getIndex("PP_Dodge_Effect");
        if (effectIndex != Integer.MIN_VALUE) {
            effectController.removeEffect(entityRef, effectIndex, commandBuffer);
        }
    }
}
