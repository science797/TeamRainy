package developmentteam.teamrainy.api.events.impl;

import developmentteam.teamrainy.api.events.Event;

public class TimerEvent extends Event {
    private float timer;
    private boolean modified;
    public TimerEvent() {
        super(Stage.Pre);
        timer = 1f;
    }

    public float get() {
        return this.timer;
    }

    public void set(float timer) {
        this.modified = true;
        this.timer = timer;
    }

    public boolean isModified() {
        return this.modified;
    }
}
