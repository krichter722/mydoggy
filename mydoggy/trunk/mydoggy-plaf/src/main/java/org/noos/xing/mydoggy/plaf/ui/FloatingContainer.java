package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.AnimationListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.ModalWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.util.DynamicPropertyChangeListener;
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

    protected FloatingMoveMouseInputHandler moveMouseInputHandler;
    protected WindowComponentAdapter windowComponentAdapter;

    protected boolean valueAdjusting = false;

    protected Rectangle lastBounds;

    protected final FloatingAnimation floatingAnimation;
    protected boolean assignFocusOnAnimFinished = false;


    public FloatingContainer(ToolWindowDescriptor toolWindowDescriptor) {
        super(toolWindowDescriptor);
        this.floatingAnimation = new FloatingAnimation();

        initComponents();
        initListeners();
    }


    public void cleanup() {
        // uninstall listeners
        uninstallWindowListeners();

        // Finalize
        super.cleanup();
    }


    public void setVisible(boolean visible) {
        // Stop animation
        floatingAnimation.stop();

        Container content = toolWindowPanel;

        if (visible) {
            // retrieve common panel
            window = descriptor.getModalWindow();

            // install listeners
            installWindowListeners();

            // Add content to window
            window.addDockable(toolWindow, content);
            content.setVisible(true);

            // Position window
            FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

            // Set Size
            if (typeDescriptor.getSize() == null) {
                Component parentComponent = descriptor.getManager().getWindowAncestor();
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

            // Show the window
            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isAnimating()) {
                floatingAnimation.show();
            } else {
                window.setModal(typeDescriptor.isModal());
                window.getContentPane().setVisible(true);
                
                SwingUtil.repaint(window.getWindow());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!window.isFocused() && toolWindow.isActive())
                            descriptor.assignFocus();
                    }
                });

                window.setVisible(true);
            }
        } else {
            try {
                // remove dockable...
                window.removeDockable(toolWindow);
            } finally {
                // uninstall listeners
                uninstallWindowListeners();

                // save and restore parameters...
                if (titleBarButtons.getFocusable().isFocusable())
                    titleBarButtons.getFocusable().setFocusable(false);

                lastBounds = window.getBounds();

                // Hide the window if necessary
                if (window.getNumDockables() <= 0) {
                    window.getContentPane().setVisible(true);
                    window.setVisible(false);
                    
                    descriptor.removeModalWindow();
                }

                window = null;
            }
        }
    }

    public void setVisible(ToolWindowDescriptor referenceAggregationTool,
                           Component content,
                           ToolWindowDescriptor aggregationOnTool,
                           AggregationPosition aggregationPosition) {
        // retrieve common panel
        window = ((FloatingContainer) referenceAggregationTool.getToolWindowContainer(ToolWindowType.FLOATING)).getWindow();

        // setup components
        window.addDockable(toolWindow,
                           content,
                           (aggregationOnTool != null) ? aggregationOnTool.getToolWindow() : null,
                           aggregationPosition);

        // install listeners
        installWindowListeners();

        // make it visible...
        if (!window.isVisible()) {
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
                            descriptor.assignFocus();
                    }
                });
            }
        }
    }

    public ModalWindow getWindow() {
        return window;
    }

    public boolean isAnimating() {
        return floatingAnimation.isAnimating();
    }


    protected void initComponents() {
    }

    protected void initListeners() {
        // Init tool window properties listeners
        PropertyChangeListener propertyChangeListener = new PropertyListener();

        PropertyChangeEventSource toolWindowSource = descriptor.getToolWindow();
        toolWindowSource.addPlafPropertyChangeListener(propertyChangeListener, "type", "maximized");

        // Init floating type descriptor properties listeners
        PropertyChangeEventSource floatingTypeDescriptorSource = (PropertyChangeEventSource) descriptor.getToolWindow().getTypeDescriptor(FloatingTypeDescriptor.class);
        floatingTypeDescriptorSource.addPlafPropertyChangeListener(propertyChangeListener);

        // Animation listener
        floatingAnimation.addAnimationListener(new AnimationListener() {
            public void onFinished() {
                if (assignFocusOnAnimFinished) {
                    descriptor.assignFocus();
                    assignFocusOnAnimFinished = false;
                }
            }
        });

        // Init window listeners
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(null);
        windowComponentAdapter = new WindowComponentAdapter();
    }

    protected void reinitWindow(ModalWindow oldWindow) {
        // Init new window
        window = descriptor.getModalWindow();
        window.importFrom(oldWindow);
        installWindowListeners();
    }

    protected void installWindowListeners() {
        moveMouseInputHandler.setFloatingContainer(window.getWindow());

        // Add listeners
        toolWindowTabPanel.addEventDispatcherlListener(moveMouseInputHandler);

        toolWindowTitleBar.addMouseMotionListener(moveMouseInputHandler);
        toolWindowTitleBar.addMouseListener(moveMouseInputHandler);

        window.getWindow().addComponentListener(windowComponentAdapter);
    }

    protected void uninstallWindowListeners() {
        moveMouseInputHandler.setFloatingContainer(null);

        // Remove listeners
        if (window != null) {
            window.getWindow().removeComponentListener(windowComponentAdapter);
        }

        toolWindowTabPanel.removeEventDispatcherlListener(moveMouseInputHandler);

        toolWindowTitleBar.removeMouseMotionListener(moveMouseInputHandler);
        toolWindowTitleBar.removeMouseListener(moveMouseInputHandler);
    }


    public class FloatingAnimation extends AbstractAnimation {
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
                        descriptor.assignFocus();
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

    public class WindowComponentAdapter extends ComponentAdapter {

        public WindowComponentAdapter() {
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

    public class PropertyListener extends DynamicPropertyChangeListener {

/*
        public void onActive(PropertyChangeEvent evt) {
            if (toolWindow.getType() == ToolWindowType.FLOATING ||
                toolWindow.getType() == ToolWindowType.FLOATING_FREE) {

                if (Boolean.TRUE.equals(evt.getNewValue())) {
                    synchronized (floatingAnimation) {
                        if (floatingAnimation.isAnimating()) {
                            assignFocusOnAnimFinished = true;
                        } else
                            descriptor.assignFocus();
                    }
                }
            }
*/

        public void onMaximized(PropertyChangeEvent evt) {
            if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                if ((Boolean) evt.getNewValue())
                    SwingUtil.setFullScreen(window.getWindow());
                else
                    SwingUtil.restoreFullScreenWindow(window.getWindow());
                SwingUtil.repaint(window.getWindow());
            }

        }

        public void onEnabled(PropertyChangeEvent evt) {
            boolean newValue = (Boolean) evt.getNewValue();

            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING)
                toolWindow.setType(ToolWindowType.DOCKED);
        }

        public void onSize(PropertyChangeEvent evt) {
            if (window == null)
                return;

            if (valueAdjusting)
                return;

            if (window.isVisible()) {
                Dimension size = (Dimension) evt.getNewValue();
                window.setSize(size);
            }
            lastBounds = null;
        }

        public void onLocation(PropertyChangeEvent evt) {
            if (window == null)
                return;

            if (valueAdjusting)
                return;

            if (window.isVisible()) {
                Point location = (Point) evt.getNewValue();
                window.setLocation(location);
            }
            lastBounds = null;
        }

        public void onModal(PropertyChangeEvent evt) {
            if (window == null)
                return;

            if (window.isVisible()) {
                window.dispose();
                window.setModal((Boolean) evt.getNewValue());
                window.setVisible(true);
            }
        }

        public void onAddToTaskBar(PropertyChangeEvent evt) {
            if (window == null)
                return;

            if (window.isVisible()) {
                ModalWindow oldWindow = window;

                // Clean
                uninstallWindowListeners();
                descriptor.removeModalWindow();

                Component focusOwner = oldWindow.getWindow().getFocusOwner();
                oldWindow.setVisible(false);

                // Reinit window
                reinitWindow(oldWindow);

                // Dispose old
                oldWindow.getWindow().dispose();

                // Show new
                window.setVisible(true);

                if (focusOwner != null)
                    SwingUtil.requestFocus(focusOwner);
            } else {
                ModalWindow oldWindow = window;

                // Clean old window
                uninstallWindowListeners();
                descriptor.removeModalWindow();

                // Finalize clean
                oldWindow.getWindow().dispose();
            }
        }

        public void onOsDecorated(PropertyChangeEvent evt) {
            window.dispose();
            window.setUndecorated(! (Boolean) evt.getNewValue());
            window.setVisible(true);
        }

        public void onAlwaysOnTop(PropertyChangeEvent evt) {
            if (window == null)
                return;

            if (window.isVisible()) {
                ModalWindow oldWindow = window;

                // Clean
                uninstallWindowListeners();
                descriptor.removeModalWindow();

                Component focusOwner = oldWindow.getWindow().getFocusOwner();
                oldWindow.setVisible(false);

                // Reinit window
                reinitWindow(oldWindow);

                // Dispose old
                oldWindow.getWindow().dispose();

                // Show new
                window.setVisible(true);

                if (focusOwner != null)
                    SwingUtil.requestFocus(focusOwner);
            } else {
                ModalWindow oldWindow = window;

                // Clean old window
                uninstallWindowListeners();
                descriptor.removeModalWindow();

                // Finalize clean
                oldWindow.getWindow().dispose();
            }
        }
    }

}
