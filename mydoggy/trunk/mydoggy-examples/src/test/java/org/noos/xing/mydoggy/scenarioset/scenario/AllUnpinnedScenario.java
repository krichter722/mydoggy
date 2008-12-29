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
            final ToolWindow topOfTheWorld = toolWindowManager.registerToolWindow("topOfTheWorld", null, null,
                                                                                  new JTextArea("I'm on top of the world!"), ToolWindowAnchor.LEFT);
            topOfTheWorld.setAvailable(true);
            topOfTheWorld.setAutoHide(true);

            final ToolWindow leftWing = toolWindowManager.registerToolWindow("leftWing", null, null,
                                                                             new JTextArea("Left Wing"), ToolWindowAnchor.LEFT);
            leftWing.setAvailable(true);
            leftWing.setAutoHide(true);
            final ToolWindow rightWing = toolWindowManager.registerToolWindow("rightWing", null, null,
                                                                              new JTextArea("Right Wing"), ToolWindowAnchor.RIGHT);
            rightWing.setAvailable(true);
            rightWing.setAutoHide(true);
            final ToolWindow bottomDweller = toolWindowManager.registerToolWindow("bottomDweller", null, null,
                                                                                  new JTextArea("Bottom dweller"), ToolWindowAnchor.BOTTOM);
            bottomDweller.setAvailable(true);
            bottomDweller.setAutoHide(true);

            frame.getContentPane().add(toolWindowManager);
            frame.setSize(640,480);
        }
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

}