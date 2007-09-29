package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveAssertor;
import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class RobotInteractiveUI implements InteractiveUI {
    private java.util.List<Container> roots;

    private Robot robot;
    private InteractiveMouse interactiveMouse;
    private InteractiveAssertor interactiveAssertor;

    public RobotInteractiveUI(Container rootContainer) {
        this.roots = new ArrayList<Container>();
        roots.add(rootContainer);
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        interactiveMouse = new RobotInteractiveMouse(this, robot);
        interactiveAssertor = new SwingInteractiveAssertor(rootContainer);
    }

    public InteractiveMouse getInteractiveMouse() {
        return interactiveMouse;
    }

    public InteractiveAssertor getInteractiveAssertor() {
        return interactiveAssertor;
    }

    public void delay(int millis) {
        robot.delay(millis);
    }

    public void importRoot(String rootName) {
        for (Object o : UIUtil.getTopContainers(rootName))
            roots.add((Container) o);
    }

    public Container[] getRoots() {
        return roots.toArray(new Container[roots.size()]);
    }

}
