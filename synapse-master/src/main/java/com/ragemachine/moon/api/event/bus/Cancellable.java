package com.ragemachine.moon.api.event.bus;

public class Cancellable {
    private boolean cancelled = false;

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean way) {
        this.cancelled = way;
    }
}
