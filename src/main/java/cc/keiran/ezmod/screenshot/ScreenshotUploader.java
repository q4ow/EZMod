package cc.keiran.ezmod.screenshot;

import cc.keiran.ezmod.EZMod;
import cc.keiran.ezmod.util.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import com.mojang.blaze3d.platform.NativeImage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ScreenshotUploader {
    private static final int BUFFER_SIZE = 4096;
    private static final String DATE_FORMAT = "yyyy-MM-dd_HH.mm.ss";

    public File takeScreenshot() {
      Minecraft mc = Minecraft.getInstance();
      File screenshotDir = new File(mc.gameDirectory, "screenshots");
      if (!screenshotDir.exists() && !screenshotDir.mkdir()) {
          ChatUtils.sendErrorMessage("Failed to create screenshots directory");
          return null;
      }

      try {
          String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
          String fileName = "screenshot_" + timestamp + "_" + 
                          UUID.randomUUID().toString().substring(0, 6) + ".png";
          File outputFile = new File(screenshotDir, fileName);
          
          // Take screenshot on the main render thread
          NativeImage image = Screenshot.takeScreenshot(mc.getMainRenderTarget());
          
          try {
              image.writeToFile(outputFile);
              return outputFile;
          } catch (IOException e) {
              e.printStackTrace();
              return null;
          } finally {
              image.close();
          }
      } catch (Exception e) {
          ChatUtils.sendErrorMessage("Failed to take screenshot: " + e.getMessage());
          e.printStackTrace();
          return null;
      }
  }

    public Map<String, String> uploadScreenshot(File file, String apiKey) throws IOException {
        Map<String, String> result = new HashMap<>();
        String boundary = "===" + System.currentTimeMillis() + "===";

        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URI(EZMod.UPLOAD_API_URL).toURL().openConnection();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URL: " + EZMod.UPLOAD_API_URL, e);
        }

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

