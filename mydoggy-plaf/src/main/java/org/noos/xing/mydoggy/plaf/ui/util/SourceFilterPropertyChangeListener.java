package org.noos.xing.mydoggy.plaf.ui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SourceFilterPropertyChangeListener implements PropertyChangeListener {
    protected PropertyChangeListener propertyChangeListener;
    protected Class sourceClass;

    public SourceFilterPropertyChangeListener(PropertyChangeListener propertyChangeListener, Class sourceClass) {
        this.propertyChangeListener = propertyChangeListener;
        this.sourceClass = sourceClass;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != null && sourceClass.isAssignableFrom(evt.getSource().getClass()))
            propertyChangeListener.propertyChange(evt);
    }
}
