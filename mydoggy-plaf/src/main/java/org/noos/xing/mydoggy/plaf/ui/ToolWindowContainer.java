package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.util.cleaner.Cleaner;

import java.awt.*;
import java.beans.PropertyChangeListener;


/**
 * @author Angelo De Caro
 */
public interface ToolWindowContainer extends PropertyChangeListener, Cleaner {

    ResourceManager getResourceManager();

    void addPropertyChangeListener(String property, PropertyChangeListener listener);

    void updateUI();

    void showPopupMenu(Component c, int x, int y);

}
