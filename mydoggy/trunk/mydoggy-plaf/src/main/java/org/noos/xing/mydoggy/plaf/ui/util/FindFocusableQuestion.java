package org.noos.xing.mydoggy.plaf.ui.util;

import org.noos.common.Question;

import javax.swing.*;
import java.awt.*;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class FindFocusableQuestion implements Question<Component, Component> {

    public FindFocusableQuestion() {
    }

    public Component getAnswer(Component cmp) {
        if (cmp.isFocusable() && !(cmp instanceof JPanel) &&
            !(cmp instanceof JLabel) &&
            !(cmp instanceof JScrollPane) &&
            !(cmp instanceof JViewport) &&
            !(cmp instanceof JToolBar) &&
            !(cmp instanceof JSplitPane) &&
            !(cmp instanceof JLayeredPane) &&
            !(cmp instanceof RootPaneContainer) &&
            !(cmp instanceof JRootPane)
                )
            return cmp;

        if (cmp instanceof Container) {
            Container container = (Container) cmp;
            
            for (int i = 0, size = container.getComponentCount(); i < size; i++) {
                Component found = getAnswer(container.getComponent(i));
                if (found != null)
                    return found;
            }
        }
        return null;
    }

}