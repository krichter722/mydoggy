package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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

    private boolean autoHide;
    private boolean available;
    private boolean visible;
    private boolean active;
    private boolean flash;
    private boolean maximized;
    private boolean aggregateEnabled;

    private java.util.List<ToolWindowTab> toolWindowTabs;
    private ToolWindowTab originalToolWindowTab;
    private ToolWindowTab defaultToolWindowTab;

    private ResourceBundle resourceBoundle;
    private ToolWindowDescriptor descriptor;

    private EventListenerList internalListenerList;
    private EventListenerList listenerList;

    private boolean publicEvent = true;

    private int availablePosition;

    MyDoggyToolWindow(MyDoggyToolWindowManager manager, Window anchestor, String id, int index,
                      ToolWindowAnchor anchor, ToolWindowType type,
                      String title, Icon icon, Component component,
                      ResourceBundle resourceBundle) {
        this.internalListenerList = new EventListenerList();
        this.listenerList = new EventListenerList();

        this.descriptor = new ToolWindowDescriptor(manager, this, anchestor, component);
        this.resourceBoundle = resourceBundle;
        this.toolWindowTabs = new ArrayList<ToolWindowTab>();

        defaultToolWindowTab = addToolWindowTab(title, component);

        defaultToolWindowTab.setIcon(icon);

        this.id = id;
        this.index = index;
        this.anchor = anchor;
        this.type = type;
        setTitle(title);
        setIcon(icon);
        this.available = this.active = this.visible = this.maximized = this.aggregateEnabled = false;
        aggregateEnabled = true;
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

            firePropertyChangeEvent("available", old, available, availablePosition);
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

    public void setAggregateMode(boolean aggregateEnabled) {
        if (this.aggregateEnabled == aggregateEnabled)
            return;

        synchronized (getLock()) {
            boolean old = this.aggregateEnabled;
            this.aggregateEnabled = aggregateEnabled;

            firePropertyChangeEvent("aggregateEnabled", old, this.aggregateEnabled);
        }
    }

    public boolean isAggregateMode() {
        return aggregateEnabled;
    }

    public boolean isFlashing() {
        return flash;
    }

    public void setFlashing(boolean flash) {
        if (flash && isActive())
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
            this.flash = true;

            firePropertyChangeEvent("flash.duration", null, duration);
        }
    }

    public boolean marker = true;
    public void setVisible(boolean visible) {
        if (aggregateEnabled && visible && !descriptor.getManager().isShiftShow() &&
            getType() == ToolWindowType.DOCKED && marker)
            aggregate();

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
        setAnchor(anchor, -2);
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        synchronized (getLock()) {
            if (this.anchor == anchor &&
                (index == getDescriptor().getLabelIndex() || index == -2))
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

                    availablePosition = index;
                    setAvailable(true);
                    if (tempActive)
                        setActive(true);
                    else if (tempVisible)
                        setVisible(true);
                } finally {
                    publicEvent = true;
                }

                fireAnchorEvent(oldAnchor, anchor, index);
            } else {
                ToolWindowAnchor oldAnchor = this.anchor;
                this.anchor = anchor;

                if (oldAnchor == anchor) {
                    if (index != -2)
                        fireAnchorEvent(null, anchor, index);
                } else
                    fireAnchorEvent(oldAnchor, anchor, index);
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
        return defaultToolWindowTab.getIcon();
    }

    public void setIcon(Icon icon) {
        synchronized (getLock()) {
            if (getIcon() == icon)
                return;

            Icon old = this.getIcon();
            defaultToolWindowTab.setIcon(icon);

            fireIconEvent(old, icon);
        }
    }

    public String getTitle() {
        return defaultToolWindowTab.getTitle();
    }

    public void setTitle(String title) {
        synchronized (getLock()) {
            String newTitle = (resourceBoundle != null) ? resourceBoundle.getString(title) : title;
            if (newTitle != null && newTitle.equals(getTitle()))
                return;

            String old = this.getTitle();
            defaultToolWindowTab.setTitle(newTitle);

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

    public ToolWindowTab addToolWindowTab(String title, Component component) {
        ToolWindowTab tab = new MyDoggyToolWindowTab(title, null, component);
        toolWindowTabs.add(tab);

        fireToolWindowTabEvent(new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_ADDED, this, tab));

        if (toolWindowTabs.size() == 1)
            defaultToolWindowTab = tab;

        return tab;
    }

    public boolean removeToolWindowTab(ToolWindowTab toolWindowTab) {
        if (toolWindowTab == null)
            throw new IllegalArgumentException("ToolWindowTab cannot be null.");

        boolean result = toolWindowTabs.remove(toolWindowTab);
        if (result)
            fireToolWindowTabEvent(new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_REMOVED, this, toolWindowTab));

        if (defaultToolWindowTab == toolWindowTab) {
            if (toolWindowTabs.size() > 0)
                defaultToolWindowTab = toolWindowTabs.get(0);
        }

        return result;
    }

    public ToolWindowTab[] getToolWindowTabs() {
        return toolWindowTabs.toArray(new ToolWindowTab[0]);
    }

    public void addToolWindowListener(ToolWindowListener listener) {
        listenerList.add(ToolWindowListener.class, listener);
    }

    public void removeToolWindowListener(ToolWindowListener listener) {
        listenerList.remove(ToolWindowListener.class, listener);
    }

    public ToolWindowListener[] getToolWindowListeners() {
        return listenerList.getListeners(ToolWindowListener.class);
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
               ", title='" + getTitle() + '\'' +
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

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue, Object userObject) {
        PropertyChangeEvent event = new UserPropertyChangeEvent(descriptor, property, oldValue, newValue, userObject);
        fireEvent(event);
    }

    protected void fireAnchorEvent(ToolWindowAnchor oldValue, ToolWindowAnchor newValue, Object userObject) {
        PropertyChangeEvent event = new UserPropertyChangeEvent(descriptor, "anchor", oldValue, newValue, userObject);
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

    protected void fireToolWindowTabEvent(ToolWindowTabEvent event) {
        ToolWindowListener[] listeners = internalListenerList.getListeners(ToolWindowListener.class);
        for (ToolWindowListener listener : listeners) {
            switch(event.getActionId()) {
                case TAB_ADDED:
                    listener.toolWindowTabAdded(event);
                    break;
                case TAB_REMOVED:
                    listener.toolWindowTabRemoved(event);
                    break;
            }
        }

        if (publicEvent) {
            listeners = listenerList.getListeners(ToolWindowListener.class);
            for (ToolWindowListener listener : listeners) {
                switch(event.getActionId()) {
                    case TAB_ADDED:
                        listener.toolWindowTabAdded(event);
                        break;
                    case TAB_REMOVED:
                        listener.toolWindowTabRemoved(event);
                        break;
                }
            }
        }
    }

}
