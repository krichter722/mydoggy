package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveSimpleTest extends MyDoggySetInteractiveTest {

    public InteractiveSimpleTest(Container root) throws AWTException {
        super("InteractiveSimpleTest", "InteractiveSimpleTest", root);
    }

    public void execute() {
        clickOn("toolWindow.rb.Tool 1");
    }

}