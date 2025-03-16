package cc.keiran.commands.impl;

import cc.keiran.commands.ISubCommand;
import cc.keiran.util.ChatUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NetworthCommand implements ISubCommand {
    private static final String API_URL = "https://sky.shiiyu.moe/api/v2/profile/";

    @Override
    public String getName() {
        return "networth";
    }

    @Override
    public String getUsage() {
        return "/ez networth [username]";
    }

    @Override
    public String getDescription() {
        return "Check your SkyBlock networth";
    }

    @Override
    public void execute(CommandSource sender, String[] args) {
        String username;

        if (args.length > 0) {
            username = args[0];
        } else {
            username = Minecraft.getMinecraft().thePlayer.getName();
        }

        ChatUtils.sendFormattedMessage("Fetching networth for " + username + "...", EnumChatFormatting.YELLOW);

        CompletableFuture.runAsync(() -> {
            try {
                JsonObject profileData = fetchProfileData(username);
                if (profileData != null) {
                    processProfileData(username, profileData);
                }
            } catch (Exception e) {
                ChatUtils.sendFormattedMessage("Error fetching networth: " + e.getMessage(), EnumChatFormatting.RED);
                e.printStackTrace();
            }
        });
    }

    private JsonObject fetchProfileData(String username) throws Exception {
        URL url = new URL(API_URL + username);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonParser parser = new JsonParser();
            return parser.parse(response.toString()).getAsJsonObject();
        } else if (responseCode == 500 || responseCode == 404) {
            ChatUtils.sendFormattedMessage("Player not found or has no SkyBlock profiles", EnumChatFormatting.RED);
            return null;
        } else {
            ChatUtils.sendFormattedMessage("API Error: HTTP " + responseCode, EnumChatFormatting.RED);
            return null;
        }
    }

    private void processProfileData(String username, JsonObject data) {
        try {
            if (!data.has("profiles") || data.get("profiles").isJsonNull()) {
                ChatUtils.sendFormattedMessage("No SkyBlock profiles found for " + username, EnumChatFormatting.RED);
                return;
            }

            JsonObject profiles = data.getAsJsonObject("profiles");
            if (profiles.entrySet().size() == 0) {
                ChatUtils.sendFormattedMessage("No SkyBlock profiles found for " + username, EnumChatFormatting.RED);
                return;
            }

            String currentProfileId = null;
            String currentProfileName = null;

            for (Map.Entry<String, JsonElement> entry : profiles.entrySet()) {
                String profileId = entry.getKey();
                JsonObject profile = entry.getValue().getAsJsonObject();

                if (profile.has("current") && profile.get("current").getAsBoolean()) {
                    currentProfileId = profileId;
                    if (profile.has("cute_name")) {
                        currentProfileName = profile.get("cute_name").getAsString();
                    }
                    break;
                }
            }

            if (currentProfileId == null) {
                Map.Entry<String, JsonElement> firstEntry = profiles.entrySet().iterator().next();
                currentProfileId = firstEntry.getKey();
                JsonObject profile = firstEntry.getValue().getAsJsonObject();
                if (profile.has("cute_name")) {
                    currentProfileName = profile.get("cute_name").getAsString();
                }
            }

            if (currentProfileName == null) {
                currentProfileName = "Unknown";
            }

            JsonObject profile = profiles.getAsJsonObject(currentProfileId);

            if (!profile.has("data") || !profile.getAsJsonObject("data").has("networth")) {
                ChatUtils.sendFormattedMessage("Networth data not available for this profile", EnumChatFormatting.RED);
                return;
            }

            JsonObject networthData = profile.getAsJsonObject("data").getAsJsonObject("networth");

            if (!networthData.has("networth")) {
                ChatUtils.sendFormattedMessage("Networth data not available for this profile", EnumChatFormatting.RED);
                return;
            }

            double networth = networthData.get("networth").getAsDouble();
            String formattedNetworth = formatNetworth(networth);

            displayNetworth(username, currentProfileName, formattedNetworth, networth);

        } catch (Exception e) {
            ChatUtils.sendFormattedMessage("Error processing profile data: " + e.getMessage(), EnumChatFormatting.RED);
            e.printStackTrace();
        }
    }

    private String formatNetworth(double networth) {
        DecimalFormat df = new DecimalFormat("#.#");

        if (networth >= 1_000_000_000) {
            return df.format(networth / 1_000_000_000) + "b";
        } else if (networth >= 1_000_000) {
            return df.format(networth / 1_000_000) + "m";
        } else if (networth >= 1_000) {
            return df.format(networth / 1_000) + "k";
        } else {
            return df.format(networth);
        }
    }

    private void displayNetworth(String username, String profileName, String formattedNetworth, double rawNetworth) {
        IChatComponent header = new ChatComponentText("§8§m                                                §r");
        Minecraft.getMinecraft().thePlayer.addChatMessage(header);

        IChatComponent playerInfo = new ChatComponentText("§8» §fPlayer: §b" + username + " §8| §fProfile: §a" + profileName);
        Minecraft.getMinecraft().thePlayer.addChatMessage(playerInfo);

        // this could be cool idk
        EnumChatFormatting networthColor;
        if (rawNetworth >= 1_000_000_000) {
            networthColor = EnumChatFormatting.LIGHT_PURPLE;
        } else if (rawNetworth >= 500_000_000) {
            networthColor = EnumChatFormatting.GOLD;
        } else if (rawNetworth >= 100_000_000) {
            networthColor = EnumChatFormatting.YELLOW;
        } else if (rawNetworth >= 10_000_000) {
            networthColor = EnumChatFormatting.GREEN;
        } else {
            networthColor = EnumChatFormatting.AQUA;
        }

        IChatComponent networthInfo = new ChatComponentText("§8» §fNetworth: " + networthColor + formattedNetworth + " coins");
        Minecraft.getMinecraft().thePlayer.addChatMessage(networthInfo);

        IChatComponent linkLine = new ChatComponentText("§8» §fView detailed breakdown: ");
        IChatComponent linkComponent = new ChatComponentText("§b§nSkyCrypt");
        linkComponent.getChatStyle()
        .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://sky.shiiyu.moe/stats/" + username))
        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                          new ChatComponentText("§7Click to open SkyCrypt profile")));
        linkLine.appendSibling(linkComponent);
        Minecraft.getMinecraft().thePlayer.addChatMessage(linkLine);

        IChatComponent footer = new ChatComponentText("§8§m                                                §r");
        Minecraft.getMinecraft().thePlayer.addChatMessage(footer);
    }
}
