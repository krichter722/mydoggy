package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindow;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsOnBarMouseListener extends MouseAdapter implements ActionListener {
    private ToolWindowManager toolWindowManager;
    private ToolWindowAnchor anchor;

    private JPopupMenu popupMenu;

    public ToolsOnBarMouseListener(ToolWindowManager toolWindowManager, ToolWindowAnchor anchor) {
        this.toolWindowManager = toolWindowManager;
        this.anchor = anchor;
        initPopupMenu();
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {

            popupMenu.removeAll();

            ToolWindow[] tools = toolWindowManager.getToolsByAnchor(anchor);
            if (tools.length > 0) {

                for (ToolWindow tool : tools) {
                    if (tool.isAvailable()) {
                        JMenuItem showTool = new JMenuItem();
                        showTool.setText(tool.getId());
                        showTool.setActionCommand("tool.visible." + tool.getId());
                        showTool.addActionListener(this);

                        popupMenu.add(showTool);
                    }
                }
                if (popupMenu.getComponentCount() > 0)
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        if (actionCommand.startsWith("tool.visible.")) {
            String toolId = actionCommand.substring(13);
            toolWindowManager.getToolWindow(toolId).setActive(true);
        }
    }

    protected void initPopupMenu() {
        popupMenu = new JPopupMenu("ToolWindowBarContainerPopupMenu");
        popupMenu.setLightWeightPopupEnabled(false);
    }

}
