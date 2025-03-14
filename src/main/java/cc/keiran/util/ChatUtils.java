package cc.keiran.util;

import cc.keiran.EZMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatUtils {
    public static void sendInfoMessage(String message) {
        sendFormattedMessage(message, EnumChatFormatting.AQUA);
    }

    public static void sendSuccessMessage(String message) {
        sendFormattedMessage(message, EnumChatFormatting.GREEN);
    }

    public static void sendErrorMessage(String message) {
        sendFormattedMessage(message, EnumChatFormatting.RED);
    }

    public static void sendFormattedMessage(String message, EnumChatFormatting color) {
        IChatComponent component = new ChatComponentText(EZMod.PREFIX);
        IChatComponent messageComponent = new ChatComponentText(message);
        messageComponent.getChatStyle().setColor(color);
        component.appendSibling(messageComponent);
        Minecraft.getMinecraft().thePlayer.addChatMessage(component);
    }
}
