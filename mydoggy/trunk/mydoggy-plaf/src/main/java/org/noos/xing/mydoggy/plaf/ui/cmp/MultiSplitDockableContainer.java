package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitPane;
import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

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
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;
    protected Map<Dockable, DockableEntry> entries;
    protected int orientation;
    protected AggregationPosition defaultAggregationPosition;

    protected MultiSplitPane multiSplitPane;
    protected MultiSplitLayout.Split multiSplitPaneModelRoot;


    public MultiSplitDockableContainer(MyDoggyToolWindowManager toolWindowManager, int orientation) {
        this.orientation = orientation;
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();
        this.entries = new LinkedHashMap<Dockable, DockableEntry>();

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
     *
     * @param dockable
     * @param content
     * @param aggregationOnDockable
     * @param aggregationPosition
     */
    public void addContent(Dockable dockable,
                           Component content,
                           Dockable aggregationOnDockable,
                           AggregationPosition aggregationPosition) {

        String dockableId = dockable.getId();

        if (aggregationOnDockable == null)
            dockableId = dockableId + ((aggregationPosition != null) ? aggregationPosition.toString()  : "" );
        else
            dockableId = dockableId + aggregationOnDockable.getId() +
                         ((aggregationPosition != null) ? aggregationPosition.toString()  : "" );

        // Store old layout
        StringBuilder builder = new StringBuilder();
        for (DockableEntry entry : entries.values()) {
            builder.append(entry.id);
        }
        if (entries.size() > 0)
            models.put(builder.toString(), encode());

        builder.append(dockableId);

        if (entries.size() == 0) {
            resetRootComponent();
            setRootComponent(content);
        } else {
            byte[] oldModel = models.get(builder.toString());

            boolean invalidAggregationPosition = false;
            if (aggregationPosition == AggregationPosition.DEFAULT || aggregationPosition == null) {
                invalidAggregationPosition = true; 
                aggregationPosition = defaultAggregationPosition;
            }

            if (entries.size() == 1) {
                Component previousContent = getRootComponent();
                resetRootComponent();

                if (aggregationOnDockable != null) {
                    // TODO:

                } else {
                    DockableLeaf leaf;
                    DockableLeaf leaf2;

                    if (oldModel != null) {
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
                                if (child instanceof DockableLeaf) {
                                    DockableLeaf leaf = (DockableLeaf) child;

                                    if (aggregationOnDockable == toolWindowManager.getDockable(leaf.getDockable())) {
                                        if (invalidAggregationPosition) {
                                            // The requeste is to add more than one dockable on the same leaf...
                                            addToComponentWrapper(
                                                    multiSplitPane.getMultiSplitLayout().getChildMap().get(leaf.getName()),
                                                    dockable,
                                                    content
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
                                                newSplit.setChildren(Arrays.asList(leaf,
                                                                                   new MultiSplitLayout.Divider(),
                                                                                   newleaf));

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
                                    children.add(0, new DockableLeaf(leafName,dockable.getId()));
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

            repaintMultiSplit();
        }

        entries.put(dockable, new DockableEntry(dockable, content, dockableId));
    }

    public void removeContent(Dockable dockable) {
        DockableEntry dockableEntry = entries.get(dockable);
        if (dockableEntry == null)
            throw new IllegalArgumentException("Cannot remove that dockable. It's not present into the container.");

        // Store layout
        StringBuilder builder = new StringBuilder();
        for (DockableEntry entry : entries.values()) {
            builder.append(entry.id);
        }
        models.put(builder.toString(), encode());
        entries.remove(dockable);

        if (entries.size() == 0) {
            resetRootComponent();
            return;
        }

        if (entries.size() == 1) {
            // remove content
            String leafName = getLeafName(dockable);
            multiSplitPane.remove(multiSplitPane.getMultiSplitLayout().getChildMap().get(leafName));

            // obtain the component remained.
            Component c = getWrappedComponent((Container) multiSplitPane.getComponent(0));
            multiSplitPane.removeAll();

            setRootComponent(c);
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


    protected MultiSplitLayout.Split getFirstSplit(List<MultiSplitLayout.Node> children) {
        for (MultiSplitLayout.Node child : children) {
            if (child instanceof MultiSplitLayout.Split)
                return (MultiSplitLayout.Split) child;
        }
        return null;
    }


    protected Component getRootComponent() {
        return getComponent(0);
    }

    protected void setRootComponent(Component component) {
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

    protected void addToComponentWrapper(Component wrapperSource, Dockable dockable, Component content) {
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

    protected void repaintMultiSplit() {
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

    protected String getLeafName(Dockable dockable) {
        Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
        stack.push(multiSplitPaneModelRoot);

        while (!stack.isEmpty()) {
            MultiSplitLayout.Split split = stack.pop();

            for (MultiSplitLayout.Node child : split.getChildren()) {
                if (child instanceof DockableLeaf) {
                    DockableLeaf leaf = (DockableLeaf) child;

                    // TODO: a leaf can cotain more than a dockable... 
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

                    // TODO: a leaf can cotain more than a dockable...
                    if (leaf.getDockables().contains(dockable.getId()))
                        return leaf;
                } else if (child instanceof MultiSplitLayout.Split) {
                    stack.push((MultiSplitLayout.Split) child);
                }
            }
        }
        return null;
    }


    /**
     * TODO: introdure this class and remove contents and ids list...
     */
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
}