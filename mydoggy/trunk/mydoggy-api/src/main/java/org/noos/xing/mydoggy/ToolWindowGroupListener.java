package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ToolWindowGroupEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" events
 * (shown, hidden, tool added, tool removed) on a tool window group.
 * <p/>
 * The listener object created is then registered with a
 * tool window group using the groups's <code>addToolWindowGroupListener</code>
 * method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowGroup
 * @see org.noos.xing.mydoggy.ToolWindowGroup#addToolWindowGroupListener(ToolWindowGroupListener)
 * @see org.noos.xing.mydoggy.event.ToolWindowGroupEvent.ActionId
 * @since 1.0.0
 */
public interface ToolWindowGroupListener extends EventListener {

    /**
     * Invoked when a tool has been added to the group.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     * @since 1.0.0
     */
    void toolAdded(ToolWindowGroupEvent event);

    /**
     * Invoked when a tool has been removed from the group.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     * @since 1.0.0
     */
    void toolRemoved(ToolWindowGroupEvent event);

    /**
     * Invoked when the group has been shown.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     * @since 1.0.0
     */
    void groupShown(ToolWindowGroupEvent event);

    /**
     * Invoked when the group has been hidden.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     * @since 1.0.0
     */
    void groupHidden(ToolWindowGroupEvent event);

}
