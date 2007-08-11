package org.noos.xing.mydoggy.examples.mydoggyset.content.interactive;

import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.InteractiveUI;
import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.ToolWindowAnchor;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public abstract class AbstractInteractiveTest implements InteractiveTest {
    private Container masterContainer;

    protected AbstractInteractiveTest(Container masterContainer) {
        this.masterContainer = masterContainer;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public Container setup() {
        return masterContainer; 
    }

    public void dispose() {
    }

    public String toString() {
        return getName();
    }

    protected void moveToolTo(InteractiveUI interactiveUI, String toolId, ToolWindowAnchor anchor) {
        InteractiveMouse mouse = interactiveUI.getInteractiveMouse();

        mouse.moveTo("toolWindow.rb." + toolId);
        interactiveUI.delay(500);
        mouse.press(InteractiveMouse.Type.LEFT);

        mouse.moveTo("toolWindowManager.bar." + anchor.toString(), 10, 15);
        interactiveUI.delay(500);
        mouse.release();
        interactiveUI.delay(500);
    }
    
}
