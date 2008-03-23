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
    public enum Action {
        ADD_DOCK,
        REMOVE_DOCK
    }

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

    protected int leafNameCounter = 0;

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

        if (orientation != JSplitPane.VERTICAL_SPLIT) {
            defaultAggregationPosition = AggregationPosition.RIGHT;
        } else
            defaultAggregationPosition = AggregationPosition.BOTTOM;
        this.multiSplitPaneModelRoot = null;

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
            DockableLeaf leaf = new DockableLeaf(getNextLeanName(),
                                                 dockable.getId());
            leaf.setWeight(1.0d);
            multiSplitPaneModelRoot = leaf;

            multiSplitPane.setModel(leaf);
            multiSplitPane.add(getWrapperForComponent(dockable, content, Action.ADD_DOCK), "1");
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
                // The root is a leaf...
                DockableLeaf rootLeaf = (DockableLeaf) multiSplitPaneModelRoot;

                if (aggregationOnDockable != null && (aggregationPosition == null || invalidAggregationPosition)) {
                    // Aggregate to an already registered leaf
                    Component componentWrapper;

                    if (rootLeaf.getDockables().size() > 1) {
                        componentWrapper = multiSplitPane.getComponent(0);
                    } else {
                        Component rootLeafCmp = getComponentFromWrapper(multiSplitPane.getComponent(0));
                        multiSplitPane.removeAll();
                        componentWrapper = forceWrapperForComponent(entries.values().iterator().next().dockable, rootLeafCmp);
                        multiSplitPane.add(componentWrapper, rootLeaf.getName());
                    }

                    // The requeste is to add more than one dockable on the same leaf... no need to request a new leaf name
                    addToWrapper(componentWrapper, dockable, aggregationIndexLocation, content);

                    repaintMultiSplit();

                    rootLeaf.addDockable(dockable.getId());
                } else {
                    // Create a new leaf...

                    DockableLeaf firstLeaf = rootLeaf;
                    DockableLeaf secondLeaf = null;

                    // Init two leafs
                    firstLeaf.setWeight(0.5);

                    secondLeaf = new DockableLeaf(getNextLeanName());
                    secondLeaf.setWeight(0.5);
                    secondLeaf.addDockable(dockable.getId());

                    List<MultiSplitLayout.Node> children = null;
                    switch (aggregationPosition) {
                        case LEFT:
                        case TOP:
                            children = Arrays.asList(secondLeaf,
                                                     new MultiSplitLayout.Divider(),
                                                     firstLeaf);
                            break;
                        case RIGHT:
                        case BOTTOM:
                            children = Arrays.asList(firstLeaf,
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

                    validateModel(multiSplitPaneModelRoot);
                    multiSplitPane.setModel(multiSplitPaneModelRoot);

                    Component wrapper;
                    if (rootLeaf.getDockables().size() > 1) {
                        wrapper = multiSplitPane.getComponent(0);
                    } else {
                        Dockable delegator = entries.values().iterator().next().dockable;
                        wrapper = getWrapperForComponent(delegator, getComponentFromWrapper(multiSplitPane.getComponent(0)), Action.ADD_DOCK);
                    }
                    multiSplitPane.removeAll();

                    multiSplitPane.add(wrapper, firstLeaf.getName());
                    multiSplitPane.add(getWrapperForComponent(dockable, content, Action.ADD_DOCK), secondLeaf.getName());

                }
            } else {
                // Take the root, it's a split
                MultiSplitLayout.Split splitRoot = (MultiSplitLayout.Split) multiSplitPaneModelRoot;

                // Build content to add
                boolean addCmp = true;
                String leafName = null;

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
                                        addToWrapper(multiSplitPane.getMultiSplitLayout().getChildMap().get(leaf.getName()),
                                                     dockable,
                                                     aggregationIndexLocation,
                                                     content);

                                        leaf.addDockable(dockable.getId());

                                        addCmp = false;
                                    } else {
                                        leafName = getNextLeanName();
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
                    leafName = getNextLeanName();
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
//                }

                validateModel(multiSplitPaneModelRoot);
                multiSplitPane.setModel(multiSplitPaneModelRoot);

                if (addCmp)
                    multiSplitPane.add(getWrapperForComponent(dockable, content, Action.ADD_DOCK), leafName);
            }

            if (!checkModel())
                System.out.println("Check model fail. addDockable end");

            if (storeLayout && oldModel != null) {
                // Decode stored model
                MultiSplitLayout.Node decodedModel = decode(oldModel);
                jumpResetBounds = true;
                multiSplitPane.getMultiSplitLayout().setFloatingDividers(false);

                setModel(decodedModel);
            } else
                repaintMultiSplit();
        }

        entries.put(dockable, new DockableEntry(dockable, content, modelId));
    }

    public void addDockable(Dockable dockable, Component component, Constraint constraint) {
        if (constraint == null)
            addDockable(dockable, component, null, -1, AggregationPosition.DEFAULT);
        else {
            if (constraint.getNode() == null)
                addDockable(dockable, component, null, constraint.getIndex(), constraint.getAggregationPosition());
            else {
                if (constraint.getNode() instanceof DockableLeaf) {
                    DockableLeaf leaf = (DockableLeaf) constraint.getNode();

                    for (String leafDockableId : leaf.getDockables()) {
                        Dockable leafDockable = toolWindowManager.getDockable(leafDockableId);
                        if (leafDockable != null && !leafDockable.isDetached()) {
                            addDockable(dockable, component, leafDockable, constraint.getIndex(), constraint.getAggregationPosition());
                            return;
                        }
                    }

                    addDockable(dockable, component, null, constraint.getIndex(), constraint.getAggregationPosition());
                } else if (constraint.getNode() instanceof MultiSplitLayout.Split) {
                    MultiSplitLayout.Split split = (MultiSplitLayout.Split) constraint.getNode();

                    if (multiSplitPaneModelRoot == split)
                        addDockable(dockable, component, null, constraint.getIndex(), constraint.getAggregationPosition());
                    else {
                        MultiSplitLayout.Split parent = split.getParent();
                        if (!isNodeAttached(split) || parent == null) {
                            addDockable(dockable, component, null, constraint.getIndex(), constraint.getAggregationPosition());
                        } else {
                            AggregationPosition aggregationPosition = constraint.getAggregationPosition();
                            switch (aggregationPosition) {
                                case TOP:
                                case LEFT:
                                    List<MultiSplitLayout.Node> nodes = parent.getChildren();
                                    DockableLeaf dockableLeaf = new DockableLeaf(getNextLeanName(), dockable.getId());
                                    dockableLeaf.setWeight(0.5d);

                                    int index = nodes.indexOf(split);
                                    nodes.add(index, dockableLeaf);
                                    nodes.add(index + 1, new MultiSplitLayout.Divider());

                                    parent.setChildren(nodes);

                                    multiSplitPane.add(getWrapperForComponent(dockable, component, Action.ADD_DOCK),
                                                       dockableLeaf.getName());
                                    break;
                                case BOTTOM:
                                case RIGHT:
                                    nodes = parent.getChildren();
                                    dockableLeaf = new DockableLeaf(getNextLeanName(), dockable.getId());
                                    dockableLeaf.setWeight(0.5d);

                                    index = nodes.indexOf(split);
                                    nodes.add(index, dockableLeaf);
                                    nodes.add(index - 1, new MultiSplitLayout.Divider());

                                    parent.setChildren(nodes);

                                    multiSplitPane.add(getWrapperForComponent(dockable, component, Action.ADD_DOCK),
                                                       dockableLeaf.getName());
                                    break;
                            }
                            entries.put(dockable, new DockableEntry(dockable, component, null));

                            validateModel(multiSplitPaneModelRoot);
                            repaintMultiSplit();
                        }
                    }

                } else
                    addDockable(dockable, component, null, constraint.getIndex(), constraint.getAggregationPosition());
            }
        }
    }

    public Constraint removeDockable(Dockable dockable) {
        if (dockable == null)
            throw new IllegalArgumentException("Cannot remove dockable. [dockable null]");

        DockableEntry dockableEntry = entries.get(dockable);
        if (dockableEntry == null)
            throw new IllegalArgumentException("Cannot remove the dockable. No Entry for it. [id : " + dockable.getId() + "]");

        // Store layout
        if (storeLayout) {
            StringBuilder builder = new StringBuilder();
            for (DockableEntry entry : entries.values()) {
                builder.append(entry.id);
            }
//            MultiSplitLayout.printModel(multiSplitPaneModelRoot);
            models.put(builder.toString(), encode());
        }
        entries.remove(dockable);

        if (entries.size() == 0) {
            multiSplitPaneModelRoot = null;
            multiSplitPane.removeAll();

            resetRootComponent();
            leafNameCounter = 0;
            SwingUtil.repaint(this);

            // No constraint for the content because it was alone on the container
            return null;
        }

        if (entries.size() == 1) {
            if (multiSplitPaneModelRoot instanceof DockableLeaf) {
                DockableLeaf leaf = (DockableLeaf) multiSplitPaneModelRoot;
                int index = removeFromWrapper(multiSplitPane.getComponent(0), dockable);
                leaf.getDockables().remove(dockable.getId());

                if (!useAlwaysContentWrapper) {
                    Component root = multiSplitPane.getComponent(0);
                    multiSplitPane.removeAll();
                    multiSplitPane.add(getComponentFromWrapper(root), "1");
                }

                return new Constraint(leaf,
                                      AggregationPosition.DEFAULT,
                                      index);
            } else {
                // the root is a split

                // remove the component related to dockable
                multiSplitPane.remove(multiSplitPane.getMultiSplitLayout().getChildMap().get(getLeafName(dockable)));

                // retrieve the component related to sole entry in entries
                Dockable leftDockable = entries.keySet().iterator().next();
                DockableLeaf leftLeaf = getLeaf(leftDockable);
                Component LeftLeafCmp = getComponentFromWrapper(multiSplitPane.getMultiSplitLayout().getChildMap().get(leftLeaf.getName()));
                leftLeaf.setName("1");

                // Update the model
                AggregationPosition constraintAggPosition = AggregationPosition.DEFAULT;
                MultiSplitLayout.Split rootSplit = (MultiSplitLayout.Split) multiSplitPaneModelRoot;
                List<MultiSplitLayout.Node> children = rootSplit.getChildren();
                for (int i = 0, size = children.size(); i < size; i++) {
                    MultiSplitLayout.Node node = children.get(i);
                    if (node == leftLeaf) {
                        if (i == 0) {
                            if (rootSplit.isRowLayout())
                                constraintAggPosition = AggregationPosition.RIGHT;
                            else
                                constraintAggPosition = AggregationPosition.BOTTOM;
                        } else if (rootSplit.isRowLayout())
                            constraintAggPosition = AggregationPosition.LEFT;
                        else
                            constraintAggPosition = AggregationPosition.TOP;
                        break;
                    }
                }


                multiSplitPaneModelRoot = leftLeaf;
                multiSplitPaneModelRoot.setParent(null);

                // Update the pane
                multiSplitPane.setModel(multiSplitPaneModelRoot);
                multiSplitPane.removeAll();
                multiSplitPane.add(getWrapperForComponent(leftDockable, LeftLeafCmp, Action.REMOVE_DOCK), "1");

                // Finalize
                leafNameCounter = 1;
                SwingUtil.repaint(this);

                // Prepare constraint
                return new Constraint(leftLeaf,
                                      constraintAggPosition,
                                      -1);
            }
        } else {
            Constraint constraint = null;

            DockableLeaf dockableLeaf = getLeaf(dockable);
            if (dockableLeaf == null)
                throw new IllegalArgumentException("Cannot remove the dockable. Cannot find leaf. [id : " + dockable.getId() + "]");

            if (dockableLeaf.getDockables().size() > 1) {
                // There are more than one dockable on the same leaf
                // Remove the dockable from leaf and from aggregating component...
                dockableLeaf.getDockables().remove(dockable.getId());

                int index = removeFromWrapper(multiSplitPane.getMultiSplitLayout().getChildMap().get(dockableLeaf.getName()),
                                              dockable);

                // Prepare constraint
                return new Constraint(dockableLeaf,
                                      AggregationPosition.DEFAULT,
                                      index);
            } else {
                leafNameCounter--;

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

                                            // Prepare constraint
                                            AggregationPosition position;
                                            if (children.get(0) == multiSplitPaneModelRoot) {
                                                position = (split.isRowLayout()) ? AggregationPosition.RIGHT : AggregationPosition.BOTTOM;
                                            } else
                                                position = (split.isRowLayout()) ? AggregationPosition.LEFT : AggregationPosition.TOP;
                                            constraint = new Constraint(multiSplitPaneModelRoot,
                                                                        position,
                                                                        -1);

                                            setChild = false;
                                        } else {
                                            List<MultiSplitLayout.Node> grenpaChildren = grandpa.getChildren();

                                            if (children.get(0) instanceof MultiSplitLayout.Divider) {
                                                grenpaChildren.set(grenpaChildren.indexOf(split),
                                                                   children.get(1));

                                                // Prepare constraint
                                                constraint = new Constraint(children.get(1),
                                                                            (split.isRowLayout()) ? AggregationPosition.LEFT : AggregationPosition.TOP,
                                                                            -1);
                                            } else {
                                                grenpaChildren.set(grenpaChildren.indexOf(split),
                                                                   children.get(0));

                                                // Prepare constraint
                                                constraint = new Constraint(children.get(0),
                                                                            (split.isRowLayout()) ? AggregationPosition.RIGHT : AggregationPosition.BOTTOM,
                                                                            -1);
                                            }
                                            grandpa.setChildren(grenpaChildren);
                                            setChild = false;
                                        }
                                    } else {
                                        // Remove the divider
                                        if (i < children.size()) {
                                            children.remove(i);
                                            constraint = new Constraint(children.get(i),
                                                                        (split.isRowLayout()) ? AggregationPosition.LEFT : AggregationPosition.TOP,
                                                                        -1);
                                        } else {
                                            children.remove(i - 1);
                                            constraint = new Constraint(children.get(i - 2),
                                                                        (split.isRowLayout()) ? AggregationPosition.RIGHT : AggregationPosition.BOTTOM,
                                                                        -1);
                                        }
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

            return constraint;
        }
    }

    public void removeDockable(Dockable dockable, boolean storeLayout) {
        boolean old = this.storeLayout;
        this.storeLayout = storeLayout;
        try {
            removeDockable(dockable);
        } finally {
            this.storeLayout = old;
        }
    }

    public void setConstraints(Dockable dockable,
                               Component content,
                               Dockable aggregationOnDockable,
                               int aggregationIndexLocation,
                               AggregationPosition aggregationPosition) {
        boolean old = this.storeLayout;
        storeLayout = false;
        try {
            removeDockable(dockable);
            addDockable(dockable, content, aggregationOnDockable, aggregationIndexLocation, aggregationPosition);
        } finally {
            storeLayout = old;
        }
    }


    public MultiSplitLayout.Node getModel() {
        return multiSplitPaneModelRoot;
    }

    public void setModel(MultiSplitLayout.Node root) {
        if (root == null)
            return;
        
        // Check for contents root

        if (root instanceof DockableLeaf) {
            DockableLeaf dockableLeaf = (DockableLeaf) root;
            if (!containsDockable(dockableLeaf) || dockableLeaf.getDockables().size() != entries.size())
                return;
        } else if (root instanceof MultiSplitLayout.Split) {
            Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
            stack.push((MultiSplitLayout.Split) root);
            int dockCounter = 0;

            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                for (MultiSplitLayout.Node child : split.getChildren()) {

                    if (child instanceof MultiSplitLayout.Split) {
                        stack.push((MultiSplitLayout.Split) child);
                    } else if (child instanceof DockableLeaf) {
                        DockableLeaf dockableLeaf = (DockableLeaf) child;
                        if (!containsDockable(dockableLeaf))
                            return;
                        dockCounter += dockableLeaf.getDockables().size();
                    }
                }
            }

            if (dockCounter != entries.size())
                return;
        } else
            throw new RuntimeException("Invalid model. [model : " + root + "]");

        // Mount new root
        Map<String, Component> currentChildMap = multiSplitPane.getMultiSplitLayout().getChildMap();

        // First Step: disaggregate...
        if (multiSplitPaneModelRoot instanceof DockableLeaf) {
            DockableLeaf leaf = (DockableLeaf) multiSplitPaneModelRoot;

            String[] dockIds = leaf.getDockables().toArray(new String[leaf.getDockables().size()]);
            for (String dockId : dockIds) {

                if (leaf.getDockables().size() == 1)
                    break;

                Component component = currentChildMap.get(leaf.getName());

                Dockable dockable = toolWindowManager.getDockable(dockId);
                setConstraints(dockable,
                               getComponentFromWrapper(component, dockable),
                               null,
                               -1,
                               AggregationPosition.DEFAULT
                );
            }
        } else {
            Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
            stack.push((MultiSplitLayout.Split) multiSplitPaneModelRoot);
            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                for (MultiSplitLayout.Node child : split.getChildren()) {
                    if (child instanceof DockableLeaf) {
                        DockableLeaf leaf = (DockableLeaf) child;

                        String[] dockIds = leaf.getDockables().toArray(new String[leaf.getDockables().size()]);
                        for (String dockId : dockIds) {

                            if (leaf.getDockables().size() == 1)
                                break;

                            Component component = currentChildMap.get(leaf.getName());

                            Dockable dockable = toolWindowManager.getDockable(dockId);
                            setConstraints(dockable,
                                           getComponentFromWrapper(component, dockable),
                                           null,
                                           -1,
                                           AggregationPosition.DEFAULT
                            );
                        }
                    } else if (child instanceof MultiSplitLayout.Split) {
                        stack.push((MultiSplitLayout.Split) child);
                    }
                }
            }
        }

        if (root instanceof MultiSplitLayout.Split) {
            // Step Two: apply model 1...Aggregate
            Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
            stack.push((MultiSplitLayout.Split) root);
            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                for (MultiSplitLayout.Node child : split.getChildren()) {
                    if (child instanceof DockableLeaf) {
                        DockableLeaf leaf = (DockableLeaf) child;

                        List<String> dockIds = leaf.getDockables();
                        Dockable masterDockable = toolWindowManager.getDockable(leaf.getDockable());
                        for (int i = 1; i < dockIds.size(); i++) {
                            String dockId = dockIds.get(i);

                            Dockable dockable = toolWindowManager.getDockable(dockId);
                            setConstraints(dockable,
                                           getComponentFromWrapper(currentChildMap.get(getLeaf(dockable).getName()), dockable),
                                           masterDockable,
                                           -1,
                                           AggregationPosition.DEFAULT
                            );
                        }
                    } else if (child instanceof MultiSplitLayout.Split) {
                        stack.push((MultiSplitLayout.Split) child);
                    }
                }
            }
            // Step Two: apply model 2...Isomorphing
            Map<String, Component> newChildMap = new HashMap<String, Component>();
            stack = new Stack<MultiSplitLayout.Split>();
            stack.push((MultiSplitLayout.Split) root);
            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                for (MultiSplitLayout.Node child : split.getChildren()) {
                    if (child instanceof DockableLeaf) {
                        DockableLeaf leaf = (DockableLeaf) child;

                        DockableLeaf currentLeaf = getLeaf(multiSplitPaneModelRoot, leaf.getDockable());

                        newChildMap.put(
                                leaf.getName(),
                                currentChildMap.get(currentLeaf.getName())
                        );
                    } else if (child instanceof MultiSplitLayout.Split) {
                        stack.push((MultiSplitLayout.Split) child);
                    }
                }
            }

            multiSplitPane.getMultiSplitLayout().setChildMap(newChildMap);

        } else {
            DockableLeaf leaf = (DockableLeaf) root;

            List<String> dockIds = leaf.getDockables();
            Dockable masterDockable = toolWindowManager.getDockable(leaf.getDockable());
            for (int i = 1; i < dockIds.size(); i++) {
                String dockId = dockIds.get(i);

                Dockable dockable = toolWindowManager.getDockable(dockId);
                setConstraints(dockable,
                               getComponentFromWrapper(currentChildMap.get(getLeaf(dockable).getName()), dockable),
                               masterDockable,
                               -1,
                               AggregationPosition.DEFAULT
                );
            }
        }

        validateModel(root);

        multiSplitPaneModelRoot = root;
        multiSplitPaneModelRoot.setParent(null);
        multiSplitPane.setModel(multiSplitPaneModelRoot);

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
                Component componentWrapper = getWrapperForComponent(entries.keySet().iterator().next(),
                                                                    multiSplitPane.getComponent(0),
                                                                    Action.REMOVE_DOCK);
                multiSplitPane.removeAll();
                multiSplitPane.add(componentWrapper, "1");

                SwingUtil.repaint(this);
            }
        } else {
            if (entries.size() == 1) {
                Component wrappedComponent = getComponentFromWrapper(multiSplitPane.getComponent(0));
                multiSplitPane.removeAll();
                multiSplitPane.add(wrappedComponent, "1");

                SwingUtil.repaint(this);
            }
        }
    }

    public Rectangle getBoundsRelativeToScreen(Dockable dockable) {
        DockableLeaf leaf = getLeaf(dockable);
        if (leaf != null) {
            Rectangle bounds = leaf.getBounds();
            Point location = bounds.getLocation();
            SwingUtilities.convertPointToScreen(location, this);
            bounds.setLocation(location);
            bounds.y+= toolWindowManager.getJMenuBarExtraHeight();

            return bounds;
        }
        
        return null;
    }


    // Methods to manage container root

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


    // Methods to manage wrappers

    protected Component getWrapperForComponent(Dockable dockable, Component component, Action action) {
        return new DockablePanel(dockable, component);
    }

    protected Component forceWrapperForComponent(Dockable dockable, Component component) {
        return new DockablePanel(dockable, component);
    }

    protected Component getComponentFromWrapper(Component wrapper) {
        if (wrapper instanceof DockablePanel)
            return ((DockablePanel) wrapper).getComponent(0);
        return wrapper;
    }

    protected void addToWrapper(Component wrapperSource, Dockable dockable, int aggregationIndexLocation, Component content) {
        throw new IllegalStateException("Cannot call this method...");
    }

    protected int removeFromWrapper(Component wrapperSource, Dockable dockable) {
        throw new IllegalStateException("Cannot call this method...");
    }

    protected Component getComponentFromWrapper(Component wrapper, Dockable dockable) {
        throw new IllegalStateException("Cannot call this method...");
    }

    protected boolean isWrapRequest(Dockable dockable, Action action) {
        return useAlwaysContentWrapper;
    }


    // Model related methods

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
        if (multiSplitPaneModelRoot == null)
            return true;

        if (multiSplitPaneModelRoot.getParent() != null)
            return false;

        if (!(multiSplitPaneModelRoot instanceof MultiSplitLayout.Split))
            return true;

        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push((MultiSplitLayout.Split) multiSplitPaneModelRoot);

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            if (split.getChildren().size() <= 2)
                return false;

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
        SwingUtilities.invokeLater(repaintRunnable);
//        SwingUtilities.invokeLater(new DebugRepaintRunnable(new RuntimeException()));
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

    protected String getNextLeanName() {
        return "" + (++leafNameCounter);
    }

    protected boolean isNodeAttached(MultiSplitLayout.Node node) {
        if (node == null)
            return false;

        if (multiSplitPaneModelRoot == node)
            return true;
        else if (multiSplitPaneModelRoot instanceof MultiSplitLayout.Split) {
            Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
            stack.push((MultiSplitLayout.Split) multiSplitPaneModelRoot);

            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                if (split.getChildren().size() <= 2)
                    return false;

                for (MultiSplitLayout.Node child : split.getChildren()) {
                    if (child == node)
                        return true;

                    if (child instanceof MultiSplitLayout.Split)
                        stack.push(split);
                }
            }
        }
        return false;
    }

    protected boolean containsDockable(DockableLeaf leaf) {
        for (String dockId : leaf.getDockables()) {
            boolean found = false;
            for (Dockable dockable : entries.keySet()) {
                if (dockable.getId().equals(dockId)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;

        }
        return true;
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

    public class Constraint {
        private MultiSplitLayout.Node node;
        private AggregationPosition aggregationPosition;
        private int index = -1;

        public Constraint(MultiSplitLayout.Node node, AggregationPosition aggregationPosition, int index) {
            this.node = node;
            this.aggregationPosition = aggregationPosition;
            this.index = index;
        }

        public MultiSplitLayout.Node getNode() {
            return node;
        }

        public AggregationPosition getAggregationPosition() {
            return aggregationPosition;
        }

        public int getIndex() {
            return index;
        }
    }

}