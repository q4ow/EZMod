package cc.keiran.commands;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.ServerChatEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandAliasHandler {
    private final Map<String, String> commandAliases;
    private File logFile;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private boolean loggingInitialized = false;
    
    // Pattern that matches various chat formats including party, guild, etc.
    private static final Pattern CHAT_PATTERN = Pattern.compile("(?:.*> )?(\\w+)(?: ☠)?: (.+)");

    public CommandAliasHandler() {
        commandAliases = new HashMap<>();
        registerCommandAliases();
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
     * Initialize logging system when it's safe to do so otherwise it fucking crashes
     */
    private void initLogging() {
        if (loggingInitialized) return;

        try {
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ServerChatEvent event) {
        if (!loggingInitialized) {
            initLogging();
        }

        String message = event.getMessage().getUnformattedText().trim();
        logMessage("RAW MESSAGE: " + message);

        String playerName = event.getUsername();
        Matcher matcher = CHAT_PATTERN.matcher(message);
        
        // Check if the message matches our pattern and is from the player
        if (matcher.find() && matcher.group(1).equals(playerName)) {
            String chatSender = matcher.group(1);
            String chatContent = matcher.group(2).trim();
            
            logMessage("PLAYER CHAT DETECTED: " + chatSender + " said: " + chatContent);

            if (commandAliases.containsKey(chatContent)) {
                logMessage("ALIAS MATCHED: '" + chatContent + "' -> '" + commandAliases.get(chatContent) + "'");
                
                // Cancel event FIRST before doing anything else to prevent Hypixel from seeing it
                event.setCanceled(true);

                String command = commandAliases.get(chatContent);
                logMessage("EXECUTING COMMAND: " + command);

                // Use a slight delay to make sure the event cancellation takes effect
                new Thread(() -> {
                    try {
                        Thread.sleep(50);
                        Minecraft.getMinecraft().addScheduledTask(() -> {
                            Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
                            logMessage("COMMAND SENT");
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                
                logMessage("EVENT CANCELLED");
            } else {
                logMessage("NO ALIAS MATCHED");
            }

            logMessage("----------------------------------------");
        }
    }

    private void logMessage(String message) {
        boolean loggingEnabled = true; // Changed to true for debugging

        if (!loggingEnabled) {
            return;
        }

        System.out.println("[EZMod Alias] " + message);

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
