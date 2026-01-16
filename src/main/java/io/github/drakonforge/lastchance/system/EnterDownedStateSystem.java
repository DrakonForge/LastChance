package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
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
import io.github.drakonforge.lastchance.component.LastChance;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EnterDownedStateSystem extends DamageEventSystem implements EntityStatsSystems.StatModifyingSystem  {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Query<EntityStore> QUERY = Query.and(EntityStatMap.getComponentType(),
            LastChance.getComponentType());
    private static final Set<Dependency<EntityStore>> DEPENDENCIES =
            Set.of(new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()), new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getFilterDamageGroup()), new SystemDependency<>(Order.BEFORE, ApplyDamage.class));

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        LOGGER.atInfo().log("Running");
        boolean isDead = archetypeChunk.getArchetype().contains(DeathComponent.getComponentType());

        LOGGER.atInfo().log("A");
        if (isDead || !canDamageTriggerDownedState(damage)) {
            return;
        }

        LOGGER.atInfo().log("B");
        int healthStat = DefaultEntityStatTypes.getHealth();
        EntityStatMap entityStatMapComponent = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        assert entityStatMapComponent != null;
        EntityStatValue healthValue = entityStatMapComponent.get(healthStat);
        Objects.requireNonNull(healthValue);

        LOGGER.atInfo().log("C");
        if (damage.getAmount() < healthValue.get()) {
            return;

        }

        LOGGER.atInfo().log("D");
        LastChance lastChanceComponent = archetypeChunk.getComponent(i, LastChance.getComponentType());
        assert lastChanceComponent != null;
        if (!lastChanceComponent.hasChancesRemaining()) {
            return;
        }

        LOGGER.atInfo().log("E");
        damage.setCancelled(true);
        // TODO: Pull from mod config
        entityStatMapComponent.setStatValue(healthStat, 10);
        LOGGER.atInfo().log("Last chance used!");
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
