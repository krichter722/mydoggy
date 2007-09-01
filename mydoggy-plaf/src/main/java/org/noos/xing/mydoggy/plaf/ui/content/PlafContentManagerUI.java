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


    boolean isSelected(Content content);

    void setSelected(Content content, boolean selected);


	void setPopupMenu(JPopupMenu popupMenu);

    JPopupMenu getPopupMenu();


    void updateUI();
}
