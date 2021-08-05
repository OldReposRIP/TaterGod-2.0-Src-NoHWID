package club.tater.tatergod;

import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.gui.font.CustomFont;
import club.tater.tatergod.manager.ColorManager;
import club.tater.tatergod.manager.CommandManager;
import club.tater.tatergod.manager.ConfigManager;
import club.tater.tatergod.manager.EventManager;
import club.tater.tatergod.manager.FileManager;
import club.tater.tatergod.manager.FriendManager;
import club.tater.tatergod.manager.HoleManager;
import club.tater.tatergod.manager.InventoryManager;
import club.tater.tatergod.manager.ModuleManager;
import club.tater.tatergod.manager.PacketManager;
import club.tater.tatergod.manager.PositionManager;
import club.tater.tatergod.manager.PotionManager;
import club.tater.tatergod.manager.ReloadManager;
import club.tater.tatergod.manager.RotationManager;
import club.tater.tatergod.manager.ServerManager;
import club.tater.tatergod.manager.SpeedManager;
import club.tater.tatergod.manager.TextManager;
import club.tater.tatergod.manager.TimerManager;
import club.tater.tatergod.util.Enemy;
import club.tater.tatergod.util.IconUtil;
import club.tater.tatergod.util.Title;
import club.tater.tatergod.util.Wrapper;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.InputStream;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(
    modid = "tatergod",
    name = "TaterGod",
    version = "2.0"
)
public class Tater {

    public static final String MODID = "tatergod";
    public static final String MODNAME = "TaterGod";
    public static final String MODVER = "2.0";
    public static final Logger LOGGER = LogManager.getLogger("TaterGod");
    public static TimerManager timerManager;
    public static CommandManager commandManager;
    public static FriendManager friendManager;
    public static ModuleManager moduleManager;
    public static PacketManager packetManager;
    public static ColorManager colorManager;
    public static HoleManager holeManager;
    public static InventoryManager inventoryManager;
    public static PotionManager potionManager;
    public static RotationManager rotationManager;
    public static PositionManager positionManager;
    public static SpeedManager speedManager;
    public static ReloadManager reloadManager;
    public static FileManager fileManager;
    public static ConfigManager configManager;
    public static ServerManager serverManager;
    public static EventManager eventManager;
    public static TextManager textManager;
    public static CustomFont fontRenderer;
    public static Render3DEvent render3DEvent;
    public static Enemy enemy;
    @Instance
    public static Tater INSTANCE;
    private static boolean unloaded = false;

    public static void load() {
        Tater.LOGGER.info("Loading TaterGod v2.0");
        Tater.unloaded = false;
        if (Tater.reloadManager != null) {
            Tater.reloadManager.unload();
            Tater.reloadManager = null;
        }

        Tater.textManager = new TextManager();
        Tater.commandManager = new CommandManager();
        Tater.friendManager = new FriendManager();
        Tater.moduleManager = new ModuleManager();
        Tater.rotationManager = new RotationManager();
        Tater.packetManager = new PacketManager();
        Tater.eventManager = new EventManager();
        Tater.speedManager = new SpeedManager();
        Tater.potionManager = new PotionManager();
        Tater.inventoryManager = new InventoryManager();
        Tater.serverManager = new ServerManager();
        Tater.fileManager = new FileManager();
        Tater.colorManager = new ColorManager();
        Tater.positionManager = new PositionManager();
        Tater.configManager = new ConfigManager();
        Tater.holeManager = new HoleManager();
        Tater.LOGGER.info("Managers loaded.");
        Tater.moduleManager.init();
        Tater.LOGGER.info("Modules loaded.");
        Tater.configManager.init();
        Tater.eventManager.init();
        Tater.LOGGER.info("EventManager loaded.");
        Tater.textManager.init(true);
        Tater.moduleManager.onLoad();
        Tater.LOGGER.info("TaterGod v2.0 successfully loaded!\n");
    }

    public static void unload(boolean unload) {
        Tater.LOGGER.info("Unloading TaterGod v2.0");
        if (unload) {
            Tater.reloadManager = new ReloadManager();
            Tater.reloadManager.init(Tater.commandManager != null ? Tater.commandManager.getPrefix() : ".");
        }

        onUnload();
        Tater.eventManager = null;
        Tater.friendManager = null;
        Tater.speedManager = null;
        Tater.holeManager = null;
        Tater.positionManager = null;
        Tater.rotationManager = null;
        Tater.configManager = null;
        Tater.commandManager = null;
        Tater.colorManager = null;
        Tater.serverManager = null;
        Tater.fileManager = null;
        Tater.potionManager = null;
        Tater.inventoryManager = null;
        Tater.moduleManager = null;
        Tater.textManager = null;
        Tater.LOGGER.info("TaterGod v2.0 unloaded!\n");
        Tater.LOGGER.info("rpai was here 5/7/2021!\n");
    }

    public static void reload() {
        unload(false);
        load();
    }

    public static void load_client() {}

    public static String starting_client() {
        return "cope es newfag (editado)";
    }

    public static void copyToClipboard() {
        StringSelection selection = new StringSelection(Wrapper.getBlock());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        clipboard.setContents(selection, selection);
    }

    public static void onUnload() {
        if (!Tater.unloaded) {
            Tater.eventManager.onUnload();
            Tater.moduleManager.onUnload();
            Tater.configManager.saveConfig(Tater.configManager.config.replaceFirst("tater/", ""));
            Tater.moduleManager.onUnloadPost();
            Tater.unloaded = true;
        }

    }

    public static void setWindowIcon() {
        if (Util.getOSType() != EnumOS.OSX) {
            try {
                InputStream e = Minecraft.class.getResourceAsStream("/assets/tatergod/icons/tatergod-x16.png");
                Throwable throwable = null;

                try {
                    InputStream inputStream32x = Minecraft.class.getResourceAsStream("/assets/tatergod/icons/tatergod-x32.png");
                    Throwable throwable1 = null;

                    try {
                        ByteBuffer[] icons = new ByteBuffer[] { IconUtil.INSTANCE.readImageToBuffer(e), IconUtil.INSTANCE.readImageToBuffer(inputStream32x)};

                        Display.setIcon(icons);
                    } catch (Throwable throwable2) {
                        throwable1 = throwable2;
                        throw throwable2;
                    } finally {
                        if (inputStream32x != null) {
                            if (throwable1 != null) {
                                try {
                                    inputStream32x.close();
                                } catch (Throwable throwable3) {
                                    throwable1.addSuppressed(throwable3);
                                }
                            } else {
                                inputStream32x.close();
                            }
                        }

                    }
                } catch (Throwable throwable4) {
                    throwable = throwable4;
                    throw throwable4;
                } finally {
                    if (e != null) {
                        if (throwable != null) {
                            try {
                                e.close();
                            } catch (Throwable throwable5) {
                                throwable.addSuppressed(throwable5);
                            }
                        } else {
                            e.close();
                        }
                    }

                }
            } catch (Exception exception) {
                Tater.LOGGER.error("Couldn\'t set Windows Icon", exception);
            }
        }

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Tater.LOGGER.info("I am going to have sex with you -perry");
    }

    private void setWindowsIcon() {
        setWindowIcon();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Title());
        load();
        this.setWindowsIcon();
    }

    @EventHandler
    public void Starting(FMLInitializationEvent event) {}
}
