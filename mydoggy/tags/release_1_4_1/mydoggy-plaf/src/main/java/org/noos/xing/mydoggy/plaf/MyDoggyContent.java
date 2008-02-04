package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.plaf.ui.content.PlafContent;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyContent implements PlafContent {
    protected transient MyDoggyContentManager contentManager;

    protected String id;
    protected String title;
    protected Color foreground;
    protected Icon icon;
    protected Icon disabledIcon;
    protected String toolTipText;
    protected boolean enabled;
    protected transient Component component;
    protected JPopupMenu popupMenu;
    protected boolean detached;
    protected int mnemonic;
    protected boolean selected;
    protected boolean maximized;
    protected transient Dockable dockableDelegator;

    protected EventListenerList uiListeners;
    protected EventListenerList listeners;


    public MyDoggyContent(MyDoggyContentManager contentManager,
                          String id, String title, Icon icon,
                          Component component,
                          String toolTipText,
                          Dockable dockableDelegator) {
        this.contentManager = contentManager;
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.toolTipText = toolTipText;
        this.enabled = true;
        this.mnemonic = -1;
        this.selected = false;
        this.maximized = false;
        this.dockableDelegator = dockableDelegator;

        this.listeners = new EventListenerList();
        this.uiListeners = new EventListenerList();
    }

    public String getId() {
        return id;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        if (this.component != null && this.component.equals(component))
            return;

        Component old = this.component;
        this.component = component;

        firePropertyChange("component", old, component);
    }

    public Icon getDisabledIcon() {
        return disabledIcon;
    }

    public void setDisabledIcon(Icon disabledIcon) {
        if (this.disabledIcon != null && this.disabledIcon.equals(disabledIcon))
            return;

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
        if (contentManager.getPlafContentManagerUI().isInstalled())
            return contentManager.getPlafContentManagerUI().isSelected(this);
        else
            return selected;
    }

    public void setSelected(boolean selected) {
        if (isSelected() != selected || !contentManager.getPlafContentManagerUI().isInstalled()) {
            boolean old = isSelected();
            this.selected = selected;
            contentManager.getPlafContentManagerUI().setSelected(this, selected);

            firePropertyChange("selected", old, selected);
        }
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        if (this.foreground != null && this.foreground.equals(foreground))
            return;

        Color old = this.foreground;
        this.foreground = foreground;

        firePropertyChange("foreground", old, foreground);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        if (this.icon != null && this.icon.equals(icon))
            return;

        Icon old = this.icon;
        this.icon = icon;

        firePropertyChange("icon", old, icon);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        if (this.popupMenu != null && this.popupMenu.equals(popupMenu))
            return;

        JPopupMenu old = this.popupMenu;
        this.popupMenu = popupMenu;

        firePropertyChange("popupMenu", old, popupMenu);
    }

    public void setDetached(boolean detached) {
        if (this.detached == detached)
            return;

        if (detached)
            setMaximized(false);

        boolean old = this.detached;
        this.detached = detached;

        firePropertyChange("detached", old, detached);
    }

    public boolean isDetached() {
        return detached;
    }

    public void setMnemonic(int mnemonic) {
        if (this.mnemonic == mnemonic)
            return;

        int old = this.mnemonic;
        this.mnemonic = mnemonic;

        firePropertyChange("mnemonic", old, mnemonic);
    }

    public int getMnemonic() {
        return mnemonic;
    }

    public void setMaximized(boolean maximized) {
        if (this.maximized == maximized)
            return;

        if (maximized) {
            for (Content content : contentManager.getContents()) {
                if (content.isMaximized() && content != this) {
                    content.setMaximized(false);
                    return;
                }
            }
        }

        boolean old = this.maximized;
        if (maximized)
            firePrivatePropertyChange("maximized.before", false, maximized);
        
        this.maximized = maximized;
        firePropertyChange("maximized", old, maximized);
    }

    public boolean isMaximized() {
        return maximized;
    }

    public ContentUI getContentUI() {
        return contentManager.getContentManagerUI().getContentUI(this);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (this.title != null && this.title.equals(title))
            return;

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

    public Dockable getDockableDelegator() {
        return dockableDelegator;
    }


    public void addPlafPropertyChangeListener(PropertyChangeListener listener) {
        uiListeners.add(PropertyChangeListener.class, listener);
    }

    public void removePlafPropertyChangeListener(PropertyChangeListener listener) {
        uiListeners.remove(PropertyChangeListener.class, listener);
    }

    public void fireSelected(boolean selected) {
        firePropertyChange("selected", !selected, selected);
    }


    public void firePropertyChange(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);

        for (PropertyChangeListener listener : uiListeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }

        for (PropertyChangeListener listener : listeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }
    }

    public void firePrivatePropertyChange(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);

        for (PropertyChangeListener listener : uiListeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }
    }

}
