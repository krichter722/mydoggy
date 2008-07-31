package org.noos.xing.mydoggy;

/**
 * A ContentManagerUI is an interface to modify the ui behaviours of
 * a content manager. For example this is used to modify the way a content
 * is showed.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see TabbedContentManagerUI
 * @see DesktopContentManagerUI
 * @since 1.1.0
 */
public interface ContentManagerUI<C extends ContentUI> extends Observable {

    /**
     * Set the closeable property. All new ContentUI will obtain this value for their closeable property.
     * Furthermore sets the closeable property of all contents already registered to content manager.
     *
     * @param closeable <code>true</code> if all contents can be closed using the ui, <code>false</code> otherwise.
     * @since 1.1.0
     */
    void setCloseable(boolean closeable);

    /**
     * Returns the closeable property value.
     *
     * @return the closeable property value.
     * @since 1.4.0
     */
    boolean isCloseable();

    /**
     * Set the detachable property. All new ContentUI will obtain this value for their detachable property.
     * Furthermore sets the detachable property of all contents already registered to content manager.
     *
     * @param detachable <code>true</code> if all contents can be detached using the ui, <code>false</code> otherwise.
     * @since 1.1.0
     */
    void setDetachable(boolean detachable);

    /**
     * Returns the detachable property value.
     *
     * @return the detachable property value.
     * @since 1.4.0
     */
    boolean isDetachable();

    /**
     * Returns whether this content manager permits the contents to be maximized using the ui.
     *
     * @return <code>true</code> if this content can be maximized using the ui, <code>false</code> otherwise.
     * @since 1.5.0
     */
    boolean isMaximizable();

    /**
     * Sets the maximizable property of this manager.
     *
     * @param maximizable <code>true</code> if this content can be maximized using the ui, <code>false</code> otherwise.
     * @since 1.5.0
     * @see #isDetachable()
     */
    void setMaximizable(boolean maximizable);

    /**
     * Returns whether this content manager permits the contents to be minimized using the ui.
     *
     * @return <code>true</code> if this content can be minimized using the ui, <code>false</code> otherwise.
     * @since 1.4.2
     */
    boolean isMinimizable();

    /**
     * Sets the minimizable property of this manager.
     *
     * @param minimizable <code>true</code> if this content can be minimized using the ui, <code>false</code> otherwise.
     * @since 1.4.2
     * @see #isDetachable()
     */
    void setMinimizable(boolean minimizable);

    /**
     * Enables or disabled the popup menu for this manager.
     *
     * @param enabled <code>true</code> to enable, <code>false</code> to disable.
     * @since 1.5.0
     */
    void setPopupMenuEnabled(boolean enabled);

    /**
     * Returns whether this content manager permits the popup menu to be showed.
     *
     * @return <code>true</code> if the popup menu can be showed, <code>false</code> otherwise. 
     * @since 1.5.0
     */
    boolean isPopupMenuEnabled();

    /**
     * Returns the ui part to which this manager maps the specified <code>content</code>.
     *
     * @param content content whose associated ui part is to be returned.
     * @return the ui part to which this manager maps the specified <code>content</code>.
     * @since 1.1.0
     */
    C getContentUI(Content content);

    /**
     * Registers <code>listener</code> so that it will receive events when
     * contentUIs are removed or detached.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ContentManagerUIListener</code> to register.
     * @see ContentManagerListener
     * @since 1.2.0
     */
    void addContentManagerUIListener(ContentManagerUIListener listener);

    /**
     * Unregisters <code>listener</code> so that it will no longer receive
     * events. This method performs no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this group.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ContentManagerUIListener</code> to be removed
     * @see #addContentManagerUIListener(ContentManagerUIListener)
     * @since 1.2.0
     */
    void removeContentManagerUIListener(ContentManagerUIListener listener);

    /**
     * Returns an array of all the content manager listeners
     * registered on this manager.
     *
     * @return all of the ContentManagerUI's <code>ContentManagerUIListener</code>s
     *         or an empty array if no tool window manager listeners are currently registered.
     * @see #addContentManagerUIListener(ContentManagerUIListener)
     * @see #removeContentManagerUIListener(ContentManagerUIListener)
     * @since 1.2.0
     */
    ContentManagerUIListener[] getContentManagerUiListener();

}
