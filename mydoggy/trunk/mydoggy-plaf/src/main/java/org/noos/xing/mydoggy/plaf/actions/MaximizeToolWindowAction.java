package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MaximizeToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {

    protected JMenuItem menuItem;

    public MaximizeToolWindowAction() {
        super(MAXIMIZE_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.maximize"));
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        super.setToolWindow(toolWindow);

        setActionName("toolWindow.maximizeButton." + toolWindow.getId());
        toolWindow.addPropertyChangeListener("maximized.before", new PropertyChangeListener() {
            private boolean flag = false;

            public void propertyChange(PropertyChangeEvent evt) {
                if ((Boolean) evt.getNewValue()) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                    flag = true;
                } else if (flag) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
                    flag = false;
                }
            }
        });
        toolWindow.addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                boolean active = (Boolean) evt.getNewValue();

                if (active) {
                    if (toolWindow.isMaximized())
                        setIcon(UIManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                    else
                        setIcon(UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
                } else {
                    if (toolWindow.isMaximized())
                        setIcon(UIManager.getIcon(MyDoggyKeySpace.MINIMIZE_INACTIVE));
                    else
                        setIcon(UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE_INACTIVE));
                }
            }
        });
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JMenuItem();
            menuItem.setText(SwingUtil.getString("@@tool.maximize"));
            menuItem.setActionCommand("menu.maximize");
            menuItem.addActionListener(this);
        }

        menuItem.setVisible(toolWindow.isVisible());
        menuItem.setText(toolWindow.isMaximized() ?
                         SwingUtil.getString("@@tool.maximize.restore") :
                         SwingUtil.getString("@@tool.maximize"));

        return menuItem;
    }

    public void actionPerformed(ActionEvent e) {
        toolWindow.setActive(true);
        toolWindow.setMaximized(!toolWindow.isMaximized());
    }

}