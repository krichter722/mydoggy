package org.noos.xing.mydoggy.plaf.descriptors;

import org.noos.xing.mydoggy.SlidingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DefaultSlidingTypeDescriptor extends DefaultToolWindowTypeDescriptor implements SlidingTypeDescriptor, 
                                                                                             InternalTypeDescriptor {

    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;

    public DefaultSlidingTypeDescriptor() {
        transparentMode = true;
        transparentRatio = 0.5f;
        transparentDelay = 1000;
        autoHide = true;
    }

    public DefaultSlidingTypeDescriptor(ToolWindowDescriptor toolWindowDescriptor,
                                        DefaultSlidingTypeDescriptor parent,
                                        int transparentDelay,
                                        float transparentRatio,
                                        boolean transparentMode,
                                        boolean enabled,
                                        boolean animating,
                                        boolean autoHide,
                                        boolean idVisibleOnTitleBar,
                                        boolean hideRepresentativeButtonOnVisible,
                                        boolean titleBarButtonsVisible) {
        super(toolWindowDescriptor, parent, enabled, animating, autoHide,
                idVisibleOnTitleBar, hideRepresentativeButtonOnVisible, titleBarButtonsVisible);

        this.transparentDelay = transparentDelay;
        this.transparentRatio = transparentRatio;
        this.transparentMode = transparentMode;
    }

    public ToolWindowType getType() {
        return ToolWindowType.SLIDING;
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
        return new DefaultSlidingTypeDescriptor(toolWindowDescriptor,
                                                this,
                                                transparentDelay, transparentRatio,
                                                transparentMode, enabled, animating,
                                                autoHide,
                                                idVisibleOnTitleBar,
                hideRepresentativeButtonOnVisible,titleBarButtonsVisible);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);

        if ("transparentMode".equals(evt.getPropertyName())) {
            setTransparentMode((Boolean) evt.getNewValue());
        } else if ("transparentRatio".equals(evt.getPropertyName())) {
            setTransparentRatio((Float) evt.getNewValue());
        } else if ("transparentDelay".equals(evt.getPropertyName())) {
            setTransparentDelay((Integer) evt.getNewValue());
        }
    }

}
