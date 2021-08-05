package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.KeyPressedEvent;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlowDown extends Module {

    private static final KeyBinding[] keys = new KeyBinding[] { NoSlowDown.mc.gameSettings.keyBindForward, NoSlowDown.mc.gameSettings.keyBindBack, NoSlowDown.mc.gameSettings.keyBindLeft, NoSlowDown.mc.gameSettings.keyBindRight, NoSlowDown.mc.gameSettings.keyBindJump, NoSlowDown.mc.gameSettings.keyBindSprint};
    private static NoSlowDown INSTANCE = new NoSlowDown();
    public final Setting webHorizontalFactor = this.register(new Setting("WebHSpeed", Double.valueOf(2.0D), Double.valueOf(0.0D), Double.valueOf(100.0D)));
    public final Setting webVerticalFactor = this.register(new Setting("WebVSpeed", Double.valueOf(2.0D), Double.valueOf(0.0D), Double.valueOf(100.0D)));
    public Setting guiMove = this.register(new Setting("GuiMove", Boolean.valueOf(true)));
    public Setting noSlow = this.register(new Setting("NoSlow", Boolean.valueOf(true)));
    public Setting soulSand = this.register(new Setting("SoulSand", Boolean.valueOf(true)));
    public Setting strict = this.register(new Setting("Strict", Boolean.valueOf(false)));
    public Setting sneakPacket = this.register(new Setting("SneakPacket", Boolean.valueOf(false)));
    public Setting endPortal = this.register(new Setting("EndPortal", Boolean.valueOf(false)));
    public Setting webs = this.register(new Setting("Webs", Boolean.valueOf(false)));
    private boolean sneaking = false;

    public NoSlowDown() {
        super("NoSlowDown", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    public static NoSlowDown getInstance() {
        if (NoSlowDown.INSTANCE == null) {
            NoSlowDown.INSTANCE = new NoSlowDown();
        }

        return NoSlowDown.INSTANCE;
    }

    private void setInstance() {
        NoSlowDown.INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) this.guiMove.getValue()).booleanValue()) {
            KeyBinding[] item;
            int i;
            int j;
            KeyBinding bind;

            if (!(NoSlowDown.mc.currentScreen instanceof GuiOptions) && !(NoSlowDown.mc.currentScreen instanceof GuiVideoSettings) && !(NoSlowDown.mc.currentScreen instanceof GuiScreenOptionsSounds) && !(NoSlowDown.mc.currentScreen instanceof GuiContainer) && !(NoSlowDown.mc.currentScreen instanceof GuiIngameMenu)) {
                if (NoSlowDown.mc.currentScreen == null) {
                    item = NoSlowDown.keys;
                    i = item.length;

                    for (j = 0; j < i; ++j) {
                        bind = item[j];
                        if (!Keyboard.isKeyDown(bind.getKeyCode())) {
                            KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                        }
                    }
                }
            } else {
                item = NoSlowDown.keys;
                i = item.length;

                for (j = 0; j < i; ++j) {
                    bind = item[j];
                    KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
                }
            }
        }

        if (((Boolean) this.webs.getValue()).booleanValue() && ((Flight) Tater.moduleManager.getModuleByClass(Flight.class)).isDisabled() && ((PacketFly) Tater.moduleManager.getModuleByClass(PacketFly.class)).isDisabled() && NoSlowDown.mc.player.isInWeb) {
            NoSlowDown.mc.player.motionX *= ((Double) this.webHorizontalFactor.getValue()).doubleValue();
            NoSlowDown.mc.player.motionZ *= ((Double) this.webHorizontalFactor.getValue()).doubleValue();
            NoSlowDown.mc.player.motionY *= ((Double) this.webVerticalFactor.getValue()).doubleValue();
        }

        Item item = NoSlowDown.mc.player.getActiveItemStack().getItem();

        if (this.sneaking && !NoSlowDown.mc.player.isHandActive() && ((Boolean) this.sneakPacket.getValue()).booleanValue()) {
            NoSlowDown.mc.player.connection.sendPacket(new CPacketEntityAction(NoSlowDown.mc.player, Action.STOP_SNEAKING));
            this.sneaking = false;
        }

    }

    @SubscribeEvent
    public void onUseItem(RightClickItem event) {
        Item item = NoSlowDown.mc.player.getHeldItem(event.getHand()).getItem();

        if ((item instanceof ItemFood || item instanceof ItemBow || item instanceof ItemPotion && ((Boolean) this.sneakPacket.getValue()).booleanValue()) && !this.sneaking) {
            NoSlowDown.mc.player.connection.sendPacket(new CPacketEntityAction(NoSlowDown.mc.player, Action.START_SNEAKING));
            this.sneaking = true;
        }

    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (((Boolean) this.noSlow.getValue()).booleanValue() && NoSlowDown.mc.player.isHandActive() && !NoSlowDown.mc.player.isRiding()) {
            MovementInput movementinput = event.getMovementInput();

            movementinput.moveStrafe *= 5.0F;
            movementinput = event.getMovementInput();
            movementinput.moveForward *= 5.0F;
        }

    }

    @SubscribeEvent
    public void onKeyEvent(KeyPressedEvent event) {
        if (((Boolean) this.guiMove.getValue()).booleanValue() && event.getStage() == 0 && !(NoSlowDown.mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }

    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && ((Boolean) this.strict.getValue()).booleanValue() && ((Boolean) this.noSlow.getValue()).booleanValue() && NoSlowDown.mc.player.isHandActive() && !NoSlowDown.mc.player.isRiding()) {
            NoSlowDown.mc.player.connection.sendPacket(new CPacketPlayerDigging(net.minecraft.network.play.client.CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(NoSlowDown.mc.player.posX), Math.floor(NoSlowDown.mc.player.posY), Math.floor(NoSlowDown.mc.player.posZ)), EnumFacing.DOWN));
        }

    }
}
