package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.Timer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Selftrap extends Module {

    private final Setting blocksPerTick = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    private final Setting disableTime = this.register(new Setting("DisableTime", Integer.valueOf(500), Integer.valueOf(50), Integer.valueOf(500)));
    private final Setting disable = this.register(new Setting("AutoDisable", Boolean.valueOf(true)));
    private final Setting packet = this.register(new Setting("Packet", Boolean.valueOf(false)));
    private final Timer offTimer = new Timer();
    private final Timer timer = new Timer();
    private final Map retries = new HashMap();
    private final Timer retryTimer = new Timer();
    private final boolean hasOffhand = false;
    private int blocksThisTick = 0;
    private boolean isSneaking;
    private boolean offHand = false;

    public Selftrap() {
        super("SelfTrap", "Lure your enemies in!", Module.Category.COMBAT, true, false, true);
    }

    public void onEnable() {
        if (fullNullCheck()) {
            this.disable();
        }

        this.offTimer.reset();
    }

    public void onTick() {
        if (this.isOn() && (((Integer) this.blocksPerTick.getValue()).intValue() != 1 || !((Boolean) this.rotate.getValue()).booleanValue())) {
            this.doSelfTrap();
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.isOn() && event.getStage() == 0 && ((Integer) this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean) this.rotate.getValue()).booleanValue()) {
            this.doSelfTrap();
        }

    }

    public void onDisable() {
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.retries.clear();
        this.offHand = false;
    }

    private void doSelfTrap() {
        if (!this.check()) {
            Iterator iterator = this.getPositions().iterator();

            while (iterator.hasNext()) {
                BlockPos position = (BlockPos) iterator.next();
                int placeability = BlockUtil.isPositionPlaceable(position, false);

                if (placeability == 1 && (this.retries.get(position) == null || ((Integer) this.retries.get(position)).intValue() < 4)) {
                    this.placeBlock(position);
                    this.retries.put(position, Integer.valueOf(this.retries.get(position) == null ? 1 : ((Integer) this.retries.get(position)).intValue() + 1));
                }

                if (placeability == 3) {
                    this.placeBlock(position);
                }
            }

        }
    }

    private List getPositions() {
        ArrayList positions = new ArrayList();

        positions.add(new BlockPos(Selftrap.mc.player.posX, Selftrap.mc.player.posY + 2.0D, Selftrap.mc.player.posZ));
        int placeability = BlockUtil.isPositionPlaceable((BlockPos) positions.get(0), false);

        switch (placeability) {
        case 0:
            return new ArrayList();

        case 1:
            if (BlockUtil.isPositionPlaceable((BlockPos) positions.get(0), false, false) == 3) {
                return positions;
            }

        case 2:
            positions.add(new BlockPos(Selftrap.mc.player.posX + 1.0D, Selftrap.mc.player.posY + 1.0D, Selftrap.mc.player.posZ));
            positions.add(new BlockPos(Selftrap.mc.player.posX + 1.0D, Selftrap.mc.player.posY + 2.0D, Selftrap.mc.player.posZ));

        default:
            positions.sort(Comparator.comparingDouble(Vec3i::getY));
            return positions;

        case 3:
            return positions;
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.blocksThisTick < ((Integer) this.blocksPerTick.getValue()).intValue()) {
            boolean smartRotate = ((Integer) this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean) this.rotate.getValue()).booleanValue();
            int originalSlot = Selftrap.mc.player.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            Selftrap.mc.player.inventory.currentItem = obbySlot == -1 ? eChestSot : obbySlot;
            Selftrap.mc.playerController.updateController();
            this.isSneaking = smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking);
            Selftrap.mc.player.inventory.currentItem = originalSlot;
            Selftrap.mc.playerController.updateController();
            this.timer.reset();
            ++this.blocksThisTick;
        }

    }

    private boolean check() {
        if (fullNullCheck()) {
            this.disable();
            return true;
        } else {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

            if (obbySlot == -1 && eChestSot == -1) {
                this.toggle();
            }

            this.blocksThisTick = 0;
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            if (this.retryTimer.passedMs(2000L)) {
                this.retries.clear();
                this.retryTimer.reset();
            }

            if (!EntityUtil.isSafe(Selftrap.mc.player)) {
                this.offTimer.reset();
                return true;
            } else if (((Boolean) this.disable.getValue()).booleanValue() && this.offTimer.passedMs((long) ((Integer) this.disableTime.getValue()).intValue())) {
                this.disable();
                return true;
            } else {
                return !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
            }
        }
    }
}
