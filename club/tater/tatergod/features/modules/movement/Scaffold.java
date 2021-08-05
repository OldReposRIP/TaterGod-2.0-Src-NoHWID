package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import club.tater.tatergod.util.Util;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold extends Module {

    private final Timer timer = new Timer();
    public Setting rotation = this.register(new Setting("Rotate", Boolean.valueOf(false)));

    public Scaffold() {
        super("Scaffold", "Places Blocks underneath you.", Module.Category.MOVEMENT, true, false, false);
    }

    public void onEnable() {
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent event) {
        if (!this.isOff() && !fullNullCheck() && event.getStage() != 0) {
            if (!Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.timer.reset();
            }

            BlockPos playerBlock;

            if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).add(0, -1, 0))) {
                if (BlockUtil.isValidBlock(playerBlock.add(0, -2, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.UP);
                } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 0))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, -1))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
                } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                    this.place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.NORTH);
                    }

                    this.place(playerBlock.add(1, -1, 1), EnumFacing.EAST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.WEST);
                    }

                    this.place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.SOUTH);
                    }

                    this.place(playerBlock.add(1, -1, 1), EnumFacing.WEST);
                } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
                    if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
                        this.place(playerBlock.add(0, -1, 1), EnumFacing.EAST);
                    }

                    this.place(playerBlock.add(1, -1, 1), EnumFacing.NORTH);
                }
            }

        }
    }

    public void place(BlockPos posI, EnumFacing face) {
        BlockPos pos = posI;

        if (face == EnumFacing.UP) {
            pos = posI.add(0, -1, 0);
        } else if (face == EnumFacing.NORTH) {
            pos = posI.add(0, 0, 1);
        } else if (face == EnumFacing.SOUTH) {
            pos = posI.add(0, 0, -1);
        } else if (face == EnumFacing.EAST) {
            pos = posI.add(-1, 0, 0);
        } else if (face == EnumFacing.WEST) {
            pos = posI.add(1, 0, 0);
        }

        int oldSlot = Scaffold.mc.player.inventory.currentItem;
        int newSlot = -1;

        for (int crouched = 0; crouched < 9; ++crouched) {
            ItemStack angle = Scaffold.mc.player.inventory.getStackInSlot(crouched);

            if (!InventoryUtil.isNull(angle) && angle.getItem() instanceof ItemBlock && Block.getBlockFromItem(angle.getItem()).getDefaultState().isFullBlock()) {
                newSlot = crouched;
                break;
            }
        }

        if (newSlot != -1) {
            boolean flag = false;

            if (!Scaffold.mc.player.isSneaking() && BlockUtil.blackList.contains(Scaffold.mc.world.getBlockState(pos).getBlock())) {
                Scaffold.mc.player.connection.sendPacket(new CPacketEntityAction(Scaffold.mc.player, Action.START_SNEAKING));
                flag = true;
            }

            if (!(Scaffold.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
                Scaffold.mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
                Scaffold.mc.player.inventory.currentItem = newSlot;
                Scaffold.mc.playerController.updateController();
            }

            if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                Scaffold.mc.player.motionX *= 0.3D;
                Scaffold.mc.player.motionZ *= 0.3D;
                Scaffold.mc.player.jump();
                if (this.timer.passedMs(1500L)) {
                    Scaffold.mc.player.motionY = -0.28D;
                    this.timer.reset();
                }
            }

            if (((Boolean) this.rotation.getValue()).booleanValue()) {
                float[] afloat = MathUtil.calcAngle(Scaffold.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() - 0.5F), (double) ((float) pos.getZ() + 0.5F)));

                Scaffold.mc.player.connection.sendPacket(new Rotation(afloat[0], (float) MathHelper.normalizeAngle((int) afloat[1], 360), Scaffold.mc.player.onGround));
            }

            Scaffold.mc.playerController.processRightClickBlock(Scaffold.mc.player, Scaffold.mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
            Scaffold.mc.player.swingArm(EnumHand.MAIN_HAND);
            Scaffold.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            Scaffold.mc.player.inventory.currentItem = oldSlot;
            Scaffold.mc.playerController.updateController();
            if (flag) {
                Scaffold.mc.player.connection.sendPacket(new CPacketEntityAction(Scaffold.mc.player, Action.STOP_SNEAKING));
            }

        }
    }
}
