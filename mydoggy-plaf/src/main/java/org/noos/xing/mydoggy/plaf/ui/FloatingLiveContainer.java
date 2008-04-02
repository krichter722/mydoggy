package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.FloatingLiveTypeDescriptor;
import org.noos.xing.mydoggy.SlidingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.TransparencyAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
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
public class FloatingLiveContainer extends MyDoggyToolWindowContainer {
    protected JLayeredPane layeredPane;
    protected JPanel mainPanel;
    protected TranslucentPanel sheet;

    protected FloatingResizeMouseInputHandler resizeMouseInputHandler;
    protected FloatingMoveMouseInputHandler moveMouseInputHandler;

    protected boolean settedListener = false;
    protected Rectangle lastBounds;
    protected boolean valueAdjusting;


    public FloatingLiveContainer(DockedContainer dockedContainer) {
        super(dockedContainer);

        initComponents();
        initListeners();
    }


    public void cleanup() {
        // Remove Listeners
        sheet.removeMouseMotionListener(resizeMouseInputHandler);
        sheet.removeMouseListener(resizeMouseInputHandler);

        titleBarTabs.removeEventDispatcherlListener(moveMouseInputHandler);

        titleBar.removeMouseMotionListener(moveMouseInputHandler);
        titleBar.removeMouseListener(moveMouseInputHandler);

        descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).removePropertyChangeListener(this);

        // Finalize
        layeredPane = null;
        super.cleanup();
    }

    public void setVisible(boolean visible) {
        Component content = dockedContainer.getContentContainer();
        sheet.remove(content);

        if (visible) {
            descriptor.setIdOnTitleBar();
            dockedContainer.getTitleBarButtons().toolWindowTypeChanged(ToolWindowType.FLOATING_LIVE);

            // Reset Layout
            TableLayout layout = (TableLayout) sheet.getLayout();
            layout.setColumn(0, 0);
            layout.setColumn(2, 0);
            layout.setRow(0, 0);
            layout.setRow(2, 0);

            content.setVisible(true);
            sheet.add(content, "1,1,FULL,FULL");

            // Prepare sheet
            sheet.setBorder(BorderFactory.createEtchedBorder());

            if (lastBounds == null) {
                FloatingLiveTypeDescriptor typeDescriptor = (FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);

                // Set Size
                if (typeDescriptor.getSize() == null) {
                    Component managerCmp = descriptor.getManager();

                    switch (toolWindow.getAnchor()) {
                        case LEFT:
                        case RIGHT:
                            sheet.setSize(descriptor.getDockedTypeDescriptor().getDockLength(),
                                    (int) (managerCmp.getHeight() / 1.5));
                            break;
                        case TOP:
                        case BOTTOM:
                            sheet.setSize((int) (managerCmp.getWidth() / 1.5),
                                    descriptor.getDockedTypeDescriptor().getDockLength());
                            break;
                    }
                } else
                    sheet.setSize(typeDescriptor.getSize());

                SwingUtil.validateBounds(sheet, descriptor.getManager().getMainContainer().getBounds());

                // Set Location
                if (typeDescriptor.getLocation() == null ||
                        typeDescriptor.getLocation().x > descriptor.getManager().getWidth() ||
                        typeDescriptor.getLocation().y > descriptor.getManager().getHeight() ||
                        typeDescriptor.getLocation().x < 0 ||
                        typeDescriptor.getLocation().y < 0) {
                    Component managerCmp = descriptor.getManager();

                    switch (toolWindow.getAnchor()) {
                        case LEFT:
                            sheet.setLocation(50, 50);
                            break;
                        case RIGHT:
                            sheet.setLocation(managerCmp.getWidth() - 50 - sheet.getWidth(),
                                    50);
                            break;
                        case TOP:
                            sheet.setLocation(50, 50);
                            break;
                        case BOTTOM:
                            sheet.setLocation(50,
                                    managerCmp.getHeight() - 50 - sheet.getHeight());
                            break;
                    }
                } else
                    sheet.setLocation(typeDescriptor.getLocation());
            } else {
                sheet.setBounds(lastBounds);
                lastBounds = null;
            }

            layeredPane.remove(sheet);
            layeredPane.setLayer(sheet, JLayeredPane.DEFAULT_LAYER + 3);
            layeredPane.add(sheet);
/*
            if (manager.getTypeDescriptor(ToolWindowType.SLIDING).isAnimating())
                slidingAnimation.show(sheet.getBounds());
*/
        } else {
            // Set Layout
            TableLayout layout = (TableLayout) sheet.getLayout();
            layout.setColumn(0, 2);
            layout.setColumn(2, 2);
            layout.setRow(0, 2);
            layout.setRow(2, 2);

/*
            if (manager.getTypeDescriptor(ToolWindowType.SLIDING).isAnimating())
                slidingAnimation.hide(sheet.getBounds());
            else {
*/
            layeredPane.remove(sheet);
            sheet.setBorder(null);
            sheet.removeAll();
            SwingUtil.repaint(layeredPane);
/*
            }
*/
        }
    }


    protected void initComponents() {
        mainPanel = new JPanel();
        sheet = new TranslucentPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));

        layeredPane = descriptor.getManager().getRootPaneContainer().getLayeredPane();
    }

    protected void initListeners() {
        addPropertyChangeListener("type", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                assert "type".equals(evt.getPropertyName());
                if (evt.getNewValue() == ToolWindowType.FLOATING_LIVE) {
                    if (layeredPane != null) {
                        descriptor.setIdOnTitleBar();

                        // Remove listeners
                        sheet.removeMouseMotionListener(resizeMouseInputHandler);
                        sheet.removeMouseListener(resizeMouseInputHandler);

                        titleBarTabs.removeEventDispatcherlListener(moveMouseInputHandler);

                        titleBar.removeMouseMotionListener(moveMouseInputHandler);
                        titleBar.removeMouseListener(moveMouseInputHandler);

                        // Add listeners
                        sheet.addMouseMotionListener(resizeMouseInputHandler);
                        sheet.addMouseListener(resizeMouseInputHandler);

                        titleBarTabs.addEventDispatcherlListener(moveMouseInputHandler);

                        titleBar.addMouseMotionListener(moveMouseInputHandler);
                        titleBar.addMouseListener(moveMouseInputHandler);

                        settedListener = true;
                    }
                } else if (evt.getOldValue() == ToolWindowType.FLOATING_LIVE) {
                    if (layeredPane != null) {
                        if (!descriptor.getDockedTypeDescriptor().isIdVisibleOnTitleBar())
                            dockedContainer.disableIdOnTitleBar();

                        if (settedListener)
                            lastBounds = sheet.getBounds();

                        // Remove listeners
                        sheet.removeMouseMotionListener(resizeMouseInputHandler);
                        sheet.removeMouseListener(resizeMouseInputHandler);

                        titleBarTabs.removeEventDispatcherlListener(moveMouseInputHandler);

                        titleBar.removeMouseMotionListener(moveMouseInputHandler);
                        titleBar.removeMouseListener(moveMouseInputHandler);

                        settedListener = false;
                    }
                }
            }
        });
        addPropertyChangeListener("active", new ActivePropertyChangeListener());
        addPropertyChangeListener("maximized", new PropertyChangeListener() {
            protected Rectangle oldBounds = null;

            public void propertyChange(PropertyChangeEvent evt) {
                if (toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                    if ((Boolean) evt.getNewValue()) {
                        oldBounds = sheet.getBounds();

                        Rectangle bounds = descriptor.getManager().getMainContainer().getBounds();
                        bounds = SwingUtilities.convertRectangle(descriptor.getManager().getMainContainer(),
                                bounds,
                                descriptor.getManager().getRootPane().getLayeredPane());
                        sheet.setBounds(bounds);
                    } else {
                        sheet.setBounds(oldBounds);
                    }
                    SwingUtil.repaint(sheet);
                }
            }
        });
        addPropertyChangeListener("location", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE) != evt.getSource())
                    return;

                if (valueAdjusting)
                    return;

                if (sheet.isVisible()) {
                    Point location = (Point) evt.getNewValue();
                    sheet.setLocation(location);
                }
                lastBounds = null;
            }
        });
        addPropertyChangeListener("size", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE) != evt.getSource())
                    return;

                if (valueAdjusting)
                    return;

                if (sheet.isVisible()) {
                    Dimension size = (Dimension) evt.getNewValue();
                    sheet.setSize(size);
                }
                lastBounds = null;
            }
        });
        addPropertyChangeListener("enabled", new TypeEnabledPropertyChangeListener());

        sheet.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                valueAdjusting = true;
                try {
                    ((FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE)).setSize(
                            sheet.getWidth(),
                            sheet.getHeight()
                    );
                } finally {
                    valueAdjusting = false;
                }
            }

            public void componentMoved(ComponentEvent e) {
                valueAdjusting = true;
                try {
                    ((FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE)).setLocation(
                            sheet.getX(),
                            sheet.getY()
                    );
                } finally {
                    valueAdjusting = false;
                }
            }
        });

        descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).addPropertyChangeListener(this);

        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(sheet);
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(sheet);
    }


    protected class SlidingAnimation extends AbstractAnimation {
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

    protected class ActivePropertyChangeListener implements PropertyChangeListener, ActionListener {
        protected TransparencyAnimation animation;
        protected Timer timer;

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

                    layeredPane.setLayer(sheet, JLayeredPane.DEFAULT_LAYER + 4);
                    sheet.setAlphaModeRatio(1.0f);
                } else {
                    FloatingLiveTypeDescriptor floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);
                    if (floatingLiveTypeDescriptor.isTransparentMode()) {
                        timer = new Timer(floatingLiveTypeDescriptor.getTransparentDelay(), this);
                        timer.start();
                    }
                    layeredPane.setLayer(sheet, JLayeredPane.DEFAULT_LAYER + 3);
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

    protected class TypeEnabledPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            boolean newValue = (Boolean) evt.getNewValue();

            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING_LIVE)
                toolWindow.setType(ToolWindowType.DOCKED);
        }

    }
}