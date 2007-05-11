package org.noos.xing.mydoggy;

import org.noos.xing.mydoggy.event.ToolWindowTabEvent;

import java.util.EventListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowListener extends EventListener {

    void toolWindowTabAdded(ToolWindowTabEvent event);

    void toolWindowTabRemoved(ToolWindowTabEvent event);
    
}
