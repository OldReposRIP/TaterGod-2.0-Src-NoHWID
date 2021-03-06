package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.BlockEvent;
import club.tater.tatergod.event.events.ProcessRightClickBlockEvent;
import club.tater.tatergod.features.modules.player.TpsSync;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ PlayerControllerMP.class})
public class MixinPlayerControllerMP {

    @Redirect(
        method = { "onPlayerDamageBlock"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/block/state/IBlockState;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)F"
            )
    )
    public float getPlayerRelativeBlockHardnessHook(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos) {
        return state.getPlayerRelativeBlockHardness(player, worldIn, pos) * (TpsSync.getInstance().isOn() && ((Boolean) TpsSync.getInstance().mining.getValue()).booleanValue() ? 1.0F / Tater.serverManager.getTpsFactor() : 1.0F);
    }

    @Inject(
        method = { "clickBlock"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable info) {
        BlockEvent event = new BlockEvent(3, pos, face);

        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(
        method = { "onPlayerDamageBlock"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable info) {
        BlockEvent event = new BlockEvent(4, pos, face);

        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(
        method = { "processRightClickBlock"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void processRightClickBlock(EntityPlayerSP player, WorldClient worldIn, BlockPos pos, EnumFacing direction, Vec3d vec, EnumHand hand, CallbackInfoReturnable cir) {
        ProcessRightClickBlockEvent event = new ProcessRightClickBlockEvent(pos, hand, Minecraft.instance.player.getHeldItem(hand));

        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.cancel();
        }

    }
}
