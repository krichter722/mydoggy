package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;

import java.beans.PropertyChangeListener;


/**
 * @author Angelo De Caro
 * TODO: check usage...
 */
public interface ToolWindowContainer extends PropertyChangeListener, Cleaner {

    void addPropertyChangeListener(String property, PropertyChangeListener listener);

    void updateUI();

}
