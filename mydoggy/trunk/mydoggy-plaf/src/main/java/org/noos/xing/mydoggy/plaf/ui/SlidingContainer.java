package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.SlidingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.SlidingBorder;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.SlidingMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class SlidingContainer extends MyDoggyToolWindowContainer implements Cleaner {
    protected SlidingAnimation slidingAnimation;

    protected SlidingBorder border;
    protected Container barContainer;
    protected JLayeredPane layeredPane;
    protected JPanel mainPanel;
    protected TranslucentPanel sheet;

    protected SlidingMouseInputHandler slidingMouseInputHandler;


    public SlidingContainer(ToolWindowDescriptor toolWindowDescriptor) {
        super(toolWindowDescriptor);

        initComponents();
        initListeners();
    }


    public void cleanup() {
        // Remove listeners
        if (sheet != null) {
            sheet.removeMouseMotionListener(slidingMouseInputHandler);
            sheet.removeMouseListener(slidingMouseInputHandler);
        }

        // Finalize
        layeredPane = null;
        super.cleanup();
    }

    public void setVisible(boolean visible, Container barContainer) {
        this.barContainer = barContainer;

        Component content = toolWindowPanel;
        sheet.remove(content);

        slidingAnimation.stop();

        if (visible) {
            descriptor.focusValueAdjusting = true;

            // Reset Layout
            TableLayout layout = (TableLayout) sheet.getLayout();
            layout.setColumn(0, 0);
            layout.setColumn(2, 0);
            layout.setRow(0, 0);
            layout.setRow(2, 0);

            // TODO: can we call validate???
            barContainer.getParent().doLayout();
            resize();

            content.setVisible(true);
            sheet.add(content, "1,1,FULL,FULL");

            // Prepare sheet
            border.setAnchor(toolWindow.getAnchor());
            sheet.setBorder(border);

            int height = mainPanel.getHeight();
            Point point = SwingUtilities.convertPoint(mainPanel, 0, 0, layeredPane);

            sheet.setBounds(point.x, point.y, mainPanel.getWidth(), height);

            layeredPane.remove(sheet);
            layeredPane.setLayer(sheet, JLayeredPane.DEFAULT_LAYER + 2);
            layeredPane.add(sheet);

            if (descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isAnimating())
                slidingAnimation.show(sheet.getBounds());
        } else {
            // Set Layout
            TableLayout layout = (TableLayout) sheet.getLayout();
            layout.setColumn(0, 2);
            layout.setColumn(2, 2);
            layout.setRow(0, 2);
            layout.setRow(2, 2);

            switch (descriptor.getToolWindow().getAnchor()) {
                case TOP:
                case BOTTOM:
                    descriptor.setDividerLocation(sheet.getHeight());
                    break;
                case LEFT:
                case RIGHT:
                    descriptor.setDividerLocation(sheet.getWidth());
                    break;
            }

            if (descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isAnimating())
                slidingAnimation.hide(sheet.getBounds());
            else {
                layeredPane.remove(sheet);
                sheet.setBorder(null);
                sheet.removeAll();
                SwingUtil.repaint(layeredPane);
            }
        }
    }


    protected void initComponents() {
        mainPanel = new JPanel();
        sheet = new TranslucentPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
        border = new SlidingBorder();
        slidingAnimation = new SlidingAnimation();

        layeredPane = descriptor.getManager().getLayeredPane();
        descriptor.getManager().addComponentListener(new ComponentResizer());
    }

    protected void initListeners() {
        // Init tool window properties listeners
        PropertyChangeEventSource toolWindowSource = descriptor.getToolWindow();
        toolWindowSource.addPlafPropertyChangeListener("anchor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ToolWindow evtToolWindow = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
                if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible() && !evtToolWindow.isVisible())
                    update();
            }
        });
        toolWindowSource.addPlafPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() == ToolWindowType.SLIDING) {
                    if (layeredPane != null) {
                        sheet.addMouseMotionListener(slidingMouseInputHandler);
                        sheet.addMouseListener(slidingMouseInputHandler);
                    }
                } else {
                    if (layeredPane != null) {
                        sheet.removeMouseMotionListener(slidingMouseInputHandler);
                        sheet.removeMouseListener(slidingMouseInputHandler);
                    }
                }
            }
        });
        toolWindowSource.addPlafPropertyChangeListener("active", new ActivePropertyChangeListener());
        toolWindowSource.addPlafPropertyChangeListener("maximized", new MaximizedPropertyChangeListener());

        // Init sliding type descriptor properties listeners
        PropertyChangeEventSource slidingDescriptorSource = (PropertyChangeEventSource) descriptor.getToolWindow().getTypeDescriptor(SlidingTypeDescriptor.class);
        slidingDescriptorSource.addPlafPropertyChangeListener(new SlidingTypePropertyChangeListener());

        descriptor.getManager().addInternalPropertyChangeListener("tempShowed", new TempShowedPropertyChangeListener());
        descriptor.getManager().addInternalPropertyChangeListener("manager.window.ancestor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() != null) {
                    layeredPane = descriptor.getManager().getLayeredPane();

                    // Check type now...
                    if (toolWindow.getType() == ToolWindowType.SLIDING) {
                        sheet.removeMouseMotionListener(slidingMouseInputHandler);
                        sheet.removeMouseListener(slidingMouseInputHandler);

                        sheet.addMouseMotionListener(slidingMouseInputHandler);
                        sheet.addMouseListener(slidingMouseInputHandler);
                    }
                }
            }
        });

        slidingMouseInputHandler = new SlidingMouseInputHandler(descriptor);

    }

    protected void update() {
        // Reset Layout
        TableLayout layout = (TableLayout) sheet.getLayout();
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);

        if (barContainer != null)
            barContainer.getParent().getLayout().layoutContainer(barContainer.getParent());
        resize();

        Component content = toolWindowPanel;
        sheet.remove(content);
        sheet.add(content, "1,1,FULL,FULL");

        // Prepare sheet
        border.setAnchor(toolWindow.getAnchor());
        sheet.setBorder(border);

        int height = mainPanel.getHeight();
        Point point = SwingUtilities.convertPoint(mainPanel, 0, 0, layeredPane);

        sheet.setBounds(point.x, point.y, mainPanel.getWidth(), height);

        layeredPane.remove(sheet);
        layeredPane.setLayer(sheet, JLayeredPane.DEFAULT_LAYER + 2);
        layeredPane.add(sheet);
        layeredPane.validate();
    }

    protected void resize() {
        int length = Math.max(descriptor.getDividerLocation(),
                              descriptor.getDockedTypeDescriptor().getMinimumDockLength());
        if (length == -1)
            length = 200;

        switch (toolWindow.getAnchor()) {
            case LEFT:
                int height = barContainer.getHeight();
                mainPanel.setSize(length, height);

                Point location = new Point(0, 0);
                SwingUtilities.convertPointToScreen(location, barContainer);
                location.x += barContainer.getWidth();
                mainPanel.setLocation(location);
                break;
            case RIGHT:
                height = barContainer.getHeight();
                mainPanel.setSize(length, height);

                location = new Point(0, 0);
                SwingUtilities.convertPointToScreen(location, barContainer);
                location.x -= mainPanel.getWidth();
                mainPanel.setLocation(location);
                break;
            case TOP:
                int width = barContainer.getWidth();
                mainPanel.setSize(width, length);

                location = new Point(0, 0);
                SwingUtilities.convertPointToScreen(location, barContainer);
                location.y += barContainer.getHeight();
                mainPanel.setLocation(location);
                break;
            case BOTTOM:
                width = barContainer.getWidth();
                mainPanel.setSize(width, length);

                location = new Point(0, 0);
                SwingUtilities.convertPointToScreen(location, barContainer);
                location.y -= mainPanel.getHeight();
                mainPanel.setLocation(location);
                break;
        }
    }


    public class SlidingAnimation extends AbstractAnimation {
        protected int length;
        protected Rectangle bounds;
        protected int lastLen = 0;

        public SlidingAnimation() {
            super(60f);
        }

        protected void onStartAnimation(Direction direction) {
            lastLen = 0;
            switch (toolWindow.getAnchor()) {
                case LEFT:
                case RIGHT:
                    length = bounds.width;
                    break;
                case TOP:
                case BOTTOM:
                    length = bounds.height;
                    break;
            }
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    sheet.setBounds(bounds);
                    descriptor.assignFocus();

                    descriptor.focusValueAdjusting = false;
                    break;
                case OUTGOING:
                    layeredPane.remove(sheet);
                    sheet.setBorder(null);
                    sheet.removeAll();
                    break;
            }
        }

        protected void onHide(Object... params) {
            this.bounds = (Rectangle) params[0];
        }

        protected void onShow(Object... params) {
            this.bounds = (Rectangle) params[0];

            switch (toolWindow.getAnchor()) {
                case LEFT:
                    sheet.setSize(0, sheet.getHeight());
                    break;
                case RIGHT:
                    sheet.setLocation(sheet.getX() + sheet.getWidth(), sheet.getY());
                    sheet.setSize(0, sheet.getHeight());
                    break;
                case TOP:
                    sheet.setSize(sheet.getWidth(), 0);
                    break;
                case BOTTOM:
                    sheet.setLocation(sheet.getX(), sheet.getY() + sheet.getHeight());
                    sheet.setSize(sheet.getWidth(), 0);
                    break;
            }
        }

        protected float onAnimating(float animationPercent) {
            int animatingLength = 0;

            Direction direction = getAnimationDirection();
            switch (toolWindow.getAnchor()) {
                case LEFT:
                    if (direction == Direction.INCOMING)
                        animatingLength = (int) (animationPercent * length);
                    else
                        animatingLength = (int) ((1f - animationPercent) * length);
                    sheet.setSize(animatingLength, sheet.getHeight());
                    break;
                case RIGHT:
                    animatingLength = (int) (animationPercent * length);
                    if (direction == Direction.INCOMING) {
                        sheet.setLocation(sheet.getX() - (animatingLength - lastLen), sheet.getY());
                        sheet.setSize(animatingLength, sheet.getHeight());
                    } else {
                        sheet.setLocation(bounds.x + animatingLength, sheet.getY());
                        sheet.setSize((int) ((1f - animationPercent) * length), sheet.getHeight());
                    }
                    break;
                case TOP:
                    if (direction == Direction.INCOMING)
                        animatingLength = (int) (animationPercent * length);
                    else
                        animatingLength = (int) ((1f - animationPercent) * length);
                    sheet.setSize(sheet.getWidth(), animatingLength);
                    break;
                case BOTTOM:
                    animatingLength = (int) (animationPercent * length);
                    if (direction == Direction.INCOMING) {
                        sheet.setLocation(sheet.getX(), sheet.getY() - (animatingLength - lastLen));
                        sheet.setSize(sheet.getWidth(), animatingLength);
                    } else {
                        sheet.setLocation(sheet.getX(), bounds.y + animatingLength);
                        sheet.setSize(sheet.getWidth(), (int) ((1f - animationPercent) * length));
                    }

                    break;
            }
            sheet.validate();
            sheet.repaint();

            lastLen = animatingLength;

            return animationPercent;
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.NONE : super.chooseFinishDirection(type);
        }

    }

    public class ActivePropertyChangeListener implements PropertyChangeListener, ActionListener {
        protected TransparencyAnimation animation;
        protected Timer timer;

        public ActivePropertyChangeListener() {
            this.animation = new TransparencyAnimation(sheet, sheet, 1.0f, 500f);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (descriptor.getToolWindow().getType() == ToolWindowType.SLIDING) {
                if (Boolean.TRUE.equals(evt.getNewValue())) {
                    if (timer != null) {
                        timer.stop();
                        if (animation.isAnimating())
                            animation.stop();
                    }

                    sheet.setAlphaModeRatio(1.0f);
                } else {
                    SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING);
                    if (slidingTypeDescriptor.isTransparentMode()) {
                        timer = new Timer(slidingTypeDescriptor.getTransparentDelay(), this);
                        timer.start();
                    }
                }
                SwingUtil.repaint(layeredPane);
            }
        }

        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                timer.stop();

                SlidingTypeDescriptor slidingTypeDescriptor = (SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING);
                animation.setAlpha(slidingTypeDescriptor.getTransparentRatio());
                animation.show();
            }
        }
    }

    public class MaximizedPropertyChangeListener implements PropertyChangeListener {
        protected Rectangle oldBounds = null;

        public void propertyChange(PropertyChangeEvent evt) {
            if (toolWindow.getType() == ToolWindowType.SLIDING) {
                if ((Boolean) evt.getNewValue()) {
                    oldBounds = sheet.getBounds();

                    switch (toolWindow.getAnchor()) {
                        case LEFT:
                            sheet.setBounds(
                                    sheet.getX(),
                                    sheet.getY(),
                                    calcMaxWidth(),
                                    sheet.getHeight()
                            );
                            break;
                        case RIGHT:
                            sheet.setBounds(
                                    calcFirstX(),
                                    sheet.getY(),
                                    calcMaxWidth(),
                                    sheet.getHeight()
                            );
                            break;
                        case TOP:
                            sheet.setBounds(
                                    sheet.getX(),
                                    sheet.getY(),
                                    sheet.getWidth(),
                                    calcMaxHeight()
                            );
                            break;
                        case BOTTOM:
                            sheet.setBounds(
                                    sheet.getX(),
                                    calcFirstY(),
                                    sheet.getWidth(),
                                    calcMaxHeight()
                            );
                            break;
                    }
                } else {
                    sheet.setBounds(oldBounds);
                }
                SwingUtil.repaint(sheet);
            }
        }

        protected int calcFirstX() {
            return descriptor.getManagerBounds().x +
                   descriptor.getToolBar(ToolWindowAnchor.LEFT).getSize();
        }

        protected int calcFirstY() {
            return descriptor.getManagerBounds().y +
                   descriptor.getToolBar(ToolWindowAnchor.TOP).getSize() +
                   descriptor.getManager().getJMenuBarExtraHeight();
        }

        protected int calcMaxWidth() {
            int width = descriptor.getManagerBounds().width;
            width -= descriptor.getToolBar(ToolWindowAnchor.LEFT).getSize();
            width -= descriptor.getToolBar(ToolWindowAnchor.RIGHT).getSize();
            return width;
        }

        protected int calcMaxHeight() {
            int height = descriptor.getManagerBounds().height;
            height -= descriptor.getToolBar(ToolWindowAnchor.TOP).getSize();
            height -= descriptor.getToolBar(ToolWindowAnchor.BOTTOM).getSize();
            return height;
        }

    }

    public class SlidingTypePropertyChangeListener implements PropertyChangeListener, Cleaner {

        public SlidingTypePropertyChangeListener() {
            descriptor.getCleaner().addBefore(SlidingContainer.this, this);
        }

        public void cleanup() {
            descriptor.getTypeDescriptor(ToolWindowType.SLIDING).removePropertyChangeListener(this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName())) {
                boolean newValue = (Boolean) evt.getNewValue();
                if (!newValue && toolWindow.getType() == ToolWindowType.SLIDING)
                    toolWindow.setType(ToolWindowType.DOCKED);
            }
        }

    }

    public class TempShowedPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible())
                update();
        }

    }

    public class ComponentResizer extends ComponentAdapter implements Cleaner {

        public ComponentResizer() {
            descriptor.getCleaner().addBefore(SlidingContainer.this, this);
        }

        public void cleanup() {
            descriptor.getManager().removeComponentListener(this);
        }

        public void componentResized(ComponentEvent e) {
            if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible())
                update();
        }
    }
}
