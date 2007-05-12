package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

import java.util.EventListener;

/**
 * TODO: javadoc
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public interface ToolWindowListener extends EventListener {

    /**
     *
     * @param event
     * @since 1.3.0
     */
    void toolWindowTabAdded(ToolWindowTabEvent event);

    /**
     *
     * @param event
     * @since 1.3.0
     */
    void toolWindowTabRemoved(ToolWindowTabEvent event);
    
}
