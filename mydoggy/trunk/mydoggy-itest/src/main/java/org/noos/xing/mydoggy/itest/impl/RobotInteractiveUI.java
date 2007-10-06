package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.ComponentLookuper;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RobotInteractiveUI implements InteractiveUI {
    protected Robot robot;
    protected ComponentLookuper componentLookuper;

    public RobotInteractiveUI(Container root) throws AWTException {
        this.robot = new Robot();
        this.componentLookuper = new RobotComponentLookuper(robot, root);
    }

    public ComponentLookuper getComponentLookuper() {
        return componentLookuper;
    }

    public void delay(int millis) {
        robot.delay(millis);
    }

}
