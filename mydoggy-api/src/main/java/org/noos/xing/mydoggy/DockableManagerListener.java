package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.DockableManagerEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" events
 * (dockable added, removed) on the dockable manager.
 * <p/>
 * The listener object created is then registered with the
 * manager using the <code>addDockableManagerListener</code>
 * method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.DockableManager
 * @see DockableManager#addDockableManagerListener(DockableManagerListener) 
 * @since 1.4.2
 */
public interface DockableManagerListener extends EventListener {

    /**
     * Invoked when a dockable has been added to the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.DockableManagerEvent
     * @since 1.4.2
     */
    void dockableAdded(DockableManagerEvent event);

    /**
     * Invoked when a dockable has been removed from the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.DockableManagerEvent
     * @since 1.4.2
     */
    void dockableRemoved(DockableManagerEvent event);

}