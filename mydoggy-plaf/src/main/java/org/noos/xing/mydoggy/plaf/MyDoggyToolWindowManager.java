package org.noos.xing.mydoggy.plaf;

import info.clearthought.layout.TableLayout;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultDockedTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultFloatingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.DefaultSlidingTypeDescriptor;
import org.noos.xing.mydoggy.plaf.support.ResolvableHashtable;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.MyDoggyTabbedContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.persistence.xml.XmlPersistenceDelegate;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.SplitPaneUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

/**
 * @author Angelo De Caro
 */
public class MyDoggyToolWindowManager extends JPanel implements ToolWindowManager, PropertyChangeListener, KeyEventPostProcessor {
    private static final int COLUMN_LENGTH = 23;
    private static final int ROW_LENGTH = 23;

    public final static Object sync = new Object();

    private ToolWindowGroup showingGroup;
    private boolean shiftShow;

    private MyDoggyContentManager contentManager;

    private Window anchestor;

    private MyDoggyToolWindowBar[] bars;
    private Map<Object, ToolWindowDescriptor> tools;
    private Map<Object, ToolWindowGroup> toolWindowGroups;
    private Map<Object, ToolWindow> aliases;

    private ToolWindowGroup allToolWindowGroup;

    private TableLayout contentPaneLayout;

    JSplitPane mainSplitPane;
    JPanel mainContainer;

    private PropertyChangeSupport propertyChangeSupport;

    private Object activeToolWindowId;

    private Component lastFocusOwner = null;

    // Type Descriptors Template.
    private DefaultFloatingTypeDescriptor floatingTypeDescriptor;
    private DefaultDockedTypeDescriptor dockingTypeDescriptor;
    private DefaultSlidingTypeDescriptor slidingTypeDescriptor;

    private ToolWindowManagerDescriptor toolWindowManagerDescriptor;

    // ToolWindwoManager Listener List
    private EventListenerList twmListeners;

    private PersistenceDelegate persistenceDelegate;

    public MyDoggyToolWindowManager(Window windowAnchestor) {
        this(windowAnchestor, Locale.getDefault());
    }

    public MyDoggyToolWindowManager(Window windowAnchestor, Locale locale) {
        if (!(windowAnchestor instanceof RootPaneContainer))
            throw new IllegalArgumentException("WindowAnchestor must implement RootPaneContainer");

        this.anchestor = windowAnchestor;

        this.persistenceDelegate = new XmlPersistenceDelegate(this);
        this.allToolWindowGroup = new AllToolWindowGroup();
        this.aliases = new Hashtable<Object, ToolWindow>();
        this.toolWindowManagerDescriptor = new MyDoggyToolWindowManagerDescriptor(this);

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
        if (allToolWindowGroup.getName().equals(name))
            return true;
        return toolWindowGroups.containsKey(name);
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


    public boolean postProcessKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_TYPED) {
            if (e.isAltDown()) {
                if (Character.isDigit(e.getKeyChar())) {
                    int index = Character.getNumericValue(e.getKeyChar());

                    for (ToolWindow toolWindow : getToolWindows()) {
                        if (toolWindow.getIndex() == index) {
                            if (toolWindow.isAvailable()) {
                                if (toolWindow.isActive())
                                    toolWindow.setVisible(false);
                                else
                                    toolWindow.setActive(true);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return false;
    }

    public synchronized void propertyChange(final PropertyChangeEvent evt) {
        ToolWindowDescriptor descriptor = (ToolWindowDescriptor) evt.getSource();

        if (!tools.containsValue(descriptor)) {
//            System.out.println("Manager doesn't containe that ToolWindow. [id : " + descriptor.getToolWindow().getId() + "]");
            new RuntimeException("Manager doesn't containe that ToolWindow. [id : " + descriptor.getToolWindow().getId() + "]").printStackTrace();
            return;
        }

        propertyChangeSupport.firePropertyChange(evt);
    }


    public Window getAnchestor() {
        return anchestor;
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

    public Component getMainContent() {
        return (mainContainer.getComponentCount() == 0) ? null : mainContainer.getComponent(0);
    }

    public MyDoggyToolWindowBar getBar(ToolWindowAnchor anchor) {
        return bars[anchor.ordinal()];
    }


    protected void initResourceBoundles(Locale locale) {
        ResourceBoundles.initResourceBoundles(locale);
    }

    protected void initComponents() {
        this.twmListeners = new EventListenerList();
        this.contentManager = new MyDoggyContentManager(this);
        this.contentManager.setContentManagerUI(new MyDoggyTabbedContentManagerUI());

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
        addBar(LEFT, JSplitPane.HORIZONTAL_SPLIT, "0,1", "0,0,c,c");
        addBar(RIGHT, JSplitPane.HORIZONTAL_SPLIT, "2,1", "2,0,c,c");
        addBar(TOP, JSplitPane.VERTICAL_SPLIT, "1,0", "2,2,c,c");
        addBar(BOTTOM, JSplitPane.VERTICAL_SPLIT, "1,2", "0,2,c,c");

        mainContainer = new JPanel();
        mainContainer.setBackground(Color.GRAY);
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

        // Init glass pane used for SLIDING
        initGlassPane();
    }

    protected void initGlassPane() {
        RootPaneContainer rootPaneContainer = (RootPaneContainer) anchestor;
        rootPaneContainer.setGlassPane(new GlassPanel(rootPaneContainer));
    }

    protected void initListeners() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener("available", new AvailablePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("visible", new VisiblePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("active", new ActivePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("anchor", new AnchorPropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("type", new TypePropertyChangeListener());
        propertyChangeSupport.addPropertyChangeListener("autoHide", new AutoHideChangeListener());
        propertyChangeSupport.addPropertyChangeListener("index", new IndexChangeListener());
        propertyChangeSupport.addPropertyChangeListener("icon", new IconChangeListener());
        propertyChangeSupport.addPropertyChangeListener("title", new TitleChangeListener());

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                Component newFocusOwner = (Component) evt.getNewValue();
                if (newFocusOwner != null && SwingUtilities.isDescendingFrom(newFocusOwner, mainContainer))
                    lastFocusOwner = newFocusOwner;
            }
        });
    }


    protected JSplitPane renderSplitPane(int orientation) {
        JSplitPane splitPane = new JSplitPane(orientation) {
            public void setUI(SplitPaneUI ui) {
                super.setUI(ui);
                setBorder(null);
                setContinuousLayout(true);
            }
        };
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(300);
        splitPane.setLeftComponent(null);
        splitPane.setRightComponent(null);
        return splitPane;
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


    private JSplitPane addBar(ToolWindowAnchor anchor, int splitPaneOrientation,
                              String barConstraints, String cornerConstraints) {
        // Initialize bar
        bars[anchor.ordinal()] = new MyDoggyToolWindowBar(this,
                                                          renderSplitPane(splitPaneOrientation),
                                                          anchor);

        // Add Bar to Container
        add(bars[anchor.ordinal()].getToolScrollBar(), barConstraints);

        // Add Corner to Container
//        JLabel groupLabel = new JLabel("G");
//        groupLabel.addMouseListener(new GroupMouseListener());
        JPanel groupLabel = new JPanel();
        add(groupLabel, cornerConstraints);

        return bars[anchor.ordinal()].getSplitPane();
    }


    void syncPanel(ToolWindowAnchor anchor) {
        boolean revalidate = false;

        MyDoggyToolWindowBar toolWindowBar = getBar(anchor);

        if (anchor == LEFT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getColumn(0) != 0) {
                contentPaneLayout.setColumn(0, 0);
                revalidate = true;
            } else if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getColumn(0) == 0) {
                contentPaneLayout.setColumn(0, COLUMN_LENGTH);
                revalidate = true;
            }
        } else if (anchor == RIGHT) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getColumn(2) != 0) {
                contentPaneLayout.setColumn(2, 0);
                revalidate = true;
            } else if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getColumn(2) == 0) {
                contentPaneLayout.setColumn(2, COLUMN_LENGTH);
                revalidate = true;
            }
        } else if (anchor == TOP) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getRow(0) != 0) {
                contentPaneLayout.setRow(0, 0);
                revalidate = true;
            } else if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getRow(0) == 0) {
                contentPaneLayout.setRow(0, ROW_LENGTH);
                revalidate = true;
            }
        } else if (anchor == BOTTOM) {
            if (toolWindowBar.getAvailableTools() == 0 && !toolWindowBar.isTempShowed() && contentPaneLayout.getRow(2) != 0) {
                contentPaneLayout.setRow(2, 0);
                revalidate = true;
            } else if ((toolWindowBar.getAvailableTools() != 0 || toolWindowBar.isTempShowed()) && contentPaneLayout.getRow(2) == 0) {
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
        this.showingGroup = null;
        this.shiftShow = false;
    }

    boolean isShiftShow() {
        return shiftShow;
    }

    void enableShiftShow() {
        setShowingGroup(allToolWindowGroup);
        this.shiftShow = true;
    }

    void resetShiftShow() {
        resetShowingGroup();
    }

    public ToolWindowGroup getShowingGroup() {
        return this.showingGroup;
    }

    public ToolWindowDescriptor getDescriptor(ToolWindow toolWindow) {
        return tools.get(toolWindow.getId());
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
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor window = (ToolWindowDescriptor) evt.getSource();

            // Fire "visible.before" to all bars
            PropertyChangeEvent event = new PropertyChangeEvent(evt.getSource(), "visible.before",
                                                                evt.getOldValue(), evt.getNewValue());
            for (MyDoggyToolWindowBar bar : bars)
                bar.propertyChange(event);

            // Fire "visible" to specific bar
            getBar(window.getToolWindow().getAnchor()).propertyChange(evt);

            // Syncronize bars panel
            syncPanel(window.getToolWindow().getAnchor());
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
                        lastFocusOwner.requestFocusInWindow();
                    }
                }
            } else activeToolWindowId = descriptor.getToolWindow().getId();
        }
    }

    class AnchorPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowDescriptor window = (ToolWindowDescriptor) evt.getSource();

            ToolWindowType windowType = window.getToolWindow().getType();
            if (windowType == ToolWindowType.FLOATING || windowType == ToolWindowType.FLOATING_FREE) {
                ToolWindowAnchor oldAnchor = (ToolWindowAnchor) evt.getOldValue();
                ToolWindowAnchor newAnchor = (ToolWindowAnchor) evt.getNewValue();

                PropertyChangeEvent avEvent = new PropertyChangeEvent(evt.getSource(), "available", true, false);
                getBar(oldAnchor).propertyChange(avEvent);
                syncPanel(oldAnchor);

                avEvent = new PropertyChangeEvent(evt.getSource(), "available", false, true);
                getBar(newAnchor).propertyChange(avEvent);
                syncPanel(newAnchor);
            }

            for (ToolWindowDescriptor tool : tools.values())
                tool.getToolWindowContainer().propertyChange(evt);


            syncPanel((ToolWindowAnchor)evt.getOldValue());
            syncPanel((ToolWindowAnchor)evt.getNewValue());
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

            descriptor.propertyChange(evt);
            getBar(descriptor.getToolWindow().getAnchor()).propertyChange(evt);
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


    static class DummyPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            System.out.println("NTW - DUMMY : " + evt);
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

        public String toString() {
            return "MyDoggyToolWindowGroup{" +
                   "name='all'" +
                   ", tools=" + tools + 
                   '}';
        }
    }


}
