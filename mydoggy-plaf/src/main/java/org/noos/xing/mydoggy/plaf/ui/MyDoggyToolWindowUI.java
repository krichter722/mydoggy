package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

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
    private static final String resourceName = "mydoggyplaf.properties";

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

    public void updateAnchor(ToolWindowDescriptor descriptor,
                             Graphics g, JComponent c,
                             Color backgroundStart, Color backgroundEnd,
                             boolean active, boolean flashing) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        if (flashing || active) {
            GraphicsUtil.fillRect(g,
                                  r,
                                  backgroundStart,
                                  backgroundEnd,
                                  null,
                                  GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        } else {
            g.setColor(getColor(ANCHOR_BACKGROUND_INACTIVE));
            g.fillRect(0, 0, r.width, r.height);
        }
    }

    public void updateToolWindowAppBar(ToolWindowDescriptor descriptor,
                                       Graphics g, JComponent c,
                                       Color backgroundStart, Color backgroundEnd,
                                       Color idBackgroundColor, Color idColor) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        GraphicsUtil.fillRect(g, r,
                              backgroundStart, backgroundEnd,
                              null,
                              GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

        if (descriptor.getDockedTypeDescriptor().isIdVisibleOnToolBar()) {
            String id = ResourceBundleManager.getInstance().getUserString(descriptor.getToolWindow().getId());
            r.width = g.getFontMetrics().stringWidth(id) + 8;

            // TODO: add customization
            int halfHeigh = (r.height / 2);
            GraphicsUtil.fillRect(g, r, Color.WHITE, idBackgroundColor,
                                  new Polygon(new int[]{r.x, r.x + r.width - halfHeigh, r.x + r.width - halfHeigh, r.x},
                                              new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                              4),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            GraphicsUtil.fillRect(g, r, Color.WHITE, idBackgroundColor,
                                  new Arc2D.Double(r.x + r.width - r.height,
                                                   r.y, r.height, r.height, -90.0d, 180.0d, Arc2D.CHORD),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            g.setColor(idColor);
            g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
        }
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
