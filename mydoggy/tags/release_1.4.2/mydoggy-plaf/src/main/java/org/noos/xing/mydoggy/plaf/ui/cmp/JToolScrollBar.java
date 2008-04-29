package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.plaf.common.context.DefaultMutableContext;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JToolScrollBar extends JComponent implements ChangeListener {
    protected enum Direction {
        LEFT, RIGHT
    }

    protected int orientation;
    protected JViewport viewport;
    protected Container container;
    protected boolean scrollEnabled;

    public JToolScrollBar(ResourceManager resourceManager, int orientation, Container container) {
        this.orientation = orientation;
        this.container = container;
        this.scrollEnabled = false;

        initComponents(resourceManager);
    }

    public void stateChanged(ChangeEvent e) {
        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                if (viewport.getView().getHeight() > viewport.getExtentSize().height) {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setRow(0, 14);
                    layout.setRow(4, 14);
                    scrollEnabled = true;
                } else {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setRow(0, 0);
                    layout.setRow(4, 0);
                    scrollEnabled = false;
                }
                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                if (viewport.getView().getWidth() > viewport.getExtentSize().width) {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setColumn(0, 14);
                    layout.setColumn(4, 14);
                    scrollEnabled = true;
                } else {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setColumn(0, 0);
                    layout.setColumn(4, 0);
                    scrollEnabled = false;
                }
                break;
        }
    }

    public void ensureVisible(Component component) {
        // Check for component
        boolean found = false;
        for (int i = 0, size = container.getComponentCount(); i < size; i++) {
            if (container.getComponent(i) == component)
                found = true;
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


    protected void initComponents(ResourceManager resourceManager) {
        viewport = new JViewport();
        viewport.setView(container);
        viewport.addMouseWheelListener(new WheelScroller());

        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {0, 1, TableLayout.FILL, 1,  0}}));
                add(renderArrow(resourceManager, Direction.LEFT, MyDoggyKeySpace.TOOL_SCROLL_BAR_UP), "0,0,c,c");
                add(viewport, "0,2,FULL,FULL");
                add(renderArrow(resourceManager, Direction.RIGHT, MyDoggyKeySpace.TOOL_SCROLL_BAR_DOWN), "0,4,c,c");

                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                setLayout(new ExtendedTableLayout(new double[][]{{0, 1, TableLayout.FILL, 1, 0}, {TableLayout.FILL}}));
                add(renderArrow(resourceManager, Direction.LEFT, MyDoggyKeySpace.TOOL_SCROLL_BAR_LEFT), "0,0,c,c");
                add(viewport, "2,0,FULL,FULL");
                add(renderArrow(resourceManager, Direction.RIGHT, MyDoggyKeySpace.TOOL_SCROLL_BAR_RIGHT), "4,0,c,c");

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

    protected Component renderArrow(ResourceManager resourceManager, Direction direction, String iconName) {
        JLabel label =  (JLabel) resourceManager.createComponent(MyDoggyKeySpace.TOOL_SCROLL_BAR_ARROW,
                                                                 new DefaultMutableContext("icon", iconName,
                                                                                           ResourceManager.class,
                                                                                           resourceManager));
        label.addMouseListener(new ArrowListener(direction));
        return label;
    }


    protected class ArrowListener extends MouseAdapter implements ActionListener {
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

    protected class WheelScroller implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (scrollEnabled)
                scrollBy(e.getWheelRotation() == 1 ? 1 : 0, e.getScrollAmount() * 3);
        }
    }

}
