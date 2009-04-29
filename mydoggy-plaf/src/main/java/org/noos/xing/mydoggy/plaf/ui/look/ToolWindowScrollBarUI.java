package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowScrollBar;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowScrollBarUI extends BasicPanelUI implements ChangeListener {

    public enum Direction {
        LEFT, RIGHT
    }

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowScrollBarUI();
    }


    protected ToolWindowScrollBar toolWindowScrollBar;
    protected TableLayout toolWindowScrollBarLayout;

    protected int orientation;
    protected Container container;

    protected JViewport viewport;
    protected boolean scrollEnabled;


    public void installUI(JComponent c) {
        // Init fields
        this.toolWindowScrollBar = (ToolWindowScrollBar) c;

        switch (toolWindowScrollBar.getToolWindowBar().getAnchor()) {
            case LEFT:
            case RIGHT:
                this.orientation = JSplitPane.VERTICAL_SPLIT;
                break;
            default:
                this.orientation = JSplitPane.HORIZONTAL_SPLIT;
        }
        this.container = toolWindowScrollBar.getContainer();

        super.installUI(c);

        installComponents();
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
    }

    public void stateChanged(ChangeEvent e) {
        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                if (viewport.getView().getHeight() > viewport.getExtentSize().height) {
                    toolWindowScrollBarLayout.setRow(0, 14);
                    toolWindowScrollBarLayout.setRow(4, 14);
                    scrollEnabled = true;
                } else {
                    toolWindowScrollBarLayout.setRow(0, 0);
                    toolWindowScrollBarLayout.setRow(4, 0);
                    scrollEnabled = false;
                }
                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                if (viewport.getView().getWidth() > viewport.getExtentSize().width) {
                    toolWindowScrollBarLayout.setColumn(0, 14);
                    toolWindowScrollBarLayout.setColumn(4, 14);
                    scrollEnabled = true;
                } else {
                    toolWindowScrollBarLayout.setColumn(0, 0);
                    toolWindowScrollBarLayout.setColumn(4, 0);
                    scrollEnabled = false;
                }
                break;
        }
    }


    public void ensureVisible(Component component) {
        // Check for component
        boolean found = false;
        for (int i = 0, size = container.getComponentCount(); i < size; i++) {
            Component containerComponent = container.getComponent(i);

            if (containerComponent == component || (containerComponent instanceof JComponent && ((JComponent)containerComponent).getClientProperty("ra") == component)) {
                component = containerComponent; 
                found = true;
                break;
            }
        }

        if (found) {
            // scrollRectToVisible
            Rectangle cellBounds = component.getBounds();
            if (cellBounds != null) {
                switch(orientation) {
                    case JSplitPane.VERTICAL_SPLIT:
                        cellBounds.y -= viewport.getViewPosition().y;
                        viewport.scrollRectToVisible(cellBounds);
                        break;
                    case JSplitPane.HORIZONTAL_SPLIT:
                        cellBounds.x -= viewport.getViewPosition().x;
                        viewport.scrollRectToVisible(cellBounds);
                        break;

                }
            }
        }
    }


    protected void installComponents() {
        viewport = new JViewport();
        viewport.setView(container);
        viewport.addMouseWheelListener(new WheelScroller());

        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                toolWindowScrollBar.setLayout(toolWindowScrollBarLayout = new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {0, 1, TableLayout.FILL, 1,  0}}));
                toolWindowScrollBar.add(renderArrow(Direction.LEFT, MyDoggyKeySpace.TOOL_SCROLL_BAR_UP), "0,0,c,c");
                toolWindowScrollBar.add(viewport, "0,2,FULL,FULL");
                toolWindowScrollBar.add(renderArrow(Direction.RIGHT, MyDoggyKeySpace.TOOL_SCROLL_BAR_DOWN), "0,4,c,c");

                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                toolWindowScrollBar.setLayout(toolWindowScrollBarLayout = new ExtendedTableLayout(new double[][]{{0, 1, TableLayout.FILL, 1, 0}, {TableLayout.FILL}}));
                toolWindowScrollBar.add(renderArrow(Direction.LEFT, MyDoggyKeySpace.TOOL_SCROLL_BAR_LEFT), "0,0,c,c");
                toolWindowScrollBar.add(viewport, "2,0,FULL,FULL");
                toolWindowScrollBar.add(renderArrow(Direction.RIGHT, MyDoggyKeySpace.TOOL_SCROLL_BAR_RIGHT), "4,0,c,c");

                break;
        }
        
        viewport.addChangeListener(this);
    }

    protected void scrollBy(int direction, int units) {
        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                switch (direction) {
                    case 0:
                        Rectangle visRect = viewport.getViewRect();
                        Rectangle bounds = container.getBounds();

                        visRect.y += units;
                        if (visRect.y + visRect.height >= bounds.height)
                            visRect.y = bounds.height - visRect.height;

                        viewport.setViewPosition(new Point(visRect.x, visRect.y));
                        break;
                    case 1:
                        visRect = viewport.getViewRect();

                        visRect.y -= units;
                        if (visRect.y < 0)
                            visRect.y = 0;
                        viewport.setViewPosition(new Point(visRect.x, visRect.y));
                        break;
                }
                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                switch (direction) {
                    case 0:
                        Rectangle visRect = viewport.getViewRect();
                        Rectangle bounds = container.getBounds();

                        visRect.x += units;
                        if (visRect.x + visRect.width >= bounds.width)
                            visRect.x = bounds.width - visRect.width;

                        viewport.setViewPosition(new Point(visRect.x, visRect.y));
                        break;
                    case 1:
                        visRect = viewport.getViewRect();

                        visRect.x -= units;
                        if (visRect.x < 0)
                            visRect.x = 0;
                        viewport.setViewPosition(new Point(visRect.x, visRect.y));
                        break;
                }
                break;
        }
    }

    protected Component renderArrow(Direction direction, String iconName) {
        JLabel label = new JLabel() {
            public void setUI(LabelUI ui) {
                if (ui instanceof ToolScrollBarArrowUI)
                    super.setUI(ui);
            }
        };
        label.setUI(new ToolScrollBarArrowUI());
        label.setPreferredSize(new Dimension(16, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        label.setFocusable(false);
        label.setBackground(Colors.orange);
        label.setIcon(UIManager.getIcon(iconName));
        label.addMouseListener(new ArrowListener(direction));

        return label;
    }


    public class ArrowListener extends MouseAdapter implements ActionListener {
        protected Timer scrollTimer;
        protected int direction;

        public ArrowListener(Direction direction) {
            this.scrollTimer = new Timer(60, this);
            this.direction = (direction == Direction.RIGHT) ? 0 : 1;
        }

        public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            JComponent c = (JComponent) e.getComponent();
            c.setOpaque(true);
            SwingUtil.repaint(c);

            scrollBy(direction, 10);

            scrollTimer.stop();
            scrollTimer.start();
        }

        public void mouseReleased(MouseEvent e) {
            JComponent c = (JComponent) e.getComponent();
            c.setOpaque(false);
            SwingUtil.repaint(c);

            scrollTimer.stop();
        }


        public void actionPerformed(ActionEvent e) {
            scrollBy(direction, 15);
        }
    }

    public class WheelScroller implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (scrollEnabled)
                scrollBy(e.getWheelRotation() == 1 ? 1 : 0, e.getScrollAmount() * 3);
        }
    }


    public static class ToolScrollBarArrowUI extends MetalLabelUI implements MouseListener {

        protected LineBorder labelBorder;


        public ToolScrollBarArrowUI() {
        }


        public void installUI(JComponent c) {
            super.installUI(c);

            labelBorder = new LineBorder(Color.GRAY, 1, true, 3, 3);
            c.setBorder(labelBorder);
        }

        protected void installListeners(JLabel c) {
            super.installListeners(c);

            c.addMouseListener(this);
        }


        protected void uninstallListeners(JLabel c) {
            super.uninstallListeners(c);

            c.removeMouseListener(this);
        }

        public void update(Graphics g, JComponent c) {
            if (c.isOpaque())
                GraphicsUtil.fillRect(g,
                                      new Rectangle(0, 0, c.getWidth(), c.getHeight()),
                                      UIManager.getColor(MyDoggyKeySpace.TOOL_SCROLL_BAR_UI_BCK_START),
                                      UIManager.getColor(MyDoggyKeySpace.TOOL_SCROLL_BAR_UI_BCK_END),
                                      null,
                                      GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
            else {
                g.setColor(c.getParent().getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }
            paint(g, c);
        }


        public void mouseEntered(MouseEvent e) {
            Component source = e.getComponent();

            if (!source.isOpaque()) {
                labelBorder.setLineColor(Color.BLACK);
                SwingUtil.repaint(source);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component source = e.getComponent();

            if (!source.isOpaque()) {
                labelBorder.setLineColor(Color.GRAY);
                SwingUtil.repaint(source);
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }
}
