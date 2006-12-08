package org.noos.xing.mydoggy.plaf.ui.content.desktop;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentDesktopManager implements DesktopManager, java.io.Serializable {
    final static String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";

    final static int DEFAULT_DRAG_MODE = 0;
    final static int OUTLINE_DRAG_MODE = 1;

    int dragMode = DEFAULT_DRAG_MODE;

    private transient Rectangle currentBounds = null;
    private transient Point currentLoc = null;

    
    public void openFrame(JInternalFrame f) {
        if (f.getDesktopIcon().getParent() != null) {
            f.getDesktopIcon().getParent().add(f);
            removeIconFor(f);
        }
    }

    public void closeFrame(JInternalFrame f) {
        boolean findNext = f.isSelected();
        Container c = f.getParent();
        if (findNext)
            try {
                f.setSelected(false);
            } catch (PropertyVetoException e2) {
            }
        if (c != null) {
            c.remove(f);
            c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        }
        removeIconFor(f);
        if (f.getNormalBounds() != null)
            f.setNormalBounds(null);
        if (wasIcon(f))
            setWasIcon(f, null);
        if (findNext) activateNextFrame(c);
    }

    public void maximizeFrame(JInternalFrame f) {
        if (f.isIcon()) {
            try {
                // In turn calls deiconifyFrame in the desktop manager.
                // That method will handle the maximization of the frame.
                f.setIcon(false);
            } catch (PropertyVetoException e2) {
            }
        } else {
            f.setNormalBounds(f.getBounds());
            Rectangle desktopBounds = f.getParent().getBounds();
            setBoundsForFrame(f, 0, 0,
                              desktopBounds.width, desktopBounds.height);
        }

        // Set the maximized frame as selected.
        try {
            f.setSelected(true);
        } catch (PropertyVetoException e2) {
        }
    }

    public void minimizeFrame(JInternalFrame f) {
        // If the frame was an icon restore it back to an icon.
        if (f.isIcon()) {
            iconifyFrame(f);
            return;
        }

        if ((f.getNormalBounds()) != null) {
            Rectangle r = f.getNormalBounds();
            f.setNormalBounds(null);
            try {
                f.setSelected(true);
            } catch (PropertyVetoException e2) {
            }
            setBoundsForFrame(f, r.x, r.y, r.width, r.height);
        }
    }

    public void iconifyFrame(JInternalFrame f) {
        JInternalFrame.JDesktopIcon desktopIcon;
        Container c = f.getParent();
        JDesktopPane d = f.getDesktopPane();
        boolean findNext = f.isSelected();

        desktopIcon = f.getDesktopIcon();
        if (!wasIcon(f)) {
            Rectangle r = getBoundsForIconOf(f);
            desktopIcon.setBounds(r.x, r.y, r.width, r.height);
            setWasIcon(f, Boolean.TRUE);
        }

        if (c == null) {
            return;
        }

        if (c instanceof JLayeredPane) {
            JLayeredPane lp = (JLayeredPane) c;
            int layer = lp.getLayer(f);
            lp.putLayer(desktopIcon, layer);
        }

        // If we are maximized we already have the normal bounds recorded
        // don't try to re-record them, otherwise we incorrectly set the
        // normal bounds to maximized state.
        if (!f.isMaximum()) {
            f.setNormalBounds(f.getBounds());
        }
        c.remove(f);
        c.add(desktopIcon);
        c.repaint(f.getX(), f.getY(), f.getWidth(), f.getHeight());
        try {
            f.setSelected(false);
        } catch (PropertyVetoException e2) {
        }

        // Get topmost of the remaining frames
        if (findNext) {
            activateNextFrame(c);
        }
    }

    public void deiconifyFrame(JInternalFrame f) {
        JInternalFrame.JDesktopIcon desktopIcon = f.getDesktopIcon();
        Container c = desktopIcon.getParent();
        if (c != null) {
            c.add(f);
            // If the frame is to be restored to a maximized state make
            // sure it still fills the whole desktop.
            if (f.isMaximum()) {
                Rectangle desktopBounds = c.getBounds();
                if (f.getWidth() != desktopBounds.width ||
                    f.getHeight() != desktopBounds.height) {
                    setBoundsForFrame(f, 0, 0,
                                      desktopBounds.width, desktopBounds.height);
                }
            }
            removeIconFor(f);
            if (f.isSelected()) {
                f.moveToFront();
            } else {
                try {
                    f.setSelected(true);
                } catch (PropertyVetoException e2) {
                }
            }
        }
    }

    public void activateFrame(JInternalFrame f) {
        Container p = f.getParent();
        Component[] c;
        JDesktopPane d = f.getDesktopPane();
        JInternalFrame currentlyActiveFrame =
                (d == null) ? null : d.getSelectedFrame();
        // fix for bug: 4162443
        if (p == null) {
            // If the frame is not in parent, its icon maybe, check it
            p = f.getDesktopIcon().getParent();
            if (p == null)
                return;
        }
        // we only need to keep track of the currentActive InternalFrame, if any
        if (currentlyActiveFrame == null) {
            if (d != null) {
                d.setSelectedFrame(f);
            }
        } else if (currentlyActiveFrame != f) {
            // if not the same frame as the current active
            // we deactivate the current
            if (currentlyActiveFrame.isSelected()) {
                try {
                    currentlyActiveFrame.setSelected(false);
                }
                catch (PropertyVetoException e2) {
                }
            }
            if (d != null) {
                d.setSelectedFrame(f);
            }
        }
        f.moveToFront();
    }

    public void deactivateFrame(JInternalFrame f) {
        JDesktopPane d = f.getDesktopPane();
        JInternalFrame currentlyActiveFrame =
                (d == null) ? null : d.getSelectedFrame();
        if (currentlyActiveFrame == f)
            d.setSelectedFrame(null);
    }

    public void beginDraggingFrame(JComponent f) {
        setupDragMode(f);
    }

    public void dragFrame(JComponent f, int newX, int newY) {
        if (dragMode == OUTLINE_DRAG_MODE) {
            JDesktopPane desktopPane = getDesktopPane(f);
            if (desktopPane != null) {
                Graphics g = desktopPane.getGraphics();

                g.setXORMode(Color.white);
                if (currentLoc != null) {
                    g.drawRect(currentLoc.x, currentLoc.y,
                               f.getWidth() - 1, f.getHeight() - 1);
                }
                g.drawRect(newX, newY, f.getWidth() - 1, f.getHeight() - 1);
                currentLoc = new Point(newX, newY);
                g.dispose();
            }
        } else {
            setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
        }
    }

    public void endDraggingFrame(JComponent f) {
        if (dragMode == OUTLINE_DRAG_MODE && currentLoc != null) {
            setBoundsForFrame(f, currentLoc.x, currentLoc.y, f.getWidth(), f.getHeight());
            currentLoc = null;
        }
    }

    public void beginResizingFrame(JComponent f, int direction) {
        setupDragMode(f);
    }

    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        if (dragMode == DEFAULT_DRAG_MODE) {
            setBoundsForFrame(f, newX, newY, newWidth, newHeight);
        } else {
            JDesktopPane desktopPane = getDesktopPane(f);
            if (desktopPane != null) {
                Graphics g = desktopPane.getGraphics();

                g.setXORMode(Color.white);
                if (currentBounds != null) {
                    g.drawRect(currentBounds.x, currentBounds.y, currentBounds.width - 1, currentBounds.height - 1);
                }
                g.drawRect(newX, newY, newWidth - 1, newHeight - 1);
                currentBounds = new Rectangle(newX, newY, newWidth, newHeight);
                g.setPaintMode();
                g.dispose();
            }
        }

    }

    public void endResizingFrame(JComponent f) {
        if (dragMode == OUTLINE_DRAG_MODE && currentBounds != null) {
            setBoundsForFrame(f, currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height);
            currentBounds = null;
        }
    }

    public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
        boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
        f.setBounds(newX, newY, newWidth, newHeight);
        if (didResize) {
            f.validate();
        }
    }


    protected void activateNextFrame(Container c) {
        int i;
        JInternalFrame nextFrame = null;
        if (c == null) return;
        for (i = 0; i < c.getComponentCount(); i++) {
            if (c.getComponent(i) instanceof JInternalFrame) {
                nextFrame = (JInternalFrame) c.getComponent(i);
                break;
            }
        }
        if (nextFrame != null) {
            try {
                nextFrame.setSelected(true);
            }
            catch (PropertyVetoException e2) {
            }
            nextFrame.moveToFront();
        }

    }

    protected void setupDragMode(JComponent f) {
        JDesktopPane p = getDesktopPane(f);
        if (p != null) {
            String mode = (String) p.getClientProperty("JDesktopPane.dragMode");
            if (mode != null && mode.equals("outline")) {
                dragMode = OUTLINE_DRAG_MODE;
            } else if (mode != null && mode.equals("faster") && f instanceof JInternalFrame && f.isOpaque()) {
                dragMode = DEFAULT_DRAG_MODE;
            } else {
                if (p.getDragMode() == JDesktopPane.OUTLINE_DRAG_MODE) {
                    dragMode = OUTLINE_DRAG_MODE;
                } else if (p.getDragMode() == JDesktopPane.LIVE_DRAG_MODE
                           && f instanceof JInternalFrame
                           && ((JInternalFrame) f).isOpaque()) {
                    dragMode = DEFAULT_DRAG_MODE;
                } else {
                    dragMode = DEFAULT_DRAG_MODE;
                }
            }
        }
    }

    protected void removeIconFor(JInternalFrame f) {
        JInternalFrame.JDesktopIcon di = f.getDesktopIcon();
        Container c = di.getParent();
        if (c != null) {
            c.remove(di);
            c.repaint(di.getX(), di.getY(), di.getWidth(), di.getHeight());
        }
    }

    protected Rectangle getBoundsForIconOf(JInternalFrame f) {
        //
        // Get the icon for this internal frame and its preferred size
        //

        JInternalFrame.JDesktopIcon icon = f.getDesktopIcon();
        Dimension prefSize = icon.getPreferredSize();
        //
        // Get the parent bounds and child components.
        //

        Container c = f.getParent();
        if (c == null) {
            c = f.getDesktopIcon().getParent();
        }

        if (c == null) {
            /* the frame has not yet been added to the parent; how about (0,0) ?*/
            return new Rectangle(0, 0, prefSize.width, prefSize.height);
        }

        Rectangle parentBounds = c.getBounds();
        Component[] components = c.getComponents();

        //
        // Iterate through valid default icon locations and return the
        // first one that does not intersect any other icons.
        //

        Rectangle availableRectangle = null;
        JInternalFrame.JDesktopIcon currentIcon = null;

        int x = 0;
        int y = parentBounds.height - prefSize.height;
        int w = prefSize.width;
        int h = prefSize.height;

        boolean found = false;

        while (!found) {

            availableRectangle = new Rectangle(x, y, w, h);

            found = true;

            for (int i = 0; i < components.length; i++) {

                //
                // Get the icon for this component
                //

                if (components[i] instanceof JInternalFrame) {
                    currentIcon = ((JInternalFrame) components[i]).getDesktopIcon();
                } else if (components[i] instanceof JInternalFrame.JDesktopIcon) {
                    currentIcon = (JInternalFrame.JDesktopIcon) components[i];
                } else
                    /* found a child that's neither an internal frame nor
                an icon. I don't believe this should happen, but at
                present it does and causes a null pointer exception.
                Even when that gets fixed, this code protects against
                the npe. hania */
                    continue;

                //
                // If this icon intersects the current location, get next location.
                //

                if (!currentIcon.equals(icon)) {
                    if (availableRectangle.intersects(currentIcon.getBounds())) {
                        found = false;
                        break;
                    }
                }
            }

            if (currentIcon == null)
                /* didn't find any useful children above. This probably shouldn't
           happen, but this check protects against an npe if it ever does
           (and it's happening now) */
                return availableRectangle;

            x += currentIcon.getBounds().width;

            if (x + w > parentBounds.width) {
                x = 0;
                y -= h;
            }
        }

        return (availableRectangle);
    }

    protected void setWasIcon(JInternalFrame f, Boolean value) {
        if (value != null) {
            f.putClientProperty(HAS_BEEN_ICONIFIED_PROPERTY, value);
        }
    }

    protected boolean wasIcon(JInternalFrame f) {
        return (f.getClientProperty(HAS_BEEN_ICONIFIED_PROPERTY) == Boolean.TRUE);
    }


    JDesktopPane getDesktopPane(JComponent frame) {
        JDesktopPane pane = null;
        Component c = frame.getParent();

        // Find the JDesktopPane
        while (pane == null) {
            if (c instanceof JDesktopPane) {
                pane = (JDesktopPane) c;
            } else if (c == null) {
                break;
            } else {
                c = c.getParent();
            }
        }

        return pane;
    }


}
