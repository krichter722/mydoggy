package org.noos.xing.mydoggy.plaf.ui.icons;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro
 */
public class CompositeIcon implements Icon, SwingConstants {
    protected Icon icon1;
    protected Icon icon2;

    protected Rectangle icon1Rec;
    protected Rectangle icon2Rec;

    protected int position;
    protected int horizontalOrientation;
    protected int verticalOrientation;

    public CompositeIcon(Icon icon1, Icon icon2) {
        this(icon1, icon2, TOP);
    }

    public CompositeIcon(Icon icon1, Icon icon2, int position) {
        this(icon1, icon2, position, CENTER, CENTER);
    }

    public CompositeIcon(Icon icon1, Icon icon2, int position, int horizontalOrientation, int verticalOrientation) {
        this.icon1 = icon1;
        this.icon2 = icon2;
        this.position = position;
        this.horizontalOrientation = horizontalOrientation;
        this.verticalOrientation = verticalOrientation;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int width = getIconWidth();
        int height = getIconHeight();
        if (position == LEFT || position == RIGHT) {
            Icon leftIcon;
            Icon rightIcon;
            if (position == LEFT) {
                leftIcon = icon1;
                rightIcon = icon2;
            } else {
                leftIcon = icon2;
                rightIcon = icon1;
            }

            paintIconInternal(c, g, leftIcon, x, y, width, height, LEFT, verticalOrientation);
            paintIconInternal(c, g, rightIcon, x + leftIcon.getIconWidth(), y, width, height, LEFT, verticalOrientation);
        } else if (position == TOP || position == BOTTOM) {
            Icon topIcon;
            Icon bottomIcon;
            if (position == TOP) {
                topIcon = icon1;
                bottomIcon = icon2;
            } else {
                topIcon = icon2;
                bottomIcon = icon1;
            }

            paintIconInternal(c, g, topIcon, x, y, width, height, horizontalOrientation, TOP);
            paintIconInternal(c, g, bottomIcon, x, y + topIcon.getIconHeight(), width, height, horizontalOrientation, TOP);
        } else {

            paintIconInternal(c, g, icon1, x, y, width, height, horizontalOrientation, verticalOrientation);
            paintIconInternal(c, g, icon2, x, y, width, height, horizontalOrientation, verticalOrientation);
        }
    }

    public int getIconWidth() {
        if (position == LEFT || position == RIGHT)
            return getIconWidth(icon1) + getIconWidth(icon2);
        return Math.max(getIconWidth(icon1), getIconWidth(icon2));
    }

    public int getIconHeight() {
        if (position == TOP || position == BOTTOM)
            return getIconHeight(icon1) + getIconHeight(icon2);
        return Math.max(getIconHeight(icon1), getIconHeight(icon2));
    }


    public Icon getIcon1() {
        return icon1;
    }

    public Icon getIcon2() {
        return icon2;
    }

    public Rectangle getIcon1Rec() {
        return icon1Rec;
    }

    public Rectangle getIcon2Rec() {
        return icon2Rec;
    }
    

    protected void paintIconInternal(Component c, Graphics g, Icon icon, int x, int y, int width, int height,
                                     int horizontalOrientation, int verticalOrientation) {
        if (icon == null)
            return;
        int xIcon;
        int yIcon;
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
        paint(c, g, icon, xIcon, yIcon);
    }

    protected void paint(Component c, Graphics g, Icon icon, int x, int y) {
        icon.paintIcon(c, g, x, y);

        if (icon == icon1) {
            icon1Rec = new Rectangle(x, y, getIconWidth(icon1), getIconHeight(icon1));
        } else
            icon2Rec = new Rectangle(x, y, getIconWidth(icon2), getIconHeight(icon2));
    }

    protected int getIconWidth(Icon icon) {
        return (icon != null) ? icon.getIconWidth() : 0;
    }

    protected int getIconHeight(Icon icon) {
        return (icon != null) ? icon.getIconHeight() : 0;
    }

}