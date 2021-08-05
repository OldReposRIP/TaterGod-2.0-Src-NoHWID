package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.features.modules.render.NoRender;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ ItemRenderer.class})
public abstract class MixinItemRenderer2 {

    private final boolean injection = true;

    @Inject(
        method = { "renderFireInFirstPerson"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().fire.getValue()).booleanValue()) {
            info.cancel();
        }

    }

    @Inject(
        method = { "renderSuffocationOverlay"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void renderSuffocationOverlay(CallbackInfo ci) {
        if (NoRender.getInstance().isOn() && ((Boolean) NoRender.getInstance().blocks.getValue()).booleanValue()) {
            ci.cancel();
        }

    }
}
