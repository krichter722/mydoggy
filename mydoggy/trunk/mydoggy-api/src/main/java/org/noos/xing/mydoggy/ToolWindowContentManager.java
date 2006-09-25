package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * This manager lets the user switch between a group of contents by clicking on a tab with a given title and/or icon.
 * Tabs/components are added to the <code>ToolWindowContentManager</code> by using the
 * <code>addContent</code>.
 * A content is represented by an index corresponding to the position it was added in,
 * where the first content has an index equal to 0 and the last content has an index equal to the content count minus 1.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowContentManager {

    /**
     * Sets whether or not the manager must show a tab for all contents also
     * when there is just one content.
     *
     * @param show whether or not the manager must show a tab for all contents.
     * @see #isShowAlwaysTab()
     */
    void setShowAlwaysTab(boolean show);

    /**
     * Returns whether or not the manager must show a tab for all contents also
     * when there is just one content.
     * Default value is false.
     *
     * @return true if the the manager must show a tab for all contents;
     *         false otherwise
     * @see #setShowAlwaysTab(boolean)
     */
    boolean isShowAlwaysTab();

    /**
     * Returns the number of contents in this manager.
     *
     * @return the number of contents.
     */
    int getContentCount();

    /**
     * Adds a <code>component</code> represented by a <code>title</code>
     * and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param title     the title to be displayed in this content
     * @param icon      the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @see #removeContentAt(int)
     */
    void addContent(String title, Icon icon, Component component);

    /**
     * Adds a <code>component</code> represented by a <code>title</code>
     * and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param title     the title to be displayed in this content
     * @param icon      the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @param tip       the tool tip text
     * @see #removeContentAt(int)
     */
    void addContent(String title, Icon icon, Component component, String tip);

    /**
     * Removes the content at <code>index</code>.
     * After the component associated with <code>index</code> is removed.
     *
     * @param index the index of the content to be removed
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #addContent(String,javax.swing.Icon,java.awt.Component)
     */
    void removeContentAt(int index);

    /**
     * Sets the selected index for this content manager. The index must be
     * a valid content index or -1, which indicates that no content should be selected
     *
     * @param index the index to be selected
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index < -1 || index >= content count)
     * @see #getSelectedContent()
     */
    void setSelectedContent(int index);

    /**
     * Returns the currently selected index for this content manager.
     * Returns -1 if there is no currently selected content.
     *
     * @return the index of the selected content
     * @see #setSelectedContent(int)
     */
    int getSelectedContent();

    /**
     * Sets the title at <code>index</code> to <code>title</code> which
     * can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index the content index where the title should be set.
     * @param title the title to be displayed in the content.
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #getTitleAt(int)
     */
    void setTitleAt(int index, String title);

    /**
     * Returns the content title at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return the title at <code>index</code>
     * @throws IndexOutOfBoundsException if index is out of range (index &lt; 0 || index &gt;= content count)
     * @see #setTitleAt(int,String)
     */
    String getTitleAt(int index);

    /**
     * Sets the icon at <code>index</code> to <code>icon</code> which
     * can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index the content index where the title should be set.
     * @param icon  the icon to be displayed in the content.
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #getIconAt(int)
     */
    void setIconAt(int index, Icon icon);

    /**
     * Returns the content icon at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return the icon at <code>index</code>
     * @throws IndexOutOfBoundsException if index is out of range (index &lt; 0 || index &gt;= content count)
     * @see #setIconAt(int,javax.swing.Icon)
     */
    Icon getIconAt(int index);

    /**
     * Sets the disabled icon at <code>index</code> to <code>icon</code>
     * which can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index        the content index where the disabled icon should be set
     * @param disabledIcon the icon to be displayed in the content when disabled
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #getDisabledIconAt(int)
     */
    void setDisabledIconAt(int index, Icon disabledIcon);

    /**
     * Returns the content disabled icon at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return the icon at <code>index</code>
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #setDisabledIconAt(int,javax.swing.Icon)
     */
    Icon getDisabledIconAt(int index);

    /**
     * Returns the content tooltip text at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return a string containing the tool tip text at <code>index</code>
     * @throws IndexOutOfBoundsException if index is out of range (index &lt; 0 || index &gt;= content count)
     * @see #setToolTipTextAt(int,String)
     */
    String getToolTipTextAt(int index);

    /**
     * Sets the tool tip text at <code>index</code> to <code>toolTipText</code> which
     * can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index       the content index where the title should be set.
     * @param toolTipText the tool tip text to be displayed in the content.
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #getToolTipTextAt(int)
     */
    void setToolTipTextAt(int index, String toolTipText);

    /**
     * Sets whether or not the content at <code>index</code> is enabled.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index   the content index which should be enabled/disabled
     * @param enabled whether or not the content should be enabled
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #isEnabledAt(int)
     */
    void setEnabledAt(int index, boolean enabled);

    /**
     * Returns whether or not the content at <code>index</code> is
     * currently enabled.
     *
     * @param index the index of the item being queried
     * @return true if the content at <code>index</code> is enabled;
     *         false otherwise
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #setEnabledAt(int,boolean)
     */
    boolean isEnabledAt(int index);

    /**
     * Sets the component at <code>index</code> to <code>component</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index     the content index where this component is being placed
     * @param component the component for the content
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #getComponentAt(int)
     */
    void setComponentAt(int index, Component component);

    /**
     * Returns the component at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return the <code>Component</code> at <code>index</code>
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #setComponentAt(int,java.awt.Component)
     */
    Component getComponentAt(int index);

    /**
     * Sets the default popup menu for the contents.
     * If a content has no  specific popupMenu then the content manager will show
     * <code>popupMenu</code>.
     *
     * @param popupMenu the default popup menu for the contents.
     */
    void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Returns the default popup menu for the contents.
     *
     * @return the default <code>PopupMenu</code> for the contents. 
     * @see #setPopupMenu(javax.swing.JPopupMenu)
     */
    JPopupMenu getPopupMenu();

    /**
     * Sets the popup menu at <code>index</code> to <code>popupMenu</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index     the content index where this component is being placed
     * @param popupMenu the popup menu for the content
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #getPopupMenuAt(int)
     */
    void setPopupMenuAt(int index, JPopupMenu popupMenu);

    /**
     * Returns the popup menu at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return the <code>JPopupMenu</code> at <code>index</code>
     * @throws IndexOutOfBoundsException if index is out of range
     *                                   (index &lt; 0 || index &gt;= content count)
     * @see #setComponentAt(int,java.awt.Component)
     */
    JPopupMenu getPopupMenuAt(int index);

}
