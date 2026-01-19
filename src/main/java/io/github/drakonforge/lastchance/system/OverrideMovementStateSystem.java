package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class OverrideMovementStateSystem extends EntityTickingSystem<EntityStore> {
    private static final Query<EntityStore> QUERY = Query.and(MovementStatesComponent.getComponentType(), PlayerInput.getComponentType(), DownedState.getComponentType());
    private static final Set<Dependency<EntityStore>> DEPENDENCIES =
            Set.of(new SystemDependency<>(Order.BEFORE, PlayerSystems.ProcessPlayerInput.class));

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        PlayerInput playerInputComponent = archetypeChunk.getComponent(i, PlayerInput.getComponentType());
        assert playerInputComponent != null;
        playerInputComponent.getMovementUpdateQueue().clear();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return DEPENDENCIES;
    }
}
