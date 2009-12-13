package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;
import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowType;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.actions.PlafToolWindowAction;
import org.noos.xing.mydoggy.plaf.cleaner.Cleaner;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButton;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowTitleButtonPanel;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowTitleButtonPanelUI extends BasicPanelUI implements Cleaner,
                                                                          PropertyChangeListener {

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowTitleButtonPanelUI();
    }


    protected ToolWindowTitleButtonPanel toolWindowTitleButtonPanel;
    protected MyDoggyToolWindow toolWindow;
    protected ToolWindowDescriptor descriptor;

    protected TableLayout containerLayout;
    protected Component focusable;


    public ToolWindowTitleButtonPanelUI() {
    }


    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("visibleOnTitleBar".equals(propertyName)) {
            ToolWindowAction toolWindowAction = (ToolWindowAction) evt.getSource();

            if (!setVisible((Component) toolWindowAction.getValue("component"), (Boolean) evt.getNewValue())) {
                Integer index = (Integer) toolWindowAction.getValue("constraint");
                addToolWindowAction(toolWindowAction, index != null ? index : -1);
                setVisible((Component) toolWindowAction.getValue("component"), (Boolean) evt.getNewValue());
            }
        } else if ("toolWindowAction".equals(propertyName)) {
            if (SwingUtil.getAt(evt, 0, null) != toolWindow.getTypeDescriptor(toolWindow.getType()))
                return;

            if (evt.getNewValue() != null) {
                if (evt.getOldValue() == null) {
                    // Add the action
                    ToolWindowAction toolWindowAction = (ToolWindowAction) evt.getNewValue();
                    if (toolWindowAction.isVisibleOnTitleBar()) {

                        int index = (Integer) toolWindowAction.getValue("constraint");
                        addToolWindowAction(toolWindowAction, index);

                        SwingUtil.repaint(toolWindowTitleButtonPanel);
                    }
                } else {
                    replaceToolWindowAction((ToolWindowAction) evt.getOldValue(), (ToolWindowAction) evt.getNewValue());
                    SwingUtil.repaint(toolWindowTitleButtonPanel);
                }
            } else {
                // Remove the action
                removeToolWindowAction((ToolWindowAction) evt.getOldValue());
            }
        } else if ("type".equals(propertyName)) {
            setType((ToolWindowType) evt.getOldValue(), (ToolWindowType) evt.getNewValue());
        } else if ("titleBarButtonsVisible".equals(propertyName)) {
            if (((ToolWindowTypeDescriptor)evt.getSource()).getType() == toolWindow.getType()) {
                // modify the visible property value of the panel now...
                toolWindowTitleButtonPanel.setVisible((Boolean) evt.getNewValue());
            }
            
        }
    }

    public void cleanup() {
        uninstallUI(toolWindowTitleButtonPanel);
    }

    public Component getFocusable() {
        return focusable;
    }


    public void installUI(JComponent c) {
        // Init fields...
        this.toolWindowTitleButtonPanel = (ToolWindowTitleButtonPanel) c;

        this.descriptor = toolWindowTitleButtonPanel.getToolWindowDescriptor();
        this.toolWindow = descriptor.getToolWindow();

        super.installUI(c);
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        unistallListeners();
        uninstallComponents();

        // Reset fields
        this.toolWindowTitleButtonPanel = null;
        this.descriptor = null;
        this.toolWindow = null;
    }


    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        toolWindowTitleButtonPanel.setFocusable(false);

        installComponents();
        installListeners();
    }

    protected void installComponents() {
        // Define the layout
        toolWindowTitleButtonPanel.setLayout(containerLayout = new ExtendedTableLayout(new double[][]{{0, 0}, {1, 14, 1}}, false));
        toolWindowTitleButtonPanel.setOpaque(false);

        // Add the default set of actions
        focusable = null;

        DockedTypeDescriptor dockedTypeDescriptor = descriptor.getDockedTypeDescriptor();
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID));
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.MAXIMIZE_ACTION_ID));
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID));
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID));
        addToolWindowAction(dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID));
    }

    protected void installListeners() {
        descriptor.addTypeDescriptorChangePropertyListener(this);
        toolWindow.addPlafPropertyChangeListener(this);

        descriptor.getCleaner().addCleaner(this);

    }

    protected void unistallListeners() {
        descriptor.removeTypeDescriptorChangePropertyListener(this);
        toolWindow.removePlafPropertyChangeListener(this);

        descriptor.getCleaner().removeCleaner(this);
    }

    protected void uninstallComponents() {
        for (Component component : toolWindowTitleButtonPanel.getComponents()) {
            ToolWindowTitleButton toolWindowTitleButton = (ToolWindowTitleButton) component;
            toolWindowTitleButton.getAction().removePropertyChangeListener(this);
        }

        toolWindowTitleButtonPanel.removeAll();
    }


    protected void setType(ToolWindowType oldType, ToolWindowType toolWindowType) {
        if (toolWindowType == ToolWindowType.EXTERN)
            return;

        // Store Current Layout
        storeCurrentLayout(oldType);

        // remove all actions from the container
        uninstallComponents();
        containerLayout.setColumn(new double[]{0,0});

        // add actions for the current type...
        focusable = null;
        
        ToolWindowTypeDescriptor typeDescriptor = descriptor.getTypeDescriptor(toolWindowType);

        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.HIDE_ACTION_ID));
        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.MAXIMIZE_ACTION_ID));
        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID));
        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID));
        addToolWindowAction(typeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID));

        // Add custom actions...
        for (ToolWindowAction toolWindowAction : typeDescriptor.getToolWindowActions()) {
            if (!(toolWindowAction instanceof PlafToolWindowAction))  {
                int index = (Integer) toolWindowAction.getValue("constraint");
                addToolWindowAction(toolWindowAction, index);
            }
        }

        // ensure the visibility of the panel...
        toolWindowTitleButtonPanel.setVisible(toolWindow.getTypeDescriptor(toolWindow.getType()).isTitleBarButtonsVisible());        
    }

    Map<ToolWindowType, Map<ToolWindowAction, Integer>> layouts = new HashMap<ToolWindowType, Map<ToolWindowAction, Integer>>();

    protected void storeCurrentLayout(ToolWindowType toolWindowType) {
        Map<ToolWindowAction, Integer> layout = layouts.get(toolWindowType);
        if (layout == null) {
            layout = new HashMap<ToolWindowAction, Integer>();
            layouts.put(toolWindowType, layout);
        }

        layout.clear();

        for (Component component : toolWindowTitleButtonPanel.getComponents()) {
            ToolWindowTitleButton toolWindowTitleButton = (ToolWindowTitleButton) component;

            layout.put((ToolWindowAction) toolWindowTitleButton.getAction(),
                       toolWindowTitleButtonPanel.getComponentCount() - 1 - containerLayout.getConstraints(toolWindowTitleButton).col1 / 2);
        }
    }

    protected int loadFromLayout(ToolWindowAction toolWindowAction) {
        Map<ToolWindowAction, Integer> layout = layouts.get(toolWindow.getType());
        if (layout != null) {
            Integer pos = layout.get(toolWindowAction);
            return (pos != null) ? pos : -1;
        }
        return -1;
    }

    protected Component addToolWindowAction(ToolWindowAction toolWindowAction) {
        return addToolWindowAction(toolWindowAction, -1);
    }

    protected Component addToolWindowAction(ToolWindowAction toolWindowAction, int index) {
        if (!toolWindowAction.isVisibleOnTitleBar())
            return null;

        if (index == -1)
            index = loadFromLayout(toolWindowAction);

        int col;

        if (index <= -1 || index >= toolWindowTitleButtonPanel.getComponentCount() - 1) {
            double[] oldCols = containerLayout.getColumn();
            double[] newCols;

            if (oldCols.length == 2) {
                // No actions are in place
                newCols = new double[]{0, -2, 0};
                col = 1;
            } else {
                newCols = new double[oldCols.length + 2];

                // Move all components
                for (Component component : toolWindowTitleButtonPanel.getComponents()) {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(component);
                    if (constraints.col1 >= 1) {
                        constraints.col1 += 2;
                        constraints.col2 = constraints.col1;
                        containerLayout.setConstraints(component, constraints);
                    }
                }

                // Prepare the space at the beginning of neCols
                System.arraycopy(oldCols, 1, newCols, 3, oldCols.length - 1);

                // Setup the columns for the new actions
                newCols[1] = -2;
                newCols[2] = 1;

                // setup the column destination
                col = 1;
            }
            containerLayout.setColumn(newCols);
        } else {
            int colIndex = (index != 0 ? 1 : 0) + ((toolWindowTitleButtonPanel.getComponentCount()  - index) * 2);

            for (Component component : toolWindowTitleButtonPanel.getComponents()) {
                TableLayoutConstraints constraints = containerLayout.getConstraints(component);
                if (constraints.col1 >= colIndex) {
                    constraints.col1 += 2;
                    constraints.col2 = constraints.col1;
                    containerLayout.setConstraints(component, constraints);
                }
            }

            double[] oldCols = containerLayout.getColumn();
            double[] newCols;

            newCols = new double[oldCols.length + 2];
            System.arraycopy(oldCols, 0, newCols, 0, colIndex);
            System.arraycopy(oldCols, colIndex, newCols, colIndex + 2, oldCols.length - colIndex - 1);
            newCols[colIndex] = -2;
            newCols[colIndex + 1] = 1;
            col = colIndex;

            containerLayout.setColumn(newCols);
        }

        ToolWindowTitleButton button = new ToolWindowTitleButton(toolWindowAction);
        button.setFocusable(false);
        button.setName(toolWindowAction.getActionName());
        if (!toolWindowAction.isShowTextOnTitleBar())
            button.setText(null);

        toolWindowAction.putValue("component", button);
        toolWindowAction.addPropertyChangeListener(this);

        toolWindowTitleButtonPanel.add(button, col + ",1,FULL,FULL");

        if (focusable == null)
            focusable = button;

        return button;
    }

    protected void removeToolWindowAction(ToolWindowAction toolWindowAction) {
        toolWindowAction.removePropertyChangeListener(this);

        for (Component component : toolWindowTitleButtonPanel.getComponents()) {
            ToolWindowTitleButton toolWindowTitleButton = (ToolWindowTitleButton) component;

            if (toolWindowTitleButton.getAction() == toolWindowAction) {
                // We found the action

                double[] newCols = null;
                if (toolWindowTitleButtonPanel.getComponentCount() == 1) {
                    newCols = new double[]{0, 0};
                } else {
                    TableLayoutConstraints constraints = containerLayout.getConstraints(toolWindowTitleButton);

                    double[] oldCols = containerLayout.getColumn();

                    if (constraints.col1 == oldCols.length - 2) {
                        newCols = new double[oldCols.length - 1];
                        System.arraycopy(oldCols, 0, newCols, 0, constraints.col1);
                        System.arraycopy(oldCols, constraints.col1 + 1, newCols, constraints.col1, 1);
                    } else {
                        for (Component cmp : toolWindowTitleButtonPanel.getComponents()) {
                            TableLayoutConstraints cst = containerLayout.getConstraints(cmp);
                            if (cst.col1 >= constraints.col1 + 2) {
                                cst.col1 -= 2;
                                cst.col2 = cst.col1;
                                containerLayout.setConstraints(cmp, cst);
                            }
                        }

                        newCols = new double[oldCols.length - 2];
                        System.arraycopy(oldCols, 0, newCols, 0, constraints.col1);
                        System.arraycopy(oldCols, constraints.col1 + 2, newCols, constraints.col1, oldCols.length - constraints.col1 - 2);
                    }
                }
                
                containerLayout.setColumn(newCols);
                toolWindowTitleButtonPanel.remove(toolWindowTitleButton);

                SwingUtil.revalidate(toolWindowTitleButtonPanel.getParent());
                SwingUtil.repaint(toolWindowTitleButtonPanel);

                if (focusable == toolWindowTitleButton)
                    focusable = null;

                break;
            }
        }
    }

    protected void replaceToolWindowAction(ToolWindowAction oldToolWindowAction, ToolWindowAction newToolWindowAction) {
        for (Component component : toolWindowTitleButtonPanel.getComponents()) {
            ToolWindowTitleButton toolWindowTitleButton = (ToolWindowTitleButton) component;

            if (toolWindowTitleButton.getAction() == oldToolWindowAction) {
                // We found the action...replace it
                toolWindowTitleButton.setAction(newToolWindowAction);
                break;
            }
        }
    }


    protected boolean setVisible(Component component, boolean visible) {
        boolean found = false;
        for (Component cmp : toolWindowTitleButtonPanel.getComponents()) {
            if (cmp == component) {
                found = true;
                if (visible) {
                    int col = containerLayout.getConstraints(component).col1;
                    containerLayout.setColumn(col, -2);
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
        SwingUtil.repaint(toolWindowTitleButtonPanel);

        return found;
    }

}
