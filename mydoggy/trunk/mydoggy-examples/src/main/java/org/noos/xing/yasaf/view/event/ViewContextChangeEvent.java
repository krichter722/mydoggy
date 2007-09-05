package org.noos.xing.yasaf.view.event;

public class ViewContextChangeEvent extends java.util.EventObject {

    private Object property;
    private Object newValue;
    private Object oldValue;

    public ViewContextChangeEvent(Object source,
                                  Object property, Object oldValue, Object newValue) {
        super(source);
        this.property = property;
        this.newValue = newValue;
        this.oldValue = oldValue;
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