package org.noos.xing.mydoggy;

import javax.swing.*;

/**
 * This interface is used to modify the behaviours of DOCKED type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType#DOCKED
 * @since 1.0.0
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
     * @since 1.0.0
     */
    void setPopupMenuEnabled(boolean enabled);

    /**
     * Indicates whether the popup menu of the representative anchor button of the tool is enabled.
     *
     * @return <code>true</code> if the popup menu is enabled;
     *         <code>false</code> otherwise.
     * @see #setPopupMenuEnabled(boolean)
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @since 1.0.0
     */
    boolean isPopupMenuEnabled();

    /**
     * The user can add customized menu items to the popup menu of the representative anchor button of this tool using
     * the result of this method invocation.
     *
     * @return the menu where to add new menu items.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @since 1.0.0
     */
    JMenu getToolsMenu();

    /**
     * Sets the width or heigth, based on anchor of tool window, of the dock panel.
     * This value is used by DOCKED and SLIDING tool window type.
     *
     * @param dockLength
     * @see #getDockLength()
     * @since 1.0.0
     */
    void setDockLength(int dockLength);

    /**
     * Returns the dock panel length.
     *
     * @return the dock panel length.
     * @see #setDockLength(int)
     * @since 1.0.0
     */
    int getDockLength();

    void setDockSpace(int space);

    int getDockSpace();

    /**
     * Sets the minimum dock length. This information is used every time a toolwindow (in docked or sliding mode)
     * is shown.
     *
     * @param minimumDockLength the minimum dock length.
     * @since 1.4.2
     */
    void setMinimumDockLength(int minimumDockLength);

    /**
     * Returns the minimum dock length used during toolwindow showing.
     *
     * @return the minimum dock length.
     * @since 1.4.2
     */
    int getMinimumDockLength();

    /**
     * Returns the ToolWindowActionHandler instance or null if it is not present.
     *
     * @return the ToolWindowActionHandler instance.
     * @see ToolWindowActionHandler
     * @see #setToolWindowActionHandler(ToolWindowActionHandler)
     * @since 1.2.0
     */
    ToolWindowActionHandler getToolWindowActionHandler();

    /**
     * Sets the ToolWindowActionHandler to this descriptor.
     *
     * @param toolWindowActionHandler the handler.
     * @see ToolWindowActionHandler
     * @see #getToolWindowActionHandler()
     * @see #getToolWindowActionHandler()
     * @since 1.2.0
     */
    void setToolWindowActionHandler(ToolWindowActionHandler toolWindowActionHandler);

}
