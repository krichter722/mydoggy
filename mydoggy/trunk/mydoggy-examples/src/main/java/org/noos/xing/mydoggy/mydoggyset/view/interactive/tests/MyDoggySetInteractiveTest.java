package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.itest.ComponentAdapter;
import org.noos.xing.mydoggy.itest.impl.AbstractInteractiveTest;
import org.noos.xing.mydoggy.itest.impl.NamedComponentFilter;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggySetInteractiveTest extends AbstractInteractiveTest {

    protected MyDoggySetInteractiveTest(String name, String description, Container root) throws AWTException {
        super(name, description, root);
    }

    protected ComponentAdapter clickOn(String componentName) {
        return componentLookuper.lookup(new NamedComponentFilter(componentName)).moveToCenter(500).click(ComponentAdapter.MouseButton.LEFT, 500);
    }

    protected ComponentAdapter drag(String from, String to) {
        componentLookuper.lookup(new NamedComponentFilter(from)).moveToCenter(500).press(ComponentAdapter.MouseButton.LEFT);
        return componentLookuper.lookup(new NamedComponentFilter(to)).moveToCenter(500).release(ComponentAdapter.MouseButton.LEFT, 500);
    }

    protected ComponentAdapter moveToAnchor(String componentName, ToolWindowAnchor anchor) {
        return drag(componentName, "toolWindowManager.bar." + anchor.toString());
    }

    protected void delay(int millis) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}