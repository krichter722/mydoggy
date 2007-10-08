package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveToolVisisbleTest extends MyDoggySetInteractiveTest {

    public InteractiveToolVisisbleTest(Container root) throws AWTException {
        super("InteractiveToolVisisbleTest", "InteractiveToolVisisbleTest", root);
    }

    public void execute() {
        clickOn("toolWindow.rb.Tool 1");
        clickOn("toolWindow.rb.Tool 3");
    }
       
}
