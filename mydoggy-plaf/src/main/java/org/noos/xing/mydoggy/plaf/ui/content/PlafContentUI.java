package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;

import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface PlafContentUI extends Content {

    /**
     * Adds a PropertyChangeListener to the ui listener list. The listener is
     * registered for all bound properties of this class and Content class.
     * If listener is null, no exception is thrown and no action is performed.
     * All event fired are recevied by this listener before the listeners registered
     * using the Content interface.
     *
     * @param listener the PropertyChangeListener to be added.
     * @see #removeUIPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    void addUIPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Removes a PropertyChangeListener from the ui listener list. This method
     * should be used to remove PropertyChangeListeners that were registered
     * for all bound properties of this class.
     * <p/>
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param listener the PropertyChangeListener to be removed
     * @see #addUIPropertyChangeListener(java.beans.PropertyChangeListener) 
     */
    void removeUIPropertyChangeListener(PropertyChangeListener listener);

    /**
     *
     * @param selected
     */
    void fireSelected(boolean selected);
    
}
