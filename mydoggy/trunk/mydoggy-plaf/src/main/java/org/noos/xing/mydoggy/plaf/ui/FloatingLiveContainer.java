package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.FloatingLiveTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.PropertyChangeEventSource;
import org.noos.xing.mydoggy.plaf.ui.cmp.FloatingLiveWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.util.DynamicPropertyChangeListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class FloatingLiveContainer extends MyDoggyToolWindowContainer {
    protected FloatingLiveWindow floatingLiveWindow;
    protected FloatingMoveMouseInputHandler moveMouseInputHandler;
    protected ComponentListener livePanelComponentListener;

    protected boolean settedListener = false;
    protected Rectangle lastBounds;
    protected boolean valueAdjusting;


    public FloatingLiveContainer(ToolWindowDescriptor toolWindowDescriptor) {
        super(toolWindowDescriptor);

        initComponents();
        initListeners();
    }


    public void cleanup() {
        // Remove Listeners
        toolWindowTabPanel.removeEventDispatcherlListener(moveMouseInputHandler);

        toolWindowTitleBar.removeMouseMotionListener(moveMouseInputHandler);
        toolWindowTitleBar.removeMouseListener(moveMouseInputHandler);

        floatingLiveWindow = null;
        
        super.cleanup();
    }


    public void setVisible(boolean visible) {
        Component content = toolWindowPanel;

        if (visible) {
            // retrieve common panel
            floatingLiveWindow = descriptor.getFloatingLivePanel();
            JComponent floatingLiveComponent = (JComponent) floatingLiveWindow;

            // setup components
            floatingLiveWindow.resetLayout();
            content.setVisible(true);
            floatingLiveWindow.addDockable(toolWindow, content);

            // setup listener
            moveMouseInputHandler.setFloatingContainer(floatingLiveComponent);
            floatingLiveComponent.addComponentListener(livePanelComponentListener);

            // Prepare common panel
            floatingLiveComponent.setBorder(BorderFactory.createEtchedBorder());

            if (lastBounds == null) {
                FloatingLiveTypeDescriptor typeDescriptor = (FloatingLiveTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE);

                // Set Size
                if (typeDescriptor.getSize() == null) {
                    Component managerCmp = descriptor.getManager();

                    switch (toolWindow.getAnchor()) {
                        case LEFT:
                        case RIGHT:
                            floatingLiveWindow.setSize(descriptor.getDockedTypeDescriptor().getDockLength(),
                                                      (int) (managerCmp.getHeight() / 1.5));
                            break;
                        case TOP:
                        case BOTTOM:
                            floatingLiveWindow.setSize((int) (managerCmp.getWidth() / 1.5),
                                                      descriptor.getDockedTypeDescriptor().getDockLength());
                            break;
                    }
                } else
                    floatingLiveWindow.setSize(typeDescriptor.getSize());

                SwingUtil.validateBounds(floatingLiveComponent, descriptor.getManager().getMainContainer().getBounds());

                // Set Location
                if (typeDescriptor.getLocation() == null ||
                    typeDescriptor.getLocation().x > descriptor.getManager().getWidth() ||
                    typeDescriptor.getLocation().y > descriptor.getManager().getHeight() ||
                    typeDescriptor.getLocation().x < 0 ||
                    typeDescriptor.getLocation().y < 0) {
                    Component managerCmp = descriptor.getManager();

                    switch (toolWindow.getAnchor()) {
                        case LEFT:
                            floatingLiveWindow.setLocation(50, 50);
                            break;
                        case RIGHT:
                            floatingLiveWindow.setLocation(managerCmp.getWidth() - 50 - floatingLiveWindow.getWidth(),
                                                          50);
                            break;
                        case TOP:
                            floatingLiveWindow.setLocation(50, 50);
                            break;
                        case BOTTOM:
                            floatingLiveWindow.setLocation(50,
                                                          managerCmp.getHeight() - 50 - floatingLiveWindow.getHeight());
                            break;
                    }
                } else
                    floatingLiveWindow.setLocation(typeDescriptor.getLocation());
            } else {
                floatingLiveWindow.setBounds(lastBounds);
                lastBounds = null;
            }

            // mount common panel
            floatingLiveWindow.mount();
        } else {
            // remove dockable...
            floatingLiveWindow.removeDockable(toolWindow);

            // remove listeners...
            JComponent floatingLiveComponent = (JComponent) floatingLiveWindow;
            floatingLiveComponent.removeComponentListener(livePanelComponentListener);

            // unmount
            floatingLiveWindow.unmount();
            descriptor.removeFloatingLivePanel();
            
            floatingLiveWindow = null;
        }
    }

    public void setVisible(ToolWindowDescriptor referenceAggregationTool,
                           Component content,
                           ToolWindowDescriptor aggregationOnTool, 
                           AggregationPosition aggregationPosition) {
        // retrieve common panel
        floatingLiveWindow = ((FloatingLiveContainer) referenceAggregationTool.getToolWindowContainer(ToolWindowType.FLOATING_LIVE)).getFloatingLiveWindow();

        // setup components
        floatingLiveWindow.addDockable(toolWindow,
                                      content,
                                      (aggregationOnTool != null) ? aggregationOnTool.getToolWindow() : null,
                                      aggregationPosition);

        // setup listeners
        JComponent floatingLiveComponent = (JComponent) floatingLiveWindow;

        moveMouseInputHandler.setFloatingContainer(floatingLiveComponent);
        floatingLiveComponent.addComponentListener(livePanelComponentListener);

        // Mount panel
        floatingLiveWindow.mount();
    }


    public FloatingLiveWindow getFloatingLiveWindow() {
        return floatingLiveWindow;
    }


    protected void initComponents() {
    }

    protected void initListeners() {
        // Init property listeners
        PropertyChangeListener propertyChangeListener = new PropertyListener();

        PropertyChangeEventSource toolWindowSource = descriptor.getToolWindow();

        toolWindowSource.addPlafPropertyChangeListener("type", propertyChangeListener);
        toolWindowSource.addPlafPropertyChangeListener("maximized",propertyChangeListener);

        // Init floating live type desrciptor properties listeners
        PropertyChangeEventSource floatingLiveTypeDescriptorSource = (PropertyChangeEventSource) descriptor.getToolWindow().getTypeDescriptor(FloatingLiveTypeDescriptor.class);
        floatingLiveTypeDescriptorSource.addPlafPropertyChangeListener("location", propertyChangeListener);
        floatingLiveTypeDescriptorSource.addPlafPropertyChangeListener("size", propertyChangeListener);
        floatingLiveTypeDescriptorSource.addPlafPropertyChangeListener("enabled", propertyChangeListener);

        moveMouseInputHandler = new FloatingMoveMouseInputHandler(null);
        livePanelComponentListener = new LivePanelComponentListener();
    }


    public class PropertyListener extends DynamicPropertyChangeListener {

        protected Rectangle oldBounds = null;


        public void onType(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor)
                return;

            assert "type".equals(evt.getPropertyName());
            if (evt.getNewValue() == ToolWindowType.FLOATING_LIVE) {
                if (descriptor.getManager().getLayeredPane() != null) {

                    // Remove listeners
                    toolWindowTabPanel.removeEventDispatcherlListener(moveMouseInputHandler);

                    toolWindowTitleBar.removeMouseMotionListener(moveMouseInputHandler);
                    toolWindowTitleBar.removeMouseListener(moveMouseInputHandler);

                    // Add listeners
                    toolWindowTabPanel.addEventDispatcherlListener(moveMouseInputHandler);

                    toolWindowTitleBar.addMouseMotionListener(moveMouseInputHandler);
                    toolWindowTitleBar.addMouseListener(moveMouseInputHandler);

                    settedListener = true;
                }
            } else if (evt.getOldValue() == ToolWindowType.FLOATING_LIVE) {
                if (descriptor.getManager().getLayeredPane() != null) {
                    if (settedListener)
                        lastBounds = descriptor.getManager().getFloatingLiveWindow(toolWindow).getBounds();

                    // Remove listeners
                    toolWindowTabPanel.removeEventDispatcherlListener(moveMouseInputHandler);

                    toolWindowTitleBar.removeMouseMotionListener(moveMouseInputHandler);
                    toolWindowTitleBar.removeMouseListener(moveMouseInputHandler);

                    settedListener = false;
                }
            }
        }

        public void onMaximized(PropertyChangeEvent evt) {
            if (toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {

                FloatingLiveWindow floatingLiveWindow = descriptor.getManager().getFloatingLiveWindow(toolWindow);
                if ((Boolean) evt.getNewValue()) {
                    oldBounds = floatingLiveWindow.getBounds();

                    Rectangle bounds = descriptor.getManager().getMainContainer().getBounds();
                    bounds = SwingUtilities.convertRectangle(descriptor.getManager().getMainContainer(),
                                                             bounds,
                                                             descriptor.getManager().getRootPane().getLayeredPane());
                    floatingLiveWindow.setBounds(bounds);
                } else {
                    floatingLiveWindow.setBounds(oldBounds);
                }

                SwingUtil.repaint((Component) floatingLiveWindow);
            }
        }

        public void onLocation(PropertyChangeEvent evt) {
            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE) != evt.getSource())
                return;

            if (valueAdjusting)
                return;

            FloatingLiveWindow floatingLiveWindow = descriptor.getManager().getFloatingLiveWindow(toolWindow);
            if (floatingLiveWindow.isVisible()) {
                Point location = (Point) evt.getNewValue();
                floatingLiveWindow.setLocation(location);
            }
            lastBounds = null;
        }

        public void onSize(PropertyChangeEvent evt) {
            if (descriptor.getTypeDescriptor(ToolWindowType.FLOATING_LIVE) != evt.getSource())
                return;

            if (valueAdjusting)
                return;

            FloatingLiveWindow floatingLiveWindow = descriptor.getManager().getFloatingLiveWindow(toolWindow);
            if (floatingLiveWindow.isVisible()) {
                Dimension size = (Dimension) evt.getNewValue();
                floatingLiveWindow.setSize(size);
            }
            lastBounds = null;
        }

        public void onEnabled(PropertyChangeEvent evt) {
            boolean newValue = (Boolean) evt.getNewValue();

            if (!newValue && toolWindow.getType() == ToolWindowType.FLOATING_LIVE)
                toolWindow.setType(ToolWindowType.DOCKED);
        }
    }

    public class LivePanelComponentListener extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            valueAdjusting = true;
            try {
                toolWindow.getTypeDescriptor(FloatingLiveTypeDescriptor.class).setSize(floatingLiveWindow.getWidth(),
                                                                                       floatingLiveWindow.getHeight());
            } finally {
                valueAdjusting = false;
            }
        }

        public void componentMoved(ComponentEvent e) {
            valueAdjusting = true;
            try {
                toolWindow.getTypeDescriptor(FloatingLiveTypeDescriptor.class).setLocation(floatingLiveWindow.getX(),
                                                                                           floatingLiveWindow.getY());
            } finally {
                valueAdjusting = false;
            }
        }
    }

}