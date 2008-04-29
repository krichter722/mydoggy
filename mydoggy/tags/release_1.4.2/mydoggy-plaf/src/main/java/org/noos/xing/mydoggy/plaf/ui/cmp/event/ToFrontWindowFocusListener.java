package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToFrontWindowFocusListener implements WindowFocusListener {
    protected long start;
    protected long end;
    protected Window window;

    public ToFrontWindowFocusListener(Window window) {
        this.window = window;
    }

    public void windowGainedFocus(WindowEvent e) {
        start = System.currentTimeMillis();
    }

    public void windowLostFocus(WindowEvent e) {
        end = System.currentTimeMillis();
        long elapsed = end - start;
        //System.out.println(elapsed);
        if (elapsed < 100)
            window.toFront();

        window.removeWindowFocusListener(this);
    }
}
