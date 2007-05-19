package org.noos.xing.mydoggy.tutorial;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.tester.InteractiveTest;
import org.noos.xing.mydoggy.tester.InteractiveTestRunner;
import org.noos.xing.mydoggy.tester.InteractiveUI;

import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SampleAppInteractiveTester {

    public static void main(String[] args) {
        InteractiveTestRunner runner = new InteractiveTestRunner();
        runner.addInteractiveTest(new ToolWindowTabText());
        runner.run();
    }

    static abstract class SampleAppInteractiveTest implements InteractiveTest {
        SampleApp sampleApp;

        public Container getRootContainer() {
            sampleApp = new SampleApp();
            sampleApp.setUp();
            sampleApp.start();

            return sampleApp.getFrame();
        }

        public void dispose() {
            sampleApp.dispose();
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

    static class ToolWindowTabText extends SampleAppInteractiveTest {

        public void interactiveText(InteractiveUI interactiveUI) {
            interactiveUI.moveMouseTo("ok");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(500);

            interactiveUI.moveMouseTo("toolWindow.Debug Tool.tabs.Profiling");
            interactiveUI.mouseLeftClick();
            interactiveUI.delay(1500);

            interactiveUI.assertTrue("Invalid Behaviour", interactiveUI.ask("Is behaviuor correct?"));
        }
    }
}



