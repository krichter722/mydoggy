package org.noos.xing.mydoggy.plaf.ui.transparency;

import java.awt.*;


/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TransparencyManager {
    private static boolean LIBRARIES_LOADED;

    private static final TransparencyManager INSTANCE = new TransparencyManager();

    static {
        try {
            Toolkit.getDefaultToolkit();    // To init necessary libraries if not already loaded.
            System.loadLibrary("jawt");
            System.loadLibrary("TransparencyManager");
            LIBRARIES_LOADED = true;
        } catch (Throwable t) {
//            t.printStackTrace();
            LIBRARIES_LOADED = false;
        }
    }

    public static TransparencyManager getInstance() {
        return INSTANCE;
    }


    private TransparencyManager() {
    }


    private static native boolean isAlphaModeEnabledWin32Impl(Window window);

    private static native void setAlphaModeEnabledWin32Impl(Window window, boolean flag);

    private static native void setAlphaModeRatioWin32Impl(Window window, float ratio);


    public boolean isServiceAvailable() {
        return LIBRARIES_LOADED;
    }

    public synchronized void setAlphaModeRatio(Window window, float transparency) {
        if (LIBRARIES_LOADED) {
//            System.out.println(isAlphaModeEnabledWin32Impl(window));
            if (isAlphaModeEnabledWin32Impl(window)) {
                if (transparency == 0f)
                    setAlphaModeEnabledWin32Impl(window, false);
                else setAlphaModeRatioWin32Impl(window, transparency);
            } else {
                if (transparency == 0f)
                    return;
                setAlphaModeEnabledWin32Impl(window, true);
                setAlphaModeRatioWin32Impl(window, transparency);
            }
//            System.out.println(isAlphaModeEnabledWin32Impl(window));
        }
    }

    public synchronized boolean isAlphaModeEnabled(Window window) {
        return LIBRARIES_LOADED && isAlphaModeEnabledWin32Impl(window);
    }

}
