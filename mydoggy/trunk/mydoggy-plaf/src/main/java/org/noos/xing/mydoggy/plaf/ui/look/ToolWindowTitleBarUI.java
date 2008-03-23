package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowTab;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.ui.DockedContainer;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureInitiator;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

        animBackStart = new MutableColor(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START));
        animBackEnd = new MutableColor(0, 0, 0);
        animTextColor = new MutableColor(0, 0, 0);

        flashingAnimation = new GradientAnimation(700f);

        animation = new GradientAnimation();

        descriptor.getToolWindow().addPlafPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String propertyName = e.getPropertyName();
                if ("flash".equals(propertyName)) {
                    if (e.getNewValue() == Boolean.TRUE) {
                        if (toolWindow.isVisible()) {
                            flasingDuration = -1;
                            SwingUtil.repaint(panel);
                        }
                    } else {
                        if (flashingTimer != null) {
                            flashingTimer.stop();
                            flashingTimer = null;
                            SwingUtil.repaint(panel);
                        }
                    }
                } else if ("flash.duration".equals(propertyName)) {
                    if (e.getNewValue() == Boolean.TRUE) {
                        if (toolWindow.isVisible()) {
                            flasingDuration = (Integer) e.getNewValue();
                            SwingUtil.repaint(panel);
                        }
                    } else {
                        if (flashingTimer != null) {
                            flashingTimer.stop();
                            flashingTimer = null;
                            SwingUtil.repaint(panel);
                        }
                    }
                } else if ("active".equals(propertyName)) {
                    toolWindow.setFlashing(false);
                }
            }
        });
    }


    public void installUI(JComponent c) {
        super.installUI(c);
        installDefaults(c);
        this.panel = c;

        // Drag Gesture
        final ToolWindowTitleBarDragGesture dragGesture = new ToolWindowTitleBarDragGesture(descriptor);
        SwingUtil.registerDragGesture(c, dragGesture);
        panel.addContainerListener(new ContainerAdapter() {
            public void componentAdded(ContainerEvent e) {
                if (e.getChild() instanceof DragGestureInitiator) {
                    DragGestureInitiator dragGestureInitiator = (DragGestureInitiator) e.getChild();
                    dragGestureInitiator.setDragGesture(dragGesture);
                }
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
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ACTIVE),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE));
            } else {
                updateToolWindowTitleBar(g, c,
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START),
                                         resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END),
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

        if (descriptor.isIdVisibleOnTitleBar()) {
            String id = resourceManager.getUserString(descriptor.getToolWindow().getId());
            r.width = g.getFontMetrics().stringWidth(id) + 8;

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
            g.drawString(id,
                         r.x + 2,
                         r.y + ((r.height - g.getFontMetrics().getHeight()) / 2) + g.getFontMetrics().getAscent());
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
                    if (animBackStart.equals(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START)))
                        animation.hide();
                } else {
                    if (animBackStart.equals(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START)))
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
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(animBackStart,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END),
                                                      resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
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
                    animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START));
                    break;
                case OUTGOING:
                    animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START));
                    break;
            }
            SwingUtil.repaint(panel);
        }

        protected void onHide(Object... params) {
            animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START));
            animBackEnd.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END));
        }

        protected void onShow(Object... params) {
            animBackStart.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START));
            animBackEnd.setRGB(resourceManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }

    }

    protected class ToolWindowTitleBarDragGesture extends DragGestureAdapter {
        protected BufferedImage ghostImage;
        protected JComponent lastOverCmp = null;
        protected Border oldBorder = null;

        protected LineBorder highligthBorder = new LineBorder(Color.BLUE, 3);

        protected boolean moveAnchor;
        protected ToolWindowAnchor lastAnchor;

        public ToolWindowTitleBarDragGesture(ToolWindowDescriptor descriptor) {
            super(descriptor);
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            // Check validaty
            if (toolWindow.getType() == ToolWindowType.FLOATING ||
                toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
                toolWindow.getType() == ToolWindowType.FLOATING_LIVE)
                return;

            // Acquire locks
            if (!acquireLocks())
                return;

            // Start Drag
            MyDoggyToolWindowTab toolWindowTab = null;
            if (SwingUtilities.isDescendingFrom(dge.getComponent(), panel)) {
                toolWindowTab = (MyDoggyToolWindowTab) SwingUtil.getParentClientProperty(dge.getComponent(), ToolWindowTab.class);
            }

            if (toolWindowTab != null && toolWindowTab.getDockableDelegator() != null) {
                // TDDO: change..this
                MyDoggyTransferable transferable = new MyDoggyTransferable(manager);
                transferable.addEntry(MyDoggyTransferable.TOOL_WINDOW_ID_DF, toolWindowTab.getDockableDelegator().getId());
                transferable.addEntry(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF, toolWindowTab.getId());

                dge.startDrag(Cursor.getDefaultCursor(), transferable, this);
            } else {
                dge.startDrag(Cursor.getDefaultCursor(),
                              new MyDoggyTransferable(manager, MyDoggyTransferable.TOOL_WINDOW_ID_DF, toolWindow.getId()),
                              this);
            }

            // Setup ghostImage
            if (!descriptor.isDragImageAvailable() || resourceManager.getBoolean("drag.icon.useDefault", false)) {
                setGhostImage(dge.getDragOrigin(),
                              resourceManager.getBufferedImage(MyDoggyKeySpace.DRAG));
            } else {
                Component contentContainer = descriptor.getComponentForDragImage();
                BufferedImage ghostImage = new BufferedImage(contentContainer.getWidth(),
                                                             contentContainer.getHeight(), BufferedImage.TYPE_INT_RGB);
                contentContainer.print(ghostImage.getGraphics());
                ghostImage = GraphicsUtil.scale(ghostImage,
                                                contentContainer.getWidth() / 3,
                                                contentContainer.getHeight() / 3);

                setGhostImage(dge.getDragOrigin(), ghostImage);
            }

            lastAnchor = null;
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;

            // Obtain anchor for location
            ToolWindowAnchor newAnchor = manager.getToolWindowAnchor(
                    SwingUtil.convertPointFromScreen(dsde.getLocation(), manager)
            );

            if (newAnchor != lastAnchor) {
                if (newAnchor == null) {
                    manager.getBar(lastAnchor).setTempShowed(false);
                } else {
                    if (manager.getBar(newAnchor).getAvailableTools() == 0)
                        manager.getBar(newAnchor).setTempShowed(true);
                }

                lastAnchor = newAnchor;
            }
            updateGhostImage(dsde.getLocation());
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocks();

            manager.setTempShowed(false);

            // Finalize drag action...
            cleanupGhostImage();
        }

    }

}
