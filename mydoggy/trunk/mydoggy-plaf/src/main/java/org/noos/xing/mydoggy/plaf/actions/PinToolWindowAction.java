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
public class PinToolWindowAction extends ToolWindowAction implements PlafToolWindowAction  {

    protected PropertyChangeListener propertyChangeListener;


    public PinToolWindowAction() {
        super(PIN_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.unpin"));
        putValue("constraint", 2);
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        if (toolWindow == null) {
            this.toolWindow.removePropertyChangeListener("autoHide", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("active", propertyChangeListener);
            this.toolWindow.removePropertyChangeListener("type", propertyChangeListener);
            this.propertyChangeListener = null;

            super.setToolWindow(toolWindow);
        } else {
            super.setToolWindow(toolWindow);

            propertyChangeListener = new PropertyListener();
            setActionName("toolWindow.pinButton." + toolWindow.getId());

            toolWindow.addPropertyChangeListener("autoHide", propertyChangeListener);
            toolWindow.addPropertyChangeListener("active", propertyChangeListener);
            toolWindow.addPropertyChangeListener("type", propertyChangeListener);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!"menu.pinned".equals(e.getActionCommand()))
            toolWindow.setActive(true);
        
        toolWindow.setAutoHide(!toolWindow.isAutoHide());
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JCheckBoxMenuItem(null, !toolWindow.isAutoHide());
            menuItem.setText(SwingUtil.getString("@@tool.mode.pinned"));
            menuItem.setActionCommand("menu.pinned");
            menuItem.addActionListener(this);
        }
        return menuItem;
    }


    public class PropertyListener extends DynamicPropertyChangeListener {

        public void onAutoHide(PropertyChangeEvent evt) {
            boolean newValue = ((Boolean) evt.getNewValue());

            if (newValue) {
                setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.pin"));
            } else {
                setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));
                putValue(Action.SHORT_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.unpin"));
            }

        }

        public void onActive(PropertyChangeEvent evt) {
            boolean active = (Boolean) evt.getNewValue();

            if (active) {
                if (toolWindow.isAutoHide()) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                } else
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));
            } else {
                if (toolWindow.isAutoHide()) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON_INACTIVE));
                } else
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE));
            }
        }

        public void onType(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();
            switch (type) {
                case DOCKED:
                case FLOATING_LIVE:
                    setVisible(true);
                    break;
                case SLIDING:
                    setVisible(false);
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    setVisible(true);
                    break;
            }
        }
    }
}