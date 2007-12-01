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
 * @todo: modify javadocs..
 */
public interface Observable
{
	/**
	 * Adds a PropertyChangeListener to the listener list. The listener is
	 * registered for all bound properties of this class, including the
	 * following:
	 * <ul>
	 * <li>this sliding's transparentMode ("transparentMode")</li>
	 * <li>this sliding's transparentRatio ("transparentRatio")</li>
	 * <li>this sliding's transparentDelay ("transparentDelay")</li>
	 * <li>this sliding's enabled property ("enabled")</li>
	 * </ul>
	 * <p/>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param listener the PropertyChangeListener to be added
	 *
	 * @see #getPropertyChangeListeners()
	 * @see #removePropertyChangeListener
	 * @since 1.2.0
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * <p/>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param listener the PropertyChangeListener to be removed.
	 *
	 * @see #addPropertyChangeListener
	 * @see #getPropertyChangeListeners
	 * @since 1.2.0
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Returns an array of all the property change listeners
	 * registered on this descritpro.
	 *
	 * @return all of this descriptor's <code>PropertyChangeListener</code>s
	 *         or an empty array if no property change
	 *         listeners are currently registered.
	 *
	 * @see #addPropertyChangeListener
	 * @see #removePropertyChangeListener
	 * @since 1.2.0
	 */
	PropertyChangeListener[] getPropertyChangeListeners();
}
