package org.noos.xing.mydoggy;

/**
 * This interface is used to modify the behaviour of a dockable's representative anchor.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public interface RepresentativeAnchorDescriptor extends Observable {

    /**
     * Sets the preview mode. If the preview mode is enabled then when the mouse waits
     * on the toolwindow representative button after a delay time the preview will become visible.
     *
     * @param enabled <code>true</code> to enable preview mode;
     *                <code>false</code> to disable preview mode.
     * @see #isPreviewEnabled()
     * @since 1.5.0
     */
    void setPreviewEnabled(boolean enabled);

    /**
     * Returns the preview mode status.
     *
     * @return <code>true</code> if the preview mode is enabled;
     *         <code>false</code> otherwise.
     * @see #setPreviewEnabled(boolean)
     * @since 1.5.0
     */
    boolean isPreviewEnabled();

    /**
     * Sets the preview delay. When the mouse waits on the toolwindow representative button
     * after a delay time the preview will become visible if the preview mode is enabled.
     *
     * @param delay the preview delay
     * @see #getPreviewDelay()
     * @since 1.5.0
     */
    void setPreviewDelay(int delay);

    /**
     * Returns the preview delay.
     *
     * @return preview delay in milliseconds.
     * @see #setPreviewDelay(int)
     * @since 1.5.0
     */
    int getPreviewDelay();

    /**
     * Sets the transparent ratio of the preview. Valid range is [0.0, 1.0]
     *
     * @param transparentRatio the transparent ratio.
     * @see #getPreviewTransparentRatio()
     * @since 1.5.0
     */
    void setPreviewTransparentRatio(float transparentRatio);

    /**
     * Returns the transparent ratio.
     *
     * @return ratio value used to describe the opacity of the preview.
     * @see #setPreviewTransparentRatio(float)
     * @since 1.5.0
     */
    float getPreviewTransparentRatio();

    /**
     * TODO
     * @param anchor
     * @since 1.5.0
     */
    void addLockingAnchor(ToolWindowAnchor anchor);

    /**
     * TODO
     * @param anchor
     * @since 1.5.0
     */
    void removeLockingAnchor(ToolWindowAnchor anchor);

    /**
     *
     * @since 1.5.0
     */
    void removeAllLockingAnchor();

    /**
     * TODO
     * @return
     * @since 1.5.0
     */
    ToolWindowAnchor[] getLockingAnchors();

    /**
     * TODO
     * @param anchor
     * @return
     * @since 1.5.0
     */
    boolean containsLockingAnchor(ToolWindowAnchor anchor);


}
