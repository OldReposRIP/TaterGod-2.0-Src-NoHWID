package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.features.modules.render.NoRender;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ GuiIngame.class})
public class MixinGuiIngame extends Gui {

    @Inject(
        method = { "renderPortal"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    protected void renderPortalHook(float n, ScaledResolution scaledResolution, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().portal.getValue()).booleanValue()) {
            info.cancel();
        }

    }

    @Inject(
        method = { "renderPumpkinOverlay"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().pumpkin.getValue()).booleanValue()) {
            info.cancel();
        }

    }
}
