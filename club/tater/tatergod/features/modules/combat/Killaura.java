package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.DamageUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.Timer;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura extends Module {

    public static Entity target;
    private final Timer timer = new Timer();
    public Setting range = this.register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
    public Setting delay = this.register(new Setting("HitDelay", Boolean.valueOf(true)));
    public Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    public Setting onlySharp = this.register(new Setting("SwordOnly", Boolean.valueOf(true)));
    public Setting raytrace = this.register(new Setting("Raytrace", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), "Wall Range."));
    public Setting players = this.register(new Setting("Players", Boolean.valueOf(true)));
    public Setting mobs = this.register(new Setting("Mobs", Boolean.valueOf(false)));
    public Setting animals = this.register(new Setting("Animals", Boolean.valueOf(false)));
    public Setting vehicles = this.register(new Setting("Entities", Boolean.valueOf(false)));
    public Setting projectiles = this.register(new Setting("Projectiles", Boolean.valueOf(false)));
    public Setting tps = this.register(new Setting("TpsSync", Boolean.valueOf(false)));
    public Setting packet = this.register(new Setting("Packet", Boolean.valueOf(false)));

    public Killaura() {
        super("KillAura", "Kills aura.", Module.Category.COMBAT, true, false, false);
    }

    public void onTick() {
        if (!((Boolean) this.rotate.getValue()).booleanValue()) {
            this.doKillaura();
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && ((Boolean) this.rotate.getValue()).booleanValue()) {
            this.doKillaura();
        }

    }

    private void doKillaura() {
        if (((Boolean) this.onlySharp.getValue()).booleanValue() && !EntityUtil.holdingWeapon(Killaura.mc.player)) {
            Killaura.target = null;
        } else {
            int wait = !((Boolean) this.delay.getValue()).booleanValue() ? 0 : (int) ((float) DamageUtil.getCooldownByWeapon(Killaura.mc.player) * (((Boolean) this.tps.getValue()).booleanValue() ? Tater.serverManager.getTpsFactor() : 1.0F));

            if (this.timer.passedMs((long) wait)) {
                Killaura.target = this.getTarget();
                if (Killaura.target != null) {
                    if (((Boolean) this.rotate.getValue()).booleanValue()) {
                        Tater.rotationManager.lookAtEntity(Killaura.target);
                    }

                    EntityUtil.attackEntity(Killaura.target, ((Boolean) this.packet.getValue()).booleanValue(), true);
                    this.timer.reset();
                }
            }
        }
    }

    private Entity getTarget() {
        Entity target = null;
        double distance = (double) ((Float) this.range.getValue()).floatValue();
        double maxHealth = 36.0D;
        Iterator iterator = Killaura.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if ((((Boolean) this.players.getValue()).booleanValue() && entity instanceof EntityPlayer || ((Boolean) this.animals.getValue()).booleanValue() && EntityUtil.isPassive(entity) || ((Boolean) this.mobs.getValue()).booleanValue() && EntityUtil.isMobAggressive(entity) || ((Boolean) this.vehicles.getValue()).booleanValue() && EntityUtil.isVehicle(entity) || ((Boolean) this.projectiles.getValue()).booleanValue() && EntityUtil.isProjectile(entity)) && (!(entity instanceof EntityLivingBase) || !EntityUtil.isntValid(entity, distance)) && (Killaura.mc.player.canEntityBeSeen(entity) || EntityUtil.canEntityFeetBeSeen(entity) || Killaura.mc.player.getDistanceSq(entity) <= MathUtil.square((double) ((Float) this.raytrace.getValue()).floatValue()))) {
                if (target == null) {
                    target = entity;
                    distance = Killaura.mc.player.getDistanceSq(entity);
                    maxHealth = (double) EntityUtil.getHealth(entity);
                } else {
                    if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer) entity, 18)) {
                        target = entity;
                        break;
                    }

                    if (Killaura.mc.player.getDistanceSq(entity) < distance) {
                        target = entity;
                        distance = Killaura.mc.player.getDistanceSq(entity);
                        maxHealth = (double) EntityUtil.getHealth(entity);
                    }

                    if ((double) EntityUtil.getHealth(entity) < maxHealth) {
                        target = entity;
                        distance = Killaura.mc.player.getDistanceSq(entity);
                        maxHealth = (double) EntityUtil.getHealth(entity);
                    }
                }
            }
        }

        return target;
    }

    public String getDisplayInfo() {
        return Killaura.target instanceof EntityPlayer ? Killaura.target.getName() : null;
    }
}
