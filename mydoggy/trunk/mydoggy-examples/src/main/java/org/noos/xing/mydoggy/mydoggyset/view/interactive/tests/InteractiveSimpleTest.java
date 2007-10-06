package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveSimpleTest extends AbstractInteractiveTest {

    public InteractiveSimpleTest(Container masterContainer) {
        super(masterContainer);
    }

    public String getDescription() {
        return "TODO";
    }

    public void execute(InteractiveUI interactiveUI) {
        clickOn(interactiveUI, "toolWindow.rb.Tool 1");
    }

}