package io.github.drakonforge.lastchance.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.lastchance.component.LastChance;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class LastChanceCommand extends AbstractPlayerCommand {

    public LastChanceCommand() {
        super("lastchance", "Prints a test message.");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext commandContext,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        LastChance lastChance = store.getComponent(ref, LastChance.getComponentType());
        if (lastChance != null) {
            commandContext.sendMessage(Message.raw("You have " + lastChance.getChancesRemaining() + "/" + lastChance.getMaxChances() + " chances remaining"));
        } else {
            commandContext.sendMessage(Message.raw("No last chance data present"));
        }
    }
}