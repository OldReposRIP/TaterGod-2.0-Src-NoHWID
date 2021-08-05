package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.PushEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {

    private static Velocity INSTANCE = new Velocity();
    public Setting noPush = this.register(new Setting("NoPush", Boolean.valueOf(true)));
    public Setting horizontal = this.register(new Setting("Horizontal", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(100.0F)));
    public Setting vertical = this.register(new Setting("Vertical", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(100.0F)));
    public Setting explosions = this.register(new Setting("Explosions", Boolean.valueOf(true)));
    public Setting bobbers = this.register(new Setting("Bobbers", Boolean.valueOf(true)));
    public Setting water = this.register(new Setting("Water", Boolean.valueOf(false)));
    public Setting blocks = this.register(new Setting("Blocks", Boolean.valueOf(false)));
    public Setting ice = this.register(new Setting("Ice", Boolean.valueOf(false)));

    public Velocity() {
        super("Velocity", "Allows you to control your velocity", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    public static Velocity getINSTANCE() {
        if (Velocity.INSTANCE == null) {
            Velocity.INSTANCE = new Velocity();
        }

        return Velocity.INSTANCE;
    }

    private void setInstance() {
        Velocity.INSTANCE = this;
    }

    public void onUpdate() {}

    public void onDisable() {}

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getStage() == 0 && Velocity.mc.player != null) {
            Packet velocity;

            if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) ((SPacketEntityVelocity) (velocity = event.getPacket()))).getEntityID() == Velocity.mc.player.entityId) {
                if (((Float) this.horizontal.getValue()).floatValue() == 0.0F && ((Float) this.vertical.getValue()).floatValue() == 0.0F) {
                    event.setCanceled(true);
                    return;
                }

                ((SPacketEntityVelocity) velocity).motionX = (int) ((float) ((SPacketEntityVelocity) velocity).motionX * ((Float) this.horizontal.getValue()).floatValue());
                ((SPacketEntityVelocity) velocity).motionY = (int) ((float) ((SPacketEntityVelocity) velocity).motionY * ((Float) this.vertical.getValue()).floatValue());
                ((SPacketEntityVelocity) velocity).motionZ = (int) ((float) ((SPacketEntityVelocity) velocity).motionZ * ((Float) this.horizontal.getValue()).floatValue());
            }

            Entity entity;
            SPacketEntityStatus packet;

            if (event.getPacket() instanceof SPacketEntityStatus && ((Boolean) this.bobbers.getValue()).booleanValue() && (packet = (SPacketEntityStatus) event.getPacket()).getOpCode() == 31 && (entity = packet.getEntity(Velocity.mc.world)) instanceof EntityFishHook) {
                EntityFishHook fishHook = (EntityFishHook) entity;

                if (fishHook.caughtEntity == Velocity.mc.player) {
                    event.setCanceled(true);
                }
            }

            if (((Boolean) this.explosions.getValue()).booleanValue() && event.getPacket() instanceof SPacketExplosion) {
                if (((Float) this.horizontal.getValue()).floatValue() == 0.0F && ((Float) this.vertical.getValue()).floatValue() == 0.0F) {
                    event.setCanceled(true);
                    return;
                }

                velocity = event.getPacket();
                ((SPacketExplosion) velocity).motionX *= ((Float) this.horizontal.getValue()).floatValue();
                ((SPacketExplosion) velocity).motionY *= ((Float) this.vertical.getValue()).floatValue();
                ((SPacketExplosion) velocity).motionZ *= ((Float) this.horizontal.getValue()).floatValue();
            }
        }

    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 0 && ((Boolean) this.noPush.getValue()).booleanValue() && event.entity.equals(Velocity.mc.player)) {
            if (((Float) this.horizontal.getValue()).floatValue() == 0.0F && ((Float) this.vertical.getValue()).floatValue() == 0.0F) {
                event.setCanceled(true);
                return;
            }

            event.x = -event.x * (double) ((Float) this.horizontal.getValue()).floatValue();
            event.y = -event.y * (double) ((Float) this.vertical.getValue()).floatValue();
            event.z = -event.z * (double) ((Float) this.horizontal.getValue()).floatValue();
        } else if (event.getStage() == 1 && ((Boolean) this.blocks.getValue()).booleanValue()) {
            event.setCanceled(true);
        } else if (event.getStage() == 2 && ((Boolean) this.water.getValue()).booleanValue() && Velocity.mc.player != null && Velocity.mc.player.equals(event.entity)) {
            event.setCanceled(true);
        }

    }

    public String getDisplayInfo() {
        return "H" + this.horizontal.getValue() + "%V" + this.vertical.getValue() + "%";
    }
}
