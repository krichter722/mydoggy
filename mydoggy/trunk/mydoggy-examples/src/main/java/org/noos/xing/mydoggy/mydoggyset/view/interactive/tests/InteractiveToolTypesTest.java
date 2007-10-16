package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.ComponentAdapter;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveToolTypesTest extends MyDoggySetInteractiveTest {

    public InteractiveToolTypesTest(Container root, ToolWindowManager manager) throws AWTException {
        super("Show All Tool Window Types",
              "Show All Tool Window Types", manager, root);
    }

    public void execute() {
        // Docked Mode
        restoreWorkspace();
        componentLookuper.lookup()
                .moveToCenter()
                .showTip("<html>First : <strong>Docked Mode</strong>", 1000);

        clickOn(getRepresentativeButtonName("Tool 1"));
        moveOn(getToolContainerName("Tool 1"),
               "<html>This is <strong>Docked Mode</strong>");

        // Sliding Mode
        restoreWorkspace();
        componentLookuper.lookup()
                .moveToCenter()
                .showTip("<html>Second : <strong>Sliding Mode</strong>", 1000);

        clickOn(getRepresentativeButtonName("Tool 1"));
        delay(1000);
        componentLookuper.lookup("toolWindow.dockButton.Tool 1")
                .moveToCenter(1000)
                .click(ComponentAdapter.MouseButton.LEFT, 1000);
        componentLookuper.lookup("toolWindow.container.Tool 1").moveToCenter();
        showTip("<html>This is <strong>Sliding Mode</strong>", 1000);

        // FlaotingLive Mode
        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();

        showTip("<html>Second : <strong>FlaotingLive Mode</strong>", 1000);
        clickOnRepresentativeButton("Tool 3");
        delay(1000);
        componentLookuper.lookup("toolWindow.titleBar.Tool 3")
                .moveToCenter(1000)
                .click(ComponentAdapter.MouseButton.RIGHT, 1000);
        componentLookuper.lookup("toolWindow.popup.floatingLive.Tool 3")
                .moveToCenter(1000).
                click(ComponentAdapter.MouseButton.LEFT, 1000);
        componentLookuper.lookup("toolWindow.container.Tool 3").moveToCenter();
        showTip("<html>This is <strong>FlaotingLive Mode</strong>", 1000);

        // Flaoting Mode
        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();

        showTip("<html>Second : <strong>Flaoting Mode</strong>", 1000);
        clickOnRepresentativeButton("Tool 1");
        delay(1000);
        componentLookuper.lookup("toolWindow.floatingButton.Tool 1")
                .moveToCenter(1000)
                .click(ComponentAdapter.MouseButton.LEFT, 1000);
        componentLookuper.lookup("toolWindow.container.Tool 1")
                .moveToCenter()
                .showTip("<html>This is <strong>Flaoting Mode</strong>", 1000);
    }


}