package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.builtin.npccombatactionevaluator.CombatActionEvaluatorSystems;
import com.hypixel.hytale.builtin.npccombatactionevaluator.memory.TargetMemory;
import com.hypixel.hytale.builtin.npccombatactionevaluator.memory.TargetMemorySystems;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.config.AttitudeGroup;
import io.github.drakonforge.lastchance.component.DownedState;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DeaggroDownedTargetSystem extends EntityTickingSystem<EntityStore> {
    @Nonnull
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final Query<EntityStore> QUERY = TargetMemory.getComponentType();
    private static final Set<Dependency<EntityStore>> DEPENDENCIES =
            Set.of(new SystemDependency<>(Order.AFTER, TargetMemorySystems.Ticking.class), new SystemDependency<>(Order.BEFORE,
                    CombatActionEvaluatorSystems.EvaluatorTick.class));

    @Override
    public void tick(float deltaTime, int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        TargetMemory targetMemoryComponent = archetypeChunk.getComponent(index, TargetMemory.getComponentType());

        assert targetMemoryComponent != null;

        Int2FloatOpenHashMap hostileMap = targetMemoryComponent.getKnownHostiles();
        List<Ref<EntityStore>> hostileList = targetMemoryComponent.getKnownHostilesList();
        iterateMemory(deltaTime, index, archetypeChunk, commandBuffer, hostileList, hostileMap, "hostile");
        Int2FloatOpenHashMap friendlyMap = targetMemoryComponent.getKnownFriendlies();
        List<Ref<EntityStore>> friendlyList = targetMemoryComponent.getKnownFriendliesList();
        iterateMemory(deltaTime, index, archetypeChunk, commandBuffer, friendlyList, friendlyMap, "friendly");
        Ref<EntityStore> closestHostileRef = targetMemoryComponent.getClosestHostile();
        if (closestHostileRef != null && !isValidTarget(closestHostileRef, commandBuffer)) {
            targetMemoryComponent.setClosestHostile(null);
        }
    }

    private static void iterateMemory(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull List<Ref<EntityStore>> targetsList, @Nonnull Int2FloatOpenHashMap targetsMap, @Nonnull String type) {
        for(int i = targetsList.size() - 1; i >= 0; --i) {
            Ref<EntityStore> ref = targetsList.get(i);
            if (!isValidTarget(ref, commandBuffer)) {
                LOGGER.atInfo().log("Removing target due to secondary override");
                removeEntry(index, archetypeChunk, i, ref, targetsList, targetsMap, type);
            }
        }

    }

    private static boolean isValidTarget(@Nonnull Ref<EntityStore> targetRef, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        if (commandBuffer.getArchetype(targetRef).contains(DownedState.getComponentType())) {
            return false;
        }
        return true;
    }

    private static void removeEntry(int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, int targetIndex, @Nonnull Ref<EntityStore> targetRef, @Nonnull List<Ref<EntityStore>> targetsList, @Nonnull Int2FloatOpenHashMap targetsMap, @Nonnull String type) {
        targetsMap.remove(targetRef.getIndex());
        targetsList.remove(targetIndex);
        HytaleLogger.Api context = LOGGER.at(Level.FINEST);
        if (context.isEnabled()) {
            context.log("%s: Removed lost %s target %s", archetypeChunk.getReferenceTo(index), type, targetRef);
        }

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
