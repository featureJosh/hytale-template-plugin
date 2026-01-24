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
        DodgeConfig config = DodgeConfig.get();
        ctx.sendMessage(Message.raw("DodgeMod: Configurations"));
        ctx.sendMessage(Message.raw("Dodge Cooldown: " + config.dodgeCooldownMs + "ms"));
        ctx.sendMessage(Message.raw("iFrame Duration: " + config.iFrameDurationMs + "ms"));
        ctx.sendMessage(Message.raw("Dodge Velocity: " + config.dodgeVelocity));
        ctx.sendMessage(Message.raw("Vertical Hop: " + config.verticalHop));
        ctx.sendMessage(Message.raw("Stamina Cost: " + config.staminaCost));
        ctx.sendMessage(Message.raw("Air Dash: " + (config.allowAirDash ? "Enabled" : "Disabled")));
    }
}
