package cc.keiran;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EZModGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {}

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return EZModConfigGui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    public static class EZModConfigGui extends GuiConfig {
        private static final int SAVE_BUTTON_ID = 98765;

        public EZModConfigGui(GuiScreen parent) {
            super(
                    parent,
                    getConfigElements(),
                    EZMod.MODID,
                    false,
                    false,
                    "E-Z Mod Configuration"
            );
        }

        @Override
        public void initGui() {
            super.initGui();

            buttonList.clear();

            int buttonWidth = 100;
            int buttonHeight = 20;
            int spacing = 10;
            int totalWidth = buttonWidth * 2 + spacing;
            int startX = this.width / 2 - totalWidth / 2;
            int buttonY = this.height - 30;

            buttonList.add(new GuiButton(
                    SAVE_BUTTON_ID,
                    startX,
                    buttonY,
                    buttonWidth,
                    buttonHeight,
                    "Save Changes"
            ));

            buttonList.add(new GuiButton(
                    2000,
                    startX + buttonWidth + spacing,
                    buttonY,
                    buttonWidth,
                    buttonHeight,
                    "Done"
            ));
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button.id == SAVE_BUTTON_ID) {
                saveConfig();
                if (Minecraft.getMinecraft().thePlayer != null) {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                            new ChatComponentText("§a[E-Z Mod] Configuration saved!")
                    );
                }
            } else {
                super.actionPerformed(button);
            }
        }

        private void saveConfig() {
            for (GuiConfigEntries.IConfigEntry entry : entryList.listEntries) {
                if (entry.isChanged()) {
                    entry.saveConfigElement();
                }
            }

            EZMod.config.save();
            EZMod.syncConfig();
        }

        @Override
        public void onGuiClosed() {
            saveConfig();
            super.onGuiClosed();
        }

        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> list = new ArrayList<IConfigElement>();

            if (EZMod.config == null) {
                return list;
            }

            list.addAll(new ConfigElement(EZMod.config.getCategory(Configuration.CATEGORY_GENERAL))
                    .getChildElements());

            return list;
        }
    }
}
