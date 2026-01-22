package io.github.drakonforge.lastchance.system.downedstate;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.WieldingInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ManageParryStateSystem extends EntityTickingSystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Query<EntityStore> QUERY = Query.and(DownedState.getComponentType(),
            DamageDataComponent.getComponentType(), EffectControllerComponent.getComponentType());
    private static final long MAX_PARRY_TIME_MS = 750;
    private static final float PARRY_COOLDOWN = 1.0f;

    @Override
    public void tick(float deltaTime, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        DownedState downedState = archetypeChunk.getComponent(i, DownedState.getComponentType());
        DamageDataComponent damageDataComponent = archetypeChunk.getComponent(i, DamageDataComponent.getComponentType());
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        assert downedState != null;
        assert damageDataComponent != null;

        WieldingInteraction interaction = damageDataComponent.getCurrentWielding();
        if (interaction == null) {
            if (downedState.isParrying()) {
                downedState.setParryCooldown(PARRY_COOLDOWN);
            }
            downedState.resetLastParryStart();
        } else {
            if (!downedState.isParrying()) {
                Instant current = Instant.now();
                downedState.setLastParryStart(current);
            } else {
                Instant lastParryStart = downedState.getLastParryStart();
                if (lastParryStart != null && ChronoUnit.MILLIS.between(lastParryStart, Instant.now()) > MAX_PARRY_TIME_MS) {
                    downedState.setParryCooldown(PARRY_COOLDOWN);
                    downedState.resetLastParryStart();
                }
            }
        }

        // Control whether entity can parry
        EffectControllerComponent effectControllerComponent = archetypeChunk.getComponent(i, EffectControllerComponent.getComponentType());
        assert effectControllerComponent != null;
        int effectIndex = EntityEffect.getAssetMap().getIndex("Stamina_Broken");
        EntityEffect entityEffect = EntityEffect.getAssetMap().getAsset(effectIndex);
        if (downedState.canParry()) {
            effectControllerComponent.removeEffect(ref, effectIndex, store);
        } else {
            downedState.decrementParryCooldown(deltaTime);
            if (entityEffect != null) {
                effectControllerComponent.addEffect(ref, effectIndex, entityEffect, store);
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
