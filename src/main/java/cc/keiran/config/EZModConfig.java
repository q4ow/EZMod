package cc.keiran.config;

import cc.keiran.EZMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class EZModConfig {
    private static Configuration config;
    private static String apiKey = "";

    public static void init(File configDir) {
        if (config == null) {
            config = new Configuration(new File(configDir, "EZMod.cfg"));
            syncConfig();
        }

        MinecraftForge.EVENT_BUS.register(new EZModConfig());
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

    public static String getApiKey() {
        return apiKey;
    }

    public static void setApiKey(String newApiKey) {
        apiKey = newApiKey;

        if (config != null) {
            Property apiKeyProperty = config.get(
                                          Configuration.CATEGORY_GENERAL,
                                          "apiKey",
                                          "",
                                          "API Key for uploading screenshots to e-z.host"
                                      );

            apiKeyProperty.set(newApiKey);

            if (config.hasChanged()) {
                config.save();
                System.out.println("[E-Z Mod] Config saved after API key update.");
            }
        }
    }

    public static Configuration getConfig() {
        return config;
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(EZMod.MODID)) {
            System.out.println("[E-Z Mod] Config changed event detected, syncing...");
            syncConfig();
        }
    }
}

