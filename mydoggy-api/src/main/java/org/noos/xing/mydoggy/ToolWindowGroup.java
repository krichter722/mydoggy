package org.noos.xing.mydoggy;

/**
 * This interface lets the user to manager a group of tool window. The idea is to provide the same mechanism of
 * prospectives of eclipse ide for toolwindows.
 * The typical usage is to retrieve a group from the tool window manager and to add any toolwindows.
 * After that we can show or hide all tools registered in a group.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.0.0
 */
public interface ToolWindowGroup {

    /**
     * Returns the group's name.
     *
     * @return group's name.
     * @since 1.0.0
     */
    String getName();


    /**
     * Appends the specified tool window to the end of this group.
     *
     * @param toolWindow tool window to be appended to this group.
     * @throws NullPointerException     if the specified tool window is null.
     * @throws IllegalArgumentException if tool window was already added.
     * @since 1.0.0
     */
    void addToolWindow(ToolWindow toolWindow);

    /**
     * Removes the occurrence in this group of the specified tool window.
     *
     * @param toolWindow tool window to be removed from this group, if present.
     * @return <tt>true</tt> if this group contained the specified tool window.
     * @throws NullPointerException if the specified element is null.
     * @since 1.0.0
     */
    boolean removeToolWindow(ToolWindow toolWindow);

    /**
     * Returns an array containing all of the toolwindows in this group in proper
     * sequence.
     *
     * @return an array containing all of the elements in this group in proper sequence.
     * @since 1.0.0
     */
    ToolWindow[] getToolsWindow();

    /**
     * Returns <tt>true</tt> if this group contains the specified tool window.
     *
     * @param toolWindow tool window whose presence in this group is to be tested.
     * @return <tt>true</tt> if this group contains the specified element.
     * @throws NullPointerException if the specified element is null.
     * @since 1.0.0
     */
    boolean containesToolWindow(ToolWindow toolWindow);

    /**
     * The method is used to set the implicit property of the group.
     * If <code>implicit</code> is <tt>true</tt> then if a tool in this group is made visible then
     * all tools in this group will be make visible.
     * <br>
     * Default value is false.
     *
     * @param implicit <tt>true</tt> to enable, <tt>false</tt> otherwise.
     * @since 1.3.0
     */
    void setImplicit(boolean implicit);

    /**
     *
     * @return
     * @since 1.3.0
     */
    boolean isImplicit();

    /**
     * Invokes <code>setVisible(visible)</code> on all toolwindows contained in this group
     * in proper sequence.
     *
     * @param visible true to make the tools, contained in this group, visible, false to hide.
     * @see ToolWindow#setVisible(boolean)
     * @since 1.0.0
     */
    void setVisible(boolean visible);


    /**
     * Registers <code>listener</code> so that it will receive events when
     * the groups are made visibile or invisible, when a tool is added or removed
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ToolWindowGroupListener</code> to register.
     * @see ToolWindowGroupListener
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
     */
    ToolWindowGroupListener[] getToolWindowGroupListeners();

}
