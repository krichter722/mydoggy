package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.awt.*;

/**
 * Every toolwindow can be considered as a special JTabbedPane and so it can contain more than one component.
 * A ToolWindowTab represents a tab in this special JTabbedPane. Is is described by a title, an icon and a component.
 * A tab can be selected or not.
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public interface ToolWindowTab {

    /**
     * Returns the tab title.
     *
     * @return the title for the tab.
     * @see #setTitle(String)
     * @since 1.3.0
     */
    String getTitle();

    /**
     * This method is used to set the title for the tab.
     *
     * @param title the new title.
     * @see #getTitle()
     * @since 1.3.0
     */
    void setTitle(String title);

    /**
     * Returns the tab icon.
     *
     * @return the icon for the tab.
     * @see #setIcon(javax.swing.Icon)
     * @since 1.3.0
     */
    Icon getIcon();

    /**
     * This method is used to set the icon for the tab.
     *
     * @param icon the new icon.
     * @see #getIcon()
     * @since 1.3.0
     */
    void setIcon(Icon icon);

    /**
     * Returns the tab component.
     *
     * @return the component for the tab.
     * @see #setComponent(java.awt.Component)
     * @since 1.3.0
     */
    Component getComponent();

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
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     * <li>this tab's title ("title")</li>
     * <li>this tab's icon ("icon")</li>
     * <li>this tab's component ("component")</li>
     * <li>this tab's selected property ("selected")</li>
     * <li>this tab's closeable property ("closeable")</li>
     * </ul>
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #getPropertyChangeListeners()
     * @see #removePropertyChangeListener
     * @since 1.3.0
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a PropertyChangeListener from the listener list.
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed.
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     * @since 1.3.0
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the property change listeners
     * registered on this tab.
     *
     * @return all of this tab's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @since 1.3.0
     */
    PropertyChangeListener[] getPropertyChangeListeners();

    ToolWindow getToolWindow();

    ToolWindowTab getToolWindowTab();

    ToolWindow getOwner();
}
