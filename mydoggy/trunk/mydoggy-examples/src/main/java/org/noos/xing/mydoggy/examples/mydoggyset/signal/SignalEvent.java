package org.noos.xing.mydoggy.examples.mydoggyset.signal;

import java.util.EventObject;

public final class SignalEvent<M> extends EventObject {

    private M message = null;
    private boolean consumed;

    public SignalEvent(Object source) {
        super(source);
    }

    public SignalEvent(Object source, M message) {
        super(source);
        this.message = message;
    }

    public final M getMessage() {
        return message;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        this.consumed = true;
    }

    public String toString() {
        return "SignalEvent[source: " + source + ", message: " + message + ", consumed: " + consumed + "]";
    }

}