package org.noos.xing.mydoggy.plaf.ui.transparency;

import com.sun.jna.examples.WindowUtils;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JNAWindowTransparencyManager implements TransparencyManager<Window> {

    private boolean available;

    public JNAWindowTransparencyManager() {
        initTransparencyManager();
    }

    public boolean isServiceAvailable() {
        return available;
    }

    public void setAlphaModeRatio(Window component, float transparency) {
        if (available)
            WindowUtils.setWindowAlpha(component, transparency);
    }

    public boolean isAlphaModeEnabled(Window component) {
        return true;    // TODO:
    }

    protected void initTransparencyManager() {
        available = false;
        try {
            Class.forName("com.sun.jna.examples.WindowUtils");
            available = WindowUtils.isWindowAlphaSupported();
        } catch (ClassNotFoundException e) {}
    }

}
