package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowTab;

import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowTabButton {

    ToolWindowTab getToolWindowTab();

    void removePropertyChangeListener(PropertyChangeListener propertyChangeBridge);
    
}
