package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" events
 * (tool registered, tool unregistered, group added, group removed) on the tool window manager.
 * <p/>
 * The listener object created is then registered with the
 * tool window manager using the <code>addToolWindowManagerListener</code>
 * method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see ToolWindowManager
 * @see ToolWindowManager#addToolWindowManagerListener(ToolWindowManagerListener)
 */
public interface ToolWindowManagerListener extends EventListener {

    /**
     * Invoked when a tool has been registered to the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent
     */
    void toolWindowRegistered(ToolWindowManagerEvent event);

    /**
     * Invoked when a tool has been unregistered from the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent
     */
    void toolWindowUnregistered(ToolWindowManagerEvent event);

    /**
     * Invoked when a tool group has been added to the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent
     */
    void toolWindowGroupAdded(ToolWindowManagerEvent event);

    /**
     * Invoked when a tool group has been removed from the manager.
     *
     * @param event the fired event.
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent
     */
    void toolWindowGroupRemoved(ToolWindowManagerEvent event);

}
