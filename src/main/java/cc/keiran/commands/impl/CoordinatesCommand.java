package cc.keiran.commands.impl;

import cc.keiran.commands.ISubCommand;
import cc.keiran.util.ChatUtils;
import cc.keiran.util.ClipboardUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;

public class CoordinatesCommand implements ISubCommand {
    @Override
    public String getName() {
        return "coords";
    }

    @Override
    public String getUsage() {
        return "/ez coords [copy]";
    }

    @Override
    public String getDescription() {
        return "Display or copy your current coordinates";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            ChatUtils.sendFormattedMessage("Player not found", EnumChatFormatting.RED);
            return;
        }

        int x = (int) player.posX;
        int y = (int) player.posY;
        int z = (int) player.posZ;
        String dimension = getDimensionName(player.dimension);

        String coordsText = String.format("X: %d, Y: %d, Z: %d (%s)", x, y, z, dimension);

        if (args.length > 0 && args[0].equalsIgnoreCase("copy")) {
            ClipboardUtils.copyToClipboard(coordsText);
            ChatUtils.sendFormattedMessage("Coordinates copied to clipboard!", EnumChatFormatting.GREEN);
        } else {
            ChatUtils.sendFormattedMessage("Your coordinates: " + coordsText, EnumChatFormatting.AQUA);
            ChatUtils.sendFormattedMessage("Use '/ez coords copy' to copy to clipboard", EnumChatFormatting.GRAY);
        }
    }

    private String getDimensionName(int dimensionId) {
        switch (dimensionId) {
        case -1:
            return "Nether";
        case 0:
            return "Overworld";
        case 1:
            return "End";
        default:
            return "Dimension " + dimensionId;
        }
    }
}
