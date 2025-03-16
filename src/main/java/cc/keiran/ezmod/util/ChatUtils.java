package cc.keiran.ezmod.util;

import cc.keiran.ezmod.EZMod;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

public class ChatUtils {
    public static void sendInfoMessage(String message) {
        sendFormattedMessage(message, ChatFormatting.AQUA);
    }

    public static void sendSuccessMessage(String message) {
        sendFormattedMessage(message, ChatFormatting.GREEN);
    }

    public static void sendErrorMessage(String message) {
        sendFormattedMessage(message, ChatFormatting.RED);
    }

    public static void sendFormattedMessage(String message, ChatFormatting color) {
        MutableComponent prefix = Component.literal(EZMod.PREFIX);
        MutableComponent messageComponent = Component.literal(message).withStyle(color);
        Minecraft.getInstance().player.displayClientMessage(prefix.append(messageComponent), false);
    }
}

