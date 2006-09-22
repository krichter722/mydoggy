package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ToolWindowGroupEvent;

import java.util.EventListener;

/**
 * The listener interface for receiving "interesting" events
 * (showed, hided, tool added, tool removed) on a tool window group.
 *
 * The listener object created is then registered with a
 * tool window group using the groups's <code>addToolWindowGroupListener</code>
 * method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowGroup
 * @see org.noos.xing.mydoggy.ToolWindowGroup#addToolWindowGroupListener(ToolWindowGroupListener)
 * @see org.noos.xing.mydoggy.event.ToolWindowGroupEvent.Id
 */
public interface ToolWindowGroupListener extends EventListener {

    /**
     * Invoked when a tool has been added to group.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     */
    void toolAdded(ToolWindowGroupEvent event);

    /**
     * Invoked when a tool has been removed from group.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     */
    void toolRemoved(ToolWindowGroupEvent event);

    /**
     * Invoked when the tool window group has been showed.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent
     */
    void groupShowed(ToolWindowGroupEvent event);

    /**
     * Invoked when the tool window group has been hided.
     *
     * @param event the fired event.
     * @see ToolWindowGroupEvent 
     */
    void groupHided(ToolWindowGroupEvent event);

}
