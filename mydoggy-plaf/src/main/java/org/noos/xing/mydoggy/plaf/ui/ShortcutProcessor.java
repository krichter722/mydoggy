package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.BackContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ShortcutProcessor implements KeyEventPostProcessor {
    private ToolWindowManager manager;

    public ShortcutProcessor(ToolWindowManager manager) {
        this.manager = manager;
    }

    public boolean postProcessKeyEvent(KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
                if (e.isAltDown()) {
                    if (Character.isDigit(e.getKeyChar())) {
                        int index = Character.getNumericValue(e.getKeyChar());

                        for (ToolWindow toolWindow : manager.getToolWindows()) {
                            if (toolWindow.getIndex() == index) {
                                if (toolWindow.isAvailable()) {
                                    if (toolWindow.isActive())
                                        toolWindow.setVisible(false);
                                    else
                                        toolWindow.setActive(true);
                                }
                                break;
                            }
                        }
                    }
                }
                break;
            case KeyEvent.KEY_PRESSED:
                if (e.isAltDown()) {
                    if (SwingUtil.hasParent(e.getComponent(), ((BackContentManagerUI) manager.getContentManager().getContentManagerUI()).getContainer())) {
                        if (e.getKeyCode() == 39) {
                            Content content = manager.getContentManager().getNextContent();
                            if (content != null)
                                content.setSelected(true);
                        } else if (e.getKeyCode() == 37) {
                            Content content = manager.getContentManager().getPreviousContent();
                            if (content != null)
                                content.setSelected(true);
                        }

                    }
                }
                break;
        }

        return false;
    }

}
