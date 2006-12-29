package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface BackContentManagerUI {

    void install(ToolWindowManager manager);

    void unistall();


    void addContent(BackContentUI content);

    void removeContent(BackContentUI content);


    boolean isSelected(Content content);

    void setSelected(Content content, boolean selected);


	void setPopupMenu(JPopupMenu popupMenu);

    JPopupMenu getPopupMenu();


    void updateUI();
}
