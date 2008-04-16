package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
import org.noos.common.object.ObjectCustomizer;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeEventSource;
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
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.basic.BasicButtonUI;
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

    protected Map<String, Icon> icons;
    protected Map<String, BufferedImage> images;
    protected Map<String, Color> colors;
    protected Map<String, ObjectCreator<Component>> cmpCreators;
    protected Map<String, ObjectCreator<ComponentUI>> cmpUiCreators;
    protected Map<String, ObjectCustomizer<Component>> cmpCustomizers;
    protected Map<Class, ObjectCreator> instanceCreators;
    protected Map cache;

    protected String bundlePath;
    protected ResourceBundle resourceBundle;
    protected ResourceBundle userResourceBundle;

    protected TransparencyManager<Window> transparencyManager;


    public MyDoggyResourceManager() {
        this.icons = new Hashtable<String, Icon>();
        this.colors = new Hashtable<String, Color>();
        this.images = new Hashtable<String, BufferedImage>();
        this.cache = new HashMap();

        loadResources();
        initComponentCreators();
        initTransparencyManager();
    }


    public <T> T createInstance(Class<T> clazz, Context context) {
        return (T) instanceCreators.get(clazz).create(context);
    }

    public Component createComponent(String key, Context context) {
        return applyCustomization(key,
                                  cmpCreators.get(key).create(context),
                                  context);
    }

    public ComponentUI createComponentUI(String key, Context context) {
        return cmpUiCreators.get(key).create(context);
    }

    public Component applyCustomization(String key, Component component, Context context) {
        if (cmpCustomizers.containsKey(key))
            cmpCustomizers.get(key).customize(component, context);
        return component;
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
        firePropertyChangeEvent("color." + id, oldColor, color);
        return oldColor;
    }

    public TransparencyManager<Window> getTransparencyManager() {
        return transparencyManager;
    }

    public void setTransparencyManager(TransparencyManager<Window> transparencyManager) {
        this.transparencyManager = transparencyManager;
    }

    public BufferedImage getBufferedImage(String id) {
        return images.get(id);
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
        return colors;
    }

    public Map<String, Icon> getIcons() {
        return icons;
    }


    public String getProperty(String name) {
        return resources.getProperty("Property." + name);
    }

    public void putProperty(String name, String value) {
        Object old = resources.put("Property." + name, value);

        // Clear cache...
        cache.clear();

        firePropertyChangeEvent(name, old, value);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        Boolean result = (Boolean) cache.get("Bool." + name);
        if (result != null)
            return result;

        String propertyValue = getProperty(name);
        if (propertyValue == null || "".equals(propertyValue.trim()))
            result = defaultValue;
        else {
            try {
                result = Boolean.parseBoolean(propertyValue);
            } catch (Exception e) {
                result = defaultValue;
            }
        }

        cache.put("Bool." + name, result);
        return result;
    }

    public void putBoolean(String name, boolean value) {
        putProperty(name, String.valueOf(value));

        cache.put("Bool." + name, value);
    }

    public float getFloat(String name, float defaultValue) {
        Float result = (Float) cache.get("Float." + name);
        if (result != null)
            return result;

        String propertyValue = getProperty(name);
        if (propertyValue == null || "".equals(propertyValue.trim()))
            result = defaultValue;
        else {
            try {
                result = Float.parseFloat(propertyValue);
            } catch (Exception e) {
                result = defaultValue;
            }
        }

        cache.put("Float." + name, result);
        return result;
    }

    public void putFloat(String name, float value) {
        putProperty(name, String.valueOf(value));

        cache.put("Float." + name, value);
    }

    public int getInt(String name, int defaultValue) {
        Integer result = (Integer) cache.get("Int." + name);
        if (result != null)
            return result;

        String propertyValue = getProperty(name);
        if (propertyValue == null || "".equals(propertyValue.trim()))
            result = defaultValue;
        else {
            try {
                result = Integer.parseInt(propertyValue);
            } catch (Exception e) {
                result = defaultValue;
            }
        }

        cache.put("Int." + name, result);
        return result;
    }

    public void putInt(String name, int value) {
        putProperty(name, String.valueOf(value));
        
        cache.put("Int." + name, value);
    }

    public void putObject(Object key, Object value) {
        resources.put(key, value);
    }

    public <T> T getObject(Class<T> clazz, T defaultValue) {
        Object value = resources.get(clazz);
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
        resources = SwingUtil.loadPropertiesFile(resourceName, this.getClass().getClassLoader());

        loadIcons();
        loadColors();
        loadImages();
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

    protected void loadImages() {
        String prefix = "Image.";

        for (Object key : resources.keySet()) {
            String strKey = key.toString();
            if (strKey.startsWith(prefix)) {
                String imageKey = strKey.substring(prefix.length());
                String imageUrl = resources.getProperty(strKey);

                images.put(imageKey, loadImage(imageUrl));
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

    protected BufferedImage loadImage(String url) {
        try {
            return ImageIO.read(this.getClass().getClassLoader().getResource(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        cmpCreators = new Hashtable<String, ObjectCreator<Component>>();
        cmpCreators.put(MyDoggyKeySpace.ANCHOR_SPLIT_PANE, new BarSplitPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.ANCHOR_CONTENT_PANE, new BarContentPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.CORNER_CONTENT_PANE, new CornerContentPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_MANAGER_CONTENT_CONTAINER, new MyDoggyManagerMainContainerComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.DESKTOP_CONTENT_PANE, new DesktopContentPaneComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR, new ToolWindowTitleBarComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON, new ToolWindowTitleButtonComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_SCROLL_BAR_ARROW, new ToolScrollBarArrowComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.TOOL_WINDOW_CONTAINER, new ToolWindowCmpContainerComponentCreator());
        cmpCreators.put(MyDoggyKeySpace.MULTI_SPLIT_CONTAINER_SPLIT, new ObjectCreator<Component>() {
            public Component create(Context context) {
                return new JSplitPane((Integer) context.get("newOrientation"));
            }
        });

        cmpUiCreators = new Hashtable<String, ObjectCreator<ComponentUI>>();
        cmpUiCreators.put(MyDoggyKeySpace.REPRESENTATIVE_ANCHOR_BUTTON_UI, new RepresentativeAnchorButtonComponentUICreator());
        cmpUiCreators.put(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR_UI, new ToolWindowTitleBarComponentUICreator());

        cmpCustomizers = new Hashtable<String, ObjectCustomizer<Component>>();
        cmpCustomizers.put(MyDoggyKeySpace.TOOL_WINDOW_MANAGER, new MyDoggyManagerPanelComponentCustomizer());
        cmpCustomizers.put(MyDoggyKeySpace.TOOL_WINDOW_CONTAINER, new ToolWindowCmpContainerComponentCustomizer());

        instanceCreators = new Hashtable<Class, ObjectCreator>();
        instanceCreators.put(TitleBarButtons.class, new TitleBarButtonsInstanceCreator());
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

    public static class BarContentPaneComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            return new JPanel();/* {
                protected void paintComponent(Graphics g) {
                    TableLayout tableLyout = (TableLayout) getLayout();
                    int y = 0;
                    Random random = new Random();
                    for (int i = 0, sizei = tableLyout.getNumRow(); i < sizei; i++) {

                        int x = 0;
                        for (int j = 0, sizej = tableLyout.getNumColumn(); j < sizej; j++) {
                            g.setColor(new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256)));
                            g.fillRect(x, y, (int) tableLyout.getColumn(j), (int) tableLyout.getRow(i));
                            x+= tableLyout.getColumn(j);
                        }
                        y+= tableLyout.getRow(i);
                    }
                    super.paintComponent(g);
                }
            };*/
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

    public static class ToolWindowTitleBarComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JPanel titleBar = new JPanel() {
                public void setUI(PanelUI ui) {
                    if (ui instanceof ToolWindowTitleBarUI)
                        super.setUI(ui);
                }
            };
            titleBar.setBorder(null);
            titleBar.setUI((PanelUI) context.get(ResourceManager.class)
                    .createComponentUI(MyDoggyKeySpace.TOOL_WINDOW_TITLE_BAR_UI, context)
            );
            return titleBar;
        }
    }

    public static class ToolWindowTitleButtonComponentCreator implements ObjectCreator<Component> {

        public Component create(Context context) {
            JButton button = new ToolWindowActiveButton();
            button.setUI((ButtonUI) BasicButtonUI.createUI(button));
            return button;
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
            ResourceManager resourceManager = context.get(ResourceManager.class);

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
            label.setIcon(resourceManager.getIcon((String) context.get("icon")));

            return label;
        }
    }


    public static class RepresentativeAnchorButtonComponentUICreator implements ObjectCreator<ComponentUI> {

        public ComponentUI create(Context context) {
            return new ToolWindowRepresentativeAnchorUI(context.get(ToolWindowDescriptor.class));
        }
    }

    public static class ToolWindowTitleBarComponentUICreator implements ObjectCreator<ComponentUI> {

        public ComponentUI create(Context context) {
            return new ToolWindowTitleBarUI(context.get(ToolWindowDescriptor.class),
                                            context.get(ToolWindowContainer.class));
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


    public static class TitleBarButtonsInstanceCreator implements ObjectCreator {

        public Object create(Context context) {
            return new DefaultTitleBarButtons(
                    context.get(ToolWindowDescriptor.class),
                    context.get(ToolWindowContainer.class)
            );
        }

    }

    public static class ParentOfQuestionInstanceCreator implements ObjectCreator {

        public Object create(Context context) {
            return new ParentOfQuestion(context.get(Component.class));
        }

    }

}