package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.TranslucentPanel;
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
public class LiveFloatingContainer extends SlidingContainer {
    private JLayeredPane layeredPane;

    private JPanel mainPanel;
    private TranslucentPanel sheet;

    public LiveFloatingContainer(ToolWindowDescriptor descriptor) {
        super(descriptor);

        initSlidingComponents();
        initSlidingListeners();
    }


    public void setVisible(boolean visible) {
        Component content = getContentContainer();
        sheet.remove(content);

        if (visible) {
            // Reset Layout
            titleBarButtons.configureIcons(ToolWindowType.DOCKED);

            TableLayout layout = (TableLayout) sheet.getLayout();
            layout.setColumn(0, 0);
            layout.setColumn(2, 0);
            layout.setRow(0, 0);
            layout.setRow(2, 0);

            resize();

            content.setVisible(true);
            sheet.add(content, "1,1,FULL,FULL");

            // Prepare sheet
            sheet.setBorder(BorderFactory.createEtchedBorder());

            int height = mainPanel.getHeight();
            Point point = SwingUtilities.convertPoint(mainPanel, 0, 0, layeredPane);

//            sheet.setBounds(point.x, point.y, mainPanel.getWidth(), height);
            sheet.setBounds(150,150,150,150);


            layeredPane.remove(sheet);
            layeredPane.setLayer(sheet, 1);
            layeredPane.add(sheet);
/*
            if (descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isAnimating())
                slidingAnimation.show(sheet.getBounds());
*/
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

/*
            if (descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isAnimating())
                slidingAnimation.hide(sheet.getBounds());
            else {
*/
                layeredPane.remove(sheet);
                sheet.setBorder(null);
                sheet.removeAll();
//            layeredPane.setVisible(false);
/*
            }
*/
        }
    }


    private void update() {
/*
        // Reset Layout
        titleBarButtons.configureIcons(ToolWindowType.SLIDING);

        TableLayout layout = (TableLayout) sheet.getLayout();
        layout.setColumn(0, 0);
        layout.setColumn(2, 0);
        layout.setRow(0, 0);
        layout.setRow(2, 0);

        if (barContainer != null)
            barContainer.getParent().getLayout().layoutContainer(barContainer.getParent());
        resize();

        Component content = getContentContainer();
        sheet.remove(content);
        sheet.add(content, "1,1,FULL,FULL");

        // Prepare sheet
        border.setAnchor(toolWindow.getAnchor());
        sheet.setBorder(border);

        int height = mainPanel.getHeight();
        Point point = SwingUtilities.convertPoint(mainPanel, 0, 0, layeredPane);

        sheet.setBounds(point.x, point.y, mainPanel.getWidth(), height);

        layeredPane.remove(sheet);
        layeredPane.setLayer(sheet, 1);
        layeredPane.add(sheet);
        layeredPane.validate();
*/
    }

    private void resize() {
/*
        int length = descriptor.getDividerLocation();
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
*/
    }

    private void initSlidingComponents() {
        mainPanel = new JPanel();
        sheet = new TranslucentPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
/*
        border = new SlidingBorder();
        slidingAnimation = new SlidingAnimation();
*/

        Window anchestor = descriptor.getWindowAnchestor();
        if (anchestor instanceof RootPaneContainer) {
            layeredPane = ((RootPaneContainer) anchestor).getLayeredPane();

            anchestor.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible())
                        update();
                }
            });
        } else
            throw new IllegalStateException("Can stay only on a RootPaneContainer");
    }

    private void initSlidingListeners() {
        addPropertyChangeListener("anchor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                ToolWindow evtToolWindow = ((ToolWindowDescriptor) evt.getSource()).getToolWindow();
                if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible() && !evtToolWindow.isVisible())
                    update();
            }
        });
        addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                assert "type".equals(evt.getPropertyName());
                if (evt.getNewValue() == ToolWindowType.SLIDING) {
                    if (layeredPane != null) {
/*
                        sheet.addMouseMotionListener(slidingMouseInputHandler);
                        sheet.addMouseListener(slidingMouseInputHandler);
*/
                    }
                } else {
                    if (layeredPane != null) {
/*
                        sheet.removeMouseMotionListener(slidingMouseInputHandler);
                        sheet.removeMouseListener(slidingMouseInputHandler);
*/
                    }
                }
            }
        });
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("maximized", new PropertyChangeListener() {
            private Rectangle oldBounds = null;

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

            private int calcFirstX() {
                return descriptor.getManager().getX() + ((descriptor.getToolBar(ToolWindowAnchor.LEFT).getAvailableTools() > 0) ? 23 : 0);
            }

            private int calcFirstY() {
                return descriptor.getManager().getY() + ((descriptor.getToolBar(ToolWindowAnchor.TOP).getAvailableTools() > 0) ? 23 : 0);
            }

            private int calcMaxWidth() {
                int width = descriptor.getManager().getWidth();
                if (descriptor.getToolBar(ToolWindowAnchor.LEFT).getAvailableTools() > 0)
                    width -= 23;
                if (descriptor.getToolBar(ToolWindowAnchor.RIGHT).getAvailableTools() > 0)
                    width -= 23;
                return width;
            }

            private int calcMaxHeight() {
                int width = descriptor.getManager().getHeight();
                if (descriptor.getToolBar(ToolWindowAnchor.TOP).getAvailableTools() > 0)
                    width -= 23;
                if (descriptor.getToolBar(ToolWindowAnchor.BOTTOM).getAvailableTools() > 0)
                    width -= 23;
                return width;
            }
        });
        addPropertyChangeListener("tempShowed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (toolWindow.getType() == ToolWindowType.SLIDING && toolWindow.isVisible())
                    update();
            }
        });

/*
        slidingMouseInputHandler = new SlidingMouseInputHandler(descriptor);
*/
    }


    private class SlidingAnimation extends AbstractAnimation {
        private int length;
        private Rectangle bounds;
        private int lastLen = 0;

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

    private class ActivePropertyChangeListener implements PropertyChangeListener, ActionListener {
        private TransparencyAnimation animation;
        private Timer timer;

        public ActivePropertyChangeListener() {
            this.animation = new TransparencyAnimation(sheet, sheet, 1.0f, 500f);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (descriptor.getToolWindow().getType() == ToolWindowType.FLOATING_LIVE) {
                if (Boolean.TRUE.equals(evt.getNewValue())) {
                    if (timer != null) {
                        timer.stop();
                        if (animation.isAnimating())
                            animation.stop();
                    }

                    sheet.setAlphaModeRatio(1.0f);
                } else {
                    FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
                    if (floatingLiveTypeDescriptor.isTransparentMode()) {
                        timer = new Timer(floatingLiveTypeDescriptor.getTransparentDelay(), this);
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
}