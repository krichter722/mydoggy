package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.itest.ComponentAdapter;
import org.noos.xing.mydoggy.itest.impl.NamedComponentFilter;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveShowToolTest extends MyDoggySetInteractiveTest {

    public InteractiveShowToolTest(Container root, ToolWindowManager manager) throws AWTException {
        super("All ways to show a tool",
              "All ways to show a tool", manager, root);
    }

    public void execute() {
        // First
        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();

        showTip("<html>Let's go to <br>show Tool 3", 1000);
        ComponentAdapter ca = componentLookuper.lookup(new NamedComponentFilter("toolWindow.rb.Tool 3"));
        ca.moveToCenter();
        showTip("<html>Now : <br>Left Mouse Click", 1000);
        ca.click(ComponentAdapter.MouseButton.LEFT);

        // Second
        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();

        showTip("<html>Let's go to <br>show Tool 3<br> using the popup menu", 1000);
        ca = componentLookuper.lookup(new NamedComponentFilter("toolWindow.rb.Tool 3"));
        ca.moveToCenter();
        showTip("<html>Now : <br>Right Mouse Click", 1000);
        ca.click(ComponentAdapter.MouseButton.RIGHT);
        ca = componentLookuper.lookup(new NamedComponentFilter("toolWindow.popup.visible.Tool 3"));
        ca.moveToCenter();
        showTip("<html>Now : <br>Left Mouse Click", 1000);
        ca.click(ComponentAdapter.MouseButton.LEFT);

        restoreWorkspace();
        componentLookuper.lookup().moveToCenter();

        showTip("<html>Let's go to <br>see aggregate mode", 1000);
        clickOnRepresentativeButton("Tool 1");
        clickOnRepresentativeButton("Tool 3", ComponentAdapter.MouseButton.RIGHT);
        componentLookuper.lookup(new NamedComponentFilter("toolWindow.popup.aggregate.Tool 3"))
                .moveToCenter()
                .click(ComponentAdapter.MouseButton.LEFT);
    }

}
