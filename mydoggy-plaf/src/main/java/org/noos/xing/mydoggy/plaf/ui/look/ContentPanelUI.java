package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentPanelUI extends BasicPanelUI implements PropertyChangeListener {


    public static ComponentUI createUI(JComponent c) {
        return new ContentPanelUI();
    }


    protected ContentPanel contentPanel;
    protected TableLayout layout;
    protected String parentPrefix;
    protected int threshold;

    protected Point mouseLocation;
    protected boolean dragActive;


    public void installUI(JComponent c) {
        // Init fields
        this.contentPanel = (ContentPanel) c;

        super.installUI(c);

        installListeners(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        uninstallListeners(c);

        // Reset fields
        layout = null;
        contentPanel = null;
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        this.layout = (TableLayout) p.getLayout();
        this.parentPrefix = contentPanel.getParentPrefix();
        this.threshold = contentPanel.getThreshold();
    }


    protected void installListeners(JComponent c) {
        c.addPropertyChangeListener("dragStart", this);
        c.addPropertyChangeListener("dragOver", this);
        c.addPropertyChangeListener("dragExit", this);
        c.addPropertyChangeListener("dragEnd", this);
    }

    protected void uninstallListeners(JComponent c) {
        c.removePropertyChangeListener("dragStart", this);
        c.removePropertyChangeListener("dragOver", this);
        c.removePropertyChangeListener("dragExit", this);
        c.removePropertyChangeListener("dragEnd", this);
    }


    public void update(Graphics g, JComponent c) {
        if (dragActive && mouseLocation != null) {
            int xLimit = threshold;
            int yLimit = threshold;

            if (mouseLocation.x <= xLimit && mouseLocation.y > yLimit && mouseLocation.y < c.getHeight() - yLimit) {
                layout.setColumn(0, xLimit);
                layout.setColumn(2, 0);
                layout.setRow(0, 0);
                layout.setRow(2, 0);

                drawRect(g, 0, 0, 20, c.getHeight());

                putProperty(c, "dragToolWindow");
                c.putClientProperty("dragAnchor", ToolWindowAnchor.LEFT);
            } else
            if (mouseLocation.y <= yLimit && mouseLocation.x > xLimit && mouseLocation.x < c.getWidth() - xLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 0);
                layout.setRow(0, 20);
                layout.setRow(2, 0);

                drawRect(g, 0, 0, c.getWidth(), 20);

                putProperty(c, "dragToolWindow");
                c.putClientProperty("dragAnchor", ToolWindowAnchor.TOP);
            } else
            if (mouseLocation.x >= c.getWidth() - xLimit && mouseLocation.y > yLimit && mouseLocation.y < c.getHeight() - yLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 20);
                layout.setRow(0, 0);
                layout.setRow(2, 0);


                drawRect(g, c.getWidth() - 20, 0, 20, c.getHeight());

                putProperty(c, "dragToolWindow");
                c.putClientProperty("dragAnchor", ToolWindowAnchor.RIGHT);
            } else
            if (mouseLocation.y >= c.getHeight() - yLimit && mouseLocation.x > xLimit && mouseLocation.x < c.getWidth() - xLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 0);
                layout.setRow(0, 0);
                layout.setRow(2, 20);


                drawRect(g, 0, c.getHeight() - 20, c.getWidth(), 20);

                putProperty(c, "dragToolWindow");
                c.putClientProperty("dragAnchor", ToolWindowAnchor.BOTTOM);
            } else {
                contentPanel.resetLayout();

                // Check if the mouse is on a toolwindow
                Component deepestCmp = SwingUtilities.getDeepestComponentAt(c, mouseLocation.x, mouseLocation.y);
                if (deepestCmp != null) {
                    JComponent toolWindowContainer = (JComponent) SwingUtil.getParent(deepestCmp, parentPrefix);
                    if (toolWindowContainer != null) {

                        // Ok the mouse is on a toolwindow

                        Rectangle toolBounds = toolWindowContainer.getBounds();
                        Point point = SwingUtilities.convertPoint(toolWindowContainer,
                                                                  new Point(0, 0),
                                                                  c);
                        toolBounds.x = point.x;
                        toolBounds.y = point.y;

                        xLimit = toolBounds.width / 3;
                        yLimit = toolBounds.height / 3;

                        Point inToolLocation = SwingUtilities.convertPoint(c, mouseLocation, toolWindowContainer);

                        if (inToolLocation.x <= xLimit && inToolLocation.y > yLimit && inToolLocation.y < toolBounds.height - yLimit) {

                            drawRect(g, toolBounds.x,
                                     toolBounds.y,
                                     xLimit,
                                     toolBounds.height);

                            c.putClientProperty("dragAnchor", ToolWindowAnchor.LEFT);
                        } else
                        if (inToolLocation.y <= yLimit && inToolLocation.x > xLimit && inToolLocation.x < toolBounds.width - xLimit) {

                            drawRect(g, toolBounds.x,
                                     toolBounds.y,
                                     toolBounds.width,
                                     yLimit);

                            c.putClientProperty("dragAnchor", ToolWindowAnchor.TOP);
                        } else
                        if (inToolLocation.x >= toolBounds.width - xLimit && inToolLocation.y > yLimit && inToolLocation.y < toolBounds.height - yLimit) {

                            drawRect(g, toolBounds.x + toolBounds.width - xLimit,
                                     toolBounds.y,
                                     xLimit,
                                     toolBounds.height);

                            c.putClientProperty("dragAnchor", ToolWindowAnchor.RIGHT);
                        } else
                        if (inToolLocation.y >= toolBounds.height - yLimit && inToolLocation.x > xLimit && inToolLocation.x < toolBounds.width - xLimit) {
                            layout.setColumn(0, 0);

                            drawRect(g, toolBounds.x,
                                     toolBounds.y + toolBounds.height - yLimit,
                                     toolBounds.width,
                                     yLimit);

                            c.putClientProperty("dragAnchor", ToolWindowAnchor.BOTTOM);
                        } else {
                            drawRect(g, toolBounds.x, toolBounds.y,
                                     toolBounds.width,
                                     toolBounds.height);
                            c.putClientProperty("dragAnchor", null);
                        }
                    } else {
                        c.putClientProperty("dragAnchor", null);
                    }
                } else {
                    c.putClientProperty("dragAnchor", null);
                }
            }
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("dragStart".equals(propertyName)) {
            dragActive = true;
        } else if ("dragOver".equals(propertyName)) {
            mouseLocation = (Point) evt.getNewValue();
        } else if ("dragExit".equals(propertyName)) {
            mouseLocation = null;
            contentPanel.resetLayout();
        } else if ("dragEnd".equals(propertyName)) {
            mouseLocation = null;
            dragActive = false;
            contentPanel.resetLayout();
        }

        SwingUtil.repaint(contentPanel);
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
}
