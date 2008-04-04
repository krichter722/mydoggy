package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDialog;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentFrame;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
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
public class MyDoggyTabbedContentManagerUI extends MyDoggyContentManagerUI implements TabbedContentManagerUI,
                                                                                      PlafContentManagerUI,
                                                                                      PropertyChangeListener {
    protected JTabbedContentPane tabbedContentPane;
    protected boolean showAlwaysTab;
    protected Map<Content, TabbedContentUI> contentUIMap;

    protected Component componentInFocusRequest = null;

    protected FocusOwnerPropertyChangeListener focusOwnerPropertyChangeListener;


    public MyDoggyTabbedContentManagerUI() {
        setContentManagerUI(this);

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
        if (this.showAlwaysTab == showAlwaysTab)
            return;

        boolean old = this.showAlwaysTab;
        this.showAlwaysTab = showAlwaysTab;

        if (showAlwaysTab) {
            if (contentManager.getContentCount() == 1 && toolWindowManager.getMainContent() != tabbedContentPane && tabbedContentPane.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContent(0));
                valueAdjusting = false;

                toolWindowManager.setMainContent(tabbedContentPane);
            }
        } else {
            if (contentManager.getContentCount() == 1) {
                toolWindowManager.setMainContent(tabbedContentPane.getComponentAt(0));
            }
        }

        fireContentManagerUIProperty("showAlwaysTab", old, showAlwaysTab);
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
            this.minimizable = oldContentManagerUI.isMinimizable();
        }
        // Import properties from the ContentManager
        setPopupMenu(contentManager.getPopupMenu());

        // Init listeners
        initListeners();

        // Import contents
        lastSelected = null;
        Content selectedContent = null;
        contentValueAdjusting = true;
        // TODO: we should import content UI proprs...
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

    public void uninstall() {
        uninstalling = true;
        try {
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

            removeListeners();
        } finally {
            uninstalling = false;
        }
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
            } else if (content.isMinimzed()) {
                toolWindowManager.getDockableDescriptor(content.getId()).setAvailable(false);
            } else {
                // Remove from tabbedContentPane
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1) {
                    valueAdjusting = true;
                    try {
                        tabbedContentPane.removeTabAt(index);
                    } finally {
                        valueAdjusting = false;
                    }
                } else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        } finally {
            // TODO: check this...
            if (contentValueAdjusting)
                return;

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

    public boolean isSelected(Content content) {
        return content == lastSelected;
    }

    public void setSelected(Content content, boolean selected) {
        if (selected) {
            if (lastSelected != null)
                lastSelected.setSelected(false);

            if (content.isMinimzed()) {
                content.setMinimzed(false);
                content.setSelected(true);
            } else if (content.isDetached()) {
                // If the content is detached request the focus for owner window
                SwingUtil.requestFocus(
                        SwingUtilities.windowForComponent(content.getComponent())
                );
            } else {
                // Choose the owner tab or check if the content is the main content
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1) {
                    valueAdjusting = true;

                    try {
                        tabbedContentPane.setSelectedIndex(index);
                        componentInFocusRequest = SwingUtil.findAndRequestFocus(tabbedContentPane.getComponentAt(index));
                        lastSelected = content;
                    } finally {
                        valueAdjusting = false;
                    }
                } else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        } else {
            if (content == lastSelected)
                lastSelected = null;
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

    public void selectNextContent(Content content) {
        // Choose next content to be selected...
        if (tabbedContentPane.getTabCount() == 0) {
            toolWindowManager.resetMainContent();
            lastSelected = null;
        }

        if (tabbedContentPane.getTabCount() == 1 && !isShowAlwaysTab()) {
            Content lastContent = contentManager.getSelectedContent();

            if (lastContent != null) {
                if (lastContent == content)
                    lastContent = contentManager.getNextContent();

                toolWindowManager.setMainContent(lastContent.getComponent());
                lastContent.setSelected(true);
            }

            lastSelected = null;
        } else {
            int selectedIndex = tabbedContentPane.getSelectedIndex();
            if (selectedIndex != -1)
                tabbedContentPane.getContentAt(selectedIndex).setSelected(true);
            else
                lastSelected = null;

            if (tabbedContentPane.getTabCount() == 0)
                toolWindowManager.resetMainContent();
        }
    }


    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        contentUIMap = new Hashtable<Content, TabbedContentUI>();

        final JTabbedContentPane tabbedContentPane = new JTabbedContentPane(true);
        tabbedContentPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!valueAdjusting && !contentValueAdjusting) {
                    int selectedIndex = tabbedContentPane.getSelectedIndex();
                    if (selectedIndex == -1)
                        return;

                    Content newSelected = tabbedContentPane.getContentAt(selectedIndex);

                    if (newSelected == lastSelected)
                        return;

                    if (lastSelected != null) {
                        try {
//                            lastSelected.fireSelected(false);
                            lastSelected.setSelected(false);
                        } catch (Exception ignoreIt) {
                        }
                    }

                    if (newSelected != null) {
//                        newSelected.fireSelected(true);
                        newSelected.setSelected(true);
                    }

                    lastSelected = newSelected;
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
            DetachedListener detachedListener = new DetachedListener();
            internalPropertyChangeSupport.addPropertyChangeListener("detached.dispose", detachedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("detached", detachedListener);
            MaximizedListener maximizedListener = new MaximizedListener();
            internalPropertyChangeSupport.addPropertyChangeListener("maximized.before", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("maximized", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("minimized", new MinimizedListener());
            contentUIListener = new ContentUIListener();

            SwingUtil.registerDragGesture(tabbedContentPane,
                                          new TabbedContentManagerDragGesture());

        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
                "focusOwner", focusOwnerPropertyChangeListener = new FocusOwnerPropertyChangeListener());
    }

    protected void removeListeners() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(
                "focusOwner", focusOwnerPropertyChangeListener);
    }

    protected int addUIForContent(Content content, Object... constaints) {
        TabbedContentUI contentUI = contentUIMap.get(content);
        if (contentUI == null) {
            if (!installed) {
                // TODO: ... This is an import
                contentUI = new MyDoggyTabbedContentUI(this, tabbedContentPane,
                                                       content);
            } else
                contentUI = new MyDoggyTabbedContentUI(this, tabbedContentPane,
                                                       content);
            contentUI.addPropertyChangeListener(contentUIListener);
        }

        contentUIMap.put(content, contentUI);

        if (!showAlwaysTab && tabbedContentPane.getTabCount() == 0 && (contentValueAdjusting || toolWindowManager.getMainContent() == null)) {
            toolWindowManager.setMainContent(content.getComponent());
            // TODO: is this right?
            lastSelected = content;
            return -1;
        } else {
            if (!showAlwaysTab && tabbedContentPane.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContent(0));
                valueAdjusting = false;
            }

            int index = addTab(content, constaints);
            toolWindowManager.setMainContent(tabbedContentPane);

            if (!tabbedContentPane.isEnabledAt(tabbedContentPane.getSelectedIndex()))
                tabbedContentPane.setSelectedIndex(tabbedContentPane.getTabCount() - 1);
            return index;
        }
    }

    protected int addTab(Content content, Object... constaints) {
        int index;
        if (constaints.length == 1 && constaints[0] instanceof Integer) {
            index = tabbedContentPane.addTab(content, content.getComponent(), (Integer) constaints[0]);
        } else {
            tabbedContentPane.addTab(content);
            index = tabbedContentPane.getTabCount() - 1;
        }

        tabbedContentPane.setDisabledIconAt(index, content.getDisabledIcon());
        int mnemonic = content.getMnemonic();
        if (mnemonic != -1)
            tabbedContentPane.setMnemonicAt(index, mnemonic);
        if (content.getForeground() != null)
            tabbedContentPane.setForegroundAt(index, content.getForeground());

        return index;
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


    protected class ComponentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            Component oldCmp = (Component) evt.getOldValue();
            Component newCmp = (Component) evt.getNewValue();

            if (content.isMinimzed())
                return;

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

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setDisabledIconAt(index, (Icon) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setIconAt(index, (Icon) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class MnemonicListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setMnemonicAt(index, (Integer) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class EnabledListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (content.isDetached()) {
                Window anchestor = SwingUtilities.windowForComponent(content.getComponent());
                anchestor.setEnabled((Boolean) evt.getNewValue());
            } else {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setEnabledAt(index, (Boolean) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setForegroundAt(index, (Color) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class TitleListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (content.isDetached()) {
                SwingUtil.setWindowTitle(content.getComponent(), (String) evt.getNewValue());
            } else {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setTitleAt(index, (String) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException();
            }
        }
    }

    protected class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1) {
                    tabbedContentPane.setToolTipTextAt(index, (String) evt.getNewValue());
                } else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
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

                    oldFucusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

                    maximizedContent = content;
                }
            } else {
                if (!(Boolean) evt.getNewValue()) {
                    if (tmpWorkspace != null) {
                        valudAdj = true;
                        try {
                            toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                                                                             resourceManager.getObject(PersistenceDelegate.MergePolicy.class,
                                                                                                       PersistenceDelegate.MergePolicy.UNION));
                            tmpWorkspace = null;
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
        protected PropertyChangeSupport contentUIListener;
        protected Map<Content, Integer> detachedContentUIMap;

        public DetachedListener() {
            parentFrame = (toolWindowManager.getParentComponent() instanceof Frame) ? (Frame) toolWindowManager.getParentComponent() : null;
            detachedContentUIMap = new HashMap<Content, Integer>();
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

                        Rectangle inBounds = toolWindowManager.getBoundsToScreen(content.getComponent().getBounds(),
                                                                                 content.getComponent().getParent());

                        // Store constraint
                        if (tabbedContentPane.getTabCount() != 0)
                            detachedContentUIMap.put(content, tabbedContentPane.indexOfContent(content));
                        else
                            detachedContentUIMap.put(content, -1);

                        // Remove content from tab
                        int tabIndex = tabbedContentPane.indexOfContent(content);
                        if (tabIndex != -1) {
                            tabbedContentPane.removeTabAt(tabIndex);
                            if (tabbedContentPane.getTabCount() == 0)
                                toolWindowManager.resetMainContent();
                        } else {
                            if (tabbedContentPane.getParent() == null)
                                toolWindowManager.resetMainContent();
                            else
                                throw new IllegalStateException("Invalid Content : " + content);
                        }

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
                        int index = addUIForContent(content, detachedContentUIMap.get(content));
                        tabbedContentPane.setSelectedIndex(index);
                        componentInFocusRequest = SwingUtil.findAndRequestFocus(tabbedContentPane.getComponentAt(index));
                    } finally {
                        contentValueAdjusting = false;
                        detachedContentUIMap.remove(content);
                    }
                }
            }
        }

    }

    protected class MinimizedListener implements PropertyChangeListener {
        protected Map<Content, Integer> minimizedContentUIMap;

        public MinimizedListener() {
            minimizedContentUIMap = new HashMap<Content, Integer>();
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
                // Store constraint
                if (tabbedContentPane.getTabCount() != 0)
                    minimizedContentUIMap.put(content, tabbedContentPane.indexOfContent(content));
                else
                    minimizedContentUIMap.put(content, -1);

                // Remove content from tab
                int tabIndex = tabbedContentPane.indexOfContent(content);
                if (tabIndex != -1) {
                    tabbedContentPane.removeTabAt(tabIndex);
                    if (tabbedContentPane.getTabCount() == 0)
                        toolWindowManager.resetMainContent();
                } else {
                    if (tabbedContentPane.getParent() == null)
                        toolWindowManager.resetMainContent();
                    else
                        throw new IllegalStateException("Invalid Content : " + content);
                }

                // Put on bar
                descriptor.setAvailable(true);
            } else {
                DockableDescriptor descriptor = toolWindowManager.getDockableDescriptor(content.getId());

                // Remove from bar
                descriptor.setAvailable(false);

                contentValueAdjusting = true;
                try {
                    addUIForContent(content, minimizedContentUIMap.get(content));
                    content.setSelected(true);

                    componentInFocusRequest = SwingUtil.findAndRequestFocus(content.getComponent());
                } finally {
                    contentValueAdjusting = false;
                    minimizedContentUIMap.remove(content);
                }
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
                                  new MyDoggyTransferable(manager,
                                                          MyDoggyTransferable.CONTENT_ID_DF,
                                                          content.getId()),
                                  this);

                    // Setup ghostImage

                    if (resourceManager.getBoolean("drag.icon.useDefault", false)) {
                        setGhostImage(dge.getDragOrigin(),
                                      resourceManager.getBufferedImage(MyDoggyKeySpace.DRAG));
                    } else {
                        Component component = tabbedContentPane.getComponentAt(index);
                        BufferedImage ghostImage = new BufferedImage(component.getWidth(),
                                                                     component.getHeight(), BufferedImage.TYPE_INT_RGB);
                        component.print(ghostImage.getGraphics());
                        ghostImage = GraphicsUtil.scale(ghostImage,
                                                        component.getWidth() / 4,
                                                        component.getHeight() / 4);

                        setGhostImage(dge.getDragOrigin(), ghostImage);
                    }
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

    protected class FocusOwnerPropertyChangeListener implements PropertyChangeListener {

        public FocusOwnerPropertyChangeListener() {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (!isContentManagerEnabled())
                return;

            if (valueAdjusting)
                return;

            if (componentInFocusRequest != null) {
                if (evt.getNewValue() == componentInFocusRequest)
                    componentInFocusRequest = null;
                else
                    return;
            }

            if (evt.getNewValue() != null) {
                Component cursor = (Component) evt.getNewValue();
                while (cursor != null) {
                    int index = tabbedContentPane.indexOfComponent(cursor);
                    if (index != -1) {
                        Content content = tabbedContentPane.getContentAt(index);
                        if (!content.isSelected() && !content.isDetached())
                            content.setSelected(true);

                        break;
                    }
                    cursor = cursor.getParent();
                }
            }
        }
    }

}
