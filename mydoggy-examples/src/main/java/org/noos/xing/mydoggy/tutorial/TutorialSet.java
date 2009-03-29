package org.noos.xing.mydoggy.tutorial;

import org.noos.xing.mydoggy.scenario.ScenarioSet;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class TutorialSet extends ScenarioSet {


    @Override
    protected String getFrameTitle() {
        return "TutorialSet...";
    }

    @Override
    protected String getScenariosPanelBorderTitle() {
        return "Tutorials";
    }

    public static void main(String[] args) {
        TutorialSet tutorialSet = new TutorialSet();

        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet1.class, "TutorialSet - Step 1", "A skeleton class called TutorialSet."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet2.class, "TutorialSet - Step 2", "Introduce ToolWindowManager with your first ToolWindow."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet3.class, "TutorialSet - Step 3", "Play with TypeDescriptors."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet4.class, "TutorialSet - Step 4", "Add first Content."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet5.class, "TutorialSet - Step 5", "Configure ContentManagerUI."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet6.class, "TutorialSet - Step 6", "Store (on close) and load (on start) the toolwindow manager workspace."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet7.class, "TutorialSet - Step 7", "ToolWindowTabs."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet8.class, "TutorialSet - Step 8", "Add another tool and show them together on the same anchor."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet9.class, "TutorialSet - Step 9", "Using the new MultiSpitContentManagerUI."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet10.class, "TutorialSet - Step 10", "Aggregate toolwindows in a new powerful way."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet11.class, "TutorialSet - Step 11", "Flashing."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet12.class, "TutorialSet - Step 12", "Work with a disabled ContentManager."));
//        tutorialSet.addScenario(new TutorialScenario(TutorialSet13.class, "TutorialSet - Step 13", "Play with ToolWindowAction."));

        tutorialSet.run();
    }

}
