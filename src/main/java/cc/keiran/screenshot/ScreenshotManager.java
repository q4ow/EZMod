package cc.keiran.screenshot;

import cc.keiran.config.EZModConfig;
import cc.keiran.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.File;
import java.util.Map;

public class ScreenshotManager {
    private final ScreenshotUploader uploader;
    
    public ScreenshotManager() {
        this.uploader = new ScreenshotUploader();
    }
    
    public void takeAndUploadScreenshot() {
        EZModConfig.syncConfig();
        String apiKey = EZModConfig.getApiKey();

        if (apiKey == null || apiKey.isEmpty()) {
            ChatUtils.sendErrorMessage("No API Key provided. Screenshot aborted.");
            ChatUtils.sendErrorMessage("Please set your API key in the mod configuration.");
            return;
        }

        try {
            File screenshot = uploader.takeScreenshot();
            if (screenshot != null) {
                ChatUtils.sendInfoMessage("Taking screenshot...");
                Map<String, String> response = uploader.uploadScreenshot(screenshot, apiKey);

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
            }
        } catch (Exception e) {
            ChatUtils.sendErrorMessage("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendFancyUrlBox(String imageUrl, String rawUrl, String deletionUrl) {
        IChatComponent header = new ChatComponentText("§8§m                                                §r");
        Minecraft.getMinecraft().thePlayer.addChatMessage(header);

        IChatComponent urlLine = new ChatComponentText("§8» §fImage URL: ");
        IChatComponent urlComponent = new ChatComponentText("§b" + imageUrl);
        urlComponent.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, imageUrl))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§7Click to open URL")));
        urlLine.appendSibling(urlComponent);
        Minecraft.getMinecraft().thePlayer.addChatMessage(urlLine);

        IChatComponent actionsLine = new ChatComponentText("§8» §fActions: ");

        IChatComponent copyUrlBtn = new ChatComponentText("§8[§bCopy URL§8]");
        copyUrlBtn.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ezmod copyurl " + imageUrl))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§7Click to copy image URL")));

        IChatComponent copyRawBtn = new ChatComponentText(" §8[§eCopy Raw§8]");
        copyRawBtn.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ezmod copyurl " + rawUrl))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§7Click to copy raw URL")));

        IChatComponent deleteBtn = new ChatComponentText(" §8[§cDelete§8]");
        deleteBtn.getChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ezmod copyurl " + deletionUrl))
                .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ChatComponentText("§7Click to copy deletion URL")));

        actionsLine.appendSibling(copyUrlBtn).appendSibling(copyRawBtn).appendSibling(deleteBtn);
        Minecraft.getMinecraft().thePlayer.addChatMessage(actionsLine);

        IChatComponent footer = new ChatComponentText("§8§m                                                §r");
        Minecraft.getMinecraft().thePlayer.addChatMessage(footer);
    }
}
