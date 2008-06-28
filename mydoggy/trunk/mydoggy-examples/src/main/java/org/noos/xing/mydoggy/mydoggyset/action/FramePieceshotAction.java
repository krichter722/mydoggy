package org.noos.xing.mydoggy.mydoggyset.action;

import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingMoveMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.FloatingResizeMouseInputHandler;
import org.noos.xing.mydoggy.plaf.ui.translucent.TranslucentPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FramePieceshotAction extends AbstractAction implements Runnable {
    protected Component parentComponent;
    protected RootPaneContainer rootPaneContainer;
    protected JFileChooser fileChooser;
    protected Preferences preferences;
    protected LensPanel lensPanel;
    protected PreviewPanel previewPanel;

    protected Rectangle boundsToShot;


    public FramePieceshotAction(Component parentComponent) {
        super("FramePieceshot");
        this.parentComponent = parentComponent;
        this.rootPaneContainer = (RootPaneContainer) parentComponent;

        fileChooser = new JFileChooser();
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        fileChooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".png");
            }

            public String getDescription() {
                return "Portable Network Graphics (PNG)";
            }
        });
        fileChooser.setAccessory(previewPanel = new PreviewPanel());

        lensPanel = new LensPanel();

        // Setup preference support...
        preferences = Preferences.userNodeForPackage(FramePieceshotAction.class);

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(new Character('g'), InputEvent.CTRL_MASK));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                switch (e.getID()) {
                    case KeyEvent.KEY_TYPED:
                        if (e.isControlDown() && e.getKeyChar() == '\007')
                            FramePieceshotAction.this.actionPerformed(null);
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

    public void run() {
        try {
            Thread.sleep(500);

            // Take the frameshow
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(boundsToShot);
            previewPanel.setImage(image);

            // Choose where
            String currentDirPath = preferences.get("currentDirPath", null);
            if (currentDirPath != null)
                fileChooser.setCurrentDirectory(new File(currentDirPath));

            if (fileChooser.showSaveDialog(parentComponent) == JFileChooser.APPROVE_OPTION) {
                preferences.put("currentDirPath", fileChooser.getCurrentDirectory().getAbsolutePath());

                // Store
                File destinationFile = fileChooser.getSelectedFile();
                if (!destinationFile.getName().endsWith(".png"))
                    destinationFile = new File(destinationFile.getParentFile(), destinationFile.getName() + ".png");

                ImageIO.write(previewPanel.getImage(), "png", destinationFile);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    protected class LensPanel extends TranslucentPanel {
        protected JLayeredPane layeredPane;
        protected boolean restore;

        public LensPanel() {
            setBorder(BorderFactory.createLineBorder(Color.BLUE));
            setAlphaModeRatio(0.15f);
            setLayout(new ExtendedTableLayout(new double[][]{{2, -1, 2}, {2, -1, 2}}));
            setFocusable(true);

            FloatingResizeMouseInputHandler floatingResizeMouseInputHandler = new FloatingResizeMouseInputHandler(this);
            floatingResizeMouseInputHandler.setMinimumSize(new Dimension(40,40));
            addMouseListener(floatingResizeMouseInputHandler);
            addMouseMotionListener(floatingResizeMouseInputHandler);

            final JComponent innerPane = new JPanel() {
                protected void paintComponent(Graphics g) {
                    if (isFocusOwner())
                        setBackground(Color.RED);
                    else
                        setBackground(Color.BLUE);

                    super.paintComponent(g);    
                }
            };
            innerPane.setFocusable(true);
            innerPane.setLayout(null);
            innerPane.setOpaque(true);
            innerPane.setBackground(Color.BLUE);

            FloatingMoveMouseInputHandler floatingMoveMouseInputHandler = new FloatingMoveMouseInputHandler(this);
            innerPane.addMouseListener(floatingMoveMouseInputHandler);
            innerPane.addMouseMotionListener(floatingMoveMouseInputHandler);
            innerPane.addMouseListener(new PopupMouseInput());

            add(innerPane, "1,1,FULL,FULL");

            layeredPane = rootPaneContainer.getLayeredPane();
        }

        public void mount() {
            int x = (parentComponent.getWidth() / 2) - 50;
            int y = (parentComponent.getHeight() / 2) - 50;

            setBounds(x,y,100,100);

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

        protected class PopupMouseInput extends MouseAdapter implements ActionListener {
            protected JPopupMenu popupMenu;

            public PopupMouseInput() {
                popupMenu = new JPopupMenu();

                JMenuItem store = new JMenuItem("Store");
                store.setActionCommand("store");
                store.addActionListener(this);

                JMenuItem close = new JMenuItem("Close");
                close.setActionCommand("close");
                close.addActionListener(this);

                popupMenu.add(store);
                popupMenu.add(close);
            }

            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                if ("store".equals(actionCommand)) {
                    boundsToShot = getBounds();
                    Point location = boundsToShot.getLocation();
                    if (rootPaneContainer.getRootPane().getJMenuBar() != null && rootPaneContainer.getRootPane().getJMenuBar().isVisible())
                        location.y+= rootPaneContainer.getRootPane().getJMenuBar().getHeight();

                    SwingUtilities.convertPointToScreen(location, parentComponent);
                    boundsToShot.setLocation(location);

                    boundsToShot = SwingUtilities.computeIntersection(parentComponent.getX(), parentComponent.getY(), parentComponent.getWidth(), parentComponent.getHeight(),
                                                                      boundsToShot);


                    unmount(FramePieceshotAction.this);
                } else if ("close".equals(actionCommand)) {
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