package club.tater.tatergod.manager;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.ConnectionEvent;
import club.tater.tatergod.event.events.DeathEvent;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.Render2DEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.event.events.TotemPopEvent;
import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.client.HUD;
import club.tater.tatergod.features.modules.misc.PopCounter;
import club.tater.tatergod.util.Timer;
import com.google.common.base.Strings;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import org.lwjgl.input.Keyboard;

public class EventManager extends Feature {

    private final Timer logoutTimer = new Timer();
    private final AtomicBoolean tickOngoing = new AtomicBoolean(false);

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean ticksOngoing() {
        return this.tickOngoing.get();
    }

    public void onUnload() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onUpdate(LivingUpdateEvent event) {
        if (!fullNullCheck() && event.getEntity().getEntityWorld().isRemote && event.getEntityLiving().equals(EventManager.mc.player)) {
            Tater.inventoryManager.update();
            Tater.moduleManager.onUpdate();
            if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
                Tater.moduleManager.sortModules(true);
            } else {
                Tater.moduleManager.sortModulesABC();
            }
        }

    }

    @SubscribeEvent
    public void onClientConnect(ClientConnectedToServerEvent event) {
        this.logoutTimer.reset();
        Tater.moduleManager.onLogin();
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
        Tater.moduleManager.onLogout();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        if (!fullNullCheck()) {
            Tater.moduleManager.onTick();
            Iterator iterator = EventManager.mc.world.playerEntities.iterator();

            while (iterator.hasNext()) {
                EntityPlayer player = (EntityPlayer) iterator.next();

                if (player != null && player.getHealth() <= 0.0F) {
                    MinecraftForge.EVENT_BUS.post(new DeathEvent(player));
                    PopCounter.getInstance().onDeath(player);
                }
            }

        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (!fullNullCheck()) {
            if (event.getStage() == 0) {
                Tater.speedManager.updateValues();
                Tater.rotationManager.updateRotations();
                Tater.positionManager.updatePosition();
            }

            if (event.getStage() == 1) {
                Tater.rotationManager.restoreRotations();
                Tater.positionManager.restorePosition();
            }

        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0) {
            Tater.serverManager.onPacketReceived();
            if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();

                if (packet.getOpCode() == 35 && packet.getEntity(EventManager.mc.world) instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) packet.getEntity(EventManager.mc.world);

                    MinecraftForge.EVENT_BUS.post(new TotemPopEvent(player));
                    PopCounter.getInstance().onTotemPop(player);
                }
            }

            if (event.getPacket() instanceof SPacketPlayerListItem && !fullNullCheck() && this.logoutTimer.passedS(1.0D)) {
                SPacketPlayerListItem packet1 = (SPacketPlayerListItem) event.getPacket();

                if (!Action.ADD_PLAYER.equals(packet1.getAction()) && !Action.REMOVE_PLAYER.equals(packet1.getAction())) {
                    return;
                }

                packet1.getEntries().stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).forEach(accept<invokedynamic>(packet1));
            }

            if (event.getPacket() instanceof SPacketTimeUpdate) {
                Tater.serverManager.update();
            }

        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!event.isCanceled()) {
            EventManager.mc.profiler.startSection("tater");
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            GlStateManager.disableDepth();
            GlStateManager.glLineWidth(1.0F);
            Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());

            Tater.moduleManager.onRender3D(render3dEvent);
            GlStateManager.glLineWidth(1.0F);
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.enableCull();
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
            EventManager.mc.profiler.endSection();
        }
    }

    @SubscribeEvent
    public void renderHUD(Post event) {
        if (event.getType() == ElementType.HOTBAR) {
            Tater.textManager.updateResolution();
        }

    }

    @SubscribeEvent(
        priority = EventPriority.LOW
    )
    public void onRenderGameOverlayEvent(Text event) {
        if (event.getType().equals(ElementType.TEXT)) {
            ScaledResolution resolution = new ScaledResolution(EventManager.mc);
            Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);

            Tater.moduleManager.onRender2D(render2DEvent);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }

    }

    @SubscribeEvent(
        priority = EventPriority.NORMAL,
        receiveCanceled = true
    )
    public void onKeyInput(KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            Tater.moduleManager.onKeyPressed(Keyboard.getEventKey());
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST
    )
    public void onChatSent(ClientChatEvent event) {
        if (event.getMessage().startsWith(Command.getCommandPrefix())) {
            event.setCanceled(true);

            try {
                EventManager.mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
                if (event.getMessage().length() > 1) {
                    Tater.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
                } else {
                    Command.sendMessage("Please enter a command.");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                Command.sendMessage(ChatFormatting.RED + "An error occurred while running this command. Check the log!");
            }
        }

    }

    private static void lambda$onPacketReceive$1(SPacketPlayerListItem packet, AddPlayerData data) {
        UUID id = data.getProfile().getId();

        switch (packet.getAction()) {
        case ADD_PLAYER:
            String name = data.getProfile().getName();

            MinecraftForge.EVENT_BUS.post(new ConnectionEvent(0, id, name));
            break;

        case REMOVE_PLAYER:
            EntityPlayer entity = EventManager.mc.world.getPlayerEntityByUUID(id);

            if (entity != null) {
                String logoutName = entity.getName();

                MinecraftForge.EVENT_BUS.post(new ConnectionEvent(1, entity, id, logoutName));
            } else {
                MinecraftForge.EVENT_BUS.post(new ConnectionEvent(2, id, (String) null));
            }
        }

    }

    private static boolean lambda$onPacketReceive$0(AddPlayerData data) {
        return !Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null;
    }
}
