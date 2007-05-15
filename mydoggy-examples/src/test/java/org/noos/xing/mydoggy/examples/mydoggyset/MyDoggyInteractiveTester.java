package org.noos.xing.mydoggy.examples.mydoggyset;

import org.noos.xing.mydoggy.tester.InteractiveTest;
import org.noos.xing.mydoggy.tester.InteractiveTestRunner;
import org.noos.xing.mydoggy.tester.InteractiveUI;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyInteractiveTester {

    public static void main(String[] args) {
        InteractiveTestRunner runner = new InteractiveTestRunner();
        runner.addInteractiveTest(new PreviewInteractiveTest());
        runner.run();
    }

    static class PreviewInteractiveTest implements InteractiveTest {
        MyDoggySet myDoggySet;

        public Container getRootContainer() {
            myDoggySet = new MyDoggySet();
            myDoggySet.setUp();
            myDoggySet.start();

            return myDoggySet.getFrame();
        }

        public void interactiveText(InteractiveUI interactiveUI) {
            ToolWindow toolWindow = myDoggySet.getToolWindowManager().getToolWindow("Title 1");

            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            descriptor.setPreviewEnabled(true);
            descriptor.setPreviewDelay(1000);

            interactiveUI.moveMouseTo("toolWindow.rb.Title 1");
            interactiveUI.delay(1100);

            interactiveUI.assertTrue("Preview not visible", interactiveUI.ask("Is preview visible?"));

            interactiveUI.moveMouseTo("toolWindowManager.mainContainer");

            interactiveUI.delay(1000);

            interactiveUI.assertTrue("Preview is still visible", interactiveUI.ask("Is preview not visible?"));
        }

        public void dispose() {
            myDoggySet.dispose();
        }
    }




}
