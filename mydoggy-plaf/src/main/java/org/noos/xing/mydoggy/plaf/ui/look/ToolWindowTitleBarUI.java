package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowTab;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.PopupUpdater;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTabButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleBar;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class ToolWindowTitleBarUI extends PanelUI implements Cleaner,
                                                             PropertyChangeListener {

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowTitleBarUI();
    }


    protected ToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;

    protected MutableColor animBackStart;
    protected MutableColor animBackEnd;
    protected MutableColor animTextColor;

    protected ToolWindowTitleBar toolWindowTitleBar;
    protected ExtendedTableLayout toolWindowTitleBarLayout;
    protected MouseListener titleBarMouseAdapter;

    // Animation fields

    protected GradientAnimation animation;

    protected Timer flashingTimer;
    protected int flasingDuration;
    protected boolean flashingState;
    protected AbstractAnimation flashingAnimation;


    protected java.util.List<PopupUpdater> popupUpdaterList;


    public ToolWindowTitleBarUI() {
    }


    public void cleanup() {
        uninstallUI(toolWindowTitleBar);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("flash".equals(propertyName)) {
            if (evt.getNewValue() == Boolean.TRUE) {
                if (toolWindow.isVisible()) {
                    flasingDuration = -1;
                    SwingUtil.repaint(toolWindowTitleBar);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(toolWindowTitleBar);
                }
            }
        } else if ("flash.duration".equals(propertyName)) {
            if (evt.getNewValue() == Boolean.TRUE) {
                if (toolWindow.isVisible()) {
                    flasingDuration = (Integer) evt.getNewValue();
                    SwingUtil.repaint(toolWindowTitleBar);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(toolWindowTitleBar);
                }
            }
        } else if ("active".equals(evt.getPropertyName())) {
            if (evt.getSource() != descriptor || !toolWindow.isVisible())
                return;

            if (evt.getNewValue() == Boolean.FALSE) {
                if (animBackStart.equals(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START)))
                    animation.hide();
                else
                    animation.stop();
            } else {
                if (animBackStart.equals(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START)))
                    animation.show();
                else
                    animation.stop();
            }
        } else if ("idVisibleOnTitleBar".equals(propertyName)) {
            ToolWindowTypeDescriptor typeDescriptor = (ToolWindowTypeDescriptor) evt.getSource();

            if (typeDescriptor.getType().equals(toolWindow.getType())) {
                setIdOnTitleBarVisible((Boolean) evt.getNewValue());
            }
        } else if ("type".equals(propertyName)) {
            if (!ToolWindowType.EXTERN.equals(evt.getNewValue()))
                setIdOnTitleBarVisible(descriptor.getTypeDescriptor((ToolWindowType) evt.getNewValue()).isIdVisibleOnTitleBar());
        }
    }


    public void installUI(JComponent c) {
        // Setup field
        this.toolWindowTitleBar = (ToolWindowTitleBar) c;
        this.descriptor = toolWindowTitleBar.getToolWindowDescriptor();
        this.toolWindow = descriptor.getToolWindow();

        super.installUI(c);

        // Install
        installDefaults(c);
        installComponents();
        installListeners(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // uninstall
        uninstallDefaults(c);
        uninstallListeners(c);

        // Stop animations and timers
        flashingAnimation.stop();
        animation.stop();

        if (flashingTimer != null)
            flashingTimer.stop();

        // Reset Fields
        flashingTimer = null;
        toolWindowTitleBar = null;
        flashingAnimation = animation = null;
        toolWindow = null;
        descriptor = null;
    }


    public void paint(Graphics g, JComponent c) {
        Rectangle r = c.getBounds();
        r.x = r.y = 0;

        if (toolWindow.isFlashing()) {
            if (flashingState)
                updateToolWindowTitleBar(g, c,
                                         animBackStart, animBackEnd,
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_FLASHING_ON),
                                         animTextColor);
            else
                updateToolWindowTitleBar(g, c,
                                         animBackStart, animBackEnd,
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_FLASHING_OFF),
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
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ANIMATING),
                                         animTextColor);
            } else if (c.isEnabled()) {
                updateToolWindowTitleBar(g, c,
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_ACTIVE),
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE));
            } else {
                updateToolWindowTitleBar(g, c,
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START),
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END),
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_BACKGROUND_INACTIVE),
                                         UIManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE));
            }
        }

    }


    protected void installDefaults(JComponent c) {
        // Setup Colors
        animBackStart = new MutableColor(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START));
        animBackEnd = new MutableColor(0, 0, 0);
        animTextColor = new MutableColor(0, 0, 0);
        flashingAnimation = new GradientAnimation(700f);
        flasingDuration = -1;
        animation = new GradientAnimation();

        // Install default
        toolWindowTitleBar.setBorder(null);

        LookAndFeel.installColorsAndFont(c, "Panel.background", "Panel.foreground", "Panel.font");
        LookAndFeel.installBorder(c, "Panel.border");
        SwingUtil.installFont(c, "ToolWindowTitleBarUI.font");
    }

    protected void uninstallDefaults(JComponent c) {
        LookAndFeel.uninstallBorder(c);
    }

    protected void installListeners(JComponent c) {
        // Register cleaner
        descriptor.getCleaner().addCleaner(this);

        // Register Listeners
        descriptor.getToolWindow().addPlafPropertyChangeListener(this);
        descriptor.addTypeDescriptorChangePropertyListener(this);

        toolWindowTitleBar.addMouseListener(titleBarMouseAdapter = new TitleBarMouseAdapter(descriptor));

        // Register Drag Gesture
        ToolWindowTitleBarDragGesture dragGesture = new ToolWindowTitleBarDragGesture(descriptor);
        SwingUtil.registerDragGesture(toolWindowTitleBar, dragGesture);
        toolWindowTitleBar.getToolWindowTabPanel().setDragGesture(dragGesture);
    }

    protected void uninstallListeners(JComponent c) {
        // Remove listeners
        descriptor.getToolWindow().removePlafPropertyChangeListener(this);
        descriptor.removeTypeDescriptorChangePropertyListener(this);

        toolWindowTitleBar.removeMouseListener(titleBarMouseAdapter);
    }

    protected void installComponents() {
        toolWindowTitleBarLayout = new ExtendedTableLayout(new double[][]{{3, TableLayout.FILL, 2, -2, 3},
                                                                          {0, SwingUtil.getInt("ToolWindowTitleBarUI.length", 16), 0}}, false);

        toolWindowTitleBar.setLayout(toolWindowTitleBarLayout);
        toolWindowTitleBar.add(toolWindowTitleBar.getToolWindowTabPanel(), "1,1");
        toolWindowTitleBar.add(toolWindowTitleBar.getToolWindowTitleButtonPanel(), "3,1,right,c");

        setIdOnTitleBarVisible(true);
    }


    protected void setIdOnTitleBarVisible(boolean visible) {
        if (!visible)
            toolWindowTitleBarLayout.setColumn(0, 3);
        SwingUtil.repaint(toolWindowTitleBar);
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
            int columWidth = getTitleWidth(g);
            if (columWidth != toolWindowTitleBarLayout.getColumn(0)) {
                toolWindowTitleBarLayout.setColumn(0, columWidth);
                SwingUtil.revalidate(toolWindowTitleBar);
            }

            String id = SwingUtil.getUserString(descriptor.getToolWindow().getRepresentativeAnchorButtonTitle());
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

    protected int getTitleWidth(Graphics g) {
        return g.getFontMetrics().stringWidth(SwingUtil.getUserString(toolWindow.getRepresentativeAnchorButtonTitle())) + 12;
    }


    public class GradientAnimation extends AbstractAnimation {

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
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE),
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(animBackStart,
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START),
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animBackEnd,
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END),
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(animTextColor,
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_INACTIVE),
                                                      UIManager.getColor(MyDoggyKeySpace.TWTB_ID_FOREGROUND_ACTIVE),
                                                      animationPercent);
                    break;
            }
            SwingUtil.repaint(toolWindowTitleBar);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    animBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START));
                    break;
                case OUTGOING:
                    animBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START));
                    break;
            }
            SwingUtil.repaint(toolWindowTitleBar);
        }

        protected void onHide(Object... params) {
            animBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_START));
            animBackEnd.setRGB(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_ACTIVE_END));
        }

        protected void onShow(Object... params) {
            animBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_START));
            animBackEnd.setRGB(UIManager.getColor(MyDoggyKeySpace.TWTB_BACKGROUND_INACTIVE_END));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }

    }

    public class ToolWindowTitleBarDragGesture extends DragGestureAdapter {
        protected JComponent lastOverCmp = null;
        protected Border oldBorder = null;

        protected LineBorder highligthBorder = new LineBorder(Color.BLUE, 3);

        protected boolean moveAnchor;
        protected ToolWindowAnchor lastAnchor;


        public ToolWindowTitleBarDragGesture(ToolWindowDescriptor descriptor) {
            super(descriptor);
            descriptor.getCleaner().addCleaner(this);
        }


        public void dragGestureRecognized(DragGestureEvent dge) {
            super.dragGestureRecognized(dge);

            // Check validaty
            if ((toolWindow.getType() == ToolWindowType.FLOATING ||
                 toolWindow.getType() == ToolWindowType.FLOATING_FREE ||
                 toolWindow.getType() == ToolWindowType.FLOATING_LIVE) && !dge.getTriggerEvent().isShiftDown())
                return;

            // Acquire locks
            if (!acquireLocks())
                return;

            // if the source component a tab?
            MyDoggyToolWindowTab toolWindowTab = null;
            if (SwingUtilities.isDescendingFrom(dge.getComponent(), toolWindowTitleBar)) {
                ToolWindowTabButton toolWindowTabButton = SwingUtil.getParent(dge.getComponent(), ToolWindowTabButton.class);
                if (toolWindowTabButton != null)
                    toolWindowTab = (MyDoggyToolWindowTab) toolWindowTabButton.getToolWindowTab();
            }

            // Start Drag
            if (toolWindowTab != null && toolWindowTab.getDockableDelegator() != null) {
                // The source is a tab
                MyDoggyTransferable transferable = new MyDoggyTransferable(manager);
                transferable.addEntry(MyDoggyTransferable.TOOL_WINDOW_ID_DF, toolWindowTab.getDockableDelegator().getId());
                transferable.addEntry(MyDoggyTransferable.TOOL_WINDOW_TAB_ID_DF, toolWindowTab.getId());

                dge.startDrag(DragSource.DefaultMoveDrop,
                              transferable,
                              this);
            } else {
                // The source is the tool window
                dge.startDrag(DragSource.DefaultMoveDrop,
                              new MyDoggyTransferable(manager, MyDoggyTransferable.TOOL_WINDOW_ID_DF, toolWindow.getId()),
                              this);
            }

            // Setup ghost image
            if (!descriptor.isDragImageAvailable() || SwingUtil.getBoolean(MyDoggyKeySpace.DRAG_USE_DEFAULT_ICON, false)) {
                // load default ghost image
                setGhostImage(dge.getDragOrigin(),
                              SwingUtil.getImage(MyDoggyKeySpace.DRAG));
            } else {
                // extract a ghost image from the component
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
            ToolWindowAnchor newAnchor = manager.getToolWindowAnchor(dsde.getLocation());

            if (newAnchor != lastAnchor) {
                if (newAnchor == null) {
                    manager.getBar(lastAnchor).setTempShown(false);
                } else {
                    if (manager.getBar(newAnchor).getAvailableTools() == 0)
                        manager.getBar(newAnchor).setTempShown(true);
                }

                lastAnchor = newAnchor;
            }

            updateGhostImage(dsde.getLocation());

            updateDropTarget(dsde);
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocks();

            // Restore bars
            manager.setTempShown(false);

            // Clean ghost image
            cleanupGhostImage();

            // Finalize drag action...
            try {
                if (lastDropPanel != null) {
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

                        SwingUtil.convertPointFromScreen2(dsdeLocation, ancestor);
                        ToolWindow toolWindow = (ToolWindow) descriptor.getDockable();
                        toolWindow.getTypeDescriptor(FloatingLiveTypeDescriptor.class).setLocation(
                                dsdeLocation.x, dsdeLocation.y
                        );

                        // Move to floating live
                        toolWindow.setType(ToolWindowType.FLOATING_LIVE);
                    } else {
                        ToolWindow toolWindow = (ToolWindow) descriptor.getDockable();
                        toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).setLocation(
                                dsdeLocation.x, dsdeLocation.y
                        );

                        // Move to floating
                        toolWindow.setType(ToolWindowType.FLOATING);
                    }
                }
            } finally {
                // End dockable drop gesture..
                dockableDropDragExit();
            }
        }

    }

}
