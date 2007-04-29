package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowActionHandler;
import org.noos.xing.mydoggy.ToolWindowPainter;
import org.noos.xing.mydoggy.plaf.ui.ResourceBoundles;
import org.noos.xing.mydoggy.plaf.ui.painter.MyDoggyToolWindowPainter;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultDockedTypeDescriptor implements DockedTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {
    private ToolWindowActionHandler toolWindowActionHandler;
    private ToolWindowPainter toolWindowPainter;
    private boolean popupMenuEnabled;
    private JMenu toolsMenu;
    private int dockLength;
    private boolean animating;

    private EventListenerList listenerList;

    public DefaultDockedTypeDescriptor() {
        this.toolsMenu = new JMenu(ResourceBoundles.getResourceBundle().getString("@@tool.toolsMenu"));
        this.popupMenuEnabled = true;
        this.dockLength = 200;
        this.toolWindowActionHandler = null;
        this.animating = true;
        this.toolWindowPainter = new MyDoggyToolWindowPainter();
    }

    public DefaultDockedTypeDescriptor(DefaultDockedTypeDescriptor parent, int dockLength, boolean popupMenuEnabled, ToolWindowActionHandler toolWindowActionHandler, boolean animating, ToolWindowPainter toolWindowPainter) {
        this.toolsMenu = new JMenu(ResourceBoundles.getResourceBundle().getString("@@tool.toolsMenu"));
        this.popupMenuEnabled = popupMenuEnabled;
        this.dockLength = dockLength;
        this.toolWindowActionHandler = toolWindowActionHandler;
        this.toolWindowPainter = toolWindowPainter;
        this.animating = animating;

        this.listenerList = new EventListenerList();

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

    public JMenu getToolsMenu() {
        return toolsMenu;
    }

    public int getDockLength() {
        return dockLength;
    }

    public void setDockLength(int dockLength) {
        int old = this.dockLength;
        this.dockLength = dockLength;

        firePropertyChange("dockLength", old, dockLength);
    }

    public ToolWindowActionHandler getToolWindowActionHandler() {
        return toolWindowActionHandler;
    }

    public void setToolWindowActionHandler(ToolWindowActionHandler toolWindowActionHandler) {
        this.toolWindowActionHandler = toolWindowActionHandler;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        if (this.animating == animating)
            return;
        
        boolean old = this.animating;
        this.animating = animating;
        firePropertyChange("animating", old, animating);
    }

    public ToolWindowPainter getToolWindowPainter() {
        return toolWindowPainter;
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (listenerList == null)
            listenerList = new EventListenerList();
        listenerList.add(PropertyChangeListener.class, propertyChangeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listenerList == null)
            return;
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        if (listenerList == null)
            return new PropertyChangeListener[0];
        return listenerList.getListeners(PropertyChangeListener.class);
    }

    public ToolWindowTypeDescriptor cloneMe() {
        return new DefaultDockedTypeDescriptor(this, dockLength, popupMenuEnabled, toolWindowActionHandler, animating, toolWindowPainter);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("popupMenuEnabled".equals(evt.getPropertyName())) {
            this.popupMenuEnabled = (Boolean) evt.getNewValue();
        } else if ("dockLength".equals(evt.getPropertyName())) {
            this.dockLength = (Integer) evt.getNewValue();
        }
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