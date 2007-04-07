package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.WindowTransparencyManager;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TransparencyAnimation extends AbstractAnimation {
    private TransparencyManager transparencyManager;
    private Component component;
    private float alpha;

    public TransparencyAnimation(Component component, float alpha) {
        this(WindowTransparencyManager.getInstance(), component, alpha, 2000f);
    }

    public TransparencyAnimation(TransparencyManager transparencyManager, Component component, float alpha, float duration) {
        super(duration);
        this.transparencyManager = transparencyManager;
        this.component = component;
        this.alpha = alpha;
    }

    protected float onAnimating(float animationPercent) {
        if (getAnimationDirection() == Direction.INCOMING) {
            float animatingLengthX = (animationPercent * (1f - alpha));
            transparencyManager.setAlphaModeRatio(component, 1f - animatingLengthX);
        }
        return animationPercent;
	}

	protected void onFinishAnimation() {
		switch (getAnimationDirection()) {
			case INCOMING:
				transparencyManager.setAlphaModeRatio(component, alpha);
				break;
			case OUTGOING:
				transparencyManager.setAlphaModeRatio(component, 0.0f);
				break;
		}
	}

	protected void onHide(Object... params) {
	}

	protected void onShow(Object... params) {
	}

	protected void onStartAnimation(Direction direction) {
	}

	protected Direction chooseFinishDirection(Type type) {
		return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
	}

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public TransparencyManager getTransparencyManager() {
        return transparencyManager;
    }
}
