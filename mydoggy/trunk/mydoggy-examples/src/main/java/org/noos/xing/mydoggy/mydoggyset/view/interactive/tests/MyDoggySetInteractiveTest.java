package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.ComponentAdapter;
import org.noos.xing.mydoggy.itest.impl.AbstractInteractiveTest;
import org.noos.xing.mydoggy.itest.impl.NamedComponentFilter;
import org.noos.xing.mydoggy.itest.impl.ui.JBalloonTip;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class MyDoggySetInteractiveTest extends AbstractInteractiveTest {
    protected JBalloonTip balloonTip;
    protected ToolWindowManager toolWindowManager;

    protected MyDoggySetInteractiveTest(String name, String description, ToolWindowManager toolWindowManager, Container root) throws AWTException {
        super(name, description, root);
        this.toolWindowManager = toolWindowManager;
        this.balloonTip = new JBalloonTip(((JFrame) root));
    }


    protected void restoreWorkspace() {
        toolWindowManager.getPersistenceDelegate().apply(
                this.getClass().getClassLoader().getResourceAsStream(
                        "org/noos/xing/mydoggy/mydoggyset/workspace.xml"
                )
        );
    }

    protected void delay(int millis) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    protected void showTip(String text, int millis) {
        balloonTip.setText(text);
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, root);
        balloonTip.show(point.x, point.y);
        delay(millis);
        balloonTip.setVisible(false);
    }


    protected String getToolContainerName(String toolId) {
        return "toolWindow.container." + toolId;
    }

    protected String getRepresentativeButtonName(String toolId) {
        return "toolWindow.rb." + toolId;
    }

    protected ComponentAdapter clickOnRepresentativeButton(String toolId) {
        return clickOn("toolWindow.rb." + toolId);
    }

    protected ComponentAdapter clickOnRepresentativeButton(String toolId, ComponentAdapter.MouseButton mouseButton) {
        return clickOn("toolWindow.rb." + toolId, mouseButton);
    }

    protected ComponentAdapter clickOn(String componentName) {
        return clickOn(componentName, ComponentAdapter.MouseButton.LEFT);
    }

    protected ComponentAdapter clickOn(String componentName, ComponentAdapter.MouseButton mouseButton) {
        return componentLookuper
                .lookup(componentName)
                .moveToCenter()
                .showTip("<html>Now : <br> " + mouseButton + " Mouse Click", 1000)
                .click(mouseButton, 1000);
    }

    protected ComponentAdapter moveOn(String componentName, String tip) {
        return componentLookuper.lookup(componentName)
                .moveToCenter()
                .showTip(tip, 1000);
    }

    protected ComponentAdapter drag(String from, String to) {
        componentLookuper.lookup(new NamedComponentFilter(from)).moveToCenter(1000).press(ComponentAdapter.MouseButton.LEFT);
        return componentLookuper.lookup(new NamedComponentFilter(to)).moveToCenter(1000).release(ComponentAdapter.MouseButton.LEFT, 1000);
    }

    protected ComponentAdapter moveToAnchor(String componentName, ToolWindowAnchor anchor) {
        return drag(componentName, "toolWindowManager.bar." + anchor.toString());
    }

}
