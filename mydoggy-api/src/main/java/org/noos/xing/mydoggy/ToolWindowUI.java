package org.noos.xing.mydoggy;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.3.0
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

    enum Style {
        BASIC
    }

    enum Target {
        RAPRESENTATIVE_BUTTON,
        TW_TITLE_BAR
    }

    Icon getIcon(IconId iconId);

    void setIcon(IconId iconId, Icon icon);

    Color getColor(String key);

    Color setColor(String key, Color color);

    Style getStyle(Target target);

    void setStyle(Target target, Style style);

}
