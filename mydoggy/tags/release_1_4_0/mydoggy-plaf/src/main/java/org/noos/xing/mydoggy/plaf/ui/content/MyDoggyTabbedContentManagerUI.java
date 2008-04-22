package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyTabbedContentManagerUI implements TabbedContentManagerUI, PlafContentManagerUI, PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ContentManager contentManager;
    protected ResourceManager resourceManager;

    protected JTabbedContentPane tabbedContentPane;
    protected boolean showAlwaysTab;
    protected boolean closeable, detachable;
    protected boolean installed;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;

    protected Content maximizedContent;
    protected PlafContent lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, TabbedContentUI> contentUIMap;
    protected Map<Content, TabbedContentUI> detachedContentUIMap;

    public MyDoggyTabbedContentManagerUI() {
        contentManagerUIListeners = new EventListenerList();
        this.closeable = this.detachable = true;
        this.showAlwaysTab = false;
        initComponents();
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

        TabPlacement old = getTabPlacement();
        tabbedContentPane.setTabPlacement(tabPlacement.ordinal() + 1);

        fireContentManagerUIProperty("tabPlacement", old, tabPlacement);
    }

    public TabPlacement getTabPlacement() {
        switch (tabbedContentPane.getTabPlacement()) {
            case SwingConstants.TOP:
                return TabPlacement.TOP;
            case SwingConstants.LEFT:
                return TabPlacement.LEFT;
            case SwingConstants.BOTTOM:
                return TabPlacement.BOTTOM;
            case SwingConstants.RIGHT:
                return TabPlacement.RIGHT;
        }
        throw new IllegalStateException("Invalid Tab Placement...");
    }

    public void setTabLayout(TabLayout tabLayout) {
        if (tabLayout == null || tabLayout == getTabLayout())
            return;

        TabLayout old = getTabLayout();
        tabbedContentPane.setTabLayoutPolicy(tabLayout.ordinal());
        SwingUtil.repaint(tabbedContentPane);

        fireContentManagerUIProperty("tabLayout", old, tabLayout);
    }

    public TabLayout getTabLayout() {
        switch (tabbedContentPane.getTabLayoutPolicy()) {
            case JTabbedPane.WRAP_TAB_LAYOUT:
                return TabLayout.WRAP;
            case JTabbedPane.SCROLL_TAB_LAYOUT:
                return TabLayout.SCROLL;
        }
        throw new IllegalStateException("Invalid Tab Layout...");
    }

    public boolean isShowAlwaysTab() {
        return showAlwaysTab;
    }

    public void setShowAlwaysTab(boolean showAlwaysTab) {
        boolean old = this.showAlwaysTab;
        this.showAlwaysTab = showAlwaysTab;

        if (showAlwaysTab) {
            if (contentManager.getContentCount() == 1 && toolWindowManager.getMainContent() != tabbedContentPane && tabbedContentPane.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContentByComponent(toolWindowManager.getMainContent()));
                valueAdjusting = false;

                toolWindowManager.setMainContent(tabbedContentPane);
            }
        }

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
        this.contentManager = manager.getContentManager();
        this.resourceManager = toolWindowManager.getResourceManager();

        // Notify tabbedContentPane
        this.tabbedContentPane.setToolWindowManager(toolWindowManager);

        if (oldContentManagerUI != null) {
            // Import properties from the old ContentManagerUI
            this.closeable = oldContentManagerUI.isCloseable();
            this.detachable = oldContentManagerUI.isDetachable();
        }
        // Import properties from the ContentManager
        setPopupMenu(contentManager.getPopupMenu());

        // Init listeners
        initListeners();

        // Import contents
        lastSelected = null;
        Content selectedContent = null;
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            if (content.isSelected())
                selectedContent = content;
            addContent((PlafContent) content);
            contentValueAdjusting = false;
        }
        contentValueAdjusting = false;

        if (oldContentManagerUI != null) {
            // Import listeners from the old ContentManagerUI
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

    public void unistall() {
        if (maximizedContent != null)
            maximizedContent.setMaximized(false);

        // Remove all contents
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            removeContent((PlafContent) content);
        }
        contentValueAdjusting = false;

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

        // Remove from tabbedContentPane
        int index = tabbedContentPane.indexOfContent(content);
        if (index != -1) {
            tabbedContentPane.removeTabAt(index);
        } else if (toolWindowManager.getMainContent() != content.getComponent())
            throw new IllegalStateException("Invalid content ui state.");

        // Remove the plaf listener
        content.removePlafPropertyChangeListener(this);

        if (contentValueAdjusting)
            return;

        // Choose next content to be selected...
        if (tabbedContentPane.getTabCount() == 0) {
            toolWindowManager.resetMainContent();
            lastSelected = null;
        }

        if (tabbedContentPane.getTabCount() == 1 && !isShowAlwaysTab()) {
            Content lastContent = contentManager.getSelectedContent();
            if (lastContent == content)
                lastContent = contentManager.getNextContent();

            detachedContentUIMap.put(lastContent, getContentUI(lastContent));
            toolWindowManager.setMainContent(lastContent.getComponent());
            lastSelected = null;
        } else {
            int selectedIndex = tabbedContentPane.getSelectedIndex();
            if (selectedIndex != -1)
                tabbedContentPane.getContentAt(selectedIndex).setSelected(true);
            else
                lastSelected = null;
        }

        // Remove the contentUI part
        contentUIMap.remove(content);
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
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1) {
                    valueAdjusting = true;

                    tabbedContentPane.setSelectedIndex(index);
                    lastSelected = (PlafContent) content;

                    valueAdjusting = false;
                } else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    public JPopupMenu getPopupMenu() {
        return tabbedContentPane.getComponentPopupMenu();
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        tabbedContentPane.setComponentPopupMenu(popupMenu);
    }

    public void updateUI() {
        tabbedContentPane.updateUI();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        detachedContentUIMap = new Hashtable<Content, TabbedContentUI>();
        contentUIMap = new Hashtable<Content, TabbedContentUI>();

        final JTabbedContentPane tabbedContentPane = new JTabbedContentPane();
        tabbedContentPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!valueAdjusting && !contentValueAdjusting) {
                    int selectedIndex = tabbedContentPane.getSelectedIndex();
                    if (selectedIndex == -1)
                        return;

                    PlafContent newSelected = (PlafContent) tabbedContentPane.getContentAt(selectedIndex);

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
        });
        tabbedContentPane.addTabbedContentPaneListener(new TabbedContentPaneListener() {
            public ByteArrayOutputStream tmpWorkspace;

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

        this.tabbedContentPane = tabbedContentPane;
        setupActions();
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

            SwingUtil.registerDragGesture(tabbedContentPane,
                                          new TabbedContentManagerDragGesture());
        }
    }

    protected void addUIForContent(Content content, Object... constaints) {
        contentUIMap.put(content, new MyDoggyTabbedContentUI(tabbedContentPane, content));

        if (!showAlwaysTab && tabbedContentPane.getTabCount() == 0 && (contentValueAdjusting || toolWindowManager.getMainContent() == null)) {
            detachedContentUIMap.put(content, getContentUI(content));
            toolWindowManager.setMainContent(content.getComponent());
            lastSelected = (PlafContent) content;
        } else {
            if (!showAlwaysTab && tabbedContentPane.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContentByComponent(toolWindowManager.getMainContent()), constaints);
                valueAdjusting = false;
            }

            addTab(content, constaints);
            toolWindowManager.setMainContent(tabbedContentPane);

            if (!tabbedContentPane.isEnabledAt(tabbedContentPane.getSelectedIndex()))
                tabbedContentPane.setSelectedIndex(tabbedContentPane.getTabCount() - 1);
        }
    }

    protected void addTab(Content content, Object... constaints) {
        tabbedContentPane.addTab(content);

        int index = tabbedContentPane.getTabCount() - 1;
        tabbedContentPane.setDisabledIconAt(index, content.getDisabledIcon());
        int mnemonic = content.getMnemonic();
        if (mnemonic != -1)
            tabbedContentPane.setMnemonicAt(index, mnemonic);
        if (content.getForeground() != null)
            tabbedContentPane.setForegroundAt(index, content.getForeground());
    }

    protected void setupActions() {
        // Setup actions
        SwingUtil.addKeyActionMapping(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tabbedContentPane,
                                      KeyStroke.getKeyStroke(39, InputEvent.ALT_MASK),
                                      "nextContent", new NextContentAction(toolWindowManager));
        SwingUtil.addKeyActionMapping(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tabbedContentPane,
                                      KeyStroke.getKeyStroke(37, InputEvent.ALT_MASK),
                                      "previousContent", new PreviousContentAction(toolWindowManager));
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
            Component oldCmp = (Component) evt.getOldValue();
            Component newCmp = (Component) evt.getNewValue();

            if (content.isDetached()) {
                RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.windowForComponent(content.getComponent());
                Container container = rootPaneContainer.getContentPane();
                container.removeAll();
                container.add(newCmp);
            } else {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setComponentAt(index, newCmp);
                else {
                    if (toolWindowManager.getMainContent() == oldCmp)
                        toolWindowManager.setMainContent(newCmp);
                    else
                        throw new IllegalStateException("Invalid content ui state.");
                }
            }
        }
    }

    protected class DisabledIconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setDisabledIconAt(index, (Icon) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setIconAt(index, (Icon) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class MnemonicListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setMnemonicAt(index, (Integer) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
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
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setEnabledAt(index, (Boolean) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setForegroundAt(index, (Color) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
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
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setTitleAt(index, (String) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException();
            }
        }
    }

    protected class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1) {
                    String newToolTip = (String) evt.getNewValue();
                    if (newToolTip == null)
                        newToolTip = "";
                    tabbedContentPane.setToolTipTextAt(index, newToolTip);
                } else if (toolWindowManager.getMainContent() != content.getComponent())
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
                    valudAdj = true;
                    try {
                        toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                                                                         PersistenceDelegate.MergePolicy.UNION);
                    } finally {
                        valudAdj = false;
                    }
                    tmpWorkspace = null;
                }

                toolWindowManager.getPersistenceDelegate().save(tmpWorkspace = new ByteArrayOutputStream());
                toolWindowManager.getToolWindowGroup().setVisible(false);
                maximizedContent = content;
            } else {
                if (tmpWorkspace != null) {
                    valudAdj = true;
                    try {
                        toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                                                                         PersistenceDelegate.MergePolicy.UNION);
                        tmpWorkspace = null;
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
        private Frame parentFrame;

        public DetachedListener() {
            parentFrame = (toolWindowManager.getAnchestor() instanceof Frame) ? (Frame) toolWindowManager.getAnchestor() : null;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (!oldValue && newValue) {
                if (tabbedContentPane.getTabCount() != 0) {
                    TabbedContentUI contentUI = getContentUI(content);
                    detachedContentUIMap.put(content, contentUI);
                }

                final JDialog dialog = new JDialog(resourceManager.getBoolean("dialog.owner.enabled", true) ? parentFrame : null,
                                                   false);
                dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

                Window parentWindow = SwingUtilities.windowForComponent(tabbedContentPane);
                Component component = content.getComponent();

                int tabIndex = tabbedContentPane.indexOfContent(content);
                if (tabIndex != -1) {
                    tabbedContentPane.removeTabAt(tabIndex);
                } else {
                    if (tabbedContentPane.getParent() == null)
                        toolWindowManager.setMainContent(null);
                    else
                        throw new IllegalStateException("Invalid Content : " + content);
                }

                component.setPreferredSize(component.getSize());

                dialog.setTitle(content.getTitle());
                dialog.getContentPane().add(component);

                Point location = parentWindow.getLocation();
                location.x += 5;
                location.y += 5;
                dialog.setLocation(location);

                dialog.pack();

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

                dialog.toFront();
                dialog.setVisible(true);
                SwingUtil.requestFocus(dialog);
            } else if (oldValue && !newValue) {
                Window window = SwingUtilities.windowForComponent(content.getComponent());
                window.setVisible(false);
                window.dispose();

                addUIForContent(content);
                tabbedContentPane.setSelectedIndex(tabbedContentPane.getTabCount() - 1);
            }
        }

    }

    protected class TabbedContentManagerDragGesture extends DragGestureAdapter {

        public TabbedContentManagerDragGesture() {
            super(toolWindowManager);
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            // Acquire locks
            if (!acquireLocks())
                return;

            // Start Drag
            Point origin = dge.getDragOrigin();
            int index = tabbedContentPane.indexAtLocation(origin.x, origin.y);
            if (index != -1) {
                Content content = tabbedContentPane.getContentAt(index);
                if (content.getDockableDelegator() != null) {
                    dge.startDrag(Cursor.getDefaultCursor(),
                                  new MyDoggyTransferable(MyDoggyTransferable.CONTENT_ID_DF,
                                                          content.getId()),
                                  this);

                    // Setup ghostImage

                    Component component = tabbedContentPane.getComponentAt(index);
                    BufferedImage ghostImage = new BufferedImage(component.getWidth(),
                                                                 component.getHeight(), BufferedImage.TYPE_INT_RGB);
                    component.print(ghostImage.getGraphics());
                    ghostImage = GraphicsUtil.scale(ghostImage,
                                                    component.getWidth() / 4,
                                                    component.getHeight() / 4);

                    setGhostImage(dge.getDragOrigin(), ghostImage);
                } else
                    releaseLocks();
            } else
                releaseLocks();
        }

        public void dragMouseMoved(DragSourceDragEvent dsde) {
            if (!checkStatus())
                return;
            updateGhostImage(dsde.getLocation());
        }

        public void dragDropEnd(DragSourceDropEvent dsde) {
            if (!checkStatus())
                return;

            releaseLocks();
            // Finalize drag action...
            cleanupGhostImage();
        }

    }
}