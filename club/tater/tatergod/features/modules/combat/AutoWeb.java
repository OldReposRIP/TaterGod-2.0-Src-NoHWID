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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AutoWeb extends Module {

    public static boolean isPlacing = false;
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting blocksPerPlace = this.register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
    private final Setting packet = this.register(new Setting("Packet", Boolean.valueOf(false)));
    private final Setting disable = this.register(new Setting("AutoDisable", Boolean.valueOf(false)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    private final Setting raytrace = this.register(new Setting("Raytrace", Boolean.valueOf(false)));
    private final Setting lowerbody = this.register(new Setting("Feet", Boolean.valueOf(true)));
    private final Setting upperBody = this.register(new Setting("Face", Boolean.valueOf(false)));
    private final Timer timer = new Timer();
    public EntityPlayer target;
    private boolean didPlace = false;
    private boolean switchedItem;
    private boolean isSneaking;
    private int lastHotbarSlot;
    private int placements = 0;
    private boolean smartRotate = false;
    private BlockPos startPos = null;

    public AutoWeb() {
        super("AutoWeb", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
    }

    public void onEnable() {
        if (!fullNullCheck()) {
            this.startPos = EntityUtil.getRoundedBlockPos(AutoWeb.mc.player);
            this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
        }
    }

    public void onTick() {
        this.smartRotate = false;
        this.doTrap();
    }

    public String getDisplayInfo() {
        return this.target != null ? this.target.getName() : null;
    }

    public void onDisable() {
        AutoWeb.isPlacing = false;
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        this.switchItem(true);
    }

    private void doTrap() {
        if (!this.check()) {
            this.doWebTrap();
            if (this.didPlace) {
                this.timer.reset();
            }

        }
    }

    private void doWebTrap() {
        List placeTargets = this.getPlacements();

        this.placeList(placeTargets);
    }

    private List getPlacements() {
        ArrayList list = new ArrayList();
        Vec3d baseVec = this.target.getPositionVector();

        if (((Boolean) this.lowerbody.getValue()).booleanValue()) {
            list.add(baseVec);
        }

        if (((Boolean) this.upperBody.getValue()).booleanValue()) {
            list.add(baseVec.add(0.0D, 1.0D, 0.0D));
        }

        return list;
    }

    private void placeList(List list) {
        list.sort((vec3d, vec3d2) -> {
            return Double.compare(AutoWeb.mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), AutoWeb.mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z));
        });
        list.sort(Comparator.comparingDouble((vec3d) -> {
            return vec3d.y;
        }));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Vec3d vec3d3 = (Vec3d) iterator.next();
            BlockPos position = new BlockPos(vec3d3);
            int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean) this.raytrace.getValue()).booleanValue());

            if (placeability == 3 || placeability == 1) {
                this.placeBlock(position);
            }
        }

    }

    private boolean check() {
        AutoWeb.isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);

        if (this.isOff()) {
            return true;
        } else if (((Boolean) this.disable.getValue()).booleanValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos(AutoWeb.mc.player))) {
            this.disable();
            return true;
        } else if (obbySlot == -1) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + ChatFormatting.RED + "No Webs in hotbar disabling...");
            this.toggle();
            return true;
        } else {
            if (AutoWeb.mc.player.inventory.currentItem != this.lastHotbarSlot && AutoWeb.mc.player.inventory.currentItem != obbySlot) {
                this.lastHotbarSlot = AutoWeb.mc.player.inventory.currentItem;
            }

            this.switchItem(true);
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            this.target = this.getTarget(10.0D);
            return this.target == null || !this.timer.passedMs((long) ((Integer) this.delay.getValue()).intValue());
        }
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2.0D) + 1.0D;
        Iterator iterator = AutoWeb.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (!EntityUtil.isntValid(player, range) && !player.isInWeb && Tater.speedManager.getPlayerSpeed(player) <= 30.0D) {
                if (target == null) {
                    target = player;
                    distance = AutoWeb.mc.player.getDistanceSq(player);
                } else if (AutoWeb.mc.player.getDistanceSq(player) < distance) {
                    target = player;
                    distance = AutoWeb.mc.player.getDistanceSq(player);
                }
            }
        }

        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < ((Integer) this.blocksPerPlace.getValue()).intValue() && AutoWeb.mc.player.getDistanceSq(pos) <= MathUtil.square(6.0D) && this.switchItem(false)) {
            AutoWeb.isPlacing = true;
            this.isSneaking = this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean) this.rotate.getValue()).booleanValue(), ((Boolean) this.packet.getValue()).booleanValue(), this.isSneaking);
            this.didPlace = true;
            ++this.placements;
        }

    }

    private boolean switchItem(boolean back) {
        boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, InventoryUtil.Switch.NORMAL, BlockWeb.class);

        this.switchedItem = value[0];
        return value[1];
    }
}
