package club.tater.tatergod.features.modules.misc;

import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.combat.AutoCrystal;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.MathUtil;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSoundLag extends Module {

    private static final Set BLACKLIST = Sets.newHashSet(new SoundEvent[] { SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER});
    private static NoSoundLag instance;
    public Setting crystals = this.register(new Setting("Crystals", Boolean.valueOf(true)));
    public Setting armor = this.register(new Setting("Armor", Boolean.valueOf(true)));
    public Setting soundRange = this.register(new Setting("SoundRange", Float.valueOf(12.0F), Float.valueOf(0.0F), Float.valueOf(12.0F)));

    public NoSoundLag() {
        super("NoSoundLag", "Prevents Lag through sound spam.", Module.Category.MISC, true, false, false);
        NoSoundLag.instance = this;
    }

    public static NoSoundLag getInstance() {
        if (NoSoundLag.instance == null) {
            NoSoundLag.instance = new NoSoundLag();
        }

        return NoSoundLag.instance;
    }

    public static void removeEntities(SPacketSoundEffect packet, float range) {
        BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        ArrayList toRemove = new ArrayList();
        Iterator iterator = NoSoundLag.mc.world.loadedEntityList.iterator();

        Entity entity;

        while (iterator.hasNext()) {
            entity = (Entity) iterator.next();
            if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(pos) <= MathUtil.square((double) range)) {
                toRemove.add(entity);
            }
        }

        iterator = toRemove.iterator();

        while (iterator.hasNext()) {
            entity = (Entity) iterator.next();
            entity.setDead();
        }

    }

    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event != null && event.getPacket() != null && NoSoundLag.mc.player != null && NoSoundLag.mc.world != null && event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();

            if (((Boolean) this.crystals.getValue()).booleanValue() && packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE && (AutoCrystal.getInstance().isOff() || !((Boolean) AutoCrystal.getInstance().sound.getValue()).booleanValue() && AutoCrystal.getInstance().threadMode.getValue() != AutoCrystal.ThreadMode.SOUND)) {
                removeEntities(packet, ((Float) this.soundRange.getValue()).floatValue());
            }

            if (NoSoundLag.BLACKLIST.contains(packet.getSound()) && ((Boolean) this.armor.getValue()).booleanValue()) {
                event.setCanceled(true);
            }
        }

    }
}
