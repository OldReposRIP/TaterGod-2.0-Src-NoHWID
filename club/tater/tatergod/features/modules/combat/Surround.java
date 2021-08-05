package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {

    public static boolean isPlacing = false;
    private final Setting blocksPerTick = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting noGhost = this.register(new Setting("Packet", Boolean.valueOf(false)));
    private final Setting center = this.register(new Setting("TPCenter", Boolean.valueOf(true)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private final Set extendingBlocks = new HashSet();
    private final Map retries = new HashMap();
    private final int obbySlot = -1;
    private int isSafe;
    private BlockPos startPos;
    private boolean didPlace = false;
    private boolean switchedItem;
    private int lastHotbarSlot;
    private boolean isSneaking;
    private int placements = 0;
    private int extenders = 1;
    private boolean offHand = false;

    public Surround() {
        super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }

        this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
        this.startPos = EntityUtil.getRoundedBlockPos(Surround.mc.player);
        if (((Boolean) this.center.getValue()).booleanValue()) {
            Tater.positionManager.setPositionPacket((double) this.startPos.getX() + 0.5D, (double) this.startPos.getY(), (double) this.startPos.getZ() + 0.5D, true, true, true);
        }

        this.retries.clear();
        this.retryTimer.reset();
    }

    public void onTick() {
        this.doFeetPlace();
    }

    public void onDisable() {
        if (!nullCheck()) {
            Surround.isPlacing = false;
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        }
    }

    public String getDisplayInfo() {
        switch (this.isSafe) {
        case 0:
            return ChatFormatting.RED + "Unsafe";

        case 1:
            return ChatFormatting.YELLOW + "Safe";

        default:
            return ChatFormatting.GREEN + "Safe";
        }
    }

    private void doFeetPlace() {
        if (!this.check()) {
            if (!EntityUtil.isSafe(Surround.mc.player, 0, true, false)) {
                this.isSafe = 0;
                this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(Surround.mc.player, 0, true), true, false, false);
            } else if (!EntityUtil.isSafe(Surround.mc.player, -1, false, false)) {
                this.isSafe = 1;
                this.placeBlocks(Surround.mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray(Surround.mc.player, -1, false), false, false, true);
            } else {
                this.isSafe = 2;
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

            for (Iterator iterator = this.extendingBlocks.iterator(); iterator.hasNext(); ++i) {
                array[i] = (Vec3d) iterator.next();
            }

            int placementsBefore = this.placements;

            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, true), true, false, true);
            }

            if (placementsBefore < this.placements) {
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
            Vec3d[] avec3d1 = EntityUtil.getUnsafeBlockArray(Surround.mc.player, 0, true);
            int k = avec3d1.length;

            for (int l = 0; l < k; ++l) {
                Vec3d pos = avec3d1[l];

                if (vec3d.equals(pos)) {
                    ++matches;
                }
            }
        }

        if (matches == 2) {
            return Surround.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        } else {
            return null;
        }
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        boolean gotHelp = true;
        Vec3d[] avec3d = vec3ds;
        int i = vec3ds.length;

        for (int j = 0; j < i; ++j) {
            Vec3d vec3d = avec3d[j];

            gotHelp = true;
            BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);

            switch (BlockUtil.isPositionPlaceable(position, false)) {
            case 1:
                if (this.retries.get(position) != null && ((Integer) this.retries.get(position)).intValue() >= 4) {
                    if (Tater.speedManager.getSpeedKpH() == 0.0D && !isExtending && this.extenders < 1) {
                        this.placeBlocks(Surround.mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(Surround.mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
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

    private boolean check() {
        if (nullCheck()) {
            return true;
        } else {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            this.offHand = InventoryUtil.isBlock(Surround.mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
            Surround.isPlacing = false;
            this.didPlace = false;
            this.extenders = 1;
            this.placements = 0;
            obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (this.isOff()) {
                return true;
            } else {
                if (this.retryTimer.passedMs(2500L)) {
                    this.retries.clear();
                    this.retryTimer.reset();
                }

                if (obbySlot == -1 && !this.offHand && echestSlot == -1) {
                    Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
                    this.disable();
                    return true;
                } else {
                    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
                    if (Surround.mc.player.inventory.currentItem != this.lastHotbarSlot && Surround.mc.player.inventory.currentItem != obbySlot && Surround.mc.player.inventory.currentItem != echestSlot) {
                        this.lastHotbarSlot = Surround.mc.player.inventory.currentItem;
                    }

                    if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(Surround.mc.player))) {
                        this.disable();
                        return true;
                    } else {
                        return !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
                    }
                }
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerTick.getValue()).intValue()) {
            int originalSlot = Surround.mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            Surround.isPlacing = true;
            Surround.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            Surround.mc.playerController.updateController();
            this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.noGhost.getValue()).booleanValue(), this.isSneaking);
            Surround.mc.player.inventory.currentItem = originalSlot;
            Surround.mc.playerController.updateController();
            this.didPlace = true;
            ++this.placements;
        }

    }
}
