package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
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
    private ToolWindowContainer toolWindowContainer;
    private Component component;
    private JLabel anchorLabel;

    private int divederLocation = -1;

    private FloatingTypeDescriptor floatingTypeDescriptor;
    private DockedTypeDescriptor dockedTypeDescriptor;

    private boolean floatingWindow = false;


    public ToolWindowDescriptor(MyDoggyToolWindowManager manager, MyDoggyToolWindow toolWindow,
								Window windowAnchestor, Component component) {
        this.manager = manager;
        this.windowAnchestor = windowAnchestor;
        this.component = component;
        this.toolWindow = toolWindow;

        toolWindow.addInternalPropertyChangeListener(this);

        initTypeDescriptors();
    }

    public void unregister() {
        toolWindow.removePropertyChangeListener(this);
        getToolWindowContainer().uninstall();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if ("type".equals(evt.getPropertyName())) {
            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.FLOATING_FREE)
                setFloatingWindow(true);
            else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                setFloatingWindow(false);
        } else if ("index".equals(evt.getPropertyName())) {
            updateAnchorLabel();
        } else if ("icon".equals(evt.getPropertyName())) {
            updateAnchorLabel();
        } else if ("title".equals(evt.getPropertyName())) {
            updateAnchorLabel();
        }
    }


    public MyDoggyToolWindowManager getManager() {
        return manager;
    }

    public MyDoggyToolWindowBar getToolBar(ToolWindowAnchor anchor) {
        return manager.getBar(anchor);
    }

    public MyDoggyToolWindowBar getToolBar() {
        return manager.getBar(toolWindow.getAnchor());
    }

    public MyDoggyToolWindow getToolWindow() {
        return toolWindow;
    }


    public Container getToolWindowManagerContainer() {
        return manager;
    }

    public Window getWindowAnchestor() {
        return windowAnchestor;
    }

    public Component getComponent() {
        return component;
    }

    public ToolWindowContainer getToolWindowContainer() {
        if (toolWindowContainer == null)
            toolWindowContainer = new SlidingContainer(this);
        return toolWindowContainer;
    }

    public ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type) {
        switch (type) {
            case FLOATING:
            case FLOATING_FREE:
                return floatingTypeDescriptor;
            case DOCKED:
                return dockedTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for : " + type);
    }


    public int getDivederLocation() {
        if (divederLocation == -1)
            this.divederLocation = ((DockedTypeDescriptor) getTypeDescriptor(ToolWindowType.DOCKED)).getDockLength();

        return divederLocation;
    }

    public void setDivederLocation(int divederLocation) {
        this.divederLocation = divederLocation;
    }


    public JLabel getAnchorLabel(Component container) {
        if (anchorLabel == null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String toolAnchorLabelName = (toolWindow.getIndex() > 0) ? toolWindow.getIndex() + " : " + toolWindow.getTitle()
                                                                  : toolWindow.getTitle();

            if (anchor == ToolWindowAnchor.BOTTOM || anchor == ToolWindowAnchor.TOP) {
                anchorLabel = new AnchorLabel(toolAnchorLabelName, toolWindow.getIcon(), JLabel.CENTER);
            } else {
                TextIcon textIcon = new TextIcon(container, toolAnchorLabelName, anchor == ToolWindowAnchor.LEFT ? TextIcon.ROTATE_LEFT : TextIcon.ROTATE_RIGHT);
                CompositeIcon compositeIcon = new CompositeIcon(textIcon, toolWindow.getIcon(),
                                                                (anchor == ToolWindowAnchor.LEFT) ? SwingConstants.TOP
                                                                : SwingConstants.BOTTOM);
                anchorLabel = new AnchorLabel(compositeIcon, JLabel.CENTER);
            }

            anchorLabel.setName(toolAnchorLabelName);
            anchorLabel.setUI(createLabelUI());
            anchorLabel.setOpaque(false);
            anchorLabel.setFocusable(false);
            anchorLabel.setBackground(Colors.skin);
        }
        return anchorLabel;
    }

    public JLabel getAnchorLabel() {
        return anchorLabel;
    }

    public void resetAnchorLabel() {
        anchorLabel = null;
    }


    public boolean isFloatingWindow() {
        return floatingWindow;
    }

    public void setFloatingWindow(boolean floatingWindow) {
        this.floatingWindow = floatingWindow;
    }


    protected void initTypeDescriptors() {
        floatingTypeDescriptor = (FloatingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING)).cloneMe();
        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe();
    }


    protected LabelUI createLabelUI() {
        return new AnchorLabelUI(this, toolWindow);
    }

    protected void updateAnchorLabel() {
        if (anchorLabel != null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String toolAnchorLabelName = (toolWindow.getIndex() > 0) ? toolWindow.getIndex() + " : " + toolWindow.getTitle()
                                                                  : toolWindow.getTitle();


            if (anchor == ToolWindowAnchor.BOTTOM || anchor == ToolWindowAnchor.TOP) {
                anchorLabel.setIcon(toolWindow.getIcon());
                anchorLabel.setText(toolAnchorLabelName);
            } else {
                TextIcon textIcon = new TextIcon(((TextIcon) ((CompositeIcon) anchorLabel.getIcon()).getLeftIcon()).getComponent(),
                                                 toolAnchorLabelName,
                                                 anchor == ToolWindowAnchor.LEFT ? TextIcon.ROTATE_LEFT : TextIcon.ROTATE_RIGHT);
                CompositeIcon compositeIcon = new CompositeIcon(textIcon, toolWindow.getIcon(),
                                                                (anchor == ToolWindowAnchor.LEFT) ? SwingConstants.TOP
                                                                : SwingConstants.BOTTOM);
                anchorLabel.setText(null);
                anchorLabel.setIcon(compositeIcon);
            }
        }
    }

    public void updateUI() {
        getToolWindowContainer().updateUI();
        SwingUtilities.updateComponentTreeUI(getComponent());
        if (getAnchorLabel() != null)
            getAnchorLabel().updateUI();
    }


    private class AnchorLabel extends JLabel {

        public AnchorLabel(Icon image, int horizontalAlignment) {
            super(image, horizontalAlignment);
            super.setUI(createLabelUI());
        }

        public AnchorLabel(String text, Icon icon, int horizontalAlignment) {
            super(text, icon, horizontalAlignment);
            super.setUI(createLabelUI());
        }

        public void setUI(LabelUI ui) {
        }

        public void updateUI() {
            firePropertyChange("UI", null, getUI());
        }
    }

}