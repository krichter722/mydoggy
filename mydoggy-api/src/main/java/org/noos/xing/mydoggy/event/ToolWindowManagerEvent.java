package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;

import java.util.EventObject;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowManagerEvent extends EventObject {
    public static enum Id {
        TOOL_REGISTERED, TOOL_UNREGISTERED,
        GROUP_ADDED, GROUP_REMOVED
    }

    private Id id;
    private ToolWindow toolWindow;
    private ToolWindowGroup toolWindowGroup;

    public ToolWindowManagerEvent(Object source, Id id, ToolWindow toolWindow) {
        super(source);
        this.id = id;
        this.toolWindow = toolWindow;
    }

    public ToolWindowManagerEvent(Object source, Id id, ToolWindowGroup toolWindowGroup) {
        super(source);
        this.id = id;
        this.toolWindowGroup = toolWindowGroup;
    }

    public Id getId() {
        return id;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public ToolWindowGroup getToolWindowGroup() {
        return toolWindowGroup;
    }
}
