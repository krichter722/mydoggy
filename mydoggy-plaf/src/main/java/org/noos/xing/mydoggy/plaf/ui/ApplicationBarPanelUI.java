package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import static org.noos.xing.mydoggy.plaf.ui.ToolWindowUI.*;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragAndDropLock;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowTrasferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.cmp.GlassPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabPanel;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class ApplicationBarPanelUI extends PanelUI {
    private ToolWindow toolWindow;
    private ToolWindowDescriptor descriptor;
    private ToolWindowUI toolWindowUI;

    private MutableColor animBackStart;
    private MutableColor animBackEnd;
    private MutableColor animTextColor;

    private JComponent panel;

    private GradientAnimation animation;

    private Timer flashingTimer;
    private int flasingDuration;
    private boolean flashingState;
    private AbstractAnimation flashingAnimation;

    public ApplicationBarPanelUI(ToolWindowDescriptor descriptor, DockedContainer dockedContainer) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();
        this.toolWindowUI = descriptor.getToolWindowUI();

        dockedContainer.addPropertyChangeListener("active", new GradientActivationListener(descriptor));

        animBackStart = new MutableColor(toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START));
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
                toolWindowUI.updateToolWindowAppBar(descriptor, g, c,
                                                    animBackStart, animBackEnd,
                                                    toolWindowUI.getColor(TW_APP_ID_BACKGROUND_FLASHING_0),
                                                    animTextColor);
            else
                toolWindowUI.updateToolWindowAppBar(descriptor, g, c,
                                                    animBackStart, animBackEnd,
                                                    toolWindowUI.getColor(TW_APP_ID_BACKGROUND_FLASHING_1),
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
                toolWindowUI.updateToolWindowAppBar(descriptor, g, c,
                                                    animBackStart, animBackEnd,
                                                    toolWindowUI.getColor(TW_APP_ID_BACKGROUND_ANIMATING),
                                                    animTextColor);
            } else if (c.isEnabled()) {
                toolWindowUI.updateToolWindowAppBar(descriptor, g, c,
                                                    toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_START),
                                                    toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_END),
                                                    toolWindowUI.getColor(TW_APP_ID_BACKGROUND_ACTIVE),
                                                    toolWindowUI.getColor(TW_APP_ID_FOREGROUND_ACTIVE));
            } else {
                toolWindowUI.updateToolWindowAppBar(descriptor, g, c,
                                                    toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START),
                                                    toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_END),
                                                    toolWindowUI.getColor(TW_APP_ID_BACKGROUND_INACTIVE),
                                                    toolWindowUI.getColor(TW_APP_ID_FOREGROUND_INACTIVE));
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


    private class GradientActivationListener implements PropertyChangeListener {
        public static final float ANIMATION_DURATION = 80f;
        public static final int ANIMATION_SLEEP = 10;

        private ToolWindowDescriptor descriptor;

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
                    if (animBackStart.equals(toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_START)))
                        animation.hide();
                } else {
                    if (animBackStart.equals(toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START)))
                        animation.show();
                }
            }
        }
    }

    private class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(300f);
        }

        private GradientAnimation(float animationDuration) {
            super(animationDuration);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    GraphicsUtil.getInterpolatedColor(animBackStart,
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_START),
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_END),
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      toolWindowUI.getColor(TW_APP_ID_FOREGROUND_ACTIVE),
                                                      toolWindowUI.getColor(TW_APP_ID_FOREGROUND_INACTIVE),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(animBackStart,
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START),
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_END),
                                                      toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      toolWindowUI.getColor(TW_APP_ID_FOREGROUND_INACTIVE),
                                                      toolWindowUI.getColor(TW_APP_ID_FOREGROUND_ACTIVE),
                                                      animationPercent);
                    break;
            }
            SwingUtil.repaint(panel);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    animBackStart.setRGB(toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_START));
                    break;
                case OUTGOING:
                    animBackStart.setRGB(toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START));
                    break;
            }
            SwingUtil.repaint(panel);
        }

        protected void onHide(Object... params) {
            animBackStart.setRGB(toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_START));
            animBackEnd.setRGB(toolWindowUI.getColor(TW_APP_BACKGROUND_ENABLED_END));
        }

        protected void onShow(Object... params) {
            animBackStart.setRGB(toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_START));
            animBackEnd.setRGB(toolWindowUI.getColor(TW_APP_BACKGROUND_DISABLED_END));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }

    }

    class DragGesture implements DragGestureListener, DragSourceMotionListener, DragSourceListener {
        private BufferedImage ghostImage;
        private JPanel lastToolWindowContainer = null;
        private Border oldBorder = null;

        private LineBorder highligthBorder = new LineBorder(Color.BLUE, 3);

        private ToolWindowAnchor lastAnchor;

        public void dragGestureRecognized(DragGestureEvent dge) {
            if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE)
                return;

            if (DragAndDropLock.isLocked()) {
                DragAndDropLock.setDragAndDropStarted(false);
                return;
            }
            DragAndDropLock.setLocked(true);
            DragAndDropLock.setDragAndDropStarted(true);

            // Start Drag
            dge.startDrag(Cursor.getDefaultCursor(), new ToolWindowTrasferable(toolWindow), this);

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

            Point p = (Point) dsde.getLocation().clone();
            SwingUtilities.convertPointFromScreen(p, glassPane);
            glassPane.setPoint(p);

            Container contentPane = descriptor.getManager().getRootPane().getContentPane();
            Component component = SwingUtilities.getDeepestComponentAt(contentPane, p.x, p.y);
            if (component != null) {
                if (lastToolWindowContainer != null)
                    lastToolWindowContainer.setBorder(oldBorder);

                lastToolWindowContainer = (JPanel) SwingUtil.getParent(component, "toolWindow.container");
                if (lastToolWindowContainer != null) {
                    oldBorder = lastToolWindowContainer.getBorder();
                    lastToolWindowContainer.setBorder(highligthBorder);
                }
            }

            p = (Point) dsde.getLocation().clone();
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

            if (lastToolWindowContainer != null) {
                lastToolWindowContainer.setBorder(oldBorder);

                String toolId = lastToolWindowContainer.getName().substring(21);
                ToolWindow destToolWindow = descriptor.getManager().getToolWindow(toolId);
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
            }

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
