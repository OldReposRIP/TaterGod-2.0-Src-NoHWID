package club.tater.tatergod.features.modules.client;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.util.Util;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiCustomizeSkin;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiModList;

public class GUIBlur extends Module implements Util {

    public GUIBlur() {
        super("GUIBlur", "nigga", Module.Category.CLIENT, true, false, false);
    }

    public void onDisable() {
        if (GUIBlur.mc.world != null) {
            GUIBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }

    }

    public void onUpdate() {
        if (GUIBlur.mc.world != null) {
            if (!ClickGui.getInstance().isEnabled() && !(GUIBlur.mc.currentScreen instanceof GuiContainer) && !(GUIBlur.mc.currentScreen instanceof GuiChat) && !(GUIBlur.mc.currentScreen instanceof GuiConfirmOpenLink) && !(GUIBlur.mc.currentScreen instanceof GuiEditSign) && !(GUIBlur.mc.currentScreen instanceof GuiGameOver) && !(GUIBlur.mc.currentScreen instanceof GuiOptions) && !(GUIBlur.mc.currentScreen instanceof GuiIngameMenu) && !(GUIBlur.mc.currentScreen instanceof GuiVideoSettings) && !(GUIBlur.mc.currentScreen instanceof GuiScreenOptionsSounds) && !(GUIBlur.mc.currentScreen instanceof GuiControls) && !(GUIBlur.mc.currentScreen instanceof GuiCustomizeSkin) && !(GUIBlur.mc.currentScreen instanceof GuiModList)) {
                if (GUIBlur.mc.entityRenderer.getShaderGroup() != null) {
                    GUIBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }
            } else if (OpenGlHelper.shadersSupported && GUIBlur.mc.getRenderViewEntity() instanceof EntityPlayer) {
                if (GUIBlur.mc.entityRenderer.getShaderGroup() != null) {
                    GUIBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
                }

                try {
                    GUIBlur.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else if (GUIBlur.mc.entityRenderer.getShaderGroup() != null && GUIBlur.mc.currentScreen == null) {
                GUIBlur.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }
        }

    }
}
