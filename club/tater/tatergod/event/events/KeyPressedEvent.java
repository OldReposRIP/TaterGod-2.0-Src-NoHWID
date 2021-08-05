package club.tater.tatergod.event.events;

import club.tater.tatergod.event.EventStage;

public class KeyPressedEvent extends EventStage {

    public boolean info;
    public boolean pressed;

    public KeyPressedEvent(boolean info, boolean pressed) {
        this.info = info;
        this.pressed = pressed;
    }
}
