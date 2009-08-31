package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsMenuToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {


    public ToolsMenuToolWindowAction() {
        super(TOOLS_MENU_ACTION_ID);
    }


    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getMenuItem() {
        JMenu toolsMenu = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolsMenu();

        return toolsMenu != null && toolsMenu.getMenuComponentCount() > 0 ? toolsMenu : null;
    }
}
