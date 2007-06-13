package org.noos.xing.mydoggy.examples.mydoggyset;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.itest.InteractiveAssertor;
import org.noos.xing.mydoggy.itest.InteractiveMouse;
import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.InteractiveUI;
import org.noos.xing.mydoggy.itest.impl.InteractiveTestRunner;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyInteractiveTester {

    public static void main(String[] args) {
        InteractiveTestRunner runner = new InteractiveTestRunner();
//        runner.addInteractiveTest(new ToolVisisbleInteractiveTest());
//        runner.addInteractiveTest(new PreviewInteractiveTest());
//        runner.addInteractiveTest(new DragInteractiveTest());
//        runner.addInteractiveTest(new SlidingTypeInteractiveTest());
        runner.addInteractiveTest(new FloatingMoveInteractiveTest());
        runner.run();
    }

    static abstract class MyDoggyInteractiveTest implements InteractiveTest {
        MyDoggySet myDoggySet;

        public Container setup() {
            myDoggySet = new MyDoggySet();
            myDoggySet.setUp();
            myDoggySet.start();

            return myDoggySet.getFrame();
        }

        public void dispose() {
            myDoggySet.dispose();
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

    static class PreviewInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveTest(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            ToolWindow toolWindow = myDoggySet.getToolWindowManager().getToolWindow("Tool 1");

            moveToolTo(interactiveUI, "Tool 7", ToolWindowAnchor.BOTTOM);

            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            descriptor.setPreviewEnabled(true);
            descriptor.setPreviewDelay(1000);

            mouse.moveTo("toolWindow.rb.Tool 6");
            interactiveUI.delay(1100);

            assertor.askForConfirm("Is preview visible?");

            moveToolTo(interactiveUI, "Tool 1", ToolWindowAnchor.RIGHT);
            moveToolTo(interactiveUI, "Tool 3", ToolWindowAnchor.RIGHT);

            mouse.moveTo("toolWindow.rb.Tool 1");
            interactiveUI.delay(1100);

            assertor.askForConfirm("Is preview visible?");

            mouse.moveTo("toolWindowManager.mainContainer");

            interactiveUI.delay(1000);

            assertor.askForConfirm("Is preview not visible?");
        }
    }

    static class ToolVisisbleInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveTest(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            mouse.moveTo("toolWindow.rb.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            mouse.moveTo("toolWindow.rb.Tool 3");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            assertor.askForConfirm("Is behaviuor correct?");
        }
    }

    static class DragInteractiveTest extends MyDoggyInteractiveTest {

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

    static class SlidingTypeInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveTest(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            // Validate initiale state...
            assertor.assertTrue("Invalid state",
                                     myDoggySet.getToolWindowManager().getToolWindow("Tool 1").getType() != ToolWindowType.SLIDING);

            mouse.moveTo("toolWindow.rb.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            mouse.moveTo("toolWindow.dockButton.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            assertor.assertTrue("Invalid state",
                                     myDoggySet.getToolWindowManager().getToolWindow("Tool 1").getType() == ToolWindowType.SLIDING);
        }

    }

    static class FloatingMoveInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveTest(InteractiveUI interactiveUI) {
            InteractiveMouse mouse = interactiveUI.getInteractiveMouse();
            InteractiveAssertor assertor = interactiveUI.getInteractiveAssertor();

            // Validate initiale state...
            mouse.moveTo("toolWindow.rb.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            mouse.moveTo("toolWindow.floatingButton.Tool 1");
            mouse.click(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            interactiveUI.importRoot("toolWindow.floating.window.Tool 1");

            mouse.moveTo("toolWindow.bar.Tool 1");
            mouse.press(InteractiveMouse.Type.LEFT);
            interactiveUI.delay(1000);

            Point wLocation = ((FloatingTypeDescriptor) myDoggySet.getToolWindowManager().getToolWindow("Tool 1").getTypeDescriptor(ToolWindowType.FLOATING)).getLocation();
            wLocation.x -= 50;
            wLocation.y -= 50;

            mouse.moveTo(wLocation.x, wLocation.y);
            mouse.release();
            interactiveUI.delay(1000);

            assertor.askForConfirm("Is behaviuor correct?");
        }

    }
}
