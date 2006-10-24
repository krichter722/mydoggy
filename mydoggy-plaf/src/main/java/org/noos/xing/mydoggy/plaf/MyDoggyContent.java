package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.plaf.ui.content.ContentUI;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyContent implements ContentUI {
    private MyDoggyContentManager contentManager;
    
    private String title;
    private Color foreground;
    private Icon icon;
    private Icon disabledIcon;
    private String toolTipText;
    private boolean enabled;
    private Component component;
    private JPopupMenu popupMenu;
    private boolean detached;

    private EventListenerList internalListeners;
    private EventListenerList listeners;


    public MyDoggyContent(MyDoggyContentManager contentManager, String title, Icon icon, Component component, String toolTipText) {
        this.contentManager = contentManager;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.toolTipText = toolTipText;
        this.enabled = true;

        this.listeners = new EventListenerList();
        this.internalListeners = new EventListenerList();
    }


    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        Component old = this.component;
        this.component = component;

        firePropertyChange("component", old, component);
    }

    public Icon getDisabledIcon() {
        return disabledIcon;
    }

    public void setDisabledIcon(Icon disabledIcon) {
        Icon old = this.disabledIcon;
        this.disabledIcon = disabledIcon;

        firePropertyChange("disabledIcon", old, disabledIcon);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            boolean old = this.enabled;
            this.enabled = enabled;

            firePropertyChange("enabled", old, enabled);
        }
    }

    public boolean isSelected() {
        return contentManager.getContentManagerUI().isSelected(this);
    }

    public void setSelected(boolean selected) {
        if (isSelected() != selected) {
            boolean old = isSelected();
            contentManager.getContentManagerUI().setSelected(this, selected);

            firePropertyChange("selected", old, selected);
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        Color old = this.foreground;
        this.foreground = foreground;

        firePropertyChange("foreground", old, foreground);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        Icon old = this.icon;
        this.icon = icon;

        firePropertyChange("icon", old, icon);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        JPopupMenu old = this.popupMenu;
        this.popupMenu = popupMenu;

        firePropertyChange("popupMenu", old, popupMenu);
    }

    public void setDetached(boolean detached) {
        boolean old = this.detached;
        this.detached = detached;

        firePropertyChange("detached", old, detached);
    }

    public boolean isDetached() {
        return detached;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String old = this.title;
        this.title = title;

        firePropertyChange("title", old, title);
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        String old = this.toolTipText;
        this.toolTipText = toolTipText;

        firePropertyChange("toolTipText", old, toolTipText);
    }
    
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(PropertyChangeListener.class, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(PropertyChangeListener.class, listener);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        return listeners.getListeners(PropertyChangeListener.class);
    }

    public void addUIPropertyChangeListener(PropertyChangeListener listener) {
        addInternalPropertyChangeListener(listener);
    }

    public void removeUIPropertyChangeListener(PropertyChangeListener listener) {
        removeInternalPropertyChangeListener(listener);
    }

    public void fireSelected(boolean selected) {
        firePropertyChange("selected", !selected, selected);
    }


    public synchronized void addInternalPropertyChangeListener(PropertyChangeListener listener) {
        internalListeners.add(PropertyChangeListener.class, listener);
    }

    public synchronized void removeInternalPropertyChangeListener(PropertyChangeListener listener) {
        internalListeners.remove(PropertyChangeListener.class, listener);
    }

    public void firePropertyChange(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);

        for (PropertyChangeListener listener : internalListeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }

        for (PropertyChangeListener listener : listeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }
    }
}
