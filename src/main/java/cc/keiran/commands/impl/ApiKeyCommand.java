package cc.keiran.commands.impl;

import cc.keiran.commands.ISubCommand;
import cc.keiran.config.EZModConfig;
import cc.keiran.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class ApiKeyCommand implements ISubCommand {
    @Override
    public String getName() {
        return "apikey";
    }

    @Override
    public String getUsage() {
        return "/ez apikey <set|get>";
    }

    @Override
    public String getDescription() {
        return "Manage your E-Z Host API key";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            ChatUtils.sendFormattedMessage("Usage: " + getUsage(), EnumChatFormatting.YELLOW);
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
        case "set":
            if (args.length >= 2) {
                String apiKey = args[1];
                EZModConfig.setApiKey(apiKey);
                ChatUtils.sendFormattedMessage("API key saved successfully!", EnumChatFormatting.GREEN);
            } else {
                ChatUtils.sendFormattedMessage("Usage: /ez apikey set <key>", EnumChatFormatting.RED);
            }
            break;

        case "get":
            String apiKey = EZModConfig.getApiKey();
            if (apiKey != null && !apiKey.isEmpty()) {
                ChatUtils.sendFormattedMessage("Current API key: " + apiKey, EnumChatFormatting.AQUA);
            } else {
                ChatUtils.sendFormattedMessage("API key is not set. Use /ez apikey set <key> to set it.",
                                               EnumChatFormatting.YELLOW);
            }
            break;

        default:
            ChatUtils.sendFormattedMessage("Unknown action. Usage: " + getUsage(), EnumChatFormatting.RED);
            break;
        }
    }
}

