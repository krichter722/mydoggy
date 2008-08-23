package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class PinToolWindowAction extends ToolWindowAction implements PropertyChangeListener, PlafToolWindowAction  {

    protected JMenuItem menuItem;

    public PinToolWindowAction() {
        super(PIN_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.unpin"));
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        super.setToolWindow(toolWindow);

        setActionName("toolWindow.pinButton." + toolWindow.getId());
        toolWindow.addPropertyChangeListener("autoHide", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                boolean newValue = ((Boolean) evt.getNewValue());

                if (newValue) {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                    putValue(Action.LONG_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.pin"));
                } else {
                    setIcon(UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));
                    putValue(Action.LONG_DESCRIPTION, SwingUtil.getString("@@tool.tooltip.unpin"));
                }
            }
        });
        toolWindow.addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
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
        });
        toolWindow.addPropertyChangeListener("type", this);
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


    public void propertyChange(PropertyChangeEvent evt) {
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
                setVisible(!toolWindow.getTypeDescriptor(FloatingTypeDescriptor.class).isModal());
                break;
        }
    }

}