package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.BlockEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.combat.Surround;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Timer;
import club.tater.tatergod.util.Util;
import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speedmine extends Module {

    private static Speedmine INSTANCE = new Speedmine();
    private final Timer timer = new Timer();
    public Setting mode;
    public Setting damage;
    public Setting webSwitch;
    public Setting doubleBreak;
    public Setting autosw;
    public Setting render;
    public Setting box;
    private final Setting boxAlpha;
    public Setting outline;
    private final Setting lineWidth;
    public BlockPos currentPos;
    public IBlockState currentBlockState;
    private int lasthotbarslot;

    public Speedmine() {
        super("SpeedMine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
        this.mode = this.register(new Setting("Mode", Speedmine.Mode.PACKET));
        this.damage = this.register(new Setting("Damage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), test<invokedynamic>(this)));
        this.webSwitch = this.register(new Setting("WebSwitch", Boolean.valueOf(false)));
        this.doubleBreak = this.register(new Setting("DoubleBreak", Boolean.valueOf(false)));
        this.autosw = this.register(new Setting("AutoSwitch", Boolean.valueOf(false)));
        this.render = this.register(new Setting("Render", Boolean.valueOf(false)));
        this.box = this.register(new Setting("Box", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.outline = this.register(new Setting("Outline", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.lineWidth = this.register(new Setting("Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), test<invokedynamic>(this)));
        this.setInstance();
    }

    public static Speedmine getInstance() {
        if (Speedmine.INSTANCE == null) {
            Speedmine.INSTANCE = new Speedmine();
        }

        return Speedmine.INSTANCE;
    }

    private void setInstance() {
        Speedmine.INSTANCE = this;
    }

    public void onTick() {
        if (this.currentPos != null) {
            if (Speedmine.mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) && Speedmine.mc.world.getBlockState(this.currentPos).getBlock() != Blocks.AIR) {
                if (((Boolean) this.webSwitch.getValue()).booleanValue() && this.currentBlockState.getBlock() == Blocks.WEB && Speedmine.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                    InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
                }
            } else {
                this.currentPos = null;
                this.currentBlockState = null;
            }
        }

    }

    public void onUpdate() {
        if (!fullNullCheck()) {
            Speedmine.mc.playerController.blockHitDelay = 0;
        }
    }

    public void onRender3D(Render3DEvent event) {
        if (((Boolean) this.render.getValue()).booleanValue() && this.currentPos != null && this.currentBlockState.getBlock() == Blocks.OBSIDIAN) {
            Color color = new Color(this.timer.passedMs((long) ((int) (2000.0F * Tater.serverManager.getTpsFactor()))) ? 0 : 255, this.timer.passedMs((long) ((int) (2000.0F * Tater.serverManager.getTpsFactor()))) ? 255 : 0, 0, 255);

            RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), false);
            if (((Boolean) this.autosw.getValue()).booleanValue()) {
                boolean hasPickaxe = Util.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE;

                if (!hasPickaxe) {
                    for (int i = 0; i < 9; ++i) {
                        ItemStack stack = Util.mc.player.inventory.getStackInSlot(i);

                        if (!stack.isEmpty()) {
                            this.lasthotbarslot = Surround.mc.player.inventory.currentItem;
                            if (Speedmine.mc.player.inventory.currentItem != this.lasthotbarslot) {
                                this.lasthotbarslot = Speedmine.mc.player.inventory.currentItem;
                            }

                            if (stack.getItem() == Items.DIAMOND_PICKAXE) {
                                hasPickaxe = true;
                                Util.mc.player.inventory.currentItem = i;
                                Util.mc.playerController.updateController();
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
        if (!fullNullCheck()) {
            if (event.getStage() == 3 && Speedmine.mc.playerController.curBlockDamageMP > 0.1F) {
                Speedmine.mc.playerController.isHittingBlock = true;
            }

            if (event.getStage() == 4) {
                if (BlockUtil.canBreak(event.pos)) {
                    Speedmine.mc.playerController.isHittingBlock = false;
                    switch ((Speedmine.Mode) this.mode.getValue()) {
                    case PACKET:
                        if (this.currentPos == null) {
                            this.currentPos = event.pos;
                            this.currentBlockState = Speedmine.mc.world.getBlockState(this.currentPos);
                            this.timer.reset();
                        }

                        Speedmine.mc.player.swingArm(EnumHand.MAIN_HAND);
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        event.setCanceled(true);
                        break;

                    case DAMAGE:
                        if (Speedmine.mc.playerController.curBlockDamageMP >= ((Float) this.damage.getValue()).floatValue()) {
                            Speedmine.mc.playerController.curBlockDamageMP = 1.0F;
                        }
                        break;

                    case INSTANT:
                        Speedmine.mc.player.swingArm(EnumHand.MAIN_HAND);
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, event.pos, event.facing));
                        Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                        Speedmine.mc.playerController.onPlayerDestroyBlock(event.pos);
                        Speedmine.mc.world.setBlockToAir(event.pos);
                    }
                }

                BlockPos above;

                if (((Boolean) this.doubleBreak.getValue()).booleanValue() && BlockUtil.canBreak(above = event.pos.add(0, 1, 0)) && Speedmine.mc.player.getDistance((double) above.getX(), (double) above.getY(), (double) above.getZ()) <= 5.0D) {
                    Speedmine.mc.player.swingArm(EnumHand.MAIN_HAND);
                    Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, above, event.facing));
                    Speedmine.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.STOP_DESTROY_BLOCK, above, event.facing));
                    Speedmine.mc.playerController.onPlayerDestroyBlock(above);
                    Speedmine.mc.world.setBlockToAir(above);
                }
            }

        }
    }

    public String getDisplayInfo() {
        return this.mode.currentEnumName();
    }

    private boolean lambda$new$4(Object v) {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$3(Object v) {
        return ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$2(Object v) {
        return ((Boolean) this.box.getValue()).booleanValue() && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$1(Object v) {
        return ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return this.mode.getValue() == Speedmine.Mode.DAMAGE;
    }

    public static enum Mode {

        PACKET, DAMAGE, INSTANT;
    }
}
