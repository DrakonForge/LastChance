package io.github.drakonforge.lastchance;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.command.LastChanceCommand;
import io.github.drakonforge.lastchance.component.LastChance;
import io.github.drakonforge.lastchance.system.EnterDownedStateSystem;
import io.github.drakonforge.lastchance.system.RegisterLastChanceSystem;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class LastChancePlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static LastChancePlugin instance;

    private ComponentType<EntityStore, LastChance> lastChanceComponentType;

    public static LastChancePlugin getInstance() {
        return instance;
    }

    public LastChancePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    // Fixing the race condition when creating new systems (query is null error) present in some build systems, including this one
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        this.lastChanceComponentType = this.getEntityStoreRegistry().registerComponent(
                LastChance.class, LastChance::new);

        this.getEntityStoreRegistry().registerSystem(new RegisterLastChanceSystem());
        this.getEntityStoreRegistry().registerSystem(new EnterDownedStateSystem());

        this.getCommandRegistry().registerCommand(new LastChanceCommand());
    }

    public ComponentType<EntityStore, LastChance> getLastChanceComponentType() {
        return lastChanceComponentType;
    }
}