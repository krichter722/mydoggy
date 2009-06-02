package org.noos.xing.mydoggy.plaf.support;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class CleanablePropertyChangeSupport implements Serializable, Cleaner {
    protected Hashtable<String, CleanablePropertyChangeSupport> children;
    protected Object source;
    protected List<PropertyChangeListener> listeners;


    public CleanablePropertyChangeSupport(Object sourceBean) {
        if (sourceBean == null)
            throw new NullPointerException();

        source = sourceBean;
    }


    public void cleanup() {
        for (PropertyChangeListener listener : getPropertyChangeListeners()) {
            removePropertyChangeListener(listener);
        }

        if (children != null) {
            for (String property : children.keySet()) {
                children.get(property).cleanup();
            }
        }
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
            // Call two argument add method.
            addPropertyChangeListener(proxy.getPropertyName(), (PropertyChangeListener) proxy.getListener());
        } else {
            if (listeners == null)
                listeners = new ArrayList<PropertyChangeListener>();

            listeners.add(listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        List<PropertyChangeListener> returnList = new ArrayList<PropertyChangeListener>();

        // Add all the PropertyChangeListeners
        if (listeners != null) {
            returnList.addAll(listeners);
        }

        // Add all the PropertyChangeListenerProxys
        if (children != null) {
            for (String key : children.keySet()) {
                CleanablePropertyChangeSupport child = children.get(key);
                PropertyChangeListener[] childListeners = child.getPropertyChangeListeners();
                for (int index = childListeners.length - 1; index >= 0; index--) {
                    returnList.add(new PropertyChangeListenerProxy(key, childListeners[index]));
                }
            }
        }
        return returnList.toArray(new PropertyChangeListener[returnList.size()]);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy proxy = (PropertyChangeListenerProxy) listener;
            // Call two argument remove method.
            removePropertyChangeListener(proxy.getPropertyName(), (PropertyChangeListener) proxy.getListener());
        } else {
            if (listeners == null) {
                return;
            }
            listeners.remove(listener);
        }
    }

    public void removePropertyChangeListeners() {
        for (PropertyChangeListener listener : getPropertyChangeListeners()) {
            removePropertyChangeListener(listener);
        }
    }


    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || propertyName == null)
            return;

        if (children == null)
            children = new Hashtable<String, CleanablePropertyChangeSupport>();

        CleanablePropertyChangeSupport child = children.get(propertyName);
        if (child == null) {
            child = new CleanablePropertyChangeSupport(source);
            children.put(propertyName, child);
        }

        child.addPropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        ArrayList<PropertyChangeListener> returnList = new ArrayList<PropertyChangeListener>();

        if (children != null && propertyName != null) {
            CleanablePropertyChangeSupport support = children.get(propertyName);
            if (support != null)
                returnList.addAll(Arrays.asList(support.getPropertyChangeListeners()));
        }

        return (returnList.toArray(new PropertyChangeListener[returnList.size()]));
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || propertyName == null || children == null)
            return;

        CleanablePropertyChangeSupport child = children.get(propertyName);
        if (child == null)
            return;

        child.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListeners(String propertyName) {
        CleanablePropertyChangeSupport child = children.get(propertyName);
        if (child == null)
            return;

        child.removePropertyChangeListeners();
    }


    public boolean hasListeners(String propertyName) {
        if (listeners != null && !listeners.isEmpty()) {
            // there is a generic listener
            return true;
        }

        if (children != null && propertyName != null) {
            CleanablePropertyChangeSupport child = children.get(propertyName);
            if (child != null && child.listeners != null) {
                return !child.listeners.isEmpty();
            }
        }
        return false;

    }


    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue != null && newValue != null && oldValue.equals(newValue))
            return;

        firePropertyChange(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue, Object userObject) {
        if (oldValue != null && newValue != null && oldValue.equals(newValue))
            return;

        firePropertyChange(new UserPropertyChangeEvent(source, propertyName, oldValue, newValue, userObject));
    }

    public boolean firePropertyChange(PropertyChangeEvent evt) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        String propertyName = evt.getPropertyName();
        if (oldValue != null && newValue != null && oldValue.equals(newValue))
            return false;


        boolean fired = false;
        if (listeners != null) {
            Object[] listeners = this.listeners.toArray();
            for (Object listener : listeners) {
                PropertyChangeListener target = (PropertyChangeListener) listener;

                if (!this.listeners.contains(target))
                    continue;
                
                target.propertyChange(evt);
                fired = true;
            }
        }

        if (children != null && propertyName != null) {
            CleanablePropertyChangeSupport child;
            child = children.get(propertyName);
            if (child != null) {
                if (child.firePropertyChange(evt))
                    fired = true;
            }
        }
        return fired;
    }

    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        firePropertyChange(new IndexedPropertyChangeEvent
                (source, propertyName, oldValue, newValue, index));
    }


}