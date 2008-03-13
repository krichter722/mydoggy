package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.content.PlafContent;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo add support for minimized to content manager ui...
 */
public class MyDoggyContent extends PropertyChangeEventSource implements PlafContent {
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
    protected boolean flash;
    protected boolean minimized;


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
        this.flash = false;
    }


    public String toString() {
        return id;  
    }

    public String getId() {
        return id;
    }

    public Component getComponent() {
        return component;
    }

    public boolean isFlashing() {
        return flash;
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
        if (this.component != null && this.component.equals(component))
            return;

        Component old = this.component;
        this.component = component;

        firePropertyChangeEvent("component", old, component);
    }

    public Icon getDisabledIcon() {
        return disabledIcon;
    }

    public void setDisabledIcon(Icon disabledIcon) {
        if (this.disabledIcon != null && this.disabledIcon.equals(disabledIcon))
            return;

        Icon old = this.disabledIcon;
        this.disabledIcon = disabledIcon;

        firePropertyChangeEvent("disabledIcon", old, disabledIcon);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            boolean old = this.enabled;
            this.enabled = enabled;

            firePropertyChangeEvent("enabled", old, enabled);
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

            firePropertyChangeEvent("selected", old, selected);
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

        firePropertyChangeEvent("foreground", old, foreground);
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        if (this.icon != null && this.icon.equals(icon))
            return;

        Icon old = this.icon;
        this.icon = icon;

        firePropertyChangeEvent("icon", old, icon);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        if (this.popupMenu != null && this.popupMenu.equals(popupMenu))
            return;

        JPopupMenu old = this.popupMenu;
        this.popupMenu = popupMenu;

        firePropertyChangeEvent("popupMenu", old, popupMenu);
    }

    public void setDetached(boolean detached) {
        if (this.detached == detached)
            return;

        if (detached)
            setMaximized(false);

        boolean old = this.detached;
        this.detached = detached;

        firePropertyChangeEvent("detached", old, detached);
    }

    public boolean isDetached() {
        return detached;
    }

    public void setMnemonic(int mnemonic) {
        if (this.mnemonic == mnemonic)
            return;

        int old = this.mnemonic;
        this.mnemonic = mnemonic;

        firePropertyChangeEvent("mnemonic", old, mnemonic);
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
            firePlafPropertyChangeEvent("maximized.before", false, maximized);
        
        this.maximized = maximized;
        firePropertyChangeEvent("maximized", old, maximized);
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMinimzed(boolean minimized) {
        if (this.minimized == minimized)
            return;

        // TODO: should be unmaximized if it's the case....

        boolean old = this.minimized;
        this.minimized = minimized;
        firePropertyChangeEvent("minimized", old, minimized);
    }

    public boolean isMinimzed() {
        return minimized;
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

        firePropertyChangeEvent("title", old, title);
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        String old = this.toolTipText;
        this.toolTipText = toolTipText;

        firePropertyChangeEvent("toolTipText", old, toolTipText);
    }

    public Dockable getDockableDelegator() {
        return dockableDelegator;
    }

}
