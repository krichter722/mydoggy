package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.DockableDropPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.DockableOwner;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiDockableOwner;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.datatransfer.Transferable;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class DockableDropPanelUI extends BasicPanelUI {

    public static ComponentUI createUI(JComponent c) {
        return new DockableDropPanelUI();
    }


    protected DockableDropPanel dockableDropPanel;
    protected TableLayout layout;
    protected String parentPrefix;
    protected int threshold;

    protected Point mouseLocation;
    protected boolean dragActive;

    protected Component onDockableContainer;
    protected ToolWindowAnchor onAnchor;
    protected Dockable onDockable;
    protected int onIndex;


    public void installUI(JComponent c) {
        // Init fields
        this.dockableDropPanel = (DockableDropPanel) c;

        super.installUI(c);

        installListeners(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        uninstallListeners(c);

        // Reset fields
        layout = null;
        dockableDropPanel = null;
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        dockableDropPanel.setLayout(layout = new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
        this.parentPrefix = dockableDropPanel.getParentPrefix();
        this.threshold = dockableDropPanel.getThreshold();
    }


    protected void installListeners(JComponent c) {
    }

    protected void uninstallListeners(JComponent c) {
    }


    public void update(Graphics g, JComponent c) {
        onDockable = null;
        onDockableContainer = null;
        onIndex = -1;

        if (dragActive && mouseLocation != null) {
            int xLimit = threshold;
            int yLimit = threshold;

            if (mouseLocation.x <= xLimit && mouseLocation.y > yLimit && mouseLocation.y < c.getHeight() - yLimit) {
                layout.setColumn(0, xLimit);
                layout.setColumn(2, 0);
                layout.setRow(0, 0);
                layout.setRow(2, 0);

                drawRect(g, 0, 0, 20, c.getHeight());

                onAnchor = ToolWindowAnchor.LEFT;
            } else
            if (mouseLocation.y <= yLimit && mouseLocation.x > xLimit && mouseLocation.x < c.getWidth() - xLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 0);
                layout.setRow(0, 20);
                layout.setRow(2, 0);

                drawRect(g, 0, 0, c.getWidth(), 20);

                onAnchor = ToolWindowAnchor.TOP;
            } else
            if (mouseLocation.x >= c.getWidth() - xLimit && mouseLocation.y > yLimit && mouseLocation.y < c.getHeight() - yLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 20);
                layout.setRow(0, 0);
                layout.setRow(2, 0);

                drawRect(g, c.getWidth() - 20, 0, 20, c.getHeight());

                onAnchor = ToolWindowAnchor.RIGHT;
            } else
            if (mouseLocation.y >= c.getHeight() - yLimit && mouseLocation.x > xLimit && mouseLocation.x < c.getWidth() - xLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 0);
                layout.setRow(0, 0);
                layout.setRow(2, 20);

                drawRect(g, 0, c.getHeight() - 20, c.getWidth(), 20);

                onAnchor = ToolWindowAnchor.BOTTOM;
            } else {
                resetLayout();

                // Check if the mouse is on a dockable container..
                Component deepestCmp = SwingUtilities.getDeepestComponentAt(c, mouseLocation.x, mouseLocation.y);
                if (deepestCmp != null) {
                    onDockableContainer = SwingUtil.getParent(deepestCmp, parentPrefix);

                    if (onDockableContainer != null) {
                        if (onDockableContainer instanceof DockableOwner) {
                            onDockable = ((DockableOwner) onDockableContainer).getDockable();
                        } else if (onDockableContainer instanceof MultiDockableOwner) {
                            MultiDockableOwner multiDockableOwner = (MultiDockableOwner) onDockableContainer;

                            onDockable = multiDockableOwner.getDockableAt(
                                    SwingUtilities.convertPoint(dockableDropPanel,
                                                                new Point(mouseLocation),
                                                                onDockableContainer));
                            onIndex = multiDockableOwner.getDockableIndex();
                        } else
                            throw new IllegalStateException("Dockable Container not reconized!!!");

                        // Ok the mouse is on a dockable container
                        Rectangle toolBounds = onDockableContainer.getBounds();
                        Point point = SwingUtilities.convertPoint(onDockableContainer,
                                                                  new Point(0, 0),
                                                                  c);
                        toolBounds.x = point.x;
                        toolBounds.y = point.y;

                        xLimit = toolBounds.width / 3;
                        yLimit = toolBounds.height / 3;

                        Point inToolLocation = SwingUtilities.convertPoint(c, mouseLocation, onDockableContainer);

                        if (inToolLocation.x <= xLimit && inToolLocation.y > yLimit && inToolLocation.y < toolBounds.height - yLimit) {

                            drawRect(g,
                                     toolBounds.x,
                                     toolBounds.y,
                                     xLimit,
                                     toolBounds.height);

                            onAnchor = ToolWindowAnchor.LEFT;
                        } else
                        if (inToolLocation.y <= yLimit && inToolLocation.x > xLimit && inToolLocation.x < toolBounds.width - xLimit) {

                            drawRect(g,
                                     toolBounds.x,
                                     toolBounds.y,
                                     toolBounds.width,
                                     yLimit);

                            onAnchor = ToolWindowAnchor.TOP;
                        } else
                        if (inToolLocation.x >= toolBounds.width - xLimit && inToolLocation.y > yLimit && inToolLocation.y < toolBounds.height - yLimit) {

                            drawRect(g,
                                     toolBounds.x + toolBounds.width - xLimit,
                                     toolBounds.y,
                                     xLimit,
                                     toolBounds.height);

                            onAnchor = ToolWindowAnchor.RIGHT;
                        } else
                        if (inToolLocation.y >= toolBounds.height - yLimit && inToolLocation.x > xLimit && inToolLocation.x < toolBounds.width - xLimit) {
                            layout.setColumn(0, 0);

                            drawRect(g,
                                     toolBounds.x,
                                     toolBounds.y + toolBounds.height - yLimit,
                                     toolBounds.width,
                                     yLimit);

                            onAnchor = ToolWindowAnchor.BOTTOM;
                        } else {
                            drawRect(g,
                                     toolBounds.x, toolBounds.y,
                                     toolBounds.width,
                                     toolBounds.height);
                            onAnchor = null;
                        }
                    }
                }
            }
        }
    }


    public Component getComponent() {
        return (dockableDropPanel.getComponentCount() == 0) ? null : dockableDropPanel.getComponent(0);
    }

    public void setComponent(Component component) {
        dockableDropPanel.removeAll();
        dockableDropPanel.add(component, "1,1,FULL,FULL");
    }

    public void resetComponent() {
        dockableDropPanel.removeAll();
        resetLayout();
    }


    public void dragExit() {
        mouseLocation = null;
        resetLayout();

        SwingUtil.repaint(dockableDropPanel);
    }

    public boolean dragStart(Transferable transferable) {
        dragActive = false;

        for (Class<? extends Dockable> clazz : dockableDropPanel.getTargets()) {
            if (clazz.isAssignableFrom(ToolWindow.class)) {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_ID_DF)) {
                    dragActive = true;
                    break;
                }
            } else if (clazz.isAssignableFrom(ToolWindowTab.class)) {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF)) {
                    dragActive = true;
                    break;
                }
            } else if (clazz.isAssignableFrom(Content.class)) {
                if (transferable.isDataFlavorSupported(MyDoggyTransferable.CONTENT_ID_DF)) {
                    dragActive = true;
                    break;
                }
            }
        }

        if (dragActive)
            SwingUtil.repaint(dockableDropPanel);

        return dragActive;
    }

    public void dragOver(Point location) {
        mouseLocation = location;

        SwingUtil.repaint(dockableDropPanel);
    }

    public void dragEnd() {
        mouseLocation = null;
        dragActive = false;
        resetLayout();

        SwingUtil.repaint(dockableDropPanel);
    }


    public Component getOnDockableContainer() {
        return onDockableContainer;
    }

    public Dockable getOnDockable() {
        return onDockable;
    }

    public ToolWindowAnchor getOnAnchor() {
        return onAnchor;
    }

    public int getOnIndex() {
        return onIndex;
    }


    protected void drawRect(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.BLUE);

        GraphicsUtil.fillRoundRect(g, x, y, width, height, 3, 10, 10, 0.25f);
    }

    protected void putProperty(JComponent c, String name) {
        Boolean value = (Boolean) c.getClientProperty(name);
        if (value != null)
            c.putClientProperty(name, !value);
        else
            c.putClientProperty(name, false);
    }

    protected void resetLayout() {
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);
    }

}
