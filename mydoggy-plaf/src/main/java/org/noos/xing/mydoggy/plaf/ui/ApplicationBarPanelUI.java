package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class ApplicationBarPanelUI extends PanelUI {
    private ToolWindowDescriptor descriptor;

    private Color backStartEnabled;
    private Color backEndEnabled;
    private Color backStartDisabled;
    private Color backEndDisabled;

    private Color animBackStart;
    private Color animBackEnd;
    private Color animTextColor;

    private JComponent panel;

    private GradientAnimation animation;

    public ApplicationBarPanelUI(ToolWindowDescriptor descriptor, DockedContainer dockedContainer) {
        this.descriptor = descriptor;

        dockedContainer.addPropertyChangeListener("active", new GradientActivationListener(descriptor));

        backStartEnabled = new Color(145, 181, 255);
        backEndEnabled = new Color(96, 123, 183);

        backStartDisabled = new Color(193, 189, 182);
        backEndDisabled = new Color(167, 164, 157);

        animation = new GradientAnimation();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        installDefaults(c);
        this.panel = c;
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        uninstallDefaults(c);
    }

    public void update(Graphics g, JComponent c) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;
        if (animation.isAnimating()) {
            GraphicsUtil.fillRect(g, r, animBackStart, animBackEnd,
                                  null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            String id = descriptor.getToolWindow().getId();
            r.width = g.getFontMetrics().stringWidth(id) + 8;

            GraphicsUtil.fillRect(g, r, Color.WHITE, Color.LIGHT_GRAY,
                                  new Polygon(new int[]{r.x, r.x + r.width - 6, r.x + r.width, r.x},
                                              new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                              4),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            g.setColor(animTextColor);
            g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
        } else if (c.isEnabled()) {
            GraphicsUtil.fillRect(g, r, backStartEnabled, backEndEnabled,
                                  null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            String id = descriptor.getToolWindow().getId();
            r.width = g.getFontMetrics().stringWidth(id) + 8;

            GraphicsUtil.fillRect(g, r, Color.WHITE, Colors.lightBlu,
                                  new Polygon(new int[]{r.x, r.x + r.width - 6, r.x + r.width, r.x},
                                              new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                              4),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            g.setColor(Color.BLACK);
            g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
        } else {
            GraphicsUtil.fillRect(g, r, backStartDisabled, backEndDisabled,
                                  null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            String id = descriptor.getToolWindow().getId();
            r.width = g.getFontMetrics().stringWidth(id) + 8;

            GraphicsUtil.fillRect(g, r, Color.WHITE, Color.LIGHT_GRAY,
                                  new Polygon(new int[]{r.x, r.x + r.width - 6, r.x + r.width, r.x},
                                              new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                              4),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            g.setColor(Color.GRAY);
            g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
        }
        paint(g, c);
    }


    protected void installDefaults(JComponent c) {
        LookAndFeel.installColorsAndFont(c, "Panel.background", "Panel.foreground", "Panel.font");
        LookAndFeel.installBorder(c, "Panel.border");
    }

    protected void uninstallDefaults(JComponent c) {
        LookAndFeel.uninstallBorder(c);
    }

    private class GradientActivationListener implements PropertyChangeListener {
        public static final float ANIMATION_DURATION = 80f;
        public static final int ANIMATION_SLEEP = 10;

        private ToolWindowDescriptor descriptor;

        public GradientActivationListener(ToolWindowDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        public synchronized void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor || !descriptor.getToolWindow().isVisible())
                return;

            assert evt.getPropertyName() != null;
            assert descriptor.getToolWindow().isVisible();

            if ("active".equals(evt.getPropertyName())) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    if (animBackStart == null || animBackStart.equals(backStartEnabled))
                        animation.hide();
                } else {
                    if (animBackStart == null || animBackStart.equals(backStartDisabled))
                        animation.show();
                }
            }
        }
    }

    private class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(300f);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    animBackStart = GraphicsUtil.getInterpolatedColor(backStartEnabled, backStartDisabled, animationPercent);
                    animBackEnd = GraphicsUtil.getInterpolatedColor(backEndEnabled, backEndDisabled, animationPercent);
                    animTextColor = GraphicsUtil.getInterpolatedColor(Color.BLACK, Color.GRAY, animationPercent);
                    break;

                case OUTGOING:
                    animBackStart = GraphicsUtil.getInterpolatedColor(backStartDisabled, backStartEnabled, animationPercent);
                    animBackEnd = GraphicsUtil.getInterpolatedColor(backEndDisabled, backEndEnabled, animationPercent);
                    animTextColor = GraphicsUtil.getInterpolatedColor(Color.GRAY, Color.BLACK, animationPercent);
                    break;
            }
            SwingUtil.repaint(panel);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    animBackStart = backStartEnabled;
                    break;
                case OUTGOING:
                    animBackStart = backStartDisabled;
                    break;
            }
            SwingUtil.repaint(panel);
        }

        protected void onHide(Object... params) {
            animBackStart = backStartEnabled;
            animBackEnd = backEndEnabled;
        }

        protected void onShow(Object... params) {
            animBackStart = backStartDisabled;
            animBackEnd = backEndDisabled;
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }

    }
}
