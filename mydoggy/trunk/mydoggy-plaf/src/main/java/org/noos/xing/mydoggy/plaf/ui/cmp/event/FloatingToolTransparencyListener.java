package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.FloatingContainer;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingToolTransparencyListener implements PropertyChangeListener, ActionListener {
    private ToolWindowDescriptor descriptor;
    private Window window;

    private final TransparencyManager<Window> transparencyManager;
    private TransparencyAnimation transparencyAnimation;

    private Timer timer;

    public FloatingToolTransparencyListener(FloatingContainer floatingContainer, ToolWindowDescriptor descriptor, final Window window) {
        this.transparencyManager = floatingContainer.getResourceManager().getTransparencyManager();
        if (transparencyManager.isServiceAvailable()) {
            this.descriptor = descriptor;
            this.window = window;

            this.transparencyAnimation = new TransparencyAnimation(
                    descriptor.getResourceManager().getTransparencyManager(),
                    window, 0.0f
            );

            floatingContainer.addPropertyChangeListener("active", this);
            floatingContainer.addPropertyChangeListener("visible.FLOATING", this);
            floatingContainer.addPropertyChangeListener("visible.FLOATING_FREE", this);
        } else
            this.transparencyAnimation = null;
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() != descriptor /*|| !descriptor.getToolWindow().isVisible()*/
            || (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING &&
                descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_FREE))
            return;

        assert evt.getPropertyName() != null;
//        assert descriptor.getToolWindow().isVisible();
        assert (descriptor.getToolWindow().getType() == ToolWindowType.FLOATING ||
                descriptor.getToolWindow().getType() == ToolWindowType.FLOATING_FREE);

        if ("active".equals(evt.getPropertyName())) {
            FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
            if (descriptor.getFloatingContainer().isAnimating()) {
                if (timer != null) {
                    timer.stop();
                    synchronized (transparencyManager) {
                        if (transparencyManager.isAlphaModeEnabled(window)) {
                            transparencyAnimation.stop();
                            transparencyManager.setAlphaModeRatio(window, 0.0f);
                        }
                    }
                }
                return;
            }
            
            if (typeDescriptor.isTransparentMode()) {
                System.out.println(evt.getNewValue());
                System.out.println(window.getBounds());
                if (evt.getNewValue() == Boolean.FALSE) {
                    timer = new Timer(typeDescriptor.getTransparentDelay(), this);
                    timer.start();
                } else {
                    if (timer != null)
                        timer.stop();

                    synchronized (transparencyManager) {
                        if (transparencyManager.isAlphaModeEnabled(window)) {
                            transparencyAnimation.stop();
                            transparencyManager.setAlphaModeRatio(window, 0.0f);
                        }
                    }
                }
            }
        } else if (evt.getPropertyName().startsWith("visible.")) {
            synchronized (transparencyManager) {
                if (evt.getNewValue() == Boolean.FALSE && transparencyManager.isAlphaModeEnabled(window)) {
                    if (timer != null)
                        timer.stop();

                    if (transparencyManager.isAlphaModeEnabled(window)) {
                        transparencyAnimation.stop();
                        transparencyManager.setAlphaModeRatio(window, 0.0f);
                    }
                }
            }

            if (evt.getNewValue() == Boolean.TRUE) {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                if (typeDescriptor.isTransparentMode()) {
                    timer = new Timer(1000 + typeDescriptor.getTransparentDelay(), this);
                    timer.start();
                }
            }
        }
    }

    public synchronized void actionPerformed(ActionEvent e) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            if (!descriptor.getToolWindow().isVisible()
                || (descriptor.getToolWindow().getType() != ToolWindowType.FLOATING &&
                    descriptor.getToolWindow().getType() != ToolWindowType.FLOATING_FREE))
                return;

            FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
            synchronized (transparencyManager) {
                transparencyAnimation.setAlpha(typeDescriptor.getTransparentRatio());
                transparencyAnimation.show();
//                transparencyManager.setAlphaModeRatio(window, typeDescriptor.getTransparentRatio());
            }
        }
    }


}
