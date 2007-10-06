package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.cmp.drag.DragAndDropLock;
import org.noos.xing.mydoggy.plaf.ui.cmp.drag.ToolWindowTabTrasferable;
import org.noos.xing.mydoggy.plaf.ui.cmp.drag.ToolWindowTrasferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * @author Angelo De Caro
 */
public class ToolWindowTitleBarUI extends PanelUI {
    protected ToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;
    protected ResourceManager resourceManager;

    protected MutableColor animBackStart;
    protected MutableColor animBackEnd;
    protected MutableColor animTextColor;

    protected JComponent panel;

    protected GradientAnimation animation;

    protected Timer flashingTimer;
    protected int flasingDuration;
    protected boolean flashingState;
    protected AbstractAnimation flashingAnimation;


    public ToolWindowTitleBarUI(ToolWindowDescriptor descriptor, DockedContainer dockedContainer) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.resourceManager = descriptor.getResourceManager();

        dockedContainer.addPropertyChangeListener("active", new GradientActivationListener(descriptor));

        animBackStart = new MutableColor(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START));
        animBackEnd = new MutableColor(0, 0, 0);
        animTextColor = new MutableColor(0, 0, 0);

        flashingAnimation = new GradientAnimation(700f);

        animation = new GradientAnimation();

        descriptor.getToolWindow().addInternalPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("flash".equals(propertyName)) {
                    if (toolWindow.isVisible()) {
                        flasingDuration = -1;
                        SwingUtil.repaint(panel);
                    }
                } else if ("flash.duration".equals(propertyName)) {
                    if (toolWindow.isVisible()) {
                        flasingDuration = (Integer) evt.getNewValue();
                        SwingUtil.repaint(panel);
                    }
                } else if ("active".endsWith(propertyName)) {
                    toolWindow.setFlashing(false);
                }
            }
        });
    }


    public void installUI(JComponent c) {
        super.installUI(c);
        installDefaults(c);
        this.panel = c;

        final DragGesture dragGesture = new DragGesture();
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_MOVE, dragGesture);
        dragSource.addDragSourceMotionListener(dragGesture);

        panel.addContainerListener(new ContainerListener() {
            public void componentAdded(ContainerEvent e) {
                if (e.getChild() instanceof ToolWindowTabPanel) {
                    ToolWindowTabPanel panel = (ToolWindowTabPanel) e.getChild();

                    for (Component cmp : panel.getTabContainer().getComponents()) {
                        DragSource dragSource = DragSource.getDefaultDragSource();
                        dragSource.createDefaultDragGestureRecognizer(cmp, DnDConstants.ACTION_MOVE, dragGesture);
                        dragSource.addDragSourceMotionListener(dragGesture);
                    }

                    panel.getTabContainer().addContainerListener(new ContainerListener() {
                        public void componentAdded(ContainerEvent e) {
                            DragSource dragSource = DragSource.getDefaultDragSource();
                            dragSource.createDefaultDragGestureRecognizer(e.getChild(), DnDConstants.ACTION_MOVE, dragGesture);
                            dragSource.addDragSourceMotionListener(dragGesture);
                        }

                        public void componentRemoved(ContainerEvent e) {

                        }
                    });

                    DragSource dragSource = DragSource.getDefaultDragSource();
                    dragSource.createDefaultDragGestureRecognizer(panel.getViewport(), DnDConstants.ACTION_MOVE, dragGesture);
                    dragSource.addDragSourceMotionListener(dragGesture);
                }
            }

            public void componentRemoved(ContainerEvent e) {
            }
        });

    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        uninstallDefaults(c);
    }

    public void update(Graphics g, JComponent c) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        if (toolWindow.isFlashing()) {

            if (flashingState)
                updateToolWindowTitleBar(g, c,
                                         animBackStart, animBackEnd,
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_FLASHING_0),
                                         animTextColor);
            else
                updateToolWindowTitleBar(g, c,
                                         animBackStart, animBackEnd,
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_FLASHING_1),
                                         animTextColor);

            if (flashingTimer == null) {
                flashingTimer = new Timer(700, new ActionListener() {
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
            if (animation.isAnimating()) {
                updateToolWindowTitleBar(g, c,
                                         animBackStart, animBackEnd,
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ANIMATING),
                                         animTextColor);
            } else if (c.isEnabled()) {
                updateToolWindowTitleBar(g, c,
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_END),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ACTIVE),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE));
            } else {
                updateToolWindowTitleBar(g, c,
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_END),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_INACTIVE),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE));
            }
        }

        paint(g, c);
    }


    protected void installDefaults(JComponent c) {
        LookAndFeel.installColorsAndFont(c, "Panel.background", "Panel.foreground", "Panel.font");
        LookAndFeel.installBorder(c, "Panel.border");
    }

    protected void uninstallDefaults(JComponent c) {
        LookAndFeel.uninstallBorder(c);
    }

    protected void updateToolWindowTitleBar(Graphics g, JComponent c,
                                            Color backgroundStart, Color backgroundEnd,
                                            Color idBackgroundColor, Color idColor) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        GraphicsUtil.fillRect(g, r,
                              backgroundStart, backgroundEnd,
                              null,
                              GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

        if (descriptor.getDockedTypeDescriptor().isIdVisibleOnTitleBar() ||
            toolWindow.getType() == ToolWindowType.FLOATING ||
            toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
            toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {

            String id = resourceManager.getUserString(descriptor.getToolWindow().getId());
            r.width = g.getFontMetrics().stringWidth(id) + 8;

            // TODO: add customization
            int halfHeigh = (r.height / 2);
            GraphicsUtil.fillRect(g, r,
                                  Color.WHITE,
                                  idBackgroundColor,
                                  new Polygon(new int[]{r.x, r.x + r.width - halfHeigh, r.x + r.width - halfHeigh, r.x},
                                              new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                              4),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            GraphicsUtil.fillRect(g, r,
                                  Color.WHITE,
                                  idBackgroundColor,
                                  new Arc2D.Double(r.x + r.width - r.height,
                                                   r.y, r.height, r.height, -90.0d, 180.0d, Arc2D.CHORD),
                                  GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

            g.setColor(idColor);
            g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
        }
    }


    protected class GradientActivationListener implements PropertyChangeListener {
        public static final float ANIMATION_DURATION = 80f;
        public static final int ANIMATION_SLEEP = 10;

        protected ToolWindowDescriptor descriptor;

        public GradientActivationListener(ToolWindowDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        public synchronized void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() != descriptor || !descriptor.getToolWindow().isVisible())
                return;

            assert evt.getPropertyName() != null;
            assert descriptor.getToolWindow().isVisible();

            if ("active".equals(evt.getPropertyName())) {
                if (evt.getNewValue() == Boolean.FALSE) {
                    if (animBackStart.equals(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START)))
                        animation.hide();
                } else {
                    if (animBackStart.equals(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START)))
                        animation.show();
                }
            }
        }
    }

    protected class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(300f);
        }

        protected GradientAnimation(float animationDuration) {
            super(animationDuration);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    GraphicsUtil.getInterpolatedColor(animBackStart,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_END),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(animBackStart,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_END),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE),
                                                      animationPercent);
                    break;
            }
            SwingUtil.repaint(panel);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START));
                    break;
                case OUTGOING:
                    animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START));
                    break;
            }
            SwingUtil.repaint(panel);
        }

        protected void onHide(Object... params) {
            animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_START));
            animBackEnd.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ENABLED_END));
        }

        protected void onShow(Object... params) {
            animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_START));
            animBackEnd.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_DISABLED_END));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }

    }

    protected class DragGesture implements DragGestureListener, DragSourceMotionListener, DragSourceListener {
        protected BufferedImage ghostImage;
        protected JComponent lastOverCmp = null;
        protected Border oldBorder = null;

        protected LineBorder highligthBorder = new LineBorder(Color.BLUE, 3);

        protected boolean moveAnchor;
        protected ToolWindowAnchor lastAnchor;

        public void dragGestureRecognized(DragGestureEvent dge) {
            if (toolWindow.getType() == ToolWindowType.FLOATING ||
                toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
                toolWindow.getType() == ToolWindowType.FLOATING_LIVE)
                return;

            if (DragAndDropLock.isLocked()) {
                DragAndDropLock.setDragAndDropStarted(false);
                return;
            }
            DragAndDropLock.setLocked(true);
            DragAndDropLock.setDragAndDropStarted(true);

            // Start Drag
//            System.out.println("dge.getComponent() = " + dge.getComponent());
            MyDoggyToolWindowTab toolWindowTab = null;
            if (dge.getComponent() instanceof ToolWindowTabPanel.TabButton) {
                ToolWindowTabPanel.TabButton tabButton = (ToolWindowTabPanel.TabButton) dge.getComponent();
                toolWindowTab = (MyDoggyToolWindowTab) tabButton.getTab();
            }

            if (toolWindowTab != null && toolWindowTab.getToolWindow() != null) {
                dge.startDrag(Cursor.getDefaultCursor(),
                              new ToolWindowTabTrasferable(toolWindowTab), this);
            } else {
                dge.startDrag(Cursor.getDefaultCursor(),
                              new ToolWindowTrasferable(toolWindow), this);
            }

            // Prepare glassPane for ghost image
            GlassPanel glassPane = descriptor.getManager().getGlassPanel();

            glassPane.setVisible(true);

            // Build orginalDragImage
            Component contentContainer = ((DockedContainer) descriptor.getToolWindowContainer()).getContentContainer();
            ghostImage = new BufferedImage(contentContainer.getWidth(),
                                           contentContainer.getHeight(), BufferedImage.TYPE_INT_RGB);
            contentContainer.print(ghostImage.getGraphics());

            // Setup glasspane
            Point p = (Point) dge.getDragOrigin().clone();
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setPoint(p);
            glassPane.setDraggingImage(ghostImage.getScaledInstance(contentContainer.getWidth() / 3,
                                                                    contentContainer.getHeight() / 3, BufferedImage.SCALE_SMOOTH));
            glassPane.repaint();

            lastAnchor = null;
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!DragAndDropLock.isDragAndDropStarted() || ghostImage == null)
                return;

            GlassPanel glassPane = descriptor.getManager().getGlassPanel();

            Point p = dsde.getLocation();
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setPoint(p);

            Container contentPane = descriptor.getManager().getRootPane().getLayeredPane();
            Component deepestCmp = SwingUtilities.getDeepestComponentAt(contentPane, p.x, p.y);

            if (deepestCmp != null) {
                if (deepestCmp instanceof ToolWindowTabPanel ||
                    SwingUtil.getParent(deepestCmp, ToolWindowTabPanel.class) != null) {
                    moveAnchor = false;

                    if (lastOverCmp != null)
                        lastOverCmp.setBorder(oldBorder);

                    lastOverCmp = (JComponent) deepestCmp;
                    oldBorder = lastOverCmp.getBorder();
                    lastOverCmp.setBorder(highligthBorder);
                } else {
                    moveAnchor = true;
                    if (lastOverCmp != null)
                        lastOverCmp.setBorder(oldBorder);

                    lastOverCmp = (JPanel) SwingUtil.getParent(deepestCmp, "toolWindow.container");
                    if (lastOverCmp != null) {
                        oldBorder = lastOverCmp.getBorder();
                        lastOverCmp.setBorder(highligthBorder);
                    }
                }
            }

            p = dsde.getLocation();
            SwingUtilities.convertPointFromScreen(p, descriptor.getManager());
            ToolWindowAnchor newAnchor = descriptor.getToolWindowAnchor(p);

            if (newAnchor != lastAnchor) {
                Rectangle dirtyRegion = glassPane.getRepaintRect();

                if (newAnchor == null) {
                    descriptor.getToolBar(lastAnchor).setTempShowed(false);
                } else {
                    if (descriptor.getToolBar(newAnchor).getAvailableTools() == 0)
                        descriptor.getToolBar(newAnchor).setTempShowed(true);
                }

                lastAnchor = newAnchor;
                glassPane.repaint(dirtyRegion);
            }

            glassPane.repaint(glassPane.getRepaintRect());
        }

        public void dragEnter(DragSourceDragEvent dsde) {
        }

        public void dragOver(DragSourceDragEvent dsde) {
        }

        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        public void dragExit(DragSourceEvent dse) {
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!DragAndDropLock.isDragAndDropStarted() || ghostImage == null)
                return;

            DragAndDropLock.setDragAndDropStarted(false);

            Transferable transferable = dsde.getDragSourceContext().getTransferable();
            if (transferable.isDataFlavorSupported(ToolWindowTrasferable.TOOL_WINDOW_DATA_FAVLOR)) {
                // Do action
                if (lastOverCmp != null) {
                    // Clear border
                    lastOverCmp.setBorder(oldBorder);

                    if (moveAnchor) {
                        // Move tool to another anchor
                        ToolWindow destToolWindow = (ToolWindow) lastOverCmp.getClientProperty(ToolWindow.class);
                        assert destToolWindow != null;

                        ToolWindowAnchor anchor = destToolWindow.getAnchor();

                        boolean oldAM = toolWindow.isAggregateMode();
                        try {
                            toolWindow.setAggregateMode(true);
                            toolWindow.setAnchor(anchor,
                                                 ((MyDoggyToolWindow) destToolWindow).getDescriptor().getLabelIndex());
                        } finally {
                            toolWindow.setAggregateMode(oldAM);
                        }
                    } else {
                        // Add the tool as tab
                        ToolWindow target = (ToolWindow) ((JComponent) SwingUtil.getParent(lastOverCmp, "toolWindow.container")).getClientProperty(
                                ToolWindow.class
                        );
                        target.addToolWindowTab(toolWindow).setSelected(true);
                    }
                }
            } else if (transferable.isDataFlavorSupported(ToolWindowTabTrasferable.TOOL_WINDOW_TAB_DATA_FAVLOR)) {
                if (lastOverCmp != null) {
                    // Clear border
                    lastOverCmp.setBorder(oldBorder);

                    try {
                        MyDoggyToolWindowTab toolWindowTab = (MyDoggyToolWindowTab) transferable.getTransferData(ToolWindowTabTrasferable.TOOL_WINDOW_TAB_DATA_FAVLOR);
                        toolWindow.removeToolWindowTab(toolWindowTab);

                        ToolWindow toolWindow = toolWindowTab.getToolWindow();
                        if (moveAnchor) {
                            // Move tool to another anchor
                            ToolWindow destToolWindow = (ToolWindow) lastOverCmp.getClientProperty(ToolWindow.class);
                            assert destToolWindow != null;

                            ToolWindowAnchor anchor = destToolWindow.getAnchor();

                            boolean oldAM = toolWindow.isAggregateMode();
                            try {
                                toolWindow.setAggregateMode(true);
                                toolWindow.setAnchor(anchor,
                                                     ((MyDoggyToolWindow) destToolWindow).getDescriptor().getLabelIndex());
                                toolWindow.aggregate();
                                toolWindow.setActive(true);
                            } finally {
                                toolWindow.setAggregateMode(oldAM);
                            }
                        } else {
                            // Add the tool as tab
                            ToolWindow target = (ToolWindow) ((JComponent) SwingUtil.getParent(lastOverCmp, "toolWindow.container")).getClientProperty(
                                    ToolWindow.class
                            );
                            target.addToolWindowTab(toolWindow).setSelected(true);
                        }

                    } catch (UnsupportedFlavorException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Finalize drag action...
            GlassPanel glassPane = descriptor.getManager().getGlassPanel();

            Point p = (Point) dsde.getLocation().clone();
            SwingUtilities.convertPointFromScreen(p, glassPane);

            glassPane.setDraggingImage(null);
            glassPane.setVisible(false);

            ghostImage = null;

            DragAndDropLock.setLocked(false);
            SwingUtilities.getWindowAncestor(descriptor.getManager()).repaint();
        }

    }

}
