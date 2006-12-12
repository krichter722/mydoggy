package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class WindowTransparencyListener extends WindowAdapter implements WindowFocusListener, ActionListener {
    private final TransparencyManager transparencyManager = TransparencyManager.getInstance();

    private TransparencyAnimation animation;

    private Timer timer;
    private Window window;

    public WindowTransparencyListener(Window window) {
        this.window = window;
        this.animation = new TransparencyAnimation(window, 0.8f);
    }

    public void windowGainedFocus(WindowEvent e) {
        if (transparencyManager.isAlphaModeEnabled(e.getWindow())) {
            timer.stop();
            animation.hide();
            transparencyManager.setAlphaModeRatio(e.getWindow(), 0.0f);
        }
    }

    public void windowLostFocus(WindowEvent e) {
        if (!transparencyManager.isAlphaModeEnabled(e.getWindow())) {
            timer = new Timer(2000, this);
            timer.start();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (timer.isRunning()) {
            timer.stop();
            synchronized (transparencyManager) {
                animation.show();
            }
        }
    }

    public void windowClosing(WindowEvent event) {
        if (transparencyManager.isAlphaModeEnabled(event.getWindow())) {
            animation.hide();
            transparencyManager.setAlphaModeRatio(window, 0.0f);
        }
    }

}
