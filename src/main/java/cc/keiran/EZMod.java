package cc.keiran;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Mod(modid = EZMod.MODID, name = EZMod.NAME, version = EZMod.VERSION,
        guiFactory = "cc.keiran.EZModGuiFactory")
public class EZMod {
    public static final String MODID = "EZMod";
    public static final String NAME = "E-Z Mod";
    public static final String VERSION = "1.0";
    public static final String UPLOAD_API_URL = "https://api.e-z.host/files";

    public static Configuration config;
    public static String apiKey = "";

    private static final int BUFFER_SIZE = 4096;
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH.mm.ss";
    private static final String PREFIX = "§8[§b§lE-Z§r§8] §r";

    public static KeyBinding screenshotKey;

    private Map<String, String> commandAliases;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(new File(event.getModConfigurationDirectory(), "EZMod.cfg"));
        syncConfig();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        screenshotKey = new KeyBinding("Take Screenshot", Keyboard.KEY_F12, "EZMod");
        ClientRegistry.registerKeyBinding(screenshotKey);

        MinecraftForge.EVENT_BUS.register(this);

        ClientCommandHandler.instance.registerCommand(new CommandBase() {
            @Override
            public String getCommandName() {
                return "ezmod";
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "/ezmod copyurl <url>";
            }

            @Override
            public void processCommand(ICommandSender sender, String[] args) {
                if (args.length >= 2 && "copyurl".equals(args[0])) {
                    String url = args[1];
                    copyToClipboard(url);
                    sendFormattedMessage("URL copied to clipboard!", EnumChatFormatting.GREEN);
                }
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 0;
            }
        });

        registerCommandAliases();
    }

    private void registerCommandAliases() {
        commandAliases = new HashMap<>();
        commandAliases.put("!f1", "/warp dungeon_hub");
        commandAliases.put("!f2", "/warp dungeon_hub");
        commandAliases.put("!f3", "/warp dungeon_hub");
        commandAliases.put("!f4", "/warp dungeon_hub");
        commandAliases.put("!f5", "/warp dungeon_hub");
        commandAliases.put("!f6", "/warp dungeon_hub");
        commandAliases.put("!f7", "/warp dungeon_hub");
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (commandAliases.containsKey(message)) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(commandAliases.get(message));
        }
    }

    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    public static void syncConfig() {
        try {
            config.load();

            Property apiKeyProperty = config.get(
                    Configuration.CATEGORY_GENERAL,
                    "apiKey",
                    "",
                    "API Key for uploading screenshots to e-z.host"
            );

            apiKey = apiKeyProperty.getString();

            System.out.println("[E-Z Mod] Config loaded. API Key: " +
                    (apiKey != null && !apiKey.isEmpty() ? "[REDACTED]" : "not set"));

            if (config.hasChanged()) {
                config.save();
                System.out.println("[E-Z Mod] Config saved after loading.");
            }
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(MODID)) {
            System.out.println("[E-Z Mod] Config changed event detected, syncing...");
            syncConfig();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (screenshotKey.isPressed()) {
            takeAndUploadScreenshot();
        }
    }

    private void takeAndUploadScreenshot() {
        syncConfig();

        if (apiKey == null || apiKey.isEmpty()) {
            sendErrorMessage("No API Key provided. Screenshot aborted.");
            sendErrorMessage("Please set your API key in the mod configuration.");
            return;
        }

        try {
            File screenshot = takeScreenshot();
            if (screenshot != null) {
                sendInfoMessage();
                Map<String, String> response = uploadScreenshot(screenshot);

                if ("true".equals(response.get("success"))) {
                    String imageUrl = response.get("imageUrl");
                    String rawUrl = response.get("rawUrl");
                    String deletionUrl = response.get("deletionUrl");

                    sendSuccessMessage();

                    sendFancyUrlBox(imageUrl, rawUrl, deletionUrl);
                } else {
                    sendErrorMessage("File Upload Failed: " +
                            response.get("error"));

                    if (response.containsKey("errorDetails")) {
                        System.err.println("[E-Z Mod] Error details: " + response.get("errorDetails"));
                    }
                }
            }
        } catch (Exception e) {
            sendErrorMessage("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendInfoMessage() {
        sendFormattedMessage("Taking screenshot...", EnumChatFormatting.AQUA);
    }

    private void sendSuccessMessage() {
        sendFormattedMessage("Taking screenshot...", EnumChatFormatting.GREEN);
    }

    private void sendErrorMessage(String message) {
        sendFormattedMessage(message, EnumChatFormatting.RED);
    }

    private void sendFormattedMessage(String message, EnumChatFormatting color) {
        IChatComponent component = new ChatComponentText(PREFIX);
        IChatComponent messageComponent = new ChatComponentText(message);
        messageComponent.getChatStyle().setColor(color);
        component.appendSibling(messageComponent);
        Minecraft.getMinecraft().thePlayer.addChatMessage(component);
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

    private File takeScreenshot() {
        Minecraft mc = Minecraft.getMinecraft();
        File screenshotDir = new File(mc.mcDataDir, "screenshots");
        if (!screenshotDir.exists() && !screenshotDir.mkdir()) {
            sendErrorMessage("Failed to create screenshots directory");
            return null;
        }

        int width = mc.displayWidth;
        int height = mc.displayHeight;
        Framebuffer framebuffer = mc.getFramebuffer();

        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            if (OpenGlHelper.isFramebufferEnabled()) {
                int frameWidth = framebuffer.framebufferTextureWidth;
                int frameHeight = framebuffer.framebufferTextureHeight;

                IntBuffer pixelBuffer = BufferUtils.createIntBuffer(frameWidth * frameHeight);
                int[] pixelValues = new int[frameWidth * frameHeight];

                GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

                framebuffer.bindFramebuffer(false);
                GL11.glReadPixels(0, 0, frameWidth, frameHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

                pixelBuffer.get(pixelValues);

                for (int i = 0; i < frameHeight; i++) {
                    for (int j = 0; j < frameWidth; j++) {
                        int pixel = pixelValues[i * frameWidth + j];
                        int alpha = (pixel >> 24) & 0xFF;
                        int red = pixel & 0xFF;
                        int green = (pixel >> 8) & 0xFF;
                        int blue = (pixel >> 16) & 0xFF;

                        image.setRGB(j, frameHeight - 1 - i, (alpha << 24) | (red << 16) | (green << 8) | blue);
                    }
                }
            } else {
                sendErrorMessage("Framebuffers not supported, using fallback method");
                return null;
            }

            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            File outputFile = new File(screenshotDir, "screenshot_" + timestamp + "_" +
                    UUID.randomUUID().toString().substring(0, 6) + ".png");

            ImageIO.write(image, "png", outputFile);
            return outputFile;

        } catch (Exception e) {
            sendErrorMessage("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> uploadScreenshot(File file) throws IOException {
        Map<String, String> result = new HashMap<>();
        String boundary = "===" + System.currentTimeMillis() + "===";

        HttpURLConnection connection = (HttpURLConnection) new URL(UPLOAD_API_URL).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("key", apiKey);
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        System.out.println("[E-Z Mod] Uploading to: " + UPLOAD_API_URL);
        System.out.println("[E-Z Mod] File size: " + file.length() + " bytes");
        System.out.println("[E-Z Mod] API Key used: " +
                (apiKey != null ? apiKey.substring(0, Math.min(5, apiKey.length())) + "..." : "null"));

        try (OutputStream outputStream = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true)) {

            writer.append("--").append(boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"\r\n");
            writer.append("Content-Type: image/png\r\n\r\n");
            writer.flush();

            try (FileInputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }

            writer.append("\r\n").append("--").append(boundary).append("--").append("\r\n");
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();

        System.out.println("[E-Z Mod] Response code: " + responseCode + " " + responseMessage);

        logResponseHeaders(connection);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String responseBody = readInputStream(connection.getInputStream());
            System.out.println("[E-Z Mod] Response body: " + responseBody);
            return parseJsonResponse(responseBody);
        } else {
            result.put("success", "false");
            result.put("error", "HTTP Error: " + responseCode);

            try {
                String errorBody = readInputStream(connection.getErrorStream());
                System.out.println("[E-Z Mod] Error response body: " + errorBody);
                result.put("errorDetails", errorBody);
            } catch (Exception e) {
                System.out.println("[E-Z Mod] Could not read error stream: " + e.getMessage());
            }
        }

        return result;
    }

    private void logResponseHeaders(HttpURLConnection connection) {
        System.out.println("[E-Z Mod] Response headers:");
        Map<String, List<String>> responseHeaders = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> header : responseHeaders.entrySet()) {
            if (header.getKey() != null) {
                System.out.println("  " + header.getKey() + ": " + header.getValue());
            }
        }
    }

    private String readInputStream(InputStream stream) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private Map<String, String> parseJsonResponse(String jsonString) {
        Map<String, String> result = new HashMap<>();

        try {
            if (jsonString.contains("\"success\":true")) {
                result.put("success", "true");

                result.put("imageUrl", extractJsonValue(jsonString, "imageUrl"));
                result.put("rawUrl", extractJsonValue(jsonString, "rawUrl"));
                result.put("deletionUrl", extractJsonValue(jsonString, "deletionUrl"));
            } else {
                result.put("success", "false");

                String errorMessage = extractJsonValue(jsonString, "message");
                result.put("error", errorMessage != null ? errorMessage : "Unknown error");
            }
        } catch (Exception e) {
            result.put("success", "false");
            result.put("error", "Failed to parse response: " + e.getMessage());
        }

        return result;
    }

    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern) + pattern.length();
        if (start < pattern.length()) return null;

        int end = json.indexOf("\"", start);
        if (end < 0) return null;

        return json.substring(start, end).replace("\\/", "/");
    }
}
