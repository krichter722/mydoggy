package org.noos.xing.mydoggy.plaf.ui.util;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

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

    public static void requestFocus(final Component component) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
//                                System.out.println("IN RUN - requestFocus for : " + component.isFocusable() + " - " + component);
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

    public static void repaintNow(Component component) {
        component.invalidate();
        component.validate();
        component.repaint();
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

    public static Icon loadIcon(String url) {
		try {
			return new ImageIcon(Toolkit.getDefaultToolkit().getImage(SwingUtil.class.getClassLoader().getResource(url)));
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
		if (cmp.isFocusable())
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

}
