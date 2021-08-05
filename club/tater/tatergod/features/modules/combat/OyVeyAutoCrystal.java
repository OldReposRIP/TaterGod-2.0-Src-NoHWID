package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.modules.misc.AutoGG;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Timer;
import club.tater.tatergod.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OyVeyAutoCrystal extends Module {

    private final Timer placeTimer = new Timer();
    private final Timer breakTimer = new Timer();
    private final Timer preditTimer = new Timer();
    private final Timer manualTimer = new Timer();
    private final Setting attackFactor = this.register(new Setting("PredictDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
    private final Setting red = this.register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255)));
    private final Setting lineWidth = this.register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
    public Setting place = this.register(new Setting("Place", Boolean.valueOf(true)));
    public Setting placeDelay = this.register(new Setting("PlaceDelay", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(500.0F)));
    public Setting placeRange = this.register(new Setting("PlaceRange", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
    public Setting explode = this.register(new Setting("Break", Boolean.valueOf(true)));
    public Setting packetBreak = this.register(new Setting("PacketBreak", Boolean.valueOf(true)));
    public Setting predicts = this.register(new Setting("Predict", Boolean.valueOf(true)));
    public Setting rotate = this.register(new Setting("Rotate", Boolean.valueOf(true)));
    public Setting breakDelay = this.register(new Setting("BreakDelay", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(500.0F)));
    public Setting breakRange = this.register(new Setting("BreakRange", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
    public Setting breakWallRange = this.register(new Setting("BreakWallRange", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
    public Setting ecmeplace = this.register(new Setting("1.13 Place", Boolean.valueOf(false)));
    public Setting suicide = this.register(new Setting("AntiSuicide", Boolean.valueOf(true)));
    public Setting autoswitch = this.register(new Setting("AutoSwitch", Boolean.valueOf(true)));
    public Setting ignoreUseAmount = this.register(new Setting("IgnoreUseAmount", Boolean.valueOf(true)));
    public Setting wasteAmount = this.register(new Setting("UseAmount", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(5)));
    public Setting facePlaceSword = this.register(new Setting("FacePlaceSword", Boolean.valueOf(true)));
    public Setting removeAttack = this.register(new Setting("EntityRemove", Boolean.valueOf(false)));
    public Setting targetRange = this.register(new Setting("TargetRange", Float.valueOf(4.0F), Float.valueOf(1.0F), Float.valueOf(20.0F)));
    public Setting minDamage = this.register(new Setting("MinDamage", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
    public Setting facePlace = this.register(new Setting("FacePlaceHP", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(36.0F)));
    public Setting breakMaxSelfDamage = this.register(new Setting("BreakMaxSelf", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
    public Setting breakMinDmg = this.register(new Setting("BreakMinDmg", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
    public Setting minArmor = this.register(new Setting("MinArmor", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(99.0F)));
    public Setting swingMode;
    public Setting render;
    public Setting renderDmg;
    public Setting box;
    public Setting outline;
    private final Setting cRed;
    private final Setting cGreen;
    private final Setting cBlue;
    private final Setting cAlpha;
    public EntityLivingBase realTarget;
    EntityEnderCrystal crystal;
    private EntityLivingBase target;
    private BlockPos pos;
    private int hotBarSlot;
    private boolean armor;
    private boolean armorTarget;
    private int crystalCount;
    private int predictWait;
    private int predictPackets;
    private boolean packetCalc;
    private float yaw;
    private int predict;
    private float pitch;
    private boolean rotating;

    public OyVeyAutoCrystal() {
        super("CrystalAura", "Tater ac best ac", Module.Category.COMBAT, true, false, false);
        this.swingMode = this.register(new Setting("Swing", OyVeyAutoCrystal.SwingMode.MainHand));
        this.render = this.register(new Setting("Render", Boolean.valueOf(true)));
        this.renderDmg = this.register(new Setting("RenderDmg", Boolean.valueOf(true)));
        this.box = this.register(new Setting("Box", Boolean.valueOf(true)));
        this.outline = this.register(new Setting("Outline", Boolean.valueOf(true)));
        this.cRed = this.register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.cGreen = this.register(new Setting("OL-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.cBlue = this.register(new Setting("OL-Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.cAlpha = this.register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.yaw = 0.0F;
        this.pitch = 0.0F;
        this.rotating = false;
    }

    public static List getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList circleblocks = new ArrayList();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();

        for (int x = cx - (int) r; (float) x <= (float) cx + r; ++x) {
            int z = cz - (int) r;

            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;

                while (true) {
                    float f = sphere ? (float) cy + r : (float) (cy + h);

                    if ((float) y >= f) {
                        ++z;
                        break;
                    }

                    double dist = (double) ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));

                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0F) * (r - 1.0F)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);

                        circleblocks.add(l);
                    }

                    ++y;
                }
            }
        }

        return circleblocks;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && ((Boolean) this.rotate.getValue()).booleanValue() && this.rotating && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();

            packet.yaw = this.yaw;
            packet.pitch = this.pitch;
            this.rotating = false;
        }

        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet1 = (CPacketUseEntity) event.getPacket();

            if (((Boolean) this.removeAttack.getValue()).booleanValue()) {
                packet1.getEntityFromWorld(Util.mc.world).setDead();
                Util.mc.world.removeEntityFromWorld(packet1.entityId);
            }
        }

    }

    private void rotateTo(Entity entity) {
        if (((Boolean) this.rotate.getValue()).booleanValue()) {
            float[] angle = MathUtil.calcAngle(OyVeyAutoCrystal.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), entity.getPositionVector());

            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }

    }

    private void rotateToPos(BlockPos pos) {
        if (((Boolean) this.rotate.getValue()).booleanValue()) {
            float[] angle = MathUtil.calcAngle(OyVeyAutoCrystal.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() - 0.5F), (double) ((float) pos.getZ() + 0.5F)));

            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
        }

    }

    public void onEnable() {
        this.placeTimer.reset();
        this.breakTimer.reset();
        this.predictWait = 0;
        this.hotBarSlot = -1;
        this.pos = null;
        this.crystal = null;
        this.predict = 0;
        this.predictPackets = 1;
        this.target = null;
        this.packetCalc = false;
        this.realTarget = null;
        this.armor = false;
        this.armorTarget = false;
    }

    public void onDisable() {
        this.rotating = false;
    }

    public void onTick() {
        this.onCrystal();
    }

    public String getDisplayInfo() {
        return this.realTarget != null ? this.realTarget.getName() : null;
    }

    public void onCrystal() {
        if (OyVeyAutoCrystal.mc.world != null && OyVeyAutoCrystal.mc.player != null) {
            this.realTarget = null;
            this.manualBreaker();
            this.crystalCount = 0;
            if (!((Boolean) this.ignoreUseAmount.getValue()).booleanValue()) {
                Iterator damage = OyVeyAutoCrystal.mc.world.loadedEntityList.iterator();

                while (damage.hasNext()) {
                    Entity n = (Entity) damage.next();

                    if (n instanceof EntityEnderCrystal && this.IsValidCrystal(n)) {
                        boolean crystalLimit = false;
                        double blockPos = (double) this.calculateDamage((double) this.target.getPosition().getX() + 0.5D, (double) this.target.getPosition().getY() + 1.0D, (double) this.target.getPosition().getZ() + 0.5D, this.target);

                        if (blockPos >= (double) ((Float) this.minDamage.getValue()).floatValue()) {
                            crystalLimit = true;
                        }

                        if (crystalLimit) {
                            ++this.crystalCount;
                        }
                    }
                }
            }

            this.hotBarSlot = -1;
            int i;

            if (OyVeyAutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
                int j = OyVeyAutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? OyVeyAutoCrystal.mc.player.inventory.currentItem : -1;

                if (j == -1) {
                    for (i = 0; i < 9; ++i) {
                        if (OyVeyAutoCrystal.mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                            j = i;
                            this.hotBarSlot = i;
                            break;
                        }
                    }
                }

                if (j == -1) {
                    this.pos = null;
                    this.target = null;
                    return;
                }
            }

            if (OyVeyAutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && OyVeyAutoCrystal.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL) {
                this.pos = null;
                this.target = null;
            } else {
                if (this.target == null) {
                    this.target = this.getTarget();
                }

                if (this.target == null) {
                    this.crystal = null;
                } else {
                    if (this.target.getDistance(OyVeyAutoCrystal.mc.player) > 12.0F) {
                        this.crystal = null;
                        this.target = null;
                    }

                    this.crystal = (EntityEnderCrystal) OyVeyAutoCrystal.mc.world.loadedEntityList.stream().filter(test<invokedynamic>(this)).map(apply<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>(this))).orElse((Object) null);
                    if (this.crystal != null && ((Boolean) this.explode.getValue()).booleanValue() && this.breakTimer.passedMs(((Float) this.breakDelay.getValue()).longValue())) {
                        this.breakTimer.reset();
                        if (((Boolean) this.packetBreak.getValue()).booleanValue()) {
                            this.rotateTo(this.crystal);
                            OyVeyAutoCrystal.mc.player.connection.sendPacket(new CPacketUseEntity(this.crystal));
                        } else {
                            this.rotateTo(this.crystal);
                            OyVeyAutoCrystal.mc.playerController.attackEntity(OyVeyAutoCrystal.mc.player, this.crystal);
                        }

                        if (this.swingMode.getValue() == OyVeyAutoCrystal.SwingMode.MainHand) {
                            OyVeyAutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
                        } else if (this.swingMode.getValue() == OyVeyAutoCrystal.SwingMode.OffHand) {
                            OyVeyAutoCrystal.mc.player.swingArm(EnumHand.OFF_HAND);
                        }
                    }

                    if (this.placeTimer.passedMs(((Float) this.placeDelay.getValue()).longValue()) && ((Boolean) this.place.getValue()).booleanValue()) {
                        this.placeTimer.reset();
                        double d0 = 0.5D;
                        Iterator iterator = this.placePostions(((Float) this.placeRange.getValue()).floatValue()).iterator();

                        while (iterator.hasNext()) {
                            BlockPos blockpos = (BlockPos) iterator.next();

                            if (blockpos != null && this.target != null && OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockpos)).isEmpty() && this.target.getDistance((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ()) <= (double) ((Float) this.targetRange.getValue()).floatValue() && !this.target.isDead && this.target.getHealth() + this.target.getAbsorptionAmount() > 0.0F) {
                                double targetDmg = (double) this.calculateDamage((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 1.0D, (double) blockpos.getZ() + 0.5D, this.target);

                                this.armor = false;
                                Iterator iterator1 = this.target.getArmorInventoryList().iterator();

                                while (iterator1.hasNext()) {
                                    ItemStack is = (ItemStack) iterator1.next();
                                    float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                                    float red = 1.0F - green;
                                    int dmg = 100 - (int) (red * 100.0F);

                                    if ((float) dmg <= ((Float) this.minArmor.getValue()).floatValue()) {
                                        this.armor = true;
                                    }
                                }

                                if (targetDmg < (double) ((Float) this.minDamage.getValue()).floatValue()) {
                                    label182: {
                                        if (((Boolean) this.facePlaceSword.getValue()).booleanValue()) {
                                            if (this.target.getAbsorptionAmount() + this.target.getHealth() <= ((Float) this.facePlace.getValue()).floatValue()) {
                                                break label182;
                                            }
                                        } else if (!(OyVeyAutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword) && this.target.getAbsorptionAmount() + this.target.getHealth() <= ((Float) this.facePlace.getValue()).floatValue()) {
                                            break label182;
                                        }

                                        if (((Boolean) this.facePlaceSword.getValue()).booleanValue()) {
                                            if (!this.armor) {
                                                continue;
                                            }
                                        } else if (OyVeyAutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword || !this.armor) {
                                            continue;
                                        }
                                    }
                                }

                                double selfDmg;

                                if (((selfDmg = (double) this.calculateDamage((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 1.0D, (double) blockpos.getZ() + 0.5D, OyVeyAutoCrystal.mc.player)) + (((Boolean) this.suicide.getValue()).booleanValue() ? 2.0D : 0.5D) < (double) (OyVeyAutoCrystal.mc.player.getHealth() + OyVeyAutoCrystal.mc.player.getAbsorptionAmount()) || selfDmg < targetDmg || targetDmg >= (double) (this.target.getHealth() + this.target.getAbsorptionAmount())) && d0 < targetDmg) {
                                    this.pos = blockpos;
                                    d0 = targetDmg;
                                }
                            }
                        }

                        if (d0 == 0.5D) {
                            this.pos = null;
                            this.target = null;
                            this.realTarget = null;
                            return;
                        }

                        this.realTarget = this.target;
                        if (AutoGG.getINSTANCE().isOn()) {
                            AutoGG autogg = (AutoGG) Tater.moduleManager.getModuleByName("AutoGG");

                            autogg.addTargetedPlayer(this.target.getName());
                        }

                        if (this.hotBarSlot != -1 && ((Boolean) this.autoswitch.getValue()).booleanValue() && !OyVeyAutoCrystal.mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                            OyVeyAutoCrystal.mc.player.inventory.currentItem = this.hotBarSlot;
                        }

                        if (!((Boolean) this.ignoreUseAmount.getValue()).booleanValue()) {
                            i = ((Integer) this.wasteAmount.getValue()).intValue();
                            if (this.crystalCount >= i) {
                                return;
                            }

                            if (d0 < (double) ((Float) this.minDamage.getValue()).floatValue()) {
                                i = 1;
                            }

                            if (this.crystalCount < i && this.pos != null) {
                                this.rotateToPos(this.pos);
                                OyVeyAutoCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, OyVeyAutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                            }
                        } else if (this.pos != null) {
                            this.rotateToPos(this.pos);
                            OyVeyAutoCrystal.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.pos, EnumFacing.UP, OyVeyAutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                        }
                    }

                }
            }
        }
    }

    @SubscribeEvent(
        priority = EventPriority.HIGHEST,
        receiveCanceled = true
    )
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSpawnObject packet;

        if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject) event.getPacket()).getType() == 51 && ((Boolean) this.predicts.getValue()).booleanValue() && this.preditTimer.passedMs(((Integer) this.attackFactor.getValue()).longValue()) && ((Boolean) this.predicts.getValue()).booleanValue() && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.packetBreak.getValue()).booleanValue() && this.target != null) {
            if (!this.isPredicting(packet)) {
                return;
            }

            CPacketUseEntity predict = new CPacketUseEntity();

            predict.entityId = packet.getEntityID();
            predict.action = Action.ATTACK;
            OyVeyAutoCrystal.mc.player.connection.sendPacket(predict);
        }

    }

    public void onRender3D(Render3DEvent event) {
        if (this.pos != null && ((Boolean) this.render.getValue()).booleanValue() && this.target != null) {
            RenderUtil.drawBoxESP(this.pos, ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.cRed.getValue()).intValue(), ((Integer) this.cGreen.getValue()).intValue(), ((Integer) this.cBlue.getValue()).intValue(), ((Integer) this.cAlpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), true);
            if (((Boolean) this.renderDmg.getValue()).booleanValue()) {
                double renderDamage = (double) this.calculateDamage((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 1.0D, (double) this.pos.getZ() + 0.5D, this.target);

                RenderUtil.drawText(this.pos, (Math.floor(renderDamage) == renderDamage ? Integer.valueOf((int) renderDamage) : String.format(ChatFormatting.WHITE + "%.1f", new Object[] { Double.valueOf(renderDamage)})) + "");
            }
        }

    }

    private boolean isPredicting(SPacketSpawnObject packet) {
        BlockPos packPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());

        if (OyVeyAutoCrystal.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > (double) ((Float) this.breakRange.getValue()).floatValue()) {
            return false;
        } else if (!this.canSeePos(packPos) && OyVeyAutoCrystal.mc.player.getDistance(packet.getX(), packet.getY(), packet.getZ()) > (double) ((Float) this.breakWallRange.getValue()).floatValue()) {
            return false;
        } else {
            double targetDmg = (double) this.calculateDamage(packet.getX() + 0.5D, packet.getY() + 1.0D, packet.getZ() + 0.5D, this.target);

            if (EntityUtil.isInHole(OyVeyAutoCrystal.mc.player) && targetDmg >= 1.0D) {
                return true;
            } else {
                double selfDmg = (double) this.calculateDamage(packet.getX() + 0.5D, packet.getY() + 1.0D, packet.getZ() + 0.5D, OyVeyAutoCrystal.mc.player);
                double d = ((Boolean) this.suicide.getValue()).booleanValue() ? 2.0D : 0.5D;

                if (selfDmg + d < (double) (OyVeyAutoCrystal.mc.player.getHealth() + OyVeyAutoCrystal.mc.player.getAbsorptionAmount()) && targetDmg >= (double) (this.target.getAbsorptionAmount() + this.target.getHealth())) {
                    return true;
                } else {
                    this.armorTarget = false;
                    Iterator iterator = this.target.getArmorInventoryList().iterator();

                    while (iterator.hasNext()) {
                        ItemStack is = (ItemStack) iterator.next();
                        float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                        float red = 1.0F - green;
                        int dmg = 100 - (int) (red * 100.0F);

                        if ((float) dmg <= ((Float) this.minArmor.getValue()).floatValue()) {
                            this.armorTarget = true;
                        }
                    }

                    return targetDmg >= (double) ((Float) this.breakMinDmg.getValue()).floatValue() && selfDmg <= (double) ((Float) this.breakMaxSelfDamage.getValue()).floatValue() ? true : EntityUtil.isInHole(this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= ((Float) this.facePlace.getValue()).floatValue();
                }
            }
        }
    }

    private boolean IsValidCrystal(Entity p_Entity) {
        if (p_Entity == null) {
            return false;
        } else if (!(p_Entity instanceof EntityEnderCrystal)) {
            return false;
        } else if (this.target == null) {
            return false;
        } else if (p_Entity.getDistance(OyVeyAutoCrystal.mc.player) > ((Float) this.breakRange.getValue()).floatValue()) {
            return false;
        } else if (!OyVeyAutoCrystal.mc.player.canEntityBeSeen(p_Entity) && p_Entity.getDistance(OyVeyAutoCrystal.mc.player) > ((Float) this.breakWallRange.getValue()).floatValue()) {
            return false;
        } else if (!this.target.isDead && this.target.getHealth() + this.target.getAbsorptionAmount() > 0.0F) {
            double targetDmg = (double) this.calculateDamage((double) p_Entity.getPosition().getX() + 0.5D, (double) p_Entity.getPosition().getY() + 1.0D, (double) p_Entity.getPosition().getZ() + 0.5D, this.target);

            if (EntityUtil.isInHole(OyVeyAutoCrystal.mc.player) && targetDmg >= 1.0D) {
                return true;
            } else {
                double selfDmg = (double) this.calculateDamage((double) p_Entity.getPosition().getX() + 0.5D, (double) p_Entity.getPosition().getY() + 1.0D, (double) p_Entity.getPosition().getZ() + 0.5D, OyVeyAutoCrystal.mc.player);
                double d = ((Boolean) this.suicide.getValue()).booleanValue() ? 2.0D : 0.5D;

                if (selfDmg + d < (double) (OyVeyAutoCrystal.mc.player.getHealth() + OyVeyAutoCrystal.mc.player.getAbsorptionAmount()) && targetDmg >= (double) (this.target.getAbsorptionAmount() + this.target.getHealth())) {
                    return true;
                } else {
                    this.armorTarget = false;
                    Iterator iterator = this.target.getArmorInventoryList().iterator();

                    while (iterator.hasNext()) {
                        ItemStack is = (ItemStack) iterator.next();
                        float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                        float red = 1.0F - green;
                        int dmg = 100 - (int) (red * 100.0F);

                        if ((float) dmg <= ((Float) this.minArmor.getValue()).floatValue()) {
                            this.armorTarget = true;
                        }
                    }

                    return targetDmg >= (double) ((Float) this.breakMinDmg.getValue()).floatValue() && selfDmg <= (double) ((Float) this.breakMaxSelfDamage.getValue()).floatValue() ? true : EntityUtil.isInHole(this.target) && this.target.getHealth() + this.target.getAbsorptionAmount() <= ((Float) this.facePlace.getValue()).floatValue();
                }
            }
        } else {
            return false;
        }
    }

    EntityPlayer getTarget() {
        EntityPlayer closestPlayer = null;
        Iterator iterator = OyVeyAutoCrystal.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entity = (EntityPlayer) iterator.next();

            if (OyVeyAutoCrystal.mc.player != null && !OyVeyAutoCrystal.mc.player.isDead && !entity.isDead && entity != OyVeyAutoCrystal.mc.player && !Tater.friendManager.isFriend(entity.getName()) && entity.getDistance(OyVeyAutoCrystal.mc.player) <= 12.0F) {
                this.armorTarget = false;
                Iterator iterator1 = entity.getArmorInventoryList().iterator();

                while (iterator1.hasNext()) {
                    ItemStack is = (ItemStack) iterator1.next();
                    float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                    float red = 1.0F - green;
                    int dmg = 100 - (int) (red * 100.0F);

                    if ((float) dmg <= ((Float) this.minArmor.getValue()).floatValue()) {
                        this.armorTarget = true;
                    }
                }

                if (!EntityUtil.isInHole(entity) || entity.getAbsorptionAmount() + entity.getHealth() <= ((Float) this.facePlace.getValue()).floatValue() || this.armorTarget || ((Float) this.minDamage.getValue()).floatValue() <= 2.2F) {
                    if (closestPlayer == null) {
                        closestPlayer = entity;
                    } else if (closestPlayer.getDistance(OyVeyAutoCrystal.mc.player) > entity.getDistance(OyVeyAutoCrystal.mc.player)) {
                        closestPlayer = entity;
                    }
                }
            }
        }

        return closestPlayer;
    }

    private void manualBreaker() {
        if (this.manualTimer.passedMs(200L) && OyVeyAutoCrystal.mc.gameSettings.keyBindUseItem.isKeyDown() && OyVeyAutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && OyVeyAutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && OyVeyAutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && OyVeyAutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE) {
            RayTraceResult result = OyVeyAutoCrystal.mc.objectMouseOver;

            if (OyVeyAutoCrystal.mc.objectMouseOver != null) {
                if (result.typeOfHit.equals(Type.ENTITY)) {
                    Entity mousePos = result.entityHit;

                    if (mousePos instanceof EntityEnderCrystal) {
                        if (((Boolean) this.packetBreak.getValue()).booleanValue()) {
                            OyVeyAutoCrystal.mc.player.connection.sendPacket(new CPacketUseEntity(mousePos));
                        } else {
                            OyVeyAutoCrystal.mc.playerController.attackEntity(OyVeyAutoCrystal.mc.player, mousePos);
                        }

                        this.manualTimer.reset();
                    }
                } else if (result.typeOfHit.equals(Type.BLOCK)) {
                    BlockPos mousePos1 = new BlockPos((double) OyVeyAutoCrystal.mc.objectMouseOver.getBlockPos().getX(), (double) OyVeyAutoCrystal.mc.objectMouseOver.getBlockPos().getY() + 1.0D, (double) OyVeyAutoCrystal.mc.objectMouseOver.getBlockPos().getZ());
                    Iterator iterator = OyVeyAutoCrystal.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(mousePos1)).iterator();

                    while (iterator.hasNext()) {
                        Entity target = (Entity) iterator.next();

                        if (target instanceof EntityEnderCrystal) {
                            if (((Boolean) this.packetBreak.getValue()).booleanValue()) {
                                OyVeyAutoCrystal.mc.player.connection.sendPacket(new CPacketUseEntity(target));
                            } else {
                                OyVeyAutoCrystal.mc.playerController.attackEntity(OyVeyAutoCrystal.mc.player, target);
                            }

                            this.manualTimer.reset();
                        }
                    }
                }
            }
        }

    }

    private boolean canSeePos(BlockPos pos) {
        return OyVeyAutoCrystal.mc.world.rayTraceBlocks(new Vec3d(OyVeyAutoCrystal.mc.player.posX, OyVeyAutoCrystal.mc.player.posY + (double) OyVeyAutoCrystal.mc.player.getEyeHeight(), OyVeyAutoCrystal.mc.player.posZ), new Vec3d((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), false, true, false) == null;
    }

    private NonNullList placePostions(float placeRange) {
        NonNullList positions = NonNullList.create();

        positions.addAll((Collection) getSphere(new BlockPos(Math.floor(OyVeyAutoCrystal.mc.player.posX), Math.floor(OyVeyAutoCrystal.mc.player.posY), Math.floor(OyVeyAutoCrystal.mc.player.posZ)), placeRange, (int) placeRange, false, true, 0).stream().filter(test<invokedynamic>(this)).collect(Collectors.toList()));
        return positions;
    }

    private boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        BlockPos boost = blockPos.add(0, 1, 0);
        BlockPos boost2 = blockPos.add(0, 2, 0);

        try {
            Iterator ignored;
            Entity entity;

            if (!((Boolean) this.ecmeplace.getValue()).booleanValue()) {
                if (OyVeyAutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && OyVeyAutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }

                if (OyVeyAutoCrystal.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || OyVeyAutoCrystal.mc.world.getBlockState(boost2).getBlock() != Blocks.AIR) {
                    return false;
                }

                if (!specialEntityCheck) {
                    return OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }

                ignored = OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).iterator();

                while (ignored.hasNext()) {
                    entity = (Entity) ignored.next();
                    if (!(entity instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }

                ignored = OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).iterator();

                while (ignored.hasNext()) {
                    entity = (Entity) ignored.next();
                    if (!(entity instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }
            } else {
                if (OyVeyAutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && OyVeyAutoCrystal.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
                    return false;
                }

                if (OyVeyAutoCrystal.mc.world.getBlockState(boost).getBlock() != Blocks.AIR) {
                    return false;
                }

                if (!specialEntityCheck) {
                    return OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }

                ignored = OyVeyAutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).iterator();

                while (ignored.hasNext()) {
                    entity = (Entity) ignored.next();
                    if (!(entity instanceof EntityEnderCrystal)) {
                        return false;
                    }
                }
            }

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0D;

        try {
            blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        } catch (Exception exception) {
            ;
        }

        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D));
        double finald = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finald = (double) this.getBlastReduction((EntityLivingBase) entity, this.getDamageMultiplied(damage), new Explosion(OyVeyAutoCrystal.mc.world, (Entity) null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finald;
    }

    private float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage;

        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);

            damage = CombatRules.getDamageAfterAbsorb(damageI, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;

            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception exception) {
                ;
            }

            float f = MathHelper.clamp((float) k, 0.0F, 20.0F);

            damage *= 1.0F - f / 25.0F;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0F;
            }

            damage = Math.max(damage, 0.0F);
            return damage;
        } else {
            damage = CombatRules.getDamageAfterAbsorb(damageI, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }
    }

    private float getDamageMultiplied(float damage) {
        int diff = OyVeyAutoCrystal.mc.world.getDifficulty().getId();

        return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
    }

    private boolean lambda$placePostions$6(BlockPos pos) {
        return this.canPlaceCrystal(pos, true);
    }

    private Float lambda$onCrystal$5(EntityEnderCrystal p_Entity) {
        return Float.valueOf(this.target.getDistance(p_Entity));
    }

    private static EntityEnderCrystal lambda$onCrystal$4(Entity p_Entity) {
        return (EntityEnderCrystal) p_Entity;
    }

    private boolean lambda$new$3(Object v) {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$2(Object v) {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$1(Object v) {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }

    public static enum SwingMode {

        MainHand, OffHand, None;
    }
}
