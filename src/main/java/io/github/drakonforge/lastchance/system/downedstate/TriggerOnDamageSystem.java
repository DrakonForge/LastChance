package io.github.drakonforge.lastchance.system.downedstate;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems.ApplyDamage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsSystems;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.LastChancePlugin;
import io.github.drakonforge.lastchance.component.DownedState;
import io.github.drakonforge.lastchance.component.LastChance;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TriggerOnDamageSystem extends DamageEventSystem implements EntityStatsSystems.StatModifyingSystem  {
    private static final Query<EntityStore> QUERY = Query.and(EntityStatMap.getComponentType(),
            LastChance.getComponentType(), Query.not(DownedState.getComponentType()));
    private static final Set<Dependency<EntityStore>> DEPENDENCIES =
            Set.of(new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()), new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getFilterDamageGroup()), new SystemDependency<>(Order.BEFORE, ApplyDamage.class));

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        boolean isDead = archetypeChunk.getArchetype().contains(DeathComponent.getComponentType());

        if (isDead || !canDamageTriggerDownedState(damage)) {
            return;
        }

        EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        assert entityStatMapComponent != null;
        EntityStatValue healthValue = entityStatMapComponent.get(DefaultEntityStatTypes.getHealth());
        Objects.requireNonNull(healthValue);

        if (damage.getAmount() < healthValue.get()) {
            return;
        }

        LastChance lastChanceComponent = archetypeChunk.getComponent(i, LastChance.getComponentType());
        assert lastChanceComponent != null;
        if (!lastChanceComponent.canEnterDownedState()) {
            return;
        }

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        damage.setAmount(0);
        // TODO: Pull from mod config
        commandBuffer.addComponent(ref, LastChancePlugin.getInstance().getDownedStateComponentType(), new DownedState(5.0f));
    }

    private boolean canDamageTriggerDownedState(Damage damage) {
        // TODO: Pull from mod config (?)
        return damage.getSource() instanceof Damage.EntitySource;
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
