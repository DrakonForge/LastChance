package io.github.drakonforge.lastchance.system;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.ApplyLookType;
import com.hypixel.hytale.protocol.ApplyMovementType;
import com.hypixel.hytale.protocol.AttachedToType;
import com.hypixel.hytale.protocol.CanMoveType;
import com.hypixel.hytale.protocol.ClientCameraView;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.MouseInputTargetType;
import com.hypixel.hytale.protocol.MouseInputType;
import com.hypixel.hytale.protocol.MovementForceRotationType;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.protocol.PositionDistanceOffsetType;
import com.hypixel.hytale.protocol.PositionType;
import com.hypixel.hytale.protocol.RotationType;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.Vector2f;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
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
            LastChance.getComponentType(), MovementManager.getComponentType(), PlayerRef.getComponentType());

    private static ServerCameraSettings createOrbitingCamera(Vector3f startingDirection) {
        ServerCameraSettings settings = new ServerCameraSettings();

        settings.rotationLerpSpeed = 0.5f;
        settings.isFirstPerson = false; // Show the player instead of hiding them
        settings.applyMovementType = ApplyMovementType.Position; // Prevent player from moving with character controller

        // Distance offset
        settings.distance = 4.0f; // Offset the camera a short distance away
        settings.eyeOffset = true; // Position the camera from the player's eye position
        settings.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffsetRaycast; // Prevents clipping through walls
        settings.position = new Position(10.0f, -10.0f, 0.0f);

        // Make camera orbit around the player
        settings.applyLookType = ApplyLookType.Rotation; // Prevent player from rotating to look direction
        settings.movementForceRotationType = MovementForceRotationType.CameraRotation; // Use camera rotation instead of following player look
        settings.rotationType = RotationType.Custom; // Allows rotation without player turning
        // Set starting rotation
        settings.rotation = new Direction(
                startingDirection.getYaw(),
                startingDirection.getPitch(),
                startingDirection.getRoll()
        );
        settings.lookMultiplier = new Vector2f(0.9f, 0.9f); // Set camera orbiting speed

        return settings;
    }

    @NonNullDecl
    @Override
    public ComponentType<EntityStore, DownedState> componentType() {
        return DownedState.getComponentType();
    }

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DownedState downedState, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        // TODO: May need to split out the player-specific parts later (PlayerRef, MovementManager)
        EntityStatMap entityStatMapComponent = store.getComponent(ref, EntityStatMap.getComponentType());
        assert entityStatMapComponent != null;
        LOGGER.atInfo().log("Entering downed state");
        // TODO: Pull from config
        entityStatMapComponent.setStatValue(DefaultEntityStatTypes.getHealth(), 50); // TODO: Temp higher value for testing

        // If player, set the camera
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        assert playerRef != null;

        if (playerRef.isValid()) {
            // Update movement
            // Not sure if this is actually needed since ApplyMovementType.Position seems to do the trick
            MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
            assert movementManager != null;
            movementManager.getSettings().baseSpeed = 0.0f;
            movementManager.getSettings().jumpForce = 0.0f;
            movementManager.update(playerRef.getPacketHandler());

            // Update camera
            Vector3f headRotation = playerRef.getHeadRotation().clone();
            ServerCameraSettings settings = createOrbitingCamera(headRotation);

            // Send packet
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, true, settings)
            );

            // TODO: Make new animation
            AnimationUtils.playAnimation(ref, AnimationSlot.Movement, "Crouch", true, store);
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
        assert playerRef != null;
        if (playerRef.isValid()) {
            // Reset movement
            MovementManager movementManager = store.getComponent(ref, MovementManager.getComponentType());
            assert movementManager != null;
            movementManager.resetDefaultsAndUpdate(ref, store);

            // Reset camera
            playerRef.getPacketHandler().writeNoCache(new SetServerCamera(ClientCameraView.Custom, false,
                    null));
        }

        AnimationUtils.stopAnimation(ref, AnimationSlot.Movement, true, store);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return QUERY;
    }
}
