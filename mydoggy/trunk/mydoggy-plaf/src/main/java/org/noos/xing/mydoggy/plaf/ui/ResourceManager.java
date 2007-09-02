package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This interface is used to customize not only icons, colors and internationalization but also
 * ui components creation with relative customization and transparency manager.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public interface ResourceManager {

    // Icons

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
    public static final String TOO_WINDOW_TAB_POPUP = "TOO_WINDOW_TAB_POPUP";
    public static final String CONTENT_PAGE_DETACH = "CONTENT_PAGE_DETACH";
    public static final String CONTENT_PAGE_CLOSE = "CONTENT_PAGE_CLOSE";
    public static final String CONTENT_PAGE_DETACH_INACTIVE = "CONTENT_PAGE_DETACH_INACTIVE";
    public static final String CONTENT_PAGE_CLOSE_INACTIVE = "CONTENT_PAGE_CLOSE_INACTIVE";
    public static final String TOOL_SCROLL_BAR_UP = "TOOL_SCROLL_BAR_UP";
    public static final String TOOL_SCROLL_BAR_DOWN = "TOOL_SCROLL_BAR_DOWN";
    public static final String TOOL_SCROLL_BAR_LEFT = "TOOL_SCROLL_BAR_LEFT";
    public static final String TOOL_SCROLL_BAR_RIGHT = "TOOL_SCROLL_BAR_RIGHT";
    public static final String TOOL_SCROLL_BAR_UI_BCK_START = "TOOL_SCROLL_BAR_UI_BCK_START";
    public static final String TOOL_SCROLL_BAR_UI_BCK_END = "TOOL_SCROLL_BAR_UI_BCK_END";

    // Representative Anchor Button - Colors

    public static final String RAB_MOUSE_IN_BORDER = "RAB_MOUSE_IN_BORDER";
    public static final String RAB_MOUSE_OUT_BORDER = "RAB_MOUSE_OUT_BORDER";
    public static final String RAB_BACKGROUND_INACTIVE = "RAB_BACKGROUND_INACTIVE";
    public static final String RAB_BACKGROUND_FLASHING_START = "RAB_BACKGROUND_FLASHING_START";
    public static final String RAB_BACKGROUND_FLASHING_END = "RAB_BACKGROUND_FLASHING_END";
    public static final String RAB_FOREGROUND = "RAB_FOREGROUND";

    // ToolWindow TitleBar - Colors

    public static final String TWTB_BACKGROUND_ENABLED_START = "TWTB_BACKGROUND_ENABLED_START";
    public static final String TWTB_BACKGROUND_ENABLED_END = "TWTB_BACKGROUND_ENABLED_END";
    public static final String TWTB_BACKGROUND_DISABLED_START = "TWTB_BACKGROUND_DISABLED_START";
    public static final String TWTB_BACKGROUND_DISABLED_END = "TWTB_BACKGROUND_DISABLED_END";
    public static final String TWTB_ID_BACKGROUND_FLASHING_0 = "TWTB_ID_BACKGROUND_FLASHING_0";
    public static final String TWTB_ID_BACKGROUND_FLASHING_1 = "TWTB_ID_BACKGROUND_FLASHING_1";
    public static final String TWTB_ID_BACKGROUND_ANIMATING = "TWTB_ID_BACKGROUND_ANIMATING";
    public static final String TWTB_ID_BACKGROUND_ACTIVE = "TWTB_ID_BACKGROUND_ACTIVE";
    public static final String TWTB_ID_BACKGROUND_INACTIVE = "TWTB_ID_BACKGROUND_INACTIVE";
    public static final String TWTB_ID_FOREGROUND_ACTIVE = "TWTB_ID_FOREGROUND_ACTIVE";
    public static final String TWTB_ID_FOREGROUND_INACTIVE = "TWTB_ID_FOREGROUND_INACTIVE";
    public static final String TWTB_TAB_FOREGROUND_SELECTED = "TWTB_TAB_FOREGROUND_SELECTED";
    public static final String TWTB_TAB_FOREGROUND_UNSELECTED = "TWTB_TAB_FOREGROUND_UNSELECTED";

    // Components, ComponentsUIs, Customizers

    public static final String BAR_SPLIT_PANE = "BAR_SPLIT_PANE";
    public static final String REPRESENTATIVE_ANCHOR_BUTTON_UI = "REPRESENTATIVE_ANCHOR_BUTTON_UI";

    public static final String TOOL_WINDOW_TITLE_BAR = "TOOL_WINDOW_TITLE_BAR";
    public static final String TOOL_WINDOW_TITLE_BAR_UI = "TOOL_WINDOW_TITLE_BAR_UI";
    public static final String TOOL_WINDOW_TITLE_BUTTON = "TOOL_WINDOW_TITLE_BUTTON";

    public static final String MDM_PANEL = "MDM_PANEL";
    public static final String MDM_MAIN_CONTAINER = "MDM_MAIN_CONTAINER";

    public static final String BAR_CONTENT_PANE = "BAR_CONTENT_PANE";
    public static final String CORNER_CONTENT_PANE = "CORNER_CONTENT_PANE";

    public static final String DESKTOP_CONTENT_PANE = "DESKTOP_CONTENT_PANE";

    public static final String TOOL_SCROLL_BAR_ARROW = "TOOL_SCROLL_BAR_ARROW";


    /**
     * Create the component using the rule specified by the key param.
     *
     * @param key key whose associated rule is used to create the component.
     * @param manager the toolwindow manager
     * @param args any arguments used to create the component.
     * @return the component created using the rule specified by the key param.
     */
    Component createComponent(String key, ToolWindowManager manager, Object... args);

    /**
     * Create the component ui using the rule specified by the key param.
     *
     * @param key key whose associated rule is used to create the component.
     * @param manager the toolwindow manager
     * @param args any arguments used to create the component ui.
     * @return the component ui created using the rule specified by the key param.
     */
    ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args);

    /**
     * Applay the customization using the rule specified by the key param.
     *
     * @param key key whose associated rule is used to customize the component.
     * @param component the component to be costumized.
     * @param args any arguments used to customize the component.
     * @return the component customized.
     */
    Component applyCustomization(String key, Component component, Object... args);

    /**
     * Returns the icon to which this manager maps the specified id.  Returns
     * <tt>null</tt> if the manager contains no mapping for this id.
     *
     * @param id id whose associated icon is to be returned.
     * @return the icon to which this manager maps the specified id, or
     *	       <tt>null</tt> if the manager contains no mapping for this id.
     */
    Icon getIcon(String id);

    /**
     * Returns the color to which this manager maps the specified id.  Returns
     * <tt>null</tt> if the manager contains no mapping for this id.
     *
     * @param id id whose associated color is to be returned.
     * @return the color to which this manager maps the specified id, or
     *	       <tt>null</tt> if the manager contains no mapping for this id.
     */
    Color getColor(String id);

    /**
     * Returns the transparency manager for the windows used to manage transparency of toolwindows
     * of FLOATING or FLOATING_FREE type.
     *
     * @return the transparency manager for the windows.
     */
    TransparencyManager<Window> getTransparencyManager();

    /**
     * Setys the transparency manager for the windows used to manage transparency of toolwindows
     * of FLOATING or FLOATING_FREE type.
     *
     * @param transparencyManager the transparency manager for the windows.
     */
    void setTransparencyManager(TransparencyManager<Window> transparencyManager);

    /**
     * Sets the locale of this manager used for the internationalization of the relative toolwindow manager.
     *
     * @param locale the locale to become this manager's locale
     */
    void setLocale(Locale locale);

    /**
     * Sets the bundle used for the internationalization of special strings like the the toolwindow id.
     *
     * @param locale the locale for which a resource bundle is desired
     * @param bundle the base name of the resource bundle, a fully qualified class name
     * @param classLoader the class loader from which to load the resource bundle
     * @see #getUserResourceBundle()
     */
    void setUserBundle(Locale locale, String bundle, ClassLoader classLoader);

    /**
     * Sets the bundle used for the internationalization of special strings like the the toolwindow id.
     *
     * @param userBundle the ResourceBundle
     */
    void setUserBundle(ResourceBundle userBundle);

    /**
     * Returns the resource bundle used for the internationalization of mydoggy.
     * @return the resource bundle used for the internationalization of mydoggy.
     */
    ResourceBundle getResourceBundle();

    /**
     * Returns the resource bundle used for the internationalization of special strings like the the toolwindow id.
     * @return the resource bundle used for the internationalization of special strings like the the toolwindow id.
     */
    ResourceBundle getUserResourceBundle();

    /**
     * This method is equivalent to <code>getResourceBundle().getString(key)</code>
     * @param key the key for the desired string
     * @return the string for the given key
     */
    String getString(String key);

    /**
     * This method is equivalent to <code>getUserResourceBundle().getString(key)</code>
     * @param key the key for the desired string
     * @return the string for the given key
     */
    String getUserString(String key);

}
