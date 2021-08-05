package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Step extends Module {

    private static Step instance;
    final double[] twoFiveOffset = new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D};
    private final double[] oneblockPositions = new double[] { 0.42D, 0.75D};
    private final double[] twoblockPositions = new double[] { 0.4D, 0.75D, 0.5D, 0.41D, 0.83D, 1.16D, 1.41D, 1.57D, 1.58D, 1.42D};
    private final double[] futurePositions = new double[] { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D};
    private final double[] threeBlockPositions = new double[] { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 1.51D, 1.9D, 2.21D, 2.45D, 2.43D};
    private final double[] fourBlockPositions = new double[] { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 1.51D, 1.9D, 2.21D, 2.45D, 2.43D, 2.78D, 2.63D, 2.51D, 2.9D, 3.21D, 3.45D, 3.43D};
    public Setting vanilla = this.register(new Setting("Vanilla", Boolean.valueOf(false)));
    public Setting stepHeightVanilla = this.register(new Setting("VHeight", Float.valueOf(2.0F), Float.valueOf(0.1F), Float.valueOf(4.0F), (v) -> {
        return ((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    public Setting stepHeight = this.register(new Setting("Height", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(4), (v) -> {
        return !((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    public Setting spoof = this.register(new Setting("Spoof", Boolean.valueOf(true), (v) -> {
        return !((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    public Setting ticks = this.register(new Setting("Delay", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(25), (v) -> {
        return ((Boolean) this.spoof.getValue()).booleanValue() && !((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    public Setting turnOff = this.register(new Setting("Disable", Boolean.valueOf(false), (v) -> {
        return !((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    public Setting check = this.register(new Setting("Check", Boolean.valueOf(true), (v) -> {
        return !((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    public Setting small = this.register(new Setting("Offset", Boolean.valueOf(false), (v) -> {
        return ((Integer) this.stepHeight.getValue()).intValue() > 1 && !((Boolean) this.vanilla.getValue()).booleanValue();
    }));
    private double[] selectedPositions = new double[0];
    private int packets;

    public Step() {
        super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
        Step.instance = this;
    }

    public static Step getInstance() {
        if (Step.instance == null) {
            Step.instance = new Step();
        }

        return Step.instance;
    }

    public void onToggle() {
        Step.mc.player.stepHeight = 0.6F;
    }

    public void onUpdate() {
        if (((Boolean) this.vanilla.getValue()).booleanValue()) {
            Step.mc.player.stepHeight = ((Float) this.stepHeightVanilla.getValue()).floatValue();
        } else {
            switch (((Integer) this.stepHeight.getValue()).intValue()) {
            case 1:
                this.selectedPositions = this.oneblockPositions;
                break;

            case 2:
                this.selectedPositions = ((Boolean) this.small.getValue()).booleanValue() ? this.twoblockPositions : this.futurePositions;
                break;

            case 3:
                this.selectedPositions = this.twoFiveOffset;

            case 4:
                this.selectedPositions = this.fourBlockPositions;
            }

            if (Step.mc.player.collidedHorizontally && Step.mc.player.onGround) {
                ++this.packets;
            }

            AxisAlignedBB bb = Step.mc.player.getEntityBoundingBox();
            int z;

            if (((Boolean) this.check.getValue()).booleanValue()) {
                for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); ++x) {
                    for (z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); ++z) {
                        Block block = Step.mc.world.getBlockState(new BlockPos((double) x, bb.maxY + 1.0D, (double) z)).getBlock();

                        if (!(block instanceof BlockAir)) {
                            return;
                        }
                    }
                }
            }

            if (Step.mc.player.onGround && !Step.mc.player.isInsideOfMaterial(Material.WATER) && !Step.mc.player.isInsideOfMaterial(Material.LAVA) && Step.mc.player.collidedVertically && Step.mc.player.fallDistance == 0.0F && !Step.mc.gameSettings.keyBindJump.pressed && Step.mc.player.collidedHorizontally && !Step.mc.player.isOnLadder() && (this.packets > this.selectedPositions.length - 2 || ((Boolean) this.spoof.getValue()).booleanValue() && this.packets > ((Integer) this.ticks.getValue()).intValue())) {
                double[] adouble = this.selectedPositions;

                z = adouble.length;

                for (int i = 0; i < z; ++i) {
                    double position = adouble[i];

                    Step.mc.player.connection.sendPacket(new Position(Step.mc.player.posX, Step.mc.player.posY + position, Step.mc.player.posZ, true));
                }

                Step.mc.player.setPosition(Step.mc.player.posX, Step.mc.player.posY + this.selectedPositions[this.selectedPositions.length - 1], Step.mc.player.posZ);
                this.packets = 0;
                if (((Boolean) this.turnOff.getValue()).booleanValue()) {
                    this.disable();
                }
            }

        }
    }
}
