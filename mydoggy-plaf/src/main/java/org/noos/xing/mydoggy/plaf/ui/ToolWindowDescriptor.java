package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.cleaner.CleanerAggregator;
import org.noos.xing.mydoggy.plaf.ui.util.cleaner.ToolWindowCleaner;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class ToolWindowDescriptor implements PropertyChangeListener, DockableDescriptor {
    protected MyDoggyToolWindowManager manager;
    protected MyDoggyToolWindow toolWindow;
    protected CleanerAggregator cleaner;

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
        //  Init manager and toolwindw
        this.manager = manager;
        this.toolWindow = toolWindow;

        // Init cleaner
        this.cleaner = new ToolWindowDescriptorCleaner(manager, toolWindow);

        toolWindow.addPlafPropertyChangeListener(this);

        initTypeDescriptors();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();

        if ("type".equals(propertyName)) {
            if (evt.getOldValue() == ToolWindowType.FLOATING_FREE || evt.getNewValue() == ToolWindowType.FLOATING_FREE)
                setFloatingWindow(true);
            else if (evt.getOldValue() == ToolWindowType.FLOATING || evt.getNewValue() == ToolWindowType.FLOATING)
                setFloatingWindow(false);
        } else if ("index".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("numberingEnabled".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("icon".equals(propertyName)) {
            updateRepresentativeAnchor();
        } else if ("dockLength".equals(propertyName)) {
            if (!valueAdj) {
                this.divederLocation = (Integer) evt.getNewValue();
                getToolBar(toolWindow.getAnchor()).propertyChange(
                        new PropertyChangeEvent(toolWindow,
                                                propertyName,
                                                evt.getOldValue(),
                                                evt.getNewValue()
                        )
                );
            }
        } else if ("idVisibleOnTitleBar".equals(propertyName)) {
            setIdOnTitleBar();
        } else if ("autoHide".equals(propertyName)) {
            getToolWindowContainer().propertyChange(evt);
        } else if ("hideRepresentativeButtonOnVisible".equals(propertyName)) {
            toolWindow.setRepresentativeAnchorButtonVisible(!(Boolean) evt.getNewValue());
        }
    }

    public String toString() {
        return "ToolWindowDescriptor{" +
               "toolWindow=" + toolWindow +
               '}';
    }


    public ToolWindowAnchor getAnchor() {
        return toolWindow.getAnchor();
    }

    public void setAvailable(boolean available) {
        toolWindow.setAvailable(available);
    }

    public boolean isAvailable(boolean available) {
        return toolWindow.isAvailable();
    }

    public DockableType getDockableType() {
        return DockableType.TOOL_WINDOW;
    }

    public Dockable getDockable() {
        return toolWindow;
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

    public MyDoggyToolWindowManager getManager() {
        return manager;
    }

    public MyDoggyToolWindowBar getToolBar(ToolWindowAnchor anchor) {
        return manager.getBar(anchor);
    }

    public MyDoggyToolWindowBar getToolBar() {
        return manager.getBar(toolWindow.getAnchor());
    }

    public boolean isDragImageAvailable() {
        return true;
    }

    public Component getComponentForDragImage() {
        return ((DockedContainer) getToolWindowContainer()).getContentContainer();
    }

    public void setAnchor(ToolWindowAnchor anchor, int index) {
        toolWindow.setAnchor(anchor, index);
    }

    public ResourceManager getResourceManager() {
            return manager.getResourceManager();
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

    public void updateUI() {
        getToolWindowContainer().updateUI();

        SwingUtilities.updateComponentTreeUI(getComponent());

        for (ToolWindowTab tab : toolWindow.getToolWindowTabs()) {
            SwingUtilities.updateComponentTreeUI(tab.getComponent());
        }

        if (getRepresentativeAnchor() != null)
            getRepresentativeAnchor().updateUI();
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
        return manager.getJMenuBarExtraHeight();
    }

    public void hideToolWindow() {
        ToolWindowActionHandler toolWindowActionHandler = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolWindowActionHandler();
        if (toolWindowActionHandler != null)
            toolWindowActionHandler.onHideButtonClick(toolWindow);
        else
            toolWindow.setVisible(false);
    }

    public CleanerAggregator getCleaner() {
        return cleaner;
    }

    
    protected void initTypeDescriptors() {
        floatingTypeDescriptor = (FloatingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING)).cloneMe(this);
        floatingTypeDescriptor.addPropertyChangeListener(this);

        floatingLiveTypeDescriptor = (FloatingLiveTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.FLOATING_LIVE)).cloneMe(this);
        floatingLiveTypeDescriptor.addPropertyChangeListener(this);

        dockedTypeDescriptor = (DockedTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.DOCKED)).cloneMe(this);
        dockedTypeDescriptor.addPropertyChangeListener(this);

        slidingTypeDescriptor = (SlidingTypeDescriptor) ((InternalTypeDescriptor) manager.getTypeDescriptorTemplate(ToolWindowType.SLIDING)).cloneMe(this);
        slidingTypeDescriptor.addPropertyChangeListener(this);
    }


    class ToolWindowDescriptorCleaner extends ToolWindowCleaner {

        public ToolWindowDescriptorCleaner(ToolWindowManager manager, ToolWindow toolWindow) {
            super(manager, toolWindow);
        }

        public void cleanup() {
            super.cleanup();
            
            ToolWindowDescriptor.this.toolWindow.removingFlag = true;

            for (ToolWindowTab toolWindowTab : toolWindow.getToolWindowTabs()) {
                toolWindow.removeToolWindowTab(toolWindowTab);
            }

            // Clean listener added to toolwindow
            ToolWindowDescriptor.this.toolWindow.removePlafPropertyChangeListener(ToolWindowDescriptor.this);

            // Clean TypeDescriptors
            floatingTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            floatingLiveTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            dockedTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            slidingTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);

            if (representativeAnchor != null)
                representativeAnchor.putClientProperty(ToolWindowDescriptor.class, null);

            toolWindow = null;
            manager = null;
            ToolWindowDescriptor.this.toolWindow = null;
            ToolWindowDescriptor.this.manager = null;
        }
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

        protected ComponentUI createRepresentativeAnchorUI() {
            return manager.getResourceManager().createComponentUI(MyDoggyKeySpace.REPRESENTATIVE_ANCHOR_BUTTON_UI, manager, ToolWindowDescriptor.this);
        }
    }

}
