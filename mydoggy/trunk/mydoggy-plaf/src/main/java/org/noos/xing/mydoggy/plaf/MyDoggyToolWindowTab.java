package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowTab extends PropertyChangeEventSource implements ToolWindowTab {
    protected boolean root;
    protected String id;
    protected ToolWindow owner;
    protected String title;
    protected Icon icon;
    protected Component component;
    protected boolean selected;
    protected boolean closeable;
    protected boolean flash;
    protected boolean maximized;
    protected boolean minimized;

    protected Dockable dockable;

    public MyDoggyToolWindowTab(ToolWindow owner, boolean root,
                                String title, Icon icon, Component component,
                                Dockable dockable) {
        this.root = root;
        this.id = "" + System.nanoTime();
        this.owner = owner;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.selected = false;
        this.closeable = !root;
        this.dockable = dockable;
        this.flash = false;
        this.maximized = false;
        this.minimized = false;

        if (dockable != null)
            dockable.addPropertyChangeListener(new DelegatorListener());
    }


    public ToolWindow getOwner() {
        return owner;
    }

    public String getId() {
        return id;
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

    public boolean isFlashing() {
        return flash;
    }

    public void setDetached(boolean detached) {
        throw new IllegalStateException("This dockable doesn't support detached mode.");
    }

    public boolean isDetached() {
        return false;
    }

    public void setFlashing(boolean flash) {
        if (flash && isSelected())
            return; 

        if (this.flash == flash)
            return;

        boolean old = this.flash;
        this.flash = flash;

        firePropertyChangeEvent("flash", old, flash);
    }

    public void setFlashing(int duration) {
        if (isSelected())
            return;

        this.flash = true;
        firePropertyChangeEvent("flash.duration", null, duration);
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

        if (selected && isMinimzed())
            setMinimzed(false);

        boolean old = this.selected;
        this.selected = selected;
        
        firePropertyChangeEvent("selected", old, selected);
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        if (root)
            throw new IllegalArgumentException("Cannot change this for root tab.");

        if (this.closeable == closeable)
            return;

        boolean old = this.closeable;
        this.closeable = closeable;

        firePropertyChangeEvent("closeable", old, closeable);
    }

    public void setMaximized(boolean maximized) {
        if (this.maximized == maximized)
            return;

        boolean old = this.maximized;
        this.maximized = maximized;
        firePropertyChangeEvent("maximized", old, maximized);
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMinimzed(boolean minimized) {
        if (this.minimized == minimized)
            return;

        boolean old = this.minimized;
        this.minimized = minimized;
        firePropertyChangeEvent("minimized", old, minimized);
    }

    public boolean isMinimzed() {
        return minimized;
    }

    public Dockable getDockableDelegator() {
        return dockable;
    }

    public String toString() {
        return "MyDoggyToolWindowTab{" +
               "owner=" + owner +
               ", title='" + title + '\'' +
               ", icon=" + icon +
               ", component=" + component +
               ", selected=" + selected +
               ", closeable=" + closeable +
               ", maximized=" + maximized +
               ", toolWindow=" + dockable +
               '}';
    }

    
    protected class DelegatorListener implements PropertyChangeListener  {

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("selected".equals(propertyName))  {
                setSelected((Boolean) evt.getNewValue());
            } else if ("component".equals(propertyName)) {
                setComponent((Component) evt.getNewValue());
            } else if ("icon".equals(propertyName)) {
                setIcon((Icon) evt.getNewValue());
            } else if ("title".equals(propertyName)) {
                setTitle((String) evt.getNewValue());
            } else if ("closeable".equals(propertyName)) {
                setCloseable((Boolean) evt.getNewValue());
            }
        }
        
    }
}
