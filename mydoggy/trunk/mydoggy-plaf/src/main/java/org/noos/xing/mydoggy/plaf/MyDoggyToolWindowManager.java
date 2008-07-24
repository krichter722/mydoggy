package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import org.noos.common.context.MutableContext;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.mydoggy.plaf.common.context.DefaultMutableContext;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultDockedTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultFloatingLiveTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultFloatingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultSlidingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.persistence.xml.XMLPersistenceDelegate;
import org.noos.xing.mydoggy.plaf.support.CleanablePropertyChangeSupport;
import org.noos.xing.mydoggy.plaf.support.ResolvableHashtable;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ShortcutProcessor;
import org.noos.xing.mydoggy.plaf.ui.content.ContentDescriptor;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.drag.ContentManagerDropTarget;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowCommonMultiSplitDropTarget;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.DockableManager2ToolWindowManagerWrapper;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * @author Angelo De Caro
 * @beaninfo attribute: isContainer true
 * description: MyDoggyToolWindowManager
 * TODO: put pnot focusable panel into content container...
 */
public class MyDoggyToolWindowManager extends JPanel implements ToolWindowManager, PropertyChangeListener {

    protected static boolean firePublic = true;

    public final static Object sync = new Object();

    protected ToolWindowGroup showingGroup;

    protected MyDoggyContentManager contentManager;

    protected Component windowAncestor;
    protected RootPaneContainer rootPaneContainer;

    protected MyDoggyToolWindowBar[] bars;
    protected Map<Object, ToolWindowDescriptor> tools;
    protected Map<Object, ToolWindowGroup> toolWindowGroups;
    protected Map<Object, ToolWindow> aliases;
    protected Map<String, DockableDescriptor> dockableDescriptorMap;

    protected ToolWindowGroup allToolWindowGroup;

    protected TableLayout contentPaneLayout;

    protected JSplitPane mainSplitPane;
    protected JPanel mainContainer;
    protected MultiSplitDockableContainer toolDockableContainer;

    protected CleanablePropertyChangeSupport propertyChangeSupport;

    protected Object activeToolWindowId;

    protected GlassPanel glassPanel;
    protected Component lastFocusOwner = null;

    protected PersistenceDelegate persistenceDelegate;

    // Type Descriptors Template.
    protected DefaultFloatingTypeDescriptor floatingTypeDescriptor;
    protected DefaultDockedTypeDescriptor dockingTypeDescriptor;
    protected DefaultSlidingTypeDescriptor slidingTypeDescriptor;
    protected DefaultFloatingLiveTypeDescriptor floatingLiveTypeDescriptor;

    protected ToolWindowManagerDescriptor toolWindowManagerDescriptor;

    // ToolWindwoManager Listener List
    protected EventListenerList twmListeners;

    protected ClassLoader uiClassLoader;
    protected ResourceManagerListener resourceManagerListener;
    protected transient ResourceManager resourceManager;

    // Support for content manager disabling
    protected Component oldMainContent = null;
    protected boolean dockableMainContentMode = false;

    protected CornerPanel nordWestCorner;
    protected CornerPanel nordEastCorner;
    protected CornerPanel southWestCorner;
    protected CornerPanel southEastCorner;


    public MyDoggyToolWindowManager() {
        this(Locale.getDefault(), null);
    }

    public MyDoggyToolWindowManager(Locale locale, ClassLoader uiClassLoader) {
        this.uiClassLoader = uiClassLoader;

        this.allToolWindowGroup = new AllToolWindowGroup();
        this.aliases = new HashMap<Object, ToolWindow>();
        this.dockableDescriptorMap = new HashMap<String, DockableDescriptor>();

        this.propertyChangeSupport = new CleanablePropertyChangeSupport(this);
        this.toolWindowManagerDescriptor = new MyDoggyToolWindowManagerDescriptor(this);
        this.toolWindowManagerDescriptor.addPropertyChangeListener(this);

        initUI(locale);
        initPersistenceDelegate();
        initComponents();
        initListeners();
    }


    public ToolWindow[] getDockables() {
        return getToolWindows();
    }

    public void addAlias(ToolWindow toolWindow, Object alias) {
        if (tools.containsKey(alias))
            throw new IllegalArgumentException("There is a tool whose id is the passed alias. Cannot add that alias.");
        aliases.put(alias, toolWindow);
    }

    public Object[] getAliases(ToolWindow toolWindow) {
        List<Object> result = new ArrayList<Object>();
        for (Map.Entry<Object, ToolWindow> entry : aliases.entrySet()) {
            if (entry.getValue() == toolWindow)
                result.add(entry.getKey());
        }
        return result.toArray();
    }

    public ToolWindow removeAlias(Object alias) {
        return aliases.remove(alias);
    }

    public void addDockableManagerListener(DockableManagerListener listener) {
        addToolWindowManagerListener(new DockableManager2ToolWindowManagerWrapper(listener));
    }

    public void removeDockableManagerListener(DockableManagerListener listener) {
        for (ToolWindowManagerListener managerListener : getToolWindowManagerListeners()) {
            if (managerListener instanceof DockableManager2ToolWindowManagerWrapper) {
                if (((DockableManager2ToolWindowManagerWrapper) managerListener).getListener() == listener) {
                    removeToolWindowManagerListener(managerListener);
                }
            }
        }
    }

    public DockableManagerListener[] getDockableManagerListeners() {
        List<DockableManagerListener> listeners = new ArrayList<DockableManagerListener>();
        for (ToolWindowManagerListener managerListener : getToolWindowManagerListeners()) {
            if (managerListener instanceof DockableManager2ToolWindowManagerWrapper) {
                listeners.add(((DockableManager2ToolWindowManagerWrapper) managerListener).getListener());
            }
        }
        return listeners.toArray(new DockableManagerListener[listeners.size()]);
    }


    public ContentManager getContentManager() {
        return contentManager;
    }

    public ToolWindowManagerDescriptor getToolWindowManagerDescriptor() {
        return toolWindowManagerDescriptor;
    }

    public PersistenceDelegate getPersistenceDelegate() {
        return persistenceDelegate;
    }

    public ToolWindow registerToolWindow(String id, String title, Icon icon,
                                         Component component,
                                         ToolWindowAnchor anchor) {
        if (id == null)
            throw new IllegalArgumentException("Cannot register tool window with an invalid id. Id cannot be null.");
        if (component == null)
            throw new IllegalArgumentException("Cannot register tool window with a null component. [id : " + id + "]");
        if (anchor == null)
            throw new IllegalArgumentException("Cannot register tool window with a null anchor. [id : " + id + "]");

        int index = tools.size() + 1;
        if (index > 9)
            index = -1;

        if (getDockable(id) != null)
            throw new IllegalArgumentException("Cannot register tool window with passed id. An already registered dockable exists. [id : " + id + "]");

        // TODO: why resourceBundle is null???
        MyDoggyToolWindow toolWindow = new MyDoggyToolWindow(this,
                                                             id,
                                                             index,
                                                             anchor,
                                                             ToolWindowType.DOCKED,
                                                             title, icon, component,
                                                             null);
        toolWindow.addPlafPropertyChangeListener(this);

        tools.put(toolWindow.getId(), toolWindow.getDescriptor());

        // fire Event
        fireRegisteredToolEvent(toolWindow);

        return toolWindow;
    }

    public void unregisterToolWindow(String id) {
        final ToolWindowDescriptor toolWindowDescriptor = tools.get(id);

        if (toolWindowDescriptor != null) {
            try {
                ToolWindow toolWindow = toolWindowDescriptor.getToolWindow();

                // Check for delegator
                removeIfDockableDelegator(toolWindow);

                // Deactivate the tool
                toolWindow.setFlashing(false);
                toolWindow.setMaximized(false);
                toolWindow.setAvailable(false);

                // Remove tabs
                for (ToolWindowTab toolWindowTab : toolWindowDescriptor.getToolWindow().getToolWindowTabs()) {
                    toolWindow.removeToolWindowTab(toolWindowTab);
                }

                // Remove from the list
                tools.remove(toolWindowDescriptor.getToolWindow().getId());

                // Remove aliases
                for (Iterator<ToolWindow> iterator = aliases.values().iterator(); iterator.hasNext();) {
                    ToolWindow aliasedToolWindow = iterator.next();
                    if (aliasedToolWindow == toolWindow)
                        iterator.remove();
                }

                // Fire event
                fireUnregisteredToolEvent(toolWindowDescriptor.getToolWindow());
            } finally {
                // Clean
                toolWindowDescriptor.cleanup();
            }
        } else
            throw new IllegalArgumentException("Doesn't exist a tool window with passed id. [id : " + id + "]");
    }

    public void unregisterAllToolWindow() {
        // Remove all tools
        for (ToolWindow toolWindow : getToolWindows()) {
            unregisterToolWindow(toolWindow.getId());
        }

        // Final cleans
        aliases.clear();
    }

    public ToolWindow getToolWindowByAlias(Object alias) {
        return aliases.get(alias);
    }

    public ToolWindow[] getToolWindows() {
        java.util.List<ToolWindow> result = new ArrayList<ToolWindow>();

        for (ToolWindowDescriptor tool : tools.values())
            result.add(tool.getToolWindow());

        return result.toArray(new ToolWindow[result.size()]);
    }

    public Object getActiveToolWindowId() {
        return activeToolWindowId;
    }

    public ToolWindow getToolWindow(Object key) {
        if (key == null)
            return null;

        ToolWindowDescriptor descriptor = tools.get(key);

        if (descriptor == null) {
            return aliases.get(key);
        } else
            return descriptor.getToolWindow();
    }

    public ToolWindow getToolWindow(int index) {
        if (index != -1 && index <= 0 && index > 9)
            throw new IllegalArgumentException("Invalid index. Valid index range is [-1, 1-9]. [index : " + index + "]");

        for (ToolWindow toolWindow : getToolWindows()) {
            if (toolWindow.getIndex() == index) {
                return toolWindow;
            }
        }
        return null;
    }

    public ToolWindow[] getToolsByAnchor(ToolWindowAnchor anchor) {
        java.util.List<ToolWindow> result = new ArrayList<ToolWindow>();

        for (ToolWindowDescriptor tool : tools.values()) {
            if (tool.getToolWindow().getAnchor().equals(anchor))
                result.add(tool.getToolWindow());
        }

        return result.toArray(new ToolWindow[result.size()]);
    }

    public ToolWindowGroup getToolWindowGroup() {
        return allToolWindowGroup;
    }

    public ToolWindowGroup getToolWindowGroup(String name) {
        return toolWindowGroups.get(name);
    }

    public ToolWindowGroup[] getToolWindowGroups() {
        Collection<ToolWindowGroup> c = toolWindowGroups.values();
        return c.toArray(new ToolWindowGroup[c.size()]);
    }

    public boolean removeToolWindowGroup(String name) {
        if (name == null)
            return false;

        ToolWindowGroup group = toolWindowGroups.remove(name);
        if (group != null) {
            fireRemovedGroupEvent(group);
            return true;
        }
        return false;
    }

    public boolean removeToolWindowGroup(ToolWindowGroup toolWindowGroup) {
        return toolWindowGroup != null && removeToolWindowGroup(toolWindowGroup.getName());
    }

    public boolean containsGroup(String name) {
        return allToolWindowGroup.getName().equals(name) || toolWindowGroups.containsKey(name);
    }

    public ToolWindowTypeDescriptor getTypeDescriptorTemplate(ToolWindowType type) {
        switch (type) {
            case FLOATING:
            case FLOATING_FREE:
                if (floatingTypeDescriptor == null)
                    floatingTypeDescriptor = new DefaultFloatingTypeDescriptor();
                return floatingTypeDescriptor;
            case FLOATING_LIVE:
                if (floatingLiveTypeDescriptor == null)
                    floatingLiveTypeDescriptor = new DefaultFloatingLiveTypeDescriptor();
                return floatingLiveTypeDescriptor;
            case DOCKED:
                if (dockingTypeDescriptor == null)
                    dockingTypeDescriptor = new DefaultDockedTypeDescriptor(resourceManager);
                return dockingTypeDescriptor;
            case SLIDING:
                if (slidingTypeDescriptor == null)
                    slidingTypeDescriptor = new DefaultSlidingTypeDescriptor();
                return slidingTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for. [type :" + type + "]");
    }

    public Dockable getDockable(Object id) {
        Dockable result = getToolWindow(id);
        if (result == null) {
            // Try tab
            for (ToolWindow toolWindow : getToolWindows()) {
                for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                    if (tab.getId().equals(id)) {
                        result = tab;
                        break;
                    }
                }
                if (result != null)
                    break;
            }

            if (result == null) {
                // Try content
                result = getContentManager().getContent(id);
            }
        }

        return result;
    }

    public ToolWindowBar getToolWindowBar(ToolWindowAnchor anchor) {
        return getBar(anchor);
    }

    public void addToolWindowManagerListener(ToolWindowManagerListener listener) {
        twmListeners.add(ToolWindowManagerListener.class, listener);
    }

    public void removeToolWindowManagerListener(ToolWindowManagerListener listener) {
        twmListeners.remove(ToolWindowManagerListener.class, listener);
    }

    public ToolWindowManagerListener[] getToolWindowManagerListeners() {
        return twmListeners.getListeners(ToolWindowManagerListener.class);
    }


    public synchronized void propertyChange(final PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source instanceof DockableDescriptor) {
            DockableDescriptor descriptor = (DockableDescriptor) source;
            if (descriptor.getDockableType() != DockableDescriptor.DockableType.CUSTOM) {
                if (getDockable(descriptor.getDockable().getId()) != descriptor.getDockable()) {
                    throw new RuntimeException("Manager doesn't contain that ToolWindow. [id : " + descriptor.getDockable().getId() + "]");
                }
            }
        } else if (!(source instanceof MyDoggyToolWindowBar) &&
                   !(source instanceof MyDoggyToolWindowManagerDescriptor) &&
                   !(source instanceof MyDoggyToolWindowManager) &&
                   !(source instanceof MyDoggyToolWindowTab) &&
                   !(source instanceof ToolWindowTypeDescriptor) &&
                   !(source instanceof ContentManager)) {
            throw new RuntimeException("Illegal Source : " + source);
        }

//        System.out.println(SwingUtil.toString(evt));
        /*boolean fired = */propertyChangeSupport.firePropertyChange(evt);
/*
        if (fired && source instanceof MyDoggyToolWindowTab) {
            System.out.println("fired when tab ");
        }
*/
    }


    public void removeNotify() {
        super.removeNotify();

        // Restore all detached ddckable...
        for (ToolWindow toolWindow : getToolWindows()) {
            if (toolWindow.getType() == ToolWindowType.FLOATING && toolWindow.isVisible()) {
                toolWindow.setVisible(false);
            }
        }

        for (Content content : getContentManager().getContents()) {
            content.setDetached(false);
        }

        // Fire event
        propertyChangeSupport.firePropertyChange(
                new PropertyChangeEvent(MyDoggyToolWindowManager.this, "manager.window.ancestor",
                                        windowAncestor,
                                        null)
        );
    }

    public void addNotify() {
        super.addNotify();

        // Load ancestor
        windowAncestor = SwingUtil.getWindowAncestor(this);
        // Check root pane container
        if (!(windowAncestor instanceof RootPaneContainer)) {
            throw new IllegalArgumentException("Window Ancestor must implement RootPaneContainer");
        }
        rootPaneContainer = (RootPaneContainer) windowAncestor;

        // Fire related event
        propertyChangeSupport.firePropertyChange("manager.window.ancestor", null, windowAncestor);

/*
        TODO: (-) introduce level
        int level = 0;
        Component mydoggyParent = this;
        while (true) {
            mydoggyParent = SwingUtil.getParent(mydoggyParent.getParent(), MyDoggyToolWindowManager.class);
            if (mydoggyParent == null)
                break;
            level++;
        }
        getResourceManager().putProperty("layer.level", String.valueOf(level * 5));
*/
    }

    public Component getWindowAncestor() {
        return windowAncestor;
    }

    public JLayeredPane getLayeredPane() {
        return (rootPaneContainer != null) ? rootPaneContainer.getLayeredPane() : null;
    }

    public RootPaneContainer getRootPaneContainer() {
        return rootPaneContainer;
    }

    public void setPersistenceDelegate(PersistenceDelegate persistenceDelegate) {
        this.persistenceDelegate = persistenceDelegate;
    }

    public void setUserResourceBundle(Locale locale, String bundle, ClassLoader classLoader) {
        resourceManager.setUserBundle(locale, bundle, classLoader);
    }

    public void setMainSplitPane(ToolWindowAnchor anchor) {
        switch (anchor) {
            case LEFT:
                mainSplitPane = getBar(anchor).getSplitPane();
                mainSplitPane.setRightComponent(mainContainer);
                break;
            case RIGHT:
                mainSplitPane = getBar(anchor).getSplitPane();
                mainSplitPane.setLeftComponent(mainContainer);
                break;
            case TOP:
                mainSplitPane = getBar(anchor).getSplitPane();
                mainSplitPane.setBottomComponent(mainContainer);
                break;
            case BOTTOM:
                mainSplitPane = getBar(anchor).getSplitPane();
                mainSplitPane.setTopComponent(mainContainer);
                break;
        }
    }

    public MyDoggyToolWindowBar getBar(ToolWindowAnchor anchor) {
        return bars[anchor.ordinal()];
    }

    public ToolWindowGroup getShowingGroup() {
        return this.showingGroup;
    }

    public ToolWindowDescriptor getDescriptor(ToolWindow toolWindow) {
        return tools.get(toolWindow.getId());
    }

    public void setCornerComponent(ToolWindowManagerDescriptor.Corner corner, Component component) {
        switch (corner) {
            case NORD_WEST:
                nordWestCorner.setComponent(component);
                break;
            case SOUTH_WEST:
                southWestCorner.setComponent(component);
                break;
            case NORD_EAST:
                nordEastCorner.setComponent(component);
                break;
            case SOUTH_EAST:
                southEastCorner.setComponent(component);
                break;
        }
//        SwingUtil.repaint(this);
    }

    public GlassPanel getGlassPanel() {
        return glassPanel;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        if (this.resourceManager != null)
            this.resourceManager.removePropertyChangeListener(resourceManagerListener);

        if (resourceManagerListener == null)
            resourceManagerListener = new ResourceManagerListener();

        this.resourceManager = resourceManager;
        resourceManager.addPropertyChangeListener(resourceManagerListener);

        propertyChange(new PropertyChangeEvent(this, "resourceManager", null, resourceManager));
    }

    public ToolWindowAnchor getToolWindowAnchor(Point p) {
        Rectangle b = getBounds();

        int leftLength = getBar(LEFT).getLength();
        int rightLength = getBar(RIGHT).getLength();
        int topLength = getBar(TOP).getLength();
        int bottomLength = getBar(BOTTOM).getLength();

        if (p.x <= leftLength && p.y >= topLength && p.y <= b.height - bottomLength) {
            return ToolWindowAnchor.LEFT;
        } else if (p.x >= b.width - rightLength && p.y >= topLength && p.y <= b.height - bottomLength) {
            return ToolWindowAnchor.RIGHT;
        } else if (p.y <= topLength && p.x >= leftLength && p.x <= b.width - rightLength) {
            return ToolWindowAnchor.TOP;
        } else if (p.y >= b.height - bottomLength && p.x >= leftLength && p.x <= b.width - rightLength) {
            return ToolWindowAnchor.BOTTOM;
        }
        return null;
    }

    public void removeIfDockableDelegator(Dockable dockable) {
        // Search for a tab...
        for (ToolWindow toolWindow : getToolWindows()) {

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                if (tab.getDockableDelegator() == dockable) {
                    toolWindow.removeToolWindowTab(tab);
                    break;
                }
            }

        }

        // Search for a content...
        for (Content content : contentManager.getContents()) {

            if (content.getDockableDelegator() == dockable) {
                contentManager.removeContent(content);
                break;
            }

        }
    }

    public void addInternalPropertyChangeListener(String property, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(property, listener);
    }


    public void setMainContent(Component content) {
        if (content == null)
            resetMainContent();
        else {
            if (dockableMainContentMode) {
                oldMainContent = content;
            } else {
                mainContainer.setOpaque(false);
                mainContainer.removeAll();
                mainContainer.add(content, "0,0,FULL,FULL");

                mainSplitPane.invalidate();
                mainSplitPane.validate();

                SwingUtil.repaint(mainSplitPane);
            }
        }
    }

    public void resetMainContent() {
        if (dockableMainContentMode) {
            oldMainContent = null;
        } else {
            mainContainer.removeAll();
            SwingUtil.repaint(mainSplitPane);
            mainContainer.setOpaque(true);
        }
    }

    public Container getMainContainer() {
        return mainContainer;
    }

    public Component getMainContent() {
        return (mainContainer.getComponentCount() == 0) ? null : mainContainer.getComponent(0);
    }

    public void setDockableMainContentMode(boolean enable) {
        if (enable) {
            toolDockableContainer = new MultiSplitDockableContainer(MyDoggyToolWindowManager.this, JSplitPane.VERTICAL_SPLIT);
            toolDockableContainer.setStoreLayout(false);

            ContentPanel contentPanel = new ContentPanel("toolWindow.container.");
            contentPanel.setDropTarget(new ToolWindowCommonMultiSplitDropTarget(contentPanel, MyDoggyToolWindowManager.this));
            contentPanel.setComponent(toolDockableContainer);

            oldMainContent = getMainContent();
            setMainContent(contentPanel);
            dockableMainContentMode = true;
        } else {
            dockableMainContentMode = false;
            setMainContent(oldMainContent);
        }
    }

    public Rectangle getBoundsToScreen(Rectangle bounds, Component ref) {
        Point location = bounds.getLocation();
        SwingUtilities.convertPointToScreen(location, ref);
        bounds.setLocation(location);
        bounds.y += getJMenuBarExtraHeight();

        return bounds;

    }


    protected void initPersistenceDelegate() {
        this.persistenceDelegate = new XMLPersistenceDelegate(this);
    }

    protected void initComponents() {
        this.twmListeners = new EventListenerList();

        initContentManager();

        resourceManager.applyCustomization(MyDoggyKeySpace.TOOL_WINDOW_MANAGER, this, getContext());

        // Init data structures
        bars = new MyDoggyToolWindowBar[4];
        tools = new LinkedHashMap<Object, ToolWindowDescriptor>();
        toolWindowGroups = new ResolvableHashtable<Object, ToolWindowGroup>(new ResolvableHashtable.Resolver<ToolWindowGroup>() {
            public ToolWindowGroup get(Object key) {
                ToolWindowGroup group = new MyDoggyToolWindowGroup(MyDoggyToolWindowManager.this, key.toString(), false);
                toolWindowGroups.put(key, group);
                fireAddedGroupEvent(group);

                return group;
            }
        });

        // Init gui
        contentPaneLayout = new ExtendedTableLayout(
                new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}}
        );
        setLayout(contentPaneLayout);

        //  Init corner panels...
        add(nordWestCorner = new CornerPanel(ToolWindowManagerDescriptor.Corner.NORD_WEST), "0,0,c,c");
        add(southWestCorner = new CornerPanel(ToolWindowManagerDescriptor.Corner.SOUTH_WEST), "0,2,c,c");
        add(nordEastCorner = new CornerPanel(ToolWindowManagerDescriptor.Corner.NORD_EAST), "2,0,c,c");
        add(southEastCorner = new CornerPanel(ToolWindowManagerDescriptor.Corner.SOUTH_EAST), "2,2,c,c");

        // Register bars, one for every anchor
        addBar(LEFT, JSplitPane.HORIZONTAL_SPLIT, "0,1", "0,0,FULL,FULL");
        addBar(RIGHT, JSplitPane.HORIZONTAL_SPLIT, "2,1", "2,0,FULL,FULL");
        addBar(TOP, JSplitPane.VERTICAL_SPLIT, "1,0", "2,2,FULL,FULL");
        addBar(BOTTOM, JSplitPane.VERTICAL_SPLIT, "1,2", "0,2,FULL,FULL");

        mainContainer = (JPanel) resourceManager.createComponent(MyDoggyKeySpace.TOOL_WINDOW_MANAGER_CONTENT_CONTAINER, getContext());
        mainContainer.setName("toolWindowManager.mainContainer");
        mainContainer.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        mainContainer.setFocusable(false);

        // TODO: 
//        mainContainer.setFocusCycleRoot(true);
//        mainContainer.setFocusTraversalPolicyProvider(true);
//        mainContainer.setFocusTraversalPolicy(new ContainerOrderFocusTraversalPolicy());

        getBar(BOTTOM).getSplitPane().setTopComponent(getBar(TOP).getSplitPane());
        getBar(TOP).getSplitPane().setBottomComponent(getBar(LEFT).getSplitPane());
        getBar(LEFT).getSplitPane().setRightComponent(getBar(RIGHT).getSplitPane());
        getBar(RIGHT).getSplitPane().setResizeWeight(1);

        add(getBar(BOTTOM).getSplitPane(), "1,1,FULL,FULL");

        mainSplitPane = getBar(RIGHT).getSplitPane();
        mainSplitPane.addPropertyChangeListener("UI", new UpdateUIChangeListener());
        mainSplitPane.setLeftComponent(mainContainer);

    }

    protected void initContentManager() {
        contentManager = new MyDoggyContentManager(this);
        contentManager.setContentManagerUI(new MyDoggyTabbedContentManagerUI());
        contentManager.addPropertyChangeListener(this);
        contentManager.addContentManagerListener(new InternalContentMananagerListener());
    }

    protected void initGlassPane() {
        this.glassPanel = new GlassPanel(rootPaneContainer);
//        rootPaneContainer.setGlassPane(this.glassPanel = new GlassPanel(rootPaneContainer));
    }

    protected void initListeners() {
        // Init PropertyChange listeners
        AvailablePropertyChangeListener availablePropertyChangeListener = new AvailablePropertyChangeListener();
        propertyChangeSupport.addPropertyChangeListener("available", availablePropertyChangeListener);
        propertyChangeSupport.addPropertyChangeListener("representativeAnchorButtonVisible", availablePropertyChangeListener);
        propertyChangeSupport.addPropertyChangeListener("showUnavailableTools", new ShowUnavailableToolsPropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("visible", new VisiblePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("active", new ActivePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("anchor", new AnchorPropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("type", new TypePropertyChangeListener());

        MaximizedChangeListener maximizedChangeListener = new MaximizedChangeListener();
        propertyChangeSupport.addPropertyChangeListener("maximized", maximizedChangeListener);
        propertyChangeSupport.addPropertyChangeListener("maximized.before", maximizedChangeListener);
        propertyChangeSupport.addPropertyChangeListener("index", new IndexChangeListener());
        propertyChangeSupport.addPropertyChangeListener("icon", new IconChangeListener());
        propertyChangeSupport.addPropertyChangeListener("numberingEnabled", new NumberingEnabledChangeListener());
        propertyChangeSupport.addPropertyChangeListener("tempShowed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
//                for (ToolWindowDescriptor tool : tools.values())
//                    tool.getToolWindowContainer().propertyChange(evt);
            }
        });
        propertyChangeSupport.addPropertyChangeListener("anchor.index", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

                if (descriptor.getToolWindow().getType() == ToolWindowType.DOCKED)
                    getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
            }
        });
        propertyChangeSupport.addPropertyChangeListener("manager.window.ancestor", new AncestorClosedChangeListener());
        propertyChangeSupport.addPropertyChangeListener("enabled", new ContentMananagerEnabledChangeListener());
        propertyChangeSupport.addPropertyChangeListener("length", new BarLengthListener());

        initKeyboardFocusManagerListeners();

        // Setup DropTarget for main container
        mainContainer.setDropTarget(new ContentManagerDropTarget(mainContainer, this));
    }

    protected void initKeyboardFocusManagerListeners() {
        propertyChangeSupport.addPropertyChangeListener("manager.window.ancestor", new PropertyChangeListener() {
            final ShortcutProcessor shortcutProcessor = new ShortcutProcessor(MyDoggyToolWindowManager.this,
                                                                              MyDoggyToolWindowManager.this);
            final FocusOwnerChangeListener focusOwnerChangeListener = new FocusOwnerChangeListener();

            public void propertyChange(PropertyChangeEvent evt) {
                KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                if (evt.getNewValue() != null) {
                    // Add listener
                    keyboardFocusManager.addKeyEventPostProcessor(shortcutProcessor);
                    keyboardFocusManager.addPropertyChangeListener("focusOwner", focusOwnerChangeListener);

                    // Init glass pane...
                    initGlassPane();
                } else {
                    keyboardFocusManager.removeKeyEventPostProcessor(shortcutProcessor);

                    for (PropertyChangeListener listener : keyboardFocusManager.getPropertyChangeListeners("focusOwner")) {
                        if (listener.getClass().getPackage().getName().startsWith(
                                MyDoggyToolWindowManager.this.getClass().getPackage().getName()))
                            keyboardFocusManager.removePropertyChangeListener("focusOwner", listener);
                    }
                }
            }
        });
    }

    protected void initUI(Locale locale) {
        Properties properties = SwingUtil.loadPropertiesFile("mydoggyplaf.properties", uiClassLoader);

        String className = properties.getProperty("ResourceManager.class");
        if (className == null) {
            System.err.println("Cannot find ResourceManager.class property value. Use default.");
            className = MyDoggyResourceManager.class.getName();
        }
        try {
            setResourceManager((ResourceManager) SwingUtil.newObject(className));
        } catch (Exception e) {
            e.printStackTrace();
            setResourceManager(new MyDoggyResourceManager());
        }
        resourceManager.setLocale(locale);
    }


    protected JSplitPane renderSplitPane(int orientation) {
        return (JSplitPane) resourceManager.createComponent(MyDoggyKeySpace.ANCHOR_SPLIT_PANE,
                                                            getContext("newOrientation", orientation));
    }

    protected JSplitPane addBar(ToolWindowAnchor anchor, int splitPaneOrientation,
                                String barConstraints, String cornerConstraints) {
        // Initialize bar
        MyDoggyToolWindowBar myDoggyToolWindowBar = new MyDoggyToolWindowBar(this,
                                                                             renderSplitPane(splitPaneOrientation),
                                                                             anchor);
        myDoggyToolWindowBar.addPropertyChangeListener(this);

        bars[anchor.ordinal()] = myDoggyToolWindowBar;

        // Add Bar to Container
        add(myDoggyToolWindowBar.getContainer(), barConstraints);

        // Add Corner to Container
        add(resourceManager.createComponent(MyDoggyKeySpace.CORNER_CONTENT_PANE, getContext()),
            cornerConstraints);

        return myDoggyToolWindowBar.getSplitPane();
    }

    protected void fireRegisteredToolEvent(ToolWindow toolWindow) {
        ToolWindowManagerEvent event = new ToolWindowManagerEvent(this,
                                                                  ToolWindowManagerEvent.ActionId.TOOL_REGISTERED,
                                                                  toolWindow);

        for (ToolWindowManagerListener listener : twmListeners.getListeners(ToolWindowManagerListener.class)) {
            listener.toolWindowRegistered(event);
        }
    }

    protected void fireUnregisteredToolEvent(ToolWindow toolWindow) {
        ToolWindowManagerEvent event = new ToolWindowManagerEvent(this,
                                                                  ToolWindowManagerEvent.ActionId.TOOL_UNREGISTERED,
                                                                  toolWindow);
        for (ToolWindowManagerListener listener : twmListeners.getListeners(ToolWindowManagerListener.class)) {
            listener.toolWindowUnregistered(event);
        }
    }

    protected void fireAddedGroupEvent(ToolWindowGroup toolWindowGroup) {
        ToolWindowManagerEvent event = new ToolWindowManagerEvent(this,
                                                                  ToolWindowManagerEvent.ActionId.GROUP_ADDED,
                                                                  toolWindowGroup);
        for (ToolWindowManagerListener listener : twmListeners.getListeners(ToolWindowManagerListener.class)) {
            listener.toolWindowGroupAdded(event);
        }
    }

    protected void fireRemovedGroupEvent(ToolWindowGroup toolWindowGroup) {
        ToolWindowManagerEvent event = new ToolWindowManagerEvent(this,
                                                                  ToolWindowManagerEvent.ActionId.GROUP_REMOVED,
                                                                  toolWindowGroup);
        for (ToolWindowManagerListener listener : twmListeners.getListeners(ToolWindowManagerListener.class)) {
            listener.toolWindowGroupRemoved(event);
        }
    }


    void syncPanel(ToolWindowAnchor anchor) {
        boolean revalidate = false;

        MyDoggyToolWindowBar toolWindowBar = getBar(anchor);

        if (anchor == LEFT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getColumn(0) != 0) {
                contentPaneLayout.setColumn(0, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getColumn(0) == 0) {
                contentPaneLayout.setColumn(0, getBar(LEFT).getLength());
                revalidate = true;
            }
        } else if (anchor == RIGHT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getColumn(2) != 0) {
                contentPaneLayout.setColumn(2, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getColumn(2) == 0) {
                contentPaneLayout.setColumn(2, getBar(RIGHT).getLength());
                revalidate = true;
            }
        } else if (anchor == TOP) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getRow(0) != 0) {
                contentPaneLayout.setRow(0, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getRow(0) == 0) {
                contentPaneLayout.setRow(0, getBar(TOP).getLength());
                revalidate = true;
            }
        } else if (anchor == BOTTOM) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getRow(2) != 0) {
                contentPaneLayout.setRow(2, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getRow(2) == 0) {
                contentPaneLayout.setRow(2, getBar(BOTTOM).getLength());
                revalidate = true;
            }
        }

        if (revalidate)
            SwingUtil.repaint(this);
    }

    void setShowingGroup(ToolWindowGroup toolWindowGroup) {
        this.showingGroup = toolWindowGroup;
    }

    void setShowingGroup() {
        if (showingGroup == null)
            setShowingGroup(allToolWindowGroup);
    }

    void resetShowingGroup() {
        if (showingGroup == getToolWindowGroup())
            this.showingGroup = null;
    }

    boolean isShowingGroup() {
        return showingGroup == allToolWindowGroup;
    }


    public boolean isWindowFocused() {
//        return (windowAncestor instanceof Window) ? ((Window) windowAncestor).isFocused() : windowAncestor.isfo
        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        return focusOwner != null ? SwingUtil.getWindowAncestor(focusOwner) == windowAncestor : false;

    }

    public int getJMenuBarExtraHeight() {
        JMenuBar jMenuBar = getRootPane().getJMenuBar();

        if (jMenuBar != null && jMenuBar.isVisible())
            return jMenuBar.getHeight();
        return 0;
    }

    public DockableDescriptor getDockableDescriptor(String id) {
        return dockableDescriptorMap.get(id);
    }

    public void putDockableDescriptor(String id, DockableDescriptor dockableDescriptor) {
        dockableDescriptorMap.put(id, dockableDescriptor);
    }

    public void removeDockableDescriptor(String id) {
        DockableDescriptor descriptor = dockableDescriptorMap.remove(id);
        if (descriptor != null)
            descriptor.cleanup();
    }

    public void setTempShowed(boolean tempShowed) {
        getBar(LEFT).setTempShowed(tempShowed);
        getBar(RIGHT).setTempShowed(tempShowed);
        getBar(TOP).setTempShowed(tempShowed);
        getBar(BOTTOM).setTempShowed(tempShowed);
    }

    public DockableDescriptor createDescriptor(Dockable dockable) {
        if (dockable instanceof ToolWindow)
            return new ToolWindowDescriptor(this, (MyDoggyToolWindow) dockable);
        else if (dockable instanceof Content)
            return new ContentDescriptor(this, (Content) dockable);
        throw new IllegalArgumentException("Invalid dockable. [dockable : " + dockable + "]");
    }

    public Dockable getDockableWrapper(Dockable dockable) {
        // Search for a tab...
        for (ToolWindow toolWindow : getToolWindows()) {
            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                if (tab.getDockableDelegator() == dockable) {
                    return tab;
                }
            }
        }

        // Search for a content...
        for (Content content : contentManager.getContents()) {
            if (content.getDockableDelegator() == dockable) {
                return content;
            }
        }

        return null;
    }

    public MutableContext getContext() {
        DefaultMutableContext context = new DefaultMutableContext();
        context.put(ToolWindowManager.class, this);
        context.put(MyDoggyToolWindowManager.class, this);
        context.put(ResourceManager.class, getResourceManager());

        return context;
    }

    public MutableContext getContext(Object... entries) {
        DefaultMutableContext context = new DefaultMutableContext(entries);
        context.put(ToolWindowManager.class, this);
        context.put(MyDoggyToolWindowManager.class, this);
        context.put(ResourceManager.class, getResourceManager());

        return context;
    }

    public void ensureContentVisible(Content content) {
        // Check if any toolwindow is maximized and restore it
        for (ToolWindowDescriptor t : tools.values()) {
            if (t.getToolWindow().isMaximized() && !t.isFloatingWindow() && !t.getToolWindow().isDetached()) {
                t.getToolWindow().setMaximized(false);
            }
        }

        // Is content area large enough?
        if (mainContainer.getWidth() < 50 || mainContainer.getHeight() < 50) {
            // If not, reset split panes to equal divisions
            mainSplitPane.setDividerLocation(0.5);
            for (MyDoggyToolWindowBar bar : bars) {
                bar.getSplitPane().setDividerLocation(0.5);
            }
        }
    }


    public class AvailablePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            DockableDescriptor descriptor = (DockableDescriptor) evt.getSource();
            ToolWindowAnchor target = descriptor.getAnchor();

            // Notify specific bar
            getBar(target).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(target);
        }
    }

    public class ShowUnavailableToolsPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            for (MyDoggyToolWindowBar bar : bars)
                bar.propertyChange(evt);

            // Syncronize bars panel
            syncPanel(LEFT);
            syncPanel(RIGHT);
            syncPanel(TOP);
            syncPanel(BOTTOM);
        }
    }

    public class VisiblePropertyChangeListener implements PropertyChangeListener {
        boolean showingGroupValueAdj = false;

        public void propertyChange(PropertyChangeEvent evt) {
            // Request by email: MyDoggy and IntelliJ IDEA GUI Editor
            SwingUtil.revalidate(MyDoggyToolWindowManager.this);

            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            // Fire "visible.before" to all bars
            PropertyChangeEvent event = new PropertyChangeEvent(evt.getSource(), "visible.before",
                                                                evt.getOldValue(), evt.getNewValue());
            for (MyDoggyToolWindowBar bar : bars)
                bar.propertyChange(event);

            // Fire "visible" to specific bar
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(descriptor.getToolWindow().getAnchor());

            // Support for implicit group...
            synchronized (sync) {
                if ((showingGroup == null || showingGroup == getToolWindowGroup()) && Boolean.TRUE.equals(evt.getNewValue()) && !showingGroupValueAdj) {
                    showingGroupValueAdj = true;
                    try {
                        for (ToolWindowGroup group : getToolWindowGroups()) {
                            if (group.isImplicit() && group.containesToolWindow(descriptor.getToolWindow())) {
                                for (ToolWindow tool : group.getToolsWindow()) {
                                    if (tool != descriptor.getToolWindow())
                                        tool.aggregate();
                                }
                                break;
                            }
                        }
                    } finally {
                        showingGroupValueAdj = false;
                    }
                }
            }
        }
    }

    public class ActivePropertyChangeListener implements PropertyChangeListener {

        public synchronized void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            // Fire "active.before" for all bars
            PropertyChangeEvent event = new PropertyChangeEvent(evt.getSource(), "active.before",
                                                                evt.getOldValue(), evt.getNewValue());
            for (MyDoggyToolWindowBar bar : bars)
                bar.propertyChange(event);

            // Fire "active" for specific bar
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);

            if (Boolean.FALSE.equals(evt.getNewValue())) {
                activeToolWindowId = null;

                if (lastFocusOwner != null) {
                    boolean shouldRequest = true;

                    for (MyDoggyToolWindowBar bar : bars) {
                        if (bar.valueAdjusting &&
                            getBar(descriptor.getToolWindow().getAnchor()) == bar) {
                            shouldRequest = false;
                            break;
                        }
                    }

                    if (shouldRequest)
                        SwingUtil.requestFocus(lastFocusOwner);
                }
            } else
                activeToolWindowId = descriptor.getToolWindow().getId();
        }
    }

    public class AnchorPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            ToolWindowAnchor oldAnchor = (ToolWindowAnchor) evt.getOldValue();
            ToolWindowAnchor newAnchor = (ToolWindowAnchor) evt.getNewValue();
            boolean force = false;
            if (oldAnchor == null) {
                oldAnchor = newAnchor;
                force = true;
            }

            ToolWindowType toolType = descriptor.getToolWindow().getType();
            if (toolType == ToolWindowType.FLOATING ||
                toolType == ToolWindowType.FLOATING_FREE ||
                toolType == ToolWindowType.FLOATING_LIVE ||
                force ||
                !descriptor.getToolWindow().isAvailable()) {

                PropertyChangeEvent avEvent = new UserPropertyChangeEvent(evt.getSource(), "available", true, false, new Object[]{-1, true});
                getBar(oldAnchor).propertyChange(avEvent);
                syncPanel(oldAnchor);

                assert evt instanceof UserPropertyChangeEvent;
                avEvent = new UserPropertyChangeEvent(evt.getSource(), "available", false, true,
                                                      new Object[]{((UserPropertyChangeEvent) evt).getUserObject(), true});
                getBar(newAnchor).propertyChange(avEvent);
                syncPanel(newAnchor);
            }

//            for (ToolWindowDescriptor tool : tools.values())
//                tool.getToolWindowContainer().propertyChange(evt);

            syncPanel(oldAnchor);
            syncPanel(newAnchor);

            if (force) {
                // Force reordering of aggregated tools.
//                getBar(newAnchor).propertyChange(
//                        new PropertyChangeEvent(evt.getSource(), "visible.reordering", false, true)
//                );
            }
        }

    }

    public class TypePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();

            toolWindowDescriptor.getToolBar().propertyChange(evt);

            syncPanel(toolWindowDescriptor.getToolWindow().getAnchor());
        }
    }

    public class IndexChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            ToolWindow modifiedTool = descriptor.getToolWindow();

            int newIndex = (Integer) evt.getNewValue();

            if (newIndex > 0) {
                for (ToolWindow toolWindow : getToolWindows()) {
                    if (toolWindow != modifiedTool && toolWindow.getIndex() == newIndex) {
                        toolWindow.setIndex(-1);
                        break;
                    }
                }
            }

            getBar(modifiedTool.getAnchor()).propertyChange(evt);
        }
    }

    public class IconChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();
            descriptor.getToolBar().propertyChange(evt);
        }

    }

    public class NumberingEnabledChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            // TODO: what is preferible...one listener that dispatch or many listeners...
//            for (ToolWindowDescriptor descriptor : tools.values())
//                descriptor.propertyChange(evt);
        }
    }

    public class UpdateUIChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            contentManager.updateUI();

            for (ToolWindowDescriptor descriptor : tools.values()) {
                descriptor.updateUI();
            }
        }
    }

    public class MaximizedChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
//            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);

            // Notify specific bar
            getBar(toolWindowDescriptor.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(toolWindowDescriptor.getToolWindow().getAnchor());
        }
    }

    public class ResourceManagerListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            SwingUtil.repaint(MyDoggyToolWindowManager.this);
        }
    }

    public class BarLengthListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowBar bar = (ToolWindowBar) evt.getSource();
            switch (bar.getAnchor()) {
                case LEFT:
                    if (contentPaneLayout.getColumn(0) != 0)
                        contentPaneLayout.setColumn(0, bar.getLength());
                    break;
                case RIGHT:
                    if (contentPaneLayout.getColumn(2) != 0)
                        contentPaneLayout.setColumn(2, bar.getLength());
                    break;
                case TOP:
                    if (contentPaneLayout.getRow(0) != 0)
                        contentPaneLayout.setRow(0, bar.getLength());
                    break;
                case BOTTOM:
                    if (contentPaneLayout.getRow(2) != 0)
                        contentPaneLayout.setRow(2, bar.getLength());
                    break;
            }
            SwingUtil.repaint(MyDoggyToolWindowManager.this);
        }
    }

    public class FocusOwnerChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Component newFocusOwner = (Component) evt.getNewValue();
            if (newFocusOwner != null && SwingUtilities.isDescendingFrom(newFocusOwner, mainContainer))
                lastFocusOwner = newFocusOwner;
        }
    }

    public class AncestorClosedChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            // TODO: this de facto initialize the toolwindow panel ...move this
            for (ToolWindowDescriptor tool : tools.values())
                tool.getToolWindowContainer().propertyChange(evt);
        }
    }

    public class ContentMananagerEnabledChangeListener implements PropertyChangeListener {

        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getSource() instanceof ContentManager) {
                //TODO (+) support request from Elvis...there are focus problem...                

                final MyDoggyToolWindowGroup group = new MyDoggyToolWindowGroup(MyDoggyToolWindowManager.this, "temp", true);
                for (ToolWindow toolWindow : getToolWindows()) {
                    if (toolWindow.isVisible() && toolWindow.getType() == ToolWindowType.DOCKED)
                        group.addToolWindow(toolWindow);
                }

                if (group.getToolsWindow().length > 0) {

/*
                    PropertyChangeListener listener = new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            System.out.println("ERORRE :" + SwingUtil.toString(evt));
                        }
                    };
*/
                    try {
//                        group.getToolsWindow()[0].addPropertyChangeListener(listener);

                        firePublic = false;
                        
                        group.setVisible(false);
                        setDockableMainContentMode(!(Boolean) evt.getNewValue());
                        group.setVisible(true);
                    } finally {
                        firePublic = true;
//                        group.getToolsWindow()[0].removePropertyChangeListener(listener);
                    }
                } else
                    setDockableMainContentMode(!(Boolean) evt.getNewValue());
            }
        }

    }

    public class InternalContentMananagerListener implements ContentManagerListener,
                                                                PropertyChangeListener {

        public void contentAdded(ContentManagerEvent event) {
            MyDoggyContent content = (MyDoggyContent) event.getContent();
            ensureContentVisible(content);
            content.addPlafPropertyChangeListener("ensureVisible", this);
        }

        public void contentRemoved(ContentManagerEvent event) {
            MyDoggyContent content = (MyDoggyContent) event.getContent();
            content.removePlafPropertyChangeListener(this);            
        }

        public void contentSelected(ContentManagerEvent event) {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ensureContentVisible((Content) evt.getNewValue());
        }
    }


    class AllToolWindowGroup extends MyDoggyToolWindowGroup {

        AllToolWindowGroup() {
            super(MyDoggyToolWindowManager.this, "all", false);
        }

        public void addToolWindow(ToolWindow toolWindow) {
            throw new IllegalStateException("Cannot call this method!!!");
        }

        public boolean removeToolWindow(ToolWindow toolWindow) {
            throw new IllegalStateException("Cannot call this method!!!");
        }

        public ToolWindow[] getToolsWindow() {
            return getToolWindows();
        }

        public boolean containesToolWindow(ToolWindow toolWindow) {
            return true;
        }

        public void setImplicit(boolean implicit) {
            throw new IllegalStateException("Cannot call this method on this instance.");
        }

        public boolean isImplicit() {
            return false;
        }

        public String toString() {
            return "MyDoggyToolWindowGroup{" +
                   "name='all'" +
                   ", tools=" + tools +
                   '}';
        }
    }

}
