package org.noos.xing.mydoggy.plaf;

import static org.noos.xing.mydoggy.plaf.ui.ToolWindowManagerUI.*;
import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultDockedTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultFloatingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultSlidingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.persistence.xml.XMLPersistenceDelegate;
import org.noos.xing.mydoggy.plaf.support.ResolvableHashtable;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ShortcutProcessor;
import org.noos.xing.mydoggy.plaf.ui.content.MyDoggyTabbedContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * @author Angelo De Caro
 * @beaninfo attribute: isContainer true
 * description: MyDoggyToolWindowManager
 */
public class MyDoggyToolWindowManager extends JPanel implements ToolWindowManager, PropertyChangeListener {
    private static final int COLUMN_LENGTH = 23;
    private static final int ROW_LENGTH = 23;

    public final static Object sync = new Object();

    private ToolWindowGroup showingGroup;
    private boolean shiftShow;

    protected MyDoggyContentManager contentManager;

    private Window anchestor;

    private MyDoggyToolWindowBar[] bars;
    private Map<Object, ToolWindowDescriptor> tools;
    private Map<Object, ToolWindowGroup> toolWindowGroups;
    private Map<Object, ToolWindow> aliases;

    private ToolWindowGroup allToolWindowGroup;

    private TableLayout contentPaneLayout;

    private JSplitPane mainSplitPane;
    private JPanel mainContainer;

    private PropertyChangeSupport propertyChangeSupport;

    private Object activeToolWindowId;

    private GlassPanel glassPanel;
    private Component lastFocusOwner = null;

    private PersistenceDelegate persistenceDelegate;

    // Type Descriptors Template.
    private DefaultFloatingTypeDescriptor floatingTypeDescriptor;
    private DefaultDockedTypeDescriptor dockingTypeDescriptor;
    private DefaultSlidingTypeDescriptor slidingTypeDescriptor;

    private ToolWindowManagerDescriptor toolWindowManagerDescriptor;

    // ToolWindwoManager Listener List
    private EventListenerList twmListeners;


    protected ToolWindowUI toolWindowUI;
    protected ToolWindowManagerUI toolWindowManagerUI;


    public MyDoggyToolWindowManager(Window windowAnchestor) {
        this(windowAnchestor, Locale.getDefault());
    }

    public MyDoggyToolWindowManager(Window windowAnchestor, Locale locale) {
        if (!(windowAnchestor instanceof RootPaneContainer))
            throw new IllegalArgumentException("WindowAnchestor must implement RootPaneContainer");

        this.anchestor = windowAnchestor;

        this.persistenceDelegate = new XMLPersistenceDelegate(this);
        this.allToolWindowGroup = new AllToolWindowGroup();
        this.aliases = new Hashtable<Object, ToolWindow>();
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.toolWindowManagerDescriptor = new MyDoggyToolWindowManagerDescriptor(this);
        this.toolWindowManagerDescriptor.addPropertyChangeListener(this);

        initResourceBoundles(locale);
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

        if (tools.containsKey(id))
            throw new IllegalArgumentException("Cannot register tool window with passed id. An already registered tool exists. [id : " + id + "]");

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
        ToolWindowDescriptor toolWindowDescriptor = tools.get(id);

        if (toolWindowDescriptor != null) {
            toolWindowDescriptor.unregister();
            toolWindowDescriptor.getToolWindow().setAvailable(false);

            tools.remove(toolWindowDescriptor.getToolWindow().getId());

            fireUnregisteredToolEvent(toolWindowDescriptor.getToolWindow());
        } else
            throw new IllegalArgumentException("Doesn't exist a tool window with passed id. [id : " + id + "]");
    }

    public void unregisterAllToolWindow() {
        for (Iterator<ToolWindowDescriptor> it = tools.values().iterator(); it.hasNext();) {
            ToolWindowDescriptor toolWindowDescriptor = it.next();
            toolWindowDescriptor.unregister();
            toolWindowDescriptor.getToolWindow().setAvailable(false);

            fireUnregisteredToolEvent(toolWindowDescriptor.getToolWindow());
            it.remove();
        }
    }

    public void addAlias(ToolWindow toolWindow, Object alias) {
        aliases.put(alias, toolWindow);
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

    public ToolWindow getToolWindow(Object id) {
        ToolWindowDescriptor descriptor = tools.get(id);

        return (descriptor != null) ? descriptor.getToolWindow() : null;
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
        ToolWindowGroup group = toolWindowGroups.remove(name);
        if (group != null) {
            fireRemovedGroupEvent(group);
            return true;
        }
        return false;
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
            case DOCKED:
                if (dockingTypeDescriptor == null)
                    dockingTypeDescriptor = new DefaultDockedTypeDescriptor();
                return dockingTypeDescriptor;
            case SLIDING:
                if (slidingTypeDescriptor == null)
                    slidingTypeDescriptor = new DefaultSlidingTypeDescriptor();
                return slidingTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for : " + type);
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
        if (source instanceof ToolWindowDescriptor) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) source;
            if (!tools.containsValue(descriptor)) {
                new RuntimeException("Manager doesn't containe that ToolWindow. [id : " + descriptor.getToolWindow().getId() + "]").printStackTrace();
                return;
            }
        } else
        if (!(source instanceof MyDoggyToolWindowBar) && !(source instanceof MyDoggyToolWindowManagerDescriptor)) {
            new RuntimeException("Illegal Source : " + source).printStackTrace();
            return;
        }

        propertyChangeSupport.firePropertyChange(evt);
    }


    public Window getAnchestor() {
        return anchestor;
    }

    public void setPersistenceDelegate(PersistenceDelegate persistenceDelegate) {
        this.persistenceDelegate = persistenceDelegate;
    }

    public void initUserResourceBundle(Locale locale, String bundle, ClassLoader classLoader) {
        ResourceBundleManager.getInstance().initUserBundle(locale, bundle, classLoader);
    }

    public void setMainContent(Component content) {
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

    public ToolWindowUI getToolWindowUI() {
        return toolWindowUI;
    }

    public ToolWindowManagerUI getToolWindowManagerUI() {
        return toolWindowManagerUI;
    }
    

    protected void initResourceBoundles(Locale locale) {
        ResourceBundleManager.getInstance().init(locale);
    }

    protected void initComponents() {
        this.twmListeners = new EventListenerList();

        initUI();
        initContentManager();

        toolWindowManagerUI.applyCustomization(MY_DOGGY_MANAGER_PANEL, this);

        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

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

        mainContainer = (JPanel) toolWindowManagerUI.createComponent(MY_DOGGY_MANAGER_MAIN_CONTAINER, this);
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
        rootPaneContainer.setGlassPane(this.glassPanel = new GlassPanel(rootPaneContainer));
    }

    protected void initListeners() {
        propertyChangeSupport.addPropertyChangeListener("available", new AvailablePropertyChangeListener());
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

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new ShortcutProcessor(this, this));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Component newFocusOwner = (Component) evt.getNewValue();
                if (newFocusOwner != null && SwingUtilities.isDescendingFrom(newFocusOwner, mainContainer))
                    lastFocusOwner = newFocusOwner;
            }
        });
    }

    protected void initUI() {
        Properties properties = SwingUtil.loadPropertiesFile("mydoggyplaf.properties", this.getClass().getClassLoader());

        this.toolWindowUI = (ToolWindowUI) SwingUtil.newObject(properties.getProperty("ToolWindowUI.class"));
        this.toolWindowManagerUI = (ToolWindowManagerUI) SwingUtil.newObject(properties.getProperty("ToolWindowManagerUI.class"));
    }

    protected JSplitPane renderSplitPane(int orientation) {
        return (JSplitPane) toolWindowManagerUI.createComponent(BAR_SPLIT_PANE, this, orientation);
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


    protected JSplitPane addBar(ToolWindowAnchor anchor, int splitPaneOrientation,
                              String barConstraints, String cornerConstraints) {
        // Initialize bar
        bars[anchor.ordinal()] = new MyDoggyToolWindowBar(this,
                                                          renderSplitPane(splitPaneOrientation),
                                                          anchor);

        // Add Bar to Container
        add(bars[anchor.ordinal()].getToolScrollBar(), barConstraints);

        // Add Corner to Container
        add(toolWindowManagerUI.createComponent(CORNER_CONTENT_PANE, this),
            cornerConstraints);

        return bars[anchor.ordinal()].getSplitPane();
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

    void resetShowingGroup() {
        if (showingGroup == getToolWindowGroup())
            this.showingGroup = null;
        this.shiftShow = false;
    }

    boolean isShiftShow() {
        return shiftShow;
    }

    void enableShiftShow() {
        if (showingGroup == null)
            setShowingGroup(allToolWindowGroup);
        this.shiftShow = true;
    }

    void resetShiftShow() {
        resetShowingGroup();
    }

    void addInternalPropertyChangeListener(String property, PropertyChangeListener propertyChangeListener) {
        propertyChangeSupport.addPropertyChangeListener(property, propertyChangeListener);
    }


    class AvailablePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor window = (ToolWindowDescriptor) evt.getSource();

            // Notify specific bar
            getBar(window.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(window.getToolWindow().getAnchor());
        }
    }

    class VisiblePropertyChangeListener implements PropertyChangeListener {
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

            event = new PropertyChangeEvent(evt.getSource(), "visible.after",
                                            evt.getOldValue(), evt.getNewValue());

            for (MyDoggyToolWindowBar bar : bars)
                bar.propertyChange(event);
        }
    }

    class ActivePropertyChangeListener implements PropertyChangeListener {

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
                    if (shouldRequest) {
                        SwingUtil.requestFocus(lastFocusOwner);
//                        lastFocusOwner.requestFocusInWindow();
                    }
                }
            } else
                activeToolWindowId = descriptor.getToolWindow().getId();
        }
    }

    class AnchorPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            ToolWindowAnchor oldAnchor = (ToolWindowAnchor) evt.getOldValue();
            ToolWindowAnchor newAnchor = (ToolWindowAnchor) evt.getNewValue();
            if (oldAnchor == null)
                oldAnchor = newAnchor;

            ToolWindowType toolType = descriptor.getToolWindow().getType();
            if (toolType == ToolWindowType.FLOATING || toolType == ToolWindowType.FLOATING_FREE) {
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
        }
    }

    static class AutoHideChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ((ToolWindowDescriptor) evt.getSource()).getToolWindowContainer().propertyChange(evt);
        }
    }

    class TypePropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor window = (ToolWindowDescriptor) evt.getSource();

            getBar(window.getToolWindow().getAnchor()).propertyChange(evt);

            for (ToolWindowDescriptor tool : tools.values())
                tool.getToolWindowContainer().propertyChange(evt);

            syncPanel(window.getToolWindow().getAnchor());
        }
    }

    class IndexChangeListener implements PropertyChangeListener {
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

    class IconChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            descriptor.propertyChange(evt);
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
        }
    }

    class TitleChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

            descriptor.getToolWindowContainer().propertyChange(evt);
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
        }
    }

    class NumberingEnabledChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            for (ToolWindowDescriptor descriptor : tools.values()) 
                descriptor.propertyChange(evt);
        }
    }

    class UpdateUIChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            contentManager.updateUI();

            for (ToolWindowDescriptor descriptor : tools.values()) {
                descriptor.updateUI();
            }
        }
    }

    class MaximizedChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor toolWindowDescriptor = (ToolWindowDescriptor) evt.getSource();
            toolWindowDescriptor.getToolWindowContainer().propertyChange(evt);

            // Notify specific bar
            getBar(toolWindowDescriptor.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(toolWindowDescriptor.getToolWindow().getAnchor());
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
