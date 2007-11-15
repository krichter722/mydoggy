package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitPane;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MultiSplitDockableContainer extends JPanel {
    protected Map<String, byte[]> models;
    protected ToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;
    protected List<Component> contents;
    protected List<String> dockableIds;
    protected int orientation;
    protected AggregationPosition defaultAggregationPosition;

    protected MultiSplitPane multiSplitPane;
    protected MultiSplitLayout.Split multiSplitPaneModelRoot;


    public MultiSplitDockableContainer(MyDoggyToolWindowManager toolWindowManager, int orientation) {
        this.orientation = orientation;
        this.contents = new ArrayList<Component>();
        this.dockableIds = new ArrayList<String>();
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();

        this.multiSplitPane = new MultiSplitPane();
        this.multiSplitPane.setDividerSize(5);
        this.multiSplitPane.setFocusable(false);

        this.multiSplitPaneModelRoot = new MultiSplitLayout.Split();
        this.multiSplitPaneModelRoot.setRowLayout(orientation != JSplitPane.VERTICAL_SPLIT);
        if (multiSplitPaneModelRoot.isRowLayout()) {
            defaultAggregationPosition = AggregationPosition.RIGHT;
        } else
            defaultAggregationPosition = AggregationPosition.BOTTOM;
        this.models = new Hashtable<String, byte[]>();

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }


    /**
     * TODO : standardize this method...
     * @param dockable
     * @param dockableId
     * @param content
     * @param aggregationOnDockable
     * @param aggregationPosition
     */
    public void addContent(Dockable dockable,
                           String dockableId,
                           Component content,
                           Dockable aggregationOnDockable,
                           AggregationPosition aggregationPosition) {

        ToolWindow toolWindow = (ToolWindow) ((JComponent) content).getClientProperty(ToolWindow.class);
        if (toolWindow != null)
            dockableId = toolWindow.getId();

        if (aggregationOnDockable == null)
            dockableId = dockableId + aggregationPosition.toString();
        else
            dockableId = dockableId + aggregationOnDockable.getId() + aggregationPosition.toString();

        // Store old layout
        StringBuilder builder = new StringBuilder();
        for (String id : dockableIds) {
            builder.append(id);
        }
        models.put(builder.toString(), encode());

        builder.append(dockableId);

        if (contents.size() == 0) {
            removeAll();
            add(content, "0,0,FULL,FULL");
        } else {
            byte[] oldModel = models.get(builder.toString());

            if (aggregationPosition == AggregationPosition.DEFAULT)
                aggregationPosition = defaultAggregationPosition;

            if (contents.size() == 1) {
                Component previousContent = getComponent(0);
                remove(previousContent);

                if (oldModel != null) {
                    multiSplitPaneModelRoot = decode(oldModel);
                } else {
                    // Create two leafs
                    MultiSplitLayout.Leaf leaf = new MultiSplitLayout.Leaf("1");
                    leaf.setWeight(0.5);
                    MultiSplitLayout.Leaf leaf2 = new MultiSplitLayout.Leaf("2");
                    leaf2.setWeight(0.5);
                    List<MultiSplitLayout.Node> children = Arrays.asList(leaf,
                                                                         new MultiSplitLayout.Divider(),
                                                                         leaf2);

                    boolean rowLayout = (aggregationPosition == AggregationPosition.LEFT || aggregationPosition == AggregationPosition.RIGHT);

                    multiSplitPaneModelRoot = new MultiSplitLayout.Split();
                    multiSplitPaneModelRoot.setRowLayout(rowLayout);
                    multiSplitPaneModelRoot.setChildren(children);
                    if (!multiSplitPane.getMultiSplitLayout().getFloatingDividers())
                        multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
                }

                validateSplit(multiSplitPaneModelRoot);
                multiSplitPane.setModel(multiSplitPaneModelRoot);

                switch (aggregationPosition) {
                    case LEFT:
                    case TOP:
                        multiSplitPane.add(getComponentWrapper(content), "1");
                        multiSplitPane.add(getComponentWrapper(previousContent), "2");
                        break;
                    case RIGHT:
                    case BOTTOM:
                        multiSplitPane.add(getComponentWrapper(previousContent), "1");
                        multiSplitPane.add(getComponentWrapper(content), "2");
                        break;
                }

                add(multiSplitPane, "0,0,FULL,FULL");
            } else {
                // Build content to add
                String leafName = "" + (contents.size() + 1);

                if (oldModel != null) {
                    multiSplitPaneModelRoot = decode(oldModel);
                } else {
                    // Modify model

                    if (aggregationOnDockable != null) {

                        // Search for aggregationOnDockable leaf
                        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
                        stack.push(multiSplitPaneModelRoot);

                        while (!stack.isEmpty()) {
                            MultiSplitLayout.Split split = stack.pop();

                            for (MultiSplitLayout.Node child : split.getChildren()) {
                                if (child instanceof MultiSplitLayout.Leaf) {
                                    MultiSplitLayout.Leaf leaf = (MultiSplitLayout.Leaf) child;

                                    // Could bypass this...
                                    JComponent root = (JComponent) getWrappedComponent((Container) multiSplitPane.getMultiSplitLayout().getChildMap().get(leaf.getName()));
                                    if (aggregationOnDockable == root.getClientProperty(ToolWindow.class)) {
                                        boolean step1Failed = false;

                                        // Check for concordance to leaf.getParent().isRowLayout and aggregationPosition
                                        MultiSplitLayout.Split parent = leaf.getParent();
                                        boolean rowLayout = parent.isRowLayout();

                                        List<MultiSplitLayout.Node> parentChildren = parent.getChildren();
                                        int startIndex = parentChildren.indexOf(leaf);
                                        if (rowLayout) {
                                            boolean finalize = false;
                                            switch (aggregationPosition) {
                                                case LEFT:
                                                    parentChildren.add(startIndex, new MultiSplitLayout.Leaf(leafName));
                                                    parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                    finalize = true;
                                                    break;
                                                case RIGHT:
                                                    parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                    parentChildren.add(startIndex + 2, new MultiSplitLayout.Leaf(leafName));
                                                    finalize = true;
                                                    break;
                                                default:
                                                    step1Failed = true;
                                            }

                                            if (finalize) {
                                                // Set new children
                                                forceWeight(parentChildren);
                                                parent.setChildren(parentChildren);
                                            }
                                        } else {
                                            boolean finalize = false;
                                            switch (aggregationPosition) {
                                                case TOP:
                                                    parentChildren.add(startIndex, new MultiSplitLayout.Leaf(leafName));
                                                    parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                    finalize = true;
                                                    break;
                                                case BOTTOM:
                                                    parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                    parentChildren.add(startIndex + 2, new MultiSplitLayout.Leaf(leafName));
                                                    finalize = true;
                                                    break;
                                                default:
                                                    step1Failed = true;
                                            }

                                            if (finalize) {
                                                // Set new children
                                                forceWeight(parentChildren);
                                                parent.setChildren(parentChildren);
                                            }
                                        }


                                        if (step1Failed) {
                                            // Create two leafs

                                            MultiSplitLayout.Leaf newleaf = new MultiSplitLayout.Leaf(leafName);
                                            newleaf.setWeight(0.5);

                                            // Creat the split
                                            MultiSplitLayout.Split newSplit = new MultiSplitLayout.Split();
                                            newSplit.setRowLayout((aggregationPosition == AggregationPosition.LEFT || aggregationPosition == AggregationPosition.RIGHT));
                                            newSplit.setWeight(leaf.getWeight());
                                            newSplit.setChildren(Arrays.asList(leaf,
                                                                               new MultiSplitLayout.Divider(),
                                                                               newleaf));

                                            leaf.setWeight(0.5);

                                            // Switch the leaf with the new split
                                            parentChildren.set(startIndex, newSplit);
                                            parent.setChildren(parentChildren);
                                        }


                                        stack.clear();
                                        break;
                                    }
                                } else if (child instanceof MultiSplitLayout.Split) {
                                    stack.push((MultiSplitLayout.Split) child);
                                }
                            }
                        }

                        if (!multiSplitPane.getMultiSplitLayout().getFloatingDividers())
                            multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
                    } else {
                        boolean rowLayout = (aggregationPosition == AggregationPosition.LEFT || aggregationPosition == AggregationPosition.RIGHT);

                        if (multiSplitPaneModelRoot.isRowLayout() == rowLayout) {
                            List<MultiSplitLayout.Node> children = multiSplitPaneModelRoot.getChildren();

                            switch (aggregationPosition) {
                                case LEFT:
                                case TOP:
                                    children.add(0, new MultiSplitLayout.Leaf(leafName));
                                    children.add(1, new MultiSplitLayout.Divider());
                                    break;
                                case RIGHT:
                                case BOTTOM:
                                    children.add(new MultiSplitLayout.Divider());
                                    children.add(new MultiSplitLayout.Leaf(leafName));
                                    break;
                            }

                            forceWeight(children);

                            multiSplitPaneModelRoot.setChildren(children);
                        } else {
                            MultiSplitLayout.Split newRoot = new MultiSplitLayout.Split();
                            newRoot.setRowLayout(rowLayout);

                            MultiSplitLayout.Leaf leaf = new MultiSplitLayout.Leaf(leafName);
                            leaf.setWeight(0.5);
                            multiSplitPaneModelRoot.setWeight(0.5);

                            List<MultiSplitLayout.Node> children = null;
                            switch (aggregationPosition) {
                                case LEFT:
                                case TOP:
                                    children = Arrays.asList(leaf,
                                                             new MultiSplitLayout.Divider(),
                                                             multiSplitPaneModelRoot);
                                    break;
                                case RIGHT:
                                case BOTTOM:
                                    children = Arrays.asList(multiSplitPaneModelRoot,
                                                             new MultiSplitLayout.Divider(),
                                                             leaf);
                                    break;
                            }
                            forceWeight(children);
                            newRoot.setChildren(children);

                            multiSplitPaneModelRoot = newRoot;
                        }

                        if (!multiSplitPane.getMultiSplitLayout().getFloatingDividers())
                            multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
                    }
                }

                validateSplit(multiSplitPaneModelRoot);
                multiSplitPane.setModel(multiSplitPaneModelRoot);
                multiSplitPane.add(getComponentWrapper(content), leafName);
            }

            repaintSplit();
        }

        contents.add(content);
        dockableIds.add(dockableId);
    }

    public void removeContent(Component content) {
        int index = contents.indexOf(content);
        if (index == -1 || contents.remove(index) == null)
            return;

        // Store layout
        StringBuilder builder = new StringBuilder();
        for (String id : dockableIds) {
            builder.append(id);
        }
        models.put(builder.toString(), encode());

        dockableIds.remove(index);

        if (contents.size() == 0) {
            removeAll();
            return;
        }

        if (contents.size() == 1) {
            dockableIds.clear();
            // remove content
            for (Component c : multiSplitPane.getComponents()) {
                if (getWrappedComponent((Container) c) == content) {
                    multiSplitPane.remove(c);
                    break;
                }
            }

            // obtain the component remained.
            Component c = getWrappedComponent((Container) multiSplitPane.getComponent(0));
            multiSplitPane.removeAll();

            add(c, "0,0,FULL,FULL");
        } else {
            Container contentContainer = null;
            String leafKey = null;
            int leafValue = -1;

            // Get key for content
            Map<String, Component> childMap = multiSplitPane.getMultiSplitLayout().getChildMap();
            for (String key : childMap.keySet()) {
                Container container = (Container) childMap.get(key);

                if (getWrappedComponent(container) == content) {
                    contentContainer = container;
                    leafValue = Integer.parseInt(key);
                    leafKey = key;
                }
            }

            // Remove content
            if (contentContainer != null) {
                multiSplitPane.remove(contentContainer);

                // Update model
                Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
                stack.push(multiSplitPaneModelRoot);

                while (!stack.isEmpty()) {
                    MultiSplitLayout.Split split = stack.pop();

                    List<MultiSplitLayout.Node> children = split.getChildren();

                    for (int i = 0; i < children.size(); i++) {
                        MultiSplitLayout.Node child = children.get(i);

                        if (child instanceof MultiSplitLayout.Leaf) {
                            MultiSplitLayout.Leaf leaf = (MultiSplitLayout.Leaf) child;

                            String leafName = leaf.getName();

                            if (leafName.equals(leafKey)) {
                                // Analyze parent
                                children.remove(i);

                                // Analyze children now...
                                if (children.size() == 2) {
                                    MultiSplitLayout.Split grandpa = split.getParent();

                                    if (grandpa == null) {
                                        multiSplitPaneModelRoot = getFirstSplit(children);
                                    } else {
                                        List<MultiSplitLayout.Node> grenpaChildren = grandpa.getChildren();

                                        if (children.get(0) instanceof MultiSplitLayout.Divider) {
                                            grenpaChildren.set(grenpaChildren.indexOf(split),
                                                               children.get(1));
                                        } else {
                                            grenpaChildren.set(grenpaChildren.indexOf(split),
                                                               children.get(0));
                                        }
                                        grandpa.setChildren(grenpaChildren);
                                    }

                                } else {
                                    if (i < children.size())
                                        children.remove(i);
                                    else
                                        children.remove(i - 1);
                                    i--;
                                }
                            } else {
                                // We have to rename the leaf if the name is not valid.
                                Integer keyValue = Integer.parseInt(leafName);
                                if (keyValue > leafValue) {
                                    String newKey = "" + (keyValue - 1);
                                    leaf.setName(newKey);
                                }
                            }
                        } else if (child instanceof MultiSplitLayout.Split) {
                            stack.push((MultiSplitLayout.Split) child);
                        }
                    }

                    split.setChildren(children);
                }

                // Change constaints for component to the new leaf order. 
                String[] keys = childMap.keySet().toArray(new String[childMap.keySet().size()]);
                Arrays.sort(keys);
                for (String key : keys) {
                    Integer keyValue = Integer.parseInt(key);
                    if (keyValue > leafValue) {
                        String newKey = "" + (keyValue - 1);

                        Component oldCmpForLeaf = multiSplitPane.getMultiSplitLayout().getChildMap().get(key);
                        multiSplitPane.remove(oldCmpForLeaf);
                        multiSplitPane.add(oldCmpForLeaf, newKey);
                    }
                }


                validateSplit(multiSplitPaneModelRoot);
                multiSplitPane.setModel(multiSplitPaneModelRoot);
                multiSplitPane.revalidate();
                repaintSplit();
            }

        }
    }


    public MultiSplitLayout.Split getModel() {
        return multiSplitPaneModelRoot;
    }

    public void setModel(MultiSplitLayout.Split model) {
        validateSplit(model);

        multiSplitPaneModelRoot = model;
        multiSplitPane.setModel(multiSplitPaneModelRoot);
        repaintSplit();
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

    public void clear() {
        removeAll();
        contents.clear();
    }


    protected MultiSplitLayout.Split getFirstSplit(List<MultiSplitLayout.Node> children) {
        for (MultiSplitLayout.Node child : children) {
            if (child instanceof MultiSplitLayout.Split)
                return (MultiSplitLayout.Split) child;
        }
        return null;
    }

    protected Container getComponentWrapper(Component component) {
        JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        panel.setFocusCycleRoot(true);
        panel.add(component, "0,0,FULL,FULL");

        return panel;
    }

    protected Component getWrappedComponent(Container container) {
        return container.getComponent(0);
    }

    protected byte[] encode() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(os);
        e.writeObject(multiSplitPaneModelRoot);
        e.flush();
        e.close();
        return os.toByteArray();
    }

    protected MultiSplitLayout.Split decode(byte[] bytes) {
        XMLDecoder d = new XMLDecoder(new ByteArrayInputStream(bytes));
        return (MultiSplitLayout.Split) (d.readObject());
    }

    protected void validateSplit(MultiSplitLayout.Split split) {
        List<MultiSplitLayout.Node> children = split.getChildren();

        double sum = 0.0;
        for (MultiSplitLayout.Node node : children) {
            if (!(node instanceof MultiSplitLayout.Divider)) {
                sum += node.getWeight();
            }

            if (node instanceof MultiSplitLayout.Split) {
                validateSplit((MultiSplitLayout.Split) node);
            }

            if (sum > 1.0d)
                break;
        }

        if (sum > 1.0d) {
            double w = 1.0 / ((children.size() / 2) + 1);
            for (MultiSplitLayout.Node node : children) {
                if (!(node instanceof MultiSplitLayout.Divider)) {
                    node.setBounds(new Rectangle());
                    node.setWeight(w);
                }
            }
        }
    }

    protected void repaintSplit() {
        SwingUtil.repaint(multiSplitPane);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                multiSplitPane.invalidate();
                multiSplitPane.validate();
                multiSplitPane.repaint();
                multiSplitPane.getMultiSplitLayout().setFloatingDividers(false);
            }
        });
    }

    protected void forceWeight(List<MultiSplitLayout.Node> children) {
        double w = 1.0 / ((children.size() / 2) + 1);
        for (MultiSplitLayout.Node node : children) {
            if (!(node instanceof MultiSplitLayout.Divider)) {
                node.setBounds(new Rectangle());
                node.setWeight(w);
            }
        }
    }

}