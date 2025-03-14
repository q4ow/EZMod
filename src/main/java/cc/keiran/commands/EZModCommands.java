package cc.keiran.commands;

import cc.keiran.commands.impl.ApiKeyCommand;
import cc.keiran.commands.impl.ClipboardCommand;
import cc.keiran.commands.impl.CoordinatesCommand;
import cc.keiran.commands.impl.ServerInfoCommand;
import cc.keiran.commands.impl.NetworthCommand;
import cc.keiran.util.ChatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class EZModCommands extends CommandBase {
    private final Map<String, ISubCommand> subCommands = new HashMap<>();

    public EZModCommands() {
        registerSubCommand(new ApiKeyCommand());
        registerSubCommand(new ClipboardCommand());
        registerSubCommand(new CoordinatesCommand());
        registerSubCommand(new ServerInfoCommand());
        registerSubCommand(new NetworthCommand());
    }

    private void registerSubCommand(ISubCommand command) {
        subCommands.put(command.getName().toLowerCase(), command);
    }

    @Override
    public String getCommandName() {
        return "ez";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ez <subcommand> [args...]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return;
        }

        String subCommandName = args[0].toLowerCase();
        ISubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand != null) {
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);

            subCommand.execute(sender, subArgs);
        } else {
            ChatUtils.sendFormattedMessage("Unknown subcommand: " + subCommandName, EnumChatFormatting.RED);
            showHelp(sender);
        }
    }

    private void showHelp(ICommandSender sender) {
        ChatUtils.sendFormattedMessage("=== EZMod Commands ===", EnumChatFormatting.AQUA);

        for (ISubCommand cmd : subCommands.values()) {
            ChatUtils.sendFormattedMessage(
                cmd.getUsage() + " - " + cmd.getDescription(),
                EnumChatFormatting.YELLOW
            );
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
