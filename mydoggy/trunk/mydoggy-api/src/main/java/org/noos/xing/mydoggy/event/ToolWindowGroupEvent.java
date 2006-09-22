package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;

import java.util.EventObject;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowGroupEvent extends EventObject {
    public static enum Id {
        TOOL_ADDED, TOOL_REMOVED,
        GROUP_SHOWED, GROUP_HIDED
    }

    private ToolWindowGroupEvent.Id id;
    private ToolWindowGroup toolWindowGroup;
    private ToolWindow toolWindow;

    public ToolWindowGroupEvent(Object source, ToolWindowGroupEvent.Id id, ToolWindowGroup toolWindowGroup) {
        super(source);
        this.id = id;
        this.toolWindowGroup = toolWindowGroup;
    }

    public ToolWindowGroupEvent(Object source, Id id, ToolWindowGroup toolWindowGroup, ToolWindow toolWindow) {
        super(source);
        this.id = id;
        this.toolWindowGroup = toolWindowGroup;
        this.toolWindow = toolWindow;
    }

    public Id getId() {
        return id;
    }

    public ToolWindowGroup getToolWindowGroup() {
        return toolWindowGroup;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }
}
