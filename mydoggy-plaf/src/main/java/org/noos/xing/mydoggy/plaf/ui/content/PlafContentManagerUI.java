package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo add javadocs
 */
public interface PlafContentManagerUI {

    Container getContainer();


    void install(ToolWindowManager manager);

    void unistall();


    void addContent(PlafContentUI content);

    void removeContent(PlafContentUI content);

    /**
     * Returns whether or not the specified content is currently selected.
     *
     * @param content on which check the selection state.
     * @return true if the content is selected; false otherwise
     */
    boolean isSelected(Content content);

    /**
     * Sets whether or not the content is selected.
     *
     * @param content on which change the selection state.
     * @param selected whether or not the content should be selected.
     */
    void setSelected(Content content, boolean selected);

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
     */
    JPopupMenu getPopupMenu();

    /**
     * Calls the updateUI method on all components used by this manager.
     */
    void updateUI();
}
