package org.featureJosh.plugin;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import javax.annotation.Nonnull;

public class DodgeModCommand extends CommandBase {
    public DodgeModCommand() {
        super("dodgemod", "Check plugin settings");
        setPermissionGroup(GameMode.Adventure);
    }

    protected void executeSync(@Nonnull CommandContext ctx) {
        SoulsDodgeSettings settings = SoulsDodgeSettings.get();
        ctx.sendMessage(Message.raw("Souls Dodge Settings"));
        ctx.sendMessage(Message.raw("Cooldown: " + settings.cooldown + "ms"));
        ctx.sendMessage(Message.raw("Invincibility: " + settings.invincibility + "ms"));
        ctx.sendMessage(Message.raw("Speed: " + settings.speed));
        ctx.sendMessage(Message.raw("Stamina: " + settings.stamina));
        ctx.sendMessage(Message.raw("Air Dodge: " + (settings.airDodge ? "Enabled" : "Disabled")));
    }
}
