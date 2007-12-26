package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowActionHandler;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MenuTitleBarButtons implements TitleBarButtons {
    protected ToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;
    protected ResourceManager resourceManager;
    protected DockedContainer dockedContainer;

    protected JPanel buttonContainer;

    protected JButton hideButton, popupButton;

    public MenuTitleBarButtons(ToolWindowDescriptor toolWindowDescriptor, DockedContainer dockedContainer) {
        this.descriptor = toolWindowDescriptor;
        this.toolWindow = toolWindowDescriptor.getToolWindow();
        this.resourceManager = dockedContainer.getResourceManager();
        this.dockedContainer = dockedContainer;

        initComponents();
        initListeners();
    }


    public Component getFocusable() {
        return hideButton;
    }

    public Component getButtonsContainer() {
        return buttonContainer;
    }

    public void toolWindowTypeChanged(ToolWindowType type) {
    }


    protected void initComponents() {
        buttonContainer = new JPanel(new ExtendedTableLayout(new double[][]{{15, 2, 15}, {1, 14, 1}}, false));
        buttonContainer.setOpaque(false);

        // Buttons
        ActionListener titleBarActionListener = new TitleBarActionListener();

        hideButton = renderTitleButton("visible", titleBarActionListener,
                "@@tool.tooltip.hide", MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE,
                null);
        popupButton = renderTitleButton("showPopup", titleBarActionListener,
                "@@tool.tooltip.showPopup", MyDoggyKeySpace.ACTIONS_POPUP,
                "toolWindow.showPopupButton." + toolWindow.getId());

        buttonContainer.add(popupButton, "0,1");
        buttonContainer.add(hideButton, "2,1");
    }

    protected void initListeners() {
        dockedContainer.addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean active = (Boolean) evt.getNewValue();

                if (active) {
                    hideButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW));
                    popupButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP));
                } else {
                    hideButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE));
                    popupButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.ACTIONS_POPUP_INACTIVE));
                }
            }
        });
    }

    protected JButton renderTitleButton(String actionCommand, ActionListener actionListener, String tooltip, String iconId, String name) {
        JButton button = (JButton) resourceManager.createComponent(
                MyDoggyKeySpace.TOOL_WINDOW_TITLE_BUTTON,
                descriptor.getManager()
        );
        button.setName(name);
        button.setActionCommand(actionCommand);
        button.addActionListener(actionListener);
        button.setToolTipText(resourceManager.getString(tooltip));
        button.setIcon(resourceManager.getIcon(iconId));

        return button;
    }


    protected class TitleBarActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String actionCommnad = e.getActionCommand();
            if (!"visible".equals(actionCommnad))
                toolWindow.setActive(true);

            if ("visible".equals(actionCommnad)) {
                ToolWindowActionHandler toolWindowActionHandler = ((DockedTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.DOCKED)).getToolWindowActionHandler();
                if (toolWindowActionHandler != null)
                    toolWindowActionHandler.onHideButtonClick(toolWindow);
                else
                    toolWindow.setVisible(false);
            } else if ("showPopup".equals(actionCommnad)) {
                Component c = (Component) e.getSource();
                dockedContainer.showPopupMenu(c, 0, c.getHeight());
            }
        }

    }

}