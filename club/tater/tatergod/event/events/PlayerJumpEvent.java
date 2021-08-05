package club.tater.tatergod.event.events;

import club.tater.tatergod.event.EventStage;

public class PlayerJumpEvent extends EventStage {

    public double motionX;
    public double motionY;

    public PlayerJumpEvent(double motionX, double motionY) {
        this.motionX = motionX;
        this.motionY = motionY;
    }
}
