package org.noos.xing.mydoggy.mydoggyset.view.interactive.tests;

import org.noos.xing.mydoggy.itest.ComponentAdapter;
import org.noos.xing.mydoggy.itest.InteractiveUI;
import org.noos.xing.mydoggy.itest.impl.NamedComponentFilter;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveFloatingLiveTest extends AbstractInteractiveTest {

    public InteractiveFloatingLiveTest(Container masterContainer) {
        super(masterContainer);
    }

    public String getDescription() {
        return "TODO";
    }

    public void execute(InteractiveUI interactiveUI) {
        clickOn(interactiveUI, "toolWindow.rb.Tool 3");
        interactiveUI.getComponentLookuper().lookup(new NamedComponentFilter("toolWindow.titleBar.Tool 3"))
                .moveToCenter()
                .click(ComponentAdapter.MouseButton.RIGHT, 500);
        interactiveUI.getComponentLookuper().lookup(new NamedComponentFilter("toolWindow.popup.floatingLive.Tool 3"))
                .moveToCenter(500)
                .click(ComponentAdapter.MouseButton.LEFT, 500);
        interactiveUI.getComponentLookuper().lookup(new NamedComponentFilter("toolWindow.titleBar.Tool 3"))
                .moveToCenter()
                .press(ComponentAdapter.MouseButton.LEFT)
                .move(new Ellipse2D.Double(250, 250, 125, 125))
                .release();
    }

}