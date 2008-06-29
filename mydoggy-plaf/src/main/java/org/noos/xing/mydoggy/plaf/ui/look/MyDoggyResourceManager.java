package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
import org.noos.common.object.ObjectCustomizer;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import static org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace.*;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDesktopManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.DebugSplitPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.WindowTransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyResourceManager extends PropertyChangeEventSource implements ResourceManager {

    private static final String resourceName = "resources.properties";

    protected Properties resources;

    protected Map<String, ObjectCreator<Component>> cmpCreators;
    protected Map<String, ObjectCreator<ComponentUI>> cmpUiCreators;
    protected Map<String, ObjectCustomizer<Component>> cmpCustomizers;
    protected Map<Class, ObjectCreator> instanceCreators;

    protected String bundlePath;
    protected ResourceBundle resourceBundle;
    protected ResourceBundle userResourceBundle;

    protected TransparencyManager<Window> transparencyManager;


    public MyDoggyResourceManager() {
        loadResources();
        initComponentCreators();
        initTransparencyManager();
    }


    public <T> T createInstance(Class<T> clazz, Context context) {
        return (T) instanceCreators.get(clazz).create(context);
    }

    public <T extends Component> T createComponent(String key, Context context) {
        return (T) applyCustomization(key,
                                      cmpCreators.get(key).create(context),
                                      context);
    }

    public ComponentUI createComponentUI(String key, Context context) {
        return cmpUiCreators.get(key).create(context);
    }

    public <T extends Component> T applyCustomization(String key, T component, Context context) {
        if (cmpCustomizers.containsKey(key))
            cmpCustomizers.get(key).customize(component, context);
        return component;
    }


    public Icon getIcon(String id) {
        return UIManager.getIcon(id);
    }

    public Icon putIcon(String id, Icon icon) {
        return (Icon) UIManager.put(id, icon);
    }

    public Color getColor(String id) {
        return UIManager.getColor(id);
    }

    public Color putColor(String id, Color color) {
        return (Color) UIManager.put(id, color);
    }

    public void putImage(String name, BufferedImage bufferedImage) {
        UIManager.put(name, bufferedImage);
    }

    public BufferedImage getImage(String id) {
        return (BufferedImage) UIManager.get(id);
    }

    public TransparencyManager<Window> getTransparencyManager() {
        return transparencyManager;
    }

    public void setTransparencyManager(TransparencyManager<Window> transparencyManager) {
        this.transparencyManager = transparencyManager;
        UIManager.put(TransparencyManager.class, transparencyManager);
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
        try {
            return resourceBundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public String getUserString(String key) {
        try {
            return (userResourceBundle != null) ? userResourceBundle.getString(key) : key;
        } catch (Exception e) {
            return key;
        }
    }


    public Map<String, Color> getColors() {
        return Collections.emptyMap();    // TODO:
    }

    public Map<String, Icon> getIcons() {
        return Collections.emptyMap();
    }


    public String getProperty(String name) {
        return UIManager.getString(name);
    }

    public void putProperty(String name, String value) {
        UIManager.put(name, value);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        return SwingUtil.getBoolean(name, defaultValue);
    }

    public void putBoolean(String name, boolean value) {
        UIManager.put(name, value);
    }

    public float getFloat(String name, float defaultValue) {
        return SwingUtil.getFloat(name, defaultValue);
    }

    public void putFloat(String name, float value) {
        UIManager.put(name, value);
    }

    public int getInt(String name, int defaultValue) {
        return SwingUtil.getInt(name, defaultValue);
    }

    public void putInt(String name, int value) {
        UIManager.put(name, value);
    }

    public void putObject(Object key, Object value) {
        UIManager.put(key, value);
    }

    public <T> T getObject(Class<T> clazz, T defaultValue) {
        Object value = UIManager.get(clazz);
        if (clazz.isInstance(value))
            return (T) value;
        return defaultValue;
    }


    public void putInstanceCreator(Class aClass, ObjectCreator instanceCreator) {
        instanceCreators.put(aClass, instanceCreator);
    }

    public void putComponentCreator(String key, ObjectCreator<Component> componentCreator) {
        cmpCreators.put(key, componentCreator);
    }

    public void putComponentCustomizer(String key, ObjectCustomizer<Component> componentCustomizer) {
        cmpCustomizers.put(key, componentCustomizer);
    }

    public void putComponentUICreator(String key, ObjectCreator<ComponentUI> componentUICreator) {
        cmpUiCreators.put(key, componentUICreator);
    }


    protected void loadResources() {
        // Load from file
        resources = SwingUtil.loadPropertiesFile(resourceName, this.getClass().getClassLoader());

        for (Object key : resources.keySet()) {
            String strKey = key.toString();
            int pointIndex = strKey.indexOf('.');
            if (pointIndex != -1) {
                String prefix = strKey.substring(0, pointIndex);
                String propertyName = strKey.substring(prefix.length() + 1);

                if (!UIManager.getDefaults().containsKey(propertyName))
                    loadResource(prefix,
                                 propertyName,
                                 resources.getProperty(strKey));
            }
        }

        // Load static
        loadObjects();

    }

    protected void loadResource(String prefix, String name, String value) {
        if ("Icon".equals(prefix)) {
            // Load icon
            loadIcon(name, value);
        } else if ("Color".equals(prefix)) {
            // Load Color
            loadColor(name, value);
        } else if ("Image".equals(prefix)) {
            // Load Color
            loadImage(name, value);
        } else if ("ResourceBundle".equals(prefix)) {
            loadResourceBundles(value);
        } else if ("Int".equals(prefix)) {
            loadInt(name, value);
        } else if ("Float".equals(prefix)) {
            loadFloat(name, value);
        } else if ("String".equals(prefix)) {
            loadString(name, value);
        }
    }

    protected void loadIcon(String name, String url) {
        putIcon(name, SwingUtil.loadIcon(url));
    }

    protected void loadImage(String name, String url) {
        try {
            putImage(name, ImageIO.read(this.getClass().getClassLoader().getResource(url)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadColor(String name, String colorDef) {
        Color color;
        colorDef = colorDef.toLowerCase();
        if ("black".equals(colorDef))
            color = Color.BLACK;
        else if ("blue".equals(colorDef))
            color = Color.BLUE;
        else if ("cyan".equals(colorDef))
            color = Color.CYAN;
        else if ("dark_grey".equals(colorDef))
            color = Color.DARK_GRAY;
        else if ("gray".equals(colorDef))
            color = Color.GRAY;
        else if ("green".equals(colorDef))
            color = Color.GREEN;
        else if ("magenta".equals(colorDef))
            color = Color.MAGENTA;
        else if ("orange".equals(colorDef))
            color = Color.ORANGE;
        else if ("pink".equals(colorDef))
            color = Color.PINK;
        else if ("red".equals(colorDef))
            color = Color.RED;
        else if ("white".equals(colorDef))
            color = Color.WHITE;
        else if ("yellow".equals(colorDef))
            color = Color.YELLOW;
        else {
            String[] elms = colorDef.split(",");
            color = new Color(
                    Integer.parseInt(elms[0].trim()),
                    Integer.parseInt(elms[1].trim()),
                    Integer.parseInt(elms[2].trim())
            );
        }

        putColor(name, color);
    }

    protected void loadInt(String name, String value) {
        try {
            putInt(name, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    protected void loadFloat(String name, String value) {
        try {
            putFloat(name, Float.parseFloat(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    protected void loadString(String name, String value) {
        putProperty(name, value);
    }

    protected void loadResourceBundles(String bundlePath) {
        if (bundlePath == null)
            bundlePath = "org/noos/xing/mydoggy/plaf/ui/messages/messages";
        this.bundlePath = bundlePath;
    }

    protected void loadObjects() {
        putObject(FindFocusableQuestion.class, new FindFocusableQuestion());
    }


    protected void initComponentCreators() {
        cmpCreators = new Hashtable<String, ObjectCreator<Component>>();
        cmpCreators.put(ANCHOR_SPLIT_PANE, new BarSplitPaneComponentCreator());
        cmpCreators.put(CORNER_CONTENT_PANE, new CornerContentPaneComponentCreator());
        cmpCreators.put(TOOL_WINDOW_MANAGER_CONTENT_CONTAINER, new MyDoggyManagerMainContainerComponentCreator());
        cmpCreators.put(DESKTOP_CONTENT_PANE, new DesktopContentPaneComponentCreator());
        cmpCreators.put(TOOL_SCROLL_BAR_ARROW, new ToolScrollBarArrowComponentCreator());
        cmpCreators.put(TOOL_WINDOW_CONTAINER, new ToolWindowCmpContainerComponentCreator());
        cmpCreators.put(MULTI_SPLIT_CONTAINER_SPLIT, new ObjectCreator<Component>() {
            public Component create(Context context) {
                return new JSplitPane((Integer) context.get("newOrientation"));
            }
        });

        cmpUiCreators = new Hashtable<String, ObjectCreator<ComponentUI>>();

        cmpCustomizers = new Hashtable<String, ObjectCustomizer<Component>>();
        cmpCustomizers.put(TOOL_WINDOW_MANAGER, new MyDoggyManagerPanelComponentCustomizer());
        cmpCustomizers.put(TOOL_WINDOW_CONTAINER, new ToolWindowCmpContainerComponentCustomizer());

        instanceCreators = new Hashtable<Class, ObjectCreator>();
        instanceCreators.put(ParentOfQuestion.class, new ParentOfQuestionInstanceCreator());
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


    public static class ToolWindowCmpContainerComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JPanel panel = new JPanel();
            panel.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));
            return panel;
        }
    }

    public static class BarSplitPaneComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JSplitPane splitPane = new DebugSplitPane((Integer) context.get("newOrientation"));
            splitPane.setBorder(null);
            splitPane.setContinuousLayout(true);
            splitPane.setDividerSize(0);
            splitPane.setDividerLocation(300);
            splitPane.setLeftComponent(null);
            splitPane.setRightComponent(null);
            return splitPane;
        }
    }

    public static class CornerContentPaneComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            return new JPanel();
        }
    }

    public static class DesktopContentPaneComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JDesktopPane desktopPane = new JDesktopPane();
            desktopPane.setDesktopManager(new ContentDesktopManager());
            return desktopPane;
        }
    }

    public static class MyDoggyManagerMainContainerComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            return panel;
        }
    }

    public static class ToolScrollBarArrowComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JLabel label = new JLabel() {
                public void setUI(LabelUI ui) {
                    if (ui instanceof ToolScrollBarArrowUI)
                        super.setUI(ui);
                }
            };
            label.setUI(new ToolScrollBarArrowUI());
            label.setPreferredSize(new Dimension(16, 16));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setOpaque(false);
            label.setFocusable(false);
            label.setBackground(Colors.orange);
            label.setIcon(UIManager.getIcon(context.get("icon")));

            return label;
        }
    }


    public static class MyDoggyManagerPanelComponentCustomizer implements ObjectCustomizer<Component> {

        public Component customize(Component component, Context context) {
//            component.setBackground(Color.black);
            return component;
        }
    }

    public static class ToolWindowCmpContainerComponentCustomizer implements ObjectCustomizer<Component> {

        public Component customize(Component component, Context context) {
            JPanel panel = (JPanel) component;
            panel.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));

            return panel;
        }
    }


    public static class ParentOfQuestionInstanceCreator implements ObjectCreator {

        public Object create(Context context) {
            return new ParentOfQuestion(context.get(Component.class));
        }

    }

}