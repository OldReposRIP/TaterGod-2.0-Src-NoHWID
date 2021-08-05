package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.DeathEvent;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.combat.AutoCrystal;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.manager.FileManager;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG2 extends Module {

    private static final String path = "tater/autogg.txt";
    private final Setting onOwnDeath = this.register(new Setting("OwnDeath", Boolean.valueOf(false)));
    private final Setting greentext = this.register(new Setting("Greentext", Boolean.valueOf(true)));
    private final Setting loadFiles = this.register(new Setting("LoadFiles", Boolean.valueOf(false)));
    private final Setting targetResetTimer = this.register(new Setting("Reset", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(90)));
    private final Setting delay = this.register(new Setting("Delay", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(30)));
    private final Timer timer = new Timer();
    private final Timer cooldownTimer = new Timer();
    public Map targets = new ConcurrentHashMap();
    public List messages = new ArrayList();
    public EntityPlayer cauraTarget;
    private boolean cooldown;

    public AutoGG2() {
        super("AutoGG2", "AutoGG but it works for the new AutoCrystal.", Module.Category.MISC, true, false, false);
        File file = new File("tater/autogg.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }

    public void onEnable() {
        this.loadMessages();
        this.timer.reset();
        this.cooldownTimer.reset();
    }

    public void onTick() {
        if (((Boolean) this.loadFiles.getValue()).booleanValue()) {
            this.loadMessages();
            Command.sendMessage("<AutoGG> Loaded messages.");
            this.loadFiles.setValue(Boolean.valueOf(false));
        }

        if (AutoCrystal.target != null && this.cauraTarget != AutoCrystal.target) {
            this.cauraTarget = AutoCrystal.target;
        }

        if (!this.cooldown) {
            this.cooldownTimer.reset();
        }

        if (this.cooldownTimer.passedS((double) ((Integer) this.delay.getValue()).intValue()) && this.cooldown) {
            this.cooldown = false;
            this.cooldownTimer.reset();
        }

        if (AutoCrystal.target != null) {
            this.targets.put(AutoCrystal.target, Integer.valueOf((int) (this.timer.getPassedTimeMs() / 1000L)));
        }

        this.targets.replaceAll((p, v) -> {
            return Integer.valueOf((int) (this.timer.getPassedTimeMs() / 1000L));
        });
        Iterator iterator = this.targets.keySet().iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (((Integer) this.targets.get(player)).intValue() > ((Integer) this.targetResetTimer.getValue()).intValue()) {
                this.targets.remove(player);
                this.timer.reset();
            }
        }

    }

    @SubscribeEvent
    public void onEntityDeath(DeathEvent event) {
        if (this.targets.containsKey(event.player) && !this.cooldown) {
            this.announceDeath(event.player);
            this.cooldown = true;
            this.targets.remove(event.player);
        }

        if (event.player == this.cauraTarget && !this.cooldown) {
            this.announceDeath(event.player);
            this.cooldown = true;
        }

        if (event.player == AutoGG.mc.player && ((Boolean) this.onOwnDeath.getValue()).booleanValue()) {
            this.announceDeath(event.player);
            this.cooldown = true;
        }

    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getTarget() instanceof EntityPlayer && !Tater.friendManager.isFriend(event.getEntityPlayer())) {
            this.targets.put((EntityPlayer) event.getTarget(), Integer.valueOf(0));
        }

    }

    @SubscribeEvent
    public void onSendAttackPacket(PacketEvent.Send event) {
        CPacketUseEntity packet;

        if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == Action.ATTACK && packet.getEntityFromWorld(AutoGG.mc.world) instanceof EntityPlayer && !Tater.friendManager.isFriend((EntityPlayer) packet.getEntityFromWorld(AutoGG.mc.world))) {
            this.targets.put((EntityPlayer) packet.getEntityFromWorld(AutoGG.mc.world), Integer.valueOf(0));
        }

    }

    public void loadMessages() {
        this.messages = FileManager.readTextFileAllLines("tater/autogg.txt");
    }

    public String getRandomMessage() {
        this.loadMessages();
        Random rand = new Random();

        return this.messages.size() == 0 ? "<player> Got Raped By TaterGod.CC" : (this.messages.size() == 1 ? (String) this.messages.get(0) : (String) this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1)));
    }

    public void announceDeath(EntityPlayer target) {
        AutoGG.mc.player.connection.sendPacket(new CPacketChatMessage((((Boolean) this.greentext.getValue()).booleanValue() ? ">" : "") + this.getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
    }
}
