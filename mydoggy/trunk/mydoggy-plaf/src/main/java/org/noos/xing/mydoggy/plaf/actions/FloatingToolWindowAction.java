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
public class FloatingToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {

    protected ToolWindowType oldType;
    protected boolean floatingWindow;
    protected JCheckBoxMenuItem menuItem;
    protected PropertyChangeListener propertyChangeListener;


    public FloatingToolWindowAction() {
        super(FLOATING_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.FLOATING_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.float"));
        putValue("constraint", 3);
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        if (toolWindow == null) {
            this.toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).removePropertyChangeListener("enabled", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("type", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("active", propertyChangeListener);
            this.propertyChangeListener = null;

            super.setToolWindow(toolWindow);
        } else {
            super.setToolWindow(toolWindow);

            propertyChangeListener = new PropertyListener();
            setActionName("toolWindow.floatingButton." + toolWindow.getId());

            toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).addPropertyChangeListener("enabled", propertyChangeListener);
            toolWindow.addPropertyChangeListener("type", propertyChangeListener);
            toolWindow.addPropertyChangeListener("active", propertyChangeListener);
        }

    }

    public void actionPerformed(ActionEvent e) {
        ToolWindowType type = toolWindow.getType();
        switch (type) {
            case FLOATING:
            case FLOATING_FREE:
                toolWindow.setType(oldType != null ? oldType : ToolWindowType.DOCKED);
                break;
            default:
                toolWindow.setType(floatingWindow ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
        }
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING);
            menuItem.setText(SwingUtil.getString("@@tool.mode.floating"));
            menuItem.setActionCommand("menu.floating");
            menuItem.addActionListener(this);
        }

        switch (toolWindow.getType()) {
            case DOCKED:
            case SLIDING:
                menuItem.setVisible(toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                break;
            case FLOATING_LIVE:
                menuItem.setState(false);
                menuItem.setVisible(toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                break;

        }

        return menuItem;
    }



    public class PropertyListener extends DynamicPropertyChangeListener {

        public void onEnabled(PropertyChangeEvent evt) {
            setVisible((Boolean) evt.getNewValue());
        }

        public void onType(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();

            if (menuItem != null)
                menuItem.setState(type == ToolWindowType.FLOATING);

            switch (type) {
                case DOCKED:
                case FLOATING_LIVE:
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.FLOATING));
                    setTooltipText(SwingUtil.getString("@@tool.tooltip.float"));
                    setVisible(toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                    break;
                case SLIDING:
                    setVisible(toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.FIX));
                    setTooltipText(SwingUtil.getString("@@tool.tooltip.fix"));
                    setVisible(true);
                    break;
            }

            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.FLOATING_FREE)
                floatingWindow = true;
            else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                floatingWindow = false;

            if (evt.getNewValue() != ToolWindowType.FLOATING &&
                evt.getNewValue() != ToolWindowType.FLOATING_FREE)
                return;

            oldType = (ToolWindowType) evt.getOldValue();
        }

        public void onActive(PropertyChangeEvent evt) {
            boolean active = (Boolean) evt.getNewValue();

            if (active) {
                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.FIX));
                } else
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.FLOATING));
            } else {
                if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.FIX_INACTIVE));
                } else
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.FLOATING_INACTIVE));
            }
        }
    }
}