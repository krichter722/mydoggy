package org.noos.xing.mydoggy.event;

import org.noos.xing.mydoggy.Dockable;

import java.util.EventObject;

/**
 * An event which indicates that an action occurred for a dockable.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.4.2
 */
public class DockableManagerEvent extends EventObject {

    public enum ActionId {
        DOCKABLE_ADDED,         // When a dockable is added.
        DOCKABLE_REMOVED,       // When a dockable is removed.
    }

    /**
     * Indicates the action identifier.
     *
     * @see ActionId
     */
    private final ActionId actionId;

    /**
     * Indicates the dockable window involved in the action. Cannot be null.
     */
    private final Dockable dockable;

    /**
     * Constructs a <code>DockableEvent</code> object with the
     * specified source, actionId and dockable.
     * <p/>
     * This constructor throws an
     * <code>IllegalArgumentException</code> if the <code>source</code> or the <code>actionId</code>
     * or the <code>dockable</code> are <code>null</code>.
     *
     * @param source         the source where the action has occured.
     * @param actionId       the action identifier
     * @param dockable       the dockable on which the action is occurred.
     * @see ActionId
     * @see org.noos.xing.mydoggy.Dockable
     */
    public DockableManagerEvent(Object source, ActionId actionId, Dockable dockable) {
        super(source);
        if (actionId == null)
            throw new IllegalArgumentException("null actionId");
        if (dockable == null)
            throw new IllegalArgumentException("null dockable");

        this.actionId = actionId;
        this.dockable = dockable;
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
     * Returns the dockable window involved in the action.
     *
     * @return the dockable window.
     */
    public Dockable getDockable() {
        return dockable;
    }

    public String toString() {
        return "DockableManagerEvent{" +
               "actionId=" + actionId +
               ", dockable=" + dockable +
               '}';
    }
}