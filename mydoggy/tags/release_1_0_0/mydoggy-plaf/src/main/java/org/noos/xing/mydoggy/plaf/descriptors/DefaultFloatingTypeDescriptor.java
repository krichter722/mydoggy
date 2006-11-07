package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;

import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultFloatingTypeDescriptor implements FloatingTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {
    private Point location;
    private Dimension size;

    private boolean modal;

    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    private EventListenerList listenerList;

    public DefaultFloatingTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.7f;
        transparentDelay = 1500;
        modal = false;
    }

    public DefaultFloatingTypeDescriptor(DefaultFloatingTypeDescriptor parent, Point location, Dimension size, int transparentDelay, float transparentRatio, boolean useTransparentMode, boolean modal) {
        this.location = location;
        this.size = size;
        this.transparentDelay = transparentDelay;
        this.transparentRatio = transparentRatio;
        this.transparentMode = useTransparentMode;
        this.modal = modal;

        parent.addPropertyChangeListener(this);
    }

    public void setLocation(int x, int y) {
        Point newLocation = new Point(x, y);
        if (location != null && location.equals(newLocation))
            return;

        Point old = this.location;
        this.location = newLocation;

        firePropertyChange("location", old, location);
    }

    public void setSize(int width, int height) {
        Dimension newSize = new Dimension(width, height);
        if (size != null && size.equals(newSize))
            return;

        Dimension old = this.size;
        this.size = newSize;

        firePropertyChange("location", old, size);
    }

    public Point getLocation() {
        return location;
    }

    public Dimension getSize() {
        return size;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        if (this.modal == modal)
            return;

        boolean old = this.modal;
        this.modal = modal;

        firePropertyChange("modal", old, modal);
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
        return new DefaultFloatingTypeDescriptor(this, getLocation(), getSize(), getTransparentDelay(), getTransparentRatio(), isTransparentMode(), modal);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("location".equals(evt.getPropertyName())) {
            Point p = (Point) evt.getNewValue();
            setLocation(p.x, p.y);
        } else if ("size".equals(evt.getPropertyName())) {
            Dimension d = (Dimension) evt.getNewValue();
            setSize(d.width, d.height);
        } else if ("modal".equals(evt.getPropertyName())) {
            setModal((Boolean) evt.getNewValue());
        } else if ("transparentMode".equals(evt.getPropertyName())) {
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
