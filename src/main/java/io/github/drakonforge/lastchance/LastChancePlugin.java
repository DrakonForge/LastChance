package io.github.drakonforge.lastchance;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketFilter;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.command.LastChanceCommand;
import io.github.drakonforge.lastchance.component.DownedState;
import io.github.drakonforge.lastchance.component.LastChance;
import io.github.drakonforge.lastchance.system.downedstate.EnterExitDownedStateSystem;
import io.github.drakonforge.lastchance.system.ResetOnRespawnSystem;
import io.github.drakonforge.lastchance.system.downedstate.ManageParryStateSystem;
import io.github.drakonforge.lastchance.system.downedstate.TriggerOnDamageSystem;
import io.github.drakonforge.lastchance.system.RegisterLastChanceSystem;
import io.github.drakonforge.lastchance.system.downedstate.RemoveOnExpireSystem;
import io.github.drakonforge.lastchance.system.UpdateLastChanceSystem;
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

    private PacketFilter inboundFilter;

    public static LastChancePlugin getInstance() {
        return instance;
    }

    public LastChancePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName() + " version " + this.getManifest().getVersion().toString());

        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.lastChanceComponentType = entityStoreRegistry.registerComponent(
                LastChance.class, LastChance::new);
        this.downedStateComponentType = entityStoreRegistry.registerComponent(
                DownedState.class, DownedState::new);
        entityStoreRegistry.registerSystem(new RegisterLastChanceSystem());
        entityStoreRegistry.registerSystem(new TriggerOnDamageSystem());
        entityStoreRegistry.registerSystem(new EnterExitDownedStateSystem());
        entityStoreRegistry.registerSystem(new UpdateLastChanceSystem());
        entityStoreRegistry.registerSystem(new RemoveOnExpireSystem());
        entityStoreRegistry.registerSystem(new ManageParryStateSystem());
        entityStoreRegistry.registerSystem(new ResetOnRespawnSystem());

        this.getCommandRegistry().registerCommand(new LastChanceCommand());

        // PlayerPacketTracker.registerPacketCounters();
        // inboundFilter = PacketAdapters.registerInbound((PlayerPacketFilter) (player,  packet) -> {
        //    if (packet instanceof SyncInteractionChains syncInteractionChainsPacket) {
        //        SyncInteractionChain[] updates = syncInteractionChainsPacket.updates;
        //        for (SyncInteractionChain item : updates) {
        //            InteractionType interactionType = item.interactionType;
        //            if (interactionType == InteractionType.Secondary) {
        //                LOGGER.atInfo().log("Secondary input received!");
        //            }
        //        }
        //    }
        //    return false;
        // });

        LOGGER.atInfo().log("Finished setting up plugin " + this.getName());
    }

    @Override
    protected void shutdown() {
        if (inboundFilter != null) {
            PacketAdapters.deregisterInbound(inboundFilter);
        }
    }

    public ComponentType<EntityStore, LastChance> getLastChanceComponentType() {
        return lastChanceComponentType;
    }

    public ComponentType<EntityStore, DownedState> getDownedStateComponentType() {
        return downedStateComponentType;
    }
}