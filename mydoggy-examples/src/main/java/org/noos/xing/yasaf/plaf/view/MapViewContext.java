package org.noos.xing.yasaf.plaf.view;

import org.noos.xing.yasaf.view.ViewContext;
import org.noos.xing.yasaf.view.ViewContextChangeListener;
import org.noos.xing.yasaf.view.event.ViewContextChangeEvent;

import javax.swing.event.EventListenerList;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MapViewContext implements ViewContext {
    protected Map<Object, Object> context;
    protected EventListenerList listeners;

    public MapViewContext() {
        this.context = new Hashtable<Object, Object>();
        this.listeners = new EventListenerList();
    }

    public Object get(Object key) {
        return context.get(key);
    }

    public <T> T get(Class<T> a) {
        return (T) context.get(a);
    }

    public Object put(Object key, Object value) {
        Object old = context.put(key, value);
        firePutEvent(key, old, value);
        return old;
    }

    public void addViewContextChangeListener(ViewContextChangeListener listener) {
        listeners.add(ViewContextChangeListener.class, listener);
    }

    protected void firePutEvent(Object key, Object oldValue, Object newValue) {
        ViewContextChangeListener[] changeListeners = listeners.getListeners(ViewContextChangeListener.class);
        ViewContextChangeEvent event = new ViewContextChangeEvent(this, key, oldValue, newValue);
        for (ViewContextChangeListener changeListener : changeListeners) {
            changeListener.contextChange(event);
        }
    }
}
