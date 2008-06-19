package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyUtil {

    public static int getNumTabs(ToolWindow toolWindow) {
        int count = 0;
        for (ToolWindowTab toolWindowTab : toolWindow.getToolWindowTabs()) {
            if (!toolWindowTab.isMinimized())
                count++;
        }
        return count;
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        if (UIManager.getDefaults().containsKey(name))
            return UIManager.getBoolean(name);
        return defaultValue;
    }

}
