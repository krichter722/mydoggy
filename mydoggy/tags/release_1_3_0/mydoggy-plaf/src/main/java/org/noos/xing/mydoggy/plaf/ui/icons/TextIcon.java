package org.noos.xing.mydoggy.plaf.ui.icons;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class TextIcon implements Icon, PropertyChangeListener {
    public static final int ROTATE_DEFAULT = 0;
    public static final int ROTATE_NONE = 1;
    public static final int ROTATE_LEFT = 2;
    public static final int ROTATE_RIGHT = 3;

    static final double NINETY_DEGREES = Math.toRadians(90.0);
    static final int kBufferSpace = 5;

    protected Color foreground;
    protected Component component;
    protected String text;
    protected int rotation;

    protected int fWidth;
    protected int fHeight;
    protected int fDescent;

    public TextIcon(Component component, String text) {
        this(component, text, ROTATE_DEFAULT);
    }

    public TextIcon(Component component, String text, int rotateHint) {
        this.component = component;
        this.text = text;
        rotation = rotateHint;
        calcDimensions();
        this.component.addPropertyChangeListener(this);
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(foreground != null ? foreground : c.getForeground());
        g.setFont(c.getFont());

        switch (rotation) {
            case ROTATE_NONE:
                g.drawString(text, x + kBufferSpace, y + fHeight - fDescent);
                break;
            case ROTATE_LEFT:
                Graphics2D g2D = (Graphics2D) g;
                g2D.translate(x + fWidth, y + fHeight);
                g2D.rotate(-NINETY_DEGREES);
                g2D.drawString(text, kBufferSpace, -fDescent);
                g2D.rotate(NINETY_DEGREES);
                g2D.translate(-(x + fWidth), -(y + fHeight));
                break;
            case ROTATE_RIGHT:
                g2D = (Graphics2D) g;
                g2D.translate(x, y);
                g2D.rotate(NINETY_DEGREES);
                g2D.drawString(text, kBufferSpace, -fDescent);
                g2D.rotate(-NINETY_DEGREES);
                g2D.translate(-x, -y);
        }
    }

    public int getIconWidth() {
        return fWidth;
    }

    public int getIconHeight() {
        return fHeight;
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if ("font".equals(prop)) {
            recalcDimensions();
        }
    }

    public void setText(String text) {
        this.text = text;
        recalcDimensions();
    }

    public Component getComponent() {
        return component;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    protected void recalcDimensions() {
        int wOld = getIconWidth();
        int hOld = getIconHeight();
        calcDimensions();
        if (wOld != getIconWidth() || hOld != getIconHeight())
            component.invalidate();
    }

    protected void calcDimensions() {
        FontMetrics fm = component.getFontMetrics(component.getFont());

        int fCharHeight = fm.getAscent() + fm.getDescent();
        fDescent = fm.getDescent();

        if (rotation == ROTATE_NONE) {
            fHeight = fCharHeight;
            fWidth = fm.stringWidth(text) + 2 * kBufferSpace;
        } else {
            fWidth = fCharHeight;
            fHeight = fm.stringWidth(text) + 2 * kBufferSpace;
        }
    }

}