package org.noos.xing.mydoggy.examples.mydoggyset;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.tester.InteractiveTest;
import org.noos.xing.mydoggy.tester.InteractiveTestRunner;
import org.noos.xing.mydoggy.tester.InteractiveUI;

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
        runner.addInteractiveTest(new SlidingTypeInteractiveTest());
        runner.addInteractiveTest(new FloatingMoveInteractiveTest());
        runner.run();
    }

    static abstract class MyDoggyInteractiveTest implements InteractiveTest {
        MyDoggySet myDoggySet;

        public Container getRootContainer() {
            myDoggySet = new MyDoggySet();
            myDoggySet.setUp();
            myDoggySet.start();

            return myDoggySet.getFrame();
        }

        public void dispose() {
            myDoggySet.dispose();
        }

        protected void moveToolTo(InteractiveUI interactiveUI, String toolId, ToolWindowAnchor anchor) {
            interactiveUI.moveMouseTo("toolWindow.rb." + toolId);
            interactiveUI.delay(500);
            interactiveUI.pressMouseLeftButton();

            interactiveUI.moveMouseTo("toolWindowManager.bar." + anchor.toString(), 10, 15);
            interactiveUI.delay(500);
            interactiveUI.releaseMouseLeftButton();
            interactiveUI.delay(500);
        }
    }

    static class PreviewInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveText(InteractiveUI interactiveUI) {
            ToolWindow toolWindow = myDoggySet.getToolWindowManager().getToolWindow("Title 1");

            moveToolTo(interactiveUI, "7", ToolWindowAnchor.BOTTOM);

            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            descriptor.setPreviewEnabled(true);
            descriptor.setPreviewDelay(1000);

            interactiveUI.moveMouseTo("toolWindow.rb.6");
            interactiveUI.delay(1100);

            interactiveUI.assertTrue("Preview not visible", interactiveUI.ask("Is preview visible?"));

            moveToolTo(interactiveUI, "Title 1", ToolWindowAnchor.RIGHT);
            moveToolTo(interactiveUI, "3", ToolWindowAnchor.RIGHT);

            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.delay(1100);

            interactiveUI.assertTrue("Preview not visible", interactiveUI.ask("Is preview visible?"));

            interactiveUI.moveMouseTo("toolWindowManager.mainContainer");

            interactiveUI.delay(1000);

            interactiveUI.assertTrue("Preview is still visible", interactiveUI.ask("Is preview not visible?"));
        }
    }

    static class ToolVisisbleInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveText(InteractiveUI interactiveUI) {
            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1000);

            interactiveUI.moveMouseTo("toolWindow.rb.3");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1000);

            interactiveUI.assertTrue("Invalid Behaviour", interactiveUI.ask("Is behaviuor correct?"));
        }
    }

    static class DragInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveText(InteractiveUI interactiveUI) {
            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.delay(500);
            interactiveUI.pressMouseLeftButton();

            interactiveUI.moveMouseTo("toolWindowManager.bar.RIGHT", 10, 80);
            interactiveUI.delay(500);
            interactiveUI.releaseMouseLeftButton();

            interactiveUI.delay(500);

            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.pressMouseLeftButton();

            interactiveUI.moveMouseTo("toolWindowManager.bar.LEFT", 10, 80);
            interactiveUI.delay(500);
            interactiveUI.releaseMouseLeftButton();

            interactiveUI.assertTrue("Invalid Behaviour", interactiveUI.ask("Is behaviuor correct?"));
        }

    }

    static class SlidingTypeInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveText(InteractiveUI interactiveUI) {
            // Validate initiale state...
            interactiveUI.assertTrue("Invalid state",
                                     myDoggySet.getToolWindowManager().getToolWindow("Title 1").getType() != ToolWindowType.SLIDING);

            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1000);

            interactiveUI.moveMouseTo("toolWindow.dockButton.Title 1");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1000);

            interactiveUI.assertTrue("Invalid state",
                                     myDoggySet.getToolWindowManager().getToolWindow("Title 1").getType() == ToolWindowType.SLIDING);
        }

    }

    static class FloatingMoveInteractiveTest extends MyDoggyInteractiveTest {

        public void interactiveText(InteractiveUI interactiveUI) {
            // Validate initiale state...
            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1000);

            interactiveUI.moveMouseTo("toolWindow.floatingButton.Title 1");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1000);

            interactiveUI.importRoot("toolWindow.floating.window.Title 1");

            interactiveUI.moveMouseTo("toolWindow.bar.Title 1");
            interactiveUI.pressMouseLeftButton();
            interactiveUI.delay(1000);

            Point wLocation = ((FloatingTypeDescriptor) myDoggySet.getToolWindowManager().getToolWindow("Title 1").getTypeDescriptor(ToolWindowType.FLOATING)).getLocation();
            wLocation.x -= 50;
            wLocation.y -= 50;

            interactiveUI.moveMouse(wLocation);
            interactiveUI.releaseMouseLeftButton();
            interactiveUI.delay(1000);

            interactiveUI.assertTrue("Invalid Behaviour", interactiveUI.ask("Is behaviuor correct?"));
        }

    }
}
