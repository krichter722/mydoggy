package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo add javadocs..
 */
public interface Dockable extends Observable {

    /**
     * Returns the id used to register this dockable.
     *
     * @return dockable id.
     * @since 1.3.2
     */
    String getId();
    
    /**
     * Returns the dockable title.
     *
     * @return the title for the this dockable.
     * @see #setTitle(String)
     * @since 1.3.2
     */
    String getTitle();

    /**
     * This method is used to set the title for this dockable.
     *
     * @param title the new title.
     * @see #getTitle()
     * @since 1.3.2
     */
    void setTitle(String title);

    /**
     * This method is used to set the icon for this dockable.
     *
     * @param icon the new icon.
     * @see #getIcon()
     * @since 1.3.2
     */
    void setIcon(Icon icon);

    /**
     * Returns the dockable icon.
     *
     * @return the icon of this dockable.
     * @see #setIcon(javax.swing.Icon)
     * @since 1.3.2
     */
    Icon getIcon();

    /**
     * Returns the dockable component.
     *
     * @return the component of this dockable.
     * @since 1.3.2
     */
    Component getComponent();
    
}
