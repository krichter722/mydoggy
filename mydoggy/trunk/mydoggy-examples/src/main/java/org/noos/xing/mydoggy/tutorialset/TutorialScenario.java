package org.noos.xing.mydoggy.tutorialset;

import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.scenario.AbstractScenario;
import org.noos.xing.mydoggy.scenario.ScenarioSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TutorialScenario extends AbstractScenario {

    protected ScenarioSet scenarioSet;
    protected Class tutorialClass;
    protected String name;
    protected String description;


    public TutorialScenario(ScenarioSet scenarioSet, Class tutorialClass, String name, String description) {
        this.scenarioSet = scenarioSet;
        this.tutorialClass = tutorialClass;
        this.name = name;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Window launch() {
        try {
            tutorialClass.getDeclaredMethod("main", String[].class).invoke(null, new Object[]{new String[0]});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (final Window window : SwingUtil.getTopContainers()) {
                    if (window instanceof Frame) {
                        if (((Frame) window).getTitle().startsWith("Sample App...")) {
                            window.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosing(WindowEvent e) {
                                    window.removeWindowListener(this);
                                    scenarioSet.getFrame().setState(Frame.NORMAL);
                                }
                            });
                        }
                    }
                }
            }
        });
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
