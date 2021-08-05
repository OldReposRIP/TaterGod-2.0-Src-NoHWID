package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.player.XCarry;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.DamageUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class AutoArmor extends Module {

    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting mendingTakeOff = this.register(new Setting("AutoMend", Boolean.valueOf(true)));
    private final Setting closestEnemy = this.register(new Setting("Enemy", Integer.valueOf(7), Integer.valueOf(1), Integer.valueOf(20), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting helmetThreshold = this.register(new Setting("Helmet%", Integer.valueOf(69), Integer.valueOf(1), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting chestThreshold = this.register(new Setting("Chest%", Integer.valueOf(69), Integer.valueOf(1), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting legThreshold = this.register(new Setting("Legs%", Integer.valueOf(69), Integer.valueOf(1), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting bootsThreshold = this.register(new Setting("Boots%", Integer.valueOf(69), Integer.valueOf(1), Integer.valueOf(100), (v) -> {
        return ((Boolean) this.mendingTakeOff.getValue()).booleanValue();
    }));
    private final Setting curse = this.register(new Setting("CurseOfBinding", Boolean.valueOf(false)));
    private final Setting actions = this.register(new Setting("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12)));
    private final Setting elytraBind = this.register(new Setting("Elytra", new Bind(-1)));
    private final Setting tps = this.register(new Setting("TpsSync", Boolean.valueOf(false)));
    private final Setting updateController = this.register(new Setting("Update", Boolean.valueOf(true)));
    private final Setting shiftClick = this.register(new Setting("ShiftClick", Boolean.valueOf(false)));
    private final Timer timer = new Timer();
    private final Timer elytraTimer = new Timer();
    private final Queue taskList = new ConcurrentLinkedQueue();
    private final List doneSlots = new ArrayList();
    private boolean elytraOn = false;

    public AutoArmor() {
        super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(AutoArmor.mc.currentScreen instanceof Gui) && ((Bind) this.elytraBind.getValue()).getKey() == Keyboard.getEventKey()) {
            this.elytraOn = !this.elytraOn;
        }

    }

    public void onLogin() {
        this.timer.reset();
        this.elytraTimer.reset();
    }

    public void onDisable() {
        this.taskList.clear();
        this.doneSlots.clear();
        this.elytraOn = false;
    }

    public void onLogout() {
        this.taskList.clear();
        this.doneSlots.clear();
    }

    public void onTick() {
        if (!fullNullCheck() && (!(AutoArmor.mc.currentScreen instanceof GuiContainer) || AutoArmor.mc.currentScreen instanceof GuiInventory)) {
            int i;

            if (this.taskList.isEmpty()) {
                if (((Boolean) this.mendingTakeOff.getValue()).booleanValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && AutoArmor.mc.gameSettings.keyBindUseItem.isKeyDown() && (this.isSafe() || EntityUtil.isSafe(AutoArmor.mc.player, 1, false, true))) {
                    ItemStack helm1 = AutoArmor.mc.player.inventoryContainer.getSlot(5).getStack();

                    if (!helm1.isEmpty && DamageUtil.getRoundedDamage(helm1) >= ((Integer) this.helmetThreshold.getValue()).intValue()) {
                        this.takeOffSlot(5);
                    }

                    ItemStack chest2 = AutoArmor.mc.player.inventoryContainer.getSlot(6).getStack();

                    if (!chest2.isEmpty && DamageUtil.getRoundedDamage(chest2) >= ((Integer) this.chestThreshold.getValue()).intValue()) {
                        this.takeOffSlot(6);
                    }

                    ItemStack legging2 = AutoArmor.mc.player.inventoryContainer.getSlot(7).getStack();

                    if (!legging2.isEmpty && DamageUtil.getRoundedDamage(legging2) >= ((Integer) this.legThreshold.getValue()).intValue()) {
                        this.takeOffSlot(7);
                    }

                    ItemStack feet2 = AutoArmor.mc.player.inventoryContainer.getSlot(8).getStack();

                    if (!feet2.isEmpty && DamageUtil.getRoundedDamage(feet2) >= ((Integer) this.bootsThreshold.getValue()).intValue()) {
                        this.takeOffSlot(8);
                    }

                    return;
                }

                ItemStack helm = AutoArmor.mc.player.inventoryContainer.getSlot(5).getStack();
                int slot4;

                if (helm.getItem() == Items.AIR && (slot4 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, ((Boolean) this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn())) != -1) {
                    this.getSlotOn(5, slot4);
                }

                int slot3;
                ItemStack chest;

                if ((chest = AutoArmor.mc.player.inventoryContainer.getSlot(6).getStack()).getItem() == Items.AIR) {
                    if (this.taskList.isEmpty()) {
                        if (this.elytraOn && this.elytraTimer.passedMs(500L)) {
                            int elytraSlot = InventoryUtil.findItemInventorySlot(Items.ELYTRA, false, XCarry.getInstance().isOn());

                            if (elytraSlot != -1) {
                                if ((elytraSlot >= 5 || elytraSlot <= 1) && ((Boolean) this.shiftClick.getValue()).booleanValue()) {
                                    this.taskList.add(new InventoryUtil.Task(elytraSlot, true));
                                } else {
                                    this.taskList.add(new InventoryUtil.Task(elytraSlot));
                                    this.taskList.add(new InventoryUtil.Task(6));
                                }

                                if (((Boolean) this.updateController.getValue()).booleanValue()) {
                                    this.taskList.add(new InventoryUtil.Task());
                                }

                                this.elytraTimer.reset();
                            }
                        } else if (!this.elytraOn && (slot3 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, ((Boolean) this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn())) != -1) {
                            this.getSlotOn(6, slot3);
                        }
                    }
                } else if (this.elytraOn && chest.getItem() != Items.ELYTRA && this.elytraTimer.passedMs(500L)) {
                    if (this.taskList.isEmpty()) {
                        slot3 = InventoryUtil.findItemInventorySlot(Items.ELYTRA, false, XCarry.getInstance().isOn());
                        if (slot3 != -1) {
                            this.taskList.add(new InventoryUtil.Task(slot3));
                            this.taskList.add(new InventoryUtil.Task(6));
                            this.taskList.add(new InventoryUtil.Task(slot3));
                            if (((Boolean) this.updateController.getValue()).booleanValue()) {
                                this.taskList.add(new InventoryUtil.Task());
                            }
                        }

                        this.elytraTimer.reset();
                    }
                } else if (!this.elytraOn && chest.getItem() == Items.ELYTRA && this.elytraTimer.passedMs(500L) && this.taskList.isEmpty()) {
                    slot3 = InventoryUtil.findItemInventorySlot(Items.DIAMOND_CHESTPLATE, false, XCarry.getInstance().isOn());
                    if (slot3 == -1 && (slot3 = InventoryUtil.findItemInventorySlot(Items.IRON_CHESTPLATE, false, XCarry.getInstance().isOn())) == -1 && (slot3 = InventoryUtil.findItemInventorySlot(Items.GOLDEN_CHESTPLATE, false, XCarry.getInstance().isOn())) == -1 && (slot3 = InventoryUtil.findItemInventorySlot(Items.CHAINMAIL_CHESTPLATE, false, XCarry.getInstance().isOn())) == -1) {
                        slot3 = InventoryUtil.findItemInventorySlot(Items.LEATHER_CHESTPLATE, false, XCarry.getInstance().isOn());
                    }

                    if (slot3 != -1) {
                        this.taskList.add(new InventoryUtil.Task(slot3));
                        this.taskList.add(new InventoryUtil.Task(6));
                        this.taskList.add(new InventoryUtil.Task(slot3));
                        if (((Boolean) this.updateController.getValue()).booleanValue()) {
                            this.taskList.add(new InventoryUtil.Task());
                        }
                    }

                    this.elytraTimer.reset();
                }

                int slot2;

                if (AutoArmor.mc.player.inventoryContainer.getSlot(7).getStack().getItem() == Items.AIR && (slot2 = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, ((Boolean) this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn())) != -1) {
                    this.getSlotOn(7, slot2);
                }

                if (AutoArmor.mc.player.inventoryContainer.getSlot(8).getStack().getItem() == Items.AIR && (i = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, ((Boolean) this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn())) != -1) {
                    this.getSlotOn(8, i);
                }
            }

            if (this.timer.passedMs((long) ((int) ((float) ((Integer) this.delay.getValue()).intValue() * (((Boolean) this.tps.getValue()).booleanValue() ? Tater.serverManager.getTpsFactor() : 1.0F))))) {
                if (!this.taskList.isEmpty()) {
                    for (i = 0; i < ((Integer) this.actions.getValue()).intValue(); ++i) {
                        InventoryUtil.Task task = (InventoryUtil.Task) this.taskList.poll();

                        if (task != null) {
                            task.run();
                        }
                    }
                }

                this.timer.reset();
            }

        }
    }

    public String getDisplayInfo() {
        return this.elytraOn ? "Elytra" : null;
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            Iterator iterator = InventoryUtil.findEmptySlots(XCarry.getInstance().isOn()).iterator();

            while (iterator.hasNext()) {
                int i = ((Integer) iterator.next()).intValue();

                if (!this.doneSlots.contains(Integer.valueOf(target))) {
                    target = i;
                    this.doneSlots.add(Integer.valueOf(i));
                }
            }

            if (target != -1) {
                if ((target >= 5 || target <= 0) && ((Boolean) this.shiftClick.getValue()).booleanValue()) {
                    this.taskList.add(new InventoryUtil.Task(slot, true));
                } else {
                    this.taskList.add(new InventoryUtil.Task(slot));
                    this.taskList.add(new InventoryUtil.Task(target));
                }

                if (((Boolean) this.updateController.getValue()).booleanValue()) {
                    this.taskList.add(new InventoryUtil.Task());
                }
            }
        }

    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove(Integer.valueOf(target));
            if ((target >= 5 || target <= 0) && ((Boolean) this.shiftClick.getValue()).booleanValue()) {
                this.taskList.add(new InventoryUtil.Task(target, true));
            } else {
                this.taskList.add(new InventoryUtil.Task(target));
                this.taskList.add(new InventoryUtil.Task(slot));
            }

            if (((Boolean) this.updateController.getValue()).booleanValue()) {
                this.taskList.add(new InventoryUtil.Task());
            }
        }

    }

    private boolean isSafe() {
        EntityPlayer closest = EntityUtil.getClosestEnemy((double) ((Integer) this.closestEnemy.getValue()).intValue());

        return closest == null ? true : AutoArmor.mc.player.getDistanceSq(closest) >= MathUtil.square((double) ((Integer) this.closestEnemy.getValue()).intValue());
    }
}
