package org.noos.xing.mydoggy.plaf.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


/**
 * @author Angelo De Caro
 */
public interface ToolWindowContainer extends PropertyChangeListener {

    ResourceManager getResourceManager();

    void addPropertyChangeListener(String property, PropertyChangeListener listener);

    void updateUI();

}
