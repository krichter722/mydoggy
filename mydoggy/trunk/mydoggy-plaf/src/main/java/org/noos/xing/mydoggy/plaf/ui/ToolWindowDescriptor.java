package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.icons.AggregateIcon;
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
    private int tempDivederLocation;

    private FloatingTypeDescriptor floatingTypeDescriptor;
    private DockedTypeDescriptor dockedTypeDescriptor;
    private SlidingTypeDescriptor slidingTypeDescriptor;

    private boolean floatingWindow = false;


    public ToolWindowDescriptor(MyDoggyToolWindowManager manager, MyDoggyToolWindow toolWindow,
                                Window windowAnchestor, Component component) {
        this.manager = manager;
        this.windowAnchestor = windowAnchestor;
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
        } else if ("numberingEnabled".equals(evt.getPropertyName())) {
            updateAnchorLabel();
        } else if ("icon".equals(evt.getPropertyName())) {
            updateAnchorLabel();
        } else if ("dockLength".equals(evt.getPropertyName())) {
            if (!valueAdj) {
                this.divederLocation = (Integer) evt.getNewValue();
                getToolBar(toolWindow.getAnchor()).propertyChange(
                        new PropertyChangeEvent(toolWindow,
                                                evt.getPropertyName(),
                                                evt.getOldValue(),
                                                evt.getNewValue()
                        )
                );
            }
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
        if (component == null)
            component = toolWindow.getToolWindowTabs()[0].getComponent();
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
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
            case SLIDING:
                return slidingTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for : " + type);
    }

    public int getDividerLocation() {
        if (divederLocation == -1)
            this.divederLocation = ((DockedTypeDescriptor) getTypeDescriptor(ToolWindowType.DOCKED)).getDockLength();

        return divederLocation;
    }

    boolean valueAdj = false;

    public void setDividerLocation(int divederLocation) {
        this.divederLocation = divederLocation;

        DockedTypeDescriptor dockedTypeDescriptor = (DockedTypeDescriptor) toolWindow.getTypeDescriptor(ToolWindowType.DOCKED);
        valueAdj = true;
        try {
            dockedTypeDescriptor.setDockLength(divederLocation);
        } finally {
            valueAdj = false;
        }
    }

    public int getTempDivederLocation() {
        return tempDivederLocation;
    }

    public void setTempDivederLocation(int tempDivederLocation) {
        this.tempDivederLocation = tempDivederLocation;
    }

    public JLabel getAnchorLabel(Component container) {
        if (anchorLabel == null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String labelText = ResourceBundleManager.getInstance().getUserString(toolWindow.getId());
            String toolAnchorLabelText = (toolWindow.getIndex() > 0 && getManager().getToolWindowManagerDescriptor().isNumberingEnabled())
                                         ? toolWindow.getIndex() + " : " + labelText
                                         : labelText;

            switch (anchor) {
                case BOTTOM :
                case TOP :
                    anchorLabel = new AnchorLabel(toolAnchorLabelText, toolWindow.getIcon(), JLabel.CENTER);
                    break;
                case LEFT :
                    TextIcon textIcon = new TextIcon(container, toolAnchorLabelText, TextIcon.ROTATE_LEFT);
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolWindow.getIcon(), SwingConstants.VERTICAL);
                    anchorLabel = new AnchorLabel(compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT :
                    textIcon = new TextIcon(container, toolAnchorLabelText, TextIcon.ROTATE_RIGHT);
                    compositeIcon = new AggregateIcon(toolWindow.getIcon(), textIcon, SwingConstants.VERTICAL);
                    anchorLabel = new AnchorLabel(compositeIcon, JLabel.CENTER);
                    break;
            }

            anchorLabel.setName("toolWindow.rb." + toolWindow.getId());
            anchorLabel.setUI(createLabelUI());
            anchorLabel.setOpaque(toolWindow.isActive());
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
        floatingTypeDescriptor.addPropertyChangeListener(this);

        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe();
        dockedTypeDescriptor.addPropertyChangeListener(this);

        slidingTypeDescriptor = (SlidingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.SLIDING)).cloneMe();
    }


    protected LabelUI createLabelUI() {
        return new AnchorLabelUI(this, toolWindow);
    }

    protected void updateAnchorLabel() {
        if (anchorLabel != null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String labelText = ResourceBundleManager.getInstance().getUserString(toolWindow.getId());
            String toolAnchorLabelText = (toolWindow.getIndex() > 0 && getManager().getToolWindowManagerDescriptor().isNumberingEnabled())
                                         ? toolWindow.getIndex() + " : " + labelText
                                         : labelText;

            switch (anchor) {
                case BOTTOM :
                case TOP :
                    anchorLabel.setIcon(toolWindow.getIcon());
                    anchorLabel.setText(toolAnchorLabelText);
                    break;
                case LEFT :
                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) anchorLabel.getIcon()).getLeftIcon()).getComponent(), toolAnchorLabelText, TextIcon.ROTATE_LEFT);
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolWindow.getIcon(), SwingConstants.VERTICAL);
                    anchorLabel.setText(null);
                    anchorLabel.setIcon(compositeIcon);
                    break;
                case RIGHT :
                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) anchorLabel.getIcon()).getLeftIcon()).getComponent(), toolAnchorLabelText, TextIcon.ROTATE_RIGHT);
                    compositeIcon = new AggregateIcon(toolWindow.getIcon(), textIcon, SwingConstants.VERTICAL);
                    anchorLabel.setText(null);
                    anchorLabel.setIcon(compositeIcon);
                    break;
            }
        }
    }

    public void updateUI() {
        getToolWindowContainer().updateUI();
        SwingUtilities.updateComponentTreeUI(getComponent());
        if (getAnchorLabel() != null)
            getAnchorLabel().updateUI();
    }

    public ToolWindowAnchor getToolWindowAnchor(Point p) {
        Rectangle b = getManager().getBounds();

        if (p.x <= 18 && p.y >= 18 && p.y <= b.height - 18) {
            return ToolWindowAnchor.LEFT;
        } else if (p.x >= b.width - 18 && p.y >= 18 && p.y <= b.height - 18) {
            return ToolWindowAnchor.RIGHT;
        } else if (p.y <= 18 && p.x >= 18 && p.x <= b.width - 18) {
            return ToolWindowAnchor.TOP;
        } else if (p.y >= b.height - 18 && p.x >= 18 && p.x <= b.width - 18) {
            return ToolWindowAnchor.BOTTOM;
        }
        return null;
    }

    public ToolWindowUI getToolWindowUI() {
        return MyDoggyToolWindowUI.getInstance();
    }

    public int getLabelIndex() {
        if (anchorLabel == null)
            return -1;
        return getToolBar().getLabelIndex(anchorLabel);
    }

    public DockedTypeDescriptor getDockedTypeDescriptor() {
        return dockedTypeDescriptor;
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
