package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowUI;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.ResourceBundleManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyToolWindowUI implements ToolWindowUI {
    private static final String resourceName = "toolwindowui.properties";

    private static final MyDoggyToolWindowUI INSTANCE = new MyDoggyToolWindowUI();

    public static MyDoggyToolWindowUI getInstance() {
        return INSTANCE;
    }

    protected Properties properties;
    protected Map<String, Icon> icons;
    protected Map<String, Color> colors;

    public MyDoggyToolWindowUI() {
        this.icons = new Hashtable<String, Icon>();
        this.colors = new Hashtable<String, Color>();

        loadResources();
    }

    public Icon getIcon(String id) {
        return icons.get(id);
    }

    public Color getColor(String id) {
        return colors.get(id);
    }

    protected void loadResources() {
        properties = loadPropertiesFile();
        
        loadIcons();
        loadColors();
    }

    protected Properties loadPropertiesFile() {
        InputStream is = null;
        try {
            URL resource = this.getClass().getClassLoader().getResource("META-INF" + File.separator + resourceName);
            if (resource == null) {
                File file = new File(resourceName);
                if (file.exists())
                    resource = file.toURL();
                else {
                    file = new File(System.getProperty("user.home") + File.separator + resourceName);
                    if (file.exists())
                        resource = file.toURL();
                    else
                        throw new RuntimeException("Cannot find resource property file.");
                }
            }

            is = resource.openStream();
            Properties properties = new Properties();
            properties.load(is);
            
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Cannot load resource property file.", e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {}
        }
    }

    protected void loadIcons() {
        String prefix = "ToolWindowUI.Icon.";

        for (Object key : properties.keySet()) {
            String strKey = key.toString();
            if (strKey.startsWith(prefix)) {
                String iconKey = strKey.substring(prefix.length());
                String iconUrl = properties.getProperty(strKey);

                icons.put(iconKey, loadIcon(iconUrl));
            }
        }
    }

    protected void loadColors() {
        String prefix = "ToolWindowUI.Color.";

        for (Object key : properties.keySet()) {
            String strKey = key.toString();
            if (strKey.startsWith(prefix)) {
                String colorKey = strKey.substring(prefix.length());
                String colorDef = properties.getProperty(strKey);

                colors.put(colorKey, loadColor(colorDef));
            }
        }
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

}