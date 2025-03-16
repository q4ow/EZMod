package cc.keiran.ezmod.commands;

import cc.keiran.ezmod.screenshot.ScreenshotManager;
import cc.keiran.ezmod.util.ChatUtils;
import cc.keiran.ezmod.util.ClipboardUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandAliasHandler {
    private final ScreenshotManager screenshotManager = new ScreenshotManager();

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("ez")
            .then(Commands.literal("screenshot")
        .executes(context -> {
            Minecraft.getInstance().execute(() -> {
                ChatUtils.sendInfoMessage("Taking screenshot...");
                screenshotManager.takeAndUploadScreenshot();
            });
            return 1;
        }))
        .then(Commands.literal("setkey")
              .then(Commands.argument("key", StringArgumentType.string())
        .executes(context -> {
            String key = StringArgumentType.getString(context, "key");
            Minecraft.getInstance().execute(() -> {
                ChatUtils.sendSuccessMessage("API Key set successfully!");
            });
            return 1;
        })))
        .then(Commands.literal("help")
        .executes(context -> {
            Minecraft.getInstance().execute(() -> {
                ChatUtils.sendInfoMessage("EZMod Commands:");
                ChatUtils.sendInfoMessage("/ez screenshot - Take and upload a screenshot");
                ChatUtils.sendInfoMessage("/ez setkey <key> - Set your API key");
                ChatUtils.sendInfoMessage("/ez help - Show this help message");
            });
            return 1;
        }))
        );
    }

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
