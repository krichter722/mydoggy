package org.noos.xing.mydoggy;

/**
 * This interface is used to modify the behaviours of SLIDING type.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowType#SLIDING
 * @since 1.2.0
 */
public interface SlidingTypeDescriptor extends ToolWindowTypeDescriptor {

    /**
     * Sets the transparent mode. If the transparent mode is enabled then when the toolwindow's content
     * losts the focus it becomes transparent. This facility is os-independent.
     *
     * @param transparentMode <code>true</code> to enable trasparent mode;
     *                        <code>false</code> to disable transparent mode.
     * @see #isTransparentMode()
     * @see #setTransparentRatio(float)
     * @since 1.2.0
     */
    void setTransparentMode(boolean transparentMode);

    /**
     * Returns the transparent mode.
     *
     * @return <code>true</code> if the trasparent mode is enabled;
     *         <code>false</code> otherwise.
     * @see #setTransparentMode(boolean)
     * @since 1.2.0
     */
    boolean isTransparentMode();

    /**
     * Sets the transparent ratio. Valid range is [0.0, 1.0]
     *
     * @param transparentRatio the transparent ratio.
     * @since 1.2.0
     */
    void setTransparentRatio(float transparentRatio);

    /**
     * Returns the transparent ratio.
     *
     * @return ratio value used to describe the opacity of the window.
     * @since 1.2.0
     */
    float getTransparentRatio();

    /**
     * Sets the transparent delay. When the window losts focus, after delay time the window will become
     * transparent.
     *
     * @param transparentDelay the transparent delay
     * @since 1.2.0
     */
    void setTransparentDelay(int transparentDelay);

    /**
     * Returns the transparent delay.
     *
     * @return delay in milliseconds.
     * @since 1.2.0
     */
    int getTransparentDelay();

}
