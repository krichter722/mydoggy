package org.noos.xing.mydoggy.plaf.ui.cmp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo add store capabilities...
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
            split.setFocusable(false);
            split.setBorder(null);

            JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
            panel.setFocusCycleRoot(true);
            panel.add(root, "0,0,FULL,FULL");
            split.setLeftComponent(panel);

            panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
            panel.setFocusCycleRoot(true);
            panel.add(content, "0,0,FULL,FULL");
            split.setRightComponent(panel);

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

            if (getRightCmp(splitPane) == content) {
                add(getLeftCmp(splitPane), "0,0");
            } else {
                add(getRightCmp(splitPane), "0,0");
            }
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

            int i = contents.size() - index - 1;
            boolean left = false;
            while (splitPane != null && i != 0) {
                if (getLeftCmp(splitPane) instanceof JSplitPane) {
                    splitPane = (JSplitPane) getLeftCmp(splitPane);
                    i--;
                } else {
                    left = true;
                    break;
                }
            }

            assert splitPane != null : "It's impossibile!!! Contact software developers.";
            
            Container container;
            if (left) {
                container = (Container) splitPane.getLeftComponent();
            } else
                container = (Container) splitPane.getRightComponent();

            container.removeAll();
            container.add(content, "0,0,FULL,FULL");
        }
    }

    public boolean isEmpty() {
        return contents.size() == 0;
    }

    public int getContentCount() {
        return contents.size();
    }

    public List<Component> getContents() {
        return contents;
    }

    protected Component getRightCmp(JSplitPane pane) {
        return ((JPanel) pane.getRightComponent()).getComponent(0);
    }

    protected Component getLeftCmp(JSplitPane pane) {
        return ((JPanel) pane.getLeftComponent()).getComponent(0);
    }

}
