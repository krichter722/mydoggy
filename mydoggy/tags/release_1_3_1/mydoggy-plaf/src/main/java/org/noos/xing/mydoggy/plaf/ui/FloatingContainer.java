package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.AnimationListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.JModalWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingToolTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class FloatingContainer extends DockedContainer {
    private JModalWindow window;

    private FloatingResizeMouseInputHandler resizeMouseInputHandler;
    private FloatingMoveMouseInputHandler moveMouseInputHandler;
    private boolean settedListener = false;

    private boolean valueAdjusting = false;

    private Rectangle lastBounds;

    private final FloatingAnimation floatingAnimation = new FloatingAnimation();

    public FloatingContainer(ToolWindowDescriptor descriptor) {
        super(descriptor);

        initFloatingComponents();
        initFloatingListeners();
    }


    public void setVisible(boolean visible) {
        synchronized (floatingAnimation) {
            if (floatingAnimation.isAnimating()) {
                floatingAnimation.stop();
            }
        }

        Container content = getContentContainer();
        content.setFocusCycleRoot(!visible);

        if (visible) {
            configureFloatingIcons();

            window.getContentPane().removeAll();

            content.setVisible(true);
            window.getContentPane().add(content, "1,1,FULL,FULL");

            if (lastBounds == null) {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

                // Set Size
                if (typeDescriptor.getSize() == null) {
                    Window windowAnchestor = descriptor.getWindowAnchestor();
                    window.setSize(windowAnchestor.getWidth() / 2, (int) (windowAnchestor.getHeight() / 1.5));
                } else {
                    window.setSize(typeDescriptor.getSize());
                }

                // Set Location
                if (typeDescriptor.getLocation() == null) {
                    if (content.getX() == 0 || content.getY() == 0)
                        SwingUtil.centrePositionOnScreen(window);
                } else
                    window.setLocation(typeDescriptor.getLocation());
            } else {
                window.setBounds(lastBounds);
                lastBounds = null;
            }

            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isAnimating()) {
                floatingAnimation.show();
            } else {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                window.setModal(typeDescriptor.isModal());
                window.setVisible(true);
                window.getContentPane().setVisible(true);
                SwingUtil.repaint(window);

                if (!window.isFocused() && toolWindow.isActive()) 
                    assignFocus();
            }
        } else {
            if (hideButton.isFocusable())
                hideButton.setFocusable(false);

            lastBounds = window.getBounds();

            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isAnimating())
                floatingAnimation.hide();
             else {
                window.getContentPane().setVisible(true);
                window.setVisible(false);
            }
        }
    }

    public boolean isAnimating() {
        return floatingAnimation.isAnimating();
    }


    private void initFloatingComponents() {
        window = new JModalWindow(getResourceManager(),
                                  descriptor.getWindowAnchestor(),
                                  null,
                                  false);
        window.setName("toolWindow.floating.window." + toolWindow.getId());

        JPanel contentPane = new JPanel(new ExtendedTableLayout(new double[][]{{1, TableLayout.FILL, 1}, {1, TableLayout.FILL, 1}}));
        contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        window.setContentPane(contentPane);
    }

    private void initFloatingListeners() {
        addPropertyChangeListener("type", new PropertyChangeListener() {
            ContainerListener listener = new ContainerListener() {
                public void componentAdded(ContainerEvent e) {
                    e.getChild().addMouseMotionListener(resizeMouseInputHandler);
                    e.getChild().addMouseListener(resizeMouseInputHandler);
                }

                public void componentRemoved(ContainerEvent e) {
                    e.getChild().removeMouseMotionListener(resizeMouseInputHandler);
                    e.getChild().removeMouseListener(resizeMouseInputHandler);
                }
            };

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                assert "type".equals(evt.getPropertyName());
                if (evt.getNewValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING_FREE) {
                    enableIdOnTitleBar();
                    
                    // Remove
                    window.removeMouseMotionListener(resizeMouseInputHandler);
                    window.removeMouseListener(resizeMouseInputHandler);

                    titleBarTabs.getViewport().removeMouseMotionListener(moveMouseInputHandler);
                    titleBarTabs.getViewport().removeMouseListener(moveMouseInputHandler);
                    for (Component cmp : titleBarTabs.getTabContainer().getComponents()) {
                        cmp.removeMouseMotionListener(moveMouseInputHandler);
                        cmp.removeMouseListener(moveMouseInputHandler);
                    }
                    titleBarTabs.getTabContainer().removeContainerListener(listener);

                    titleBar.removeMouseMotionListener(moveMouseInputHandler);
                    titleBar.removeMouseListener(moveMouseInputHandler);

                    // Add
                    window.addMouseMotionListener(resizeMouseInputHandler);
                    window.addMouseListener(resizeMouseInputHandler);

                    titleBarTabs.getViewport().addMouseMotionListener(moveMouseInputHandler);
                    titleBarTabs.getViewport().addMouseListener(moveMouseInputHandler);
                    for (Component cmp : titleBarTabs.getTabContainer().getComponents()) {
                        cmp.addMouseMotionListener(moveMouseInputHandler);
                        cmp.addMouseListener(moveMouseInputHandler);
                    }
                    titleBarTabs.getTabContainer().addContainerListener(listener);

                    titleBar.addMouseMotionListener(moveMouseInputHandler);
                    titleBar.addMouseListener(moveMouseInputHandler);

                    settedListener = true;
                } else {
                    if (!descriptor.getDockedTypeDescriptor().isIdVisibleOnTitleBar())
                        disableIdOnTitleBar();
                                        
                    if (settedListener)
                        lastBounds = window.getBounds();

                    // Remove listeners
                    window.removeMouseMotionListener(resizeMouseInputHandler);
                    window.removeMouseListener(resizeMouseInputHandler);

                    titleBarTabs.getViewport().removeMouseMotionListener(moveMouseInputHandler);
                    titleBarTabs.getViewport().removeMouseListener(moveMouseInputHandler);
                    for (Component cmp : titleBarTabs.getTabContainer().getComponents()) {
                        cmp.removeMouseMotionListener(moveMouseInputHandler);
                        cmp.removeMouseListener(moveMouseInputHandler);
                    }
                    titleBarTabs.getTabContainer().removeContainerListener(listener);

                    titleBar.removeMouseMotionListener(moveMouseInputHandler);
                    titleBar.removeMouseListener(moveMouseInputHandler);

                    settedListener = false;
                }
            }
        });
        addPropertyChangeListener("location", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (valueAdjusting)
                    return;

                if (window.isVisible()) {
                    Point location = (Point) evt.getNewValue();
                    window.setLocation(location);
                }
                lastBounds = null;
            }
        });
        addPropertyChangeListener("size", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (valueAdjusting)
                    return;

                if (window.isVisible()) {
                    Dimension size = (Dimension) evt.getNewValue();
                    window.setSize(size);
                }
                lastBounds = null;
            }
        });
        addPropertyChangeListener("modal", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (window.isVisible()) {
                    window.setModal((Boolean) evt.getNewValue());
                }
            }
        });
        addPropertyChangeListener("maximized", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    if ((Boolean)evt.getNewValue())
                        SwingUtil.setFullScreen(window);
                     else
                        SwingUtil.restoreFullScreenWindow(window);
                }

            }
        });
        addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    if (Boolean.TRUE.equals(evt.getNewValue())) {
                        synchronized (floatingAnimation) {
                            if (floatingAnimation.isAnimating()) {
                                lister = true;
                            } else
                                assignFocus();
                        }
                    }
                }
            }
        });

        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
        typeDescriptor.addPropertyChangeListener(this);

        new FloatingToolTransparencyListener(this, descriptor, window);
        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(window);
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(window);

        window.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                valueAdjusting = true;
                try {
                    ((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).setSize(
                            window.getWidth(),
                            window.getHeight()
                    );
                } finally {
                    valueAdjusting = false;
                }
            }

            public void componentMoved(ComponentEvent e) {
                valueAdjusting = true;
                try {
                    ((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).setLocation(
                            window.getX(),
                            window.getY()
                    );
                } finally {
                    valueAdjusting = false;
                }
            }
        });

        floatingAnimation.addAnimationListener(new AnimationListener() {
            public void onFinished() {
                if (lister) {
                    assignFocus();
                    lister = false;
                }
            }
        });
    }

    boolean lister = false;

    private void configureFloatingIcons() {
        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

        if (typeDescriptor.isModal()) {
            setPinVisible(false);
            setFloatingVisible(false);
            setDockedVisible(false);
        } else {
            setPinVisible(true);
            setFloatingVisible(true);
            setDockedVisible(false);
            setFix();
        }
    }


    public class FloatingAnimation extends AbstractAnimation {
        private Rectangle originalBounds;
        private int lastLenX = 0;
        private int lastLenY = 0;

        public FloatingAnimation() {
            super(80f);
        }

        protected float onAnimating(float animationPercent) {
            final int animatingLengthX = (int) (animationPercent * originalBounds.width);
            final int animatingLengthY = (int) (animationPercent * originalBounds.height);

            if (getAnimationDirection() == Direction.INCOMING) {
                window.setBounds(
                        window.getX() - (animatingLengthX / 2 - lastLenX / 2),
                        window.getY() - (animatingLengthY / 2 - lastLenY / 2),
                        window.getWidth() + (animatingLengthX - lastLenX),
                        window.getHeight() + (animatingLengthY - lastLenY));
            } else {
                window.setBounds(window.getX() + (animatingLengthX / 2 - lastLenX / 2),
                                 window.getY() + (animatingLengthY / 2 - lastLenY / 2),
                                 window.getWidth() - (animatingLengthX - lastLenX),
                                 window.getHeight() - (animatingLengthY - lastLenY)
                );
            }

            lastLenX = animatingLengthX;
            lastLenY = animatingLengthY;

            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    window.getContentPane().setVisible(true);
                    window.setBounds(originalBounds);

                    SwingUtil.repaint(window);

                    if (!window.isFocused() && toolWindow.isActive())
                        assignFocus();
                    break;
                case OUTGOING:
                    window.getContentPane().setVisible(true);
                    window.setVisible(false);
                    window.setBounds(originalBounds);
                    break;
            }
        }

        protected void onHide(Object... params) {
            this.originalBounds = window.getBounds();
            window.getContentPane().setVisible(false);
        }

        protected void onShow(Object... params) {
            this.originalBounds = window.getBounds();
//            window.getContentPane().setVisible(false);
            window.setBounds(new Rectangle(originalBounds.x + (originalBounds.width / 2),
                                           originalBounds.y + (originalBounds.height / 2),
                                           0, 0));

            FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
            window.setModal(typeDescriptor.isModal());
            window.setVisible(true);
        }

        protected void onStartAnimation(Direction direction) {
            lastLenX = 0;
            lastLenY = 0;
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }
    }

}
