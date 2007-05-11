package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.ToolWindow;

import java.util.EventObject;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabEvent extends EventObject {

    public enum ActionId {
        TAB_ADDED,        // When a tab is added.
        TAB_REMOVED       // When a tab is removed.
    }

    /**
     * Indicates the action identifier.
     *
     * @see ContentManagerEvent.ActionId
     */
    private final ActionId actionId;

    private final ToolWindow toolWindow;

    private final ToolWindowTab toolWindowTab;

    public ToolWindowTabEvent(Object source, ActionId actionId, ToolWindow toolWindow, ToolWindowTab toolWindowTab) {
        super(source);
        this.actionId = actionId;
        this.toolWindow = toolWindow;
        this.toolWindowTab = toolWindowTab;
    }

    public ActionId getActionId() {
        return actionId;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public ToolWindowTab getToolWindowTab() {
        return toolWindowTab;
    }
}
