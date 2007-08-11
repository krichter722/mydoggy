package org.noos.xing.mydoggy.examples.mydoggyset.content.interactive;

import org.noos.xing.mydoggy.itest.InteractiveAssertor;
import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.itest.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveDragTest extends AbstractInteractiveTest {

    public InteractiveDragTest(Container masterContainer) {
        super(masterContainer);
    }

    public String getDescription() {
        return "TODO";
    }

    public void interactiveTest(InteractiveUI interactiveUI) {
        InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
        InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

        mouse.moveTo("toolWindow.rb.Tool 1");
        interactiveUI.delay(500);
        mouse.press(InteractiveMouse.Type.LEFT);

        mouse.moveTo("toolWindowManager.bar.RIGHT", 10, 80);
        interactiveUI.delay(500);
        mouse.release();

        interactiveUI.delay(500);

        mouse.moveTo("toolWindow.rb.Tool 1");
        mouse.press(InteractiveMouse.Type.LEFT);

        mouse.moveTo("toolWindowManager.bar.LEFT", 10, 80);
        interactiveUI.delay(500);
        mouse.release();

        assertor.askForConfirm("Is behaviuor correct?");
    }

}