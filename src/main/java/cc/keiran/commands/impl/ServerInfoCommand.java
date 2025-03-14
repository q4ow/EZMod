package cc.keiran.commands.impl;

import cc.keiran.commands.ISubCommand;
import cc.keiran.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

public class ServerInfoCommand implements ISubCommand {
    @Override
    public String getName() {
        return "server";
    }

    @Override
    public String getUsage() {
        return "/ez server";
    }

    @Override
    public String getDescription() {
        return "Display current server information";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.isSingleplayer()) {
            ChatUtils.sendFormattedMessage("You are playing in singleplayer mode", EnumChatFormatting.YELLOW);
            return;
        }

        ServerData serverData = mc.getCurrentServerData();
        if (serverData == null) {
            ChatUtils.sendFormattedMessage("Server information not available", EnumChatFormatting.RED);
            return;
        }

        ChatUtils.sendFormattedMessage("=== Server Information ===", EnumChatFormatting.AQUA);
        ChatUtils.sendFormattedMessage("Name: " + serverData.serverName, EnumChatFormatting.YELLOW);
        ChatUtils.sendFormattedMessage("IP: " + serverData.serverIP, EnumChatFormatting.YELLOW);

        int playerCount = -1;
        int maxPlayers = -1;

        if (serverData.populationInfo != null) {
            String[] parts = serverData.populationInfo.split("/");
            if (parts.length == 2) {
                try {
                    playerCount = Integer.parseInt(parts[0].trim());
                    maxPlayers = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    // Ignoring this bc im lazy
                }
            }
        }

        if (playerCount >= 0 && maxPlayers >= 0) {
            ChatUtils.sendFormattedMessage(
                String.format("Players: %d/%d", playerCount, maxPlayers),
                EnumChatFormatting.YELLOW
            );
        }

        if (serverData.gameVersion != null) {
            ChatUtils.sendFormattedMessage("Version: " + serverData.gameVersion, EnumChatFormatting.YELLOW);
        }

        long ping = serverData.pingToServer;
        EnumChatFormatting pingColor = ping < 100 ? EnumChatFormatting.GREEN :
                                       ping < 300 ? EnumChatFormatting.YELLOW :
                                       EnumChatFormatting.RED;

        ChatUtils.sendFormattedMessage("Ping: " + pingColor + ping + "ms", EnumChatFormatting.YELLOW);
    }
}

