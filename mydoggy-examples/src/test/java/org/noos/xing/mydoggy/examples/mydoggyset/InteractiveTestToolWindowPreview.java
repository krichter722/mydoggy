package org.noos.xing.mydoggy.examples.mydoggyset;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveTestToolWindowPreview {

    public static void main(String[] args) {
        MyDoggySet myDoggySet = new MyDoggySet();
        myDoggySet.setUp();
        myDoggySet.start();

        UIInteractiveTest test = new UIInteractiveTest(myDoggySet.getFrame());

        ToolWindow toolWindow = myDoggySet.getToolWindowManager().getToolWindow("Title 1");
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        descriptor.setPreviewEnabled(true);
        descriptor.setPreviewDelay(1000);

        test.moveMouseTo("toolWindow.rb.Title 1");
        test.delay(1100);

        test.assertTrue("Preview not visible", test.ask("Is preview visible?"));

        test.moveMouseTo("toolWindowManager.mainContainer");

        test.delay(1000);

        test.assertTrue("Preview is still visible", test.ask("Is preview not visible?"));
    }

}
