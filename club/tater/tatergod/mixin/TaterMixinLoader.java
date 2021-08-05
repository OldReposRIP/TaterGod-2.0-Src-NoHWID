package club.tater.tatergod.mixin;

import club.tater.tatergod.Tater;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

public class TaterMixinLoader implements IFMLLoadingPlugin {

    private static boolean isObfuscatedEnvironment = false;

    public TaterMixinLoader() {
        Tater.LOGGER.info("\n\nLoading mixins by Tater");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.tater.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("name");
        Tater.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map data) {
        TaterMixinLoader.isObfuscatedEnvironment = ((Boolean) data.get("runtimeDeobfuscationEnabled")).booleanValue();
    }

    public String getAccessTransformerClass() {
        return null;
    }
}
