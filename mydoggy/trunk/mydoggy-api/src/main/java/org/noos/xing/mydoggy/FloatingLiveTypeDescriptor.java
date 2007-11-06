package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * This interface is used to modify the behaviours of FLOATING_LIVE type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see ToolWindowType#FLOATING_LIVE
 * @since 1.3.2
 */
public interface FloatingLiveTypeDescriptor extends ToolWindowTypeDescriptor {

    /**
     * Sets the window location. This location is also used the first time the window becomes visible.
     * The top-left corner of the new location
     * is specified by the <code>x</code> and <code>y</code>
     * parameters in the system coordinate space.
     *
     * @param x the <i>x</i>-coordinate of the new location's
     *          top-left corner in the system's coordinate space
     * @param y the <i>y</i>-coordinate of the new location's
     *          top-left corner in the system's coordinate space
     * @see #getLocation
     * @since 1.3.2
     */
    void setLocation(int x, int y);

    /**
     * Gets the location of the window in the form of a
     * point specifying the component's top-left corner.
     * The location will be relative to the system coordinate space.
     *
     * @return an instance of <code>Point</code> representing the top-left corner of the component's bounds in
     *         the system coordinate space.
     * @see #setLocation
     * @since 1.3.2
     */
    Point getLocation();

    /**
     * Sets the window size. This size is also used the first time the window becomes visible.
     *
     * @param width  the new width of this component in pixels
     * @param height the new height of this component in pixels
     * @see #getSize
     * @since 1.3.2
     */
    void setSize(int width, int height);

    /**
     * Returns the size of the window in the form of a
     * <code>Dimension</code> object. The <code>height</code>
     * field of the <code>Dimension</code> object contains
     * this window's height, and the <code>width</code>
     * field of the <code>Dimension</code> object contains
     * this window's width.
     *
     * @return a <code>Dimension</code> object that indicates the
     *         size of this window.
     * @see #setSize
     * @since 1.3.2
     */
    Dimension getSize();

    /**
     * Sets the transparent mode. If the transparent mode is enabled then when the toolwindow's content
     * losts the focus it becomes transparent. This facility is os-independent.
     *
     * @param transparentMode <code>true</code> to enable trasparent mode;
     *                        <code>false</code> to disable transparent mode.
     * @see #isTransparentMode()
     * @see #setTransparentRatio(float)
     * @since 1.3.2
     */
    void setTransparentMode(boolean transparentMode);

    /**
     * Returns the transparent mode.
     *
     * @return <code>true</code> if the trasparent mode is enabled;
     *         <code>false</code> otherwise.
     * @see #setTransparentMode(boolean)
     * @since 1.3.2
     */
    boolean isTransparentMode();

    /**
     * Sets the transparent ratio. Valid range is [0.0, 1.0]
     *
     * @param transparentRatio the transparent ratio.
     * @since 1.3.2
     */
    void setTransparentRatio(float transparentRatio);

    /**
     * Returns the transparent ratio.
     *
     * @return ratio value used to describe the opacity of the window.
     * @since 1.3.2
     */
    float getTransparentRatio();

    /**
     * Sets the transparent delay. When the window losts focus, after delay time the window will become
     * transparent.
     *
     * @param transparentDelay the transparent delay
     * @since 1.3.2
     */
    void setTransparentDelay(int transparentDelay);

    /**
     * Returns the transparent delay.
     *
     * @return delay in milliseconds.
     * @since 1.3.2
     */
    int getTransparentDelay();

    /**
     * Enables or disables this mode, depending on the value of the
     * parameter <code>enabled</code>. An enabled mode can used by user.
     * This Mode is enabled initially by default.
     *
     * @param  enabled   If <code>true</code>, this mode is
     *         enabled; otherwise this mode is disabled
     * @see #isEnabled
     * @since 1.3.2
     */
    void setEnabled(boolean enabled);

    /**
     * Determines whether this mode is enabled. An enabled mode
     * can used by user. This Mode is enabled initially by default.
     * This mode may be enabled or disabled by
     * calling its <code>setEnabled</code> method.
     * @return <code>true</code> if the mode is enabled,
     *          <code>false</code> otherwise
     * @see #setEnabled
     * @since 1.3.2
     */
    boolean isEnabled();
}