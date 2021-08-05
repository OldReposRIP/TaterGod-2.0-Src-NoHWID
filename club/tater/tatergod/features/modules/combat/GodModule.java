package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtilll;
import club.tater.tatergod.util.MathUtilll;
import club.tater.tatergod.util.Util;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GodModule extends Module {

    public Setting rotations = this.register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20)));
    public Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(false)));
    public Setting render = this.register(new Setting("Render", Boolean.valueOf(false)));
    public Setting antiIllegal = this.register(new Setting("AntiIllegal", Boolean.valueOf(true)));
    public Setting checkPos = this.register(new Setting("CheckPos", Boolean.valueOf(true)));
    public Setting oneDot15 = this.register(new Setting("1.15", Boolean.valueOf(false)));
    public Setting entitycheck = this.register(new Setting("EntityCheck", Boolean.valueOf(true)));
    public Setting attacks = this.register(new Setting("Attacks", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10)));
    public Setting delay = this.register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(50)));
    private float yaw = 0.0F;
    private float pitch = 0.0F;
    private boolean rotating;
    private int rotationPacketsSpoofed;
    private int highestID = -100000;

    public GodModule() {
        super("GodModule", "Wow", Module.Category.COMBAT, true, false, false);
    }

    public void onToggle() {
        this.resetFields();
        if (GodModule.mc.world != null) {
            this.updateEntityID();
        }

    }

    public void onUpdate() {
        if (((Boolean) this.render.getValue()).booleanValue()) {
            Iterator iterator = GodModule.mc.world.loadedEntityList.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity instanceof EntityEnderCrystal) {
                    entity.setCustomNameTag(String.valueOf(entity.entityId));
                    entity.setAlwaysRenderNameTag(true);
                }
            }
        }

    }

    public void onLogout() {
        this.resetFields();
    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();

            if (GodModule.mc.player.getHeldItem(packet.hand).getItem() instanceof ItemEndCrystal) {
                if (((Boolean) this.checkPos.getValue()).booleanValue() && !BlockUtilll.canPlaceCrystal(packet.position, ((Boolean) this.entitycheck.getValue()).booleanValue(), ((Boolean) this.oneDot15.getValue()).booleanValue()) || this.checkPlayers()) {
                    return;
                }

                this.updateEntityID();

                for (int i = 1; i < ((Integer) this.attacks.getValue()).intValue(); ++i) {
                    this.attackID(packet.position, this.highestID + i);
                }
            }
        }

        if (event.getStage() == 0 && this.rotating && ((Boolean) this.rotate.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer cpacketplayer = (CPacketPlayer) event.getPacket();

            cpacketplayer.yaw = this.yaw;
            cpacketplayer.pitch = this.pitch;
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= ((Integer) this.rotations.getValue()).intValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }

    }

    private void attackID(BlockPos pos, int id) {
        Entity entity = GodModule.mc.world.getEntityByID(id);

        if (entity == null || entity instanceof EntityEnderCrystal) {
            GodModule.AttackThread attackThread = new GodModule.AttackThread(id, pos, ((Integer) this.delay.getValue()).intValue(), this);

            attackThread.start();
        }

    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            this.checkID(((SPacketSpawnObject) event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnExperienceOrb) {
            this.checkID(((SPacketSpawnExperienceOrb) event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnPlayer) {
            this.checkID(((SPacketSpawnPlayer) event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnGlobalEntity) {
            this.checkID(((SPacketSpawnGlobalEntity) event.getPacket()).getEntityId());
        } else if (event.getPacket() instanceof SPacketSpawnPainting) {
            this.checkID(((SPacketSpawnPainting) event.getPacket()).getEntityID());
        } else if (event.getPacket() instanceof SPacketSpawnMob) {
            this.checkID(((SPacketSpawnMob) event.getPacket()).getEntityID());
        }

    }

    private void checkID(int id) {
        if (id > this.highestID) {
            this.highestID = id;
        }

    }

    public void updateEntityID() {
        Iterator iterator = GodModule.mc.world.loadedEntityList.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity.getEntityId() > this.highestID) {
                this.highestID = entity.getEntityId();
            }
        }

    }

    private boolean checkPlayers() {
        if (((Boolean) this.antiIllegal.getValue()).booleanValue()) {
            Iterator iterator = GodModule.mc.world.playerEntities.iterator();

            while (iterator.hasNext()) {
                EntityPlayer player = (EntityPlayer) iterator.next();

                if (this.checkItem(player.getHeldItemMainhand()) || this.checkItem(player.getHeldItemOffhand())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkItem(ItemStack stack) {
        return stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemExpBottle || stack.getItem() == Items.STRING;
    }

    public void rotateTo(BlockPos pos) {
        float[] angle = MathUtilll.calcAngle(GodModule.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d(pos));

        this.yaw = angle[0];
        this.pitch = angle[1];
        this.rotating = true;
    }

    private void resetFields() {
        this.rotating = false;
        this.highestID = -1000000;
    }

    public static class AttackThread extends Thread {

        private final BlockPos pos;
        private final int id;
        private final int delay;
        private final GodModule godModule;

        public AttackThread(int idIn, BlockPos posIn, int delayIn, GodModule godModuleIn) {
            this.id = idIn;
            this.pos = posIn;
            this.delay = delayIn;
            this.godModule = godModuleIn;
        }

        public void run() {
            try {
                this.wait((long) this.delay);
                CPacketUseEntity e = new CPacketUseEntity();

                e.entityId = this.id;
                e.action = Action.ATTACK;
                this.godModule.rotateTo(this.pos.up());
                Util.mc.player.connection.sendPacket(e);
                Util.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }

        }
    }
}
