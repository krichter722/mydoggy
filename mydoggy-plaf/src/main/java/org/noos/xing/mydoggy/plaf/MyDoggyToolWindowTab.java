package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowTab implements ToolWindowTab {
    protected ToolWindow owner;
    protected String title;
    protected Icon icon;
    protected Component component;
    protected boolean selected;
    protected boolean closeable;
    
    protected ToolWindow toolWindow;
    protected ToolWindowTab toolWindowTab;

    protected EventListenerList listenerList;

    public MyDoggyToolWindowTab(ToolWindow owner, String title, Icon icon, Component component,
                                ToolWindow toolWindow, ToolWindowTab toolWindowTab) {
        this.owner = owner;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.selected = false;
        this.listenerList = new EventListenerList();
        this.closeable = true;
        this.toolWindow = toolWindow;
        this.toolWindowTab = toolWindowTab;
        if (toolWindowTab != null)  {
            toolWindowTab.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    //  TODO: listen to all properties
                    String propertyName = evt.getPropertyName();
                    if ("selected".equals(propertyName))  {
                        setSelected((Boolean) evt.getNewValue());
                    }
                }
            });
        }
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public ToolWindowTab getToolWindowTab() {
        return toolWindowTab;
    }

    public ToolWindow getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null && title.equals(getTitle()))
            return;

        String old = this.title;
        this.title = title;

        firePropertyChangeEvent("title", old, title);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        if (this.icon == icon)
            return;

        Icon old = this.icon;
        this.icon = icon;
        
        firePropertyChangeEvent("icon", old, icon);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        if (this.component == component)
            return;

        Component old = this.component;
        this.component = component;

        firePropertyChangeEvent("component", old, component);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (this.selected == selected)
            return;

        boolean old = this.selected;
        this.selected = selected;
        
        firePropertyChangeEvent("selected", old, selected);
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        if (this.closeable == closeable)
            return;

        boolean old = this.closeable;
        this.closeable = closeable;

        firePropertyChangeEvent("closeable", old, closeable);
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
        return "MyDoggyToolWindowTab{" +
               "owner=" + owner +
               ", title='" + title + '\'' +
               ", icon=" + icon +
               ", component=" + component +
               ", selected=" + selected +
               ", closeable=" + closeable +
               ", toolWindow=" + toolWindow +
               ", listenerList=" + listenerList +
               '}';
    }

    
    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);

        PropertyChangeListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

}
