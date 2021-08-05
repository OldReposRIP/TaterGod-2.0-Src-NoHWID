package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.modules.client.FontMod;
import club.tater.tatergod.features.modules.client.NickHider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ FontRenderer.class})
public abstract class MixinFontRenderer {

    @Shadow
    protected abstract int renderString(String s, float f, float f1, int i, boolean flag);

    @Shadow
    protected abstract void renderStringAtPos(String s, boolean flag);

    @Redirect(
        method = { "renderString(Ljava/lang/String;FFIZ)I"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"
            )
    )
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
        if (NickHider.getInstance().isOn()) {
            this.renderStringAtPos(text.replace(Minecraft.getMinecraft().getSession().getUsername(), NickHider.getInstance().NameString.getValueAsString()), shadow);
        } else {
            this.renderStringAtPos(text, shadow);
        }

    }

    @Inject(
        method = { "drawString(Ljava/lang/String;FFIZ)I"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void renderStringHook(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable info) {
        if (FontMod.getInstance().isOn() && ((Boolean) ((FontMod) Tater.moduleManager.getModuleT(FontMod.class)).customAll.getValue()).booleanValue() && Tater.textManager != null) {
            float result = Tater.textManager.drawString(text, x, y, color, dropShadow);

            info.setReturnValue(Integer.valueOf((int) result));
        }

    }
}
