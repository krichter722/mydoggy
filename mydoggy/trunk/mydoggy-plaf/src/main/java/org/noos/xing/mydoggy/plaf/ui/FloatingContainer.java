package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.animation.AnimationListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.ModalWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
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
//            if (lastBounds == null) {
            // TODO: validate this procedure....expacially for when x + width excede the total width..
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
//            } else {
//                window.setBounds(lastBounds);
//                lastBounds = null;
//            }

            // Show the window
            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isAnimating()) {
                floatingAnimation.show();
            } else {
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
        PropertyChangeEventSource toolWindowSource = descriptor.getToolWindow();
        toolWindowSource.addPlafPropertyChangeListener("type", new TypePropertyChangeListener());
        toolWindowSource.addPlafPropertyChangeListener("maximized", new PropertyChangeListener() {
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
/*
        toolWindowSource.addPlafPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
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
            }
        });

*/
        // Init floating type descriptor properties listeners
        PropertyChangeEventSource floatingTypeDescriptorSource = (PropertyChangeEventSource) descriptor.getToolWindow().getTypeDescriptor(FloatingTypeDescriptor.class);
        floatingTypeDescriptorSource.addPlafPropertyChangeListener("location", new LocationPropertyChangeListener());
        floatingTypeDescriptorSource.addPlafPropertyChangeListener("size", new SizePropertyChangeListener());
        floatingTypeDescriptorSource.addPlafPropertyChangeListener("modal", new ModalPropertyChangeListener());
        floatingTypeDescriptorSource.addPlafPropertyChangeListener("enabled", new TypeEnabledPropertyChangeListener());
        floatingTypeDescriptorSource.addPlafPropertyChangeListener("addToTaskBar", new AddToTaskBarPropertyChangeListener());

        floatingAnimation.addAnimationListener(new AnimationListener() {
            public void onFinished() {
                if (assignFocusOnAnimFinished) {
                    descriptor.assignFocus();
                    assignFocusOnAnimFinished = false;
                }
            }
        });

        // Init window listeners
        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(null);
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(null);
        windowComponentAdapter = new WindowComponentAdapter();
    }


    protected void reinitWindow(PropertyChangeEvent evt, ModalWindow oldWindow) {
        // Init new window
/* TODO: remove this method
        if ((Boolean) evt.getNewValue())
            window = new ModalFrame(toolWindow, descriptor.getAncestorForWindow(), null, false);
        else
            window = new ModalDialog(descriptor.getManager(), descriptor.getAncestorForWindow(), null, false);

        window.setName("toolWindow.floating.window." + toolWindow.getId());
        window.setBounds(oldWindow.getBounds());
        window.setContentPane(oldWindow.getContentPane());

        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(window.getWindow());
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(window.getWindow());
        window.getWindow().addMouseMotionListener(resizeMouseInputHandler);
        window.getWindow().addMouseListener(resizeMouseInputHandler);
        window.getWindow().addComponentListener(windowComponentAdapter);
*/
    }


    protected void installWindowListeners() {
        resizeMouseInputHandler.setFloatingContainer(window.getWindow());
        moveMouseInputHandler.setFloatingContainer(window.getWindow());

        // Add listeners
        window.getWindow().addMouseMotionListener(resizeMouseInputHandler);
        window.getWindow().addMouseListener(resizeMouseInputHandler);

        toolWindowTabPanel.addEventDispatcherlListener(moveMouseInputHandler);

        toolWindowTitleBar.addMouseMotionListener(moveMouseInputHandler);
        toolWindowTitleBar.addMouseListener(moveMouseInputHandler);

        window.getWindow().addComponentListener(windowComponentAdapter);
    }

    protected void uninstallWindowListeners() {
        resizeMouseInputHandler.setFloatingContainer(null);
        moveMouseInputHandler.setFloatingContainer(null);

        // Remove listeners
        if (window != null) {
            window.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
            window.getWindow().removeMouseListener(resizeMouseInputHandler);
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


    public class TypeEnabledPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            boolean newValue = (Boolean) evt.getNewValue();

            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING)
                toolWindow.setType(ToolWindowType.DOCKED);
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

    public class TypePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            // TODO: reimplement this...
            if (evt.getSource() != descriptor || window == null)
                return;

            if (evt.getNewValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING_FREE) {
//                initWindowListeners();
            } else {
//                if (settedListener)
//                    lastBounds = window.getBounds();

                // Remove listeners
                window.getWindow().removeMouseMotionListener(resizeMouseInputHandler);
                window.getWindow().removeMouseListener(resizeMouseInputHandler);

                toolWindowTabPanel.removeEventDispatcherlListener(moveMouseInputHandler);

                toolWindowTitleBar.removeMouseMotionListener(moveMouseInputHandler);
                toolWindowTitleBar.removeMouseListener(moveMouseInputHandler);

//                settedListener = false;
            }
        }
    }

    public class LocationPropertyChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
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
    }

    public class SizePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
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

    }

    public class ModalPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (window == null)
                return;

            if (window.isVisible())
                window.setModal((Boolean) evt.getNewValue());
        }

    }

    public class AddToTaskBarPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            if (window == null)
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

    }

}
