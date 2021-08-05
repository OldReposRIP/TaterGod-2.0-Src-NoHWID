package club.tater.tatergod.util;

import club.tater.tatergod.Tater;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class CombatUtil {

    public static final List blackList = Arrays.asList(new Block[] { Blocks.TALLGRASS, Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR});
    public static final List shulkerList = Arrays.asList(new Block[] { Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX});
    public static final Vec3d[] cityOffsets = new Vec3d[] { new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 2.0D), new Vec3d(-2.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -2.0D)};
    private static final List invalidSlots = Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8)});
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int findCrapple() {
        if (CombatUtil.mc.player == null) {
            return -1;
        } else {
            for (int x = 0; x < CombatUtil.mc.player.inventoryContainer.getInventory().size(); ++x) {
                if (!CombatUtil.invalidSlots.contains(Integer.valueOf(x))) {
                    ItemStack stack = (ItemStack) CombatUtil.mc.player.inventoryContainer.getInventory().get(x);

                    if (!stack.isEmpty() && stack.getItem().equals(Items.GOLDEN_APPLE) && stack.getItemDamage() != 1) {
                        return x;
                    }
                }
            }

            return -1;
        }
    }

    public static int findItemSlotDamage1(Item i) {
        if (CombatUtil.mc.player == null) {
            return -1;
        } else {
            for (int x = 0; x < CombatUtil.mc.player.inventoryContainer.getInventory().size(); ++x) {
                if (!CombatUtil.invalidSlots.contains(Integer.valueOf(x))) {
                    ItemStack stack = (ItemStack) CombatUtil.mc.player.inventoryContainer.getInventory().get(x);

                    if (!stack.isEmpty() && stack.getItem().equals(i) && stack.getItemDamage() == 1) {
                        return x;
                    }
                }
            }

            return -1;
        }
    }

    public static int findItemSlot(Item i) {
        if (CombatUtil.mc.player == null) {
            return -1;
        } else {
            for (int x = 0; x < CombatUtil.mc.player.inventoryContainer.getInventory().size(); ++x) {
                if (!CombatUtil.invalidSlots.contains(Integer.valueOf(x))) {
                    ItemStack stack = (ItemStack) CombatUtil.mc.player.inventoryContainer.getInventory().get(x);

                    if (!stack.isEmpty() && stack.getItem().equals(i)) {
                        return x;
                    }
                }
            }

            return -1;
        }
    }

    public static boolean isHoldingCrystal(boolean onlyMainHand) {
        return onlyMainHand ? CombatUtil.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL : CombatUtil.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || CombatUtil.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
    }

    public static boolean requiredDangerSwitch(double dangerRange) {
        int dangerousCrystals = (int) CombatUtil.mc.world.loadedEntityList.stream().filter(test<invokedynamic>()).filter(test<invokedynamic>(dangerRange)).filter(test<invokedynamic>()).count();

        return dangerousCrystals > 0;
    }

    public static boolean passesOffhandCheck(double requiredHealth, Item item, boolean isCrapple) {
        double totalPlayerHealth = (double) (CombatUtil.mc.player.getHealth() + CombatUtil.mc.player.getAbsorptionAmount());

        if (!isCrapple) {
            if (findItemSlot(item) == -1) {
                return false;
            }
        } else if (findCrapple() == -1) {
            return false;
        }

        return totalPlayerHealth >= requiredHealth;
    }

    public static void switchOffhandStrict(int targetSlot, int step) {
        switch (step) {
        case 0:
            CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, CombatUtil.mc.player);
            break;

        case 1:
            CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, CombatUtil.mc.player);
            break;

        case 2:
            CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, CombatUtil.mc.player);
            CombatUtil.mc.playerController.updateController();
        }

    }

    public static void switchOffhandTotemNotStrict() {
        int targetSlot = findItemSlot(Items.TOTEM_OF_UNDYING);

        if (targetSlot != -1) {
            CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, CombatUtil.mc.player);
            CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, CombatUtil.mc.player);
            CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, CombatUtil.mc.player);
            CombatUtil.mc.playerController.updateController();
        }

    }

    public static void switchOffhandNonStrict(int targetSlot) {
        CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, CombatUtil.mc.player);
        CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, CombatUtil.mc.player);
        CombatUtil.mc.playerController.windowClick(CombatUtil.mc.player.inventoryContainer.windowId, targetSlot, 0, ClickType.PICKUP, CombatUtil.mc.player);
        CombatUtil.mc.playerController.updateController();
    }

    public static boolean canSeeBlock(BlockPos pos) {
        return CombatUtil.mc.world.rayTraceBlocks(new Vec3d(CombatUtil.mc.player.posX, CombatUtil.mc.player.posY + (double) CombatUtil.mc.player.getEyeHeight(), CombatUtil.mc.player.posZ), new Vec3d((double) pos.getX(), (double) ((float) pos.getY() + 1.0F), (double) pos.getZ()), false, true, false) == null;
    }

    public static boolean placeBlock(BlockPos blockPos, boolean offhand, boolean rotate, boolean packetRotate, boolean doSwitch, boolean silentSwitch, int toSwitch) {
        if (!checkCanPlace(blockPos)) {
            return false;
        } else {
            EnumFacing placeSide = getPlaceSide(blockPos);
            BlockPos adjacentBlock = blockPos.offset(placeSide);
            EnumFacing opposingSide = placeSide.getOpposite();

            if (!CombatUtil.mc.world.getBlockState(adjacentBlock).getBlock().canCollideCheck(CombatUtil.mc.world.getBlockState(adjacentBlock), false)) {
                return false;
            } else {
                if (doSwitch) {
                    if (silentSwitch) {
                        CombatUtil.mc.player.connection.sendPacket(new CPacketHeldItemChange(toSwitch));
                    } else if (CombatUtil.mc.player.inventory.currentItem != toSwitch) {
                        CombatUtil.mc.player.inventory.currentItem = toSwitch;
                    }
                }

                boolean isSneak = false;

                if (CombatUtil.blackList.contains(CombatUtil.mc.world.getBlockState(adjacentBlock).getBlock()) || CombatUtil.shulkerList.contains(CombatUtil.mc.world.getBlockState(adjacentBlock).getBlock())) {
                    CombatUtil.mc.player.connection.sendPacket(new CPacketEntityAction(CombatUtil.mc.player, Action.START_SNEAKING));
                    isSneak = true;
                }

                Vec3d hitVector = getHitVector(adjacentBlock, opposingSide);

                if (rotate) {
                    float[] actionHand = getLegitRotations(hitVector);

                    CombatUtil.mc.player.connection.sendPacket(new Rotation(actionHand[0], actionHand[1], CombatUtil.mc.player.onGround));
                }

                EnumHand actionHand1 = offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

                CombatUtil.mc.playerController.processRightClickBlock(CombatUtil.mc.player, CombatUtil.mc.world, adjacentBlock, opposingSide, hitVector, actionHand1);
                CombatUtil.mc.player.connection.sendPacket(new CPacketAnimation(actionHand1));
                if (isSneak) {
                    CombatUtil.mc.player.connection.sendPacket(new CPacketEntityAction(CombatUtil.mc.player, Action.STOP_SNEAKING));
                }

                return true;
            }
        }
    }

    private static Vec3d getHitVector(BlockPos pos, EnumFacing opposingSide) {
        return (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposingSide.getDirectionVec())).scale(0.5D));
    }

    public static Vec3d getHitAddition(double x, double y, double z, BlockPos pos, EnumFacing opposingSide) {
        return (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposingSide.getDirectionVec())).scale(0.5D));
    }

    public static void betterRotate(BlockPos blockPos, EnumFacing opposite, boolean packetRotate) {
        float offsetZ = 0.0F;
        float offsetY = 0.0F;
        float offsetX = 0.0F;

        switch (getPlaceSide(blockPos)) {
        case UP:
            offsetZ = 0.5F;
            offsetX = 0.5F;
            offsetY = 0.0F;
            break;

        case DOWN:
            offsetZ = 0.5F;
            offsetX = 0.5F;
            offsetY = -0.5F;
            break;

        case NORTH:
            offsetX = 0.5F;
            offsetY = -0.5F;
            offsetZ = -0.5F;
            break;

        case EAST:
            offsetX = 0.5F;
            offsetY = -0.5F;
            offsetZ = 0.5F;
            break;

        case SOUTH:
            offsetX = 0.5F;
            offsetY = -0.5F;
            offsetZ = 0.5F;
            break;

        case WEST:
            offsetX = -0.5F;
            offsetY = -0.5F;
            offsetZ = 0.5F;
        }

        float[] angle = getLegitRotations(getHitAddition((double) offsetX, (double) offsetY, (double) offsetZ, blockPos, opposite));

        CombatUtil.mc.player.connection.sendPacket(new Rotation(angle[0], angle[1], CombatUtil.mc.player.onGround));
    }

    private static EnumFacing getPlaceSide(BlockPos blockPos) {
        EnumFacing placeableSide = null;
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing side = aenumfacing[j];
            BlockPos adjacent = blockPos.offset(side);

            if (CombatUtil.mc.world.getBlockState(adjacent).getBlock().canCollideCheck(CombatUtil.mc.world.getBlockState(adjacent), false) && !CombatUtil.mc.world.getBlockState(adjacent).getMaterial().isReplaceable()) {
                placeableSide = side;
            }
        }

        return placeableSide;
    }

    public static boolean checkCanPlace(BlockPos pos) {
        if (!(CombatUtil.mc.world.getBlockState(pos).getBlock() instanceof BlockAir) && !(CombatUtil.mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) {
            return false;
        } else {
            Iterator iterator = CombatUtil.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(pos)).iterator();

            Entity entity;

            do {
                if (!iterator.hasNext()) {
                    return getPlaceSide(pos) != null;
                }

                entity = (Entity) iterator.next();
            } while (entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityArrow);

            return false;
        }
    }

    public static boolean isInCity(EntityPlayer player, double range, double placeRange, boolean checkFace, boolean topBlock, boolean checkPlace, boolean checkRange) {
        BlockPos pos = new BlockPos(player.getPositionVector());
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing face = aenumfacing[j];

            if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
                BlockPos pos1 = pos.offset(face);
                BlockPos pos2 = pos1.offset(face);

                if (CombatUtil.mc.world.getBlockState(pos1).getBlock() == Blocks.AIR && (CombatUtil.mc.world.getBlockState(pos2).getBlock() == Blocks.AIR && isHard(CombatUtil.mc.world.getBlockState(pos2.up()).getBlock()) || !checkFace) && !checkRange || CombatUtil.mc.player.getDistanceSq(pos2) <= placeRange * placeRange && CombatUtil.mc.player.getDistanceSq(player) <= range * range && isHard(CombatUtil.mc.world.getBlockState(pos.up(3)).getBlock()) || !topBlock) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isHard(Block block) {
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
    }

    public static boolean canLegPlace(EntityPlayer player, double range) {
        int safety = 0;
        int blocksInRange = 0;
        Vec3d[] avec3d = HoleUtil.cityOffsets;
        int i = avec3d.length;

        for (int j = 0; j < i; ++j) {
            Vec3d vec = avec3d[j];
            BlockPos pos = getFlooredPosition(player).add(vec.x, vec.y, vec.z);

            if (CombatUtil.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || CombatUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                ++safety;
            }

            if (CombatUtil.mc.player.getDistanceSq(pos) >= range * range) {
                ++blocksInRange;
            }
        }

        return safety == 4 && blocksInRange >= 1;
    }

    public static int getSafetyFactor(BlockPos pos) {
        return 0;
    }

    public static boolean canPlaceCrystal(BlockPos pos, double range, double wallsRange, boolean raytraceCheck) {
        BlockPos up = pos.up();
        BlockPos up1 = up.up();
        AxisAlignedBB bb = (new AxisAlignedBB(up)).expand(0.0D, 1.0D, 0.0D);

        return (CombatUtil.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || CombatUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) && CombatUtil.mc.world.getBlockState(up).getBlock() == Blocks.AIR && CombatUtil.mc.world.getBlockState(up1).getBlock() == Blocks.AIR && CombatUtil.mc.world.getEntitiesWithinAABB(Entity.class, bb).isEmpty() && CombatUtil.mc.player.getDistanceSq(pos) <= range * range && !raytraceCheck || rayTraceRangeCheck(pos, wallsRange, 0.0D);
    }

    public static int getVulnerability(EntityPlayer player, double range, double placeRange, double wallsRange, double maxSelfDamage, double maxFriendDamage, double minDamage, double friendRange, double facePlaceHP, int minArmor, boolean cityCheck, boolean rayTrace, boolean lowArmorCheck, boolean antiSuicide, boolean antiFriendPop) {
        return isInCity(player, range, placeRange, true, true, true, false) && cityCheck ? 5 : (getClosestValidPos(player, maxSelfDamage, maxFriendDamage, minDamage, placeRange, wallsRange, friendRange, rayTrace, antiSuicide, antiFriendPop, true) != null ? 4 : ((double) (player.getHealth() + player.getAbsorptionAmount()) <= facePlaceHP ? 3 : (isArmorLow(player, minArmor, true) && lowArmorCheck ? 2 : 0)));
    }

    public static Map mapBlockDamage(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
        HashMap damageMap = new HashMap();
        Iterator iterator = getSphere(new BlockPos(getFlooredPosition(CombatUtil.mc.player)), (float) placeRange, (int) placeRange, false, true, 0).iterator();

        while (iterator.hasNext()) {
            BlockPos pos = (BlockPos) iterator.next();

            if (canPlaceCrystal(pos, placeRange, wallsRange, rayTrace) && checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop) && checkSelf(pos, maxSelfDamage, antiSuicide) && (!rayTrace || rayTraceRangeCheck(pos, wallsRange, 0.0D))) {
                double damage = (double) calculateDamage(pos, player);

                if (damage >= minDamage) {
                    damageMap.put(pos, Double.valueOf(damage));
                }
            }
        }

        return damageMap;
    }

    public static boolean checkFriends(BlockPos pos, double maxFriendDamage, double friendRange, boolean antiFriendPop) {
        Iterator iterator = CombatUtil.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (CombatUtil.mc.player.getDistanceSq(player) <= friendRange * friendRange) {
                if ((double) calculateDamage(pos, player) > maxFriendDamage) {
                    return false;
                }

                if (calculateDamage(pos, player) > player.getHealth() + player.getAbsorptionAmount() && antiFriendPop) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkFriends(EntityEnderCrystal crystal, double maxFriendDamage, double friendRange, boolean antiFriendPop) {
        Iterator iterator = CombatUtil.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (CombatUtil.mc.player.getDistanceSq(player) <= friendRange * friendRange) {
                if ((double) calculateDamage((Entity) crystal, player) > maxFriendDamage) {
                    return false;
                }

                if (calculateDamage((Entity) crystal, player) > player.getHealth() + player.getAbsorptionAmount() && antiFriendPop) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkSelf(BlockPos pos, double maxSelfDamage, boolean antiSuicide) {
        boolean willPopSelf = calculateDamage(pos, CombatUtil.mc.player) > CombatUtil.mc.player.getHealth() + CombatUtil.mc.player.getAbsorptionAmount();
        boolean willDamageSelf = (double) calculateDamage(pos, CombatUtil.mc.player) > maxSelfDamage;

        return (!antiSuicide || !willPopSelf) && !willDamageSelf;
    }

    public static boolean checkSelf(EntityEnderCrystal crystal, double maxSelfDamage, boolean antiSuicide) {
        boolean willPopSelf = calculateDamage((Entity) crystal, CombatUtil.mc.player) > CombatUtil.mc.player.getHealth() + CombatUtil.mc.player.getAbsorptionAmount();
        boolean willDamageSelf = (double) calculateDamage((Entity) crystal, CombatUtil.mc.player) > maxSelfDamage;

        return (!antiSuicide || !willPopSelf) && !willDamageSelf;
    }

    public static boolean isPosValid(EntityPlayer player, BlockPos pos, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
        if (pos == null) {
            return false;
        } else if (!isHard(CombatUtil.mc.world.getBlockState(pos).getBlock())) {
            return false;
        } else if (!canPlaceCrystal(pos, placeRange, wallsRange, rayTrace)) {
            return false;
        } else if (!checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop)) {
            return false;
        } else if (!checkSelf(pos, maxSelfDamage, antiSuicide)) {
            return false;
        } else {
            double damage = (double) calculateDamage(pos, player);

            return damage < minDamage ? false : !rayTrace || rayTraceRangeCheck(pos, wallsRange, 0.0D);
        }
    }

    public static BlockPos getClosestValidPos(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop, boolean multiplace) {
        double highestDamage = -1.0D;
        BlockPos finalPos = null;

        if (player == null) {
            return null;
        } else {
            List placeLocations = getSphere(new BlockPos(getFlooredPosition(CombatUtil.mc.player)), (float) placeRange, (int) placeRange, false, true, 0);

            placeLocations.sort(Comparator.comparing(apply<invokedynamic>()));
            Iterator iterator = placeLocations.iterator();

            while (iterator.hasNext()) {
                BlockPos pos = (BlockPos) iterator.next();

                if (canPlaceCrystal(pos, placeRange, wallsRange, rayTrace) && (!rayTrace || rayTraceRangeCheck(pos, wallsRange, 0.0D))) {
                    double damage = (double) calculateDamage(pos, player);

                    if (damage >= minDamage && checkFriends(pos, maxFriendDamage, friendRange, antiFriendPop) && checkSelf(pos, maxSelfDamage, antiSuicide)) {
                        if (damage > 15.0D) {
                            return pos;
                        }

                        if (damage > highestDamage) {
                            highestDamage = damage;
                            finalPos = pos;
                        }
                    }
                }
            }

            return finalPos;
        }
    }

    public static BlockPos getClosestValidPosMultiThread(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
        CopyOnWriteArrayList threads = new CopyOnWriteArrayList();
        BlockPos finalPos = null;
        Iterator areAllInvalid = getSphere(new BlockPos(player.getPositionVector()), 13.0F, 13, false, true, 0).iterator();

        CombatUtil.ValidPosThread thread;

        while (areAllInvalid.hasNext()) {
            BlockPos pos = (BlockPos) areAllInvalid.next();

            thread = new CombatUtil.ValidPosThread(player, pos, maxSelfDamage, maxFriendDamage, minDamage, placeRange, wallsRange, friendRange, rayTrace, antiSuicide, antiFriendPop);
            threads.add(thread);
            thread.start();
        }

        boolean areAllInvalid1 = false;

        do {
            Iterator pos1 = threads.iterator();

            while (pos1.hasNext()) {
                thread = (CombatUtil.ValidPosThread) pos1.next();
                if (thread.isInterrupted() && thread.isValid) {
                    finalPos = thread.pos;
                }
            }

            areAllInvalid1 = threads.stream().noneMatch(test<invokedynamic>());
        } while (finalPos == null && !areAllInvalid1);

        Tater.LOGGER.info(finalPos == null ? "pos was null" : finalPos.toString());
        return finalPos;
    }

    public static List getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList circleblocks = new ArrayList();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                for (int y = sphere ? cy - (int) r : cy; (float) y < (sphere ? (float) cy + r : (float) (cy + h)); ++y) {
                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));

                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);

                        circleblocks.add(l);
                    }
                }
            }
        }

        return circleblocks;
    }

    public static boolean isArmorLow(EntityPlayer player, int durability, boolean checkDurability) {
        Iterator iterator = player.inventory.armorInventory.iterator();

        ItemStack piece;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            piece = (ItemStack) iterator.next();
            if (piece == null) {
                return true;
            }
        } while (!checkDurability || getItemDamage(piece) >= durability);

        return true;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }

    public static boolean rayTraceRangeCheck(Entity target, double range) {
        boolean isVisible = CombatUtil.mc.player.canEntityBeSeen(target);

        return !isVisible || CombatUtil.mc.player.getDistanceSq(target) <= range * range;
    }

    public static boolean rayTraceRangeCheck(BlockPos pos, double range, double height) {
        RayTraceResult result = CombatUtil.mc.world.rayTraceBlocks(new Vec3d(CombatUtil.mc.player.posX, CombatUtil.mc.player.posY + (double) CombatUtil.mc.player.getEyeHeight(), CombatUtil.mc.player.posZ), new Vec3d((double) pos.getX(), (double) pos.getY() + height, (double) pos.getZ()), false, true, false);

        return result == null || CombatUtil.mc.player.getDistanceSq(pos) <= range * range;
    }

    public static EntityEnderCrystal getClosestValidCrystal(EntityPlayer player, double maxSelfDamage, double maxFriendDamage, double minDamage, double breakRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
        if (player == null) {
            return null;
        } else {
            List crystals = (List) CombatUtil.mc.world.loadedEntityList.stream().filter(test<invokedynamic>()).filter(test<invokedynamic>(breakRange)).sorted(Comparator.comparingDouble(applyAsDouble<invokedynamic>())).map(apply<invokedynamic>()).collect(Collectors.toList());
            Iterator iterator = crystals.iterator();

            EntityEnderCrystal crystal;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                crystal = (EntityEnderCrystal) iterator.next();
            } while (rayTrace && !rayTraceRangeCheck(crystal, wallsRange) || (double) calculateDamage((Entity) crystal, player) < minDamage || !checkSelf(crystal, maxSelfDamage, antiSuicide) || !checkFriends(crystal, maxFriendDamage, friendRange, antiFriendPop));

            return crystal;
        }
    }

    public static List getDisc(BlockPos pos, float r) {
        ArrayList circleblocks = new ArrayList();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            for (int z = cz - (int) r; (float) z <= (float) cz + r; ++z) {
                double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z));

                if (dist < (double) (r * r)) {
                    BlockPos position = new BlockPos(x, cy, z);

                    circleblocks.add(position);
                }
            }
        }

        return circleblocks;
    }

    public static BlockPos getFlooredPosition(Entity entity) {
        return new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0D;

        try {
            blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception exception) {
            ;
        }

        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finald = (double) getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(Minecraft.getMinecraft().world, (Entity) null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage;

        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);

            damage = CombatRules.getDamageAfterAbsorb(damageI, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;

            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception exception) {
                ;
            }

            float f = MathHelper.clamp((float) k, 0.0F, 20.0F);

            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0F;
            }

            damage = Math.max(damage, 0.0F);
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damageI, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    public static float getDamageMultiplied(float damage) {
        int diff = Minecraft.getMinecraft().world.getDifficulty().getId();

        return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
    }

    public static float calculateDamage(Entity crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    public static float calculateDamage(BlockPos pos, Entity entity) {
        return calculateDamage((double) pos.getX() + 0.5D, (double) (pos.getY() + 1), (double) pos.getZ() + 0.5D, entity);
    }

    public static Vec3d interpolateEntity(Entity entity) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) CombatUtil.mc.getRenderPartialTicks(), entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) CombatUtil.mc.getRenderPartialTicks(), entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) CombatUtil.mc.getRenderPartialTicks());
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0D;
        double difZ = to.z - from.z;
        double dist = (double) MathHelper.sqrt(difX * difX + difZ * difZ);

        return new float[] { (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(CombatUtil.mc.player.posX, CombatUtil.mc.player.posY + (double) CombatUtil.mc.player.getEyeHeight(), CombatUtil.mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        return new float[] { CombatUtil.mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - CombatUtil.mc.player.rotationYaw), CombatUtil.mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - CombatUtil.mc.player.rotationPitch)};
    }

    private static EntityEnderCrystal lambda$getClosestValidCrystal$8(Entity entity) {
        return (EntityEnderCrystal) entity;
    }

    private static double lambda$getClosestValidCrystal$7(Entity entity) {
        return CombatUtil.mc.player.getDistanceSq(entity);
    }

    private static boolean lambda$getClosestValidCrystal$6(double breakRange, Entity entity) {
        return CombatUtil.mc.player.getDistanceSq(entity) <= breakRange * breakRange;
    }

    private static boolean lambda$getClosestValidCrystal$5(Entity entity) {
        return entity instanceof EntityEnderCrystal;
    }

    private static boolean lambda$getClosestValidPosMultiThread$4(CombatUtil.ValidPosThread thread) {
        return thread.isValid && thread.isInterrupted();
    }

    private static Double lambda$getClosestValidPos$3(BlockPos blockPos) {
        return Double.valueOf(CombatUtil.mc.player.getDistanceSq(blockPos));
    }

    private static boolean lambda$requiredDangerSwitch$2(Entity entity) {
        return calculateDamage(entity.posX, entity.posY, entity.posZ, CombatUtil.mc.player) >= CombatUtil.mc.player.getHealth() + CombatUtil.mc.player.getAbsorptionAmount();
    }

    private static boolean lambda$requiredDangerSwitch$1(double dangerRange, Entity entity) {
        return (double) CombatUtil.mc.player.getDistance(entity) <= dangerRange;
    }

    private static boolean lambda$requiredDangerSwitch$0(Entity entity) {
        return entity instanceof EntityEnderCrystal;
    }

    public static class CombatPosInfo {

        public EntityPlayer player;
        public BlockPos pos;
        public float damage;

        public CombatPosInfo(EntityPlayer player, BlockPos pos, float damage) {
            this.pos = pos;
            this.damage = damage;
            this.player = player;
        }
    }

    public static class ValidPosThread extends Thread {

        public float damage;
        public boolean isValid;
        public CombatUtil.CombatPosInfo info;
        BlockPos pos;
        EntityPlayer player;
        double maxSelfDamage;
        double maxFriendDamage;
        double minDamage;
        double placeRange;
        double wallsRange;
        double friendRange;
        boolean rayTrace;
        boolean antiSuicide;
        boolean antiFriendPop;

        public ValidPosThread(EntityPlayer player, BlockPos pos, double maxSelfDamage, double maxFriendDamage, double minDamage, double placeRange, double wallsRange, double friendRange, boolean rayTrace, boolean antiSuicide, boolean antiFriendPop) {
            super("Break");
            this.pos = pos;
            this.maxSelfDamage = maxSelfDamage;
            this.maxFriendDamage = maxFriendDamage;
            this.minDamage = minDamage;
            this.placeRange = placeRange;
            this.wallsRange = wallsRange;
            this.friendRange = friendRange;
            this.rayTrace = rayTrace;
            this.antiSuicide = antiSuicide;
            this.antiFriendPop = antiFriendPop;
            this.player = player;
        }

        public void run() {
            if (CombatUtil.mc.player.getDistanceSq(this.pos) <= this.placeRange * this.placeRange && CombatUtil.canPlaceCrystal(this.pos, this.placeRange, this.wallsRange, this.rayTrace) && CombatUtil.checkFriends(this.pos, this.maxFriendDamage, this.friendRange, this.antiFriendPop) && CombatUtil.checkSelf(this.pos, this.maxSelfDamage, this.antiSuicide)) {
                this.damage = CombatUtil.calculateDamage(this.pos, this.player);
                if ((double) this.damage >= this.minDamage && (!this.rayTrace || CombatUtil.rayTraceRangeCheck(this.pos, this.wallsRange, 0.0D))) {
                    this.isValid = true;
                    this.info = new CombatUtil.CombatPosInfo(this.player, this.pos, this.damage);
                    Tater.LOGGER.info("Pos was valid.");
                    return;
                }
            }

            this.isValid = false;
            this.info = new CombatUtil.CombatPosInfo(this.player, this.pos, -1.0F);
            Tater.LOGGER.info("Pos was invalid.");
        }
    }
}
