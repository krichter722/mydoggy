package org.noos.xing.mydoggy.plaf.ui;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface MyDoggyKeySpace {

    // Icons

    String DOCKED = "mydoggy.icon.docked";
    String DOCKED_INACTIVE = "mydoggy.icon.docked.inactive";
    String SLIDING = "mydoggy.icon.sliding";
    String SLIDING_INACTIVE = "mydoggy.icon.sliding.inactive";
    String FLOATING = "mydoggy.icon.floating";
    String FLOATING_INACTIVE = "mydoggy.icon.floating.inactive";
    String FIX = "mydoggy.icon.fix";
    String FIX_INACTIVE = "mydoggy.icon.fix.inactive";
    String AUTO_HIDE_ON = "mydoggy.icon.autoHideOn";
    String AUTO_HIDE_ON_INACTIVE = "mydoggy.icon.autoHideOn.inactive";
    String AUTO_HIDE_OFF = "mydoggy.icon.autoHideOff";
    String AUTO_HIDE_OFF_INACTIVE = "mydoggy.icon.autoHideOff.inactive";
    String HIDE_TOOL_WINDOW = "mydoggy.icon.hideToolWindow";
    String HIDE_TOOL_WINDOW_INACTIVE = "mydoggy.icon.hideToolWindow.inactive";
    String MAXIMIZE = "mydoggy.icon.maximize";
    String MAXIMIZE_INACTIVE = "mydoggy.icon.maximize.inactive";
    String MINIMIZE = "mydoggy.icon.minimize";
    String MINIMIZE_INACTIVE = "mydoggy.icon.minimize.inactive";
    String ACTIONS_POPUP = "mydoggy.icon.actionsPopup";
    String ACTIONS_POPUP_INACTIVE = "mydoggy.icon.actionsPopup.inactive";

    String TOO_WINDOW_TAB_POPUP = "mydoggy.icon.toolWindowTabPopup";

    String CONTENT_PAGE_MINIMIZE = "mydoggy.icon.contentPageMinimize";
    String CONTENT_PAGE_DETACH = "mydoggy.icon.contentPageDetach";
    String CONTENT_PAGE_CLOSE = "mydoggy.icon.contentPageClose";
    String CONTENT_PAGE_DETACH_INACTIVE = "mydoggy.icon.contentPageDetach.inactive";
    String CONTENT_PAGE_CLOSE_INACTIVE = "mydoggy.icon.contentPageClose.inactive";
    String CONTENT_PAGE_MAXIMIZE = "mydoggy.icon.contentPageMaximize";
    String CONTENT_PAGE_RESTORE = "mydoggy.icon.contentPageRestore";

    String TOOL_SCROLL_BAR_UP = "mydoggy.icon.toolScroolBarUp";
    String TOOL_SCROLL_BAR_DOWN = "mydoggy.icon.toolScroolBarDown";
    String TOOL_SCROLL_BAR_LEFT = "mydoggy.icon.toolScroolBarLeft";
    String TOOL_SCROLL_BAR_RIGHT = "mydoggy.icon.toolScroolBarRight";

    String TAB_CLOSE = "mydoggy.icon.tabClose";
    String TAB_CLOSE_INACTIVE = "mydoggy.icon.tabClose.inactive";
    String TAB_MINIMIZE = "mydoggy.icon.tabMinimize";
    String TAB_MINIMIZE_INACTIVE = "mydoggy.icon.tabMinimize.inactive";

    // Images

    String DRAG = "mydoggy.image.drag";

    // Representative Anchor Button - Colors

    String TOOL_SCROLL_BAR_UI_BCK_START = "mydoggy.color.ToolScrollBarArrowUI.background.start";
    String TOOL_SCROLL_BAR_UI_BCK_END = "mydoggy.color.ToolScrollBarArrowUI.background.end";

    String RAB_MOUSE_IN_BORDER = "mydoggy.color.rab.border.mouseIn";
    String RAB_MOUSE_OUT_BORDER = "mydoggy.color.rab.border.mouseOut";
    String RAB_BACKGROUND_INACTIVE = "mydoggy.color.rab.background.inactive";
    String RAB_BACKGROUND_ACTIVE_START = "mydoggy.color.rab.background.active.start";
    String RAB_BACKGROUND_ACTIVE_END = "mydoggy.color.rab.background.active.end";
    String RAB_FOREGROUND = "mydoggy.color.rab.foreground";
    String RAB_FOREGROUND_UNAVAILABLE = "mydoggy.color.rab.foreground.unavailable";
    
    // ToolWindow TitleBar - Colors

    String TWTB_BACKGROUND_ACTIVE_START = "mydoggy.color.ToolWindowTitleBarUI.background.active.start";
    String TWTB_BACKGROUND_ACTIVE_END = "mydoggy.color.ToolWindowTitleBarUI.background.active.end";
    String TWTB_BACKGROUND_INACTIVE_START = "mydoggy.color.ToolWindowTitleBarUI.background.inactive.start";
    String TWTB_BACKGROUND_INACTIVE_END = "mydoggy.color.ToolWindowTitleBarUI.background.inactive.end";
    String TWTB_ID_BACKGROUND_FLASHING_ON = "mydoggy.color.ToolWindowTitleBarUI.id.background.flashing.on";
    String TWTB_ID_BACKGROUND_FLASHING_OFF = "mydoggy.color.ToolWindowTitleBarUI.id.background.flashing.off";
    String TWTB_ID_BACKGROUND_ANIMATING = "mydoggy.color.ToolWindowTitleBarUI.id.background.animating";
    String TWTB_ID_BACKGROUND_ACTIVE = "mydoggy.color.ToolWindowTitleBarUI.id.background.active";
    String TWTB_ID_BACKGROUND_INACTIVE = "mydoggy.color.ToolWindowTitleBarUI.id.background.inactive";
    String TWTB_ID_FOREGROUND_ACTIVE = "mydoggy.color.ToolWindowTitleBarUI.id.foreground.active";
    String TWTB_ID_FOREGROUND_INACTIVE = "mydoggy.color.ToolWindowTitleBarUI.id.foreground.inactive";
    String TWTB_TAB_FOREGROUND_SELECTED = "mydoggy.color.ToolWindowTitleBarUI.tab.foreground.selected";
    String TWTB_TAB_FOREGROUND_UNSELECTED = "mydoggy.color.ToolWindowTitleBarUI.tab.foreground.unselected";

    // Components, ComponentsUIs, Customizers

    String TOOL_WINDOW_CONTAINER = "TOOL_WINDOW_CONTAINER";
    String TOOL_WINDOW_TITLE_BAR = "TOOL_WINDOW_TITLE_BAR";
    String TOOL_WINDOW_TITLE_BAR_UI = "TOOL_WINDOW_TITLE_BAR_UI";
    String TOOL_WINDOW_TITLE_BUTTON = "TOOL_WINDOW_TITLE_BUTTON";

    String TOOL_WINDOW_TAB_BUTTON = "TOOL_WINDOW_TAB_BUTTON";
    String TOOL_WINDOW_TAB_BUTTON_UI = "TOOL_WINDOW_TAB_BUTTON_UI";
    String TOOL_WINDOW_TAB_TITLE = "TOOL_WINDOW_TAB_TITLE";

    String REPRESENTATIVE_ANCHOR_BUTTON_UI = "REPRESENTATIVE_ANCHOR_BUTTON_UI";

    String TOOL_WINDOW_MANAGER = "TOOL_WINDOW_MANAGER";
    String TOOL_WINDOW_MANAGER_CONTENT_CONTAINER = "TOOL_WINDOW_MANAGER_CONTENT_CONTAINER";

    String TOOL_SCROLL_BAR_ARROW = "TOOL_SCROLL_BAR_ARROW";

    String ANCHOR_SPLIT_PANE = "ANCHOR_SPLIT_PANE";
    String ANCHOR_CONTENT_PANE = "ANCHOR_CONTENT_PANE";

    String CORNER_CONTENT_PANE = "CORNER_CONTENT_PANE";
    String DESKTOP_CONTENT_PANE = "DESKTOP_CONTENT_PANE";

    String MULTI_SPLIT_CONTAINER_SPLIT = "MULTI_SPLIT_CONTAINER_SPLIT";

    // Properties
    String TWB_LEFT_LENGTH = "toolwindowbar.left.length";
    String TWB_RIGHT_LENGTH = "toolwindowbar.right.length";
    String TWB_TOP_LENGTH = "toolwindowbar.top.length";
    String TWB_BOTTOM_LENGTH = "toolwindowbar.bottom.length";


    String PERSISTENCE_DELEGATE_PARSING = "PERSISTENCE_DELEGATE_PARSING";
}
