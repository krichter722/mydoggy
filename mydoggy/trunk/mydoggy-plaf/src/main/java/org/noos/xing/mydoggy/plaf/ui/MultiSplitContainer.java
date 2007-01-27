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
            Component c = getComponent(0);
            remove(c);

            JSplitPane split = new JSplitPane(orientation);
            split.setResizeWeight(0.5d);
            split.setContinuousLayout(true);
            split.setBorder(null);
            split.setLeftComponent(c);
            split.setRightComponent(content);
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
            if (splitPane.getRightComponent() == content)
                add(splitPane.getLeftComponent(), "0,0");
            else
                add(splitPane.getRightComponent(), "0,0");
        } else {
            JSplitPane splitPane = (JSplitPane) getComponent(0);
            JSplitPane previous = null;
            while (splitPane != null) {
                if (splitPane.getRightComponent() == content) {
                    if (previous == null) {
                        removeAll();
                        add(splitPane.getLeftComponent(), "0,0");
                        break;
                    } else {
                        previous.setLeftComponent(splitPane.getLeftComponent());
                        break;
                    }
                } else if (splitPane.getLeftComponent() == content) {
                    assert previous != null;
                    previous.setLeftComponent(splitPane.getRightComponent());
                    break;
                } else if (splitPane.getLeftComponent() instanceof JSplitPane) {
                    previous = splitPane;
                    splitPane = (JSplitPane) splitPane.getLeftComponent();
                } else
                    throw new IllegalStateException("It's impossibile!!! Contact software developers.");
            }
        }
    }

    public boolean isEmpty() {
        return components.size() == 0;
    }
}
