package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowDescriptor implements PropertyChangeListener {
    protected MyDoggyToolWindowManager manager;
    protected MyDoggyToolWindow toolWindow;

//    protected Window windowAnchestor;

    protected DockedContainer dockedContainer;
    protected FloatingContainer floatingContainer;
    protected SlidingContainer slidingContainer;
    protected FloatingLiveContainer floatingLiveContainer;

    protected Component component;
    protected JLabel representativeAnchor;

    protected int divederLocation = -1;
    protected int tempDivederLocation;

    protected FloatingTypeDescriptor floatingTypeDescriptor;
    protected DockedTypeDescriptor dockedTypeDescriptor;
    protected SlidingTypeDescriptor slidingTypeDescriptor;
    protected FloatingLiveTypeDescriptor floatingLiveTypeDescriptor;

    protected boolean floatingWindow = false;

    boolean valueAdj = false;


    public ToolWindowDescriptor(MyDoggyToolWindowManager manager,
                                MyDoggyToolWindow toolWindow) {
        this.manager = manager;
        this.toolWindow = toolWindow;

        toolWindow.addInternalPropertyChangeListener(this);

        initTypeDescriptors();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if ("type".equals(evt.getPropertyName())) {
            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.FLOATING_FREE)
                setFloatingWindow(true);
            else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                setFloatingWindow(false);
            if (evt.getNewValue() == ToolWindowType.EXTERN)
                resetRepresentativeAnchor();
        } else if ("index".equals(evt.getPropertyName())) {
            updateRepresentativeAnchor();
        } else if ("numberingEnabled".equals(evt.getPropertyName())) {
            updateRepresentativeAnchor();
        } else if ("icon".equals(evt.getPropertyName())) {
            updateRepresentativeAnchor();
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
        } else if ("idVisibleOnTitleBar".equals(evt.getPropertyName())) {
            setIdOnTitleBar();
        }
    }

    public String toString() {
        return "ToolWindowDescriptor{" +
               "toolWindow=" + toolWindow +
               '}';
    }


    public Component getComponent() {
        if (component == null)
            component = toolWindow.getToolWindowTabs()[0].getComponent();
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public int getDividerLocation() {
        if (divederLocation == -1)
            this.divederLocation = ((DockedTypeDescriptor) getTypeDescriptor(ToolWindowType.DOCKED)).getDockLength();

        return divederLocation;
    }

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

    public JLabel getRepresentativeAnchor(Component container) {
        if (representativeAnchor == null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String labelText = getResourceManager().getUserString(toolWindow.getId());
            String toolRepresentativeAnchorText = (toolWindow.getIndex() > 0 && getManager().getToolWindowManagerDescriptor().isNumberingEnabled())
                                                  ? toolWindow.getIndex() + " : " + labelText
                                                  : labelText;
            Icon toolIcon = toolWindow.isAvailable() || toolWindow.getIcon() == null ? toolWindow.getIcon()
                            : new ImageIcon(GrayFilter.createDisabledImage(
                    GraphicsUtil.getImage(representativeAnchor, toolWindow.getIcon()))
            );

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor = new RepresentativeAnchor(toolRepresentativeAnchorText, toolIcon, JLabel.CENTER);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(container, toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                                                    : manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new RepresentativeAnchor(compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(container, toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                                                    : manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new RepresentativeAnchor(compositeIcon, JLabel.CENTER);
                    break;
            }

            representativeAnchor.setName("toolWindow.rb." + toolWindow.getId());
            representativeAnchor.setOpaque(toolWindow.isActive());
            representativeAnchor.setFocusable(false);
            representativeAnchor.putClientProperty(ToolWindowDescriptor.class, this);
        }
        return representativeAnchor;
    }

    public JLabel getRepresentativeAnchor() {
        return representativeAnchor;
    }

    public void resetRepresentativeAnchor() {
        representativeAnchor = null;
    }

    public int getRepresentativeAnchorIndex() {
        if (representativeAnchor == null)
            return -1;
        return getToolBar().getRepresentativeAnchorIndex(representativeAnchor);
    }

    public void updateRepresentativeAnchor() {
        if (representativeAnchor != null) {
            ToolWindowAnchor anchor = toolWindow.getAnchor();

            String labelText = getResourceManager().getUserString(toolWindow.getId());
            String toolRepresentativeAnchorText = (toolWindow.getIndex() > 0 && getManager().getToolWindowManagerDescriptor().isNumberingEnabled())
                                                  ? toolWindow.getIndex() + " : " + labelText
                                                  : labelText;
            Icon toolIcon = toolWindow.isAvailable() || toolWindow.getIcon() == null ? toolWindow.getIcon()
                            : new ImageIcon(GrayFilter.createDisabledImage(
                    GraphicsUtil.getImage(representativeAnchor, toolWindow.getIcon()))
            );

            switch (anchor) {
                case BOTTOM:
                case TOP:
                    representativeAnchor.setIcon(toolIcon);
                    representativeAnchor.setText(toolRepresentativeAnchorText);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getLeftIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                                                    : manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getRightIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                                                    : manager.getResourceManager().getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
            }
        }
    }

    public boolean isFloatingWindow() {
        return floatingWindow;
    }

    public void setFloatingWindow(boolean floatingWindow) {
        this.floatingWindow = floatingWindow;
    }

    public boolean isIdVisibleOnTitleBar() {
        switch (toolWindow.getType()) {
            case DOCKED:
                return toolWindow.getTypeDescriptor(ToolWindowType.DOCKED).isIdVisibleOnTitleBar();
            case SLIDING:
                return toolWindow.getTypeDescriptor(ToolWindowType.SLIDING).isIdVisibleOnTitleBar();
            case FLOATING:
            case FLOATING_FREE:
                return toolWindow.getTypeDescriptor(ToolWindowType.FLOATING).isIdVisibleOnTitleBar();
            case FLOATING_LIVE:
                return toolWindow.getTypeDescriptor(ToolWindowType.FLOATING_LIVE).isIdVisibleOnTitleBar();
        }
        throw new IllegalStateException("ToolWindowDescriptor.isIdVisibleOnTitleBar");
    }

    public void setIdOnTitleBar() {
        if (dockedContainer != null) {
            if (isIdVisibleOnTitleBar())
                dockedContainer.enableIdOnTitleBar();
            else
                dockedContainer.disableIdOnTitleBar();
        }
    }


    public void unregister() {
        toolWindow.removePropertyChangeListener(this);

        if (dockedContainer != null) {
            dockedContainer.uninstall();
            slidingContainer.uninstall();
            floatingContainer.uninstall();
            floatingLiveContainer.uninstall();
        }
    }

    public void updateUI() {
        getToolWindowContainer().updateUI();

        SwingUtilities.updateComponentTreeUI(getComponent());

        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            SwingUtilities.updateComponentTreeUI(tab.getComponent());
        }

        if (getRepresentativeAnchor() != null)
            getRepresentativeAnchor().updateUI();
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
        return manager.getParentComponent() instanceof Window ? (Window) manager.getParentComponent() : null;
    }

    public ToolWindowContainer getToolWindowContainer() {
        if (dockedContainer == null) {
            dockedContainer = new DockedContainer(this);
            slidingContainer = new SlidingContainer(dockedContainer);
            floatingContainer = new FloatingContainer(dockedContainer);
            floatingLiveContainer = new FloatingLiveContainer(dockedContainer);
        }
        return dockedContainer;
    }

    public ToolWindowContainer getToolWindowContainer(ToolWindowType toolWindowType) {
        if (dockedContainer == null)
            getToolWindowContainer();
        switch (toolWindowType) {
            case FLOATING:
            case FLOATING_FREE:
                return floatingContainer;
            case FLOATING_LIVE:
                return floatingLiveContainer;
            case DOCKED:
                return dockedContainer;
            case SLIDING:
                return slidingContainer;
        }
        throw new IllegalArgumentException("Type not recognized.");
    }

    public ToolWindowTypeDescriptor getTypeDescriptor(ToolWindowType type) {
        switch (type) {
            case FLOATING:
            case FLOATING_FREE:
                return floatingTypeDescriptor;
            case FLOATING_LIVE:
                return floatingLiveTypeDescriptor;
            case DOCKED:
                return dockedTypeDescriptor;
            case SLIDING:
                return slidingTypeDescriptor;
        }
        throw new IllegalStateException("Doen't exist a TypeDescriptor for. [type : " + type + "]");
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

    public ResourceManager getResourceManager() {
        return manager.getResourceManager();
    }


    public DockedTypeDescriptor getDockedTypeDescriptor() {
        return dockedTypeDescriptor;
    }

    public FloatingContainer getFloatingContainer() {
        return floatingContainer;
    }

    public Component getContentContainer() {
        return ((DockedContainer) getToolWindowContainer()).getContentContainer();
    }

    public int getJMenuBarExtraHeight() {
        JMenuBar jMenuBar = manager.getRootPane().getJMenuBar();

        if (jMenuBar != null && jMenuBar.isVisible())
            return jMenuBar.getHeight();
        return 0;
    }

    public void hideToolWindow() {
        ToolWindowActionHandler toolWindowActionHandler = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolWindowActionHandler();
        if (toolWindowActionHandler != null)
            toolWindowActionHandler.onHideButtonClick(toolWindow);
        else
            toolWindow.setVisible(false);
    }


    protected void initTypeDescriptors() {
        floatingTypeDescriptor = (FloatingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING)).cloneMe();
        floatingTypeDescriptor.addPropertyChangeListener(this);

        floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING_LIVE)).cloneMe();
        floatingLiveTypeDescriptor.addPropertyChangeListener(this);

        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe();
        dockedTypeDescriptor.addPropertyChangeListener(this);

        slidingTypeDescriptor = (SlidingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.SLIDING)).cloneMe();
        slidingTypeDescriptor.addPropertyChangeListener(this);
    }

    protected ComponentUI createRepresentativeAnchorUI() {
        return manager.getResourceManager().createComponentUI(MyDoggyKeySpace.REPRESENTATIVE_ANCHOR_BUTTON_UI, manager, this);
    }


    public class RepresentativeAnchor extends JLabel {

        public RepresentativeAnchor(Icon image, int horizontalAlignment) {
            super(image, horizontalAlignment);
            super.setUI((LabelUI) createRepresentativeAnchorUI());
        }

        public RepresentativeAnchor(String text, Icon icon, int horizontalAlignment) {
            super(text, icon, horizontalAlignment);
            super.setUI((LabelUI) createRepresentativeAnchorUI());
        }

        public void setUI(LabelUI ui) {
        }

        public void updateUI() {
            firePropertyChange("UI", null, getUI());
        }
    }

}
