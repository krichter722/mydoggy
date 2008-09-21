package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.util.DynamicPropertyChangeListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FloatingLiveToolWindowAction extends ToolWindowAction implements PlafToolWindowAction  {

    protected JCheckBoxMenuItem menuItem;
    protected PropertyChangeListener propertyChangeListener;


    public FloatingLiveToolWindowAction() {
        super(FLOATING_LIVE_ACTION_ID);
        setTooltipText(SwingUtil.getString("@@tool.tooltip.floatingLive"));
        setVisibleOnMenuBar(true);
        setVisibleOnTitleBar(false);
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        if (toolWindow == null) {
            this.toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).removePropertyChangeListener("enabled", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("type", propertyChangeListener);
            this.propertyChangeListener = null;

            super.setToolWindow(toolWindow);
        } else {
            super.setToolWindow(toolWindow);

            propertyChangeListener = new PropertyListener();
            setActionName("toolWindow.popup.floatingLive." + toolWindow.getId());

            toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).addPropertyChangeListener("enabled", propertyChangeListener);
            toolWindow.addPropertyChangeListener("type", propertyChangeListener);
        }

    }

    public void actionPerformed(ActionEvent e) {
        toolWindow.setType(toolWindow.getType() != ToolWindowType.FLOATING_LIVE ? ToolWindowType.FLOATING_LIVE : ToolWindowType.DOCKED);
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JCheckBoxMenuItem(null, toolWindow.getType() == ToolWindowType.FLOATING_LIVE);
            menuItem.setName("toolWindow.popup.floatingLive." + toolWindow.getId());
            menuItem.setText(SwingUtil.getString("@@tool.mode.floatingLive"));
            menuItem.setActionCommand("menu.floatingLive");
            menuItem.addActionListener(this);
        }

        switch (toolWindow.getType()) {
            case DOCKED:
            case SLIDING:
            case FLOATING:
                menuItem.setState(false);
                menuItem.setVisible(toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isEnabled());
                break;
        }

        return menuItem;
    }



    public class PropertyListener extends DynamicPropertyChangeListener {

        public void onEnabled(PropertyChangeEvent evt) {
            setVisibleOnMenuBar((Boolean) evt.getNewValue());
        }

        public void onType(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();

            if (menuItem != null)
                menuItem.setState(type == ToolWindowType.FLOATING_LIVE);
        }
    }

}