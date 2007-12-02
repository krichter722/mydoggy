package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitPane;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.DockableUI;
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
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;

    protected Map<String, byte[]> models;
    protected Map<Dockable, DockableEntry> entries;
    protected int orientation;
    protected AggregationPosition defaultAggregationPosition;

    protected MultiSplitPane multiSplitPane;
    protected MultiSplitLayout.Split multiSplitPaneModelRoot;

    protected RepaintRunnable repaintRunnable;

    protected boolean storeLayout;
    protected boolean useAlwaysContentWrapper;

    public MultiSplitDockableContainer(MyDoggyToolWindowManager toolWindowManager, int orientation) {
        this.orientation = orientation;
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();
        this.entries = new LinkedHashMap<Dockable, DockableEntry>();

        this.multiSplitPane = new MultiSplitPane();
        this.multiSplitPane.setDividerSize(5);
        this.multiSplitPane.setFocusable(false);
        this.storeLayout = true;
        this.repaintRunnable = new RepaintRunnable();

        this.multiSplitPaneModelRoot = new MultiSplitLayout.Split();
        this.multiSplitPaneModelRoot.setRowLayout(orientation != JSplitPane.VERTICAL_SPLIT);
        if (multiSplitPaneModelRoot.isRowLayout()) {
            defaultAggregationPosition = AggregationPosition.RIGHT;
        } else
            defaultAggregationPosition = AggregationPosition.BOTTOM;
        this.models = new Hashtable<String, byte[]>();
        this.useAlwaysContentWrapper = false;

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }

    /**
     * @param dockable
     * @param content
     * @param aggregationOnDockable
     * @param aggregationIndexLocation
     * @param aggregationPosition
     */
    public void addDockable(Dockable dockable,
                            Component content,
                            Dockable aggregationOnDockable,
                            int aggregationIndexLocation,
                            AggregationPosition aggregationPosition) {
        if (!checkModel())
            System.out.println("Check model fail. addDockable before");

        // Build id
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(dockable.getId());
        if (aggregationOnDockable != null)
            idBuilder.append(aggregationOnDockable.getId());
        if (aggregationPosition != null)
            idBuilder.append(aggregationPosition.toString());

        String dockableId = idBuilder.toString();

        // Store old layout
        String modelKey = null;
        if (storeLayout && entries.size() > 0) {
            idBuilder.setLength(0); 
            for (DockableEntry entry : entries.values()) {
                idBuilder.append(entry.id);
            }
            models.put(idBuilder.toString(), encode());
            idBuilder.append(dockableId);
            modelKey = idBuilder.toString();
        }


        if (entries.size() == 0) {
            resetRootComponent();
            setRootComponent((useAlwaysContentWrapper) ? getComponentWrapper(dockable, content) : content);
        } else {
            byte[] oldModel = (modelKey != null) ? models.get(modelKey) : null;

            boolean invalidAggregationPosition = false;
            if (aggregationPosition == AggregationPosition.DEFAULT || aggregationPosition == null) {
                invalidAggregationPosition = true;
                aggregationPosition = defaultAggregationPosition;
            }

            if (entries.size() == 1) {
                Component previousContent = (useAlwaysContentWrapper)
                                            ? getWrappedComponent((Container) getRootComponent())
                                            : getRootComponent();
                resetRootComponent();

                if (aggregationOnDockable != null && (aggregationPosition == null || invalidAggregationPosition)) {
                    if (multiSplitPaneModelRoot != null)
                        throw new IllegalArgumentException("Invalid aggregationOnDockable");

                    Component componentWrapper = getComponentWrapper(entries.values().iterator().next().dockable, previousContent);

                    // The requeste is to add more than one dockable on the same leaf...
                    addToComponentWrapper(componentWrapper, dockable, aggregationIndexLocation, content);

                    resetRootComponent();
                    setRootComponent(componentWrapper);
                } else {
                    DockableLeaf leaf;
                    DockableLeaf leaf2;

                    if (storeLayout && oldModel != null) {
                        multiSplitPaneModelRoot = decode(oldModel);

                        List<MultiSplitLayout.Node> children = multiSplitPaneModelRoot.getChildren();
                        leaf = (DockableLeaf) children.get(0);
                        leaf2 = (DockableLeaf) children.get(2);
                    } else {
                        // Create two leafs
                        leaf = new DockableLeaf("1");
                        leaf.setWeight(0.5);

                        leaf2 = new DockableLeaf("2");
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

                    validateModel(multiSplitPaneModelRoot);
                    multiSplitPane.setModel(multiSplitPaneModelRoot);

                    switch (aggregationPosition) {
                        case LEFT:
                        case TOP:
                            if (oldModel == null) {
                                leaf.addDockable(dockable.getId());
                                leaf2.addDockable(entries.values().iterator().next().dockable.getId());
                            }
                            multiSplitPane.add(getComponentWrapper(dockable, content), "1");
                            multiSplitPane.add(getComponentWrapper(toolWindowManager.getDockable(leaf2.getDockable()), previousContent), "2");
                            break;
                        case RIGHT:
                        case BOTTOM:
                            if (oldModel == null) {
                                leaf.addDockable(entries.values().iterator().next().dockable.getId());
                                leaf2.addDockable(dockable.getId());
                            }
                            multiSplitPane.add(getComponentWrapper(toolWindowManager.getDockable(leaf.getDockable()), previousContent), "1");
                            multiSplitPane.add(getComponentWrapper(dockable, content), "2");
                            break;
                    }

                    setRootComponent(multiSplitPane);
                }
            } else {
                boolean addCmp = true;

                // Build content to add
                String leafName = "" + (entries.size() + 1);

                if (storeLayout && oldModel != null) {
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
                                if (child instanceof DockableLeaf) {
                                    DockableLeaf leaf = (DockableLeaf) child;

                                    if (aggregationOnDockable == toolWindowManager.getDockable(leaf.getDockable())) {
                                        if (invalidAggregationPosition) {
                                            // The requeste is to add more than one dockable on the same leaf...
                                            addToComponentWrapper(
                                                    multiSplitPane.getMultiSplitLayout().getChildMap().get(leaf.getName()),
                                                    dockable,
                                                    aggregationIndexLocation, content
                                            );

                                            leaf.addDockable(dockable.getId());

                                            addCmp = false;
                                        } else {
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
                                                        parentChildren.add(startIndex, new DockableLeaf(leafName, dockable.getId()));
                                                        parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                        finalize = true;
                                                        break;
                                                    case RIGHT:
                                                        parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                        parentChildren.add(startIndex + 2, new DockableLeaf(leafName, dockable.getId()));
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
                                                        parentChildren.add(startIndex, new DockableLeaf(leafName, dockable.getId()));
                                                        parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                        finalize = true;
                                                        break;
                                                    case BOTTOM:
                                                        parentChildren.add(startIndex + 1, new MultiSplitLayout.Divider());
                                                        parentChildren.add(startIndex + 2, new DockableLeaf(leafName, dockable.getId()));
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

                                                MultiSplitLayout.Leaf newleaf = new DockableLeaf(leafName, dockable.getId());
                                                newleaf.setWeight(0.5);

                                                // Creat the split
                                                MultiSplitLayout.Split newSplit = new MultiSplitLayout.Split();
                                                newSplit.setRowLayout((aggregationPosition == AggregationPosition.LEFT || aggregationPosition == AggregationPosition.RIGHT));
                                                newSplit.setWeight(leaf.getWeight());
                                                leaf.getParent().removeNode(leaf);
                                                switch (aggregationPosition) {
                                                    case LEFT:
                                                    case TOP:
                                                        newSplit.setChildren(Arrays.asList(newleaf,
                                                                                           new MultiSplitLayout.Divider(),
                                                                                           leaf));
                                                        break;
                                                    default:
                                                        newSplit.setChildren(Arrays.asList(leaf,
                                                                                           new MultiSplitLayout.Divider(),
                                                                                           newleaf));
                                                        break;
                                                }

                                                leaf.setWeight(0.5);

                                                // Switch the leaf with the new split
                                                parentChildren.set(startIndex, newSplit);
                                                parent.setChildren(parentChildren);
                                            }
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
                                    children.add(0, new DockableLeaf(leafName, dockable.getId()));
                                    children.add(1, new MultiSplitLayout.Divider());
                                    break;
                                case RIGHT:
                                case BOTTOM:
                                    children.add(new MultiSplitLayout.Divider());
                                    children.add(new DockableLeaf(leafName, dockable.getId()));
                                    break;
                            }

                            forceWeight(children);

                            multiSplitPaneModelRoot.setChildren(children);
                        } else {
                            MultiSplitLayout.Split newRoot = new MultiSplitLayout.Split();
                            newRoot.setRowLayout(rowLayout);

                            MultiSplitLayout.Leaf leaf = new DockableLeaf(leafName, dockable.getId());
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

                validateModel(multiSplitPaneModelRoot);
                multiSplitPane.setModel(multiSplitPaneModelRoot);

                if (addCmp)
                    multiSplitPane.add(getComponentWrapper(dockable, content), leafName);
            }
            if (!checkModel())
                System.out.println("Check model fail. addDockable end");

            repaintMultiSplit();
        }

        entries.put(dockable, new DockableEntry(dockable, content, dockableId));
    }

    public void removeDockable(Dockable dockable) {
        DockableEntry dockableEntry = entries.get(dockable);
        if (dockableEntry == null)
            throw new IllegalArgumentException("Cannot remove that dockable. It's not present into the container.");

        // Store layout
        if (storeLayout) {
            StringBuilder builder = new StringBuilder();
            for (DockableEntry entry : entries.values()) {
                builder.append(entry.id);
            }
            models.put(builder.toString(), encode());
        }
        entries.remove(dockable);

        if (entries.size() == 0) {
            multiSplitPaneModelRoot = null;

            resetRootComponent();
            return;
        }

        if (entries.size() == 1) {
            // remove content
            String leafName = getLeafName(dockable);
            if (leafName == null) {
                removeComponentWrapper(getRootComponent(),
                                       dockable);

                if (useAlwaysContentWrapper) {
                    setRootComponent(getComponentWrapper(entries.keySet().iterator().next(),
                                                         getWrappedComponent((Container) getRootComponent())));
                } else
                    setRootComponent(getWrappedComponent((Container) getRootComponent()));
            } else {
                Component c = multiSplitPane.getMultiSplitLayout().getChildMap().get(leafName);
                multiSplitPane.remove(c);

                if (useAlwaysContentWrapper) {
                    c = getComponentWrapper(entries.keySet().iterator().next(),
                                            getWrappedComponent((Container) multiSplitPane.getComponent(0)));
                    multiSplitPane.removeAll();
                } else {
                    // obtain the component remained.
                    c = getWrappedComponent((Container) multiSplitPane.getComponent(0));
                    multiSplitPane.removeAll();
                }
                setRootComponent(c);
            }
            SwingUtil.repaint(this);

            multiSplitPaneModelRoot = null;
        } else {
            DockableLeaf dockableLeaf = getLeaf(dockable);
            if (dockableLeaf == null)
                throw new IllegalArgumentException("Cannot remove that dockable. It's not present into the container. Call the admin.");

            if (dockableLeaf.getDockables().size() > 1) {
                // There are more than one dockable on the same leaf
                // Remove the dockable from leaf and from aggregating component...

                dockableLeaf.getDockables().remove(dockable.getId());

                removeComponentWrapper(multiSplitPane.getMultiSplitLayout().getChildMap().get(dockableLeaf.getName()),
                                       dockable);
            } else {
                // There is one dockable on the leaf. We have to rearrange the layout...
                String leafKey = dockableLeaf.getName();
                int leafValue = Integer.parseInt(leafKey);
                Container contentContainer = (Container) multiSplitPane.getMultiSplitLayout().getChildMap().get(leafKey);

                // Remove content
                if (contentContainer != null) {
                    // Remove the contentContainer from the multiSplitPane 
                    multiSplitPane.remove(contentContainer);

                    // Update model

                    // Navigate the model to look for the requested leaf
                    Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
                    stack.push(multiSplitPaneModelRoot);

                    boolean setChild = true;
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
                                            setChild = false;

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

                        if (setChild)
                            split.setChildren(children);
                        if (!checkModel())
                            System.out.println("Check model fail. removeDockable inner");
                    }

                    // Change constaints for component to the new leaf order.
                    Map<String, Component> childMap = multiSplitPane.getMultiSplitLayout().getChildMap();
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


                    validateModel(multiSplitPaneModelRoot);
                    multiSplitPane.setModel(multiSplitPaneModelRoot);
                    multiSplitPane.revalidate();
                } else
                    throw new IllegalArgumentException("Cannot find component on multisplit...");
            }

            if (!checkModel())
                System.out.println("Check model fail. removeDockable end");
            resetBounds();
            repaintMultiSplit();
        }
    }


    public MultiSplitLayout.Split getModel() {
        return multiSplitPaneModelRoot;
    }

    public void setModel(MultiSplitLayout.Split model) {
        validateModel(model);

        multiSplitPaneModelRoot = model;
        multiSplitPane.setModel(multiSplitPaneModelRoot);

        // TODO: Check the every leaf contains the right dockable
/*
        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push(multiSplitPaneModelRoot);

        Map<String, Component> childMap = multiSplitPane.getMultiSplitLayout().getChildMap();

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                if (child instanceof DockableLeaf) {
                    DockableLeaf leaf = (DockableLeaf) child;
                    


                } else if (child instanceof MultiSplitLayout.Split) {
                    stack.push((MultiSplitLayout.Split) child);
                }
            }
        }
*/

        repaintMultiSplit();
    }

    public boolean isEmpty() {
        return entries.size() == 0;
    }

    public int getContentCount() {
        return entries.size();
    }

    public List<DockableEntry> getContents() {
        return new ArrayList<DockableEntry>(entries.values());
    }

    public void clear() {
        resetRootComponent();
        multiSplitPane.removeAll();
        entries.clear();
    }

    public boolean isStoreLayout() {
        return storeLayout;
    }

    public void setStoreLayout(boolean storeLayout) {
        this.storeLayout = storeLayout;
    }

    public boolean isUseAlwaysContentWrapper() {
        return useAlwaysContentWrapper;
    }

    public void setUseAlwaysContentWrapper(boolean useAlwaysContentWrapper) {
        if (this.useAlwaysContentWrapper == useAlwaysContentWrapper)
            return;
        this.useAlwaysContentWrapper = useAlwaysContentWrapper;

        if (useAlwaysContentWrapper) {
            if (entries.size() == 1) {
                setRootComponent(getComponentWrapper(entries.keySet().iterator().next(),
                                                     getRootComponent()));
                SwingUtil.repaint(this);
            }
        } else {
            if (entries.size() == 1) {
                setRootComponent(getWrappedComponent((Container) getRootComponent()));
                SwingUtil.repaint(this);
            }
        }
    }


    protected Component getRootComponent() {
        return getComponent(0);
    }

    protected void setRootComponent(Component component) {
        resetRootComponent();
        add(component, "0,0,FULL,FULL");
    }

    protected void resetRootComponent() {
        removeAll();
    }

    protected Container getComponentWrapper(Dockable dockable, Component component) {
        JPanel panel = new JPanel(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        panel.setFocusCycleRoot(true);
        panel.add(component, "0,0,FULL,FULL");

        return panel;
    }

    protected Component getWrappedComponent(Container container) {
        return container.getComponent(0);
    }

    protected void addToComponentWrapper(Component wrapperSource, Dockable dockable, int aggregationIndexLocation, Component content) {
        throw new IllegalStateException("Cannot call this method...");
    }

    protected void removeComponentWrapper(Component wrapperSource, Dockable dockable) {
        throw new IllegalStateException("Cannot call this method...");
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

    protected void validateModel(MultiSplitLayout.Split split) {
        List<MultiSplitLayout.Node> children = split.getChildren();

        double sum = 0.0;
        for (MultiSplitLayout.Node node : children) {
            if (!(node instanceof MultiSplitLayout.Divider)) {
                sum += node.getWeight();
            }

            if (node instanceof MultiSplitLayout.Split) {
                validateModel((MultiSplitLayout.Split) node);
            }

            if (sum > 1.0d)
                break;
        }

        if (sum != 1.0d) {
            double w = 1.0 / ((children.size() / 2) + 1);
            for (MultiSplitLayout.Node node : children) {
                node.resetBounds();
                if (!(node instanceof MultiSplitLayout.Divider)) {
                    node.setWeight(w);
                }
            }
            multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
        }
    }

    protected void repaintMultiSplit() {
        SwingUtilities.invokeLater(repaintRunnable);
    }

    protected void forceWeight(List<MultiSplitLayout.Node> children) {
        double w = 1.0 / ((children.size() / 2) + 1);
        for (MultiSplitLayout.Node node : children) {
            node.resetBounds();
            if (!(node instanceof MultiSplitLayout.Divider)) {
                node.setWeight(w);
            }
        }
        multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
    }

    protected void resetBounds() {
        // Reset the model bounds...
        if (multiSplitPaneModelRoot == null)
            return;

        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push(multiSplitPaneModelRoot);
        multiSplitPaneModelRoot.resetBounds();
        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                child.resetBounds();

                if (child instanceof MultiSplitLayout.Split) {
                    stack.push((MultiSplitLayout.Split) child);
                }
            }
        }
    }

    protected boolean checkModel() {
        if (multiSplitPaneModelRoot == null)
            return true;
        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push(multiSplitPaneModelRoot);

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                if (child.getParent() == null || child.getParent() != split)
                    return false;
                if (child instanceof MultiSplitLayout.Split)
                    stack.push((MultiSplitLayout.Split) child);
            }
        }
        return true;
    }

    protected MultiSplitLayout.Split getFirstSplit(List<MultiSplitLayout.Node> children) {
        for (MultiSplitLayout.Node child : children) {
            if (child instanceof MultiSplitLayout.Split)
                return (MultiSplitLayout.Split) child;
        }
        return null;
    }

    protected String getLeafName(Dockable dockable) {
        if (multiSplitPaneModelRoot == null)
            return null;

        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push(multiSplitPaneModelRoot);

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                if (child instanceof DockableLeaf) {
                    DockableLeaf leaf = (DockableLeaf) child;

                    if (leaf.getDockables().contains(dockable.getId()))
                        return leaf.getName();
                } else if (child instanceof MultiSplitLayout.Split) {
                    stack.push((MultiSplitLayout.Split) child);
                }
            }
        }
        return null;
    }

    protected DockableLeaf getLeaf(Dockable dockable) {
        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push(multiSplitPaneModelRoot);

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                if (child instanceof DockableLeaf) {
                    DockableLeaf leaf = (DockableLeaf) child;

                    if (leaf.getDockables().contains(dockable.getId()))
                        return leaf;
                } else if (child instanceof MultiSplitLayout.Split) {
                    stack.push((MultiSplitLayout.Split) child);
                }
            }
        }
        return null;
    }


    public class DockableEntry {
        Dockable dockable;
        Component component;
        String id;

        DockableEntry(Dockable dockable, Component component, String id) {
            this.dockable = dockable;
            this.component = component;
            this.id = id;
        }

        public Dockable getDockable() {
            return dockable;
        }

        public Component getComponent() {
            return component;
        }

        public String getId() {
            return id;
        }
    }

    public static class DockableLeaf extends MultiSplitLayout.Leaf {
        private List<String> dockables;

        public DockableLeaf() {
        }

        public DockableLeaf(String name) {
            super(name);
            this.dockables = new ArrayList<String>();
        }

        public DockableLeaf(String name, String dockableId) {
            super(name);
            this.dockables = new ArrayList<String>();
            this.dockables.add(dockableId);
        }


        public String getDockable() {
            return dockables.get(0);
        }

        public List<String> getDockables() {
            return dockables;
        }

        public void setDockables(List<String> dockables) {
            this.dockables = dockables;
        }

        public void addDockable(String dockableId) {
            dockables.add(dockableId);
        }
    }

    public class RepaintRunnable implements Runnable {
        protected Exception exception;

        public RepaintRunnable() {
        }

        public RepaintRunnable(Exception exception) {
            this.exception = exception;
        }

        public void run() {
            checkModel();
            resetBounds();
            multiSplitPane.validate();
            multiSplitPane.getMultiSplitLayout().setFloatingDividers(false);
        }
    }
}