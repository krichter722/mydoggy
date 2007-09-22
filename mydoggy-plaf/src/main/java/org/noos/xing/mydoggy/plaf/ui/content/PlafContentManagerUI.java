package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.1
 */
public interface PlafContentManagerUI {

    /**
     * Configures this manager appropriate for the specified toolwindow manager.
     *
     * @param manager the component where this UI delegate is being installed
     * @param oldContentManagerUI
     * @see #unistall()
     * @since 1.3.1
     * @return
     */
    PlafContentManagerUI install(ContentManagerUI oldContentManagerUI, ToolWindowManager manager);

    /**
     * Reverses configuration which was done during <code>install(...)</code>.
     *
     * @see #install
     * @since 1.3.1
     */
    void unistall();

    /**
     * Returns <tt>true</tt> is the manager is installed, <tt>false</tt> otherwise.
     * 
     * @return <tt>true</tt> is the manager is installed, <tt>false</tt> otherwise.
     * @since 1.3.1
     */
    boolean isInstalled();

    /**
     * Adds the ui part of a content.
     *
     * @param content the content ui part to be added.
     * @since 1.3.1
     */
    void addContent(PlafContentUI content);

    /**
     * Removes the ui part of a content.
     *
     * @param content the content ui part to be removed.
     * @since 1.3.1
     */
    void removeContent(PlafContentUI content);

    /**
     * Returns whether or not the specified content is currently selected.
     *
     * @param content on which check the selection state.
     * @return true if the content is selected; false otherwise
     * @since 1.3.1
     */
    boolean isSelected(Content content);

    /**
     * Sets whether or not the content is selected.
     *
     * @param content on which change the selection state.
     * @param selected whether or not the content should be selected.
     * @since 1.3.1
     */
    void setSelected(Content content, boolean selected);

    /**
     * Sets the default popup menu for the contents.
     * If a content has no specific popup menu then the content manager will show
     * <code>popupMenu</code>.
     *
     * @param popupMenu the default popup menu for the contents.
     * @since 1.3.1
     */
    void setPopupMenu(JPopupMenu popupMenu);

    /**
     * Returns the default popup menu for the contents.
     *
     * @return the default <code>PopupMenu</code> for the contents.
     * @since 1.3.1
     */
    JPopupMenu getPopupMenu();

    /**
     * Calls the updateUI method on all components used by this manager.
     *
     * @since 1.3.1
     */
    void updateUI();

}
