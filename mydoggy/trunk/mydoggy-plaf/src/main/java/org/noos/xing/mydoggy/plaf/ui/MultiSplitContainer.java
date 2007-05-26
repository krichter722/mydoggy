package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitContainer extends JPanel {
    private List<Component> components;
    private int orientation;

    public MultiSplitContainer(int orientation) {
        this.orientation = orientation;
        this.components = new ArrayList<Component>();

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }

    public void addContent(Component content) {
        if (components.size() == 0) {
            removeAll();
            add(content, "0,0");
        } else {
            Component root = getComponent(0);
            remove(root);

            JSplitPane split = new JSplitPane(orientation);
            split.setResizeWeight(0.5d);
            split.setContinuousLayout(true);
            split.setBorder(null);

            JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
            panel.setFocusCycleRoot(true);
            panel.add(root, "0,0,FULL,FULL");

            split.setLeftComponent(panel);

            panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
            panel.setFocusCycleRoot(true);
            panel.add(content, "0,0,FULL,FULL");

            split.setRightComponent(panel);
            split.setFocusable(false);
            add(split, "0,0");
        }
        components.add(content);
    }

    public void removeContent(Component content) {
        if (!components.remove(content))
            return;

        if (components.size() == 0)
            return;

        if (components.size() == 1) {
            JSplitPane splitPane = (JSplitPane) getComponent(0);
            removeAll();
            if (getRightCmp(splitPane) == content)
                add(getLeftCmp(splitPane), "0,0");
            else
                add(getRightCmp(splitPane), "0,0");
        } else {
            JSplitPane splitPane = (JSplitPane) getComponent(0);
            JSplitPane previous = null;
            while (splitPane != null) {
                if (getRightCmp(splitPane) == content) {
                    if (previous == null) {
                        removeAll();
                        add(getLeftCmp(splitPane), "0,0");
                        break;
                    } else {
                        previous.setLeftComponent(splitPane.getLeftComponent());
                        break;
                    }
                } else if (getLeftCmp(splitPane) == content) {
                    assert previous != null;
                    previous.setLeftComponent(splitPane.getRightComponent());
                    break;
                } else if (getLeftCmp(splitPane) instanceof JSplitPane) {
                    previous = splitPane;
                    splitPane = (JSplitPane) getLeftCmp(splitPane);
                } else
                    throw new IllegalStateException("It's impossibile!!! Contact software developers.");
            }
        }
    }

    public void moveTo(Component c, int index) {

    }

    public boolean isEmpty() {
        return components.size() == 0;
    }

    protected Component getRightCmp(JSplitPane pane) {
        return ((JPanel) pane.getRightComponent()).getComponent(0);
    }

    protected Component getLeftCmp(JSplitPane pane) {
        return ((JPanel) pane.getLeftComponent()).getComponent(0);
    }

}
