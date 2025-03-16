package cc.keiran.ezmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import cc.keiran.ezmod.EZMod;

@Mod.EventBusSubscriber(modid = EZMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EZModConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    private static final ForgeConfigSpec.ConfigValue<String> API_KEY = BUILDER
        .comment("API Key for uploading screenshots to e-z.host")
        .define("apiKey", "");
    
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    
    public static String getApiKey() {
        return API_KEY.get();
    }
    
    public static void setApiKey(String newApiKey) {
        API_KEY.set(newApiKey);
    }
    
    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        System.out.println("[E-Z Mod] Config loaded. API Key: " + 
                          (getApiKey() != null && !getApiKey().isEmpty() ? "[REDACTED]" : "not set"));
    }
    
    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        System.out.println("[E-Z Mod] Config reloaded");
    }
}

