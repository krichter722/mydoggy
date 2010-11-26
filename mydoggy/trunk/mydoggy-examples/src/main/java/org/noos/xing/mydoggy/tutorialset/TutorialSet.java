package org.noos.xing.mydoggy.tutorialset;

import org.noos.xing.mydoggy.scenario.ScenarioSet;
import org.noos.xing.mydoggy.tutorialset.basic.*;
import org.noos.xing.mydoggy.tutorialset.customization.TutorialSetCustomization1;

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

        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet1.class, "Baisc - Step 1", "A skeleton class called TutorialSet."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet2.class, "Baisc - Step 2", "Introduce ToolWindowManager with your first ToolWindow."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet3.class, "Baisc - Step 3", "Play with TypeDescriptors."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet4.class, "Baisc - Step 4", "Add first Content."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet5.class, "Baisc - Step 5", "Configure ContentManagerUI."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet6.class, "Baisc - Step 6", "Store (on close) and load (on start) the toolwindow manager workspace."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet7.class, "Baisc - Step 7", "ToolWindowTabs."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet8.class, "Baisc - Step 8", "Add another tool and show them together on the same anchor."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet9.class, "Baisc - Step 9", "Using the new MultiSpitContentManagerUI."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet10.class, "Baisc - Step 10", "Aggregate toolwindows in a new powerful way."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet11.class, "Baisc - Step 11", "Flashing."));
        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSet12.class, "Baisc - Step 12", "Work with a disabled ContentManager."));
//        tutorialSet.addScenario(new TutorialScenario(TutorialSet13.class, "Step 13", "Play with ToolWindowAction."));

        tutorialSet.addScenario(new TutorialScenario(tutorialSet, TutorialSetCustomization1.class, "Customization - Step 1", "A skeleton class called CustomizationTutorialSet."));

        tutorialSet.run();
    }

}
