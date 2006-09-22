package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TransparencyAnimation implements ActionListener {
    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;
    public static final float ANIMATION_DURATION = 1000f;
    public static final int ANIMATION_SLEEP = 10;

    private final TransparencyManager transparencyManager = TransparencyManager.getInstance();

    private boolean animating;
    private int animationDirection;
    private Timer animationTimer;
    private long animationStart;

    private Window window;
    private float alpha;

    private boolean endAnimation = false;

    public TransparencyAnimation(Window window, float alpha) {
        this.window = window;
        this.alpha = alpha;
    }

    public void actionPerformed(ActionEvent e) {
        if (animating) {
            // calculate height to show
            float animationPercent = (System.currentTimeMillis() - animationStart) / ANIMATION_DURATION;
            animationPercent = Math.min(1.0f, animationPercent);

            float animatingLengthX = 1f - (animationPercent * alpha);
            if (animatingLengthX < alpha) {
                animationPercent = 1.0f;
            } else if (animationDirection == INCOMING) {
                transparencyManager.setAlphaModeRatio(window, animatingLengthX);
            }

            if (animationPercent >= 1.0f) {
                stopAnimation();
                finishAnimation();
            }
        }
    }


    public synchronized void show() {
        if (animating) {
            stopAnimation();
            animationDirection = OUTGOING;
            finishAnimation();
        }

        startAnimation(INCOMING);
    }

    public synchronized void hide() {
        if (animating) {
            stopAnimation();
            animationDirection = INCOMING;
            finishAnimation();
        }

        if (endAnimation)
            startAnimation(OUTGOING);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    private void startAnimation(int direction) {
        if (!animating) {
            animationDirection = direction;
            // start floatingAnimation timer
            animationStart = System.currentTimeMillis();
            if (animationTimer == null)
                animationTimer = new Timer(ANIMATION_SLEEP, this);
            animating = true;
            animationTimer.start();
        }
    }

    private void stopAnimation() {
        animationTimer.stop();
        animating = false;
    }

    private void finishAnimation() {
        switch (animationDirection) {
            case INCOMING:
                transparencyManager.setAlphaModeRatio(window, alpha);
                break;
            case OUTGOING:
                transparencyManager.setAlphaModeRatio(window, 0.0f);
                break;
        }
    }
}
