package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitPane;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsContainer extends JPanel implements PropertyChangeListener {
    protected Map<String, byte[]> models;
    protected ToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;
    protected List<Component> contents;
    protected List<String> toolIds;
    protected int orientation;
    protected ToolWindow.AggregationPosition defaultAggregationPosition;

    protected MultiSplitPane multiSplitPane;
    protected MultiSplitLayout.Split multiSplitPaneModelRoot;


    public ToolsContainer(MyDoggyToolWindowManager toolWindowManager, int orientation) {
        this.orientation = orientation;
        this.contents = new ArrayList<Component>();
        this.toolIds = new ArrayList<String>();
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();

        this.multiSplitPane = new MultiSplitPane();
        this.multiSplitPane.setDividerSize(5);
        this.multiSplitPane.setFocusable(false);
        this.multiSplitPaneModelRoot = new MultiSplitLayout.Split();
        this.multiSplitPaneModelRoot.setRowLayout(orientation != JSplitPane.VERTICAL_SPLIT);
        if (multiSplitPaneModelRoot.isRowLayout()) {
            defaultAggregationPosition = ToolWindow.AggregationPosition.RIGHT;
        } else
            defaultAggregationPosition = ToolWindow.AggregationPosition.BOTTOM;
        this.models = new Hashtable<String, byte[]>();

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }


    public void propertyChange(PropertyChangeEvent evt) {
    }


    public void addContent(String toolId, Component content,
                           ToolWindow aggregationOnTool, ToolWindow.AggregationPosition aggregationPosition) {
        ToolWindow toolWindow = (ToolWindow) ((JComponent) content).getClientProperty(ToolWindow.class);
        if (toolWindow != null)
            toolId = toolWindow.getId();

        toolId = toolId + aggregationPosition.toString();

        // Store old layout
        StringBuilder builder = new StringBuilder();
        for (String id : toolIds) {
            builder.append(id);
        }
        models.put(builder.toString(), encode());

        builder.append(toolId);

        if (contents.size() == 0) {
            removeAll();
            add(content, "0,0,FULL,FULL");
        } else {
            byte[] oldModel = models.get(builder.toString());

            if (aggregationPosition == ToolWindow.AggregationPosition.DEFAULT)
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

                    boolean rowLayout = (aggregationPosition == ToolWindow.AggregationPosition.LEFT || aggregationPosition == ToolWindow.AggregationPosition.RIGHT);

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

                    if (aggregationOnTool != null) {

                        // Search for aggregationOnTool leaf
                        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
                        stack.push(multiSplitPaneModelRoot);

                        while (!stack.isEmpty()) {
                            MultiSplitLayout.Split split = stack.pop();

                            for (MultiSplitLayout.Node child : split.getChildren()) {
                                if (child instanceof MultiSplitLayout.Leaf) {
                                    MultiSplitLayout.Leaf leaf = (MultiSplitLayout.Leaf) child;

                                    JComponent root = (JComponent) getWrappedComponent((Container) multiSplitPane.getMultiSplitLayout().getChildMap().get(leaf.getName()));
                                    if (aggregationOnTool == root.getClientProperty(ToolWindow.class)) {                                        
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
                                                default :
                                                    step1Failed = true;
                                            }

                                            if (finalize) {
                                                // Set new children
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
                                                default :
                                                    step1Failed = true;
                                            }

                                            if (finalize) {
                                                // Set new children
                                                parent.setChildren(parentChildren);
                                            }
                                        }


                                        if (step1Failed) {
                                            // Create two leafs

                                            MultiSplitLayout.Leaf newleaf = new MultiSplitLayout.Leaf(leafName);
                                            newleaf.setWeight(0.5);

                                            // Creat the split
                                            MultiSplitLayout.Split newSplit = new MultiSplitLayout.Split();
                                            newSplit.setRowLayout((aggregationPosition == ToolWindow.AggregationPosition.LEFT || aggregationPosition == ToolWindow.AggregationPosition.RIGHT));
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
                        boolean rowLayout = (aggregationPosition == ToolWindow.AggregationPosition.LEFT || aggregationPosition == ToolWindow.AggregationPosition.RIGHT);

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
        toolIds.add(toolId);
    }

    public void removeContent(Component content) {
        int index = contents.indexOf(content);
        if (index == -1 || contents.remove(index) == null)
            return;

        // Store layout
        StringBuilder builder = new StringBuilder();
        for (String id : toolIds) {
            builder.append(id);
        }
        models.put(builder.toString(), encode());

        toolIds.remove(index);

        if (contents.size() == 0) {
            removeAll();
            return;
        }

        if (contents.size() == 1) {
            toolIds.clear();
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

    protected String foundLeafName(MultiSplitLayout.Split root, int counter, int index) {
        return (String) foundLeaf(root, counter, index)[0];
    }

    protected Object[] foundLeaf(MultiSplitLayout.Split root, int counter, int index) {
        for (MultiSplitLayout.Node child : root.getChildren()) {
            if (child instanceof MultiSplitLayout.Leaf) {
                counter++;
                if (counter == index) {
                    return new Object[]{((MultiSplitLayout.Leaf) child).getName(), counter};
                }
            } else if (child instanceof MultiSplitLayout.Split) {
                Object[] result = foundLeaf((MultiSplitLayout.Split) child, counter, index);
                if (result[0] != null)
                    return result;
                else
                    counter = (Integer) result[1];
            }
        }
        return new Object[]{null, counter};
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