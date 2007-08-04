package org.noos.xing.mydoggy.examples.mydoggyset.signal;

import java.util.EventListener;

public interface SignalListener<M> extends EventListener {

    void handleSignalEvent(String signal, SignalEvent<M> event);

}