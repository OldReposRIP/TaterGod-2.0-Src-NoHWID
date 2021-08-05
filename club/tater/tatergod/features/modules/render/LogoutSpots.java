package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.ConnectionEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Util;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LogoutSpots extends Module {

    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting scaleing = this.register(new Setting("Scale", Boolean.valueOf(false)));
    private final Setting scaling = this.register(new Setting("Size", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
    private final Setting factor = this.register(new Setting("Factor", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(1.0F), test<invokedynamic>(this)));
    private final Setting smartScale = this.register(new Setting("SmartScale", Boolean.valueOf(false), test<invokedynamic>(this)));
    private final Setting rect = this.register(new Setting("Rectangle", Boolean.valueOf(true)));
    private final Setting coords = this.register(new Setting("Coords", Boolean.valueOf(true)));
    private final Setting notification = this.register(new Setting("Notification", Boolean.valueOf(true)));
    private final List spots = new CopyOnWriteArrayList();
    public Setting range = this.register(new Setting("Range", Float.valueOf(300.0F), Float.valueOf(50.0F), Float.valueOf(500.0F)));
    public Setting message = this.register(new Setting("Message", Boolean.valueOf(false)));

    public LogoutSpots() {
        super("LogoutSpots", "Renders LogoutSpots", Module.Category.RENDER, true, false, false);
    }

    public void onLogout() {
        this.spots.clear();
    }

    public void onDisable() {
        this.spots.clear();
    }

    public void onRender3D(Render3DEvent event) {
        if (!this.spots.isEmpty()) {
            List list = this.spots;

            synchronized (list) {
                this.spots.forEach(accept<invokedynamic>(this, event));
            }
        }

    }

    public void onUpdate() {
        if (!fullNullCheck()) {
            this.spots.removeIf(test<invokedynamic>(this));
        }

    }

    @SubscribeEvent
    public void onConnection(ConnectionEvent event) {
        if (event.getStage() == 0) {
            UUID entity = event.getUuid();
            EntityPlayer uuid = LogoutSpots.mc.world.getPlayerEntityByUUID(entity);

            if (uuid != null && ((Boolean) this.message.getValue()).booleanValue()) {
                Command.sendMessage("§a" + uuid.getName() + " just logged in" + (((Boolean) this.coords.getValue()).booleanValue() ? " at (" + uuid.posX + ", " + uuid.posY + ", " + uuid.posZ + ")!" : "!"));
            }

            this.spots.removeIf(test<invokedynamic>(event));
        } else if (event.getStage() == 1) {
            EntityPlayer entity1 = event.getEntity();
            UUID uuid1 = event.getUuid();
            String name = event.getName();

            if (((Boolean) this.message.getValue()).booleanValue()) {
                Command.sendMessage("§c" + event.getName() + " just logged out" + (((Boolean) this.coords.getValue()).booleanValue() ? " at (" + entity1.posX + ", " + entity1.posY + ", " + entity1.posZ + ")!" : "!"));
            }

            if (name != null && entity1 != null && uuid1 != null) {
                this.spots.add(new LogoutSpots.LogoutPos(name, uuid1, entity1));
            }
        }

    }

    private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
        double y = yi + 0.7D;
        Entity camera = Util.mc.getRenderViewEntity();

        assert camera != null;

        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;

        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = name + " XYZ: " + (int) xPos + ", " + (int) yPos + ", " + (int) zPos;
        double distance = camera.getDistance(x + LogoutSpots.mc.getRenderManager().viewerPosX, y + LogoutSpots.mc.getRenderManager().viewerPosY, z + LogoutSpots.mc.getRenderManager().viewerPosZ);
        int width = this.renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018D + (double) ((Float) this.scaling.getValue()).floatValue() * distance * (double) ((Float) this.factor.getValue()).floatValue()) / 1000.0D;

        if (distance <= 8.0D && ((Boolean) this.smartScale.getValue()).booleanValue()) {
            scale = 0.0245D;
        }

        if (!((Boolean) this.scaleing.getValue()).booleanValue()) {
            scale = (double) ((Float) this.scaling.getValue()).floatValue() / 100.0D;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) y + 1.4F, (float) z);
        GlStateManager.rotate(-LogoutSpots.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        float f = LogoutSpots.mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;

        GlStateManager.rotate(LogoutSpots.mc.getRenderManager().playerViewX, f, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (((Boolean) this.rect.getValue()).booleanValue()) {
            RenderUtil.drawRect((float) (-width - 2), (float) (-(this.renderer.getFontHeight() + 1)), (float) width + 2.0F, 1.5F, 1426063360);
        }

        GlStateManager.disableBlend();
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
        GlStateManager.popMatrix();
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    private static boolean lambda$onConnection$4(ConnectionEvent event, LogoutSpots.LogoutPos pos) {
        return pos.getName().equalsIgnoreCase(event.getName());
    }

    private boolean lambda$onUpdate$3(LogoutSpots.LogoutPos spot) {
        return LogoutSpots.mc.player.getDistanceSq(spot.getEntity()) >= MathUtil.square((double) ((Float) this.range.getValue()).floatValue());
    }

    private void lambda$onRender3D$2(Render3DEvent event, LogoutSpots.LogoutPos spot) {
        if (spot.getEntity() != null) {
            AxisAlignedBB bb = RenderUtil.interpolateAxis(spot.getEntity().getEntityBoundingBox());
            double x = this.interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, event.getPartialTicks()) - LogoutSpots.mc.getRenderManager().renderPosX;
            double y = this.interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, event.getPartialTicks()) - LogoutSpots.mc.getRenderManager().renderPosY;
            double z = this.interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, event.getPartialTicks()) - LogoutSpots.mc.getRenderManager().renderPosZ;

            this.renderNameTag(spot.getName(), x, y, z, event.getPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
        }

    }

    private boolean lambda$new$1(Object v) {
        return ((Boolean) this.scaleing.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return ((Boolean) this.scaleing.getValue()).booleanValue();
    }

    private static class LogoutPos {

        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }

        public String getName() {
            return this.name;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public EntityPlayer getEntity() {
            return this.entity;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}
