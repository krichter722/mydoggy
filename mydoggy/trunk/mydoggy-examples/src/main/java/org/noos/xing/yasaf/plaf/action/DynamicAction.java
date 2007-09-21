package org.noos.xing.yasaf.plaf.action;

import org.noos.xing.yasaf.bean.Invocation;
import org.noos.xing.yasaf.bean.Source;
import org.noos.xing.yasaf.plaf.bean.DefaultInvocation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DynamicAction extends AbstractAction {
    protected Method method;
    protected Invocation invocation;

    public DynamicAction(Class targetClass, String methodName, Invocation invocation) {
        this.invocation = invocation;
        try {
            for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(targetClass).getPropertyDescriptors()) {
                if (methodName.equals(propertyDescriptor.getName())) {
                    method = propertyDescriptor.getWriteMethod();
                    break;
                }
            }

            if (method == null) {
                for (MethodDescriptor methodDescriptor : Introspector.getBeanInfo(targetClass).getMethodDescriptors()) {
                    if (methodName.equals(methodDescriptor.getName())) {
                        method = methodDescriptor.getMethod();
                        break;
                    }
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        if (method == null)
            throw new IllegalArgumentException("Cannot find method named : " + methodName);
    }

    public DynamicAction(Class targetClass, String property, Source target, Source args) {
        this(targetClass, property, new DefaultInvocation(target, args));
    }


    public void actionPerformed(ActionEvent e) {
        try {
            method.invoke(invocation.getTarget(),
                          invocation.getArgs());
        } catch (IllegalAccessException e1) {
            throw new RuntimeException(e1);
        } catch (InvocationTargetException e1) {
            throw new RuntimeException(e1);
        }
    }
}
