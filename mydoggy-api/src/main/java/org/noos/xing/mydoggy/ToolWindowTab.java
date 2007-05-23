package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.awt.*;

/**
 * TODO: javadocs
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public interface ToolWindowTab {

    /**
     *
     * @return
     * @since 1.3.0
     */
    String getTitle();

    /**
     *
     * @param title
     * @since 1.3.0
     */
    void setTitle(String title);

    /**
     *
     * @return
     * @since 1.3.0
     */
    Icon getIcon();

    /**
     *
     * @param icon
     * @since 1.3.0
     */
    void setIcon(Icon icon);

    /**
     *
     * @return
     * @since 1.3.0
     */
    Component getComponent();

    /**
     *
     * @param component
     * @since 1.3.0
     */
    void setComponent(Component component);

    /**
     *
     * @return
     * @since 1.3.0
     */
    boolean isSelected();

    /**
     *
     * @param selected
     * @since 1.3.0
     */
    void setSelected(boolean selected);

    boolean isCloseable();

    void setCloseable(boolean closeable);

    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     * <li>this tool's title ("title")</li>
     * <li>this tool's icon ("icon")</li>
     * <li>this tool's component ("component")</li>
     * <li>this tool's selected ("selected")</li>
     * </ul>
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #getPropertyChangeListeners()
     * @see #removePropertyChangeListener
     * @since 1.0.0
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
     * @since 1.0.0
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the property change listeners
     * registered on this tool.
     *
     * @return all of this tool's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @since 1.0.0
     */
    PropertyChangeListener[] getPropertyChangeListeners();
}
