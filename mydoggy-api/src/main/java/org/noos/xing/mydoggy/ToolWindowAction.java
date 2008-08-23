package org.noos.xing.mydoggy;

import javax.swing.*;

/**
 * TODO: add javadocs...
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public abstract class ToolWindowAction extends AbstractAction {

    public static final String HIDE_ACTION_ID = "HIDE_ACTION_ID";
    public static final String DOCK_ACTION_ID = "DOCK_ACTION_ID";
    public static final String PIN_ACTION_ID = "PIN_ACTION_ID";
    public static final String MAXIMIZE_ACTION_ID = "MAXIMIZE_ACTION_ID";
    public static final String FLOATING_ACTION_ID = "FLOATING_ACTION_ID";
    public static final String FLOATING_LIVE_ACTION_ID = "FLOATING_LIVE_ACTION_ID";
    public static final String POPUP_ACTION_ID = "POPUP_ACTION_ID";
    public static final String MOVE_TO_ACTION_ID = "MOVE_TO_ACTION_ID";
    public static final String AGGREGATE_ACTION_ID = "AGGREGATE_ACTION_ID";
    public static final String AGGREGATE_MENU_ACTION_ID = "AGGREGATE_MENU_ACTION_ID";
    public static final String TOOLS_MENU_ACTION_ID = "TOOLS_MENU_ACTION_ID";


    protected String id;
    protected String actionName;
    protected ToolWindow toolWindow;

    protected boolean visibleOnTitleBar = true;
    protected boolean visibleOnMenuBar = true;


    protected ToolWindowAction(String id) {
        this.id = id;
    }

    protected ToolWindowAction(String id, Icon icon) {
        super(null, icon);
        this.id = id;
    }


    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }


    public String getId() {
        return id;
    }

    public void setIcon(Icon icon) {
        putValue(Action.SMALL_ICON, icon);
    }

    public Icon getIcon() {
        return (Icon) getValue(Action.SMALL_ICON);
    }

    public void setTooltipText(String tooltipText) {
        putValue(Action.SHORT_DESCRIPTION, tooltipText);
    }

    public String getTooltipText() {
        return (String) getValue(Action.SHORT_DESCRIPTION);
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public boolean isVisibleOnTitleBar() {
        return visibleOnTitleBar;
    }

    public void setVisibleOnTitleBar(boolean visibleOnTitleBar) {
        if (this.visibleOnTitleBar == visibleOnTitleBar)
            return;

        boolean old = this.visibleOnTitleBar;
        this.visibleOnTitleBar = visibleOnTitleBar;

        firePropertyChange("visibleOnTitleBar", old, visibleOnTitleBar);
    }

    public boolean isVisibleOnMenuBar() {
        return visibleOnMenuBar;
    }

    public void setVisibleOnMenuBar(boolean visibleOnMenuBar) {
        if (this.visibleOnMenuBar == visibleOnMenuBar)
            return;

        boolean old = this.visibleOnMenuBar;
        this.visibleOnMenuBar = visibleOnMenuBar;

        firePropertyChange("visibleOnMenuBar", old, visibleOnMenuBar);
    }

    public void setVisible(boolean visible) {
        setVisibleOnMenuBar(visible);
        setVisibleOnTitleBar(visible);
    }

    public int getWidth() {
        int width = getIcon() != null ? getIcon().getIconWidth() : 0;
        return (width > 0) ? width : 13;
    }

    public JMenuItem getMenuItem() {
        return null;
    }
}
