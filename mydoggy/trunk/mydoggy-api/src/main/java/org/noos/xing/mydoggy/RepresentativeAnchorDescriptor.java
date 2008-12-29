package org.noos.xing.mydoggy;

/**
 * This interface is used to modify the behaviour of a dockable's representative anchor.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public interface RepresentativeAnchorDescriptor extends Observable {

    Dockable getDockable();
    
    /**
     * Enable or disable the representative anchor button. The method throw an exception when it is called
     * on a tool whose type is FLOATING_FREE.
     *
     * @param visible <tt>true</tt> to enable make visible the representative anchor button, false otherwise.
     * @since 1.5.0
     */
    void setVisible(boolean visible);

    /**
     * Returns <tt>true</tt> if the representative anchor button is visible, <tt>false</tt> otherwise.
     *
     * @return <tt>true</tt> if the representative anchor button is visible, <tt>false</tt> otherwise.
     * @since 1.5.0
     */
    boolean isVisible();

    /**
     * TODO:
     * @param title
     * @since 1.5.0
     */
    void setTitle(String title);

    /**
     *
     * @return
     * @since 1.5.0
     */
    String getTitle();

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
     * Add an anchor used to indicate where the dockable can be placed.
     *
     * @param anchor an anchor used to indicate where the dockable can be placed.
     * @since 1.5.0
     */
    void addLockingAnchor(ToolWindowAnchor anchor);

    /**
     * Remove an anchor to indicate where the dockable cannot be placed.
     *
     * @param anchor an anchor to indicate where the dockable cannot be placed.
     * @since 1.5.0
     */
    void removeLockingAnchor(ToolWindowAnchor anchor);

    /**
     * Remove all anchors so the dockable can stay only in the current position
     *
     * @since 1.5.0
     */
    void removeAllLockingAnchor();

    /**
     * Returns the anchors register as locking anchors.
     *
     * @return the anchors register as locking anchors.
     * @since 1.5.0
     */
    ToolWindowAnchor[] getLockingAnchors();

    /**
     * Checks if the specified anchor is registered as a locking anchor.
     *
     * @param anchor the anchor whose presence must be checked. 
     * @return <code>true</code> if the anchor is present, <code>false</code> otherwise. 
     * @since 1.5.0
     */
    boolean containsLockingAnchor(ToolWindowAnchor anchor);

    /**
     * TODO:...
     * @param message
     */
    void showMessage(String message);

}
