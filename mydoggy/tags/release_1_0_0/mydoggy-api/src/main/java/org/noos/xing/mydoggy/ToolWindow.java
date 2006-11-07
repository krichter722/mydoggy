package org.noos.xing.mydoggy;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * Tool Windows are secondary windows within the main window that provide access to and/or
 * support for a particular functionality.
 * This interface is the main entry point to modify tool window properties.
 * Moreover there are methods to make the tool available, visible and active.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see org.noos.xing.mydoggy.ToolWindowManager
 */
public interface ToolWindow {

    /**
     * Returns the id used to register the tool.
     *
     * @return tool id.
     */
    Object getId();

    /**
     * This method is used to set the index for the tool. The index is used to activate or hide
     * the tool with that index when the user uses the key combination ALT-index.
     * Valid indexs are -1 or [1..9] where -1 means no index for the tool.
     *
     * @param index the new index for the tool.
     * @see #getIcon()
     */
    void setIndex(int index);

    /**
     * Returns the tool's index.
     *
     * @return tool's index.
     * @see #setIndex(int)
     */
    int getIndex();

    /**
     * The method is used to set the available property of the tool.
     * If <code>available</code> is <tt>true</tt> then tool becomes available in a way that depends on tool window type.
     * If <code>available</code> is <tt>false</tt> then tool becomes not available in a way that depends on tool window
     * type.
     *
     * @param available <tt>true</tt> to make the tool available, <tt>false</tt> to make the tool not available.
     * @see #setAvailable(boolean)
     * @see #setVisible(boolean)
     */
    void setAvailable(boolean available);

    /**
     * Returns <tt>true</tt> is the tool is available.
     *
     * @return <tt>true</tt> is the tool is available, false otherwise.
     * @see #setAvailable(boolean)
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
     * @see #setAvailable(boolean)
     */
    void setVisible(boolean visible);

    /**
     * Returns <tt>true</tt> is the tool is visible.
     *
     * @return <tt>true</tt> is the tool is vixible, false otherwise.
     * @see #setVisible(boolean)
     */
    boolean isVisible();

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
     */
    void setActive(boolean active);

    /**
     * Returns <tt>true</tt> is the tool is active.
     *
     * @return <tt>true</tt> is the tool is active, false otherwise.
     * @see #setActive(boolean)
     */
    boolean isActive();

    /**
     * This method is used to set the anchor for the tool. The anchor specifies the position of the tool when
     * it is anchored to the docking system.
     *
     * @param anchor the new anchor.
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     * @see #getAnchor()
     */
    void setAnchor(ToolWindowAnchor anchor);

    /**
     * Returns the anchor which the tool is anchored.
     *
     * @return the anchor for the tool.
     * @see #setAnchor(ToolWindowAnchor)
     * @see org.noos.xing.mydoggy.ToolWindowAnchor
     */
    ToolWindowAnchor getAnchor();

    /**
     * This method is used to set the autoHide property for the tool.
     *
     * @param autoHide <code>true</code> to hide the tool when the tool losts focus;
     *                 <code>false</code> to make inactive the tool when the tool losts focus.
     * @see #isAutoHide() ()
     */
    void setAutoHide(boolean autoHide);

    /**
     * Returns the autoHide property value of the tool.
     *
     * @return autoHide property value.
     * @see #setAutoHide(boolean)
     */
    boolean isAutoHide();


    /**
     * This method is used to set the type for the tool. The type specifies the way the tool is showed, made available,
     * etc.
     *
     * @param type the new type.
     * @see ToolWindowType
     * @see #getType()
     */
    void setType(ToolWindowType type);

    /**
     * Returns the tool type.
     *
     * @return the type for the tool.
     * @see #setType(ToolWindowType)
     * @see ToolWindowType
     */
    ToolWindowType getType();

    /**
     * This method is used to set the icon for the tool.
     *
     * @param icon the new icon.
     * @see #getIcon()
     */
    void setIcon(Icon icon);

    /**
     * Returns the tool icon.
     *
     * @return the icon for the tool.
     * @see #setIcon(javax.swing.Icon)
     */
    Icon getIcon();

    /**
     * This method is used to set the title for the tool.
     *
     * @param title the new title.
     * @see #getTitle() ()
     */
    void setTitle(String title);

    /**
     * Returns the tool title.
     *
     * @return the title for the tool.
     * @see #setTitle(String)
     */
    String getTitle();

    /**
     * This method retrieves the TypeDescriptor for <code>type</code> that the tool use to modify the behaviours
     * of the that type. The modifications are visible only for this tool.
     *
     * @param type tool window type.
     * @return the type descriptor for <code>type</code>.
     */
    ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type);

    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of this class, including the
     * following:
     * <ul>
     * <li>this tool's index ("index")</li>
     * <li>this tool's aailable status ("available")</li>
     * <li>this tool's visible status ("visible")</li>
     * <li>this tool's active status ("active")</li>
     * <li>this tool's anchor ("anchor")</li>
     * <li>this tool's autoHide ("autoHide")</li>
     * <li>this tool's type ("type")</li>
     * <li>this tool's icon ("icon")</li>
     * <li>this tool's title ("title")</li>
     * </ul>
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #getPropertyChangeListeners()
     * @see #removePropertyChangeListener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a PropertyChangeListener from the listener list.
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed.
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the property change listeners
     * registered on this tool.
     *
     * @return all of this tool's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    PropertyChangeListener[] getPropertyChangeListeners();

}