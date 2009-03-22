package org.noos.xing.mydoggy.tutorial;

import org.noos.xing.mydoggy.scenario.Scenario;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TutorialScenario implements Scenario {

    protected Class tutorialClass;
    protected String name;
    protected String description;


    public TutorialScenario(Class tutorialClass, String name, String description) {
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

    public void launch() {
        try {
            tutorialClass.getDeclaredMethod("main", String[].class).invoke(null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
