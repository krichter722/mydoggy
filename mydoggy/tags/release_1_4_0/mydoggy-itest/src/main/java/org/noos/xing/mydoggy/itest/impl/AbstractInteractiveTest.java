package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.ComponentLookuper;
import org.noos.xing.mydoggy.itest.InteractiveTest;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class AbstractInteractiveTest implements InteractiveTest {
    protected ComponentLookuper componentLookuper;

    protected String name;
    protected String description;
    protected Container root;

    protected AbstractInteractiveTest(String name, String description, Container root) throws AWTException {
        this.name = name;
        this.description = description;
        this.root = root;
        this.componentLookuper = new RobotComponentLookuper(new Robot(), root);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setup() {
    }

    public void dispose() {
    }

    public String toString() {
        return getName();
    }

    public Container getRoot() {
        return root;
    }
}
