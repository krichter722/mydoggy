package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro
 */
public class MyDoggyToolWindow implements ToolWindow {
    static final Object LOCK = new ToolWindowLock();

    static class ToolWindowLock {
    }

    private String id;
    private int index;
    private ToolWindowAnchor anchor;
    private ToolWindowType type;
    private String title;
    private Icon icon;

    private boolean autoHide;
    private boolean available;
    private boolean visible;
    private boolean active;
    private boolean flash;
    private boolean maximized;

    private ResourceBundle resourceBoundle;
    private ToolWindowDescriptor descriptor;

    private EventListenerList internalListenerList;
    private EventListenerList listenerList;

    private int lastAnchorIndex = -1;

    private boolean publicEvent = true;

    MyDoggyToolWindow(MyDoggyToolWindowManager manager, Window anchestor, String id, int index,
                      ToolWindowAnchor anchor, ToolWindowType type,
                      String title, Icon icon, Component component,
                      ResourceBundle resourceBundle) {
        this.internalListenerList = new EventListenerList();
        this.listenerList = new EventListenerList();

        this.descriptor = new ToolWindowDescriptor(manager, this, anchestor, component);
        this.resourceBoundle = resourceBundle;

        this.id = id;
        this.index = index;
        this.anchor = anchor;
        this.type = type;
        setTitle(title);
        this.icon = icon;
        this.available = this.active = this.visible = this.maximized = false;
    }


    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (index != -1 && index <= 0 && index > 9)
            throw new IllegalArgumentException("Invalid index. Valid index range is [-1, 1-9]. [tool : " + getId() + ", index : " + index + "]");

        synchronized (getLock()) {
            if (index == this.index)
                return;

            int old = this.index;
            this.index = index;

            firePropertyChangeEvent("index", old, index);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        if (this.available == available)
            return;

        synchronized (getLock()) {
            if (!available) {
                if (isActive() && publicEvent)
                    setActive(false);
                if (isVisible())
                    setVisible(false);
            }

            boolean old = this.available;
            this.available = available;

            firePropertyChangeEvent("available", old, available);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void aggregate() {
        try {
            descriptor.getManager().enableShiftShow();
            if (!isVisible()) {
                if (getType() == ToolWindowType.SLIDING)
                    setType(ToolWindowType.DOCKED);
                
                setVisible(true);
            }
        } finally {
            descriptor.getManager().resetShiftShow();
        }
    }

    public boolean isFlashing() {
        return flash;
    }

    public void setFlashing(boolean flash) {
        if (flash && isVisible())
            return;

        if (this.flash == flash)
            return;

        synchronized (getLock()) {

            boolean old = this.flash;
            this.flash = flash;

            firePropertyChangeEvent("flash", old, flash);
        }
    }

    public void setFlashing(int duration) {
        if (isVisible())
            return;

        if (this.flash)
            return;

        synchronized (getLock()) {

            boolean old = this.flash;
            this.flash = true;

            firePropertyChangeEvent("flash.duration", null, duration);
        }
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible)
            return;

        synchronized (getLock()) {
            if (visible)
                setAvailable(visible);
            else if (active && publicEvent)
                setActive(false);

            boolean old = this.visible;
            this.visible = visible;

            firePropertyChangeEvent("visible", old, visible);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active == active && publicEvent)
            return;

        synchronized (getLock()) {
            if (active) {
                setAvailable(active);
                setVisible(active);
            }

            boolean old = this.active;
            this.active = active;

            firePropertyChangeEvent("active", old, active);
        }
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(ToolWindowAnchor anchor) {
        synchronized (getLock()) {
            if (this.anchor == anchor && anchor.getIndex() == lastAnchorIndex)
                return;

            if (getType() == ToolWindowType.DOCKED || getType() == ToolWindowType.SLIDING) {
                boolean tempVisible = isVisible();
                boolean tempActive = isActive();

                publicEvent = false;

                ToolWindowAnchor oldAnchor;
                try {
                    setAvailable(false);

                    oldAnchor = this.anchor;
                    this.anchor = anchor;
                    this.lastAnchorIndex = anchor.getIndex();

                    setAvailable(true);
                    if (tempActive)
                        setActive(true);
                    else if (tempVisible)
                        setVisible(true);
                } finally {
                    publicEvent = true;
                }

                fireAnchorEvent(oldAnchor, anchor);
            } else {
                ToolWindowAnchor oldAnchor = this.anchor;
                this.anchor = anchor;
                this.lastAnchorIndex = anchor.getIndex();

                fireAnchorEvent(oldAnchor, anchor);
            }
        }
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public void setAutoHide(boolean autoHide) {
        if (this.autoHide == autoHide)
            return;

        synchronized (getLock()) {
            boolean old = this.autoHide;
            this.autoHide = autoHide;
            firePropertyChangeEvent("autoHide", old, autoHide);
        }
    }

    public ToolWindowType getType() {
        return type;
    }

    public void setType(ToolWindowType type) {
        synchronized (getLock()) {
            if (this.type == type)
                return;

            boolean tempVisible = isVisible();
            boolean tempActive = isActive();

            publicEvent = false;

            ToolWindowType oldType;
            try {
                setVisible(false);
                if (tempActive)
                    active = false;

                publicEvent = true;

                oldType = this.type;
                this.type = type;

                if (tempActive) {
                    setActive(true);
                } else if (tempVisible)
                    setVisible(true);
            } finally {
                publicEvent = true;
            }

            fireTypeEvent(oldType, type);
        }
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        synchronized (getLock()) {
            if (this.icon == icon)
                return;

            Icon old = this.icon;
            this.icon = icon;

            fireIconEvent(old, icon);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        synchronized (getLock()) {
            String newTitle = (resourceBoundle != null) ? resourceBoundle.getString(title) : title;
            if (newTitle != null && newTitle.equals(this.title))
                return;

            String old = this.title;
            this.title = newTitle;

            fireTitleEvent(old, newTitle);
        }
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        if (this.maximized == maximized || !isVisible())
            return;

        synchronized (getLock()) {
            boolean old = this.maximized;
            this.maximized = maximized;

            firePropertyChangeEvent("maximized", old, maximized);
        }
    }

    public ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type) {
        return descriptor.getTypeDescriptor(type);
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


    public String toString() {
        return "MyDoggyToolWindow{" +
               "id='" + id + '\'' +
               ", index=" + index +
               ", available=" + available +
               ", visible=" + visible +
               ", active=" + active +
               ", anchor=" + anchor +
               ", type=" + type +
               ", title='" + title + '\'' +
               ", descriptor=" + descriptor +
               ", autoHide=" + autoHide +
               ", flashing=" + flash +
               ", maximized=" + maximized +
               '}';
    }


    public final Object getLock() {
        return LOCK;
    }

    public ToolWindowDescriptor getDescriptor() {
        return descriptor;
    }

    public void addInternalPropertyChangeListener(PropertyChangeListener listener) {
        internalListenerList.add(PropertyChangeListener.class, listener);
    }

    public void removeInternalPropertyChangeListener(PropertyChangeListener listener) {
        internalListenerList.remove(PropertyChangeListener.class, listener);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, property, oldValue, newValue);
        fireEvent(event);
    }

    protected void fireAnchorEvent(ToolWindowAnchor oldValue, ToolWindowAnchor newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "anchor", oldValue, newValue);
        fireEvent(event);
    }

    protected void fireTypeEvent(ToolWindowType oldValue, ToolWindowType newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "type", oldValue, newValue);
        fireEvent(event);
    }

    protected void fireIconEvent(Icon oldValue, Icon newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "icon", oldValue, newValue);
        fireEvent(event);
    }

    protected void fireTitleEvent(String oldValue, String newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "title", oldValue, newValue);
        fireEvent(event);
    }

    protected void fireEvent(PropertyChangeEvent event) {
        PropertyChangeListener[] listeners = internalListenerList.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }

        if (publicEvent) {
            listeners = listenerList.getListeners(PropertyChangeListener.class);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

}
