package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ShortcutProcessor implements KeyEventPostProcessor {
    protected MyDoggyToolWindowManager manager;
    protected Container root;

    public ShortcutProcessor(MyDoggyToolWindowManager manager, Container root) {
        this.manager = manager;
        this.root = root;
    }

    public boolean postProcessKeyEvent(KeyEvent e) {
        switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
                if (e.isAltDown() &&
                    Character.isDigit(e.getKeyChar()) &&
                    manager.isWindowFocused()) {

                    if (manager.getToolWindowManagerDescriptor().isNumberingEnabled()) {
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
        }

        return false;
    }

}
