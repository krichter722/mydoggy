package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowGroup;
import org.noos.xing.mydoggy.ToolWindowManager;

import java.util.EventObject;

/**
 * An event which indicates that an action occurred in the tool window manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowManagerEvent extends EventObject {
    private static final long serialVersionUID = 1719383277615714309L;

    /**
     * Action Identifier Enum.
     */
    public enum ActionId {
        TOOL_REGISTERED,    // When a tool window is registered.
        TOOL_UNREGISTERED,  // When a tool window is unregistered.
        GROUP_ADDED,        // When a tool window group is added.
        GROUP_REMOVED       // When a tool window group is removed.
    }

    /**
     * Indicates the action identifier.
     *
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent.ActionId
     */
    private final ActionId actionId;

    /**
     * Indicates the tool window on which the action has occured.
     * It is not null id <code>actionID</code> is ActionId.TOOL_REGISTERED or ActionId.TOOL_UNREGISTERED.
     */
    private transient ToolWindow toolWindow = null;

    /**
     * Indicates the tool window on which the action has occured.
     * It is not null id <code>actionID</code> is ActionId.GROUP_ADDED or ActionId.GROUP_REMOVED.
     */
    private transient ToolWindowGroup toolWindowGroup = null;

    /**
     * Constructs a <code>ToolWindowManagerEvent</code> object with the
     * specified source tool window manager, actionId, toolWindow.
     * Creating an invalid event (such as by using ActionId.TOOL_REGISTERED with a null tool window)
     * results in unspecified behavior.
     * <p/>
     * This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * @param source     the tool window manager where the action has occured.
     * @param actionId   the action identifier
     * @param toolWindow the tool window subject of the action.
     * @see org.noos.xing.mydoggy.ToolWindowManager
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent.ActionId
     * @see org.noos.xing.mydoggy.ToolWindow
     */
    public ToolWindowManagerEvent(ToolWindowManager source, ActionId actionId, ToolWindow toolWindow) {
        super(source);
        this.actionId = actionId;
        this.toolWindow = toolWindow;
    }

    /**
     * Constructs a <code>ToolWindowManagerEvent</code> object with the
     * specified source tool window manager, actionId, toolWindowGroup.
     * Creating an invalid event (such as by using ActionId.GROUP_ADDED with a null tool window group)
     * results in unspecified behavior.
     * <p/>
     * This method throws an
     * <code>IllegalArgumentException</code> if <code>source</code>
     * is <code>null</code>.
     *
     * @param source     the tool window manager where the action has occured.
     * @param actionId   the action identifier
     * @param toolWindowGroup the tool window group subject of the action.
     * @see org.noos.xing.mydoggy.ToolWindowManager
     * @see org.noos.xing.mydoggy.event.ToolWindowManagerEvent.ActionId
     * @see org.noos.xing.mydoggy.ToolWindowGroup
     */
    public ToolWindowManagerEvent(ToolWindowManager source, ActionId actionId, ToolWindowGroup toolWindowGroup) {
        super(source);
        this.actionId = actionId;
        this.toolWindowGroup = toolWindowGroup;
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
     * Returns the tool window subject of the action.
     *
     * @return the tool window.
     */
    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    /**
     * Returns the tool window group subject of the action.
     *
     * @return the tool window group.
     */
    public ToolWindowGroup getToolWindowGroup() {
        return toolWindowGroup;
    }
}
