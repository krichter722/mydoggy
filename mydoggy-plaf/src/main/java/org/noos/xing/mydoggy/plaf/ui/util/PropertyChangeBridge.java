package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.plaf.ui.util.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.util.cleaner.CleanerPropertyChangeSupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PropertyChangeBridge implements PropertyChangeListener, Cleaner {
    protected CleanerPropertyChangeSupport bridgePropertyChangeSupport;

    public PropertyChangeBridge() {
    }

    public void cleanup() {
        if (bridgePropertyChangeSupport != null)
            bridgePropertyChangeSupport.cleanup();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (bridgePropertyChangeSupport != null)
            bridgePropertyChangeSupport.firePropertyChange(evt);
    }

    public void addBridgePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (bridgePropertyChangeSupport == null)
            bridgePropertyChangeSupport = new CleanerPropertyChangeSupport(this);
        bridgePropertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removeBridgePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (bridgePropertyChangeSupport == null)
            bridgePropertyChangeSupport = new CleanerPropertyChangeSupport(this);
        bridgePropertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getBridgePropertyChangeListeners(String propertyName) {
        if (bridgePropertyChangeSupport == null)
            bridgePropertyChangeSupport = new CleanerPropertyChangeSupport(this);
        return bridgePropertyChangeSupport.getPropertyChangeListeners(propertyName);
    }
}
