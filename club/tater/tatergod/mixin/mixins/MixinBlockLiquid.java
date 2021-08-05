package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.features.modules.player.LiquidInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ BlockLiquid.class})
public class MixinBlockLiquid extends Block {

    protected MixinBlockLiquid(Material materialIn) {
        super(materialIn);
    }

    @Inject(
        method = { "canCollideCheck"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void canCollideCheckHook(IBlockState blockState, boolean hitIfLiquid, CallbackInfoReturnable info) {
        info.setReturnValue(Boolean.valueOf(hitIfLiquid && ((Integer) blockState.getValue(BlockLiquid.LEVEL)).intValue() == 0 || LiquidInteract.getInstance().isOn()));
    }
}
