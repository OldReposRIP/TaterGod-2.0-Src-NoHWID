package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.Tater;
import club.tater.tatergod.event.events.Render3DEvent;
import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import club.tater.tatergod.util.ColorHolder;
import club.tater.tatergod.util.DamageUtil;
import club.tater.tatergod.util.EntityUtil;
import club.tater.tatergod.util.TextUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class NameTags extends Module {

    private static NameTags INSTANCE = new NameTags();
    private final Setting rect = this.register(new Setting("Rectangle", Boolean.valueOf(true)));
    private final Setting armor = this.register(new Setting("Armor", Boolean.valueOf(true)));
    private final Setting reversed = this.register(new Setting("ArmorReversed", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.armor.getValue()).booleanValue();
    }));
    private final Setting health = this.register(new Setting("Health", Boolean.valueOf(true)));
    private final Setting ping = this.register(new Setting("Ping", Boolean.valueOf(true)));
    private final Setting gamemode = this.register(new Setting("Gamemode", Boolean.valueOf(false)));
    private final Setting entityID = this.register(new Setting("EntityID", Boolean.valueOf(false)));
    private final Setting heldStackName = this.register(new Setting("StackName", Boolean.valueOf(true)));
    private final Setting max = this.register(new Setting("Max", Boolean.valueOf(true)));
    private final Setting maxText = this.register(new Setting("NoMaxText", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting Mred = this.register(new Setting("Max-Red", Integer.valueOf(178), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting Mgreen = this.register(new Setting("Max-Green", Integer.valueOf(52), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting Mblue = this.register(new Setting("Max-Blue", Integer.valueOf(57), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.max.getValue()).booleanValue();
    }));
    private final Setting size = this.register(new Setting("Size", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
    private final Setting scaleing = this.register(new Setting("Scale", Boolean.valueOf(false)));
    private final Setting smartScale = this.register(new Setting("SmartScale", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.scaleing.getValue()).booleanValue();
    }));
    private final Setting factor = this.register(new Setting("Factor", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(1.0F), (v) -> {
        return ((Boolean) this.scaleing.getValue()).booleanValue();
    }));
    private final Setting textcolor = this.register(new Setting("TextColor", Boolean.valueOf(true)));
    private final Setting NCRainbow = this.register(new Setting("Text-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting NCred = this.register(new Setting("Text-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting NCgreen = this.register(new Setting("Text-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting NCblue = this.register(new Setting("Text-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.textcolor.getValue()).booleanValue();
    }));
    private final Setting outline = this.register(new Setting("Outline", Boolean.valueOf(true)));
    private final Setting ORainbow = this.register(new Setting("Outline-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Owidth = this.register(new Setting("Outline-Width", Float.valueOf(1.3F), Float.valueOf(0.0F), Float.valueOf(5.0F), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Ored = this.register(new Setting("Outline-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Ogreen = this.register(new Setting("Outline-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting Oblue = this.register(new Setting("Outline-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue();
    }));
    private final Setting friendcolor = this.register(new Setting("FriendColor", Boolean.valueOf(true)));
    private final Setting FCRainbow = this.register(new Setting("Friend-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FCred = this.register(new Setting("Friend-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FCgreen = this.register(new Setting("Friend-Green", Integer.valueOf(213), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FCblue = this.register(new Setting("Friend-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FORainbow = this.register(new Setting("FriendOutline-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FOred = this.register(new Setting("FriendOutline-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FOgreen = this.register(new Setting("FriendOutline-Green", Integer.valueOf(213), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting FOblue = this.register(new Setting("FriendOutline-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.friendcolor.getValue()).booleanValue();
    }));
    private final Setting sneakcolor = this.register(new Setting("Sneak", Boolean.valueOf(false)));
    private final Setting sneak = this.register(new Setting("EnableSneak", Boolean.valueOf(true), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCRainbow = this.register(new Setting("Sneak-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCred = this.register(new Setting("Sneak-Red", Integer.valueOf(245), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCgreen = this.register(new Setting("Sneak-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SCblue = this.register(new Setting("Sneak-Blue", Integer.valueOf(122), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SORainbow = this.register(new Setting("SneakOutline-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SOred = this.register(new Setting("SneakOutline-Red", Integer.valueOf(245), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SOgreen = this.register(new Setting("SneakOutline-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting SOblue = this.register(new Setting("SneakOutline-Blue", Integer.valueOf(122), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.sneakcolor.getValue()).booleanValue();
    }));
    private final Setting invisiblescolor = this.register(new Setting("InvisiblesColor", Boolean.valueOf(false)));
    private final Setting invisibles = this.register(new Setting("EnableInvisibles", Boolean.valueOf(true), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICRainbow = this.register(new Setting("Invisible-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICred = this.register(new Setting("Invisible-Red", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICgreen = this.register(new Setting("Invisible-Green", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting ICblue = this.register(new Setting("Invisible-Blue", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IORainbow = this.register(new Setting("InvisibleOutline-Rainbow", Boolean.valueOf(false), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IOred = this.register(new Setting("InvisibleOutline-Red", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IOgreen = this.register(new Setting("InvisibleOutline-Green", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));
    private final Setting IOblue = this.register(new Setting("InvisibleOutline-Blue", Integer.valueOf(148), Integer.valueOf(0), Integer.valueOf(255), (v) -> {
        return ((Boolean) this.outline.getValue()).booleanValue() && ((Boolean) this.invisiblescolor.getValue()).booleanValue();
    }));

    public NameTags() {
        super("NameTags", "Renders info about the player on a NameTag", Module.Category.RENDER, false, false, false);
    }

    public static NameTags getInstance() {
        if (NameTags.INSTANCE == null) {
            NameTags.INSTANCE = new NameTags();
        }

        return NameTags.INSTANCE;
    }

    public void onRender3D(Render3DEvent event) {
        Iterator iterator = NameTags.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (player != null && !player.equals(NameTags.mc.player) && player.isEntityAlive() && (!player.isInvisible() || ((Boolean) this.invisibles.getValue()).booleanValue())) {
                double x = this.interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - NameTags.mc.getRenderManager().renderPosX;
                double y = this.interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - NameTags.mc.getRenderManager().renderPosY;
                double z = this.interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - NameTags.mc.getRenderManager().renderPosZ;

                this.renderNameTag(player, x, y, z, event.getPartialTicks());
            }
        }

    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y + (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = NameTags.mc.getRenderViewEntity();

        assert camera != null;

        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;

        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = this.getDisplayTag(player);
        double distance = camera.getDistance(x + NameTags.mc.getRenderManager().viewerPosX, y + NameTags.mc.getRenderManager().viewerPosY, z + NameTags.mc.getRenderManager().viewerPosZ);
        int width = this.renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018D + (double) ((Float) this.size.getValue()).floatValue() * distance * (double) ((Float) this.factor.getValue()).floatValue()) / 1000.0D;

        if (distance <= 8.0D && ((Boolean) this.smartScale.getValue()).booleanValue()) {
            scale = 0.0245D;
        }

        if (!((Boolean) this.scaleing.getValue()).booleanValue()) {
            scale = (double) ((Float) this.size.getValue()).floatValue() / 100.0D;
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        float f = NameTags.mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F;

        GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX, f, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (((Boolean) this.rect.getValue()).booleanValue()) {
            this.drawRect((float) (-width - 2), (float) (-(NameTags.mc.fontRenderer.FONT_HEIGHT + 1)), (float) width + 2.0F, 1.5F, 1426063360);
        }

        if (((Boolean) this.outline.getValue()).booleanValue()) {
            this.drawOutlineRect((float) (-width - 2), (float) (-(NameTags.mc.fontRenderer.FONT_HEIGHT + 1)), (float) width + 2.0F, 1.5F, this.getOutlineColor(player));
        }

        GlStateManager.disableBlend();
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();

        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }

        int count;

        if (((Boolean) this.heldStackName.getValue()).booleanValue() && !renderMainHand.isEmpty && renderMainHand.getItem() != Items.AIR) {
            String xOffset = renderMainHand.getDisplayName();

            count = this.renderer.getStringWidth(xOffset) / 2;
            GL11.glPushMatrix();
            GL11.glScalef(0.75F, 0.75F, 0.0F);
            this.renderer.drawStringWithShadow(xOffset, (float) (-count), -(this.getBiggestArmorTag(player) + 20.0F), -1);
            GL11.glScalef(1.5F, 1.5F, 1.0F);
            GL11.glPopMatrix();
        }

        if (((Boolean) this.armor.getValue()).booleanValue()) {
            GlStateManager.pushMatrix();
            int i = -6;

            count = 0;
            Iterator renderOffhand = player.inventory.armorInventory.iterator();

            while (renderOffhand.hasNext()) {
                ItemStack index = (ItemStack) renderOffhand.next();

                if (index != null) {
                    i -= 8;
                    if (index.getItem() != Items.AIR) {
                        ++count;
                    }
                }
            }

            i -= 8;
            ItemStack itemstack = player.getHeldItemOffhand().copy();

            if (itemstack.hasEffect() && (itemstack.getItem() instanceof ItemTool || itemstack.getItem() instanceof ItemArmor)) {
                itemstack.stackSize = 1;
            }

            this.renderItemStack(itemstack, i, -26);
            i += 16;
            ItemStack armourStack;
            ItemStack renderStack1;
            int j;

            if (((Boolean) this.reversed.getValue()).booleanValue()) {
                for (j = 0; j <= 3; ++j) {
                    armourStack = (ItemStack) player.inventory.armorInventory.get(j);
                    if (armourStack != null && armourStack.getItem() != Items.AIR) {
                        renderStack1 = armourStack.copy();
                        this.renderItemStack(armourStack, i, -26);
                        i += 16;
                    }
                }
            } else {
                for (j = 3; j >= 0; --j) {
                    armourStack = (ItemStack) player.inventory.armorInventory.get(j);
                    if (armourStack != null && armourStack.getItem() != Items.AIR) {
                        renderStack1 = armourStack.copy();
                        this.renderItemStack(armourStack, i, -26);
                        i += 16;
                    }
                }
            }

            this.renderItemStack(renderMainHand, i, -26);
            GlStateManager.popMatrix();
        }

        this.renderer.drawStringWithShadow(displayTag, (float) (-width), (float) (-(this.renderer.getFontHeight() - 1)), this.getDisplayColor(player));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
        GlStateManager.popMatrix();
    }

    private int getDisplayColor(EntityPlayer player) {
        int displaycolor = ColorHolder.toHex(((Integer) this.NCred.getValue()).intValue(), ((Integer) this.NCgreen.getValue()).intValue(), ((Integer) this.NCblue.getValue()).intValue());

        if (Tater.friendManager.isFriend(player)) {
            return ColorHolder.toHex(((Integer) this.FCred.getValue()).intValue(), ((Integer) this.FCgreen.getValue()).intValue(), ((Integer) this.FCblue.getValue()).intValue());
        } else {
            if (player.isInvisible() && ((Boolean) this.invisibles.getValue()).booleanValue()) {
                displaycolor = ColorHolder.toHex(((Integer) this.ICred.getValue()).intValue(), ((Integer) this.ICgreen.getValue()).intValue(), ((Integer) this.ICblue.getValue()).intValue());
            } else if (player.isSneaking() && ((Boolean) this.sneak.getValue()).booleanValue()) {
                displaycolor = ColorHolder.toHex(((Integer) this.SCred.getValue()).intValue(), ((Integer) this.SCgreen.getValue()).intValue(), ((Integer) this.SCblue.getValue()).intValue());
            }

            return displaycolor;
        }
    }

    private int getOutlineColor(EntityPlayer player) {
        int outlinecolor = ColorHolder.toHex(((Integer) this.Ored.getValue()).intValue(), ((Integer) this.Ogreen.getValue()).intValue(), ((Integer) this.Oblue.getValue()).intValue());

        if (Tater.friendManager.isFriend(player)) {
            outlinecolor = ColorHolder.toHex(((Integer) this.FOred.getValue()).intValue(), ((Integer) this.FOgreen.getValue()).intValue(), ((Integer) this.FOblue.getValue()).intValue());
        } else if (player.isInvisible() && ((Boolean) this.invisibles.getValue()).booleanValue()) {
            outlinecolor = ColorHolder.toHex(((Integer) this.IOred.getValue()).intValue(), ((Integer) this.IOgreen.getValue()).intValue(), ((Integer) this.IOblue.getValue()).intValue());
        } else if (player.isSneaking() && ((Boolean) this.sneak.getValue()).booleanValue()) {
            outlinecolor = ColorHolder.toHex(((Integer) this.SOred.getValue()).intValue(), ((Integer) this.SOgreen.getValue()).intValue(), ((Integer) this.SOblue.getValue()).intValue());
        }

        return outlinecolor;
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        NameTags.mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        NameTags.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        NameTags.mc.getRenderItem().renderItemOverlays(NameTags.mc.fontRenderer, stack, x, y);
        NameTags.mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        this.renderEnchantmentText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;

        if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            this.renderer.drawStringWithShadow("god", (float) (x * 2), (float) enchantmentY, -3977919);
            enchantmentY -= 8;
        }

        NBTTagList enchants = stack.getEnchantmentTagList();
        int percent;

        if (enchants.tagCount() > 2 && ((Boolean) this.max.getValue()).booleanValue()) {
            if (((Boolean) this.maxText.getValue()).booleanValue()) {
                this.renderer.drawStringWithShadow("", (float) (x * 2), (float) enchantmentY, ColorHolder.toHex(((Integer) this.Mred.getValue()).intValue(), ((Integer) this.Mgreen.getValue()).intValue(), ((Integer) this.Mblue.getValue()).intValue()));
                enchantmentY -= 8;
            } else {
                this.renderer.drawStringWithShadow("max", (float) (x * 2), (float) enchantmentY, ColorHolder.toHex(((Integer) this.Mred.getValue()).intValue(), ((Integer) this.Mgreen.getValue()).intValue(), ((Integer) this.Mblue.getValue()).intValue()));
                enchantmentY -= 8;
            }
        } else {
            for (percent = 0; percent < enchants.tagCount(); ++percent) {
                short color = enchants.getCompoundTagAt(percent).getShort("id");
                short level = enchants.getCompoundTagAt(percent).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(color);

                if (enc != null) {
                    String encName = enc.isCurse() ? TextFormatting.RED + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase() : enc.getTranslatedName(level).substring(0, 1).toLowerCase();

                    encName = encName + level;
                    this.renderer.drawStringWithShadow(encName, (float) (x * 2), (float) enchantmentY, -1);
                    enchantmentY -= 8;
                }
            }
        }

        if (DamageUtil.hasDurability(stack)) {
            percent = DamageUtil.getRoundedDamage(stack);
            String s;

            if (percent >= 60) {
                s = TextUtil.GREEN;
            } else if (percent >= 25) {
                s = TextUtil.YELLOW;
            } else {
                s = TextUtil.RED;
            }

            this.renderer.drawStringWithShadow(s + percent + "%", (float) (x * 2), (float) enchantmentY, -1);
        }

    }

    private float getBiggestArmorTag(EntityPlayer player) {
        float enchantmentY = 0.0F;
        boolean arm = false;
        Iterator renderMainHand = player.inventory.armorInventory.iterator();

        ItemStack renderOffHand;
        float encY;
        NBTTagList enchants;
        int index;
        short id;
        Enchantment enc;

        while (renderMainHand.hasNext()) {
            renderOffHand = (ItemStack) renderMainHand.next();
            encY = 0.0F;
            if (renderOffHand != null) {
                enchants = renderOffHand.getEnchantmentTagList();

                for (index = 0; index < enchants.tagCount(); ++index) {
                    id = enchants.getCompoundTagAt(index).getShort("id");
                    enc = Enchantment.getEnchantmentByID(id);
                    if (enc != null) {
                        encY += 8.0F;
                        arm = true;
                    }
                }
            }

            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }

        ItemStack itemstack = player.getHeldItemMainhand().copy();

        if (itemstack.hasEffect()) {
            float f = 0.0F;
            NBTTagList nbttaglist = itemstack.getEnchantmentTagList();

            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                short short0 = nbttaglist.getCompoundTagAt(i).getShort("id");
                Enchantment enchantment = Enchantment.getEnchantmentByID(short0);

                if (enchantment != null) {
                    f += 8.0F;
                    arm = true;
                }
            }

            if (f > enchantmentY) {
                enchantmentY = f;
            }
        }

        renderOffHand = player.getHeldItemOffhand().copy();
        if (renderOffHand.hasEffect()) {
            encY = 0.0F;
            enchants = renderOffHand.getEnchantmentTagList();

            for (index = 0; index < enchants.tagCount(); ++index) {
                id = enchants.getCompoundTagAt(index).getShort("id");
                enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    encY += 8.0F;
                    arm = true;
                }
            }

            if (encY > enchantmentY) {
                enchantmentY = encY;
            }
        }

        return (float) (arm ? 0 : 20) + enchantmentY;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();

        if (name.contains(NameTags.mc.getSession().getUsername())) {
            name = "You";
        }

        if (!((Boolean) this.health.getValue()).booleanValue()) {
            return name;
        } else {
            float health = EntityUtil.getHealth(player);
            String color;

            if (health > 18.0F) {
                color = TextUtil.GREEN;
            } else if (health > 16.0F) {
                color = TextUtil.DARK_GREEN;
            } else if (health > 12.0F) {
                color = TextUtil.YELLOW;
            } else if (health > 8.0F) {
                color = TextUtil.RED;
            } else if (health > 5.0F) {
                color = TextUtil.DARK_RED;
            } else {
                color = TextUtil.DARK_RED;
            }

            String pingStr = "";

            if (((Boolean) this.ping.getValue()).booleanValue()) {
                try {
                    int idString = ((NetHandlerPlayClient) Objects.requireNonNull(NameTags.mc.getConnection())).getPlayerInfo(player.getUniqueID()).getResponseTime();

                    pingStr = pingStr + idString + "ms ";
                } catch (Exception exception) {
                    ;
                }
            }

            String idString1 = "";

            if (((Boolean) this.entityID.getValue()).booleanValue()) {
                idString1 = idString1 + "ID: " + player.getEntityId() + " ";
            }

            String gameModeStr = "";

            if (((Boolean) this.gamemode.getValue()).booleanValue()) {
                if (player.isCreative()) {
                    gameModeStr = gameModeStr + "[C] ";
                } else if (!player.isSpectator() && !player.isInvisible()) {
                    gameModeStr = gameModeStr + "[S] ";
                } else {
                    gameModeStr = gameModeStr + "[I] ";
                }
            }

            if (Math.floor((double) health) == (double) health) {
                name = name + color + " " + (health > 0.0F ? Integer.valueOf((int) Math.floor((double) health)) : "dead");
            } else {
                name = name + color + " " + (health > 0.0F ? Integer.valueOf((int) health) : "dead");
            }

            return " " + pingStr + idString1 + gameModeStr + name + " ";
        }
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    public void drawOutlineRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(((Float) this.Owidth.getValue()).floatValue());
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.glLineWidth(((Float) this.Owidth.getValue()).floatValue());
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double) x, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) h, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) w, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos((double) x, (double) y, 0.0D).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void onUpdate() {
        if (((Boolean) this.outline.getValue()).equals(Boolean.valueOf(false))) {
            this.rect.setValue(Boolean.valueOf(true));
        } else if (((Boolean) this.rect.getValue()).equals(Boolean.valueOf(false))) {
            this.outline.setValue(Boolean.valueOf(true));
        }

        if (((Boolean) this.ORainbow.getValue()).booleanValue()) {
            this.OutlineRainbow();
        }

        if (((Boolean) this.NCRainbow.getValue()).booleanValue()) {
            this.TextRainbow();
        }

        if (((Boolean) this.FCRainbow.getValue()).booleanValue()) {
            this.FriendRainbow();
        }

        if (((Boolean) this.SCRainbow.getValue()).booleanValue()) {
            this.SneakColorRainbow();
        }

        if (((Boolean) this.ICRainbow.getValue()).booleanValue()) {
            this.InvisibleRainbow();
        }

        if (((Boolean) this.FORainbow.getValue()).booleanValue()) {
            this.FriendOutlineRainbow();
        }

        if (((Boolean) this.IORainbow.getValue()).booleanValue()) {
            this.InvisibleOutlineRainbow();
        }

        if (((Boolean) this.SORainbow.getValue()).booleanValue()) {
            this.SneakOutlineRainbow();
        }

    }

    public void OutlineRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.Ored.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.Ogreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.Oblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void TextRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.NCred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.NCgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.NCblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void FriendRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.FCred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.FCgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.FCblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void SneakColorRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.SCred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.SCgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.SCblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void InvisibleRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.ICred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.ICgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.ICblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void InvisibleOutlineRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.IOred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.IOgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.IOblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void FriendOutlineRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.FOred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.FOgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.FOblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }

    public void SneakOutlineRainbow() {
        float[] tick_color = new float[] { (float) (System.currentTimeMillis() % 11520L) / 11520.0F};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8F, 0.8F);

        this.SOred.setValue(Integer.valueOf(color_rgb_o >> 16 & 255));
        this.SOgreen.setValue(Integer.valueOf(color_rgb_o >> 8 & 255));
        this.SOblue.setValue(Integer.valueOf(color_rgb_o & 255));
    }
}
