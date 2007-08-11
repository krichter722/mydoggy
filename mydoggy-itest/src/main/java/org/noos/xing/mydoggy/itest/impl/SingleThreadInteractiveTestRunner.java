package org.noos.xing.mydoggy.itest.impl;

import org.noos.xing.mydoggy.itest.InteractiveTestRunner;
import org.noos.xing.mydoggy.itest.InteractiveTest;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SingleThreadInteractiveTestRunner implements InteractiveTestRunner {
    private InteractiveTestRunner interactiveTestRunner;

    public SingleThreadInteractiveTestRunner() {
        this(new ListInteractiveTestRunner());
    }

    public SingleThreadInteractiveTestRunner(InteractiveTestRunner interactiveTestRunner) {
        this.interactiveTestRunner = interactiveTestRunner;
    }

    public void addInteractiveTest(InteractiveTest interactiveTest) {
        interactiveTestRunner.addInteractiveTest(interactiveTest);
    }

    public void run() {
        new Thread(new Runnable() {
            public void run() {
                interactiveTestRunner.run();
            }
        }).start();

    }
}
