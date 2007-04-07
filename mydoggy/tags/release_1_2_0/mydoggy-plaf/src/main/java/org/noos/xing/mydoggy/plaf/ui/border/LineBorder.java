package org.noos.xing.mydoggy.plaf.ui.border;

import org.noos.xing.mydoggy.plaf.ui.util.Colors;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * @author Angelo De Caro
 */
public class LineBorder extends AbstractBorder {
    protected int thickness;
    protected Color lineColor;

    protected boolean roundedCorners;
    protected int arcHeight;
    protected int arcWidth;

    public LineBorder() {
        this(Colors.buttonBorder);
    }

    public LineBorder(Color color) {
        this(color, 1, false, 0, 0);
    }

    public LineBorder(Color color, int thickness) {
        this(color, thickness, false, 0, 0);
    }

    public LineBorder(Color lineColor, int thickness, boolean roundedCorners, int arcWidth, int arcHeight) {
        this.lineColor = lineColor;
        this.thickness = thickness;
        this.roundedCorners = roundedCorners;
        this.arcHeight = arcHeight;
        this.arcWidth = arcWidth;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        int i;

        g.setColor(lineColor);
        for (i = 0; i < thickness; i++) {
            if (!roundedCorners)
                g.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
            else
                g.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, arcWidth, arcHeight);
        }
        g.setColor(oldColor);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = thickness;
        return insets;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public int getThickness() {
        return thickness;
    }

    public boolean getRoundedCorners() {
        return roundedCorners;
    }

    public boolean isBorderOpaque() {
        return !roundedCorners;
    }

}
