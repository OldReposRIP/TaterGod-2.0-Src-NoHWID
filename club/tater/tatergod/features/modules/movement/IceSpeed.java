package club.tater.tatergod.features.modules.movement;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import net.minecraft.init.Blocks;

public class IceSpeed extends Module {

    private static IceSpeed INSTANCE = new IceSpeed();
    private final Setting speed = this.register(new Setting("Speed", Float.valueOf(0.4F), Float.valueOf(0.2F), Float.valueOf(1.5F)));

    public IceSpeed() {
        super("IceSpeed", "Speeds you up on ice.", Module.Category.MOVEMENT, false, false, false);
        IceSpeed.INSTANCE = this;
    }

    public static IceSpeed getINSTANCE() {
        if (IceSpeed.INSTANCE == null) {
            IceSpeed.INSTANCE = new IceSpeed();
        }

        return IceSpeed.INSTANCE;
    }

    public void onUpdate() {
        Blocks.ICE.slipperiness = ((Float) this.speed.getValue()).floatValue();
        Blocks.PACKED_ICE.slipperiness = ((Float) this.speed.getValue()).floatValue();
        Blocks.FROSTED_ICE.slipperiness = ((Float) this.speed.getValue()).floatValue();
    }

    public void onDisable() {
        Blocks.ICE.slipperiness = 0.98F;
        Blocks.PACKED_ICE.slipperiness = 0.98F;
        Blocks.FROSTED_ICE.slipperiness = 0.98F;
    }
}
