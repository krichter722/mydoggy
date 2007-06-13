package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class InteractiveTestRunner {
    private List<InteractiveTest> interactiveTests;

    public InteractiveTestRunner() {
        this.interactiveTests = new ArrayList<InteractiveTest>();
    }

    public void addInteractiveTest(InteractiveTest interactiveTest) {
        interactiveTests.add(interactiveTest);
    }

    public void run() {
        for (InteractiveTest interactiveTest : interactiveTests) {
            RobotInteractiveUI interactiveUI = new RobotInteractiveUI(interactiveTest.setup());
            try {
                interactiveTest.interactiveTest(interactiveUI);
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
