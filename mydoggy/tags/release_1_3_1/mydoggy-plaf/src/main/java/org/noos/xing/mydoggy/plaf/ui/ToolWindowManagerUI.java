package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowManagerUI {


    Component createComponent(String key, ToolWindowManager manager, Object... args);

    ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args);

    void applyCustomization(String key, Component component, Object... args);

}
