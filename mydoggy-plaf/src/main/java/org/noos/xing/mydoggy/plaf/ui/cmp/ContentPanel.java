package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowDropTarget;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ContentPanel extends JPanel implements PropertyChangeListener {
    protected Point mouseLocation;
    protected boolean dragActive;

    public ContentPanel(ToolWindowManager manager, ToolWindowAnchor anchor) {
        setLayout(new TableLayout(new double[][]{{-1},{-1}}));
        setOpaque(false);
        setDropTarget(new ToolWindowDropTarget(this, manager, anchor));

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
        } else if ("dragEnd".equals(propertyName)) {
            mouseLocation = null;
            dragActive = false;
        }
        SwingUtil.repaint(this);
    }

    public Component getComponent() {
        return (getComponentCount() == 0) ? null : getComponent(0);
    }

    public void setComponent(Component component) {
        removeAll();
        add(component, "0,0,FULL,FULL");
    }

    public void paint(Graphics g) {
        super.paint(g);

        if (dragActive && mouseLocation != null) {            
            int xLimit = getWidth() / 4;
            int yLimit = getHeight() / 4;

            if (mouseLocation.x <= xLimit && mouseLocation.y > yLimit && mouseLocation.y < getHeight() - yLimit) {
                g.setColor(Color.BLUE);
                g.fillRect(xLimit, 0, 3, getHeight());
                putClientProperty("dragAnchor", ToolWindowAnchor.LEFT);
            } else if (mouseLocation.y <= yLimit && mouseLocation.x > xLimit && mouseLocation.x < getWidth() - xLimit) {
                g.setColor(Color.BLUE);
                g.fillRect(0, yLimit, getWidth(), 3);
                putClientProperty("dragAnchor", ToolWindowAnchor.TOP);
            } else if (mouseLocation.x >= getWidth() - xLimit && mouseLocation.y > yLimit && mouseLocation.y < getHeight() - yLimit) {
                g.setColor(Color.BLUE);
                g.fillRect(getWidth() - xLimit, 0, 3, getHeight());
                putClientProperty("dragAnchor", ToolWindowAnchor.RIGHT);
            } else if (mouseLocation.y >= getHeight() - yLimit && mouseLocation.x > xLimit && mouseLocation.x < getWidth() - xLimit) {
                g.setColor(Color.BLUE);
                g.fillRect(0, getHeight() - yLimit, getWidth(), 3);
                putClientProperty("dragAnchor", ToolWindowAnchor.BOTTOM);
            } else {
                putClientProperty("dragAnchor", null);
            }
        }
    }
}
