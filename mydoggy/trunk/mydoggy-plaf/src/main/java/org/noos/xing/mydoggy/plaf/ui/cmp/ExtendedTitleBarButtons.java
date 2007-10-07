package org.noos.xing.mydoggy.plaf.ui.cmp;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import info.clearthought.layout.TableLayout;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ExtendedTitleBarButtons implements TitleBarButtons {
    protected ToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;
    protected ResourceManager resourceManager;
    protected DockedContainer dockedContainer;

    protected JPanel buttonContainer;

    protected JButton floatingButton;
    protected JButton pinButton;
    protected JButton dockButton;
    protected JButton hideButton;
    protected JButton maximizeButton;

    public ExtendedTitleBarButtons(ToolWindowDescriptor toolWindowDescriptor, DockedContainer dockedContainer) {
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
        switch (type) {
            case DOCKED:
                setPinVisible(true);

                setFloating();
                setFloatingVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());

                setDockedVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
                setDocked();
                break;
            case FLOATING_LIVE:
                setPinVisible(true);
                setFloating();
                setFloatingVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
                setDockedVisible(((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).isEnabled());
                setSliding();
                break;
            case SLIDING:
                setPinVisible(false);
                setFloatingVisible(((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isEnabled());
                setSliding();
                break;
            case FLOATING:
            case FLOATING_FREE:
                FloatingTypeDescriptor typeDescriptor = (FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING);

                if (typeDescriptor.isModal()) {
                    setPinVisible(false);
                    setFloatingVisible(false);
                    setDockedVisible(false);
                } else {
                    setPinVisible(true);
                    setFloatingVisible(true);
                    setDockedVisible(false);
                    setFix();
                }
                break;
        }
    }

    protected void initComponents() {
        buttonContainer = new JPanel(new ExtendedTableLayout(new double[][]{{13, 1, 13, 1, 13, 1, 13, 1, 13}, {1, 14, 1}}, false));
        buttonContainer.setOpaque(false);

        // Buttons
        ActionListener titleBarActionListener = new TitleBarActionListener();

        hideButton = renderTitleButton("visible", titleBarActionListener,
                                       "@@tool.tooltip.hide", MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE,
                                       null);
        maximizeButton = renderTitleButton("maximize", titleBarActionListener, "@@tool.tooltip.maximize", MyDoggyKeySpace.MAXIMIZE_INACTIVE, null);
        pinButton = renderTitleButton("pin", titleBarActionListener, "@@tool.tooltip.unpin", MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE, null);
        floatingButton = renderTitleButton("floating", titleBarActionListener,
                                           "@@tool.tooltip.float", MyDoggyKeySpace.FLOATING_INACTIVE,
                                           "toolWindow.floatingButton." + toolWindow.getId());
        dockButton = renderTitleButton("undock", titleBarActionListener,
                                       "@@tool.tooltip.undock", MyDoggyKeySpace.DOCKED_INACTIVE,
                                       "toolWindow.dockButton." + toolWindow.getId());

        buttonContainer.add(dockButton, "0,1");
        buttonContainer.add(floatingButton, "2,1");
        buttonContainer.add(pinButton, "4,1");
        buttonContainer.add(maximizeButton, "6,1");
        buttonContainer.add(hideButton, "8,1");
    }

    protected void initListeners() {
        dockedContainer.addPropertyChangeListener("autoHide", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean newValue = ((Boolean) evt.getNewValue());

                if (newValue) {
                    pinButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                    pinButton.setToolTipText(resourceManager.getString("@@tool.tooltip.pin"));
                } else {
                    pinButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));
                    pinButton.setToolTipText(resourceManager.getString("@@tool.tooltip.unpin"));
                }
            }
        });
        dockedContainer.addPropertyChangeListener("maximized.before", new PropertyChangeListener() {
            private boolean flag = false;
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                if ((Boolean) evt.getNewValue()) {
                    maximizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                    flag = true;
                } else if (flag) {
                    maximizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
                    flag = false;
                }
            }
        });
        dockedContainer.addPropertyChangeListener("active", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() != descriptor)
                    return;

                boolean active = (Boolean) evt.getNewValue();

                if (active) {
                    if (toolWindow.isAutoHide()) {
                        pinButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                    } else
                        pinButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));

                    hideButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW));

                    if (toolWindow.getType() == ToolWindowType.SLIDING || toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                        dockButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.DOCKED));
                    } else
                        dockButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.SLIDING));

                    if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                        floatingButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.FIX));
                    } else
                        floatingButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.FLOATING));

                    if (toolWindow.isMaximized())
                        maximizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                    else
                        maximizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
                } else {
                    if (toolWindow.isAutoHide()) {
                        pinButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON_INACTIVE));
                    } else
                        pinButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE));

                    hideButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE));

                    if (toolWindow.getType() == ToolWindowType.SLIDING || toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                        dockButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.DOCKED_INACTIVE));
                    } else
                        dockButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.SLIDING_INACTIVE));

                    if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                        floatingButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.FIX_INACTIVE));
                    } else
                        floatingButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.FLOATING_INACTIVE));

                    if (toolWindow.isMaximized())
                        maximizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.MINIMIZE_INACTIVE));
                    else
                        maximizeButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.MAXIMIZE_INACTIVE));
                }
            }
        });

        ((SlidingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.SLIDING)).addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) 
                            setDockedVisible((Boolean) evt.getNewValue());
                    }
                }
        );
        ((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).addPropertyChangeListener(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) 
                            setFloatingVisible((Boolean) evt.getNewValue());
                    }
                }
        );

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

    protected void setPinVisible(boolean visible) {
        pinButton.setVisible(visible);
        TableLayout tableLayout = (TableLayout) pinButton.getParent().getLayout();
        tableLayout.setColumn(4, (visible) ? 13 : 0);
        tableLayout.setColumn(5, (visible) ? 1 : 0);
    }

    protected void setFloatingVisible(boolean visible) {
        floatingButton.setVisible(visible);
        TableLayout tableLayout = (TableLayout) pinButton.getParent().getLayout();
        tableLayout.setColumn(2, (visible) ? 13 : 0);
        tableLayout.setColumn(3, (visible) ? 1 : 0);
    }

    protected void setDockedVisible(boolean visible) {
        dockButton.setVisible(visible);
        TableLayout tableLayout = (TableLayout) pinButton.getParent().getLayout();
        tableLayout.setColumn(0, (visible) ? 13 : 0);
        tableLayout.setColumn(1, (visible) ? 1 : 0);
    }

    protected void setSliding() {
        dockButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.DOCKED));
        dockButton.setToolTipText(resourceManager.getString("@@tool.tooltip.dock"));
    }

    protected void setDocked() {
        dockButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.SLIDING));
        dockButton.setToolTipText(resourceManager.getString("@@tool.tooltip.undock"));
    }

    protected void setFix() {
        floatingButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.FIX));
        floatingButton.setToolTipText(resourceManager.getString("@@tool.tooltip.fix"));
    }

    protected void setFloating() {
        floatingButton.setIcon(resourceManager.getIcon(MyDoggyKeySpace.FLOATING));
        floatingButton.setToolTipText(resourceManager.getString("@@tool.tooltip.float"));
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
            } else if ("pin".equals(actionCommnad)) {
                toolWindow.setAutoHide(!toolWindow.isAutoHide());
            } else if ("floating".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.FLOATING || type == ToolWindowType.FLOATING_FREE) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                } else if (type == ToolWindowType.DOCKED || type == ToolWindowType.SLIDING || type == ToolWindowType.FLOATING_LIVE) {
                    toolWindow.setType(descriptor.isFloatingWindow() ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
                }
            } else if ("undock".equals(actionCommnad)) {
                ToolWindowType type = toolWindow.getType();
                if (type == ToolWindowType.DOCKED) {
                    toolWindow.setType(ToolWindowType.SLIDING);
                } else if (type == ToolWindowType.SLIDING || type == ToolWindowType.FLOATING_LIVE) {
                    toolWindow.setType(ToolWindowType.DOCKED);
                }
            } else if ("maximize".equals(actionCommnad)) {
                toolWindow.setMaximized(!toolWindow.isMaximized());
            }
        }

    }

}
