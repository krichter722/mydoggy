package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowManager;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ResourceManager {

    public static final String DOCKED = "DOCKED";
    public static final String DOCKED_INACTIVE = "DOCKED_INACTIVE";
    public static final String SLIDING = "SLIDING";
    public static final String SLIDING_INACTIVE = "SLIDING_INACTIVE";
    public static final String FLOATING = "FLOATING";
    public static final String FLOATING_INACTIVE = "FLOATING_INACTIVE";
    public static final String FIX = "FIX";
    public static final String FIX_INACTIVE = "FIX_INACTIVE";
    public static final String AUTO_HIDE_ON = "AUTO_HIDE_ON";
    public static final String AUTO_HIDE_ON_INACTIVE = "AUTO_HIDE_ON_INACTIVE";
    public static final String AUTO_HIDE_OFF = "AUTO_HIDE_OFF";
    public static final String AUTO_HIDE_OFF_INACTIVE = "AUTO_HIDE_OFF_INACTIVE";
    public static final String HIDE_TOOL_WINDOW = "HIDE_TOOL_WINDOW";
    public static final String HIDE_TOOL_WINDOW_INACTIVE = "HIDE_TOOL_WINDOW_INACTIVE";
    public static final String MAXIMIZE = "MAXIMIZE";
    public static final String MAXIMIZE_INACTIVE = "MAXIMIZE_INACTIVE";
    public static final String MINIMIZE = "MINIMIZE";
    public static final String MINIMIZE_INACTIVE = "MINIMIZE_INACTIVE";
    public static final String CONTENT_PAGE_DETACH = "CONTENT_PAGE_DETACH";
    public static final String CONTENT_PAGE_CLOSE = "CONTENT_PAGE_CLOSE";
    public static final String CONTENT_PAGE_DETACH_INACTIVE = "CONTENT_PAGE_DETACH_INACTIVE";
    public static final String CONTENT_PAGE_CLOSE_INACTIVE = "CONTENT_PAGE_CLOSE_INACTIVE";

    public static final String TOO_WINDOW_TAB_POPUP = "TOO_WINDOW_TAB_POPUP";
    public static final String ANCHOR_BORDER_MOUSE_IN = "ANCHOR_BORDER_MOUSE_IN";
    public static final String ANCHOR_BORDER_MOUSE_OUT = "ANCHOR_BORDER_MOUSE_OUT";
    public static final String ANCHOR_BACKGROUND_INACTIVE = "ANCHOR_BACKGROUND_INACTIVE";
    public static final String ANCHOR_FLASHING_START = "ANCHOR_FLASHING_START";
    public static final String ANCHOR_FLASHING_END = "ANCHOR_FLASHING_END";
    public static final String TW_APP_BACKGROUND_ENABLED_START = "TW_APP_BACKGROUND_ENABLED_START";
    public static final String TW_APP_BACKGROUND_ENABLED_END = "TW_APP_BACKGROUND_ENABLED_END";
    public static final String TW_APP_BACKGROUND_DISABLED_START = "TW_APP_BACKGROUND_DISABLED_START";
    public static final String TW_APP_BACKGROUND_DISABLED_END = "TW_APP_BACKGROUND_DISABLED_END";
    public static final String TW_APP_ID_BACKGROUND_FLASHING_0 = "TW_APP_ID_BACKGROUND_FLASHING_0";
    public static final String TW_APP_ID_BACKGROUND_FLASHING_1 = "TW_APP_ID_BACKGROUND_FLASHING_1";
    public static final String TW_APP_ID_BACKGROUND_ANIMATING = "TW_APP_ID_BACKGROUND_ANIMATING";
    public static final String TW_APP_ID_BACKGROUND_ACTIVE = "TW_APP_ID_BACKGROUND_ACTIVE";
    public static final String TW_APP_ID_BACKGROUND_INACTIVE = "TW_APP_ID_BACKGROUND_INACTIVE";
    public static final String TW_APP_ID_FOREGROUND_ACTIVE = "TW_APP_ID_FOREGROUND_ACTIVE";
    public static final String TW_APP_ID_FOREGROUND_INACTIVE = "TW_APP_ID_FOREGROUND_INACTIVE";
    public static final String TW_APP_TAB_FOREGROUND_SELECTED = "TW_APP_TAB_FOREGROUND_SELECTED";
    public static final String TW_APP_TAB_FOREGROUND_UNSELECTED = "TW_APP_TAB_FOREGROUND_UNSELECTED";

    public static final String BAR_SPLIT_PANE = "BAR_SPLIT_PANE";
    public static final String REPRESENTATIVE_ANCHOR_BUTTON_UI = "REPRESENTATIVE_ANCHOR_BUTTON_UI";
    public static final String TOOL_WINDOW_TITLE_BAR = "TOOL_WINDOW_TITLE_BAR";
    public static final String TOOL_WINDOW_TITLE_BUTTON = "TOOL_WINDOW_TITLE_BUTTON";
    public static final String TOOL_WINDOW_TITLE_BAR_UI = "TOOL_WINDOW_TITLE_BAR_UI";
    public static final String BAR_CONTENT_PANE = "BAR_CONTENT_PANE";
    public static final String CORNER_CONTENT_PANE = "CORNER_CONTENT_PANE";
    public static final String MY_DOGGY_MANAGER_PANEL = "MY_DOGGY_MANAGER_PANEL";
    public static final String MY_DOGGY_MANAGER_MAIN_CONTAINER = "MY_DOGGY_MANAGER_MAIN_CONTAINER";
    public static final String DESKTOP_CONTENT_PANE = "DESKTOP_CONTENT_PANE";


    Component createComponent(String key, ToolWindowManager manager, Object... args);

    ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args);

    void applyCustomization(String key, Component component, Object... args);


    Icon getIcon(String id);

    Color getColor(String id);


    void setLocale(Locale locale);

    void setUserBundle(Locale locale, String bundle, ClassLoader classLoader);

    ResourceBundle getResourceBundle();

    ResourceBundle getUserResourceBundle();

    String getString(String key);

    String getUserString(String key);
    
}
