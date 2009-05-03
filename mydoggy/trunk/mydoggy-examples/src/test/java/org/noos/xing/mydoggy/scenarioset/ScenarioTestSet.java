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
        tutorialSet.addScenario(new TestScenario(tutorialSet, MultiSplitPaneDividerFreeze.class, "ScenarioTest - MultiSplitPaneDivider Freeze",
                                                 "the problem i have is very strange. I try to explain it. With the example attached\n" +
                                                 "you can reproduce it.\n" +
                                                 "\n" +
                                                 "1. DnD Content_0 to the right\n" +
                                                 "2. DnD Content_1 to the bottom\n" +
                                                 "3. DnD Content_0 to the right\n" +
                                                 "\n" +
                                                 "--> The divider between Content_1 and Content_3 is freezing and could not be\n" +
                                                 "moved."));

        tutorialSet.run();
    }

}
