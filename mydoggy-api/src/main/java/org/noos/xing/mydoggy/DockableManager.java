package org.noos.xing.mydoggy;

/**
 * This is a markup interface for all Dockable Manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.2
 */
public interface DockableManager {

    /**
     * Registers <code>listener</code> so that it will receive events when
     * a dockable is registered or removed..
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ToolWindowManagerListener</code> to register.
     * @see ToolWindowManagerListener
     * @since 1.4.2
     */
    void addDockableManagerListener(DockableManagerListener listener);

    /**
     * Unregisters <code>listener</code> so that it will no longer receive
     * events. This method performs no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this group.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>DockableManagerListener</code> to be removed
     * @see #addDockableManagerListener(DockableManagerListener)
     * @since 1.4.2
     */
    void removeDockableManagerListener(DockableManagerListener listener);

    /**
     * Returns an array of all the manager listeners
     * registered on this manager.
     *
     * @return all of the group's <code>DockableManagerListener</code>s
     *         or an empty array if no tool window manager listeners are currently registered.
     * @see #addDockableManagerListener(DockableManagerListener)
     * @see #removeDockableManagerListener(DockableManagerListener)
     * @since 1.4.2
     */
    DockableManagerListener[] getDockableManagerListeners();


}
