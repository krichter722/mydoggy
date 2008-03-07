package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.xing.mydoggy.plaf.ui.drag.DragGesture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Angelo De Caro
 */
public class SwingUtil {
    private static Map<Window, Rectangle> fullScreenBounds = new Hashtable<Window, Rectangle>();

    private SwingUtil() {
    }

    public static void addKeyActionMapping(JComponent component, KeyStroke keyStroke, Object actionMapKey, Action action) {
        addKeyActionMapping(JComponent.WHEN_FOCUSED, component, keyStroke, actionMapKey, action);
    }

    public static void addKeyActionMapping(int condition, JComponent component, KeyStroke keyStroke, Object actionMapKey, Action action) {
        component.getInputMap(condition).put(keyStroke, actionMapKey);
        component.getActionMap().put(actionMapKey, action);
    }

    public static void centrePositionOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();
        window.setLocation(screenSize.width - frameSize.width >> 1, screenSize.height - frameSize.height >> 1);
    }

    public static void requestFocus(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
//                                System.out.println("IN RUN - requestFocus for : " + component.isFocusable() + " - " + component);
//                System.out.println("false = " + SwingUtilities.getWindowAncestor(component).isFocusableWindow());
//                System.out.println("false = " + SwingUtilities.getWindowAncestor(component).getBounds());
//                System.out.println("false = " + SwingUtilities.getWindowAncestor(component));
//                System.out.println("Focus result : " + component.requestFocusInWindow());

                Window ancestor = SwingUtilities.getWindowAncestor(component);
                if (ancestor != null && ancestor.isFocused())
                    component.requestFocusInWindow();
                else
                    component.requestFocus();
            }
        });
    }

    public static void repaint(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                component.invalidate();
                component.validate();
                component.repaint();
            }
        });
    }

    public static void repaint(final Component component, final Runnable runnable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                component.invalidate();
                component.validate();
                component.repaint();
                SwingUtilities.invokeLater(runnable);
            }
        });
    }

    public static void revalidate(final JComponent component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                component.revalidate();
            }
        });
    }

    public static void repaintNow(Component component) {
        component.invalidate();
        component.validate();
        component.repaint();
    }

    public static void registerDragGesture(Component c, DragGesture dragGesture) {
        DragSource dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_MOVE, dragGesture);
        dragSource.addDragSourceMotionListener(dragGesture);
    }

    public static boolean isLeftToRight(Component c) {
        return c != null && c.getComponentOrientation().isLeftToRight();
    }

    public static void invokeAndWait(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void dispatchEvent(Object src, AWTEvent event) {
        if (src instanceof Component) {
            ((Component) src).dispatchEvent(event);
        } else if (src instanceof MenuComponent) {
            ((MenuComponent) src).dispatchEvent(event);
        }
    }

    public static Object getDeepestObjectAt(Object parent, int x, int y) {
        if (parent != null && parent instanceof Container) {
            // use a copy of 1.3 Container.findComponentAt
            Component child = findComponentAt((Container) parent, x, y);
            if (child != null && child != parent) {
                if (child instanceof JRootPane) {
                    JLayeredPane lp = ((JRootPane) child).getLayeredPane();
                    Rectangle b = lp.getBounds();
                    child = (Component) getDeepestObjectAt(lp, x - b.x, y - b.y);
                    if (child != null) {
                        return child;
                    }
                } else {
                    return child;
                }
            }
        }
        // if the parent is not a Container then it might be a MenuItem.
        // But even if it isn't a MenuItem just return the parent because
        // that's a close as we can come.
        return parent;
    }

    public static Component findComponentAt(Container cont, int x, int y) {
        synchronized (cont.getTreeLock()) {
            return findComponentAt(cont, x, y, true, false);
        }
    }

    public static Component findComponentAt(Container cont, int x, int y, boolean ignoreEnabled, boolean ignoreGlassPane) {
        if (!((cont instanceof CellRendererPane) || (cont.getParent() instanceof CellRendererPane)) &&

            !(cont.contains(x, y) /*&& cont.isVisible() */ && (ignoreEnabled || cont.isEnabled()))) {
            return null;
        }
        int ncomponents = cont.getComponentCount();
        Component component[] = cont.getComponents();

        Component glassPane = null;
        if (ignoreGlassPane && (cont instanceof JRootPane)) {
            glassPane = ((JRootPane) cont).getGlassPane();
        }

        // Two passes: see comment in sun.awt.SunGraphicsCallback
        for (int i = 0; i < ncomponents; i++) {
            Component comp = component[i];
            if (comp != null && comp != glassPane && !comp.isLightweight()) {
                Point point = comp.getLocation();
                if (comp instanceof Container) {
                    comp = findComponentAt((Container) comp,
                                           x - point.x,
                                           y - point.y,
                                           ignoreEnabled,
                                           ignoreGlassPane);
                } else {
                    comp = comp.getComponentAt(x - point.x, y - point.y);
                }
                if (comp != null && comp.isVisible() &&
                    (ignoreEnabled || comp.isEnabled())) {
                    return comp;
                }
            }
        }
        for (int i = 0; i < ncomponents; i++) {
            Component comp = component[i];
            if (comp != null && comp != glassPane && comp.isLightweight()) {
                Point point = comp.getLocation();
                if (comp instanceof Container) {
                    comp = findComponentAt((Container) comp,
                                           x - point.x,
                                           y - point.y,
                                           ignoreEnabled,
                                           ignoreGlassPane);
                } else {
                    comp = comp.getComponentAt(x - point.x, y - point.y);
                }
                if (comp != null && comp.isVisible() &&
                    (ignoreEnabled || comp.isEnabled())) {
                    return comp;
                }
            }
        }
        return cont;
    }

    public static Container getLastParent(Component component) {
        for (Container p = component.getParent(); p != null; p = p.getParent()) {
            if (p.getParent() == null)
                return p;
        }
        return null;
    }

    public static boolean hasParent(Component component, Container parent) {
        for (Container p = component.getParent(); p != null; p = p.getParent()) {
            if (p == parent)
                return true;
        }
        return false;
    }

    public static Icon loadIcon(String urlDef) {
        try {
            URL url = SwingUtil.class.getClassLoader().getResource(urlDef);
            if (url == null)
                throw new IllegalArgumentException("Invalid URL : " + urlDef);

            return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image loadImage(String url) {
        try {
            return Toolkit.getDefaultToolkit().getImage(SwingUtil.class.getClassLoader().getResource(url));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage loadImageIO(String url) {
        try {
            return ImageIO.read(SwingUtil.class.getClassLoader().getResource(url));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Component findFocusable(Component cmp) {
        if (cmp.isFocusable() && !(cmp instanceof JPanel) && !(cmp instanceof JLabel) && !(cmp instanceof JScrollPane)
            && !(cmp instanceof JViewport))
            return cmp;

        if (cmp instanceof Container) {
            Container container = (Container) cmp;
            for (int i = 0, size = container.getComponentCount(); i < size; i++) {
                Component finded = findFocusable(container.getComponent(i));
                if (finded != null)
                    return finded;
            }
        }
        return null;
    }

    public static Vector<Window> getTopContainers(String name) {
        Vector<Window> containers = new Vector<Window>();

        Frame frames[] = Frame.getFrames();
        for (Frame frame : frames) {
            Window[] windows = frame.getOwnedWindows();

            for (Window window : windows) {
                if (window.getName() != null && window.getName().equals(name))
                    containers.add(window);
            }

            if (!containers.contains(frame)) {
                containers.add(frame);
            }
        }
        return containers;
    }

    public static Component getParent(Component c, String parentName) {
        if (c == null || parentName == null)
            return null;

        if (c.getName() != null && c.getName().startsWith(parentName))
            return c;

        for (; c != null; c = c.getParent()) {
            if (c.getName() != null && c.getName().startsWith(parentName))
                return c;
        }
        return null;
    }

    public static int getIconWidth(Icon icon) {
        return (icon != null) ? icon.getIconWidth() : 0;
    }

    public static int getIconHeight(Icon icon) {
        return (icon != null) ? icon.getIconHeight() : 0;
    }


    public static Rectangle getVirtualScreenBounds() {
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();

        for (GraphicsDevice gd : gs) {
            GraphicsConfiguration[] gc = gd.getConfigurations();
            for (GraphicsConfiguration aGc : gc) {
                virtualBounds = virtualBounds.union(aGc.getBounds());
            }
        }
        return virtualBounds;
    }

    public static Rectangle validateBounds(Rectangle bounds, Rectangle referenceBounds) {
        if (bounds == null)
            return null;

        if (bounds.x < referenceBounds.x)
            bounds.x = referenceBounds.x;

        if (bounds.x > referenceBounds.getMaxX())
            bounds.x = (int) referenceBounds.getMaxX() - bounds.width;

        if (bounds.y < referenceBounds.y)
            bounds.y = referenceBounds.y;

        if (bounds.y > referenceBounds.getMaxY())
            bounds.y = (int) referenceBounds.getMaxY() - bounds.height;

        return bounds;
    }

    public static Rectangle validateBounds(Rectangle bounds) {
        return validateBounds(bounds, getVirtualScreenBounds());
    }

    public static void validateBounds(Component component) {
        component.setBounds(validateBounds(component.getBounds(), getVirtualScreenBounds()));
    }

    public static void validateBounds(Component component, Rectangle referenceBounds) {
        component.setBounds(validateBounds(component.getBounds(), referenceBounds));
    }

    public static void setFullScreen(Window window) {
        GraphicsDevice[] gda = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        if (gda.length > 1) {
            Rectangle targetBounds = window.getBounds();
            fullScreenBounds.put(window, targetBounds);

            Point location = window.getLocationOnScreen();

            for (final GraphicsDevice graphicsDevice : gda) {
                final Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();

                if (graphicsDevice.getType() == GraphicsDevice.TYPE_RASTER_SCREEN && bounds.contains(location)) {
                    targetBounds = bounds;
                    break;
                }
            }

            window.setBounds(targetBounds);
        } else {
            GraphicsDevice graphicsDevice = window.getGraphicsConfiguration().getDevice();
//            if (graphicsDevice.isFullScreenSupported())
//                graphicsDevice.setFullScreenWindow(window);
//            else {
                Rectangle targetBounds = window.getBounds();
                fullScreenBounds.put(window, targetBounds);
                window.setBounds(graphicsDevice.getDefaultConfiguration().getBounds());
//            }
        }
    }

    public static void restoreFullScreenWindow(Window window) {
        Rectangle bounds = fullScreenBounds.remove(window);
        if (bounds != null) {
            window.setBounds(bounds);
        } else {
            GraphicsDevice graphicsDevice = window.getGraphicsConfiguration().getDevice();
            if (graphicsDevice.isFullScreenSupported())
                graphicsDevice.setFullScreenWindow(null);
        }
    }

    public static int findDisplayedMnemonicIndex(String text, int mnemonic) {
        if (text == null || mnemonic == '\0') {
            return -1;
        }

        char uc = Character.toUpperCase((char) mnemonic);
        char lc = Character.toLowerCase((char) mnemonic);

        int uci = text.indexOf(uc);
        int lci = text.indexOf(lc);

        if (uci == -1) {
            return lci;
        } else if (lci == -1) {
            return uci;
        } else {
            return (lci < uci) ? lci : uci;
        }
    }

    public static Properties loadPropertiesFile(String resourceName, ClassLoader classLoader) {
        InputStream is = null;
        try {
            if (classLoader == null)
                classLoader = SwingUtil.class.getClassLoader();

            URL resource = classLoader.getResource("META-INF/" + resourceName);
            if (resource == null) {
                File file = new File(resourceName);
                if (file.exists())
                    resource = file.toURL();
                else {
                    file = new File(System.getProperty("user.home") + File.separator + resourceName);
                    if (file.exists())
                        resource = file.toURL();
                    else
                        throw new RuntimeException("Cannot find resource property file called " + resourceName + ".");
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
                } catch (IOException e) {
                }
        }
    }

    public static Object newObject(String className) {
        try {
            return SwingUtil.class.getClassLoader().loadClass(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(PropertyChangeEvent event) {
        return event.getPropertyName() + ":\n\t" + event.getSource() + ":\n\t" + event.getOldValue() + ":\n\t" + event.getNewValue();
    }

    public static Point convertPointFromScreen(Point p, Component c) {
        Rectangle b;
        int x, y;

        do {
            if (c instanceof JComponent) {
                x = ((JComponent) c).getX();
                y = ((JComponent) c).getY();
            } else if (c instanceof java.applet.Applet ||
                       c instanceof java.awt.Window) {
                try {
                    Point pp = c.getLocationOnScreen();
                    x = pp.x;
                    y = pp.y;
                } catch (IllegalComponentStateException icse) {
                    x = c.getX();
                    y = c.getY();
                }
            } else {
                x = c.getX();
                y = c.getY();
            }

            p.x -= x;
            p.y -= y;

            if (c instanceof java.awt.Window || c instanceof java.applet.Applet)
                break;
            c = c.getParent();
        } while (c != null);
        return p;
    }

    public static <T> T getParent(Component c, Class<? extends T> parentClass) {
        if (c == null || parentClass == null)
            return null;

        if (parentClass.isInstance(c))
            return (T) c;

        for (; c != null; c = c.getParent()) {
            if (parentClass.isInstance(c))
                return (T) c;
        }
        return null;
    }

    public static Component findAndRequestFocus(Component component) {
        Container container;
        if (component instanceof JDialog) {
            container = ((JDialog)component).getContentPane();
        } else if (component instanceof Container)
            container = (Container) component;
        else
            return null;
        
        Component focusRequester = SwingUtil.findFocusable(container);
        if (focusRequester == null) {
            focusRequester = container;
        }
        SwingUtil.requestFocus(focusRequester);
        return focusRequester;
    }
}
