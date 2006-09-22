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
     *		false otherwise
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
     * @param title the title to be displayed in this content
     * @param icon the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @see #removeContentAt(int)
     */
    void addContent(String title, Icon icon, Component component);

    /**
     * Adds a <code>component</code> represented by a <code>title</code>
     * and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param title the title to be displayed in this content
     * @param icon the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @param tip the tool tip text
     * @see #removeContentAt(int)
     */
    void addContent(String title, Icon icon, Component component, String tip);


    /**
     * Removes the content at <code>index</code>.
     * After the component associated with <code>index</code> is removed.
     *
     * @param index the index of the content to be removed
     * @exception IndexOutOfBoundsException if index is out of range
     *            (index &lt; 0 || index &gt;= content count)
     * @see #addContent(String, javax.swing.Icon, java.awt.Component)
     */
    void removeContentAt(int index);


    /**
     * Returns the content title at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return the title at <code>index</code>
     * @exception IndexOutOfBoundsException if index is out of range (index &lt; 0 || index &gt;= content count)
     * @see #setTitleAt
     */
    String getTitleAt(int index);

    /**
     * Sets the title at <code>index</code> to <code>title</code> which
     * can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index the content index where the title should be set.
     * @param title the title to be displayed in the content.
     * @exception IndexOutOfBoundsException if index is out of range
     *            (index &lt; 0 || index &gt;= content count)
     *
     * @see #getTitleAt
     */
    void setTitleAt(int index, String title);

    /**
     * Returns the content icon at <code>index</code>.
     *
     * @param index  the index of the item being queried
     * @return the icon at <code>index</code>
     * @exception IndexOutOfBoundsException if index is out of range (index &lt; 0 || index &gt;= content count)
     *
     * @see #setIconAt
     */
    Icon getIconAt(int index);

    /**
     * Sets the icon at <code>index</code> to <code>icon</code> which
     * can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index the content index where the title should be set.
     * @param icon the icon to be displayed in the content.
     * @exception IndexOutOfBoundsException if index is out of range
     *            (index &lt; 0 || index &gt;= content count)
     *
     * @see #getIconAt(int)
     */
    void setIconAt(int index, Icon icon);

    Icon getDisabledIconAt(int index);

    void setDisabledIconAt(int index, Icon disabledIcon);

    /**
     * Returns the content tooltip text at <code>index</code>.
     *
     * @param index the index of the item being queried
     * @return a string containing the tool tip text at <code>index</code>
     * @exception IndexOutOfBoundsException if index is out of range (index &lt; 0 || index &gt;= content count)
     *
     * @see #setToolTipTextAt
     */
    String getToolTipTextAt(int index);

    /**
     * Sets the tool tip text at <code>index</code> to <code>toolTipText</code> which
     * can be <code>null</code>.
     * An internal exception is raised if there is no content at that index.
     *
     * @param index the content index where the title should be set.
     * @param toolTipText the tool tip text to be displayed in the content.
     * @exception IndexOutOfBoundsException if index is out of range
     *            (index &lt; 0 || index &gt;= content count)
     *
     * @see #getToolTipTextAt(int)
     */
    void setToolTipTextAt(int index, String toolTipText);

    boolean isEnabledAt(int index);

    void setEnabledAt(int index, boolean enabled);

    Component getComponentAt(int index);

    void setComponentAt(int index, Component component);

}
