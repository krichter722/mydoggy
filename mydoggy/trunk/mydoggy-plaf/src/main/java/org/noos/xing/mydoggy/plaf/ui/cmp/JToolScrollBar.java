package org.noos.xing.mydoggy.plaf.ui.cmp;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.look.ScrollToolBarArrowUI;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.LabelUI;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class JToolScrollBar extends JComponent implements ChangeListener {
    private int orientation;
    private JViewport viewport;
    private Container container;
    private boolean scrollEnabled;

    public JToolScrollBar(int orientation, Container container) {
        this.orientation = orientation;
        this.container = container;
        this.scrollEnabled = false;

        initComponents();
    }

    public void stateChanged(ChangeEvent e) {
        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                if (viewport.getView().getHeight() > viewport.getExtentSize().height) {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setRow(0, 14);
                    layout.setRow(2, 14);
                    scrollEnabled = true;
                } else {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setRow(0, 0);
                    layout.setRow(2, 0);
                    scrollEnabled = false;
                }
                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                if (viewport.getView().getWidth() > viewport.getExtentSize().width) {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setColumn(0, 14);
                    layout.setColumn(2, 14);
                    scrollEnabled = true;
                } else {
                    TableLayout layout = (TableLayout) getLayout();
                    layout.setColumn(0, 0);
                    layout.setColumn(2, 0);
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


    protected void initComponents() {
        viewport = new JViewport();
        viewport.setView(container);
        viewport.addMouseWheelListener(new WheelScroller());

        switch (orientation) {
            case JSplitPane.VERTICAL_SPLIT:
                setLayout(new ExtendedTableLayout(new double[][]{{TableLayout.FILL}, {0, TableLayout.FILL, 0}}));
                add(renderArrow("U", "arrowUp"), "0,0,c,c");
                add(viewport, "0,1,FULL,FULL");
                add(renderArrow("D", "arrowDown"), "0,2,c,c");

                break;
            case JSplitPane.HORIZONTAL_SPLIT:
                setLayout(new ExtendedTableLayout(new double[][]{{0, TableLayout.FILL, 0}, {TableLayout.FILL}}));
                add(renderArrow("U", "arrowLeft"), "0,0,c,c");
                add(viewport, "1,0,FULL,FULL");
                add(renderArrow("D", "arrowRight"), "2,0,c,c");

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

    protected Component renderArrow(String name, String iconName) {
        JLabel label = new JLabel() {
            public void setUI(LabelUI ui) {
                if (ui instanceof ScrollToolBarArrowUI)
                    super.setUI(ui);
            }
        };
        label.setUI(new ScrollToolBarArrowUI());
        label.setPreferredSize(new Dimension(16, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setOpaque(false);
        label.setFocusable(false);
        label.setBackground(Colors.orange);
        label.setName(name);
        label.addMouseListener(new ArrowListener());
        // TODO: move to resourceManager
        label.setIcon(SwingUtil.loadIcon("org/noos/xing/mydoggy/plaf/ui/icons/" + iconName + ".png"));

        return label;
    }


    class ArrowListener extends MouseAdapter implements ActionListener {
        private Timer scrollTimer;

        private int direction;

        public ArrowListener() {
            scrollTimer = new Timer(60, this);
        }

        public void mousePressed(MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e))
                return;

            JComponent c = (JComponent) e.getComponent();
            c.setOpaque(true);
            SwingUtil.repaint(c);


            direction = ("U".equals(c.getName())) ? 1 : 0;
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

    class WheelScroller implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (scrollEnabled)
                scrollBy(e.getWheelRotation() == 1 ? 0 : 1, e.getScrollAmount() * 3);
        }
    }

}
