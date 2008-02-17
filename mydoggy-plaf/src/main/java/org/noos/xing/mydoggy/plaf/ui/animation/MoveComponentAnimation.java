package org.noos.xing.mydoggy.plaf.ui.animation;

import java.awt.*;

/**
 * @author Angelo De Caro  (angelo.decaro@gmail.com)
 */
public class MoveComponentAnimation extends AbstractAnimation {
    protected Rectangle startBounds;
    protected Rectangle endBounds;
    protected Component component;

    public MoveComponentAnimation(float animationDuration, Component component) {
        super(animationDuration);
        this.component = component;
    }

    protected void onShow(Object... params) {
        endBounds = (Rectangle) params[0];
    }

    protected void onHide(Object... params) {
    }

    protected void onStartAnimation(Direction direction) {
        startBounds = component.getBounds();
    }

    protected void onFinishAnimation() {
        component.setBounds(endBounds);
    }

    protected float onAnimating(float animationPercent) {
        return 0;  // TODO: implement this.
    }
}
