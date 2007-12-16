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
    protected JFrame frame;
    protected JFileChooser fileChooser;
    protected Preferences preferences;
    protected LensPanel lensPanel;
    protected PreviewPanel previewPanel;

    protected Rectangle boundsToShot;


    public MagnifierAction(JFrame frame) {
        super("Magnifier");
        this.frame = frame;

        this.lensPanel = new LensPanel();

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(new Character('m'), InputEvent.CTRL_MASK));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                switch (e.getID()) {
                    case KeyEvent.KEY_TYPED:
                        if (e.isControlDown() && e.getKeyChar() == '\011')
                            MagnifierAction.this.actionPerformed(null);
                        break;
                }
                return false;
            }
        });
    }


    public void actionPerformed(ActionEvent e) {
        lensPanel.mount();
        SwingUtil.repaint(frame);
    }


    protected class LensPanel extends JPanel {
        protected JLayeredPane layeredPane;
        protected boolean restore;

        public LensPanel() {
            setOpaque(false);

            FloatingMoveMouseInputHandler floatingMoveMouseInputHandler = new FloatingMoveMouseInputHandler(this);
            addMouseListener(floatingMoveMouseInputHandler);
            addMouseMotionListener(floatingMoveMouseInputHandler);
            addMouseListener(new PopupMouseInput());

            layeredPane = frame.getLayeredPane();
        }

        public void mount() {
            int x = (frame.getWidth() / 2) - 50;
            int y = (frame.getHeight() / 2) - 50;

            setBounds(x, y, 200, 200);

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

                g.setColor(Color.BLACK);

                Rectangle bounds = new Rectangle(154, 154, 42, 42);
                Point location = bounds.getLocation();
                SwingUtilities.convertPointToScreen(location, this);
                bounds.setLocation(location);

                Robot robot = new Robot();
                BufferedImage image = robot.createScreenCapture(bounds);


                g.drawOval(0, 0, 150, 150);
                g.drawOval(1, 1, 148, 148);
                g.drawOval(2, 2, 146, 146);
                g.drawOval(3, 3, 144, 144);

                g.drawOval(150, 150, 50, 50);
                g.drawOval(151, 151, 48, 48);
                g.drawOval(152, 152, 46, 46);
                g.drawOval(153, 153, 44, 44);

                Shape oldClip = g.getClip();
                g.setClip(new Ellipse2D.Double(4, 4, 142, 142));

                image = GraphicsUtil.scale(image, 142, 142);

                g.drawImage(image, 0, 0, this);

                g.setClip(oldClip);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                scratchGraphics.dispose();
            }
        }

        protected class PopupMouseInput extends MouseAdapter implements ActionListener {
            protected JPopupMenu popupMenu;

            public PopupMouseInput() {
                popupMenu = new JPopupMenu();

                JMenuItem close = new JMenuItem("Close");
                close.setActionCommand("close");
                close.addActionListener(this);

                popupMenu.add(close);
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