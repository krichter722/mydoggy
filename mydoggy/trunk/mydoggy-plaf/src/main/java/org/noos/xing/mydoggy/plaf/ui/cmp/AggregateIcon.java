package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AggregateIcon implements Icon, SwingConstants {
    private int orientation;

    private Icon leftIcon;
    private Icon rightIcon;

    private boolean leftVisible;
    private boolean rightVisible;

    private Rectangle lastPaintedLeftRec;
    private Rectangle lastPaintedRightRec;


    public AggregateIcon(Icon leftIcon, Icon rightIcon, int orientation) {
        this.leftIcon = leftIcon;
        this.rightIcon = rightIcon;
        this.orientation = orientation;
        this.leftVisible = this.rightVisible = true;
    }


    public void paintIcon(Component c, Graphics g, int x, int y) {
        boolean leftVisible = isLeftVisible();
        boolean rightVisible = isRightVisible();
        Icon leftIcon = getLeftIcon();
        Icon righIcon = getRightIcon();

        switch (orientation) {
            case HORIZONTAL:
                if (leftVisible && rightVisible) {

                    if (leftIcon.getIconHeight() > righIcon.getIconHeight()) {
                        leftIcon.paintIcon(c, g, x, y);
                        lastPaintedLeftRec = new Rectangle(x, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());

                        int rightX = x + leftIcon.getIconWidth();
                        int rightY = y + ((leftIcon.getIconHeight() - righIcon.getIconHeight()) / 2);

                        righIcon.paintIcon(c, g, rightX, rightY);
                        lastPaintedRightRec = new Rectangle(rightX, rightY,
                                                            righIcon.getIconWidth(), righIcon.getIconHeight());
                    } else if (leftIcon.getIconHeight() == righIcon.getIconHeight()) {
                        leftIcon.paintIcon(c, g, x, y);
                        lastPaintedLeftRec = new Rectangle(x, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());

                        int rightX = x + leftIcon.getIconWidth();
                        int rightY = y;

                        righIcon.paintIcon(c, g, rightX, rightY);
                        lastPaintedRightRec = new Rectangle(rightX, rightY,
                                                            righIcon.getIconWidth(), righIcon.getIconHeight());
                    } else {
                        int leftY = y + ((righIcon.getIconHeight() - leftIcon.getIconHeight()) / 2);
                        leftIcon.paintIcon(c, g, x, leftY);
                        lastPaintedLeftRec = new Rectangle(x, leftY, leftIcon.getIconWidth(), leftIcon.getIconHeight());

                        int rightX = x + leftIcon.getIconWidth();
                        int rightY = y;

                        righIcon.paintIcon(c, g, rightX, rightY);
                        lastPaintedRightRec = new Rectangle(rightX, rightY,
                                                            righIcon.getIconWidth(), righIcon.getIconHeight());
                    }
                } else if (leftVisible) {
                    leftIcon.paintIcon(c, g, x, y);
                    lastPaintedLeftRec = new Rectangle(x, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());
                    lastPaintedRightRec = null;
                } else if (rightVisible) {
                    righIcon.paintIcon(c, g, x, y);
                    lastPaintedRightRec = new Rectangle(x, y, righIcon.getIconWidth(), righIcon.getIconHeight());
                    lastPaintedLeftRec = null;
                }
                break;
            case VERTICAL:
                if (leftVisible && rightVisible) {

                    if (leftIcon.getIconWidth() > righIcon.getIconWidth()) {
                        leftIcon.paintIcon(c, g, x, y);
                        lastPaintedLeftRec = new Rectangle(x, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());

                        int rightX = x + ((leftIcon.getIconWidth() - righIcon.getIconWidth()) / 2);
                        int rightY = y + leftIcon.getIconHeight();

                        righIcon.paintIcon(c, g, rightX, rightY);
                        lastPaintedRightRec = new Rectangle(rightX, rightY,
                                                            righIcon.getIconWidth(), righIcon.getIconHeight());
                    } else if (leftIcon.getIconWidth() == righIcon.getIconWidth()) {
                        leftIcon.paintIcon(c, g, x, y);
                        lastPaintedLeftRec = new Rectangle(x, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());

                        int rightX = x;
                        int rightY = y + leftIcon.getIconHeight();

                        righIcon.paintIcon(c, g, rightX, rightY);
                        lastPaintedRightRec = new Rectangle(rightX, rightY,
                                                            righIcon.getIconWidth(), righIcon.getIconHeight());
                    } else {
                        int leftX = x + ((righIcon.getIconWidth() - leftIcon.getIconWidth()) / 2);
                        leftIcon.paintIcon(c, g, leftX, y);
                        lastPaintedLeftRec = new Rectangle(leftX, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());

                        int rightX = x;
                        int rightY = y + leftIcon.getIconHeight();

                        righIcon.paintIcon(c, g, rightX, rightY);
                        lastPaintedRightRec = new Rectangle(rightX, rightY,
                                                            righIcon.getIconWidth(), righIcon.getIconHeight());
                    }
                } else if (leftVisible) {
                    leftIcon.paintIcon(c, g, x, y);
                    lastPaintedLeftRec = new Rectangle(x, y, leftIcon.getIconWidth(), leftIcon.getIconHeight());
                    lastPaintedRightRec = null;
                } else if (rightVisible) {
                    righIcon.paintIcon(c, g, x, y);
                    lastPaintedRightRec = new Rectangle(x, y, righIcon.getIconWidth(), righIcon.getIconHeight());
                    lastPaintedLeftRec = null;
                }
                break;
        }
    }

    public int getIconWidth() {
        if (orientation == HORIZONTAL)
            return (isLeftVisible() ? SwingUtil.getIconWidth(getLeftIcon()) : 0) + (isRightVisible() ? SwingUtil.getIconWidth(getRightIcon()) : 0);

        return Math.max(isLeftVisible() ? SwingUtil.getIconWidth(getLeftIcon()) : 0,
                        isRightVisible() ? SwingUtil.getIconWidth(getRightIcon()) : 0);
    }

    public int getIconHeight() {
        if (orientation == VERTICAL)
            return SwingUtil.getIconHeight(getLeftIcon()) + SwingUtil.getIconHeight(getRightIcon());

        return Math.max(SwingUtil.getIconHeight(getLeftIcon()), SwingUtil.getIconHeight(getRightIcon()));
    }


    public Rectangle getLastPaintedLeftRec() {
        return lastPaintedLeftRec;
    }

    public Rectangle getLastPaintedRightRec() {
        return lastPaintedRightRec;
    }

    public Icon getLeftIcon() {
        return leftIcon;
    }

    public void setLeftIcon(Icon leftIcon) {
        this.leftIcon = leftIcon;
    }

    public Icon getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Icon rightIcon) {
        this.rightIcon = rightIcon;
    }

    public boolean isLeftVisible() {
        return leftVisible && getLeftIcon() != null;
    }

    public void setLeftVisible(boolean leftVisible) {
        this.leftVisible = leftVisible;
    }

    public boolean isRightVisible() {
        return rightVisible && getRightIcon() != null;
    }

    public void setRightVisible(boolean rightVisible) {
        this.rightVisible = rightVisible;
    }

}
