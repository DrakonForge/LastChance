package io.github.drakonforge.lastchance;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesSystems;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseMotionEvent;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.systems.MovementStatesSystem;
import io.github.drakonforge.lastchance.command.LastChanceCommand;
import io.github.drakonforge.lastchance.component.DownedState;
import io.github.drakonforge.lastchance.component.LastChance;
import io.github.drakonforge.lastchance.system.ChangeDownedStateSystem;
import io.github.drakonforge.lastchance.system.DeaggroDownedTargetSystem;
import io.github.drakonforge.lastchance.system.ResetStateSystem;
import io.github.drakonforge.lastchance.system.TriggerDownedStateSystem;
import io.github.drakonforge.lastchance.system.RegisterLastChanceSystem;
import io.github.drakonforge.lastchance.system.UpdateDownedStateSystem;
import io.github.drakonforge.lastchance.system.UpdateLastChanceSystem;
import io.github.drakonforge.lastchance.util.PlayerPacketTracker;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class LastChancePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static LastChancePlugin instance;

    private ComponentType<EntityStore, LastChance> lastChanceComponentType;
    private ComponentType<EntityStore, DownedState> downedStateComponentType;

    public static LastChancePlugin getInstance() {
        return instance;
    }

    public LastChancePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        LOGGER.atInfo().log("Is my new code running for " + this.getName());

        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.lastChanceComponentType = entityStoreRegistry.registerComponent(
                LastChance.class, LastChance::new);
        this.downedStateComponentType = entityStoreRegistry.registerComponent(
                DownedState.class, DownedState::new);

        entityStoreRegistry.registerSystem(new RegisterLastChanceSystem());
        entityStoreRegistry.registerSystem(new TriggerDownedStateSystem());
        entityStoreRegistry.registerSystem(new ChangeDownedStateSystem());
        entityStoreRegistry.registerSystem(new UpdateLastChanceSystem());
        entityStoreRegistry.registerSystem(new UpdateDownedStateSystem());
        entityStoreRegistry.registerSystem(new ResetStateSystem());
        entityStoreRegistry.registerSystem(new DeaggroDownedTargetSystem());

        this.getCommandRegistry().registerCommand(new LastChanceCommand());

        // PlayerPacketTracker.registerPacketCounters();
        // PacketAdapters.registerInbound((PlayerPacketFilter) (player,  packet) -> {
        //    if (packet instanceof ClientMovement movementPacket) {
        //        return true;
        //        // if (movementPacket.movementStates != null) {
        //        //     if (!movementPacket.movementStates.idle) {
        //        //         LOGGER.atInfo().log("Movement input received!");
        //        //         return true;
        //        //     }
        //        // }
        //        // LOGGER.atInfo().log("Movement input received 2!");
        //    }
        //    return false;
        // });
    }

    public ComponentType<EntityStore, LastChance> getLastChanceComponentType() {
        return lastChanceComponentType;
    }

    public ComponentType<EntityStore, DownedState> getDownedStateComponentType() {
        return downedStateComponentType;
    }
}