package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowType;
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
public class DockToolWindowAction extends ToolWindowAction implements PlafToolWindowAction  {

    protected JCheckBoxMenuItem menuItem;
    protected PropertyChangeListener propertyChangeListener;

    public DockToolWindowAction() {
        super(DOCK_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.DOCKED_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.undock"));
        putValue("constraint", 4);
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        if (toolWindow == null) {
            this.toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).removePropertyChangeListener("enabled", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("active", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("type", propertyChangeListener);
            this.propertyChangeListener = null;

            super.setToolWindow(toolWindow);
        } else {
            super.setToolWindow(toolWindow);

            propertyChangeListener = new PropertyListener();
            setActionName("toolWindow.dockButton." + toolWindow.getId());

            toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).addPropertyChangeListener("enabled", propertyChangeListener);
            toolWindow.addPropertyChangeListener("active", propertyChangeListener);
            toolWindow.addPropertyChangeListener("type", propertyChangeListener);
        }
    }

    public void actionPerformed(ActionEvent e) {
        ToolWindowType type = toolWindow.getType();
        if (type == ToolWindowType.DOCKED) {
            toolWindow.setType(ToolWindowType.SLIDING);
        } else if (type == ToolWindowType.SLIDING || type == ToolWindowType.FLOATING_LIVE) {
            toolWindow.setType(ToolWindowType.DOCKED);
        }
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.DOCKED);
            menuItem.setText(SwingUtil.getString("@@tool.mode.docked"));
            menuItem.setActionCommand("menu.docked");
            menuItem.addActionListener(this);
        }
        
        switch (toolWindow.getType()) {
            case DOCKED:
                menuItem.setVisible(toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                break;
            case FLOATING_LIVE:
                menuItem.setState(false);
                break;
        }

        return menuItem;
    }



    public class PropertyListener extends DynamicPropertyChangeListener {

        public void onEnabled(PropertyChangeEvent evt) {
            setVisible((Boolean) evt.getNewValue());
        }

        public void onActive(PropertyChangeEvent evt) {
            boolean active = (Boolean) evt.getNewValue();

            if (active) {
                if (toolWindow.getType() == ToolWindowType.SLIDING || toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.DOCKED));
                } else
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.SLIDING));
            } else {
                if (toolWindow.getType() == ToolWindowType.SLIDING || toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.DOCKED_INACTIVE));
                } else
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.SLIDING_INACTIVE));
            }
        }

        public void onType(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();

            if (menuItem != null) {
                menuItem.setState(type == ToolWindowType.DOCKED);
                menuItem.setVisible(type != ToolWindowType.FLOATING);
            }

            switch (type) {
                case DOCKED:
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.SLIDING));
                    putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.undock"));

                    setVisible(toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                    break;
                case FLOATING_LIVE:
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.DOCKED));
                    putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.dock"));

                    setVisible(toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                    break;
                case SLIDING:
                    putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.dock"));

                    setVisible(toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                    break;
                case FLOATING:
                case FLOATING_FREE:

                    setVisible(false);
                    break;
            }
        }
    }

}
