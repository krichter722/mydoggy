package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.AnimationListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.JModalFrame;
import org.noos.xing.mydoggy.plaf.ui.cmp.JModalWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.ModalWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingToolTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class FloatingContainer extends MyDoggyToolWindowContainer {
    protected ModalWindow window;

    protected FloatingResizeMouseInputHandler resizeMouseInputHandler;
    protected FloatingMoveMouseInputHandler moveMouseInputHandler;
    protected WindowComponentAdapter windowComponentAdapter;

    protected boolean settedListener = false;
    protected boolean valueAdjusting = false;

    protected Rectangle lastBounds;

    protected final FloatingAnimation floatingAnimation;
    protected boolean assignFocusOnAnimFinished = false;


    public FloatingContainer(DockedContainer dockedContainer) {
        super(dockedContainer);
        this.floatingAnimation = new FloatingAnimation();

        initComponents();
        initListeners();
    }


    public void cleanup() {
        // Remove listeners
        if (window != null) {
            window.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
            window.getWindow().removeMouseListener(resizeMouseInputHandler);
            window.getWindow().dispose();
        }

        toolWindowTabContainer.removeEventDispatcherlListener(moveMouseInputHandler);

        titleBar.removeMouseMotionListener(moveMouseInputHandler);
        titleBar.removeMouseListener(moveMouseInputHandler);

        descriptor.getTypeDescriptor(ToolWindowType.FLOATING).removePropertyChangeListener(this);

        // Finalize
        super.cleanup();
    }

    public void setVisible(boolean visible) {
        initWindow();
        
        // Stop animation
        floatingAnimation.stop();

        Container content = dockedContainer.getContentContainer();
        content.setFocusCycleRoot(!visible);

        if (visible) {
            // Setup toolwindow title bar components
            descriptor.setIdOnTitleBar();
            titleBarButtons.setType(ToolWindowType.FLOATING);

            // Add content to window
            window.getContentPane().removeAll();
            window.getContentPane().add(content, "1,1,FULL,FULL");
            content.setVisible(true);

            // Position window
            if (lastBounds == null) {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

                // Set Size
                if (typeDescriptor.getSize() == null) {
                    Component parentComponent = descriptor.getManager().getWindowAnchestor();
                    window.setSize(parentComponent.getWidth() / 2, (int) (parentComponent.getHeight() / 1.5));
                } else {
                    window.setSize(typeDescriptor.getSize());
                }

                // Set Location
                if (typeDescriptor.getLocation() == null) {
                    SwingUtil.centrePositionOnScreen(window.getWindow());
                } else
                    window.setLocation(typeDescriptor.getLocation());

                SwingUtil.validateBounds(window.getWindow());
            } else {
                window.setBounds(lastBounds);
                lastBounds = null;
            }

            // Show the window
            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isAnimating()) {
                floatingAnimation.show();
            } else {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                window.setModal(typeDescriptor.isModal());
                window.setVisible(true);
                window.getContentPane().setVisible(true);
                SwingUtil.repaint(window.getWindow());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!window.isFocused() && toolWindow.isActive())
                            dockedContainer.assignFocus();
                    }
                });
            }
        } else {
            if (titleBarButtons.getFocusable().isFocusable())
                titleBarButtons.getFocusable().setFocusable(false);

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


    protected void initComponents() {
    }

    protected void initListeners() {
        addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor || window == null)
                    return;

                if (evt.getNewValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING_FREE) {
                    initWindowListeners();
                } else {
                    if (!descriptor.getDockedTypeDescriptor().isIdVisibleOnTitleBar())
                        dockedContainer.disableIdOnTitleBar();

                    if (settedListener)
                        lastBounds = window.getBounds();

                    // Remove listeners
                    window.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
                    window.getWindow().removeMouseListener(resizeMouseInputHandler);

                    toolWindowTabContainer.removeEventDispatcherlListener(moveMouseInputHandler);

                    titleBar.removeMouseMotionListener(moveMouseInputHandler);
                    titleBar.removeMouseListener(moveMouseInputHandler);

                    settedListener = false;
                }
            }
        });
        addPropertyChangeListener("location", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING) != evt.getSource() || window == null)
                    return;

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
                if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING) != evt.getSource()  || window == null)
                    return;

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
                if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING) != evt.getSource()  || window == null)
                    return;

                if (window.isVisible()) {
                    window.setModal((Boolean) evt.getNewValue());
                }
            }
        });
        addPropertyChangeListener("maximized", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    if ((Boolean) evt.getNewValue())
                        SwingUtil.setFullScreen(window.getWindow());
                    else
                        SwingUtil.restoreFullScreenWindow(window.getWindow());
                    SwingUtil.repaint(window.getWindow());
                }

            }
        });
        addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    if (Boolean.TRUE.equals(evt.getNewValue())) {
                        synchronized (floatingAnimation) {
                            if (floatingAnimation.isAnimating()) {
                                assignFocusOnAnimFinished = true;
                            } else
                                dockedContainer.assignFocus();
                        }
                    }
                }
            }
        });
        addPropertyChangeListener("addToTaskBar", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if (window.isVisible()) {
                    ModalWindow oldWindow = window;

                    // Clean
                    Component focusOwner = oldWindow.getWindow().getFocusOwner();
                    oldWindow.setVisible(false);
                    oldWindow.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
                    oldWindow.getWindow().removeMouseListener(resizeMouseInputHandler);
                    oldWindow.getWindow().removeComponentListener(windowComponentAdapter);

                    // Reinit window
                    reinitWindow(evt, oldWindow);

                    // Dispose old
                    oldWindow.getWindow().dispose();

                    // Show new
                    window.setVisible(true);

                    if (focusOwner != null)
                        SwingUtil.requestFocus(focusOwner);
                } else {
                    ModalWindow oldWindow = window;

                    // Clean old window
                    oldWindow.getWindow().removeComponentListener(windowComponentAdapter);
                    oldWindow.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
                    oldWindow.getWindow().removeMouseListener(resizeMouseInputHandler);

                    // Prepare for new
                    reinitWindow(evt, oldWindow);
                    
                    // Finalize clean
                    oldWindow.getWindow().dispose();
                }
            }
        });
        addPropertyChangeListener("enabled", new TypeEnabledPropertyChangeListener());

        descriptor.getTypeDescriptor(ToolWindowType.FLOATING).addPropertyChangeListener(this);
        floatingAnimation.addAnimationListener(new AnimationListener() {
            public void onFinished() {
                if (assignFocusOnAnimFinished) {
                    dockedContainer.assignFocus();
                    assignFocusOnAnimFinished = false;
                }
            }
        });
    }


    protected void initWindow() {
        if (window != null)
            return;

        if (toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).isAddToTaskBar()) {
            window = new JModalFrame(toolWindow,
                                     SwingUtil.getBoolean("dialog.owner.enabled", true) ? descriptor.getWindowAnchestor() : null,
                                     null,
                                     false);
        } else {
            window = new JModalWindow(
                    SwingUtil.getBoolean("dialog.owner.enabled", true) ? descriptor.getWindowAnchestor() : null,
                                      null,
                                      false);
        }
        window.setName("toolWindow.floating.window." + toolWindow.getId());

        JPanel contentPane = new JPanel(new ExtendedTableLayout(new double[][]{{1, TableLayout.FILL, 1}, {1, TableLayout.FILL, 1}}));
        contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        window.setContentPane(contentPane);

        new FloatingToolTransparencyListener(this, descriptor, window.getWindow());
        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(window.getWindow());
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(window.getWindow());

        window.getWindow().addComponentListener(windowComponentAdapter = new WindowComponentAdapter());

        if (!settedListener)
            initWindowListeners();
    }

    protected void reinitWindow(PropertyChangeEvent evt, ModalWindow oldWindow) {
        // Init new window
        if ((Boolean) evt.getNewValue()) {
            window = new JModalFrame(toolWindow,
                                     SwingUtil.getBoolean("dialog.owner.enabled", true) ? descriptor.getWindowAnchestor() : null,
                                     null,
                                     false);
        } else {
            window = new JModalWindow(
                    SwingUtil.getBoolean("dialog.owner.enabled", true) ? descriptor.getWindowAnchestor() : null,
                                      null,
                                      false);
        }
        window.setBounds(oldWindow.getBounds());
        window.setName("toolWindow.floating.window." + toolWindow.getId());
        window.setContentPane(oldWindow.getContentPane());
        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(window.getWindow());
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(window.getWindow());
        window.getWindow().addMouseMotionListener(resizeMouseInputHandler);
        window.getWindow().addMouseListener(resizeMouseInputHandler);
        window.getWindow().addComponentListener(windowComponentAdapter);
    }

    protected void initWindowListeners() {
        descriptor.setIdOnTitleBar();

        // Remove listeners
        window.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
        window.getWindow().removeMouseListener(resizeMouseInputHandler);

        toolWindowTabContainer.removeEventDispatcherlListener(moveMouseInputHandler);

        titleBar.removeMouseMotionListener(moveMouseInputHandler);
        titleBar.removeMouseListener(moveMouseInputHandler);

        // Add listeners
        window.getWindow().addMouseMotionListener(resizeMouseInputHandler);
        window.getWindow().addMouseListener(resizeMouseInputHandler);

        toolWindowTabContainer.addEventDispatcherlListener(moveMouseInputHandler);

        titleBar.addMouseMotionListener(moveMouseInputHandler);
        titleBar.addMouseListener(moveMouseInputHandler);

        settedListener = true;
    }

    protected class FloatingAnimation extends AbstractAnimation {
        protected Rectangle originalBounds;
        protected int lastLenX = 0;
        protected int lastLenY = 0;

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

                    SwingUtil.repaint(window.getWindow());

                    if (!window.isFocused() && toolWindow.isActive())
                        dockedContainer.assignFocus();
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

    protected class TypeEnabledPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            boolean newValue = (Boolean) evt.getNewValue();

            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING)
                toolWindow.setType(ToolWindowType.DOCKED);
        }

    }

    protected class WindowComponentAdapter extends ComponentAdapter implements Cleaner {

        public WindowComponentAdapter() {
            descriptor.getCleaner().addCleaner(this);
        }

        public void cleanup() {
            window.getWindow().removeComponentListener(this);
        }

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
    }
}
