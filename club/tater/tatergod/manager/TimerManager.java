package club.tater.tatergod.manager;

import club.tater.tatergod.features.Feature;

public class TimerManager extends Feature {

    private float timer = 1.0F;

    public void unload() {
        this.timer = 1.0F;
        TimerManager.mc.timer.tickLength = 50.0F;
    }

    public void update() {
        TimerManager.mc.timer.tickLength = 50.0F / (this.timer <= 0.0F ? 0.1F : this.timer);
    }

    public float getTimer() {
        return this.timer;
    }

    public void setTimer(float timer) {
        if (timer > 0.0F) {
            this.timer = timer;
        }

    }

    public void reset() {
        this.timer = 1.0F;
    }
}
