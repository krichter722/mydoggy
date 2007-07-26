package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;

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
     * The user can add specific menu items to the popup menu of the representative anchor button of the tool using
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
     * @param length dock panel length.
     * @see #getDockLength()
     * @since 1.0.0
     */
    void setDockLength(int length);

    /**
     * Returns the dock panel length.
     *
     * @return the dock panel length.
     * @see #setDockLength(int)
     * @since 1.0.0
     */
    int getDockLength();

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
     * @since 1.2.0
     * @see #getToolWindowActionHandler()
     */
    void setToolWindowActionHandler(ToolWindowActionHandler toolWindowActionHandler);

    /**
     * Sets the preview mode. If the preview mode is enabled then when the mouse waits
     * on the toolwindow representative button after a delay time the preview will become visible.
     *
     * @param enabled <code>true</code> to enable preview mode;
     *                <code>false</code> to disable preview mode.
     * @since 1.3.0
     * @see #isPreviewEnabled()
     */
    void setPreviewEnabled(boolean enabled);

    /**
     * Returns the preview mode status.
     *
     * @return <code>true</code> if the preview mode is enabled;
     *         <code>false</code> otherwise.
     * @since 1.3.0
     * @see #setPreviewEnabled(boolean)
     */
    boolean isPreviewEnabled();

    /**
     * Sets the preview delay. When the mouse waits on the toolwindow representative button
     * after a delay time the preview will become visible if the preview mode is enabled.
     *
     * @param delay the preview delay
     * @since 1.3.0
     * @see #getPreviewDelay()
     */
    void setPreviewDelay(int delay);

    /**
     * Returns the preview delay.
     *
     * @return preview delay in milliseconds.
     * @since 1.3.0
     * @see #setPreviewDelay(int)
     */
    int getPreviewDelay();

    /**
     * Sets the transparent ratio of the preview. Valid range is [0.0, 1.0]
     *
     * @param transparentRatio the transparent ratio.
     * @since 1.3.0
     * @see #getPreviewTransparentRatio()
     */
    void setPreviewTransparentRatio(float transparentRatio);

    /**
     * Returns the transparent ratio.
     *
     * @return ratio value used to describe the opacity of the preview.
     * @since 1.3.0
     * @see #setPreviewTransparentRatio(float)  
     */
    float getPreviewTransparentRatio();

    /**
     * TODO:
     * @since 1.3.1
     */
    void setHideLabelOnVisible(boolean hideLabelOnVisible);

    /**
     *
     * @return
     * @since 1.3.1
     */
    boolean isHideLabelOnVisible();

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
     * registered on this descritpro.
     *
     * @return all of this descriptor's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @since 1.0.0
     */
    PropertyChangeListener[] getPropertyChangeListeners();

}
