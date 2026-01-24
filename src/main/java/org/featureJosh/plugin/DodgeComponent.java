package org.featureJosh.plugin;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class DodgeComponent implements Component<EntityStore> {
    long lastDodgeTimeMs;
    long dodgeStartTimeMs;
    boolean isDodging;
    private boolean queuedDodge;
    double dodgeVelocityX;
    double dodgeVelocityY;
    double dodgeVelocityZ;
    float dodgeYaw;

    public boolean isQueuedDodge() {
        return this.queuedDodge;
    }

    public void setQueuedDodge(boolean queued) {
        this.queuedDodge = queued;
    }

    public void setDodgeVelocity(double x, double y, double z) {
        this.dodgeVelocityX = x;
        this.dodgeVelocityY = y;
        this.dodgeVelocityZ = z;
    }

    @Nonnull
    public DodgeComponent clone() {
        DodgeComponent c = new DodgeComponent();
        c.lastDodgeTimeMs = this.lastDodgeTimeMs;
        c.dodgeStartTimeMs = this.dodgeStartTimeMs;
        c.isDodging = this.isDodging;
        c.queuedDodge = this.queuedDodge;
        c.dodgeVelocityX = this.dodgeVelocityX;
        c.dodgeVelocityY = this.dodgeVelocityY;
        c.dodgeVelocityZ = this.dodgeVelocityZ;
        c.dodgeYaw = this.dodgeYaw;
        return c;
    }
}
