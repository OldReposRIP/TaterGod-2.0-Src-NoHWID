package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class BowSpam extends Module {

    private final Timer timer = new Timer();
    public Setting mode;
    public Setting bowbomb;
    public Setting allowOffhand;
    public Setting ticks;
    public Setting delay;
    public Setting tpsSync;
    public Setting autoSwitch;
    public Setting onlyWhenSave;
    public Setting targetMode;
    public Setting range;
    public Setting health;
    public Setting ownHealth;
    private boolean offhand;
    private boolean switched;
    private int lastHotbarSlot;

    public BowSpam() {
        super("BowSpam", "Spams your bow", Module.Category.COMBAT, true, false, false);
        this.mode = this.register(new Setting("Mode", BowSpam.Mode.FAST));
        this.bowbomb = this.register(new Setting("BowBomb", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.allowOffhand = this.register(new Setting("Offhand", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.ticks = this.register(new Setting("Ticks", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(20), test<invokedynamic>(this), "Speed"));
        this.delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this), "Speed"));
        this.tpsSync = this.register(new Setting("TpsSync", Boolean.valueOf(false)));
        this.autoSwitch = this.register(new Setting("AutoSwitch", Boolean.valueOf(false)));
        this.onlyWhenSave = this.register(new Setting("OnlyWhenSave", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.targetMode = this.register(new Setting("Target", BowSpam.Target.LOWEST, test<invokedynamic>(this)));
        this.range = this.register(new Setting("Range", Float.valueOf(3.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), test<invokedynamic>(this), "Range of the target"));
        this.health = this.register(new Setting("Lethal", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), test<invokedynamic>(this), "When should it switch?"));
        this.ownHealth = this.register(new Setting("OwnHealth", Float.valueOf(20.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), test<invokedynamic>(this), "Own Health."));
        this.offhand = false;
        this.switched = false;
        this.lastHotbarSlot = -1;
    }

    public void onEnable() {
        this.lastHotbarSlot = BowSpam.mc.player.inventory.currentItem;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            if (((Boolean) this.autoSwitch.getValue()).booleanValue() && InventoryUtil.findHotbarBlock(ItemBow.class) != -1 && ((Float) this.ownHealth.getValue()).floatValue() <= EntityUtil.getHealth(BowSpam.mc.player) && (!((Boolean) this.onlyWhenSave.getValue()).booleanValue() || EntityUtil.isSafe(BowSpam.mc.player))) {
                EntityPlayer f2 = this.getTarget();

                if (f2 != null && (!((AutoCrystal) Tater.moduleManager.getModuleByClass(AutoCrystal.class)).isOn() || !InventoryUtil.holdingItem(ItemEndCrystal.class))) {
                    Vec3d f3 = f2.getPositionVector();
                    double xPos = f3.x;
                    double yPos = f3.y;
                    double zPos = f3.z;

                    if (BowSpam.mc.player.canEntityBeSeen(f2)) {
                        yPos += (double) f2.eyeHeight;
                    } else {
                        if (!EntityUtil.canEntityFeetBeSeen(f2)) {
                            return;
                        }

                        yPos += 0.1D;
                    }

                    if (!(BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)) {
                        this.lastHotbarSlot = BowSpam.mc.player.inventory.currentItem;
                        InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
                        BowSpam.mc.gameSettings.keyBindUseItem.pressed = true;
                        this.switched = true;
                    }

                    Tater.rotationManager.lookAtVec3d(xPos, yPos, zPos);
                    if (BowSpam.mc.player.getHeldItemMainhand().getItem() instanceof ItemBow) {
                        this.switched = true;
                    }
                }
            } else if (event.getStage() == 0 && this.switched && this.lastHotbarSlot != -1) {
                InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
                BowSpam.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
                this.switched = false;
            } else {
                BowSpam.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }

            if (this.mode.getValue() == BowSpam.Mode.FAST && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.isHandActive()) {
                float f = (float) BowSpam.mc.player.getItemInUseMaxCount();
                float f21 = (float) ((Integer) this.ticks.getValue()).intValue();
                float f31 = ((Boolean) this.tpsSync.getValue()).booleanValue() ? Tater.serverManager.getTpsFactor() : 1.0F;

                if (f >= f21 * f31) {
                    BowSpam.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                    BowSpam.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                    BowSpam.mc.player.stopActiveHand();
                }
            }

        }
    }

    public void onUpdate() {
        this.offhand = BowSpam.mc.player.getHeldItemOffhand().getItem() == Items.BOW && ((Boolean) this.allowOffhand.getValue()).booleanValue();
        switch ((BowSpam.Mode) this.mode.getValue()) {
        case AUTORELEASE:
            if ((this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && this.timer.passedMs((long) ((int) ((float) ((Integer) this.delay.getValue()).intValue() * (((Boolean) this.tpsSync.getValue()).booleanValue() ? Tater.serverManager.getTpsFactor() : 1.0F))))) {
                BowSpam.mc.playerController.onStoppedUsingItem(BowSpam.mc.player);
                this.timer.reset();
            }
            break;

        case BOWBOMB:
            if ((this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.isHandActive()) {
                float f = (float) BowSpam.mc.player.getItemInUseMaxCount();
                float f2 = (float) ((Integer) this.ticks.getValue()).intValue();
                float f3 = ((Boolean) this.tpsSync.getValue()).booleanValue() ? Tater.serverManager.getTpsFactor() : 1.0F;

                if (f >= f2 * f3) {
                    BowSpam.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, BowSpam.mc.player.getHorizontalFacing()));
                    BowSpam.mc.player.connection.sendPacket(new PositionRotation(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 0.0624D, BowSpam.mc.player.posZ, BowSpam.mc.player.rotationYaw, BowSpam.mc.player.rotationPitch, false));
                    BowSpam.mc.player.connection.sendPacket(new PositionRotation(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 999.0D, BowSpam.mc.player.posZ, BowSpam.mc.player.rotationYaw, BowSpam.mc.player.rotationPitch, true));
                    BowSpam.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
                    BowSpam.mc.player.stopActiveHand();
                }
            }
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && ((Boolean) this.bowbomb.getValue()).booleanValue() && this.mode.getValue() != BowSpam.Mode.BOWBOMB && event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) event.getPacket()).getAction() == Action.RELEASE_USE_ITEM && (this.offhand || BowSpam.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && BowSpam.mc.player.getItemInUseMaxCount() >= 20 && !BowSpam.mc.player.onGround) {
            BowSpam.mc.player.connection.sendPacket(new Position(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 0.10000000149011612D, BowSpam.mc.player.posZ, false));
            BowSpam.mc.player.connection.sendPacket(new Position(BowSpam.mc.player.posX, BowSpam.mc.player.posY - 10000.0D, BowSpam.mc.player.posZ, true));
        }

    }

    private EntityPlayer getTarget() {
        double maxHealth = 36.0D;
        EntityPlayer target = null;
        Iterator iterator = BowSpam.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (player != null && !EntityUtil.isDead(player) && EntityUtil.getHealth(player) <= ((Float) this.health.getValue()).floatValue() && !player.equals(BowSpam.mc.player) && !Tater.friendManager.isFriend(player) && BowSpam.mc.player.getDistanceSq(player) <= MathUtil.square((double) ((Float) this.range.getValue()).floatValue()) && (BowSpam.mc.player.canEntityBeSeen(player) || EntityUtil.canEntityFeetBeSeen(player))) {
                if (target == null) {
                    target = player;
                    maxHealth = (double) EntityUtil.getHealth(player);
                }

                if (this.targetMode.getValue() == BowSpam.Target.CLOSEST && BowSpam.mc.player.getDistanceSq(player) < BowSpam.mc.player.getDistanceSq(target)) {
                    target = player;
                    maxHealth = (double) EntityUtil.getHealth(player);
                }

                if (this.targetMode.getValue() == BowSpam.Target.LOWEST && (double) EntityUtil.getHealth(player) < maxHealth) {
                    target = player;
                    maxHealth = (double) EntityUtil.getHealth(player);
                }
            }
        }

        return target;
    }

    private boolean lambda$new$8(Object v) {
        return ((Boolean) this.autoSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$7(Object v) {
        return ((Boolean) this.autoSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$6(Object v) {
        return ((Boolean) this.autoSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$5(Object v) {
        return ((Boolean) this.autoSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$4(Object v) {
        return ((Boolean) this.autoSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$3(Object v) {
        return this.mode.getValue() == BowSpam.Mode.AUTORELEASE;
    }

    private boolean lambda$new$2(Object v) {
        return this.mode.getValue() == BowSpam.Mode.BOWBOMB || this.mode.getValue() == BowSpam.Mode.FAST;
    }

    private boolean lambda$new$1(Object v) {
        return this.mode.getValue() != BowSpam.Mode.AUTORELEASE;
    }

    private boolean lambda$new$0(Object v) {
        return this.mode.getValue() != BowSpam.Mode.BOWBOMB;
    }

    public static enum Target {

        CLOSEST, LOWEST;
    }

    public static enum Mode {

        FAST, AUTORELEASE, BOWBOMB;
    }
}
