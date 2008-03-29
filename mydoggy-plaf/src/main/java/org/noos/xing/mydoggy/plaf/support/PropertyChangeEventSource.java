package org.noos.xing.mydoggy.plaf.support;

import org.noos.xing.mydoggy.plaf.ui.util.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.util.CleanerPropertyChangeSupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PropertyChangeEventSource implements Cleaner {
    protected CleanerPropertyChangeSupport publicChangeSupport;
    protected CleanerPropertyChangeSupport plafChangeSupport;
    protected boolean publicEvent;


    protected PropertyChangeEventSource() {
        this.publicEvent = true;
    }


    public void cleanup() {
        if (publicChangeSupport != null)
            publicChangeSupport.cleanup();
        if (plafChangeSupport != null)
            plafChangeSupport.cleanup();
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (publicChangeSupport == null)
            publicChangeSupport = initPropertyChangeSupport();
        
        publicChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || publicChangeSupport == null)
            return;

        publicChangeSupport.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (publicChangeSupport == null)
            return new PropertyChangeListener[0];

        return publicChangeSupport.getPropertyChangeListeners();
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (publicChangeSupport == null)
            publicChangeSupport = initPropertyChangeSupport();

        publicChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || publicChangeSupport == null)
            return;

        publicChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        if (publicChangeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return publicChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public void addPlafPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (plafChangeSupport == null)
            plafChangeSupport = initPropertyChangeSupport();

        plafChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePlafPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || plafChangeSupport == null)
            return;

        plafChangeSupport.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPlafPropertyChangeListeners() {
        if (plafChangeSupport == null)
            return new PropertyChangeListener[0];

        return plafChangeSupport.getPropertyChangeListeners();
    }

    public void addPlafPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (plafChangeSupport == null)
            plafChangeSupport = initPropertyChangeSupport();

        plafChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePlafPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || plafChangeSupport == null)
            return;

        plafChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPlafPropertyChangeListeners(String propertyName) {
        if (plafChangeSupport == null)
            return new PropertyChangeListener[0];
        
        return plafChangeSupport.getPropertyChangeListeners(propertyName);
    }


    protected CleanerPropertyChangeSupport initPropertyChangeSupport() {
        return new CleanerPropertyChangeSupport(this);
    }

    protected void firePropertyChangeEvent(PropertyChangeEvent event) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(event);

        if (publicEvent && this.publicChangeSupport != null)
            this.publicChangeSupport.firePropertyChange(event);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(property, oldValue, newValue);

        if (publicEvent && this.publicChangeSupport != null)
            this.publicChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

    protected void firePlafPropertyChangeEvent(PropertyChangeEvent event) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(event);

    }

    protected void firePlafPropertyChangeEvent(String property, Object oldValue, Object newValue) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

    protected void firePropertyChangeEvent(PropertyChangeEvent event, PropertyChangeEvent pblEvent) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(event);

        if (publicEvent && this.publicChangeSupport != null)
            this.publicChangeSupport.firePropertyChange(pblEvent);
    }

    
}
