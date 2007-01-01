package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.border.SlidingBorder;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class SlidingContainer extends FloatingContainer {
	private final SlidingAnimation slidingAnimation = new SlidingAnimation();

	private SlidingBorder border;
	private Container barContainer;

	private Container glassPane;

	private SlidingMouseInputHandler slidingMouseInputHandler;

	private JPanel mainPanel = new JPanel();
	private JComponent sheet = new JPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));

	public SlidingContainer(ToolWindowDescriptor descriptor) {
		super(descriptor);

		initSlidingComponents();
		initSlidingListeners();
	}

	public void setVisible(boolean visible, Container barContainer) {
		this.barContainer = barContainer;
		Component content = getContentContainer();
		sheet.remove(content);

		synchronized (slidingAnimation) {
			if (slidingAnimation.isAnimating())
				slidingAnimation.stop();
		}

		if (visible) {
			// Reset Layout
			configureSlidingIcons();

			TableLayout layout = (TableLayout) sheet.getLayout();
			layout.setColumn(0, 0);
			layout.setColumn(2, 0);
			layout.setRow(0, 0);
			layout.setRow(2, 0);

			barContainer.getParent().getLayout().layoutContainer(barContainer.getParent());
			resize();

			content.setVisible(true);
			sheet.add(content, "1,1,FULL,FULL");

			// Prepare sheet
			border.setAnchor(toolWindow.getAnchor());
			sheet.setBorder(border);

			int height = mainPanel.getHeight();
			Point point = SwingUtilities.convertPoint(mainPanel, 0, 0, glassPane);

			sheet.setBounds(point.x, point.y, mainPanel.getWidth(), height);

			glassPane.remove(sheet);
			glassPane.add(sheet);

			slidingAnimation.show(sheet.getBounds());
		} else {
			// Set Layout
			TableLayout layout = (TableLayout) sheet.getLayout();
			layout.setColumn(0, 2);
			layout.setColumn(2, 2);
			layout.setRow(0, 2);
			layout.setRow(2, 2);

			switch (descriptor.getToolWindow().getAnchor()) {
				case TOP:
				case BOTTOM:
					descriptor.setDivederLocation(sheet.getHeight());
					break;
				case LEFT:
				case RIGHT:
					descriptor.setDivederLocation(sheet.getWidth());
					break;
			}
			slidingAnimation.hide(sheet.getBounds());
		}
	}


	protected void update() {
		// Reset Layout
		configureSlidingIcons();

		TableLayout layout = (TableLayout) sheet.getLayout();
		layout.setColumn(0, 0);
		layout.setColumn(2, 0);
		layout.setRow(0, 0);
		layout.setRow(2, 0);

		barContainer.getParent().getLayout().layoutContainer(barContainer.getParent());	// TODO : si verifica un errore nei test...
		resize();

		Component content = getContentContainer();
		sheet.remove(content);
		sheet.add(content, "1,1,FULL,FULL");

		// Prepare sheet
		border.setAnchor(toolWindow.getAnchor());
		sheet.setBorder(border);

		int height = mainPanel.getHeight();
		Point point = SwingUtilities.convertPoint(mainPanel, 0, 0, glassPane);

		sheet.setBounds(point.x, point.y, mainPanel.getWidth(), height);

		glassPane.remove(sheet);
		glassPane.add(sheet);
		glassPane.validate();
	}

	protected void resize() {
		int length = descriptor.getDivederLocation();
		if (length == -1)
			length = 200;

		switch (toolWindow.getAnchor()) {
			case LEFT:
				int height = barContainer.getHeight();
				mainPanel.setSize(length, height);

				Point location = new Point(0, 0);
				SwingUtilities.convertPointToScreen(location, barContainer);
				location.x += barContainer.getWidth();
				mainPanel.setLocation(location);
				break;
			case RIGHT:
				height = barContainer.getHeight();
				mainPanel.setSize(length, height);

				location = new Point(0, 0);
				SwingUtilities.convertPointToScreen(location, barContainer);
				location.x -= mainPanel.getWidth();
				mainPanel.setLocation(location);
				break;
			case TOP:
				int width = barContainer.getWidth();
				mainPanel.setSize(width, length);

				location = new Point(0, 0);
				SwingUtilities.convertPointToScreen(location, barContainer);
				location.y += barContainer.getHeight();
				mainPanel.setLocation(location);
				break;
			case BOTTOM:
				width = barContainer.getWidth();
				mainPanel.setSize(width, length);

				location = new Point(0, 0);
				SwingUtilities.convertPointToScreen(location, barContainer);
				location.y -= mainPanel.getHeight();
				mainPanel.setLocation(location);
				break;
		}
	}


	private void initSlidingComponents() {
		border = new SlidingBorder();

		Window anchestor = descriptor.getWindowAnchestor();
		if (anchestor instanceof RootPaneContainer) {
			glassPane = (Container) ((RootPaneContainer) anchestor).getGlassPane();

			anchestor.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible())
						update();
				}
			});
		} else
			throw new IllegalStateException("Can stay only on a RootPaneContainer");
	}

	private void initSlidingListeners() {
		addPropertyChangeListener("anchor", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				assert "anchor".equals(evt.getPropertyName());
				ToolWindow evtToolWindow = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
				if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible() && !evtToolWindow.isVisible())
					update();
			}
		});
		addPropertyChangeListener("type", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getSource() != descriptor)
					return;

				assert "type".equals(evt.getPropertyName());
				if (evt.getNewValue() == ToolWindowType.SLIDING) {
					if (glassPane != null) {
						sheet.addMouseMotionListener(slidingMouseInputHandler);
						sheet.addMouseListener(slidingMouseInputHandler);
					}
				} else {
					if (glassPane != null) {
						sheet.removeMouseMotionListener(slidingMouseInputHandler);
						sheet.removeMouseListener(slidingMouseInputHandler);
					}
				}
			}
		});

		slidingMouseInputHandler = new SlidingMouseInputHandler(descriptor);
	}


	private void configureSlidingIcons() {
		setPinVisible(false);
		setFloatingVisible(true);
		setSliding();
	}


	private class SlidingAnimation extends AbstractAnimation {
		private int length;
		private Rectangle bounds;
		private int lastLen = 0;

		public SlidingAnimation() {
			super(100f);
		}

		protected void onStartAnimation(Direction direction) {
			lastLen = 0;
			switch (toolWindow.getAnchor()) {
				case LEFT:
				case RIGHT:
					length = bounds.width;
					break;
				case TOP:
				case BOTTOM:
					length = bounds.height;
					break;
			}
		}

		protected void onFinishAnimation() {
			switch (getAnimationDirection()) {
				case INCOMING:
					sheet.setBounds(bounds);
					break;
				case OUTGOING:
					glassPane.remove(sheet);
					sheet.setBorder(null);
					sheet.removeAll();
					break;
			}
		}

		protected void onHide(Object... params) {
			this.bounds = (Rectangle) params[0];
		}

		protected void onShow(Object... params) {
			this.bounds = (Rectangle) params[0];

			switch (toolWindow.getAnchor()) {
				case LEFT:
					sheet.setSize(0, sheet.getHeight());
					break;
				case RIGHT:
					sheet.setLocation(sheet.getX() + sheet.getWidth(), sheet.getY());
					sheet.setSize(0, sheet.getHeight());
					break;
				case TOP:
					sheet.setSize(sheet.getWidth(), 0);
					break;
				case BOTTOM:
					sheet.setLocation(sheet.getX(), sheet.getY() + sheet.getHeight());
					sheet.setSize(sheet.getWidth(), 0);
					break;
			}
		}

		protected float onAnimating(float animationPercent) {
			int animatingLength = 0;

			Direction direction = getAnimationDirection();
			switch (toolWindow.getAnchor()) {
				case LEFT:
					if (direction == Direction.INCOMING)
						animatingLength = (int) (animationPercent * length);
					else
						animatingLength = (int) ((1f - animationPercent) * length);
					sheet.setSize(animatingLength, sheet.getHeight());
					break;
				case RIGHT:
					animatingLength = (int) (animationPercent * length);
					if (direction == Direction.INCOMING) {
						sheet.setLocation(sheet.getX() - (animatingLength - lastLen), sheet.getY());
						sheet.setSize(animatingLength, sheet.getHeight());
					} else {
						sheet.setLocation(bounds.x + animatingLength, sheet.getY());
						sheet.setSize((int) ((1f - animationPercent) * length), sheet.getHeight());
					}
					break;
				case TOP:
					if (direction == Direction.INCOMING)
						animatingLength = (int) (animationPercent * length);
					else
						animatingLength = (int) ((1f - animationPercent) * length);
					sheet.setSize(sheet.getWidth(), animatingLength);
					break;
				case BOTTOM:
					animatingLength = (int) (animationPercent * length);
					if (direction == Direction.INCOMING) {
						sheet.setLocation(sheet.getX(), sheet.getY() - (animatingLength - lastLen));
						sheet.setSize(sheet.getWidth(), animatingLength);
					} else {
						sheet.setLocation(sheet.getX(), bounds.y + animatingLength);
						sheet.setSize(sheet.getWidth(), (int) ((1f - animationPercent) * length));
					}

					break;
			}
			sheet.validate();
			sheet.repaint();

			lastLen = animatingLength;

			return animationPercent;
		}

		protected Direction chooseFinishDirection(Type type) {
			return (type == Type.SHOW) ? Direction.NONE : super.chooseFinishDirection(type);
		}

	}
}
