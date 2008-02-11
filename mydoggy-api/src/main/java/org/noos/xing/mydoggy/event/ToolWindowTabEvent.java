package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;

import java.util.EventObject;

/**
 * An event which indicates that an action occurred in a tool window with a tab as object.
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
     * @see ActionId
     */
    private final ActionId actionId;

    /**
     * Indicates the tool window involved in the action. Cannot be null.
     */
    private final ToolWindow toolWindow;

    /**
     * Indicates the tab on which the action has occured. Cannot be null.
     */
    private final ToolWindowTab toolWindowTab;

    /**
     * Constructs a <code>ToolWindowTabEvent</code> object with the
     * specified source, actionId, tool window and tab.
     * <p/>
     * This constructor throws an
     * <code>IllegalArgumentException</code> if the <code>source</code> or the <code>actionId</code>
     * or the <code>toolWindow</code> or the <code>toolWindowTab</code> is <code>null</code>.
     * 
     * @param source         the source where the action has occured.
     * @param actionId       the action identifier
     * @param toolWindow     the tool window on wicht the action is occurred.
     * @param toolWindowTab  the tab involved in the action.
     * @see ActionId
     * @see org.noos.xing.mydoggy.ToolWindow
     * @see org.noos.xing.mydoggy.ToolWindowTab
     */
    public ToolWindowTabEvent(Object source, ActionId actionId, ToolWindow toolWindow, ToolWindowTab toolWindowTab) {
        super(source);
        if (actionId == null)
            throw new IllegalArgumentException("null actionId");
        if (toolWindow == null)
            throw new IllegalArgumentException("null toolWindow");
        if (toolWindowTab == null)
            throw new IllegalArgumentException("null toolWindowTab");

        this.actionId = actionId;
        this.toolWindow = toolWindow;
        this.toolWindowTab = toolWindowTab;
    }

    /**
     * Returns the action identifier.
     *
     * @return the action identifier.
     */
    public ActionId getActionId() {
        return actionId;
    }

    /**
     * Returns the tool window involved in the action.
     *
     * @return the tool window.
     */
    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    /**
     * Returns the tab involved in the action.
     *
     * @return the tool window.
     */
    public ToolWindowTab getToolWindowTab() {
        return toolWindowTab;
    }

    public String toString() {
        return "ToolWindowTabEvent{" +
               "actionId=" + actionId +
               ", toolWindow=" + toolWindow +
               ", toolWindowTab=" + toolWindowTab +
               '}';
    }
}
