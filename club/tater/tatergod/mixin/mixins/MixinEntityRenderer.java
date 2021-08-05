package club.tater.tatergod.mixin.mixins;

import club.tater.tatergod.features.modules.misc.NoHitBox;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ EntityRenderer.class})
public class MixinEntityRenderer {

    @Redirect(
        method = { "getMouseOver"},
        at =             @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"
            )
    )
    public List getEntitiesInAABBexcluding(WorldClient worldClient, Entity entityIn, AxisAlignedBB boundingBox, Predicate predicate) {
        return (List) (NoHitBox.getINSTANCE().isOn() && (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && ((Boolean) NoHitBox.getINSTANCE().pickaxe.getValue()).booleanValue() || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && ((Boolean) NoHitBox.getINSTANCE().crystal.getValue()).booleanValue() || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && ((Boolean) NoHitBox.getINSTANCE().gapple.getValue()).booleanValue() || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.FLINT_AND_STEEL || Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.TNT_MINECART) ? new ArrayList() : worldClient.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate));
    }
}
