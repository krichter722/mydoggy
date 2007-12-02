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
    
}
