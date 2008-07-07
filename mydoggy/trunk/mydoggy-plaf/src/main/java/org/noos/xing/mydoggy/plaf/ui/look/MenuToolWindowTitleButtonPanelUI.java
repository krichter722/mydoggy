package org.noos.xing.mydoggy.plaf.ui.look;

import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowContainer;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
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
        return new MenuToolWindowTitleButtonPanelUI((ToolWindowDescriptor) c.getClientProperty(ToolWindowDescriptor.class),
                                                    (ToolWindowContainer) c.getClientProperty(ToolWindowContainer.class));
    }


    public MenuToolWindowTitleButtonPanelUI(ToolWindowDescriptor toolWindowDescriptor,
                                            ToolWindowContainer dockedContainer) {
        super(toolWindowDescriptor, dockedContainer);
    }


    protected void installComponents() {
        toolWindowTitleButtonPanel.setLayout(containerLayout = new ExtendedTableLayout(new double[][]{{0, 0}, {1, 14, 1}}, false));
        toolWindowTitleButtonPanel.setOpaque(false);

        addTitleBarAction(new PopupAction());
        focusable = addTitleBarAction(new HideAction());
    }


    public class PopupAction extends TitleBarAction {

        public PopupAction() {
            super("toolWindow.showPopupButton." + toolWindow.getId(), MyDoggyKeySpace.ACTIONS_POPUP, "@@tool.tooltip.showPopup");
            dockedContainer.addPropertyChangeListener("active", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor)
                        return;

                    boolean active = (Boolean) evt.getNewValue();

                    if (active) {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP));
                    } else {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP_INACTIVE));
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            Component c = (Component) e.getSource();
            dockedContainer.showPopupMenu(c, 0, c.getHeight());
        }

        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

}