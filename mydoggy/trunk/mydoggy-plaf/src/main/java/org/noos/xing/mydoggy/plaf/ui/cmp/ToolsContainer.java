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
 * @todo add store capabilities...
 */
public class ToolsContainer extends JPanel implements PropertyChangeListener {
    protected Map<String, byte[]> models;
    protected ToolWindowManager toolWindowManager;
    protected ResourceManager resourceManager;
    protected List<Component> contents;
    protected List<String> toolIds;
    protected int orientation;
    protected ToolWindow.Where defaultWhere;

    protected MultiSplitPane multiSplitPane;
    protected MultiSplitLayout.Split multiSplitPaneModelRoot;


    public ToolsContainer(MyDoggyToolWindowManager toolWindowManager, int orientation) {
        this.orientation = orientation;
        this.contents = new ArrayList<Component>();
        this.toolIds = new ArrayList<String>();
        this.toolWindowManager = toolWindowManager;
        this.resourceManager = toolWindowManager.getResourceManager();

        this.multiSplitPane = new MultiSplitPane();
        this.multiSplitPane.setFocusable(false);
        this.multiSplitPaneModelRoot = new MultiSplitLayout.Split();
        this.multiSplitPaneModelRoot.setRowLayout(orientation != JSplitPane.VERTICAL_SPLIT);
        if (multiSplitPaneModelRoot.isRowLayout()) {
            defaultWhere = ToolWindow.Where.RIGHT;
        } else
            defaultWhere = ToolWindow.Where.BOTTOM;
        this.models = new Hashtable<String, byte[]>();

        setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
    }


    public void propertyChange(PropertyChangeEvent evt) {
    }


    public void addContent(String toolId, Component content, ToolWindow.Where where) {
        ToolWindow toolWindow = (ToolWindow) ((JComponent) content).getClientProperty(ToolWindow.class);
        if (toolWindow != null)
            toolId = toolWindow.getId();

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

            if (where == ToolWindow.Where.DEFAULT)
                where = defaultWhere;

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

                    boolean rowLayout = (where == ToolWindow.Where.LEFT || where == ToolWindow.Where.RIGHT);

                    multiSplitPaneModelRoot = new MultiSplitLayout.Split();
                    multiSplitPaneModelRoot.setRowLayout(rowLayout);
                    multiSplitPaneModelRoot.setChildren(children);
                    if (!multiSplitPane.getMultiSplitLayout().getFloatingDividers())
                        multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
                }

                multiSplitPane.getMultiSplitLayout().setModel(multiSplitPaneModelRoot);

                switch (where) {
                    case LEFT:
                    case TOP:
                        multiSplitPane.add(getComponentWrapper(content), "1");
                        multiSplitPane.add(getComponentWrapper(content), "2");
                        break;
                    case RIGHT:
                    case BOTTOM:
                        multiSplitPane.add(getComponentWrapper(content), "1");
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

                    boolean rowLayout = (where == ToolWindow.Where.LEFT || where == ToolWindow.Where.RIGHT);

                    if (multiSplitPaneModelRoot.isRowLayout() == rowLayout) {
                        List<MultiSplitLayout.Node> children = multiSplitPaneModelRoot.getChildren();

                        switch (where) {
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

                        double w = 1.0 / ((children.size() / 2) + 1);
                        for (MultiSplitLayout.Node child : children) {
                            if (!(child instanceof MultiSplitLayout.Divider)) {
                                child.setBounds(new Rectangle());
                                child.setWeight(w);
                            }
                        }

                        multiSplitPaneModelRoot.setChildren(children);
                    } else {
                        MultiSplitLayout.Split newRoot = new MultiSplitLayout.Split();
                        newRoot.setRowLayout(rowLayout);

                        MultiSplitLayout.Leaf leaf = new MultiSplitLayout.Leaf(leafName);
                        leaf.setWeight(0.5);
                        multiSplitPaneModelRoot.setWeight(0.5);

                        List<MultiSplitLayout.Node> children = null;
                        switch (where) {
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
                        newRoot.setChildren(children);

                        multiSplitPaneModelRoot = newRoot;
                    }
                    if (!multiSplitPane.getMultiSplitLayout().getFloatingDividers())
                        multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
                }

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

            // TODO: complete remove

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


                    // TODO: recompute weight
                    double w = 1.0 / ((children.size() / 2) + 1);
                    for (MultiSplitLayout.Node child : children) {
                        if (!(child instanceof MultiSplitLayout.Divider)) {
                            child.setBounds(new Rectangle());
                            child.setWeight(w);
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


                multiSplitPane.setModel(multiSplitPaneModelRoot);
                multiSplitPane.revalidate();
                repaintSplit();
            }

        }
    }

    public void setComponentAt(String toolId, Component content, int index) {
        if (index >= contents.size())
            index = contents.size() - 1;

        if (contents.size() == 0)
            addContent(toolId, content, ToolWindow.Where.DEFAULT);
        else if (contents.size() == 1) {
            removeAll();
            if (contents.contains(content))
                return;

            add(content, "0,0");
        } else {
            String leafName = "" + (index + 1);
            Component c = multiSplitPane.getMultiSplitLayout().getChildMap().get(leafName);
            if (c != null)
                multiSplitPane.remove(c);

            multiSplitPane.add(getComponentWrapper(content), leafName);
        }


        if (multiSplitPaneModelRoot != null)  {
            // Update model
            Stack<MultiSplitLayout.Split> stack = new Stack<MultiSplitLayout.Split>();
            stack.push(multiSplitPaneModelRoot);

            while (!stack.isEmpty()) {
                MultiSplitLayout.Split split = stack.pop();

                List<MultiSplitLayout.Node> children = split.getChildren();

                double w = 1.0 / ((children.size() / 2) + 1);
                for (int i = 0; i < children.size(); i++) {
                    MultiSplitLayout.Node child = children.get(i);

                    if (child instanceof MultiSplitLayout.Leaf) {
                        child.setBounds(new Rectangle());
                        child.setWeight(w);
                    } else if (child instanceof MultiSplitLayout.Split) {
                        stack.push((MultiSplitLayout.Split) child);
                    }
                }
                
                split.setChildren(children);
            }
            multiSplitPane.getMultiSplitLayout().setFloatingDividers(true);
            multiSplitPane.setModel(multiSplitPaneModelRoot);
            repaintSplit();
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                multiSplitPane.invalidate();
                multiSplitPane.validate();
                multiSplitPane.repaint();
                multiSplitPane.getMultiSplitLayout().setFloatingDividers(false);
            }
        });
    }
}