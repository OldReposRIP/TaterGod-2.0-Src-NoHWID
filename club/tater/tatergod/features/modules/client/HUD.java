package club.tater.tatergod.features.modules.client;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.ClientEvent;
import club.tater.tatergod.event.events.Render2DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.ColorUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.MathUtil;
import club.tater.tatergod.util.RenderUtil;
import club.tater.tatergod.util.TextUtil;
import club.tater.tatergod.util.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {

    private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private static RenderItem itemRender;
    private static HUD INSTANCE = new HUD();
    private final Setting grayNess = this.register(new Setting("Gray", Boolean.valueOf(true)));
    private final Setting renderingUp = this.register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
    private final Setting waterMark = this.register(new Setting("Watermark", Boolean.valueOf(false), "displays watermark"));
    private final Setting arrayList = this.register(new Setting("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
    private final Setting pvp = this.register(new Setting("PvpInfo", Boolean.valueOf(false)));
    private final Setting coords = this.register(new Setting("Coords", Boolean.valueOf(false), "Your current coordinates"));
    private final Setting direction = this.register(new Setting("Direction", Boolean.valueOf(false), "The Direction you are facing."));
    private final Setting armor = this.register(new Setting("Armor", Boolean.valueOf(false), "ArmorHUD"));
    private final Setting totems = this.register(new Setting("Totems", Boolean.valueOf(false), "TotemHUD"));
    private final Setting greeter = this.register(new Setting("Welcomer", Boolean.valueOf(false), "The time"));
    private final Setting speed = this.register(new Setting("Speed", Boolean.valueOf(false), "Your Speed"));
    private final Setting potions = this.register(new Setting("Potions", Boolean.valueOf(false), "Your Speed"));
    private final Setting ping = this.register(new Setting("Ping", Boolean.valueOf(false), "Your response time to the server."));
    private final Setting tps = this.register(new Setting("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
    private final Setting fps = this.register(new Setting("FPS", Boolean.valueOf(false), "Your frames per second."));
    private final Setting lag = this.register(new Setting("LagNotifier", Boolean.valueOf(false), "The time"));
    private final Timer timer = new Timer();
    private final Map players = new HashMap();
    public Setting waterMarkY = this.register(new Setting("WatermarkPosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), test<invokedynamic>(this)));
    public Setting command = this.register(new Setting("Command", "TaterGod.CC"));
    public Setting bracketColor;
    public Setting commandColor;
    public Setting commandBracket;
    public Setting commandBracket2;
    public Setting notifyToggles;
    public Setting animationHorizontalTime;
    public Setting animationVerticalTime;
    public Setting renderingMode;
    public Setting time;
    public Setting lagTime;
    public float hue;
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;

    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
        this.bracketColor = this.register(new Setting("BracketColor", TextUtil.Color.RED));
        this.commandColor = this.register(new Setting("NameColor", TextUtil.Color.GRAY));
        this.commandBracket = this.register(new Setting("Bracket", "["));
        this.commandBracket2 = this.register(new Setting("Bracket2", "]"));
        this.notifyToggles = this.register(new Setting("ChatNotify", Boolean.valueOf(false), "notifys in chat"));
        this.animationHorizontalTime = this.register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), test<invokedynamic>(this)));
        this.animationVerticalTime = this.register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), test<invokedynamic>(this)));
        this.renderingMode = this.register(new Setting("Ordering", HUD.RenderingMode.ABC));
        this.time = this.register(new Setting("Time", Boolean.valueOf(false), "The time"));
        this.lagTime = this.register(new Setting("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000)));
        this.setInstance();
    }

    public static HUD getInstance() {
        if (HUD.INSTANCE == null) {
            HUD.INSTANCE = new HUD();
        }

        return HUD.INSTANCE;
    }

    private void setInstance() {
        HUD.INSTANCE = this;
    }

    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }

        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }

    }

    public void onRender2D(Render2DEvent event) {
        if (!fullNullCheck()) {
            int width = this.renderer.scaledWidth;
            int height = this.renderer.scaledHeight;

            this.color = ColorUtil.toRGBA(((Integer) ClickGui.getInstance().red.getValue()).intValue(), ((Integer) ClickGui.getInstance().green.getValue()).intValue(), ((Integer) ClickGui.getInstance().blue.getValue()).intValue());
            int posX;
            int posY;

            if (((Boolean) this.waterMark.getValue()).booleanValue()) {
                String counter1 = (String) this.command.getPlannedValue() + " v2.0";

                if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                    if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                        this.renderer.drawString(counter1, 2.0F, (float) ((Integer) this.waterMarkY.getValue()).intValue(), ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    } else {
                        int[] j = new int[] { 1};
                        char[] grayString = counter1.toCharArray();
                        float i = 0.0F;
                        char[] inHell = grayString;

                        posX = grayString.length;

                        for (posY = 0; posY < posX; ++posY) {
                            char posZ = inHell[posY];

                            this.renderer.drawString(String.valueOf(posZ), 2.0F + i, (float) ((Integer) this.waterMarkY.getValue()).intValue(), ColorUtil.rainbow(j[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                            i += (float) this.renderer.getStringWidth(String.valueOf(posZ));
                            ++j[0];
                        }
                    }
                } else {
                    this.renderer.drawString(counter1, 2.0F, (float) ((Integer) this.waterMarkY.getValue()).intValue(), this.color, true);
                }
            }

            int[] aint = new int[] { 1};
            int i = HUD.mc.currentScreen instanceof GuiChat && !((Boolean) this.renderingUp.getValue()).booleanValue() ? 14 : 0;
            String s;

            if (((Boolean) this.arrayList.getValue()).booleanValue()) {
                int j;
                String s1;
                Module module;

                if (((Boolean) this.renderingUp.getValue()).booleanValue()) {
                    if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
                        for (j = 0; j < Tater.moduleManager.sortedModulesABC.size(); ++j) {
                            s1 = (String) Tater.moduleManager.sortedModulesABC.get(j);
                            this.renderer.drawString(s1, (float) (width - 2 - this.renderer.getStringWidth(s1)), (float) (2 + i * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                            ++i;
                            ++aint[0];
                        }
                    } else {
                        for (j = 0; j < Tater.moduleManager.sortedModules.size(); ++j) {
                            module = (Module) Tater.moduleManager.sortedModules.get(j);
                            s = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                            this.renderer.drawString(s, (float) (width - 2 - this.renderer.getStringWidth(s)), (float) (2 + i * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                            ++i;
                            ++aint[0];
                        }
                    }
                } else if (this.renderingMode.getValue() == HUD.RenderingMode.ABC) {
                    for (j = 0; j < Tater.moduleManager.sortedModulesABC.size(); ++j) {
                        s1 = (String) Tater.moduleManager.sortedModulesABC.get(j);
                        i += 10;
                        this.renderer.drawString(s1, (float) (width - 2 - this.renderer.getStringWidth(s1)), (float) (height - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }
                } else {
                    for (j = 0; j < Tater.moduleManager.sortedModules.size(); ++j) {
                        module = (Module) Tater.moduleManager.sortedModules.get(j);
                        s = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        i += 10;
                        this.renderer.drawString(s, (float) (width - 2 - this.renderer.getStringWidth(s)), (float) (height - i), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }
                }
            }

            String s2 = ((Boolean) this.grayNess.getValue()).booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
            int k = HUD.mc.currentScreen instanceof GuiChat && ((Boolean) this.renderingUp.getValue()).booleanValue() ? 13 : (((Boolean) this.renderingUp.getValue()).booleanValue() ? -2 : 0);
            ArrayList arraylist;
            Iterator iterator;
            String s3;
            PotionEffect potioneffect;
            String s4;

            if (((Boolean) this.renderingUp.getValue()).booleanValue()) {
                if (((Boolean) this.potions.getValue()).booleanValue()) {
                    arraylist = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                    iterator = arraylist.iterator();

                    while (iterator.hasNext()) {
                        potioneffect = (PotionEffect) iterator.next();
                        s4 = Tater.potionManager.getColoredPotionString(potioneffect);
                        k += 10;
                        this.renderer.drawString(s4, (float) (width - this.renderer.getStringWidth(s4) - 2), (float) (height - 2 - k), potioneffect.getPotion().getLiquidColor(), true);
                    }
                }

                if (((Boolean) this.speed.getValue()).booleanValue()) {
                    s = s2 + "Speed " + ChatFormatting.WHITE + Tater.speedManager.getSpeedKpH() + " km/h";
                    k += 10;
                    this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++aint[0];
                }

                if (((Boolean) this.time.getValue()).booleanValue()) {
                    s = s2 + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                    k += 10;
                    this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++aint[0];
                }

                if (((Boolean) this.tps.getValue()).booleanValue()) {
                    s = s2 + "TPS " + ChatFormatting.WHITE + Tater.serverManager.getTPS();
                    k += 10;
                    this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++aint[0];
                }

                s = s2 + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
                s3 = s2 + "Ping " + ChatFormatting.WHITE + Tater.serverManager.getPing();
                if (this.renderer.getStringWidth(s3) > this.renderer.getStringWidth(s)) {
                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        k += 10;
                        this.renderer.drawString(s3, (float) (width - this.renderer.getStringWidth(s3) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }

                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        k += 10;
                        this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }
                } else {
                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        k += 10;
                        this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }

                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        k += 10;
                        this.renderer.drawString(s3, (float) (width - this.renderer.getStringWidth(s3) - 2), (float) (height - 2 - k), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }
                }
            } else {
                if (((Boolean) this.potions.getValue()).booleanValue()) {
                    arraylist = new ArrayList(Minecraft.getMinecraft().player.getActivePotionEffects());
                    iterator = arraylist.iterator();

                    while (iterator.hasNext()) {
                        potioneffect = (PotionEffect) iterator.next();
                        s4 = Tater.potionManager.getColoredPotionString(potioneffect);
                        this.renderer.drawString(s4, (float) (width - this.renderer.getStringWidth(s4) - 2), (float) (2 + k++ * 10), potioneffect.getPotion().getLiquidColor(), true);
                    }
                }

                if (((Boolean) this.speed.getValue()).booleanValue()) {
                    s = s2 + "Speed " + ChatFormatting.WHITE + Tater.speedManager.getSpeedKpH() + " km/h";
                    this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++aint[0];
                }

                if (((Boolean) this.time.getValue()).booleanValue()) {
                    s = s2 + "Time " + ChatFormatting.WHITE + (new SimpleDateFormat("h:mm a")).format(new Date());
                    this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++aint[0];
                }

                if (((Boolean) this.tps.getValue()).booleanValue()) {
                    s = s2 + "TPS " + ChatFormatting.WHITE + Tater.serverManager.getTPS();
                    this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                    ++aint[0];
                }

                s = s2 + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
                s3 = s2 + "Ping " + ChatFormatting.WHITE + Tater.serverManager.getPing();
                if (this.renderer.getStringWidth(s3) > this.renderer.getStringWidth(s)) {
                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        this.renderer.drawString(s3, (float) (width - this.renderer.getStringWidth(s3) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }

                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }
                } else {
                    if (((Boolean) this.fps.getValue()).booleanValue()) {
                        this.renderer.drawString(s, (float) (width - this.renderer.getStringWidth(s) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }

                    if (((Boolean) this.ping.getValue()).booleanValue()) {
                        this.renderer.drawString(s3, (float) (width - this.renderer.getStringWidth(s3) - 2), (float) (2 + k++ * 10), ((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(aint[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB() : ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB()) : this.color, true);
                        ++aint[0];
                    }
                }
            }

            boolean flag = HUD.mc.world.getBiome(HUD.mc.player.getPosition()).getBiomeName().equals("Hell");

            posX = (int) HUD.mc.player.posX;
            posY = (int) HUD.mc.player.posY;
            int l = (int) HUD.mc.player.posZ;
            float nether = !flag ? 0.125F : 8.0F;
            int hposX = (int) (HUD.mc.player.posX * (double) nether);
            int hposZ = (int) (HUD.mc.player.posZ * (double) nether);

            k = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0;
            String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (flag ? posX + ", " + posY + ", " + l + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET : posX + ", " + posY + ", " + l + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]");
            String direction = ((Boolean) this.direction.getValue()).booleanValue() ? Tater.rotationManager.getDirection4D(false) : "";
            String coords = ((Boolean) this.coords.getValue()).booleanValue() ? coordinates : "";

            k += 10;
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                String rainbowCoords = ((Boolean) this.coords.getValue()).booleanValue() ? "XYZ " + (flag ? posX + ", " + posY + ", " + l + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + l + " [" + hposX + ", " + hposZ + "]") : "";

                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(direction, 2.0F, (float) (height - k - 11), ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    this.renderer.drawString(rainbowCoords, 2.0F, (float) (height - k), ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    int[] counter2 = new int[] { 1};
                    char[] stringToCharArray = direction.toCharArray();
                    float s = 0.0F;
                    char[] counter3 = stringToCharArray;
                    int stringToCharArray2 = stringToCharArray.length;

                    for (int u = 0; u < stringToCharArray2; ++u) {
                        char c = counter3[u];

                        this.renderer.drawString(String.valueOf(c), 2.0F + s, (float) (height - k - 11), ColorUtil.rainbow(counter2[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        s += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++counter2[0];
                    }

                    int[] aint1 = new int[] { 1};
                    char[] achar = rainbowCoords.toCharArray();
                    float f = 0.0F;
                    char[] achar1 = achar;
                    int i1 = achar.length;

                    for (int j1 = 0; j1 < i1; ++j1) {
                        char c1 = achar1[j1];

                        this.renderer.drawString(String.valueOf(c1), 2.0F + f, (float) (height - k), ColorUtil.rainbow(aint1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c1));
                        ++aint1[0];
                    }
                }
            } else {
                this.renderer.drawString(direction, 2.0F, (float) (height - k - 11), this.color, true);
                this.renderer.drawString(coords, 2.0F, (float) (height - k), this.color, true);
            }

            if (((Boolean) this.armor.getValue()).booleanValue()) {
                this.renderArmorHUD(true);
            }

            if (((Boolean) this.totems.getValue()).booleanValue()) {
                this.renderTotemHUD();
            }

            if (((Boolean) this.greeter.getValue()).booleanValue()) {
                this.renderGreeter();
            }

            if (((Boolean) this.lag.getValue()).booleanValue()) {
                this.renderLag();
            }

        }
    }

    public Map getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "Welcome, ";

        if (((Boolean) this.greeter.getValue()).booleanValue()) {
            text = text + HUD.mc.player.getDisplayNameString();
        }

        if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
            } else {
                int[] counter1 = new int[] { 1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0F;
                char[] achar = stringToCharArray;
                int i = stringToCharArray.length;

                for (int j = 0; j < i; ++j) {
                    char c = achar[j];

                    this.renderer.drawString(String.valueOf(c), (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F + i, 2.0F, ColorUtil.rainbow(counter1[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                    i += (float) this.renderer.getStringWidth(String.valueOf(c));
                    ++counter1[0];
                }
            }
        } else {
            this.renderer.drawString(text, (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, this.color, true);
        }

    }

    public void renderLag() {
        int width = this.renderer.scaledWidth;

        if (Tater.serverManager.isServerNotResponding()) {
            String text = ChatFormatting.RED + "Server being chinese for " + MathUtil.round((float) Tater.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";

            this.renderer.drawString(text, (float) width / 2.0F - (float) this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, this.color, true);
        }

    }

    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = HUD.mc.player.inventory.mainInventory.stream().filter(test<invokedynamic>()).mapToInt(applyAsInt<invokedynamic>()).sum();

        if (HUD.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += HUD.mc.player.getHeldItemOffhand().getCount();
        }

        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            boolean iteration = false;
            int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 180 + 2;

            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, HUD.totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", (float) (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float) (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

    }

    public void renderArmorHUD(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;

        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        Iterator iterator = HUD.mc.player.inventory.armorInventory.iterator();

        while (iterator.hasNext()) {
            ItemStack is = (ItemStack) iterator.next();

            ++iteration;
            if (!is.isEmpty()) {
                int x = i - 90 + (9 - iteration) * 20 + 2;

                GlStateManager.enableDepth();
                RenderUtil.itemRender.zLevel = 200.0F;
                RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
                RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
                RenderUtil.itemRender.zLevel = 0.0F;
                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                String s = is.getCount() > 1 ? is.getCount() + "" : "";

                this.renderer.drawStringWithShadow(s, (float) (x + 19 - 2 - this.renderer.getStringWidth(s)), (float) (y + 9), 16777215);
                if (percent) {
                    boolean dmg = false;
                    int itemDurability = is.getMaxDamage() - is.getItemDamage();
                    float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                    float red = 1.0F - green;
                    int i;

                    if (percent) {
                        i = 100 - (int) (red * 100.0F);
                    } else {
                        i = itemDurability;
                    }

                    this.renderer.drawStringWithShadow(i + "", (float) (x + 8 - this.renderer.getStringWidth(i + "") / 2), (float) (y - 11), ColorUtil.toRGBA((int) (red * 255.0F), (int) (green * 255.0F), 0));
                }
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    public void renderPvpInfo() {
        String caOn = "CA:" + ChatFormatting.GREEN + " TRUE";
        String caOff = "CA:" + ChatFormatting.DARK_RED + " FALSE";
        String acOn = "AC:" + ChatFormatting.GREEN + " TRUE";
        String acOff = "AC:" + ChatFormatting.DARK_RED + " FALSE";
        String atOn = "AT:" + ChatFormatting.GREEN + " TRUE";
        String atOff = "AT:" + ChatFormatting.DARK_RED + " FALSE";
        String suOn = "SU:" + ChatFormatting.GREEN + " TRUE";
        String suOff = "SU:" + ChatFormatting.DARK_RED + " FALSE";
        String su2On = "SU2:" + ChatFormatting.GREEN + " TRUE";
        String su2Off = "SU2:" + ChatFormatting.DARK_RED + " FALSE";
        String hfOn = "HF:" + ChatFormatting.GREEN + " TRUE";
        String hfOff = "HF:" + ChatFormatting.DARK_RED + " FALSE";
        int[] arrayOfInt;
        char[] stringToCharArray;
        float f;
        char[] achar;
        int i;
        int j;
        char c;

        if (Tater.moduleManager.getModuleByName("CrystalAura").isEnabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(caOn, 2.0F, 10.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = caOn.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 10.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(caOn, 2.0F, 10.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("AutoCrystal").isEnabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(acOn, 2.0F, 20.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = acOn.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 20.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(acOn, 2.0F, 20.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("AutoTrap").isEnabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(atOn, 2.0F, 30.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = atOn.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 30.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(atOn, 2.0F, 30.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("Surround").isEnabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(suOn, 2.0F, 40.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = suOn.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 40.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(suOn, 2.0F, 40.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("Surround2").isEnabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(su2On, 2.0F, 50.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = su2On.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 50.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(su2On, 2.0F, 50.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("HoleFill").isEnabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(hfOn, 2.0F, 60.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = hfOn.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 60.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(hfOn, 2.0F, 60.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("CrystalAura").isDisabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(caOff, 2.0F, 10.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = caOff.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 10.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(caOff, 2.0F, 10.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("AutoCrystal").isDisabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(acOff, 2.0F, 20.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = acOff.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 20.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(acOff, 2.0F, 20.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("AutoTrap").isDisabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(atOff, 2.0F, 30.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = atOff.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 30.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(atOff, 2.0F, 30.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("Surround").isDisabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(suOff, 2.0F, 40.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = suOff.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 40.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(suOff, 2.0F, 40.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("Surround2").isDisabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(su2Off, 2.0F, 50.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = su2Off.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 50.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(su2Off, 2.0F, 50.0F, this.color, true);
            }
        }

        if (Tater.moduleManager.getModuleByName("HoleFill").isDisabled()) {
            if (((Boolean) ClickGui.getInstance().rainbow.getValue()).booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(hfOff, 2.0F, 60.0F, ColorUtil.rainbow(((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    arrayOfInt = new int[] { 1};
                    stringToCharArray = hfOff.toCharArray();
                    f = 0.0F;
                    achar = stringToCharArray;
                    i = stringToCharArray.length;

                    for (j = 0; j < i; ++j) {
                        c = achar[j];
                        this.renderer.drawString(String.valueOf(c), 2.0F + f, 60.0F, ColorUtil.rainbow(arrayOfInt[0] * ((Integer) ClickGui.getInstance().rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += (float) this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            } else {
                this.renderer.drawString(hfOff, 2.0F, 60.0F, this.color, true);
            }
        }

    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(AttackEntityEvent event) {
        this.shouldIncrement = true;
    }

    public void onLoad() {
        Tater.commandManager.setClientMessage(this.getCommandMessage());
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && this.equals(event.getSetting().getFeature())) {
            Tater.commandManager.setClientMessage(this.getCommandMessage());
        }

    }

    public String getCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());

        stringBuilder.insert(0, "§+");
        stringBuilder.append("§r");
        return stringBuilder.toString();
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());

        stringBuilder.insert(0, "§+");
        stringBuilder.append("§r");
        return stringBuilder.toString();
    }

    public String getRawCommandMessage() {
        return (String) this.commandBracket.getValue() + (String) this.command.getValue() + (String) this.commandBracket2.getValue();
    }

    public void drawTextRadar(int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;

            int textheight;

            for (Iterator iterator = this.players.entrySet().iterator(); iterator.hasNext(); y += textheight) {
                Entry player = (Entry) iterator.next();
                String text = (String) player.getKey() + " ";

                textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0F, (float) y, this.color, true);
            }
        }

    }

    private static boolean lambda$renderTotemHUD$3(ItemStack itemStack) {
        return itemStack.getItem() == Items.TOTEM_OF_UNDYING;
    }

    private boolean lambda$new$2(Object v) {
        return ((Boolean) this.arrayList.getValue()).booleanValue();
    }

    private boolean lambda$new$1(Object v) {
        return ((Boolean) this.arrayList.getValue()).booleanValue();
    }

    private boolean lambda$new$0(Object v) {
        return ((Boolean) this.waterMark.getValue()).booleanValue();
    }

    public static enum RenderingMode {

        Length, ABC;
    }
}
