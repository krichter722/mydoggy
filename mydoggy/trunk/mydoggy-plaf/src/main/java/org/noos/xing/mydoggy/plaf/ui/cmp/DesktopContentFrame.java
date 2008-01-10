package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.DesktopContentUI;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class DesktopContentFrame extends JInternalFrame implements DesktopContentUI {
    private boolean detachable;
    private boolean transparentMode;
    private float transparentRatio;
    private int transparentDelay;
    private Content content;

    public DesktopContentFrame(Content content, String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        this.content = content;
        this.detachable = true;
        this.transparentMode = true;
        this.transparentRatio = 0.8f;
        this.transparentDelay = 1000;
    }

    public boolean isIconified() {
        return super.isIcon();
    }

    public void setIconified(boolean iconified) {
        try {
            setIcon(iconified);
        } catch (PropertyVetoException ignore) {
            ignore.printStackTrace();
        }
    }

    public boolean isCloseable() {
        return isClosable();
    }

    public void setCloseable(boolean closeable) {
        setClosable(closeable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
    }

    public boolean isTransparentMode() {
        return transparentMode;
    }

    public void setTransparentMode(boolean transparentMode) {
        this.transparentMode = transparentMode;
    }

    public float getTransparentRatio() {
        return transparentRatio;
    }

    public void setTransparentRatio(float transparentRatio) {
        this.transparentRatio = transparentRatio;
    }

    public int getTransparentDelay() {
        return transparentDelay;
    }

    public void setConstraints(Object... constraints) {
        if (constraints.length > 0) {
            if (constraints[0] instanceof Point) {
                Point location = (Point) constraints[0];

                setBounds(location.x, location.y, 320, 200);
            } else if (constraints[0] instanceof Rectangle) {
                setBounds((Rectangle) constraints[0]);
            } 
        }
    }

    public Rectangle getDetachedBounds() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDetachedBounds(Rectangle bounds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTransparentDelay(int transparentDelay) {
        this.transparentDelay = transparentDelay;
    }

    public Content getContent() {
        return content;
    }
}
