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
    protected ToolWindowManager manager;
    protected Container root;

    public ShortcutProcessor(ToolWindowManager manager, Container root) {
        this.manager = manager;
        this.root = root;
    }

    public boolean postProcessKeyEvent(KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
//                if (SwingUtil.hasParent(e.getComponent(), root) ) {
                    if (e.isAltDown() && Character.isDigit(e.getKeyChar())) {
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
//                }
                break;
        }

        return false;
    }

}
