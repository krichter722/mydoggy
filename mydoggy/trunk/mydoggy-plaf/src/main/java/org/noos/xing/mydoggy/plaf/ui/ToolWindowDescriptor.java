package org.noos.xing.mydoggy.plaf.ui;

import org.noos.common.Question;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerAggregator;
import org.noos.xing.mydoggy.plaf.cleaner.DefaultCleanerAggregator;
import org.noos.xing.mydoggy.plaf.common.context.DefaultMutableContext;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.util.ToolWindowManagerListenerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowDescriptor implements PropertyChangeListener,
                                             DockableDescriptor {

    // TODO: comment this...
    private static Map<ToolWindow, FloatingLivePanel> livePanelMap = new HashMap<ToolWindow, FloatingLivePanel>();


    protected MyDoggyToolWindowManager manager;
    protected MyDoggyToolWindow toolWindow;
    protected CleanerAggregator cleaner;

    // Components
    protected ToolWindowPanel toolWindowPanel;

    // Listeners
    PropertyChangeListener focusListener;

    // Containers
    protected DockedContainer dockedContainer;
    protected FloatingContainer floatingContainer;
    protected SlidingContainer slidingContainer;
    protected FloatingLiveContainer floatingLiveContainer;

    protected Component focusRequester;
    protected Component component;

    // Anchor
    protected JLabel representativeAnchor;

    protected int divederLocation = -1;
    protected int tempDivederLocation;

    //  Descriptors
    protected FloatingTypeDescriptor floatingTypeDescriptor;
    protected DockedTypeDescriptor dockedTypeDescriptor;
    protected SlidingTypeDescriptor slidingTypeDescriptor;
    protected FloatingLiveTypeDescriptor floatingLiveTypeDescriptor;

    protected boolean floatingWindow = false;

    // TODO: change this names...
    boolean valueAdj = false;
    boolean valueAdjusting;
    boolean focusValueAdjusting = false;

    // Popup menu fiedls
    protected ToolWindowType oldType;

    protected JPopupMenu popupMenu;
    protected JMenuItem visible;
    protected JMenuItem aggregate;
    protected JMenu aggregateMenu;
    protected JCheckBoxMenuItem floatingMode;
    protected JCheckBoxMenuItem floatingLiveMode;
    protected JCheckBoxMenuItem dockedMode;
    protected JCheckBoxMenuItem pinnedMode;

    protected JMenu old;

    protected JMenuItem maximize;
    protected JMenu moveTo;
    protected JMenuItem right;
    protected JMenuItem left;
    protected JMenuItem top;
    protected JMenuItem bottom;

    protected ArrayList<PopupUpdater> popupUpdaterList;


    public ToolWindowDescriptor(MyDoggyToolWindowManager manager,
                                MyDoggyToolWindow toolWindow) {
        //  Init manager and toolwindw
        this.manager = manager;
        this.toolWindow = toolWindow;

        // Init cleaner
        this.cleaner = new ToolWindowDescriptorCleaner();

        initListeners();
        initTypeDescriptors();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();

        if ("type".equals(propertyName)) {
            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.FLOATING_FREE)
                setFloatingWindow(true);
            else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                setFloatingWindow(false);

            initPopupMenu();
            ToolWindowType type = (ToolWindowType) evt.getNewValue();
            dockedMode.setState(type == ToolWindowType.DOCKED);
            dockedMode.setVisible(type != ToolWindowType.FLOATING);
//                pinnedMode.setVisible(type != ToolWindowType.SLIDING);
            floatingMode.setState(type == ToolWindowType.FLOATING);

            if (evt.getSource() != this ||
                (evt.getNewValue() != ToolWindowType.FLOATING &&
                 evt.getNewValue() != ToolWindowType.FLOATING_FREE))
                return;

            oldType = (ToolWindowType) evt.getOldValue();
        } else if ("index".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("numberingEnabled".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("icon".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("representativeAnchorButtonTitle".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("dockLength".equals(propertyName)) {
            if (!valueAdj) {
                this.divederLocation = (Integer) evt.getNewValue();
                getToolBar(toolWindow.getAnchor()).propertyChange(
                        new PropertyChangeEvent(toolWindow,
                                                propertyName,
                                                evt.getOldValue(),
                                                evt.getNewValue()
                        )
                );
            }
        } else if ("autoHide".equals(propertyName)) {
            initPopupMenu();
            pinnedMode.setState(!(Boolean) evt.getNewValue());
        } else if ("hideRepresentativeButtonOnVisible".equals(propertyName)) {
            if (toolWindow.isVisible())
                toolWindow.setRepresentativeAnchorButtonVisible(!(Boolean) evt.getNewValue());
        } else if ("UI".equals(evt.getPropertyName())) {
            initPopupMenu();
            SwingUtilities.updateComponentTreeUI(popupMenu);

            DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
            SwingUtilities.updateComponentTreeUI(descriptor.getToolsMenu());
        }
    }

    public String toString() {
        return "ToolWindowDescriptor{" +
               "toolWindow=" + toolWindow +
               '}';
    }


    public ToolWindowAnchor getAnchor() {
        return toolWindow.getAnchor();
    }

    public void setAvailable(boolean available) {
        toolWindow.setAvailable(available);
    }

    public boolean isAvailable() {
        return toolWindow.isAvailable();
    }

    public DockableType getDockableType() {
        return DockableType.TOOL_WINDOW;
    }

    public Dockable getDockable() {
        return toolWindow;
    }

    public JLabel getRepresentativeAnchor(Component parent) {
        if (representativeAnchor == null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String labelText = SwingUtil.getUserString(toolWindow.getRepresentativeAnchorButtonTitle());
            String toolRepresentativeAnchorText = (toolWindow.getIndex() > 0 && getManager().getToolWindowManagerDescriptor().isNumberingEnabled())
                                                  ? toolWindow.getIndex() + " : " + labelText
                                                  : labelText;
            Icon toolIcon = toolWindow.isAvailable() || toolWindow.getIcon() == null ? toolWindow.getIcon()
                            : new ImageIcon(GrayFilter.createDisabledImage(
                    GraphicsUtil.getImage(representativeAnchor, toolWindow.getIcon()))
            );

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, toolRepresentativeAnchorText, toolIcon, JLabel.CENTER);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(parent, toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(parent, toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
                    break;
            }

            representativeAnchor.setName("toolWindow.rb." + toolWindow.getId());
            representativeAnchor.setOpaque(toolWindow.isActive());
            representativeAnchor.setFocusable(false);
            representativeAnchor.putClientProperty(ToolWindowDescriptor.class, this);
        }
        return representativeAnchor;
    }

    public JLabel getRepresentativeAnchor() {
        return representativeAnchor;
    }

    public void resetRepresentativeAnchor() {
        if (representativeAnchor != null)
            representativeAnchor.putClientProperty(ToolWindowDescriptor.class, null);
        representativeAnchor = null;
    }

    public int getAnchorIndex() {
        if (representativeAnchor == null)
            return -1;
        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }

    public void updateRepresentativeAnchor() {
        if (representativeAnchor != null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String labelText = SwingUtil.getUserString(toolWindow.getRepresentativeAnchorButtonTitle());
            String toolRepresentativeAnchorText = (toolWindow.getIndex() > 0 && getManager().getToolWindowManagerDescriptor().isNumberingEnabled())
                                                  ? toolWindow.getIndex() + " : " + labelText
                                                  : labelText;
            Icon toolIcon = toolWindow.isAvailable() || toolWindow.getIcon() == null ? toolWindow.getIcon()
                            : new ImageIcon(GrayFilter.createDisabledImage(
                    GraphicsUtil.getImage(representativeAnchor, toolWindow.getIcon()))
            );

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor.setIcon(toolIcon);
                    representativeAnchor.setText(toolRepresentativeAnchorText);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getLeftIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getRightIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
            }
        }
    }

    public MyDoggyToolWindowManager getManager() {
        return manager;
    }

    public MyDoggyToolWindowBar getToolBar(ToolWindowAnchor anchor) {
        return manager.getBar(anchor);
    }

    public MyDoggyToolWindowBar getToolBar() {
        return manager.getBar(toolWindow.getAnchor());
    }

    public boolean isDragImageAvailable() {
        return true;
    }

    public Component getComponentForDragImage() {
        return getToolWindowPanel();
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        toolWindow.setAnchor(anchor, index);
    }

    public CleanerAggregator getCleaner() {
        return cleaner;
    }

    public void cleanup() {
        getCleaner().cleanup();
    }


    public MyDoggyToolWindow getToolWindow() {
        return toolWindow;
    }


    public ToolWindowPanel getToolWindowPanel() {
        return toolWindowPanel;
    }

    public Component getComponent() {
        if (component == null) {
            if (toolWindow.getToolWindowTabs().length > 0)
                component = toolWindow.getToolWindowTabs()[0].getComponent();
        }
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public int getDividerLocation() {
        if (divederLocation == -1)
            this.divederLocation = ((DockedTypeDescriptor) getTypeDescriptor(ToolWindowType.DOCKED)).getDockLength();

        return divederLocation;
    }

    public void setDividerLocation(int divederLocation) {
        if (divederLocation <= 0)
            return;

        this.divederLocation = divederLocation;

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        valueAdj = true;
        try {
            dockedTypeDescriptor.setDockLength(divederLocation);
        } finally {
            valueAdj = false;
        }
    }

    public int getTempDivederLocation() {
        return tempDivederLocation;
    }

    public void setTempDivederLocation(int tempDivederLocation) {
        this.tempDivederLocation = tempDivederLocation;
    }

    public boolean isFloatingWindow() {
        return floatingWindow;
    }

    public void setFloatingWindow(boolean floatingWindow) {
        this.floatingWindow = floatingWindow;
    }

    public boolean isIdVisibleOnTitleBar() {
        switch (toolWindow.getType()) {
            case DOCKED:
                return toolWindow.getTypeDescriptor(ToolWindowType.DOCKED).isIdVisibleOnTitleBar();
            case SLIDING:
                return toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).isIdVisibleOnTitleBar();
            case FLOATING:
            case FLOATING_FREE:
                return toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).isIdVisibleOnTitleBar();
            case FLOATING_LIVE:
                return toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isIdVisibleOnTitleBar();
            case EXTERN:
                return true;
        }
        throw new IllegalStateException("ToolWindowDescriptor.isIdVisibleOnTitleBar");
    }

    public void updateUI() {
        // TODO: check this procedure..
        getToolWindowContainer().updateUI();

        SwingUtilities.updateComponentTreeUI(getComponent());

        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            SwingUtilities.updateComponentTreeUI(tab.getComponent());
        }

        if (getRepresentativeAnchor() != null)
            getRepresentativeAnchor().updateUI();
    }


    public Rectangle getManagerBounds() {
        return SwingUtilities.convertRectangle(manager,
                                               manager.getBounds(),
                                               manager.getRootPaneContainer().getContentPane());
    }

    public Window getWindowAncestor() {
        return manager.getWindowAncestor() instanceof Window ? (Window) manager.getWindowAncestor() : null;
    }

    public Window getAncestorForWindow() {
        return SwingUtil.getBoolean("dialog.owner.enabled", true) ? getWindowAncestor() : null;
    }

    public ToolWindowContainer getToolWindowContainer() {
        if (dockedContainer == null)
            initContainers();

        return dockedContainer;
    }

    public ToolWindowContainer getToolWindowContainer(ToolWindowType toolWindowType) {
        if (dockedContainer == null)
            initContainers();

        switch (toolWindowType) {
            case FLOATING:
            case FLOATING_FREE:
                return floatingContainer;
            case FLOATING_LIVE:
                return floatingLiveContainer;
            case DOCKED:
                return dockedContainer;
            case SLIDING:
                return slidingContainer;
        }
        throw new IllegalArgumentException("Type not recognized.");
    }

    public ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type) {
        switch (type) {
            case FLOATING:
            case FLOATING_FREE:
                return floatingTypeDescriptor;
            case FLOATING_LIVE:
                return floatingLiveTypeDescriptor;
            case DOCKED:
                return dockedTypeDescriptor;
            case SLIDING:
                return slidingTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for. [type : " + type + "]");
    }

    public DockedTypeDescriptor getDockedTypeDescriptor() {
        return dockedTypeDescriptor;
    }

    public FloatingContainer getFloatingContainer() {
        return floatingContainer;
    }

    public Component getContentContainer() {
        return getToolWindowPanel();
    }

    public void hideToolWindow() {
        ToolWindowActionHandler toolWindowActionHandler = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolWindowActionHandler();
        if (toolWindowActionHandler != null)
            toolWindowActionHandler.onHideButtonClick(toolWindow);
        else
            toolWindow.setVisible(false);
    }

    public void addTypeDescriptorChangePropertyListener(PropertyChangeListener listener) {
        floatingTypeDescriptor.addPropertyChangeListener(listener);
        floatingLiveTypeDescriptor.addPropertyChangeListener(listener);
        dockedTypeDescriptor.addPropertyChangeListener(listener);
        slidingTypeDescriptor.addPropertyChangeListener(listener);
    }

    public void removeTypeDescriptorChangePropertyListener(PropertyChangeListener listener) {
        floatingTypeDescriptor.removePropertyChangeListener(listener);
        floatingLiveTypeDescriptor.removePropertyChangeListener(listener);
        dockedTypeDescriptor.removePropertyChangeListener(listener);
        slidingTypeDescriptor.removePropertyChangeListener(listener);
    }

    public void showPopupMenu(Component source, int x, int y) {
        initPopupMenu();

        ToolWindowTitleBar toolWindowTitleBar = toolWindowPanel.getToolWindowTitleBar();
        if (source == toolWindowTitleBar ||
            SwingUtil.hasParent(source, toolWindowTitleBar) ||
            source instanceof ToolWindowRepresentativeAnchor) {

            popupMenu.removeAll();
            popupMenu.add(pinnedMode);
            popupMenu.add(dockedMode);
            popupMenu.add(floatingMode);
            popupMenu.add(floatingLiveMode);
            popupMenu.add(moveTo);
            popupMenu.add(maximize);
            popupMenu.addSeparator();
            popupMenu.add(visible);
            popupMenu.add(aggregate);
            popupMenu.add(aggregateMenu);

            enableVisible();
            enableMoveToItem();
            enableUserDefined();
            enableMaximize();

            if (popupUpdaterList != null) {
                for (PopupUpdater popupUpdater : popupUpdaterList) {
                    popupUpdater.update(source, popupMenu);
                }
            }

            popupMenu.show(source, x, y);
        }

    }

    public void addPopupUpdater(PopupUpdater popupUpdater) {
        if (popupUpdaterList == null)
            popupUpdaterList = new ArrayList<PopupUpdater>();

        popupUpdaterList.add(popupUpdater);
    }


    public FloatingLivePanel getFloatingLivePanel(ToolWindow toolWindow) {
        FloatingLivePanel panel = livePanelMap.get(toolWindow);
        if (panel == null) {
            panel = new FloatingLivePanel(manager);
            livePanelMap.put(toolWindow, panel);
        }

        return panel;
    }

    public void removeFloatingLivePanel() {
        livePanelMap.remove(toolWindow);
    }

    public FloatingLivePanel getFloatingLivePanel() {
        return getFloatingLivePanel(toolWindow);
    }

    public void assignFocus() {
        focusRequester = SwingUtil.findFocusable(getComponent());
        ToolWindowTitleButtonPanel toolWindowTitleButtonPanel = toolWindowPanel.getToolWindowTitleBar().getToolWindowTitleButtonPanel();

        if (focusRequester == null) {
            toolWindowTitleButtonPanel.getFocusable().setFocusable(true);
            focusRequester = toolWindowTitleButtonPanel.getFocusable();
        } else {
            toolWindowTitleButtonPanel.getFocusable().setFocusable(false);
        }
        SwingUtil.requestFocus(focusRequester);
    }

    public void setMainComponent(Component component) {
        JPanel componentContainer = toolWindowPanel.getComponentContainer();

        componentContainer.removeAll();
        setComponent(component);
        componentContainer.add(component, "0,0,FULL,FULL");

        SwingUtil.repaint(componentContainer);
    }



    public void initContainers() {
        if (toolWindowPanel != null)
                    return;
        // init components
        toolWindowPanel = new ToolWindowPanel(this);

        // init containers .. TODO: move to a method..
        dockedContainer = new DockedContainer(this);
        slidingContainer = new SlidingContainer(this);
        floatingContainer = new FloatingContainer(this);
        floatingLiveContainer = new FloatingLiveContainer(this);

        // init listeners

        // Init tool window properties listeners
        PropertyChangeEventSource toolWindowSource = getToolWindow();
        toolWindowSource.addPlafPropertyChangeListener("active", new ActivePropertyChangeListener());

        // Register focus listener ....
        focusListener = new FocusOwnerPropertyChangeListener(
                getManager().getResourceManager().createInstance(ParentOfQuestion.class,
                                                                 new DefaultMutableContext(ToolWindow.class, toolWindow,
                                                                                           Component.class, toolWindowPanel))
        );
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusListener);
        manager.addInternalPropertyChangeListener("manager.window.ancestor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() == null) 
                    toolWindow.setFlashing(false);
            }
        });

        // Load focusRequester
        focusRequester = SwingUtil.findFocusable(getComponent());
        if (focusRequester == null) {
            ToolWindowTitleButtonPanel toolWindowTitleButtonPanel = toolWindowPanel.getToolWindowTitleBar().getToolWindowTitleButtonPanel();
            toolWindowTitleButtonPanel.getFocusable().setFocusable(true);

            focusRequester = toolWindowTitleButtonPanel.getFocusable();
        }
    }

    protected void initListeners() {
        toolWindow.addPlafPropertyChangeListener(this);

        manager.addToolWindowManagerListener(new ToolWindowManagerListenerAdapter() {
            public void toolWindowRegistered(ToolWindowManagerEvent event) {
                initContainers();
                manager.removeToolWindowManagerListener(this);
            }
        });

        // todo: add as plaf
        manager.getToolWindowManagerDescriptor().addPropertyChangeListener(this);
    }

    protected void initTypeDescriptors() {
        floatingTypeDescriptor = (FloatingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING)).cloneMe(this);
        floatingTypeDescriptor.addPropertyChangeListener(this);

        floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING_LIVE)).cloneMe(this);
        floatingLiveTypeDescriptor.addPropertyChangeListener(this);

        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe(this);
        dockedTypeDescriptor.addPropertyChangeListener(this);

        slidingTypeDescriptor = (SlidingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.SLIDING)).cloneMe(this);
        slidingTypeDescriptor.addPropertyChangeListener(this);
    }

    protected void initPopupMenu() {
        if (popupMenu != null)
            return;

        popupMenu = new JPopupMenu("ToolWindowBarPopupMenu");
        popupMenu.setLightWeightPopupEnabled(false);

        ActionListener actionListener = new PopupMenuItemActionListener();

        // Visible
        visible = new JMenuItem();
        visible.setName("toolWindow.popup.visible." + toolWindow.getId());
        visible.setActionCommand("visible");
        visible.addActionListener(actionListener);

        aggregate = new JMenuItem();
        aggregate.setName("toolWindow.popup.aggregate." + toolWindow.getId());
        aggregate.setText(SwingUtil.getString("@@tool.aggregate"));
        aggregate.setActionCommand("aggregate");
        aggregate.addActionListener(actionListener);

        aggregateMenu = new JMenu(SwingUtil.getString("@@tool.aggregateMenu"));

        JMenuItem aggregateLeft = new JMenuItem();
        aggregateLeft.setName("toolWindow.popup.aggregate.left." + toolWindow.getId());
        aggregateLeft.setText(SwingUtil.getString("@@tool.aggregate.left"));
        aggregateLeft.setActionCommand("aggregate.left");
        aggregateLeft.addActionListener(actionListener);
        aggregateMenu.add(aggregateLeft);

        JMenuItem aggregateRight = new JMenuItem();
        aggregateRight.setName("toolWindow.popup.aggregate.right." + toolWindow.getId());
        aggregateRight.setText(SwingUtil.getString("@@tool.aggregate.right"));
        aggregateRight.setActionCommand("aggregate.right");
        aggregateRight.addActionListener(actionListener);
        aggregateMenu.add(aggregateRight);

        JMenuItem aggregateTop = new JMenuItem();
        aggregateTop.setName("toolWindow.popup.aggregate.top." + toolWindow.getId());
        aggregateTop.setText(SwingUtil.getString("@@tool.aggregate.top"));
        aggregateTop.setActionCommand("aggregate.top");
        aggregateTop.addActionListener(actionListener);
        aggregateMenu.add(aggregateTop);

        JMenuItem aggregateBottom = new JMenuItem();
        aggregateBottom.setName("toolWindow.popup.aggregate.bottom." + toolWindow.getId());
        aggregateBottom.setText(SwingUtil.getString("@@tool.aggregate.bottom"));
        aggregateBottom.setActionCommand("aggregate.bottom");
        aggregateBottom.addActionListener(actionListener);
        aggregateMenu.add(aggregateBottom);

        floatingMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
        floatingMode.setText(SwingUtil.getString("@@tool.mode.floating"));
        floatingMode.setActionCommand("floating");
        floatingMode.addActionListener(actionListener);

        floatingLiveMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING_LIVE);
        floatingLiveMode.setName("toolWindow.popup.floatingLive." + toolWindow.getId());
        floatingLiveMode.setText(SwingUtil.getString("@@tool.mode.floatingLive"));
        floatingLiveMode.setActionCommand("floatingLive");
        floatingLiveMode.addActionListener(actionListener);

        dockedMode = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
        dockedMode.setText(SwingUtil.getString("@@tool.mode.docked"));
        dockedMode.setActionCommand("docked");
        dockedMode.addActionListener(actionListener);

        pinnedMode = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
        pinnedMode.setText(SwingUtil.getString("@@tool.mode.pinned"));
        pinnedMode.setActionCommand("pinned");
        pinnedMode.addActionListener(actionListener);

        maximize = new JMenuItem();
        maximize.setText(SwingUtil.getString("@@tool.maximize"));
        maximize.setActionCommand("maximize");
        maximize.addActionListener(actionListener);

        // MoveTo SubMenu
        moveTo = new JMenu();
        moveTo.getPopupMenu().setLightWeightPopupEnabled(false);
        moveTo.setText(SwingUtil.getString("@@tool.moveTo"));

        right = new JMenuItem();
        right.setText(SwingUtil.getString("@@tool.move.right"));
        right.setActionCommand("move.right");
        right.addActionListener(actionListener);

        left = new JMenuItem();
        left.setText(SwingUtil.getString("@@tool.move.left"));
        left.setActionCommand("move.left");
        left.addActionListener(actionListener);

        top = new JMenuItem();
        top.setText(SwingUtil.getString("@@tool.move.top"));
        top.setActionCommand("move.top");
        top.addActionListener(actionListener);

        bottom = new JMenuItem();
        bottom.setText(SwingUtil.getString("@@tool.move.bottom"));
        bottom.setActionCommand("move.bottom");
        bottom.addActionListener(actionListener);

        moveTo.add(right);
        moveTo.add(left);
        moveTo.add(top);
        moveTo.add(bottom);

        popupMenu.add(pinnedMode);
        popupMenu.add(dockedMode);
        popupMenu.add(floatingMode);
        popupMenu.add(floatingLiveMode);
        popupMenu.add(moveTo);
        popupMenu.add(maximize);
        popupMenu.addSeparator();
        popupMenu.add(visible);
        popupMenu.add(aggregate);
        popupMenu.add(aggregateMenu);
    }


    protected void enableVisible() {
        aggregate.setVisible(!toolWindow.isVisible());
        aggregateMenu.setVisible(aggregate.isVisible());
        visible.setText(toolWindow.isVisible() ?
                        SwingUtil.getString("@@tool.hide") :
                        SwingUtil.getString("@@tool.show"));

        if (toolWindow.getType() == ToolWindowType.DOCKED) {
            dockedMode.setVisible(getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
            floatingMode.setVisible(getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
            floatingLiveMode.setState(false);
            floatingLiveMode.setVisible(getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
        } else if (toolWindow.getType() == ToolWindowType.SLIDING) {
            floatingMode.setVisible(getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
            floatingLiveMode.setState(false);
            floatingLiveMode.setVisible(getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
        } else if (toolWindow.getType() == ToolWindowType.FLOATING) {
            floatingLiveMode.setState(false);
            floatingLiveMode.setVisible(getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
        } else if (toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
            dockedMode.setState(false);
            floatingMode.setState(false);
            floatingMode.setVisible(getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
        }
    }

    protected void enableMoveToItem() {
        if (toolWindow.isLockedOnAnchor()) {
            ToolWindowAnchor[] anchors = getDockedTypeDescriptor().getLockingAnchors();

            if (anchors.length == 0) {
                moveTo.setVisible(false);
                return;
            }

            left.setVisible(false);
            right.setVisible(false);
            top.setVisible(false);
            bottom.setVisible(false);

            for (ToolWindowAnchor anchor : anchors) {
                switch (anchor) {
                    case LEFT:
                        left.setVisible(true);
                        break;
                    case RIGHT:
                        right.setVisible(true);
                        break;
                    case TOP:
                        top.setVisible(true);
                        break;
                    case BOTTOM:
                        bottom.setVisible(true);
                        break;
                }
            }
        } else {
            ToolWindowAnchor anchor = toolWindow.getAnchor();
            if (anchor == ToolWindowAnchor.LEFT) {
                left.setVisible(false);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.RIGHT) {
                left.setVisible(true);
                right.setVisible(false);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.BOTTOM) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(false);
            } else if (anchor == ToolWindowAnchor.TOP) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(false);
                bottom.setVisible(true);
            }
        }
    }

    protected void enableUserDefined() {
        DockedTypeDescriptor descriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        if (old != null) {
            popupMenu.remove(old);
        }

        JMenu menu = descriptor.getToolsMenu();
        if (menu.getMenuComponentCount() > 0) {
            popupMenu.add(menu, 4);
            old = menu;
        }
    }

    protected void enableMaximize() {
        maximize.setVisible(toolWindow.isVisible());
        maximize.setText(toolWindow.isMaximized() ?
                         SwingUtil.getString("@@tool.maximize.restore") :
                         SwingUtil.getString("@@tool.maximize"));

    }


    public class ToolWindowDescriptorCleaner extends DefaultCleanerAggregator {

        public void cleanup() {
            super.cleanup();

            // Clean listener added to toolwindow
            toolWindow.removePlafPropertyChangeListener(ToolWindowDescriptor.this);

            // Clean TypeDescriptors
            floatingTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            floatingLiveTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            dockedTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            slidingTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);

            // Clean Representative Anchor
            resetRepresentativeAnchor();

            // Clean listeners...
            toolWindow.cleanup();

            // Remove floating live entry...
            removeFloatingLivePanel();

            if (toolWindowPanel != null) {
                toolWindowPanel.putClientProperty(ToolWindow.class, null);
                toolWindowPanel.removeAll();
            }

            // Finalizy clean
            toolWindow = null;
            manager = null;
        }
    }

    public class PopupMenuItemActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("visible".equals(actionCommand)) {
                if (toolWindow.isActive()) {

                    toolWindow.setActive(false);
                    hideToolWindow();
                } else if (toolWindow.isVisible()) {
                    hideToolWindow();
                } else
                    toolWindow.setActive(true);
            } else if (actionCommand.startsWith("aggregate")) {
                if (toolWindow.isActive()) {
                    toolWindow.setActive(false);
                    toolWindow.setVisible(false);
                } else if (toolWindow.isVisible())
                    toolWindow.setVisible(false);
                else {
                    if (actionCommand.endsWith("left"))
                        toolWindow.aggregate(AggregationPosition.LEFT);
                    else if (actionCommand.endsWith("right"))
                        toolWindow.aggregate(AggregationPosition.RIGHT);
                    else if (actionCommand.endsWith("top"))
                        toolWindow.aggregate(AggregationPosition.TOP);
                    else if (actionCommand.endsWith("bottom"))
                        toolWindow.aggregate(AggregationPosition.BOTTOM);
                    else
                        toolWindow.aggregate();

                    toolWindow.setActive(true);
                }
            } else if ("move.right".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.RIGHT);
            } else if ("move.left".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.LEFT);
            } else if ("move.top".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.TOP);
            } else if ("move.bottom".equals(actionCommand)) {
                toolWindow.setAnchor(ToolWindowAnchor.BOTTOM);
            } else if ("floating".equals(actionCommand)) {
                if (floatingMode.isSelected()) {
                    toolWindow.setType((isFloatingWindow()) ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
                    dockedMode.setVisible(!floatingMode.isSelected());
                } else {
                    toolWindow.setType(oldType != null ? oldType : ToolWindowType.DOCKED);
                }
            } else if ("floatingLive".equals(actionCommand)) {
                toolWindow.setType(floatingLiveMode.isSelected() ? ToolWindowType.FLOATING_LIVE : ToolWindowType.DOCKED);
            } else if ("docked".equals(actionCommand)) {
                toolWindow.setType(dockedMode.isSelected() ? ToolWindowType.DOCKED : ToolWindowType.SLIDING);
            } else if ("pinned".equals(actionCommand)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            } else if ("maximize".equals(actionCommand)) {
                toolWindow.setMaximized(!toolWindow.isMaximized());
            }
        }
    }

    public class FocusOwnerPropertyChangeListener implements PropertyChangeListener, Cleaner {
        protected Question<Component, Boolean> parentOf;

        public FocusOwnerPropertyChangeListener(Question<Component, Boolean> parentOf) {
            this.parentOf = parentOf;

            getCleaner().addBefore(dockedContainer, this);
        }

        public void cleanup() {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!toolWindow.isVisible() || valueAdjusting || focusValueAdjusting)
                return;

            Component component = (Component) evt.getNewValue();
            if (component == null) return;
            if (component instanceof JRootPane) return;

            valueAdjusting = true;

            if (parentOf.getAnswer(component)) {
                toolWindow.setActive(true);
                if (focusRequester == null)
                    focusRequester = component;
                else {
                    if (!(focusRequester instanceof ToolWindowTitleButton))
                        focusRequester = component;
                    else {
                        if (focusRequester == getToolWindowPanel().getToolWindowTitleBar().getToolWindowTitleButtonPanel().getFocusable())
                            assignFocus();
                        else
                            focusRequester.requestFocusInWindow();
                    }
                }
            } else {
                System.out.println(toolWindow.getId() + " - cmp = " + component);

                getToolBar().deactiveTool(toolWindow);

                if (toolWindow.isAutoHide() && toolWindow.getType() != ToolWindowType.EXTERN)
                    toolWindow.setVisible(false);
            }

            valueAdjusting = false;
        }
    }

    public class ActivePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            boolean active = (Boolean) evt.getNewValue();

            ToolWindowTitleBar toolWindowTitleBar = toolWindowPanel.getToolWindowTitleBar();
            toolWindowTitleBar.setEnabled(active);

            boolean found = false;
            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                if (tab.isSelected()) {
                    found = true;
                    break;
                }
            }

            if (!found && toolWindow.getToolWindowTabs().length > 0)
                toolWindow.getToolWindowTabs()[0].setSelected(true);

            if (active && focusRequester != null && !valueAdjusting) {
//                System.out.println("focusRequester = " + focusRequester);
                if (focusRequester == toolWindowTitleBar.getToolWindowTitleButtonPanel().getFocusable())
                    assignFocus();
                else
                    SwingUtil.requestFocus(focusRequester);

            }
        }

    }
}
