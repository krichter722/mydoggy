package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * Every toolwindow can be considered as a special JTabbedPane and so it can contain more than one component.
 * A ToolWindowTab represents a tab in this special JTabbedPane. Is is described by a title, an icon and a component.
 * A tab can be selected or not.
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 * @todo add javadocs...
 */
public interface ToolWindowTab extends Dockable {

    /**
     * Returns the id used to register the tool.
     *
     * @return
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
     * Returns whether or not the tan is currently selected.
     *
     * @return true if the tab is selected;
     *         false otherwise
     * @see #setSelected(boolean)
     * @since 1.3.0
     */
    boolean isSelected();

    /**
     * Sets whether or not the tab is selected.
     *
     * @param selected whether or not the tab should be selected.
     * @see #isSelected()
     * @since 1.3.0
     */
    void setSelected(boolean selected);

    /**
	 * Returns whether this content could be close using the ui.
     *
	 * @return <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
     * @see #setCloseable(boolean)
	 * @since 1.3.0
	 */
    boolean isCloseable();

    /**
     * Sets the closeable property of this content.
     *
     * @param closeable <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
     * @since 1.3.0
     * @see #isCloseable()
     */
    void setCloseable(boolean closeable);

    /**
     *
     * @return
     * @since 1.4.0
     */
    Dockable getDockableDelegator();
}
