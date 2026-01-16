package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class OverrideMovementStateSystem extends EntityTickingSystem<EntityStore> {
    private static final Query<EntityStore> QUERY = Query.and(MovementStatesComponent.getComponentType(), Player.getComponentType(), DownedState.getComponentType());
    private static final Set<Dependency<EntityStore>> DEPENDENCIES =
            Set.of(new SystemGroupDependency<>(Order.AFTER, EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP), new SystemDependency<>(Order.BEFORE, MovementStatesSystems.TickingSystem.class));

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
        // TODO: This doesn't seem to do anything
        Player playerComponent = archetypeChunk.getComponent(i, Player.getComponentType());
        assert movementStatesComponent != null;
        MovementStates newMovementStates = movementStatesComponent.getMovementStates();
        newMovementStates.walking = false;
        newMovementStates.crouching = true;
        newMovementStates.sprinting = false;
        newMovementStates.jumping = false;
        movementStatesComponent.setMovementStates(newMovementStates);
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
