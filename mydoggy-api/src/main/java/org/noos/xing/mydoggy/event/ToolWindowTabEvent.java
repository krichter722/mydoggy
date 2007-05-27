package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.ToolWindow;

import java.util.EventObject;

/**
 * TODO: javadoc
 * 
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
 */
public class ToolWindowTabEvent extends EventObject {

    public enum ActionId {
        TAB_ADDED,         // When a tab is added.
        TAB_REMOVED,       // When a tab is removed.
        TAB_REMOVING       // When a tab is removing.
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
