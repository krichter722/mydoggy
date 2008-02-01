package org.noos.xing.mydoggy.mydoggyset.action;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FrameshotAction extends AbstractAction implements Runnable {
    protected Component parentComponent;
    protected JFileChooser fileChooser;
    protected PreviewPanel previewPanel;
    protected Preferences preferences;

    public FrameshotAction(Component parentComponent) {
        super("Frameshot");
        this.parentComponent = parentComponent;

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

        // Setup preference support...
        preferences = Preferences.userNodeForPackage(FrameshotAction.class);

        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(new Character('f'), InputEvent.CTRL_MASK));
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new KeyEventPostProcessor() {
            public boolean postProcessKeyEvent(KeyEvent e) {
                switch (e.getID()) {
                    case KeyEvent.KEY_TYPED:
                        if (e.isControlDown() && e.getKeyChar() == '\006')
                            FrameshotAction.this.actionPerformed(null);
                        break;
                }
                return false;
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(this);
    }

    public void run() {
        try {
            Thread.sleep(100);

            // Take the frameshow
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(parentComponent.getBounds());
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


}
