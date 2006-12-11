package org.noos.xing.mydoggy.plaf.ui.icons;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro
 */
public class CompositeIcon implements Icon, SwingConstants {
    protected Icon leftIcon;
    protected Icon rightIcon;

    protected boolean leftVisible;
    protected boolean rightVisible;

    protected Rectangle lastPaintedLeftRec;
    protected Rectangle lastPaintedRightRec;

    protected int position;
    protected int horizontalOrientation;
    protected int verticalOrientation;

    public CompositeIcon(Icon leftIcon, Icon rightVisible) {
        this(leftIcon, rightVisible, TOP);
    }

    public CompositeIcon(Icon leftIcon, Icon rightVisible, int position) {
        this(leftIcon, rightVisible, position, CENTER, CENTER);
    }

    public CompositeIcon(Icon leftIcon, Icon rightVisible, int position, int horizontalOrientation, int verticalOrientation) {
        this.position = position;

        switch (position) {
            case LEFT:
                this.leftIcon = leftIcon;
                this.rightIcon = rightVisible;
                break;
            case RIGHT:
                this.leftIcon = rightVisible;
                this.rightIcon = leftIcon;
                break;
            case TOP:
                this.leftIcon = leftIcon;
                this.rightIcon = rightVisible;
                break;
            case BOTTOM:
                this.leftIcon = rightVisible;
                this.rightIcon = leftIcon;
                break;
        }

        this.horizontalOrientation = horizontalOrientation;
        this.verticalOrientation = verticalOrientation;

        this.leftVisible = this.rightVisible = true;
    }


    public void paintIcon(Component c, Graphics g, int x, int y) {
        int width = getIconWidth();
        int height = getIconHeight();

        switch (position) {
            case LEFT:
            case RIGHT:
                if (leftIcon != null && isLeftVisible()) {
                    paintLeftIcon(c, g, leftIcon, x, y, width, height, horizontalOrientation, verticalOrientation);
                    if (isRightVisible())
                        paintRightIcon(c, g, rightIcon, x + leftIcon.getIconWidth(), y , width, height, horizontalOrientation, TOP);
                } else
                    if (isRightVisible())
                        paintRightIcon(c, g, rightIcon, x, y, width, height, horizontalOrientation, TOP);
                break;
            case TOP:
            case BOTTOM:
                if (leftIcon != null && isLeftVisible()) {
                    paintLeftIcon(c, g, leftIcon, x, y, width, height, horizontalOrientation, TOP);
                    if (isRightVisible())
                        paintRightIcon(c, g, rightIcon, x, y + leftIcon.getIconHeight(), width, height, horizontalOrientation, TOP);
                } else
                    if (isRightVisible())
                        paintRightIcon(c, g, rightIcon, x, y, width, height, horizontalOrientation, TOP);
                break;
            default:
                if (isLeftVisible())
                    paintLeftIcon(c, g, leftIcon, x, y, width, height, horizontalOrientation, verticalOrientation);
                if (isRightVisible())
                    paintRightIcon(c, g, rightIcon, x, y, width, height, horizontalOrientation, verticalOrientation);
        }
    }

    public int getIconWidth() {
        if (position == LEFT || position == RIGHT)
            return (isLeftVisible() ? getIconWidth(leftIcon) : 0) + (isRightVisible() ? getIconWidth(rightIcon) : 0);
        return Math.max(isLeftVisible() ? getIconWidth(leftIcon) : 0,
                        isRightVisible() ? getIconWidth(rightIcon) : 0);
    }

    public int getIconHeight() {
        if (position == TOP || position == BOTTOM)
            return getIconHeight(leftIcon) + getIconHeight(rightIcon);
        return Math.max(getIconHeight(leftIcon), getIconHeight(rightIcon));
    }


    public Icon getLeftIcon() {
        return leftIcon;
    }

    public Icon getRightIcon() {
        return rightIcon;
    }

    public boolean isLeftVisible() {
        return leftVisible;
    }

    public void setLeftVisible(boolean leftVisible) {
        this.leftVisible = leftVisible;
    }

    public boolean isRightVisible() {
        return rightVisible;
    }

    public void setRightVisible(boolean rightVisible) {
        this.rightVisible = rightVisible;
    }

    public Rectangle getLastPaintedLeftRec() {
        return lastPaintedLeftRec;
    }

    public Rectangle getLastPaintedRightRec() {
        return lastPaintedRightRec;
    }


    protected void paintLeftIcon(Component c, Graphics g, Icon icon, int x, int y, int width, int height,
                                 int horizontalOrientation, int verticalOrientation) {
        Point p = paintIconInternal(c, g,
                          icon,
                          x, y, width, height, horizontalOrientation, verticalOrientation);
        lastPaintedLeftRec = new Rectangle(p.x, p.y, getIconWidth(icon), getIconHeight(icon));
    }

    protected void paintRightIcon(Component c, Graphics g, Icon icon, int x, int y, int width, int height,
                                  int horizontalOrientation, int verticalOrientation) {
        Point p = paintIconInternal(c, g,
                          icon,
                          x, y, width, height, horizontalOrientation, verticalOrientation);
        lastPaintedRightRec = new Rectangle(p.x, p.y, getIconWidth(icon), getIconHeight(icon));
    }


    protected Point paintIconInternal(Component c, Graphics g, Icon icon, int x, int y, int width, int height,
                                     int horizontalOrientation, int verticalOrientation) {
        if (icon == null)
            return new Point(x,y);

        int xIcon = x;
        int yIcon = y;

        switch (horizontalOrientation) {
            case LEFT:
                xIcon = x;
                break;
            case RIGHT:
                xIcon = x + width - icon.getIconWidth();
                break;
            default:
                xIcon = x + (width - icon.getIconWidth() >> 1);
                break;
        }

        switch (verticalOrientation) {
            case TOP:
                yIcon = y;
                break;
            case BOTTOM:
                yIcon = y + height - icon.getIconHeight();
                break;
            default:
                yIcon = y + (height - icon.getIconHeight() >> 1);
                break;
        }

        icon.paintIcon(c, g, xIcon, yIcon);
        return new Point(xIcon, yIcon);
    }

    protected int getIconWidth(Icon icon) {
        return (icon != null) ? icon.getIconWidth() : 0;
    }

    protected int getIconHeight(Icon icon) {
        return (icon != null) ? icon.getIconHeight() : 0;
    }

}