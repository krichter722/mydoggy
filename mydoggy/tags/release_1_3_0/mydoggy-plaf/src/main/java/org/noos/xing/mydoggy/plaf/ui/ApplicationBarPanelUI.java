package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.ui.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragAndDropLock;
import org.noos.xing.mydoggy.plaf.ui.drag.ToolWindowTrasferable;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class ApplicationBarPanelUI extends PanelUI {
    private ToolWindow toolWindow;
    private ToolWindowDescriptor descriptor;

    private Color backStartEnabled;
    private Color backEndEnabled;
    private Color backStartDisabled;
    private Color backEndDisabled;

    private MutableColor animBackStart;
    private MutableColor animBackEnd;
    private MutableColor animTextColor;

    private JComponent panel;

    private GradientAnimation animation;

    private Timer flashingTimer;
    private int flasingDuration;
    private boolean flashingState;

    public ApplicationBarPanelUI(ToolWindowDescriptor descriptor, DockedContainer dockedContainer) {
        this.descriptor = descriptor;
        this.toolWindow = descriptor.getToolWindow();

        dockedContainer.addPropertyChangeListener("active", new GradientActivationListener(descriptor));

        backStartEnabled = new Color(145, 181, 255);
        backEndEnabled = new Color(96, 123, 183);

        backStartDisabled = new Color(193, 189, 182);
        backEndDisabled = new Color(167, 164, 157);

        animBackStart = new MutableColor(backStartDisabled);
        animBackEnd = new MutableColor(0, 0, 0);
        animTextColor = new MutableColor(0, 0, 0);

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
                updateInternal(g, c, backStartEnabled, backEndEnabled, Colors.lightBlu, Color.BLACK);
            else
                updateInternal(g, c, backStartDisabled, backEndDisabled, Color.LIGHT_GRAY, Color.GRAY);

            if (flashingTimer == null) {
                flashingTimer = new Timer(500, new ActionListener() {
                    long start = 0;

                    public void actionPerformed(ActionEvent e) {
                        if (start == 0)
                            start = System.currentTimeMillis();

                        flashingState = !flashingState;
                        SwingUtil.repaint(panel);

                        if (flasingDuration != -1 && System.currentTimeMillis() - start > flasingDuration)
                            toolWindow.setFlashing(false);
                    }
                });
            }
            if (!flashingTimer.isRunning()) {
                flashingTimer.start();
            }
        } else {
            if (animation.isAnimating()) {
                updateInternal(g, c, animBackStart, animBackEnd, Color.LIGHT_GRAY, animTextColor);
            } else if (c.isEnabled()) {
                updateInternal(g, c, backStartEnabled, backEndEnabled, Colors.lightBlu, Color.BLACK);
            } else {
                updateInternal(g, c, backStartDisabled, backEndDisabled, Color.LIGHT_GRAY, Color.GRAY);
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

    protected void updateInternal(Graphics g, JComponent c, Color c1, Color c2, Color c3, Color c4) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        GraphicsUtil.fillRect(g, r, c1, c2,
                              null, GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

        String id = descriptor.getToolWindow().getId();
        r.width = g.getFontMetrics().stringWidth(id) + 8;

        int halfHeigh = (r.height / 2);
        GraphicsUtil.fillRect(g, r, Color.WHITE, c3,
                              new Polygon(new int[]{r.x, r.x + r.width - halfHeigh, r.x + r.width - halfHeigh, r.x},
                                          new int[]{r.y, r.y, r.y + r.height, r.y + r.height},
                                          4),
                              GraphicsUtil.UP_TO_BOTTOM_GRADIENT);

        GraphicsUtil.fillRect(g, r, Color.WHITE, c3,
                              new Arc2D.Double(r.x + r.width - r.height,
                                               r.y, r.height, r.height, -90.0d, 180.0d, Arc2D.CHORD),
                              GraphicsUtil.UP_TO_BOTTOM_GRADIENT);


        g.setColor(c4);
        g.drawString(id, r.x + 2, r.y + g.getFontMetrics().getAscent());
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
                    if (animBackStart.equals(backStartEnabled))
                        animation.hide();
                } else {
                    if (animBackStart.equals(backStartDisabled))
                        animation.show();
                }
            }
        }
    }

    private class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(300f);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    GraphicsUtil.getInterpolatedColor(animBackStart, backStartEnabled, backStartDisabled, animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd, backEndEnabled, backEndDisabled, animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor, Color.BLACK, Color.GRAY, animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(animBackStart, backStartDisabled, backStartEnabled, animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd, backEndDisabled, backEndEnabled, animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor, Color.GRAY, Color.BLACK, animationPercent);
                    break;
            }
            SwingUtil.repaint(panel);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    animBackStart.setRGB(backStartEnabled);
                    break;
                case OUTGOING:
                    animBackStart.setRGB(backStartDisabled);
                    break;
            }
            SwingUtil.repaint(panel);
        }

        protected void onHide(Object... params) {
            animBackStart.setRGB(backStartEnabled);
            animBackEnd.setRGB(backEndEnabled);
        }

        protected void onShow(Object... params) {
            animBackStart.setRGB(backStartDisabled);
            animBackEnd.setRGB(backEndDisabled);
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
            GlassPanel glassPane = (GlassPanel) SwingUtilities.getRootPane(descriptor.getManager()).getGlassPane();

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

            GlassPanel glassPane = (GlassPanel) descriptor.getManager().getRootPane().getGlassPane();

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

            GlassPanel glassPane = (GlassPanel) SwingUtilities.getRootPane(descriptor.getManager()).getGlassPane();

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
