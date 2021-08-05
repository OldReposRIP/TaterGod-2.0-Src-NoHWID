package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.PlayerJumpEvent;
import club.tater.tatergod.features.modules.player.TpsSync;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase {

    EntityPlayer player;

    public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn);
    }

    @Inject(
        method = { "getCooldownPeriod"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void getCooldownPeriodHook(CallbackInfoReturnable callbackInfoReturnable) {
        if (TpsSync.getInstance().isOn() && ((Boolean) TpsSync.getInstance().attack.getValue()).booleanValue()) {
            callbackInfoReturnable.setReturnValue(Float.valueOf((float) (1.0D / this.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue() * 20.0D * (double) Tater.serverManager.getTpsFactor())));
        }

    }

    @Inject(
        method = { "jump"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void onJump(CallbackInfo ci) {
        if (Minecraft.getMinecraft().player.getName() == this.getName()) {
            MinecraftForge.EVENT_BUS.post(new PlayerJumpEvent(this.motionX, this.motionY));
        }

    }
}
