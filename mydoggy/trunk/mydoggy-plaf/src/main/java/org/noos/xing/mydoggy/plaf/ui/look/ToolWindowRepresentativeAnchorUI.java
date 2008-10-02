package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowRepresentativeAnchor;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.drag.RepresentativeAnchorDragListener;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.RemoveNotifyDragListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro
 */
public class ToolWindowRepresentativeAnchorUI extends MetalLabelUI implements Cleaner {


    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowRepresentativeAnchorUI();
    }


    protected ToolWindowRepresentativeAnchor representativeAnchor;

    protected LineBorder labelBorder;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected RepresentativeAnchorDescriptor representativeAnchorDescriptor;

    protected ToolWindowRepresentativeAnchorMouseAdapter adapter;

    protected Timer flashingTimer;
    protected int flasingDuration;
    protected boolean flashingState;
    protected MutableColor flashingAnimBackStart;
    protected MutableColor flashingAnimBackEnd;
    protected AbstractAnimation flashingAnimation;

    protected TranslucentPanel previewPanel;

    // Drag fields
    protected RemoveNotifyDragListener removeNotifyDragListener;


    public ToolWindowRepresentativeAnchorUI() {
    }


    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if ("visible".equals(propertyName)) {
            boolean visible = (Boolean) e.getNewValue();
            representativeAnchor.setOpaque(visible);
            if (visible) {
                labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.TWRA_MOUSE_IN_BORDER));

                descriptor.getToolBar().ensureVisible(representativeAnchor);
                toolWindow.setFlashing(false);
            } else
                labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.TWRA_MOUSE_OUT_BORDER));

            SwingUtil.repaint(representativeAnchor);
        } else if ("flash".equals(propertyName)) {
            if (e.getNewValue() == Boolean.TRUE) {
                if (!toolWindow.isVisible()) {
                    flasingDuration = SwingUtil.getInt(e, -1);
                    SwingUtil.repaint(representativeAnchor);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(representativeAnchor);
                }
            }
        } 
    }

    public void cleanup() {
        uninstallUI(representativeAnchor);
    }


    public void installUI(JComponent c) {
        // Init fields
        this.representativeAnchor = (ToolWindowRepresentativeAnchor) c;
        this.descriptor = representativeAnchor.getDockableDescriptor();
        this.toolWindow = descriptor.getToolWindow();

        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Release timers and stop animations
        if (flashingTimer != null)
            flashingTimer.stop();
        flashingTimer = null;

        flashingAnimation.stop();

        // Reset Fields
        descriptor = null;
        toolWindow = null;
        representativeAnchorDescriptor = null;
        representativeAnchor = null;
    }


    public void update(Graphics g, JComponent c) {
        if (toolWindow.isAvailable())
            c.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND));
        else
            c.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND_UNAVAILABLE));

        if (isFlashing() && !toolWindow.isVisible()) {

            updateAnchor(g, c,
                         flashingAnimBackStart,
                         flashingAnimBackEnd,
                         false,
                         true);

            if (flashingTimer == null) {
                flashingTimer = new Timer(600, new ActionListener() {
                    long start = 0;

                    public void actionPerformed(ActionEvent e) {
                        if (start == 0)
                            start = System.currentTimeMillis();

                        flashingState = !flashingState;

                        if (flashingAnimation.isAnimating())
                            flashingAnimation.stop();

                        if (flashingState) {
                            flashingAnimation.show();
                        } else {
                            flashingAnimation.hide();
                        }

                        if (flasingDuration != -1 && System.currentTimeMillis() - start > flasingDuration)
                            toolWindow.setFlashing(false);
                    }
                });
                flashingState = true;
                flashingAnimation.show();
            }
            if (!flashingTimer.isRunning()) {
                flashingTimer.start();
            }
        } else {
            if (flashingTimer != null) {
                flashingTimer.stop();
                flashingTimer = null;
            }

            updateAnchor(g, c,
                         UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START),
                         UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_END),
                         toolWindow.isVisible(),
                         false);
        }
        paint(g, c);
    }


    protected void installDefaults(JLabel c) {
        super.installDefaults(c);
        
        // Flashing animation fields
        this.flashingAnimation = new GradientAnimation();
        this.flasingDuration = -1;
        this.flashingAnimBackStart = new MutableColor(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
        this.flashingAnimBackEnd = new MutableColor(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));

        labelBorder = new LineBorder(UIManager.getColor(MyDoggyKeySpace.TWRA_MOUSE_OUT_BORDER), 1, true, 3, 3);

        c.setBorder(labelBorder);
        c.setForeground(UIManager.getColor(MyDoggyKeySpace.TWRA_FOREGROUND));

        String oldText = c.getText();
        if (oldText != null) {
            c.setText(null);
            c.setText(oldText);
        }

        oldText = c.getToolTipText();
        if (oldText != null) {
            c.setToolTipText(null);
            c.setToolTipText(oldText);
        }

        SwingUtil.installFont(c, "ToolWindowRepresentativeAnchorUI.font");
    }

    protected void installListeners(JLabel c) {
        super.installListeners(c);

        this.representativeAnchorDescriptor = toolWindow.getRepresentativeAnchorDescriptor();
        this.representativeAnchorDescriptor.addPropertyChangeListener(this);

        descriptor.getCleaner().addCleaner(this);

        adapter = new ToolWindowRepresentativeAnchorMouseAdapter();
        c.addMouseListener(adapter);
        c.addMouseMotionListener(adapter);

        descriptor.getToolWindow().addPlafPropertyChangeListener(this);

        descriptor.getManager().addRemoveNotifyListener(removeNotifyDragListener = new RemoveNotifyDragListener(c, new ToolWindowRepresentativeAnchorDragListener(descriptor, c)));
    }

    protected void uninstallListeners(JLabel c) {
        super.uninstallListeners(c);

        representativeAnchorDescriptor.removePropertyChangeListener(this);

        descriptor.getCleaner().removeCleaner(this);

        c.removeMouseListener(adapter);
        c.removeMouseMotionListener(adapter);

        descriptor.getToolWindow().removePlafPropertyChangeListener(this);

        // Remove drag gesture
        removeNotifyDragListener.cleanup();
        descriptor.getManager().removeRemoveNotifyListener(removeNotifyDragListener);
    }


    protected void updateAnchor(Graphics g, JComponent c,
                                Color backgroundStart, Color backgroundEnd,
                                boolean active, boolean flashing) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        if (flashing || active) {
            GraphicsUtil.fillRect(g,
                                  r,
                                  backgroundStart,
                                  backgroundEnd,
                                  null,
                                  GraphicsUtil.FROM_CENTRE_GRADIENT_ON_X);
        } else {
            g.setColor(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
            g.fillRect(0, 0, r.width, r.height);
        }
    }

    protected void hideAllPreview() {
        if (previewPanel != null) {
            GlassPanel glassPane = descriptor.getManager().getGlassPanel();
            glassPane.remove(previewPanel);
        }
    }

    protected boolean isFlashing() {
        boolean result = toolWindow.isFlashing();
        if (!result)
            for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
                if (tab.isFlashing())
                    return true;
            }
        return result;
    }


    public class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(600f);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackStart,
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE),
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackEnd,
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE),
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_END),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackStart,
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START),
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackEnd,
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_END),
                                                      UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE),
                                                      animationPercent);
                    break;
            }
            SwingUtil.repaint(representativeAnchor);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
                    break;
                case OUTGOING:
                    flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START));
                    break;
            }
            SwingUtil.repaint(representativeAnchor);
        }

        protected void onHide(Object... params) {
            flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_START));
            flashingAnimBackEnd.setRGB(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_ACTIVE_END));
        }

        protected void onShow(Object... params) {
            flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
            flashingAnimBackEnd.setRGB(UIManager.getColor(MyDoggyKeySpace.TWRA_BACKGROUND_INACTIVE));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }
    }

    public class ToolWindowRepresentativeAnchorMouseAdapter extends MouseInputAdapter implements ActionListener, Cleaner {
        protected Timer previewTimer;
        protected boolean firstPreview = true;


        public ToolWindowRepresentativeAnchorMouseAdapter() {
            previewTimer = new Timer(0, this);
            
            descriptor.getCleaner().addBefore(ToolWindowRepresentativeAnchorUI.this, this);
        }


        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            previewTimer.stop();
            firstPreview = false;
            actionPerformed(new ActionEvent(previewTimer, 0, "stop"));

            if (SwingUtilities.isLeftMouseButton(e)) {
                int onmask = MouseEvent.SHIFT_DOWN_MASK;
                if ((e.getModifiersEx() & onmask) == onmask) {
                    if (toolWindow.isVisible()) {
                        toolWindow.setVisible(false);
                    } else {
                        if (toolWindow.isAggregateMode()) {
                            toolWindow.setAggregateMode(false);
                            try {
                                toolWindow.setVisible(true);
                            } finally {
                                toolWindow.setAggregateMode(true);
                            }
                        } else {
                            toolWindow.aggregate();
                        }
                        toolWindow.setActive(true);
                    }
                } else {
                    if (toolWindow.isVisible()) {
                        toolWindow.setVisible(false);
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                toolWindow.setActive(true);
                            }
                        });
                        toolWindow.setVisible(true);
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled()) {
                    descriptor.showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
            }

//            if (label.getBorder() != labelBorder)
            representativeAnchor.setBorder(labelBorder);
            labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.TWRA_MOUSE_IN_BORDER));
            SwingUtil.repaint(representativeAnchor);
        }

        public void mouseEntered(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            if (!toolWindow.isVisible()) {
                if (previewPanel == null) {
                    previewTimer.setInitialDelay(
                            representativeAnchorDescriptor.getPreviewDelay()
                    );
                    previewTimer.start();
                }
            }

            if (toolWindow.isFlashing())
                return;

            Component source = e.getComponent();
            if (!source.isOpaque()) {
                labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.TWRA_MOUSE_IN_BORDER));
                SwingUtil.repaint(source);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            if (e.getX() >= representativeAnchor.getWidth() || e.getX() <= 0 ||
                e.getY() >= representativeAnchor.getHeight() || e.getY() <= 0)
                firstPreview = false;

            previewTimer.stop();
            actionPerformed(new ActionEvent(previewTimer, 0, "stop"));

            if (toolWindow.isFlashing())
                return;

            Component source = e.getComponent();
            if (!source.isOpaque()) {
                labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.TWRA_MOUSE_OUT_BORDER));
                SwingUtil.repaint(source);
            }
        }

        public void mouseDragged(MouseEvent e) {
            firstPreview = false;
            previewTimer.stop();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == previewTimer) {
                if ("stop".equals(e.getActionCommand())) {
                    if (previewPanel != null && !firstPreview) {
                        Window windowAncestor = SwingUtilities.getWindowAncestor(representativeAnchor);

                        if (windowAncestor != null) {
                            GlassPanel glassPane = descriptor.getManager().getGlassPanel();
                            glassPane.remove(previewPanel);
                            glassPane.setVisible(false);
                            SwingUtil.repaint(glassPane);
                            SwingUtil.repaint(windowAncestor);

                            previewPanel = null;
                        }
                    }
                    firstPreview = false;
                } else
                if (representativeAnchorDescriptor.isPreviewEnabled() &&
                    descriptor.getManager().getToolWindowManagerDescriptor().isPreviewEnabled()) {
                    Container contentContainer = descriptor.getToolWindowPanel();

                    // Show Preview
                    RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.getWindowAncestor(representativeAnchor);
                    if (rootPaneContainer != null) {
                        firstPreview = true;
                        previewTimer.stop();

                        GlassPanel glassPane = descriptor.getManager().getGlassPanel();

                        if (previewPanel != null)
                            glassPane.remove(previewPanel);

                        previewPanel = new TranslucentPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
                        previewPanel.setAlphaModeRatio(representativeAnchorDescriptor.getPreviewTransparentRatio());
                        setPreviewPanelBounds(rootPaneContainer);
                        previewPanel.add(contentContainer, "1,1,FULL,FULL");

                        glassPane.add(previewPanel);
                        glassPane.setVisible(true);
                        SwingUtil.repaint(glassPane);
                    }
                }
            }
        }

        public void cleanup() {
            // Stop preview
            actionPerformed(new ActionEvent(previewTimer, 0, "stop"));

            // Clean timers
            if (previewTimer != null) {
                previewTimer.stop();
                previewTimer = null;
            }
        }


        protected void setPreviewPanelBounds(RootPaneContainer rootPaneContainer) {
            if (SwingUtil.getBoolean(MyDoggyKeySpace.TOOL_WINDOW_PREVIEW_FULL, false))
                setFullPreviewBounds();
            else
                setThumbnailPreviewBounds(rootPaneContainer);
        }

        protected void setThumbnailPreviewBounds(RootPaneContainer rootPaneContainer) {
            int width = 176;
            int height = 132;
            JMenuBar jMenuBar = rootPaneContainer instanceof JFrame ?
                                ((JFrame) rootPaneContainer).getJMenuBar() : null;


            previewPanel.setSize(width + 4, height + 4);

            Rectangle containerRect = descriptor.getManagerBounds();
            switch (descriptor.getToolWindow().getAnchor()) {
                case LEFT:
                    previewPanel.setLocation(
                            containerRect.x +
                            representativeAnchor.getX() + representativeAnchor.getWidth() + 3,

                            (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                            containerRect.y +
                            representativeAnchor.getY() +
                            (descriptor.getToolBar(TOP).getSize())
                    );
                    break;
                case TOP:
                    previewPanel.setLocation(
                            containerRect.x +
                            representativeAnchor.getX() +
                            (descriptor.getToolBar(LEFT).getSize()),

                            (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                            containerRect.y +
                            representativeAnchor.getY() + representativeAnchor.getHeight() + 3
                    );
                    break;
                case BOTTOM:
                    previewPanel.setLocation(
                            containerRect.x +
                            representativeAnchor.getX() +
                            (descriptor.getToolBar(LEFT).getSize()),

                            (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                            containerRect.y +
                            containerRect.height -
                                                      previewPanel.getHeight() - 26
                    );
                    break;
                case RIGHT:
                    previewPanel.setLocation(
                            containerRect.x +
                            containerRect.width -
                                                     previewPanel.getWidth() - 26,

                            (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                            containerRect.y +
                            representativeAnchor.getY() +
                            (descriptor.getToolBar(TOP).getSize())
                    );
                    break;
            }

            if (previewPanel.getY() + previewPanel.getHeight() >
                containerRect.getY() + containerRect.getHeight() - 26) {

                previewPanel.setLocation(
                        previewPanel.getX(),

                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                        containerRect.y +
                        containerRect.height -
                                                  (descriptor.getToolBar(BOTTOM).getSize()) -
                                                  previewPanel.getHeight() - 3
                );
            }

            if (previewPanel.getX() + previewPanel.getWidth() >
                containerRect.x + containerRect.getWidth() - 26) {

                previewPanel.setLocation(
                        containerRect.x +
                        containerRect.width -
                                                 (descriptor.getToolBar(RIGHT).getSize()) -
                                                 previewPanel.getWidth() - 3,

                        previewPanel.getY()
                );
            }
        }

        protected void setFullPreviewBounds() {
            Component barContainer = descriptor.getToolBar(toolWindow.getAnchor()).getContainer();

            int length = Math.max(descriptor.getDividerLocation(),
                                  descriptor.getDockedTypeDescriptor().getMinimumDockLength());
            if (length == -1)
                length = 200;

            switch (toolWindow.getAnchor()) {
                case LEFT:
                    int height = barContainer.getHeight();
                    previewPanel.setSize(length, height);

                    Point location = new Point(0, 0);
                    SwingUtilities.convertPointToScreen(location, barContainer);
                    location.x += barContainer.getWidth();
                    previewPanel.setLocation(location);
                    break;
                case RIGHT:
                    height = barContainer.getHeight();
                    previewPanel.setSize(length, height);

                    location = new Point(0, 0);
                    SwingUtilities.convertPointToScreen(location, barContainer);
                    location.x -= previewPanel.getWidth();
                    previewPanel.setLocation(location);
                    break;
                case TOP:
                    int width = barContainer.getWidth();
                    previewPanel.setSize(width, length);

                    location = new Point(0, 0);
                    SwingUtilities.convertPointToScreen(location, barContainer);
                    location.y += barContainer.getHeight();
                    previewPanel.setLocation(location);
                    break;
                case BOTTOM:
                    width = barContainer.getWidth();
                    previewPanel.setSize(width, length);

                    location = new Point(0, 0);
                    SwingUtilities.convertPointToScreen(location, barContainer);
                    location.y -= previewPanel.getHeight();
                    previewPanel.setLocation(location);
                    break;
            }

            int height = previewPanel.getHeight();
            Point point = SwingUtilities.convertPoint(previewPanel, 0, 0,
                                                      descriptor.getManager().getLayeredPane());

            previewPanel.setBounds(point.x, point.y, previewPanel.getWidth(), height);

        }
    }

    public class ToolWindowRepresentativeAnchorDragListener extends RepresentativeAnchorDragListener {

        public ToolWindowRepresentativeAnchorDragListener(DockableDescriptor descriptor, Component component) {
            super(descriptor, component);
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
            // Check if a preview is still visible.
            hideAllPreview();

            super.dragGestureRecognized(dge);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            super.dragDropEnd(dsde);

            // Finalize drag action...
            try {
                if (lastDropPanel != null) {
                    // The drop is on a dockable drop panel
                    lastDropPanel.drop(dsde.getDragSourceContext().getTransferable());
                } else if (lastBarAnchor == null) {
                    // The drop is not on an ToolWindowBar... so move the tool to FLOATING_LIVE or FLOATING

                    Window ancestor = SwingUtilities.getWindowAncestor(manager);

                    Rectangle ancestorBounds = ancestor.getBounds();
                    Point dsdeLocation = dsde.getLocation();

                    if (dsdeLocation.x >= ancestorBounds.x &&
                        dsdeLocation.y >= ancestorBounds.y &&
                        dsdeLocation.x <= ancestorBounds.getMaxX() &&
                        dsdeLocation.y <= ancestorBounds.getMaxY()) {

                        // Setup location
                        SwingUtil.convertPointFromScreen2(dsdeLocation, ancestor);
                        ToolWindow toolWindow = (ToolWindow) descriptor.getDockable();
                        toolWindow.getTypeDescriptor(FloatingLiveTypeDescriptor.class).setLocation(dsdeLocation.x, dsdeLocation.y);

                        // Move to floating live
                        toolWindow.setType(ToolWindowType.FLOATING_LIVE);
                    } else {
                        // Setup location
                        ToolWindow toolWindow = (ToolWindow) descriptor.getDockable();
                        toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).setLocation(
                                dsdeLocation.x, dsdeLocation.y
                        );

                        // Move to floating
                        toolWindow.setType(ToolWindowType.FLOATING);
                    }
                    // activate the tool
                    if (!toolWindow.isVisible())
                        toolWindow.setActive(true);
                }
            } finally {
                // End dockable drop gesture..
                dockableDropDragEnd();
            }
        }

        protected Transferable createTransferable() {
            return new MyDoggyTransferable(manager, MyDoggyTransferable.TOOL_WINDOW_ID_DF, toolWindow.getId());
        }

    }

}
