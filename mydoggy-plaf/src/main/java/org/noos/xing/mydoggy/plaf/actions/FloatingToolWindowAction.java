package org.noos.xing.mydoggy.plaf.actions;

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
public class FloatingToolWindowAction extends ToolWindowAction implements PropertyChangeListener {

    protected ToolWindowType oldType;
    protected boolean floatingWindow;
    protected JCheckBoxMenuItem menuItem;


    public FloatingToolWindowAction() {
        super(FLOATING_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.FLOATING_INACTIVE));
        setTooltipText("@@tool.tooltip.float");
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        super.setToolWindow(toolWindow);

        setActionName("toolWindow.floatingButton." + toolWindow.getId());
        toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName()))
                            setVisible((Boolean) evt.getNewValue());
                    }
                }
        );
        toolWindow.addPropertyChangeListener("type", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getOldValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.FLOATING_FREE)
                    floatingWindow = true;
                else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                    floatingWindow = false;


                if (evt.getNewValue() != ToolWindowType.FLOATING &&
                    evt.getNewValue() != ToolWindowType.FLOATING_FREE)
                    return;

                oldType = (ToolWindowType) evt.getOldValue();
            }
        });

        toolWindow.addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
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
        });
        toolWindow.addPropertyChangeListener("type", this);
    }

    public void actionPerformed(ActionEvent e) {
        toolWindow.setActive(true);

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

    public void propertyChange(PropertyChangeEvent evt) {
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
    }

}