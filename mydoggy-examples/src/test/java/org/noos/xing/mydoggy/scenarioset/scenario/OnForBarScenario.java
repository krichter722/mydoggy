package org.noos.xing.mydoggy.scenarioset.scenario;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.scenario.AbstractScenario;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class OnForBarScenario extends AbstractScenario {

    protected MyDoggyToolWindowManager toolWindowManager = new MyDoggyToolWindowManager();
    protected JFrame frame = new JFrame();
    protected boolean setup = false;

    public String getName() {
        return this.getClass().getName();
    }

    public Window launch() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setUp();
                start();
            }
        });
        return frame;
    }

    public String getDescription() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return getName();
    }

    protected void setUp() {
        if (!setup) {
            final ToolWindow topOfTheWorld = toolWindowManager.registerToolWindow("topOfTheWorld", null, null,
                                                                                  new JTextArea("I'm on top of the world!"), ToolWindowAnchor.TOP);
            DockedTypeDescriptor descriptor = topOfTheWorld.getTypeDescriptor(DockedTypeDescriptor.class);
            descriptor.setMinimumDockLength(200);
            descriptor.setDockLength(200);

            final ToolWindow leftWing = toolWindowManager.registerToolWindow("leftWing", null, null,
                                                                             new JTextArea("Left Wing"), ToolWindowAnchor.LEFT);
            descriptor = leftWing.getTypeDescriptor(DockedTypeDescriptor.class);
            descriptor.setMinimumDockLength(200);
            descriptor.setDockLength(200);

            final ToolWindow rightWing = toolWindowManager.registerToolWindow("rightWing", null, null,
                                                                              new JTextArea("Right Wing"), ToolWindowAnchor.RIGHT);
            descriptor = rightWing.getTypeDescriptor(DockedTypeDescriptor.class);
            descriptor.setMinimumDockLength(200);
            descriptor.setDockLength(200);

            final ToolWindow bottomDweller = toolWindowManager.registerToolWindow("bottomDweller", null, null,
                                                                                  new JTextArea("Bottom dweller"), ToolWindowAnchor.BOTTOM);
            descriptor = bottomDweller.getTypeDescriptor(DockedTypeDescriptor.class);
            descriptor.setMinimumDockLength(200);
            descriptor.setDockLength(200);

            frame.getContentPane().add(toolWindowManager);
            frame.pack();
        }
    }

    protected void start() {
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (ToolWindow toolWindow : toolWindowManager.getToolWindows()) {
                    toolWindow.setAvailable(true);
                    toolWindow.setVisible(true);
                }
            }
        });
    }

}
