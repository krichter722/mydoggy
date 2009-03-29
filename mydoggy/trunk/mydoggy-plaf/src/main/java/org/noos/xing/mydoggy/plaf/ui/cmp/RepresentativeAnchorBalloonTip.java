package org.noos.xing.mydoggy.plaf.ui.cmp;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RepresentativeAnchorBalloonTip extends JPanel {

    enum Position {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    enum Angle {
        LEFT,
        RIGHT
    }

    protected JLabel label = new JLabel();
    protected Position position;
    protected Angle angle;
    protected boolean reset = true;
    protected JLayeredPane layeredPane;
    protected int hOffset;
    protected int vOffset;

    public RepresentativeAnchorBalloonTip() {
        this(null,
             Color.BLACK, new Color(255, 255, 225),
             10,
             15, 15,
             7, 7);
    }

    public RepresentativeAnchorBalloonTip(RootPaneContainer rootPaneContainer) {
        this(rootPaneContainer,
             Color.BLACK, new Color(255, 255, 225),
             10,
             15, 15,
             7, 7);
    }

    public RepresentativeAnchorBalloonTip(RootPaneContainer rootPaneContainer, Color borderColor, Color fillColor,
                       int borderWidth, int hOffset, int vOffset,
                       int arcWidth, int arcHeight) {
        this.position = Position.LEFT;
        this.angle = Angle.RIGHT;
        this.hOffset = hOffset;
        this.vOffset = vOffset;

        setBorder(new RoundedBalloonBorder(arcWidth, arcHeight, fillColor, borderColor));
        setOpaque(false);
        setLayout(new GridBagLayout());

        label.setBorder(new EmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth));
        add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        if (rootPaneContainer != null) {
            layeredPane = rootPaneContainer.getLayeredPane();
            layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 5);
            layeredPane.add(this);
        }

        // don't allow to click 'through' the component
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });
    }

    public void setVisible(boolean show) {
        if (show) {
            determineAndSetLocation();
        }
        super.setVisible(show);
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
        System.out.println("x = " + x + " - " + "y = " + y);
        Dimension size = getPreferredSize();

        if (y + size.height > layeredPane.getHeight()) {
            y -= (size.height + vOffset);
            setPosition(Position.BOTTOM);
        } else {
            setPosition(Position.TOP);
        }

        if (x + size.width > layeredPane.getWidth()) {
            x -= (size.width + hOffset);
            hOffset = size.width - 30;
            setAngle(Angle.RIGHT);
        } else {
            setAngle(Angle.LEFT);
            hOffset = 15;
        }

        setLocation(x, y);
        setVisible(true);
    }

    public void setPosition(Position position) {
        this.position = position;
        reset = true;
        revalidate();
        repaint();
    }

    public void setAngle(Angle angle) {
        this.angle = angle;
        reset = true;
        revalidate();
        repaint();
    }

    public void setRootPaneContainer(RootPaneContainer rootPaneContainer) {
        if (rootPaneContainer != null) {
            if (layeredPane != null) {
                layeredPane.remove(this);
            }

            layeredPane = rootPaneContainer.getLayeredPane();
            layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 5);
            layeredPane.add(this);
        }
    }


    protected void determineAndSetLocation() {
//        Point location = SwingUtilities.convertPoint(attachedComponent, getLocation(), this);
        setSize(getPreferredSize().width,
                getPreferredSize().height);
        validate();
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
            if (reset) {
                lastSize = null;
                reset = false;
            } else if (currentSize.equals(lastSize))
                return insets;

            switch (position) {
                case BOTTOM:
                    int hInset = arcWidth;
                    int vInset = arcHeight;
                    insets = new Insets(vInset, hInset, vOffset + vInset, hInset);
                    lastSize = currentSize;

                    return insets;
                case TOP:
                    hInset = arcWidth;
                    vInset = arcHeight;
                    insets = new Insets(vOffset + vInset, hInset, vInset, hInset);
                    lastSize = currentSize;

                    return insets;
                case LEFT:
                    hInset = arcWidth;
                    vInset = arcHeight;
                    insets = new Insets(vInset, hOffset + hInset, vInset, hInset);
                    lastSize = currentSize;

                    return insets;
                case RIGHT:
                    hInset = arcWidth;
                    vInset = arcHeight;
                    insets = new Insets(vInset, hInset, vInset, hOffset + hInset);
                    lastSize = currentSize;

                    return insets;
            }
            throw new IllegalStateException("Invalid position...");
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int bWidth, int bHeight) {
            switch (position) {
                case BOTTOM:
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
                    break;
                case TOP:
                    g.setColor(fillColor);
                    g.fillRoundRect(x, y + vOffset, bWidth, bHeight - vOffset, arcWidth * 2, arcHeight * 2);

                    g.setColor(borderColor);
                    g.drawRoundRect(x, y + vOffset, bWidth - 1, bHeight - vOffset - 1, arcWidth * 2, arcHeight * 2);

                    switch (angle) {
                        case LEFT:
                            xPoints = new int[]{x + hOffset, x + hOffset, x + hOffset + vOffset, };
                            yPoints = new int[]{y + vOffset + 1, y + 1, y + vOffset + 1};
                            break;
                        case RIGHT:
                            xPoints = new int[]{x + hOffset, x + hOffset + vOffset, x + hOffset + vOffset};
                            yPoints = new int[]{y + vOffset + 1, y + 1, y + vOffset + 1};
                            break;
                        default:
                            throw new IllegalStateException();
                    }

                    g.setColor(fillColor);
                    g.fillPolygon(xPoints, yPoints, 3);

                    g.setColor(borderColor);
                    g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                    g.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);
                    break;
                case LEFT:
                    g.setColor(fillColor);
                    g.fillRoundRect(x + hOffset, y, bWidth - hOffset, bHeight, arcWidth * 2, arcHeight * 2);

                    g.setColor(borderColor);
                    g.drawRoundRect(x + hOffset, y, bWidth - hOffset - 1, bHeight - 1, arcWidth * 2, arcHeight * 2);

                    switch (angle) {
                        case LEFT:
                            xPoints = new int[]{x, x + hOffset + 1, x + hOffset + 1};
                            yPoints = new int[]{y + vOffset, y + vOffset + hOffset, y + vOffset};
                            break;
                        case RIGHT:
                            xPoints = new int[]{x, x + hOffset + 1, x + hOffset + 1};
                            yPoints = new int[]{y + vOffset+ hOffset, y + vOffset, y + vOffset+ hOffset};
                            break;
                        default:
                            throw new IllegalStateException();
                    }


                    g.setColor(fillColor);
                    g.fillPolygon(xPoints, yPoints, 3);

                    g.setColor(borderColor);
                    g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                    g.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
                    break;
                case RIGHT:
                    g.setColor(fillColor);
                    g.fillRoundRect(x, y, bWidth - hOffset, bHeight, arcWidth * 2, arcHeight * 2);

                    g.setColor(borderColor);
                    g.drawRoundRect(x, y, bWidth - hOffset - 1, bHeight - 1, arcWidth * 2, arcHeight * 2);

                    switch (angle) {
                        case LEFT:
                            xPoints = new int[]{x + bWidth - hOffset, x + bWidth, x + bWidth - hOffset};
                            yPoints = new int[]{y + vOffset, y + vOffset, y + vOffset + hOffset};
                            break;
                        case RIGHT:
                            xPoints = new int[]{x + bWidth - hOffset, x + bWidth, x + bWidth - hOffset};
                            yPoints = new int[]{y + vOffset + hOffset, y + vOffset + hOffset, y + vOffset};
                            break;
                        default:
                            throw new IllegalStateException();
                    }

                    g.setColor(fillColor);
                    g.fillPolygon(xPoints, yPoints, 3);

                    g.setColor(borderColor);
                    g.drawLine(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
                    g.drawLine(xPoints[2], yPoints[2], xPoints[1], yPoints[1]);
                    break;
            }
        }

    }

}
