package org.noos.xing.mydoggy.plaf.actions;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MoveToToolWindowAction extends ToolWindowAction implements PropertyChangeListener, PlafToolWindowAction  {

    protected JMenu moveTo;
    protected JMenuItem right;
    protected JMenuItem left;
    protected JMenuItem top;
    protected JMenuItem bottom;


    public MoveToToolWindowAction() {
        super(MOVE_TO_ACTION_ID);
        setTooltipText(SwingUtil.getString("@@tool.tooltip.floatingLive"));
        setVisibleOnMenuBar(true);
        setVisibleOnTitleBar(false);
    }


    public void setToolWindow(final ToolWindow toolWindow) {
        super.setToolWindow(toolWindow);

        setActionName("toolWindow.popup.floatingLive." + toolWindow.getId());
        toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).addPropertyChangeListener("enabled", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                setVisibleOnMenuBar((Boolean) evt.getNewValue());
            }
        }
        );
        toolWindow.addPropertyChangeListener("type", this);
    }

    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if ("move.right".equals(actionCommand)) {
            toolWindow.setAnchor(ToolWindowAnchor.RIGHT);
        } else if ("move.left".equals(actionCommand)) {
            toolWindow.setAnchor(ToolWindowAnchor.LEFT);
        } else if ("move.top".equals(actionCommand)) {
            toolWindow.setAnchor(ToolWindowAnchor.TOP);
        } else if ("move.bottom".equals(actionCommand)) {
            toolWindow.setAnchor(ToolWindowAnchor.BOTTOM);
        }
    }

    public JMenuItem getMenuItem() {
        if (moveTo == null) {
            moveTo = new JMenu();
            moveTo.getPopupMenu().setLightWeightPopupEnabled(false);
            moveTo.setText(SwingUtil.getString("@@tool.moveTo"));

            right = new JMenuItem();
            right.setText(SwingUtil.getString("@@tool.move.right"));
            right.setActionCommand("move.right");
            right.addActionListener(this);

            left = new JMenuItem();
            left.setText(SwingUtil.getString("@@tool.move.left"));
            left.setActionCommand("move.left");
            left.addActionListener(this);

            top = new JMenuItem();
            top.setText(SwingUtil.getString("@@tool.move.top"));
            top.setActionCommand("move.top");
            top.addActionListener(this);

            bottom = new JMenuItem();
            bottom.setText(SwingUtil.getString("@@tool.move.bottom"));
            bottom.setActionCommand("move.bottom");
            bottom.addActionListener(this);

            moveTo.add(right);
            moveTo.add(left);
            moveTo.add(top);
            moveTo.add(bottom);
        }

        if (toolWindow.isLockedOnAnchor()) {
            ToolWindowAnchor[] anchors = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getLockingAnchors();

            if (anchors.length == 0) {
                moveTo.setVisible(false);
                return moveTo;
            }

            left.setVisible(false);
            right.setVisible(false);
            top.setVisible(false);
            bottom.setVisible(false);

            for (ToolWindowAnchor anchor : anchors) {
                switch (anchor) {
                    case LEFT:
                        left.setVisible(true);
                        break;
                    case RIGHT:
                        right.setVisible(true);
                        break;
                    case TOP:
                        top.setVisible(true);
                        break;
                    case BOTTOM:
                        bottom.setVisible(true);
                        break;
                }
            }
        } else {
            ToolWindowAnchor anchor = toolWindow.getAnchor();
            if (anchor == ToolWindowAnchor.LEFT) {
                left.setVisible(false);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.RIGHT) {
                left.setVisible(true);
                right.setVisible(false);
                top.setVisible(true);
                bottom.setVisible(true);
            } else if (anchor == ToolWindowAnchor.BOTTOM) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(true);
                bottom.setVisible(false);
            } else if (anchor == ToolWindowAnchor.TOP) {
                left.setVisible(true);
                right.setVisible(true);
                top.setVisible(false);
                bottom.setVisible(true);
            }
        }


        return moveTo;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        ToolWindowType type = (ToolWindowType) evt.getNewValue();
    }

}