package org.noos.xing.mydoggy.tester;

import org.noos.xing.mydoggy.tester.impl.RobotInteractiveUI;

import java.util.List;
import java.util.ArrayList;

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
            RobotInteractiveUI interactiveUI = new RobotInteractiveUI(interactiveTest.getRootContainer());
            try {
                interactiveTest.interactiveText(interactiveUI);
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
