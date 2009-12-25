package org.noos.xing.mydoggy;

import javax.swing.*;

/**
 * This abstract class is used to define a new behaviour associated to one or more toolwindows.
 *
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @since 1.5.0
 */
public abstract class ToolWindowAction extends AbstractAction {

    // Default action ids...

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
    public static final String INDEX_MENU_ACTION_ID = "INDEX_MENU_ACTION_ID";


    /**
     * Used to identify the actioon
     */
    protected String id;
    /**

     * The action name.
     */
    protected String actionName;

    /**
     * The toolwindow that ownes the action.
     */
    protected ToolWindow toolWindow;

    protected JMenuItem menuItem;

    protected boolean showTextOnTitleBar;
    protected boolean visibleOnTitleBar;
    protected boolean visibleOnMenuBar;


    protected ToolWindowAction(String id) {
        this(id, null);
    }

    protected ToolWindowAction(String id, Icon icon) {
        super(null, icon);
        this.id = id;
        this.showTextOnTitleBar = false;
        this.visibleOnTitleBar = true;
        this.visibleOnMenuBar = true;
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

    public void setText(String text) {
        putValue(Action.NAME, text);
    }

    public String getText() {
        return (String) getValue(Action.NAME);
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

    public boolean isShowTextOnTitleBar() {
        return showTextOnTitleBar;
    }

    public void setShowTextOnTitleBar(boolean showTextOnTitleBar) {
        this.showTextOnTitleBar = showTextOnTitleBar;
    }

    public void setVisible(boolean visible) {
        firePropertyChange("visibleOnTitleBar", !visible, visible);
        firePropertyChange("visibleOnMenuBar", !visible, visible);
//        setVisibleOnMenuBar(visible);
//        setVisibleOnTitleBar(visible);
    }

    public JMenuItem getMenuItem() {
        if (menuItem == null) {
            menuItem = new JMenuItem(this);
        }

        return menuItem;
    }
}
