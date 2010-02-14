package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.util.DynamicPropertyChangeListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MaximizeToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {

    protected PropertyChangeListener propertyChangeListener;


    public MaximizeToolWindowAction() {
        super(MAXIMIZE_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.maximize"));
        putValue("constraint", 1);
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        if (toolWindow == null) {
            this.toolWindow.addPropertyChangeListener("maximized", propertyChangeListener);
            this.toolWindow.addPropertyChangeListener("active", propertyChangeListener);

            this.propertyChangeListener = null;
            super.setToolWindow(toolWindow);
        } else {
            super.setToolWindow(toolWindow);
            propertyChangeListener = new PropertyListener();

            setActionName("toolWindow.maximizeButton." + toolWindow.getId());

            toolWindow.addPropertyChangeListener("maximized", propertyChangeListener);
            toolWindow.addPropertyChangeListener("active", propertyChangeListener);
        }
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


    public class PropertyListener extends DynamicPropertyChangeListener {

        private boolean flag = false;

        
        public void onMaximized(PropertyChangeEvent evt) {
            if ((Boolean) evt.getNewValue()) {
                setIcon(UIManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.restore"));
                flag = true;
            } else if (flag) {
                setIcon(UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
                putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.maximize"));
                flag = false;
            }
        }

        public void onActive(PropertyChangeEvent evt) {
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
    }
}