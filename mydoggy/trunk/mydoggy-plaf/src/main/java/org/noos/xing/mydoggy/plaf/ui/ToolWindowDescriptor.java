package org.noos.xing.mydoggy.plaf.ui;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindow;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowBar;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.cleaner.CleanerAggregator;
import org.noos.xing.mydoggy.plaf.cleaner.DefaultCleanerAggregator;
import org.noos.xing.mydoggy.plaf.descriptors.InternalTypeDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.AggregateIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.FloatingLivePanel;
import org.noos.xing.mydoggy.plaf.ui.cmp.TextIcon;
import org.noos.xing.mydoggy.plaf.ui.cmp.ToolWindowRepresentativeAnchor;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

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
        this.cleaner = new ToolWindowDescriptorCleaner();

        initListeners();
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
        } else if ("autoHide".equals(propertyName)) {
            getToolWindowContainer().propertyChange(evt);
        } else if ("hideRepresentativeButtonOnVisible".equals(propertyName)) {
            if (toolWindow.isVisible())
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

    public boolean isAvailable() {
        return toolWindow.isAvailable();
    }

    public DockableType getDockableType() {
        return DockableType.TOOL_WINDOW;
    }

    public Dockable getDockable() {
        return toolWindow;
    }

    public JLabel getRepresentativeAnchor(Component parent) {
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
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, toolRepresentativeAnchorText, toolIcon, JLabel.CENTER);
                    break;
                case LEFT:
                    TextIcon textIcon = new TextIcon(parent, toolRepresentativeAnchorText, TextIcon.ROTATE_LEFT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(parent, toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    compositeIcon = new AggregateIcon(toolIcon, textIcon, SwingConstants.VERTICAL);
                    representativeAnchor = new ToolWindowRepresentativeAnchor(this, compositeIcon, JLabel.CENTER);
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
        if (representativeAnchor != null)
            representativeAnchor.putClientProperty(ToolWindowDescriptor.class, null);
        representativeAnchor = null;
    }

    public int getAnchorIndex() {
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
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
                    AggregateIcon compositeIcon = new AggregateIcon(textIcon, toolIcon, SwingConstants.VERTICAL);
                    representativeAnchor.setText(null);
                    representativeAnchor.setIcon(compositeIcon);
                    break;
                case RIGHT:
                    textIcon = new TextIcon(((TextIcon) ((AggregateIcon) representativeAnchor.getIcon()).getRightIcon()).getComponent(), toolRepresentativeAnchorText, TextIcon.ROTATE_RIGHT);
                    textIcon.setForeground(toolWindow.isAvailable() ? UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND)
                                           : UIManager.getColor(MyDoggyKeySpace.RAB_FOREGROUND_UNAVAILABLE));
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

    public CleanerAggregator getCleaner() {
            return cleaner;
        }

    public void cleanup() {
        getCleaner().cleanup();
    }


    public Component getComponent() {
        if (component == null) {
            if (toolWindow.getToolWindowTabs().length > 0)
                component = toolWindow.getToolWindowTabs()[0].getComponent();
        }
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
        if (divederLocation <= 0)
            return;
        
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
            case EXTERN:
                return true;
        }
        throw new IllegalStateException("ToolWindowDescriptor.isIdVisibleOnTitleBar");
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

    public Rectangle getToolWindowManagerContainerBounds() {
        return SwingUtilities.convertRectangle(manager,
                                               manager.getBounds(),
                                               manager.getRootPaneContainer().getContentPane());
    }

    public Window getWindowAncestor() {
        return manager.getWindowAncestor() instanceof Window ? (Window) manager.getWindowAncestor() : null;
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

    public DockedTypeDescriptor getDockedTypeDescriptor() {
        return dockedTypeDescriptor;
    }

    public FloatingContainer getFloatingContainer() {
        return floatingContainer;
    }

    public Component getContentContainer() {
        return ((DockedContainer) getToolWindowContainer()).getContentContainer();
    }

    public void hideToolWindow() {
        ToolWindowActionHandler toolWindowActionHandler = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class).getToolWindowActionHandler();
        if (toolWindowActionHandler != null)
            toolWindowActionHandler.onHideButtonClick(toolWindow);
        else
            toolWindow.setVisible(false);
    }

    public void addTypeDescriptorChangePropertyListener(PropertyChangeListener listener) {
        floatingTypeDescriptor.addPropertyChangeListener(listener);
        floatingLiveTypeDescriptor.addPropertyChangeListener(listener);
        dockedTypeDescriptor.addPropertyChangeListener(listener);
        slidingTypeDescriptor.addPropertyChangeListener(listener);
    }

    public void removeTypeDescriptorChangePropertyListener(PropertyChangeListener listener) {
        floatingTypeDescriptor.removePropertyChangeListener(listener);
        floatingLiveTypeDescriptor.removePropertyChangeListener(listener);
        dockedTypeDescriptor.removePropertyChangeListener(listener);
        slidingTypeDescriptor.removePropertyChangeListener(listener);
    }


    private static Map<ToolWindow, FloatingLivePanel> livePanelMap = new HashMap<ToolWindow, FloatingLivePanel>();

    public FloatingLivePanel getFloatingLivePanel(ToolWindow toolWindow) {
        FloatingLivePanel panel = livePanelMap.get(toolWindow);
        if (panel == null) {
            panel = new FloatingLivePanel(manager);
            livePanelMap.put(toolWindow, panel);
        }

        return panel;
    }

    public FloatingLivePanel getFloatingLivePanel() {
        return getFloatingLivePanel(toolWindow);
    }


    protected void initListeners() {
        toolWindow.addPlafPropertyChangeListener(this);
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




    public class ToolWindowDescriptorCleaner extends DefaultCleanerAggregator {

        public void cleanup() {
            super.cleanup();

            // Clean listener added to toolwindow
            toolWindow.removePlafPropertyChangeListener(ToolWindowDescriptor.this);

            // Clean TypeDescriptors
            floatingTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            floatingLiveTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            dockedTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);
            slidingTypeDescriptor.removePropertyChangeListener(ToolWindowDescriptor.this);

            // Clean Representative Anchor
            resetRepresentativeAnchor();

            // Clean listeners...
            toolWindow.cleanup();

            // Finalizy clean
            toolWindow = null;
            manager = null;
        }
    }

}
