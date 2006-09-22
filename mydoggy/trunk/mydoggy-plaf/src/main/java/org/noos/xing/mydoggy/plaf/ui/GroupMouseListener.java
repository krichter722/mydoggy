package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.ToolWindowGroup;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class GroupMouseListener extends MouseAdapter implements ActionListener {
    private ToolWindowDescriptor descriptor;

    private JPopupMenu popupMenu;

    private JMenu groupTo;
    private JMenuItem addNewGroup;

    public GroupMouseListener(ToolWindowDescriptor descriptor) {
        this.descriptor = descriptor;
        initPopupMenu();
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {

            groupTo.removeAll();
            ToolWindowGroup[] groups = descriptor.getManager().getToolWindowGroups();
            for (ToolWindowGroup group : groups) {
                JMenu addToGroup = new JMenu();
                addToGroup.getPopupMenu().setLightWeightPopupEnabled(false);
                addToGroup.setText(group.getName());

                JMenuItem showGroup = new JMenuItem();
                showGroup.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.group.visible"));
                showGroup.setActionCommand("group.visible." + group.getName());
                showGroup.addActionListener(this);

                addToGroup.add(showGroup);

                groupTo.add(addToGroup, 0);
            }
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        if (actionCommand.startsWith("group.visible.")) {
            String group = actionCommand.substring(14);
            descriptor.getManager().getToolWindowGroup(group).setVisible(true);
        }
    }

    protected void initPopupMenu() {
        popupMenu = new JPopupMenu("ToolWindowBarContainerPopupMenu");
        popupMenu.setLightWeightPopupEnabled(false);

        groupTo = new JMenu();
        groupTo.getPopupMenu().setLightWeightPopupEnabled(false);
        groupTo.setText(ResourceBoundles.getResourceBoundle().getString("@@tool.groupTo"));

        popupMenu.add(groupTo);
    }

}
