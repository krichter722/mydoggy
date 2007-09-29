package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class WindowTransparencyListener extends WindowAdapter implements ActionListener {
    private final TransparencyManager<Window> transparencyManager;

    private TransparencyAnimation animation;

    private Timer timer;
    private ContentUI contentUI;
    private Window window;

    public WindowTransparencyListener(TransparencyManager<Window> transparencyManager, ContentUI contentUI, Window window) {
        this.transparencyManager = transparencyManager;
        this.contentUI = contentUI;
        this.window = window;
        this.animation = new TransparencyAnimation(transparencyManager, window, contentUI.getTransparentRatio());
    }

    public void windowGainedFocus(WindowEvent e) {
        if (transparencyManager.isAlphaModeEnabled(e.getWindow())) {
            timer.stop();
            animation.hide();
            transparencyManager.setAlphaModeRatio(e.getWindow(), 0.0f);
        }
    }

    public void windowLostFocus(WindowEvent e) {
        if (contentUI.isTransparentMode() && !window.isFocused()) {
            if (!transparencyManager.isAlphaModeEnabled(e.getWindow())) {
                timer = new Timer(contentUI.getTransparentDelay(), this);
                timer.start();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (timer.isRunning()) {
            timer.stop();
            synchronized (transparencyManager) {
                animation.setAlpha(contentUI.getTransparentRatio());
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
