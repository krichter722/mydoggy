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
    private List<Component> contents;
    private int orientation;

    public MultiSplitContainer(int orientation) {
        this.orientation = orientation;
        this.contents = new ArrayList<Component>();

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }

    public void addContent(Component content) {
        if (contents.size() == 0) {
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
        contents.add(content);
    }

    public void removeContent(Component content) {
        if (!contents.remove(content))
            return;

        if (contents.size() == 0)
            return;

        if (contents.size() == 1) {
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

    public void setComponentAt(Component content, int index) {
        if (index >= contents.size())
            throw new IllegalArgumentException("Illegal index.");

        if (contents.size() == 0)
            addContent(content);
        else if (contents.size() == 1) {
            removeAll();
            if (contents.contains(content))
                return;
            
            add(content, "0,0");
        } else {
            JSplitPane splitPane = (JSplitPane) getComponent(0);
            int i = 0;
            int pos = 0;
            for (; i < index; i++) {
                Container right = (Container) splitPane.getRightComponent();
                if (right.getComponentCount() == 0) {
                    pos = 1;
                    break;
                }

                Component cmp = right.getComponent(0);
                if (cmp instanceof JSplitPane) {
                    splitPane = (JSplitPane) right;
                } else {
                    pos = 1;
                    break;
                }
            }

            JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
            panel.setFocusCycleRoot(true);
            panel.add(content, "0,0,FULL,FULL");

            if (pos == 0) {
                splitPane.setLeftComponent(panel);
            } else {
                splitPane.setRightComponent(panel);
            }
        }
    }

    public boolean isEmpty() {
        return contents.size() == 0;
    }

    public int getContentCount() {
        return contents.size();
    }

    protected Component getRightCmp(JSplitPane pane) {
        return ((JPanel) pane.getRightComponent()).getComponent(0);
    }

    protected Component getLeftCmp(JSplitPane pane) {
        return ((JPanel) pane.getLeftComponent()).getComponent(0);
    }

}
