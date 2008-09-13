package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListener;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RemoveNotifyDragListener implements PropertyChangeListener, Cleaner {
    protected Component[] components;
    protected DragListener dragListener;


    public RemoveNotifyDragListener(Component components, DragListener dragListener) {
        this.components = new Component[]{components};
        this.dragListener = dragListener;

        installListeners();
    }

    public RemoveNotifyDragListener(DragListener dragListener, Component... components) {
        this.components = components;
        this.dragListener = dragListener;

        installListeners();
    }



    public void cleanup() {
        uninstallListeners();

        this.components = null;
        this.dragListener = null;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getNewValue() == null) {
            uninstallListeners();
        } else
            installListeners();
    }


    protected void installListeners() {
        if (components == null)
            return;

        for (Component component : components) {
            SwingUtil.registerDragListener(component, dragListener);
        }
    }

    protected void uninstallListeners() {
        if (components == null)
            return;

        for (Component component : components) {
            SwingUtil.unregisterDragListener(component);
        }
    }

}
