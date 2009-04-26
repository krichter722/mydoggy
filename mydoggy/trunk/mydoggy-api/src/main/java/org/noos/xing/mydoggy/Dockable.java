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
public interface Dockable<M extends DockableManager> extends Observable {

    /**
     * Returns the DockableManager associated with this dockable.
     *
     * @return the DockableManager associated with this dockable.
     * @since 1.4.2
     * @see org.noos.xing.mydoggy.ToolWindowManager
     * @see org.noos.xing.mydoggy.ToolWindow
     * @see org.noos.xing.mydoggy.ContentManager
     */
    M getDockableManager();

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
     * If the tool is visible but not active then the toolwindow title bar will be flashing until the tool
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
     * This method is used to detach a dockable from its position.
     *
     * @param detached true to detach the dockable, false to reattach the dockable into the old position.
     * @since 1.4.2
     */
    void setDetached(boolean detached);

    /**
     * Returns whether or not the dockable is currently detached.
     *
     * @return true if the dockable is detached;
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
     * Maximizes this dockable. A maximized dockable is resized to
     * fully fit the related area.
     *
     * @param maximized a boolean, where <code>true</code> maximizes this dockable and <code>false</code>
     *                  restores it.
     * @since 1.4.2
     */
    void setMaximized(boolean maximized);

    /**
     * Returns whether this dockable is currently maximized.
     *
     * @return <code>true</code> if this dockable is maximized, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isMaximized();

    /**
     * Minimizes this dockable. A minimized dockable is a dockable not visible but available.
     *
     * @param minimized a boolean, where <code>true</code> minimized this dockable and <code>false</code>
     *                  restores it.
     * @since 1.4.2
     */
    void setMinimized(boolean minimized);

    /**
     * Returns whether this dockable is currently minimized.
     *
     * @return <code>true</code> if this dockable is minimized, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isMinimized();

    /**
     * Used to ensure that the dockable is visible. The behaviour dependes on the specific dockable
     * (ToolWindow, Content, ToolWindowTab).
     * @since 1.4.2
     */
    void ensureVisible();

    /**
     * Returns <code>true</code> if the dockable is visible, <code>false</code> otherwise.
     *
     * @return <code>true</code> if the dockable is visible, <code>false</code> otherwise.
     * @since 1.4.0
     */
    boolean isVisible();

}
