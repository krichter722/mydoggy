package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.ToolWindowTab;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowTab implements ToolWindowTab {
    private String title;
    private Icon icon;
    private Component component;
    private boolean selected;

    private EventListenerList listenerList;

    public MyDoggyToolWindowTab(String title, Icon icon, Component component) {
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.selected = false;
        this.listenerList = new EventListenerList();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        String old = this.title;
        this.title = title;

        firePropertyChangeEvent("title", old, title);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        Icon old = this.icon;
        this.icon = icon;
        
        firePropertyChangeEvent("icon", old, icon);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return listenerList.getListeners(PropertyChangeListener.class);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);

        PropertyChangeListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

}
