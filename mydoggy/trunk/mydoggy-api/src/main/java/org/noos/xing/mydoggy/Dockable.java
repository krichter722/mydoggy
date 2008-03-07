package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * This interface represents the super interface for of all dockable object, i.e. ToolWindow,
 * ToolWindowTab and Content.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see ToolWindow
 * @see ToolWindowTab
 * @see Content
 * @since 1.4.0
 */
public interface Dockable extends Observable {

    /**
     * Returns the id used to register this dockable.
     *
     * @return dockable id.
     * @since 1.4.0
     */
    String getId();
    
    /**
     * Returns the dockable title.
     *
     * @return the title for the this dockable.
     * @see #setTitle(String)
     * @since 1.4.0
     */
    String getTitle();

    /**
     * This method is used to set the title for this dockable.
     *
     * @param title the new title.
     * @see #getTitle()
     * @since 1.4.0
     */
    void setTitle(String title);

    /**
     * This method is used to set the icon for this dockable.
     *
     * @param icon the new icon.
     * @see #getIcon()
     * @since 1.4.0
     */
    void setIcon(Icon icon);

    /**
     * Returns the dockable icon.
     *
     * @return the icon of this dockable.
     * @see #setIcon(javax.swing.Icon)
     * @since 1.4.0
     */
    Icon getIcon();

    /**
     * Returns the dockable component.
     *
     * @return the component of this dockable.
     * @since 1.4.0
     */
    Component getComponent();

    /**
     * Sets the flashing mode. For toolwindows if the flashing mode is enabled then the
     * toolwindow representative button  will be flashing until the tool will be made visible.
     * If the tool is visible but not active then  the toolwindow title bar will be flashing until the tool
     * will be made visible.
     *
     * @param flash <code>true</code> to enable flashing mode;
     *              <code>false</code> to disable flashing mode.
     * @see #isFlashing()
     * @see #setFlashing(int)
     * @since 1.4.2
     */
    void setFlashing(boolean flash);

    /**
     * This method is used to enable flashing for a specific duration.
     *
     * @param duration the duration of the flashing in millisiconds.
     * @see #setFlashing(boolean)
     * @since 1.4.2
     */
    void setFlashing(int duration);

    /**
     * Returns whether flashing is currently enabled.
     *
     * @return <code>true</code> if the flashing is currently enabled, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isFlashing();

    /**
     * This method is used to detach a content from the main window. When a content is detached
     * it is showed into a separete window.
     *
     * @param detached true to detach the content, false to reattach the content into the main window
     * @since 1.4.2
     */
    void setDetached(boolean detached);

    /**
     * Returns whether or not the content is currently detached.
     *
     * @return true if the content is detached;
     *         false otherwise
     * @see #setDetached(boolean)
     * @since 1.4.2
     */
    boolean isDetached();

    /**
     * Sets whether or not the dockable is selected.
     *
     * @param selected whether or not the dockable should be selected.
     * @see #isSelected()
     * @since 1.4.2
     */
    void setSelected(boolean selected);

    /**
     * Returns whether or not the dockable is currently selected.
     *
     * @return true if the dockable is selected;
     *         false otherwise
     * @see #setSelected(boolean)
     * @since 1.4.2
     */
    boolean isSelected();

    /**
     * Maximizes this toolwindow. A maximized toolwindow is resized to
     * fully fit the main content area.
     *
     * @param maximized a boolean, where <code>true</code> maximizes this toolwindow and <code>false</code>
     *                  restores it.
     * @since 1.4.2
     */
    void setMaximized(boolean maximized);

    /**
     * Returns whether this toolwindow is currently maximized.
     *
     * @return <code>true</code> if this toolwindow is maximized, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isMaximized();

    /**
     * Minimizes this dockable. A minimized dockable is a dockable not visible but available..
     *
     * @param minimized a boolean, where <code>true</code> minimized this dockable and <code>false</code>
     *                  restores it.
     * @since 1.4.2
     */
    void setMinimzed(boolean minimized);

    /**
     * Returns whether this dockable is currently minimized.
     *
     * @return <code>true</code> if this dockable is minimized, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isMinimzed();

}
