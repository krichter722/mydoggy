package org.noos.xing.mydoggy.plaf;

import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface PlafObservable {


    void addPlafPropertyChangeListener(PropertyChangeListener listener);

    void removePlafPropertyChangeListener(PropertyChangeListener listener);

	PropertyChangeListener[] getPlafPropertyChangeListeners();


    void addPlafPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePlafPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    PropertyChangeListener[] getPlafPropertyChangeListeners(String propertyName);


}
