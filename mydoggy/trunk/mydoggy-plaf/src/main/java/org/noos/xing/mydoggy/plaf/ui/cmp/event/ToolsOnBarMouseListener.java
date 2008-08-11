package org.noos.xing.mydoggy.plaf.ui.cmp.event;

import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowBar;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolsOnBarMouseListener extends MouseAdapter implements ActionListener {
    protected ToolWindowManager toolWindowManager;
    protected ToolWindowBar toolWindowBar;
    protected ToolWindowAnchor anchor;

    protected JPopupMenu popupMenu;


    public ToolsOnBarMouseListener(ToolWindowManager toolWindowManager, ToolWindowBar toolWindowBar) {
        this.toolWindowManager = toolWindowManager;
        this.toolWindowBar = toolWindowBar;
        this.anchor = toolWindowBar.getAnchor();
    }


    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (toolWindowBar.getPopupMenu() != null) {
                toolWindowBar.getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
            } else {
                initPopupMenu();
                
                popupMenu.removeAll();

                ToolWindow[] tools = toolWindowManager.getToolsByAnchor(anchor);
                if (tools.length > 0) {

                    for (ToolWindow tool : tools) {
                        if (tool.isAvailable()) {
                            JMenuItem showTool = new JMenuItem();
                            showTool.setText(SwingUtil.getUserString(tool.getRepresentativeAnchorButtonTitle()));
                            showTool.setActionCommand("tool.visible." + tool.getId());
                            showTool.addActionListener(this);

                            popupMenu.add(showTool);
                        }
                    }

                    if (popupMenu.getComponentCount() > 0)
                        popupMenu.addSeparator();

                    // Add store/restore item
                    JMenuItem showTool = new JMenuItem();
                    showTool.setText(toolWindowBar.isVisible() ? SwingUtil.getString("@@tool.bar.hide") : SwingUtil.getString("@@tool.bar.show"));
                    showTool.setActionCommand("bar.visible");
                    showTool.addActionListener(this);
                    popupMenu.add(showTool);

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }

            }
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        if (actionCommand.startsWith("tool.visible.")) {
            String toolId = actionCommand.substring(13);
            toolWindowManager.getToolWindow(toolId).setActive(true);
        } else if ("bar.visible".equals(actionCommand)) {
            toolWindowBar.setVisible(!toolWindowBar.isVisible());
        }
    }

    protected void initPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu("ToolWindowBarContainerPopupMenu");
            popupMenu.setLightWeightPopupEnabled(false);
        }            
    }

}
