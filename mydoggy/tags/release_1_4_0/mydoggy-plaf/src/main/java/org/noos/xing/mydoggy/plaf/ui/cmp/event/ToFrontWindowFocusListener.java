package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToFrontWindowFocusListener implements WindowFocusListener {
    long start;
    long end;
    private final JDialog dialog;

    public ToFrontWindowFocusListener(JDialog dialog) {
        this.dialog = dialog;
    }

    public void windowGainedFocus(WindowEvent e) {
        start = System.currentTimeMillis();
    }

    public void windowLostFocus(WindowEvent e) {
        end = System.currentTimeMillis();
        long elapsed = end - start;
        //System.out.println(elapsed);
        if (elapsed < 100)
            dialog.toFront();

        dialog.removeWindowFocusListener(this);
    }
}
