package org.noos.xing.mydoggy.plaf.ui.util;

import sun.awt.EventListenerAggregate;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.io.Serializable;
import java.util.*;

/**
 * TODO: update java doc...
 * This is a utility class that can be used by beans that support bound
 * properties.  You can use an instance of this class as a member field
 * of your bean and delegate various work to it.
 * <p/>
 * This class is serializable.  When it is serialized it will save
 * (and restore) any listeners that are themselves serializable.  Any
 * non-serializable listeners will be skipped during serialization.
 */
public class CleanerPropertyChangeSupport implements Serializable, Cleaner {
    protected Hashtable<String, CleanerPropertyChangeSupport> children;
    protected Object source;
    protected EventListenerAggregate listeners;


    public CleanerPropertyChangeSupport(Object sourceBean) {
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

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy proxy =
                    (PropertyChangeListenerProxy) listener;
            // Call two argument add method.
            addPropertyChangeListener(proxy.getPropertyName(),
                                      (PropertyChangeListener) proxy.getListener());
        } else {
            if (listeners == null) {
                listeners = new EventListenerAggregate(PropertyChangeListener.class);
            }
            listeners.add(listener);
        }
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            return;
        }

        if (listener instanceof PropertyChangeListenerProxy) {
            PropertyChangeListenerProxy proxy =
                    (PropertyChangeListenerProxy) listener;
            // Call two argument remove method.
            removePropertyChangeListener(proxy.getPropertyName(),
                                         (PropertyChangeListener) proxy.getListener());
        } else {
            if (listeners == null) {
                return;
            }
            listeners.remove(listener);
        }
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
        List<EventListener> returnList = new ArrayList<EventListener>();

        // Add all the PropertyChangeListeners
        if (listeners != null) {
            returnList.addAll(Arrays.asList(listeners.getListenersInternal()));
        }

        // Add all the PropertyChangeListenerProxys
        if (children != null) {
            Iterator<String> iterator = children.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                CleanerPropertyChangeSupport child = children.get(key);
                PropertyChangeListener[] childListeners = child.getPropertyChangeListeners();
                for (int index = childListeners.length - 1; index >= 0; index--) {
                    returnList.add(new PropertyChangeListenerProxy(key, childListeners[index]));
                }
            }
        }
        return returnList.toArray(new PropertyChangeListener[returnList.size()]);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || propertyName == null) {
            return;
        }
        if (children == null) {
            children = new java.util.Hashtable<String, CleanerPropertyChangeSupport>();
        }
        CleanerPropertyChangeSupport child = children.get(propertyName);
        if (child == null) {
            child = new CleanerPropertyChangeSupport(source);
            children.put(propertyName, child);
        }
        child.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || propertyName == null) {
            return;
        }
        if (children == null) {
            return;
        }
        CleanerPropertyChangeSupport child = children.get(propertyName);
        if (child == null) {
            return;
        }
        child.removePropertyChangeListener(listener);
    }

    public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        ArrayList<PropertyChangeListener> returnList = new ArrayList<PropertyChangeListener>();

        if (children != null && propertyName != null) {
            CleanerPropertyChangeSupport support =
                    children.get(propertyName);
            if (support != null) {
                returnList.addAll(
                        Arrays.asList(support.getPropertyChangeListeners()));
            }
        }
        return (PropertyChangeListener[]) (returnList.toArray(
                new PropertyChangeListener[0]));
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }
        firePropertyChange(new PropertyChangeEvent(source, propertyName,
                                                   oldValue, newValue));
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (oldValue == newValue) {
            return;
        }
        firePropertyChange(propertyName, new Integer(oldValue), new Integer(newValue));
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (oldValue == newValue) {
            return;
        }
        firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    }

    public void firePropertyChange(PropertyChangeEvent evt) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        String propertyName = evt.getPropertyName();
        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
            return;
        }

        if (listeners != null) {
            Object[] list = listeners.getListenersInternal();
            for (int i = 0; i < list.length; i++) {
                PropertyChangeListener target = (PropertyChangeListener) list[i];
                target.propertyChange(evt);
            }
        }

        if (children != null && propertyName != null) {
            CleanerPropertyChangeSupport child = null;
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

    public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        if (oldValue == newValue) {
            return;
        }
        fireIndexedPropertyChange(propertyName, index,
                                  new Integer(oldValue),
                                  new Integer(newValue));
    }

    public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
        if (oldValue == newValue) {
            return;
        }
        fireIndexedPropertyChange(propertyName, index, Boolean.valueOf(oldValue),
                                  Boolean.valueOf(newValue));
    }

    public synchronized boolean hasListeners(String propertyName) {
        if (listeners != null && !listeners.isEmpty()) {
            // there is a generic listener
            return true;
        }
        if (children != null && propertyName != null) {
            CleanerPropertyChangeSupport child = children.get(propertyName);
            if (child != null && child.listeners != null) {
                return !child.listeners.isEmpty();
            }
        }
        return false;
    }

}