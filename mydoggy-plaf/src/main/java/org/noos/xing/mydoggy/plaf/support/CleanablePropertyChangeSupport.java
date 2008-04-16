package org.noos.xing.mydoggy.plaf.support;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import sun.awt.EventListenerAggregate;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.io.Serializable;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class CleanablePropertyChangeSupport implements Serializable, Cleaner {
    protected Hashtable<String, CleanablePropertyChangeSupport> children;
    protected Object source;
    protected EventListenerAggregate listeners;


    public CleanablePropertyChangeSupport(Object sourceBean) {
        if (sourceBean == null) {
            throw new NullPointerException();
        }
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
                listeners = new EventListenerAggregate(PropertyChangeListener.class);

            listeners.add(listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        List<EventListener> returnList = new ArrayList<EventListener>();

        // Add all the PropertyChangeListeners
        if (listeners != null) {
            returnList.addAll(Arrays.asList(listeners.getListenersInternal()));
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
        if (listener == null || propertyName == null) {
            return;
        }
        if (children == null) {
            children = new Hashtable<String, CleanablePropertyChangeSupport>();
        }
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
            CleanablePropertyChangeSupport support =
                    children.get(propertyName);
            if (support != null) {
                returnList.addAll(
                        Arrays.asList(support.getPropertyChangeListeners()));
            }
        }
        return (returnList.toArray(new PropertyChangeListener[returnList.size()]));
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || propertyName == null) {
            return;
        }
        if (children == null) {
            return;
        }
        CleanablePropertyChangeSupport child = children.get(propertyName);
        if (child == null) {
            return;
        }
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

    public void firePropertyChange(PropertyChangeEvent evt) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        String propertyName = evt.getPropertyName();
        if (oldValue != null && newValue != null && oldValue.equals(newValue))
            return;

        if (listeners != null) {
            Object[] listeners = this.listeners.getListenersInternal();
            for (Object listener : listeners) {
                PropertyChangeListener target = (PropertyChangeListener) listener;
                target.propertyChange(evt);
            }
        }

        if (children != null && propertyName != null) {
            CleanablePropertyChangeSupport child;
            child = children.get(propertyName);
            if (child != null) {
                child.firePropertyChange(evt);
            }
        }
    }

    public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        firePropertyChange(new IndexedPropertyChangeEvent
                (source, propertyName, oldValue, newValue, index));
    }


}