package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.actions.PlafToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MenuToolWindowTitleButtonPanelUI extends ToolWindowTitleButtonPanelUI {

    
    public static ComponentUI createUI(JComponent c) {
        return new MenuToolWindowTitleButtonPanelUI();
    }


    public MenuToolWindowTitleButtonPanelUI() {
    }


    protected void installComponents() {
        toolWindowTitleButtonPanel.setLayout(containerLayout = new ExtendedTableLayout(new double[][]{{0, 0}, {1, 14, 1}}, false));
        toolWindowTitleButtonPanel.setOpaque(false);

        descriptor.getToolWindow().addToolWindowAction(new PopupAction());

        // Add the default set of actions
        focusable = null;

        DockedTypeDescriptor dockedTypeDescriptor = descriptor.getDockedTypeDescriptor();
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID));
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.POPUP_ACTION_ID));
    }

    @Override
    protected void setType(ToolWindowType oldType, ToolWindowType toolWindowType) {
        if (toolWindowType == ToolWindowType.EXTERN)
            return;

        // Store Current Layout
        storeCurrentLayout(oldType);

        // remove all actions from the container
        toolWindowTitleButtonPanel.removeAll();
        containerLayout.setColumn(new double[]{0,0});

        // add actions for the current type...
        focusable = null;

        ToolWindowTypeDescriptor typeDescriptor = descriptor.getTypeDescriptor(toolWindowType);

        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID));
        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.POPUP_ACTION_ID));

        // Add custom actions...
        for (ToolWindowAction toolWindowAction : typeDescriptor.getToolWindowActions()) {
            if (!(toolWindowAction instanceof PlafToolWindowAction))  {
                int index = (Integer) toolWindowAction.getValue("constraint");
                addToolWindowAction(toolWindowAction, index);
            }
        }

        // ensure the visibility of the panel...
        toolWindowTitleButtonPanel.setVisible(toolWindow.getTypeDescriptor(toolWindow.getType()).isTitleBarButtonsVisible());
    }

    public class PopupAction extends ToolWindowAction implements PlafToolWindowAction {

        public PopupAction() {
            super(POPUP_ACTION_ID);
        }

        @Override
        public void setToolWindow(ToolWindow toolWindow) {
            super.setToolWindow(toolWindow);
            setIcon(UIManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP));
            setTooltipText(SwingUtil.getString("@@tool.tooltip.showPopup"));
            setActionName("toolWindow.showPopupButton." + toolWindow.getId());

            toolWindow.addPropertyChangeListener("active", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor.getToolWindow())
                        return;

                    boolean active = (Boolean) evt.getNewValue();

                    if (active) {
                        setIcon(UIManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP));
                    } else {
                        setIcon(UIManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP_INACTIVE));
                    }
                }
            });
        }

        public JMenuItem getMenuItem() {
            return null;
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            Component c = (Component) e.getSource();
            descriptor.showPopupMenu(c, 0, c.getHeight());
        }

    }

}