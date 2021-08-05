package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.Util;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Shaders extends Module {

    private static final Shaders INSTANCE = new Shaders();
    public Setting shader;

    public Shaders() {
        super("Shaders", "i dont even know anymore", Module.Category.RENDER, false, false, false);
        this.shader = this.register(new Setting("Mode", Shaders.Mode.green));
    }

    public void onUpdate() {
        if (OpenGlHelper.shadersSupported && Util.mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (Util.mc.entityRenderer.getShaderGroup() != null) {
                Util.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            }

            try {
                Util.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/" + this.shader.getValue() + ".json"));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else if (Util.mc.entityRenderer.getShaderGroup() != null && Util.mc.currentScreen == null) {
            Util.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }

    }

    public String getDisplayInfo() {
        return this.shader.currentEnumName();
    }

    public void onDisable() {
        if (Util.mc.entityRenderer.getShaderGroup() != null) {
            Util.mc.entityRenderer.getShaderGroup().deleteShaderGroup();
        }

    }

    public static enum Mode {

        notch, antialias, art, bits, blobs, blobs2, blur, bumpy, color_convolve, creeper, deconverge, desaturate, entity_outline, flip, fxaa, green, invert, ntsc, outline, pencil, phosphor, scan_pincusion, sobel, spider, wobble;
    }
}
