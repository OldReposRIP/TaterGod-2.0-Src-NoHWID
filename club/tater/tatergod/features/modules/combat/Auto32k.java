package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.event.events.ClientEvent;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.player.Freecam;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.EntityUtill;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.RotationUtil;
import club.tater.tatergod.util.RotationUtil2;
import club.tater.tatergod.util.Timer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class Auto32k extends Module {

    private static Auto32k instance;
    private final Setting delay = this.register(new Setting("Delay/Place", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(250)));
    private final Setting range = this.register(new Setting("PlaceRange", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(6.0F)));
    private final Setting raytrace = this.register(new Setting("Raytrace", Boolean.valueOf(false)));
    private final Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(false)));
    private final Setting targetRange = this.register(new Setting("TargetRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(20.0D)));
    private final Setting extra = this.register(new Setting("ExtraRotation", Boolean.valueOf(false)));
    private final Setting placeType;
    private final Setting freecam;
    private final Setting onOtherHoppers;
    private final Setting checkForShulker;
    private final Setting checkDelay;
    private final Setting drop;
    private final Setting mine;
    private final Setting checkStatus;
    private final Setting packet;
    private final Setting superPacket;
    private final Setting secretClose;
    private final Setting closeGui;
    private final Setting repeatSwitch;
    private final Setting hopperDistance;
    private final Setting trashSlot;
    private final Setting messages;
    private final Setting antiHopper;
    private final Timer placeTimer;
    private final Timer disableTimer;
    public Setting mode;
    private final Setting delayDispenser;
    private final Setting blocksPerPlace;
    private final Setting preferObby;
    private final Setting simulate;
    public Setting autoSwitch;
    public Setting withBind;
    public Setting switchBind;
    public boolean switching;
    public Auto32k.Step currentStep;
    private float yaw;
    private float pitch;
    private boolean spoof;
    private int lastHotbarSlot;
    private int shulkerSlot;
    private int hopperSlot;
    private BlockPos hopperPos;
    private EntityPlayer target;
    private int obbySlot;
    private int dispenserSlot;
    private int redstoneSlot;
    private Auto32k.DispenserData finalDispenserData;
    private int actionsThisTick;
    private boolean checkedThisTick;
    private boolean authSneakPacket;
    private boolean shouldDisable;
    private boolean rotationprepared;

    public Auto32k() {
        super("Auto32k", "Auto32ks", Module.Category.COMBAT, true, false, false);
        this.placeType = this.register(new Setting("Place", Auto32k.PlaceType.CLOSE));
        this.freecam = this.register(new Setting("Freecam", Boolean.valueOf(false)));
        this.onOtherHoppers = this.register(new Setting("UseHoppers", Boolean.valueOf(false)));
        this.checkForShulker = this.register(new Setting("CheckShulker", Boolean.valueOf(true)));
        this.checkDelay = this.register(new Setting("CheckDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.drop = this.register(new Setting("Drop", Boolean.valueOf(false)));
        this.mine = this.register(new Setting("Mine", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.checkStatus = this.register(new Setting("CheckState", Boolean.valueOf(true)));
        this.packet = this.register(new Setting("Packet", Boolean.valueOf(false)));
        this.superPacket = this.register(new Setting("DispExtra", Boolean.valueOf(false)));
        this.secretClose = this.register(new Setting("SecretClose", Boolean.valueOf(false)));
        this.closeGui = this.register(new Setting("CloseGui", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.repeatSwitch = this.register(new Setting("SwitchOnFail", Boolean.valueOf(true)));
        this.hopperDistance = this.register(new Setting("HopperRange", Float.valueOf(8.0F), Float.valueOf(0.0F), Float.valueOf(20.0F)));
        this.trashSlot = this.register(new Setting("32kSlot", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(9)));
        this.messages = this.register(new Setting("Messages", Boolean.valueOf(false)));
        this.antiHopper = this.register(new Setting("AntiHopper", Boolean.valueOf(false)));
        this.placeTimer = new Timer();
        this.disableTimer = new Timer();
        this.mode = this.register(new Setting("Mode", Auto32k.Mode.NORMAL));
        this.delayDispenser = this.register(new Setting("Blocks/Place", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(8), test<invokedynamic>(this)));
        this.blocksPerPlace = this.register(new Setting("Actions/Place", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(3), test<invokedynamic>(this)));
        this.preferObby = this.register(new Setting("UseObby", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.simulate = this.register(new Setting("Simulate", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.autoSwitch = this.register(new Setting("AutoSwitch", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.withBind = this.register(new Setting("WithBind", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.switchBind = this.register(new Setting("SwitchBind", new Bind(-1), test<invokedynamic>(this)));
        this.currentStep = Auto32k.Step.PRE;
        this.lastHotbarSlot = -1;
        this.shulkerSlot = -1;
        this.hopperSlot = -1;
        this.obbySlot = -1;
        this.dispenserSlot = -1;
        this.redstoneSlot = -1;
        this.actionsThisTick = 0;
        this.checkedThisTick = false;
        this.authSneakPacket = false;
        this.rotationprepared = false;
        Auto32k.instance = this;
    }

    public static Auto32k getInstance() {
        if (Auto32k.instance == null) {
            Auto32k.instance = new Auto32k();
        }

        return Auto32k.instance;
    }

    public void onEnable() {
        this.checkedThisTick = false;
        this.resetFields();
        if (Auto32k.mc.currentScreen instanceof GuiHopper) {
            this.currentStep = Auto32k.Step.HOPPERGUI;
        }

        if (this.mode.getValue() == Auto32k.Mode.NORMAL && ((Boolean) this.autoSwitch.getValue()).booleanValue() && !((Boolean) this.withBind.getValue()).booleanValue()) {
            this.switching = true;
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0) {
            if (this.shouldDisable && this.disableTimer.passedMs(1000L)) {
                this.shouldDisable = false;
                this.disable();
            } else {
                this.checkedThisTick = false;
                this.actionsThisTick = 0;
                if (!this.isOff() && (this.mode.getValue() != Auto32k.Mode.NORMAL || !((Boolean) this.autoSwitch.getValue()).booleanValue() || this.switching)) {
                    if (this.mode.getValue() == Auto32k.Mode.NORMAL) {
                        this.normal32k();
                    } else {
                        this.processDispenser32k();
                    }

                }
            }
        }
    }

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (!fullNullCheck() && !this.isOff()) {
            if (!((Boolean) this.secretClose.getValue()).booleanValue() && Auto32k.mc.currentScreen instanceof GuiHopper) {
                if (((Boolean) this.drop.getValue()).booleanValue() && Auto32k.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && this.hopperPos != null) {
                    Auto32k.mc.player.dropItem(true);
                    int pickaxeSlot;

                    if (((Boolean) this.mine.getValue()).booleanValue() && this.hopperPos != null && (pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class)) != -1) {
                        InventoryUtil.switchToHotbarSlot(pickaxeSlot, false);
                        if (((Boolean) this.rotate.getValue()).booleanValue()) {
                            this.rotateToPos(this.hopperPos.up(), (Vec3d) null);
                        }

                        Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                        Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }

                this.resetFields();
                if (this.mode.getValue() != Auto32k.Mode.NORMAL) {
                    this.disable();
                    return;
                }

                if (((Boolean) this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() != Auto32k.Mode.DISPENSER) {
                    if (!((Boolean) this.withBind.getValue()).booleanValue()) {
                        this.disable();
                    }
                } else {
                    this.disable();
                }
            } else if (event.getGui() instanceof GuiHopper) {
                this.currentStep = Auto32k.Step.HOPPERGUI;
            }

        }
    }

    public String getDisplayInfo() {
        return this.switching ? "§aSwitch" : null;
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (!this.isOff()) {
            if (Keyboard.getEventKeyState() && !(Auto32k.mc.currentScreen instanceof Gui) && ((Bind) this.switchBind.getValue()).getKey() == Keyboard.getEventKey() && ((Boolean) this.withBind.getValue()).booleanValue()) {
                if (this.switching) {
                    this.resetFields();
                    this.switching = true;
                }

                this.switching = !this.switching;
            }

        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        Setting setting;

        if (event.getStage() == 2 && (setting = event.getSetting()) != null && setting.getFeature().equals(this) && setting.equals(this.mode)) {
            this.resetFields();
        }

    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!fullNullCheck() && !this.isOff()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                if (this.spoof) {
                    CPacketPlayer pickaxeSlot = (CPacketPlayer) event.getPacket();

                    pickaxeSlot.yaw = this.yaw;
                    pickaxeSlot.pitch = this.pitch;
                    this.spoof = false;
                }
            } else if (event.getPacket() instanceof CPacketCloseWindow) {
                if (!((Boolean) this.secretClose.getValue()).booleanValue() && Auto32k.mc.currentScreen instanceof GuiHopper && this.hopperPos != null) {
                    if (((Boolean) this.drop.getValue()).booleanValue() && Auto32k.mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
                        Auto32k.mc.player.dropItem(true);
                        int pickaxeSlot1;

                        if (((Boolean) this.mine.getValue()).booleanValue() && (pickaxeSlot1 = InventoryUtil.findHotbarBlock(ItemPickaxe.class)) != -1) {
                            InventoryUtil.switchToHotbarSlot(pickaxeSlot1, false);
                            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                                this.rotateToPos(this.hopperPos.up(), (Vec3d) null);
                            }

                            Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                            Auto32k.mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), Auto32k.mc.player.getHorizontalFacing());
                            Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
                        }
                    }

                    this.resetFields();
                    if (((Boolean) this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() != Auto32k.Mode.DISPENSER) {
                        if (!((Boolean) this.withBind.getValue()).booleanValue()) {
                            this.disable();
                        }
                    } else {
                        this.disable();
                    }
                } else if (((Boolean) this.secretClose.getValue()).booleanValue() && (!((Boolean) this.autoSwitch.getValue()).booleanValue() || this.switching || this.mode.getValue() == Auto32k.Mode.DISPENSER) && this.currentStep == Auto32k.Step.HOPPERGUI) {
                    event.setCanceled(true);
                }
            }

        }
    }

    private void normal32k() {
        if (((Boolean) this.autoSwitch.getValue()).booleanValue()) {
            if (this.switching) {
                this.processNormal32k();
            } else {
                this.resetFields();
            }
        } else {
            this.processNormal32k();
        }

    }

    private void processNormal32k() {
        if (!this.isOff()) {
            if (this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                this.check();
                switch (this.currentStep) {
                case PRE:
                    this.runPreStep();
                    if (this.currentStep == Auto32k.Step.PRE) {
                        break;
                    }

                case HOPPER:
                    if (this.currentStep == Auto32k.Step.HOPPER) {
                        this.checkState();
                        if (this.currentStep == Auto32k.Step.PRE) {
                            if (this.checkedThisTick) {
                                this.processNormal32k();
                            }

                            return;
                        }

                        this.runHopperStep();
                        if (this.actionsThisTick >= ((Integer) this.blocksPerPlace.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                            break;
                        }
                    }

                case SHULKER:
                    this.checkState();
                    if (this.currentStep == Auto32k.Step.PRE) {
                        if (this.checkedThisTick) {
                            this.processNormal32k();
                        }

                        return;
                    }

                    this.runShulkerStep();
                    if (this.actionsThisTick >= ((Integer) this.blocksPerPlace.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                        break;
                    }

                case CLICKHOPPER:
                    this.checkState();
                    if (this.currentStep == Auto32k.Step.PRE) {
                        if (this.checkedThisTick) {
                            this.processNormal32k();
                        }

                        return;
                    }

                    this.runClickHopper();

                case HOPPERGUI:
                    this.runHopperGuiStep();
                    break;

                default:
                    Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
                    Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
                    Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
                    Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
                    Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
                    this.currentStep = Auto32k.Step.PRE;
                }
            }

        }
    }

    private void runPreStep() {
        if (!this.isOff()) {
            Auto32k.PlaceType type = (Auto32k.PlaceType) this.placeType.getValue();

            if (Freecam.getInstance().isOn() && !((Boolean) this.freecam.getValue()).booleanValue()) {
                if (((Boolean) this.messages.getValue()).booleanValue()) {
                    Command.sendMessage("§c<Auto32k> Disable Freecam.");
                }

                if (((Boolean) this.autoSwitch.getValue()).booleanValue()) {
                    this.resetFields();
                    if (!((Boolean) this.withBind.getValue()).booleanValue()) {
                        this.disable();
                    }
                } else {
                    this.disable();
                }

            } else {
                this.lastHotbarSlot = Auto32k.mc.player.inventory.currentItem;
                this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
                this.shulkerSlot = InventoryUtil.findHotbarBlock(BlockShulkerBox.class);
                if (Auto32k.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock) Auto32k.mc.player.getHeldItemOffhand().getItem()).getBlock();

                    if (block instanceof BlockShulkerBox) {
                        this.shulkerSlot = -2;
                    } else if (block instanceof BlockHopper) {
                        this.hopperSlot = -2;
                    }
                }

                if (this.shulkerSlot != -1 && this.hopperSlot != -1) {
                    this.target = EntityUtil.getClosestEnemy(((Double) this.targetRange.getValue()).doubleValue());
                    if (this.target == null) {
                        if (((Boolean) this.autoSwitch.getValue()).booleanValue()) {
                            if (this.switching) {
                                this.resetFields();
                                this.switching = true;
                            } else {
                                this.resetFields();
                            }

                            return;
                        }

                        type = this.placeType.getValue() == Auto32k.PlaceType.MOUSE ? Auto32k.PlaceType.MOUSE : Auto32k.PlaceType.CLOSE;
                    }

                    this.hopperPos = this.findBestPos(type, this.target);
                    if (this.hopperPos != null) {
                        this.currentStep = Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper ? Auto32k.Step.SHULKER : Auto32k.Step.HOPPER;
                    } else {
                        if (((Boolean) this.messages.getValue()).booleanValue()) {
                            Command.sendMessage("§c<Auto32k> Block not found.");
                        }

                        if (((Boolean) this.autoSwitch.getValue()).booleanValue()) {
                            this.resetFields();
                            if (!((Boolean) this.withBind.getValue()).booleanValue()) {
                                this.disable();
                            }
                        } else {
                            this.disable();
                        }
                    }

                } else {
                    if (((Boolean) this.messages.getValue()).booleanValue()) {
                        Command.sendMessage("§c<Auto32k> Materials not found.");
                    }

                    if (((Boolean) this.autoSwitch.getValue()).booleanValue()) {
                        this.resetFields();
                        if (!((Boolean) this.withBind.getValue()).booleanValue()) {
                            this.disable();
                        }
                    } else {
                        this.disable();
                    }

                }
            }
        }
    }

    private void runHopperStep() {
        if (!this.isOff()) {
            if (this.currentStep == Auto32k.Step.HOPPER) {
                this.runPlaceStep(this.hopperPos, this.hopperSlot);
                this.currentStep = Auto32k.Step.SHULKER;
            }

        }
    }

    private void runShulkerStep() {
        if (!this.isOff()) {
            if (this.currentStep == Auto32k.Step.SHULKER) {
                this.runPlaceStep(this.hopperPos.up(), this.shulkerSlot);
                this.currentStep = Auto32k.Step.CLICKHOPPER;
            }

        }
    }

    private void runClickHopper() {
        if (!this.isOff()) {
            if (this.currentStep == Auto32k.Step.CLICKHOPPER) {
                if (this.mode.getValue() == Auto32k.Mode.NORMAL && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) && ((Boolean) this.checkForShulker.getValue()).booleanValue()) {
                    if (this.placeTimer.passedMs((long) ((Integer) this.checkDelay.getValue()).intValue())) {
                        this.currentStep = Auto32k.Step.SHULKER;
                    }

                } else {
                    this.clickBlock(this.hopperPos);
                    this.currentStep = Auto32k.Step.HOPPERGUI;
                }
            }
        }
    }

    private void runHopperGuiStep() {
        if (!this.isOff()) {
            if (this.currentStep == Auto32k.Step.HOPPERGUI) {
                if (Auto32k.mc.player.openContainer instanceof ContainerHopper) {
                    if (!EntityUtill.holding32k(Auto32k.mc.player)) {
                        int swordIndex = -1;

                        for (int i = 0; i < 5; ++i) {
                            if (EntityUtil.is32k(((Slot) Auto32k.mc.player.openContainer.inventorySlots.get(0)).inventory.getStackInSlot(i))) {
                                swordIndex = i;
                                break;
                            }
                        }

                        if (swordIndex == -1) {
                            return;
                        }

                        if (((Integer) this.trashSlot.getValue()).intValue() != 0) {
                            InventoryUtil.switchToHotbarSlot(((Integer) this.trashSlot.getValue()).intValue() - 1, false);
                        } else if (this.mode.getValue() != Auto32k.Mode.NORMAL && this.shulkerSlot > 35 && this.shulkerSlot != 45) {
                            InventoryUtil.switchToHotbarSlot(44 - this.shulkerSlot, false);
                        }

                        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, swordIndex, ((Integer) this.trashSlot.getValue()).intValue() == 0 ? Auto32k.mc.player.inventory.currentItem : ((Integer) this.trashSlot.getValue()).intValue() - 1, ClickType.SWAP, Auto32k.mc.player);
                    } else if (((Boolean) this.closeGui.getValue()).booleanValue() && ((Boolean) this.secretClose.getValue()).booleanValue()) {
                        Auto32k.mc.player.closeScreen();
                    }
                } else if (EntityUtill.holding32k(Auto32k.mc.player)) {
                    if (((Boolean) this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() == Auto32k.Mode.NORMAL) {
                        this.switching = false;
                    } else if (!((Boolean) this.autoSwitch.getValue()).booleanValue() || this.mode.getValue() == Auto32k.Mode.DISPENSER) {
                        this.shouldDisable = true;
                        this.disableTimer.reset();
                    }
                }

            }
        }
    }

    private void runPlaceStep(BlockPos pos, int slot) {
        if (!this.isOff()) {
            EnumFacing side = EnumFacing.UP;

            if (((Boolean) this.antiHopper.getValue()).booleanValue() && this.currentStep == Auto32k.Step.HOPPER) {
                boolean neighbour = false;
                EnumFacing[] opposite = EnumFacing.values();
                int hitVec = opposite.length;

                for (int neighbourBlock = 0; neighbourBlock < hitVec; ++neighbourBlock) {
                    EnumFacing angle = opposite[neighbourBlock];

                    if (Auto32k.mc.world.getBlockState(pos.offset(angle)).getBlock() != Blocks.HOPPER && !Auto32k.mc.world.getBlockState(pos.offset(angle)).getMaterial().isReplaceable()) {
                        neighbour = true;
                        side = angle;
                        break;
                    }
                }

                if (!neighbour) {
                    this.resetFields();
                    return;
                }
            } else {
                side = BlockUtil.getFirstFacing(pos);
                if (side == null) {
                    this.resetFields();
                    return;
                }
            }

            BlockPos blockpos = pos.offset(side);
            EnumFacing enumfacing = side.getOpposite();
            Vec3d vec3d = (new Vec3d(blockpos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumfacing.getDirectionVec())).scale(0.5D));
            Block block = Auto32k.mc.world.getBlockState(blockpos).getBlock();

            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.START_SNEAKING));
            this.authSneakPacket = false;
            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                if (((Integer) this.blocksPerPlace.getValue()).intValue() > 1) {
                    float[] afloat = RotationUtil.getLegitRotations(vec3d);

                    if (((Boolean) this.extra.getValue()).booleanValue()) {
                        RotationUtil.faceYawAndPitch(afloat[0], afloat[1]);
                    }
                } else {
                    this.rotateToPos((BlockPos) null, vec3d);
                }
            }

            InventoryUtil.switchToHotbarSlot(slot, false);
            BlockUtil.rightClickBlock(blockpos, vec3d, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, enumfacing, ((Boolean) this.packet.getValue()).booleanValue());
            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.STOP_SNEAKING));
            this.authSneakPacket = false;
            this.placeTimer.reset();
            ++this.actionsThisTick;
        }
    }

    private BlockPos findBestPos(Auto32k.PlaceType type, EntityPlayer target) {
        BlockPos pos = null;
        NonNullList positions = NonNullList.create();

        positions.addAll((Collection) BlockUtil.getSphere(EntityUtil.getPlayerPos(Auto32k.mc.player), ((Float) this.range.getValue()).floatValue(), ((Float) this.range.getValue()).intValue(), false, true, 0).stream().filter(test<invokedynamic>(this)).collect(Collectors.toList()));
        if (positions.isEmpty()) {
            return null;
        } else {
            switch (type) {
            case MOUSE:
                if (Auto32k.mc.objectMouseOver != null && Auto32k.mc.objectMouseOver.typeOfHit == Type.BLOCK) {
                    BlockPos toRemove1 = Auto32k.mc.objectMouseOver.getBlockPos();

                    if (toRemove1 != null && !this.canPlace(toRemove1)) {
                        BlockPos copy1 = toRemove1.up();

                        if (this.canPlace(copy1)) {
                            pos = copy1;
                        }
                    } else {
                        pos = toRemove1;
                    }
                }

                if (pos != null) {
                    break;
                }

            case CLOSE:
                positions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
                pos = (BlockPos) positions.get(0);
                break;

            case ENEMY:
                target.getClass();
                positions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>(target)));
                pos = (BlockPos) positions.get(0);
                break;

            case MIDDLE:
                ArrayList toRemove = new ArrayList();
                NonNullList copy = NonNullList.create();

                copy.addAll(positions);
                Iterator iterator = copy.iterator();

                while (iterator.hasNext()) {
                    BlockPos position = (BlockPos) iterator.next();
                    double difference = Auto32k.mc.player.getDistanceSq(position) - target.getDistanceSq(position);

                    if (difference > 1.0D || difference < -1.0D) {
                        toRemove.add(position);
                    }
                }

                copy.removeAll(toRemove);
                if (copy.isEmpty()) {
                    copy.addAll(positions);
                }

                copy.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
                pos = (BlockPos) copy.get(0);
                break;

            case FAR:
                positions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>(target)));
                pos = (BlockPos) positions.get(0);
                break;

            case SAFE:
                positions.sort(Comparator.comparingInt(applyAsInt<invokedynamic>(this)));
                pos = (BlockPos) positions.get(0);
            }

            return pos;
        }
    }

    private boolean canPlace(BlockPos pos) {
        if (pos == null) {
            return false;
        } else {
            BlockPos boost = pos.up();

            return this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos).getBlock(), ((Boolean) this.onOtherHoppers.getValue()).booleanValue()) && this.isGoodMaterial(Auto32k.mc.world.getBlockState(boost).getBlock(), false) ? (((Boolean) this.raytrace.getValue()).booleanValue() && (!BlockUtil.rayTracePlaceCheck(pos, ((Boolean) this.raytrace.getValue()).booleanValue()) || !BlockUtil.rayTracePlaceCheck(pos, ((Boolean) this.raytrace.getValue()).booleanValue())) ? false : (!this.badEntities(pos) && !this.badEntities(boost) ? (((Boolean) this.onOtherHoppers.getValue()).booleanValue() && Auto32k.mc.world.getBlockState(pos).getBlock() instanceof BlockHopper ? true : this.findFacing(pos)) : false)) : false;
        }
    }

    private void check() {
        if (this.currentStep != Auto32k.Step.PRE && this.currentStep != Auto32k.Step.HOPPER && this.hopperPos != null && !(Auto32k.mc.currentScreen instanceof GuiHopper) && !EntityUtill.holding32k(Auto32k.mc.player) && (Auto32k.mc.player.getDistanceSq(this.hopperPos) > MathUtil.square((double) ((Float) this.hopperDistance.getValue()).floatValue()) || Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() != Blocks.HOPPER)) {
            this.resetFields();
            if (!((Boolean) this.autoSwitch.getValue()).booleanValue() || !((Boolean) this.withBind.getValue()).booleanValue() || this.mode.getValue() != Auto32k.Mode.NORMAL) {
                this.disable();
            }
        }

    }

    private void checkState() {
        if (((Boolean) this.checkStatus.getValue()).booleanValue() && !this.checkedThisTick && (this.currentStep == Auto32k.Step.HOPPER || this.currentStep == Auto32k.Step.SHULKER || this.currentStep == Auto32k.Step.CLICKHOPPER)) {
            if (this.hopperPos == null || !this.isGoodMaterial(Auto32k.mc.world.getBlockState(this.hopperPos).getBlock(), true) || !this.isGoodMaterial(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock(), false) && !(Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) || this.badEntities(this.hopperPos) || this.badEntities(this.hopperPos.up())) {
                if (((Boolean) this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() == Auto32k.Mode.NORMAL) {
                    if (this.switching) {
                        this.resetFields();
                        if (((Boolean) this.repeatSwitch.getValue()).booleanValue()) {
                            this.switching = true;
                        }
                    } else {
                        this.resetFields();
                    }

                    if (!((Boolean) this.withBind.getValue()).booleanValue()) {
                        this.disable();
                    }
                } else {
                    this.disable();
                }

                this.checkedThisTick = true;
            }

        } else {
            this.checkedThisTick = false;
        }
    }

    private void processDispenser32k() {
        if (!this.isOff()) {
            if (this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                this.check();
                switch (this.currentStep) {
                case PRE:
                    this.runDispenserPreStep();
                    if (this.currentStep == Auto32k.Step.PRE) {
                        break;
                    }

                case HOPPER:
                    this.runHopperStep();
                    this.currentStep = Auto32k.Step.DISPENSER;
                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                        break;
                    }

                case DISPENSER:
                    this.runDispenserStep();
                    boolean quickCheck = !Auto32k.mc.world.getBlockState(this.finalDispenserData.getHelpingPos()).getMaterial().isReplaceable();

                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue()) || this.currentStep != Auto32k.Step.DISPENSER_HELPING && this.currentStep != Auto32k.Step.CLICK_DISPENSER || ((Boolean) this.rotate.getValue()).booleanValue() && quickCheck) {
                        break;
                    }

                case DISPENSER_HELPING:
                    this.runDispenserStep();
                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue()) || this.currentStep != Auto32k.Step.CLICK_DISPENSER && this.currentStep != Auto32k.Step.DISPENSER_HELPING || ((Boolean) this.rotate.getValue()).booleanValue()) {
                        break;
                    }

                case CLICK_DISPENSER:
                    this.clickDispenser();
                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                        break;
                    }

                case DISPENSER_GUI:
                    this.dispenserGui();
                    if (this.currentStep == Auto32k.Step.DISPENSER_GUI) {
                        break;
                    }

                case REDSTONE:
                    this.placeRedstone();
                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                        break;
                    }

                case CLICKHOPPER:
                    this.runClickHopper();
                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                        break;
                    }

                case HOPPERGUI:
                    this.runHopperGuiStep();
                    if (this.actionsThisTick >= ((Integer) this.delayDispenser.getValue()).intValue() && this.placeTimer.passedMs((long) ((Integer) this.delay.getValue()).intValue())) {
                        ;
                    }

                case SHULKER:
                }
            }

        }
    }

    private void placeRedstone() {
        if (!this.isOff()) {
            if (!this.badEntities(this.hopperPos.up()) || Auto32k.mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) {
                this.runPlaceStep(this.finalDispenserData.getRedStonePos(), this.redstoneSlot);
                this.currentStep = Auto32k.Step.CLICKHOPPER;
            }
        }
    }

    private void clickDispenser() {
        if (!this.isOff()) {
            this.clickBlock(this.finalDispenserData.getDispenserPos());
            this.currentStep = Auto32k.Step.DISPENSER_GUI;
        }
    }

    private void dispenserGui() {
        if (!this.isOff()) {
            if (Auto32k.mc.currentScreen instanceof GuiDispenser) {
                Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, this.shulkerSlot, 0, ClickType.QUICK_MOVE, Auto32k.mc.player);
                Auto32k.mc.player.closeScreen();
                this.currentStep = Auto32k.Step.REDSTONE;
            }
        }
    }

    private void clickBlock(BlockPos pos) {
        if (!this.isOff() && pos != null) {
            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.STOP_SNEAKING));
            this.authSneakPacket = false;
            Vec3d hitVec = (new Vec3d(pos)).add(0.5D, -0.5D, 0.5D);

            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                this.rotateToPos((BlockPos) null, hitVec);
            }

            EnumFacing facing = EnumFacing.UP;

            if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getDispenserPos().equals(pos) && pos.getY() > (new BlockPos(Auto32k.mc.player.getPositionVector())).up().getY()) {
                facing = EnumFacing.DOWN;
            }

            BlockUtil.rightClickBlock(pos, hitVec, this.shulkerSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, facing, ((Boolean) this.packet.getValue()).booleanValue());
            Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
            Auto32k.mc.rightClickDelayTimer = 4;
            ++this.actionsThisTick;
        }
    }

    private void runDispenserStep() {
        if (!this.isOff()) {
            if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getHelpingPos() != null) {
                if (this.currentStep == Auto32k.Step.DISPENSER || this.currentStep == Auto32k.Step.DISPENSER_HELPING) {
                    BlockPos dispenserPos = this.finalDispenserData.getDispenserPos();
                    BlockPos helpingPos = this.finalDispenserData.getHelpingPos();

                    if (!Auto32k.mc.world.getBlockState(helpingPos).getMaterial().isReplaceable()) {
                        this.placeDispenserAgainstBlock(dispenserPos, helpingPos);
                        ++this.actionsThisTick;
                        this.currentStep = Auto32k.Step.CLICK_DISPENSER;
                    } else {
                        this.currentStep = Auto32k.Step.DISPENSER_HELPING;
                        EnumFacing facing = EnumFacing.DOWN;
                        boolean foundHelpingPos = false;
                        EnumFacing[] neighbour = EnumFacing.values();
                        int opposite = neighbour.length;

                        for (int hitVec = 0; hitVec < opposite; ++hitVec) {
                            EnumFacing neighbourBlock = neighbour[hitVec];
                            BlockPos slot = helpingPos.offset(neighbourBlock);

                            if (!slot.equals(this.hopperPos) && !slot.equals(this.hopperPos.up()) && !slot.equals(dispenserPos) && !slot.equals(this.finalDispenserData.getRedStonePos()) && Auto32k.mc.player.getDistanceSq(slot) <= MathUtil.square((double) ((Float) this.range.getValue()).floatValue()) && (!((Boolean) this.raytrace.getValue()).booleanValue() || BlockUtil.rayTracePlaceCheck(slot, ((Boolean) this.raytrace.getValue()).booleanValue())) && !Auto32k.mc.world.getBlockState(slot).getMaterial().isReplaceable()) {
                                foundHelpingPos = true;
                                facing = neighbourBlock;
                                break;
                            }
                        }

                        if (!foundHelpingPos) {
                            this.disable();
                        } else {
                            BlockPos blockpos = helpingPos.offset(facing);
                            EnumFacing enumfacing = facing.getOpposite();
                            Vec3d vec3d = (new Vec3d(blockpos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumfacing.getDirectionVec())).scale(0.5D));
                            Block block = Auto32k.mc.world.getBlockState(blockpos).getBlock();

                            this.authSneakPacket = true;
                            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.START_SNEAKING));
                            this.authSneakPacket = false;
                            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                                if (((Integer) this.blocksPerPlace.getValue()).intValue() > 1) {
                                    float[] afloat = RotationUtil.getLegitRotations(vec3d);

                                    if (((Boolean) this.extra.getValue()).booleanValue()) {
                                        RotationUtil.faceYawAndPitch(afloat[0], afloat[1]);
                                    }
                                } else {
                                    this.rotateToPos((BlockPos) null, vec3d);
                                }
                            }

                            int i = ((Boolean) this.preferObby.getValue()).booleanValue() && this.obbySlot != -1 ? this.obbySlot : this.dispenserSlot;

                            InventoryUtil.switchToHotbarSlot(i, false);
                            BlockUtil.rightClickBlock(blockpos, vec3d, i == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, enumfacing, ((Boolean) this.packet.getValue()).booleanValue());
                            this.authSneakPacket = true;
                            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.STOP_SNEAKING));
                            this.authSneakPacket = false;
                            this.placeTimer.reset();
                            ++this.actionsThisTick;
                        }
                    }
                }
            } else {
                this.resetFields();
            }
        }
    }

    private void placeDispenserAgainstBlock(BlockPos dispenserPos, BlockPos helpingPos) {
        if (!this.isOff()) {
            EnumFacing facing = EnumFacing.DOWN;
            EnumFacing[] opposite = EnumFacing.values();
            int hitVec = opposite.length;

            EnumFacing rotationVec;

            for (int neighbourBlock = 0; neighbourBlock < hitVec; ++neighbourBlock) {
                rotationVec = opposite[neighbourBlock];
                BlockPos facings = dispenserPos.offset(rotationVec);

                if (facings.equals(helpingPos)) {
                    facing = rotationVec;
                    break;
                }
            }

            EnumFacing enumfacing = facing.getOpposite();
            Vec3d vec3d = (new Vec3d(helpingPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumfacing.getDirectionVec())).scale(0.5D));
            Block block = Auto32k.mc.world.getBlockState(helpingPos).getBlock();

            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.START_SNEAKING));
            this.authSneakPacket = false;
            rotationVec = null;
            EnumFacing enumfacing1 = EnumFacing.UP;
            float[] arrf;
            Vec3d vec3d1;

            if (((Boolean) this.rotate.getValue()).booleanValue()) {
                if (((Integer) this.blocksPerPlace.getValue()).intValue() > 1) {
                    arrf = RotationUtil.getLegitRotations(vec3d);
                    if (((Boolean) this.extra.getValue()).booleanValue()) {
                        RotationUtil.faceYawAndPitch(arrf[0], arrf[1]);
                    }
                } else {
                    this.rotateToPos((BlockPos) null, vec3d);
                }

                vec3d1 = (new Vec3d(helpingPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumfacing.getDirectionVec())).scale(0.5D));
            } else if (dispenserPos.getY() <= (new BlockPos(Auto32k.mc.player.getPositionVector())).up().getY()) {
                EnumFacing[] aenumfacing = EnumFacing.values();
                int angle = aenumfacing.length;

                for (int i = 0; i < angle; ++i) {
                    EnumFacing enumFacing = aenumfacing[i];
                    BlockPos position = this.hopperPos.up().offset(enumFacing);

                    if (position.equals(dispenserPos)) {
                        enumfacing1 = enumFacing;
                        break;
                    }
                }

                arrf = RotationUtil2.simpleFacing(enumfacing1);
                this.yaw = arrf[0];
                this.pitch = arrf[1];
                this.spoof = true;
            } else {
                arrf = RotationUtil2.simpleFacing(enumfacing1);
                this.yaw = arrf[0];
                this.pitch = arrf[1];
                this.spoof = true;
            }

            vec3d1 = (new Vec3d(helpingPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(enumfacing.getDirectionVec())).scale(0.5D));
            arrf = RotationUtil2.simpleFacing(enumfacing1);
            float[] afloat = RotationUtil.getLegitRotations(vec3d);

            if (((Boolean) this.superPacket.getValue()).booleanValue()) {
                RotationUtil.faceYawAndPitch(!((Boolean) this.rotate.getValue()).booleanValue() ? arrf[0] : afloat[0], !((Boolean) this.rotate.getValue()).booleanValue() ? arrf[1] : afloat[1]);
            }

            InventoryUtil.switchToHotbarSlot(this.dispenserSlot, false);
            BlockUtil.rightClickBlock(helpingPos, vec3d1, this.dispenserSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, enumfacing, ((Boolean) this.packet.getValue()).booleanValue());
            this.authSneakPacket = true;
            Auto32k.mc.player.connection.sendPacket(new CPacketEntityAction(Auto32k.mc.player, Action.STOP_SNEAKING));
            this.authSneakPacket = false;
            this.placeTimer.reset();
            ++this.actionsThisTick;
            this.currentStep = Auto32k.Step.CLICK_DISPENSER;
        }
    }

    private void runDispenserPreStep() {
        if (!this.isOff()) {
            if (Freecam.getInstance().isOn() && !((Boolean) this.freecam.getValue()).booleanValue()) {
                if (((Boolean) this.messages.getValue()).booleanValue()) {
                    Command.sendMessage("§c<Auto32k> Disable Freecam.");
                }

                this.disable();
            } else {
                this.lastHotbarSlot = Auto32k.mc.player.inventory.currentItem;
                this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
                this.shulkerSlot = InventoryUtil.findBlockSlotInventory(BlockShulkerBox.class, false, false);
                this.dispenserSlot = InventoryUtil.findHotbarBlock(BlockDispenser.class);
                this.redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
                this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
                if (Auto32k.mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock) Auto32k.mc.player.getHeldItemOffhand().getItem()).getBlock();

                    if (block instanceof BlockHopper) {
                        this.hopperSlot = -2;
                    } else if (block instanceof BlockDispenser) {
                        this.dispenserSlot = -2;
                    } else if (block == Blocks.REDSTONE_BLOCK) {
                        this.redstoneSlot = -2;
                    } else if (block instanceof BlockObsidian) {
                        this.obbySlot = -2;
                    }
                }

                if (this.shulkerSlot != -1 && this.hopperSlot != -1 && this.dispenserSlot != -1 && this.redstoneSlot != -1) {
                    this.finalDispenserData = this.findBestPos();
                    if (this.finalDispenserData.isPlaceable()) {
                        this.hopperPos = this.finalDispenserData.getHopperPos();
                        this.currentStep = Auto32k.mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper ? Auto32k.Step.DISPENSER : Auto32k.Step.HOPPER;
                    } else {
                        if (((Boolean) this.messages.getValue()).booleanValue()) {
                            Command.sendMessage("§c<Auto32k> Block not found.");
                        }

                        this.disable();
                    }

                } else {
                    if (((Boolean) this.messages.getValue()).booleanValue()) {
                        Command.sendMessage("§c<Auto32k> Materials not found.");
                    }

                    this.disable();
                }
            }
        }
    }

    private Auto32k.DispenserData findBestPos() {
        Auto32k.PlaceType type = (Auto32k.PlaceType) this.placeType.getValue();

        this.target = EntityUtil.getClosestEnemy(((Double) this.targetRange.getValue()).doubleValue());
        if (this.target == null) {
            type = this.placeType.getValue() == Auto32k.PlaceType.MOUSE ? Auto32k.PlaceType.MOUSE : Auto32k.PlaceType.CLOSE;
        }

        NonNullList positions = NonNullList.create();

        positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos(Auto32k.mc.player), ((Float) this.range.getValue()).floatValue(), ((Float) this.range.getValue()).intValue(), false, true, 0));
        Auto32k.DispenserData data = new Auto32k.DispenserData();

        switch (type) {
        case MOUSE:
            BlockPos toRemove1;

            if (Auto32k.mc.objectMouseOver != null && Auto32k.mc.objectMouseOver.typeOfHit == Type.BLOCK && (toRemove1 = Auto32k.mc.objectMouseOver.getBlockPos()) != null && !(data = this.analyzePos(toRemove1)).isPlaceable()) {
                data = this.analyzePos(toRemove1.up());
            }

            if (data.isPlaceable()) {
                return data;
            }

        case CLOSE:
            positions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
            break;

        case ENEMY:
            EntityPlayer entityplayer = this.target;

            this.target.getClass();
            positions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>(entityplayer)));
            break;

        case MIDDLE:
            ArrayList toRemove = new ArrayList();
            NonNullList copy = NonNullList.create();

            copy.addAll(positions);
            Iterator iterator = copy.iterator();

            while (iterator.hasNext()) {
                BlockPos position = (BlockPos) iterator.next();
                double difference = Auto32k.mc.player.getDistanceSq(position) - this.target.getDistanceSq(position);

                if (difference > 1.0D || difference < -1.0D) {
                    toRemove.add(position);
                }
            }

            copy.removeAll(toRemove);
            if (copy.isEmpty()) {
                copy.addAll(positions);
            }

            copy.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
            break;

        case FAR:
            positions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>(this)));
            break;

        case SAFE:
            positions.sort(Comparator.comparingInt(applyAsInt<invokedynamic>(this)));
        }

        data = this.findData(positions);
        return data;
    }

    private Auto32k.DispenserData findData(NonNullList positions) {
        Iterator iterator = positions.iterator();

        Auto32k.DispenserData data;

        do {
            if (!iterator.hasNext()) {
                return new Auto32k.DispenserData();
            }

            BlockPos position = (BlockPos) iterator.next();

            data = this.analyzePos(position);
        } while (!data.isPlaceable());

        return data;
    }

    private Auto32k.DispenserData analyzePos(BlockPos pos) {
        Auto32k.DispenserData data = new Auto32k.DispenserData(pos);

        if (pos == null) {
            return data;
        } else if (this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos).getBlock(), ((Boolean) this.onOtherHoppers.getValue()).booleanValue()) && this.isGoodMaterial(Auto32k.mc.world.getBlockState(pos.up()).getBlock(), false)) {
            if (((Boolean) this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(pos, ((Boolean) this.raytrace.getValue()).booleanValue())) {
                return data;
            } else if (!this.badEntities(pos) && !this.badEntities(pos.up())) {
                if (this.hasAdjancedRedstone(pos)) {
                    return data;
                } else if (!this.findFacing(pos)) {
                    return data;
                } else {
                    BlockPos[] otherPositions = this.checkForDispenserPos(pos);

                    if (otherPositions[0] != null && otherPositions[1] != null && otherPositions[2] != null) {
                        data.setDispenserPos(otherPositions[0]);
                        data.setRedStonePos(otherPositions[1]);
                        data.setHelpingPos(otherPositions[2]);
                        data.setPlaceable(true);
                        return data;
                    } else {
                        return data;
                    }
                }
            } else {
                return data;
            }
        } else {
            return data;
        }
    }

    private boolean findFacing(BlockPos pos) {
        boolean foundFacing = false;
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing facing = aenumfacing[j];

            if (facing != EnumFacing.UP) {
                if (facing == EnumFacing.DOWN && ((Boolean) this.antiHopper.getValue()).booleanValue() && Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) {
                    foundFacing = false;
                    break;
                }

                if (!Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable() && (!((Boolean) this.antiHopper.getValue()).booleanValue() || Auto32k.mc.world.getBlockState(pos.offset(facing)).getBlock() != Blocks.HOPPER)) {
                    foundFacing = true;
                }
            }
        }

        return foundFacing;
    }

    private BlockPos[] checkForDispenserPos(BlockPos posIn) {
        BlockPos[] pos = new BlockPos[3];
        BlockPos playerPos = new BlockPos(Auto32k.mc.player.getPositionVector());

        if (posIn.getY() < playerPos.down().getY()) {
            return pos;
        } else {
            List possiblePositions = this.getDispenserPositions(posIn);

            if (posIn.getY() < playerPos.getY()) {
                possiblePositions.remove(posIn.up().up());
            } else if (posIn.getY() > playerPos.getY()) {
                possiblePositions.remove(posIn.west().up());
                possiblePositions.remove(posIn.north().up());
                possiblePositions.remove(posIn.south().up());
                possiblePositions.remove(posIn.east().up());
            }

            BlockPos position2;

            if (!((Boolean) this.rotate.getValue()).booleanValue() && !((Boolean) this.simulate.getValue()).booleanValue()) {
                possiblePositions.removeIf(test<invokedynamic>(this));
                possiblePositions.removeIf(test<invokedynamic>(this));
                possiblePositions.removeIf(test<invokedynamic>(this));
                possiblePositions.removeIf(test<invokedynamic>(this));
                possiblePositions.removeIf(test<invokedynamic>(this));
                Iterator playerPos1 = possiblePositions.iterator();

                while (playerPos1.hasNext()) {
                    position2 = (BlockPos) playerPos1.next();
                    List possibleRedStonePositions1 = this.checkRedStone(position2, posIn);
                    BlockPos[] helpingStuff1;

                    if (!possiblePositions.isEmpty() && (helpingStuff1 = this.getHelpingPos(position2, posIn, possibleRedStonePositions1)) != null && helpingStuff1[0] != null && helpingStuff1[1] != null) {
                        pos[0] = position2;
                        pos[1] = helpingStuff1[1];
                        pos[2] = helpingStuff1[0];
                        break;
                    }
                }
            } else {
                possiblePositions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
                position2 = (BlockPos) possiblePositions.get(0);
                if (!this.isGoodMaterial(Auto32k.mc.world.getBlockState(position2).getBlock(), false)) {
                    return pos;
                }

                if (Auto32k.mc.player.getDistanceSq(position2) > MathUtil.square((double) ((Float) this.range.getValue()).floatValue())) {
                    return pos;
                }

                if (((Boolean) this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(position2, ((Boolean) this.raytrace.getValue()).booleanValue())) {
                    return pos;
                }

                if (this.badEntities(position2)) {
                    return pos;
                }

                if (this.hasAdjancedRedstone(position2)) {
                    return pos;
                }

                List helpingStuff = this.checkRedStone(position2, posIn);

                if (possiblePositions.isEmpty()) {
                    return pos;
                }

                BlockPos[] possibleRedStonePositions = this.getHelpingPos(position2, posIn, helpingStuff);

                if (possibleRedStonePositions != null && possibleRedStonePositions[0] != null && possibleRedStonePositions[1] != null) {
                    pos[0] = position2;
                    pos[1] = possibleRedStonePositions[1];
                    pos[2] = possibleRedStonePositions[0];
                }
            }

            return pos;
        }
    }

    private List checkRedStone(BlockPos pos, BlockPos hopperPos) {
        ArrayList toCheck = new ArrayList();
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing facing = aenumfacing[j];

            toCheck.add(pos.offset(facing));
        }

        toCheck.removeIf(test<invokedynamic>(hopperPos));
        toCheck.removeIf(test<invokedynamic>(this));
        toCheck.removeIf(test<invokedynamic>(this));
        toCheck.removeIf(test<invokedynamic>(this));
        toCheck.removeIf(test<invokedynamic>(this));
        toCheck.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
        return toCheck;
    }

    private boolean hasAdjancedRedstone(BlockPos pos) {
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing facing = aenumfacing[j];
            BlockPos position = pos.offset(facing);

            if (Auto32k.mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_BLOCK || Auto32k.mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_TORCH) {
                return true;
            }
        }

        return false;
    }

    private List getDispenserPositions(BlockPos pos) {
        ArrayList list = new ArrayList();
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing facing = aenumfacing[j];

            if (facing != EnumFacing.DOWN) {
                list.add(pos.offset(facing).up());
            }
        }

        return list;
    }

    private BlockPos[] getHelpingPos(BlockPos pos, BlockPos hopperPos, List redStonePositions) {
        BlockPos[] result = new BlockPos[2];
        ArrayList possiblePositions = new ArrayList();

        if (redStonePositions.isEmpty()) {
            return null;
        } else {
            EnumFacing[] aenumfacing = EnumFacing.values();
            int i = aenumfacing.length;

            for (int j = 0; j < i; ++j) {
                EnumFacing facing = aenumfacing[j];
                BlockPos facingPos = pos.offset(facing);

                if (!facingPos.equals(hopperPos) && !facingPos.equals(hopperPos.up())) {
                    if (!Auto32k.mc.world.getBlockState(facingPos).getMaterial().isReplaceable()) {
                        if (!redStonePositions.contains(facingPos)) {
                            result[0] = facingPos;
                            result[1] = (BlockPos) redStonePositions.get(0);
                            return result;
                        }

                        redStonePositions.remove(facingPos);
                        if (!redStonePositions.isEmpty()) {
                            result[0] = facingPos;
                            result[1] = (BlockPos) redStonePositions.get(0);
                            return result;
                        }

                        redStonePositions.add(facingPos);
                    } else {
                        EnumFacing[] aenumfacing1 = EnumFacing.values();
                        int k = aenumfacing1.length;

                        for (int l = 0; l < k; ++l) {
                            EnumFacing facing1 = aenumfacing1[l];
                            BlockPos facingPos1 = facingPos.offset(facing1);

                            if (!facingPos1.equals(hopperPos) && !facingPos1.equals(hopperPos.up()) && !facingPos1.equals(pos) && !Auto32k.mc.world.getBlockState(facingPos1).getMaterial().isReplaceable()) {
                                if (redStonePositions.contains(facingPos)) {
                                    redStonePositions.remove(facingPos);
                                    if (redStonePositions.isEmpty()) {
                                        redStonePositions.add(facingPos);
                                    } else {
                                        possiblePositions.add(facingPos);
                                    }
                                } else {
                                    possiblePositions.add(facingPos);
                                }
                            }
                        }
                    }
                }
            }

            possiblePositions.removeIf(test<invokedynamic>(this));
            possiblePositions.sort(Comparator.comparingDouble(applyAsDouble<invokedynamic>()));
            if (!possiblePositions.isEmpty()) {
                redStonePositions.remove(possiblePositions.get(0));
                if (!redStonePositions.isEmpty()) {
                    result[0] = (BlockPos) possiblePositions.get(0);
                    result[1] = (BlockPos) redStonePositions.get(0);
                }

                return result;
            } else {
                return null;
            }
        }
    }

    private void rotateToPos(BlockPos pos, Vec3d vec3d) {
        float[] angle = vec3d == null ? MathUtil.calcAngle(Auto32k.mc.player.getPositionEyes(Auto32k.mc.getRenderPartialTicks()), new Vec3d((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() - 0.5F), (double) ((float) pos.getZ() + 0.5F))) : RotationUtil.getLegitRotations(vec3d);

        this.yaw = angle[0];
        this.pitch = angle[1];
        this.spoof = true;
    }

    private boolean isGoodMaterial(Block block, boolean allowHopper) {
        return block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow || allowHopper && block instanceof BlockHopper;
    }

    private void resetFields() {
        this.shouldDisable = false;
        this.spoof = false;
        this.switching = false;
        this.lastHotbarSlot = -1;
        this.shulkerSlot = -1;
        this.hopperSlot = -1;
        this.hopperPos = null;
        this.target = null;
        this.currentStep = Auto32k.Step.PRE;
        this.obbySlot = -1;
        this.dispenserSlot = -1;
        this.redstoneSlot = -1;
        this.finalDispenserData = null;
        this.actionsThisTick = 0;
        this.rotationprepared = false;
    }

    private boolean badEntities(BlockPos pos) {
        Iterator iterator = Auto32k.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).iterator();

        Entity entity;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity = (Entity) iterator.next();
        } while (entity instanceof EntityExpBottle || entity instanceof EntityItem || entity instanceof EntityXPOrb);

        return true;
    }

    private int safetyFactor(BlockPos pos) {
        return this.safety(pos) + this.safety(pos.up());
    }

    private int safety(BlockPos pos) {
        int safety = 0;
        EnumFacing[] aenumfacing = EnumFacing.values();
        int i = aenumfacing.length;

        for (int j = 0; j < i; ++j) {
            EnumFacing facing = aenumfacing[j];

            if (!Auto32k.mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
                ++safety;
            }
        }

        return safety;
    }

    private static double lambda$getHelpingPos$28(BlockPos position) {
        return Auto32k.mc.player.getDistanceSq(position);
    }

    private boolean lambda$getHelpingPos$27(BlockPos position) {
        return Auto32k.mc.player.getDistanceSq(position) > MathUtil.square((double) ((Float) this.range.getValue()).floatValue());
    }

    private static double lambda$checkRedStone$26(BlockPos pos2) {
        return Auto32k.mc.player.getDistanceSq(pos2);
    }

    private boolean lambda$checkRedStone$25(BlockPos position) {
        return ((Boolean) this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(position, ((Boolean) this.raytrace.getValue()).booleanValue());
    }

    private boolean lambda$checkRedStone$24(BlockPos position) {
        return !this.isGoodMaterial(Auto32k.mc.world.getBlockState(position).getBlock(), false);
    }

    private boolean lambda$checkRedStone$23(BlockPos position) {
        return Auto32k.mc.player.getDistanceSq(position) > MathUtil.square((double) ((Float) this.range.getValue()).floatValue());
    }

    private static boolean lambda$checkRedStone$22(BlockPos hopperPos, BlockPos position) {
        return position.equals(hopperPos.up());
    }

    private boolean lambda$checkForDispenserPos$21(BlockPos position) {
        return ((Boolean) this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(position, ((Boolean) this.raytrace.getValue()).booleanValue());
    }

    private boolean lambda$checkForDispenserPos$20(BlockPos position) {
        return !this.isGoodMaterial(Auto32k.mc.world.getBlockState(position).getBlock(), false);
    }

    private boolean lambda$checkForDispenserPos$19(BlockPos position) {
        return Auto32k.mc.player.getDistanceSq(position) > MathUtil.square((double) ((Float) this.range.getValue()).floatValue());
    }

    private static double lambda$checkForDispenserPos$18(BlockPos pos2) {
        return -Auto32k.mc.player.getDistanceSq(pos2);
    }

    private int lambda$findBestPos$17(BlockPos pos2) {
        return -this.safetyFactor(pos2);
    }

    private double lambda$findBestPos$16(BlockPos pos2) {
        return -this.target.getDistanceSq(pos2);
    }

    private static double lambda$findBestPos$15(BlockPos pos2) {
        return Auto32k.mc.player.getDistanceSq(pos2);
    }

    private static double lambda$findBestPos$14(BlockPos pos2) {
        return Auto32k.mc.player.getDistanceSq(pos2);
    }

    private int lambda$findBestPos$13(BlockPos pos2) {
        return -this.safetyFactor(pos2);
    }

    private static double lambda$findBestPos$12(EntityPlayer target, BlockPos pos2) {
        return -target.getDistanceSq(pos2);
    }

    private static double lambda$findBestPos$11(BlockPos pos2) {
        return Auto32k.mc.player.getDistanceSq(pos2);
    }

    private static double lambda$findBestPos$10(BlockPos pos2) {
        return Auto32k.mc.player.getDistanceSq(pos2);
    }

    private boolean lambda$new$9(Object v) {
        return ((Boolean) this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() == Auto32k.Mode.NORMAL && ((Boolean) this.withBind.getValue()).booleanValue();
    }

    private boolean lambda$new$8(Object v) {
        return this.mode.getValue() == Auto32k.Mode.NORMAL && ((Boolean) this.autoSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$7(Object v) {
        return this.mode.getValue() == Auto32k.Mode.NORMAL;
    }

    private boolean lambda$new$6(Object v) {
        return this.mode.getValue() != Auto32k.Mode.NORMAL;
    }

    private boolean lambda$new$5(Object v) {
        return this.mode.getValue() != Auto32k.Mode.NORMAL;
    }

    private boolean lambda$new$4(Object v) {
        return this.mode.getValue() == Auto32k.Mode.NORMAL;
    }

    private boolean lambda$new$3(Object v) {
        return this.mode.getValue() != Auto32k.Mode.NORMAL;
    }

    private boolean lambda$new$2(Object v) {
        return ((Boolean) this.secretClose.getValue()).booleanValue();
    }

    private boolean lambda$new$1(Object v) {
        return ((Boolean) this.drop.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return ((Boolean) this.checkForShulker.getValue()).booleanValue();
    }

    public static class DispenserData {

        private BlockPos dispenserPos;
        private BlockPos redStonePos;
        private BlockPos hopperPos;
        private BlockPos helpingPos;
        private boolean isPlaceable = false;

        public DispenserData() {}

        public DispenserData(BlockPos pos) {
            this.hopperPos = pos;
        }

        public boolean isPlaceable() {
            return this.dispenserPos != null && this.hopperPos != null && this.redStonePos != null && this.helpingPos != null;
        }

        public void setPlaceable(boolean placeable) {
            this.isPlaceable = placeable;
        }

        public BlockPos getDispenserPos() {
            return this.dispenserPos;
        }

        public void setDispenserPos(BlockPos dispenserPos) {
            this.dispenserPos = dispenserPos;
        }

        public BlockPos getRedStonePos() {
            return this.redStonePos;
        }

        public void setRedStonePos(BlockPos redStonePos) {
            this.redStonePos = redStonePos;
        }

        public BlockPos getHopperPos() {
            return this.hopperPos;
        }

        public void setHopperPos(BlockPos hopperPos) {
            this.hopperPos = hopperPos;
        }

        public BlockPos getHelpingPos() {
            return this.helpingPos;
        }

        public void setHelpingPos(BlockPos helpingPos) {
            this.helpingPos = helpingPos;
        }
    }

    public static enum PlaceType {

        MOUSE, CLOSE, ENEMY, MIDDLE, FAR, SAFE;
    }

    public static enum Mode {

        NORMAL, DISPENSER;
    }

    public static enum Step {

        PRE, HOPPER, SHULKER, CLICKHOPPER, HOPPERGUI, DISPENSER_HELPING, DISPENSER_GUI, DISPENSER, CLICK_DISPENSER, REDSTONE;
    }
}
