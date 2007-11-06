package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * This interface is used to modify the behaviours of the ToolWindowManager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.2.0
 */
public interface ToolWindowManagerDescriptor extends Observable {

    /**
     * @since 1.3.0
     */
    enum Corner {
        NORD_WEST,
        SOUTH_WEST,
        NORD_EAST,
        SOUTH_EAST
    }

    /**
     * Sets the push away mode to <code>pushAwayMode</code>
     *
     * @param pushAwayMode the new push away mode
     * @see org.noos.xing.mydoggy.PushAwayMode
     * @since 1.2.0
     */
    void setPushAwayMode(PushAwayMode pushAwayMode);

    /**
     * Returns the current push away mode.
     *
     * @return the current push away mode.
     * @since 1.2.0
     */
    PushAwayMode getPushAwayMode();

    /**
     * Returns the PushAwayModeDescriptor for the passed push away mode if
     * it is supported, null otherwise.
     *
     * @param pushAwayMode mode whose descriptor is to be returned.
     * @return the PushAwayModeDescriptor for the passed push away mode if
     *         it is supported, null otherwise.
     * @see PushAwayModeDescriptor
     * @see PushAwayMode
     * @since 1.3.0
     */
    PushAwayModeDescriptor getPushAwayModeDescriptor(PushAwayMode pushAwayMode);

    /**
     * Adds the specified component at the given corner.
     *
     * @param corner    the corner at which to insert the component.
     * @param component the component to be added.
     * @see Corner
     * @since 1.3.0
     */
    void setCornerComponent(Corner corner, Component component);

    /**
     * Enable or disable toolwindow index shortcuts.
     *
     * @param numberingEnabled <tt>true</tt> to enabled toolwindow index shortcuts, <tt>false</tt> to disable..
     * @since 1.3.1
     * @see org.noos.xing.mydoggy.ToolWindow#setIndex(int)
     * @see #isNumberingEnabled() 
     */
    void setNumberingEnabled(boolean numberingEnabled);

    /**
     * Returns the activation status of the toolwindow index shortcuts.
     *
     * @return <tt>true</tt> is the toolwindow index shortcuts are enabled, <tt>false</tt> otherwise. 
     * @since 1.3.1
     * @see #setNumberingEnabled(boolean)
     */
    boolean isNumberingEnabled();

    /**
     * Sets the size of the border the separate a docked toolwindow from the contenManager for a
     * specific anchor.
     *
     * @param anchor the anchor.
     * @param size an integer giving the size of the divider in pixels
     * @see #getDividerSize(ToolWindowAnchor)
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @since 1.3.1
     */
    void setDividerSize(ToolWindowAnchor anchor, int size);

    /**
     * Returns the size of the border the separate a docked toolwindow from the contenManager for the
     * specified anchor.
     *
     * @param anchor the anchor.
     * @return the size of the border
     * @see #setDividerSize(ToolWindowAnchor, int)
     * @since 1.3.1
     */
    int getDividerSize(ToolWindowAnchor anchor);

    /**
     * TODO
     * @param anchor
     * @param enable
     * @since 1.3.2
     */
    void setAggregateMode(ToolWindowAnchor anchor, boolean enable);

    /**
     * TODO
     * @param anchor
     * @return
     * @since 1.3.2
     */
    boolean isAggregateMode(ToolWindowAnchor anchor);
}
