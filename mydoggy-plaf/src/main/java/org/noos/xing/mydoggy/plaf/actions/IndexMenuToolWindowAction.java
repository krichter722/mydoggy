package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class IndexMenuToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {


    public IndexMenuToolWindowAction() {
        super(INDEX_MENU_ACTION_ID);
    }


    public void actionPerformed(ActionEvent e) {
        toolWindow.setIndex(Integer.parseInt(e.getActionCommand()));
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            JMenu indexMenu = new JMenu(SwingUtil.getString("@@tool.index"));

            for (int i = 1; i < 10; i++) {
                JMenuItem indexItem = new JMenuItem(String.valueOf(i));
                indexItem.setActionCommand(indexItem.getText());
                indexItem.addActionListener(this);

                indexMenu.add(indexItem);
            }

            indexMenu.addSeparator();

            JMenuItem indexItem = new JMenuItem(SwingUtil.getString("@@tool.index.reset"));
            indexItem.setActionCommand("-1");
            indexItem.addActionListener(this);

            indexMenu.add(indexItem);

            this.menuItem = indexMenu;
        }

        JMenu indexMenu = (JMenu) menuItem;
        for (int i = 0, size = indexMenu.getMenuComponentCount(); i < size; i++) {
            if (indexMenu.getMenuComponent(i) instanceof JMenuItem) {
                JMenuItem indexItem = (JMenuItem) indexMenu.getMenuComponent(i);
                indexItem.setEnabled(toolWindow.getIndex() != Integer.parseInt(indexItem.getActionCommand()));
            }
        }
                
        return menuItem;
    }
}