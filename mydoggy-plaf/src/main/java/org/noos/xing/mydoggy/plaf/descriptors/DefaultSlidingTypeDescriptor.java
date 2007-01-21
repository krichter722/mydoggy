package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.SlidingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultSlidingTypeDescriptor implements SlidingTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {
    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    private EventListenerList listenerList;

    public DefaultSlidingTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.7f;
        transparentDelay = 1500;
    }

    public DefaultSlidingTypeDescriptor(DefaultSlidingTypeDescriptor parent, int transparentDelay, float transparentRatio, boolean useTransparentMode) {
        this.transparentDelay = transparentDelay;
        this.transparentRatio = transparentRatio;
        this.transparentMode = useTransparentMode;

        parent.addPropertyChangeListener(this);
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        if (transparentRatio < 0.0f || transparentRatio > 1.0f)
            throw new IllegalArgumentException("Invalid transparent ratio. Valid range is [0.0f, 1.0f]. [transparentRatio : " + transparentRatio + "]");

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        firePropertyChange("transparentRatio", old, transparentRatio);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (this.transparentMode == transparentMode)
            return;

        boolean old = this.transparentMode;
        this.transparentMode = transparentMode;

        firePropertyChange("transparentMode", old, transparentMode);
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChange("transparentDelay", old, transparentDelay);
    }


    public ToolWindowTypeDescriptor cloneMe() {
        return new DefaultSlidingTypeDescriptor(this, getTransparentDelay(), getTransparentRatio(), isTransparentMode());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("transparentMode".equals(evt.getPropertyName())) {
            setTransparentMode((Boolean) evt.getNewValue());
        } else if ("transparentRatio".equals(evt.getPropertyName())) {
            setTransparentRatio((Float) evt.getNewValue());
        } else if ("transparentDelay".equals(evt.getPropertyName())) {
            setTransparentDelay((Integer) evt.getNewValue());
        }
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listenerList == null)
            listenerList = new EventListenerList();
        listenerList.add(PropertyChangeListener.class, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return listenerList.getListeners(PropertyChangeListener.class);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listenerList != null) {
            PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

            PropertyChangeListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

}
