package org.noos.xing.mydoggy.plaf.ui.animation;

import java.awt.*;

/**
 * @author Angelo De Caro  (angelo.decaro@gmail.com)
 */
public class MoveComponentAnimation extends AbstractAnimation {
    protected Rectangle startBounds;
    protected Rectangle endBounds;
    protected Component component;

    protected int deltaX, deltaY, deltaWidth, deltaHeight;

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
        deltaX = endBounds.x - startBounds.x;
        deltaY = endBounds.y - startBounds.y;
        deltaWidth = endBounds.width - startBounds.width;
        deltaHeight = endBounds.height - startBounds.height;
    }

    protected void onFinishAnimation() {
        component.setBounds(endBounds);
    }

    protected float onAnimating(float animationPercent) {
        int offsetX = (int) (animationPercent * deltaX);
        int offsetY = (int) (animationPercent * deltaY);
        int offsetWidth = (int) (animationPercent * deltaWidth);
        int offsetHeight = (int) (animationPercent * deltaHeight);

        Rectangle newBounds = new Rectangle(
                startBounds.x + offsetX,
                startBounds.y + offsetY,
                startBounds.width + offsetWidth,
                startBounds.height + offsetHeight
        );

        component.setBounds(newBounds);

        return animationPercent;
    }
}
