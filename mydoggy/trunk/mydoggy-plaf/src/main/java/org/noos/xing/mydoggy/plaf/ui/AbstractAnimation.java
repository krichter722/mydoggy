package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class AbstractAnimation implements ActionListener {

	protected enum Direction {INCOMING, OUTGOING}

	private static final int ANIMATION_SLEEP = 1;

	private boolean animating;
	private Direction animationDirection;
	private Timer animationTimer;
	private long animationStart;
	private float animationDuration;

	protected AbstractAnimation(float animationDuration) {
		this.animationDuration = animationDuration;
	}

	public final synchronized void actionPerformed(ActionEvent e) {
		if (animating) {
			// calculate height to show
			float animationPercent = (System.currentTimeMillis() - animationStart) / animationDuration;
			animationPercent = Math.min(1.0f, animationPercent);
			try {
				animationPercent = onAnimating(animationPercent);
			} finally {
				if (animationPercent >= 1.0f) {
					stopAnimation();
					onFinishAnimation();
				}
			}
		}
	}

	public final synchronized void show(Object... params) {
		if (animating) {
			stopAnimation();
			onFinishAnimation();
		}
		onShow(params);
		startAnimation(Direction.INCOMING);
	}

	public final synchronized void hide(Object... params) {
		if (animating) {
			stopAnimation();
			onFinishAnimation();
		}
		onHide(params);
		startAnimation(Direction.OUTGOING);
	}

	public final synchronized void stop() {
		stopAnimation();
		onFinishAnimation();
	}

	public final synchronized boolean isAnimating() {
		return animating;
	}

	public final synchronized Direction getAnimationDirection() {
		return animationDirection;
	}


	private synchronized void startAnimation(Direction direction) {
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

	private synchronized void stopAnimation() {
		animationTimer.stop();
		animating = false;
	}


	protected abstract void onShow(Object... params);

	protected abstract void onHide(Object... params);

	protected abstract void onStartAnimation(Direction direction);

	protected abstract void onFinishAnimation();

	protected abstract float onAnimating(float animationPercent);

}
