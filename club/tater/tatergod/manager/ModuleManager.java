package club.tater.tatergod.manager;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.Render2DEvent;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.Feature;
import club.tater.tatergod.features.gui.Gui;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.modules.client.ClickGui;
import club.tater.tatergod.features.modules.client.FontMod;
import club.tater.tatergod.features.modules.client.GUIBlur;
import club.tater.tatergod.features.modules.client.HUD;
import club.tater.tatergod.features.modules.client.HudComponents;
import club.tater.tatergod.features.modules.client.NickHider;
import club.tater.tatergod.features.modules.combat.Auto32k;
import club.tater.tatergod.features.modules.combat.AutoArmor;
import club.tater.tatergod.features.modules.combat.AutoCrystal;
import club.tater.tatergod.features.modules.combat.AutoMinecart;
import club.tater.tatergod.features.modules.combat.AutoTrap;
import club.tater.tatergod.features.modules.combat.AutoWeb;
import club.tater.tatergod.features.modules.combat.BowSpam;
import club.tater.tatergod.features.modules.combat.Burrow;
import club.tater.tatergod.features.modules.combat.Criticals;
import club.tater.tatergod.features.modules.combat.GodModule;
import club.tater.tatergod.features.modules.combat.HoleFiller;
import club.tater.tatergod.features.modules.combat.Killaura;
import club.tater.tatergod.features.modules.combat.Offhand;
import club.tater.tatergod.features.modules.combat.OyVeyAutoCrystal;
import club.tater.tatergod.features.modules.combat.SelfWeb;
import club.tater.tatergod.features.modules.combat.Selftrap;
import club.tater.tatergod.features.modules.combat.Surround;
import club.tater.tatergod.features.modules.combat.WurstSurround;
import club.tater.tatergod.features.modules.misc.AutoGG;
import club.tater.tatergod.features.modules.misc.AutoGG2;
import club.tater.tatergod.features.modules.misc.BuildHeight;
import club.tater.tatergod.features.modules.misc.ChatModifier;
import club.tater.tatergod.features.modules.misc.ChatSuffix;
import club.tater.tatergod.features.modules.misc.ExtraTab;
import club.tater.tatergod.features.modules.misc.GhastNotifier;
import club.tater.tatergod.features.modules.misc.MCF;
import club.tater.tatergod.features.modules.misc.NoHandShake;
import club.tater.tatergod.features.modules.misc.NoHitBox;
import club.tater.tatergod.features.modules.misc.NoSoundLag;
import club.tater.tatergod.features.modules.misc.PearlNotify;
import club.tater.tatergod.features.modules.misc.PopCounter;
import club.tater.tatergod.features.modules.misc.RPC;
import club.tater.tatergod.features.modules.misc.Timestamps;
import club.tater.tatergod.features.modules.misc.ToolTips;
import club.tater.tatergod.features.modules.misc.Tracker;
import club.tater.tatergod.features.modules.movement.AntiVoid;
import club.tater.tatergod.features.modules.movement.Flight;
import club.tater.tatergod.features.modules.movement.IceSpeed;
import club.tater.tatergod.features.modules.movement.NoFall;
import club.tater.tatergod.features.modules.movement.NoSlowDown;
import club.tater.tatergod.features.modules.movement.PacketFly;
import club.tater.tatergod.features.modules.movement.ReverseStep;
import club.tater.tatergod.features.modules.movement.Scaffold;
import club.tater.tatergod.features.modules.movement.Speed;
import club.tater.tatergod.features.modules.movement.Sprint;
import club.tater.tatergod.features.modules.movement.Step;
import club.tater.tatergod.features.modules.movement.Velocity;
import club.tater.tatergod.features.modules.player.FakePlayer;
import club.tater.tatergod.features.modules.player.FastPlace;
import club.tater.tatergod.features.modules.player.Freecam;
import club.tater.tatergod.features.modules.player.LiquidInteract;
import club.tater.tatergod.features.modules.player.MCP;
import club.tater.tatergod.features.modules.player.MultiTask;
import club.tater.tatergod.features.modules.player.Replenish;
import club.tater.tatergod.features.modules.player.Speedmine;
import club.tater.tatergod.features.modules.player.TpsSync;
import club.tater.tatergod.features.modules.player.XCarry;
import club.tater.tatergod.features.modules.render.ArrowESP;
import club.tater.tatergod.features.modules.render.BlockHighlight;
import club.tater.tatergod.features.modules.render.Capes;
import club.tater.tatergod.features.modules.render.ESP;
import club.tater.tatergod.features.modules.render.GlintModify;
import club.tater.tatergod.features.modules.render.HandChams;
import club.tater.tatergod.features.modules.render.HitMarkers;
import club.tater.tatergod.features.modules.render.HoleESP;
import club.tater.tatergod.features.modules.render.LogoutSpots;
import club.tater.tatergod.features.modules.render.NameTags;
import club.tater.tatergod.features.modules.render.NoRender;
import club.tater.tatergod.features.modules.render.Shaders;
import club.tater.tatergod.features.modules.render.Skeleton;
import club.tater.tatergod.features.modules.render.SmallShield;
import club.tater.tatergod.features.modules.render.Swing;
import club.tater.tatergod.features.modules.render.TexturedChams;
import club.tater.tatergod.features.modules.render.Trajectories;
import club.tater.tatergod.features.modules.render.ViewModel;
import club.tater.tatergod.features.modules.render.Wireframe;
import club.tater.tatergod.util.Util;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

public class ModuleManager extends Feature {

    public static ArrayList nigger;
    public ArrayList modules = new ArrayList();
    public List sortedModules = new ArrayList();
    public List sortedModulesABC = new ArrayList();
    public ModuleManager.Animation animationThread;

    public static ArrayList getModules() {
        return ModuleManager.nigger;
    }

    public static boolean isModuleEnablednigger(String name) {
        Module modulenigger = (Module) getModules().stream().filter(test<invokedynamic>(name)).findFirst().orElse((Object) null);

        return modulenigger.isEnabled();
    }

    public static boolean isModuleEnablednigger(Module modulenigger) {
        return modulenigger.isEnabled();
    }

    public void init() {
        this.modules.add(new ClickGui());
        this.modules.add(new FontMod());
        this.modules.add(new GUIBlur());
        this.modules.add(new HUD());
        this.modules.add(new HudComponents());
        this.modules.add(new NickHider());
        this.modules.add(new Shaders());
        this.modules.add(new BlockHighlight());
        this.modules.add(new HoleESP());
        this.modules.add(new Skeleton());
        this.modules.add(new Wireframe());
        this.modules.add(new SmallShield());
        this.modules.add(new HandChams());
        this.modules.add(new Trajectories());
        this.modules.add(new Swing());
        this.modules.add(new ArrowESP());
        this.modules.add(new ESP());
        this.modules.add(new NameTags());
        this.modules.add(new TexturedChams());
        this.modules.add(new HitMarkers());
        this.modules.add(new Offhand());
        this.modules.add(new Surround());
        this.modules.add(new AutoTrap());
        this.modules.add(new GodModule());
        this.modules.add(new AutoWeb());
        this.modules.add(new AutoCrystal());
        this.modules.add(new OyVeyAutoCrystal());
        this.modules.add(new Killaura());
        this.modules.add(new Criticals());
        this.modules.add(new HoleFiller());
        this.modules.add(new AutoArmor());
        this.modules.add(new Selftrap());
        this.modules.add(new SelfWeb());
        this.modules.add(new Burrow());
        this.modules.add(new Freecam());
        this.modules.add(new FastPlace());
        this.modules.add(new TpsSync());
        this.modules.add(new Replenish());
        this.modules.add(new FakePlayer());
        this.modules.add(new MultiTask());
        this.modules.add(new MCP());
        this.modules.add(new LiquidInteract());
        this.modules.add(new Speedmine());
        this.modules.add(new ExtraTab());
        this.modules.add(new NoHitBox());
        this.modules.add(new Timestamps());
        this.modules.add(new NoSoundLag());
        this.modules.add(new NoHandShake());
        this.modules.add(new BuildHeight());
        this.modules.add(new ChatModifier());
        this.modules.add(new MCF());
        this.modules.add(new PearlNotify());
        this.modules.add(new AutoGG());
        this.modules.add(new ToolTips());
        this.modules.add(new RPC());
        this.modules.add(new Tracker());
        this.modules.add(new PopCounter());
        this.modules.add(new GhastNotifier());
        this.modules.add(new XCarry());
        this.modules.add(new PacketFly());
        this.modules.add(new Speed());
        this.modules.add(new Step());
        this.modules.add(new ReverseStep());
        this.modules.add(new AntiVoid());
        this.modules.add(new Flight());
        this.modules.add(new Scaffold());
        this.modules.add(new NoSlowDown());
        this.modules.add(new Velocity());
        this.modules.add(new IceSpeed());
        this.modules.add(new NoFall());
        this.modules.add(new Sprint());
        this.modules.add(new ViewModel());
        this.modules.add(new GlintModify());
        this.modules.add(new NoRender());
        this.modules.add(new BowSpam());
        this.modules.add(new LogoutSpots());
        this.modules.add(new ChatSuffix());
        this.modules.add(new AutoGG2());
        this.modules.add(new WurstSurround());
        this.modules.add(new AutoMinecart());
        this.modules.add(new Auto32k());
        this.modules.add(new Capes());
    }

    public Module getModuleByName(String name) {
        Iterator iterator = this.modules.iterator();

        Module module;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            module = (Module) iterator.next();
        } while (!module.getName().equalsIgnoreCase(name));

        return module;
    }

    public Module getModuleByClass(Class clazz) {
        Iterator iterator = this.modules.iterator();

        Module module;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            module = (Module) iterator.next();
        } while (!clazz.isInstance(module));

        return module;
    }

    public void enableModule(Class clazz) {
        Module module = this.getModuleByClass(clazz);

        if (module != null) {
            module.enable();
        }

    }

    public void disableModule(Class clazz) {
        Module module = this.getModuleByClass(clazz);

        if (module != null) {
            module.disable();
        }

    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);

        if (module != null) {
            module.enable();
        }

    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);

        if (module != null) {
            module.disable();
        }

    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);

        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class clazz) {
        Module module = this.getModuleByClass(clazz);

        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        Iterator iterator = this.modules.iterator();

        Module module;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            module = (Module) iterator.next();
        } while (!module.getDisplayName().equalsIgnoreCase(displayName));

        return module;
    }

    public ArrayList getEnabledModules() {
        ArrayList enabledModules = new ArrayList();
        Iterator iterator = this.modules.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }

        return enabledModules;
    }

    public ArrayList getEnabledModulesName() {
        ArrayList enabledModules = new ArrayList();
        Iterator iterator = this.modules.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.isEnabled() && module.isDrawn()) {
                enabledModules.add(module.getFullArrayString());
            }
        }

        return enabledModules;
    }

    public ArrayList getModulesByCategory(Module.Category category) {
        ArrayList modulesCategory = new ArrayList();

        this.modules.forEach(accept<invokedynamic>(category, modulesCategory));
        return modulesCategory;
    }

    public List getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        Stream stream = this.modules.stream().filter(test<invokedynamic>());
        EventBus eventbus = MinecraftForge.EVENT_BUS;

        MinecraftForge.EVENT_BUS.getClass();
        stream.forEach(accept<invokedynamic>(eventbus));
        this.modules.forEach(accept<invokedynamic>());
    }

    public void onUpdate() {
        this.modules.stream().filter(test<invokedynamic>()).forEach(accept<invokedynamic>());
    }

    public void onTick() {
        this.modules.stream().filter(test<invokedynamic>()).forEach(accept<invokedynamic>());
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(test<invokedynamic>()).forEach(accept<invokedynamic>(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(test<invokedynamic>()).forEach(accept<invokedynamic>(event));
    }

    public Module getModuleT(Class clazz) {
        return (Module) this.modules.stream().filter(test<invokedynamic>(clazz)).map(apply<invokedynamic>()).findFirst().orElse((Object) null);
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = (List) this.getEnabledModules().stream().filter(test<invokedynamic>()).sorted(Comparator.comparing(apply<invokedynamic>(this, reverse))).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.modules.forEach(accept<invokedynamic>());
    }

    public void onLogin() {
        this.modules.forEach(accept<invokedynamic>());
    }

    public void onUnload() {
        ArrayList arraylist = this.modules;
        EventBus eventbus = MinecraftForge.EVENT_BUS;

        MinecraftForge.EVENT_BUS.getClass();
        arraylist.forEach(accept<invokedynamic>(eventbus));
        this.modules.forEach(accept<invokedynamic>());
    }

    public void onUnloadPost() {
        Iterator iterator = this.modules.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            module.enabled.setValue(Boolean.valueOf(false));
        }

    }

    public void onKeyPressed(int eventKey) {
        if (eventKey != 0 && Keyboard.getEventKeyState() && !(ModuleManager.mc.currentScreen instanceof Gui)) {
            this.modules.forEach(accept<invokedynamic>(eventKey));
        }
    }

    private static void lambda$onKeyPressed$7(int eventKey, Module module) {
        if (module.getBind().getKey() == eventKey) {
            module.toggle();
        }

    }

    private Integer lambda$sortModules$6(boolean reverse, Module module) {
        return Integer.valueOf(this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1));
    }

    private static Module lambda$getModuleT$5(Module module) {
        return module;
    }

    private static boolean lambda$getModuleT$4(Class clazz, Module module) {
        return module.getClass() == clazz;
    }

    private static void lambda$onRender3D$3(Render3DEvent event, Module module) {
        module.onRender3D(event);
    }

    private static void lambda$onRender2D$2(Render2DEvent event, Module module) {
        module.onRender2D(event);
    }

    private static void lambda$getModulesByCategory$1(Module.Category category, ArrayList modulesCategory, Module module) {
        if (module.getCategory() == category) {
            modulesCategory.add(module);
        }

    }

    private static boolean lambda$isModuleEnablednigger$0(String name, Module mm) {
        return mm.getName().equalsIgnoreCase(name);
    }

    private class Animation extends Thread {

        public Module module;
        public float offset;
        public float vOffset;
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        public Animation() {
            super("Animation");
        }

        public void run() {
            Iterator iterator;

            if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
                iterator = ModuleManager.this.sortedModules.iterator();

                while (iterator.hasNext()) {
                    Module e = (Module) iterator.next();
                    String module = e.getDisplayName() + ChatFormatting.GRAY + (e.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + e.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");

                    e.offset = (float) ModuleManager.this.renderer.getStringWidth(module) / ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).floatValue();
                    e.vOffset = (float) ModuleManager.this.renderer.getFontHeight() / ((Integer) HUD.getInstance().animationVerticalTime.getValue()).floatValue();
                    if (e.isEnabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                        if (e.arrayListOffset > e.offset && Util.mc.world != null) {
                            e.arrayListOffset -= e.offset;
                            e.sliding = true;
                        }
                    } else if (e.isDisabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                        if (e.arrayListOffset < (float) ModuleManager.this.renderer.getStringWidth(module) && Util.mc.world != null) {
                            e.arrayListOffset += e.offset;
                            e.sliding = true;
                        } else {
                            e.sliding = false;
                        }
                    }
                }
            } else {
                iterator = ModuleManager.this.sortedModulesABC.iterator();

                while (iterator.hasNext()) {
                    String e1 = (String) iterator.next();
                    Module module1 = Tater.moduleManager.getModuleByName(e1);
                    String text = module1.getDisplayName() + ChatFormatting.GRAY + (module1.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module1.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");

                    module1.offset = (float) ModuleManager.this.renderer.getStringWidth(text) / ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).floatValue();
                    module1.vOffset = (float) ModuleManager.this.renderer.getFontHeight() / ((Integer) HUD.getInstance().animationVerticalTime.getValue()).floatValue();
                    if (module1.isEnabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                        if (module1.arrayListOffset > module1.offset && Util.mc.world != null) {
                            module1.arrayListOffset -= module1.offset;
                            module1.sliding = true;
                        }
                    } else if (module1.isDisabled() && ((Integer) HUD.getInstance().animationHorizontalTime.getValue()).intValue() != 1) {
                        if (module1.arrayListOffset < (float) ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                            module1.arrayListOffset += module1.offset;
                            module1.sliding = true;
                        } else {
                            module1.sliding = false;
                        }
                    }
                }
            }

        }

        public void start() {
            System.out.println("Starting animation thread.");
            this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
        }
    }
}
