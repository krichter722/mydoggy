package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" events
 * (tab added, removed and removing).
 * <p/>
 * The listener object created is then registered with a
 * tool window using the <code>addToolWindowListener</code> method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see ToolWindowTab
 * @see ToolWindow#addToolWindowListener(ToolWindowListener)
 * @see ToolWindowTabEvent
 * @since 1.3.0
 */
public interface ToolWindowListener extends EventListener {

    /**
     * Invoked when a tab has been added to the tool.
     *
     * @param event the fired event.
     * @see ToolWindowTabEvent
     * @since 1.3.0
     */
    void toolWindowTabAdded(ToolWindowTabEvent event);

    /**
     * Invoked before removing the tab.
     *
     * @param event the fired event.
     * @return false if you want to rollback the remove action.
     * @see ToolWindowTabEvent
     * @since 1.3.0
     */
    boolean toolWindowTabRemoving(ToolWindowTabEvent event);

    /**
     * Invoked when a tool has been removed from the group.
     *
     * @param event the fired event.
     * @see ToolWindowTabEvent
     * @since 1.3.0
     */
    void toolWindowTabRemoved(ToolWindowTabEvent event);
    
}
