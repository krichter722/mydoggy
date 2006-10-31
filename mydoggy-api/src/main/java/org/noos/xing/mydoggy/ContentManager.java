package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * This manager manages the main window. You can add several contents to be displayed
 * in the main window. The user can switch between displayed contents.
 * Contents are added to the <code>ContentManager</code> by using the
 * <code>addContent</code> method.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @see Content
 */
public interface ContentManager {

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
     * @return the a <code>Content</code> object rapresents the content added.
     * @see #removeContent(Content)
     * @see Content
     */
    Content addContent(String title, Icon icon, Component component);

    /**
     * Adds a <code>component</code> represented by a <code>title</code>
     * and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param title     the title to be displayed in this content
     * @param icon      the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @param tip       the tool tip text
     * @return the a <code>Content</code> object rapresents the content added.
     * @see #removeContent(Content)
     */
    Content addContent(String title, Icon icon, Component component, String tip);

    /**
     * Removes the content <code>content</code>.
     *
     * @param content content to be removed from this manager, if present.
     * @return <tt>true</tt> if this manager contained the specified element.
     * @see #addContent(String,javax.swing.Icon,java.awt.Component)
     */
    boolean removeContent(Content content);

    /**
     * Removes the content at the specified position in this manager
     *
     * @param index the index of the element to removed.
     * @return <tt>true</tt> if this manager contained the specified element.
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= getContentCount()).
     */
    boolean removeContent(int index);

    /**
     * Returns the content at the specified position in this manager.
     *
     * @param index index of content to return.
     * @return the content at the specified position in this manager.
     * @throws IndexOutOfBoundsException if the index is out of range (index
     * 		  &lt; 0 || index &gt;= getContentCount()).
     */
    Content getContent(int index);

    /**
     * Returns an array containing all of the contents in this manager in proper
     * sequence.
     *
     * @return an array containing all of the contents in this list in proper sequence.
     * @see #getContent(int)
     */
    Content[] getContents();

    /**
     * Sets the default popup menu for the contents.
     * If a content has no specific popup menu then the content manager will show
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
     * Registers <code>listener</code> so that it will receive events when
     * contents are registered or removed..
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ContentManagerListener</code> to register.
     * @see ContentManagerListener
     */
    void addContentManagerListener(ContentManagerListener listener);

    /**
     * Unregisters <code>listener</code> so that it will no longer receive
     * events. This method performs no function, nor does it throw an exception, if the listener
     * specified by the argument was not previously added to this group.
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ContentManagerListener</code> to be removed
     * @see #addContentManagerListener(ContentManagerListener)
     */
    void removeContentManagerListener(ContentManagerListener listener);

    /**
     * Returns an array of all the content manager listeners
     * registered on this manager.
     *
     * @return all of the group's <code>ContentManagerListener</code>s
     *         or an empty array if no tool window manager listeners are currently registered.
     * @see #addContentManagerListener(ContentManagerListener)
     * @see #removeContentManagerListener(ContentManagerListener)
     */
    ContentManagerListener[] getContentManagerListeners();


}

