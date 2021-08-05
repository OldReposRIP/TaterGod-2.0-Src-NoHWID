package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class FakePlayer extends Module {

    public Setting fakename = this.register(new Setting("Name", "taterontop"));
    private EntityOtherPlayerMP clonedPlayer;

    public FakePlayer() {
        super("FakePlayer", "Spawns a literal fake player", Module.Category.PLAYER, false, false, false);
    }

    public void onEnable() {
        Command.sendMessage("FakePlayer by the name of " + this.fakename.getValueAsString() + " has been spawned!");
        if (FakePlayer.mc.player != null && !FakePlayer.mc.player.isDead) {
            this.clonedPlayer = new EntityOtherPlayerMP(FakePlayer.mc.world, new GameProfile(UUID.fromString("c7dd2d8e-3707-4daf-b4f2-c71bffc1eeab"), this.fakename.getValueAsString()));
            this.clonedPlayer.copyLocationAndAnglesFrom(FakePlayer.mc.player);
            this.clonedPlayer.rotationYawHead = FakePlayer.mc.player.rotationYawHead;
            this.clonedPlayer.rotationYaw = FakePlayer.mc.player.rotationYaw;
            this.clonedPlayer.rotationPitch = FakePlayer.mc.player.rotationPitch;
            this.clonedPlayer.setGameType(GameType.SURVIVAL);
            this.clonedPlayer.setHealth(20.0F);
            FakePlayer.mc.world.addEntityToWorld(-12345, this.clonedPlayer);
            this.clonedPlayer.onLivingUpdate();
        } else {
            this.disable();
        }
    }

    public void onDisable() {
        if (FakePlayer.mc.world != null) {
            FakePlayer.mc.world.removeEntityFromWorld(-12345);
        }

    }

    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
        if (this.isEnabled()) {
            this.disable();
        }

    }
}
