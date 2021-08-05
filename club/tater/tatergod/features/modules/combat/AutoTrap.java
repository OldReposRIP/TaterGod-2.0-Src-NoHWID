package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoTrap extends Module {

    public static boolean isPlacing = false;
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting blocksPerPlace = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    private final Setting raytrace = this.register(new Setting("Raytrace", Boolean.valueOf(false)));
    private final Setting antiScaffold = this.register(new Setting("AntiScaffold", Boolean.valueOf(false)));
    private final Setting antiStep = this.register(new Setting("AntiStep", Boolean.valueOf(false)));
    private final Setting noGhost = this.register(new Setting("Packet", Boolean.valueOf(false)));
    private final Timer timer = new Timer();
    private final Map retries = new HashMap();
    private final Timer retryTimer = new Timer();
    private final boolean offHand = false;
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private BlockPos startPos = null;

    public AutoTrap() {
        super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.startPos = EntityUtil.getRoundedBlockPos(AutoTrap.mc.player);
            this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
            this.retries.clear();
        }
    }

    public void onTick() {
        if (!fullNullCheck()) {
            this.doTrap();
        }
    }

    public String getDisplayInfo() {
        return this.target != null ? this.target.getName() : null;
    }

    public void onDisable() {
        AutoTrap.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    }

    private void doTrap() {
        if (!this.check()) {
            this.doStaticTrap();
            if (this.didPlace) {
                this.timer.reset();
            }

        }
    }

    private void doStaticTrap() {
        List placeTargets = EntityUtil.targets(this.target.getPositionVector(), ((Boolean) this.antiScaffold.getValue()).booleanValue(), ((Boolean) this.antiStep.getValue()).booleanValue(), false, false, false, ((Boolean) this.raytrace.getValue()).booleanValue());

        this.placeList(placeTargets);
    }

    private void placeList(List list) {
        list.sort((vec3d, vec3d2) -> {
            return Double.compare(AutoTrap.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoTrap.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z));
        });
        list.sort(Comparator.comparingDouble((vec3d) -> {
            return vec3d.y;
        }));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Vec3d vec3d3 = (Vec3d) iterator.next();
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean) this.raytrace.getValue()).booleanValue());

            if (placeability == 1 && (this.retries.get(position) == null || ((Integer) this.retries.get(position)).intValue() < 4)) {
                this.placeBlock(position);
                this.retries.put(position, Integer.valueOf(this.retries.get(position) == null ? 1 : ((Integer) this.retries.get(position)).intValue() + 1));
                this.retryTimer.reset();
            } else if (placeability == 3) {
                this.placeBlock(position);
            }
        }

    }

    private boolean check() {
        AutoTrap.isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot2 = InventoryUtil.findHotbarBlock(BlockObsidian.class);

        if (obbySlot2 == -1) {
            this.toggle();
        }

        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);

        if (this.isOff()) {
            return true;
        } else if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(AutoTrap.mc.player))) {
            this.disable();
            return true;
        } else {
            if (this.retryTimer.passedMs(2000L)) {
                this.retries.clear();
                this.retryTimer.reset();
            }

            if (obbySlot == -1) {
                Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
                this.disable();
                return true;
            } else {
                if (AutoTrap.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoTrap.mc.player.inventory.currentItem != obbySlot) {
                    this.lastHotbarSlot = AutoTrap.mc.player.inventory.currentItem;
                }

                this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
                this.target = this.getTarget(10.0D, true);
                return this.target == null || !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
            }
        }
    }

    private EntityPlayer getTarget(double range, boolean trapped) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0D) + 1.0D;
        Iterator iterator = AutoTrap.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (!EntityUtil.isntValid(player, range) && (!trapped || !EntityUtil.isTrapped(player, ((Boolean) this.antiScaffold.getValue()).booleanValue(), ((Boolean) this.antiStep.getValue()).booleanValue(), false, false, false)) && Tater.speedManager.getPlayerSpeed(player) <= 10.0D) {
                if (target == null) {
                    target = player;
                    distance = AutoTrap.mc.player.getDistanceSq(player);
                } else if (AutoTrap.mc.player.getDistanceSq(player) < distance) {
                    target = player;
                    distance = AutoTrap.mc.player.getDistanceSq(player);
                }
            }
        }

        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerPlace.getValue()).intValue() && AutoTrap.mc.player.getDistanceSq(pos) <= MathUtil.square(5.0D)) {
            AutoTrap.isPlacing = true;
            int originalSlot = AutoTrap.mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                AutoTrap.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
                AutoTrap.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.noGhost.getValue()).booleanValue(), this.isSneaking);
                AutoTrap.mc.player.inventory.currentItem = originalSlot;
                AutoTrap.mc.playerController.updateController();
            } else {
                AutoTrap.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
                AutoTrap.mc.playerController.updateController();
                this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.noGhost.getValue()).booleanValue(), this.isSneaking);
                AutoTrap.mc.player.inventory.currentItem = originalSlot;
                AutoTrap.mc.playerController.updateController();
            }

            this.didPlace = true;
            ++this.placements;
        }

    }
}
