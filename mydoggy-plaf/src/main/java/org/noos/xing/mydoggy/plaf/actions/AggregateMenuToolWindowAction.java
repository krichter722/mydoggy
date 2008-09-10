package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.AggregationPosition;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class AggregateMenuToolWindowAction extends ToolWindowAction implements PlafToolWindowAction {

    protected JMenuItem menuItem;

    public AggregateMenuToolWindowAction() {
        super(AGGREGATE_MENU_ACTION_ID);
        setVisibleOnTitleBar(false);
    }


    public void setToolWindow(ToolWindow toolWindow) {
        super.setToolWindow(toolWindow);

        setActionName("toolWindow.popup.aggegateMenu." + toolWindow.getId());
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JMenu(SwingUtil.getString("@@tool.aggregateMenu"));

            JMenuItem aggregateLeft = new JMenuItem();
            aggregateLeft.setName("toolWindow.popup.aggregate.left." + toolWindow.getId());
            aggregateLeft.setText(SwingUtil.getString("@@tool.aggregate.left"));
            aggregateLeft.setActionCommand("aggregate.left");
            aggregateLeft.addActionListener(this);
            menuItem.add(aggregateLeft);

            JMenuItem aggregateRight = new JMenuItem();
            aggregateRight.setName("toolWindow.popup.aggregate.right." + toolWindow.getId());
            aggregateRight.setText(SwingUtil.getString("@@tool.aggregate.right"));
            aggregateRight.setActionCommand("aggregate.right");
            aggregateRight.addActionListener(this);
            menuItem.add(aggregateRight);

            JMenuItem aggregateTop = new JMenuItem();
            aggregateTop.setName("toolWindow.popup.aggregate.top." + toolWindow.getId());
            aggregateTop.setText(SwingUtil.getString("@@tool.aggregate.top"));
            aggregateTop.setActionCommand("aggregate.top");
            aggregateTop.addActionListener(this);
            menuItem.add(aggregateTop);

            JMenuItem aggregateBottom = new JMenuItem();
            aggregateBottom.setName("toolWindow.popup.aggregate.bottom." + toolWindow.getId());
            aggregateBottom.setText(SwingUtil.getString("@@tool.aggregate.bottom"));
            aggregateBottom.setActionCommand("aggregate.bottom");
            aggregateBottom.addActionListener(this);
            menuItem.add(aggregateBottom);
        }

        menuItem.setVisible(!toolWindow.isVisible());

        return menuItem;
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (toolWindow.isActive()) {
            toolWindow.setActive(false);
            toolWindow.setVisible(false);
        } else if (toolWindow.isVisible())
            toolWindow.setVisible(false);
        else {
            if (actionCommand.endsWith("left"))
                toolWindow.aggregate(AggregationPosition.LEFT);
            else if (actionCommand.endsWith("right"))
                toolWindow.aggregate(AggregationPosition.RIGHT);
            else if (actionCommand.endsWith("top"))
                toolWindow.aggregate(AggregationPosition.TOP);
            else if (actionCommand.endsWith("bottom"))
                toolWindow.aggregate(AggregationPosition.BOTTOM);
            else
                toolWindow.aggregate();

            toolWindow.setActive(true);
        }
    }

}