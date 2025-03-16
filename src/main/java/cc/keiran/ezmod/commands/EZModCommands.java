package cc.keiran.ezmod.commands;

import cc.keiran.ezmod.config.EZModConfig;
import cc.keiran.ezmod.screenshot.ScreenshotManager;
import cc.keiran.ezmod.util.ChatUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class EZModCommands {
    private static final ScreenshotManager screenshotManager = new ScreenshotManager();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ez")
            .then(Commands.literal("screenshot")
        .executes(context -> {
            screenshotManager.takeAndUploadScreenshot();
            return 1;
        }))
        .then(Commands.literal("setkey")
              .then(Commands.argument("key", StringArgumentType.string())
        .executes(context -> {
            String key = StringArgumentType.getString(context, "key");
            EZModConfig.setApiKey(key);
            ChatUtils.sendSuccessMessage("API Key set successfully!");
            return 1;
        })))
        .then(Commands.literal("help")
        .executes(context -> {
            sendHelpMessage();
            return 1;
        }))
        );
    }

    private static void sendHelpMessage() {
        ChatUtils.sendInfoMessage("EZMod Commands:");
        ChatUtils.sendInfoMessage("/ez screenshot - Take and upload a screenshot");
        ChatUtils.sendInfoMessage("/ez setkey <key> - Set your API key");
        ChatUtils.sendInfoMessage("/ez help - Show this help message");
    }
}

