package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.RespawnSystems.OnRespawnSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import io.github.drakonforge.lastchance.component.LastChance;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ResetStateSystem extends OnRespawnSystem {
    // TODO: Separate into two possibly
    private static final Query<EntityStore> QUERY =
            LastChance.getComponentType();

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DeathComponent deathComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        LastChance lastChanceComponent = store.getComponent(ref, LastChance.getComponentType());
        assert lastChanceComponent != null;
        // TODO: Pull from config whether to reset progress
        lastChanceComponent.reset();
        commandBuffer.tryRemoveComponent(ref, DownedState.getComponentType());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
