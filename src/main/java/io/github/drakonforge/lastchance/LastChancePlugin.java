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
import io.github.drakonforge.lastchance.system.RegisterLastChanceSystem;
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

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        this.lastChanceComponentType = this.getEntityStoreRegistry().registerComponent(
                LastChance.class, LastChance::new);

        // This register system doesn't work yet, so latching onto the PlayerReadyEvent instead
        // this.getEntityStoreRegistry().registerSystem(new RegisterLastChanceSystem());
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, event -> {
            Ref<EntityStore> ref = event.getPlayerRef();
            ref.getStore().addComponent(ref, this.lastChanceComponentType);
        });

        this.getCommandRegistry().registerCommand(new LastChanceCommand());
    }

    public ComponentType<EntityStore, LastChance> getLastChanceComponentType() {
        return lastChanceComponentType;
    }
}