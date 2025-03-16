package cc.keiran.ezmod;

import cc.keiran.ezmod.commands.CommandAliasHandler;
import cc.keiran.ezmod.commands.EZModCommands;
import cc.keiran.ezmod.config.EZModConfig;
import cc.keiran.ezmod.screenshot.ScreenshotManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

@Mod(EZMod.MODID)
public class EZMod {
    public static final String MODID = "ezmod";
    public static final String NAME = "E-Z Mod";
    public static final String VERSION = "2.0.0";
    public static final String UPLOAD_API_URL = "https://api.e-z.host/files";
    public static final String PREFIX = "§8[§b§lE-Z§r§8] §r";

    public static KeyMapping screenshotKey;
    private static ScreenshotManager screenshotManager;
    private CommandAliasHandler commandAliasHandler;

    @SuppressWarnings("removal")
    public EZMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerKeyBindings);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, EZModConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        screenshotManager = new ScreenshotManager();
        commandAliasHandler = new CommandAliasHandler();
        MinecraftForge.EVENT_BUS.register(commandAliasHandler);
    }

    private void registerKeyBindings(final RegisterKeyMappingsEvent event) {
        screenshotKey = new KeyMapping("Take Screenshot", GLFW.GLFW_KEY_F12, "EZMod");
        event.register(screenshotKey);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (Minecraft.getInstance().screen == null && screenshotKey.consumeClick()) {
                screenshotManager.takeAndUploadScreenshot();
            }
        }
    }
}

