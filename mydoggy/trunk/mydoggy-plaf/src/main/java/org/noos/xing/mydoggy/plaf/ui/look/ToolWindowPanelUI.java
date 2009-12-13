package org.noos.xing.mydoggy.plaf.ui.look;

import info.clearthought.layout.TableLayout;
import org.noos.xing.mydoggy.ToolWindowTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.ExtendedTableLayout;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowPanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.border.LineBorder;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowPanelUI extends BasicPanelUI implements PropertyChangeListener {

    public static ComponentUI createUI(JComponent c) {
        return new ToolWindowPanelUI();
    }


    protected ToolWindowPanel toolWindowPanel;
    protected TableLayout toolWindowPanelLayout;


    @Override
    public void installUI(JComponent c) {
        // Init fields
        this.toolWindowPanel = (ToolWindowPanel) c;

        super.installUI(c);
    }

    @Override
    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);

        // Reset fields
        toolWindowPanel = null;
    }

    @Override
    protected void installDefaults(JPanel p) {
        super.installDefaults(p);

        installComponents();
        installListeners();
    }

    @Override
    protected void uninstallDefaults(JPanel p) {
        super.uninstallDefaults(p);

        uninstallListeners();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("titleBarVisible".equals(propertyName)) {
            if (((ToolWindowTypeDescriptor) evt.getSource()).getType() == toolWindowPanel.getToolWindowDescriptor().getToolWindow().getType()) {
                if ((Boolean) evt.getNewValue()) {
                    toolWindowPanelLayout.setColumn(0, SwingUtil.getInt("ToolWindowTitleBarUI.length", 16));
                } else {
                    toolWindowPanelLayout.setRow(0, 0);
                }
            }
        }
    }

    public void setComponent(Component component) {
        JPanel componentContainer = toolWindowPanel.getComponentContainer();

        componentContainer.removeAll();
        componentContainer.add(component, "0,0,FULL,FULL");

        SwingUtil.repaint(componentContainer);
    }

    public void updateComponent() {
        JPanel componentContainer = toolWindowPanel.getComponentContainer();

        componentContainer.removeAll();
        if (toolWindowPanel.getComponent() !=  null)
            componentContainer.add(toolWindowPanel.getComponent(), "0,0,FULL,FULL");

        SwingUtil.repaint(componentContainer);
    }

    public Component getComponent() {
        JPanel componentContainer = toolWindowPanel.getComponentContainer();

        return componentContainer.getComponent(0);
    }


    public void removeComponent() {
        if (toolWindowPanel.getComponent() == null)
            return;
        JPanel componentContainer = toolWindowPanel.getComponentContainer();
        componentContainer.remove(toolWindowPanel.getComponent());

        SwingUtil.repaint(componentContainer);
    }


    protected void installComponents() {
        toolWindowPanel.setLayout(toolWindowPanelLayout = new ExtendedTableLayout(new double[][]{{TableLayout.FILL},
                                                                                                 {SwingUtil.getInt("ToolWindowTitleBarUI.length", 16), TableLayout.FILL}},
                                                                                  false));
        toolWindowPanel.setBorder(new LineBorder(Color.GRAY, 1, true, 3, 3));

        JPanel componentContainer = toolWindowPanel.getComponentContainer();
        componentContainer.setLayout(new ExtendedTableLayout(new double[][]{{-1}, {-1}}));
        componentContainer.removeAll();
        componentContainer.add(toolWindowPanel.getToolWindowDescriptor().getComponent(), "0,0,FULL,FULL");
        componentContainer.setBorder(null);

        toolWindowPanel.removeAll();
        toolWindowPanel.add(toolWindowPanel.getToolWindowTitleBar(), "0,0");
        toolWindowPanel.add(componentContainer, "0,1");

        toolWindowPanel.getToolWindowDescriptor().getToolWindow().getToolWindowTabs()[0].setSelected(true);
    }

    protected void installListeners() {
        toolWindowPanel.getToolWindowDescriptor().addTypeDescriptorChangePropertyListener(this);
    }

    protected void uninstallListeners() {
        toolWindowPanel.getToolWindowDescriptor().removeTypeDescriptorChangePropertyListener(this);
    }

}
