package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.util.EventObject;

/**
 * An event which indicates that an action occurred in a tool window group.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.0.0
 */
public class ToolWindowGroupEvent extends EventObject {
    private static final long serialVersionUID = -6028203866456501444L;

    /**
     * An event which indicates that an action occurred in the tool window manager.
     */
    public enum ActionId {
        TOOL_ADDED,         // When a tool window is added to a group.
        TOOL_REMOVED,       // When a tool window is added from a group.
        GROUP_SHOWN,       // When a group is made visible.
        GROUP_HIDDEN         // When a group is made hide.
    }

    /**
     * Indicates the action identifier.
     *
     * @see ToolWindowGroupEvent.ActionId
     */
    private ActionId actionId;

    /**
     * Indicates the tool window group on which the action has occured. Cannot be null.
     */
    private transient ToolWindowGroup toolWindowGroup;

    /**
     * Indicates the tool window involved in the action. Can be null.
     */
    private transient ToolWindow toolWindow;

    /**
     * Constructs a <code>ToolWindowGroupEvent</code> object with the
     * specified source tool window manager, actionId, toolWindowGroup.
     * <p/>
     * This constructor throws an
     * <code>IllegalArgumentException</code> if the <code>source</code> or the <code>toolWindowGroup</code>
     * or the <code>actionId</code> is <code>null</code>.
     *
     * @param source          the tool window manager where the action has occured.
     * @param actionId        the action identifier
     * @param toolWindowGroup the tool window group on wicht the action is occurred.
     * @see org.noos.xing.mydoggy.ToolWindowManager
     * @see ToolWindowGroupEvent.ActionId
     * @see org.noos.xing.mydoggy.ToolWindowGroup
     */
    public ToolWindowGroupEvent(ToolWindowManager source, ActionId actionId, ToolWindowGroup toolWindowGroup) {
        super(source);
        if (actionId == null)
            throw new IllegalArgumentException("null actionId");
        if (toolWindowGroup == null)
            throw new IllegalArgumentException("null toolWindowGroup");

        this.actionId = actionId;
        this.toolWindow = null;
        this.toolWindowGroup = toolWindowGroup;
    }

    /**
     * Constructs a <code>ToolWindowGroupEvent</code> object with the
     * specified source tool window manager, actionId, toolWindowGroup, toolWindow.
     * <p/>
     * This constructor throws an
     * <code>IllegalArgumentException</code> if the <code>source</code> or the <code>toolWindowGroup</code>
     * or the <code>actionId</code> is <code>null</code>.
     *
     * @param source          the tool window manager where the action has occured.
     * @param actionId        the action identifier
     * @param toolWindowGroup the tool window group on wicht the action is occurred.
     * @param toolWindow      the tool window involved in the action.
     * @see org.noos.xing.mydoggy.ToolWindowManager
     * @see ToolWindowGroupEvent.ActionId
     * @see org.noos.xing.mydoggy.ToolWindowGroup
     * @see org.noos.xing.mydoggy.ToolWindow
     */
    public ToolWindowGroupEvent(ToolWindowManager source, ActionId actionId, ToolWindowGroup toolWindowGroup, ToolWindow toolWindow) {
        super(source);
        if (actionId == null)
            throw new IllegalArgumentException("null actionId");
        if (toolWindowGroup == null)
            throw new IllegalArgumentException("null toolWindowGroup");

        this.actionId = actionId;
        this.toolWindowGroup = toolWindowGroup;
        this.toolWindow = toolWindow;
    }

    /**
     * Returns the action identifier.
     *
     * @return the action identifier.
     */
    public ActionId getId() {
        return actionId;
    }

    /**
     * Returns the tool window group on whiche the action is occured.
     *
     * @return the tool window group.
     */
    public ToolWindowGroup getToolWindowGroup() {
        return toolWindowGroup;
    }

    /**
     * Returns the tool window involved in the action.
     *
     * @return the tool window.
     */
    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public String toString() {
        return "ToolWindowGroupEvent{" +
               "actionId=" + actionId +
               ", toolWindowGroup=" + toolWindowGroup +
               ", toolWindow=" + toolWindow +
               '}';
    }
}
