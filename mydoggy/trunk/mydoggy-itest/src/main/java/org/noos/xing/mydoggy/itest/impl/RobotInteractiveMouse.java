package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RobotInteractiveMouse implements InteractiveMouse {
    private InteractiveUI interactiveUI;
    private Robot robot;
    private Type lastPressType;

    public RobotInteractiveMouse(InteractiveUI interactiveUI, Robot robot) {
        this.interactiveUI = interactiveUI;
        this.robot = robot;
    }

    public void moveTo(int x, int y) {
        moveMouse(new Point(x, y));
    }

    public void moveTo(String componentName) {
        moveTo(componentName, 5, 5);
    }

    public void moveTo(String componentName, int offsetX, int offsetY) {
        Component target = findComponentInRoots(componentName);
        assert target != null;

        if (!target.isValid()) {
            target.validate();
            interactiveUI.delay(100);
        }

        Point targetPoint = target.getLocationOnScreen();
        targetPoint.x += offsetX;
        targetPoint.y += offsetY;

        moveMouse(targetPoint);
    }

    public void press(Type type) {
        lastPressType = type;
        switch (type) {
            case LEFT:
                robot.mousePress(InputEvent.BUTTON1_MASK);
                break;
            case CENTER:
                robot.mousePress(InputEvent.BUTTON2_MASK);
                break;
            case RIGHT:
                robot.mousePress(InputEvent.BUTTON3_MASK);
                break;
        }
    }

    public void release() {
        if (lastPressType != null) {
            try {
                release(lastPressType);
            } finally {
                lastPressType = null;
            }
        }
    }

    public void release(Type type) {
        switch (type) {
            case LEFT:
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
                break;
            case CENTER:
                robot.mouseRelease(InputEvent.BUTTON2_MASK);
                break;
            case RIGHT:
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
                break;
        }
    }

    public void click(Type type) {
        press(type);
        release(type);
    }

    public void click(String componentName, Type type) {
        moveTo(componentName);
        click(type);
    }

    public void wheel(int amount) {
        robot.mouseWheel(amount);
    }

    public void moveMouse(Point to) {
        Point from = MouseInfo.getPointerInfo().getLocation();

        int x0 = from.x;
        int y0 = from.y;

        int x1 = to.x;
        int y1 = to.y;

        int dx = x1 - x0;
        int dy = y1 - y0;

        robot.mouseMove(x0, y0);
        if (Math.abs(dx) > Math.abs(dy)) {          // slope < 1
            float m = (float) dy / (float) dx;      // compute slope
            float b = y0 - m * x0;
            dx = (dx < 0) ? -5 : 5;
            while ((dx < 0 && x0 >= x1) || (dx > 0 && x0 <= x1)) {
                x0 += dx;
                robot.mouseMove(x0, Math.round(m * x0 + b));
                interactiveUI.delay(1);
            }
        } else if (dy != 0) {                        // slope >= 1
            float m = (float) dx / (float) dy;      // compute slope
            float b = x0 - m * y0;
            dy = (dy < 0) ? -5 : 5;
            while ((dy < 0 && y0 >= y1) || (dy > 0 && y0 <= y1)) {
                y0 += dy;
                robot.mouseMove(Math.round(m * y0 + b), y0);
                interactiveUI.delay(1);
            }
        }
        robot.mouseMove(to.x, to.y);
    }


    protected Component findComponentInRoots(String componentName) {
        for (Container root : interactiveUI.getRoots()) {
            Component result = UIUtil.findComponentByName(root, componentName);
            if (result != null)
                return result;
        }
        return null;
    }

}
