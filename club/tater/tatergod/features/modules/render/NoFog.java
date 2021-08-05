package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFog extends Module {

    public NoFog() {
        super("NoFog", "Removes fog", Module.Category.RENDER, false, false, false);
    }

    @SubscribeEvent
    public void fog_density(FogDensity event) {
        event.setDensity(0.0F);
        event.setCanceled(true);
    }
}
