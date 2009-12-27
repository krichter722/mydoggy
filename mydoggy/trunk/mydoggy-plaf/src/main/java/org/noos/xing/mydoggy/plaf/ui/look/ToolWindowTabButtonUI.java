package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowListener;
import org.noos.xing.mydoggy.event.ToolWindowTabEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabTitle;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButton;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MyDoggyUtil;
import org.noos.xing.mydoggy.plaf.ui.util.RemoveNotifyDragListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabButtonUI extends BasicPanelUI implements Cleaner,
                                                                   PropertyChangeListener,
                                                                   MouseListener,
                                                                   ActionListener {


    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowTabButtonUI();
    }


    protected ToolWindowTabPanel toolWindowTabPanel;
    protected ToolWindowTabButton toolWindowTabButton;

    protected MyDoggyToolWindow toolWindow;
    protected MyDoggyToolWindowTab tab;

    protected MouseListener titleBarMouseListener;
    protected TabButtonToolWindowListener tabButtonToolWindowListener;

    protected TableLayout layout;
    protected JLabel titleLabel;
    protected JButton closeButton;
    protected JButton minimizeButton;

    protected boolean pressed;
    protected boolean inside;
    protected boolean selected;

    // Flashing fields
    protected Timer flashingTimer;
    protected int flasingDuration = -1;
    protected boolean flashingState;

    // Drag fields
    protected RemoveNotifyDragListener removeNotifyDragListener;


    public ToolWindowTabButtonUI() {
    }


    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if ("flash".equals(property)) {
            if (evt.getNewValue() == Boolean.TRUE) {
                if (!tab.isSelected()) {
                    flasingDuration = SwingUtil.getInt(evt, -1);;
                    SwingUtil.repaint(toolWindowTabButton);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(toolWindowTabButton);
                }
            }
        } else if ("selected".equals(property)) {
            if (evt.getNewValue() == Boolean.FALSE) {
                // Disable the button
                setEnabled(false);

                // The tab is no more selected
                selected = false;
            } else {
                // Stop flashing
                tab.setFlashing(false);

                // Ensure position
                ensureVisible();

                // Enable the button
                setEnabled(true);

                // The tab is now selected
                selected = true;
            }
            SwingUtil.repaint(toolWindowTabButton);
        } else if ("title".equals(property)) {
            titleLabel.setText((String) evt.getNewValue());
            toolWindowTabButton.setName("toolWindow." + toolWindow.getId() + ".tabs." + tab.getTitle());
            SwingUtil.repaint(titleLabel);
        } else if ("icon".equals(property)) {
            titleLabel.setIcon((Icon) evt.getNewValue());
        } else if ("ensureVisible".equals(property)) {
            ensureVisible();
        } else if ("minimizable".equals(property)) {
            if (tab.isMinimizable()) {
                layout.setColumn(1, 1);
                layout.setColumn(2, 14);
            } else {
                layout.setColumn(1, 0);
                layout.setColumn(2, 0);
            }
            toolWindowTabButton.revalidate();
            toolWindowTabButton.repaint();
        } else if ("closeable".equals(property)) {
            if (tab.isCloseable()) {
                layout.setColumn(3, 1);
                layout.setColumn(4, 14);
            } else {
                layout.setColumn(3, 0);
                layout.setColumn(4, 0);
            }
            toolWindowTabButton.revalidate();
            toolWindowTabButton.repaint();
        }                                              
    }

    public void cleanup() {
        uninstallUI(toolWindowTabButton);
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
            toolWindowTabButton.repaint();
        } else {
            pressed = false;
            toolWindowTabButton.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        pressed = false;
        toolWindowTabButton.repaint();
    }

    public void mouseEntered(MouseEvent e) {
        inside = true;
        toolWindowTabButton.repaint();
    }

    public void mouseExited(MouseEvent e) {
        inside = false;
        toolWindowTabButton.repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    tab.setSelected(true);
                }
            });
        }
    }


    public void installUI(JComponent c) {
        // Init fields
        this.toolWindowTabButton = (ToolWindowTabButton) c;
        this.tab = (MyDoggyToolWindowTab) toolWindowTabButton.getToolWindowTab();
        this.toolWindow = (MyDoggyToolWindow) tab.getOwner();

        this.toolWindowTabPanel = toolWindowTabButton.getToolWindowTabPanel();

        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Release timers
        if (flashingTimer != null)
            flashingTimer.stop();

        // Reset Fields
        flashingTimer = null;
        tab = null;
        toolWindow = null;
    }

    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        installComponents();
        installListeners();
    }

    protected void uninstallDefaults(JPanel p) {
        super.uninstallDefaults(p);

        uninstallListeners();
        uninstallComponents();
    }


    public void update(Graphics g, final JComponent c) {
        if (tab == null || toolWindow == null)
            return;

        Rectangle bounds = c.getBounds();
        bounds.x = bounds.y = 0;

        if (tab.isFlashing() && toolWindow.isVisible()) {
            if (flashingState) {
                GraphicsUtil.fillRect(g, bounds,
                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                      new RoundRectangle2D.Double(
                                              bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                      ),
                                      GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
            } else {
                if (toolWindow.isActive()) {
                    GraphicsUtil.fillRect(g, bounds,
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
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
                        Rectangle bounds = toolWindowTabButton.getBounds();
                        bounds.x = bounds.y = 0;

                        if (start == 0)
                            start = System.currentTimeMillis();

                        flashingState = !flashingState;
                        c.putClientProperty("mydoggy.flashingState", flashingState);

                        SwingUtil.repaint(toolWindowTabButton);

                        if (flasingDuration != -1 && System.currentTimeMillis() - start > flasingDuration)
                            tab.setFlashing(false);
                    }
                });

                flashingState = true;
            }

            if (!flashingTimer.isRunning()) {
                flashingTimer.start();
            }
        } else if (MyDoggyUtil.getNumTabs(toolWindow) > 1) {
            if (tab.isSelected()) {
                if (toolWindow.isActive()) {
                    GraphicsUtil.fillRect(g, bounds,
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                          UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                          new RoundRectangle2D.Double(
                                                  bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10
                                          ),
                                          GraphicsUtil.UP_TO_BOTTOM_GRADIENT);
                }
            }
        }

        super.update(g, c);
    }


    protected void installComponents() {
        this.toolWindow = (MyDoggyToolWindow) tab.getOwner();

        String name = "toolWindow." + tab.getOwner().getId() + ".tab." + tab.getTitle();
        toolWindowTabButton.setName(name);
        toolWindowTabButton.setLayout(layout = new TableLayout(new double[][]{{-1, 0, 0, 0, 0}, {-1}}));
        toolWindowTabButton.setOpaque(false);
        toolWindowTabButton.setFocusable(false);
        toolWindowTabButton.setBorder(new EmptyBorder(0, 5, 0, 5));

        this.selected = this.pressed = this.inside = false;

        // Prepare components

        // Title
        titleLabel = new ToolWindowTabTitle(tab);
        titleLabel.setName(name + ".title");
        toolWindowTabButton.add(titleLabel, "0,0,FULL,FULL");

        // Buttons
        closeButton = new ToolWindowTitleButton();
        closeButton.setName(name + ".closeButton");
        closeButton.setActionCommand("close");
        closeButton.setToolTipText(SwingUtil.getString("@@tool.tab.close"));
        closeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_CLOSE));

        minimizeButton = new ToolWindowTitleButton();
        minimizeButton.setName(name + ".minimizeButton");
        minimizeButton.setActionCommand("minimize");
        minimizeButton.setToolTipText(SwingUtil.getString("@@tool.tab.minimize"));
        minimizeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE));

        toolWindowTabButton.add(minimizeButton, "2,0,FULL,c");
        toolWindowTabButton.add(closeButton, "4,0,FULL,c");
    }

    protected void uninstallComponents() {

    }

    protected void installListeners() {
        tab.addPlafPropertyChangeListener(this); 
        tab.getCleanerAggregator().addCleaner(this);

        toolWindow.addToolWindowListener(tabButtonToolWindowListener = new TabButtonToolWindowListener());

        toolWindowTabButton.addMouseListener(toolWindowTabPanel.getMouseEventDispatcher());
        toolWindowTabButton.addMouseMotionListener(toolWindowTabPanel.getMouseEventDispatcher());

        titleLabel.addMouseListener(titleBarMouseListener = new TitleBarMouseAdapter(((MyDoggyToolWindowManager) tab.getOwner().getDockableManager()).getDescriptor(tab.getOwner())));
        titleLabel.addMouseListener(toolWindowTabPanel.getMouseEventDispatcher());
        titleLabel.addMouseMotionListener(toolWindowTabPanel.getMouseEventDispatcher());
        titleLabel.addMouseListener(this);

        closeButton.addActionListener(this);
        minimizeButton.addActionListener(this);

        // Register DragSupportListener
        toolWindow.getDescriptor().getManager().addRemoveNotifyListener(
                removeNotifyDragListener = new RemoveNotifyDragListener(
                        toolWindowTabPanel.getDragListener(),
                        toolWindowTabButton, titleLabel, minimizeButton, closeButton

                )
        );
    }

    protected void uninstallListeners() {
        // Remove drag gesture
        removeNotifyDragListener.cleanup();

        tab.removePlafPropertyChangeListener(this);
        tab.getCleanerAggregator().removeCleaner(this);

        toolWindow.removeToolWindowListener(tabButtonToolWindowListener);

        toolWindowTabButton.removeMouseListener(toolWindowTabPanel.getMouseEventDispatcher());
        toolWindowTabButton.removeMouseMotionListener(toolWindowTabPanel.getMouseEventDispatcher());

        titleLabel.removeMouseListener(titleBarMouseListener);
        titleLabel.removeMouseListener(toolWindowTabPanel.getMouseEventDispatcher());
        titleLabel.removeMouseMotionListener(toolWindowTabPanel.getMouseEventDispatcher());
        titleLabel.removeMouseListener(this);

        closeButton.removeActionListener(this);
        minimizeButton.removeActionListener(this);

        toolWindow.getDescriptor().getManager().removeRemoveNotifyListener(removeNotifyDragListener);
    }


    protected void setEnabled(boolean enabled) {
        if (enabled) {
            titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
            closeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_CLOSE));
            minimizeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE));
        } else {
            titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
            closeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_CLOSE_INACTIVE));
            minimizeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE_INACTIVE));
        }

        if (enabled && MyDoggyUtil.getNumTabs(toolWindow) > 1) {
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

        toolWindowTabButton.revalidate();
        toolWindowTabButton.repaint();
    }

    protected void ensureVisible() {
        toolWindowTabPanel.ensureVisible(toolWindowTabButton.getBounds());
    }

    protected void hideAllButtons() {
        layout.setColumn(1, 0);
        layout.setColumn(2, 0);
        layout.setColumn(3, 0);
        layout.setColumn(4, 0);

        SwingUtil.repaint(toolWindowTabButton);
    }


    public class TabButtonToolWindowListener implements ToolWindowListener, Cleaner {

        public TabButtonToolWindowListener() {
            tab.getCleanerAggregator().addBefore(ToolWindowTabButtonUI.this, this);
        }

        public void toolWindowTabAdded(ToolWindowTabEvent event) {
        }

        public boolean toolWindowTabRemoving(ToolWindowTabEvent event) {
            return true;
        }

        public void toolWindowTabRemoved(ToolWindowTabEvent event) {
            if (toolWindow != null)
                if (MyDoggyUtil.getNumTabs(toolWindow) == 1) 
                    hideAllButtons();
        }

        public void cleanup() {
            if (toolWindow != null)
                toolWindow.removeToolWindowListener(this);
        }
    }
}
