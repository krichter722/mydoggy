package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TransparencyAnimation extends AbstractAnimation {
    private final TransparencyManager transparencyManager = TransparencyManager.getInstance();

    private Window window;
    private float alpha;

    public TransparencyAnimation(Window window, float alpha) {
		super(2000f);
		this.window = window;
        this.alpha = alpha;
    }


	protected float onAnimating(float animationPercent) {
		float animatingLengthX = 1f - (animationPercent * alpha);
		if (animatingLengthX < alpha) {
			return 1.0f;
		} else if (getAnimationDirection() == Direction.INCOMING) {
			transparencyManager.setAlphaModeRatio(window, animatingLengthX);
		}
		return animationPercent;
	}

	protected void onFinishAnimation() {
		switch (getAnimationDirection()) {
			case INCOMING:
				transparencyManager.setAlphaModeRatio(window, alpha);
				break;
			case OUTGOING:
				transparencyManager.setAlphaModeRatio(window, 0.0f);
				break;
		}
	}

	protected void onHide(Object... params) {
	}

	protected void onShow(Object... params) {
	}

	protected void onStartAnimation(Direction direction) {
	}

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
