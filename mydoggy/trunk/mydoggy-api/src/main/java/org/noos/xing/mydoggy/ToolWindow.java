package org.noos.xing.mydoggy;

import java.awt.*;

/**
 * Tool Windows are secondary windows within the main window that provide access to and/or
 * support for a particular functionality.
 * This interface is the main entry point to modify tool window properties.
 * Moreover there are methods to make the tool available, visible and active.
 * A PropertyChangeEvent is fired for the following properties:
 * <ul>
 * <li>this tool's index ("index")</li>
 * <li>this tool's available status ("available")</li>
 * <li>this tool's visible status ("visible")</li>
 * <li>this tool's active status ("active")</li>
 * <li>this tool's anchor ("anchor")</li>
 * <li>this tool's autoHide ("autoHide")</li>
 * <li>this tool's type ("type")</li>
 * <li>this tool's icon ("icon")</li>
 * <li>this tool's title ("title")</li>
 * </ul>
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowManager
 * @since 1.0.0
 */
public interface ToolWindow extends Dockable<ToolWindowManager>, DockableManager<ToolWindowTab> {

    /**
     * This method is used to set the index for the tool. The index is used to activate or hide
     * the tool with that index when the user uses the key combination ALT-index.
     * Valid indexs are -1 or [1..9] where -1 means no index for the tool.
     *
     * @param index the new index for the tool.
     * @see #getIcon()
     * @since 1.0.0
     */
    void setIndex(int index);

    /**
     * Returns the tool's index.
     *
     * @return tool's index.
     * @see #setIndex(int)
     * @since 1.0.0
     */
    int getIndex();

    /**
     * The method is used to set the available property of the tool.
     * If <code>available</code> is <tt>true</tt> then tool becomes available in a way that depends on tool window type.
     * If <code>available</code> is <tt>false</tt> then tool becomes not available in a way that depends on tool window
     * type.
     *
     * @param available <tt>true</tt> to make the tool available, <tt>false</tt> to make the tool not available.
     * @see #isAvailable()
     * @see #setVisible(boolean)
     * @since 1.0.0
     */
    void setAvailable(boolean available);

    /**
     * Returns <tt>true</tt> is the tool is available.
     *
     * @return <tt>true</tt> is the tool is available, false otherwise.
     * @see #setAvailable(boolean)
     * @since 1.0.0
     */
    boolean isAvailable();

    /**
     * The method is used to set the visible property of the tool.
     * If <code>visible</code> is <tt>true</tt> then tool becomes available if not already was.
     * Moreover the tool shows the component in a way that depends on tool window type and becomes visible.
     * If <code>visible</code> is <tt>false</tt> then tool becomes not available if not already was.
     * Moreover the tool hides the component in a way that depends on tool window type and becomes not visible.
     *
     * @param visible <tt>true</tt> to make the tool visible, <tt>false</tt> to make the tool not visible.
     * @see #isVisible()
     * @since 1.0.0
     */
    void setVisible(boolean visible);

    /**
     * Returns <tt>true</tt> is the tool is visible.
     *
     * @return <tt>true</tt> is the tool is vixible, false otherwise.
     * @see #setVisible(boolean)
     * @since 1.0.0
     */
    boolean isVisible();

    /**
     * The method is used to set to the true value the visible property of the tool.
     * The tool becomes visible in a special way. In fact, if there is another tool visible
     * with the same anchor then these two tools will be aggregate to be visible both.
     *
     * @since 1.2.0
     */
    void aggregate();

    /**
     * Agggregate this tool to the already visible tools using the specified aggregation position.
     * This method is usable also when the tool is already visible.
     *
     * @param onPosition the position used to aggregate the tool.
     * @since 1.4.0
     */
    void aggregate(AggregationPosition onPosition);

    /**
     * Aggregate this tool using <code>onToolWindow</code> as a relative position and
     * the aggregation position as the position relative to the specified toolwindow.
     * This method is usable also when the tool is already visible.
     *
     * @param onToolWindow  the toolwindow used as relative position. It must be already visible.
     * @param onPosition the position used to aggregate the tool.
     * @since 1.4.0
     */
    void aggregate(ToolWindow onToolWindow, AggregationPosition onPosition);

    /**
     * This method is used when the user want to aggregate the toolwindow to another toolwindow
     * whose type is FLOATING, FLOATING_FREE or FLOATING_LIVE.
     *
     * @param refToolWindow the floating toolwindow used as aggregation target.
     * @param onPosition the position used to aggregate the tool.
     * @since 1.5.0
     */
    void aggregateByReference(ToolWindow refToolWindow, AggregationPosition onPosition);

    /**
     * The method is used to set the aggregateEnabled property of the tool.
     * If <code>aggregateEnabled</code> is <tt>true</tt> then every call to
     * <code>setVisible(true)</code> will have the same behaviout of a call to <code>aggregate()</code>
     * method.
     * <br>
     * Default value is false.
     *
     * @param aggregateEnabled <tt>true</tt> to translate every call to <code>setVisible(true)</code>
     *                         to a call to <code>aggregate()</code> method, <tt>false</tt> to disable the translation.
     * @see #isAggregateMode()
     * @since 1.3.0
     */
    void setAggregateMode(boolean aggregateEnabled);

    /**
     * Returns aggregateEnabled property value.
     *
     * @return the value of aggregateEnabled property.
     * @see #setAggregateMode(boolean)
     * @since 1.3.0
     */
    boolean isAggregateMode();

    /**
     * The method is used to set the active property of the tool.
     * If <code>active</code> is <tt>true</tt> then tool becomes available and visibile if not already was.
     * Moreover the tool grabs the focus from focus owner and becomes active.
     * If <code>active</code> is <tt>false</tt> then the focus is passed to another component outer the tool and tool
     * becomes not active.
     *
     * @param active <tt>true</tt> to make the tool active, <tt>false</tt> to deactivate the tool.
     * @see #setAvailable(boolean)
     * @see #setVisible(boolean)
     * @since 1.0.0
     */
    void setActive(boolean active);

    /**
     * Returns <tt>true</tt> is the tool is active.
     *
     * @return <tt>true</tt> is the tool is active, false otherwise.
     * @see #setActive(boolean)
     * @since 1.0.0
     */
    boolean isActive();

    /**
     * This method is used to set the anchor for the tool. The anchor specifies the position of the tool when
     * it is anchored to the docking system.
     * The behaviour is equivalent to a call of the method <code>setAnchor(anchor, -1)</code>.
     *
     * @param anchor the new anchor.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @see #getAnchor()
     * @since 1.0.0
     */
    void setAnchor(ToolWindowAnchor anchor);

    /**
     * This method is used to set the anchor for the tool. The anchor specifies the position of the tool when
     * it is anchored to the docking system. The index specifies the position relative to the other tools on
     * the same anchor.
     *
     * @param anchor the new anchor.
     * @param index  the position relative to the other tools on the same anchor.
     *               Use 0 for the first position, -1 for the last position.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @see #getAnchor()
     * @since 1.3.0
     */
    void setAnchor(ToolWindowAnchor anchor, int index);

    /**
     * Returns the anchor which the tool is anchored.
     *
     * @return the anchor for the tool.
     * @see #setAnchor(ToolWindowAnchor)                                               s
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @since 1.0.0
     */
    ToolWindowAnchor getAnchor();

    /**
     * Returns the anchor index.
     *
     * @return anchor index.
     * @since 1.4.0
     */
    int getAnchorIndex();

    /**
     * This method is used to set the autoHide property for the tool.
     *
     * @param autoHide <code>true</code> to hide the tool when the tool losts focus;
     *                 <code>false</code> to make inactive the tool when the tool losts focus.
     * @see #isAutoHide() ()
     * @since 1.0.0
     */
    void setAutoHide(boolean autoHide);

    /**
     * Bounds or not the toolwindow's representative anchro to stay in a subset of the available anchors.
     *
     * @param lockedOnAnchor <code>true</code> if the toolwindow's representative anchor is bonded to a subset of the available anchors,
     * <code>false</code> otherwise.
     * @since 1.5.0
     */
    void setLockedOnAnchor(boolean lockedOnAnchor);

    /**
     * Returns <code>true</code> if the toolwindow's representative anchor is bonded to a subset of the available anchors,
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> if the toolwindow's representative anchor is bonded to a subset of the available anchors,
     * <code>false</code> otherwise.
     * @since 1.5.0
     */
    boolean isLockedOnAnchor();

    /**
     * Returns the autoHide property value of the tool.
     *
     * @return autoHide property value.
     * @see #setAutoHide(boolean)
     * @since 1.0.0
     */
    boolean isAutoHide();

    /**
     * This method is used to set the type for the tool. The type specifies the way the tool is shown, made available,
     * etc.
     *
     * @param type the new type.
     * @see ToolWindowType
     * @see #getType()
     * @since 1.0.0
     */
    void setType(ToolWindowType type);

    /**
     * Returns the tool type.
     *
     * @return the type for the tool.
     * @see #setType(ToolWindowType)
     * @see ToolWindowType
     * @since 1.0.0
     */
    ToolWindowType getType();

    /**
     * Sets the "hideOnZeroTabs" property. When <code>true</code> is used this means that
     * the tool is hidden when no tabs are available on it.
     *
     * @param hideOnZeroTabs <code>true</code> to hide the tool when no tabs are available on it,
     * <code>false</code> otherwise.
     * @since 1.5.0
     * @see #isHideOnZeroTabs()
     */
    void setHideOnZeroTabs(boolean hideOnZeroTabs);

    /**
     * Return the "hideOnZeroTabs" property value.
     * @since 1.5.0
     * @return <code>true</code> if the the tool is hidden when no tabs are available on it, 
     * <code>false</code> otherwise.
     * @see #setHideOnZeroTabs(boolean)  
     */
    boolean isHideOnZeroTabs();

    /**
     * Adds a <code>component</code> represented by a <code>title</code> and no icon.
     *
     * @param title     the title to be displayed in this tab
     * @param component the component to be displayed when this tab is clicked
     * @return a ToolWindowTab instance
     * @since 1.3.0
     */
    ToolWindowTab addToolWindowTab(String title, Component component);

    /**
     * Adds a <code>dockable</code>. A toolwindow tab is created to accommodate the dockable.
     *
     * @param dockable the dockable to be accommodated.
     * @return a <code>ToolWindowTab</code> instance that represents the accommodated dockable.
     * @since 1.4.0
     */
    ToolWindowTab addToolWindowTab(Dockable dockable);

    /**
     * Removes the specified tab from this toolwindow.
     *
     * @param toolWindowTab the tab to be removed
     * @return <tt>true</tt> if this toolwindow contained the specified tab.
     * @see #addToolWindowTab(String, java.awt.Component)
     * @since 1.3.0
     */
    boolean removeToolWindowTab(ToolWindowTab toolWindowTab);

    /**
     * Gets all the tabs in this toolwindow.
     *
     * @return an array of all the tabs in this toolwindow.
     * @since 1.3.0
     */
    ToolWindowTab[] getToolWindowTabs();

    /**
     * Adds the passed toolwindow action to all the type descriptors.
     *
     * @param toolWindowAction the action to be added to all the type desccriptors.
     * @since 1.5.0
     * @see #removeToolWindowAction(String)
     */
    void addToolWindowAction(ToolWindowAction toolWindowAction);

    /**
     * Removes the toolwindow action by id from all the type descriptors.
     *
     * @param id the id of the action to be removed from all the type descriptors.
     * @since 1.5.0
     * @see #addToolWindowAction(ToolWindowAction) 
     */
    void removeToolWindowAction(String id);

    /**
     * Adds the specified toolwindow listener to receive toolwindow events from
     * this tool.
     * If listener <code>l</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the toolwindow listener
     * @see ToolWindowListener
     * @see #removeToolWindowListener(ToolWindowListener)
     * @see #getToolWindowListeners()
     * @since 1.3.0
     */
    void addToolWindowListener(ToolWindowListener listener);

    /**
     * Removes the specified toolwindow listener so that it no longer
     * receives toolwindow events from this tool. This method performs
     * no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this tool.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the toolwindow listener
     * @see #addToolWindowListener(ToolWindowListener)
     * @since 1.3.0
     */
    void removeToolWindowListener(ToolWindowListener listener);

    /**
     * Returns an array of all the toolwindow listeners registered on this tool.
     *
     * @return all of this toolwindowt's <code>ToolWindowListener</code>s
     *         or an empty array if no toolwindow listeners are currently registered
     * @see #addToolWindowListener(ToolWindowListener)
     * @since 1.3.0
     */
    ToolWindowListener[] getToolWindowListeners();

    /**
     * This method retrieves the TypeDescriptor for <code>type</code> that the tool uses to modify the behaviours
     * of that type. The modifications are visible only for this tool.
     *
     * @param type tool window type.
     * @return the type descriptor for <code>type</code>.
     * @since 1.0.0
     */
    ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type);

    /**
     * This method retrieves the TypeDescriptor for the <code>descriptorClass</code>.
     *
     * @param descriptorClass the descriptor class.
     * @return the type descriptor for the <code>descriptorClass</code>.
     * @since 1.4.0
     */
    <T extends ToolWindowTypeDescriptor> T getTypeDescriptor(Class<T> descriptorClass);

    /**
     * Returns the representative anchor descriptor used to modify the behaviours of that object.
     *
     * @return the representative anchor descriptor used to modify the behaviours of that object.
     * @since 1.5.0
     */
    RepresentativeAnchorDescriptor<ToolWindow> getRepresentativeAnchorDescriptor();
}
