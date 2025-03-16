package cc.keiran.commands.impl;

import cc.keiran.commands.ISubCommand;
import cc.keiran.util.ChatUtils;
import cc.keiran.util.ClipboardUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.EnumChatFormatting;

public class ClipboardCommand implements ISubCommand {
    @Override
    public String getName() {
        return "copy";
    }

    @Override
    public String getUsage() {
        return "/ez copy <url|text>";
    }

    @Override
    public String getDescription() {
        return "Copy text to clipboard";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        if (args.length == 0) {
            ChatUtils.sendFormattedMessage("Usage: " + getUsage(), EnumChatFormatting.RED);
            return;
        }

        StringBuilder textToCopy = new StringBuilder();
        for (String arg : args) {
            if (textToCopy.length() > 0) {
                textToCopy.append(" ");
            }
            textToCopy.append(arg);
        }

        ClipboardUtils.copyToClipboard(textToCopy.toString());
        ChatUtils.sendFormattedMessage("Text copied to clipboard!", EnumChatFormatting.GREEN);
    }
}
