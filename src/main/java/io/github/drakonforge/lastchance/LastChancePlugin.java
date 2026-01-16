package io.github.drakonforge.lastchance;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.command.LastChanceCommand;
import io.github.drakonforge.lastchance.component.DownedState;
import io.github.drakonforge.lastchance.component.LastChance;
import io.github.drakonforge.lastchance.system.ChangeDownedStateSystem;
import io.github.drakonforge.lastchance.system.ResetStateSystem;
import io.github.drakonforge.lastchance.system.TriggerDownedStateSystem;
import io.github.drakonforge.lastchance.system.RegisterLastChanceSystem;
import io.github.drakonforge.lastchance.system.UpdateDownedStateSystem;
import io.github.drakonforge.lastchance.system.UpdateLastChanceSystem;
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

        this.getCommandRegistry().registerCommand(new LastChanceCommand());
    }

    public ComponentType<EntityStore, LastChance> getLastChanceComponentType() {
        return lastChanceComponentType;
    }

    public ComponentType<EntityStore, DownedState> getDownedStateComponentType() {
        return downedStateComponentType;
    }
}