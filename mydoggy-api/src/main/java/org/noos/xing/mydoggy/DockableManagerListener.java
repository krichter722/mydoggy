package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.DockableManagerEvent;

import java.util.EventListener;

/**
 * TODO:
 * @since 1.4.2
 */
public interface DockableManagerListener extends EventListener {

    /**
     * Invoked when a dockable has been added to the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ToolWindowTabEvent
     * @since 1.4.2
     */
    void dockableAdded(DockableManagerEvent event);

    /**
     * Invoked when a dockable has been removed from the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ToolWindowTabEvent
     * @since 1.4.2
     */
    void dockableRemoved(DockableManagerEvent event);

}