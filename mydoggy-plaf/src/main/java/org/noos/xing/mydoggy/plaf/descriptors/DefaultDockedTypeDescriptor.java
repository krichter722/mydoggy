package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ResourceBoundles;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultDockedTypeDescriptor implements DockedTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {

    private EventListenerList listenerList;
    private boolean popupMenuEnabled;
    private JMenu menu;
    private int dockLength;

    public DefaultDockedTypeDescriptor() {
        this.menu = new JMenu(ResourceBoundles.getResourceBoundle().getString("@@tool.userDefined"));
        this.popupMenuEnabled = true;
        this.dockLength = 200;
        this.listenerList = new EventListenerList();
    }

    public DefaultDockedTypeDescriptor(DefaultDockedTypeDescriptor parent, int dockLength, boolean popupMenuEnabled) {
        this.menu = new JMenu(ResourceBoundles.getResourceBoundle().getString("@@tool.userDefined"));
        this.popupMenuEnabled = popupMenuEnabled;
        this.dockLength = dockLength;
        parent.addPropertyChangeListener(this);
    }

    public void setPopupMenuEnabled(boolean enabled) {
        boolean old = this.popupMenuEnabled;
        this.popupMenuEnabled = enabled;
        firePropertyChange("popupMenuEnabled", old, enabled);
    }

    public boolean isPopupMenuEnabled() {
        return popupMenuEnabled;
    }

    public JMenu getUserDefinedMenu() {
        return menu;
    }

    public int getDockLength() {
        return dockLength;
    }

    public void setDockLength(int dockLength) {
        int old = this.dockLength;
        this.dockLength = dockLength;
        firePropertyChange("dockLength", old, dockLength);
    }

    public ToolWindowTypeDescriptor cloneMe() {
        return new DefaultDockedTypeDescriptor(this, dockLength, popupMenuEnabled);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("popupMenuEnabled".equals(evt.getPropertyName())) {
            this.popupMenuEnabled = (Boolean) evt.getNewValue();
        } else if ("dockLength".equals(evt.getPropertyName())) {
            this.dockLength = (Integer) evt.getNewValue();
        }
    }


    private void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        listenerList.add(PropertyChangeListener.class, propertyChangeListener);
    }

    private void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (listenerList != null) {
            PropertyChangeListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);
            PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            for (PropertyChangeListener listener : listeners) {
                listener.propertyChange(event);
            }
        }
    }

}
