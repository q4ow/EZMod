package cc.keiran.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AliasCommand extends CommandBase {
    private final String commandName;
    private final String alias;
    private final String targetCommand;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AliasCommand(String commandName, String alias, String targetCommand) {
        this.commandName = commandName;
        this.alias = alias;
        this.targetCommand = targetCommand;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + commandName;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        logMessage("COMMAND EXECUTED: /" + commandName);
        logMessage("SENDING TARGET COMMAND: " + targetCommand);

        Minecraft.getMinecraft().thePlayer.sendChatMessage(targetCommand);

        logMessage("TARGET COMMAND SENT");
        logMessage("----------------------------------------");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    private void logMessage(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = "[" + timestamp + "] " + message;
        System.out.println("[EZMod Alias Command] " + message);

        /*
          // logging
          File mcDir = Minecraft.getMinecraft().mcDataDir;
          File logsDir = new File(mcDir, "logs");
          File logFile = new File(logsDir, "ezmod_aliases.log");

          try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
              writer.write(logEntry);
              writer.newLine();
          } catch (IOException e) {
              System.err.println("Error writing to alias log file: " + e.getMessage());
          }
        */
    }
}

