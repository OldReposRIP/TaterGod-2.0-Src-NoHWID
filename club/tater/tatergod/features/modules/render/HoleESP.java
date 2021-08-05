package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.RenderUtil;
import java.awt.Color;
import java.util.function.Predicate;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleESP extends Module {

    private static HoleESP INSTANCE = new HoleESP();
    private final Setting range = this.register(new Setting("RangeX", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(20)));
    private final Setting rangeY = this.register(new Setting("RangeY", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(20)));
    private final Setting red = this.register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final int currentAlpha = 0;
    public Setting renderOwn = this.register(new Setting("RenderOwn", Boolean.valueOf(true)));
    public Setting fov = this.register(new Setting("InFov", Boolean.valueOf(true)));
    public Setting rainbow = this.register(new Setting("Rainbow", Boolean.valueOf(false)));
    public Setting box = this.register(new Setting("Box", Boolean.valueOf(true)));
    private final Setting boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.box.getValue()).booleanValue();
    }));
    public Setting gradientBox = this.register(new Setting("Gradient", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.box.getValue()).booleanValue();
    }));
    public Setting invertGradientBox = this.register(new Setting("ReverseGradient", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.gradientBox.getValue()).booleanValue();
    }));
    public Setting outline = this.register(new Setting("Outline", Boolean.valueOf(true)));
    private final Setting lineWidth = this.register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    public Setting gradientOutline = this.register(new Setting("GradientOutline", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    public Setting invertGradientOutline = this.register(new Setting("ReverseOutline", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.gradientOutline.getValue()).booleanValue();
    }));
    public Setting height = this.register(new Setting("Height", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
    public Setting safeColor = this.register(new Setting("BedrockColor", Boolean.valueOf(false)));
    private final Setting safeRed = this.register(new Setting("BedrockRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private final Setting safeGreen = this.register(new Setting("BedrockGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private final Setting safeBlue = this.register(new Setting("BedrockBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private final Setting safeAlpha = this.register(new Setting("BedrockAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    public Setting customOutline = this.register(new Setting("CustomLine", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting cRed = this.register(new Setting("OL-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting cGreen = this.register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting cBlue = this.register(new Setting("OL-Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting cAlpha = this.register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting safecRed = this.register(new Setting("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private final Setting safecGreen = this.register(new Setting("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private final Setting safecBlue = this.register(new Setting("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));
    private final Setting safecAlpha = this.register(new Setting("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.safeColor.getValue()).booleanValue();
    }));

    public HoleESP() {
        super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static HoleESP getInstance() {
        if (HoleESP.INSTANCE == null) {
            HoleESP.INSTANCE = new HoleESP();
        }

        return HoleESP.INSTANCE;
    }

    private void setInstance() {
        HoleESP.INSTANCE = this;
    }

    public void onRender3D(Render3DEvent event) {
        assert HoleESP.mc.renderViewEntity != null;

        Vec3i playerPos = new Vec3i(HoleESP.mc.renderViewEntity.posX, HoleESP.mc.renderViewEntity.posY, HoleESP.mc.renderViewEntity.posZ);

        for (int x = playerPos.getX() - ((Integer) this.range.getValue()).intValue(); x < playerPos.getX() + ((Integer) this.range.getValue()).intValue(); ++x) {
            for (int z = playerPos.getZ() - ((Integer) this.range.getValue()).intValue(); z < playerPos.getZ() + ((Integer) this.range.getValue()).intValue(); ++z) {
                for (int y = playerPos.getY() + ((Integer) this.rangeY.getValue()).intValue(); y > playerPos.getY() - ((Integer) this.rangeY.getValue()).intValue(); --y) {
                    BlockPos pos = new BlockPos(x, y, z);

                    if (HoleESP.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) && HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR) && (!pos.equals(new BlockPos(HoleESP.mc.player.posX, HoleESP.mc.player.posY, HoleESP.mc.player.posZ)) || ((Boolean) this.renderOwn.getValue()).booleanValue()) && (BlockUtil.isPosInFov(pos).booleanValue() || !((Boolean) this.fov.getValue()).booleanValue())) {
                        if (HoleESP.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && HoleESP.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
                            RenderUtil.drawBoxESP(pos, ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.safeRed.getValue()).intValue(), ((Integer) this.safeGreen.getValue()).intValue(), ((Integer) this.safeBlue.getValue()).intValue(), ((Integer) this.safeAlpha.getValue()).intValue()), ((Boolean) this.customOutline.getValue()).booleanValue(), new Color(((Integer) this.safecRed.getValue()).intValue(), ((Integer) this.safecGreen.getValue()).intValue(), ((Integer) this.safecBlue.getValue()).intValue(), ((Integer) this.safecAlpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), true, ((Double) this.height.getValue()).doubleValue(), ((Boolean) this.gradientBox.getValue()).booleanValue(), ((Boolean) this.gradientOutline.getValue()).booleanValue(), ((Boolean) this.invertGradientBox.getValue()).booleanValue(), ((Boolean) this.invertGradientOutline.getValue()).booleanValue(), 0);
                        } else if (BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.down()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.east()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.west()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.south()).getBlock()) && BlockUtil.isBlockUnSafe(HoleESP.mc.world.getBlockState(pos.north()).getBlock())) {
                            RenderUtil.drawBoxESP(pos, ((Boolean) this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), ((Boolean) this.customOutline.getValue()).booleanValue(), new Color(((Integer) this.cRed.getValue()).intValue(), ((Integer) this.cGreen.getValue()).intValue(), ((Integer) this.cBlue.getValue()).intValue(), ((Integer) this.cAlpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), true, ((Double) this.height.getValue()).doubleValue(), ((Boolean) this.gradientBox.getValue()).booleanValue(), ((Boolean) this.gradientOutline.getValue()).booleanValue(), ((Boolean) this.invertGradientBox.getValue()).booleanValue(), ((Boolean) this.invertGradientOutline.getValue()).booleanValue(), 0);
                        }
                    }
                }
            }
        }

    }
}
