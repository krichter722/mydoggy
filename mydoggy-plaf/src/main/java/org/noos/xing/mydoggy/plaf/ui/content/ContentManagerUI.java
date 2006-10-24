package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ContentManagerUI {

    void addContent(ContentUI content);

    void removeContent(ContentUI content);

    void detach(Content content);

    void setPopupMenu(JPopupMenu popupMenu);

    JPopupMenu getPopupMenu();

    boolean isSelected(Content content);

    void setSelected(Content content, boolean selected);

    void updateUI();
}
