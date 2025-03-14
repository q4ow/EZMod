package cc.keiran.commands;

import net.minecraft.command.ICommandSender;

public interface ISubCommand {
    String getName();
    String getUsage();
    String getDescription();
    void execute(ICommandSender sender, String[] args);
}

