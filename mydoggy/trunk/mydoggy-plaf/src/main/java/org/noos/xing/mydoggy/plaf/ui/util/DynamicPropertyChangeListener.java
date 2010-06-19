package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DynamicPropertyChangeListener implements PropertyChangeListener {
    protected Object source;
    protected Class sourceClass;

    public DynamicPropertyChangeListener(Object source) {
        this.source = source;
        this.sourceClass = source.getClass();
    }

    public DynamicPropertyChangeListener() {
        this.source = this;
        this.sourceClass = this.getClass();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Method method;
        try {
            String propertyName = evt.getPropertyName();
            method = sourceClass.getMethod("on" + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1),
                                           PropertyChangeEvent.class);
        } catch (NoSuchMethodException e) {
            if (UIManager.getBoolean(MyDoggyKeySpace.DEBUG))
                System.err.println("No method for event found. " + evt.getPropertyName() + " - " + this.getClass().getName());
//            e.printStackTrace();
            return;
        }

        if (method != null)
            try {
                method.invoke(source, evt);
            } catch (Exception e) {
                throw new RuntimeException("Invocation Failed. " + evt.getPropertyName() + " - " + method.toGenericString(), e);
            }
    }
}
