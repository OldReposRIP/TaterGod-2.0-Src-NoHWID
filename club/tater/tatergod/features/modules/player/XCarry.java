package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.event.events.ClientEvent;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.ReflectionUtil;
import club.tater.tatergod.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class XCarry extends Module {

    private static XCarry INSTANCE = new XCarry();
    private final Setting simpleMode = this.register(new Setting("Simple", Boolean.valueOf(false)));
    private final Setting autoStore = this.register(new Setting("AutoDuel", new Bind(-1)));
    private final Setting obbySlot = this.register(new Setting("ObbySlot", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(9), test<invokedynamic>(this)));
    private final Setting slot1 = this.register(new Setting("Slot1", Integer.valueOf(22), Integer.valueOf(9), Integer.valueOf(44), test<invokedynamic>(this)));
    private final Setting slot2 = this.register(new Setting("Slot2", Integer.valueOf(23), Integer.valueOf(9), Integer.valueOf(44), test<invokedynamic>(this)));
    private final Setting slot3 = this.register(new Setting("Slot3", Integer.valueOf(24), Integer.valueOf(9), Integer.valueOf(44), test<invokedynamic>(this)));
    private final Setting tasks = this.register(new Setting("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12), test<invokedynamic>(this)));
    private final Setting store = this.register(new Setting("Store", Boolean.valueOf(false)));
    private final Setting shiftClicker = this.register(new Setting("ShiftClick", Boolean.valueOf(false)));
    private final Setting withShift = this.register(new Setting("WithShift", Boolean.valueOf(true), test<invokedynamic>(this)));
    private final Setting keyBind = this.register(new Setting("ShiftBind", new Bind(-1), test<invokedynamic>(this)));
    private final AtomicBoolean guiNeedsClose = new AtomicBoolean(false);
    private final Queue taskList = new ConcurrentLinkedQueue();
    private GuiInventory openedGui = null;
    private boolean guiCloseGuard = false;
    private boolean autoDuelOn = false;
    private boolean obbySlotDone = false;
    private boolean slot1done = false;
    private boolean slot2done = false;
    private boolean slot3done = false;
    private List doneSlots = new ArrayList();

    public XCarry() {
        super("XCarry", "Uses the crafting inventory for storage", Module.Category.PLAYER, true, false, false);
        this.setInstance();
    }

    public static XCarry getInstance() {
        if (XCarry.INSTANCE == null) {
            XCarry.INSTANCE = new XCarry();
        }

        return XCarry.INSTANCE;
    }

    private void setInstance() {
        XCarry.INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) this.shiftClicker.getValue()).booleanValue() && XCarry.mc.currentScreen instanceof GuiInventory) {
            boolean task = ((Bind) this.keyBind.getValue()).getKey() != -1 && Keyboard.isKeyDown(((Bind) this.keyBind.getValue()).getKey()) && !Keyboard.isKeyDown(42);
            Slot i;

            if ((Keyboard.isKeyDown(42) && ((Boolean) this.withShift.getValue()).booleanValue() || task) && Mouse.isButtonDown(0) && (i = ((GuiInventory) XCarry.mc.currentScreen).getSlotUnderMouse()) != null && InventoryUtil.getEmptyXCarry() != -1) {
                int slotNumber = i.slotNumber;

                if (slotNumber > 4 && task) {
                    this.taskList.add(new InventoryUtil.Task(slotNumber));
                    this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
                } else if (slotNumber > 4 && ((Boolean) this.withShift.getValue()).booleanValue()) {
                    boolean isHotBarFull = true;
                    boolean isInvFull = true;
                    Iterator iterator = InventoryUtil.findEmptySlots(false).iterator();

                    while (iterator.hasNext()) {
                        int i1 = ((Integer) iterator.next()).intValue();

                        if (i1 > 4 && i1 < 36) {
                            isInvFull = false;
                        } else if (i1 > 35 && i1 < 45) {
                            isHotBarFull = false;
                        }
                    }

                    if (slotNumber > 35 && slotNumber < 45) {
                        if (isInvFull) {
                            this.taskList.add(new InventoryUtil.Task(slotNumber));
                            this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
                        }
                    } else if (isHotBarFull) {
                        this.taskList.add(new InventoryUtil.Task(slotNumber));
                        this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
                    }
                }
            }
        }

        if (this.autoDuelOn) {
            this.doneSlots = new ArrayList();
            if (InventoryUtil.getEmptyXCarry() == -1 || this.obbySlotDone && this.slot1done && this.slot2done && this.slot3done) {
                this.autoDuelOn = false;
            }

            if (this.autoDuelOn) {
                if (!this.obbySlotDone && !XCarry.mc.player.inventory.getStackInSlot(((Integer) this.obbySlot.getValue()).intValue() - 1).isEmpty) {
                    this.addTasks(36 + ((Integer) this.obbySlot.getValue()).intValue() - 1);
                }

                this.obbySlotDone = true;
                if (!this.slot1done && !((Slot) XCarry.mc.player.inventoryContainer.inventorySlots.get(((Integer) this.slot1.getValue()).intValue())).getStack().isEmpty) {
                    this.addTasks(((Integer) this.slot1.getValue()).intValue());
                }

                this.slot1done = true;
                if (!this.slot2done && !((Slot) XCarry.mc.player.inventoryContainer.inventorySlots.get(((Integer) this.slot2.getValue()).intValue())).getStack().isEmpty) {
                    this.addTasks(((Integer) this.slot2.getValue()).intValue());
                }

                this.slot2done = true;
                if (!this.slot3done && !((Slot) XCarry.mc.player.inventoryContainer.inventorySlots.get(((Integer) this.slot3.getValue()).intValue())).getStack().isEmpty) {
                    this.addTasks(((Integer) this.slot3.getValue()).intValue());
                }

                this.slot3done = true;
            }
        } else {
            this.obbySlotDone = false;
            this.slot1done = false;
            this.slot2done = false;
            this.slot3done = false;
        }

        if (!this.taskList.isEmpty()) {
            for (int i = 0; i < ((Integer) this.tasks.getValue()).intValue(); ++i) {
                InventoryUtil.Task inventoryutil_task = (InventoryUtil.Task) this.taskList.poll();

                if (inventoryutil_task != null) {
                    inventoryutil_task.run();
                }
            }
        }

    }

    private void addTasks(int slot) {
        if (InventoryUtil.getEmptyXCarry() != -1) {
            int xcarrySlot = InventoryUtil.getEmptyXCarry();

            if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                ++xcarrySlot;
                if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                    ++xcarrySlot;
                    if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                        ++xcarrySlot;
                        if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
                            return;
                        }
                    }
                }
            }

            if (xcarrySlot > 4) {
                return;
            }

            this.doneSlots.add(Integer.valueOf(xcarrySlot));
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task(xcarrySlot));
            this.taskList.add(new InventoryUtil.Task());
        }

    }

    public void onDisable() {
        if (!fullNullCheck()) {
            if (!((Boolean) this.simpleMode.getValue()).booleanValue()) {
                this.closeGui();
                this.close();
            } else {
                XCarry.mc.player.connection.sendPacket(new CPacketCloseWindow(XCarry.mc.player.inventoryContainer.windowId));
            }
        }

    }

    public void onLogout() {
        this.onDisable();
    }

    @SubscribeEvent
    public void onCloseGuiScreen(PacketEvent.Send event) {
        if (((Boolean) this.simpleMode.getValue()).booleanValue() && event.getPacket() instanceof CPacketCloseWindow) {
            CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();

            if (packet.windowId == XCarry.mc.player.inventoryContainer.windowId) {
                event.setCanceled(true);
            }
        }

    }

    @SubscribeEvent(
        priority = EventPriority.LOWEST
    )
    public void onGuiOpen(GuiOpenEvent event) {
        if (!((Boolean) this.simpleMode.getValue()).booleanValue()) {
            if (this.guiCloseGuard) {
                event.setCanceled(true);
            } else if (event.getGui() instanceof GuiInventory) {
                this.openedGui = this.createGuiWrapper((GuiInventory) event.getGui());
                event.setGui(this.openedGui);
                this.guiNeedsClose.set(false);
            }
        }

    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            Setting setting = event.getSetting();
            String settingname = event.getSetting().getName();

            if (setting.equals(this.simpleMode) && setting.getPlannedValue() != setting.getValue()) {
                this.disable();
            } else if (settingname.equalsIgnoreCase("Store")) {
                event.setCanceled(true);
                this.autoDuelOn = !this.autoDuelOn;
                Command.sendMessage("<XCarry> §aAutostoring...");
            }
        }

    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(XCarry.mc.currentScreen instanceof Gui) && ((Bind) this.autoStore.getValue()).getKey() == Keyboard.getEventKey()) {
            this.autoDuelOn = !this.autoDuelOn;
            Command.sendMessage("<XCarry> §aAutostoring...");
        }

    }

    private void close() {
        this.openedGui = null;
        this.guiNeedsClose.set(false);
        this.guiCloseGuard = false;
    }

    private void closeGui() {
        if (this.guiNeedsClose.compareAndSet(true, false) && !fullNullCheck()) {
            this.guiCloseGuard = true;
            XCarry.mc.player.closeScreen();
            if (this.openedGui != null) {
                this.openedGui.onGuiClosed();
                this.openedGui = null;
            }

            this.guiCloseGuard = false;
        }

    }

    private GuiInventory createGuiWrapper(GuiInventory gui) {
        try {
            XCarry.GuiInventoryWrapper e = new XCarry.GuiInventoryWrapper();

            ReflectionUtil.copyOf(gui, e);
            return e;
        } catch (NoSuchFieldException | IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
            return null;
        }
    }

    private boolean lambda$new$6(Object v) {
        return ((Boolean) this.shiftClicker.getValue()).booleanValue();
    }

    private boolean lambda$new$5(Object v) {
        return ((Boolean) this.shiftClicker.getValue()).booleanValue();
    }

    private boolean lambda$new$4(Object v) {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }

    private boolean lambda$new$3(Object v) {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }

    private boolean lambda$new$2(Object v) {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }

    private boolean lambda$new$1(Object v) {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }

    private boolean lambda$new$0(Object v) {
        return ((Bind) this.autoStore.getValue()).getKey() != -1;
    }

    private class GuiInventoryWrapper extends GuiInventory {

        GuiInventoryWrapper() {
            super(Util.mc.player);
        }

        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            if (XCarry.this.isEnabled() && (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))) {
                XCarry.this.guiNeedsClose.set(true);
                this.mc.displayGuiScreen((GuiScreen) null);
            } else {
                super.keyTyped(typedChar, keyCode);
            }

        }

        public void onGuiClosed() {
            if (XCarry.this.guiCloseGuard || !XCarry.this.isEnabled()) {
                super.onGuiClosed();
            }

        }
    }
}
