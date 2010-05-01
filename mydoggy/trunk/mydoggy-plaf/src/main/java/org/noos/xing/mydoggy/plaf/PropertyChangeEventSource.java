package org.noos.xing.mydoggy.plaf;

import org.noos.common.Question;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.support.CleanablePropertyChangeSupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PropertyChangeEventSource implements Cleaner {
    protected CleanablePropertyChangeSupport publicChangeSupport;
    protected CleanablePropertyChangeSupport plafChangeSupport;
    protected Question<Object, Boolean> firePublicEventQuestion;
    protected boolean publicEvent;


    protected PropertyChangeEventSource() {
        this.publicEvent = true;
    }

    public PropertyChangeEventSource(Question<Object, Boolean> firePublicEventQuestion) {
        this();
        this.firePublicEventQuestion = firePublicEventQuestion;
    }


    public void cleanup() {
        if (publicChangeSupport != null)
            publicChangeSupport.cleanup();
        publicChangeSupport = null;

        if (plafChangeSupport != null)
            plafChangeSupport.cleanup();
        plafChangeSupport = null;

        firePublicEventQuestion = null;
    }


    protected CleanablePropertyChangeSupport initPropertyChangeSupport() {
        return new CleanablePropertyChangeSupport(this);
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (publicChangeSupport == null)
            publicChangeSupport = initPropertyChangeSupport();

        publicChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener, String... excludeProperties) {
        if (listener == null)
            return;

        if (publicChangeSupport == null)
            publicChangeSupport = initPropertyChangeSupport();

        if (excludeProperties != null && excludeProperties.length > 0)
            listener = new ExcludePropertyChangeListener(listener, excludeProperties);

        publicChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (publicChangeSupport == null)
            publicChangeSupport = initPropertyChangeSupport();

        publicChangeSupport.addPropertyChangeListener(propertyName, listener);
    }


    public void addPlafPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (plafChangeSupport == null)
            plafChangeSupport = initPropertyChangeSupport();

        plafChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPlafPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null)
            return;

        if (plafChangeSupport == null)
            plafChangeSupport = initPropertyChangeSupport();

        plafChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void addPlafPropertyChangeListener(PropertyChangeListener listener, String... propertyNames) {
        for (String propertyName : propertyNames) {
            addPlafPropertyChangeListener(propertyName, listener);
        }
    }


    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || publicChangeSupport == null)
            return;

        publicChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || publicChangeSupport == null)
            return;

        publicChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void removePlafPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || plafChangeSupport == null)
            return;

        plafChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePlafPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || plafChangeSupport == null)
            return;

        plafChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void removePlafPropertyChangeListener(PropertyChangeListener listener, String... propertyNames) {
        for (String propertyName : propertyNames) {
            removePlafPropertyChangeListener(propertyName, listener);
        }
    }


    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (publicChangeSupport == null)
            return new PropertyChangeListener[0];

        return publicChangeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        if (publicChangeSupport == null) {
            return new PropertyChangeListener[0];
        }
        return publicChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public PropertyChangeListener[] getPlafPropertyChangeListeners() {
        if (plafChangeSupport == null)
            return new PropertyChangeListener[0];

        return plafChangeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPlafPropertyChangeListeners(String propertyName) {
        if (plafChangeSupport == null)
            return new PropertyChangeListener[0];

        return plafChangeSupport.getPropertyChangeListeners(propertyName);
    }


    protected void firePropertyChangeEvent(PropertyChangeEvent event) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(event);

        if (canFirePublicEvent())
            this.publicChangeSupport.firePropertyChange(event);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(property, oldValue, newValue);

        if (canFirePublicEvent())
            this.publicChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

    protected void firePropertyChangeEvent(String property, Object oldValue, Object newValue, Object userObject) {
        if (this.plafChangeSupport != null)
            this.plafChangeSupport.firePropertyChange(property, oldValue, newValue, userObject);

        if (canFirePublicEvent())
            this.publicChangeSupport.firePropertyChange(property, oldValue, newValue, userObject);
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
        if (this.plafChangeSupport != null && event != null)
            this.plafChangeSupport.firePropertyChange(event);

        if (canFirePublicEvent())
            this.publicChangeSupport.firePropertyChange(pblEvent);
    }

    protected void firePropertyChangeEventPublicListenerOnly(String property, Object oldValue, Object newValue) {
        if (canFirePublicEvent())
            this.publicChangeSupport.firePropertyChange(property, oldValue, newValue);
    }


    private final boolean canFirePublicEvent() {
        return publicEvent && this.publicChangeSupport != null && (firePublicEventQuestion == null || (firePublicEventQuestion != null && firePublicEventQuestion.getAnswer(null)));
    }


    public class ExcludePropertyChangeListener implements PropertyChangeListener {
        protected PropertyChangeListener delegate;
        protected Set<String> excludePropertiesSet;


        public ExcludePropertyChangeListener(PropertyChangeListener delegate, String... excludeProperties) {
            this.delegate = delegate;
            this.excludePropertiesSet = new HashSet<String>(Arrays.asList(excludeProperties));
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!excludePropertiesSet.contains(evt.getPropertyName()))
                delegate.propertyChange(evt);
        }

    }

}
