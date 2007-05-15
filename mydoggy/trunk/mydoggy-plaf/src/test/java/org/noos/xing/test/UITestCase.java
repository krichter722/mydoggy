package org.noos.xing.test;

import junit.framework.TestCase;
import org.noos.xing.test.util.UIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class UITestCase extends TestCase {
    private Container rootContainer;
    private Robot robot;

    public UITestCase() {
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void moveMouseOn(String componentName) {
        Component target = UIUtil.findComponentByName(rootContainer, componentName);
        assertNotNull(componentName + " not found.", target);

        Point targetPoint = new Point(2,2);
        SwingUtilities.convertPointToScreen(targetPoint, target);

        robot.mouseMove(targetPoint.x, targetPoint.y);
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

}
