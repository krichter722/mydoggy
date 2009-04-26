package org.noos.xing.mydoggy;

/**
 * This is a markup interface for all Dockable Manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.2
 */
public interface DockableManager<D extends Dockable> {

    /**
     * Returns an array of the dockables registered into this manager.
     *
     * @return an array of the dockables registered into this manager.
     *         If there is no tool registered then it returns an empty array.
     * @since 1.5.0
     */
    D[] getDockables();

    /**
     * Returns the dockable whose id is the passed value, null otherwise.
     *
     * @param id the dockable id.
     * @return the dockable whose id is the passed value, null otherwise. 
     * @since 1.5.0
     */
    D getDockableById(String id);

    /**
     * Associates the specified dockable with the specified alias in this manager.
     * If the manager previously contained a mapping for this alias, the old dockable is replaced
     * by the specified dockable.
     *
     * @param d dockable to be associated with the specified alias.
     * @param alias alias with which the specified dockable is to be associated.
     * @since 1.5.0
     */
    void addAlias(D d, Object alias);

    /**
     * Returns all aliases associated to the passed dockable.
     *
     * @param d dockable whose aliases are to be returned.
     * @return all aliases associated to the passed dockable.
     * @since 1.5.0
     * @see #addAlias(Dockable, Object)
     */
    Object[] getAliases(D d);

    /**
     * Remove the alias from the manager. If the manager previously contained a mapping for this alias,
     * the dockable is returned.
     *
     * @param alias alias to be removed.
     * @return If the manager previously contained a mapping for this alias,
     * the dockable is returned.
     * @since 1.5.0
     */
    D removeAlias(Object alias);

    /**
     * Registers <code>listener</code> so that it will receive events when
     * a dockable is registered or removed.
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
