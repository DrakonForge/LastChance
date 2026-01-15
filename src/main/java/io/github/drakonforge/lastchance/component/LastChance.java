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

    public static ComponentType<EntityStore, LastChance> getComponentType() {
        return LastChancePlugin.getInstance().getLastChanceComponentType();
    }

    public LastChance() {
        this(1, 1, 0.0f);
    }

    public LastChance(int maxChances, int chancesRemaining, float regenerationProgress) {
        this.maxChances = maxChances;
        this.chancesRemaining = chancesRemaining;
        this.regenerationProgress = regenerationProgress;
    }

    public void addRegenerationProgress(float deltaTime) {
        this.regenerationProgress += deltaTime;
    }

    public void setChancesRemaining(int chancesRemaining) {
        this.chancesRemaining = chancesRemaining;
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

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        LastChance clone = new LastChance();
        clone.maxChances = this.maxChances;
        clone.chancesRemaining = this.chancesRemaining;
        clone.regenerationProgress = this.regenerationProgress;
        return clone;
    }
}
