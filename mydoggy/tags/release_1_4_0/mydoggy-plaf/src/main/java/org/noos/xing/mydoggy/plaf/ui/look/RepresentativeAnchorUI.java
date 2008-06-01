package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import static org.noos.xing.mydoggy.ToolWindowAnchor.*;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro
 */
public class RepresentativeAnchorUI extends MetalLabelUI {
    protected JComponent label;

    protected LineBorder labelBorder;

    protected ToolWindowDescriptor descriptor;
    protected ToolWindow toolWindow;
    protected ResourceManager resourceManager;
    protected DockedTypeDescriptor dockedTypeDescriptor;

    protected RepresentativeAnchorMouseAdapter adapter;

    protected Timer flashingTimer;
    protected int flasingDuration;
    protected boolean flashingState;
    protected MutableColor flashingAnimBackStart;
    protected MutableColor flashingAnimBackEnd;
    protected AbstractAnimation flashingAnimation;

    protected TranslucentPanel previewPanel;


    public RepresentativeAnchorUI(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.resourceManager = descriptor.getResourceManager();

        this.flashingAnimation = new GradientAnimation();
        this.flashingAnimBackStart = new MutableColor(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
        this.flashingAnimBackEnd = new MutableColor(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));

        this.dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        this.dockedTypeDescriptor.addPropertyChangeListener(this);
    }


    public void installUI(JComponent c) {
        super.installUI(c);

        this.label = c;
        labelBorder = new LineBorder(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER), 1, true, 3, 3);
        c.setBorder(labelBorder);
        c.setForeground(resourceManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND));

        SwingUtil.registerDragGesture(c, new RepresentativeAnchorUIDragGesture(descriptor));
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        toolWindow.removePropertyChangeListener(this);
        c.removeMouseListener(adapter);
        c.removeMouseMotionListener(adapter);
    }

    public void update(Graphics g, JComponent c) {
        if (toolWindow.isAvailable())
            c.setForeground(resourceManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND));

        if (toolWindow.isFlashing() && !toolWindow.isVisible()) {

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
                         resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                         resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                         toolWindow.isVisible(),
                         false);
        }
        paint(g, c);
    }

    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if ("visible".equals(propertyName)) {
            boolean visible = (Boolean) e.getNewValue();
            label.setOpaque(visible);
            if (visible) {
                labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));

                descriptor.getToolBar().ensureVisible(label);
            } else
                labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER));

            toolWindow.setFlashing(false);
            SwingUtil.repaint(label);
        } else if ("flash".equals(propertyName)) {
            if (e.getNewValue() == Boolean.TRUE) {
                if (!toolWindow.isVisible()) {
                    flasingDuration = -1;
                    SwingUtil.repaint(label);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(label);
                }
            }
        } else if ("flash.duration".equals(propertyName)) {
            if (e.getNewValue() == Boolean.TRUE) {
                if (!toolWindow.isVisible()) {
                    flasingDuration = (Integer) e.getNewValue();
                    SwingUtil.repaint(label);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(label);
                }
            }
        }
    }

    public ToolWindowDescriptor getDescriptor() {
        return descriptor;
    }

    protected void installListeners(JLabel c) {
        super.installListeners(c);

        // Forse PropertyChangeListener
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

        adapter = new RepresentativeAnchorMouseAdapter();
        c.addMouseListener(adapter);
        c.addMouseMotionListener(adapter);

        descriptor.getToolWindow().addInternalPropertyChangeListener(this);
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
            g.setColor(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
            g.fillRect(0, 0, r.width, r.height);
        }
    }

    protected void hideAllPreview() {
        if (previewPanel != null) {
            GlassPanel glassPane = descriptor.getManager().getGlassPanel();
            glassPane.remove(previewPanel);
        }
    }


    protected class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(600f);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackStart,
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackEnd,
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackStart,
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackEnd,
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                                                      resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      animationPercent);
                    break;
            }
            SwingUtil.repaint(label);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    flashingAnimBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
                    break;
                case OUTGOING:
                    flashingAnimBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START));
                    break;
            }
            SwingUtil.repaint(label);
        }

        protected void onHide(Object... params) {
            flashingAnimBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START));
            flashingAnimBackEnd.setRGB(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END));
        }

        protected void onShow(Object... params) {
            flashingAnimBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
            flashingAnimBackEnd.setRGB(resourceManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }

    }

    protected class RepresentativeAnchorMouseAdapter extends MouseInputAdapter implements ActionListener {
        Timer previewTimer;
        boolean firstPreview = true;

        public RepresentativeAnchorMouseAdapter() {
            previewTimer = new Timer(0, this);
        }

        public void mouseClicked(MouseEvent e) {
            if (!toolWindow.isAvailable())
                return;

            previewTimer.stop();
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
                        toolWindow.setActive(true);
                    }
                }
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled()) {
                    descriptor.getToolWindowContainer().showPopupMenu(e.getComponent(), e.getX(), e.getY());
                }
            }
//            if (label.getBorder() != labelBorder)
            label.setBorder(labelBorder);
            labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));
            SwingUtil.repaint(label);
        }

        public void mouseEntered(MouseEvent e) {
            if (!toolWindow.isVisible()) {
                if (previewPanel == null) {
                    // TODO: when dockedTypeDescriptor.getPreviewDelay() is grater than 1000 then there is a
                    // a delay of 1000 ms in addition...BOH!!
                    previewTimer.setInitialDelay(
                            dockedTypeDescriptor.getPreviewDelay()
                    );
                    previewTimer.start();
                }
            }

            if (toolWindow.isFlashing())
                return;

            Component source = e.getComponent();
            if (!source.isOpaque()) {
                labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));
                SwingUtil.repaint(source);
            }
        }

        public void mouseExited(MouseEvent e) {
            if (e.getX() >= label.getWidth() || e.getX() <= 0 ||
                e.getY() >= label.getHeight() || e.getY() <= 0)
                firstPreview = false;

            previewTimer.stop();
            actionPerformed(new ActionEvent(previewTimer, 0, "stop"));

            if (toolWindow.isFlashing())
                return;

            Component source = e.getComponent();
            if (!source.isOpaque()) {
                labelBorder.setLineColor(resourceManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER));
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
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(label);

                        if (frame != null) {
                            GlassPanel glassPane = descriptor.getManager().getGlassPanel();
                            glassPane.remove(previewPanel);
                            SwingUtil.repaint(glassPane);
                            glassPane.setVisible(false);

                            SwingUtil.repaint(frame);

                            previewPanel = null;
                        }
                    }
                    firstPreview = false;
                } else
                if (dockedTypeDescriptor.isPreviewEnabled() && descriptor.getManager().getToolWindowManagerDescriptor().isPreviewEnabled()) {
                    Container contentContainer = ((DockedContainer) descriptor.getToolWindowContainer()).getContentContainer();
                    int width = 176;
                    int height = 132;

                    // Show Preview
                    RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.getWindowAncestor(label);
                    if (rootPaneContainer != null) {
                        JMenuBar jMenuBar = rootPaneContainer instanceof JFrame ?
                                            ((JFrame) rootPaneContainer).getJMenuBar() : null;

                        firstPreview = true;
                        previewTimer.stop();

                        GlassPanel glassPane = descriptor.getManager().getGlassPanel();

                        if (previewPanel != null)
                            glassPane.remove(previewPanel);

                        previewPanel = new TranslucentPanel(new ExtendedTableLayout(new double[][]{{2, TableLayout.FILL, 2}, {2, TableLayout.FILL, 2}}));
                        previewPanel.setAlphaModeRatio(dockedTypeDescriptor.getPreviewTransparentRatio());
                        previewPanel.setSize(width + 4, height + 4);

                        Container mainContainer = descriptor.getManager();
                        switch (descriptor.getToolWindow().getAnchor()) {
                            case LEFT:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        label.getX() + label.getWidth() + 3,

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        label.getY() +
                                        (descriptor.getToolBar(TOP).getAvailableTools() != 0 ? 23 : 0)
                                );
                                break;
                            case TOP:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        label.getX() +
                                        (descriptor.getToolBar(LEFT).getAvailableTools() != 0 ? 23 : 0),

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        label.getY() + label.getHeight() + 3
                                );
                                break;
                            case BOTTOM:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        label.getX() +
                                        (descriptor.getToolBar(LEFT).getAvailableTools() != 0 ? 23 : 0),

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        mainContainer.getHeight() -
                                                                  previewPanel.getHeight() - 26
                                );
                                break;
                            case RIGHT:
                                previewPanel.setLocation(
                                        mainContainer.getX() +
                                        mainContainer.getWidth() -
                                                                 previewPanel.getWidth() - 26,

                                        (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                        mainContainer.getY() +
                                        label.getY() +
                                        (descriptor.getToolBar(TOP).getAvailableTools() != 0 ? 23 : 0)
                                );
                                break;
                        }

                        if (previewPanel.getY() + previewPanel.getHeight() >
                            mainContainer.getY() + mainContainer.getHeight() - 26) {

                            previewPanel.setLocation(
                                    previewPanel.getX(),

                                    (jMenuBar != null ? jMenuBar.getHeight() : 0) +
                                    mainContainer.getY() +
                                    mainContainer.getHeight() -
                                                              (descriptor.getToolBar(BOTTOM).getAvailableTools() != 0 ? 23 : 0) -
                                                              previewPanel.getHeight() - 3
                            );
                        }

                        if (previewPanel.getX() + previewPanel.getWidth() >
                            mainContainer.getX() + mainContainer.getWidth() - 26) {

                            previewPanel.setLocation(
                                    mainContainer.getX() +
                                    mainContainer.getWidth() -
                                                             (descriptor.getToolBar(RIGHT).getAvailableTools() != 0 ? 23 : 0) -
                                                             previewPanel.getWidth() - 3,

                                    previewPanel.getY()
                            );
                        }


                        previewPanel.add(contentContainer, "1,1,FULL,FULL");

                        glassPane.add(previewPanel);
                        glassPane.setVisible(true);
                        SwingUtil.repaint(glassPane);
                    }
                }
            }
        }

    }

    protected class RepresentativeAnchorUIDragGesture extends DragGestureAdapter {
        private ToolWindowAnchor lastAnchor;

        public RepresentativeAnchorUIDragGesture(ToolWindowDescriptor descriptor) {
            super(descriptor);
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            // Check if a preview is still visible.
            hideAllPreview();

            // Acquire locks
            acquireLocks();

            // Start Drag
            dge.startDrag(Cursor.getDefaultCursor(),
                          new MyDoggyTransferable(MyDoggyTransferable.TOOL_WINDOW_ID_DF, toolWindow.getId()),
                          this);

            // Fire startDrag Event
            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(label, "startDrag", null, dge));

            // Setup ghostImage
            JComponent representativeAnchor = descriptor.getRepresentativeAnchor();
            BufferedImage ghostImage = new BufferedImage(representativeAnchor.getWidth(),
                                                         representativeAnchor.getHeight(),
                                                         BufferedImage.TYPE_INT_RGB);
            representativeAnchor.print(ghostImage.createGraphics());
            setGhostImage(dge.getDragOrigin(), ghostImage);

            lastAnchor = null;
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;

            // Obtain anchor for location
            ToolWindowAnchor newAnchor = manager.getToolWindowAnchor(
                    SwingUtil.convertPointFromScreen(dsde.getLocation(), manager)
            );

            // Produce updatedGhostImage
            if (newAnchor != lastAnchor) {
                resetGhostImage();

                if (newAnchor == null) {
                    updatedGhostImage = ghostImage;
                    manager.getBar(lastAnchor).setTempShowed(false);
                } else {
                    if (manager.getBar(newAnchor).getAvailableTools() == 0)
                        manager.getBar(newAnchor).setTempShowed(true);

                    switch (newAnchor) {
                        case LEFT:
                            switch (descriptor.getToolWindow().getAnchor()) {
                                case LEFT:
                                    updatedGhostImage = ghostImage;
                                    break;
                                case RIGHT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, Math.PI);
                                    break;
                                default:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI);
                                    break;
                            }
                            break;
                        case RIGHT:
                            switch (descriptor.getToolWindow().getAnchor()) {
                                case LEFT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, Math.PI);
                                    break;
                                case RIGHT:
                                    updatedGhostImage = ghostImage;
                                    break;
                                default:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI);
                                    break;
                            }
                            break;
                        case TOP:
                        case BOTTOM:
                            switch (descriptor.getToolWindow().getAnchor()) {
                                case LEFT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, -1.5 * Math.PI);
                                    break;
                                case RIGHT:
                                    updatedGhostImage = GraphicsUtil.rotate(ghostImage, 1.5 * Math.PI);
                                    break;
                                default:
                                    updatedGhostImage = ghostImage;
                                    break;
                            }
                            break;
                    }
                }
                lastAnchor = newAnchor;
            }

            updateGhostImage(dsde.getLocation(), updatedGhostImage);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocksOne();

            // Restore graphics
            if (lastAnchor != null)
                manager.getBar(lastAnchor).setTempShowed(false);
            else {
                manager.getBar(LEFT).setTempShowed(false);
                manager.getBar(RIGHT).setTempShowed(false);
                manager.getBar(TOP).setTempShowed(false);
                manager.getBar(BOTTOM).setTempShowed(false);
            }

            // Fire endDrag event
            descriptor.getToolBar().propertyChange(new PropertyChangeEvent(label, "endDrag", null, dsde));

            // cleanup glassPane
            cleanupGhostImage();
            lastAnchor = null;

            releaseLocksTwo();
        }

    }


}