package org.noos.xing.mydoggy.plaf.ui.animation;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class AbstractAnimation implements ActionListener {
    public enum Direction {
        INCOMING, OUTGOING, NONE
    }

    protected enum Type {
        SHOW, HIDE
    }

    public final Object SYNC = new Object();

    private static final int ANIMATION_SLEEP = 1;

    private boolean animating;
    private Direction animationDirection;
    private Timer animationTimer;
    private long animationStart;
    private float animationDuration;
    private EventListenerList listenerList;


    protected AbstractAnimation(float animationDuration) {
        this.animationDuration = animationDuration;
    }

    public final void actionPerformed(ActionEvent e) {
        synchronized (SYNC) {
            if (animating) {
                // calculate height to show
                float animationPercent = (System.currentTimeMillis() - animationStart) / animationDuration;
                animationPercent = Math.min(1.0f, animationPercent);
                try {
                    animationPercent = onAnimating(animationPercent);
                } finally {
                    if (animationPercent >= 1.0f) {
                        stop();
                    }
                }
            }
        }
    }

    public final void show(Object... params) {
        synchronized (SYNC) {
            if (animating && animationDirection == Direction.INCOMING)
                return;

            if (animating) {
                stopAnimation();
                animationDirection = chooseFinishDirection(Type.SHOW);
                if (animationDirection != Direction.NONE)
                    onFinishAnimation();
            }
            onShow(params);
            startAnimation(Direction.INCOMING);
        }
    }

    public final void hide(Object... params) {
        synchronized (SYNC) {
            if (animating && animationDirection == Direction.OUTGOING)
                return;

            if (animating) {
                stopAnimation();
                animationDirection = chooseFinishDirection(Type.HIDE);
                if (animationDirection != Direction.NONE)
                    onFinishAnimation();
            }
            onHide(params);
            startAnimation(Direction.OUTGOING);
        }
    }

    public final void stop() {
        synchronized (SYNC) {
            if (isAnimating()) {
                stopAnimation();
                onFinishAnimation();
                fireOnFinished();
            }
        }
    }

    public final boolean isAnimating() {
        synchronized (SYNC) {
            return animating;
        }
    }

    public final Direction getAnimationDirection() {
        synchronized (SYNC) {
            return animationDirection;
        }
    }

    public void addAnimationListener(AnimationListener animationListener) {
        if (listenerList == null)
            listenerList = new EventListenerList();
        listenerList.add(AnimationListener.class, animationListener);

    }

    private void startAnimation(Direction direction) {
        synchronized (SYNC) {
            if (!animating) {
                onStartAnimation(direction);

                // Set Direction
                animationDirection = direction;

                // start animation timer
                animationStart = System.currentTimeMillis();
                if (animationTimer == null)
                    animationTimer = new Timer(ANIMATION_SLEEP, this);
                animating = true;
                animationTimer.start();
            }
        }
    }

    private void stopAnimation() {
        synchronized (SYNC) {
            if (animationTimer != null)
                animationTimer.stop();
            animating = false;
        }
    }


    protected abstract void onShow(Object... params);

    protected abstract void onHide(Object... params);

    protected abstract void onStartAnimation(Direction direction);

    protected abstract void onFinishAnimation();

    protected abstract float onAnimating(float animationPercent);


    protected Direction chooseFinishDirection(Type type) {
        return getAnimationDirection();
    }

    protected void fireOnFinished() {
        if (listenerList != null) {
            for (AnimationListener listener : listenerList.getListeners(AnimationListener.class))
                listener.onFinished();
        }
    }

}
