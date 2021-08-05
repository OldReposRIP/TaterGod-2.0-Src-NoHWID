package club.tater.tatergod.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ItemUtil {

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = getFirstFacing(pos);

        if (side == null) {
            return isSneaking;
        } else {
            BlockPos neighbour = pos.offset(side);
            EnumFacing opposite = side.getOpposite();
            Vec3d hitVec = (new Vec3d(neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
            Block neighbourBlock = ItemUtil.mc.world.getBlockState(neighbour).getBlock();

            if (!ItemUtil.mc.player.isSneaking()) {
                ItemUtil.mc.player.connection.sendPacket(new CPacketEntityAction(ItemUtil.mc.player, Action.START_SNEAKING));
                ItemUtil.mc.player.setSneaking(true);
                sneaking = true;
            }

            if (rotate) {
                faceVector(hitVec, true);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet);
            ItemUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
            ItemUtil.mc.rightClickDelayTimer = 4;
            return sneaking || isSneaking;
        }
    }

    public static List getPossibleSides(BlockPos pos) {
        ArrayList facings = new ArrayList();
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = pos.offset(side);

            if (ItemUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(ItemUtil.mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = ItemUtil.mc.world.getBlockState(neighbour);

                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }

        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator iterator = getPossibleSides(pos).iterator();

        if (iterator.hasNext()) {
            EnumFacing facing = (EnumFacing) iterator.next();

            return facing;
        } else {
            return null;
        }
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(ItemUtil.mc.player.posX, ItemUtil.mc.player.posY + (double) ItemUtil.mc.player.getEyeHeight(), ItemUtil.mc.player.posZ);
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { ItemUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - ItemUtil.mc.player.rotationYaw), ItemUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - ItemUtil.mc.player.rotationPitch)};
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = getLegitRotations(vec);

        ItemUtil.mc.player.connection.sendPacket(new Rotation(rotations[0], normalizeAngle ? (float) MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], ItemUtil.mc.player.onGround));
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());

            ItemUtil.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            ItemUtil.mc.playerController.processRightClickBlock(ItemUtil.mc.player, ItemUtil.mc.world, pos, direction, vec, hand);
        }

        ItemUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
        ItemUtil.mc.rightClickDelayTimer = 4;
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = ItemUtil.mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY) {
                if (clazz.isInstance(stack.getItem())) {
                    return i;
                }

                if (stack.getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock) stack.getItem()).getBlock();

                    if (clazz.isInstance(block)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    public static void switchToSlot(int slot) {
        ItemUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        ItemUtil.mc.player.inventory.currentItem = slot;
        ItemUtil.mc.playerController.updateController();
    }
}
