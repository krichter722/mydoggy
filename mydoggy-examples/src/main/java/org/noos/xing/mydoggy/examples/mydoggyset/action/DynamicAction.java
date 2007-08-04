package org.noos.xing.mydoggy.examples.mydoggyset.action;

import org.noos.xing.mydoggy.examples.mydoggyset.signal.SignalListener;
import org.noos.xing.mydoggy.examples.mydoggyset.signal.SignalManager;
import org.noos.xing.mydoggy.examples.mydoggyset.signal.SignalEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DynamicAction extends AbstractAction implements SignalListener {
    private Method method;
    private Object target;
    private Source source;

    public DynamicAction(Class targetClass, String property, Source source) {
        SignalManager.getInstance().addSignalListener(targetClass, this);
        this.source = source;
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(targetClass).getPropertyDescriptors()) {
                if (property.equals(propertyDescriptor.getName())) {
                    method = propertyDescriptor.getWriteMethod();
                    break;
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSignalEvent(String signal, SignalEvent event) {
        this.target = event.getMessage();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            method.invoke(target, source.getSource());
        } catch (IllegalAccessException e1) {
            throw new RuntimeException(e1);
        } catch (InvocationTargetException e1) {
            throw new RuntimeException(e1);
        }
    }
}
