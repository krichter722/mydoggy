package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultFloatingTypeDescriptor extends DefaultToolWindowTypeDescriptor implements FloatingTypeDescriptor,
                                                                                              InternalTypeDescriptor {
    private Point location;
    private Dimension size;

    private boolean modal;

    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    private boolean addToTaskBar;
    private boolean alwaysOnTop;
    private boolean osDecorated;

    public DefaultFloatingTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.7f;
        transparentDelay = 1500;
        modal = false;
        autoHide = false;
        addToTaskBar = false;
        alwaysOnTop = true;
        osDecorated = false;
    }

    public DefaultFloatingTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                         DefaultFloatingTypeDescriptor parent,
                                         Point location,
                                         Dimension size,
                                         int transparentDelay,
                                         float transparentRatio,
                                         boolean useTransparentMode,
                                         boolean modal,
                                         boolean addToTaskBar, boolean alwaysOnTop, boolean osDecorated, boolean enabled,
                                         boolean animating,
                                         boolean autoHide,
                                         boolean idVisibleOnTitleBar,
                                         boolean hideRepresentativeButtonOnVisible) {
        
        super(toolWindowDescriptor, parent, enabled, animating, autoHide, idVisibleOnTitleBar, hideRepresentativeButtonOnVisible);

        this.location = location;
        this.size = size;
        this.transparentDelay = transparentDelay;
        this.transparentRatio = transparentRatio;
        this.transparentMode = useTransparentMode;
        this.modal = modal;
        this.addToTaskBar = addToTaskBar;
        this.alwaysOnTop = alwaysOnTop;
        this.osDecorated = osDecorated;
    }


    public ToolWindowType getType() {
        return ToolWindowType.FLOATING;
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

    public void setTransparentDelay(int transparentDelay) {
        if (this.transparentDelay == transparentDelay)
            return;

        int old = this.transparentDelay;
        this.transparentDelay = transparentDelay;

        firePropertyChangeEvent("transparentDelay", old, transparentDelay);
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

    public void setAlwaysOnTop(boolean alwaysOnTop) {
        if (this.alwaysOnTop == alwaysOnTop)
            return;

        boolean old = this.alwaysOnTop;
        this.alwaysOnTop = alwaysOnTop;

        firePropertyChangeEvent("alwaysOnTop", old, alwaysOnTop);
    }

    public boolean isAlwaysOnTop() {
        return alwaysOnTop;
    }

    public boolean isOsDecorated() {
        return osDecorated;
    }

    public void setOsDecorated(boolean osDecorated) {
        if (this.osDecorated == osDecorated)
            return;

        boolean old = this.osDecorated;
        this.osDecorated = osDecorated;

        firePropertyChangeEvent("osDecorated", old, osDecorated);
    }


    public ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor) {
        return new DefaultFloatingTypeDescriptor(toolWindowDescriptor,
                                                 this, location, size, transparentDelay,
                                                 transparentRatio, transparentMode,
                                                 modal, addToTaskBar, alwaysOnTop, osDecorated, enabled, animating, autoHide,
                                                 idVisibleOnTitleBar,
                                                 hideRepresentativeButtonOnVisible);
    }

    public void propertyChange(PropertyChangeEvent evt) {        
        super.propertyChange(evt);

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
        } else if ("addToTaskBar".equals(evt.getPropertyName())) {
            setAddToTaskBar((Boolean) evt.getNewValue());
        } else if ("alwaysOnTop".equals(evt.getPropertyName())) {
            setAlwaysOnTop((Boolean) evt.getNewValue());
        }
    }

}
