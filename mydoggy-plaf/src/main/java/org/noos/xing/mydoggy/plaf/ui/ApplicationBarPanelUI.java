package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class ApplicationBarPanelUI extends PanelUI {

    private Color startTemp;
    private Color endTemp;

    private Color startEnabled;
    private Color endEnabled;
    private Color startDisabled;
    private Color endDisabled;

    private boolean animating = false;

    private JPanel panel;

    public ApplicationBarPanelUI(ToolWindowDescriptor descriptor, DockedContainer dockedContainer) {
        dockedContainer.addPropertyChangeListener("active", new GradientActivationListener(descriptor));

        startEnabled = new Color(145, 181, 255);
        endEnabled = new Color(96, 123, 183);

        startDisabled = new Color(193, 189, 182);
        endDisabled = new Color(167, 164, 157);
    }

    public ApplicationBarPanelUI(Color startEnabled, Color endEnabled, Color startDisabled, Color endDisabled) {
        this.startEnabled = startEnabled;
        this.endEnabled = endEnabled;
        this.startDisabled = startDisabled;
        this.endDisabled = endDisabled;
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        installDefaults(c);
        this.panel = (JPanel) c;
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        uninstallDefaults(c);
    }

    protected void installDefaults(JComponent c) {
        LookAndFeel.installColorsAndFont(c, "Panel.background", "Panel.foreground", "Panel.font");
        LookAndFeel.installBorder(c, "Panel.border");
    }

    protected void uninstallDefaults(JComponent c) {
        LookAndFeel.uninstallBorder(c);
    }

    public void update(Graphics g, JComponent c) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;
        if (animating) {
            GraphicsUtil.fillRect(g, r, startTemp, endTemp,
                                  null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
        } else if (c.isEnabled())
            GraphicsUtil.fillRect(g, r, startEnabled, endEnabled,
                                  null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
        else
            GraphicsUtil.fillRect(g, r, startDisabled, endDisabled,
                                  null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
        paint(g, c);
//        super.update(g, c);
    }

    private class GradientActivationListener implements PropertyChangeListener {
        public static final float ANIMATION_DURATION = 80f;
        public static final int ANIMATION_SLEEP = 10;

        private ToolWindowDescriptor descriptor;
        private GradientAnimation animation;

        public GradientActivationListener(ToolWindowDescriptor descriptor) {
            this.descriptor = descriptor;
            this.animation = new GradientAnimation();
        }

        public synchronized void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor || !descriptor.getToolWindow().isVisible())
                return;

            assert evt.getPropertyName() != null;
            assert descriptor.getToolWindow().isVisible();

            if ("active".equals(evt.getPropertyName())) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    if (startTemp == null || startTemp.equals(startEnabled))
                        animation.hide();
                } else {
                    if (startTemp == null || startTemp.equals(startDisabled))
                        animation.show();
                }
            }
        }

        private class GradientAnimation implements ActionListener {
            public static final int INCOMING = 1;
            public static final int OUTGOING = -1;
            public static final float ANIMATION_DURATION = 300f;
            public static final int ANIMATION_SLEEP = 10;

            //            private boolean animating;
            private int animationDirection;
            private Timer animationTimer;
            private long animationStart;


            public void actionPerformed(ActionEvent e) {
                if (animating) {
                    // calculate height to show
                    float animationPercent = (System.currentTimeMillis() - animationStart) / ANIMATION_DURATION;
                    animationPercent = Math.min(1.0f, animationPercent);

                    switch (animationDirection) {
                        case INCOMING:
                            int r = (int) (animationPercent * Math.abs((startEnabled.getRed() - startDisabled.getRed())));
                            if (startEnabled.getRed() < startDisabled.getRed()) {
                                r = startDisabled.getRed() - r;
                            } else
                                r = startDisabled.getRed() + r;

                            int g = (int) (animationPercent * Math.abs((startEnabled.getGreen() - startDisabled.getGreen())));
                            if (startEnabled.getGreen() < startDisabled.getGreen()) {
                                g = startDisabled.getGreen() - g;
                            } else
                                g = startDisabled.getGreen() + g;


                            int b = (int) (animationPercent * Math.abs((startEnabled.getBlue() - startDisabled.getBlue())));
                            if (startEnabled.getBlue() < startDisabled.getBlue()) {
                                b = startDisabled.getBlue() - b;
                            } else
                                b = startDisabled.getBlue() + b;

                            startTemp = new Color(r, g, b);

                            r = (int) (animationPercent * Math.abs((endEnabled.getRed() - endDisabled.getRed())));
                            if (endEnabled.getRed() < endDisabled.getRed()) {
                                r = endDisabled.getRed() - r;
                            } else
                                r = endDisabled.getRed() + r;

                            g = (int) (animationPercent * Math.abs((endEnabled.getGreen() - endDisabled.getGreen())));
                            if (endEnabled.getGreen() < endDisabled.getGreen()) {
                                g = endDisabled.getGreen() - g;
                            } else
                                g = endDisabled.getGreen() + g;


                            b = (int) (animationPercent * Math.abs((endEnabled.getBlue() - endDisabled.getBlue())));
                            if (endEnabled.getBlue() < endDisabled.getBlue()) {
                                b = endDisabled.getBlue() - b;
                            } else
                                b = endDisabled.getBlue() + b;

                            endTemp = new Color(r, g, b);
                            break;

                        case OUTGOING:

                            r = (int) (animationPercent * Math.abs((startEnabled.getRed() - startDisabled.getRed())));
                            if (startEnabled.getRed() < startDisabled.getRed()) {
                                r = startEnabled.getRed() + r;
                            } else
                                r = startEnabled.getRed() - r;

                            g = (int) (animationPercent * Math.abs((startEnabled.getGreen() - startDisabled.getGreen())));
                            if (startEnabled.getGreen() < startDisabled.getGreen()) {
                                g = startEnabled.getGreen() + g;
                            } else
                                g = startEnabled.getGreen() - g;


                             b = (int) (animationPercent * Math.abs((startEnabled.getBlue() - startDisabled.getBlue())));
                            if (startEnabled.getBlue() < startDisabled.getBlue()) {
                                b = startEnabled.getBlue() + b;
                            } else
                                b = startEnabled.getBlue() - b;

                            startTemp = new Color(r, g, b);

                            r = (int) (animationPercent * Math.abs((endEnabled.getRed() - endDisabled.getRed())));
                            if (endEnabled.getRed() < endDisabled.getRed()) {
                                r = endEnabled.getRed() + r;
                            } else
                                r = endEnabled.getRed() - r;

                            g = (int) (animationPercent * Math.abs((endEnabled.getGreen() - endDisabled.getGreen())));
                            if (endEnabled.getGreen() < endDisabled.getGreen()) {
                                g = endEnabled.getGreen() + g;
                            } else
                                g = endEnabled.getGreen() - g;


                            b = (int) (animationPercent * Math.abs((endEnabled.getBlue() - endDisabled.getBlue())));
                            if (endEnabled.getBlue() < endDisabled.getBlue()) {
                                b = endEnabled.getBlue() + b;
                            } else
                                b = endEnabled.getBlue() - b;

                            endTemp = new Color(r, g, b);
                            break;
                    }

                    SwingUtil.repaint(panel);

                    if (animationPercent >= 1.0f) {
                        stopAnimation();
                        finishAnimation();
                    }
                }
            }


            public synchronized void show() {
                startTemp = startDisabled;
                endTemp = endDisabled;

                if (animating) {
                    stopAnimation();
                    animationDirection = OUTGOING;
                    finishAnimation();
                }

                startAnimation(INCOMING);
            }

            public synchronized void hide() {
                startTemp = startEnabled;
                endTemp = endEnabled;

                if (animating) {
                    stopAnimation();
                    animationDirection = INCOMING;
                    finishAnimation();
                }

                startAnimation(OUTGOING);
            }


            private void startAnimation(int incoming) {
                if (!animating) {
                    animationDirection = incoming;
                    animationStart = System.currentTimeMillis();
                    if (animationTimer == null)
                        animationTimer = new Timer(ANIMATION_SLEEP, this);
                    animating = true;
                    animationTimer.start();
                }
            }

            private void stopAnimation() {
                if (animationTimer != null)
                    animationTimer.stop();
                animating = false;
            }

            private void finishAnimation() {
                switch (animationDirection) {
                    case INCOMING:
                        startTemp = startEnabled;
                        break;
                    case OUTGOING:
                        startTemp = startDisabled;
                        break;
                }
                SwingUtil.repaint(panel);
            }
        }

    }

}
