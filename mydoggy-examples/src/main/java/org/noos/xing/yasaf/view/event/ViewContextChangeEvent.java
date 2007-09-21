package org.noos.xing.yasaf.view.event;

import org.noos.xing.yasaf.view.ViewContext;

public class ViewContextChangeEvent extends java.util.EventObject {
    private ViewContext viewContext;
    private Object property;
    private Object newValue;
    private Object oldValue;

    public ViewContextChangeEvent(Object source, ViewContext viewContext,
                                  Object property, Object oldValue, Object newValue) {
        super(source);
        this.viewContext = viewContext;
        this.property = property;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }


    public ViewContext getViewContext() {
        return viewContext;
    }

    public Object getProperty() {
        return property;
    }

    public Object getNewValue() {
        return newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

}