package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowTabContainer;
import org.noos.xing.mydoggy.plaf.ui.look.ToolWindowTabTilelUI;
import org.noos.xing.mydoggy.plaf.ui.util.MyDoggyUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTabButtonPanel extends JPanel implements
                                                     PropertyChangeListener,
                                                                MouseListener,
                                                                ActionListener,
                                                                Cleaner {
    protected ToolWindowTabContainer toolWindowTabContainer;

    protected MyDoggyToolWindowManager manager;
    protected ToolWindow toolWindow;
    protected MyDoggyToolWindowTab tab;
    protected DockedContainer dockedContainer;

    protected TableLayout layout;
    protected JLabel titleLabel;
    protected JButton closeButton;
    protected JButton minimizeButton;

    protected boolean pressed;
    protected boolean inside;
    protected boolean selected;


    public ToolWindowTabButtonPanel(MyDoggyToolWindowManager manager,
                                    MyDoggyToolWindowTab tab,
                                    ToolWindowTabContainer toolWindowTabContainer,
                                    DockedContainer dockedContainer) {
        this.tab = tab;
        this.toolWindow = tab.getOwner();
        this.toolWindowTabContainer = toolWindowTabContainer;
        this.dockedContainer = dockedContainer;

        tab.addPropertyChangeListener(this);
        tab.getCleaner().addCleaner(this);

        putClientProperty(ToolWindowTab.class, tab);

        final ResourceManager resourceManager = manager.getResourceManager();

        // Setup Panel
        String name = "toolWindow." + tab.getOwner().getId() + ".tab." + tab.getTitle();
        setName(name);
        setLayout(layout = new TableLayout(new double[][]{{-1, 0, 0, 0, 0}, {-1}}));
        setOpaque(false);
        setFocusable(false);
        manager.getResourceManager().applyCustomization(MyDoggyKeySpace.TOOL_WINDOW_TAB_BUTTON, this,
                                                        manager.getContext(MyDoggyToolWindowTab.class, tab));

        addMouseListener(toolWindowTabContainer.getMouseEventDispatcher());
        addMouseMotionListener(toolWindowTabContainer.getMouseEventDispatcher());

        this.selected = this.pressed = this.inside = false;

        // Prepare components

        // Title
        titleLabel = (JLabel) manager.getResourceManager().createComponent(
                MyDoggyKeySpace.TOOL_WINDOW_TAB_TITLE,
                manager.getContext()
        );
        titleLabel.setText(tab.getTitle());
        titleLabel.setName(name + ".title");
        titleLabel.setIcon(tab.getIcon());

        titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
        titleLabel.setOpaque(false);
        titleLabel.setFocusable(false);
        // TODO: change toolwindow.title.font.size...
        titleLabel.setFont(titleLabel.getFont().deriveFont(MyDoggyUtil.getFloat("toolwindow.title.font.size", 12)));
        titleLabel.setUI(new ToolWindowTabTilelUI(tab));
        titleLabel.addMouseListener(dockedContainer.getTitleBarMouseAdapter());
        titleLabel.addMouseListener(toolWindowTabContainer.getMouseEventDispatcher());
        titleLabel.addMouseMotionListener(toolWindowTabContainer.getMouseEventDispatcher());
        titleLabel.addMouseListener(this);
        add(titleLabel, "0,0,FULL,FULL");

        // Buttons
        closeButton = (JButton) resourceManager.createComponent(
                MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON,
                manager.getContext()
        );
        closeButton.setName(name + ".closeButton");
        closeButton.setActionCommand("close");
        closeButton.addActionListener(this);
        closeButton.setToolTipText(resourceManager.getString("@@tool.tab.close"));
        closeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_CLOSE));

        minimizeButton = (JButton) resourceManager.createComponent(
                MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON,
                manager.getContext()
        );
        minimizeButton.setName(name + ".minimizeButton");
        minimizeButton.setActionCommand("minimize");
        minimizeButton.addActionListener(this);
        minimizeButton.setToolTipText(resourceManager.getString("@@tool.tab.minimize"));
        minimizeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE));

        add(minimizeButton, "2,0,FULL,c");
        add(closeButton, "4,0,FULL,c");

        // Register DragGesture
        SwingUtil.registerDragGesture(this, toolWindowTabContainer.getDragGesture());
        SwingUtil.registerDragGesture(titleLabel, toolWindowTabContainer.getDragGesture());
        SwingUtil.registerDragGesture(minimizeButton, toolWindowTabContainer.getDragGesture());
        SwingUtil.registerDragGesture(closeButton, toolWindowTabContainer.getDragGesture());
    }


    public ToolWindowTab getToolWindowTab() {
        return tab;
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
                    ToolWindowTabButtonPanel.this.tab.setSelected(true);
                }
            });
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if ("selected".equals(property)) {
            if (evt.getNewValue() == Boolean.FALSE) {
                titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_UNSELECTED));
                closeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_CLOSE_INACTIVE));
                minimizeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE_INACTIVE));
                setButtonsEnabled(false);

                selected = false;
            } else {
                tab.setFlashing(false);

                // Ensure position
                ensureVisible();

                titleLabel.setForeground(UIManager.getColor(MyDoggyKeySpace.TWTB_TAB_FOREGROUND_SELECTED));
                closeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_CLOSE));
                minimizeButton.setIcon(UIManager.getIcon(MyDoggyKeySpace.TAB_MINIMIZE));
                setButtonsEnabled(true);
                selected = true;
            }
            SwingUtil.repaint(this);
        } else if ("title".equals(property)) {
            titleLabel.setText((String) evt.getNewValue());
            setName("toolWindow." + toolWindow.getId() + ".tabs." + tab.getTitle());
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
            revalidate();
            repaint();
        } else if ("closeable".equals(property)) {
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
        tab.removePropertyChangeListener(this);

        removeMouseMotionListener(toolWindowTabContainer.getMouseEventDispatcher());
        removeMouseListener(toolWindowTabContainer.getMouseEventDispatcher());

        putClientProperty(ToolWindowTab.class, null);

        titleLabel.removeMouseListener(dockedContainer.getTitleBarMouseAdapter());
        titleLabel.removeMouseListener(toolWindowTabContainer.getMouseEventDispatcher());
        titleLabel.removeMouseMotionListener(toolWindowTabContainer.getMouseEventDispatcher());
        titleLabel.removeMouseListener(this);

        tab = null;
        manager = null;
    }


    protected void setButtonsEnabled(boolean enabled) {
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
        revalidate();
        repaint();
    }

    protected void ensureVisible() {
        toolWindowTabContainer.ensureVisible(getBounds());
    }


}
