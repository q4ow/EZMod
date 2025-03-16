package cc.keiran.ezmod.screenshot;

import cc.keiran.ezmod.config.EZModConfig;
import cc.keiran.ezmod.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ScreenshotManager {
    private final ScreenshotUploader uploader;

    public ScreenshotManager() {
        this.uploader = new ScreenshotUploader();
    }

    public void takeAndUploadScreenshot() {
        String apiKey = EZModConfig.getApiKey();

        if (apiKey == null || apiKey.isEmpty()) {
            ChatUtils.sendErrorMessage("No API Key provided. Screenshot aborted.");
            ChatUtils.sendErrorMessage("Please set your API key in the mod configuration.");
            return;
        }

        try {
            ChatUtils.sendInfoMessage("Taking screenshot...");

            // Schedule the screenshot taking on the main render thread
            Minecraft.getInstance().execute(() -> {
                File screenshot = uploader.takeScreenshot();

                if (screenshot == null) {
                    ChatUtils.sendErrorMessage("Failed to take screenshot.");
                    return;
                }

                // Upload can happen on a background thread
                CompletableFuture.supplyAsync(() -> {
                    try {
                        return uploader.uploadScreenshot(screenshot, apiKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }).thenAcceptAsync(response -> {
                    if (response == null) {
                        ChatUtils.sendErrorMessage("Failed to upload screenshot.");
                        return;
                    }

                    if ("true".equals(response.get("success"))) {
                        String imageUrl = response.get("imageUrl");
                        String rawUrl = response.get("rawUrl");
                        String deletionUrl = response.get("deletionUrl");

                        ChatUtils.sendSuccessMessage("Screenshot uploaded successfully!");
                        sendFancyUrlBox(imageUrl, rawUrl, deletionUrl);
                    } else {
                        ChatUtils.sendErrorMessage("File Upload Failed: " +
                                                   response.get("error"));

                        if (response.containsKey("errorDetails")) {
                            System.err.println("[E-Z Mod] Error details: " + response.get("errorDetails"));
                        }
                    }
                }).exceptionally(e -> {
                    ChatUtils.sendErrorMessage("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                });
            });
        } catch (Exception e) {
            ChatUtils.sendErrorMessage("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendFancyUrlBox(String imageUrl, String rawUrl, String deletionUrl) {
        Minecraft mc = Minecraft.getInstance();

        MutableComponent header = Component.literal("§8§m                                                §r");
        mc.player.displayClientMessage(header, false);

        MutableComponent urlLine = Component.literal("§8» §fImage URL: ");
        MutableComponent urlComponent = Component.literal("§b" + imageUrl)
                                        .setStyle(Style.EMPTY
                                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, imageUrl))
                                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                Component.literal("§7Click to open URL"))));
        mc.player.displayClientMessage(urlLine.append(urlComponent), false);

        MutableComponent actionsLine = Component.literal("§8» §fActions: ");

        MutableComponent copyUrlBtn = Component.literal("§8[§bCopy URL§8]")
                                      .setStyle(Style.EMPTY
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, imageUrl))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("§7Click to copy image URL"))));

        MutableComponent copyRawBtn = Component.literal(" §8[§eCopy Raw§8]")
                                      .setStyle(Style.EMPTY
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, rawUrl))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                    Component.literal("§7Click to copy raw URL"))));

        MutableComponent deleteBtn = Component.literal(" §8[§cDelete§8]")
                                     .setStyle(Style.EMPTY
                                               .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, deletionUrl))
                                               .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                   Component.literal("§7Click to copy deletion URL"))));

        mc.player.displayClientMessage(actionsLine.append(copyUrlBtn).append(copyRawBtn).append(deleteBtn), false);

        MutableComponent footer = Component.literal("§8§m                                                §r");
        mc.player.displayClientMessage(footer, false);
    }
}

