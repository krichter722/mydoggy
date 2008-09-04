package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.FloatingLiveTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultFloatingLiveTypeDescriptor extends DefaultToolWindowTypeDescriptor implements FloatingLiveTypeDescriptor,
                                                                                                  InternalTypeDescriptor {
    private Point location;
    private Dimension size;

    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;


    public DefaultFloatingLiveTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.5f;
        transparentDelay = 1000;

        autoHide = false;
    }

    public DefaultFloatingLiveTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                             DefaultFloatingLiveTypeDescriptor parent,
                                             Point location,
                                             Dimension size,
                                             int transparentDelay,
                                             float transparentRatio,
                                             boolean useTransparentMode,
                                             boolean enabled,
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
    }


    public ToolWindowType getType() {
        return ToolWindowType.FLOATING_LIVE;
    }


    public void setLocation(int x, int y) {
        Point newLocation = new Point(x, y);
        if (location != null && location.equals(newLocation))
            return;

        Point old = this.location;
        this.location = newLocation;

        firePropertyChangeEvent("location", old, location);
    }

    public Point getLocation() {
        return location;
    }

    public void setSize(int width, int height) {
        Dimension newSize = new Dimension(width, height);
        if (size != null && size.equals(newSize))
            return;

        Dimension old = this.size;
        this.size = newSize;

        firePropertyChangeEvent("size", old, size);
    }

    public Dimension getSize() {
        return size;
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


    public ToolWindowTypeDescriptor cloneMe(ToolWindowDescriptor toolWindowDescriptor) {
        return new DefaultFloatingLiveTypeDescriptor(toolWindowDescriptor,
                                                     this, location, size, transparentDelay,
                                                     transparentRatio, transparentMode,
                                                     enabled, animating, autoHide, idVisibleOnTitleBar, hideRepresentativeButtonOnVisible);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);

        if ("location".equals(evt.getPropertyName())) {
            Point p = (Point) evt.getNewValue();
            setLocation(p.x, p.y);
        } else if ("size".equals(evt.getPropertyName())) {
            Dimension d = (Dimension) evt.getNewValue();
            setSize(d.width, d.height);
        } else if ("transparentMode".equals(evt.getPropertyName())) {
            setTransparentMode((Boolean) evt.getNewValue());
        } else if ("transparentRatio".equals(evt.getPropertyName())) {
            setTransparentRatio((Float) evt.getNewValue());
        } else if ("transparentDelay".equals(evt.getPropertyName())) {
            setTransparentDelay((Integer) evt.getNewValue());
        } 
    }

}