package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultDockedTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultFloatingLiveTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultFloatingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultSlidingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.persistence.xml.XMLPersistenceDelegate;
import org.noos.xing.mydoggy.plaf.support.ResolvableHashtable;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ShortcutProcessor;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.drag.ContentManagerDropTarget;
import org.noos.xing.mydoggy.plaf.ui.look.MyDoggyResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.List;

/**
 * @author Angelo De Caro
 * @beaninfo attribute: isContainer true
 * description: MyDoggyToolWindowManager
 */
public class MyDoggyToolWindowManager extends JPanel implements ToolWindowManager, PropertyChangeListener {
    protected static final int COLUMN_LENGTH = 23;
    protected static final int ROW_LENGTH = 23;

    public final static Object sync = new Object();

    protected ToolWindowGroup showingGroup;

    protected MyDoggyContentManager contentManager;

    protected Window anchestor;

    protected MyDoggyToolWindowBar[] bars;
    protected Map<Object, ToolWindowDescriptor> tools;
    protected Map<Object, ToolWindowGroup> toolWindowGroups;
    protected Map<Object, ToolWindow> aliases;

    protected ToolWindowGroup allToolWindowGroup;

    protected TableLayout contentPaneLayout;

    protected JSplitPane mainSplitPane;
    protected JPanel mainContainer;

    protected PropertyChangeSupport propertyChangeSupport;

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


    public MyDoggyToolWindowManager(Window windowAnchestor) {
        this(windowAnchestor, Locale.getDefault(), null);
    }

    public MyDoggyToolWindowManager(Window windowAnchestor, Locale locale, ClassLoader uiClassLoader) {
        if (!(windowAnchestor instanceof RootPaneContainer))
            throw new IllegalArgumentException("WindowAnchestor must implement RootPaneContainer");

        this.anchestor = windowAnchestor;
        this.uiClassLoader = uiClassLoader;

        this.allToolWindowGroup = new AllToolWindowGroup();
        this.aliases = new HashMap<Object, ToolWindow>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.toolWindowManagerDescriptor = new MyDoggyToolWindowManagerDescriptor(this);
        this.toolWindowManagerDescriptor.addPropertyChangeListener(this);

        initUI(locale);
        initPersistenceDelegate();
        initComponents();
        initListeners();
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

        MyDoggyToolWindow toolWindow = new MyDoggyToolWindow(this,
                                                             anchestor, id,
                                                             index,
                                                             anchor,
                                                             ToolWindowType.DOCKED,
                                                             title, icon, component,
                                                             null);
        toolWindow.addInternalPropertyChangeListener(this);

        tools.put(toolWindow.getId(), toolWindow.getDescriptor());

        // fire Event
        fireRegisteredToolEvent(toolWindow);

        return toolWindow;
    }

    public void unregisterToolWindow(String id) {
        final ToolWindowDescriptor toolWindowDescriptor = tools.get(id);

        if (toolWindowDescriptor != null) {
            removeIfDockableDelegator(toolWindowDescriptor.getToolWindow());
            toolWindowDescriptor.unregister();
            toolWindowDescriptor.getToolWindow().setAvailable(false);

            tools.remove(toolWindowDescriptor.getToolWindow().getId());

            // Remove aliases
            for (Iterator<ToolWindow> iterator = aliases.values().iterator(); iterator.hasNext();) {
                ToolWindow toolWindow = iterator.next();
                if (toolWindow == toolWindowDescriptor.getToolWindow())
                    iterator.remove();
            }

            fireUnregisteredToolEvent(toolWindowDescriptor.getToolWindow());
        } else
            throw new IllegalArgumentException("Doesn't exist a tool window with passed id. [id : " + id + "]");
    }

    public void unregisterAllToolWindow() {
        for (Iterator<ToolWindowDescriptor> it = tools.values().iterator(); it.hasNext();) {
            ToolWindowDescriptor toolWindowDescriptor = it.next();

            removeIfDockableDelegator(toolWindowDescriptor.getToolWindow());
            toolWindowDescriptor.unregister();
            toolWindowDescriptor.getToolWindow().setAvailable(false);

            fireUnregisteredToolEvent(toolWindowDescriptor.getToolWindow());
            it.remove();
        }
        aliases.clear();
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

    public void addToolWindowManagerListener(ToolWindowManagerListener listener) {
        twmListeners.add(ToolWindowManagerListener.class, listener);
    }

    public void removeToolWindowManagerListener(ToolWindowManagerListener listener) {
        twmListeners.remove(ToolWindowManagerListener.class, listener);
    }

    public ToolWindowManagerListener[] getToolWindowManagerListeners() {
        return twmListeners.getListeners(ToolWindowManagerListener.class);
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

    public synchronized void propertyChange(final PropertyChangeEvent evt) {
        Object source = evt.getSource();
        if (source instanceof ToolWindowDescriptor) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) source;
            if (!tools.containsValue(descriptor)) {
                new RuntimeException("Manager doesn't contain that ToolWindow. [id : " + descriptor.getToolWindow().getId() + "]").printStackTrace();
                return;
            }
        } else if (!(source instanceof MyDoggyToolWindowBar) &&
                   !(source instanceof MyDoggyToolWindowManagerDescriptor) &&
                   !(source instanceof MyDoggyToolWindowManager)) {
            new RuntimeException("Illegal Source : " + source).printStackTrace();
            return;
        }

//        System.out.println(SwingUtil.toString(evt));

        propertyChangeSupport.firePropertyChange(evt);
    }


    public Window getAnchestor() {
        return anchestor;
    }

    public void setPersistenceDelegate(PersistenceDelegate persistenceDelegate) {
        this.persistenceDelegate = persistenceDelegate;
    }

    public void setUserResourceBundle(Locale locale, String bundle, ClassLoader classLoader) {
        resourceManager.setUserBundle(locale, bundle, classLoader);
    }

    public void setMainContent(Component content) {
        if (content == null)
            resetMainContent();
        
        mainContainer.setOpaque(false);
        mainContainer.removeAll();
        mainContainer.add(content, "0,0,FULL,FULL");

        mainSplitPane.invalidate();
        mainSplitPane.validate();

        SwingUtil.repaint(mainSplitPane);
    }

    public void resetMainContent() {
        mainContainer.removeAll();
        SwingUtil.repaint(mainSplitPane);
        mainContainer.setOpaque(true);
    }

    public Container getMainContainer() {
        return mainContainer;
    }

    public Component getMainContent() {
        return (mainContainer.getComponentCount() == 0) ? null : mainContainer.getComponent(0);
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
                for (Component cmp : getComponents()) {
                    if (contentPaneLayout.getConstraints(cmp).row1 == 0 &&
                        contentPaneLayout.getConstraints(cmp).col1 == 0)
                        remove(cmp);
                }

                add(component, "0,0,c,c");
                break;
            case SOUTH_WEST:
                for (Component cmp : getComponents()) {
                    if (contentPaneLayout.getConstraints(cmp).row1 == 2 &&
                        contentPaneLayout.getConstraints(cmp).col1 == 0) {
                        remove(cmp);
                    }
                }

                add(component, "0,2,c,c");
                break;
            case NORD_EAST:
                for (Component cmp : getComponents()) {
                    if (contentPaneLayout.getConstraints(cmp).row1 == 0 &&
                        contentPaneLayout.getConstraints(cmp).col1 == 2)
                        remove(cmp);
                }

                add(component, "2,0,c,c");
                break;
            case SOUTH_EAST:
                for (Component cmp : getComponents()) {
                    if (contentPaneLayout.getConstraints(cmp).row1 == 2 &&
                        contentPaneLayout.getConstraints(cmp).col1 == 2)
                        remove(cmp);
                }

                add(component, "2,2,c,c");
                break;
        }
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

        if (p.x <= 18 && p.y >= 18 && p.y <= b.height - 18) {
            return ToolWindowAnchor.LEFT;
        } else if (p.x >= b.width - 18 && p.y >= 18 && p.y <= b.height - 18) {
            return ToolWindowAnchor.RIGHT;
        } else if (p.y <= 18 && p.x >= 18 && p.x <= b.width - 18) {
            return ToolWindowAnchor.TOP;
        } else if (p.y >= b.height - 18 && p.x >= 18 && p.x <= b.width - 18) {
            return ToolWindowAnchor.BOTTOM;
        }
        return null;
    }

    public void removeIfDockableDelegator(Dockable dockable) {
        for (ToolWindow toolWindow : getToolWindows()) {
            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                if (tab.getDockableDelegator() == dockable) {
                    toolWindow.removeToolWindowTab(tab);
                    break;
                }
            }
        }
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


    protected void initPersistenceDelegate() {
        this.persistenceDelegate = new XMLPersistenceDelegate(this);
    }

    protected void initComponents() {
        this.twmListeners = new EventListenerList();

        initContentManager();

        resourceManager.applyCustomization(MyDoggyKeySpace.TOOL_WINDOW_MANAGER, this);

        // Init data structures
        bars = new MyDoggyToolWindowBar[4];
        tools = new LinkedHashMap<Object, ToolWindowDescriptor>();
        toolWindowGroups = new ResolvableHashtable<Object, ToolWindowGroup>(new ResolvableHashtable.Resolver<ToolWindowGroup>() {
            public ToolWindowGroup get(Object key) {
                ToolWindowGroup group = new MyDoggyToolWindowGroup(MyDoggyToolWindowManager.this, key.toString());
                toolWindowGroups.put(key, group);
                fireAddedGroupEvent(group);

                return group;
            }
        });

        // Init gui
        contentPaneLayout = new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {0, TableLayout.FILL, 0}});
        setLayout(contentPaneLayout);

        // Register bars, one for every anchor
        addBar(LEFT, JSplitPane.HORIZONTAL_SPLIT, "0,1", "0,0,FULL,FULL");
        addBar(RIGHT, JSplitPane.HORIZONTAL_SPLIT, "2,1", "2,0,FULL,FULL");
        addBar(TOP, JSplitPane.VERTICAL_SPLIT, "1,0", "2,2,FULL,FULL");
        addBar(BOTTOM, JSplitPane.VERTICAL_SPLIT, "1,2", "0,2,FULL,FULL");

        mainContainer = (JPanel) resourceManager.createComponent(MyDoggyKeySpace.TOOL_WINDOW_MANAGER_CONTENT_CONTAINER, this);
        mainContainer.setName("toolWindowManager.mainContainer");
        mainContainer.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        mainContainer.setFocusCycleRoot(true);

        getBar(BOTTOM).getSplitPane().setTopComponent(getBar(TOP).getSplitPane());
        getBar(TOP).getSplitPane().setBottomComponent(getBar(LEFT).getSplitPane());
        getBar(LEFT).getSplitPane().setRightComponent(getBar(RIGHT).getSplitPane());
        getBar(RIGHT).getSplitPane().setResizeWeight(1);

        add(getBar(BOTTOM).getSplitPane(), "1,1,FULL,FULL");

        mainSplitPane = getBar(RIGHT).getSplitPane();
        mainSplitPane.addPropertyChangeListener("UI", new UpdateUIChangeListener());
        mainSplitPane.setLeftComponent(mainContainer);

        // Init glass pane...
        initGlassPane();
    }

    protected void initContentManager() {
        this.contentManager = new MyDoggyContentManager(this);
        this.contentManager.setContentManagerUI(new MyDoggyTabbedContentManagerUI());
    }

    protected void initGlassPane() {
        RootPaneContainer rootPaneContainer = (RootPaneContainer) anchestor;
        this.glassPanel = new GlassPanel(resourceManager, rootPaneContainer);
//        rootPaneContainer.setGlassPane(this.glassPanel = new GlassPanel(rootPaneContainer));
    }

    protected void initListeners() {
        AvailablePropertyChangeListener availablePropertyChangeListener = new AvailablePropertyChangeListener();
        propertyChangeSupport.addPropertyChangeListener("available", availablePropertyChangeListener);
        propertyChangeSupport.addPropertyChangeListener("representativeAnchorButtonVisible", availablePropertyChangeListener);
        propertyChangeSupport.addPropertyChangeListener("showUnavailableTools", new ShowUnavailableToolsPropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("visible", new VisiblePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("active", new ActivePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("anchor", new AnchorPropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("type", new TypePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("autoHide", new AutoHideChangeListener());

        MaximizedChangeListener maximizedChangeListener = new MaximizedChangeListener();
        propertyChangeSupport.addPropertyChangeListener("maximized", maximizedChangeListener);
        propertyChangeSupport.addPropertyChangeListener("maximized.before", maximizedChangeListener);
        propertyChangeSupport.addPropertyChangeListener("index", new IndexChangeListener());
        propertyChangeSupport.addPropertyChangeListener("icon", new IconChangeListener());
        propertyChangeSupport.addPropertyChangeListener("title", new TitleChangeListener());
        propertyChangeSupport.addPropertyChangeListener("numberingEnabled", new NumberingEnabledChangeListener());
        propertyChangeSupport.addPropertyChangeListener("tempShowed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                for (ToolWindowDescriptor tool : tools.values())
                    tool.getToolWindowContainer().propertyChange(evt);
            }
        });
        propertyChangeSupport.addPropertyChangeListener("anchor.index", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

                if (descriptor.getToolWindow().getType() == ToolWindowType.DOCKED)
                    getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
            }
        });
        propertyChangeSupport.addPropertyChangeListener("anchestor.closed", new AnchorClosedChangeListener());
        propertyChangeSupport.addPropertyChangeListener("resourceManager", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                glassPanel.setResourceManager((ResourceManager) evt.getNewValue());
            }
        });

        anchestor.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                propertyChangeSupport.firePropertyChange(
                        new PropertyChangeEvent(MyDoggyToolWindowManager.this, "anchestor.closed", true, false)
                );
            }
        });

        initKeyboardFocusManagerListeners();

        // Setup DropTarget for main container
        mainContainer.setDropTarget(new ContentManagerDropTarget(mainContainer, this, resourceManager));
    }

    protected void initKeyboardFocusManagerListeners() {
        final ShortcutProcessor shortcutProcessor = new ShortcutProcessor(this, this);
        final FocusOwnerChangeListener focusOwnerChangeListener = new FocusOwnerChangeListener();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(shortcutProcessor);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusOwnerChangeListener);

        propertyChangeSupport.addPropertyChangeListener("anchestor.closed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(shortcutProcessor);
                KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", focusOwnerChangeListener);
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
        return (JSplitPane) resourceManager.createComponent(MyDoggyKeySpace.ANCHOR_SPLIT_PANE, this, orientation);
    }

    protected JSplitPane addBar(ToolWindowAnchor anchor, int splitPaneOrientation,
                                String barConstraints, String cornerConstraints) {
        // Initialize bar
        bars[anchor.ordinal()] = new MyDoggyToolWindowBar(this,
                                                          renderSplitPane(splitPaneOrientation),
                                                          anchor);

        // Add Bar to Container
        add(bars[anchor.ordinal()].getToolScrollBar(), barConstraints);

        // Add Corner to Container
        add(resourceManager.createComponent(MyDoggyKeySpace.CORNER_CONTENT_PANE, this),
            cornerConstraints);

        return bars[anchor.ordinal()].getSplitPane();
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
                contentPaneLayout.setColumn(0, COLUMN_LENGTH);
                revalidate = true;
            }
        } else if (anchor == RIGHT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getColumn(2) != 0) {
                contentPaneLayout.setColumn(2, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getColumn(2) == 0) {
                contentPaneLayout.setColumn(2, COLUMN_LENGTH);
                revalidate = true;
            }
        } else if (anchor == TOP) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getRow(0) != 0) {
                contentPaneLayout.setRow(0, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getRow(0) == 0) {
                contentPaneLayout.setRow(0, ROW_LENGTH);
                revalidate = true;
            }
        } else if (anchor == BOTTOM) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getRow(2) != 0) {
                contentPaneLayout.setRow(2, 0);
                revalidate = true;
            } else
            if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getRow(2) == 0) {
                contentPaneLayout.setRow(2, ROW_LENGTH);
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


    protected class AvailablePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            ToolWindowAnchor target = descriptor.getToolWindow().getAnchor();
            // Notify specific bar
            getBar(target).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(target);
        }
    }

    protected class ShowUnavailableToolsPropertyChangeListener implements PropertyChangeListener {

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

    protected class VisiblePropertyChangeListener implements PropertyChangeListener {
        boolean showingGroupValueAdj = false;

        public void propertyChange(PropertyChangeEvent evt) {
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

    protected class ActivePropertyChangeListener implements PropertyChangeListener {

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
                        if (bar.valueAdjusting && getBar(descriptor.getToolWindow().getAnchor()) == bar) {
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

    protected class AnchorPropertyChangeListener implements PropertyChangeListener {

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
                force || !descriptor.getToolWindow().isAvailable()) {

                PropertyChangeEvent avEvent = new UserPropertyChangeEvent(evt.getSource(), "available", true, false, -1);
                getBar(oldAnchor).propertyChange(avEvent);
                syncPanel(oldAnchor);

                assert evt instanceof UserPropertyChangeEvent;
                avEvent = new UserPropertyChangeEvent(evt.getSource(), "available", false, true,
                                                      ((UserPropertyChangeEvent) evt).getUserObject());
                getBar(newAnchor).propertyChange(avEvent);
                syncPanel(newAnchor);
            }

            for (ToolWindowDescriptor tool : tools.values())
                tool.getToolWindowContainer().propertyChange(evt);

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

    protected static class AutoHideChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ((ToolWindowDescriptor) evt.getSource()).getToolWindowContainer().propertyChange(evt);
        }
    }

    protected class TypePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor window = (ToolWindowDescriptor) evt.getSource();

            getBar(window.getToolWindow().getAnchor()).propertyChange(evt);

            for (ToolWindowDescriptor tool : tools.values())
                tool.getToolWindowContainer().propertyChange(evt);

            syncPanel(window.getToolWindow().getAnchor());
        }
    }

    protected class IndexChangeListener implements PropertyChangeListener {
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

            descriptor.propertyChange(evt);
            getBar(modifiedTool.getAnchor()).propertyChange(evt);
        }
    }

    protected class IconChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            descriptor.propertyChange(evt);
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
        }
    }

    protected class TitleChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            descriptor.getToolWindowContainer().propertyChange(evt);
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
        }
    }

    protected class NumberingEnabledChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            for (ToolWindowDescriptor descriptor : tools.values())
                descriptor.propertyChange(evt);
        }
    }

    protected class UpdateUIChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            contentManager.updateUI();

            for (ToolWindowDescriptor descriptor : tools.values()) {
                descriptor.updateUI();
            }
        }
    }

    protected class MaximizedChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);

            // Notify specific bar
            getBar(toolWindowDescriptor.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(toolWindowDescriptor.getToolWindow().getAnchor());
        }
    }

    protected class ResourceManagerListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            SwingUtil.repaint(MyDoggyToolWindowManager.this);
        }
    }

    protected class FocusOwnerChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Component newFocusOwner = (Component) evt.getNewValue();
            if (newFocusOwner != null && SwingUtilities.isDescendingFrom(newFocusOwner, mainContainer))
                lastFocusOwner = newFocusOwner;
        }
    }

    protected class AnchorClosedChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            for (ToolWindowDescriptor tool : tools.values())
                tool.getToolWindowContainer().propertyChange(evt);
        }
    }

    static class DummyPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
//            System.out.println("NTW - DUMMY : " + evt);
        }
    }

    class AllToolWindowGroup extends MyDoggyToolWindowGroup {

        AllToolWindowGroup() {
            super(MyDoggyToolWindowManager.this, "all");
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
