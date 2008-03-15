package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class MyDoggyTabbedContentUI extends PropertyChangeEventSource implements TabbedContentUI {
    protected JTabbedContentPane tabbedContentPane;

    protected Content content;
    protected boolean closable;
    protected boolean detachable;
    protected boolean transparentMode;
    protected float transparentRatio;
    protected int transparentDelay;
    protected Rectangle detachedBounds;
    protected boolean addToTaskBar;

    public MyDoggyTabbedContentUI(JTabbedContentPane tabbedContentPane, Content content) {
        this.tabbedContentPane = tabbedContentPane;
        this.content = content;
        this.closable = this.detachable = true;
        this.transparentMode = true;
        this.transparentRatio = 0.7f;
        this.transparentDelay = 0;
        this.addToTaskBar = false;
    }

    public Content getContent() {
        return content;
    }

    public boolean isCloseable() {
        return closable;
    }

    public void setCloseable(boolean closable) {
        if (this.closable == closable)
            return;

        boolean old = this.closable;
        this.closable = closable;

        firePropertyChangeEvent("closable", old, closable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        if (this.detachable == detachable)
            return;

        boolean old = this.detachable;
        this.detachable = detachable;

        firePropertyChangeEvent("detachable", old, detachable);
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

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        if (this.transparentRatio == transparentRatio)
            return;

        float old = this.transparentRatio;
        this.transparentRatio = transparentRatio;

        firePropertyChangeEvent("transparentRatio", old, transparentRatio);
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

    public void setConstraints(Object... constraints) {
        if (constraints.length > 0 && constraints[0] instanceof Integer) 
            tabbedContentPane.setIndex(content, (Integer) constraints[0]);                           
    }

    public Rectangle getDetachedBounds() {
        return detachedBounds;
    }

    public void setDetachedBounds(Rectangle detachedBounds) {
        if ((this.detachedBounds != null && this.detachedBounds.equals(detachedBounds)) || detachedBounds == null)
            return;
        
        this.detachedBounds = detachedBounds;
        firePropertyChangeEvent("detachedBounds", null, detachedBounds);
    }

    public void setAddToTaskBarWhenDetached(boolean addToTaskBar) {
        if (this.addToTaskBar == addToTaskBar)
            return;

        boolean old = this.addToTaskBar;
        this.addToTaskBar = addToTaskBar;

        firePropertyChangeEvent("addToTaskBar", old, addToTaskBar);
    }

    public boolean isAddToTaskBarWhenDetached() {
        return addToTaskBar;
    }
}
