package cc.keiran;

import cc.keiran.commands.CommandAliasHandler;
import cc.keiran.commands.EZModCommand;
import cc.keiran.config.EZModConfig;
import cc.keiran.screenshot.ScreenshotManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;

@Mod(modid = EZMod.MODID, name = EZMod.NAME, version = EZMod.VERSION,
        guiFactory = "cc.keiran.config.EZModGuiFactory")
public class EZMod {
    public static final String MODID = "EZMod";
    public static final String NAME = "E-Z Mod";
    public static final String VERSION = "1.1.1";
    public static final String UPLOAD_API_URL = "https://api.e-z.host/files";
    public static final String PREFIX = "§8[§b§lE-Z§r§8] §r";

    public static KeyBinding screenshotKey;
    private ScreenshotManager screenshotManager;
    private CommandAliasHandler commandAliasHandler;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EZModConfig.init(event.getModConfigurationDirectory());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        screenshotKey = new KeyBinding("Take Screenshot", Keyboard.KEY_F12, "EZMod");
        ClientRegistry.registerKeyBinding(screenshotKey);

        screenshotManager = new ScreenshotManager();
        
        ClientCommandHandler.instance.registerCommand(new EZModCommand());
        
        commandAliasHandler = new CommandAliasHandler();
        MinecraftForge.EVENT_BUS.register(commandAliasHandler);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (screenshotKey.isPressed()) {
            screenshotManager.takeAndUploadScreenshot();
        }
    }
}
