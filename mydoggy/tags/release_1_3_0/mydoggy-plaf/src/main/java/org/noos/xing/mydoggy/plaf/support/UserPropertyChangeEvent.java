package org.noos.xing.mydoggy.plaf.support;

import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UserPropertyChangeEvent extends PropertyChangeEvent {
    private Object userObject;

    public UserPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue, Object userObject) {
        super(source, propertyName, oldValue, newValue);
        this.userObject = userObject;
    }

    public Object getUserObject() {
        return userObject;
    }
}
