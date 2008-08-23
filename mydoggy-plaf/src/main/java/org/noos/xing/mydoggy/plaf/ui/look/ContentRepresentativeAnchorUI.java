package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.Dockable;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.animation.AbstractAnimation;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentRepresentativeAnchor;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.drag.RepresentativeAnchorDragGesture;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.MutableColor;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLabelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

/**
 * @author Angelo De Caro
 */
public class ContentRepresentativeAnchorUI extends MetalLabelUI implements Cleaner {


    public static ComponentUI createUI(JComponent c) {
        return new ContentRepresentativeAnchorUI();
    }


    protected ContentRepresentativeAnchor contentRepresentativeAnchor;

    protected LineBorder labelBorder;

    protected DockableDescriptor descriptor;
    protected Dockable dockable;

    protected RepresentativeAnchorMouseAdapter adapter;

    protected Timer flashingTimer;
    protected int flasingDuration;
    protected boolean flashingState;
    protected MutableColor flashingAnimBackStart;
    protected MutableColor flashingAnimBackEnd;
    protected AbstractAnimation flashingAnimation;

    
    public ContentRepresentativeAnchorUI() {
    }


    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();

        if ("flash".equals(propertyName)) {
            if (e.getNewValue() == Boolean.TRUE) {
                if (descriptor.isAvailable()) {
                    flasingDuration = -1;
                    SwingUtil.repaint(contentRepresentativeAnchor);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(contentRepresentativeAnchor);
                }
            }
        } else if ("flash.duration".equals(propertyName)) {
            if (e.getNewValue() == Boolean.TRUE) {
                if (descriptor.isAvailable()) {
                    flasingDuration = (Integer) e.getNewValue();
                    SwingUtil.repaint(contentRepresentativeAnchor);
                }
            } else {
                if (flashingTimer != null) {
                    flashingTimer.stop();
                    flashingTimer = null;
                    SwingUtil.repaint(contentRepresentativeAnchor);
                }
            }
        }
    }

    public void cleanup() {
        uninstallUI(contentRepresentativeAnchor);
    }


    public void installUI(JComponent c) {
        // Init fields
        this.contentRepresentativeAnchor = (ContentRepresentativeAnchor) c;

        this.descriptor = contentRepresentativeAnchor.getContentDescriptor();
        this.dockable = descriptor.getDockable();

        this.flashingAnimation = new GradientAnimation();
        this.flashingAnimBackStart = new MutableColor(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
        this.flashingAnimBackEnd = new MutableColor(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
        this.flasingDuration = -1;

        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Release timers
        if (flashingTimer != null)
            flashingTimer.stop();
        flashingTimer = null;

        // Finalize
        this.descriptor = null;
        this.dockable = null;
        this.contentRepresentativeAnchor = null;
    }


    public void update(Graphics g, JComponent c) {
        c.setForeground(UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND));

        if (dockable.isFlashing() && descriptor.isAvailable()) {
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
                            dockable.setFlashing(false);
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
                         UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                         UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                         false,
                         false);
        }

        paint(g, c);
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

        SwingUtil.registerDragGesture(c, new RepresentativeAnchorDragGesture(descriptor, c));

        dockable.addPropertyChangeListener(this);
        descriptor.getCleaner().addCleaner(this);
    }

    protected void installDefaults(JLabel c) {
        labelBorder = new LineBorder(UIManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER), 1, true, 3, 3);

        c.setBorder(labelBorder);
        c.setForeground(UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND));

        SwingUtil.installFont(c, "ToolWindowRepresentativeAnchorUI.font");
    }

    protected void uninstallListeners(JLabel c) {
        super.uninstallListeners(c);

        dockable.removePropertyChangeListener(this);
        c.removeMouseListener(adapter);
        c.removeMouseMotionListener(adapter);
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
            g.setColor(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
            g.fillRect(0, 0, r.width, r.height);
        }
    }


    public class GradientAnimation extends AbstractAnimation {

        public GradientAnimation() {
            super(600f);
        }

        protected float onAnimating(float animationPercent) {
            switch (getAnimationDirection()) {
                case INCOMING:
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackStart,
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackEnd,
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                                                      animationPercent);
                    break;

                case OUTGOING:
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackStart,
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START),
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      animationPercent);
                    GraphicsUtil.getInterpolatedColor(flashingAnimBackEnd,
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END),
                                                      UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE),
                                                      animationPercent);
                    break;
            }
            SwingUtil.repaint(contentRepresentativeAnchor);
            return animationPercent;
        }

        protected void onFinishAnimation() {
            switch (getAnimationDirection()) {
                case INCOMING:
                    flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
                    break;
                case OUTGOING:
                    flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START));
                    break;
            }
            SwingUtil.repaint(contentRepresentativeAnchor);
        }

        protected void onHide(Object... params) {
            flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START));
            flashingAnimBackEnd.setRGB(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END));
        }

        protected void onShow(Object... params) {
            flashingAnimBackStart.setRGB(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
            flashingAnimBackEnd.setRGB(UIManager.getColor(MyDoggyKeySpace.RAB_BACKGROUND_INACTIVE));
        }

        protected void onStartAnimation(Direction direction) {
        }

        protected Direction chooseFinishDirection(Type type) {
            return (type == Type.SHOW) ? Direction.OUTGOING : Direction.INCOMING;
        }
    }

    public class RepresentativeAnchorMouseAdapter extends MouseInputAdapter {

        public RepresentativeAnchorMouseAdapter() {
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dockable.setMinimized(false);
            } else if (SwingUtilities.isRightMouseButton(e)) {
            }

            contentRepresentativeAnchor.setBorder(labelBorder);
            labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));
            SwingUtil.repaint(contentRepresentativeAnchor);
        }

        public void mouseEntered(MouseEvent e) {
            Component source = e.getComponent();

            labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.RAB_MOUSE_IN_BORDER));
            SwingUtil.repaint(source);
        }

        public void mouseExited(MouseEvent e) {
            Component source = e.getComponent();

            labelBorder.setLineColor(UIManager.getColor(MyDoggyKeySpace.RAB_MOUSE_OUT_BORDER));
            SwingUtil.repaint(source);
        }

        public void mouseDragged(MouseEvent e) {
        }

    }

}