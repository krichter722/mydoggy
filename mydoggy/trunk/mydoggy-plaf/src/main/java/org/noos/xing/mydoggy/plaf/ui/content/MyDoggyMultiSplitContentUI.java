package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.MultiSplitContentUI;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class MyDoggyMultiSplitContentUI implements MultiSplitContentUI {
    protected Content content;
    protected boolean closable;
    protected boolean detachable;
    protected boolean transparentMode;
    protected float transparentRatio;
    protected int transparentDelay;

    protected EventListenerList listenerList;

    public MyDoggyMultiSplitContentUI(Content content) {
        this.content = content;
        this.listenerList = new EventListenerList();
        this.closable = this.detachable = true;
        this.transparentMode = true;
        this.transparentRatio = 0.7f;
        this.transparentDelay = 0;
    }

    public Content getContent() {
        return content;
    }

    public boolean isCloseable() {
        return closable;
    }

    public void setCloseable(boolean closable) {
        if (this.closable == closable)
            return;

        boolean old = this.closable;
        this.closable = closable;

        fireEvent("closable", old, closable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        if (this.detachable == detachable)
            return;

        boolean old = this.detachable;
        this.detachable = detachable;

        fireEvent("detachable", old, detachable);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (this.transparentMode == transparentMode)
            return;

        boolean old = this.transparentMode;
        this.transparentMode = transparentMode;

        fireEvent("transparentMode", old, transparentMode);
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        fireEvent("transparentRatio", old, transparentRatio);
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        fireEvent("transparentDelay", old, transparentDelay);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return listenerList.getListeners(PropertyChangeListener.class);
    }


    protected void fireEvent(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
        for (PropertyChangeListener listener : getPropertyChangeListeners()) {
            listener.propertyChange(event);
        }
    }
}