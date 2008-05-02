package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.prefs.Preferences;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MagnifierAction extends AbstractAction {
    protected Component parentComponent;
    protected JFileChooser fileChooser;
    protected Preferences preferences;
    protected LensPanel lensPanel;
    protected PreviewPanel previewPanel;

    protected Rectangle boundsToShot;


    public MagnifierAction(Component parentComponent) {
        super("Magnifier");
        this.parentComponent = parentComponent;

        this.lensPanel = new LensPanel();

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(new Character('m'), InputEvent.CTRL_MASK));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                switch (e.getID()) {
                    case KeyEvent.KEY_TYPED:
                        if (e.isControlDown() && e.getKeyChar() == 13)
                            MagnifierAction.this.actionPerformed(null);
                        break;
                }
                return false;
            }
        });
    }


    public void actionPerformed(ActionEvent e) {
        lensPanel.mount();
        SwingUtil.repaint(parentComponent);
    }


    protected class LensPanel extends JPanel {
        protected JLayeredPane layeredPane;
        protected boolean restore;
        protected Ellipse2D bigLen = new Ellipse2D.Double(3,3,144,144);
        protected Ellipse2D smallLen = new Ellipse2D.Double(125,125,40,40);
        protected int zoomLevel;
        protected int lensPosition;

        public LensPanel() {
            setOpaque(false);

            FloatingMoveMouseInputHandler floatingMoveMouseInputHandler = new FloatingMoveMouseInputHandler(this) {
                public void mouseReleased(MouseEvent ev) {
                    super.mouseReleased(ev);
                    SwingUtil.repaint(LensPanel.this);
                }
            };
            addMouseListener(floatingMoveMouseInputHandler);
            addMouseMotionListener(floatingMoveMouseInputHandler);
            LensMouseInput lensMouseInput = new LensMouseInput();
            addMouseListener(lensMouseInput);
            addMouseWheelListener(lensMouseInput);

            layeredPane = ((RootPaneContainer)parentComponent).getLayeredPane();

            lensPosition = 0;
            zoomLevel = 0;
        }


        protected void processMouseEvent(MouseEvent e) {
            switch (e.getID()) {
                case MouseEvent.MOUSE_PRESSED:
                case MouseEvent.MOUSE_CLICKED:
                    if (bigLen.contains(e.getX(), e.getY()) ||
                        smallLen.contains(e.getX(), e.getY()))
                        super.processMouseEvent(e);
                    break;
                default:
                    super.processMouseEvent(e);
            }
        }

        protected void processMouseWheelEvent(MouseWheelEvent e) {
            switch (e.getID()) {
                case MouseEvent.MOUSE_WHEEL:
                    if (bigLen.contains(e.getX(), e.getY()) ||
                        smallLen.contains(e.getX(), e.getY()))
                        super.processMouseWheelEvent(e);
                    break;
                default:
                    super.processMouseWheelEvent(e);
            }
        }

        public void mount() {
            int x = (parentComponent.getWidth() / 2) - 50;
            int y = (parentComponent.getHeight() / 2) - 50;

            setBounds(x, y, 171, 171);

            layeredPane.remove(this);
            layeredPane.setLayer(this, JLayeredPane.DEFAULT_LAYER + 3);
            layeredPane.add(this);

            SwingUtil.repaint(layeredPane);
        }

        public void unmount() {
            layeredPane.remove(this);

            SwingUtil.repaint(layeredPane);
        }

        public void unmount(Runnable runnable) {
            layeredPane.remove(this);

            SwingUtil.repaint(layeredPane, runnable);
        }


        protected void paintComponent(Graphics g) {
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                Graphics2D g2D = (Graphics2D) g;
                g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle bounds = smallLen.getBounds();
                Point location = bounds.getLocation();
                SwingUtilities.convertPointToScreen(location, this);
                bounds.setLocation(location);

                Robot robot = new Robot();
                int extra = zoomLevel * 10;
 
                BufferedImage image = GraphicsUtil.scale(robot.createScreenCapture(bounds), 144 + extra, 144 + extra);

                g.setColor(Color.BLACK);
                switch (lensPosition) {
                    case 0 :
                        g.drawOval(0, 0, 150, 150);
                        g.drawOval(1, 1, 148, 148);
                        g.drawOval(2, 2, 146, 146);
                        g.drawOval(3, 3, 144, 144);

                        g.drawOval(120, 120, 50, 50);
                        g.drawOval(121, 121, 48, 48);
                        g.drawOval(122, 122, 46, 46);
                        Shape oldClip = g.getClip();
                        g.setClip(bigLen);
                        g.drawImage(image, 3 - extra / 2, 3 - extra / 2, this);
                        g.setClip(oldClip);
                        break;
                    case 1 :
                        g.drawOval(20, 0, 150, 150);
                        g.drawOval(21, 1, 148, 148);
                        g.drawOval(22, 2, 146, 146);
                        g.drawOval(23, 3, 144, 144);

                        g.drawOval(0, 120, 50, 50);
                        g.drawOval(1, 121, 48, 48);
                        g.drawOval(2, 122, 46, 46);
                        oldClip = g.getClip();
                        g.setClip(bigLen);
                        g.drawImage(image, 23 - extra / 2, 3 - extra / 2, this);
                        g.setClip(oldClip);
                        break;
                    case 2 :
                        g.drawOval(20, 20, 150, 150);
                        g.drawOval(21, 21, 148, 148);
                        g.drawOval(22, 22, 146, 146);
                        g.drawOval(23, 23, 144, 144);

                        g.drawOval(0, 0, 50, 50);
                        g.drawOval(1, 1, 48, 48);
                        g.drawOval(2, 2, 46, 46);
                        oldClip = g.getClip();
                        g.setClip(bigLen);
                        g.drawImage(image, 23 - extra / 2, 23 - extra / 2, this);
                        g.setClip(oldClip);
                        break;
                    case 3 :
                        g.drawOval(0, 20, 150, 150);
                        g.drawOval(1, 21, 148, 148);
                        g.drawOval(2, 22, 146, 146);
                        g.drawOval(3, 23, 144, 144);

                        g.drawOval(120, 0, 50, 50);
                        g.drawOval(121, 1, 48, 48);
                        g.drawOval(122, 2, 46, 46);
                        oldClip = g.getClip();
                        g.setClip(bigLen);
                        g.drawImage(image, 3 - extra / 2, 23 - extra / 2, this);
                        g.setClip(oldClip);
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                scratchGraphics.dispose();
            }
        }


        protected class LensMouseInput extends MouseAdapter implements ActionListener, MouseWheelListener {
            protected JPopupMenu popupMenu;

            public LensMouseInput() {
                popupMenu = new JPopupMenu();

                JMenuItem close = new JMenuItem("Close");
                close.setActionCommand("close");
                close.addActionListener(this);

                popupMenu.add(close);
            }

            public void mouseWheelMoved(MouseWheelEvent e) {
                if (bigLen.contains(e.getX(), e.getY())) {
                    zoomLevel += e.getWheelRotation();
                    if (zoomLevel < 0)
                        zoomLevel = 0;   
                    SwingUtil.repaint(LensPanel.this);
                } else if (smallLen.contains(e.getX(), e.getY())) {
                    // Change ellipse position...

                    lensPosition = (lensPosition + e.getWheelRotation()) % 4;
                    switch (lensPosition) {
                        case 0 :
                            bigLen.setFrame(3,3,144,144);
                            smallLen.setFrame(125,125,40,40);
                            break;
                        case 1 :
                            bigLen.setFrame(23,3,144,144);
                            smallLen.setFrame(5,125,40,40);
                            break;
                        case 2 :
                            bigLen.setFrame(23,23,144,144);
                            smallLen.setFrame(5,5,40,40);
                            break;
                        case 3 :
                            bigLen.setFrame(3,23,144,144);
                            smallLen.setFrame(125,5,40,40);
                            break;
                    }
                    SwingUtil.repaint(LensPanel.this);
                 }
             }

            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                if ("close".equals(actionCommand)) {
                    unmount();
                }
            }

            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }


}