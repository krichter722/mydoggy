package org.noos.xing.mydoggy.scenarioset;

import org.noos.xing.mydoggy.scenario.ScenarioSet;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ScenarioTestSet extends ScenarioSet {


    @Override
    protected String getFrameTitle() {
        return "ScenarioTestSet...";
    }

    @Override
    protected String getScenariosPanelBorderTitle() {
        return "Tests";
    }

    public static void main(String[] args) {
        ScenarioTestSet tutorialSet = new ScenarioTestSet();

        tutorialSet.addScenario(new TestScenario(tutorialSet, LoadWorkspaceScenarioTest.class, "ScenarioTest - LoadWorkspace", "Load a workspace..."));

        tutorialSet.run();
    }

}
