package club.tater.tatergod.features.modules.combat;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.ClientEvent;
import club.tater.tatergod.event.events.PacketEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.event.events.UpdateWalkingPlayerEvent;
import club.tater.tatergod.features.command.Command;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.modules.misc.NoSoundLag;
import club.tater.tatergod.features.setting.Bind;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.BlockUtil;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.DamageUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.InventoryUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.Timer;
import club.tater.tatergod.util.Util;
import com.mojang.authlib.GameProfile;
import io.netty.util.internal.ConcurrentSet;
import java.awt.Color;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AutoCrystal extends Module {

    public static EntityPlayer target = null;
    public static Set lowDmgPos = new ConcurrentSet();
    public static Set placedPos = new HashSet();
    public static Set brokenPos = new HashSet();
    private static AutoCrystal instance;
    public final Timer threadTimer = new Timer();
    private final Setting setting;
    public final Setting attackOppositeHand;
    public final Setting removeAfterAttack;
    public final Setting antiBlock;
    private final Setting switchCooldown;
    private final Setting eventMode;
    private final Timer switchTimer;
    private final Timer manualTimer;
    private final Timer breakTimer;
    private final Timer placeTimer;
    private final Timer syncTimer;
    private final Timer predictTimer;
    private final Timer renderTimer;
    private final AtomicBoolean shouldInterrupt;
    private final Timer syncroTimer;
    private final Map totemPops;
    private final Queue packetUseEntities;
    private final AtomicBoolean threadOngoing;
    public Setting raytrace;
    public Setting place;
    public Setting placeDelay;
    public Setting placeRange;
    public Setting minDamage;
    public Setting maxSelfPlace;
    public Setting wasteAmount;
    public Setting wasteMinDmgCount;
    public Setting facePlace;
    public Setting placetrace;
    public Setting antiSurround;
    public Setting limitFacePlace;
    public Setting oneDot15;
    public Setting doublePop;
    public Setting popHealth;
    public Setting popDamage;
    public Setting popTime;
    public Setting explode;
    public Setting switchMode;
    public Setting breakDelay;
    public Setting breakRange;
    public Setting packets;
    public Setting maxSelfBreak;
    public Setting breaktrace;
    public Setting manual;
    public Setting manualMinDmg;
    public Setting manualBreak;
    public Setting sync;
    public Setting instant;
    public Setting instantTimer;
    public Setting resetBreakTimer;
    public Setting predictDelay;
    public Setting predictCalc;
    public Setting superSafe;
    public Setting antiCommit;
    public Setting render;
    private final Setting red;
    private final Setting green;
    private final Setting blue;
    private final Setting alpha;
    public Setting colorSync;
    public Setting box;
    private final Setting boxAlpha;
    public Setting outline;
    private final Setting lineWidth;
    public Setting text;
    public Setting customOutline;
    private final Setting cRed;
    private final Setting cGreen;
    private final Setting cBlue;
    private final Setting cAlpha;
    public Setting holdFacePlace;
    public Setting holdFaceBreak;
    public Setting slowFaceBreak;
    public Setting actualSlowBreak;
    public Setting facePlaceSpeed;
    public Setting antiNaked;
    public Setting range;
    public Setting targetMode;
    public Setting minArmor;
    public Setting autoSwitch;
    public Setting switchBind;
    public Setting offhandSwitch;
    public Setting switchBack;
    public Setting lethalSwitch;
    public Setting mineSwitch;
    public Setting rotate;
    public Setting suicide;
    public Setting webAttack;
    public Setting fullCalc;
    public Setting sound;
    public Setting soundRange;
    public Setting soundPlayer;
    public Setting soundConfirm;
    public Setting extraSelfCalc;
    public Setting antiFriendPop;
    public Setting noCount;
    public Setting calcEvenIfNoDamage;
    public Setting predictFriendDmg;
    public Setting minMinDmg;
    public Setting breakSwing;
    public Setting placeSwing;
    public Setting exactHand;
    public Setting justRender;
    public Setting fakeSwing;
    public Setting logic;
    public Setting damageSync;
    public Setting damageSyncTime;
    public Setting dropOff;
    public Setting confirm;
    public Setting syncedFeetPlace;
    public Setting fullSync;
    public Setting syncCount;
    public Setting hyperSync;
    public Setting gigaSync;
    public Setting syncySync;
    public Setting enormousSync;
    public Setting holySync;
    public Setting rotateFirst;
    public Setting threadMode;
    public Setting threadDelay;
    public Setting syncThreadBool;
    public Setting syncThreads;
    public Setting predictPos;
    public Setting renderExtrapolation;
    public Setting predictTicks;
    public Setting rotations;
    public Setting predictRotate;
    public Setting predictOffset;
    public Setting brownZombie;
    public Setting doublePopOnDamage;
    public boolean rotating;
    private Queue attackList;
    private Map crystalMap;
    private Entity efficientTarget;
    private double currentDamage;
    private double renderDamage;
    private double lastDamage;
    private boolean didRotation;
    private boolean switching;
    private BlockPos placePos;
    private BlockPos renderPos;
    private boolean mainHand;
    private boolean offHand;
    private int crystalCount;
    private int minDmgCount;
    private int lastSlot;
    private float yaw;
    private float pitch;
    private BlockPos webPos;
    private BlockPos lastPos;
    private boolean posConfirmed;
    private boolean foundDoublePop;
    private int rotationPacketsSpoofed;
    private ScheduledExecutorService executor;
    private Thread thread;
    private EntityPlayer currentSyncTarget;
    private BlockPos syncedPlayerPos;
    private BlockPos syncedCrystalPos;
    private AutoCrystal.PlaceInfo placeInfo;
    private boolean addTolowDmg;
    private Object BlockPos;

    public AutoCrystal() {
        super("AutoCrystal", "Best CA on the market", Module.Category.COMBAT, true, false, false);
        this.setting = this.register(new Setting("Settings", AutoCrystal.Settings.PLACE));
        this.attackOppositeHand = this.register(new Setting("OppositeHand", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.removeAfterAttack = this.register(new Setting("AttackRemove", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.antiBlock = this.register(new Setting("AntiFeetPlace", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.switchCooldown = this.register(new Setting("Cooldown", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), test<invokedynamic>(this)));
        this.eventMode = this.register(new Setting("Updates", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(3), test<invokedynamic>(this)));
        this.switchTimer = new Timer();
        this.manualTimer = new Timer();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
        this.syncTimer = new Timer();
        this.predictTimer = new Timer();
        this.renderTimer = new Timer();
        this.shouldInterrupt = new AtomicBoolean(false);
        this.syncroTimer = new Timer();
        this.totemPops = new ConcurrentHashMap();
        this.packetUseEntities = new LinkedList();
        this.threadOngoing = new AtomicBoolean(false);
        this.raytrace = this.register(new Setting("Raytrace", AutoCrystal.Raytrace.NONE, test<invokedynamic>(this)));
        this.place = this.register(new Setting("Place", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.placeDelay = this.register(new Setting("PlaceDelay", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.placeRange = this.register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), test<invokedynamic>(this)));
        this.minDamage = this.register(new Setting("MinDamage", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), test<invokedynamic>(this)));
        this.maxSelfPlace = this.register(new Setting("MaxSelfPlace", Float.valueOf(12.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), test<invokedynamic>(this)));
        this.wasteAmount = this.register(new Setting("WasteAmount", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(5), test<invokedynamic>(this)));
        this.wasteMinDmgCount = this.register(new Setting("CountMinDmg", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.facePlace = this.register(new Setting("FacePlace", Float.valueOf(10.5F), Float.valueOf(0.1F), Float.valueOf(20.0F), test<invokedynamic>(this)));
        this.placetrace = this.register(new Setting("Placetrace", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), test<invokedynamic>(this)));
        this.antiSurround = this.register(new Setting("AntiSurround", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.limitFacePlace = this.register(new Setting("LimitFacePlace", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.oneDot15 = this.register(new Setting("1.15", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.doublePop = this.register(new Setting("AntiTotem", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.popHealth = this.register(new Setting("PopHealth", Double.valueOf(3.0D), Double.valueOf(0.0D), Double.valueOf(3.0D), test<invokedynamic>(this)));
        this.popDamage = this.register(new Setting("PopDamage", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), test<invokedynamic>(this)));
        this.popTime = this.register(new Setting("PopTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(1000), test<invokedynamic>(this)));
        this.explode = this.register(new Setting("Break", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.switchMode = this.register(new Setting("Attack", AutoCrystal.Switch.BREAKSLOT, test<invokedynamic>(this)));
        this.breakDelay = this.register(new Setting("BreakDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.breakRange = this.register(new Setting("BreakRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), test<invokedynamic>(this)));
        this.packets = this.register(new Setting("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(6), test<invokedynamic>(this)));
        this.maxSelfBreak = this.register(new Setting("MaxSelfBreak", Float.valueOf(12.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), test<invokedynamic>(this)));
        this.breaktrace = this.register(new Setting("Breaktrace", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), test<invokedynamic>(this)));
        this.manual = this.register(new Setting("Manual", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.manualMinDmg = this.register(new Setting("ManMinDmg", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.manualBreak = this.register(new Setting("ManualDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.sync = this.register(new Setting("Sync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.instant = this.register(new Setting("Predict", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.instantTimer = this.register(new Setting("PredictTimer", AutoCrystal.PredictTimer.NONE, test<invokedynamic>(this)));
        this.resetBreakTimer = this.register(new Setting("ResetBreakTimer", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.predictDelay = this.register(new Setting("PredictDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.predictCalc = this.register(new Setting("PredictCalc", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.superSafe = this.register(new Setting("SuperSafe", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.antiCommit = this.register(new Setting("AntiOverCommit", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.render = this.register(new Setting("Render", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.red = this.register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.green = this.register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.blue = this.register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.alpha = this.register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.colorSync = this.register(new Setting("ColorSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.box = this.register(new Setting("Box", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.boxAlpha = this.register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.outline = this.register(new Setting("Outline", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.lineWidth = this.register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F), test<invokedynamic>(this)));
        this.text = this.register(new Setting("Text", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.customOutline = this.register(new Setting("CustomLine", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.cRed = this.register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.cGreen = this.register(new Setting("OL-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.cBlue = this.register(new Setting("OL-Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.cAlpha = this.register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), test<invokedynamic>(this)));
        this.holdFacePlace = this.register(new Setting("HoldFacePlace", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.holdFaceBreak = this.register(new Setting("HoldSlowBreak", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.slowFaceBreak = this.register(new Setting("SlowFaceBreak", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.actualSlowBreak = this.register(new Setting("ActuallySlow", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.facePlaceSpeed = this.register(new Setting("FaceSpeed", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.antiNaked = this.register(new Setting("AntiNaked", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.range = this.register(new Setting("Range", Float.valueOf(10.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), test<invokedynamic>(this)));
        this.targetMode = this.register(new Setting("Target", AutoCrystal.Target.CLOSEST, test<invokedynamic>(this)));
        this.minArmor = this.register(new Setting("MinArmor", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(125), test<invokedynamic>(this)));
        this.autoSwitch = this.register(new Setting("Switch", AutoCrystal.AutoSwitch.TOGGLE, test<invokedynamic>(this)));
        this.switchBind = this.register(new Setting("SwitchBind", new Bind(-1), test<invokedynamic>(this)));
        this.offhandSwitch = this.register(new Setting("Offhand", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.switchBack = this.register(new Setting("Switchback", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.lethalSwitch = this.register(new Setting("LethalSwitch", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.mineSwitch = this.register(new Setting("MineSwitch", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.rotate = this.register(new Setting("Rotate", AutoCrystal.Rotate.OFF, test<invokedynamic>(this)));
        this.suicide = this.register(new Setting("Suicide", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.webAttack = this.register(new Setting("WebAttack", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.fullCalc = this.register(new Setting("ExtraCalc", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.sound = this.register(new Setting("Sound", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.soundRange = this.register(new Setting("SoundRange", Float.valueOf(12.0F), Float.valueOf(0.0F), Float.valueOf(12.0F), test<invokedynamic>(this)));
        this.soundPlayer = this.register(new Setting("SoundPlayer", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(12.0F), test<invokedynamic>(this)));
        this.soundConfirm = this.register(new Setting("SoundConfirm", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.extraSelfCalc = this.register(new Setting("MinSelfDmg", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.antiFriendPop = this.register(new Setting("AntiFriendPop", AutoCrystal.AntiFriendPop.NONE, test<invokedynamic>(this)));
        this.noCount = this.register(new Setting("AntiCount", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.calcEvenIfNoDamage = this.register(new Setting("BigFriendCalc", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.predictFriendDmg = this.register(new Setting("PredictFriend", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.minMinDmg = this.register(new Setting("MinMinDmg", Float.valueOf(2.0F), Float.valueOf(0.0F), Float.valueOf(3.0F), test<invokedynamic>(this)));
        this.breakSwing = this.register(new Setting("BreakSwing", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.placeSwing = this.register(new Setting("PlaceSwing", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.exactHand = this.register(new Setting("ExactHand", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.justRender = this.register(new Setting("JustRender", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.fakeSwing = this.register(new Setting("FakeSwing", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.logic = this.register(new Setting("Logic", AutoCrystal.Logic.PLACEBREAK, test<invokedynamic>(this)));
        this.damageSync = this.register(new Setting("DamageSync", AutoCrystal.DamageSync.NONE, test<invokedynamic>(this)));
        this.damageSyncTime = this.register(new Setting("SyncDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), test<invokedynamic>(this)));
        this.dropOff = this.register(new Setting("DropOff", Float.valueOf(5.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), test<invokedynamic>(this)));
        this.confirm = this.register(new Setting("Confirm", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(1000), test<invokedynamic>(this)));
        this.syncedFeetPlace = this.register(new Setting("FeetSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.fullSync = this.register(new Setting("FullSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.syncCount = this.register(new Setting("SyncCount", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.hyperSync = this.register(new Setting("HyperSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.gigaSync = this.register(new Setting("GigaSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.syncySync = this.register(new Setting("SyncySync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.enormousSync = this.register(new Setting("EnormousSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.holySync = this.register(new Setting("UnbelievableSync", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.rotateFirst = this.register(new Setting("FirstRotation", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.threadMode = this.register(new Setting("Thread", AutoCrystal.ThreadMode.NONE, test<invokedynamic>(this)));
        this.threadDelay = this.register(new Setting("ThreadDelay", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(1000), test<invokedynamic>(this)));
        this.syncThreadBool = this.register(new Setting("ThreadSync", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.syncThreads = this.register(new Setting("SyncThreads", Integer.valueOf(1000), Integer.valueOf(1), Integer.valueOf(10000), test<invokedynamic>(this)));
        this.predictPos = this.register(new Setting("PredictPos", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.renderExtrapolation = this.register(new Setting("RenderExtrapolation", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.predictTicks = this.register(new Setting("ExtrapolationTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20), test<invokedynamic>(this)));
        this.rotations = this.register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), test<invokedynamic>(this)));
        this.predictRotate = this.register(new Setting("PredictRotate", Boolean.valueOf(true), test<invokedynamic>(this)));
        this.predictOffset = this.register(new Setting("PredictOffset", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(4.0F), test<invokedynamic>(this)));
        this.brownZombie = this.register(new Setting("BrownZombieMode", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.doublePopOnDamage = this.register(new Setting("DamagePop", Boolean.valueOf(false), test<invokedynamic>(this)));
        this.rotating = false;
        this.attackList = new ConcurrentLinkedQueue();
        this.crystalMap = new HashMap();
        this.efficientTarget = null;
        this.currentDamage = 0.0D;
        this.renderDamage = 0.0D;
        this.lastDamage = 0.0D;
        this.didRotation = false;
        this.switching = false;
        this.placePos = null;
        this.renderPos = null;
        this.mainHand = false;
        this.offHand = false;
        this.crystalCount = 0;
        this.minDmgCount = 0;
        this.lastSlot = -1;
        this.yaw = 0.0F;
        this.pitch = 0.0F;
        this.webPos = null;
        this.lastPos = null;
        this.posConfirmed = false;
        this.foundDoublePop = false;
        this.rotationPacketsSpoofed = 0;
        AutoCrystal.instance = this;
    }

    public static AutoCrystal getInstance() {
        if (AutoCrystal.instance == null) {
            AutoCrystal.instance = new AutoCrystal();
        }

        return AutoCrystal.instance;
    }

    public void onTick() {
        if (this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && ((Integer) this.eventMode.getValue()).intValue() == 3) {
            this.doAutoCrystal();
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            this.postProcessing();
        }

        if (event.getStage() == 0) {
            if (((Integer) this.eventMode.getValue()).intValue() == 2) {
                this.doAutoCrystal();
            }

        }
    }

    public void postTick() {
        if (this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) {
            this.processMultiThreading();
        }

    }

    public void onUpdate() {
        if (this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && ((Integer) this.eventMode.getValue()).intValue() == 1) {
            this.doAutoCrystal();
        }

    }

    public void onToggle() {
        AutoCrystal.brokenPos.clear();
        AutoCrystal.placedPos.clear();
        this.totemPops.clear();
        this.rotating = false;
    }

    public void onDisable() {
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }

        if (this.executor != null) {
            this.executor.shutdown();
        }

    }

    public void onEnable() {
        if (this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) {
            this.processMultiThreading();
        }

    }

    public String getDisplayInfo() {
        return this.switching ? "§aSwitch" : (AutoCrystal.target != null ? AutoCrystal.target.getName() : null);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getStage() == 0 && this.rotate.getValue() != AutoCrystal.Rotate.OFF && this.rotating && ((Integer) this.eventMode.getValue()).intValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer pos = (CPacketPlayer) event.getPacket();

            pos.yaw = this.yaw;
            pos.pitch = this.pitch;
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= ((Integer) this.rotations.getValue()).intValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }

        BlockPos pos1 = null;
        CPacketUseEntity packet;

        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == Action.ATTACK && packet.getEntityFromWorld(AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            pos1 = packet.getEntityFromWorld(AutoCrystal.mc.world).getPosition();
            if (((Boolean) this.removeAfterAttack.getValue()).booleanValue()) {
                ((Entity) Objects.requireNonNull(packet.getEntityFromWorld(AutoCrystal.mc.world))).setDead();
                AutoCrystal.mc.world.removeEntityFromWorld(packet.entityId);
            }
        }

        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity) event.getPacket()).getAction() == Action.ATTACK && packet.getEntityFromWorld(AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            EntityEnderCrystal crystal = (EntityEnderCrystal) packet.getEntityFromWorld(AutoCrystal.mc.world);

            if (((Boolean) this.antiBlock.getValue()).booleanValue() && EntityUtil.isCrystalAtFeet(crystal, (double) ((Float) this.range.getValue()).floatValue()) && pos1 != null) {
                this.rotateToPos(pos1);
                BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.placeSwing.getValue()).booleanValue(), ((Boolean) this.exactHand.getValue()).booleanValue());
            }
        }

    }

    @SubscribeEvent(
        priority = EventPriority.HIGH,
        receiveCanceled = true
    )
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!fullNullCheck()) {
            BlockPos blockpos;

            if (!((Boolean) this.justRender.getValue()).booleanValue() && this.switchTimer.passedMs((long) ((Integer) this.switchCooldown.getValue()).intValue()) && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !((Boolean) this.syncedFeetPlace.getValue()).booleanValue() || this.damageSync.getValue() == AutoCrystal.DamageSync.NONE)) {
                SPacketSpawnObject spacketspawnobject = (SPacketSpawnObject) event.getPacket();

                if (spacketspawnobject.getType() == 51 && AutoCrystal.mc.player.getDistanceSq(blockpos = new BlockPos(spacketspawnobject.getX(), spacketspawnobject.getY(), spacketspawnobject.getZ())) + (double) ((Float) this.predictOffset.getValue()).floatValue() <= MathUtil.square((double) ((Float) this.breakRange.getValue()).floatValue()) && (this.instantTimer.getValue() == AutoCrystal.PredictTimer.NONE || this.instantTimer.getValue() == AutoCrystal.PredictTimer.BREAK && this.breakTimer.passedMs((long) ((Integer) this.breakDelay.getValue()).intValue()) || this.instantTimer.getValue() == AutoCrystal.PredictTimer.PREDICT && this.predictTimer.passedMs((long) ((Integer) this.predictDelay.getValue()).intValue()))) {
                    if (this.predictSlowBreak(blockpos.down())) {
                        return;
                    }

                    if (((Boolean) this.predictFriendDmg.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL) && this.isRightThread()) {
                        Iterator iterator = AutoCrystal.mc.world.playerEntities.iterator();

                        while (iterator.hasNext()) {
                            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                            if (entityplayer != null && !AutoCrystal.mc.player.equals(entityplayer) && entityplayer.getDistanceSq(blockpos) <= MathUtil.square((double) (((Float) this.range.getValue()).floatValue() + ((Float) this.placeRange.getValue()).floatValue())) && Tater.friendManager.isFriend(entityplayer) && (double) DamageUtil.calculateDamage(blockpos, entityplayer) > (double) EntityUtil.getHealth(entityplayer) + 0.5D) {
                                return;
                            }
                        }
                    }

                    float f;

                    if (AutoCrystal.placedPos.contains(blockpos.down())) {
                        if (this.isRightThread() && ((Boolean) this.superSafe.getValue()).booleanValue()) {
                            if (DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue()) && ((double) (f = DamageUtil.calculateDamage(blockpos, AutoCrystal.mc.player)) - 0.5D > (double) EntityUtil.getHealth(AutoCrystal.mc.player) || f > ((Float) this.maxSelfBreak.getValue()).floatValue())) {
                                return;
                            }
                        } else if (((Boolean) this.superSafe.getValue()).booleanValue()) {
                            return;
                        }

                        this.attackCrystalPredict(spacketspawnobject.getEntityID(), blockpos);
                        return;
                    } else if (((Boolean) this.predictCalc.getValue()).booleanValue() && this.isRightThread()) {
                        f = -1.0F;
                        if (DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue())) {
                            f = DamageUtil.calculateDamage(blockpos, AutoCrystal.mc.player);
                        }

                        if ((double) f + 0.5D < (double) EntityUtil.getHealth(AutoCrystal.mc.player) && f <= ((Float) this.maxSelfBreak.getValue()).floatValue()) {
                            Iterator iterator1 = AutoCrystal.mc.world.playerEntities.iterator();

                            while (iterator1.hasNext()) {
                                EntityPlayer entityplayer1 = (EntityPlayer) iterator1.next();
                                float f1;

                                if (entityplayer1.getDistanceSq(blockpos) <= MathUtil.square((double) ((Float) this.range.getValue()).floatValue()) && EntityUtil.isValid(entityplayer1, (double) (((Float) this.range.getValue()).floatValue() + ((Float) this.breakRange.getValue()).floatValue())) && (!((Boolean) this.antiNaked.getValue()).booleanValue() || !DamageUtil.isNaked(entityplayer1)) && ((f1 = DamageUtil.calculateDamage(blockpos, entityplayer1)) > f || f1 > ((Float) this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue()) || f1 > EntityUtil.getHealth(entityplayer1))) {
                                    if (((Boolean) this.predictRotate.getValue()).booleanValue() && ((Integer) this.eventMode.getValue()).intValue() != 2 && (this.rotate.getValue() == AutoCrystal.Rotate.BREAK || this.rotate.getValue() == AutoCrystal.Rotate.ALL)) {
                                        this.rotateToPos(blockpos);
                                    }

                                    this.attackCrystalPredict(spacketspawnobject.getEntityID(), blockpos);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (!((Boolean) this.soundConfirm.getValue()).booleanValue() && event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion spacketexplosion = (SPacketExplosion) event.getPacket();
                BlockPos blockpos1 = (new BlockPos(spacketexplosion.getX(), spacketexplosion.getY(), spacketexplosion.getZ())).down();

                this.removePos(blockpos1);
            } else if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities pos = (SPacketDestroyEntities) event.getPacket();
                int[] pos1 = pos.getEntityIDs();
                int selfDamage = pos1.length;

                for (int friend = 0; friend < selfDamage; ++friend) {
                    int id = pos1[friend];
                    Entity entity = AutoCrystal.mc.world.getEntityByID(id);

                    if (entity instanceof EntityEnderCrystal) {
                        AutoCrystal.brokenPos.remove((new BlockPos(entity.getPositionVector())).down());
                        AutoCrystal.placedPos.remove((new BlockPos(entity.getPositionVector())).down());
                    }
                }
            } else if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus spacketentitystatus = (SPacketEntityStatus) event.getPacket();

                if (spacketentitystatus.getOpCode() == 35 && spacketentitystatus.getEntity(AutoCrystal.mc.world) instanceof EntityPlayer) {
                    this.totemPops.put((EntityPlayer) spacketentitystatus.getEntity(AutoCrystal.mc.world), (new Timer()).reset());
                }
            } else {
                SPacketSoundEffect packet;

                if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect) event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    blockpos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
                    if (((Boolean) this.sound.getValue()).booleanValue() || this.threadMode.getValue() == AutoCrystal.ThreadMode.SOUND) {
                        NoSoundLag.removeEntities(packet, ((Float) this.soundRange.getValue()).floatValue());
                    }

                    if (((Boolean) this.soundConfirm.getValue()).booleanValue()) {
                        this.removePos(blockpos);
                    }

                    if (this.threadMode.getValue() == AutoCrystal.ThreadMode.SOUND && this.isRightThread() && AutoCrystal.mc.player != null && AutoCrystal.mc.player.getDistanceSq(blockpos) < MathUtil.square((double) ((Float) this.soundPlayer.getValue()).floatValue())) {
                        this.handlePool(true);
                    }
                }
            }

        }
    }

    private boolean predictSlowBreak(BlockPos pos) {
        return ((Boolean) this.antiCommit.getValue()).booleanValue() && AutoCrystal.lowDmgPos.remove(pos) ? this.shouldSlowBreak(false) : false;
    }

    private boolean isRightThread() {
        return Util.mc.isCallingFromMinecraftThread() || !Tater.eventManager.ticksOngoing() && !this.threadOngoing.get();
    }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if (((Boolean) this.predictRotate.getValue()).booleanValue() && (((Integer) this.eventMode.getValue()).intValue() != 2 || this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) && (this.rotate.getValue() == AutoCrystal.Rotate.BREAK || this.rotate.getValue() == AutoCrystal.Rotate.ALL)) {
            this.rotateToPos(pos);
        }

        CPacketUseEntity attackPacket = new CPacketUseEntity();

        attackPacket.entityId = entityID;
        attackPacket.action = Action.ATTACK;
        AutoCrystal.mc.player.connection.sendPacket(attackPacket);
        if (((Boolean) this.breakSwing.getValue()).booleanValue()) {
            AutoCrystal.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }

        if (((Boolean) this.resetBreakTimer.getValue()).booleanValue()) {
            this.breakTimer.reset();
        }

        this.predictTimer.reset();
    }

    private void removePos(BlockPos pos) {
        if (this.damageSync.getValue() == AutoCrystal.DamageSync.PLACE) {
            if (AutoCrystal.placedPos.remove(pos)) {
                this.posConfirmed = true;
            }
        } else if (this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK && AutoCrystal.brokenPos.remove(pos)) {
            this.posConfirmed = true;
        }

    }

    public void onRender3D(Render3DEvent event) {
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == AutoCrystal.Switch.CALC) && this.renderPos != null && ((Boolean) this.render.getValue()).booleanValue() && (((Boolean) this.box.getValue()).booleanValue() || ((Boolean) this.text.getValue()).booleanValue() || ((Boolean) this.outline.getValue()).booleanValue())) {
            RenderUtil.drawBoxESP(this.renderPos, ((Boolean) this.colorSync.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()) : new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue()), ((Boolean) this.customOutline.getValue()).booleanValue(), ((Boolean) this.colorSync.getValue()).booleanValue() ? this.getCurrentColor() : new Color(((Integer) this.cRed.getValue()).intValue(), ((Integer) this.cGreen.getValue()).intValue(), ((Integer) this.cBlue.getValue()).intValue(), ((Integer) this.cAlpha.getValue()).intValue()), ((Float) this.lineWidth.getValue()).floatValue(), ((Boolean) this.outline.getValue()).booleanValue(), ((Boolean) this.box.getValue()).booleanValue(), ((Integer) this.boxAlpha.getValue()).intValue(), false);
            if (((Boolean) this.text.getValue()).booleanValue()) {
                RenderUtil.drawText(this.renderPos, (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int) this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage)})) + "");
            }
        }

    }

    @SubscribeEvent
    public void onKeyInput(KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !(AutoCrystal.mc.currentScreen instanceof Gui) && ((Bind) this.switchBind.getValue()).getKey() == Keyboard.getEventKey()) {
            if (((Boolean) this.switchBack.getValue()).booleanValue() && ((Boolean) this.offhandSwitch.getValue()).booleanValue() && this.offHand) {
                Offhand module = (Offhand) Tater.moduleManager.getModuleByClass(Offhand.class);

                if (module.isOff()) {
                    Command.sendMessage("<" + this.getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
                } else {
                    module.setMode(Offhand.Mode2.TOTEMS);
                    module.doSwitch();
                }

                return;
            }

            this.switching = !this.switching;
        }

    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
            if (this.executor != null) {
                this.executor.shutdown();
            }

            if (this.thread != null) {
                this.shouldInterrupt.set(true);
            }
        }

    }

    private void postProcessing() {
        if (this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && ((Integer) this.eventMode.getValue()).intValue() == 2 && this.rotate.getValue() != AutoCrystal.Rotate.OFF && ((Boolean) this.rotateFirst.getValue()).booleanValue()) {
            switch ((AutoCrystal.Logic) this.logic.getValue()) {
            case BREAKPLACE:
                this.postProcessBreak();
                this.postProcessPlace();
                break;

            case PLACEBREAK:
                this.postProcessPlace();
                this.postProcessBreak();
            }

        }
    }

    private void postProcessBreak() {
        for (; !this.packetUseEntities.isEmpty(); this.breakTimer.reset()) {
            CPacketUseEntity packet = (CPacketUseEntity) this.packetUseEntities.poll();

            AutoCrystal.mc.player.connection.sendPacket(packet);
            if (((Boolean) this.breakSwing.getValue()).booleanValue()) {
                AutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }

    }

    private void postProcessPlace() {
        if (this.placeInfo != null) {
            this.placeInfo.runPlace();
            this.placeTimer.reset();
            this.placeInfo = null;
        }

    }

    private void processMultiThreading() {
        if (!this.isOff()) {
            if (this.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
                this.handleWhile();
            } else if (this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) {
                this.handlePool(false);
            }

        }
    }

    private void handlePool(boolean justDoIt) {
        if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || this.syncroTimer.passedMs((long) ((Integer) this.syncThreads.getValue()).intValue()) && ((Boolean) this.syncThreadBool.getValue()).booleanValue()) {
            if (this.executor != null) {
                this.executor.shutdown();
            }

            this.executor = this.getExecutor();
            this.syncroTimer.reset();
        }

    }

    private void handleWhile() {
        if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs((long) ((Integer) this.syncThreads.getValue()).intValue()) && ((Boolean) this.syncThreadBool.getValue()).booleanValue()) {
            if (this.thread == null) {
                this.thread = new Thread(AutoCrystal.RAutoCrystal.getInstance(this));
            } else if (this.syncroTimer.passedMs((long) ((Integer) this.syncThreads.getValue()).intValue()) && !this.shouldInterrupt.get() && ((Boolean) this.syncThreadBool.getValue()).booleanValue()) {
                this.shouldInterrupt.set(true);
                this.syncroTimer.reset();
                return;
            }

            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                this.thread = new Thread(AutoCrystal.RAutoCrystal.getInstance(this));
            }

            if (this.thread != null && this.thread.getState() == State.NEW) {
                try {
                    this.thread.start();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                this.syncroTimer.reset();
            }
        }

    }

    private ScheduledExecutorService getExecutor() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        service.scheduleAtFixedRate(AutoCrystal.RAutoCrystal.getInstance(this), 0L, (long) ((Integer) this.threadDelay.getValue()).intValue(), TimeUnit.MILLISECONDS);
        return service;
    }

    public void doAutoCrystal() {
        if (!((Boolean) this.brownZombie.getValue()).booleanValue()) {
            if (this.check()) {
                switch ((AutoCrystal.Logic) this.logic.getValue()) {
                case BREAKPLACE:
                    this.breakCrystal();
                    this.placeCrystal();
                    break;

                case PLACEBREAK:
                    this.placeCrystal();
                    this.breakCrystal();
                }

                this.manualBreaker();
            }

        }
    }

    private boolean check() {
        if (fullNullCheck()) {
            return false;
        } else {
            if (this.syncTimer.passedMs((long) ((Integer) this.damageSyncTime.getValue()).intValue())) {
                this.currentSyncTarget = null;
                this.syncedCrystalPos = null;
                this.syncedPlayerPos = null;
            } else if (((Boolean) this.syncySync.getValue()).booleanValue() && this.syncedCrystalPos != null) {
                this.posConfirmed = true;
            }

            this.foundDoublePop = false;
            if (this.renderTimer.passedMs(500L)) {
                this.renderPos = null;
                this.renderTimer.reset();
            }

            this.mainHand = AutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
            this.offHand = AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
            this.currentDamage = 0.0D;
            this.placePos = null;
            if (this.lastSlot != AutoCrystal.mc.player.inventory.currentItem || AutoTrap.isPlacing || Surround.isPlacing) {
                this.lastSlot = AutoCrystal.mc.player.inventory.currentItem;
                this.switchTimer.reset();
            }

            if (!this.offHand && !this.mainHand) {
                this.placeInfo = null;
                this.packetUseEntities.clear();
            }

            if (this.offHand || this.mainHand) {
                this.switching = false;
            }

            if ((this.offHand || this.mainHand || this.switchMode.getValue() != AutoCrystal.Switch.BREAKSLOT || this.switching) && DamageUtil.canBreakWeakness(AutoCrystal.mc.player) && this.switchTimer.passedMs((long) ((Integer) this.switchCooldown.getValue()).intValue())) {
                if (((Boolean) this.mineSwitch.getValue()).booleanValue() && Mouse.isButtonDown(0) && (this.switching || this.autoSwitch.getValue() == AutoCrystal.AutoSwitch.ALWAYS) && Mouse.isButtonDown(1) && AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
                    this.switchItem();
                }

                this.mapCrystals();
                if (!this.posConfirmed && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && this.syncTimer.passedMs((long) ((Integer) this.confirm.getValue()).intValue())) {
                    this.syncTimer.setMs((long) (((Integer) this.damageSyncTime.getValue()).intValue() + 1));
                }

                return true;
            } else {
                this.renderPos = null;
                AutoCrystal.target = null;
                this.rotating = false;
                return false;
            }
        }
    }

    private void mapCrystals() {
        this.efficientTarget = null;
        if (((Integer) this.packets.getValue()).intValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue();
            this.crystalMap = new HashMap();
        }

        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5F;
        Iterator iterator = AutoCrystal.mc.world.loadedEntityList.iterator();

        Entity entry;

        while (iterator.hasNext()) {
            entry = (Entity) iterator.next();
            if (!entry.isDead && entry instanceof EntityEnderCrystal && this.isValid(entry)) {
                if (((Boolean) this.syncedFeetPlace.getValue()).booleanValue() && entry.getPosition().down().equals(this.syncedCrystalPos) && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
                    ++this.minDmgCount;
                    ++this.crystalCount;
                    if (((Boolean) this.syncCount.getValue()).booleanValue()) {
                        this.minDmgCount = ((Integer) this.wasteAmount.getValue()).intValue() + 1;
                        this.crystalCount = ((Integer) this.wasteAmount.getValue()).intValue() + 1;
                    }

                    if (((Boolean) this.hyperSync.getValue()).booleanValue()) {
                        maxCrystal = null;
                        break;
                    }
                } else {
                    boolean crystal = false;
                    boolean damage = false;
                    float selfDamage = -1.0F;

                    if (DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue())) {
                        selfDamage = DamageUtil.calculateDamage(entry, AutoCrystal.mc.player);
                    }

                    if ((double) selfDamage + 0.5D < (double) EntityUtil.getHealth(AutoCrystal.mc.player) && selfDamage <= ((Float) this.maxSelfBreak.getValue()).floatValue()) {
                        Iterator iterator1 = AutoCrystal.mc.world.playerEntities.iterator();

                        while (iterator1.hasNext()) {
                            EntityPlayer player = (EntityPlayer) iterator1.next();

                            if (player.getDistanceSq(entry) <= MathUtil.square((double) ((Float) this.range.getValue()).floatValue())) {
                                if (EntityUtil.isValid(player, (double) (((Float) this.range.getValue()).floatValue() + ((Float) this.breakRange.getValue()).floatValue()))) {
                                    float damage1;

                                    if ((!((Boolean) this.antiNaked.getValue()).booleanValue() || !DamageUtil.isNaked(player)) && ((damage1 = DamageUtil.calculateDamage(entry, player)) > selfDamage || damage1 > ((Float) this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue()) || damage1 > EntityUtil.getHealth(player))) {
                                        if (damage1 > maxDamage) {
                                            maxDamage = damage1;
                                            maxCrystal = entry;
                                        }

                                        if (((Integer) this.packets.getValue()).intValue() != 1) {
                                            if (this.crystalMap.get(entry) == null || ((Float) this.crystalMap.get(entry)).floatValue() < damage1) {
                                                this.crystalMap.put(entry, Float.valueOf(damage1));
                                            }
                                        } else {
                                            if (damage1 >= ((Float) this.minDamage.getValue()).floatValue() || !((Boolean) this.wasteMinDmgCount.getValue()).booleanValue()) {
                                                crystal = true;
                                            }

                                            damage = true;
                                        }
                                    }
                                } else if ((this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL) && Tater.friendManager.isFriend(player.getName()) && (double) DamageUtil.calculateDamage(entry, player) > (double) EntityUtil.getHealth(player) + 0.5D) {
                                    maxCrystal = maxCrystal;
                                    maxDamage = maxDamage;
                                    this.crystalMap.remove(entry);
                                    if (((Boolean) this.noCount.getValue()).booleanValue()) {
                                        crystal = false;
                                        damage = false;
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    if (damage) {
                        ++this.minDmgCount;
                        if (crystal) {
                            ++this.crystalCount;
                        }
                    }
                }
            }
        }

        if (this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK && ((double) maxDamage > this.lastDamage || this.syncTimer.passedMs((long) ((Integer) this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == AutoCrystal.DamageSync.NONE)) {
            this.lastDamage = (double) maxDamage;
        }

        if (((Boolean) this.enormousSync.getValue()).booleanValue() && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue() && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && this.syncedCrystalPos != null) {
            if (((Boolean) this.syncCount.getValue()).booleanValue()) {
                this.minDmgCount = ((Integer) this.wasteAmount.getValue()).intValue() + 1;
                this.crystalCount = ((Integer) this.wasteAmount.getValue()).intValue() + 1;
            }

        } else {
            if (((Boolean) this.webAttack.getValue()).booleanValue() && this.webPos != null) {
                if (AutoCrystal.mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square((double) ((Float) this.breakRange.getValue()).floatValue())) {
                    this.webPos = null;
                } else {
                    iterator = AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.webPos.up())).iterator();

                    while (iterator.hasNext()) {
                        entry = (Entity) iterator.next();
                        if (entry instanceof EntityEnderCrystal) {
                            this.attackList.add(entry);
                            this.efficientTarget = entry;
                            this.webPos = null;
                            this.lastDamage = 0.5D;
                            return;
                        }
                    }
                }
            }

            if (this.shouldSlowBreak(true) && maxDamage < ((Float) this.minDamage.getValue()).floatValue() && (AutoCrystal.target == null || EntityUtil.getHealth(AutoCrystal.target) > ((Float) this.facePlace.getValue()).floatValue() || !this.breakTimer.passedMs((long) ((Integer) this.facePlaceSpeed.getValue()).intValue()) && ((Boolean) this.slowFaceBreak.getValue()).booleanValue() && Mouse.isButtonDown(0) && ((Boolean) this.holdFacePlace.getValue()).booleanValue() && ((Boolean) this.holdFaceBreak.getValue()).booleanValue())) {
                this.efficientTarget = null;
            } else {
                if (((Integer) this.packets.getValue()).intValue() == 1) {
                    this.efficientTarget = maxCrystal;
                } else {
                    this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);

                    for (iterator = this.crystalMap.entrySet().iterator(); iterator.hasNext(); ++this.minDmgCount) {
                        Entry entry = (Entry) iterator.next();
                        Entity entity = (Entity) entry.getKey();
                        float f = ((Float) entry.getValue()).floatValue();

                        if (f >= ((Float) this.minDamage.getValue()).floatValue() || !((Boolean) this.wasteMinDmgCount.getValue()).booleanValue()) {
                            ++this.crystalCount;
                        }

                        this.attackList.add(entity);
                    }
                }

            }
        }
    }

    private boolean shouldSlowBreak(boolean withManual) {
        return withManual && ((Boolean) this.manual.getValue()).booleanValue() && ((Boolean) this.manualMinDmg.getValue()).booleanValue() && Mouse.isButtonDown(1) && (!Mouse.isButtonDown(0) || !((Boolean) this.holdFacePlace.getValue()).booleanValue()) || ((Boolean) this.holdFacePlace.getValue()).booleanValue() && ((Boolean) this.holdFaceBreak.getValue()).booleanValue() && Mouse.isButtonDown(0) && !this.breakTimer.passedMs((long) ((Integer) this.facePlaceSpeed.getValue()).intValue()) || ((Boolean) this.slowFaceBreak.getValue()).booleanValue() && !this.breakTimer.passedMs((long) ((Integer) this.facePlaceSpeed.getValue()).intValue());
    }

    private void placeCrystal() {
        int crystalLimit = ((Integer) this.wasteAmount.getValue()).intValue();

        if (this.placeTimer.passedMs((long) ((Integer) this.placeDelay.getValue()).intValue()) && ((Boolean) this.place.getValue()).booleanValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == AutoCrystal.Switch.CALC || this.switchMode.getValue() == AutoCrystal.Switch.BREAKSLOT && this.switching)) {
            if ((this.offHand || this.mainHand || this.switchMode.getValue() != AutoCrystal.Switch.ALWAYS && !this.switching) && this.crystalCount >= crystalLimit && (!((Boolean) this.antiSurround.getValue()).booleanValue() || this.lastPos == null || !this.lastPos.equals(this.placePos))) {
                return;
            }

            this.calculateDamage(this.getTarget(this.targetMode.getValue() == AutoCrystal.Target.UNSAFE));
            if (AutoCrystal.target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE && (this.currentDamage > (double) ((Float) this.minDamage.getValue()).floatValue() || ((Boolean) this.lethalSwitch.getValue()).booleanValue() && EntityUtil.getHealth(AutoCrystal.target) <= ((Float) this.facePlace.getValue()).floatValue()) && !this.switchItem()) {
                    return;
                }

                if (this.currentDamage < (double) ((Float) this.minDamage.getValue()).floatValue() && ((Boolean) this.limitFacePlace.getValue()).booleanValue()) {
                    crystalLimit = 1;
                }

                if (this.currentDamage >= (double) ((Float) this.minMinDmg.getValue()).floatValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE) && (this.crystalCount < crystalLimit || ((Boolean) this.antiSurround.getValue()).booleanValue() && this.lastPos != null && this.lastPos.equals(this.placePos)) && (this.currentDamage > (double) ((Float) this.minDamage.getValue()).floatValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0D && (DamageUtil.isArmorLow(AutoCrystal.target, ((Integer) this.minArmor.getValue()).intValue()) || EntityUtil.getHealth(AutoCrystal.target) <= ((Float) this.facePlace.getValue()).floatValue() || this.currentDamage > (double) ((Float) this.minDamage.getValue()).floatValue() || this.shouldHoldFacePlace())) {
                    float damageOffset = this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK ? ((Float) this.dropOff.getValue()).floatValue() - 5.0F : 0.0F;
                    boolean syncflag = false;

                    if (((Boolean) this.syncedFeetPlace.getValue()).booleanValue() && this.placePos.equals(this.lastPos) && this.isEligableForFeetSync(AutoCrystal.target, this.placePos) && !this.syncTimer.passedMs((long) ((Integer) this.damageSyncTime.getValue()).intValue()) && AutoCrystal.target.equals(this.currentSyncTarget) && AutoCrystal.target.getPosition().equals(this.syncedPlayerPos) && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
                        this.syncedCrystalPos = this.placePos;
                        this.lastDamage = this.currentDamage;
                        if (((Boolean) this.fullSync.getValue()).booleanValue()) {
                            this.lastDamage = 100.0D;
                        }

                        syncflag = true;
                    }

                    if (syncflag || this.currentDamage - (double) damageOffset > this.lastDamage || this.syncTimer.passedMs((long) ((Integer) this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == AutoCrystal.DamageSync.NONE) {
                        if (!syncflag && this.damageSync.getValue() != AutoCrystal.DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }

                        this.renderPos = this.placePos;
                        this.renderDamage = this.currentDamage;
                        if (this.switchItem()) {
                            this.currentSyncTarget = AutoCrystal.target;
                            this.syncedPlayerPos = AutoCrystal.target.getPosition();
                            if (this.foundDoublePop) {
                                this.totemPops.put(AutoCrystal.target, (new Timer()).reset());
                            }

                            this.rotateToPos(this.placePos);
                            if (this.addTolowDmg || ((Boolean) this.actualSlowBreak.getValue()).booleanValue() && this.currentDamage < (double) ((Float) this.minDamage.getValue()).floatValue()) {
                                AutoCrystal.lowDmgPos.add(this.placePos);
                            }

                            AutoCrystal.placedPos.add(this.placePos);
                            if (!((Boolean) this.justRender.getValue()).booleanValue()) {
                                if (((Integer) this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && ((Boolean) this.rotateFirst.getValue()).booleanValue() && this.rotate.getValue() != AutoCrystal.Rotate.OFF) {
                                    this.placeInfo = new AutoCrystal.PlaceInfo(this.placePos, this.offHand, ((Boolean) this.placeSwing.getValue()).booleanValue(), ((Boolean) this.exactHand.getValue()).booleanValue());
                                } else {
                                    BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean) this.placeSwing.getValue()).booleanValue(), ((Boolean) this.exactHand.getValue()).booleanValue());
                                }
                            }

                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs((long) ((Integer) this.damageSyncTime.getValue()).intValue())) {
                                this.syncedCrystalPos = null;
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            } else {
                this.renderPos = null;
            }
        }

    }

    private boolean shouldHoldFacePlace() {
        this.addTolowDmg = false;
        if (((Boolean) this.holdFacePlace.getValue()).booleanValue() && Mouse.isButtonDown(0)) {
            this.addTolowDmg = true;
            return true;
        } else {
            return false;
        }
    }

    private boolean switchItem() {
        if (!this.offHand && !this.mainHand) {
            switch ((AutoCrystal.AutoSwitch) this.autoSwitch.getValue()) {
            case NONE:
                return false;

            case TOGGLE:
                if (!this.switching) {
                    return false;
                }

            case ALWAYS:
                if (this.doSwitch()) {
                    return true;
                }

            default:
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean doSwitch() {
        if (((Boolean) this.offhandSwitch.getValue()).booleanValue()) {
            Offhand module = (Offhand) Tater.moduleManager.getModuleByClass(Offhand.class);

            if (module.isOff()) {
                Command.sendMessage("<" + this.getDisplayName() + "> §cSwitch failed. Enable the Offhand module.");
                this.switching = false;
                return false;
            } else {
                this.switching = false;
                return true;
            }
        } else {
            if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
                this.mainHand = false;
            } else {
                InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
                this.mainHand = true;
            }

            this.switching = false;
            return true;
        }
    }

    private void calculateDamage(EntityPlayer targettedPlayer) {
        if (targettedPlayer != null || this.targetMode.getValue() == AutoCrystal.Target.DAMAGE || ((Boolean) this.fullCalc.getValue()).booleanValue()) {
            float maxDamage = 0.5F;
            EntityPlayer currentTarget = null;
            BlockPos currentPos = null;
            float maxSelfDamage = 0.0F;

            this.foundDoublePop = false;
            BlockPos setToAir = null;
            IBlockState state = null;
            BlockPos playerPos;

            if (((Boolean) this.webAttack.getValue()).booleanValue() && targettedPlayer != null && AutoCrystal.mc.world.getBlockState(playerPos = new BlockPos(targettedPlayer.getPositionVector())).getBlock() == Blocks.WEB) {
                setToAir = playerPos;
                state = AutoCrystal.mc.world.getBlockState(playerPos);
                AutoCrystal.mc.world.setBlockToAir(playerPos);
            }

            Iterator iterator = BlockUtil.possiblePlacePositions(((Float) this.placeRange.getValue()).floatValue(), ((Boolean) this.antiSurround.getValue()).booleanValue(), ((Boolean) this.oneDot15.getValue()).booleanValue()).iterator();

            while (iterator.hasNext()) {
                BlockPos pos = (BlockPos) iterator.next();

                if (BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == AutoCrystal.Raytrace.PLACE || this.raytrace.getValue() == AutoCrystal.Raytrace.FULL) && AutoCrystal.mc.player.getDistanceSq(pos) > MathUtil.square((double) ((Float) this.placetrace.getValue()).floatValue()), 1.0F)) {
                    float selfDamage = -1.0F;

                    if (DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue())) {
                        selfDamage = DamageUtil.calculateDamage(pos, AutoCrystal.mc.player);
                    }

                    if ((double) selfDamage + 0.5D < (double) EntityUtil.getHealth(AutoCrystal.mc.player) && selfDamage <= ((Float) this.maxSelfPlace.getValue()).floatValue()) {
                        if (targettedPlayer != null) {
                            float maxDamageBefore = DamageUtil.calculateDamage(pos, targettedPlayer);

                            if (((Boolean) this.calcEvenIfNoDamage.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.PLACE)) {
                                boolean currentTargetBefore = false;
                                Iterator currentPosBefore = AutoCrystal.mc.world.playerEntities.iterator();

                                while (currentPosBefore.hasNext()) {
                                    EntityPlayer maxSelfDamageBefore = (EntityPlayer) currentPosBefore.next();

                                    if (maxSelfDamageBefore != null && !AutoCrystal.mc.player.equals(maxSelfDamageBefore) && maxSelfDamageBefore.getDistanceSq(pos) <= MathUtil.square((double) (((Float) this.range.getValue()).floatValue() + ((Float) this.placeRange.getValue()).floatValue())) && Tater.friendManager.isFriend(maxSelfDamageBefore) && (double) DamageUtil.calculateDamage(pos, maxSelfDamageBefore) > (double) EntityUtil.getHealth(maxSelfDamageBefore) + 0.5D) {
                                        currentTargetBefore = true;
                                        break;
                                    }
                                }

                                if (currentTargetBefore) {
                                    continue;
                                }
                            }

                            if (this.isDoublePoppable(targettedPlayer, maxDamageBefore) && (currentPos == null || targettedPlayer.getDistanceSq(pos) < targettedPlayer.getDistanceSq(currentPos))) {
                                currentTarget = targettedPlayer;
                                maxDamage = maxDamageBefore;
                                currentPos = pos;
                                this.foundDoublePop = true;
                            } else if (!this.foundDoublePop && (maxDamageBefore > maxDamage || ((Boolean) this.extraSelfCalc.getValue()).booleanValue() && maxDamageBefore >= maxDamage && selfDamage < maxSelfDamage) && (maxDamageBefore > selfDamage || maxDamageBefore > ((Float) this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue()) || maxDamageBefore > EntityUtil.getHealth(targettedPlayer))) {
                                maxDamage = maxDamageBefore;
                                currentTarget = targettedPlayer;
                                currentPos = pos;
                                maxSelfDamage = selfDamage;
                            }
                        } else {
                            Iterator friendDamage = AutoCrystal.mc.world.playerEntities.iterator();

                            while (friendDamage.hasNext()) {
                                EntityPlayer player = (EntityPlayer) friendDamage.next();

                                if (EntityUtil.isValid(player, (double) (((Float) this.placeRange.getValue()).floatValue() + ((Float) this.range.getValue()).floatValue()))) {
                                    if (!((Boolean) this.antiNaked.getValue()).booleanValue() || !DamageUtil.isNaked(player)) {
                                        float playerDamage = DamageUtil.calculateDamage(pos, player);

                                        if (((Boolean) this.doublePopOnDamage.getValue()).booleanValue() && this.isDoublePoppable(player, playerDamage) && (currentPos == null || player.getDistanceSq(pos) < player.getDistanceSq(currentPos))) {
                                            currentTarget = player;
                                            maxDamage = playerDamage;
                                            currentPos = pos;
                                            maxSelfDamage = selfDamage;
                                            this.foundDoublePop = true;
                                            if (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.PLACE) {
                                                break;
                                            }
                                        } else if (!this.foundDoublePop && (playerDamage > maxDamage || ((Boolean) this.extraSelfCalc.getValue()).booleanValue() && playerDamage >= maxDamage && selfDamage < maxSelfDamage) && (playerDamage > selfDamage || playerDamage > ((Float) this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean) this.suicide.getValue()).booleanValue()) || playerDamage > EntityUtil.getHealth(player))) {
                                            maxDamage = playerDamage;
                                            currentTarget = player;
                                            currentPos = pos;
                                            maxSelfDamage = selfDamage;
                                        }
                                    }
                                } else if ((this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.PLACE) && player != null && player.getDistanceSq(pos) <= MathUtil.square((double) (((Float) this.range.getValue()).floatValue() + ((Float) this.placeRange.getValue()).floatValue())) && Tater.friendManager.isFriend(player) && (double) DamageUtil.calculateDamage(pos, player) > (double) EntityUtil.getHealth(player) + 0.5D) {
                                    maxDamage = maxDamage;
                                    currentTarget = currentTarget;
                                    currentPos = currentPos;
                                    maxSelfDamage = maxSelfDamage;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (setToAir != null) {
                AutoCrystal.mc.world.setBlockState(setToAir, state);
                this.webPos = currentPos;
            }

            AutoCrystal.target = currentTarget;
            this.currentDamage = (double) maxDamage;
            this.placePos = currentPos;
        }
    }

    private EntityPlayer getTarget(boolean unsafe) {
        if (this.targetMode.getValue() == AutoCrystal.Target.DAMAGE) {
            return null;
        } else {
            Object currentTarget = null;
            Iterator profile = AutoCrystal.mc.world.playerEntities.iterator();

            while (profile.hasNext()) {
                EntityPlayer newTarget = (EntityPlayer) profile.next();

                if (!EntityUtil.isntValid(newTarget, (double) (((Float) this.placeRange.getValue()).floatValue() + ((Float) this.range.getValue()).floatValue())) && (!((Boolean) this.antiNaked.getValue()).booleanValue() || !DamageUtil.isNaked(newTarget)) && (!unsafe || !EntityUtil.isSafe(newTarget))) {
                    if (((Integer) this.minArmor.getValue()).intValue() > 0 && DamageUtil.isArmorLow(newTarget, ((Integer) this.minArmor.getValue()).intValue())) {
                        currentTarget = newTarget;
                        break;
                    }

                    if (currentTarget == null) {
                        currentTarget = newTarget;
                    } else if (AutoCrystal.mc.player.getDistanceSq(newTarget) < AutoCrystal.mc.player.getDistanceSq((Entity) currentTarget)) {
                        currentTarget = newTarget;
                    }
                }
            }

            if (unsafe && currentTarget == null) {
                return this.getTarget(false);
            } else {
                if (((Boolean) this.predictPos.getValue()).booleanValue() && currentTarget != null) {
                    GameProfile profile1 = new GameProfile(((EntityPlayer) currentTarget).getUniqueID() == null ? UUID.fromString("8af022c8-b926-41a0-8b79-2b544ff00fcf") : ((EntityPlayer) currentTarget).getUniqueID(), ((EntityPlayer) currentTarget).getName());
                    EntityOtherPlayerMP newTarget1 = new EntityOtherPlayerMP(AutoCrystal.mc.world, profile1);
                    Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition((EntityPlayer) currentTarget, ((Integer) this.predictTicks.getValue()).intValue());

                    newTarget1.copyLocationAndAnglesFrom((Entity) currentTarget);
                    newTarget1.posX = extrapolatePosition.x;
                    newTarget1.posY = extrapolatePosition.y;
                    newTarget1.posZ = extrapolatePosition.z;
                    newTarget1.setHealth(EntityUtil.getHealth((Entity) currentTarget));
                    newTarget1.inventory.copyInventory(((EntityPlayer) currentTarget).inventory);
                    currentTarget = newTarget1;
                }

                return (EntityPlayer) currentTarget;
            }
        }
    }

    private void breakCrystal() {
        if (((Boolean) this.explode.getValue()).booleanValue() && this.breakTimer.passedMs((long) ((Integer) this.breakDelay.getValue()).intValue()) && (this.switchMode.getValue() == AutoCrystal.Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (((Integer) this.packets.getValue()).intValue() == 1 && this.efficientTarget != null) {
                if (((Boolean) this.justRender.getValue()).booleanValue()) {
                    this.doFakeSwing();
                    return;
                }

                if (((Boolean) this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean) this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
                    return;
                }

                this.rotateTo(this.efficientTarget);
                this.attackEntity(this.efficientTarget);
                this.breakTimer.reset();
            } else if (!this.attackList.isEmpty()) {
                if (((Boolean) this.justRender.getValue()).booleanValue()) {
                    this.doFakeSwing();
                    return;
                }

                if (((Boolean) this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean) this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
                    return;
                }

                for (int i = 0; i < ((Integer) this.packets.getValue()).intValue(); ++i) {
                    Entity entity = (Entity) this.attackList.poll();

                    if (entity != null) {
                        this.rotateTo(entity);
                        this.attackEntity(entity);
                    }
                }

                this.breakTimer.reset();
            }
        }

    }

    private void attackEntity(Entity entity) {
        if (entity != null) {
            if (((Integer) this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && ((Boolean) this.rotateFirst.getValue()).booleanValue() && this.rotate.getValue() != AutoCrystal.Rotate.OFF) {
                this.packetUseEntities.add(new CPacketUseEntity(entity));
            } else {
                EntityUtil.attackEntity(entity, ((Boolean) this.sync.getValue()).booleanValue(), ((Boolean) this.breakSwing.getValue()).booleanValue());
                AutoCrystal.brokenPos.add((new BlockPos(entity.getPositionVector())).down());
            }
        }

    }

    private void doFakeSwing() {
        if (((Boolean) this.fakeSwing.getValue()).booleanValue()) {
            EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND, AutoCrystal.mc.player);
        }

    }

    private void manualBreaker() {
        if (this.rotate.getValue() != AutoCrystal.Rotate.OFF && ((Integer) this.eventMode.getValue()).intValue() != 2 && this.rotating) {
            if (this.didRotation) {
                AutoCrystal.mc.player.rotationPitch = (float) ((double) AutoCrystal.mc.player.rotationPitch + 4.0E-4D);
                this.didRotation = false;
            } else {
                AutoCrystal.mc.player.rotationPitch = (float) ((double) AutoCrystal.mc.player.rotationPitch - 4.0E-4D);
                this.didRotation = true;
            }
        }

        if ((this.offHand || this.mainHand) && ((Boolean) this.manual.getValue()).booleanValue() && this.manualTimer.passedMs((long) ((Integer) this.manualBreak.getValue()).intValue()) && Mouse.isButtonDown(1) && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE) {
            RayTraceResult result = AutoCrystal.mc.objectMouseOver;

            if (AutoCrystal.mc.objectMouseOver != null) {
                switch (result.typeOfHit) {
                case ENTITY:
                    Entity mousePos1 = result.entityHit;

                    if (mousePos1 instanceof EntityEnderCrystal) {
                        EntityUtil.attackEntity(mousePos1, ((Boolean) this.sync.getValue()).booleanValue(), ((Boolean) this.breakSwing.getValue()).booleanValue());
                        this.manualTimer.reset();
                    }
                    break;

                case BLOCK:
                    BlockPos mousePos = AutoCrystal.mc.objectMouseOver.getBlockPos().up();
                    Iterator iterator = AutoCrystal.mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null, new AxisAlignedBB(mousePos)).iterator();

                    while (iterator.hasNext()) {
                        Entity target = (Entity) iterator.next();

                        if (target instanceof EntityEnderCrystal) {
                            EntityUtil.attackEntity(target, ((Boolean) this.sync.getValue()).booleanValue(), ((Boolean) this.breakSwing.getValue()).booleanValue());
                            this.manualTimer.reset();
                        }
                    }
                }
            }
        }

    }

    private void rotateTo(Entity entity) {
        switch ((AutoCrystal.Rotate) this.rotate.getValue()) {
        case OFF:
            this.rotating = false;

        case PLACE:
        default:
            break;

        case BREAK:
        case ALL:
            float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), entity.getPositionVector());

            if (((Integer) this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE) {
                Tater.rotationManager.setPlayerRotations(angle[0], angle[1]);
            } else {
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
            }
        }

    }

    private void rotateToPos(BlockPos pos) {
        switch ((AutoCrystal.Rotate) this.rotate.getValue()) {
        case OFF:
            this.rotating = false;
            break;

        case PLACE:
        case ALL:
            float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() - 0.5F), (double) ((float) pos.getZ() + 0.5F)));

            if (((Integer) this.eventMode.getValue()).intValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE) {
                Tater.rotationManager.setPlayerRotations(angle[0], angle[1]);
            } else {
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
            }

        case BREAK:
        }

    }

    private boolean isDoublePoppable(EntityPlayer player, float damage) {
        float health;

        if (((Boolean) this.doublePop.getValue()).booleanValue() && (double) (health = EntityUtil.getHealth(player)) <= ((Double) this.popHealth.getValue()).doubleValue() && (double) damage > (double) health + 0.5D && damage <= ((Float) this.popDamage.getValue()).floatValue()) {
            Timer timer = (Timer) this.totemPops.get(player);

            return timer == null || timer.passedMs((long) ((Integer) this.popTime.getValue()).intValue());
        } else {
            return false;
        }
    }

    private boolean isValid(Entity entity) {
        return entity != null && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square((double) ((Float) this.breakRange.getValue()).floatValue()) && (this.raytrace.getValue() == AutoCrystal.Raytrace.NONE || this.raytrace.getValue() == AutoCrystal.Raytrace.PLACE || AutoCrystal.mc.player.canEntityBeSeen(entity) || !AutoCrystal.mc.player.canEntityBeSeen(entity) && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square((double) ((Float) this.breaktrace.getValue()).floatValue()));
    }

    private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
        if (((Boolean) this.holySync.getValue()).booleanValue()) {
            BlockPos playerPos = new BlockPos(player.getPositionVector());
            EnumFacing[] aenumfacing = EnumFacing.values();
            int i = aenumfacing.length;

            for (int j = 0; j < i; ++j) {
                EnumFacing facing = aenumfacing[j];

                if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && pos.equals(playerPos.down().offset(facing))) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public Color getCurrentColor() {
        return new Color(((Integer) this.red.getValue()).intValue(), ((Integer) this.green.getValue()).intValue(), ((Integer) this.blue.getValue()).intValue(), ((Integer) this.alpha.getValue()).intValue());
    }

    private boolean lambda$new$115(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.doublePop.getValue()).booleanValue() && this.targetMode.getValue() == AutoCrystal.Target.DAMAGE;
    }

    private boolean lambda$new$114(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$113(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$112(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$111(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$110(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && ((Boolean) this.predictPos.getValue()).booleanValue();
    }

    private boolean lambda$new$109(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && ((Boolean) this.predictPos.getValue()).booleanValue();
    }

    private boolean lambda$new$108(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$107(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE && ((Boolean) this.syncThreadBool.getValue()).booleanValue();
    }

    private boolean lambda$new$106(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE;
    }

    private boolean lambda$new$105(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE;
    }

    private boolean lambda$new$104(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$103(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.rotate.getValue() != AutoCrystal.Rotate.OFF && ((Integer) this.eventMode.getValue()).intValue() == 2;
    }

    private boolean lambda$new$102(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$101(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$100(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$99(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$98(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$97(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$96(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && ((Boolean) this.syncedFeetPlace.getValue()).booleanValue();
    }

    private boolean lambda$new$95(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE;
    }

    private boolean lambda$new$94(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE;
    }

    private boolean lambda$new$93(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK;
    }

    private boolean lambda$new$92(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE;
    }

    private boolean lambda$new$91(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$90(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$89(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && ((Boolean) this.justRender.getValue()).booleanValue();
    }

    private boolean lambda$new$88(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$87(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && ((Boolean) this.placeSwing.getValue()).booleanValue();
    }

    private boolean lambda$new$86(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$85(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$84(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$83(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK) && ((Boolean) this.instant.getValue()).booleanValue();
    }

    private boolean lambda$new$82(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK) && this.targetMode.getValue() != AutoCrystal.Target.DAMAGE;
    }

    private boolean lambda$new$81(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK);
    }

    private boolean lambda$new$80(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$79(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$78(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$77(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$76(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$75(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$74(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$73(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && this.targetMode.getValue() != AutoCrystal.Target.DAMAGE;
    }

    private boolean lambda$new$72(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$71(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$70(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE;
    }

    private boolean lambda$new$69(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE;
    }

    private boolean lambda$new$68(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE && ((Boolean) this.offhandSwitch.getValue()).booleanValue();
    }

    private boolean lambda$new$67(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE;
    }

    private boolean lambda$new$66(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() == AutoCrystal.AutoSwitch.TOGGLE;
    }

    private boolean lambda$new$65(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$64(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$63(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$62(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$61(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$60(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$59(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$58(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$57(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC && ((Boolean) this.holdFacePlace.getValue()).booleanValue();
    }

    private boolean lambda$new$56(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$55(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$54(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$53(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$52(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.customOutline.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$51(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$50(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$49(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.outline.getValue()).booleanValue();
    }

    private boolean lambda$new$48(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$47(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue() && ((Boolean) this.box.getValue()).booleanValue();
    }

    private boolean lambda$new$46(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$45(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER;
    }

    private boolean lambda$new$44(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$43(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$42(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$41(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER && ((Boolean) this.render.getValue()).booleanValue();
    }

    private boolean lambda$new$40(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.RENDER;
    }

    private boolean lambda$new$39(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue();
    }

    private boolean lambda$new$38(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue();
    }

    private boolean lambda$new$37(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue();
    }

    private boolean lambda$new$36(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue() && this.instantTimer.getValue() == AutoCrystal.PredictTimer.PREDICT;
    }

    private boolean lambda$new$35(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue();
    }

    private boolean lambda$new$34(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.instant.getValue()).booleanValue();
    }

    private boolean lambda$new$33(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$32(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && (((Boolean) this.explode.getValue()).booleanValue() || ((Boolean) this.manual.getValue()).booleanValue());
    }

    private boolean lambda$new$31(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.manual.getValue()).booleanValue();
    }

    private boolean lambda$new$30(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.manual.getValue()).booleanValue();
    }

    private boolean lambda$new$29(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK;
    }

    private boolean lambda$new$28(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue() && this.raytrace.getValue() != AutoCrystal.Raytrace.NONE && this.raytrace.getValue() != AutoCrystal.Raytrace.PLACE;
    }

    private boolean lambda$new$27(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue();
    }

    private boolean lambda$new$26(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue();
    }

    private boolean lambda$new$25(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue();
    }

    private boolean lambda$new$24(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue();
    }

    private boolean lambda$new$23(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean) this.explode.getValue()).booleanValue();
    }

    private boolean lambda$new$22(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.BREAK;
    }

    private boolean lambda$new$21(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.doublePop.getValue()).booleanValue();
    }

    private boolean lambda$new$20(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.doublePop.getValue()).booleanValue();
    }

    private boolean lambda$new$19(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue() && ((Boolean) this.doublePop.getValue()).booleanValue();
    }

    private boolean lambda$new$18(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$17(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$16(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$15(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$14(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue() && this.raytrace.getValue() != AutoCrystal.Raytrace.NONE && this.raytrace.getValue() != AutoCrystal.Raytrace.BREAK;
    }

    private boolean lambda$new$13(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$12(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$11(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$10(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$9(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$8(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$7(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE && ((Boolean) this.place.getValue()).booleanValue();
    }

    private boolean lambda$new$6(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.PLACE;
    }

    private boolean lambda$new$5(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$4(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$3(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.MISC;
    }

    private boolean lambda$new$2(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$1(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    private boolean lambda$new$0(Object v) {
        return this.setting.getValue() == AutoCrystal.Settings.DEV;
    }

    public class switchTimer {

        private long time = -1L;

        public boolean passedS(double s) {
            return this.passedMs((long) s * 1000L);
        }

        public boolean passedDms(double dms) {
            return this.passedMs((long) dms * 10L);
        }

        public boolean passedDs(double ds) {
            return this.passedMs((long) ds * 100L);
        }

        public boolean passedMs(long ms) {
            return this.passedNS(this.convertToNS(ms));
        }

        public void setMs(long ms) {
            this.time = System.nanoTime() - this.convertToNS(ms);
        }

        public boolean passedNS(long ns) {
            return System.nanoTime() - this.time >= ns;
        }

        public long getPassedTimeMs() {
            return this.getMs(System.nanoTime() - this.time);
        }

        public AutoCrystal.switchTimer reset() {
            this.time = System.nanoTime();
            return this;
        }

        public long getMs(long time) {
            return time / 1000000L;
        }

        public long convertToNS(long time) {
            return time * 1000000L;
        }
    }

    private static class RAutoCrystal implements Runnable {

        private static AutoCrystal.RAutoCrystal instance;
        private AutoCrystal autoCrystal;

        public static AutoCrystal.RAutoCrystal getInstance(AutoCrystal autoCrystal) {
            if (AutoCrystal.RAutoCrystal.instance == null) {
                AutoCrystal.RAutoCrystal.instance = new AutoCrystal.RAutoCrystal();
                AutoCrystal.RAutoCrystal.instance.autoCrystal = autoCrystal;
            }

            return AutoCrystal.RAutoCrystal.instance;
        }

        public void run() {
            if (this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
                while (this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
                    while (true) {
                        if (!Tater.eventManager.ticksOngoing()) {
                            if (this.autoCrystal.shouldInterrupt.get()) {
                                this.autoCrystal.shouldInterrupt.set(false);
                                this.autoCrystal.syncroTimer.reset();
                                this.autoCrystal.thread.interrupt();
                                return;
                            }

                            this.autoCrystal.threadOngoing.set(true);
                            this.autoCrystal.doAutoCrystal();
                            this.autoCrystal.threadOngoing.set(false);

                            try {
                                Thread.sleep((long) ((Integer) this.autoCrystal.threadDelay.getValue()).intValue());
                            } catch (InterruptedException interruptedexception) {
                                this.autoCrystal.thread.interrupt();
                                interruptedexception.printStackTrace();
                            }
                        }
                    }
                }
            } else if (this.autoCrystal.threadMode.getValue() != AutoCrystal.ThreadMode.NONE && this.autoCrystal.isOn()) {
                while (true) {
                    if (!Tater.eventManager.ticksOngoing()) {
                        this.autoCrystal.threadOngoing.set(true);
                        this.autoCrystal.doAutoCrystal();
                        this.autoCrystal.threadOngoing.set(false);
                        break;
                    }
                }
            }

        }
    }

    public static class PlaceInfo {

        private final BlockPos pos;
        private final boolean offhand;
        private final boolean placeSwing;
        private final boolean exactHand;

        public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand) {
            this.pos = pos;
            this.offhand = offhand;
            this.placeSwing = placeSwing;
            this.exactHand = exactHand;
        }

        public void runPlace() {
            BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand);
        }
    }

    public static enum Settings {

        PLACE, BREAK, RENDER, MISC, DEV;
    }

    public static enum DamageSync {

        NONE, PLACE, BREAK;
    }

    public static enum Rotate {

        OFF, PLACE, BREAK, ALL;
    }

    public static enum Target {

        CLOSEST, UNSAFE, DAMAGE;
    }

    public static enum Logic {

        BREAKPLACE, PLACEBREAK;
    }

    public static enum Switch {

        ALWAYS, BREAKSLOT, CALC;
    }

    public static enum Raytrace {

        NONE, PLACE, BREAK, FULL;
    }

    public static enum AutoSwitch {

        NONE, TOGGLE, ALWAYS;
    }

    public static enum ThreadMode {

        NONE, POOL, SOUND, WHILE;
    }

    public static enum AntiFriendPop {

        NONE, PLACE, BREAK, ALL;
    }

    public static enum PredictTimer {

        NONE, BREAK, PREDICT;
    }
}
