package org.noos.xing.mydoggy.plaf.ui.util;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.applet.Applet;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.LinkedList;
import java.util.Vector;

/**
 * @author Angelo De Caro
 */
public class SwingUtil {
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

    public static final int linearInterpolation(long v1, long v2, double d) {
        return (int) Math.round(v1 * (1.0 - d) + v2 * d);
    }

    public static void goToPath(JTree tree, TreePath treePath) {
        tree.setSelectionPath(treePath);
        tree.expandPath(tree.getSelectionPath());
        tree.scrollPathToVisible(tree.getSelectionPath());
    }

    public static TreePath getPathFromRoot(TreeNode node) {
        java.util.List list = new LinkedList();
        while (node != null) {
            list.add(0, node);
            node = node.getParent();
        }
        return new TreePath(list.toArray());
    }

    public static void requestFocus(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //                System.out.println("IN RUN - requestFocus for : " + component.isFocusable() + " - " + component);
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

    public static boolean isLeftToRight(Component c) {
        return c != null && c.getComponentOrientation().isLeftToRight();
    }

    public static final void invokeAndWait(Runnable runnable) {
        try {
            SwingUtilities.invokeAndWait(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static final void setText(final AbstractButton button, final String text, final int mnemonic) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                button.setMnemonic('\0');
                button.setText(text);
                button.setMnemonic(mnemonic);
            }
        });
    }

    public static final void setText(final JLabel label, final String text, final int mnemonic) {
        if (mnemonic != 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    label.setDisplayedMnemonic('\0');
                    label.setText(text);
                    label.setDisplayedMnemonic(mnemonic);
                }
            });
        } else {
            label.setDisplayedMnemonic('\0');
            label.setText(text);
            label.setDisplayedMnemonic(mnemonic);
        }
    }

    public static boolean containsEventListener(EventListener[] eventListeners, EventListener eventListener) {
        for (int i = 0; i < eventListeners.length; i++) {
            if (eventListeners[i] == eventListener)
                return true;
        }
        return false;
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

    public static Vector getTopContainers(Object source) {
        Vector containers = new Vector();
        Component topComponent;
        topComponent = getRoot(source);
        if (topComponent instanceof Applet) {
            try {
                Enumeration applets = ((Applet) topComponent).getAppletContext().getApplets();
                while (applets.hasMoreElements()) {
                    containers.add(applets.nextElement());
                }
            } catch (NullPointerException npe) {
                containers.add(topComponent);
            }
        }
        Frame frames[] = Frame.getFrames();
        for (int i = 0; i < frames.length; i++) {
            Window[] windows = frames[i].getOwnedWindows();
            for (int j = 0; j < windows.length; j++) {
                containers.add(windows[j]);
            }
            if (!containers.contains(frames[i])) {
                containers.add(frames[i]);
            }
        }
        return containers;
    }

    private static Component getRoot(Object comp) {
        Object parent = comp;
        while (parent != null) {
            comp = parent;
            if (comp instanceof MenuComponent) {
                parent = ((MenuComponent) comp).getParent();
            } else if (comp instanceof Component) {
                if (comp instanceof Window) {
                    break;
                }
                if (comp instanceof Applet) {
                    break;
                }
                parent = ((Component) comp).getParent();
            } else {
                break;
            }
        }
        if (comp instanceof Component) {
            return ((Component) comp);
        }
        return null;
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

    public static void goTo(JTextArea textArea, int pos) {
        textArea.moveCaretPosition(0);
    }

    public static Icon loadIcon(String url) {
        return new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                Thread.currentThread().getContextClassLoader().getResource(url))
        );
    }
}
