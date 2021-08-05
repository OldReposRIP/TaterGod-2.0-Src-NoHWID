package club.tater.tatergod.util;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class HoleFillUtil {

    public static final List blackList = Arrays.asList(new Block[] { Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE});
    public static final List shulkerList = Arrays.asList(new Block[] { Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static FMLCommonHandler fmlHandler = FMLCommonHandler.instance();
    private static Entity player = HoleFillUtil.mc.player;

    public static void placeBlockScaffold(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(HoleFillUtil.player.posX, HoleFillUtil.player.posY + (double) HoleFillUtil.player.getEyeHeight(), HoleFillUtil.player.posZ);
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbor = pos.offset(side);
            EnumFacing side2 = side.getOpposite();

            if (canBeClicked(neighbor)) {
                Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));

                if (eyesPos.squareDistanceTo(hitVec) <= 18.0625D) {
                    faceVectorPacketInstant(hitVec);
                    processRightClickBlock(neighbor, side2, hitVec);
                    HoleFillUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                    HoleFillUtil.mc.rightClickDelayTimer = 4;
                    return;
                }
            }
        }

    }

    private static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { HoleFillUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - HoleFillUtil.mc.player.rotationYaw), HoleFillUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - HoleFillUtil.mc.player.rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(HoleFillUtil.mc.player.posX, HoleFillUtil.mc.player.posY + (double) HoleFillUtil.mc.player.getEyeHeight(), HoleFillUtil.mc.player.posZ);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);

        HoleFillUtil.mc.player.connection.sendPacket(new Rotation(rotations[0], rotations[1], HoleFillUtil.mc.player.onGround));
    }

    static void processRightClickBlock(BlockPos pos, EnumFacing side, Vec3d hitVec) {
        getPlayerController().processRightClickBlock(HoleFillUtil.mc.player, HoleFillUtil.mc.world, pos, side, hitVec, EnumHand.MAIN_HAND);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static PlayerControllerMP getPlayerController() {
        return Minecraft.getMinecraft().playerController;
    }

    private static IBlockState getState(BlockPos pos) {
        return HoleFillUtil.mc.world.getBlockState(pos);
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            EnumFacing[] aenumfacing = EnumFacing.values();
            int i = aenumfacing.length;

            for (int j = 0; j < i; ++j) {
                EnumFacing side = aenumfacing[j];
                BlockPos neighbour = blockPos.offset(side);

                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = pos.offset(side);

            if (HoleFillUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(HoleFillUtil.mc.world.getBlockState(neighbour), false)) {
                IBlockState blockState = HoleFillUtil.mc.world.getBlockState(neighbour);

                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }

        return null;
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = blockPos.offset(side);

            if (!HoleFillUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }

        return false;
    }
}
