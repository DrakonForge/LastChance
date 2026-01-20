package io.github.drakonforge.lastchance.system.downedstate;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems.ApplyDamage;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ParrySuccessSystem extends DamageEventSystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Query<EntityStore> QUERY = Query.and(DownedState.getComponentType(), EntityStatMap.getComponentType(), TransformComponent.getComponentType());
    private static final Set<Dependency<EntityStore>> DEPENDENCIES =
            Set.of(new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()), new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getFilterDamageGroup()), new SystemDependency<>(Order.BEFORE, ApplyDamage.class));
    private static final float HEALTH_REGAIN = 40; // TODO: Pull from config

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return DamageModule.get().getInspectDamageGroup();
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        boolean wasBlocked = damage.getMetaObject(Damage.BLOCKED);
        if (wasBlocked) {
            triggerParrySuccess(i, archetypeChunk, store, commandBuffer, damage);

        }
    }

    private void triggerParrySuccess(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        assert entityStatMapComponent != null;
        entityStatMapComponent.addStatValue(DefaultEntityStatTypes.getHealth(), HEALTH_REGAIN);
        entityStatMapComponent.maximizeStatValue(DefaultEntityStatTypes.getStamina());
        commandBuffer.removeComponent(archetypeChunk.getReferenceTo(i), DownedState.getComponentType());

        // Play sound
        // TODO: Change sound
        int index = SoundEvent.getAssetMap().getIndex("SFX_Light_Melee_T2_Guard_Hit");
        TransformComponent transformComponent = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        assert transformComponent != null;
        Vector3d pos = transformComponent.getPosition();
        SoundUtil.playSoundEvent3d(index, SoundCategory.SFX, pos.getX(), pos.getY(), pos.getZ(), 3.0f, 1.2f, store);

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
