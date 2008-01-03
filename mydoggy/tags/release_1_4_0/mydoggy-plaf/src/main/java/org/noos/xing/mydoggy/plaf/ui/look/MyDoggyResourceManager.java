package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDesktopManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.DebugSplitPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.DefaultTitleBarButtons;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowActiveButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.WindowTransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.mydoggy.plaf.ui.util.DummyResourceBundle;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    protected Map<Class, InstanceCreator> instanceCreators;

    protected String bundlePath;
    protected ResourceBundle resourceBundle;
    protected ResourceBundle userResourceBundle;

    protected TransparencyManager<Window> transparencyManager;

    protected EventListenerList listenerList;

    public MyDoggyResourceManager() {
        this.icons = new Hashtable<String, Icon>();
        this.colors = new Hashtable<String, Color>();
        this.listenerList = new EventListenerList();

        loadResources();
        initComponentCreators();
        initTransparencyManager();
    }


    public Icon getIcon(String id) {
        return icons.get(id);
    }

    public Icon putIcon(String id, Icon icon) {
        return icons.put(id, icon);
    }

    public Color getColor(String id) {
        return colors.get(id);
    }

    public Color putColor(String id, Color color) {
        Color oldColor = colors.put(id, color);
        fireColorChanged(id, oldColor, color);
        return oldColor;
    }

    public TransparencyManager<Window> getTransparencyManager() {
        return transparencyManager;
    }

    public void setTransparencyManager(TransparencyManager<Window> transparencyManager) {
        this.transparencyManager = transparencyManager;
    }

    public <T> T createInstance(Class<T> clazz, Object... args) {
        return (T) instanceCreators.get(clazz).createComponent(args);
    }

    public Component createComponent(String key, ToolWindowManager manager, Object... args) {
        return applyCustomization(key,
                cmpCreators.get(key).createComponent(manager, this, args),
                args);
    }

    public ComponentUI createComponentUI(String key, ToolWindowManager manager, Object... args) {
        return cmpUiCreators.get(key).createComponentUI(manager, this, args);
    }

    public Component applyCustomization(String key, Component component, Object... args) {
        if (cmpCustomizers.containsKey(key))
            cmpCustomizers.get(key).applyCustomization(component, this, args);
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

    public void setUserBundle(ResourceBundle userBundle) {
        if (userBundle == null)
            userResourceBundle = new DummyResourceBundle();
        else
            this.userResourceBundle = userBundle;
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

    public Map<String, Color> getColors() {
        return colors;
    }

    public Map<String, Icon> getIcons() {
        return icons;
    }

    public String getProperty(String name) {
        return resources.getProperty("Property." + name);
    }

    public void putProperty(String name, String value) {
        resources.put("Property." + name, value);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String propertyValue = getProperty(name);
        if (propertyValue == null || "".equals(propertyValue.trim()))
            return defaultValue;

        try {
            return Boolean.parseBoolean(propertyValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener changeListener) {
        listenerList.add(PropertyChangeListener.class, changeListener);
    }

    public void removePropertyChangeListener(PropertyChangeListener changeListener) {
        listenerList.remove(PropertyChangeListener.class, changeListener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return listenerList.getListeners(PropertyChangeListener.class);
    }

    public void putInstanceCreator(Class aClass, InstanceCreator instanceCreator) {
        instanceCreators.put(aClass, instanceCreator);
    }

    public void putComponentCreator(String key, ComponentCreator componentCreator) {
        cmpCreators.put(key, componentCreator);
    }

    public void putComponentCustomizer(String key, ComponentCustomizer componentCustomizer) {
        cmpCustomizers.put(key, componentCustomizer);
    }

    public void putComponentUICreator(String key, ComponentUICreator componentUICreator) {
        cmpUiCreators.put(key, componentUICreator);
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
        else if ("blue".equals(colorDef))
            return Color.BLUE;
        else if ("cyan".equals(colorDef))
            return Color.CYAN;
        else if ("dark_grey".equals(colorDef))
            return Color.DARK_GRAY;
        else if ("gray".equals(colorDef))
            return Color.GRAY;
        else if ("green".equals(colorDef))
            return Color.GREEN;
        else if ("magenta".equals(colorDef))
            return Color.MAGENTA;
        else if ("orange".equals(colorDef))
            return Color.ORANGE;
        else if ("pink".equals(colorDef))
            return Color.PINK;
        else if ("red".equals(colorDef))
            return Color.RED;
        else if ("white".equals(colorDef))
            return Color.WHITE;
        else if ("yellow".equals(colorDef))
            return Color.YELLOW;

        String[] elms = colorDef.split(",");
        return new Color(
                Integer.parseInt(elms[0].trim()),
                Integer.parseInt(elms[1].trim()),
                Integer.parseInt(elms[2].trim())
        );
    }

    protected void initComponentCreators() {
        cmpCreators = new Hashtable<String, ComponentCreator>();
        cmpCreators.put(MyDoggyKeySpace.ANCHOR_SPLIT_PANE, new BarSplitPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.ANCHOR_CONTENT_PANE, new BarContentPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.CORNER_CONTENT_PANE, new CornerContentPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_MANAGER_CONTENT_CONTAINER, new MyDoggyManagerMainContainerComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.DESKTOP_CONTENT_PANE, new DesktopContentPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR, new ToolWindowTitleBarComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON, new ToolWindowTitleButtonComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_SCROLL_BAR_ARROW, new ToolScrollBarArrowComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_CONTAINER, new ToolWindowCmpContainerComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.MULTI_SPLIT_CONTAINER_SPLIT, new ComponentCreator() {
            public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
                return new JSplitPane((Integer) args[0]);
            }
        });

        cmpUiCreators = new Hashtable<String, ComponentUICreator>();
        cmpUiCreators.put(MyDoggyKeySpace.REPRESENTATIVE_ANCHOR_BUTTON_UI, new RepresentativeAnchorButtonComponentUICreator());
        cmpUiCreators.put(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR_UI, new ToolWindowTitleBarComponentUICreator());

        cmpCustomizers = new Hashtable<String, ComponentCustomizer>();
        cmpCustomizers.put(MyDoggyKeySpace.TOOL_WINDOW_MANAGER, new MyDoggyManagerPanelComponentCustomizer());
        cmpCustomizers.put(MyDoggyKeySpace.TOOL_WINDOW_CONTAINER, new ToolWindowCmpContainerComponentCustomizer());

        instanceCreators = new Hashtable<Class, InstanceCreator>();
        instanceCreators.put(TitleBarButtons.class, new TitleBarButtonsInstanceCreator());
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
            result = new DummyResourceBundle();
        }
        return result;
    }

    protected void initTransparencyManager() {
        setTransparencyManager(new WindowTransparencyManager());
    }

    protected void fireColorChanged(String key, Color oldValue, Color newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, key, oldValue, newValue);
        for (PropertyChangeListener listener : listenerList.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }
    }


    public static interface InstanceCreator {
        Object createComponent(Object... args);
    }

    public static interface ComponentCreator {
        Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args);
    }

    public static interface ComponentUICreator {
        ComponentUI createComponentUI(ToolWindowManager manager, ResourceManager resourceManager, Object... args);
    }

    public static interface ComponentCustomizer {

        void applyCustomization(Component component, ResourceManager resourceManager, Object... args);
    }


    public static class ToolWindowCmpContainerComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            JPanel panel = new JPanel();
            panel.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
            return panel;
        }
    }

    public static class BarSplitPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
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

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            return new JPanel();
        }
    }

    public static class CornerContentPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            return new JPanel();
        }
    }

    public static class DesktopContentPaneComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            JDesktopPane desktopPane = new JDesktopPane();
            desktopPane.setDesktopManager(new ContentDesktopManager());
            return desktopPane;
        }
    }

    public static class ToolWindowTitleBarComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            JPanel titleBar = new JPanel() {
                public void setUI(PanelUI ui) {
                    if (ui instanceof ToolWindowTitleBarUI)
                        super.setUI(ui);
                }
            };
            titleBar.setBorder(null);
            titleBar.setUI(
                    (PanelUI) resourceManager.createComponentUI(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR_UI, manager, args)
            );
            return titleBar;
        }
    }

    public static class ToolWindowTitleButtonComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            JButton button = new ToolWindowActiveButton();
            button.setUI((ButtonUI) BasicButtonUI.createUI(button));
            return button;
        }

    }

    public static class MyDoggyManagerMainContainerComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            return panel;
        }
    }

    public static class ToolScrollBarArrowComponentCreator implements ComponentCreator {

        public Component createComponent(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            JLabel label = new JLabel() {
                public void setUI(LabelUI ui) {
                    if (ui instanceof ToolScrollBarArrowUI)
                        super.setUI(ui);
                }
            };
            label.setUI(new ToolScrollBarArrowUI(resourceManager));
            label.setPreferredSize(new Dimension(16, 16));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);
            label.setFocusable(false);
            label.setBackground(Colors.orange);
            label.setIcon(resourceManager.getIcon((String) args[0]));

            return label;
        }
    }


    public static class RepresentativeAnchorButtonComponentUICreator implements ComponentUICreator {

        public ComponentUI createComponentUI(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            return new RepresentativeAnchorUI((ToolWindowDescriptor) args[0]);
        }
    }

    public static class ToolWindowTitleBarComponentUICreator implements ComponentUICreator {

        public ComponentUI createComponentUI(ToolWindowManager manager, ResourceManager resourceManager, Object... args) {
            return new ToolWindowTitleBarUI((ToolWindowDescriptor) args[0], (DockedContainer) args[1]);
        }
    }


    public static class MyDoggyManagerPanelComponentCustomizer implements ComponentCustomizer {

        public void applyCustomization(Component component, ResourceManager resourceManager, Object... args) {
//            component.setBackground(Color.black);
        }
    }

    public static class ToolWindowCmpContainerComponentCustomizer implements ComponentCustomizer {

        public void applyCustomization(Component component, ResourceManager resourceManager, Object... args) {
            JPanel panel = (JPanel) component;
            panel.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
        }
    }


    public static class TitleBarButtonsInstanceCreator implements InstanceCreator {
        public Object createComponent(Object... args) {
            return new DefaultTitleBarButtons(
                    (ToolWindowDescriptor) args[0],
                    (DockedContainer) args[1]
            );
        }
    }

}