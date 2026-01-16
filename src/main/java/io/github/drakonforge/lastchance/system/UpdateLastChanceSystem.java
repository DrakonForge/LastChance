package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.LastChance;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateLastChanceSystem extends EntityTickingSystem<EntityStore> {
    private static final float REGENERATE_SECONDS = 60; // TODO: Pull from config
    private static final Query<EntityStore> QUERY = LastChance.getComponentType();

    @Override
    public void tick(float deltaTime, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        LastChance lastChanceComponent = archetypeChunk.getComponent(i, LastChance.getComponentType());
        assert lastChanceComponent != null;
        if (lastChanceComponent.shouldRegenerateChances()) {
            lastChanceComponent.addRegenerationProgress(deltaTime);
            if (lastChanceComponent.getRegenerationProgress() >= REGENERATE_SECONDS) {
                lastChanceComponent.addRegenerationProgress(-REGENERATE_SECONDS);
                lastChanceComponent.setChancesRemaining(lastChanceComponent.getChancesRemaining() + 1);
            }
        } else if (lastChanceComponent.getRegenerationProgress() > 0) {
            lastChanceComponent.resetRegenerationProgress();
        }
        if (lastChanceComponent.getDownedStateCooldown() > 0) {
            lastChanceComponent.decrementDownedStateCooldown(deltaTime);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
