package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.AnimationListener;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.RepresentativeAnchorBalloonTip;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentComponent;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RepresentativeAnchorBalloonTipUI extends BasicPanelUI implements PropertyChangeListener {

    public static ComponentUI createUI(JComponent c) {
        return new RepresentativeAnchorBalloonTipUI();
    }

    protected RepresentativeAnchorBalloonTip balloonTip;
    protected JLabel label;
    protected RootPaneContainer rootPaneContainer;
    protected JLayeredPane layeredPane;

    protected TransparencyAnimation transparencyAnimation;
    protected float transparencyAlpha = 0.0f;

    protected int hOffset;
    protected int vOffset;


    @Override
    public void installUI(JComponent c) {
        this.balloonTip = (RepresentativeAnchorBalloonTip) c;
        super.installUI(c);
    }

    @Override
    protected void installDefaults(final JPanel p) {
        super.installDefaults(p);

        this.transparencyAnimation = new TransparencyAnimation(
                new TranslucentComponent() {
                    public void setAlphaModeRatio(float transparency) {
                        if (transparencyAnimation.getAnimationDirection() == AbstractAnimation.Direction.OUTGOING){
                            transparencyAlpha = transparency;
                        } else {
                            transparencyAlpha = 1.0f - transparency;
                        }

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                balloonTip.repaint();
                            }
                        });
                    }

                    public float getAlphaModeEnabled() {
                        return transparencyAlpha;
                    }
                },
                balloonTip,
                SwingUtil.getFloat("RepresentativeAnchorBalloonTipUI.animation.alpha", 0.15f),
                SwingUtil.getInt("RepresentativeAnchorBalloonTipUI.animation.duration", 500)
        );
        transparencyAnimation.addAnimationListener(new AnimationListener() {
            public void onFinished() {
                if (transparencyAnimation.getAnimationDirection() == AbstractAnimation.Direction.OUTGOING)
                    balloonTip.setVisible(false);
            }
        });

        hOffset = SwingUtil.getInt("RepresentativeAnchorBalloonTipUI.hOffset", 15);
        vOffset = SwingUtil.getInt("RepresentativeAnchorBalloonTipUI.vOffset", 15);

        p.setLayout(new TableLayout(new double[][]{{-1}, {-1}}));
        p.setBorder(new RoundedBalloonBorder(SwingUtil.getInt("RepresentativeAnchorBalloonTipUI.arcWidth", 7),
                                             SwingUtil.getInt("RepresentativeAnchorBalloonTipUI.arcHeight", 7),
                                             SwingUtil.getColor("RepresentativeAnchorBalloonTipUI.fillColor", new Color(193, 240, 193)),
                                             SwingUtil.getColor("RepresentativeAnchorBalloonTipUI.borderColor", Color.GRAY)));
        p.setOpaque(false);

        int borderLength = SwingUtil.getInt("RepresentativeAnchorBalloonTipUI.borderLength", 15);
        label = new JLabel();
        label.setBorder(new EmptyBorder(borderLength, borderLength, borderLength, borderLength));
        label.setFont(label.getFont().deriveFont(Font.PLAIN));
        p.add(label, "0,0,FULL,FULL");

        if (rootPaneContainer != null) {
            layeredPane = rootPaneContainer.getLayeredPane();
            layeredPane.setLayer(p, JLayeredPane.DEFAULT_LAYER + 5);
            layeredPane.add(p);
        }

        // don't allow to click 'through' the component
        p.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });

        p.addPropertyChangeListener(this);
    }

    @Override
    protected void uninstallDefaults(JPanel p) {
        super.uninstallDefaults(p);

        p.removePropertyChangeListener(this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("rootPaneContainer".equals(propertyName)) {
            this.rootPaneContainer = (RootPaneContainer) evt.getNewValue();

            if (rootPaneContainer != null) {
                if (layeredPane != null) {
                    layeredPane.remove(balloonTip);
                }

                layeredPane = rootPaneContainer.getLayeredPane();
                layeredPane.setLayer(balloonTip, JLayeredPane.DEFAULT_LAYER + 5);
                layeredPane.add(balloonTip);
            }
        } else if ("text".equals(propertyName)) {
            label.setText((String) evt.getNewValue());
        } else if ("icon".equals(propertyName)) {
            label.setIcon((Icon) evt.getNewValue());
        }
    }

    public void updateLocation(boolean visible) {
        if (rootPaneContainer != null) {
            layeredPane = rootPaneContainer.getLayeredPane();
            layeredPane.remove(balloonTip);
        }
        rootPaneContainer = SwingUtil.getParent(balloonTip.getRepresentativeAnchor(), RootPaneContainer.class);

        Point source = balloonTip.getRepresentativeAnchor().getLocation();
        Dimension size = balloonTip.getRepresentativeAnchor().getSize();
        source.setLocation(source.x + (size.width / 2), source.y + (size.height / 2));


        Point finalLocatioon = SwingUtil.convertPoint(balloonTip.getRepresentativeAnchor(), source, (Component) rootPaneContainer);
        Dimension finalSize = balloonTip.getPreferredSize();

        switch (balloonTip.getRepresentativeAnchorDescriptor().getAnchor()) {
            case TOP:
                finalLocatioon.setLocation(finalLocatioon.getX() - (finalSize.getWidth() / 2), finalLocatioon.getY());
                break;
            case LEFT:
                finalLocatioon.setLocation(finalLocatioon.getX(), finalLocatioon.getY() - (finalSize.getHeight() / 2));
                break;
            case RIGHT:
                finalLocatioon.setLocation(finalLocatioon.getX() - finalSize.getWidth(), finalLocatioon.getY() - (finalSize.getHeight() / 2));
                break;
            case BOTTOM:
                finalLocatioon.setLocation(finalLocatioon.getX() - (finalSize.getWidth() / 2), finalLocatioon.getY() - finalSize.getHeight());
                break;
        }

        balloonTip.setLocation(finalLocatioon);
        balloonTip.setSize(finalSize);

        layeredPane = rootPaneContainer.getLayeredPane();
        layeredPane.setLayer(balloonTip, JLayeredPane.DEFAULT_LAYER + 5);
        layeredPane.add(balloonTip);

        transparencyAnimation.show();
    }

    public void showTip() {
        transparencyAlpha = 0.0f;
        updateLocation(true);
        balloonTip.setVisible(true);
    }

    public void hideTip() {
        transparencyAnimation.hide();
    }


    public class RoundedBalloonBorder implements Border {

        int arcWidth;
        int arcHeight;

        Color fillColor;
        Color borderColor;

        Dimension lastSize;
        Insets insets = new Insets(0, 0, 0, 0);


        public RoundedBalloonBorder(int arcWidth, int arcHeight, Color fillColor, Color borderColor) {
            this.arcWidth = arcWidth;
            this.arcHeight = arcHeight;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
        }


        public Insets getBorderInsets(Component c) {
            Dimension currentSize = c.getSize();

            switch (balloonTip.getRepresentativeAnchorDescriptor().getAnchor()) {
                case BOTTOM:
                    insets = new Insets(0, 0, vOffset, 0);
                    lastSize = currentSize;

                    return insets;
                case TOP:
                    insets = new Insets(vOffset, 0, 0, 0);
                    lastSize = currentSize;

                    return insets;
                case LEFT:
                    insets = new Insets(0, hOffset, 0, 0);
                    lastSize = currentSize;

                    return insets;
                case RIGHT:
                    insets = new Insets(0, 0, 0, hOffset);
                    lastSize = currentSize;

                    return insets;
            }
            throw new IllegalStateException("Invalid position...");
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int bWidth, int bHeight) {
            Graphics2D g2D = (Graphics2D) g;

            Composite oldComposite = g2D.getComposite();
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparencyAlpha));
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            try {
                switch (balloonTip.getRepresentativeAnchorDescriptor().getAnchor()) {
                    case BOTTOM:
                        g.setColor(fillColor);
                        g.fillRoundRect(x, y, bWidth, bHeight - vOffset, arcWidth * 2, arcHeight * 2);

                        g.setColor(borderColor);
                        g.drawRoundRect(x, y, bWidth - 1, bHeight - vOffset - 1, arcWidth * 2, arcHeight * 2);

                        int[] xPoints = new int[]{x + (bWidth / 2) - 7, x + (bWidth / 2), x + (bWidth / 2) + 7};
                        int[] yPoints = new int[]{y + bHeight - vOffset - 1, y + bHeight, y + bHeight - vOffset - 1};

                        g.setColor(fillColor);
                        g.fillPolygon(xPoints, yPoints, 3);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);

                        g.setColor(borderColor);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                        g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
                        break;
                    case TOP:
                        g.setColor(fillColor);
                        g.fillRoundRect(x, y + vOffset, bWidth, bHeight - vOffset, arcWidth * 2, arcHeight * 2);

                        g.setColor(borderColor);
                        g.drawRoundRect(x, y + vOffset, bWidth - 1, bHeight - vOffset - 1, arcWidth * 2, arcHeight * 2);

                        xPoints = new int[]{x + (bWidth / 2) - 7, x + (bWidth / 2), x + (bWidth / 2) + 7};
                        yPoints = new int[]{y + vOffset, y + 1, y + vOffset};

                        g.setColor(fillColor);
                        g.fillPolygon(xPoints, yPoints, 3);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);

                        g.setColor(borderColor);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                        g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
                        break;
                    case LEFT:
                        g.setColor(fillColor);
                        g.fillRoundRect(x + hOffset, y, bWidth - hOffset, bHeight, arcWidth * 2, arcHeight * 2);

                        g.setColor(borderColor);
                        g.drawRoundRect(x + hOffset, y, bWidth - hOffset - 1, bHeight - 1, arcWidth * 2, arcHeight * 2);

                        xPoints = new int[]{x + hOffset, x + 1, x + hOffset};
                        yPoints = new int[]{y + (bHeight / 2) + 7, y + (bHeight / 2), y + (bHeight / 2) - 7};

                        g.setColor(fillColor);
                        g.fillPolygon(xPoints, yPoints, 3);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);

                        g.setColor(borderColor);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                        g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
                        break;
                    case RIGHT:
                        g.setColor(fillColor);
                        g.fillRoundRect(x, y, bWidth - hOffset, bHeight, arcWidth * 2, arcHeight * 2);

                        g.setColor(borderColor);
                        g.drawRoundRect(x, y, bWidth - hOffset - 1, bHeight - 1, arcWidth * 2, arcHeight * 2);

                        xPoints = new int[]{x + bWidth - hOffset - 1, x + bWidth, x + bWidth - hOffset - 1};
                        yPoints = new int[]{y + (bHeight / 2) + 7, y + (bHeight / 2), y + (bHeight / 2) - 7};

                        g.setColor(fillColor);
                        g.fillPolygon(xPoints, yPoints, 3);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);

                        g.setColor(borderColor);
                        g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                        g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
                        break;
                }
            } finally {
//                g2D.setComposite(oldComposite);
            }
        }
    }

}
