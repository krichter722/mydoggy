package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;
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
    protected MultiSplitLayout.Node multiSplitPaneModelRoot;

    protected Runnable repaintRunnable;

    protected boolean storeLayout;
    protected boolean useAlwaysContentWrapper;
    protected boolean jumpResetBounds;

    int counter = 2;

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

        MultiSplitLayout.Split split = new MultiSplitLayout.Split();
        split.setRowLayout(orientation != JSplitPane.VERTICAL_SPLIT);
        if (split.isRowLayout()) {
            defaultAggregationPosition = AggregationPosition.RIGHT;
        } else
            defaultAggregationPosition = AggregationPosition.BOTTOM;
        this.multiSplitPaneModelRoot = split;

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

        String modelId = idBuilder.toString();

        // Store old layout
        String modelKey = null;
        if (storeLayout && entries.size() > 0) {
            idBuilder.setLength(0);
            for (DockableEntry entry : entries.values()) {
                idBuilder.append(entry.id);
            }
            models.put(idBuilder.toString(), encode());
            idBuilder.append(modelId);
            modelKey = idBuilder.toString();
        }


        if (entries.size() == 0) {
            // Just one leaf
            DockableLeaf leaf = new DockableLeaf("1", dockable.getId());
            leaf.setWeight(1.0d);
            multiSplitPaneModelRoot = leaf;

            multiSplitPane.setModel(leaf);
            multiSplitPane.add((useAlwaysContentWrapper) ? getComponentWrapper(dockable, content) : content, "1");
            setRootComponent(multiSplitPane);
            SwingUtil.repaint(this);
        } else {
            byte[] oldModel = (modelKey != null) ? models.get(modelKey) : null;

            boolean invalidAggregationPosition = false;
            if (aggregationPosition == AggregationPosition.DEFAULT || aggregationPosition == null) {
                invalidAggregationPosition = true;
                aggregationPosition = defaultAggregationPosition;
            }

            if (multiSplitPaneModelRoot instanceof DockableLeaf) {
                DockableLeaf rootLeaf = (DockableLeaf) multiSplitPaneModelRoot;

                if (aggregationOnDockable != null && (aggregationPosition == null || invalidAggregationPosition)) {
                    Component componentWrapper;

                    if (rootLeaf.getDockables().size() > 1) {
                        componentWrapper = multiSplitPane.getComponent(0);
                    } else {
                        Component rootLeafCmp = (useAlwaysContentWrapper)
                                                ? getWrappedComponent((Container) multiSplitPane.getComponent(0))
                                                : multiSplitPane.getComponent(0);
                        multiSplitPane.removeAll();
                        componentWrapper = getComponentWrapper(entries.values().iterator().next().dockable, rootLeafCmp);
                        multiSplitPane.add(componentWrapper, rootLeaf.getName());
                    }

                    // The requeste is to add more than one dockable on the same leaf...
                    addToComponentWrapper(componentWrapper, dockable, aggregationIndexLocation, content);

                    repaintMultiSplit();

                    rootLeaf.addDockable(dockable.getId());
                } else {
                    if (storeLayout && oldModel != null) {
                        multiSplitPaneModelRoot = decode(oldModel);
                        multiSplitPaneModelRoot.setParent(null);
                        jumpResetBounds = true;
                    } else {
                        // Create two leafs
                        rootLeaf.setWeight(0.5);

                        DockableLeaf secondLeaf = new DockableLeaf("2");
                        secondLeaf.setWeight(0.5);
                        secondLeaf.addDockable(dockable.getId());

                        List<MultiSplitLayout.Node> children = null;
                        switch (aggregationPosition) {
                            case LEFT:
                            case TOP:
                                children = Arrays.asList(secondLeaf,
                                                         new MultiSplitLayout.Divider(),
                                                         rootLeaf);
                                break;
                            case RIGHT:
                            case BOTTOM:
                                children = Arrays.asList(rootLeaf,
                                                         new MultiSplitLayout.Divider(),
                                                         secondLeaf);
                                break;
                        }

                        boolean rowLayout = (aggregationPosition == AggregationPosition.LEFT || aggregationPosition == AggregationPosition.RIGHT);

                        MultiSplitLayout.Split split = new MultiSplitLayout.Split();
                        split.setRowLayout(rowLayout);
                        split.setChildren(children);
                        multiSplitPaneModelRoot = split;

                        if (!multiSplitPane.getMultiSplitLayout().getFloatingDividers())
                            multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
                    }

                    validateModel(multiSplitPaneModelRoot);
                    multiSplitPane.setModel(multiSplitPaneModelRoot);

                    Component componentWrapper;
                    if (rootLeaf.getDockables().size() > 1) {
                        componentWrapper = multiSplitPane.getComponent(0);
                    } else {
                        componentWrapper = (useAlwaysContentWrapper)
                                           ? multiSplitPane.getComponent(0)
                                           : getComponentWrapper(entries.values().iterator().next().dockable, multiSplitPane.getComponent(0));
                    }
                    multiSplitPane.removeAll();

                    multiSplitPane.add(componentWrapper, "1");
                    multiSplitPane.add(getComponentWrapper(dockable, content), "2");

                }
            } else {
                MultiSplitLayout.Split splitRoot = (MultiSplitLayout.Split) multiSplitPaneModelRoot;

                boolean addCmp = true;

                // Build content to add
//              TODO  String leafName = "" + (entries.size() + 1);
                String leafName = "" + (++counter);


                if (storeLayout && oldModel != null) {
                    multiSplitPaneModelRoot = decode(oldModel);
                    multiSplitPaneModelRoot.setParent(null);
                    jumpResetBounds = true;
                } else {
                    // Modify model

                    if (aggregationOnDockable != null) {

                        // Search for aggregationOnDockable leaf
                        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
                        stack.push(splitRoot);

                        while (!stack.isEmpty()) {
                            MultiSplitLayout.Split split = stack.pop();

                            for (MultiSplitLayout.Node child : split.getChildren()) {
                                if (child instanceof DockableLeaf) {
                                    DockableLeaf leaf = (DockableLeaf) child;

                                    if (leaf.getDockables().contains(aggregationOnDockable.getId())) {
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

                        if (splitRoot.isRowLayout() == rowLayout) {
                            List<MultiSplitLayout.Node> children = splitRoot.getChildren();

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

                            splitRoot.setChildren(children);
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

        entries.put(dockable, new DockableEntry(dockable, content, modelId));
    }

    public void removeDockable(Dockable dockable) {
        if (dockable == null)
            throw new IllegalArgumentException("Cannot remove dockable. [dockable null]");

        DockableEntry dockableEntry = entries.get(dockable);
        if (dockableEntry == null)
            throw new IllegalArgumentException("Cannot remove the dockable. No Entry for it. [id : "  + dockable.getId() + "]");

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
            counter = 2;
            return;
        }

        if (entries.size() == 1) {
            if (multiSplitPaneModelRoot instanceof DockableLeaf) {
                // TODO: ma quando accade???
                DockableLeaf leaf = (DockableLeaf) multiSplitPaneModelRoot;
                removeComponentWrapper(multiSplitPane.getComponent(0),
                                       dockable);
                leaf.getDockables().remove(dockable.getId());

                if (!useAlwaysContentWrapper) {
                    Component root = multiSplitPane.getComponent(0);
                    multiSplitPane.removeAll();
                    multiSplitPane.add(getWrappedComponent((Container) root), "1");
                }
            } else {
                // the root is a split

                // remove the component related to dockable
                multiSplitPane.remove(multiSplitPane.getMultiSplitLayout().getChildMap().get(getLeafName(dockable)));

                // retrieve the component related to sole entry in entries
                Dockable soleDockable = entries.keySet().iterator().next();
                DockableLeaf soleLeaf = getLeaf(soleDockable);
                Component soleLeafCmp = getWrappedComponent((Container) multiSplitPane.getMultiSplitLayout().getChildMap().get(soleLeaf.getName()));
                soleLeaf.setName("1");
                multiSplitPaneModelRoot = soleLeaf;
                multiSplitPaneModelRoot.setParent(null);
                multiSplitPane.setModel(multiSplitPaneModelRoot);

                multiSplitPane.removeAll();

                if (useAlwaysContentWrapper)
                    multiSplitPane.add(getComponentWrapper(soleDockable, soleLeafCmp), "1");
                else
                    multiSplitPane.add(soleLeafCmp, "1");
            }

            SwingUtil.repaint(this);
        } else {
            DockableLeaf dockableLeaf = getLeaf(dockable);
            if (dockableLeaf == null)
                throw new IllegalArgumentException("Cannot remove the dockable. Cannot find leaf. [id : "  + dockable.getId() + "]");

            if (dockableLeaf.getNameValue() == counter)
                counter--;

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
                    stack.push((MultiSplitLayout.Split) multiSplitPaneModelRoot);

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
                                            multiSplitPaneModelRoot = getFirstNotDivider(children);
                                            multiSplitPaneModelRoot.setParent(null);
                                            setChild = false;
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
            repaintMultiSplit();
        }
    }


    public MultiSplitLayout.Node getModel() {
        return multiSplitPaneModelRoot;
    }

    public void setModel(MultiSplitLayout.Node root) {
        if (root instanceof MultiSplitLayout.Leaf) {
            validateModel(root);

            multiSplitPaneModelRoot = root;
            multiSplitPaneModelRoot.setParent(null);
            multiSplitPane.setModel(multiSplitPaneModelRoot);

            repaintMultiSplit();
        } else if (root instanceof MultiSplitLayout.Split) {
            // TODO: support multi dockable leaf...

            // Scan model
            Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
            stack.push((MultiSplitLayout.Split) root);

            Map<String, Component> currentChildMap = multiSplitPane.getMultiSplitLayout().getChildMap();
            Map<String, Component> newChildMap = new HashMap<String, Component>();

            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                for (MultiSplitLayout.Node child : split.getChildren()) {
                    if (child instanceof DockableLeaf) {
                        DockableLeaf leaf = (DockableLeaf) child;

                        DockableLeaf currentLeaf = getLeaf(multiSplitPaneModelRoot, leaf.getDockable());

                        newChildMap.put(
                                currentLeaf.getName(),
                                currentChildMap.get(leaf.getName())
                        );
                    } else if (child instanceof MultiSplitLayout.Split) {
                        stack.push((MultiSplitLayout.Split) child);
                    }
                }
            }

            multiSplitPane.getMultiSplitLayout().setChildMap(newChildMap);

            validateModel(root);

            multiSplitPaneModelRoot = root;
            multiSplitPaneModelRoot.setParent(null);
            multiSplitPane.setModel(multiSplitPaneModelRoot);

            repaintMultiSplit();
        } else
            throw new RuntimeException("Invalid model. [model : " + root + "]");
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
                Component componentWrapper = getComponentWrapper(entries.keySet().iterator().next(),
                                                                 multiSplitPane.getComponent(0));
                multiSplitPane.removeAll();
                multiSplitPane.add(componentWrapper, "0");

                SwingUtil.repaint(this);
            }
        } else {
            if (entries.size() == 1) {
                Component wrappedComponent = getWrappedComponent((Container) multiSplitPane.getComponent(0));
                multiSplitPane.removeAll();
                multiSplitPane.add(wrappedComponent, "0");

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

    protected void validateModel(MultiSplitLayout.Node root) {
        if (root == null || !(root instanceof MultiSplitLayout.Split))
            return;

        List<MultiSplitLayout.Node> children = ((MultiSplitLayout.Split) root).getChildren();

        double sum = 0.0;
        for (MultiSplitLayout.Node node : children) {
            if (!(node instanceof MultiSplitLayout.Divider)) {
                sum += node.getWeight();
            }

            if (node instanceof MultiSplitLayout.Split) {
                validateModel(node);
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
        if (multiSplitPaneModelRoot == null || !(multiSplitPaneModelRoot instanceof MultiSplitLayout.Split))
            return;

        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push((MultiSplitLayout.Split) multiSplitPaneModelRoot);
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
        if (multiSplitPaneModelRoot == null || !(multiSplitPaneModelRoot instanceof MultiSplitLayout.Split))
            return true;

        if (multiSplitPaneModelRoot.getParent() != null)
            return false;

        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push((MultiSplitLayout.Split) multiSplitPaneModelRoot);

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

    protected void repaintMultiSplit() {
//        SwingUtilities.invokeLater(repaintRunnable);
        SwingUtilities.invokeLater(new DebugRepaintRunnable(new RuntimeException()));
    }

    protected MultiSplitLayout.Node getFirstNotDivider(List<MultiSplitLayout.Node> children) {
        for (MultiSplitLayout.Node child : children) {
            if (!(child instanceof MultiSplitLayout.Divider))
                return child;
        }
        return null;
    }

    protected String getLeafName(Dockable dockable) {
        DockableLeaf leaf = getLeaf(multiSplitPaneModelRoot, dockable.getId());
        return (leaf != null) ? leaf.getName() : null;
    }

    protected DockableLeaf getLeaf(Dockable dockable) {
        return getLeaf(multiSplitPaneModelRoot, dockable.getId());
    }

    protected DockableLeaf getLeaf(MultiSplitLayout.Node root, String dockableId) {
        if (root == null || !(root instanceof MultiSplitLayout.Split)) {
            if (root instanceof MultiSplitLayout.Leaf) {
                DockableLeaf leaf = (DockableLeaf) root;
                if (leaf.getDockables().contains(dockableId))
                    return leaf;
            }
            return null;
        }

        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push((MultiSplitLayout.Split) root);

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                if (child instanceof DockableLeaf) {
                    DockableLeaf leaf = (DockableLeaf) child;

                    if (leaf.getDockables().contains(dockableId))
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

        public int getNameValue() {
            return Integer.parseInt(getName());
        }
    }

    public class RepaintRunnable implements Runnable {

        public RepaintRunnable() {
        }

        public void run() {
            checkModel();
            if (jumpResetBounds) {
                jumpResetBounds = false;
            } else
                resetBounds();

            multiSplitPane.invalidate();
            multiSplitPane.validate();
            multiSplitPane.repaint();
            multiSplitPane.getMultiSplitLayout().setFloatingDividers(false);
        }
    }

    public class DebugRepaintRunnable implements Runnable {
        protected Exception exception;

        public DebugRepaintRunnable(Exception exception) {
            this.exception = exception;
        }

        public void run() {
//            exception.printStackTrace();
            try {
                checkModel();
                if (jumpResetBounds) {
                    jumpResetBounds = false;
                } else
                    resetBounds();

                multiSplitPane.invalidate();
                multiSplitPane.validate();
                multiSplitPane.repaint();
                multiSplitPane.getMultiSplitLayout().setFloatingDividers(false);
            } catch (RuntimeException e) {
                System.out.println("-----------------------------------------------------------------------");
                exception.printStackTrace();
                System.out.println("-----------------------------------------------------------------------");
                throw e;
            }
        }
    }

}