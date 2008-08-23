package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AggregateToolWindowAction extends ToolWindowAction implements PlafToolWindowAction  {

    protected JMenuItem menuItem;

    public AggregateToolWindowAction() {
        super(AGGREGATE_ACTION_ID);
        setVisibleOnTitleBar(false);
    }


    public void setToolWindow(ToolWindow toolWindow) {
        super.setToolWindow(toolWindow);

        setActionName("toolWindow.popup.aggegate." + toolWindow.getId());
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem= new JMenuItem();
            menuItem.setName("toolWindow.popup.aggregate." + toolWindow.getId());
            menuItem.setText(SwingUtil.getString("@@tool.aggregate"));
            menuItem.setActionCommand("menu.aggregate");
            menuItem.addActionListener(this);
        }

        menuItem.setVisible(!toolWindow.isVisible());

        return menuItem;
    }

    public void actionPerformed(ActionEvent e) {
        if (toolWindow.isActive()) {
            toolWindow.setActive(false);
            toolWindow.setVisible(false);
        } else if (toolWindow.isVisible()) {
            toolWindow.setVisible(false);
        } else {
            toolWindow.aggregate();
            toolWindow.setActive(true);
        }
    }

}