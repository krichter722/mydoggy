package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowManagerUI;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.cmp.DebugSplitPane;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.util.Map;
import java.util.Hashtable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowManagerUI implements ToolWindowManagerUI {
    protected Map<String, ComponentCreator> cmpCreators;
    protected Map<String, ComponentUICreator> cmpUiCreators;
    protected Map<String, ComponentCustomizer> cmpCustomizers;

    public MyDoggyToolWindowManagerUI() {
        initComponentCreators();
    }

    public Component createComponent(String key, ToolWindowManager manager, Object... args) {
        return cmpCreators.get(key).createComponent(manager, args);
    }

    public ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args) {
        return cmpUiCreators.get(key).createComponentUI(manager, args);
    }

    public void applyCustomization(String key, Component component, Object... args) {
        cmpCustomizers.get(key).applyCustomization(component, args);
    }


    protected void initComponentCreators() {
        cmpCreators = new Hashtable<String, ComponentCreator>();
        cmpCreators.put(BAR_SPLIT_PANE, new BarSplitPaneComponentCreator());
        cmpCreators.put(BAR_CONTENT_PANE, new BarContentPaneComponentCreator());
        cmpCreators.put(CORNER_CONTENT_PANE, new CornerContentPaneComponentCreator());
        cmpCreators.put(MY_DOGGY_MANAGER_MAIN_CONTAINER, new MyDoggyManagerMainContainerComponentCreator());

        cmpUiCreators = new Hashtable<String, ComponentUICreator>();
        cmpUiCreators.put(ANCHOR_LABEL_UI, new AnchorLabelComponentUICreator());
        cmpUiCreators.put(APP_BAR_PANEL, new ApplicationBarPanelComponentUICreator());

        cmpCustomizers = new Hashtable<String, ComponentCustomizer>();
        cmpCustomizers.put(MY_DOGGY_MANAGER_PANEL, new MyDoggyManagerPanelComponentCustomizer());
    }


    public static interface ComponentCreator {
        Component createComponent(ToolWindowManager manager, Object... args);
    }

    public static interface ComponentUICreator {
        ComponentUI createComponentUI(ToolWindowManager manager, Object... args);
    }

    public static interface ComponentCustomizer {

        void applyCustomization(Component component, Object... args);
    }


    public static class BarSplitPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JSplitPane splitPane = new DebugSplitPane((Integer) args[0]);
            splitPane.setBorder(null);
            splitPane.setContinuousLayout(true);
            splitPane.setDividerSize(0);
            splitPane.setDividerLocation(300);
            splitPane.setLeftComponent(null);
            splitPane.setRightComponent(null);
            return splitPane;
        }
    }

    public static class BarContentPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JPanel panel = new JPanel();
//            panel.setBackground(Color.black);
            return panel;
        }
    }

    public static class CornerContentPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JPanel panel = new JPanel();
//            panel.setBackground(Color.black);
            return panel;
        }
    }

    public static class MyDoggyManagerMainContainerComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            return panel;
        }
    }

    public static class AnchorLabelComponentUICreator implements ComponentUICreator {

        public ComponentUI createComponentUI(ToolWindowManager manager, Object... args) {
            return new AnchorLabelUI((ToolWindowDescriptor) args[0]);
        }
    }

    public static class ApplicationBarPanelComponentUICreator implements ComponentUICreator {

        public ComponentUI createComponentUI(ToolWindowManager manager, Object... args) {
            return new ApplicationBarPanelUI((ToolWindowDescriptor) args[0], (DockedContainer) args[1]);
        }
    }

    public static class MyDoggyManagerPanelComponentCustomizer implements ComponentCustomizer {

        public void applyCustomization(Component component, Object... args) {
//            component.setBackground(Color.black);
        }
    }

}

