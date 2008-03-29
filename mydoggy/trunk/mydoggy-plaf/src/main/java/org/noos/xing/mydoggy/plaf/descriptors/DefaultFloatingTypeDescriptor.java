package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultFloatingTypeDescriptor extends PropertyChangeEventSource implements FloatingTypeDescriptor, PropertyChangeListener, InternalTypeDescriptor {
    private Point location;
    private Dimension size;

    private boolean modal;

    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    private boolean enabled;
    private boolean animating;
    private boolean autoHide;
    private boolean idVisibleOnTitleBar;
    private boolean addToTaskBar;

    public DefaultFloatingTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.7f;
        transparentDelay = 1500;
        modal = false;
        enabled = true;
        animating = true;
        autoHide = false;
        idVisibleOnTitleBar = true;
        addToTaskBar = false;
    }

    public DefaultFloatingTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                         DefaultFloatingTypeDescriptor parent, Point location, Dimension size,
                                         int transparentDelay, float transparentRatio, boolean useTransparentMode,
                                         boolean modal, boolean enabled, boolean animating, boolean autoHide, boolean idVisibleOnTitleBar,
                                         boolean addToTaskBar) {
        this.location = location;
        this.size = size;
        this.transparentDelay = transparentDelay;
        this.transparentRatio = transparentRatio;
        this.transparentMode = useTransparentMode;
        this.modal = modal;
        this.enabled = enabled;
        this.animating = animating;
        this.autoHide = autoHide;
        this.idVisibleOnTitleBar = idVisibleOnTitleBar;
        this.addToTaskBar = addToTaskBar;

        parent.addPropertyChangeListener(this);

        toolWindowDescriptor.getCleaner().addCleaner(this);
    }

    public void setLocation(int x, int y) {
        Point newLocation = new Point(x, y);
        if (location != null && location.equals(newLocation))
            return;

        Point old = this.location;
        this.location = newLocation;

        firePropertyChangeEvent("location", old, location);
    }

    public void setSize(int width, int height) {
        Dimension newSize = new Dimension(width, height);
        if (size != null && size.equals(newSize))
            return;

        Dimension old = this.size;
        this.size = newSize;

        firePropertyChangeEvent("size", old, size);
    }

    public Point getLocation() {
        return location;
    }

    public Dimension getSize() {
        return size;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        if (this.modal == modal)
            return;

        boolean old = this.modal;
        this.modal = modal;

        firePropertyChangeEvent("modal", old, modal);
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        if (transparentRatio < 0.0f || transparentRatio > 1.0f)
            throw new IllegalArgumentException("Invalid transparent ratio. Valid range is [0.0f, 1.0f]. [transparentRatio : " + transparentRatio + "]");

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        firePropertyChangeEvent("transparentRatio", old, transparentRatio);
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        if (this.transparentMode == transparentMode)
            return;

        boolean old = this.transparentMode;
        this.transparentMode = transparentMode;

        firePropertyChangeEvent("transparentMode", old, transparentMode);
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setAddToTaskBar(boolean addToTaskBar) {
        if (this.addToTaskBar == addToTaskBar)
            return;

        boolean old = this.addToTaskBar;
        this.addToTaskBar = addToTaskBar;

        firePropertyChangeEvent("addToTaskBar", old, addToTaskBar);
    }

    public boolean isAddToTaskBar() {
        return addToTaskBar;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;

        boolean old = this.enabled;
        this.enabled = enabled;

        firePropertyChangeEvent("enabled", old, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChangeEvent("transparentDelay", old, transparentDelay);
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        if (this.animating == animating)
            return;

        boolean old = this.animating;
        this.animating = animating;
        firePropertyChangeEvent("animating", old, animating);
    }

    public void setIdVisibleOnTitleBar(boolean idVisibleOnTitleBar) {
        if (this.idVisibleOnTitleBar == idVisibleOnTitleBar)
            return;

        boolean old = this.idVisibleOnTitleBar;
        this.idVisibleOnTitleBar = idVisibleOnTitleBar;
        firePropertyChangeEvent("idVisibleOnTitleBar", old, idVisibleOnTitleBar);
    }

    public boolean isIdVisibleOnTitleBar() {
        return idVisibleOnTitleBar;
    }

    public void setAutoHide(boolean autoHide) {
        boolean old = this.autoHide;
        this.autoHide = autoHide;

        firePropertyChangeEvent("autoHide", old, autoHide);
    }

    public boolean isAutoHide() {
        return autoHide;
    }


    public ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor) {
        return new DefaultFloatingTypeDescriptor(toolWindowDescriptor,
                                                 this, location, size, transparentDelay,
                                                 transparentRatio, transparentMode,
                                                 modal, enabled, animating, autoHide,
                                                 idVisibleOnTitleBar,
                                                 addToTaskBar);
    }

    public void propertyChange(PropertyChangeEvent evt) {        
        if ("location".equals(evt.getPropertyName())) {
            Point p = (Point) evt.getNewValue();
            setLocation(p.x, p.y);
        } else if ("size".equals(evt.getPropertyName())) {
            Dimension d = (Dimension) evt.getNewValue();
            setSize(d.width, d.height);
        } else if ("modal".equals(evt.getPropertyName())) {
            setModal((Boolean) evt.getNewValue());
        } else if ("transparentMode".equals(evt.getPropertyName())) {
            setTransparentMode((Boolean) evt.getNewValue());
        } else if ("transparentRatio".equals(evt.getPropertyName())) {
            setTransparentRatio((Float) evt.getNewValue());
        } else if ("transparentDelay".equals(evt.getPropertyName())) {
            setTransparentDelay((Integer) evt.getNewValue());
        } else if ("enabled".equals(evt.getPropertyName())) {
            setEnabled((Boolean) evt.getNewValue());
        } else if ("autoHide".equals(evt.getPropertyName())) {
            setAutoHide((Boolean) evt.getNewValue());
        } else if ("animating".equals(evt.getPropertyName())) {
            setAnimating((Boolean) evt.getNewValue());
        } else if ("idVisibleOnTitleBar".equals(evt.getPropertyName())) {
            setIdVisibleOnTitleBar((Boolean) evt.getNewValue());
        } else if ("addToTaskBar".equals(evt.getPropertyName())) {
            setAddToTaskBar((Boolean) evt.getNewValue());
        }
    }

}
