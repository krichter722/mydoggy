package org.noos.xing.mydoggy;

/**
 * This interface lets the user to manager a group of tool window. The idea is to provide the same mechanism of
 * prospectives of eclipse ide for tools window.
 * The typical usage is to retrieve a group from the tool window manager and to add any tools window.
 * After that we can show or hide all tools registerd in a group.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowGroup {

    /**
     * Returns the group's name.
     *
     * @return group's name.
     */
    String getName();


    /**
     * Appends the specified tool window to the end of this group.
     *
     * @param toolWindow tool window to be appended to this group.
     * @throws NullPointerException     if the specified tool window is null.
     * @throws IllegalArgumentException if tool window was already added.
     */
    void addToolWindow(ToolWindow toolWindow);

    /**
     * Removes the occurrence in this group of the specified tool window.
     *
     * @param toolWindow tool window to be removed from this group, if present.
     * @return <tt>true</tt> if this group contained the specified tool window.
     * @throws NullPointerException if the specified element is null.
     */
    boolean removeToolWindow(ToolWindow toolWindow);

    /**
     * Returns an array containing all of the tools window in this group in proper
     * sequence.
     *
     * @return an array containing all of the elements in this group in proper sequence.
     */
    ToolWindow[] getToolsWindow();

    /**
     * Returns <tt>true</tt> if this group contains the specified tool window.
     *
     * @param toolWindow tool window whose presence in this group is to be tested.
     * @return <tt>true</tt> if this group contains the specified element.
     * @throws NullPointerException if the specified element is null.
     */
    boolean containesToolWindow(ToolWindow toolWindow);


    /**
     * Invokes <code>setVisible(visible)</code> on all tools window contained in this group
     * in proper sequence.
     *
     * @param visible true to make the tools, contained in this group, visible, false to hide.
     * @see ToolWindow#setVisible(boolean)
     */
    void setVisible(boolean visible);


    /**
     * Registers <code>listener</code> so that it will receive events when
     * the groups are made visibile or invisible.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ToolWindowGroupListener</code> to register.
     * @see ToolWindowGroupListener
     */
    void addToolWindowGroupListener(ToolWindowGroupListener listener);

    /**
     * Unregisters <code>listener</code> so that it will no longer receive
     * events. This method performs no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this group.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ToolWindowGroupListener</code> to be removed
     * @see #addToolWindowGroupListener(ToolWindowGroupListener)
     */
    void removeToolWindowGroupListener(ToolWindowGroupListener listener);

    /**
     * Returns an array of all the tool window group listeners
     * registered on this group.
     *
     * @return all of the group's <code>ToolWindowGroupListener</code>s
     *         or an empty array if no tool window group listeners are currently registered.
     * @see #addToolWindowGroupListener(ToolWindowGroupListener)
     * @see #removeToolWindowGroupListener(ToolWindowGroupListener)
     */
    ToolWindowGroupListener[] getToolWindowGroupListeners();

}
