package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeBridge;
import org.noos.xing.mydoggy.plaf.ui.*;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGesture;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureDelegate;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MouseEventDispatcher;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLabelUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo: (-) implemets maximized
 */
public class ToolWindowTabPanel extends JComponent implements TitleBarTabs, Cleaner {
    protected DockedContainer dockedContainer;
    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ResourceManager resourceManager;

    protected JViewport viewport;
    protected JPanel tabContainer;
    protected TableLayout containerLayout;

    protected ToolWindowTab selectedTab;
    protected Component selecTabButton;
    protected PopupButton popupButton;

    protected DragGestureDelegate dragGestureDelegate;
    protected MouseEventDispatcher mouseEventDispatcher;

    protected PropertyChangeBridge propertyChangeBridge;


    public ToolWindowTabPanel(DockedContainer dockedContainer, ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.resourceManager = descriptor.getResourceManager();
        this.dockedContainer = dockedContainer;
        this.mouseEventDispatcher = new MouseEventDispatcher();
        this.dragGestureDelegate = new DragGestureDelegate();

        descriptor.getCleaner().addCleaner(this);

        initComponents();
        initListeners();
    }


    public void cleanup() {
        //Finalize

        toolWindow = null;
        descriptor = null;
        resourceManager = null;
        dockedContainer = null;

        selectedTab = null;
        selecTabButton = null;
        dragGestureDelegate = null;
    }

    public void setDragGesture(DragGesture dragGesture) {
        dragGestureDelegate.setDragGesture(dragGesture);
    }


    public void addEventDispatcherlListener(EventListener eventListener) {
        mouseEventDispatcher.addListener(eventListener);
    }

    public void removeEventDispatcherlListener(EventListener eventListener) {
        mouseEventDispatcher.removeListener(eventListener);
    }


    protected void initComponents() {
        propertyChangeBridge = new PropertyChangeBridge();
        descriptor.getCleaner().addCleaner(propertyChangeBridge);

        setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL, 1, 14}, {0, TableLayout.FILL, 0}}, false));
        setFocusable(false);
        setBorder(null);

        tabContainer = new JPanel(containerLayout = new TableLayout(new double[][]{{0},
                                                                                   {resourceManager.getFloat("toolwindow.title.font.size", 12) + 4}}));
        tabContainer.setName("toolWindow.tabContainer." + descriptor.getToolWindow().getId());
        tabContainer.setOpaque(false);
        tabContainer.setBorder(null);
        tabContainer.setFocusable(false);

        viewport = new JViewport();
        viewport.setBorder(null);
        viewport.setOpaque(false);
        viewport.setFocusable(false);
        viewport.setView(tabContainer);

        add(viewport, "0,1,FULL,FULL");
        add(popupButton = new PopupButton(), "2,1,FULL,FULL");

        viewport.addMouseWheelListener(new WheelScroller());

        initTabs();
    }

    protected void initListeners() {
        propertyChangeBridge.addBridgePropertyChangeListener("selected", new TabSelectedPropertyChangeListener());

        dockedContainer.setPopupUpdater(new DockedContainer.PopupUpdater() {
            final JMenuItem nextTabItem = new JMenuItem(new SelectNextTabAction());
            final JMenuItem previousTabItem = new JMenuItem(new SelectPreviousTabAction());
            final JMenuItem closeAllItem = new JMenuItem(new CloseAllTabAction());

            public void update(Component source, JPopupMenu popupMenu) {
                if (source.getParent() instanceof TabButton) {
                    boolean enableByTabsCount = toolWindow.getToolWindowTabs().length > 1;

                    TabButton tabButton = (TabButton) source.getParent();

                    int index = 0;
                    if (tabButton.tab.isCloseable()) {
                        final JMenuItem closeItem = new JMenuItem(new CloseTabAction(tabButton.tab));
                        popupMenu.add(closeItem, index++);
                        popupMenu.add(closeAllItem, index++);
                        popupMenu.add(new JSeparator(), index++);
                    }
                    popupMenu.add(nextTabItem, index++);
                    popupMenu.add(previousTabItem, index++);
                    popupMenu.add(new JSeparator(), index);

                    nextTabItem.setEnabled(enableByTabsCount);
                    previousTabItem.setEnabled(enableByTabsCount);
                }
            }

            class CloseTabAction extends AbstractAction {
                ToolWindowTab tab;

                public CloseTabAction(ToolWindowTab tab) {
                    super(resourceManager.getString("@@tool.tab.close"));
                    this.tab = tab;
                }

                public void actionPerformed(ActionEvent e) {
                    if (tab.isCloseable()) {
                        ToolWindowTabEvent event = new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_REMOVING,
                                                                          toolWindow, tab);
                        for (ToolWindowListener listener : toolWindow.getToolWindowListeners()) {
                            boolean result = listener.toolWindowTabRemoving(event);
                            if (!result)
                                break;
                        }

                        toolWindow.removeToolWindowTab(tab);
                    }
                }
            }

            class CloseAllTabAction extends AbstractAction {
                public CloseAllTabAction() {
                    super(resourceManager.getString("@@tool.tab.closeAll"));
                }

                public void actionPerformed(ActionEvent e) {
                    ToolWindowTab selectedTab = null;

                    for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                        if (tab.isSelected()) {
                            selectedTab = tab;
                            continue;
                        }
                        tryToClose(tab);
                    }

                    tryToClose(selectedTab);
                }

                protected void tryToClose(ToolWindowTab tab) {
                    if (tab != null && tab.isCloseable()) {
                        ToolWindowTabEvent event = new ToolWindowTabEvent(this, ToolWindowTabEvent.ActionId.TAB_REMOVING,
                                                                          toolWindow, tab);

                        for (ToolWindowListener listener : toolWindow.getToolWindowListeners()) {
                            boolean result = listener.toolWindowTabRemoving(event);
                            if (!result)
                                break;
                        }

                        toolWindow.removeToolWindowTab(tab);
                    }
                }
            }

        });

        toolWindow.addToolWindowListener(new TabToolWindowListener());

        viewport.addMouseListener(dockedContainer.getTitleBarMouseAdapter());
        viewport.addMouseListener(mouseEventDispatcher);
        viewport.addMouseMotionListener(mouseEventDispatcher);

        // Register drag gesture
        SwingUtil.registerDragGesture(viewport, dragGestureDelegate);
    }

    protected void initTabs() {
        for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
            addTab((MyDoggyToolWindowTab) tab);

        checkPopupButton();
    }

    protected void addTab(TabButton tabButton) {
        int column = containerLayout.getNumColumn();
        containerLayout.insertColumn(column, 0);
        containerLayout.insertColumn(column + 1, -2);
        containerLayout.insertColumn(column + 2, 0);

        tabContainer.add(tabButton, (column + 1) + ",0" + ",FULL,FULL");

        SwingUtil.repaint(tabContainer);
    }

    protected void addTab(MyDoggyToolWindowTab tab) {
        TabButton tabButton = new TabButton(tab);
        tab.removePlafPropertyChangeListener(propertyChangeBridge);
        tab.addPlafPropertyChangeListener(propertyChangeBridge);

        addTab(tabButton);
    }

    protected ToolWindowTab removeTab(ToolWindowTab toolWindowTab, boolean flag) {
        int nextTabCol = -1;
        for (Component component : tabContainer.getComponents()) {
            if (component instanceof TabButton) {
                TabButton tabButton = (TabButton) component;
                if (tabButton.tab == toolWindowTab) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);
                    tabContainer.remove(tabButton);

                    if (flag)
                        tabButton.removePropertyChangeListener(propertyChangeBridge);

                    nextTabCol = constraints.col1;
                    int col = constraints.col1 - 1;
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    containerLayout.deleteColumn(col);
                    break;
                }
            }
        }

        SwingUtil.repaint(tabContainer);

        ToolWindowTab firstTab = null;
        if (nextTabCol != -1) {
            for (Component component : tabContainer.getComponents()) {
                if (component instanceof TabButton) {
                    TabButton tabButton = (TabButton) component;
                    TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                    if (constraints.col1 == nextTabCol)
                        return tabButton.tab;

                    if (constraints.col1 == 2)
                        firstTab = tabButton.tab;
                }
            }
        }
        return firstTab;
    }

    protected void checkPopupButton() {
        boolean visible = toolWindow.getToolWindowTabs().length > 1;
        popupButton.setVisible(visible);

        ((TableLayout) getLayout()).setColumn(2, visible ? 14 : 0);
    }


    protected class TabToolWindowListener implements ToolWindowListener, Cleaner {

        public TabToolWindowListener() {
            descriptor.getCleaner().addBefore(ToolWindowTabPanel.this, this);
        }

        public void cleanup() {
            descriptor.getToolWindow().removeToolWindowListener(this);
        }

        public void toolWindowTabAdded(ToolWindowTabEvent event) {
            if (tabContainer.getComponentCount() == 0)
                initTabs();
            else
                addTab((MyDoggyToolWindowTab) event.getToolWindowTab());

            checkPopupButton();
        }

        public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
            return true;
        }

        public void toolWindowTabRemoved(ToolWindowTabEvent event) {
            ToolWindowTab nextTab = removeTab(event.getToolWindowTab(), true);

            if (event.getToolWindowTab().isSelected()) {
                ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
                if (tabs.length > 0) {
                    if (nextTab != null)
                        nextTab.setSelected(true);
                    else
                        tabs[0].setSelected(true);
                } else {
                    selectedTab = null;
                    selecTabButton = null;
                }
            }

            checkPopupButton();
        }

    }


    protected class TabButton extends JPanel implements PropertyChangeListener,
                                                        MouseListener,
                                                        ActionListener,
                                                        Cleaner {
        protected MyDoggyToolWindowTab tab;

        protected TableLayout layout;
        protected JLabel titleLabel;
        protected JButton closeButton;
        protected JButton minimizeButton;

        protected boolean pressed;
        protected boolean inside;
        protected boolean selected;

        protected Timer flashingTimer;
        protected int flasingDuration = -1;
        protected boolean flashingState;


        public TabButton(final MyDoggyToolWindowTab tab) {
            this.tab = tab;
            tab.addPropertyChangeListener(this);
            tab.getCleaner().addCleaner(this);

            putClientProperty(ToolWindowTab.class, tab);

            // Setup Panel
            setLayout(layout = new TableLayout(new double[][]{{-1, 0, 0, 0, 0}, {-1}}));
            setOpaque(false);
            setFocusable(false);
            setUI(new TabButtonPanelUI());
            addMouseListener(mouseEventDispatcher);
            addMouseMotionListener(mouseEventDispatcher);
            String name = "toolWindow." + tab.getOwner().getId() + ".tab." + tab.getTitle();
            setName(name);

            this.selected = this.pressed = this.inside = false;

            // Prepare components

            // Title
            titleLabel = new JLabel(tab.getTitle());
            titleLabel.setName(name + ".title");
            titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
            titleLabel.setOpaque(false);
            titleLabel.setFocusable(false);
            titleLabel.setIcon(tab.getIcon());
            titleLabel.setFont(titleLabel.getFont().deriveFont(resourceManager.getFloat("toolwindow.title.font.size", 12)));
            titleLabel.setUI(new BasicLabelUI() {

                public void update(Graphics g, JComponent c) {
                    if (tab.isFlashing() && toolWindow.isVisible()) {
                        if (flashingState) {
                            titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
                        } else {
                            titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
                        }
                    } else {
                        if (selected)
                            titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
                        else
                            titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
                    }

                    super.update(g, c);
                }

                protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
                    if (pressed && inside)
                        super.paintEnabledText(l, g, s, textX + 1, textY + 1);
                    else
                        super.paintEnabledText(l, g, s, textX, textY);
                }


            });
            titleLabel.addMouseListener(dockedContainer.getTitleBarMouseAdapter());
            titleLabel.addMouseListener(mouseEventDispatcher);
            titleLabel.addMouseMotionListener(mouseEventDispatcher);
            titleLabel.addMouseListener(this);
            add(titleLabel, "0,0,FULL,FULL");

            // Buttons
            closeButton = (JButton) resourceManager.createComponent(
                    MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON,
                    descriptor.getManager().getContext()
            );
            closeButton.setName(name + ".closeButton");
            closeButton.setActionCommand("close");
            closeButton.addActionListener(this);
            closeButton.setToolTipText(resourceManager.getString("@@tool.tab.close"));
            closeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.TAB_CLOSE));

            minimizeButton = (JButton) resourceManager.createComponent(
                    MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON,
                    descriptor.getManager().getContext()
            );
            minimizeButton.setName(name + ".minimizeButton");
            minimizeButton.setActionCommand("minimize");
            minimizeButton.addActionListener(this);
            minimizeButton.setToolTipText(resourceManager.getString("@@tool.tab.minimize"));
            minimizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE));

            add(minimizeButton, "2,0,FULL,c");
            add(closeButton, "4,0,FULL,c");

            // Register DragGesture
            SwingUtil.registerDragGesture(this, dragGestureDelegate);
            SwingUtil.registerDragGesture(titleLabel, dragGestureDelegate);
            SwingUtil.registerDragGesture(minimizeButton, dragGestureDelegate);
            SwingUtil.registerDragGesture(closeButton, dragGestureDelegate);
        }


        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("close".equals(actionCommand)) {
                toolWindow.removeToolWindowTab(tab);
            } else {
                tab.setMinimized(true);
            }
        }

        public void mousePressed(MouseEvent e) {
            toolWindow.setActive(true);

            if (SwingUtilities.isLeftMouseButton(e) && !selected) {
                pressed = true;
                repaint();
            } else {
                pressed = false;
                repaint();
            }
        }

        public void mouseReleased(MouseEvent e) {
            pressed = false;
            repaint();
        }

        public void mouseEntered(MouseEvent e) {
            inside = true;
            repaint();
        }

        public void mouseExited(MouseEvent e) {
            inside = false;
            repaint();
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        TabButton.this.tab.setSelected(true);
                    }
                });
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String property = evt.getPropertyName();
            if ("selected".equals(property)) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    selecTabButton = null;

                    titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
                    closeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.TAB_CLOSE_INACTIVE));
                    minimizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE_INACTIVE));
                    setButtonsEnabled(false);

                    selected = false;
                } else {
                    tab.setFlashing(false);
                    selecTabButton = this;

                    // Ensure position
                    ensureVisible();

                    titleLabel.setForeground(resourceManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
                    closeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.TAB_CLOSE));
                    minimizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE));
                    setButtonsEnabled(true);
                    selected = true;
                }
                SwingUtil.repaint(this);
            } else if ("title".equals(property)) {
                titleLabel.setText((String) evt.getNewValue());
                setName("toolWindow." + toolWindow.getId() + ".tabs." + tab.getTitle());
            } else if ("icon".equals(property)) {
                titleLabel.setIcon((Icon) evt.getNewValue());
            } else if ("flash".equals(property)) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    if (!tab.isSelected()) {
                        flasingDuration = -1;
                        SwingUtil.repaint(this);
                    }
                } else {
                    if (flashingTimer != null) {
                        flashingTimer.stop();
                        flashingTimer = null;
                        SwingUtil.repaint(this);
                    }
                }
            } else if ("flash.duration".equals(property)) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    if (!tab.isSelected()) {
                        flasingDuration = (Integer) evt.getNewValue();
                        SwingUtil.repaint(this);
                    }
                } else {
                    if (flashingTimer != null) {
                        flashingTimer.stop();
                        flashingTimer = null;
                        SwingUtil.repaint(this);
                    }
                }
            } else if ("minimized".equals(property)) {
                if (evt.getNewValue() == Boolean.TRUE) {
                    ToolWindowTab nextTab = removeTab(tab, false);
                    if (nextTab != null)
                        nextTab.setSelected(true);
                } else {
                    addTab(this);
                }
            } else if ("ensureVisible".equals(property)) {
                ensureVisible();
            } else if ("minimizable".equals(property))  {
                if (tab.isMinimizable()) {
                    layout.setColumn(1, 1);
                    layout.setColumn(2, 14);
                } else {
                    layout.setColumn(1, 0);
                    layout.setColumn(2, 0);
                }
                revalidate();
                repaint();
            } else if ("closeable".equals(property))  {
                if (tab.isCloseable()) {
                    layout.setColumn(3, 1);
                    layout.setColumn(4, 14);
                } else {
                    layout.setColumn(3, 0);
                    layout.setColumn(4, 0);
                }
                revalidate();
                repaint();
            }
        }

        public Insets getInsets() {
            return new Insets(0, 5, 0, 5);
        }

        public void cleanup() {
            if (flashingTimer != null)
                flashingTimer.stop();
            flashingTimer = null;

            tab.removePropertyChangeListener(this);

            removeMouseMotionListener(mouseEventDispatcher);
            removeMouseListener(mouseEventDispatcher);

            putClientProperty(ToolWindowTab.class, null);

            titleLabel.removeMouseListener(dockedContainer.getTitleBarMouseAdapter());
            titleLabel.removeMouseListener(mouseEventDispatcher);
            titleLabel.removeMouseMotionListener(mouseEventDispatcher);
            titleLabel.removeMouseListener(this);

            if (selectedTab == tab)
                selectedTab = null;

            tab = null;
        }


        protected void setButtonsEnabled(boolean enabled) {
            if (enabled && tabContainer.getComponentCount() > 1) {
                if (tab.isCloseable()) {
                    layout.setColumn(3, 1);
                    layout.setColumn(4, 14);
                } else {
                    layout.setColumn(3, 0);
                    layout.setColumn(4, 0);
                }

                if (tab.isMinimizable()) {
                    layout.setColumn(1, 1);
                    layout.setColumn(2, 14);
                } else {
                    layout.setColumn(1, 0);
                    layout.setColumn(2, 0);
                }
            } else {
                layout.setColumn(1, 0);
                layout.setColumn(2, 0);
                layout.setColumn(3, 0);
                layout.setColumn(4, 0);
            }
            revalidate();
            repaint();
        }

        protected void ensureVisible() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Rectangle cellBounds = getBounds();
                    cellBounds.x -= viewport.getViewPosition().x;
                    viewport.scrollRectToVisible(cellBounds);
                }
            });
        }

        protected class TabButtonPanelUI extends BasicPanelUI {

            public void update(Graphics g, JComponent c) {
                if (tab == null || toolWindow == null)
                    return;

                Rectangle bounds = c.getBounds();
                bounds.x = bounds.y = 0;

                if (tab.isFlashing() && toolWindow.isVisible()) {
                    if (flashingState) {
                        GraphicsUtil.fillRect(g, bounds,
                                              resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                              resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                              new RoundRectangle2D.Double(
                                                      bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                              ),
                                              GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                    } else {
                        if (toolWindow.isActive()) {
                            GraphicsUtil.fillRect(g, bounds,
                                                  resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                                  resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                                  new RoundRectangle2D.Double(
                                                          bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                                  ),
                                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                        }
                    }

                    if (flashingTimer == null) {
                        flashingTimer = new Timer(600, new ActionListener() {
                            long start = 0;

                            public void actionPerformed(ActionEvent e) {
                                Rectangle bounds = TabButton.this.getBounds();
                                bounds.x = bounds.y = 0;

                                if (start == 0)
                                    start = System.currentTimeMillis();

                                flashingState = !flashingState;

                                SwingUtil.repaint(TabButton.this);

                                if (flasingDuration != -1 && System.currentTimeMillis() - start > flasingDuration)
                                    tab.setFlashing(false);
                            }
                        });
                        flashingState = true;
                    }
                    if (!flashingTimer.isRunning()) {
                        flashingTimer.start();
                    }
                } else if (tabContainer.getComponentCount() > 1) {
                    if (selected) {
                        if (toolWindow.isActive()) {
                            GraphicsUtil.fillRect(g, bounds,
                                                  resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                                  resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                                  new RoundRectangle2D.Double(
                                                          bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                                  ),
                                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                        }
                    }
                }

                super.update(g, c);
            }
        }
    }

    protected class PopupButton extends ToolWindowActiveButton implements ActionListener {
        protected JPopupMenu popupMenu;

        public PopupButton() {
            setIcon(resourceManager.getIcon(MyDoggyKeySpace.TOO_WINDOW_TAB_POPUP));
            addActionListener(this);
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    toolWindow.setActive(true);
                }
            });
            setVisible(false);
        }

        public void actionPerformed(ActionEvent e) {
            initPopup();
            popupMenu.show(this, 10, 10);
        }

        protected void initPopup() {
            if (popupMenu == null) {
                popupMenu = new JPopupMenu("TabPopup");
                popupMenu.add(new SelectNextTabAction());
                popupMenu.add(new SelectPreviousTabAction());
                popupMenu.addSeparator();
            }

            for (int i = 3, size = popupMenu.getComponentCount(); i < size; i++)
                popupMenu.remove(3);

            for (ToolWindowTab tab : toolWindow.getToolWindowTabs())
                popupMenu.add(new SelectTabAction(tab));
        }

    }

    protected class WheelScroller implements MouseWheelListener {

        public void mouseWheelMoved(MouseWheelEvent e) {
            switch (e.getWheelRotation()) {
                case 1:
                    Rectangle visRect = viewport.getViewRect();
                    Rectangle bounds = tabContainer.getBounds();

                    visRect.x += e.getUnitsToScroll() * 2;
                    if (visRect.x + visRect.width >= bounds.width)
                        visRect.x = bounds.width - visRect.width;

                    viewport.setViewPosition(new Point(visRect.x, visRect.y));
                    break;
                case -1:
                    visRect = viewport.getViewRect();

                    visRect.x += e.getUnitsToScroll() * 2;
                    if (visRect.x < 0)
                        visRect.x = 0;
                    viewport.setViewPosition(new Point(visRect.x, visRect.y));
                    break;
            }
        }
    }

    protected class TabSelectedPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowTab tab = (ToolWindowTab) evt.getSource();
            if (evt.getNewValue() == Boolean.TRUE) {
                if (selectedTab != null)
                    selectedTab.setSelected(false);
                selectedTab = tab;
            }
        }
    }

    protected class SelectNextTabAction extends AbstractAction {

        public SelectNextTabAction() {
            super(resourceManager.getString("@@tool.tab.selectNext"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 + 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof TabButton) {
                        TabButton tabButton = (TabButton) component;
                        TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                        if (constraints.col1 == nextTabCol) {
                            tabButton.tab.setSelected(true);
                            return;
                        }
                    }
                }
                if (tabs.length > 0)
                    tabs[0].setSelected(true);
            } else if (tabs.length > 0)
                tabs[0].setSelected(true);
        }
    }

    protected class SelectPreviousTabAction extends AbstractAction {

        public SelectPreviousTabAction() {
            super(resourceManager.getString("@@tool.tab.selectPreviuos"));
        }

        public void actionPerformed(ActionEvent e) {
            ToolWindowTab[] tabs = toolWindow.getToolWindowTabs();
            if (selectedTab != null && selecTabButton != null) {
                int nextTabCol = containerLayout.getConstraints(selecTabButton).col1 - 3;

                for (Component component : tabContainer.getComponents()) {
                    if (component instanceof TabButton) {
                        TabButton tabButton = (TabButton) component;
                        TableLayoutConstraints constraints = containerLayout.getConstraints(tabButton);

                        if (constraints.col1 == nextTabCol) {
                            tabButton.tab.setSelected(true);
                            return;
                        }
                    }
                }
                if (tabs.length > 0)
                    tabs[tabs.length - 1].setSelected(true);
            } else if (tabs.length > 0)
                tabs[tabs.length - 1].setSelected(true);
        }
    }

    protected class SelectTabAction extends AbstractAction {
        private ToolWindowTab tab;

        public SelectTabAction(ToolWindowTab tab) {
            super(tab.getTitle());
            this.tab = tab;
        }

        public void actionPerformed(ActionEvent e) {
            tab.setSelected(true);
        }
    }

}
