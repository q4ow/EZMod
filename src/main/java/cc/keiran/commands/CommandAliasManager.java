package cc.keiran.commands;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class CommandAliasHandler {
    private final Map<String, String> commandAliases;

    public CommandAliasHandler() {
        commandAliases = new HashMap<>();
        registerCommandAliases();
    }

    private void registerCommandAliases() {
        commandAliases.put("!d", "/warp dungeon_hub");
        commandAliases.put("!c", "/collections");
        commandAliases.put("!f1", "/joininstance CATACOMBS_FLOOR_ONE");
        commandAliases.put("!f2", "/joininstance CATACOMBS_FLOOR_TWO");
        commandAliases.put("!f3", "/joininstance CATACOMBS_FLOOR_THREE");
        commandAliases.put("!f4", "/joininstance CATACOMBS_FLOOR_FOUR");
        commandAliases.put("!f5", "/joininstance CATACOMBS_FLOOR_FIVE");
        commandAliases.put("!f6", "/joininstance CATACOMBS_FLOOR_SIX");
        commandAliases.put("!f7", "/joininstance CATACOMBS_FLOOR_SEVEN");
        commandAliases.put("!m1", "/joininstance MASTER_CATACOMBS_FLOOR_ONE");
        commandAliases.put("!m2", "/joininstance MASTER_CATACOMBS_FLOOR_TWO");
        commandAliases.put("!m3", "/joininstance MASTER_CATACOMBS_FLOOR_THREE");
        commandAliases.put("!m4", "/joininstance MASTER_CATACOMBS_FLOOR_FOUR");
        commandAliases.put("!m5", "/joininstance MASTER_CATACOMBS_FLOOR_FIVE");
        commandAliases.put("!m6", "/joininstance MASTER_CATACOMBS_FLOOR_SIX");
        commandAliases.put("!m7", "/joininstance MASTER_CATACOMBS_FLOOR_SEVEN");
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText().trim();
        String[] parts = message.split(" ", 2);
        String commandAlias = parts.length > 1 ? parts[1] : parts[0];

        if (commandAliases.containsKey(commandAlias)) {
            event.setCanceled(true);
            String command = commandAliases.get(commandAlias);
            Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
        }
    }
}
