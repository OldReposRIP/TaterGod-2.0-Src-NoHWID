package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.event.events.TransformSideFirstPersonEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ViewModel extends Module {

    private static ViewModel instance;
    public Setting viewType;
    public Setting cancelEating;
    public Setting xLeft;
    public Setting yLeft;
    public Setting zLeft;
    public Setting xRight;
    public Setting yRight;
    public Setting zRight;
    public Setting fov;

    public ViewModel() {
        super("ViewModel", "Changes player view model!", Module.Category.RENDER, true, false, false);
        this.viewType = this.register(new Setting("Types", ViewModel.Types.Value));
        this.cancelEating = this.register(new Setting("CancelEating", Boolean.valueOf(false)));
        this.xLeft = this.register(new Setting("LeftX", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
        this.yLeft = this.register(new Setting("LeftY", Double.valueOf(0.2D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
        this.zLeft = this.register(new Setting("LeftZ", Double.valueOf(-1.2D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
        this.xRight = this.register(new Setting("RightX", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
        this.yRight = this.register(new Setting("RightY", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
        this.zRight = this.register(new Setting("RightZ", Double.valueOf(-1.2D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
        this.fov = this.register(new Setting("ItemFOV", Integer.valueOf(110), Integer.valueOf(90), Integer.valueOf(180)));
        ViewModel.instance = this;
    }

    @SubscribeEvent
    public void TransformSideFirstPersonEvent(TransformSideFirstPersonEvent event) {
        if (((ViewModel.Types) this.viewType.getValue()).equals(ViewModel.Types.Value) || ((ViewModel.Types) this.viewType.getValue()).equals(ViewModel.Types.Both)) {
            if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
                GlStateManager.translate(((Double) this.xRight.getValue()).doubleValue(), ((Double) this.yRight.getValue()).doubleValue(), ((Double) this.zRight.getValue()).doubleValue());
            } else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
                GlStateManager.translate(((Double) this.xLeft.getValue()).doubleValue(), ((Double) this.yLeft.getValue()).doubleValue(), ((Double) this.zLeft.getValue()).doubleValue());
            }
        }

    }

    @SubscribeEvent
    public void onFov(FOVModifier event) {
        if (((ViewModel.Types) this.viewType.getValue()).equals(ViewModel.Types.FOV) || ((ViewModel.Types) this.viewType.getValue()).equals(ViewModel.Types.Both)) {
            event.setFOV((float) ((Integer) this.fov.getValue()).intValue());
        }

    }

    public static enum Types {

        Value, FOV, Both;
    }
}
