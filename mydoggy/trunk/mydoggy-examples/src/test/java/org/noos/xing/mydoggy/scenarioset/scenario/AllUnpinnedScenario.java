package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AllUnpinnedScenario implements Scenario {

    protected MyDoggyToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();
    protected JFrame frame = new JFrame();
    protected boolean setup = false;

    public String getName() {
        return this.getClass().getName();
    }

    public void launch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
    }


    @Override
    public String toString() {
        return getName();
    }

    protected void setUp() {
        if (!setup) {
            for (int i = 0; i < 5; i++) {
                ToolWindow tool = toolWindowManager.registerToolWindow("tool_" + i,
                                                                       null, null,
                                                                       new JTextArea("Hello World!!!"), ToolWindowAnchor.LEFT);
                tool.setAvailable(true);
                tool.setAutoHide(true);
            }

            frame.getContentPane().add(toolWindowManager);
            frame.setSize(640, 480);
        }
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

}