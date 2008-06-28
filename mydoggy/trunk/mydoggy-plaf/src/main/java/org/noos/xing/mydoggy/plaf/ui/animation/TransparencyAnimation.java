package org.noos.xing.mydoggy.plaf.ui.animation;

import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentComponent;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TransparencyAnimation extends AbstractAnimation {
    private final TransparencyManager<Component> transparencyManager;
    private Component component;
    private float alpha;

    public TransparencyAnimation(TransparencyManager transparencyManager, Component component, float alpha) {
        this(transparencyManager, component, alpha, 2000f);
    }

    public TransparencyAnimation(TransparencyManager transparencyManager, Component component, float alpha, float duration) {
        super(duration);
        this.transparencyManager = transparencyManager;
        this.component = component;
        this.alpha = alpha;
    }

    public TransparencyAnimation(final TranslucentComponent translucentComponent, Component component, float alpha, float duration) {
        super(duration);
        this.transparencyManager = new Translucent2TransparencyManager(translucentComponent);
        this.component = component;
        this.alpha = alpha;
    }

    protected float onAnimating(float animationPercent) {
        if (getAnimationDirection() == Direction.INCOMING) {
            double animatingLengthX = (animationPercent * (1.0f - alpha));
            synchronized(transparencyManager) {
                transparencyManager.setAlphaModeRatio(component, 1.0f - (float) animatingLengthX);
            }
        }
        return animationPercent;
	}

	protected void onFinishAnimation() {
        synchronized(transparencyManager) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    transparencyManager.setAlphaModeRatio(component, alpha);
                    break;
                case OUTGOING:
                    transparencyManager.setAlphaModeRatio(component, 0.0f);
                    break;
            }
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


    public static class Translucent2TransparencyManager implements TransparencyManager<Component> {
        protected TranslucentComponent translucentComponent;

        public Translucent2TransparencyManager(TranslucentComponent translucentComponent) {
            this.translucentComponent = translucentComponent;
        }

        public boolean isServiceAvailable() {
            return true;
        }

        public void setAlphaModeRatio(Component component, float transparency) {
            translucentComponent.setAlphaModeRatio(transparency);
        }

        public boolean isAlphaModeEnabled(Component component) {
            return true;
        }
    }
}
