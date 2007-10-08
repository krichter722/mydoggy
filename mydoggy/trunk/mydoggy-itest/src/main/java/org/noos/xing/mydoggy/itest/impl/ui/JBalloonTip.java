package org.noos.xing.mydoggy.itest.impl.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class JBalloonTip extends JPanel {
    private JLabel label = new JLabel();

    private Component attachedComponent;

    public static JBalloonTip createRoundedBalloonTip(Component attachedComponent,
                                                     Color borderColor, Color fillColor,
                                                     int borderWidth,
                                                     int horizontalOffset, int verticalOffset,
                                                     int arcWidth, int arcHeight,
                                                     boolean useCloseButton) {
        Border border = new RoundedBalloonBorder(arcHeight, arcHeight, horizontalOffset, verticalOffset, fillColor, borderColor);
        return new JBalloonTip(attachedComponent, border, fillColor, borderWidth, useCloseButton);
    }

    private JBalloonTip(Component attachedComponent,
                        Border border,
                        Color fillColor,
                        int borderWidth,
                        boolean useCloseButton) {
        this.attachedComponent = attachedComponent;

        setBorder(border);
        setOpaque(false);
        setLayout(new GridBagLayout());

        label.setBorder(new EmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth));
        add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // we use the popup layer of the top level container (frame or dialog) to show the balloon tip
        // first we need to determine the top level container...
        Container parent = attachedComponent.getParent();
        JLayeredPane layeredPane;
        while (true) {
            if (parent instanceof JFrame) {
                layeredPane = ((JFrame) parent).getLayeredPane();
                break;
            } else if (parent instanceof JDialog) {
                layeredPane = ((JDialog) parent).getLayeredPane();
                break;
            } else if (parent instanceof JInternalFrame) {
                layeredPane = ((JInternalFrame) parent).getLayeredPane();
                break;
            }
            parent = parent.getParent();
        }
        layeredPane.add(this, JLayeredPane.POPUP_LAYER);

        // if the attached component is moved while the balloon tip is visible, we need to move as well
        attachedComponent.addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                if (isShowing()) {
                    determineAndSetLocation();
                }
            }
        });

        // don't allow to click 'through' the component
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    private void determineAndSetLocation() {
//        Point location = SwingUtilities.convertPoint(attachedComponent, getLocation(), this);
        setSize(getPreferredSize().width,
                  getPreferredSize().height);
        validate();
    }

    public void setText(String text) {
        label.setText(text);
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    public void setIconTextGap(int iconTextGap) {
        label.setIconTextGap(iconTextGap);
    }

    public void show(int x, int y) {
        setLocation(x, y);
        setVisible(true);
    }

    public void setVisible(boolean show) {
        if (show) {
            determineAndSetLocation();
        }
        super.setVisible(show);
    }

    public static class RoundedBalloonBorder implements Border {

        private int arcWidth;
        private int arcHeight;
        private int hOffset;
        private int vOffset;

        private Color fillColor;
        private Color borderColor;

        Dimension lastSize;
        Insets insets = new Insets(0, 0, 0, 0);

        public RoundedBalloonBorder(int arcWidth, int arcHeight, int hOffset, int vOffset, Color fillColor, Color borderColor) {
            this.arcWidth = arcWidth;
            this.arcHeight = arcHeight;
            this.hOffset = hOffset;
            this.vOffset = vOffset;
            this.fillColor = fillColor;
            this.borderColor = borderColor;
        }

        public Insets getBorderInsets(Component c) {
            Dimension currentSize = c.getSize();

            if (currentSize.equals(lastSize)) {
                return insets;
            }

            int hInset = arcWidth;
            int vInset = arcHeight;
            insets = new Insets(vInset, hInset, vOffset + vInset, hInset);
            lastSize = currentSize;

            return insets;
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int bWidth, int bHeight) {
            g.setColor(fillColor);
            g.fillRoundRect(x, y, bWidth, bHeight - vOffset, arcWidth * 2, arcHeight * 2);
            g.setColor(borderColor);
            g.drawRoundRect(x, y, bWidth - 1, bHeight - vOffset - 1, arcWidth * 2, arcHeight * 2);
            int[] xPoints = {x + hOffset, x + hOffset + vOffset, x + hOffset};
            int[] yPoints = {y + bHeight - vOffset - 1, y + bHeight - vOffset - 1, y + bHeight - 1};
            g.setColor(fillColor);
            g.fillPolygon(xPoints, yPoints, 3);
            g.setColor(borderColor);
            g.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
            g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
        }

    }

}
