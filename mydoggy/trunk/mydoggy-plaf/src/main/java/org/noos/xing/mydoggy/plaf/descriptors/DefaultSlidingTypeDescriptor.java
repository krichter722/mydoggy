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
    private boolean enabled;

    private EventListenerList listenerList;

    public DefaultSlidingTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.5f;
        transparentDelay = 1000;
        enabled = true;
    }

    public DefaultSlidingTypeDescriptor(DefaultSlidingTypeDescriptor parent, int transparentDelay, float transparentRatio, boolean transparentMode, boolean enabled) {
        this.transparentDelay = transparentDelay;
        this.transparentRatio = transparentRatio;
        this.transparentMode = transparentMode;
        this.enabled = enabled;

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
             
    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;

        boolean old = this.enabled;
        this.enabled = enabled;

        firePropertyChange("enabled", old, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChange("transparentDelay", old, transparentDelay);
    }


    public ToolWindowTypeDescriptor cloneMe() {
        return new DefaultSlidingTypeDescriptor(this,
                                                getTransparentDelay(), getTransparentRatio(),
                                                isTransparentMode(), isEnabled());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("transparentMode".equals(evt.getPropertyName())) {
            setTransparentMode((Boolean) evt.getNewValue());
        } else if ("transparentRatio".equals(evt.getPropertyName())) {
            setTransparentRatio((Float) evt.getNewValue());
        } else if ("transparentDelay".equals(evt.getPropertyName())) {
            setTransparentDelay((Integer) evt.getNewValue());
        } else if ("enabled".equals(evt.getPropertyName())) {
            setEnabled((Boolean) evt.getNewValue());
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
