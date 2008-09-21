package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * This interface is used to modify the ui behaviours of a single content.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.1.0
 * @see TabbedContentUI
 * @see DesktopContentUI
 */
public interface ContentUI extends DockableUI {

    /**
     * Returns the Content contropart of this ContentUI.
     *
     * @return the Content contropart.
     * @since 1.2.0
     */
    Content getContent();

    /**
     * Returns whether this content could be close using the ui.
     *
	 * @return <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
     * @see #setCloseable(boolean)
	 * @since 1.1.0
	 */
	boolean isCloseable();

	/**
	 * Sets the closeable property of this content.
     *
	 * @param closeable <code>true</code> if this content can be closed using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 * @see #isCloseable()
	 */
	void setCloseable(boolean closeable);

	/**
	 * Returns whether this content could be detach using the ui.
     *
	 * @return <code>true</code> if this content can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 */
	boolean isDetachable();

	/**
	 * Sets the detachable property of this content.
     *
	 * @param detachable <code>true</code> if this content can be detached using the ui, <code>false</code> otherwise.
	 * @since 1.1.0
	 * @see #isDetachable() 
	 */
	void setDetachable(boolean detachable);

    /**
     * Returns whether this content could be minimized using the ui.
     *
     * @return <code>true</code> if this content can be minimized using the ui, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isMinimizable();

    /**
     * Sets the minimizable property of this content.
     *
     * @param minimizable <code>true</code> if this content can be minimized using the ui, <code>false</code> otherwise.
     * @since 1.4.2
     * @see #isMinimizable() ()
     */
    void setMinimizable(boolean minimizable);

    /**
     * Returns whether this content could be maximized using the ui.
     *
     * @return <code>true</code> if this content can be maximized using the ui, <code>false</code> otherwise.
     * @since 1.5.0
     */
    boolean isMaximizable();

    /**
     * Sets the maximizable property of this content.
     *
     * @param maximizable <code>true</code> if this content can be maximized using the ui, <code>false</code> otherwise.
     * @since 1.5.0
     * @see #isMaximizable()
     */
    void setMaximizable(boolean maximizable);

    /**
     * Sets the transparent mode. If the transparent mode is enabled then when
     * the content is detached and the window containing the content losts
     * the focus then the window becomes transparent. This facility is os-dependent.
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

    /**
     * Set up contraints for the content. This dependes on the spcific ContentManagerUI mounted.
     *
     * @param contraints the constraint to bound the content into the ui.
     * @since 1.4.0
     */
    void setConstraints(Object... contraints);

    /**
     * Returns the bounds of the detached window from the last detached session.
     *
     * @return the bounds.
     * @since 1.4.1
     */
    Rectangle getDetachedBounds();

    /**
     * Sets the bounds for the next detached session.
     *
     * @param detachedBounds bounds to be setted.
     * @since 1.4.1
     */
    void setDetachedBounds(Rectangle detachedBounds);

    /**
     * When it sets to <tt>true</tt> then when the content is detached a button
     * is added to the task bar of your operating system.
     *
     * @param addToTaskBarWhenDetached <tt>true</tt> if to the content is associated a button
     * into the task bar when it is detached, <tt>false</tt> otherwise.
     * @since 1.4.2
     */
    void setAddToTaskBarWhenDetached(boolean addToTaskBarWhenDetached);

    /**
     * Returns the property value of "addToTaskBarWhenDetached".
     *
     * @return <tt>true</tt> if to the content is associated a button
     * into the task bar when it is detached, <tt>false</tt> otherwise. 
     * @since 1.4.2
     */
    boolean isAddToTaskBarWhenDetached();

    /**
     * Changes the alwaysOnTop property value used for the window associated to the detache content.
     *
     * @param alwaysOnTop new value of always-on-top state of the window.
     * @since 1.5.0
     */
    void setAlwaysOnTop(boolean alwaysOnTop);

    /**
     * Returns the value of the alwaysOnTop property.
     *
     * @return <code>true</code>, if the content window is in always-on-top state,
     *         <code>false</code> otherwise
     * @see #setAlwaysOnTop
     * @since 1.5
     */
    boolean isAlwaysOnTop();

}
