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

    protected int index;
    protected String id;
    protected ToolWindowAnchor anchor;
    protected ToolWindowType type;

    protected boolean autoHide;
    protected boolean available;
    protected boolean visible;
    protected boolean active;
    protected boolean flash;
    protected boolean maximized;
    protected boolean aggregateEnabled;

    protected java.util.List<ToolWindowTab> toolWindowTabs;
    protected ToolWindowTab defaultToolWindowTab;

    protected ResourceBundle resourceBoundle;
    protected ToolWindowDescriptor descriptor;

    protected EventListenerList internalListenerList;
    protected EventListenerList listenerList;

    protected boolean publicEvent = true;

    protected int availablePosition;

    protected MyDoggyToolWindow(MyDoggyToolWindowManager manager, Window anchestor, String id, int index,
                      ToolWindowAnchor anchor, ToolWindowType type,
                      String title, Icon icon, Component component,
                      ResourceBundle resourceBundle) {
        this.internalListenerList = new EventListenerList();
        this.listenerList = new EventListenerList();

        this.descriptor = new ToolWindowDescriptor(manager, this, anchestor);
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
    }


    public String getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public Component getComponent() {
        if (toolWindowTabs.size() == 0)
            return null;
        if (toolWindowTabs.size() == 1)
            return toolWindowTabs.get(0).getComponent();
        else {
            for (ToolWindowTab toolWindowTab : toolWindowTabs) {
                if (toolWindowTab.isSelected())
                    return toolWindowTab.getComponent();
            }
            return toolWindowTabs.get(0).getComponent();
        }
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
        if (getType() == ToolWindowType.TABBED) {
            for (ToolWindow tool : descriptor.getManager().getToolWindows()) {
                for (ToolWindowTab tab : tool.getToolWindowTabs()) {
                    if (tab.getToolWindow() == this) {
                        return tool.isVisible() && tab.isSelected();
                    }
                }
            }
        }

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

    public void setVisible(boolean visible) {
        if (aggregateEnabled && visible && !descriptor.getManager().isShiftShow() &&
            getType() == ToolWindowType.DOCKED)
            aggregate();

        if (getType() == ToolWindowType.TABBED) {
            // Call setVisible on tool that own this tool as tab...
            for (ToolWindow tool : descriptor.getManager().getToolWindows()) {
                for (ToolWindowTab tab : tool.getToolWindowTabs()) {
                    if (tab.getToolWindow() == this) {
                        tool.setVisible(visible);
                        if (visible)
                            tab.setSelected(true);
                        return;
                    }
                }
            }
        }

        if (this.visible == visible)
            return;

        synchronized (getLock()) {
            if (!visible && isMaximized())
                setMaximized(false);

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
            if (!publicEvent)
                old = false;
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

            if (isMaximized())
                setMaximized(false);

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
                    if (tempActive) {
                        setActive(true);
                    } else if (tempVisible)
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
        if (type == ToolWindowType.TABBED /*|| this.type == ToolWindowType.TABBED TODO: */)
            throw new IllegalArgumentException("Cannot call this method using that paramenter.");

        boolean forceAvailable = false;
        if (this.type == ToolWindowType.TABBED && type != ToolWindowType.FLOATING_FREE)
            forceAvailable = true;

        setTypeInternal(type);

        if (forceAvailable) {
            available = false;
            setAvailable(true);
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
            publicEvent = false;
            try {
                firePropertyChangeEvent("maximized.before", this.maximized, maximized);
            } finally {
                publicEvent = true;
            }

            boolean old = this.maximized;
            this.maximized = maximized;

            firePropertyChangeEvent("maximized", old, maximized);
        }
    }

    public ToolWindowTab addToolWindowTab(String title, Component component) {
        return addTabInternal(title, null, component, null);
    }

    public ToolWindowTab addToolWindowTab(ToolWindow toolWindow) {
        synchronized (getLock()) {
            for (ToolWindowTab toolWindowTab : toolWindowTabs) {
                if (toolWindowTab.getToolWindow() == toolWindow)
                    return toolWindowTab;
            }

            ((MyDoggyToolWindow) toolWindow).setTypeInternal(ToolWindowType.TABBED);
            return addTabInternal(toolWindow.getTitle(),
                                  toolWindow.getIcon(),
                                  toolWindow.getComponent(),
                                  toolWindow);
        }
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

        if (toolWindowTab.getToolWindow() != null) {
            ToolWindow toolWindow = toolWindowTab.getToolWindow(); 
            toolWindow.setType(ToolWindowType.DOCKED);
        }

        return result;
    }

    public ToolWindowTab[] getToolWindowTabs() {
        return toolWindowTabs.toArray(new ToolWindowTab[toolWindowTabs.size()]);
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


    protected ToolWindowTab addTabInternal(String title, Icon icon, Component component, ToolWindow toolWindow) {
        ToolWindowTab tab = new MyDoggyToolWindowTab(this, title, icon, component, toolWindow);
        toolWindowTabs.add(tab);

        fireToolWindowTabEvent(new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_ADDED, this, tab));

        if (toolWindowTabs.size() == 1)
            defaultToolWindowTab = tab;

        return tab;

    }

    protected void setTypeInternal(ToolWindowType type) {
        synchronized (getLock()) {
            if (this.type == type)
                return;

            switch (type) {
                case SLIDING:
                    if (!((SlidingTypeDescriptor) getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled())
                        return;
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    if (!((FloatingTypeDescriptor) getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled())
                        return;
                    break;
            }

            if (isMaximized())
                setMaximized(false);

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

                if (type != ToolWindowType.TABBED) {
                    if (tempActive) {
                        setActive(true);
                    } else if (tempVisible)
                        setVisible(true);
                }
            } finally {
                publicEvent = true;
            }

            fireTypeEvent(oldType, type);
        }
    }
         
    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, property, oldValue, newValue);
        PropertyChangeEvent publicEvent = new PropertyChangeEvent(this, property, oldValue, newValue);
        fireEvent(event, publicEvent);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue, Object userObject) {
        PropertyChangeEvent event = new UserPropertyChangeEvent(descriptor, property, oldValue, newValue, userObject);
        PropertyChangeEvent publicEvent = new UserPropertyChangeEvent(this, property, oldValue, newValue, userObject);
        fireEvent(event, publicEvent);
    }

    protected void fireAnchorEvent(ToolWindowAnchor oldValue, ToolWindowAnchor newValue, Object userObject) {
        PropertyChangeEvent event = new UserPropertyChangeEvent(descriptor, "anchor", oldValue, newValue, userObject);
        PropertyChangeEvent publicEvent = new UserPropertyChangeEvent(this, "anchor", oldValue, newValue, userObject);
        fireEvent(event, publicEvent);
    }

    protected void fireTypeEvent(ToolWindowType oldValue, ToolWindowType newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "type", oldValue, newValue);
        PropertyChangeEvent publicEvent = new PropertyChangeEvent(this, "type", oldValue, newValue);
        fireEvent(event, publicEvent);
    }

    protected void fireIconEvent(Icon oldValue, Icon newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "icon", oldValue, newValue);
        PropertyChangeEvent publicEvent = new PropertyChangeEvent(this, "icon", oldValue, newValue);
        fireEvent(event, publicEvent);
    }

    protected void fireTitleEvent(String oldValue, String newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(descriptor, "title", oldValue, newValue);
        PropertyChangeEvent publicEvent = new PropertyChangeEvent(this, "title", oldValue, newValue);
        fireEvent(event, publicEvent);
    }

    protected void fireEvent(PropertyChangeEvent event, PropertyChangeEvent publiEvent) {
        PropertyChangeListener[] listeners = internalListenerList.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }

        if (publicEvent) {
            listeners = listenerList.getListeners(PropertyChangeListener.class);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(publiEvent);
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
