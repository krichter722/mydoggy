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
        robot.mouseMove(x, y);
    }

    public void moveTo(String componentName) {
        moveTo(componentName, 5 ,5);
    }

    public void moveTo(String componentName, int offsetX, int offsetY) {
        Component target = findComponentInRoots(componentName);
        assert target != null;

        if (!target.isValid()) {
            target.validate();
            interactiveUI.delay(100);
        }
        System.out.println(target.isShowing());

        Point targetPoint = target.getLocationOnScreen();
        targetPoint.x += offsetX;
        targetPoint.y += offsetY;

        moveMouse(targetPoint);
    }

    public void press(Type type) {
        lastPressType = type;
        switch(type) {
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
        switch(type) {
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

    public void wheel(int amount) {
        robot.mouseWheel(amount);
    }

    public void moveMouse(Point to) {
        Point from = MouseInfo.getPointerInfo().getLocation();

        System.out.println("from = " + from);
        System.out.println("to = " + to);

        int signX = to.x - from.x < 0 ? -1 : 1;
        int signY = to.y - from.y < 0 ? -1 : 1;

        int deltaX = Math.abs(to.x - from.x);
        int deltaY = Math.abs(to.y - from.y);

        if (deltaX > deltaY) {
            long step = Math.round((double)deltaX / (double)deltaY);
            for (int i = 0; i < deltaX; i++) {
                from.x += signX;
                if (i % step == 0)
                    from.y += signY;

                robot.mouseMove(from.x, from.y);
                interactiveUI.delay(1);
            }
        } else {
            long step = Math.round((double)deltaY / (double)deltaX);
            for (int i = 0; i < deltaY; i++) {
                from.y += signY;
                if (i % step == 0)
                    from.x += signX;

                robot.mouseMove(from.x, from.y);
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
