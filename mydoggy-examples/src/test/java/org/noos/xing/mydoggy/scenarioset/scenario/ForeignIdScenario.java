package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ForeignIdScenario implements Scenario {

    protected MyDoggyToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();
    protected JFrame frame;

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
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(640, 480);

        toolWindowManager.registerToolWindow("Paramètres", "Paramètres", null, new JButton("Paramètres"), ToolWindowAnchor.LEFT);
        toolWindowManager.registerToolWindow("Vue gÃ©ometrique", "Vue gÃ©ometrique", null, new JButton("Vue gÃ©ometrique") , ToolWindowAnchor.LEFT);

        frame.getContentPane().add(toolWindowManager);
    }

    protected void start() {
        SwingUtil.centrePositionOnScreen(frame);
        frame.setVisible(true);
    }

}