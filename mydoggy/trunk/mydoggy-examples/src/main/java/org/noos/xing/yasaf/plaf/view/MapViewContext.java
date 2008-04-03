package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.event.EventListenerList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MapViewContext implements ViewContext {
    protected Map<Object, Object> context;

    protected Map<Object, EventListenerList> listeners;

    public MapViewContext() {
        this.context = new HashMap<Object, Object>();
        this.listeners = new Hashtable<Object, EventListenerList>();
        listeners.put(MapViewContext.class, new EventListenerList());
    }

    public Object get(Object key) {
        return context.get(key);
    }

    public <T> T get(Class<T> key) {
        return (T) context.get(key);
    }

    public Object put(Object key, Object value) {
        Object old = context.put(key, value);
        firePutEvent(key, old, value);
        return old;
    }

    public void addViewContextChangeListener(Object key, ViewContextChangeListener listener) {
        EventListenerList listenerList = listeners.get(key);
        if (listenerList == null) {
            listenerList = new EventListenerList();
            listeners.put(key, listenerList);
        }
        listenerList.add(ViewContextChangeListener.class, listener);
    }

    public void addViewContextChangeListener(ViewContextChangeListener listener) {
        addViewContextChangeListener(MapViewContext.class, listener);
    }

    protected void firePutEvent(Object key, Object oldValue, Object newValue) {
        EventListenerList unrestricted = listeners.get(MapViewContext.class);
        EventListenerList restricted = listeners.get(key);

        ViewContextChangeEvent event = new ViewContextChangeEvent(this, this, key, oldValue, newValue);

        ViewContextChangeListener[] changeListeners = unrestricted.getListeners(ViewContextChangeListener.class);
        for (ViewContextChangeListener changeListener : changeListeners) {
            changeListener.contextChange(event);
        }

        if (restricted != null) {
            changeListeners = restricted.getListeners(ViewContextChangeListener.class);
            for (ViewContextChangeListener changeListener : changeListeners) {
                changeListener.contextChange(event);
            }
        }
    }
}
