package org.noos.xing.mydoggy.plaf.ui;

import javax.swing.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowUI {

    enum IconId {
        DOCKED,
        DOCKED_INACTIVE,
        SLIDING,
        SLIDING_INACTIVE,
        FLOATING,
        FLOATING_INACTIVE,
        FIX,
        FIX_INACTIVE,
        AUTO_HIDE_ON,
        AUTO_HIDE_ON_INACTIVE,
        AUTO_HIDE_OFF,
        AUTO_HIDE_OFF_INACTIVE,
        HIDE_TOOL_WINDOW,
        HIDE_TOOL_WINDOW_INACTIVE,
        MAXIMIZE,
        MAXIMIZE_INACTIVE,
        MINIMIZE,
        MINIMIZE_INACTIVE
    }

    Icon getIcon(IconId iconId);

}
