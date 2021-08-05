package club.tater.tatergod.features.modules.render;

import club.tater.tatergod.features.modules.Module;
import club.tater.tatergod.features.setting.Setting;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoRender extends Module {

    private static NoRender INSTANCE = new NoRender();
    public Setting fire = this.register(new Setting("Fire", Boolean.valueOf(false), "Removes the portal overlay."));
    public Setting portal = this.register(new Setting("Portal", Boolean.valueOf(false), "Removes the portal overlay."));
    public Setting pumpkin = this.register(new Setting("Pumpkin", Boolean.valueOf(false), "Removes the pumpkin overlay."));
    public Setting totemPops = this.register(new Setting("TotemPop", Boolean.valueOf(false), "Removes the Totem overlay."));
    public Setting items = this.register(new Setting("Items", Boolean.valueOf(false), "Removes items on the ground."));
    public Setting nausea = this.register(new Setting("Nausea", Boolean.valueOf(false), "Removes Portal Nausea."));
    public Setting hurtcam = this.register(new Setting("HurtCam", Boolean.valueOf(false), "Removes shaking after taking damage."));
    public Setting fog;
    public Setting noWeather;
    public Setting boss;
    public Setting scale;
    public Setting bats;
    public Setting noArmor;
    public Setting skylight;
    public Setting barriers;
    public Setting blocks;

    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.fog = this.register(new Setting("Fog", NoRender.Fog.NONE, "Removes Fog."));
        this.noWeather = this.register(new Setting("Weather", Boolean.valueOf(false), "AntiWeather"));
        this.boss = this.register(new Setting("BossBars", NoRender.Boss.NONE, "Modifies the bossbars."));
        this.scale = this.register(new Setting("Scale", Float.valueOf(0.0F), Float.valueOf(0.5F), Float.valueOf(1.0F), test<invokedynamic>(this), "Scale of the bars."));
        this.bats = this.register(new Setting("Bats", Boolean.valueOf(false), "Removes bats."));
        this.noArmor = this.register(new Setting("NoArmor", NoRender.NoArmor.NONE, "Doesnt Render Armor on players."));
        this.skylight = this.register(new Setting("Skylight", NoRender.Skylight.NONE));
        this.barriers = this.register(new Setting("Barriers", Boolean.valueOf(false), "Barriers"));
        this.blocks = this.register(new Setting("Blocks", Boolean.valueOf(false), "Blocks"));
        this.setInstance();
    }

    public static NoRender getInstance() {
        if (NoRender.INSTANCE == null) {
            NoRender.INSTANCE = new NoRender();
        }

        return NoRender.INSTANCE;
    }

    private void setInstance() {
        NoRender.INSTANCE = this;
    }

    public void onUpdate() {
        if (((Boolean) this.items.getValue()).booleanValue()) {
            Stream stream = NoRender.mc.world.loadedEntityList.stream();

            EntityItem.class.getClass();
            stream = stream.filter(test<invokedynamic>(EntityItem.class));
            EntityItem.class.getClass();
            stream.map(apply<invokedynamic>(EntityItem.class)).forEach(accept<invokedynamic>());
        }

        if (((Boolean) this.noWeather.getValue()).booleanValue() && NoRender.mc.world.isRaining()) {
            NoRender.mc.world.setRainStrength(0.0F);
        }

    }

    public void doVoidFogParticles(int posX, int posY, int posZ) {
        boolean i = true;
        Random random = new Random();
        ItemStack itemstack = NoRender.mc.player.getHeldItemMainhand();
        boolean flag = !((Boolean) this.barriers.getValue()).booleanValue() || NoRender.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Item.getItemFromBlock(Blocks.BARRIER);
        MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

        for (int j = 0; j < 667; ++j) {
            this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
            this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
        }

    }

    public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, MutableBlockPos pos) {
        int i = x + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int j = y + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int k = z + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);

        pos.setPos(i, j, k);
        IBlockState iblockstate = NoRender.mc.world.getBlockState(pos);

        iblockstate.getBlock().randomDisplayTick(iblockstate, NoRender.mc.world, pos, random);
        if (!holdingBarrier && iblockstate.getBlock() == Blocks.BARRIER) {
            NoRender.mc.world.spawnParticle(EnumParticleTypes.BARRIER, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    @SubscribeEvent
    public void onRenderPre(Pre event) {
        if (event.getType() == ElementType.BOSSINFO && this.boss.getValue() != NoRender.Boss.NONE) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onRenderPost(Post event) {
        if (event.getType() == ElementType.BOSSINFO && this.boss.getValue() != NoRender.Boss.NONE) {
            Map map;
            int l;
            String text;

            if (this.boss.getValue() == NoRender.Boss.MINIMIZE) {
                map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;
                if (map == null) {
                    return;
                }

                ScaledResolution to = new ScaledResolution(NoRender.mc);
                int scaledresolution2 = to.getScaledWidth();

                l = 12;

                for (Iterator m = map.entrySet().iterator(); m.hasNext(); l += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT) {
                    Entry p = (Entry) m.next();
                    BossInfoClient entry3 = (BossInfoClient) p.getValue();

                    text = entry3.getName().getFormattedText();
                    int info2 = (int) ((float) scaledresolution2 / ((Float) this.scale.getValue()).floatValue() / 2.0F - 91.0F);

                    GL11.glScaled((double) ((Float) this.scale.getValue()).floatValue(), (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                    if (!event.isCanceled()) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        NoRender.mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                        NoRender.mc.ingameGUI.getBossOverlay().render(info2, l, entry3);
                        NoRender.mc.fontRenderer.drawStringWithShadow(text, (float) scaledresolution2 / ((Float) this.scale.getValue()).floatValue() / 2.0F - (float) (NoRender.mc.fontRenderer.getStringWidth(text) / 2), (float) (l - 9), 16777215);
                    }

                    GL11.glScaled(1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                }
            } else if (this.boss.getValue() == NoRender.Boss.STACK) {
                map = NoRender.mc.ingameGUI.getBossOverlay().mapBossInfos;
                HashMap to1 = new HashMap();
                Iterator scaledresolution21 = map.entrySet().iterator();

                while (scaledresolution21.hasNext()) {
                    Entry l1 = (Entry) scaledresolution21.next();
                    String m1 = ((BossInfoClient) l1.getValue()).getName().getFormattedText();
                    NoRender.Pair p1;

                    if (to1.containsKey(m1)) {
                        p1 = (NoRender.Pair) to1.get(m1);
                        p1 = new NoRender.Pair(p1.getKey(), Integer.valueOf(((Integer) p1.getValue()).intValue() + 1));
                        to1.put(m1, p1);
                    } else {
                        p1 = new NoRender.Pair(l1.getValue(), Integer.valueOf(1));
                        to1.put(m1, p1);
                    }
                }

                ScaledResolution scaledresolution22 = new ScaledResolution(NoRender.mc);

                l = scaledresolution22.getScaledWidth();
                int m2 = 12;

                for (Iterator p2 = to1.entrySet().iterator(); p2.hasNext(); m2 += 10 + NoRender.mc.fontRenderer.FONT_HEIGHT) {
                    Entry entry31 = (Entry) p2.next();

                    text = (String) entry31.getKey();
                    BossInfoClient info21 = (BossInfoClient) ((NoRender.Pair) entry31.getValue()).getKey();
                    int a = ((Integer) ((NoRender.Pair) entry31.getValue()).getValue()).intValue();

                    text = text + " x" + a;
                    int k2 = (int) ((float) l / ((Float) this.scale.getValue()).floatValue() / 2.0F - 91.0F);

                    GL11.glScaled((double) ((Float) this.scale.getValue()).floatValue(), (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                    if (!event.isCanceled()) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        NoRender.mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                        NoRender.mc.ingameGUI.getBossOverlay().render(k2, m2, info21);
                        NoRender.mc.fontRenderer.drawStringWithShadow(text, (float) l / ((Float) this.scale.getValue()).floatValue() / 2.0F - (float) (NoRender.mc.fontRenderer.getStringWidth(text) / 2), (float) (m2 - 9), 16777215);
                    }

                    GL11.glScaled(1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D / (double) ((Float) this.scale.getValue()).floatValue(), 1.0D);
                }
            }
        }

    }

    @SubscribeEvent
    public void onRenderLiving(net.minecraftforge.client.event.RenderLivingEvent.Pre event) {
        if (((Boolean) this.bats.getValue()).booleanValue() && event.getEntity() instanceof EntityBat) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public void onPlaySound(PlaySoundAtEntityEvent event) {
        if (((Boolean) this.bats.getValue()).booleanValue() && event.getSound().equals(SoundEvents.ENTITY_BAT_AMBIENT) || event.getSound().equals(SoundEvents.ENTITY_BAT_DEATH) || event.getSound().equals(SoundEvents.ENTITY_BAT_HURT) || event.getSound().equals(SoundEvents.ENTITY_BAT_LOOP) || event.getSound().equals(SoundEvents.ENTITY_BAT_TAKEOFF)) {
            event.setVolume(0.0F);
            event.setPitch(0.0F);
            event.setCanceled(true);
        }

    }

    private boolean lambda$new$0(Object v) {
        return this.boss.getValue() == NoRender.Boss.MINIMIZE || this.boss.getValue() != NoRender.Boss.STACK;
    }

    public static class Pair {

        private Object key;
        private Object value;

        public Pair(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return this.key;
        }

        public void setKey(Object key) {
            this.key = key;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public static enum NoArmor {

        NONE, ALL, HELMET;
    }

    public static enum Boss {

        NONE, REMOVE, STACK, MINIMIZE;
    }

    public static enum Fog {

        NONE, AIR, NOFOG;
    }

    public static enum Skylight {

        NONE, WORLD, ENTITY, ALL;
    }
}
