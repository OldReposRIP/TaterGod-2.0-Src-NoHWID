package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.NiggaUtil;
import club.tater.tatergod.util.Timer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WurstSurround extends Module {

    private final Setting hybrid = this.register(new Setting("Hybrid", Boolean.valueOf(true)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    private final Setting packet = this.register(new Setting("Packet", Boolean.valueOf(false)));
    private final Setting center = this.register(new Setting("Center", Boolean.valueOf(true)));
    private final Setting blockFace = this.register(new Setting("BlockFace", Boolean.valueOf(false)));
    private final Setting blocksPerTick = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
    private final Setting timeout = this.register(new Setting("Timeout", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(50)));
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set extendingBlocks = new HashSet();
    private final Map retries = new HashMap();
    private int yLevel = 0;
    private BlockPos startPos;
    private boolean didPlace = false;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private boolean offHand = false;
    private int ticksPassed = 0;

    public WurstSurround() {
        super("Surround2", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (WurstSurround.mc.player != null) {
            this.yLevel = (int) Math.round(WurstSurround.mc.player.posY);
            this.startPos = EntityUtil.getRoundedBlockPos(Surround.mc.player);
            if (((Boolean) this.center.getValue()).booleanValue()) {
                double y = (double) WurstSurround.mc.player.getPosition().getY();
                double x = (double) WurstSurround.mc.player.getPosition().getX();
                double z = (double) WurstSurround.mc.player.getPosition().getZ();
                Vec3d plusPlus = new Vec3d(x + 0.5D, y, z + 0.5D);
                Vec3d plusMinus = new Vec3d(x + 0.5D, y, z - 0.5D);
                Vec3d minusMinus = new Vec3d(x - 0.5D, y, z - 0.5D);
                Vec3d minusPlus = new Vec3d(x - 0.5D, y, z + 0.5D);

                if (this.getDst(plusPlus) < this.getDst(plusMinus) && this.getDst(plusPlus) < this.getDst(minusMinus) && this.getDst(plusPlus) < this.getDst(minusPlus)) {
                    x = (double) WurstSurround.mc.player.getPosition().getX() + 0.5D;
                    z = (double) WurstSurround.mc.player.getPosition().getZ() + 0.5D;
                    this.centerPlayer(x, y, z);
                }

                if (this.getDst(plusMinus) < this.getDst(plusPlus) && this.getDst(plusMinus) < this.getDst(minusMinus) && this.getDst(plusMinus) < this.getDst(minusPlus)) {
                    x = (double) WurstSurround.mc.player.getPosition().getX() + 0.5D;
                    z = (double) WurstSurround.mc.player.getPosition().getZ() - 0.5D;
                    this.centerPlayer(x, y, z);
                }

                if (this.getDst(minusMinus) < this.getDst(plusPlus) && this.getDst(minusMinus) < this.getDst(plusMinus) && this.getDst(minusMinus) < this.getDst(minusPlus)) {
                    x = (double) WurstSurround.mc.player.getPosition().getX() - 0.5D;
                    z = (double) WurstSurround.mc.player.getPosition().getZ() - 0.5D;
                    this.centerPlayer(x, y, z);
                }

                if (this.getDst(minusPlus) < this.getDst(plusPlus) && this.getDst(minusPlus) < this.getDst(plusMinus) && this.getDst(minusPlus) < this.getDst(minusMinus)) {
                    x = (double) WurstSurround.mc.player.getPosition().getX() - 0.5D;
                    z = (double) WurstSurround.mc.player.getPosition().getZ() + 0.5D;
                    this.centerPlayer(x, y, z);
                }
            }

            this.ticksPassed = 0;
            this.retries.clear();
            this.retryTimer.reset();
            this.lastHotbarSlot = WurstSurround.mc.player.inventory.currentItem;
        }

    }

    public void onTick() {
        if ((int) Math.round(WurstSurround.mc.player.posY) != this.yLevel && ((Boolean) this.hybrid.getValue()).booleanValue()) {
            this.disable();
        } else {
            this.doFeetPlace();
            ++this.ticksPassed;
        }
    }

    private void doFeetPlace() {
        if (!this.check()) {
            if (!NiggaUtil.isSafe(WurstSurround.mc.player, 0, true)) {
                this.placeBlocks(WurstSurround.mc.player.getPositionVector(), NiggaUtil.getUnsafeBlockArray(WurstSurround.mc.player, 0, true), true, false, false);
            } else if (!NiggaUtil.isSafe(WurstSurround.mc.player, -1, false)) {
                this.placeBlocks(WurstSurround.mc.player.getPositionVector(), NiggaUtil.getUnsafeBlockArray(WurstSurround.mc.player, -1, false), false, false, true);
            }

            if (((Boolean) this.blockFace.getValue()).booleanValue()) {
                this.placeBlocks(WurstSurround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(WurstSurround.mc.player, 1, false), false, false, false);
            }

            this.processExtendingBlocks();
            if (this.didPlace) {
                this.timer.reset();
            }

        }
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;

            for (Iterator placementsBefore = this.extendingBlocks.iterator(); placementsBefore.hasNext(); ++i) {
                Vec3d extendingBlock = (Vec3d) placementsBefore.next();

                array[i] = extendingBlock;
            }

            int i = this.placements;

            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, true), true, false, true);
            }

            if (i < this.placements) {
                this.extendingBlocks.clear();
            }
        } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
            this.extendingBlocks.clear();
        }

    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        Vec3d[] avec3d = vec3ds;
        int i = vec3ds.length;

        for (int j = 0; j < i; ++j) {
            Vec3d vec3d = avec3d[j];
            Vec3d[] avec3d1 = EntityUtil.getUnsafeBlockArray(WurstSurround.mc.player, 0, true);
            int k = avec3d1.length;

            for (int l = 0; l < k; ++l) {
                Vec3d pos = avec3d1[l];

                if (vec3d.equals(pos)) {
                    ++matches;
                }
            }
        }

        if (matches == 2) {
            return WurstSurround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        } else {
            return null;
        }
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        Vec3d[] avec3d = vec3ds;
        int i = vec3ds.length;

        for (int j = 0; j < i; ++j) {
            Vec3d vec3d = avec3d[j];
            boolean gotHelp = true;
            BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);

            switch (BlockUtil.isPositionPlaceable(position, false)) {
            case 1:
                if (this.retries.get(position) != null && ((Integer) this.retries.get(position)).intValue() >= 4) {
                    if (WurstSurround.mc.player.motionX == 0.0D && WurstSurround.mc.player.motionZ == 0.0D && !isExtending && this.extenders < 1) {
                        this.placeBlocks(WurstSurround.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(WurstSurround.mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
                        this.extendingBlocks.add(vec3d);
                        ++this.extenders;
                    }
                } else {
                    this.placeBlock(position);
                    this.retries.put(position, Integer.valueOf(this.retries.get(position) == null ? 1 : ((Integer) this.retries.get(position)).intValue() + 1));
                    this.retryTimer.reset();
                }
                break;

            case 2:
                if (!hasHelpingBlocks) {
                    break;
                }

                gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);

            case 3:
                if (gotHelp) {
                    this.placeBlock(position);
                }

                if (isHelping) {
                    return true;
                }
            }
        }

        return false;
    }

    private double getDst(Vec3d vec) {
        return WurstSurround.mc.player.getPositionVector().distanceTo(vec);
    }

    private void centerPlayer(double x, double y, double z) {
        WurstSurround.mc.player.connection.sendPacket(new Position(x, y, z, true));
        WurstSurround.mc.player.setPosition(x, y, z);
    }

    private boolean check() {
        if (nullCheck()) {
            return true;
        } else {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
                return true;
            } else {
                this.offHand = InventoryUtil.isBlock(WurstSurround.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
                this.didPlace = false;
                this.extenders = 1;
                this.placements = 0;
                int obbySlot1 = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

                if (this.retryTimer.passedMs(2500L)) {
                    this.retries.clear();
                    this.retryTimer.reset();
                }

                if (obbySlot1 == -1 && !this.offHand && echestSlot == -1) {
                    this.disable();
                    return true;
                } else {
                    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
                    if (WurstSurround.mc.player.inventory.currentItem != this.lastHotbarSlot && WurstSurround.mc.player.inventory.currentItem != obbySlot1 && WurstSurround.mc.player.inventory.currentItem != echestSlot) {
                        this.lastHotbarSlot = WurstSurround.mc.player.inventory.currentItem;
                    }

                    if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(WurstSurround.mc.player))) {
                        this.disable();
                        return true;
                    } else {
                        return this.ticksPassed > ((Integer) this.timeout.getValue()).intValue() && !((Boolean) this.hybrid.getValue()).booleanValue();
                    }
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerTick.getValue()).intValue()) {
            int originalSlot = WurstSurround.mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            WurstSurround.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            WurstSurround.mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking);
            WurstSurround.mc.player.inventory.currentItem = originalSlot;
            WurstSurround.mc.playerController.updateController();
            this.didPlace = true;
            ++this.placements;
        }

    }

    public Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D;

        return new Vec3d(x, y, z);
    }
}
