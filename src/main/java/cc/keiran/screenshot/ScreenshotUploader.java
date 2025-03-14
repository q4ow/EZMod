package cc.keiran.screenshot;

import cc.keiran.EZMod;
import cc.keiran.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScreenshotUploader {
    private static final int BUFFER_SIZE = 4096;
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH.mm.ss";

    public File takeScreenshot() {
        Minecraft mc = Minecraft.getMinecraft();
        File screenshotDir = new File(mc.mcDataDir, "screenshots");
        if (!screenshotDir.exists() && !screenshotDir.mkdir()) {
            ChatUtils.sendErrorMessage("Failed to create screenshots directory");
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
                ChatUtils.sendErrorMessage("Framebuffers not supported, using fallback method");
                return null;
            }

            String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            File outputFile = new File(screenshotDir, "screenshot_" + timestamp + "_" +
                                       UUID.randomUUID().toString().substring(0, 6) + ".png");

            ImageIO.write(image, "png", outputFile);
            return outputFile;

        } catch (Exception e) {
            ChatUtils.sendErrorMessage("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> uploadScreenshot(File file, String apiKey) throws IOException {
        Map<String, String> result = new HashMap<>();
        String boundary = "===" + System.currentTimeMillis() + "===";

        HttpURLConnection connection = (HttpURLConnection) new URL(EZMod.UPLOAD_API_URL).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("key", apiKey);
        connection.setRequestProperty("User-Agent",
                                      "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        System.out.println("[E-Z Mod] Uploading to: " + EZMod.UPLOAD_API_URL);
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
