package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
*/
public class TitleBarMouseAdapter extends MouseAdapter {
    protected ToolWindowDescriptor toolWindowDescriptor;
    protected ToolWindow toolWindow;

    public TitleBarMouseAdapter(ToolWindowDescriptor toolWindowDescriptor) {
        this.toolWindowDescriptor = toolWindowDescriptor;
        this.toolWindow = toolWindowDescriptor.getToolWindow();
    }

    public void mouseClicked(MouseEvent e) {
        if (!toolWindow.isAvailable())
            return;

        if (SwingUtilities.isLeftMouseButton(e)) {
            toolWindow.setActive(true);

            if (e.getClickCount() == 2)
                toolWindow.setMaximized(!toolWindow.isMaximized());
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (((DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED)).isPopupMenuEnabled())
                toolWindowDescriptor.showPopupMenu(e.getComponent(), e.getX(), e.getY());
        }
    }

}
