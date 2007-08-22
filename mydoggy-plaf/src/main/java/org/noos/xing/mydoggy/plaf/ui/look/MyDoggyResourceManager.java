package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDesktopManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.DebugSplitPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowActiveButton;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyResourceManager implements ResourceManager {

    private static final String resourceName = "resources.properties";

    protected Properties resources;

    protected Map<String, Icon> icons;
    protected Map<String, Color> colors;
    protected Map<String, ComponentCreator> cmpCreators;
    protected Map<String, ComponentUICreator> cmpUiCreators;
    protected Map<String, ComponentCustomizer> cmpCustomizers;

    private String bundlePath;
    private ResourceBundle resourceBundle;
    private ResourceBundle userResourceBundle;


    public MyDoggyResourceManager() {
        this.icons = new Hashtable<String, Icon>();
        this.colors = new Hashtable<String, Color>();

        loadResources();
        initComponentCreators();
    }


    public Icon getIcon(String id) {
        return icons.get(id);
    }

    public Color getColor(String id) {
        return colors.get(id);
    }

    public Component createComponent(String key, ToolWindowManager manager, Object... args) {
        return applyCustomization(key, 
                                  cmpCreators.get(key).createComponent(manager, args),
                                  args);
    }

    public ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args) {
        return cmpUiCreators.get(key).createComponentUI(manager, args);
    }

    public Component applyCustomization(String key, Component component, Object... args) {
        if (cmpCustomizers.containsKey(key))
            cmpCustomizers.get(key).applyCustomization(component, args);
        return component;
    }

    public void setLocale(Locale locale) {
        this.resourceBundle = initResourceBundle(locale,
                                                 bundlePath,
                                                 this.getClass().getClassLoader());
    }

    public void setUserBundle(Locale locale, String bundle, ClassLoader classLoader) {
        this.userResourceBundle = initResourceBundle(locale, bundle, classLoader);
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public ResourceBundle getUserResourceBundle() {
        return userResourceBundle;
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    public String getUserString(String key) {
        return (userResourceBundle != null) ? userResourceBundle.getString(key) : key;
    }


    protected void loadResources() {
        resources = SwingUtil.loadPropertiesFile(resourceName, this.getClass().getClassLoader());

        loadIcons();
        loadColors();
        loadResourceBundles();
    }


    protected void loadIcons() {
        String prefix = "Icon.";

        for (Object key : resources.keySet()) {
            String strKey = key.toString();
            if (strKey.startsWith(prefix)) {
                String iconKey = strKey.substring(prefix.length());
                String iconUrl = resources.getProperty(strKey);

                icons.put(iconKey, loadIcon(iconUrl));
            }
        }
    }

    protected void loadColors() {
        String prefix = "Color.";

        for (Object key : resources.keySet()) {
            String strKey = key.toString();
            if (strKey.startsWith(prefix)) {
                String colorKey = strKey.substring(prefix.length());
                String colorDef = resources.getProperty(strKey);

                colors.put(colorKey, loadColor(colorDef));
            }
        }
    }

    protected void loadResourceBundles() {
        bundlePath = resources.getProperty("ResourceBundle");
        if (bundlePath == null)
            bundlePath = "org/noos/xing/mydoggy/plaf/ui/messages/messages";
    }

    protected Icon loadIcon(String url) {
        return SwingUtil.loadIcon(url);
    }

    protected Color loadColor(String colorDef) {
        colorDef = colorDef.toLowerCase();
        if ("black".equals(colorDef))
            return Color.BLACK;
        else if ("gray".equals(colorDef))
            return Color.GRAY;

        String[] elms = colorDef.split(",");
        return new Color(
                Integer.parseInt(elms[0].trim()),
                Integer.parseInt(elms[1].trim()),
                Integer.parseInt(elms[2].trim())
        );
    }

    protected void initComponentCreators() {
        cmpCreators = new Hashtable<String, ComponentCreator>();
        cmpCreators.put(ResourceManager.BAR_SPLIT_PANE, new BarSplitPaneComponentCreator());
        cmpCreators.put(ResourceManager.BAR_CONTENT_PANE, new BarContentPaneComponentCreator());
        cmpCreators.put(ResourceManager.CORNER_CONTENT_PANE, new CornerContentPaneComponentCreator());
        cmpCreators.put(ResourceManager.MY_DOGGY_MANAGER_MAIN_CONTAINER, new MyDoggyManagerMainContainerComponentCreator());
        cmpCreators.put(ResourceManager.DESKTOP_CONTENT_PANE, new DesktopContentPaneComponentCreator());
        cmpCreators.put(ResourceManager.TOOL_WINDOW_TITLE_BAR, new ToolWindowTitleBarComponentCreator());
        cmpCreators.put(ResourceManager.TOOL_WINDOW_TITLE_BUTTON, new ToolWindowTitleButtonComponentCreator());

        cmpUiCreators = new Hashtable<String, ComponentUICreator>();
        cmpUiCreators.put(ResourceManager.REPRESENTATIVE_ANCHOR_BUTTON_UI, new RepresentativeAnchorButtonComponentUICreator());
        cmpUiCreators.put(ResourceManager.TOOL_WINDOW_TITLE_BAR_UI, new ToolWindowTitleBarComponentUICreator());

        cmpCustomizers = new Hashtable<String, ComponentCustomizer>();
        cmpCustomizers.put(ResourceManager.MY_DOGGY_MANAGER_PANEL, new MyDoggyManagerPanelComponentCustomizer());
    }

    protected ResourceBundle initResourceBundle(Locale locale, String bundle, ClassLoader classLoader) {
        ResourceBundle result;
        if (locale == null)
            locale = Locale.getDefault();

        try {
            if (classLoader == null)
                result = ResourceBundle.getBundle(bundle, locale);
            else
                result = ResourceBundle.getBundle(bundle, locale, classLoader);
        } catch (Throwable e) {
            e.printStackTrace();

            result = new ResourceBundle() {
                protected Object handleGetObject(String key) {
                    return key;
                }

                public Enumeration<String> getKeys() {
                    return new Enumeration<String>() {
                        public boolean hasMoreElements() {
                            return false;
                        }

                        public String nextElement() {
                            return null;
                        }
                    };
                }
            };
        }
        return result;
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
            return new JPanel();
        }
    }

    public static class CornerContentPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            return new JPanel();
        }
    }

    public static class DesktopContentPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JDesktopPane desktopPane = new JDesktopPane();
            desktopPane.setDesktopManager(new ContentDesktopManager());
            return desktopPane;
        }
    }

    public static class ToolWindowTitleBarComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JPanel titleBar = new JPanel() {
                public void setUI(PanelUI ui) {
                    if (ui instanceof ToolWindowTitleBarUI)
                        super.setUI(ui);
                }
            };
            titleBar.setBorder(null);
            titleBar.setUI(new ToolWindowTitleBarUI((ToolWindowDescriptor) args[0], (DockedContainer) args[1]));
            return titleBar;
        }
    }

    public static class ToolWindowTitleButtonComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JButton button = new ToolWindowActiveButton();
            button.setUI((ButtonUI) BasicButtonUI.createUI(button));
            return button;
        }

    }

    public static class MyDoggyManagerMainContainerComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, Object... args) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            return panel;
        }
    }

    public static class RepresentativeAnchorButtonComponentUICreator implements ComponentUICreator {

        public ComponentUI createComponentUI(ToolWindowManager manager, Object... args) {
            return new RepresentativeAnchorUI((ToolWindowDescriptor) args[0]);
        }
    }

    public static class ToolWindowTitleBarComponentUICreator implements ComponentUICreator {

        public ComponentUI createComponentUI(ToolWindowManager manager, Object... args) {
            return new ToolWindowTitleBarUI((ToolWindowDescriptor) args[0], (DockedContainer) args[1]);
        }
    }

    public static class MyDoggyManagerPanelComponentCustomizer implements ComponentCustomizer {

        public void applyCustomization(Component component, Object... args) {
//            component.setBackground(Color.black);
        }
    }


}
