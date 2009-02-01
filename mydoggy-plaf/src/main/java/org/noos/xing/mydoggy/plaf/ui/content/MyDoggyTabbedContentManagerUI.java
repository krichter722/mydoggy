package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.UserPropertyChangeEvent;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDialog;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentFrame;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentWindow;
import org.noos.xing.mydoggy.plaf.ui.cmp.TabbedContentPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.drag.DragListenerAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;
import org.noos.xing.mydoggy.plaf.ui.util.RemoveNotifyDragListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
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
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyTabbedContentManagerUI extends MyDoggyContentManagerUI<TabbedContentUI> implements TabbedContentManagerUI,
                                                                                                       PlafContentManagerUI {
    protected TabbedContentPane tabbedContentPane;
    protected boolean showAlwaysTab;

    protected Component componentInFocusRequest = null;

    protected FocusOwnerPropertyChangeListener focusOwnerPropertyChangeListener;

    // Drag fields
    protected RemoveNotifyDragListener removeNotifyDragListener;


    public MyDoggyTabbedContentManagerUI() {
        setContentManagerUI(this);

        this.showAlwaysTab = false;
        initComponents();
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
        try {
            for (Content content : contentManager.getContents()) {
                if (content.isSelected())
                    selectedContent = content;

                addContent((PlafContent) content);
            }
        } finally {
            contentValueAdjusting = false;
        }

        if (oldContentManagerUI != null) {
            // Import listeners from the old ContentManagerUI
            if (SwingUtil.getBoolean("ContentManagerUI.ContentManagerUiListener.import", false)) {
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
        if (oldContentManagerUI != null) {
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
        }

        return this;
    }

    public void uninstall() {
        uninstalling = true;
        try {
            if (maximizedContent != null)
                maximizedContent.setMaximized(false);

            // Remove all contents
            contentValueAdjusting = true;
            try {
                for (Content content : contentManager.getContents()) {
                    removeContent((PlafContent) content);
                }
            } finally {
                contentValueAdjusting = false;
            }

            removeListeners();

            // Now you can consider this manager uninstalled
            this.installed = false;
        } finally {
            uninstalling = false;
        }
    }

    public synchronized void setSelected(Content content, boolean selected) {
//        System.out.println("content = " + content.getId() + " selected =  " + selected);

        if (selected) {
            if (lastSelected != null)
                lastSelected.setSelected(false);

            if (content.isMinimized()) {
                content.setMinimized(false);
                content.setSelected(true);
            } else if (content.isDetached()) {
                // If the content is detached request the focus for owner window
                Window window = SwingUtilities.windowForComponent(content.getComponent());
                window.toFront();
                SwingUtil.requestFocus(window);
            } else {
                // Choose the owner tab or check if the content is the main content
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1) {
                    valueAdjusting = true;

                    try {
                        tabbedContentPane.setSelectedIndex(index);
                        if (!isFocusAncestor(content.getComponent()))
                            componentInFocusRequest = findAndRequestFocus(tabbedContentPane.getComponentAt(index));
                        lastSelected = content;
                    } finally {
                        valueAdjusting = false;
                    }
                } else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent()) {
                    throw new IllegalStateException("Invalid content ui state.");
                } else {
                    if (!isFocusAncestor(content.getComponent()))
                        componentInFocusRequest = findAndRequestFocus(toolWindowManager.getMainContent());
                    lastSelected = content;
                }
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


    protected void initComponents() {
        final TabbedContentPane tabbedContentPane = new TabbedContentPane(true);
        tabbedContentPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!valueAdjusting && !contentValueAdjusting && !tabbedContentPane.valueAdjusting) {
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

                    if (newSelected != null && !newSelected.isMinimized()) {
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
            internalPropertyChangeSupport.addPropertyChangeListener("maximizedBefore", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("maximized", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("minimized", new MinimizedListener());
            contentUIListener = new ContentUIListener();

            toolWindowManager.addRemoveNotifyListener(
                    removeNotifyDragListener = new RemoveNotifyDragListener(tabbedContentPane,
                                                                            new TabbedContentManagerDragListener())
            );
        }

        toolWindowManager.addInternalPropertyChangeListener("managerWindowAncestor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue() == null)
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(
                            "focusOwner", focusOwnerPropertyChangeListener
                    );
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(
                "focusOwner", focusOwnerPropertyChangeListener = new FocusOwnerPropertyChangeListener()
        );
    }

    protected void removeListeners() {
        // Remove drag gesture
        removeNotifyDragListener.cleanup();
        toolWindowManager.removeRemoveNotifyListener(removeNotifyDragListener);

        // Remove focus listener
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(
                "focusOwner", focusOwnerPropertyChangeListener
        );
    }

    protected Object addUIForContent(Content content, Object... constaints) {
        TabbedContentUI contentUI = contentUIMap.get(content);
        if (contentUI == null) {
            contentUI = new MyDoggyTabbedContentUI(this, tabbedContentPane, content);
            contentUI.addPropertyChangeListener(contentUIListener);
            contentUI.setCloseable(closeable);
            contentUI.setDetachable(detachable);
            contentUI.setMinimizable(minimizable);

            contentUIMap.put(content, contentUI);
        }


        if (!showAlwaysTab && tabbedContentPane.getTabCount() == 0 && (/*contentValueAdjusting || */toolWindowManager.getMainContent() == null)) {
            toolWindowManager.setMainContent(content.getComponent());
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

    protected void removeUIForContent(Content content) {
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


    public class ComponentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            Component oldCmp = (Component) evt.getOldValue();
            Component newCmp = (Component) evt.getNewValue();

            if (content.isMinimized())
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

    public class DisabledIconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
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

    public class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
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

    public class MnemonicListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
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

    public class EnabledListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (content.isDetached()) {
                Window ancestor = SwingUtilities.windowForComponent(content.getComponent());
                ancestor.setEnabled((Boolean) evt.getNewValue());
            } else {
                int index = tabbedContentPane.indexOfContent(content);
                if (index != -1)
                    tabbedContentPane.setEnabledAt(index, (Boolean) evt.getNewValue());
                else if (isContentManagerEnabled() && toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    public class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
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

    public class TitleListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
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

    public class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
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

    public class MaximizedListener implements PropertyChangeListener {
        protected ByteArrayOutputStream tmpWorkspace;
        protected Component oldFucusOwner;
        protected boolean valudAdj;

        public void propertyChange(PropertyChangeEvent evt) {
            if (valudAdj)
                return;
            Content content = (Content) evt.getSource();

            if ("maximizedBefore".equals(evt.getPropertyName())) {
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

    public class DetachedListener implements PropertyChangeListener {
        protected PropertyChangeSupport contentUIListener;
        protected Map<Content, Integer> detachedContentUIMap;

        public DetachedListener() {
            detachedContentUIMap = new HashMap<Content, Integer>();
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if ("detached.dispose".equals(evt.getPropertyName())) {
                Window window = SwingUtilities.windowForComponent(content.getComponent());
                window.setVisible(false);
                window.dispose();

                detachedContentUIMap.remove(content);
            } else {
                if ((Boolean) evt.getNewValue()) {
                    valueAdjusting = true;
                    try {
                        if (evt instanceof UserPropertyChangeEvent) {
                            // We are here because a call ot the detach methods was made.

                            UserPropertyChangeEvent userEvent = (UserPropertyChangeEvent) evt;
                            MultiSplitConstraint constraint = (MultiSplitConstraint) userEvent.getUserObject();

                            switch (constraint.getOnIndex()) {
                                case -2:
                                    for (Window window : SwingUtil.getTopContainers()) {
                                        if (window instanceof ContentWindow) {
                                            ContentWindow contentWindow = (ContentWindow) window;

                                            if (contentWindow.containsDockable(constraint.getOnContent())) {
                                                // remove content
                                                removeContent(content);

                                                contentWindow.addDockable(content,
                                                                          content.getComponent(),
                                                                          null,
                                                                          constraint.getOnPosition());
                                                break;
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    for (Window window : SwingUtil.getTopContainers()) {
                                        if (window instanceof ContentWindow) {
                                            ContentWindow contentWindow = (ContentWindow) window;

                                            if (contentWindow.containsDockable(constraint.getOnContent())) {
                                                // remove content
                                                removeContent(content);

                                                contentWindow.addDockable(content,
                                                                          content.getComponent(),
                                                                          constraint.getOnContent(),
                                                                          constraint.getOnPosition());
                                                break;
                                            }
                                        }
                                    }
                            }
                        } else {
                            ContentUI contentUI = getContentUI(content);

                            Rectangle inBounds = toolWindowManager.getBoundsToScreen(content.getComponent().getBounds(),
                                                                                     content.getComponent().getParent());

                            // remove content
                            removeContent(content);

                            // Setup dialog
                            Frame parentFrame = (toolWindowManager.getWindowAncestor() instanceof Frame) ? (Frame) toolWindowManager.getWindowAncestor() : null;

                            Window dialog;
                            if (contentUI.isAddToTaskBarWhenDetached()) {
                                dialog = new ContentFrame(
                                        content, contentUI,
                                        parentFrame, inBounds);
                            } else {
                                dialog = new ContentDialog(
                                        content, contentUI,
                                        parentFrame, inBounds);
                            }
                            dialog.addWindowFocusListener(new ContentDialogFocusListener(content));
                            dialog.toFront();
                            dialog.setVisible(true);

                            componentInFocusRequest = findAndRequestFocus(dialog);
                        }
                    } finally {
                        valueAdjusting = false;
                    }
                } else {
                    ContentWindow window = (ContentWindow) SwingUtilities.windowForComponent(content.getComponent());
                    window.removeDockable(content);

                    if (window.getNumDockables() <= 0) {
                        window.setVisible(false);
                        window.dispose();
                    }

                    contentValueAdjusting = true;
                    try {
                        int index = 0;
                        Integer constraint = SwingUtil.getAt(evt, 0, null);

                        if (constraint != null) {
                            // We are here because a call ot the detach methods was made.
                            index = (Integer) addUIForContent(content, constraint);
                        } else {
                            index = (Integer) addUIForContent(content, detachedContentUIMap.get(content));
                        }

                        tabbedContentPane.setSelectedIndex(index);
                        componentInFocusRequest = findAndRequestFocus(tabbedContentPane.getComponentAt(index));
                    } finally {
                        contentValueAdjusting = false;
                        detachedContentUIMap.remove(content);
                    }
                }
            }
        }

        protected void removeContent(Content content) {
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
        }
    }

    public class MinimizedListener implements PropertyChangeListener {
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

                    componentInFocusRequest = findAndRequestFocus(content.getComponent());
                } finally {
                    contentValueAdjusting = false;
                    minimizedContentUIMap.remove(content);
                }
            }
        }

    }


    public class TabbedContentManagerDragListener extends DragListenerAdapter {

        public TabbedContentManagerDragListener() {
            super(toolWindowManager);
        }

        public void dragGestureRecognized(DragGestureEvent dge) {
            super.dragGestureRecognized(dge);

            // Acquire locks
            if (!acquireLocks())
                return;

            // Start Drag
            Point origin = dge.getDragOrigin();
            int index = tabbedContentPane.indexAtLocation(origin.x, origin.y);
            if (index != -1) {
                Content content = tabbedContentPane.getContentAt(index);
                if (content.getDockableDelegator() != null) {
                    dge.startDrag(DragSource.DefaultMoveDrop,
                                  new MyDoggyTransferable(manager,
                                                          MyDoggyTransferable.CONTENT_ID_DF,
                                                          content.getId()),
                                  this);

                    // Setup ghostImage

                    if (SwingUtil.getBoolean("drag.icon.useDefault", false)) {
                        setGhostImage(dge.getDragOrigin(),
                                      SwingUtil.getImage(MyDoggyKeySpace.DRAG));
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
            try {
                if (!checkStatus())
                    return;

                releaseLocks();
            } finally {
                // Finalize drag action...
                cleanupGhostImage();
                dockableDropDragEnd();
            }
        }

    }

    public class FocusOwnerPropertyChangeListener implements PropertyChangeListener {

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
