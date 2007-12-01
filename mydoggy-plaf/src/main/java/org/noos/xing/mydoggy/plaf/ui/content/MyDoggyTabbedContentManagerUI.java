package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.drag.DragGestureAdapter;
import org.noos.xing.mydoggy.plaf.ui.drag.MyDoggyTransferable;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;
import org.noos.xing.mydoggy.plaf.ui.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
    protected boolean installed;

    protected PropertyChangeSupport propertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;
    protected PropertyChangeSupport propertyChangeListeners;

    protected PlafContentUI lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, TabbedContentUI> contentUIMap;
    protected Map<Content, TabbedContentUI> detachedContentUIMap;

    public MyDoggyTabbedContentManagerUI() {
        this.propertyChangeListeners = new PropertyChangeSupport(this);

        initComponents();
    }


    public void setCloseable(boolean closeable) {
        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setCloseable(closeable);
        }
    }

    public void setDetachable(boolean detachable) {
        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setDetachable(detachable);
        }
    }

    public TabbedContentUI getContentUI(Content content) {
        return contentUIMap.get(content);
    }

    public void setTabPlacement(TabPlacement tabPlacement) {
        if (tabPlacement == null || tabPlacement == getTabPlacement())
            return;

        TabPlacement old = getTabPlacement();
        tabbedContentPane.setTabPlacement(tabPlacement.ordinal() + 1);

        propertyChangeListeners.firePropertyChange("tabPlacement", old, tabPlacement);
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

        propertyChangeListeners.firePropertyChange("tabLayout", old, tabLayout);
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
        this.showAlwaysTab = showAlwaysTab;

        if (showAlwaysTab) {
            if (contentManager.getContentCount() == 1 && toolWindowManager.getMainContent() != tabbedContentPane && tabbedContentPane.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContentByComponent(toolWindowManager.getMainContent()));
                valueAdjusting = false;

                toolWindowManager.setMainContent(tabbedContentPane);
            }
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListeners.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListeners.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeListeners.getPropertyChangeListeners();
    }


    public Container getContainer() {
        return tabbedContentPane;
    }

    public PlafContentManagerUI install(ContentManagerUI oldContentManagerUI, ToolWindowManager manager) {
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = manager.getContentManager();
        this.resourceManager = toolWindowManager.getResourceManager();
        this.tabbedContentPane.setToolWindowManager(toolWindowManager);

        initListeners();

        setPopupMenu(contentManager.getPopupMenu());

        lastSelected = null;
        Content selectedContent = null;
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            if (content.isSelected())
                selectedContent = content;
            addContent((PlafContentUI) content);
            contentValueAdjusting = false;
        }
        contentValueAdjusting = false;

        if (oldContentManagerUI != null) {
            for (ContentManagerUIListener listener : oldContentManagerUI.getContentManagerUiListener()) {
                addContentManagerUIListener(listener);
            }
        }

        this.installed = true;

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
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            removeContent((PlafContentUI) content);
        }
        contentValueAdjusting = false;
        this.installed = false;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void addContent(PlafContentUI content, Object... constraints) {
        addUIForContent(content);
        content.addUIPropertyChangeListener(this);
    }

    public void removeContent(PlafContentUI content) {
        if (content.isDetached())
            content.setDetached(false);

        int index = tabbedContentPane.indexOfContent(content);
        if (index != -1) {
            tabbedContentPane.removeTabAt(index);
            content.removePropertyChangeListener(this);
        } else if (toolWindowManager.getMainContent() != content.getComponent())
            throw new IllegalStateException("Invalid content ui state.");

        content.removeUIPropertyChangeListener(this);

        if (contentValueAdjusting)
            return;

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

        contentUIMap.remove(content);
    }

    public boolean isSelected(Content content) {
        return content == lastSelected;
    }

    public void setSelected(Content content, boolean selected) {
        if (content.isDetached()) {
            SwingUtil.requestFocus(
                    SwingUtilities.windowForComponent(content.getComponent())
            );
        } else {
            int index = tabbedContentPane.indexOfContent(content);
            if (index != -1) {
                valueAdjusting = true;
                tabbedContentPane.setSelectedIndex(index);
                lastSelected = (PlafContentUI) content;
                valueAdjusting = false;
            } else if (toolWindowManager.getMainContent() != content.getComponent())
                throw new IllegalStateException("Invalid content ui state.");
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

    public void addContentManagerUIListener(ContentManagerUIListener listener) {
        contentManagerUIListeners.add(ContentManagerUIListener.class, listener);
    }

    public void removeContentManagerUIListener(ContentManagerUIListener listener) {
        contentManagerUIListeners.remove(ContentManagerUIListener.class, listener);
    }

    public ContentManagerUIListener[] getContentManagerUiListener() {
        return contentManagerUIListeners.getListeners(ContentManagerUIListener.class);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        detachedContentUIMap = new Hashtable<Content, TabbedContentUI>();
        contentUIMap = new Hashtable<Content, TabbedContentUI>();

        final JTabbedContentPane tabbedContentPane = new JTabbedContentPane();
        tabbedContentPane .addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!valueAdjusting && !contentValueAdjusting) {
                    int selectedIndex = tabbedContentPane.getSelectedIndex();
                    if (selectedIndex == -1)
                        return;

                    PlafContentUI newSelected = (PlafContentUI) tabbedContentPane.getContentAt(selectedIndex);

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
    }

    protected void initListeners() {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
            propertyChangeSupport.addPropertyChangeListener("component", new ComponentListener());
            propertyChangeSupport.addPropertyChangeListener("disabledIcon", new DisabledIconListener());
            propertyChangeSupport.addPropertyChangeListener("icon", new IconListener());
            propertyChangeSupport.addPropertyChangeListener("mnemonic", new MnemonicListener());
            propertyChangeSupport.addPropertyChangeListener("enabled", new EnabledListener());
            propertyChangeSupport.addPropertyChangeListener("foreground", new ForegroundListener());
            propertyChangeSupport.addPropertyChangeListener("title", new TitleListener());
            propertyChangeSupport.addPropertyChangeListener("toolTipText", new ToolTipTextListener());
            propertyChangeSupport.addPropertyChangeListener("detached", new DetachedListener());
        }
        SwingUtil.registerDragGesture(tabbedContentPane,
                                      new TabbedContentManagerDragGesture());
        contentManagerUIListeners = new EventListenerList();
    }

    protected void addUIForContent(Content content, Object... constaints) {
        contentUIMap.put(content, new MyDoggyTabbedContentUI(content));
                
        if (!showAlwaysTab && tabbedContentPane.getTabCount() == 0 && (contentValueAdjusting || toolWindowManager.getMainContent() == null)) {
            detachedContentUIMap.put(content, getContentUI(content));
            toolWindowManager.setMainContent(content.getComponent());
            lastSelected = (PlafContentUI) content;
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


    class ComponentListener implements PropertyChangeListener {
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

    class DisabledIconListener implements PropertyChangeListener {
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

    class IconListener implements PropertyChangeListener {
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

    class MnemonicListener implements PropertyChangeListener {
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

    class EnabledListener implements PropertyChangeListener {
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

    class ForegroundListener implements PropertyChangeListener {
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

    class TitleListener implements PropertyChangeListener {
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

    class ToolTipTextListener implements PropertyChangeListener {
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

    class DetachedListener implements PropertyChangeListener {
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

                final JDialog dialog = new JDialog(resourceManager.getBoolean("dialog.owner.enabled", true) ?  parentFrame : null,
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
                        PlafContentUI content = (PlafContentUI) contentManager.getContentByComponent(component);
                        content.fireSelected(false);
                        content.setDetached(false);
                    }
                });

                dialog.addWindowFocusListener(new WindowFocusListener() {
                    public void windowGainedFocus(WindowEvent e) {
                        if (!valueAdjusting && !contentValueAdjusting) {
                            PlafContentUI newSelected = (PlafContentUI) contentManager.getContentByComponent(
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

    class TabbedContentManagerDragGesture extends DragGestureAdapter {

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
                if (content.getDockableDelegator() !=null) {
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
