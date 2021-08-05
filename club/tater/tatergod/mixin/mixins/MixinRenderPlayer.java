package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.modules.render.TexturedChams;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderPlayer.class})
public class MixinRenderPlayer {

    @Inject(
        method = { "renderEntityName"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void renderEntityNameHook(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (Tater.moduleManager.isModuleEnabled("NameTags")) {
            info.cancel();
        }

    }

    @Overwrite
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        if (Tater.moduleManager.isModuleEnabled("TexturedChams")) {
            GL11.glColor4f((float) ((Integer) TexturedChams.red.getValue()).intValue() / 255.0F, (float) ((Integer) TexturedChams.green.getValue()).intValue() / 255.0F, (float) ((Integer) TexturedChams.blue.getValue()).intValue() / 255.0F, (float) ((Integer) TexturedChams.alpha.getValue()).intValue() / 255.0F);
            return new ResourceLocation("minecraft:steve_skin1.png");
        } else {
            return entity.getLocationSkin();
        }
    }
}
