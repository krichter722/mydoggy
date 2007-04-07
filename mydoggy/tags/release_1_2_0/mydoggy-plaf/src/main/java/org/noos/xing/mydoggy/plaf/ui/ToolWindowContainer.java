package org.noos.xing.mydoggy.plaf.ui;

import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public interface ToolWindowContainer extends PropertyChangeListener {

    void updateUI();

    void uninstall();

}
