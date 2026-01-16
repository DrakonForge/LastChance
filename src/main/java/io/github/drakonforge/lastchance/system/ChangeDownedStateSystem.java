package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.ClientCameraView;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.DownedState;
import io.github.drakonforge.lastchance.component.LastChance;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;


public class ChangeDownedStateSystem extends RefChangeSystem<EntityStore, DownedState> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final Query<EntityStore> QUERY = Query.and(EntityStatMap.getComponentType(),
            LastChance.getComponentType());

    @NonNullDecl
    @Override
    public ComponentType<EntityStore, DownedState> componentType() {
        return DownedState.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DownedState downedState, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        EntityStatMap entityStatMapComponent = store.getComponent(ref, EntityStatMap.getComponentType());
        assert entityStatMapComponent != null;
        LOGGER.atInfo().log("Entering downed state");
        // TODO: Pull from config
        entityStatMapComponent.setStatValue(DefaultEntityStatTypes.getHealth(), 10);

        // If player, set the camera
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null && playerRef.isValid()) {
            ServerCameraSettings settings = new ServerCameraSettings();
            settings.distance = 5.0f;
            settings.isFirstPerson = false;
            settings.positionLerpSpeed = 0.2f;
            settings.displayCursor = false;
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, true, settings)
            );
        }
    }

    @Override
    public void onComponentSet(@NonNullDecl Ref<EntityStore> ref,
            @NullableDecl DownedState downedState, @NonNullDecl DownedState t1,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }

    @Override
    public void onComponentRemoved(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DownedState downedState, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        LastChance lastChanceComponent = store.getComponent(ref, LastChance.getComponentType());
        assert lastChanceComponent != null;
        // TODO: Pull from config
        lastChanceComponent.setDownedStateCooldown(3.0f);

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null && playerRef.isValid()) {
            // Reset camera
            playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, false,
                    null));
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
