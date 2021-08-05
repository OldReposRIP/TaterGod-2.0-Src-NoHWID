package club.tater.tatergod.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BlockInteractionUtil {

    public static final List blackList = Arrays.asList(new Block[] { Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER});
    public static final List shulkerList = Arrays.asList(new Block[] { Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static BlockInteractionUtil.PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule) {
        IBlockState l_State = BlockInteractionUtil.mc.world.getBlockState(pos);
        boolean l_Replaceable = l_State.getMaterial().isReplaceable();
        boolean l_IsSlabAtBlock = l_State.getBlock() instanceof BlockSlab;

        if (!l_Replaceable && !l_IsSlabAtBlock) {
            return BlockInteractionUtil.PlaceResult.NotReplaceable;
        } else if (!checkForNeighbours(pos)) {
            return BlockInteractionUtil.PlaceResult.Neighbors;
        } else if (p_UseSlabRule && l_IsSlabAtBlock && !l_State.isFullCube()) {
            return BlockInteractionUtil.PlaceResult.CantPlace;
        } else {
            Vec3d eyesPos = new Vec3d(BlockInteractionUtil.mc.player.posX, BlockInteractionUtil.mc.player.posY + (double) BlockInteractionUtil.mc.player.getEyeHeight(), BlockInteractionUtil.mc.player.posZ);
            EnumFacing[] aenumfacing = EnumFacing.values();
            int i = aenumfacing.length;

            for (int j = 0; j < i; ++j) {
                EnumFacing side = aenumfacing[j];
                BlockPos neighbor = pos.offset(side);
                EnumFacing side2 = side.getOpposite();

                if (BlockInteractionUtil.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockInteractionUtil.mc.world.getBlockState(neighbor), false)) {
                    Vec3d hitVec = (new Vec3d(neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));

                    if (eyesPos.distanceTo(hitVec) <= (double) p_Distance) {
                        Block neighborPos = BlockInteractionUtil.mc.world.getBlockState(neighbor).getBlock();
                        boolean activated = neighborPos.onBlockActivated(BlockInteractionUtil.mc.world, pos, BlockInteractionUtil.mc.world.getBlockState(pos), BlockInteractionUtil.mc.player, EnumHand.MAIN_HAND, side, 0.0F, 0.0F, 0.0F);

                        if (BlockInteractionUtil.blackList.contains(neighborPos) || BlockInteractionUtil.shulkerList.contains(neighborPos) || activated) {
                            BlockInteractionUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockInteractionUtil.mc.player, Action.START_SNEAKING));
                        }

                        if (p_Rotate) {
                            faceVectorPacketInstant(hitVec);
                        }

                        EnumActionResult l_Result2 = BlockInteractionUtil.mc.playerController.processRightClickBlock(BlockInteractionUtil.mc.player, BlockInteractionUtil.mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);

                        if (l_Result2 != EnumActionResult.FAIL) {
                            BlockInteractionUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
                            if (activated) {
                                BlockInteractionUtil.mc.player.connection.sendPacket(new CPacketEntityAction(BlockInteractionUtil.mc.player, Action.STOP_SNEAKING));
                            }

                            return BlockInteractionUtil.PlaceResult.Placed;
                        }
                    }
                }
            }

            return BlockInteractionUtil.PlaceResult.CantPlace;
        }
    }

    public static BlockInteractionUtil.ValidResult valid(BlockPos pos) {
        if (!BlockInteractionUtil.mc.world.checkNoEntityCollision(new AxisAlignedBB(pos))) {
            return BlockInteractionUtil.ValidResult.NoEntityCollision;
        } else if (!checkForNeighbours(pos)) {
            return BlockInteractionUtil.ValidResult.NoNeighbors;
        } else {
            IBlockState l_State = BlockInteractionUtil.mc.world.getBlockState(pos);

            if (l_State.getBlock() != Blocks.AIR) {
                return BlockInteractionUtil.ValidResult.AlreadyBlockThere;
            } else {
                BlockPos[] l_Blocks = new BlockPos[] { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};
                BlockPos[] ablockpos = l_Blocks;
                int i = l_Blocks.length;

                for (int j = 0; j < i; ++j) {
                    BlockPos l_Pos = ablockpos[j];
                    IBlockState l_State2 = BlockInteractionUtil.mc.world.getBlockState(l_Pos);

                    if (l_State2.getBlock() != Blocks.AIR) {
                        EnumFacing[] aenumfacing = EnumFacing.values();
                        int k = aenumfacing.length;

                        for (int l = 0; l < k; ++l) {
                            EnumFacing side = aenumfacing[l];
                            BlockPos neighbor = pos.offset(side);

                            if (BlockInteractionUtil.mc.world.getBlockState(neighbor).getBlock().canCollideCheck(BlockInteractionUtil.mc.world.getBlockState(neighbor), false)) {
                                return BlockInteractionUtil.ValidResult.Ok;
                            }
                        }
                    }
                }

                return BlockInteractionUtil.ValidResult.NoNeighbors;
            }
        }
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { BlockInteractionUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - BlockInteractionUtil.mc.player.rotationYaw), BlockInteractionUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - BlockInteractionUtil.mc.player.rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(BlockInteractionUtil.mc.player.posX, BlockInteractionUtil.mc.player.posY + (double) BlockInteractionUtil.mc.player.getEyeHeight(), BlockInteractionUtil.mc.player.posZ);
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getLegitRotations(vec);

        BlockInteractionUtil.mc.player.connection.sendPacket(new Rotation(rotations[0], rotations[1], BlockInteractionUtil.mc.player.onGround));
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(BlockPos pos) {
        return BlockInteractionUtil.mc.world.getBlockState(pos);
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

    private static boolean hasNeighbour(BlockPos blockPos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos neighbour = blockPos.offset(side);

            if (!BlockInteractionUtil.mc.world.getBlockState(neighbour).getMaterial().isReplaceable()) {
                return true;
            }
        }

        return false;
    }

    public static List getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList circleblocks = new ArrayList();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            int z = cz - (int) r;

            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;

                while (true) {
                    float f = sphere ? (float) cy + r : (float) (cy + h);

                    if ((float) y >= f) {
                        ++z;
                        break;
                    }

                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));

                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);

                        circleblocks.add(l);
                    }

                    ++y;
                }
            }
        }

        return circleblocks;
    }

    public static enum PlaceResult {

        NotReplaceable, Neighbors, CantPlace, Placed;
    }

    public static enum ValidResult {

        NoEntityCollision, AlreadyBlockThere, NoNeighbors, Ok;
    }
}
