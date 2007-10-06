package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveTest;
import org.noos.xing.mydoggy.itest.InteractiveTestRunner;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ListInteractiveTestRunner implements InteractiveTestRunner {
    private List<InteractiveTest> interactiveTests;

    public ListInteractiveTestRunner() {
        this.interactiveTests = new ArrayList<InteractiveTest>();
    }

    public void addInteractiveTest(InteractiveTest interactiveTest) {
        interactiveTests.add(interactiveTest);
    }

    public void run() {
        for (InteractiveTest interactiveTest : interactiveTests) {

            try {
                RobotInteractiveUI interactiveUI = new RobotInteractiveUI(interactiveTest.setup());
                interactiveTest.execute(interactiveUI);
            } catch (AWTException e) {
                e.printStackTrace();
            } finally {
                interactiveTest.dispose();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }

}
