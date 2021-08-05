package club.tater.tatergod.features.modules.player;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;

public class TpsSync extends Module {

    private static TpsSync INSTANCE = new TpsSync();
    public Setting attack;
    public Setting mining;

    public TpsSync() {
        super("TpsSync", "Syncs your client with the TPS.", Module.Category.PLAYER, true, false, false);
        this.attack = this.register(new Setting("Attack", Boolean.FALSE));
        this.mining = this.register(new Setting("Mine", Boolean.TRUE));
        this.setInstance();
    }

    public static TpsSync getInstance() {
        if (TpsSync.INSTANCE == null) {
            TpsSync.INSTANCE = new TpsSync();
        }

        return TpsSync.INSTANCE;
    }

    private void setInstance() {
        TpsSync.INSTANCE = this;
    }
}
