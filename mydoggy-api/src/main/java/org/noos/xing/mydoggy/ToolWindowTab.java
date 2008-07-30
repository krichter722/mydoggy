package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * Every toolwindow can be considered as a special JTabbedPane and so it can contain more than one component.
 * A ToolWindowTab represents a tab in this special JTabbedPane. Is is described by a title, an icon and a component.
 * A tab can be selected or not.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public interface ToolWindowTab extends Dockable<ToolWindow> {

    /**
     * Returns the owner ot this tab.
     *
     * @return the owner ot this tab.
     * @since 1.4.0
     */
    ToolWindow getOwner();

    /**
     * This method is used to set the component for the tab.
     *
     * @param component the new component.
     * @see #getComponent() ()
     * @since 1.3.0
     */
    void setComponent(Component component);

    /**
     * Returns whether this tab could be close using the ui.
     *
     * @return <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
     * @see #setCloseable(boolean)
     * @since 1.3.0
     */
    boolean isCloseable();

    /**
     * Sets the closeable property of this tab.
     *
     * @param closeable <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
     * @see #isCloseable()
     * @since 1.3.0
     */
    void setCloseable(boolean closeable);

    /**
     * Returns the dockable that this tab is accomodating,  <code>null</code> if no dockable is accomodated.
     *
     * @return the dockable that this tab is accomodating,  <code>null</code> if no dockable is accomodated.
     * @see ToolWindow#addToolWindowTab(Dockable)
     * @since 1.4.0
     */
    Dockable getDockableDelegator();
}
