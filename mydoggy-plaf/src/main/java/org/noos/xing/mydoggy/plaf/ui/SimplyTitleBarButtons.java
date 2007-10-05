package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class SimplyTitleBarButtons implements TitleBarButtons {
    protected ToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;
    protected ResourceManager resourceManager;
    protected DockedContainer dockedContainer;

    protected JPanel buttonContainer;

    protected JButton hideButton;

    public SimplyTitleBarButtons(ToolWindowDescriptor toolWindowDescriptor, DockedContainer dockedContainer) {
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

    public void configureIcons(ToolWindowType type) {
    }

    public void configureIcons(boolean active) {
    }


    protected void initComponents() {
        buttonContainer = new JPanel(new ExtendedTableLayout(new double[][]{{15, 2, 15}, {1, 14, 1}}, false));
        buttonContainer.setOpaque(false);

        // Buttons
        ActionListener titleBarActionListener = new TitleBarActionListener();

        hideButton = renderTitleButton("visible", titleBarActionListener,
                                       "@@tool.tooltip.hide", ResourceManager.HIDE_TOOL_WINDOW_INACTIVE,
                                       null);
        JButton popupButton = renderTitleButton("showPopup", titleBarActionListener,
                                       "@@tool.tooltip.showPopup", ResourceManager.ACTIONS,
                                       "toolWindow.showPopupButton." + toolWindow.getId());

        buttonContainer.add(popupButton, "0,1");
        buttonContainer.add(hideButton, "2,1");
    }

    protected void initListeners() {
    }

    protected JButton renderTitleButton(String actionCommand, ActionListener actionListener, String tooltip, String iconId, String name) {
        JButton button = (JButton) resourceManager.createComponent(
                ResourceManager.TOOL_WINDOW_TITLE_BUTTON,
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