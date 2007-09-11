package org.noos.xing.mydoggy.mydoggyset.view.interactive;

import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveToolVisisbleTest extends AbstractInteractiveTest {

    public InteractiveToolVisisbleTest(Container masterContainer) {
        super(masterContainer);
    }

    public String getDescription() {
        return "TODO";
    }

    public void interactiveTest(InteractiveUI interactiveUI) {
        InteractiveMouse mouse = interactiveUI.getInteractiveMouse();

        mouse.click("toolWindow.rb.Tool 1", InteractiveMouse.Type.LEFT);
        interactiveUI.delay(1000);

        mouse.click("toolWindow.rb.Tool 3", InteractiveMouse.Type.LEFT);
        interactiveUI.delay(1000);
    }
       
}
