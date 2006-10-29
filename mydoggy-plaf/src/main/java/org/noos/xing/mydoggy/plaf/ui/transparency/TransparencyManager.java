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
            // TODO: add so checck...
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


    private static native boolean isAlphaModeEnabledNative(Window window);

    private static native void setAlphaModeEnabledNative(Window window, boolean flag);

    private static native void setAlphaModeRatioNative(Window window, float ratio);


    public boolean isServiceAvailable() {
        return LIBRARIES_LOADED;
    }

    public synchronized void setAlphaModeRatio(Window window, float transparency) {
        if (LIBRARIES_LOADED) {
//            System.out.println(isAlphaModeEnabledNative(window));
            if (isAlphaModeEnabledNative(window)) {
                if (transparency == 0f)
                    setAlphaModeEnabledNative(window, false);
                else setAlphaModeRatioNative(window, transparency);
            } else {
                if (transparency == 0f)
                    return;
                setAlphaModeEnabledNative(window, true);
                setAlphaModeRatioNative(window, transparency);
            }
//            System.out.println(isAlphaModeEnabledNative(window));
        }
    }

    public synchronized boolean isAlphaModeEnabled(Window window) {
        return LIBRARIES_LOADED && isAlphaModeEnabledNative(window);
    }

}
