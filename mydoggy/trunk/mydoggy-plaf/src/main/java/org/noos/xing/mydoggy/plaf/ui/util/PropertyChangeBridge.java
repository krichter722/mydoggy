package org.noos.xing.mydoggy.plaf.ui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PropertyChangeBridge extends PropertyChangeSupport implements PropertyChangeListener {

    public PropertyChangeBridge(Object sourceBean) {
        super(sourceBean);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt);
    }
}
