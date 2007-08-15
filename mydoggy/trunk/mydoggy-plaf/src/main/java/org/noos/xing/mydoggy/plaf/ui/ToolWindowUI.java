package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ToolWindowUI {

    public static String DOCKED = "DOCKED";
    public static String DOCKED_INACTIVE = "DOCKED_INACTIVE";
    public static String SLIDING = "SLIDING";
    public static String SLIDING_INACTIVE = "SLIDING_INACTIVE";
    public static String FLOATING = "FLOATING";
    public static String FLOATING_INACTIVE = "FLOATING_INACTIVE";
    public static String FIX = "FIX";
    public static String FIX_INACTIVE = "FIX_INACTIVE";
    public static String AUTO_HIDE_ON = "AUTO_HIDE_ON";
    public static String AUTO_HIDE_ON_INACTIVE = "AUTO_HIDE_ON_INACTIVE";
    public static String AUTO_HIDE_OFF = "AUTO_HIDE_OFF";
    public static String AUTO_HIDE_OFF_INACTIVE = "AUTO_HIDE_OFF_INACTIVE";
    public static String HIDE_TOOL_WINDOW = "HIDE_TOOL_WINDOW";
    public static String HIDE_TOOL_WINDOW_INACTIVE = "HIDE_TOOL_WINDOW_INACTIVE";
    public static String MAXIMIZE = "MAXIMIZE";
    public static String MAXIMIZE_INACTIVE = "MAXIMIZE_INACTIVE";
    public static String MINIMIZE = "MINIMIZE";
    public static String MINIMIZE_INACTIVE = "MINIMIZE_INACTIVE";

    public static String ANCHOR_BORDER_MOUSE_IN = "ANCHOR_BORDER_MOUSE_IN";
    public static String ANCHOR_BORDER_MOUSE_OUT = "ANCHOR_BORDER_MOUSE_OUT";
    public static String ANCHOR_BACKGROUND_INACTIVE = "ANCHOR_BACKGROUND_INACTIVE";
    public static String ANCHOR_FLASHING_START = "ANCHOR_FLASHING_START";
    public static String ANCHOR_FLASHING_END = "ANCHOR_FLASHING_END";
    
    public static String TW_APP_BACKGROUND_ENABLED_START = "TW_APP_BACKGROUND_ENABLED_START";
    public static String TW_APP_BACKGROUND_ENABLED_END = "TW_APP_BACKGROUND_ENABLED_END";
    public static String TW_APP_BACKGROUND_DISABLED_START = "TW_APP_BACKGROUND_DISABLED_START";
    public static String TW_APP_BACKGROUND_DISABLED_END = "TW_APP_BACKGROUND_DISABLED_END";

    public static String TW_APP_ID_BACKGROUND_FLASHING_0 = "TW_APP_ID_BACKGROUND_FLASHING_0";
    public static String TW_APP_ID_BACKGROUND_FLASHING_1 = "TW_APP_ID_BACKGROUND_FLASHING_1";
    public static String TW_APP_ID_BACKGROUND_ANIMATING = "TW_APP_ID_BACKGROUND_ANIMATING";
    public static String TW_APP_ID_BACKGROUND_ACTIVE = "TW_APP_ID_BACKGROUND_ACTIVE";
    public static String TW_APP_ID_BACKGROUND_INACTIVE = "TW_APP_ID_BACKGROUND_INACTIVE";
    public static String TW_APP_ID_FOREGROUND_ACTIVE = "TW_APP_ID_FOREGROUND_ACTIVE";
    public static String TW_APP_ID_FOREGROUND_INACTIVE = "TW_APP_ID_FOREGROUND_INACTIVE";

    public static String TW_APP_TAB_FOREGROUND_SELECTED = "TW_APP_TAB_FOREGROUND_SELECTED";
    public static String TW_APP_TAB_FOREGROUND_UNSELECTED = "TW_APP_TAB_FOREGROUND_UNSELECTED";

    Icon getIcon(String id);

    Color getColor(String id);

    void updateAnchor(ToolWindowDescriptor descriptor, Graphics g, JComponent c,
                      Color backgroundStart, Color backgroundEnd,
                      boolean active, boolean flashing);

    void updateToolWindowAppBar(ToolWindowDescriptor descriptor, Graphics g, JComponent c,
                                Color backgroundStart, Color backgroundEnd,
                                Color idBackgroundColor, Color idColor);

}
