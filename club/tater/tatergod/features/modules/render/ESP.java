package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Util;
import java.awt.Color;
import java.util.Iterator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {

    private static ESP INSTANCE = new ESP();
    private final Setting items = this.register(new Setting("Items", Boolean.valueOf(false)));
    private final Setting xporbs = this.register(new Setting("XpOrbs", Boolean.valueOf(false)));
    private final Setting xpbottles = this.register(new Setting("XpBottles", Boolean.valueOf(false)));
    private final Setting pearl = this.register(new Setting("Pearls", Boolean.valueOf(false)));
    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));

    public ESP() {
        super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static ESP getInstance() {
        if (ESP.INSTANCE == null) {
            ESP.INSTANCE = new ESP();
        }

        return ESP.INSTANCE;
    }

    private void setInstance() {
        ESP.INSTANCE = this;
    }

    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        Iterator iterator;
        Entity entity;

        if (((Boolean) this.items.getValue()).booleanValue()) {
            i = 0;
            iterator = ESP.mc.world.loadedEntityList.iterator();

            while (iterator.hasNext()) {
                entity = (Entity) iterator.next();
                if (entity instanceof EntityItem && ESP.mc.player.getDistanceSq(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                    bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0D - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.renderFilledBox(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }

        if (((Boolean) this.xporbs.getValue()).booleanValue()) {
            i = 0;
            iterator = ESP.mc.world.loadedEntityList.iterator();

            while (iterator.hasNext()) {
                entity = (Entity) iterator.next();
                if (entity instanceof EntityXPOrb && ESP.mc.player.getDistanceSq(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                    bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0D - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.renderFilledBox(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }

        if (((Boolean) this.pearl.getValue()).booleanValue()) {
            i = 0;
            iterator = ESP.mc.world.loadedEntityList.iterator();

            while (iterator.hasNext()) {
                entity = (Entity) iterator.next();
                if (entity instanceof EntityEnderPearl && ESP.mc.player.getDistanceSq(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                    bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0D - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.renderFilledBox(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }

        if (((Boolean) this.xpbottles.getValue()).booleanValue()) {
            i = 0;
            iterator = ESP.mc.world.loadedEntityList.iterator();

            while (iterator.hasNext()) {
                entity = (Entity) iterator.next();
                if (entity instanceof EntityExpBottle && ESP.mc.player.getDistanceSq(entity) < 2500.0D) {
                    interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                    bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0D - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05D - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05D - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1D - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05D - entity.posZ + interp.z);
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.disableDepth();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    GL11.glEnable(2848);
                    GL11.glHint(3154, 4354);
                    GL11.glLineWidth(1.0F);
                    RenderGlobal.renderFilledBox(bb, (float) ((Integer) this.red.getValue()).intValue() / 255.0F, (float) ((Integer) this.green.getValue()).intValue() / 255.0F, (float) ((Integer) this.blue.getValue()).intValue() / 255.0F, (float) ((Integer) this.boxAlpha.getValue()).intValue() / 255.0F);
                    GL11.glDisable(2848);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    RenderUtil.drawBlockOutline(bb, new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), 1.0F);
                    ++i;
                    if (i < 50) {
                        continue;
                    }
                    break;
                }
            }
        }

    }
}
