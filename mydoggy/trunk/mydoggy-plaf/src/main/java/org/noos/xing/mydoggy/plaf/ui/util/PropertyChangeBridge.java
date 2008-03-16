package org.noos.xing.mydoggy.plaf.ui.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PropertyChangeBridge implements PropertyChangeListener {
    protected PropertyChangeSupport bridgePropertyChangeSupport;

    public PropertyChangeBridge() {
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (bridgePropertyChangeSupport != null)
            bridgePropertyChangeSupport.firePropertyChange(evt);
    }

    public void addBridgePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (bridgePropertyChangeSupport == null)
            bridgePropertyChangeSupport = new PropertyChangeSupport(this);
        bridgePropertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removeBridgePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (bridgePropertyChangeSupport == null)
            bridgePropertyChangeSupport = new PropertyChangeSupport(this);
        bridgePropertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getBridgePropertyChangeListeners(String propertyName) {
        if (bridgePropertyChangeSupport == null)
            bridgePropertyChangeSupport = new PropertyChangeSupport(this);
        return bridgePropertyChangeSupport.getPropertyChangeListeners(propertyName);
    }
}
