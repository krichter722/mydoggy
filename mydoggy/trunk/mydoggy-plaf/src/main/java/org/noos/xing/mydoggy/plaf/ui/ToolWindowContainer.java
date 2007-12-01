package org.noos.xing.mydoggy.plaf.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;


/**
 * @author Angelo De Caro
 */
public interface ToolWindowContainer extends PropertyChangeListener {

    ResourceManager getResourceManager();

    void addPropertyChangeListener(String property, PropertyChangeListener listener);

    void updateUI();

    void uninstall();

    void showPopupMenu(Component c, int x, int y);

}
