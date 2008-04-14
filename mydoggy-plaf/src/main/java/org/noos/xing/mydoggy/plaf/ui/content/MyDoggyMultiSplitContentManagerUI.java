package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.cmp.*;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyMultiSplitContentManagerUI extends MyDoggyContentManagerUI implements MultiSplitContentManagerUI, PlafContentManagerUI, PropertyChangeListener {
    protected MultiSplitContainer multiSplitContainer;
    protected Map<Content, MultiSplitContentUI> contentUIMap;
    protected boolean focusValueAdj = false;

    protected TabLayout tabLayout;
    protected TabPlacement tabPlacement;

    protected JPopupMenu popupMenu;
    protected Component componentInFocusRequest;

    protected FocusOwnerPropertyChangeListener focusOwnerPropertyChangeListener;


    public MyDoggyMultiSplitContentManagerUI() {
        setContentManagerUI(this);

        this.contentUIMap = new Hashtable<Content, MultiSplitContentUI>();
        this.tabPlacement = TabPlacement.TOP;
        this.tabLayout = TabLayout.SCROLL;
    }


    public void setCloseable(boolean closeable) {
        boolean old = this.closeable;
        this.closeable = closeable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setCloseable(closeable);
        }

        fireContentManagerUIProperty("closeable", old, closeable);
    }

    public void setDetachable(boolean detachable) {
        boolean old = this.detachable;
        this.detachable = detachable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setDetachable(detachable);
        }

        fireContentManagerUIProperty("detachable", old, detachable);
    }

    public void setMinimizable(boolean minimizable) {
        boolean old = this.minimizable;
        this.minimizable = minimizable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setMinimizable(minimizable);
        }

        fireContentManagerUIProperty("minimizable", old, minimizable);
    }

    public MultiSplitContentUI getContentUI(Content content) {
        return contentUIMap.get(content);
    }

    public void setTabPlacement(TabPlacement tabPlacement) {
        if (tabPlacement == null || tabPlacement == getTabPlacement())
            return;

        TabPlacement old = this.tabPlacement;
        this.tabPlacement = tabPlacement;

        multiSplitContainer.setTabPlacement(tabPlacement);

        fireContentManagerUIProperty("tabPlacement", old, tabPlacement);
    }

    public TabPlacement getTabPlacement() {
        return tabPlacement;
    }

    public void setTabLayout(TabLayout tabLayout) {
        if (tabLayout == null || tabLayout == getTabLayout())
            return;

        TabLayout old = this.tabLayout;
        this.tabLayout = tabLayout;

        multiSplitContainer.setTabLayout(tabLayout);

        fireContentManagerUIProperty("tabLayout", old, tabLayout);
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    public boolean isShowAlwaysTab() {
        return multiSplitContainer.isUseAlwaysContentWrapper();
    }

    public void setShowAlwaysTab(boolean showAlwaysTab) {
        if (isShowAlwaysTab() == showAlwaysTab)
            return;

        boolean old = isShowAlwaysTab();
        multiSplitContainer.setUseAlwaysContentWrapper(showAlwaysTab);

        fireContentManagerUIProperty("showAlwaysTab", old, showAlwaysTab);
    }


    public PlafContentManagerUI install(ContentManagerUI oldContentManagerUI, ToolWindowManager manager) {
        // Init managers
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = manager.getContentManager();
        this.resourceManager = toolWindowManager.getResourceManager();

        if (oldContentManagerUI != null) {
            // Import properties from the old ContentManagerUI
            this.closeable = oldContentManagerUI.isCloseable();
            this.detachable = oldContentManagerUI.isDetachable();
            this.minimizable = oldContentManagerUI.isMinimizable();
        }
        // Import properties from the ContentManager
        setPopupMenu(contentManager.getPopupMenu());

        // Init Components
        initComponents();

        // Init listeners
        initListeners();

        // Set the main content with the multiSplitContainer
        toolWindowManager.setMainContent(multiSplitContainer);

        // Import contents
        contentValueAdjusting = true;
        Content selectedContent = null;
        for (Content content : contentManager.getContents()) {
            if (content.isSelected())
                selectedContent = content;
            addContent((PlafContent) content);
        }
        contentValueAdjusting = false;

        if (oldContentManagerUI != null) {
            if (resourceManager.getBoolean("ContentManagerUI.ContentManagerUiListener.import", false)) {
                // Import listeners from the old ContentManagerUI
                for (ContentManagerUIListener listener : oldContentManagerUI.getContentManagerUiListener()) {
                    oldContentManagerUI.removeContentManagerUIListener(listener);
                    addContentManagerUIListener(listener);
                }
            }
        }

        // Now you can consider this manager installed
        this.installed = true;

        // Select the content selected on the previous ContentManagerUI
        final Content selectedContent1 = selectedContent;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (selectedContent1 != null)
                    selectedContent1.setSelected(true);
                else if (contentManager.getContentCount() > 0) {
                    contentManager.getContent(0).setSelected(true);
                }
            }
        });

        return this;
    }

    public void uninstall() {
        if (maximizedContent != null)
            maximizedContent.setMaximized(false);

        // Remove all contents
        for (Content content : contentManager.getContents()) {
            removeContent((PlafContent) content);
        }

        removeListeners();

        // Now you can consider this manager uninstalled
        this.installed = false;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void addContent(PlafContent content, Object... constraints) {
        if (maximizedContent != null) {
            maximizedContent.setMaximized(false);
            maximizedContent = null;
        }

        // Add the content to the ui...
        addUIForContent(content, constraints);

        // Register a plaf listener
        content.addPlafPropertyChangeListener(this);
    }

    public void removeContent(PlafContent content) {
        try {
            if (content.isDetached()) {
                propertyChange(new PropertyChangeEvent(content, "detached.dispose", true, false));
            } else if (content.isMinimized()) {
                toolWindowManager.getDockableDescriptor(content.getId()).setAvailable(false);
            } else {
                // Remove component
                valueAdjusting = true;
                try {
                    // Remove from multiSplitContainer
                    multiSplitContainer.removeDockable(content);
                } finally {
                    valueAdjusting = false;
                }
            }
        } finally {
            // Remove listeners
            content.getContentUI().removePropertyChangeListener(contentUIListener);
            content.removePlafPropertyChangeListener(this);

            // Clean desccriptor for minimization
            toolWindowManager.removeDockableDescriptor(content.getId());

            // Remove the contentUI part
            contentUIMap.remove(content);
            if (lastSelected == content)
                lastSelected = null;
        }
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public boolean isSelected(Content content) {
        return content == lastSelected;
    }

    public void setSelected(Content content, boolean selected) {
        if (selected) {
            if (lastSelected != null)
                lastSelected.setSelected(false);

            if (content.isMinimized()) {
                content.setMinimized(false);
                content.setSelected(true);
            } else if (content.isDetached()) {
                // If the content is detached request the focus for owner window
                SwingUtil.requestFocus(
                        SwingUtilities.windowForComponent(content.getComponent())
                );
            } else {
                // Restore an eventually maximized content
                Content maximizedContent = getMaximizedContent();
                if (maximizedContent != null)
                    maximizedContent.setMaximized(false);

                // Choose the owner tab or check if the content is the main content
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            valueAdjusting = true;

                            try {
                                tabbedContentPane.setSelectedIndex(index);
                                if (!focusValueAdj)
                                    componentInFocusRequest = SwingUtil.findAndRequestFocus(tabbedContentPane.getComponentAt(index));
                                lastSelected = content;
                            } finally {
                                valueAdjusting = false;
                            }

                            return;
                        }
                    } else if (c instanceof DockablePanel) {
                        DockablePanel dockablePanel = (DockablePanel) c;
                        if (dockablePanel.getDockable() == content) {
                            valueAdjusting = true;

                            try {
                                if (!focusValueAdj)
                                    componentInFocusRequest = SwingUtil.findAndRequestFocus(dockablePanel);
                                lastSelected = content;
                            } finally {
                                valueAdjusting = false;
                            }

                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        } else {
            if (content == lastSelected)
                lastSelected = null;
        }
    }

    public void updateUI() {
        multiSplitContainer.updateUI();
    }

    public void selectNextContent(Content content) {
        if (contentManager.getSelectedContent() == null) {
            if (contentManager.getContentCount() > 0)
                contentManager.getContent(0).setSelected(true);
        }
    }


    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        if (multiSplitContainer == null) {
            /// Init just once
            multiSplitContainer = new MultiSplitContainer(toolWindowManager);
            setupActions();
        }
    }

    protected void initListeners() {
        if (internalPropertyChangeSupport == null) {
            /// Init just once

            internalPropertyChangeSupport = new PropertyChangeSupport(this);
            internalPropertyChangeSupport.addPropertyChangeListener("component", new ComponentListener());
            internalPropertyChangeSupport.addPropertyChangeListener("disabledIcon", new DisabledIconListener());
            internalPropertyChangeSupport.addPropertyChangeListener("icon", new IconListener());
            internalPropertyChangeSupport.addPropertyChangeListener("mnemonic", new MnemonicListener());
            internalPropertyChangeSupport.addPropertyChangeListener("enabled", new EnabledListener());
            internalPropertyChangeSupport.addPropertyChangeListener("foreground", new ForegroundListener());
            internalPropertyChangeSupport.addPropertyChangeListener("title", new TitleListener());
            internalPropertyChangeSupport.addPropertyChangeListener("toolTipText", new ToolTipTextListener());
            DetachedListener detachedListener = new DetachedListener();
            internalPropertyChangeSupport.addPropertyChangeListener("detached.dispose", detachedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("detached", detachedListener);
            MaximizedListener maximizedListener = new MaximizedListener();
            internalPropertyChangeSupport.addPropertyChangeListener("maximized.before", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("maximized", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("minimized", new MinimizedListener());

            contentUIListener = new ContentUIListener();
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
                "focusOwner", focusOwnerPropertyChangeListener = new FocusOwnerPropertyChangeListener());

    }


    protected void removeListeners() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(
                "focusOwner", focusOwnerPropertyChangeListener);
    }

    protected void setupActions() {
        SwingUtil.addKeyActionMapping(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, multiSplitContainer,
                                      KeyStroke.getKeyStroke(39, InputEvent.ALT_MASK),
                                      "nextContent", new NextContentAction(toolWindowManager));
        SwingUtil.addKeyActionMapping(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, multiSplitContainer,
                                      KeyStroke.getKeyStroke(37, InputEvent.ALT_MASK),
                                      "previousContent", new PreviousContentAction(toolWindowManager));
    }

    protected void addUIForContent(Content content, Object... constraints) {
        MultiSplitContentUI contentUI = contentUIMap.get(content);
        if (contentUI == null) {
            contentUI = new MyDoggyMultiSplitContentUI(this, multiSplitContainer, content);
            contentUI.addPropertyChangeListener(contentUIListener);
            contentUI.setCloseable(closeable);
            contentUI.setDetachable(detachable);
            contentUI.setMinimizable(minimizable);

            contentUIMap.put(content, contentUI);
        }


        if (constraints != null && constraints.length > 0) {
            if (constraints[0] instanceof MultiSplitConstraint) {
                MultiSplitConstraint constraint = (MultiSplitConstraint) constraints[0];
                multiSplitContainer.addDockable(content,
                                                content.getComponent(),
                                                constraint.getAggregationContent(),
                                                constraint.getAggregationIndexLocation(),
                                                constraint.getAggregationPosition());
            } else if (constraints[0] instanceof MultiSplitContainer.Constraint) {
                MultiSplitContainer.Constraint constraint = (MultiSplitContainer.Constraint) constraints[0];
                multiSplitContainer.addDockable(content,
                                                content.getComponent(),
                                                constraint);
            } else
                multiSplitContainer.addDockable(content,
                                                content.getComponent(),
                                                null,
                                                -1,
                                                AggregationPosition.DEFAULT);
        } else {
            multiSplitContainer.addDockable(content,
                                            content.getComponent(),
                                            null,
                                            -1,
                                            AggregationPosition.DEFAULT);
        }


        SwingUtil.repaint(multiSplitContainer);
    }

    public Object getLayout() {
        return multiSplitContainer.getModel();
    }

    public void setLayout(final Object layout) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                multiSplitContainer.setModel((MultiSplitLayout.Node) layout);
            }
        });
    }


    protected class ComponentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (content.isDetached()) {
                RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.windowForComponent(content.getComponent());
                Container container = rootPaneContainer.getContentPane();
                container.removeAll();
                container.add((Component) evt.getNewValue());
            } else {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setComponentAt(index, (Component) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent()) {
                        multiSplitContainer.setRootComponent((Component) evt.getNewValue());
                        return;
                    }
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class DisabledIconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setDisabledIconAt(index, (Icon) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setIconAt(index, (Icon) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class MnemonicListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setMnemonicAt(index, (Integer) evt.getNewValue());
                            return;
                        }
                    } else if (c instanceof DockablePanel) {
                        DockablePanel dockablePanel = (DockablePanel) c;
                        if (dockablePanel.getDockable() == content)
                            return;
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class EnabledListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (content.isDetached()) {
                Window anchestor = SwingUtilities.windowForComponent(content.getComponent());
                anchestor.setEnabled((Boolean) evt.getNewValue());
            } else {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setEnabledAt(index, (Boolean) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setForegroundAt(index, (Color) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class TitleListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (content.isDetached()) {
                SwingUtil.setWindowTitle(content.getComponent(), (String) evt.getNewValue());
            } else {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setTitleAt(index, (String) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setToolTipTextAt(index, (String) evt.getNewValue());
                            return;
                        }
                    }
                    if (c == content.getComponent())
                        return;
                }
                throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class MaximizedListener implements PropertyChangeListener {
        protected ByteArrayOutputStream tmpWorkspace;
        protected Component oldFucusOwner;
        protected boolean valudAdj;

        public void propertyChange(PropertyChangeEvent evt) {
            if (valudAdj)
                return;
            Content content = (Content) evt.getSource();

            if ("maximized.before".equals(evt.getPropertyName())) {
                if ((Boolean) evt.getNewValue()) {
                    if (tmpWorkspace != null) {
                        // Restore...
                        multiSplitContainer.setMaximizedDockable(null);
                        valudAdj = true;
                        try {
                            toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                                                                             resourceManager.getObject(PersistenceDelegate.MergePolicy.class,
                                                                                                       PersistenceDelegate.MergePolicy.UNION));
                        } finally {
                            valudAdj = false;
                        }
                        tmpWorkspace = null;
                    }

                    // Save and clear the workspace
                    toolWindowManager.getPersistenceDelegate().save(tmpWorkspace = new ByteArrayOutputStream());
                    toolWindowManager.getToolWindowGroup().setVisible(false);

                    oldFucusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                    // Maximize
                    multiSplitContainer.setMaximizedDockable(content);

                    // Resotre the focus owner

                    maximizedContent = content;
                }
            } else {
                if (!(Boolean) evt.getNewValue()) {
                    if (tmpWorkspace != null) {
                        multiSplitContainer.setMaximizedDockable(null);
                        valudAdj = true;
                        try {
                            toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                                                                             resourceManager.getObject(PersistenceDelegate.MergePolicy.class,
                                                                                                       PersistenceDelegate.MergePolicy.UNION));
                        } finally {
                            valudAdj = false;
                        }
                        tmpWorkspace = null;
                        maximizedContent = null;

                        // Restore focus owner
                        if (oldFucusOwner != null) {
                            SwingUtil.requestFocus(oldFucusOwner);
                            oldFucusOwner = null;
                        }
                    }
                }
            }
        }
    }

    protected class DetachedListener implements PropertyChangeListener {
        protected Frame parentFrame;
        protected Map<Content, MultiSplitDockableContainer.Constraint> detachedContentUIMap;

        public DetachedListener() {
            parentFrame = (toolWindowManager.getWindowAnchestor() instanceof Frame) ? (Frame) toolWindowManager.getWindowAnchestor() : null;
            detachedContentUIMap = new HashMap<Content, MultiSplitDockableContainer.Constraint>();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if ("detached.dispose".equals(evt.getPropertyName())) {
                Window window = SwingUtilities.windowForComponent(content.getComponent());
                window.setVisible(false);
                window.dispose();
                detachedContentUIMap.remove(content);
            } else {
                if (!oldValue && newValue) {
                    valueAdjusting = true;
                    try {
                        ContentUI contentUI = getContentUI(content);

                        Rectangle inBounds = multiSplitContainer.getBoundsRelativeToScreen(content);

                        // Remove from multiSpli and store constraint
                        detachedContentUIMap.put(content, multiSplitContainer.removeDockable(content));

                        // Setup dialog
                        Window dialog;
                        if (contentUI.isAddToTaskBarWhenDetached()) {
                            dialog = new ContentFrame(resourceManager,
                                                      content, contentUI,
                                                      parentFrame, inBounds);
                        } else {
                            dialog = new ContentDialog(resourceManager,
                                                       content, contentUI,
                                                       parentFrame, inBounds);
                        }
                        dialog.addWindowFocusListener(new ContentDialogFocusListener(content));
                        dialog.toFront();
                        dialog.setVisible(true);

                        componentInFocusRequest = SwingUtil.findAndRequestFocus(dialog);
                    } finally {
                        valueAdjusting = false;
                    }
                } else if (oldValue && !newValue) {
                    Window window = SwingUtilities.windowForComponent(content.getComponent());
                    window.setVisible(false);
                    window.dispose();

                    contentValueAdjusting = true;
                    try {
                        addUIForContent(content, detachedContentUIMap.get(content));
                        content.setSelected(true);

                        componentInFocusRequest = SwingUtil.findAndRequestFocus(content.getComponent());
                    } finally {
                        contentValueAdjusting = false;
                        detachedContentUIMap.remove(content);
                    }
                }
            }
        }

    }

    protected class FocusOwnerPropertyChangeListener implements PropertyChangeListener {

        public FocusOwnerPropertyChangeListener() {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!isContentManagerEnabled())
                return;

            if (componentInFocusRequest != null) {
                if (evt.getNewValue() == componentInFocusRequest)
                    componentInFocusRequest = null;
                else
                    return;
            }

            if (evt.getNewValue() != null) {
                if (SwingUtil.getParent((Component) evt.getNewValue(), "toolWindow.container.") != null)
                    return;

                Dockable newSelected = null;

                JTabbedContentPane tabbedPane = SwingUtil.getParent((Component) evt.getNewValue(), JTabbedContentPane.class);
                if (tabbedPane == null) {
                    DockablePanel dockablePanel = SwingUtil.getParent((Component) evt.getNewValue(), DockablePanel.class);
                    if (dockablePanel != null)
                        newSelected = dockablePanel.getDockable();
                } else
                    newSelected = (Dockable) tabbedPane.getSelectedContent();

                if (newSelected != null && !valueAdjusting && !contentValueAdjusting) {
                    if (newSelected == lastSelected)
                        return;

                    if (lastSelected != null)
                        lastSelected.setSelected(false);

                    focusValueAdj = true;
                    try {
                        newSelected.setSelected(true);
                    } finally {
                        focusValueAdj = false;
                    }
                }
            }
        }
    }

    protected class MinimizedListener implements PropertyChangeListener {
        protected Map<Content, MultiSplitDockableContainer.Constraint> minimizedContentUIMap;

        public MinimizedListener() {
            minimizedContentUIMap = new HashMap<Content, MultiSplitDockableContainer.Constraint>();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            if ((Boolean) evt.getNewValue()) {
                content.setSelected(false);
                content.setMaximized(false);

                DockableDescriptor descriptor = toolWindowManager.getDockableDescriptor(content.getId());
                if (descriptor == null)
                    descriptor = toolWindowManager.createDescriptor(content);

                // Remove content
                minimizedContentUIMap.put(content, multiSplitContainer.removeDockable(content));

                // Put on bar
                descriptor.setAvailable(true);
            } else {
                DockableDescriptor descriptor = toolWindowManager.getDockableDescriptor(content.getId());

                // Remove from bar
                descriptor.setAvailable(false);

                contentValueAdjusting = true;
                try {
                    addUIForContent(content,
                                    minimizedContentUIMap.get(content));
                    content.setSelected(true);

                    componentInFocusRequest = SwingUtil.findAndRequestFocus(content.getComponent());
                } finally {
                    contentValueAdjusting = false;
                    minimizedContentUIMap.remove(content);
                }
            }
        }

    }

    protected class MultiSplitContainer extends MultiSplitTabbedContentContainer {
        protected Component oldRoot;
        protected DockablePanel oldParent;
        protected Dockable currentMaximizedDockable;

        public MultiSplitContainer(MyDoggyToolWindowManager toolWindowManager) {
            super(toolWindowManager);
        }


        public void setRootComponent(Component component) {
            super.setRootComponent(component);
        }

        protected Component forceWrapperForComponent(Dockable dockable, Component component) {
            final JTabbedContentPane tabbedContentPane = (JTabbedContentPane) super.forceWrapperForComponent(dockable, component);

            tabbedContentPane.setTabPlacement(tabPlacement.ordinal() + 1);
            tabbedContentPane.setTabLayoutPolicy(tabLayout.ordinal());
            tabbedContentPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (!valueAdjusting && !contentValueAdjusting) {
                        Content newSelected = (Content) tabbedContentPane.getSelectedContent();
                        if (newSelected != null) {
                            if (newSelected == lastSelected)
                                return;

                            if (lastSelected != null)
                                lastSelected.setSelected(false);

                            newSelected.setSelected(true);
                        }
                    }
                }
            });
            tabbedContentPane.addTabbedContentPaneListener(new TabbedContentPaneListener() {
                public void tabbedContentPaneEventFired(TabbedContentPaneEvent event) {
                    Content content = event.getContent();
                    switch (event.getActionId()) {
                        case ON_CLOSE:
                            if (fireContentUIRemoving(getContentUI(content)))
                                contentManager.removeContent(content);
                            break;
                        case ON_DETACH:
                            content.setDetached(true);
                            fireContentUIDetached(getContentUI(content));
                            break;
                    }
                }
            });
            return tabbedContentPane;
        }

        protected boolean isWrapRequest(Dockable dockable, Action action) {
            switch (action) {
                case ADD_DOCK:
                    if (getContentCount() == 0) {
                        return useAlwaysContentWrapper;
                    } else if (getContentCount() >= 1) {
                        return (((MultiSplitContentUI) ((Content) dockable).getContentUI()).isShowAlwaysTab());
                    }
                    return useAlwaysContentWrapper;
                case REMOVE_DOCK:
                    if (getContentCount() <= 1) {
                        return useAlwaysContentWrapper;
                    } else if (getContentCount() > 1) {
                        return (((MultiSplitContentUI) ((Content) dockable).getContentUI()).isShowAlwaysTab());
                    }
                    return useAlwaysContentWrapper;
            }
            return useAlwaysContentWrapper;
        }

        public void setMaximizedDockable(Dockable dockable) {
            if (dockable == null) {
                if (oldRoot != null) {
                    oldParent.setComponent(currentMaximizedDockable.getComponent());
                    setRootComponent(oldRoot);

                    this.oldRoot = null;
                    this.oldParent = null;
                    this.currentMaximizedDockable = null;
                }
            } else {
                this.currentMaximizedDockable = dockable;
                this.oldRoot = getRootComponent();
                this.oldParent = (DockablePanel) dockable.getComponent().getParent();

                setRootComponent(forceWrapperForComponent(dockable, dockable.getComponent()));
            }
            SwingUtil.repaint(this);
        }

        public void setTabPlacement(TabPlacement tabPlacement) {
            for (Component c : multiSplitContainer.getTabbedComponents()) {
                if (c instanceof JTabbedContentPane) {
                    JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                    tabbedContentPane.setTabPlacement(tabPlacement.ordinal() + 1);
                }
            }
        }

        public void setTabLayout(TabLayout tabLayout) {
            for (Component c : multiSplitContainer.getTabbedComponents()) {
                if (c instanceof JTabbedContentPane) {
                    JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                    tabbedContentPane.setTabLayoutPolicy(tabLayout.ordinal());
                }
            }
        }


    }

}