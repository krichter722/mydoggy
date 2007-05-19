package org.noos.xing.mydoggy.tester.impl;

import org.noos.xing.mydoggy.tester.InteractiveUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RobotInteractiveUI implements InteractiveUI {
    private Container rootContainer;
    private Robot robot;

    public RobotInteractiveUI(Container rootContainer) {
        this.rootContainer = rootContainer;
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void moveMouseTo(String componentName) {
        moveMouseTo(componentName, 5 ,5);
    }

    public void moveMouseTo(String componentName, int offsetX, int offsetY) {
        Component target = UIUtil.findComponentByName(rootContainer, componentName);
        assert target != null;

        if (!target.isValid()) {
            target.validate();
            delay(100);
        }

        Point targetPoint = new Point();
        SwingUtilities.convertPointToScreen(targetPoint, target);
        targetPoint.x += offsetX;
        targetPoint.y += offsetY;

        moveMouse(targetPoint);
    }

    public void pressMouseLeftButton() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
    }

    public void releaseMouseLeftButton() {
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public boolean ask(String message) {
        return JOptionPane.showConfirmDialog(rootContainer, message) == JOptionPane.OK_OPTION;
    }

    public void delay(int millis) {
        robot.delay(millis);
    }

    public void setRootContainer(Container rootContainer) {
        this.rootContainer = rootContainer;
    }

    public void moveMouse(Point to) {
        Point from = MouseInfo.getPointerInfo().getLocation();

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
                delay(1);
            }
        } else {
            long step = Math.round((double)deltaY / (double)deltaX);
            for (int i = 0; i < deltaY; i++) {
                from.y += signY;
                if (i % step == 0)
                    from.x += signX;

                robot.mouseMove(from.x, from.y);
                delay(1);
            }
        }

        robot.mouseMove(to.x, to.y);
    }

    public void assertTrue(String s, boolean b) {
        if (!b)
            throw new IllegalStateException(s);
    }

    public void mouseLeftClick() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public Frame getFrame(String name) {
        for (Frame frame : JFrame.getFrames()) {
            if (name.equals(frame.getName()))
                return frame;
        }
        return null;
    }

}
