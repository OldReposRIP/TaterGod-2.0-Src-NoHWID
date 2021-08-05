package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.Render2DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Util;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class ArrowESP extends Module {

    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting radius = this.register(new Setting("Placement", Integer.valueOf(45), Integer.valueOf(10), Integer.valueOf(200)));
    private final Setting size = this.register(new Setting("Size", Float.valueOf(10.0F), Float.valueOf(5.0F), Float.valueOf(25.0F)));
    private final Setting outline = this.register(new Setting("Outline", Boolean.valueOf(true)));
    private final Setting outlineWidth = this.register(new Setting("Outline-Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
    private final Setting fadeDistance = this.register(new Setting("Range", Integer.valueOf(100), Integer.valueOf(10), Integer.valueOf(200)));
    private final Setting invisibles = this.register(new Setting("Invisibles", Boolean.valueOf(false)));
    private final ArrowESP.EntityListener entityListener = new ArrowESP.EntityListener(null);

    public ArrowESP() {
        super("ArrowESP", "Arrow tracers ", Module.Category.RENDER, true, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        this.entityListener.render();
        ArrowESP.mc.world.loadedEntityList.forEach(accept<invokedynamic>(this));
    }

    private boolean isOnScreen(Vec3d pos) {
        if (pos.x <= -1.0D) {
            return false;
        } else if (pos.y >= 1.0D) {
            return false;
        } else if (pos.x <= -1.0D) {
            return false;
        } else if (pos.z >= 1.0D) {
            return false;
        } else {
            int n = ArrowESP.mc.gameSettings.guiScale == 0 ? 1 : ArrowESP.mc.gameSettings.guiScale;

            if (pos.x / (double) n < 0.0D) {
                return false;
            } else {
                int n2 = ArrowESP.mc.gameSettings.guiScale == 0 ? 1 : ArrowESP.mc.gameSettings.guiScale;

                if (pos.x / (double) n2 > (double) Display.getWidth()) {
                    return false;
                } else {
                    int n3 = ArrowESP.mc.gameSettings.guiScale == 0 ? 1 : ArrowESP.mc.gameSettings.guiScale;

                    if (pos.y / (double) n3 < 0.0D) {
                        return false;
                    } else {
                        int n4 = ArrowESP.mc.gameSettings.guiScale == 0 ? 1 : ArrowESP.mc.gameSettings.guiScale;

                        return pos.y / (double) n4 <= (double) Display.getHeight();
                    }
                }
            }
        }
    }

    private boolean isValid(EntityPlayer entity) {
        return entity != ArrowESP.mc.player && (!entity.isInvisible() || ((Boolean) this.invisibles.getValue()).booleanValue()) && entity.isEntityAlive();
    }

    private float getRotations(EntityLivingBase ent) {
        double x = ent.posX - ArrowESP.mc.player.posX;
        double z = ent.posZ - ArrowESP.mc.player.posZ;

        return (float) (-(Math.atan2(x, z) * 57.29577951308232D));
    }

    private void lambda$onRender2D$0(Entity o) {
        if (o instanceof EntityPlayer && this.isValid((EntityPlayer) o)) {
            EntityPlayer entity = (EntityPlayer) o;
            Vec3d pos = (Vec3d) this.entityListener.getEntityLowerBounds().get(entity);

            if (pos != null && !this.isOnScreen(pos) && !RenderUtil.isInViewFrustrum((Entity) entity)) {
                Color color = EntityUtil.getColor(entity, ((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), (int) MathHelper.clamp(255.0F - 255.0F / (float) ((Integer) this.fadeDistance.getValue()).intValue() * ArrowESP.mc.player.getDistance(entity), 100.0F, 255.0F), true);
                int x = Display.getWidth() / 2 / (ArrowESP.mc.gameSettings.guiScale == 0 ? 1 : ArrowESP.mc.gameSettings.guiScale);
                int y = Display.getHeight() / 2 / (ArrowESP.mc.gameSettings.guiScale == 0 ? 1 : ArrowESP.mc.gameSettings.guiScale);
                float yaw = this.getRotations(entity) - ArrowESP.mc.player.rotationYaw;

                GL11.glTranslatef((float) x, (float) y, 0.0F);
                GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef((float) (-x), (float) (-y), 0.0F);
                RenderUtil.drawTracerPointer((float) x, (float) (y - ((Integer) this.radius.getValue()).intValue()), ((Float) this.size.getValue()).floatValue(), 2.0F, 1.0F, ((Boolean) this.outline.getValue()).booleanValue(), ((Float) this.outlineWidth.getValue()).floatValue(), color.getRGB());
                GL11.glTranslatef((float) x, (float) y, 0.0F);
                GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef((float) (-x), (float) (-y), 0.0F);
            }
        }

    }

    private static class EntityListener {

        private final Map entityUpperBounds;
        private final Map entityLowerBounds;

        private EntityListener() {
            this.entityUpperBounds = Maps.newHashMap();
            this.entityLowerBounds = Maps.newHashMap();
        }

        private void render() {
            if (!this.entityUpperBounds.isEmpty()) {
                this.entityUpperBounds.clear();
            }

            if (!this.entityLowerBounds.isEmpty()) {
                this.entityLowerBounds.clear();
            }

            Iterator iterator = Util.mc.world.loadedEntityList.iterator();

            while (iterator.hasNext()) {
                Entity e = (Entity) iterator.next();
                Vec3d bound = this.getEntityRenderPosition(e);

                bound.add(new Vec3d(0.0D, (double) e.height + 0.2D, 0.0D));
                Vec3d upperBounds = RenderUtil.to2D(bound.x, bound.y, bound.z);
                Vec3d lowerBounds = RenderUtil.to2D(bound.x, bound.y - 2.0D, bound.z);

                if (upperBounds != null && lowerBounds != null) {
                    this.entityUpperBounds.put(e, upperBounds);
                    this.entityLowerBounds.put(e, lowerBounds);
                }
            }

        }

        private Vec3d getEntityRenderPosition(Entity entity) {
            double partial = (double) Util.mc.timer.renderPartialTicks;
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - Util.mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - Util.mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - Util.mc.getRenderManager().viewerPosZ;

            return new Vec3d(x, y, z);
        }

        public Map getEntityLowerBounds() {
            return this.entityLowerBounds;
        }

        EntityListener(Object x0) {
            this();
        }
    }
}
