package io.github.drakonforge.lastchance.system.downedstate;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RemoveOnExpireSystem extends EntityTickingSystem<EntityStore> {

    private static final Query<EntityStore> QUERY = Query.and(DownedState.getComponentType());

    @Override
    public void tick(float deltaTime, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        DownedState downedState = archetypeChunk.getComponent(i, DownedState.getComponentType());
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        assert downedState != null;
        downedState.decrementTimeRemaining(deltaTime);

        if (downedState.shouldExpire()) {
            commandBuffer.removeComponent(ref, DownedState.getComponentType());
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
