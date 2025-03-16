package cc.keiran.ezmod.commands;

import cc.keiran.ezmod.util.ChatUtils;
import cc.keiran.ezmod.util.ClipboardUtils;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandAliasHandler {
    @SubscribeEvent
    public void onClientChat(ClientChatEvent event) {
        String message = event.getMessage();
        
        if (message.startsWith("/ez copy ")) {
            event.setCanceled(true);
            String textToCopy = message.substring("/ez copy ".length());
            ClipboardUtils.copyToClipboard(textToCopy);
            ChatUtils.sendSuccessMessage("Copied to clipboard: " + textToCopy);
        }
    }
}

