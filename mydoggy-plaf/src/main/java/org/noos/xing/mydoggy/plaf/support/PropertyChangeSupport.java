package org.noos.xing.mydoggy.plaf.support;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PropertyChangeSupport {
    private Map<String, List<PropertyChangeListener>> propertyChangeListeners;

    public PropertyChangeSupport() {
        this.propertyChangeListeners = new ResolvableHashtable<String, List<PropertyChangeListener>>(
                new ResolvableHashtable.Resolver<List<PropertyChangeListener>>() {
                    public List<PropertyChangeListener> get(Object key) {
                        List<PropertyChangeListener> result = new LinkedList<PropertyChangeListener>();
                        propertyChangeListeners.put((String) key, result);

                        return result;
                    }
                }
        );
    }

    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        List<PropertyChangeListener> listeners = propertyChangeListeners.get(property);
        listeners.add(listener);
    }

    public void removePropertyChangeListener(String property, PropertyChangeListener listener) {
        List<PropertyChangeListener> listeners = propertyChangeListeners.get(property);
        listeners.remove(listener);
    }

    public void firePropertyChangeEvent(PropertyChangeEvent event) {
        List<PropertyChangeListener> listeners = propertyChangeListeners.get(event.getPropertyName());
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

}
