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
 * @since 1.0.0
 */
public interface ContentManager extends DockableManager<Content>, Observable {

    /**
     * Sets the content manager ui to <code>contentManagerUI</code>.
     *
     * @param contentManagerUI the content manager ui.
     * @see #getContentManagerUI()
     * @see org.noos.xing.mydoggy.ContentManagerUI
     * @since 1.1.0
     */
    void setContentManagerUI(ContentManagerUI contentManagerUI);

    /**
     * Returns the content manager ui.
     *
     * @return the content manager ui.
     * @see org.noos.xing.mydoggy.ContentManagerUI
     * @since 1.1.0
     */
    ContentManagerUI getContentManagerUI();

    /**
     * Returns the number of contents in this manager.
     *
     * @return the number of contents.
     * @since 1.0.0
     */
    int getContentCount();

    /**
     * Adds a <code>component</code> represented by a unique identifier <code>id</code>
     * with a <code>title</code> and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param id        the content id. It must be unique.
     * @param title     the title to be displayed in this content
     * @param icon      the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected. @return the a <code>Content</code> object rapresents the content added. @see #removeContent(Content)
     * @return a <code>Content</code> instance that rapresents the <code>component</code>
     * @see Content
     * @since 1.0.0
     */
    Content addContent(String id, String title, Icon icon, Component component);

    /**
     * Adds a <code>component</code> represented by a unique identifier <code>id</code>
     * with a <code>title</code> and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param id        the content id. It must be unique.
     * @param title     the title to be displayed in this content
     * @param icon      the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @param tip       the tool tip text @return the a <code>Content</code> object rapresents the content added. @see #removeContent(Content)
     * @return a <code>Content</code> instance that rapresents the <code>component</code>
     * @since 1.0.0
     */
    Content addContent(String id, String title, Icon icon, Component component, String tip);

    /**
     * Adds a <code>component</code> represented by a unique identifier <code>id</code>
     * with a <code>title</code> and/or <code>icon</code>, either of which can be <code>null</code>.
     *
     * @param id        the content id. It must be unique.
     * @param title     the title to be displayed in this content
     * @param icon      the icon to be displayed in this content
     * @param component the component to be displayed when this content is selected.
     * @param tip       the tool tip text @return the a <code>Content</code> object rapresents the content added. @see #removeContent(Content)
     * @param constraints a constraints used by the underlining ContentManagerUI. For example a location for the DesktopContentManagerUI.
     * @return a <code>Content</code> instance that rapresents the <code>component</code>
     * @since 1.4.0
     */
    Content addContent(String id, String title, Icon icon, Component component, String tip, Object... constraints);

    /**
     * Adds a <code>dockable</code>. A content is created to accommodate the dockable.
     *
     * @param dockable the dockable to be accommodated.
     * @return a <code>Content</code> instance that represents the accommodated dockable.
     * @since 1.4.0
     */
    Content addContent(Dockable dockable);

    /**
     * Removes the content <code>content</code>.
     *
     * @param content content to be removed from this manager, if present.
     * @return <tt>true</tt> if this manager contained the specified element.
     * @see #addContent(String, String,javax.swing.Icon,java.awt.Component)
     * @see #addContent(String, String,javax.swing.Icon,java.awt.Component, String)
     * @since 1.0.0
     */
    boolean removeContent(Content content);

    /**
     * Removes the content at the specified position in this manager
     *
     * @param index the index of the element to removed.
     * @return <tt>true</tt> if this manager contained the specified element.
     * @throws IndexOutOfBoundsException if the index is out of range (index &lt; 0 || index &gt;= getContentCount()).
     * @since 1.0.0
     */
    boolean removeContent(int index);

    /**
     * Removes all of the contents from this manager.
     *
     * @since 1.1.0
     */
    void removeAllContents();

    /**
     * Returns the content at the specified position in this manager.
     *
     * @param index index of content to return.
     * @return the content at the specified position in this manager.
     * @throws IndexOutOfBoundsException if the index is out of range (index
     *                                   &lt; 0 || index &gt;= getContentCount()).
     * @since 1.0.0
     */
    Content getContent(int index);

    /**
     * Returns an array containing all of the contents in this manager in proper
     * sequence.
     *
     * @return an array containing all of the contents in this list in proper sequence.
     * @see #getContent(int)
     * @since 1.0.0
     */
    Content[] getContents();

    /**
     * Returns the content to which this manager maps the specified key (the key could be the id or
     * an alias). Returns <tt>null</tt> if the manager contains no mapping for this key.
     *
     * @param key key whose associated content is to be returned.
     * @return the content to which this manager maps the specified key
     * @throws NullPointerException if the key is <tt>null</tt>.
     * @since 1.0.0
     */
    Content getContent(Object key);

    /**
     * Returns the content that wraps the passed <code>component</code>
     *
     * @param component the component wrapped by a content.
     * @return the content that wraps the passed <code>component</code>
     * @since 1.2.0
     */
    Content getContentByComponent(Component component);

    /**
     * Returns the selected content, or <code>null</code> if the
     * selection is empty.
     *
     * @return the selected content.
     * @since 1.2.0
     */
    Content getSelectedContent();

    /**
     * Returns in order the next enabled content related to the selected content, or <code>null</code> if no
     * contents are registered or no content is enabled.
     *
     * @return the next enabled content related to the selected content.
     * @since 1.3.1
     */
    Content getNextContent();

    /**
     * Returns in order the previous enabled content related to the selected content, or <code>null</code> if no
     * contents are registered or no content is enabled.
     *
     * @return the previous enabled content related to the selected content.
     * @since 1.3.1
     */
    Content getPreviousContent();

    /**
     * Sets the default popup menu for the contents.
     * If a content has no specific popup menu then the content manager will show
     * <code>popupMenu</code>.
     *
     * @param popupMenu the default popup menu for the contents.
     * @since 1.0.0
     */
    void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Returns the default popup menu for the contents.
     *
     * @return the default <code>PopupMenu</code> for the contents.
     * @see #setPopupMenu(javax.swing.JPopupMenu)
     * @since 1.0.0
     */
    JPopupMenu getPopupMenu();

    /**
     * Enables or disables the content manager. If the content manager is disable the whole space
     * will be available for toolwindows.
     *
     * @param enabled <tt>true</tt> to enable the content manager, <tt>false</tt> otherwise.
     * @since 1.4.2
     */
    void setEnabled(boolean enabled);

    /**
     * Returns if the content manager is enabled.
     *
     * @return <tt>true</tt> if the content manager is enabled, <tt>false</tt> otherwise.
     */
    boolean isEnabled();


    /**
     * Registers <code>listener</code> so that it will receive events when
     * contents are registered or removed..
     * If listener <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is performed.
     *
     * @param listener the <code>ContentManagerListener</code> to register.
     * @see ContentManagerListener
     * @since 1.0.0
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
     * @since 1.0.0
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
     * @since 1.0.0
     */
    ContentManagerListener[] getContentManagerListeners();

}

