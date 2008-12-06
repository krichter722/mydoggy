package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowActionHandler;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class HideToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {

    protected PropertyChangeListener propertyChangeListener;


    public HideToolWindowAction() {
        super(HIDE_ACTION_ID, UIManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE));
        setTooltipText(SwingUtil.getString("@@tool.tooltip.hide"));
        putValue("constraint", 0);
    }


    public void setToolWindow(ToolWindow toolWindow) {
        if (toolWindow == null) {
            this.toolWindow.removePropertyChangeListener("active", propertyChangeListener);
            this.propertyChangeListener = null;

            super.setToolWindow(toolWindow);
        } else {
            super.setToolWindow(toolWindow);

            propertyChangeListener = new PropertyListener();
            setActionName("toolWindow.hideButton." + toolWindow.getId());

            toolWindow.addPropertyChangeListener("active", propertyChangeListener);
        }
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem= new JMenuItem();
            menuItem.setName("toolWindow.popup.visible." + toolWindow.getId());
            menuItem.setActionCommand("menu.visible");
            menuItem.addActionListener(this);
        }

        menuItem.setText(toolWindow.isVisible() ?
                        SwingUtil.getString("@@tool.hide") :
                        SwingUtil.getString("@@tool.show"));

        return menuItem;
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if ("menu.visible".equals(actionCommand)) {
            if (toolWindow.isActive()) {
                toolWindow.setActive(false);
                hideToolWindow();
            } else if (toolWindow.isVisible()) {
                hideToolWindow();
            } else
                toolWindow.setActive(true);
        } else {
            hideToolWindow();
        }
    }

    public void hideToolWindow() {
        ToolWindowActionHandler toolWindowActionHandler = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolWindowActionHandler();
        if (toolWindowActionHandler != null)
            toolWindowActionHandler.onHideButtonClick(toolWindow);
        else
            toolWindow.setVisible(false);
    }


    public class PropertyListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            boolean active = (Boolean) evt.getNewValue();

            if (active) {
                setIcon(UIManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW));
            } else {
                setIcon(UIManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE));
            }
        }

    }

}
