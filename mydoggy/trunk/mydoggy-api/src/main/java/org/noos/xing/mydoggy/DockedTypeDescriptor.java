package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * This interface is used to modify the behaviours of DOCKED type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType#DOCKED
 */
public interface DockedTypeDescriptor extends ToolWindowTypeDescriptor {

    /**
     * Specifies whether the popup menu of the representative anchor button of the tool should be enabled.
     *
     * @param enabled <code>true</code> to enable popup menu.
     *                <code>false</code> otherwise.
     * @see org.noos.xing.mydoggy.ToolWindowType
     * @see #isPopupMenuEnabled()
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    void setPopupMenuEnabled(boolean enabled);

    /**
     * Indicates whether the popup menu of the representative anchor button of the tool is enabled.
     *
     * @return <code>true</code> if the popup menu is enabled;
     *         <code>false</code> otherwise.
     * @see #setPopupMenuEnabled(boolean)
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    boolean isPopupMenuEnabled();

    /**
     * The user can add specific menu items to the popup menu of the representative anchor button of the tool using
     * the result of this method invocation.
     *
     * @return the menu where to add new menu items.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    JMenu getToolsMenu();

    /**
     * Sets the width or heigth, based on anchor of tool window, of the dock panel.
     * This value is used by DOCKED and SLIDING tool window type.
     *
     * @param length dock panel length.
     * @see #getDockLength()
     */
    void setDockLength(int length);

    /**
     * Returns the dock panel length.
     *
     * @return the dock panel length.
     * @see #setDockLength(int)
     */
    int getDockLength();

    ToolWindowActionHandler getToolWindowActionHandler();

    void setToolWindowActionHandler(ToolWindowActionHandler toolWindowActionHandler);

    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     * <li>this type's dockLength ("dockLength")</li>
     * <li>this type's popupMenuEnabled property ("popupMenuEnabled")</li>
     * </ul>
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #getPropertyChangeListeners()
     * @see #removePropertyChangeListener
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
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the property change listeners
     * registered on this descritpro.
     *
     * @return all of this descriptor's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    PropertyChangeListener[] getPropertyChangeListeners();

}
