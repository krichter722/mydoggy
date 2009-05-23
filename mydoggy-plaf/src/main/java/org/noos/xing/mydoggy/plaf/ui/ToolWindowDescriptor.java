package org.noos.xing.mydoggy.plaf.ui;

import org.noos.common.Question;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ToolWindowManagerEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.actions.*;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerAggregator;
import org.noos.xing.mydoggy.plaf.cleaner.DefaultCleanerAggregator;
import org.noos.xing.mydoggy.plaf.common.context.DefaultMutableContext;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.descriptors.ToolWindowRepresentativeAnchorDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.ParentOfQuestion;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.util.ToolWindowManagerListenerAdapter;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowDescriptor implements PropertyChangeListener,
                                             DockableDescriptor {

    protected MyDoggyToolWindowManager manager;
    protected MyDoggyToolWindow toolWindow;
    protected CleanerAggregator cleaner;

    protected boolean anchorPositionLocked = false;
    protected boolean floatingWindow = false;

    // Components
    protected ToolWindowPanel toolWindowPanel;

    // Listeners
    protected PropertyChangeListener focusListener;

    // Containers
    protected ToolWindowContainer dockedContainer;
    protected ToolWindowContainer floatingContainer;
    protected ToolWindowContainer slidingContainer;
    protected ToolWindowContainer floatingLiveContainer;

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
    protected RepresentativeAnchorDescriptor representativeAnchorDescriptor;

    // Field for adjusting
    boolean dockLengthValueAdjusting = false;
    boolean internalFocusValueAdjusting;
    public boolean externalFocusValueAdjusting = false;
    public static boolean fullExternalFocusValueAdjusting = false;

    // Popup menu fiedls
    protected ToolWindowType oldType;

    protected JPopupMenu popupMenu;

    protected JMenu old;

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

            if (evt.getSource() != toolWindow ||
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
        } else if (evt.getSource() instanceof RepresentativeAnchorDescriptor && "title".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if (evt.getSource() instanceof RepresentativeAnchorDescriptor && "icon".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("dockLength".equals(propertyName)) {
            if (!dockLengthValueAdjusting) {
                this.divederLocation = (Integer) evt.getNewValue();
                getToolBar(toolWindow.getAnchor()).propertyChange(
                        new PropertyChangeEvent(toolWindow,
                                                propertyName,
                                                evt.getOldValue(),
                                                evt.getNewValue()
                        )
                );
            }
        } else if ("hideRepresentativeButtonOnVisible".equals(propertyName)) {
            if (toolWindow.isVisible())
                toolWindow.getRepresentativeAnchorDescriptor().setVisible(!(Boolean) evt.getNewValue());
        } else if ("UI".equals(propertyName)) {
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

    public void setAnchorPositionLocked(boolean anchorPositionLocked) {
        this.anchorPositionLocked = anchorPositionLocked;
    }

    public boolean isAnchorPositionLocked() {
        return anchorPositionLocked;
    }

    public void setAvailable(boolean available) {
        toolWindow.setAvailable(available);
    }

    public boolean isAvailable() {
        return toolWindow.isAvailable();
    }

    public boolean isAvailableCountable() {
        return true;
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

            String labelText = SwingUtil.getUserString(toolWindow.getRepresentativeAnchorDescriptor().getTitle());
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
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, -Math.PI/2);

                    TextIcon textIcon = new TextIcon(parent, toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, Math.PI/2);

                    textIcon = new TextIcon(parent, toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND_UNAVAILABLE));
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

            String labelText = SwingUtil.getUserString(toolWindow.getRepresentativeAnchorDescriptor().getTitle());
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
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, -Math.PI/2);

                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getLeftIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    if (SwingUtil.getBoolean(MyDoggyKeySpace.TWRA_ROTATE_ICON_ON_ANCHOR, false) && toolIcon != null)
                        toolIcon = GraphicsUtil.rotate(toolIcon, Math.PI/2);

                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getRightIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND_UNAVAILABLE));
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
        toolWindowPanel.setComponent(this.component = component);
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
        dockLengthValueAdjusting = true;
        try {
            dockedTypeDescriptor.setDockLength(divederLocation);
        } finally {
            dockLengthValueAdjusting = false;
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
//        getToolWindowContainer().updateUI();

//        SwingUtilities.updateComponentTreeUI(getComponent());

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

    public ToolWindowContainer getToolWindowContainer() {
        if (dockedContainer == null)
            initContainers();

        return dockedContainer;
    }

    /**
     * Returns the ToolWindowContainer binded to the specified <code>type</code>.
     *
     * @param type tool window type.
     * @return the ToolWindowContainer binded to the specified <code>type</code>.
     * @see ToolWindowContainer
     */
    public ToolWindowContainer getToolWindowContainer(ToolWindowType type) {
        if (dockedContainer == null)
            initContainers();

        switch (type) {
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

    /**
     * This method retrieves the TypeDescriptor for <code>type</code> that the tool uses to modify the behaviours
     * of that type. The modifications are visible only for this tool.
     *
     * @param type the tool window type.
     * @return the type descriptor for <code>type</code>.
     */
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

    public ToolWindowTypeDescriptor getTypeDescriptor() {
        return toolWindow.getTypeDescriptor(toolWindow.getType());
    }

    public RepresentativeAnchorDescriptor getRepresentativeAnchorDescriptor() {
            return representativeAnchorDescriptor;
        }

    public void hideToolWindow() {
        ToolWindowActionHandler toolWindowActionHandler = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolWindowActionHandler();
        if (toolWindowActionHandler != null)
            toolWindowActionHandler.onHideButtonClick(toolWindow);
        else
            toolWindow.setVisible(false);
    }

    public void assignFocus() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                focusRequester = toolWindowPanel.getFocusable();
                focusRequester.setFocusable(true);
                focusRequester.requestFocusInWindow();
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Component toolWindowPanelFocusable = toolWindowPanel.getFocusable();

                if (toolWindowPanelFocusable.isFocusOwner()) {
                    focusRequester = SwingUtil.findFocusable(getComponent());
                    if (focusRequester != null) {
                        focusRequester.requestFocusInWindow();
                        toolWindowPanelFocusable.setFocusable(false);
                    } else
                        toolWindowPanelFocusable.requestFocusInWindow();
                } else
                    SwingUtilities.invokeLater(this);
            }
        });

    }

    public void showPopupMenu(Component source, int x, int y) {
        initPopupMenu();

        ToolWindowTitleBar toolWindowTitleBar = toolWindowPanel.getToolWindowTitleBar();
        if (source == toolWindowTitleBar ||
            SwingUtil.hasParent(source, toolWindowTitleBar) ||
            source instanceof ToolWindowRepresentativeAnchor) {

            // clean the menu
            popupMenu.removeAll();

            ToolWindowTypeDescriptor typeDescriptor = toolWindow.getTypeDescriptor(toolWindow.getType());

            // populate the menu
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.MOVE_TO_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.MAXIMIZE_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.TOOLS_MENU_ACTION_ID));

            // add user actions
            for (ToolWindowAction toolWindowAction : typeDescriptor.getToolWindowActions()) {
                if (!(toolWindowAction instanceof PlafToolWindowAction))  {
                    addPopupAction(popupMenu, toolWindowAction);
                }
            }

            popupMenu.addSeparator();

            // add show/hide/aggregate actions...
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.AGGREGATE_ACTION_ID));
            addPopupAction(popupMenu, typeDescriptor.getToolWindowAction(ToolWindowAction.AGGREGATE_MENU_ACTION_ID));

            // call for popup updater...
            if (popupUpdaterList != null) {
                for (PopupUpdater popupUpdater : popupUpdaterList) {
                    popupUpdater.update(source, popupMenu);
                }
            }

            // show the menu
            popupMenu.show(source, x, y);
        }
    }

    public void addPopupUpdater(PopupUpdater popupUpdater) {
        if (popupUpdaterList == null)
            popupUpdaterList = new ArrayList<PopupUpdater>();

        popupUpdaterList.add(popupUpdater);
    }

    public void removePopupUpdater(PopupUpdater popupUpdater) {
        if (popupUpdaterList == null)
            return;

        popupUpdaterList.remove(popupUpdater);
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

    public void removeFloatingLivePanel() {
        manager.removeFloatingLiveWindow(toolWindow);
    }

    public FloatingLiveWindow getFloatingLivePanel() {
        return manager.getFloatingLiveWindow(toolWindow);
    }

    public void removeModalWindow() {
        manager.removeFloatingWindow(toolWindow);
    }

    public FloatingWindow getModalWindow() {
        return manager.getFloatingWindow(toolWindow);
    }

    public Rectangle getScreenWindowBounds() {
        return SwingUtil.getScreenWindowBounds(getModalWindow().getWindow());
    }


    public void addCommonToolWindowAction(ToolWindowAction toolWindowAction) {
        getTypeDescriptor(ToolWindowType.DOCKED).addToolWindowAction(toolWindowAction);
        getTypeDescriptor(ToolWindowType.SLIDING).addToolWindowAction(toolWindowAction);
        getTypeDescriptor(ToolWindowType.FLOATING).addToolWindowAction(toolWindowAction);
        getTypeDescriptor(ToolWindowType.FLOATING_LIVE).addToolWindowAction(toolWindowAction);
    }

    public void removeCommonToolWindowAction(String id) {
        getTypeDescriptor(ToolWindowType.DOCKED).removeToolWindowAction(id);
        getTypeDescriptor(ToolWindowType.SLIDING).removeToolWindowAction(id);
        getTypeDescriptor(ToolWindowType.FLOATING).removeToolWindowAction(id);
        getTypeDescriptor(ToolWindowType.FLOATING_LIVE).removeToolWindowAction(id);
    }

    public boolean containsToolWindowAction(ToolWindowTypeDescriptor except, String id) {
        if (except != dockedTypeDescriptor && dockedTypeDescriptor.getToolWindowAction(id) != null)
            return true;

        if (except != slidingTypeDescriptor && slidingTypeDescriptor.getToolWindowAction(id) != null)
            return true;

        if (except != floatingTypeDescriptor && floatingTypeDescriptor.getToolWindowAction(id) != null)
            return true;

        if (except != floatingLiveTypeDescriptor && floatingLiveTypeDescriptor.getToolWindowAction(id) != null)
            return true;

        return false;
    }

    public boolean containsToolWindowAction(ToolWindowTypeDescriptor except, ToolWindowAction action) {
        if (except != dockedTypeDescriptor && dockedTypeDescriptor.getToolWindowAction(action.getId()) == action)
            return true;

        if (except != slidingTypeDescriptor && slidingTypeDescriptor.getToolWindowAction(action.getId()) == action)
            return true;

        if (except != floatingTypeDescriptor && floatingTypeDescriptor.getToolWindowAction(action.getId()) == action)
            return true;

        if (except != floatingLiveTypeDescriptor && floatingLiveTypeDescriptor.getToolWindowAction(action.getId()) == action)
            return true;

        return false;
    }


    protected void initContainers() {
        if (toolWindowPanel != null)
            return;
        // init components
        toolWindowPanel = new ToolWindowPanel(this);

        // init containers ...
        initToolWindowContainers();

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
        
        manager.addInternalPropertyChangeListener("managerWindowAncestor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

                if (evt.getNewValue() != null) {
                    keyboardFocusManager.removePropertyChangeListener("focusOwner", focusListener);
                    keyboardFocusManager.addPropertyChangeListener("focusOwner", focusListener);
                } else {
                    keyboardFocusManager.removePropertyChangeListener("focusOwner", focusListener);

                    if (toolWindow != null)
                        toolWindow.setFlashing(false);
                }
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

    protected void initToolWindowContainers() {
        dockedContainer = new DockedContainer(this);
        slidingContainer = new SlidingContainer(this);
        floatingContainer = new FloatingContainer(this);
        floatingLiveContainer = new FloatingLiveContainer(this);
    }

    protected void initListeners() {
        toolWindow.addPlafPropertyChangeListener(this);

        manager.addToolWindowManagerListener(new ToolWindowManagerListenerAdapter() {
            public void toolWindowRegistered(ToolWindowManagerEvent event) {
                initToolWindowActions();
                initContainers();

                manager.removeToolWindowManagerListener(this);
            }
        });
        ((PropertyChangeEventSource) manager.getToolWindowManagerDescriptor()).addPlafPropertyChangeListener(this);
    }

    protected void initToolWindowActions() {
        toolWindow.addToolWindowAction(new HideToolWindowAction());
        toolWindow.addToolWindowAction(new DockToolWindowAction());
        toolWindow.addToolWindowAction(new MaximizeToolWindowAction());
        toolWindow.addToolWindowAction(new FloatingToolWindowAction());
        toolWindow.addToolWindowAction(new PinToolWindowAction());
        toolWindow.addToolWindowAction(new FloatingLiveToolWindowAction());
        toolWindow.addToolWindowAction(new MoveToToolWindowAction());
        toolWindow.addToolWindowAction(new AggregateToolWindowAction());
        toolWindow.addToolWindowAction(new AggregateMenuToolWindowAction());
        toolWindow.addToolWindowAction(new ToolsMenuToolWindowAction());
    }

    protected void initTypeDescriptors() {
        floatingTypeDescriptor = (FloatingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING)).cloneMe(this);
        floatingTypeDescriptor.addPropertyChangeListener(this);

        floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING_LIVE)).cloneMe(this);
        floatingLiveTypeDescriptor.addPropertyChangeListener(this);

        slidingTypeDescriptor = (SlidingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.SLIDING)).cloneMe(this);
        slidingTypeDescriptor.addPropertyChangeListener(this);

        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe(this);
        dockedTypeDescriptor.addPropertyChangeListener(this);

        representativeAnchorDescriptor = new ToolWindowRepresentativeAnchorDescriptor(this);
        representativeAnchorDescriptor.addPropertyChangeListener(this);
    }

    protected void initPopupMenu() {
        if (popupMenu != null)
            return;

        popupMenu = new JPopupMenu("ToolWindowBarPopupMenu");
        popupMenu.setLightWeightPopupEnabled(false);
    }


    protected void addPopupAction(JPopupMenu popupMenu, ToolWindowAction toolWindowAction) {
        if (toolWindowAction.isVisibleOnMenuBar()) {
            JMenuItem menuItem = toolWindowAction.getMenuItem();
            
            if (menuItem != null)
                popupMenu.add(menuItem);
        }
    }

    public void disableExternalFocusValueAdjustingLater() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                externalFocusValueAdjusting = false;
            }
        });
    }

    public void disableFullExternalFocusValueAdjustingLater() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fullExternalFocusValueAdjusting = false;
            }
        });
    }


    public class ToolWindowDescriptorCleaner extends DefaultCleanerAggregator {

        public void cleanup() {
            super.cleanup();

            // Clean listener added to toolwindow
            toolWindow.removePlafPropertyChangeListener(ToolWindowDescriptor.this);

            // Clean tool window manager descriptor listener
            ((PropertyChangeEventSource) manager.getToolWindowManagerDescriptor()).removePlafPropertyChangeListener(ToolWindowDescriptor.this);

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
            if (!toolWindow.isVisible() || internalFocusValueAdjusting || externalFocusValueAdjusting || fullExternalFocusValueAdjusting)
                return;

//            System.out.println(toolWindow.getId() + " internalFocusValueAdjusting = " + internalFocusValueAdjusting);
//            System.out.println(toolWindow.getId() + " externalFocusValueAdjusting = " + externalFocusValueAdjusting);

            Component component = (Component) evt.getNewValue();
            if (component == null) return;
            if (component instanceof JRootPane) return;

            internalFocusValueAdjusting = true;

//            System.out.println(toolWindow.getId() + " - cmp = " + component);

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
                if (toolWindow.isActive()) {
                    getToolBar().deactiveTool(toolWindow);
                }

                if (toolWindow.isAutoHide() && toolWindow.getType() != ToolWindowType.EXTERN) {
                    toolWindow.setVisible(false);
                }
            }

            internalFocusValueAdjusting = false;
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

            if (active && !internalFocusValueAdjusting) {

                if (focusRequester != null) {
//                    System.out.println("focusRequester = " + focusRequester);
                    if (focusRequester == toolWindowTitleBar.getToolWindowTitleButtonPanel().getFocusable())
                        assignFocus();
                    else
                        SwingUtil.requestFocus(focusRequester);
                } else
                    assignFocus();
            }
        }

    }
}
