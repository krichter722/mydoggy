package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.common.context.Context;
import org.noos.common.object.ObjectCreator;
import org.noos.common.object.ObjectCustomizer;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.transparency.WindowTransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.DummyResourceBundle;
import org.noos.xing.mydoggy.plaf.ui.util.FindFocusableQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyResourceManager extends PropertyChangeEventSource implements ResourceManager {

    private static final String resourceName = "resources.properties";

    protected ClassLoader classLoader;
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
        // To initialize the resource manager use @link{setClassLoader}
    }


    public void setClassloader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        
        // Now load all the resources...
        loadResources();
        initComponentCreators();
        initTransparencyManager();

        // Put a flag
        UIManager.put(ResourceManager.class, this);
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
        Icon oldIcon = (Icon) UIManager.put(id, icon);

        List<String> icons = (List<String>) UIManager.get("mydoggy.icons");
        if (icons == null) {
            icons = new ArrayList<String>();
            UIManager.put("mydoggy.icons", icons);
        }
        icons.add(id);

        return oldIcon;
    }

    public Color getColor(String id) {
        return UIManager.getColor(id);
    }

    public Color putColor(String id, Color color) {
        Color oldColor = (Color) UIManager.put(id, color);

        List<String> colors = (List<String>) UIManager.get("mydoggy.colors");
        if (colors == null) {
            colors = new ArrayList<String>();
            UIManager.put("mydoggy.colors", colors);
        }
        colors.add(id);

        return oldColor;
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
        if (UIManager.get("mydoggy.resourceBundle") != null) {
            this.resourceBundle = (ResourceBundle) UIManager.get("mydoggy.resourceBundle");
            this.bundlePath = (String) UIManager.get("mydoggy.bundlePath");
        } else {
            this.resourceBundle = loadResourceBundle(locale, bundlePath);
            UIManager.put("mydoggy.resourceBundle", resourceBundle);
            UIManager.put("mydoggy.bundlePath", bundlePath);
        }
    }

    public void setUserBundle(Locale locale, String bundle, ClassLoader classLoader) {
        this.userResourceBundle = loadResourceBundle(locale, bundle, classLoader);
        UIManager.put("mydoggy.resourceBundle.user", resourceBundle);
    }

    public void setUserBundle(ResourceBundle userBundle) {
        if (userBundle == null)
            this.userResourceBundle = new DummyResourceBundle();
        else
            this.userResourceBundle = userBundle;
        UIManager.put("mydoggy.resourceBundle.user", userResourceBundle);
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


    public java.util.List<String> getColors() {
        return (List<String>) UIManager.get("mydoggy.colors");
    }

    public java.util.List<String> getIcons() {
        return (List<String>) UIManager.get("mydoggy.icons");
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
        // Check for the flag
        if (isAlreadyLoaded())
            return;

        // Load from file
        resources = SwingUtil.loadPropertiesFile(resourceName, classLoader);

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
        } else if ("Boolean".equals(prefix)) {
            loadBoolean(name, value);
        }
    }

    protected void loadIcon(String name, String url) {
        putIcon(name, new ImageIcon(Toolkit.getDefaultToolkit().getImage(getUrl(url))));
    }

    protected void loadImage(String name, String url) {
        try {
            putImage(name, ImageIO.read(getUrl(url)));
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
        this.bundlePath = bundlePath;
    }

    protected void loadObjects() {
        putObject(FindFocusableQuestion.class, new FindFocusableQuestion());
    }

    protected void loadBoolean(String name, String value) {
        try {
            putBoolean(name, Boolean.parseBoolean(value));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    protected boolean isAlreadyLoaded() {
        return UIManager.get(ResourceManager.class) != null;
    }

    protected URL getUrl(String url) {
        URL result = null;

        if (classLoader != null)
            result = classLoader.getResource(url);

        if (result == null)
            result = this.getClass().getClassLoader().getResource(url);

        if (result == null)
            result = Thread.currentThread().getContextClassLoader().getResource(url);

        if (result == null)
            result = ClassLoader.getSystemClassLoader().getResource(url);

        return result;
    }


    protected void initComponentCreators() {
        cmpCreators = new Hashtable<String, ObjectCreator<Component>>();
        cmpUiCreators = new Hashtable<String, ObjectCreator<ComponentUI>>();

        cmpCustomizers = new Hashtable<String, ObjectCustomizer<Component>>();

        instanceCreators = new Hashtable<Class, ObjectCreator>();
        instanceCreators.put(ParentOfQuestion.class, new ParentOfQuestionInstanceCreator());
    }

    protected ResourceBundle loadResourceBundle(Locale locale, String bundle) {
        ResourceBundle result = loadResourceBundle(locale, bundle, classLoader);
        if (result instanceof DummyResourceBundle)
            result = loadResourceBundle(locale, bundle, this.getClass().getClassLoader());

        if (result instanceof DummyResourceBundle)
            result = loadResourceBundle(locale, bundle, Thread.currentThread().getContextClassLoader());

        if (result instanceof DummyResourceBundle)
            result = loadResourceBundle(locale, bundle, ClassLoader.getSystemClassLoader());

        return result;
    }

    protected ResourceBundle loadResourceBundle(Locale locale, String bundle, ClassLoader classLoader) {
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
        if (isAlreadyLoaded())
            return;

        setTransparencyManager(new WindowTransparencyManager());
    }


    public static class ParentOfQuestionInstanceCreator implements ObjectCreator {

        public Object create(Context context) {
            return new ParentOfQuestion(context.get(Component.class));
        }

    }

}