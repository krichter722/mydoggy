package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowDropTarget;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentPanel extends JPanel implements PropertyChangeListener {
    protected String parentPrefix;

    protected TableLayout layout;
    protected Point mouseLocation;
    protected boolean dragActive;

    public ContentPanel(String parentPrefix) {
        this.parentPrefix = parentPrefix;
        setLayout(layout = new TableLayout(new double[][]{{0, -1, 0}, {0, -1, 0}}));
        setOpaque(false);

        addPropertyChangeListener("dragStart", this);
        addPropertyChangeListener("dragOver", this);
        addPropertyChangeListener("dragExit", this);
        addPropertyChangeListener("dragEnd", this);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if ("dragStart".equals(propertyName)) {
            dragActive = true;
        } else if ("dragOver".equals(propertyName)) {
            mouseLocation = (Point) evt.getNewValue();
        } else if ("dragExit".equals(propertyName)) {
            mouseLocation = null;
            resetLayout();
        } else if ("dragEnd".equals(propertyName)) {
            mouseLocation = null;
            dragActive = false;
            resetLayout();
        }
        SwingUtil.repaint(this);
    }

    public Component getComponent() {
        return (getComponentCount() == 0) ? null : getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "1,1,FULL,FULL");
    }

    public void resetComponent() {
        removeAll();
        resetLayout();        
    }

    public void paint(Graphics g) {
        super.paint(g);

        if (dragActive && mouseLocation != null) {
            int xLimit = 20;
            int yLimit = 20;

            if (mouseLocation.x <= xLimit && mouseLocation.y > yLimit && mouseLocation.y < getHeight() - yLimit) {
                layout.setColumn(0, xLimit);
                layout.setColumn(2, 0);
                layout.setRow(0, 0);
                layout.setRow(2, 0);

                g.setColor(Color.BLUE);
                GraphicsUtil.drawRect(g, 0, 0, xLimit, getHeight(), 3);

                putProperty("dragToolWindow");
                putClientProperty("dragAnchor", ToolWindowAnchor.LEFT);
            } else if (mouseLocation.y <= yLimit && mouseLocation.x > xLimit && mouseLocation.x < getWidth() - xLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 0);
                layout.setRow(0, 20);
                layout.setRow(2, 0);

                g.setColor(Color.BLUE);
                GraphicsUtil.drawRect(g, 0, 0, getWidth(), yLimit, 3);

                putProperty("dragToolWindow");
                putClientProperty("dragAnchor", ToolWindowAnchor.TOP);
            } else
            if (mouseLocation.x >= getWidth() - xLimit && mouseLocation.y > yLimit && mouseLocation.y < getHeight() - yLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 20);
                layout.setRow(0, 0);
                layout.setRow(2, 0);

                g.setColor(Color.BLUE);
                GraphicsUtil.drawRect(g, getWidth() - xLimit, 0, xLimit, getHeight(), 3);

                putProperty("dragToolWindow");
                putClientProperty("dragAnchor", ToolWindowAnchor.RIGHT);
            } else
            if (mouseLocation.y >= getHeight() - yLimit && mouseLocation.x > xLimit && mouseLocation.x < getWidth() - xLimit) {
                layout.setColumn(0, 0);
                layout.setColumn(2, 0);
                layout.setRow(0, 0);
                layout.setRow(2, 20);

                g.setColor(Color.BLUE);
                GraphicsUtil.drawRect(g, 0, getHeight() - yLimit, getWidth(), yLimit, 3);

                putProperty("dragToolWindow");
                putClientProperty("dragAnchor", ToolWindowAnchor.BOTTOM);
            } else {
                resetLayout();

                // Check if the mouse is on a toolwindow
                Component deepestCmp = SwingUtilities.getDeepestComponentAt(this, mouseLocation.x, mouseLocation.y);
                if (deepestCmp != null) {
                    JComponent toolWindowContainer = (JComponent) SwingUtil.getParent(deepestCmp, parentPrefix);
                    if (toolWindowContainer != null) {

                        // Ok the mouse is on a toolwindow

                        Rectangle toolBounds = toolWindowContainer.getBounds();
                        Point point = SwingUtilities.convertPoint(toolWindowContainer,
                                                                  new Point(0, 0),
                                                                  this);
                        toolBounds.x = point.x;
                        toolBounds.y = point.y;

                        xLimit = toolBounds.width / 3;
                        yLimit = toolBounds.height / 3;

                        Point inToolLocation = SwingUtilities.convertPoint(this, mouseLocation, toolWindowContainer);

                        if (inToolLocation.x <= xLimit && inToolLocation.y > yLimit && inToolLocation.y < toolBounds.height - yLimit) {
                            g.setColor(Color.BLUE);
                            GraphicsUtil.drawRect(g, toolBounds.x,
                                                  toolBounds.y,
                                                  xLimit,
                                                  toolBounds.height, 3);

                            putClientProperty("dragAnchor", ToolWindowAnchor.LEFT);
                        } else
                        if (inToolLocation.y <= yLimit && inToolLocation.x > xLimit && inToolLocation.x < toolBounds.width - xLimit) {
                            g.setColor(Color.BLUE);
                            GraphicsUtil.drawRect(g, toolBounds.x,
                                                  toolBounds.y,
                                                  toolBounds.width,
                                                  yLimit, 3);

                            putClientProperty("dragAnchor", ToolWindowAnchor.TOP);
                        } else if (inToolLocation.x >= toolBounds.width - xLimit && inToolLocation.y > yLimit && inToolLocation.y < toolBounds.height - yLimit) {
                            g.setColor(Color.BLUE);
                            GraphicsUtil.drawRect(g, toolBounds.x + toolBounds.width - xLimit,
                                                  toolBounds.y,
                                                  xLimit,
                                                  toolBounds.height, 3);

                            putClientProperty("dragAnchor", ToolWindowAnchor.RIGHT);
                        } else if (inToolLocation.y >= toolBounds.height - yLimit && inToolLocation.x > xLimit && inToolLocation.x < toolBounds.width - xLimit) {
                            layout.setColumn(0, 0);
                            g.setColor(Color.BLUE);
                            GraphicsUtil.drawRect(g, toolBounds.x,
                                                  toolBounds.y + toolBounds.height - yLimit,
                                                  toolBounds.width,
                                                  yLimit, 3);

                            putClientProperty("dragAnchor", ToolWindowAnchor.BOTTOM);
                        } else {
                            g.setColor(Color.BLUE);
                            GraphicsUtil.drawRect(g, toolBounds.x, toolBounds.y,
                                                  toolBounds.width,
                                                  toolBounds.height, 3);
                            putClientProperty("dragAnchor", null);
                        }
                    } else {
                        putClientProperty("dragAnchor", null);
                    }
                } else {
                    putClientProperty("dragAnchor", null);
                }
            }
        }

    }

    protected void resetLayout() {
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);
    }

    protected void putProperty(String name) {
        Boolean value = (Boolean) getClientProperty(name);
        if (value != null)
            putClientProperty(name, !value);
        else
            putClientProperty(name, false);
    }

}
