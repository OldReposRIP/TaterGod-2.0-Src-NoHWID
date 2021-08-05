package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.RenderUtil;
import java.awt.Color;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

public class BlockHighlight extends Module {

    private final Setting lineWidth = this.register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting rainbow = this.register(new Setting("Rainbow", Boolean.valueOf(false)));
    private final Setting rainbowhue = this.register(new Setting("RainbowHue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.rainbow.getValue()).booleanValue();
    }));

    public BlockHighlight() {
        super("BlockHighlight", "Highlights the block u look at.", Module.Category.RENDER, false, false, false);
    }

    public void onRender3D(Render3DEvent event) {
        RayTraceResult ray = BlockHighlight.mc.objectMouseOver;

        if (ray != null && ray.typeOfHit == Type.BLOCK) {
            BlockPos blockpos = ray.getBlockPos();

            RenderUtil.drawBlockOutline(blockpos, ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) this.rainbowhue.getValue()).intValue()) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), false);
        }

    }
}
