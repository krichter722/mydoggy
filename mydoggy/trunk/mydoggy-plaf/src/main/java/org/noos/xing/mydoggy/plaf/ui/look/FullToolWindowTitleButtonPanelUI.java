package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.FloatingTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowContainer;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButtonPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class FullToolWindowTitleButtonPanelUI extends ToolWindowTitleButtonPanelUI implements Cleaner {


    public static ComponentUI createUI(JComponent c) {
        return new FullToolWindowTitleButtonPanelUI((ToolWindowDescriptor) c.getClientProperty(ToolWindowDescriptor.class),
                                                    (ToolWindowContainer) c.getClientProperty(ToolWindowContainer.class));
    }


    protected ToolWindowTitleButtonPanel toolWindowTitleButtonPanel;
    protected ToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;
    protected transient ResourceManager resourceManager;
    protected ToolWindowContainer dockedContainer;

    protected TableLayout containerLayout;
    protected Component focusable;

    protected PropertyChangeSupport propertyChangeSupport;

    
    public FullToolWindowTitleButtonPanelUI(ToolWindowDescriptor toolWindowDescriptor,
                                            ToolWindowContainer dockedContainer) {
        this.descriptor = toolWindowDescriptor;
        this.toolWindow = toolWindowDescriptor.getToolWindow();
        this.resourceManager = dockedContainer.getResourceManager();
        this.dockedContainer = dockedContainer;
        this.propertyChangeSupport = new SwingPropertyChangeSupport(this);
    }


    @Override
    protected void installDefaults(JPanel p) {
        this.toolWindowTitleButtonPanel = (ToolWindowTitleButtonPanel) p;

        super.installDefaults(p);

        initComponents();
        initListeners();
    }

    public void cleanup() {
        descriptor = null;
        toolWindow = null;
        dockedContainer = null;
        resourceManager = null;
    }

    public Component getFocusable() {
        return focusable;
    }

    public void setType(ToolWindowType type) {
        propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, "type", null, type));
    }


    protected void initComponents() {
        toolWindowTitleButtonPanel.setLayout(containerLayout = new ExtendedTableLayout(new double[][]{{0, 0}, {1, 14, 1}}, false));
        toolWindowTitleButtonPanel.setOpaque(false);

        addTitleBarAction(new DockAction());
        addTitleBarAction(new FloatingAction());
        addTitleBarAction(new PinAction());
        addTitleBarAction(new MaximizeAction());
        focusable = addTitleBarAction(new HideAction());
    }

    protected void initListeners() {
        descriptor.getCleaner().addCleaner(this);

    }


    protected Component addTitleBarAction(TitleBarAction titleBarAction) {
        return addTitleBarAction(-1, titleBarAction);
    }

    protected Component addTitleBarAction(int index, TitleBarAction titleBarAction) {
        int row;
        if (index == -1) {
            double[] oldCols = containerLayout.getColumn();

            double[] newCols;
            if (oldCols.length == 2) {
                newCols = new double[]{0, titleBarAction.getWidth(), 0};
                row = 1;
            } else {
                newCols = new double[oldCols.length + 2];

                System.arraycopy(oldCols, 0, newCols, 0, oldCols.length);
                newCols[oldCols.length - 1] = 1;
                newCols[oldCols.length] = titleBarAction.getWidth();
                newCols[oldCols.length + 1] = 0;
                row = oldCols.length;
            }
            containerLayout.setColumn(newCols);
        } else {
            throw new IllegalStateException("Not implemented yet!!!");
        }

        JButton button = new ToolWindowTitleButton(titleBarAction);
        button.setName((String) titleBarAction.getValue("action.name"));
        titleBarAction.putValue("component", button);

        toolWindowTitleButtonPanel.add(button, row + ",1,FULL,FULL");

        return button;
    }

    protected void setVisible(Component component, boolean visible) {
        for (Component cmp : toolWindowTitleButtonPanel.getComponents()) {
            if (cmp == component) {
                if (visible) {
                    int col = containerLayout.getConstraints(component).col1;
                    containerLayout.setColumn(col, 13);
                    if (col != containerLayout.getColumn().length - 1)
                        containerLayout.setColumn(col + 1, 1);
                } else {
                    int col = containerLayout.getConstraints(component).col1;
                    containerLayout.setColumn(col, 0);
                    if (col != containerLayout.getColumn().length - 1)
                        containerLayout.setColumn(col + 1, 0);
                }
            }
        }

    }


    protected abstract class TitleBarAction extends AbstractAction implements PropertyChangeListener {

        protected TitleBarAction() {
            propertyChangeSupport.addPropertyChangeListener(this);
        }

        protected TitleBarAction(String name, String icon, String tooltip) {
            putValue("action.name", name);
            putValue(Action.SMALL_ICON, UIManager.getIcon(icon));
            putValue(Action.SHORT_DESCRIPTION, resourceManager.getString(tooltip));
            propertyChangeSupport.addPropertyChangeListener(this);
        }

        public void setVisible(boolean visible) {
            FullToolWindowTitleButtonPanelUI.this.setVisible((Component) getValue("component"), visible);
            SwingUtil.repaint(toolWindowTitleButtonPanel);
        }

        public int getWidth() {
            int width = ((Icon) getValue(Action.SMALL_ICON)).getIconWidth();
            return (width > 0) ? width : 13;
        }

    }

    public class HideAction extends TitleBarAction {

        public HideAction() {
            super("toolWindow.hideButton." + toolWindow.getId(), MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE, "@@tool.tooltip.hide");
            dockedContainer.addPropertyChangeListener("active", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor)
                        return;

                    boolean active = (Boolean) evt.getNewValue();

                    if (active) {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW));
                    } else {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.HIDE_TOOL_WINDOW_INACTIVE));
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            descriptor.hideToolWindow();
        }

        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    public class DockAction extends TitleBarAction {

        public DockAction() {
            super("toolWindow.dockButton." + toolWindow.getId(), MyDoggyKeySpace.DOCKED_INACTIVE, "@@tool.tooltip.undock");
            descriptor.getTypeDescriptor(ToolWindowType.SLIDING).addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            if ("enabled".equals(evt.getPropertyName()))
                                setVisible((Boolean) evt.getNewValue());
                        }
                    }
            );
            dockedContainer.addPropertyChangeListener("active", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor)
                        return;

                    boolean active = (Boolean) evt.getNewValue();

                    if (active) {
                        if (toolWindow.getType() == ToolWindowType.SLIDING || toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.DOCKED));
                        } else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.SLIDING));
                    } else {
                        if (toolWindow.getType() == ToolWindowType.SLIDING || toolWindow.getType() == ToolWindowType.FLOATING_LIVE) {
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.DOCKED_INACTIVE));
                        } else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.SLIDING_INACTIVE));
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            ToolWindowType type = toolWindow.getType();
            if (type == ToolWindowType.DOCKED) {
                toolWindow.setType(ToolWindowType.SLIDING);
            } else if (type == ToolWindowType.SLIDING || type == ToolWindowType.FLOATING_LIVE) {
                toolWindow.setType(ToolWindowType.DOCKED);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();
            switch (type) {
                case DOCKED:
                    putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.SLIDING));
                    putValue(Action.SHORT_DESCRIPTION, resourceManager.getString("@@tool.tooltip.undock"));
                    setVisible(descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                    break;
                case FLOATING_LIVE:
                    putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.DOCKED));
                    putValue(Action.SHORT_DESCRIPTION, resourceManager.getString("@@tool.tooltip.dock"));
                    setVisible(descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                    break;
                case SLIDING:
                    putValue(Action.SHORT_DESCRIPTION, resourceManager.getString("@@tool.tooltip.dock"));
                    setVisible(descriptor.getTypeDescriptor(ToolWindowType.SLIDING).isEnabled());
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    setVisible(false);
                    break;
            }
        }
    }

    public class PinAction extends TitleBarAction {

        public PinAction() {
            super("toolWindow.pinButton." + toolWindow.getId(), MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE, "@@tool.tooltip.unpin");
            dockedContainer.addPropertyChangeListener("autoHide", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    boolean newValue = ((Boolean) evt.getNewValue());

                    if (newValue) {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                        putValue(Action.LONG_DESCRIPTION, resourceManager.getString("@@tool.tooltip.pin"));
                    } else {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));
                        putValue(Action.LONG_DESCRIPTION, resourceManager.getString("@@tool.tooltip.unpin"));
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
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON));
                        } else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF));
                    } else {
                        if (toolWindow.isAutoHide()) {
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_ON_INACTIVE));
                        } else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.AUTO_HIDE_OFF_INACTIVE));
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);
            toolWindow.setAutoHide(!toolWindow.isAutoHide());
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();
            switch (type) {
                case DOCKED:
                case FLOATING_LIVE:
                    setVisible(true);
                    break;
                case SLIDING:
                    setVisible(false);
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    setVisible(!((FloatingTypeDescriptor) descriptor.getTypeDescriptor(ToolWindowType.FLOATING)).isModal());
                    break;
            }
        }
    }

    public class MaximizeAction extends TitleBarAction {

        public MaximizeAction() {
            super("toolWindow.maximizeButton." + toolWindow.getId(), MyDoggyKeySpace.MAXIMIZE_INACTIVE, "@@tool.tooltip.maximize");
            dockedContainer.addPropertyChangeListener("maximized.before", new PropertyChangeListener() {
                private boolean flag = false;

                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor)
                        return;

                    if ((Boolean) evt.getNewValue()) {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                        flag = true;
                    } else if (flag) {
                        putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
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
                        if (toolWindow.isMaximized())
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.MINIMIZE));
                        else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE));
                    } else {
                        if (toolWindow.isMaximized())
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.MINIMIZE_INACTIVE));
                        else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.MAXIMIZE_INACTIVE));
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            toolWindow.setMaximized(!toolWindow.isMaximized());
        }

        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    public class FloatingAction extends TitleBarAction {
        protected ToolWindowType oldType;

        public FloatingAction() {
            super("toolWindow.floatingButton." + toolWindow.getId(), MyDoggyKeySpace.FLOATING_INACTIVE, "@@tool.tooltip.float");
            descriptor.getTypeDescriptor(ToolWindowType.FLOATING).addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            if ("enabled".equals(evt.getPropertyName()))
                                setVisible((Boolean) evt.getNewValue());
                        }
                    }
            );
            dockedContainer.addPropertyChangeListener("type", new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor ||
                        (evt.getNewValue() != ToolWindowType.FLOATING &&
                         evt.getNewValue() != ToolWindowType.FLOATING_FREE))
                        return;

                    oldType = (ToolWindowType) evt.getOldValue();
                }
            });

            dockedContainer.addPropertyChangeListener("active", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getSource() != descriptor)
                        return;

                    boolean active = (Boolean) evt.getNewValue();

                    if (active) {
                        if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.FIX));
                        } else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.FLOATING));
                    } else {
                        if (toolWindow.getType() == ToolWindowType.FLOATING || toolWindow.getType() == ToolWindowType.FLOATING_FREE) {
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.FIX_INACTIVE));
                        } else
                            putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.FLOATING_INACTIVE));
                    }
                }
            });
        }

        public void actionPerformed(ActionEvent e) {
            toolWindow.setActive(true);

            ToolWindowType type = toolWindow.getType();
            switch (type) {
                case FLOATING:
                case FLOATING_FREE:
                    toolWindow.setType(oldType != null ? oldType : ToolWindowType.DOCKED);
                    break;
                default:
                    toolWindow.setType(descriptor.isFloatingWindow() ? ToolWindowType.FLOATING_FREE : ToolWindowType.FLOATING);
            }
        }

        public void propertyChange(PropertyChangeEvent evt) {
            ToolWindowType type = (ToolWindowType) evt.getNewValue();
            switch (type) {
                case DOCKED:
                case FLOATING_LIVE:
                    putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.FLOATING));
                    putValue(Action.SHORT_DESCRIPTION, resourceManager.getString("@@tool.tooltip.float"));
                    setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                    break;
                case SLIDING:
                    setVisible(descriptor.getTypeDescriptor(ToolWindowType.FLOATING).isEnabled());
                    break;
                case FLOATING:
                case FLOATING_FREE:
                    putValue(Action.SMALL_ICON, UIManager.getIcon(MyDoggyKeySpace.FIX));
                    putValue(Action.SHORT_DESCRIPTION, resourceManager.getString("@@tool.tooltip.fix"));
                    setVisible(true);
                    break;
            }
        }
    }

}