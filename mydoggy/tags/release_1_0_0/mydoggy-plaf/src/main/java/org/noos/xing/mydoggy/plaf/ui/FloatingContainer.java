package org.noos.xing.mydoggy.plaf.ui;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.layout.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.modal.JModalWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro
 */
public class FloatingContainer extends DockedContainer {
    private JModalWindow window;

    private FloatingResizeMouseInputHandler resizeMouseInputHandler;
    private FloatingMoveMouseInputHandler moveMouseInputHandler;
    private boolean settedListener = false;

    private Rectangle lastBounds;

    private FloatingAnimation floatingAnimation;

    public FloatingContainer(ToolWindowDescriptor descriptor) {
        super(descriptor);

        initFloatingComponents();
        initFloatingListeners();
    }


    public void setVisible(boolean visible) {
        if (visible) {
            configureFloatingIcons();

            window.getContentPane().removeAll();

            Component content = getContentContainer();
            content.setVisible(true);
            window.getContentPane().add(content, "1,1,FULL,FULL");

            if (lastBounds == null) {
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

                // Set Size
                if (typeDescriptor.getSize() == null) {
                    Window windowAnchestor = descriptor.getWindowAnchestor();
                    window.setSize(windowAnchestor.getWidth() / 2, (int) (windowAnchestor.getHeight() / 1.5));
                } else {
                    window.setSize(typeDescriptor.getSize());
                }

                // Set Location
                if (typeDescriptor.getLocation() == null) {
                    if (content.getX() == 0 || content.getY() == 0)
                        SwingUtil.centrePositionOnScreen(window);
                } else
                    window.setLocation(typeDescriptor.getLocation());
            } else {
                window.setBounds(lastBounds);
                lastBounds = null;
            }

            applicationBarTitle.setIcon(toolWindow.getIcon());

            floatingAnimation.show();
        } else {
            lastBounds = window.getBounds();
            applicationBarTitle.setIcon(null);

            floatingAnimation.hide();
        }
    }


    private void initFloatingComponents() {
        window = new JModalWindow(descriptor.getWindowAnchestor(), null, false);
//                new JWindow(descriptor.getWindowAnchestor());

        JPanel contentPane = new JPanel(new ExtendedTableLayout(new double[][]{{1, TableLayout.FILL, 1}, {1, TableLayout.FILL, 1}}));
        contentPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        window.setContentPane(contentPane);

        resizeMouseInputHandler = new FloatingResizeMouseInputHandler(window);
        moveMouseInputHandler = new FloatingMoveMouseInputHandler(window, applicationBarTitle);
        floatingAnimation = new FloatingAnimation();
    }

    private void initFloatingListeners() {
        addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                assert"type".equals(evt.getPropertyName());
                if (evt.getNewValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING_FREE) {
                    window.removeMouseMotionListener(resizeMouseInputHandler);
                    window.removeMouseListener(resizeMouseInputHandler);

                    applicationBarTitle.removeMouseMotionListener(moveMouseInputHandler);
                    applicationBarTitle.removeMouseListener(moveMouseInputHandler);

                    window.addMouseMotionListener(resizeMouseInputHandler);
                    window.addMouseListener(resizeMouseInputHandler);

                    applicationBarTitle.addMouseMotionListener(moveMouseInputHandler);
                    applicationBarTitle.addMouseListener(moveMouseInputHandler);
                    settedListener = true;
                } else {
                    if (settedListener)
                        lastBounds = window.getBounds();
                    window.removeMouseMotionListener(resizeMouseInputHandler);
                    window.removeMouseListener(resizeMouseInputHandler);

                    applicationBarTitle.removeMouseMotionListener(moveMouseInputHandler);
                    applicationBarTitle.removeMouseListener(moveMouseInputHandler);
                    settedListener = false;
                }
            }
        });
        
        addPropertyChangeListener("location", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (window.isVisible()) {
                    Point location = (Point) evt.getNewValue();
                    window.setLocation(location);
                }
            }
        });
        addPropertyChangeListener("size", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (window.isVisible()) {
                    Dimension size = (Dimension) evt.getNewValue();
                    window.setSize(size);
                }
            }
        });
        addPropertyChangeListener("modal", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (window.isVisible()) {
                    window.setModal((Boolean) evt.getNewValue());
                }
            }
        });

        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
        typeDescriptor.addPropertyChangeListener(this);

        new TransparencyListener(this, descriptor, window);
    }


    private void configureFloatingIcons() {
        FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

        if (typeDescriptor.isModal()) {
            setPinVisible(false);
            setFloatingVisible(false);
            setDockedVisible(false);
        } else {
            setPinVisible(true);
            setFloatingVisible(true);
            setDockedVisible(false);
            setFix();
        }
    }


    private class FloatingAnimation implements ActionListener {
        private final Object LOCK = new Object();

        public static final int INCOMING = 1;
        public static final int OUTGOING = -1;
        public static final float ANIMATION_DURATION = 80f;
        public static final int ANIMATION_SLEEP = 1;

        private boolean animating;
        private int animationDirection;
        private Timer animationTimer;
        private long animationStart;

        private Rectangle originalBounds;

        private int lastLenX = 0;
        private int lastLenY = 0;

        public void actionPerformed(ActionEvent e) {
            if (animating) {
                // calculate height to show
                float animationPercent = (System.currentTimeMillis() - animationStart) / ANIMATION_DURATION;
                animationPercent = Math.min(1.0f, animationPercent);

                synchronized(LOCK) {
                    int animatingLengthX = (int) (animationPercent * originalBounds.width);
                    int animatingLengthY = (int) (animationPercent * originalBounds.height);
                    if (animationDirection == INCOMING) {
                        window.setLocation(
                                window.getX() - (animatingLengthX / 2 - lastLenX / 2),
                                window.getY() - (animatingLengthY / 2 - lastLenY / 2)
                        );
                        window.setSize(
                                window.getWidth() + (animatingLengthX - lastLenX),
                                window.getHeight() + (animatingLengthY - lastLenY)
                        );
                    } else {
                        window.setLocation(
                                window.getX() + (animatingLengthX / 2 - lastLenX / 2),
                                window.getY() + (animatingLengthY / 2 - lastLenY / 2)
                        );
                        window.setSize(
                                window.getWidth() - (animatingLengthX - lastLenX),
                                window.getHeight() - (animatingLengthY - lastLenY)
                        );
                    }

    //                window.validate();
    //                window.repaint();

                    lastLenX = animatingLengthX;
                    lastLenY = animatingLengthY;
                    if (animationPercent >= 1.0f) {
                        stopAnimation();
                        finishAnimation();
                    }
                }
            }
        }


        public void show() {
            synchronized(LOCK) {
                if (animating) {
                    stopAnimation();
                    animationDirection = OUTGOING;
                    finishAnimation();
                }

                this.originalBounds = window.getBounds();
                window.setBounds(new Rectangle(originalBounds.x + (originalBounds.width / 2),
                                               originalBounds.y + (originalBounds.height / 2),
                                               0, 0));

                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);
                window.setModal(typeDescriptor.isModal());
                window.setVisible(true);
                window.getContentPane().setVisible(true);

                startAnimation(INCOMING);
            }
        }

        public void hide() {
            synchronized(LOCK) {
                if (animating) {
                    stopAnimation();
                    animationDirection = INCOMING;
                    finishAnimation();
                }

                this.originalBounds = window.getBounds();
                window.getContentPane().setVisible(false);

                startAnimation(OUTGOING);
            }
        }


        private void startAnimation(int incoming) {
            if (!animating) {
                lastLenX = 0;
                lastLenY = 0;

                animationDirection = incoming;
                // start floatingAnimation timer
                animationStart = System.currentTimeMillis();
                if (animationTimer == null)
                    animationTimer = new Timer(ANIMATION_SLEEP, this);
                animating = true;
                animationTimer.start();
            }
        }

        private void stopAnimation() {
            animationTimer.stop();
            animating = false;
        }

        private void finishAnimation() {
            switch (animationDirection) {
                case INCOMING:
                    window.setBounds(originalBounds);
                    SwingUtil.repaint(window);
                    break;
                case OUTGOING:
                    window.getContentPane().setVisible(true);
                    window.setVisible(false);
                    window.setBounds(originalBounds);
                    break;
            }
        }
    }

}
