package cc.keiran.config;

import cc.keiran.EZMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;

public class EZModConfig {
    private static ModConfig config;
    private static String apiKey = "";

    public static void init(File configDir) {
        if (config == null) {
            config = new ModConfig(ModConfig.Type.CLIENT, new File(configDir, "EZMod.toml"));
            syncConfig();
        }

        MinecraftForge.EVENT_BUS.register(new EZModConfig());
    }

    public static void syncConfig() {
        try {
            config.load();

            ModConfig.ConfigValue<String> apiKeyProperty = config.getConfigData().get("apiKey");

            apiKey = apiKeyProperty.get();

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
            ModConfig.ConfigValue<String> apiKeyProperty = config.getConfigData().get("apiKey");

            apiKeyProperty.set(newApiKey);

            if (config.hasChanged()) {
                config.save();
                System.out.println("[E-Z Mod] Config saved after API key update.");
            }
        }
    }

    public static ModConfig getConfig() {
        return config;
    }

    @SubscribeEvent
    public void onConfigChanged(ModConfigEvent event) {
        if (event.getConfig().getModId().equals(EZMod.MODID)) {
            System.out.println("[E-Z Mod] Config changed event detected, syncing...");
            syncConfig();
        }
    }
}
