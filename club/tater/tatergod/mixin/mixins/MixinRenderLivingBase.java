package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.modules.render.Wireframe;
import club.tater.tatergod.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ RenderLivingBase.class})
public abstract class MixinRenderLivingBase extends Render {

    @Shadow
    private static final Logger LOGGER = LogManager.getLogger();
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected boolean renderMarker;
    float red = 0.0F;
    float green = 0.0F;
    float blue = 0.0F;

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Overwrite
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!MinecraftForge.EVENT_BUS.post(new Pre(entity, (RenderLivingBase) RenderLivingBase.class.cast(this), partialTicks, x, y, z))) {
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
            boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();

            this.mainModel.isRiding = shouldSit;
            this.mainModel.isChild = entity.isChild();

            try {
                float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                float f2 = f1 - f;
                float f8;

                if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase) {
                    EntityLivingBase f7 = (EntityLivingBase) entity.getRidingEntity();

                    f = this.interpolateRotation(f7.prevRenderYawOffset, f7.renderYawOffset, partialTicks);
                    f2 = f1 - f;
                    f8 = MathHelper.wrapDegrees(f2);
                    if (f8 < -85.0F) {
                        f8 = -85.0F;
                    }

                    if (f8 >= 85.0F) {
                        f8 = 85.0F;
                    }

                    f = f1 - f8;
                    if (f8 * f8 > 2500.0F) {
                        f += f8 * 0.2F;
                    }

                    f2 = f1 - f;
                }

                float f71 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

                this.renderLivingAt(entity, x, y, z);
                f8 = this.handleRotationFloat(entity, partialTicks);
                this.applyRotations(entity, f8, f, partialTicks);
                float f4 = this.prepareScale(entity, partialTicks);
                float f5 = 0.0F;
                float f6 = 0.0F;

                if (!entity.isRiding()) {
                    f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                    f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
                    if (entity.isChild()) {
                        f6 *= 3.0F;
                    }

                    if (f5 > 1.0F) {
                        f5 = 1.0F;
                    }

                    f2 = f1 - f;
                }

                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
                this.mainModel.setRotationAngles(f6, f5, f8, f2, f71, f4, entity);
                boolean flag1;

                if (this.renderOutlines) {
                    flag1 = this.setScoreTeamColor(entity);
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                    if (!this.renderMarker) {
                        this.renderModel(entity, f6, f5, f8, f2, f71, f4);
                    }

                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                        this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f71, f4);
                    }

                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                    if (flag1) {
                        this.unsetScoreTeamColor();
                    }
                } else {
                    if (Wireframe.getInstance().isOn() && ((Boolean) Wireframe.getInstance().players.getValue()).booleanValue() && entity instanceof EntityPlayer && ((Wireframe.RenderMode) Wireframe.getInstance().mode.getValue()).equals(Wireframe.RenderMode.SOLID)) {
                        this.red = (float) ((Integer) Wireframe.getInstance().red.getValue()).intValue() / 255.0F;
                        this.green = (float) ((Integer) Wireframe.getInstance().green.getValue()).intValue() / 255.0F;
                        this.blue = (float) ((Integer) Wireframe.getInstance().blue.getValue()).intValue() / 255.0F;
                        GlStateManager.pushMatrix();
                        GL11.glPushAttrib(1048575);
                        GL11.glDisable(3553);
                        GL11.glDisable(2896);
                        GL11.glEnable(2848);
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        GL11.glDisable(2929);
                        GL11.glDepthMask(false);
                        if (!Tater.friendManager.isFriend(entity.getName()) && entity != Minecraft.getMinecraft().player) {
                            GL11.glColor4f(((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getRed() / 255.0F : this.red, ((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getGreen() / 255.0F : this.green, ((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getBlue() / 255.0F : this.blue, ((Float) Wireframe.getInstance().alpha.getValue()).floatValue() / 255.0F);
                        } else {
                            GL11.glColor4f(0.0F, 191.0F, 255.0F, ((Float) Wireframe.getInstance().alpha.getValue()).floatValue() / 255.0F);
                        }

                        this.renderModel(entity, f6, f5, f8, f2, f71, f4);
                        GL11.glDisable(2896);
                        GL11.glEnable(2929);
                        GL11.glDepthMask(true);
                        if (!Tater.friendManager.isFriend(entity.getName()) && entity != Minecraft.getMinecraft().player) {
                            GL11.glColor4f(((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getRed() / 255.0F : this.red, ((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getGreen() / 255.0F : this.green, ((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getBlue() / 255.0F : this.blue, ((Float) Wireframe.getInstance().alpha.getValue()).floatValue() / 255.0F);
                        } else {
                            GL11.glColor4f(0.0F, 191.0F, 255.0F, ((Float) Wireframe.getInstance().alpha.getValue()).floatValue() / 255.0F);
                        }

                        this.renderModel(entity, f6, f5, f8, f2, f71, f4);
                        GL11.glEnable(2896);
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }

                    flag1 = this.setDoRenderBrightness(entity, partialTicks);
                    if (!(entity instanceof EntityPlayer) || Wireframe.getInstance().isOn() && ((Wireframe.RenderMode) Wireframe.getInstance().mode.getValue()).equals(Wireframe.RenderMode.WIREFRAME) && ((Boolean) Wireframe.getInstance().playerModel.getValue()).booleanValue() || Wireframe.getInstance().isOff()) {
                        this.renderModel(entity, f6, f5, f8, f2, f71, f4);
                    }

                    if (flag1) {
                        this.unsetBrightness();
                    }

                    GlStateManager.depthMask(true);
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
                        this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f71, f4);
                    }

                    if (Wireframe.getInstance().isOn() && ((Boolean) Wireframe.getInstance().players.getValue()).booleanValue() && entity instanceof EntityPlayer && ((Wireframe.RenderMode) Wireframe.getInstance().mode.getValue()).equals(Wireframe.RenderMode.WIREFRAME)) {
                        this.red = (float) ((Integer) Wireframe.getInstance().red.getValue()).intValue() / 255.0F;
                        this.green = (float) ((Integer) Wireframe.getInstance().green.getValue()).intValue() / 255.0F;
                        this.blue = (float) ((Integer) Wireframe.getInstance().blue.getValue()).intValue() / 255.0F;
                        GlStateManager.pushMatrix();
                        GL11.glPushAttrib(1048575);
                        GL11.glPolygonMode(1032, 6913);
                        GL11.glDisable(3553);
                        GL11.glDisable(2896);
                        GL11.glDisable(2929);
                        GL11.glEnable(2848);
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        if (!Tater.friendManager.isFriend(entity.getName()) && entity != Minecraft.getMinecraft().player) {
                            GL11.glColor4f(((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getRed() / 255.0F : this.red, ((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getGreen() / 255.0F : this.green, ((Boolean) Wireframe.getInstance().rainbow.getValue()).booleanValue() ? (float) ColorUtil.rainbow(((Integer) Wireframe.getInstance().rainbowHue.getValue()).intValue()).getBlue() / 255.0F : this.blue, ((Float) Wireframe.getInstance().alpha.getValue()).floatValue() / 255.0F);
                        } else {
                            GL11.glColor4f(0.0F, 191.0F, 255.0F, ((Float) Wireframe.getInstance().alpha.getValue()).floatValue() / 255.0F);
                        }

                        GL11.glLineWidth(((Float) Wireframe.getInstance().lineWidth.getValue()).floatValue());
                        this.renderModel(entity, f6, f5, f8, f2, f71, f4);
                        GL11.glEnable(2896);
                        GlStateManager.popAttrib();
                        GlStateManager.popMatrix();
                    }
                }

                GlStateManager.disableRescaleNormal();
            } catch (Exception exception) {
                MixinRenderLivingBase.LOGGER.error("Couldn\'t render entity", exception);
            }

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            MinecraftForge.EVENT_BUS.post(new Post(entity, (RenderLivingBase) RenderLivingBase.class.cast(this), partialTicks, x, y, z));
        }

    }

    @Shadow
    protected abstract boolean isVisible(EntityLivingBase entitylivingbase);

    @Shadow
    protected abstract float getSwingProgress(EntityLivingBase entitylivingbase, float f);

    @Shadow
    protected abstract float interpolateRotation(float f, float f1, float f2);

    @Shadow
    protected abstract float handleRotationFloat(EntityLivingBase entitylivingbase, float f);

    @Shadow
    protected abstract void applyRotations(EntityLivingBase entitylivingbase, float f, float f1, float f2);

    @Shadow
    public abstract float prepareScale(EntityLivingBase entitylivingbase, float f);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract boolean setScoreTeamColor(EntityLivingBase entitylivingbase);

    @Shadow
    protected abstract void renderLivingAt(EntityLivingBase entitylivingbase, double d0, double d1, double d2);

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract void renderModel(EntityLivingBase entitylivingbase, float f, float f1, float f2, float f3, float f4, float f5);

    @Shadow
    protected abstract void renderLayers(EntityLivingBase entitylivingbase, float f, float f1, float f2, float f3, float f4, float f5, float f6);

    @Shadow
    protected abstract boolean setDoRenderBrightness(EntityLivingBase entitylivingbase, float f);
}
