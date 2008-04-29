package org.noos.xing.mydoggy;

import java.beans.PropertyChangeListener;

/**
 * This interface contains generic methods concerning {@link java.beans.PropertyChangeListener}s.
 * Implementors gain ability to be observed, which means they can add, remove or get the list
 * of {@link java.beans.PropertyChangeListener]s currently registered.
 *
 * @author David DOLCIMASCOLO
 * @version 1.0 5 nov. 2007 11:40:15
 * @since 1.4.0
 */
public interface Observable {
    /**
     * Adds a PropertyChangeListener to the listener list. The listener is
     * registered for all bound properties of the class.
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be added
     * @see #getPropertyChangeListeners()
     * @see #removePropertyChangeListener
     * @since 1.4.0
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a PropertyChangeListener from the listener list.
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed.
     * @see #addPropertyChangeListener
     * @see #getPropertyChangeListeners
     * @since 1.4.0
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * Returns an array of all the property change listeners
     * registered on this descritpro.
     *
     * @return all of this descriptor's <code>PropertyChangeListener</code>s
     *         or an empty array if no property change
     *         listeners are currently registered.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @since 1.4.0
     */
	PropertyChangeListener[] getPropertyChangeListeners();

    /**
     * Adds a PropertyChangeListener to the listener list for a specific
     * property.
     * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName one of the property names listed above
     * @param listener the property change listener to be added
     * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners(java.lang.String)
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @since 1.4.2
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener
     * list for a specific property. This method should be used to remove
     * <code>PropertyChangeListener</code>s
     * that were registered for a specific bound property.
     * <p>
     * If <code>propertyName</code> or <code>listener</code> is <code>null</code>,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName a valid property name
     * @param listener the PropertyChangeListener to be removed
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners(java.lang.String)
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     * @since 1.4.2
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Returns an array of all the listeners which have been associated
     * with the named property.
     *
     * @param propertyName the property whose associated listeners are to be returned. 
     * @return all of the <code>PropertyChangeListener</code>s associated with
     *         the named property; if no such listeners have been added or
     *         if <code>propertyName</code> is <code>null</code>, an empty
     *         array is returned
     * @see #addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     * @see #getPropertyChangeListeners
     * @since 1.4.2
     */
    PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

}
