package org.noos.xing.mydoggy.plaf.ui.content.desktop;

import org.noos.xing.mydoggy.DesktopContentUI;
import org.noos.xing.mydoggy.Content;

import javax.swing.*;
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

    public void setTransparentDelay(int transparentDelay) {
        this.transparentDelay = transparentDelay;
    }

    public Content getContent() {
        return content;
    }
}
