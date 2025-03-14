package cc.keiran.commands;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CommandAliasHandler {
    private final Map<String, String> commandAliases;
    private File logFile;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean loggingInitialized = false;

    public CommandAliasHandler() {
        commandAliases = new HashMap<>();
        registerCommandAliases();
        
        // We'll initialize logging later when Minecraft is fully loaded
        System.out.println("[EZMod Alias] CommandAliasHandler initialized with " + commandAliases.size() + " aliases");
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

    /**
     * Initialize logging system when it's safe to do so
     */
    private void initLogging() {
        if (loggingInitialized) return;
        
        try {
            // Create log file in the Minecraft directory
            File mcDir = Minecraft.getMinecraft().mcDataDir;
            if (mcDir != null) {
                File logsDir = new File(mcDir, "logs");
                if (!logsDir.exists()) {
                    logsDir.mkdir();
                }
                logFile = new File(logsDir, "ezmod_aliases.log");
                loggingInitialized = true;
                
                logMessage("Logging system initialized");
                logMessage("Registered aliases: " + commandAliases.keySet());
            }
        } catch (Exception e) {
            System.err.println("[EZMod Alias] Failed to initialize logging: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This event is fired when a chat message is received from the server
     * We use it to detect when the player types an alias in chat
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ClientChatReceivedEvent event) {
        // Initialize logging if not done yet
        if (!loggingInitialized) {
            initLogging();
        }
        
        // Only process chat messages (type 0)
        if (event.type != 0) {
            return;
        }
        
        String message = event.message.getUnformattedText().trim();
        
        // Check if this is a message from the player
        String playerName = Minecraft.getMinecraft().thePlayer.getName();
        if (message.contains(playerName + ":")) {
            logMessage("PLAYER CHAT DETECTED: " + message);
            
            // Extract just the message part after the colon
            int colonIndex = message.indexOf(":");
            if (colonIndex >= 0 && colonIndex + 1 < message.length()) {
                String chatContent = message.substring(colonIndex + 1).trim();
                logMessage("CHAT CONTENT: '" + chatContent + "'");
                
                // Check if the message is an alias
                if (commandAliases.containsKey(chatContent)) {
                    logMessage("ALIAS MATCHED: '" + chatContent + "' -> '" + commandAliases.get(chatContent) + "'");
                    
                    // Cancel this message so it doesn't appear in chat
                    event.setCanceled(true);
                    
                    // Execute the command
                    String command = commandAliases.get(chatContent);
                    logMessage("EXECUTING COMMAND: " + command);
                    
                    // Execute the command
                    Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
                    logMessage("COMMAND SENT");
                } else {
                    logMessage("NO ALIAS MATCHED");
                }
            }
            
            logMessage("----------------------------------------");
        }
    }
    
    private void logMessage(String message) {
        // Print to console for immediate feedback
        System.out.println("[EZMod Alias] " + message);
        
        // Only write to file if logging is initialized
        if (loggingInitialized && logFile != null) {
            try {
                String timestamp = DATE_FORMAT.format(new Date());
                String logEntry = "[" + timestamp + "] " + message;
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    writer.write(logEntry);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.err.println("[EZMod Alias] Error writing to log file: " + e.getMessage());
            }
        }
    }
}

