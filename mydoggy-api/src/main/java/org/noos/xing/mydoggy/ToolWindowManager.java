package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowManager {

    /**
     * Returns an instance of <code>ToolWindowContentManager</code> that manages tool window manager
     * contents.
     * @return an instance of <code>ToolWindowContentManager</code>.
     * @see org.noos.xing.mydoggy.ToolWindowContentManager
     */
    ToolWindowContentManager getContentManager();


    /**
     * Register a new tool window into this window manager based on the passed parameters.
     *
     * @param id id of tool window to be registered.
     * @param title title of tool window to be registered (can be null).
     * @param icon icon of tool window to be registered (can be null).
     * @param component component which represents tool window content.
     * @param anchor anchor of tool window to be registered.
     * @return the registered tool window.
     * @throws java.lang.IllegalArgumentException if exist a tool window registered
     * with the same id or one or more of the parameters is null.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @see org.noos.xing.mydoggy.ToolWindowManager
     * @see #unregisterToolWindow(String)
     */
    ToolWindow registerToolWindow(String id, String title, Icon icon,
                                  Component component, ToolWindowAnchor anchor);

    /**
     * Removes the tool window for this id from this window manager if it is present.
     *
     * @param id id of tool window to be removed.
     * @see #registerToolWindow(String, String, javax.swing.Icon, java.awt.Component, ToolWindowAnchor)
     * @see #unregisterAllToolWindow()
     * @throws java.lang.IllegalArgumentException - if tool window with specified id isn't registered.
     */
    void unregisterToolWindow(String id);

    /**
     * Removes all tools window from this window manager if there are any.
     */
    void unregisterAllToolWindow();

    /**
     *
     * @return <tt>ID</tt> of currently active tool window or <tt>null</tt> if there is no active tool window.
     */
    String getActiveToolWindowId();


    /**
     * Returns the tool window to which this manager maps the specified id.
     * Returns <tt>null</tt> if the manager contains no mapping for this id.
     *
     * @param id id of tool window
     * @return registered tool window with specified id. If there is no registered tool
     * window with specified id then the method returns <tt>null</tt>.
     */
    ToolWindow getToolWindow(String id);

    ToolWindow getToolWindow(int index);

    /**
     * Returns an array of the tools window registered into this manager.
     *
     * @return an array of the tools window registered into this manager.
     * If there is no tool registered then it returns an empty array.
     */
    ToolWindow[] getToolWindows();

    /**
     * Returns an array of the tools window, registered into this manager,
     * anchored on passed anchor.
     *
     * @param anchor anchor which tools window are anchored.
     * @return an array of the tools window, registered into this manager,
     * anchored on passed anchor. If there is no registered tool window anchored on that anchor
     * then it returns an empty array.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    ToolWindow[] getToolsByAnchor(ToolWindowAnchor anchor);


    /**
     * Returns the tool window group to which this manager maps the specified name.
     * If the manager contains no mapping for this name then the manager create a new instance
     * of ToolWindowGroup and associates the group created with the specified name in this manager.
     * .
     * @param name name of tool window group.
     * @return the tool window group to which this manager maps the specified name.
     * @see org.noos.xing.mydoggy.ToolWindowGroup
     * @see #getToolWindowGroups()
     */
    ToolWindowGroup getToolWindowGroup(String name);

    /**
     * Returns an array of the tools window groups registered into this manager.
     *
     * @return an array of the tools window groups registered into this manager.
     * If there is no group registered then it returns an empty array.
     * @see #getToolWindowGroup(String)
     */
    ToolWindowGroup[] getToolWindowGroups();

    /**
     * Removes the tool window group for this name from this manager if it is present.
     *
     * @param name name whose group is to be removed from the manager.
     * @return true if there exist a group for this name from this manager, false otherwise.
     * @see #getToolWindowGroup(String)
     */
    boolean removeToolWindowGroup(String name);

    /**
     * Returns <tt>true</tt> if this manager contains a group for the specified name.
     *
     * @param name name whose presence in this manager is to be tested.
     * @return <tt>true</tt> if this manager contains a group for the specified name.
     */
    boolean containsGroup(String name);


    /**
     *
     * @param type type whose template is to be returned from the manager.
     * @return
     * @see ToolWindowType
     * @see org.noos.xing.mydoggy.ToolWindowTypeDescriptor
     * @throws java.lang.IllegalArgumentException - if doen't exist a template for <code>type</code>.
     */
    ToolWindowTypeDescriptor getTypeDescriptorTemplate(ToolWindowType type);


    /**
     * Registers <code>listener</code> so that it will receive events when
     * the tools window and groups are registered or removed..
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener  the <code>ToolWindowManagerListener</code> to register.
     * @see ToolWindowManagerListener
     */
    void addToolWindowManagerListener(ToolWindowManagerListener listener);

    /**
     * Unregisters <code>listener</code> so that it will no longer receive
     * events. This method performs no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this group.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener  the <code>ToolWindowManagerListener</code> to be removed
     * @see #addToolWindowManagerListener(ToolWindowManagerListener)
     */
    void removeToolWindowManagerListener(ToolWindowManagerListener listener);

    /**
     * Returns an array of all the tool window manager listeners
     * registered on this manager.
     *
     * @return all of the group's <code>ToolWindowManagerListener</code>s
     *         or an empty array if no tool window manager listeners are currently registered.
     * @see #addToolWindowManagerListener(ToolWindowManagerListener)
     * @see #removeToolWindowManagerListener(ToolWindowManagerListener)
     */
    ToolWindowManagerListener[] getToolWindowManagerListeners();
}
