package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.PushEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam extends Module {

    private static Freecam INSTANCE = new Freecam();
    public Setting speed = this.register(new Setting("Speed", Double.valueOf(1.0D), Double.valueOf(0.1D), Double.valueOf(5.0D)));
    public Setting view = this.register(new Setting("3D", Boolean.valueOf(false)));
    public Setting packet = this.register(new Setting("Packet", Boolean.valueOf(true)));
    public Setting disable = this.register(new Setting("Logout/Off", Boolean.valueOf(true)));
    private AxisAlignedBB oldBoundingBox;
    private EntityOtherPlayerMP entity;
    private Vec3d position;
    private Entity riding;
    private float yaw;
    private float pitch;

    public Freecam() {
        super("Freecam", "Look around freely.", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static Freecam getInstance() {
        if (Freecam.INSTANCE == null) {
            Freecam.INSTANCE = new Freecam();
        }

        return Freecam.INSTANCE;
    }

    private void setInstance() {
        Freecam.INSTANCE = this;
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.oldBoundingBox = Freecam.mc.player.getEntityBoundingBox();
            Freecam.mc.player.setEntityBoundingBox(new AxisAlignedBB(Freecam.mc.player.posX, Freecam.mc.player.posY, Freecam.mc.player.posZ, Freecam.mc.player.posX, Freecam.mc.player.posY, Freecam.mc.player.posZ));
            if (Freecam.mc.player.getRidingEntity() != null) {
                this.riding = Freecam.mc.player.getRidingEntity();
                Freecam.mc.player.dismountRidingEntity();
            }

            this.entity = new EntityOtherPlayerMP(Freecam.mc.world, Freecam.mc.session.getProfile());
            this.entity.copyLocationAndAnglesFrom(Freecam.mc.player);
            this.entity.rotationYaw = Freecam.mc.player.rotationYaw;
            this.entity.rotationYawHead = Freecam.mc.player.rotationYawHead;
            this.entity.inventory.copyInventory(Freecam.mc.player.inventory);
            Freecam.mc.world.addEntityToWorld(69420, this.entity);
            this.position = Freecam.mc.player.getPositionVector();
            this.yaw = Freecam.mc.player.rotationYaw;
            this.pitch = Freecam.mc.player.rotationPitch;
            Freecam.mc.player.noClip = true;
        }

    }

    public void onDisable() {
        if (!fullNullCheck()) {
            Freecam.mc.player.setEntityBoundingBox(this.oldBoundingBox);
            if (this.riding != null) {
                Freecam.mc.player.startRiding(this.riding, true);
            }

            if (this.entity != null) {
                Freecam.mc.world.removeEntity(this.entity);
            }

            if (this.position != null) {
                Freecam.mc.player.setPosition(this.position.x, this.position.y, this.position.z);
            }

            Freecam.mc.player.rotationYaw = this.yaw;
            Freecam.mc.player.rotationPitch = this.pitch;
            Freecam.mc.player.noClip = false;
        }

    }

    public void onUpdate() {
        Freecam.mc.player.noClip = true;
        Freecam.mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        Freecam.mc.player.jumpMovementFactor = ((Double) this.speed.getValue()).floatValue();
        double[] dir = MathUtil.directionSpeed(((Double) this.speed.getValue()).doubleValue());

        if (Freecam.mc.player.movementInput.moveStrafe == 0.0F && Freecam.mc.player.movementInput.moveForward == 0.0F) {
            Freecam.mc.player.motionX = 0.0D;
            Freecam.mc.player.motionZ = 0.0D;
        } else {
            Freecam.mc.player.motionX = dir[0];
            Freecam.mc.player.motionZ = dir[1];
        }

        Freecam.mc.player.setSprinting(false);
        if (((Boolean) this.view.getValue()).booleanValue() && !Freecam.mc.gameSettings.keyBindSneak.isKeyDown() && !Freecam.mc.gameSettings.keyBindJump.isKeyDown()) {
            Freecam.mc.player.motionY = ((Double) this.speed.getValue()).doubleValue() * -MathUtil.degToRad((double) Freecam.mc.player.rotationPitch) * (double) Freecam.mc.player.movementInput.moveForward;
        }

        EntityPlayerSP entityplayersp;

        if (Freecam.mc.gameSettings.keyBindJump.isKeyDown()) {
            entityplayersp = Freecam.mc.player;
            entityplayersp.motionY += ((Double) this.speed.getValue()).doubleValue();
        }

        if (Freecam.mc.gameSettings.keyBindSneak.isKeyDown()) {
            entityplayersp = Freecam.mc.player;
            entityplayersp.motionY -= ((Double) this.speed.getValue()).doubleValue();
        }

    }

    public void onLogout() {
        if (((Boolean) this.disable.getValue()).booleanValue()) {
            this.disable();
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && (event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput)) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPush(PushEvent event) {
        if (event.getStage() == 1) {
            event.setCanceled(true);
        }

    }
}
