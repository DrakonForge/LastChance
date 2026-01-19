package io.github.drakonforge.lastchance.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.LastChancePlugin;
import java.time.Instant;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DownedState implements Component<EntityStore> {

    private float timeRemaining;
    private float invulnerableTimeRemaining; // TODO
    private Instant lastParryStart = null;
    private float parryCooldown = 0;

    public static ComponentType<EntityStore, DownedState> getComponentType() {
        return LastChancePlugin.getInstance().getDownedStateComponentType();
    }

    public DownedState() {
        this(0.0f);
    }

    public DownedState(float timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public void decrementTimeRemaining(float deltaTime) {
        this.timeRemaining = Math.max(this.timeRemaining - deltaTime, 0.0f);
    }

    public void decrementParryCooldown(float deltaTime) {
        this.parryCooldown = Math.max(this.parryCooldown - deltaTime, 0.0f);
    }

    public void setLastParryStart(Instant lastParryStart) {
        this.lastParryStart = lastParryStart;
    }

    public void setParryCooldown(float timeRemaining) {
        this.parryCooldown = timeRemaining;
    }

    public void resetLastParryStart() {
        this.lastParryStart = null;
    }

    public boolean isParrying() {
        return this.lastParryStart != null;
    }

    public boolean canParry() {
        return this.parryCooldown <= 0;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    @Nullable
    public Instant getLastParryStart() {
        return lastParryStart;
    }

    public boolean shouldExpire() {
        return timeRemaining <= 0.0f;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        DownedState clone = new DownedState();
        clone.timeRemaining = this.timeRemaining;
        return clone;
    }
}
