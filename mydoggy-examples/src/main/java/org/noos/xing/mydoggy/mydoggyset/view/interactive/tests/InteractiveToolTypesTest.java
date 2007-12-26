package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.ComponentAdapter;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveToolTypesTest extends MyDoggySetInteractiveTest {

    public InteractiveToolTypesTest(Container root, ToolWindowManager manager) throws AWTException {
        super(manager, root);
    }

    public String getName() {
        return "Demostrate Tool Window Types...";
    }

    public String getDescription() {
        return "<html>" +
                "<h2>Test Description</h2>" +
                "<p>" +
                "This test demostrate all toolwindow Types available..." +
                "</p>" +
                "</html>";
    }

    public void execute() {
        // Docked Mode
        restoreWorkspace();
        componentLookuper.lookup()
                .moveToCenter()
                .showTip("<html>First : <strong>Docked Mode</strong>");

        clickOn(getRepresentativeButtonName("Tool 1"));
        moveOn(getToolContainerName("Tool 1"), "<html>This is <strong>Docked Mode</strong>");

        // Sliding Mode
        restoreWorkspace();
        componentLookuper.lookup()
                .moveToCenter()
                .showTip("<html>Second : <strong>Sliding Mode</strong>");

        clickOn(getRepresentativeButtonName("Tool 1"));
        clickOn("toolWindow.dockButton.Tool 1");
        moveOn("toolWindow.container.Tool 1", "<html>This is <strong>Sliding Mode</strong>");

        // FlaotingLive Mode
        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();
        showTip("<html>Second : <strong>FlaotingLive Mode</strong>", 1000);

        clickOn(getRepresentativeButtonName("Tool 3"));
        clickOn("toolWindow.titleBar.Tool 3", ComponentAdapter.MouseButton.RIGHT);
        clickOn("toolWindow.popup.floatingLive.Tool 3");
        moveOn("toolWindow.container.Tool 3", "<html>This is <strong>FlaotingLive Mode</strong>");

        // Flaoting Mode
        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();
        showTip("<html>Second : <strong>Flaoting Mode</strong>", 1000);

        clickOn(getRepresentativeButtonName("Tool 1"));
        clickOn("toolWindow.floatingButton.Tool 1");
        moveOn("toolWindow.container.Tool 1", "<html>This is <strong>Flaoting Mode</strong>");
    }


}