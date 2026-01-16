package io.github.drakonforge.lastchance.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.LastChancePlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LastChance implements Component<EntityStore> {

    private int maxChances;
    private int chancesRemaining;
    private float regenerationProgress;
    private float downedStateCooldown;

    public static ComponentType<EntityStore, LastChance> getComponentType() {
        return LastChancePlugin.getInstance().getLastChanceComponentType();
    }

    public LastChance() {
        this(1, 1, 0.0f, 0.0f);
    }

    public LastChance(int maxChances, int chancesRemaining, float regenerationProgress, float downedStateCooldown) {
        this.maxChances = maxChances;
        this.chancesRemaining = chancesRemaining;
        this.regenerationProgress = regenerationProgress;
        this.downedStateCooldown = downedStateCooldown;
    }

    public void addRegenerationProgress(float deltaTime) {
        this.regenerationProgress += deltaTime;
    }

    public void resetRegenerationProgress() {
        this.regenerationProgress = 0.0f;
    }

    // Only decrement after leaving the downed state
    public void decrementDownedStateCooldown(float deltaTime) {
        this.downedStateCooldown = Math.max(this.downedStateCooldown - deltaTime, 0.0f);
    }

    public void setChancesRemaining(int chancesRemaining) {
        this.chancesRemaining = Math.clamp(chancesRemaining, 0, this.maxChances);
    }

    public void setDownedStateCooldown(float downedStateCooldown) {
        this.downedStateCooldown = downedStateCooldown;
    }

    public boolean canEnterDownedState() {
        return chancesRemaining > 0 && downedStateCooldown <= 0.0f;
    }

    public boolean shouldRegenerateChances() {
        return chancesRemaining < maxChances;
    }

    public void reset() {
        this.chancesRemaining = this.maxChances;
        this.regenerationProgress = 0.0f;
        this.downedStateCooldown = 0.0f;
    }

    public int getMaxChances() {
        return maxChances;
    }

    public int getChancesRemaining() {
        return chancesRemaining;
    }

    public float getRegenerationProgress() {
        return regenerationProgress;
    }

    public float getDownedStateCooldown() {
        return downedStateCooldown;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        LastChance clone = new LastChance();
        clone.maxChances = this.maxChances;
        clone.chancesRemaining = this.chancesRemaining;
        clone.regenerationProgress = this.regenerationProgress;
        clone.downedStateCooldown = this.downedStateCooldown;
        return clone;
    }
}
