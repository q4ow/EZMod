package cc.keiran.commands;

import cc.keiran.util.ClipboardUtils;
import cc.keiran.util.ChatUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class EZModCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "ezmod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ezmod copyurl <url>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 2 && "copyurl".equals(args[0])) {
            String url = args[1];
            ClipboardUtils.copyToClipboard(url);
            ChatUtils.sendFormattedMessage("URL copied to clipboard!", EnumChatFormatting.GREEN);
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
