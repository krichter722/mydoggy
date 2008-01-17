package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitDockableContainer;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitTabbedContentContainer;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
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
public class MyDoggyMultiSplitContentManagerUI implements MultiSplitContentManagerUI, PlafContentManagerUI, PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected MyDoggyContentManager contentManager;
    protected ResourceManager resourceManager;

    protected MultiSplitContainer multiSplitContainer;
    protected boolean closeable, detachable;
    protected boolean installed;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;
    protected PropertyChangeListener contentUIListener;

    protected Content maximizedContent;
    protected PlafContent lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, TabbedContentUI> contentUIMap;

    protected TabLayout tabLayout;
    protected TabPlacement tabPlacement;

    protected JPopupMenu popupMenu;


    public MyDoggyMultiSplitContentManagerUI() {
        this.contentManagerUIListeners = new EventListenerList();
        this.contentUIMap = new Hashtable<Content, TabbedContentUI>();

        this.closeable = this.detachable = true;
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

    public boolean isCloseable() {
        return closeable;
    }

    public void setDetachable(boolean detachable) {
        boolean old = this.detachable;
        this.detachable = detachable;

        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setDetachable(detachable);
        }

        fireContentManagerUIProperty("detachable", old, detachable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public TabbedContentUI getContentUI(Content content) {
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        contentManagerUIListeners.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        contentManagerUIListeners.remove(PropertyChangeListener.class, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return contentManagerUIListeners.getListeners(PropertyChangeListener.class);
    }

    public void addContentManagerUIListener(ContentManagerUIListener listener) {
        contentManagerUIListeners.add(ContentManagerUIListener.class, listener);
    }

    public void removeContentManagerUIListener(ContentManagerUIListener listener) {
        contentManagerUIListeners.remove(ContentManagerUIListener.class, listener);
    }

    public ContentManagerUIListener[] getContentManagerUiListener() {
        return contentManagerUIListeners.getListeners(ContentManagerUIListener.class);
    }


    public PlafContentManagerUI install(ContentManagerUI oldContentManagerUI, ToolWindowManager manager) {
        // Init managers
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = (MyDoggyContentManager) manager.getContentManager();
        this.resourceManager = toolWindowManager.getResourceManager();

        if (oldContentManagerUI != null) {
            // Import properties from the old ContentManagerUI
            this.closeable = oldContentManagerUI.isCloseable();
            this.detachable = oldContentManagerUI.isDetachable();
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

        // Now you can consider this manager uninstalled
        this.installed = false;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void addContent(PlafContent content, Object... constraints) {
        // Add the content to the ui...
        addUIForContent(content, constraints);

        // Register a plaf listener
        content.addPlafPropertyChangeListener(this);
    }

    public void removeContent(PlafContent content) {
        // If the content is detached, reattach it
        if (content.isDetached())
            content.setDetached(false);

        content.getContentUI().removePropertyChangeListener(contentUIListener);

        // Remove from multiSplitContainer
        multiSplitContainer.removeDockable(content);

        // Remove the plaf listener
        content.removePlafPropertyChangeListener(this);
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
            if (content.isDetached()) {
                // If the content is detached request the focus for owner window
                SwingUtil.requestFocus(
                        SwingUtilities.windowForComponent(content.getComponent())
                );
            } else {
                // Choose the owner tab or check if the content is the main content
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            valueAdjusting = true;

                            tabbedContentPane.setSelectedIndex(index);
                            lastSelected = (PlafContent) content;

                            valueAdjusting = false;
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

    public void updateUI() {
        multiSplitContainer.updateUI();
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
            internalPropertyChangeSupport.addPropertyChangeListener("detached", new DetachedListener());
            internalPropertyChangeSupport.addPropertyChangeListener("maximized", new MaximizedListener());

            final PropertyChangeListener focusOwnerPropertyChangeListener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue() != null) {
                        JTabbedContentPane tabbedPane = SwingUtil.getParent((Component) evt.getNewValue(), JTabbedContentPane.class);
                        if (tabbedPane != null) {
                            if (!valueAdjusting && !contentValueAdjusting) {
                                PlafContent newSelected = (PlafContent) tabbedPane.getSelectedContent();
                                if (newSelected != null) {
                                    if (newSelected == lastSelected)
                                        return;

                                    if (lastSelected != null) {
                                        try {
                                            lastSelected.fireSelected(false);
                                        } catch (Exception ignoreIt) {
                                        }
                                    }

                                    lastSelected = newSelected;
                                    newSelected.fireSelected(true);
                                }
                            }
                        }
                    }
                }
            };

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", focusOwnerPropertyChangeListener);
            toolWindowManager.addInternalPropertyChangeListener("anchestor.closed", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner", focusOwnerPropertyChangeListener);
                }
            });
            contentUIListener = new ContentUIListener();
        }
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
        TabbedContentUI contentUI = contentUIMap.get(content);
        if (contentUI == null) {
            contentUI = new MyDoggyMultiSplitContentUI(multiSplitContainer, content);
            contentUI.addPropertyChangeListener(contentUIListener);
        }
        
        contentUIMap.put(content, contentUI);
        contentUI.setCloseable(closeable);
        contentUI.setDetachable(detachable);

        if (constraints.length > 0 && constraints[0] instanceof MultiSplitConstraint) {
            MultiSplitConstraint multiSplitConstraint = (MultiSplitConstraint) constraints[0];
            multiSplitContainer.addDockable(content,
                                            content.getComponent(),
                                            multiSplitConstraint.getAggregationContent(),
                                            multiSplitConstraint.getAggregationIndexLocation(),
                                            multiSplitConstraint.getAggregationPosition());
        } else {
            multiSplitContainer.addDockable(content,
                                            content.getComponent(),
                                            null,
                                            -1,
                                            AggregationPosition.DEFAULT);
        }


        SwingUtil.repaint(multiSplitContainer);
    }


    protected boolean fireContentUIRemoving(ContentUI contentUI) {
        ContentManagerUIEvent event = new ContentManagerUIEvent(this, ContentManagerUIEvent.ActionId.CONTENTUI_REMOVING, contentUI);

        for (ContentManagerUIListener listener : contentManagerUIListeners.getListeners(ContentManagerUIListener.class)) {
            if (!listener.contentUIRemoving(event))
                return false;
        }
        return true;
    }

    protected void fireContentUIDetached(ContentUI contentUI) {
        ContentManagerUIEvent event = new ContentManagerUIEvent(this, ContentManagerUIEvent.ActionId.CONTENTUI_DETACHED, contentUI);
        for (ContentManagerUIListener listener : contentManagerUIListeners.getListeners(ContentManagerUIListener.class)) {
            listener.contentUIDetached(event);
        }
    }

    protected void fireContentManagerUIProperty(String property, Object oldValue, Object newValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
        for (PropertyChangeListener listener : contentManagerUIListeners.getListeners(PropertyChangeListener.class)) {
            listener.propertyChange(event);
        }
    }


    protected class ComponentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

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

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            tabbedContentPane.setMnemonicAt(index, (Integer) evt.getNewValue());
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

    protected class EnabledListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

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

            if (content.isDetached()) {
                JDialog dialog = (JDialog) SwingUtilities.windowForComponent(content.getComponent());
                dialog.setTitle((String) evt.getNewValue());
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

            if (!content.isDetached()) {
                for (Component c : multiSplitContainer.getTabbedComponents()) {
                    if (c instanceof JTabbedContentPane) {
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane) c);
                        int index = tabbedContentPane.indexOfContent(content);
                        if (index != -1) {
                            String newToolTip = (String) evt.getNewValue();
                            if (newToolTip == null)
                                newToolTip = "";
                            tabbedContentPane.setToolTipTextAt(index, newToolTip);
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
        protected boolean valudAdj;

        public void propertyChange(PropertyChangeEvent evt) {
            if (valudAdj)
                return;
            Content content = (Content) evt.getSource();

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

                toolWindowManager.getPersistenceDelegate().save(tmpWorkspace = new ByteArrayOutputStream());
                toolWindowManager.getToolWindowGroup().setVisible(false);
                multiSplitContainer.setMaximizedDockable(content);
                maximizedContent = content;
            } else {
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
                }
            }
        }
    }

    protected class DetachedListener implements PropertyChangeListener {
        protected Frame parentFrame;
        protected Map<Content, MultiSplitDockableContainer.Constraint> detachedContentUIMap;

        public DetachedListener() {
            parentFrame = (toolWindowManager.getAnchestor() instanceof Frame) ? (Frame) toolWindowManager.getAnchestor() : null;
            detachedContentUIMap = new HashMap<Content, MultiSplitDockableContainer.Constraint>();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (!oldValue && newValue) {
                final ContentUI contentUI = getContentUI(content);

                final JDialog dialog = new JDialog(resourceManager.getBoolean("dialog.owner.enabled", true) ? parentFrame : null,
                                                   false);
                dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

                Component component = content.getComponent();

                detachedContentUIMap.put(content, multiSplitContainer.removeDockable(content));

                component.setPreferredSize(component.getSize());

                dialog.setTitle(content.getTitle());
                dialog.getContentPane().add(component);

                Rectangle detachedBounds = contentUI.getDetachedBounds();
                if (detachedBounds != null) {
                    dialog.setBounds(detachedBounds);
                } else {
                    if (parentFrame != null) {
                        Point location = parentFrame.getLocation();
                        location.translate(5, 5);
                        dialog.setLocation(location);
                    } else {
                        SwingUtil.centrePositionOnScreen(dialog);
                    }
                    dialog.pack();
                }

                // TODO: move to DockablePanel
                if (resourceManager.getTransparencyManager().isServiceAvailable()) {
                    WindowTransparencyListener windowTransparencyListener = new WindowTransparencyListener(
                            resourceManager.getTransparencyManager(),
                            getContentUI(content),
                            dialog
                    );
                    dialog.addWindowListener(windowTransparencyListener);
                    dialog.addWindowFocusListener(windowTransparencyListener);
                }

                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent event) {
                        Component component = dialog.getContentPane().getComponent(0);
                        PlafContent content = (PlafContent) contentManager.getContentByComponent(component);
                        content.fireSelected(false);
                        content.setDetached(false);
                    }
                });

                dialog.addWindowFocusListener(new WindowFocusListener() {
                    public void windowGainedFocus(WindowEvent e) {
                        if (!valueAdjusting && !contentValueAdjusting) {
                            PlafContent newSelected = (PlafContent) contentManager.getContentByComponent(
                                    dialog.getContentPane().getComponent(0));

                            if (newSelected == lastSelected)
                                return;

                            if (lastSelected != null) {
                                try {
                                    lastSelected.fireSelected(false);
                                } catch (Exception ignoreIt) {
                                }
                            }

                            lastSelected = newSelected;
                            newSelected.fireSelected(true);
                        }
                    }

                    public void windowLostFocus(WindowEvent e) {
                    }
                });

                if (parentFrame == null)
                    dialog.addWindowFocusListener(new ToFrontWindowFocusListener(dialog));

                dialog.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        contentUI.setDetachedBounds(dialog.getBounds());
                    }
                    public void componentMoved(ComponentEvent e) {
                        contentUI.setDetachedBounds(dialog.getBounds());
                    }
                });

                dialog.toFront();
                dialog.setVisible(true);
                SwingUtil.requestFocus(dialog);
            } else if (oldValue && !newValue) {
                Window window = SwingUtilities.windowForComponent(content.getComponent());
                window.setVisible(false);
                window.dispose();

                addUIForContent(content, detachedContentUIMap.get(content));
                content.setSelected(true);
            }
        }

    }

    protected class ContentUIListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            ContentUI contentUI = (ContentUI) evt.getSource();
            
            if ("detachedBounds".equals(evt.getPropertyName()) && contentUI.getContent().isDetached()) {
                Window window = SwingUtilities.windowForComponent(contentUI.getContent().getComponent());
                window.setBounds((Rectangle) evt.getNewValue());                
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

                setRootComponent(getComponentWrapper(dockable, dockable.getComponent()));
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


        protected Container getComponentWrapper(Dockable dockable, Component component) {
            final JTabbedContentPane tabbedContentPane = (JTabbedContentPane) super.getComponentWrapper(dockable, component);
            tabbedContentPane.setTabPlacement(tabPlacement.ordinal() + 1);
            tabbedContentPane.setTabLayoutPolicy(tabLayout.ordinal());
            tabbedContentPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (!valueAdjusting && !contentValueAdjusting) {
                        PlafContent newSelected = (PlafContent) tabbedContentPane.getSelectedContent();
                        if (newSelected != null) {
                            if (newSelected == lastSelected)
                                return;

                            if (lastSelected != null) {
                                try {
                                    lastSelected.fireSelected(false);
                                } catch (Exception ignoreIt) {
                                }
                            }

                            lastSelected = newSelected;
                            newSelected.fireSelected(true);
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

    }

}