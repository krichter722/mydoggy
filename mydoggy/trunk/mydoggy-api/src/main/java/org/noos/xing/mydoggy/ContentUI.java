package org.noos.xing.mydoggy;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 */
public interface ContentUI {

    /**
     * Returns the Content contropart of this ContentUI.
     * @return the Content contropart.
     * @since 1.2.0
     */
    Content getContent();

    /**
	 * Returns whether this content could be close using the ui.
	 * @return <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	boolean isCloseable();

	/**
	 * Sets the closeable property of this content.
	 * @param closeable <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 * @see #isCloseable()
	 */
	void setCloseable(boolean closeable);

	/**
	 * Returns whether this content could be detach using the ui.
	 * @return <code>true</code> if this content can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	boolean isDetachable();

	/**
	 * Sets the detachable property of this content.
	 * @param detachable <code>true</code> if this content can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 * @see #isDetachable() 
	 */
	void setDetachable(boolean detachable);

    /**
     * Sets the transparent mode. If the transparent mode is enabled then when
     * the window used by <code>FLOATING</code> and <code>FLOATING_FREE</code> type losts
     * the focus it becomes transparent. This facility is os-dependent.
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
