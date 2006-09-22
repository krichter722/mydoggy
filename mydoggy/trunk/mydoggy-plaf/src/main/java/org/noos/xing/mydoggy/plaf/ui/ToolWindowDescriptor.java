package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.icons.CompositeIcon;
import org.noos.xing.mydoggy.plaf.ui.icons.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.util.Colors;

import javax.swing.*;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowDescriptor implements PropertyChangeListener {

    private MyDoggyToolWindowManager manager;
    private MyDoggyToolWindow toolWindow;

    private Window windowAnchestor;
    private Component component;
    private JLabel barLabel;
    private ToolWindowContainer toolWindowContainer;

    private int divederLocation = -1;

    private FloatingTypeDescriptor floatingTypeDescriptor;
    private DockedTypeDescriptor dockedTypeDescriptor;

    private boolean floatingWindow = false;


    public ToolWindowDescriptor(MyDoggyToolWindowManager manager, MyDoggyToolWindow toolWindow, Window windowAnchestor, Component component) {
        this.manager = manager;
        this.windowAnchestor = windowAnchestor;
        this.component = component;
        this.toolWindow = toolWindow;

        toolWindow.addInternalPropertyChangeListener(this);

        initTypeDescriptors();
    }

    private void initTypeDescriptors() {
        floatingTypeDescriptor = (FloatingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING)).cloneMe();
        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if ("type".equals(evt.getPropertyName())) {
            if (evt.getOldValue() == ToolWindowType.FLOATING_WINDOW || evt.getNewValue() == ToolWindowType.FLOATING_WINDOW)
                setFloatingWindow(true);
            else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                setFloatingWindow(false);
        } else if ("index".equals(evt.getPropertyName())) {
            updateBarLabel();
        } else if ("icon".equals(evt.getPropertyName())) {
            updateBarLabel();
        } else if ("title".equals(evt.getPropertyName())) {
            updateBarLabel();
        }
    }


    public void unregister() {
        toolWindow.removePropertyChangeListener(this);
        getToolWindowContainer().uninstall();
    }

    public MyDoggyToolWindowManager getManager() {
        return manager;
    }

    public Component getComponent() {
        return component;
    }

    public MyDoggyToolWindow getToolWindow() {
        return toolWindow;
    }

    public int getDivederLocation() {
        if (divederLocation == -1)
            this.divederLocation = ((DockedTypeDescriptor) getTypeDescriptor(ToolWindowType.DOCKED)).getDockLength();

        return divederLocation;
    }

    public void setDivederLocation(int divederLocation) {
        this.divederLocation = divederLocation;
    }

    public JLabel getBarLabel(Component container) {
        if (barLabel == null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String toolBarLabelName = (toolWindow.getIndex() > 0) ? toolWindow.getIndex() + " : " + toolWindow.getTitle()
                                                                  : toolWindow.getTitle();

            if (anchor == ToolWindowAnchor.BOTTOM || anchor == ToolWindowAnchor.TOP) {
                barLabel = new JLabel(toolBarLabelName, toolWindow.getIcon(), JLabel.CENTER);
            } else {
                TextIcon textIcon = new TextIcon(container, toolBarLabelName, anchor == ToolWindowAnchor.LEFT ? TextIcon.ROTATE_LEFT : TextIcon.ROTATE_RIGHT);
                CompositeIcon compositeIcon = new CompositeIcon(textIcon, toolWindow.getIcon());
                barLabel = new JLabel(compositeIcon, JLabel.CENTER);
            }

            barLabel.setName(toolBarLabelName);
            barLabel.setUI(createLabelUI());
            barLabel.setOpaque(false);
            barLabel.setFocusable(false);
            barLabel.setBackground(Colors.skin);
        }
        return barLabel;
    }

    public JLabel getBarLabel() {
        return barLabel;
    }

    public void resetBarLabel() {
        barLabel = null;
    }

    public ToolWindowContainer getToolWindowContainer() {
        if (toolWindowContainer == null)
            toolWindowContainer = new SlidingContainer(this);
        return toolWindowContainer;
    }

    public ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type) {
        switch (type) {
            case FLOATING:
            case FLOATING_WINDOW:
                return floatingTypeDescriptor;
            case DOCKED:
                return dockedTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for : " + type);
    }

    public boolean isFloatingWindow() {
        return floatingWindow;
    }

    public void setFloatingWindow(boolean floatingWindow) {
        this.floatingWindow = floatingWindow;
    }

    public org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar getToolBar(ToolWindowAnchor anchor) {
        return manager.getBar(anchor);
    }

    public Container getToolWindowManagerContainer() {
        return manager.getContentPane();
    }

    public Window getWindowAnchestor() {
        return windowAnchestor;
    }


    protected LabelUI createLabelUI() {
        return new BarLabelUI(this, toolWindow);
    }

    protected void updateBarLabel() {
        if (barLabel != null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String toolBarLabelName = (toolWindow.getIndex() > 0) ? toolWindow.getIndex() + " : " + toolWindow.getTitle()
                                                                  : toolWindow.getTitle();


            if (anchor == ToolWindowAnchor.BOTTOM || anchor == ToolWindowAnchor.TOP) {
                barLabel.setIcon(toolWindow.getIcon());
                barLabel.setText(toolBarLabelName);
            } else {
                TextIcon textIcon = new TextIcon(((TextIcon) ((CompositeIcon) barLabel.getIcon()).getIcon1()).getComponent(),
                                                 toolBarLabelName,
                                                 anchor == ToolWindowAnchor.LEFT ? TextIcon.ROTATE_LEFT : TextIcon.ROTATE_RIGHT);
                CompositeIcon compositeIcon = new CompositeIcon(textIcon, toolWindow.getIcon());
                barLabel.setText(null);
                barLabel.setIcon(compositeIcon);
            }
        }
    }

}
