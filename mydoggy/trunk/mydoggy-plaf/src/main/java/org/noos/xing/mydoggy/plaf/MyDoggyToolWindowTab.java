package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerAggregator;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerProvider;
import org.noos.xing.mydoggy.plaf.cleaner.DefaultCleanerAggregator;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowTab extends PropertyChangeEventSource implements ToolWindowTab, CleanerProvider {
    protected boolean root;
    protected String id;
    protected ToolWindow owner;
    protected String title;
    protected Icon icon;
    protected Component component;
    protected boolean selected;
    protected boolean closeable;
    protected boolean minimizable;
    protected boolean flash;
    protected boolean maximized;
    protected boolean minimized;

    protected Dockable dockable;
    protected PropertyChangeListener delegatorListener;

    protected CleanerAggregator cleanerAggregator;


    public MyDoggyToolWindowTab(ToolWindow owner, boolean root,
                                String title, Icon icon, Component component,
                                Dockable dockable) {
        super(((MyDoggyToolWindowManager) owner.getDockableManager()).getFirePublicEvent());

        this.root = root;
        this.id = "" + System.nanoTime();
        this.owner = owner;
        this.title = title;
        this.icon = icon;
        this.component = component;
        this.selected = false;
        this.closeable = !root;
        this.minimizable = true;
        this.dockable = dockable;
        this.flash = false;
        this.maximized = false;
        this.minimized = false;

        if (dockable != null) {
            dockable.addPropertyChangeListener(delegatorListener = new DelegatorListener());
            addPropertyChangeListener(new DelegateListener());
        }

        cleanerAggregator = new DefaultCleanerAggregator();
    }


    public ToolWindow getDockableManager() {
        return owner;
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
        if (flash && owner.isVisible() && isSelected())
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

        if (this.flash)
            return;

        this.flash = true;
        firePropertyChangeEvent("flash", false, true, duration);
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

        if (selected && isMinimized())
            setMinimized(false);

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

    public boolean isMinimizable() {
        return minimizable;
    }

    public void setMinimizable(boolean minimizable) {
        if (this.minimizable == minimizable)
            return;

        boolean old = this.minimizable;
        this.minimizable = minimizable;

        firePropertyChangeEvent("minimizable", old, minimizable);
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

    public void setMinimized(boolean minimized) {
        if (this.minimized == minimized)
            return;

        boolean old = this.minimized;
        this.minimized = minimized;
        firePropertyChangeEvent("minimized", old, minimized);
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void ensureVisible() {
        if (!isMinimized())
            firePlafPropertyChangeEvent("ensureVisible", null, this);
    }

    public boolean isVisible() {
        return isSelected();
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

    public void cleanup() {
        if (dockable != null)
            dockable.removePropertyChangeListener(delegatorListener);

        cleanerAggregator.cleanup();
        super.cleanup();
    }


    public CleanerAggregator getCleanerAggregator() {
        return cleanerAggregator;
    }


    public class DelegatorListener implements PropertyChangeListener  {

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
            } else if ("flash".equals(propertyName)) {
                setFlashing((Boolean) evt.getNewValue());
            }
        }
        
    }

    public class DelegateListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            if ("flash".equals(propertyName))  {
                dockable.setFlashing((Boolean) evt.getNewValue());
            } 
        }
    }

}
