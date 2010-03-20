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

        tutorialSet.addScenario(new TestScenario(tutorialSet, LoadWorkspaceScenario.class, "ScenarioTest - LoadWorkspace", "Load a workspace..."));
        tutorialSet.addScenario(new TestScenario(tutorialSet, MultiSplitPaneDividerFreezeScenario.class, "ScenarioTest - MultiSplitPaneDivider Freeze",
                                                 "the problem i have is very strange. I try to explain it. With the example attached\n" +
                                                 "you can reproduce it.\n" +
                                                 "\n" +
                                                 "1. DnD Content_0 to the right\n" +
                                                 "2. DnD Content_1 to the bottom\n" +
                                                 "3. DnD Content_0 to the right\n" +
                                                 "\n" +
                                                 "--> The divider between Content_1 and Content_3 is freezing and could not be\n" +
                                                 "moved."));
        tutorialSet.addScenario(new TestScenario(tutorialSet, NextContentSelectionOnRemoveScenario.class, "ScenarioTest - Remove a Content does not select the right last content win.",
                                                 "When clicking the x1 in the content window tab and multiple content windows\n" +
                                                 "are opened. The expected behavior would be that after a remove content\n" +
                                                 "window event, the last content window that the user clicked would be\n" +
                                                 "selected. However the window that is selected seems to always be the first\n" +
                                                 "content window of the aggregate that was added.\n" +
                                                 "\n" +
                                                 "Steps to reproduce:\n" +
                                                 "\n" +
                                                 "1)use the addContent method of the content manager to add JPanel and use a\n" +
                                                 "MultiSplitConstraint with an AggregatePosition.TOP\n" +
                                                 "2) Add another content window that aggregate to the previous window with a\n" +
                                                 "multisplitconstraint\n" +
                                                 "3) Add another content window that aggregates to the BOTTOM\n" +
                                                 "4) Add another content window that aggregates to the BOTTOM\n" +
                                                 "5) At this point you should have 4 content window 2 tabs aggregated at the\n" +
                                                 "top and 2 tabs aggregated at the bottom.\n" +
                                                 "6) Select the second TOP aggregate window\n" +
                                                 "7) Then select one of the BOTTOM aggregate window. Not just the tab but\n" +
                                                 "select a component in the JPanel so the panel in the BOTTOM window has\n" +
                                                 "focus.\n" +
                                                 "8) Click the x1 in the tab of one of the BOTTOM aggregate windows.\n" +
                                                 "9)This is where the problem occurs. The second TOP window is showing and\n" +
                                                 "you would expect it to keep showing since it was selected prior to the\n" +
                                                 "BOTTOM selection. However it looses focus and the first TOP window switches\n" +
                                                 "to show and is now selected. This creates a behavior where the windows are\n" +
                                                 "constantly swapping everytime you close a BOTTOM aggregate window.\n" +
                                                 "\n" +
                                                 "I put a print statement in the propertychange listener with\n" +
                                                 "toolWindowManager.getContentManager().getNextContent() which always returns\n" +
                                                 "the first added TOP aggregate window.\n" +
                                                 "\n" +
                                                 "My best guess is the getNextContent method seems to always return the first\n" +
                                                 "added window to an Aggregate instead of the last selected content window in\n" +
                                                 "the current aggregate that the content window was closed from."));
        tutorialSet.addScenario(new TestScenario(tutorialSet, ContentScenario.class, "ScenarioTest - ContentScenario", "..."));

        tutorialSet.run();
    }

}
