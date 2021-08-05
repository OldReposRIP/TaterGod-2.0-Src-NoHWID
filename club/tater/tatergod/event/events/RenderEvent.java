package club.tater.tatergod.event.events;

import club.tater.tatergod.event.EventStage;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.math.Vec3d;

public class RenderEvent extends EventStage {

    private final float partialTicks;
    private final Vec3d renderPos;
    private final Tessellator tessellator;

    public RenderEvent(Tessellator nameTessellator, Vec3d nameVec3d, float ticks) {
        this.tessellator = nameTessellator;
        this.renderPos = nameVec3d;
        this.partialTicks = ticks;
    }

    public void resetTranslation() {
        this.setTranslation(this.renderPos);
    }

    public Vec3d getRenderPos() {
        return this.renderPos;
    }

    public BufferBuilder getBuffer() {
        return this.tessellator.getBuffer();
    }

    public Tessellator getTessellator() {
        return this.tessellator;
    }

    public void setTranslation(Vec3d nameVec3d) {
        this.getBuffer().setTranslation(-nameVec3d.x, -nameVec3d.y, -nameVec3d.z);
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
}
