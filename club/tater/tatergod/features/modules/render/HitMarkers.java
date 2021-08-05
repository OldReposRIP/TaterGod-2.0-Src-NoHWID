package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.Render2DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class HitMarkers extends Module {

    public final ResourceLocation image = new ResourceLocation("hitmarker.png");
    public Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    public Setting thickness = this.register(new Setting("Thickness", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(6)));
    public Setting time = this.register(new Setting("Time", Double.valueOf(20.0D), Double.valueOf(1.0D), Double.valueOf(50.0D)));
    private int renderTicks = 100;

    public HitMarkers() {
        super("HitMarkers", "hitmarker thingys", Module.Category.RENDER, false, false, false);
    }

    public void onRender2D(Render2DEvent event) {
        if ((double) this.renderTicks < ((Double) this.time.getValue()).doubleValue()) {
            new ScaledResolution(HitMarkers.mc);
            this.drawHitMarkers();
        }

    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().equals(HitMarkers.mc.player)) {
            this.renderTicks = 0;
        }
    }

    @SubscribeEvent
    public void onTickClientTick(TickEvent event) {
        ++this.renderTicks;
    }

    public void drawHitMarkers() {
        ScaledResolution resolution = new ScaledResolution(HitMarkers.mc);

        RenderUtil.drawLine((float) resolution.getScaledWidth() / 2.0F - 4.0F, (float) resolution.getScaledHeight() / 2.0F - 4.0F, (float) resolution.getScaledWidth() / 2.0F - 8.0F, (float) resolution.getScaledHeight() / 2.0F - 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
        RenderUtil.drawLine((float) resolution.getScaledWidth() / 2.0F + 4.0F, (float) resolution.getScaledHeight() / 2.0F - 4.0F, (float) resolution.getScaledWidth() / 2.0F + 8.0F, (float) resolution.getScaledHeight() / 2.0F - 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
        RenderUtil.drawLine((float) resolution.getScaledWidth() / 2.0F - 4.0F, (float) resolution.getScaledHeight() / 2.0F + 4.0F, (float) resolution.getScaledWidth() / 2.0F - 8.0F, (float) resolution.getScaledHeight() / 2.0F + 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
        RenderUtil.drawLine((float) resolution.getScaledWidth() / 2.0F + 4.0F, (float) resolution.getScaledHeight() / 2.0F + 4.0F, (float) resolution.getScaledWidth() / 2.0F + 8.0F, (float) resolution.getScaledHeight() / 2.0F + 8.0F, (float) ((Integer) this.thickness.getValue()).intValue(), (new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue())).getRGB());
    }
}
