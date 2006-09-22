package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.boundle.ResourceBoundle;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 * @todo there are some syncronization problems if we use a tool window in a multi thread environment. Investigate it. 
 */
public class MyDoggyToolWindow implements ToolWindow {
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

    private ResourceBoundle resourceBoundle;
    private ToolWindowDescriptor descriptor;

    private EventListenerList internalListenerList;
    private EventListenerList listenerList;

    private int lastAnchorIndex;

    private boolean fireToAllListeners = true;  // TODO: eliminate it.

    public MyDoggyToolWindow(MyDoggyToolWindowManager manager, Window anchestor, String id, int index,
                             ToolWindowAnchor anchor, ToolWindowType type,
                             String title, Icon icon, Component component,
                             ResourceBoundle resourceBoundle) {
        this.internalListenerList = new EventListenerList();
        this.listenerList = new EventListenerList();

        this.descriptor = new ToolWindowDescriptor(manager, this, anchestor, component);
        this.resourceBoundle = resourceBoundle;

        this.id = id;
        this.index = index;
        this.anchor = anchor;
        this.type = type;
        setTitle(title);
        this.icon = icon;
        this.available = this.active = this.visible = false;

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

        int old = this.index;
        this.index = index;

        firePropertyChangeEvent("index", old, index);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        if (this.available == available)
            return;

        if (!available) {
            if (isActive())
                setActive(false);
            if (isVisible())
                setVisible(false);
        }

        boolean old = this.available;
        this.available = available;

        firePropertyChangeEvent("available", old, available);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible)
            return;

        if (visible)
            setAvailable(visible);
        else if (active)
            setActive(false);

        boolean old = this.visible;
        this.visible = visible;

        firePropertyChangeEvent("visible", old, visible);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active == active)
            return;

        if (active) {
            setAvailable(active);
            setVisible(active);
        }

        boolean old = this.active;
        this.active = active;

        firePropertyChangeEvent("active", old, active);
    }

    public ToolWindowAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(ToolWindowAnchor anchor) {
        if (this.anchor == anchor && anchor.getIndex() == lastAnchorIndex)
            return;

        boolean tempVisible = isVisible();
        boolean tempActive = isActive();

        setAvailable(false);
        ToolWindowAnchor oldAnchor = this.anchor;
        this.anchor = anchor;
        this.lastAnchorIndex = anchor.getIndex();

        setAvailable(true);
        if (tempActive)
            setActive(true);
        else if (tempVisible)
            setVisible(true);

        fireAnchorEvent(oldAnchor, anchor);
    }

    public boolean isAutoHide() {
        return autoHide;
    }

    public void setAutoHide(boolean autoHide) {
        if (this.autoHide == autoHide)
            return;

        boolean old = this.autoHide;
        this.autoHide = autoHide;
        firePropertyChangeEvent("autoHide", old, autoHide);
    }

    public ToolWindowType getType() {
        return type;
    }

    public void setType(ToolWindowType type) {
        if (this.type == type)
            return;


        boolean tempVisible = isVisible();
        boolean tempActive = isActive();

        fireToAllListeners = false;

        setActive(false);
        setVisible(false);

        fireToAllListeners = true;

        ToolWindowType oldType = this.type;
        this.type = type;


        fireTypeEvent(oldType, type);

        if (tempActive) {
            setActive(true);
        } else if (tempVisible)
            setVisible(true);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        Icon old = this.icon;
        this.icon = icon;

        fireIconEvent(old, icon);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String newTitle = (resourceBoundle != null) ? resourceBoundle.getString(title) : title;
        String old = this.title;
        this.title = newTitle;

        fireTitleEvent(old, newTitle);
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
        return getClass().getName() + "[index : " + index + ", active : " + active + ", visible : " + visible + ", available : " + available + ']';
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

        if (fireToAllListeners) {
            listeners = listenerList.getListeners(PropertyChangeListener.class);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

}
