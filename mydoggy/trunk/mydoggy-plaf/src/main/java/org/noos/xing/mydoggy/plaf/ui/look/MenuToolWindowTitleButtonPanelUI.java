package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MenuToolWindowTitleButtonPanelUI extends FullToolWindowTitleButtonPanelUI {

    
    public static ComponentUI createUI(JComponent c) {
        return new MenuToolWindowTitleButtonPanelUI();
    }


    public MenuToolWindowTitleButtonPanelUI() {
    }


    protected void installComponents() {
        toolWindowTitleButtonPanel.setLayout(containerLayout = new ExtendedTableLayout(new double[][]{{0, 0}, {1, 14, 1}}, false));
        toolWindowTitleButtonPanel.setOpaque(false);

        DockedTypeDescriptor dockedTypeDescriptor = descriptor.getDockedTypeDescriptor();
        dockedTypeDescriptor.addToolWindowAction(new PopupAction());

        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.POPUP_ACTION_ID));
        focusable = addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID));
    }


    public class PopupAction extends ToolWindowAction {

        public PopupAction() {
            super(POPUP_ACTION_ID);
        }

        @Override
        public void setToolWindow(ToolWindow toolWindow) {
            super.setToolWindow(toolWindow);
            setIcon(UIManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP));
            setTooltipText("@@tool.tooltip.showPopup");
            setActionName("toolWindow.showPopupButton." + toolWindow.getId());

            toolWindow.addPropertyChangeListener("active", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor)
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
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            Component c = (Component) e.getSource();
            descriptor.showPopupMenu(c, 0, c.getHeight());
        }

    }

}