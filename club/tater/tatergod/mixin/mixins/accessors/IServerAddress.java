package club.tater.tatergod.mixin.mixins.accessors;

import net.minecraft.client.multiplayer.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ ServerAddress.class})
public interface IServerAddress {

    @Invoker("getServerAddress")
    static default String[] getServerAddress(String string) {
        throw new IllegalStateException("Mixin didnt transform this");
    }
}
