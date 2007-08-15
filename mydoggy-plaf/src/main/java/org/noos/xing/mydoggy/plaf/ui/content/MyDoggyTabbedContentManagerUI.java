package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabEvent;
import org.noos.xing.mydoggy.plaf.ui.content.BackContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.content.BackContentUI;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentPage;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabListener;
import org.noos.xing.mydoggy.plaf.ui.transparency.WindowTransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
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
public class MyDoggyTabbedContentManagerUI implements TabbedContentManagerUI, BackContentManagerUI, PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected ContentManager contentManager;

    protected JTabbedContentManager tabbedContentManager;
    protected boolean showAlwaysTab;

    protected PropertyChangeSupport propertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;
    protected PropertyChangeSupport propertyChangeListeners;

    protected BackContentUI lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, TabbedContentUI> detachedContentUIMap;

    public MyDoggyTabbedContentManagerUI() {
        this.propertyChangeListeners = new PropertyChangeSupport(this);

        initComponents();
    }


    public void setCloseable(boolean closeable) {
        tabbedContentManager.setCloseable(closeable);
    }

    public void setDetachable(boolean detachable) {
        tabbedContentManager.setDetachable(detachable);
    }

    public TabbedContentUI getContentUI(Content content) {
        if (content.isDetached() || tabbedContentManager.getTabCount() == 0) {
            return detachedContentUIMap.get(content);
        } else
            return tabbedContentManager.getContentPage(
                    tabbedContentManager.indexOfComponent(content.getComponent()));
    }

    public void setTabPlacement(TabPlacement tabPlacement) {
        if (tabPlacement == null || tabPlacement == getTabPlacement())
            return;

        TabPlacement old = getTabPlacement();
        tabbedContentManager.setTabPlacement(tabPlacement.ordinal() + 1);

        propertyChangeListeners.firePropertyChange("tabPlacement", old, tabPlacement);
    }

    public TabPlacement getTabPlacement() {
        switch (tabbedContentManager.getTabPlacement()) {
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
        tabbedContentManager.setTabLayoutPolicy(tabLayout.ordinal());

        propertyChangeListeners.firePropertyChange("tabLayout", old, tabLayout);
    }

    public TabLayout getTabLayout() {
        switch (tabbedContentManager.getTabLayoutPolicy()) {
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
            if (contentManager.getContentCount() == 1 && toolWindowManager.getMainContent() != tabbedContentManager && tabbedContentManager.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContentByComponent(toolWindowManager.getMainContent()));
                valueAdjusting = false;

                toolWindowManager.setMainContent(tabbedContentManager);
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
        return tabbedContentManager;
    }

    public void install(ToolWindowManager manager) {
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = manager.getContentManager();
        tabbedContentManager.setToolWindowManager(toolWindowManager);
        initListeners();

        setPopupMenu(contentManager.getPopupMenu());

        lastSelected = null;
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            addContent((BackContentUI) content);
            contentValueAdjusting = false;
        }
        contentValueAdjusting = false;
    }

    public void unistall() {
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            removeContent((BackContentUI) content);
        }
        contentValueAdjusting = false;
    }

    public void addContent(BackContentUI content) {
        addUIForContent(content);
        content.addUIPropertyChangeListener(this);
    }

    public void removeContent(BackContentUI content) {
        if (content.isDetached())
            content.setDetached(false);

        int index = tabbedContentManager.indexOfComponent(content.getComponent());
        if (index != -1) {
            tabbedContentManager.removeTabAt(index);
            content.removePropertyChangeListener(this);
        } else if (toolWindowManager.getMainContent() != content.getComponent())
            throw new IllegalStateException("Invalid content ui state.");

        content.removeUIPropertyChangeListener(this);

        if (tabbedContentManager.getTabCount() == 0) {
            toolWindowManager.resetMainContent();
            lastSelected = null;
        } else {
            tabbedContentManager.getContentPage(tabbedContentManager.getSelectedIndex()).getContent().setSelected(true);
        }
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
            int index = tabbedContentManager.indexOfComponent(content.getComponent());
            if (index != -1) {
                valueAdjusting = true;
                tabbedContentManager.setSelectedIndex(index);
                lastSelected = (BackContentUI) content;
                valueAdjusting = false;
            } else if (toolWindowManager.getMainContent() != content.getComponent())
                throw new IllegalStateException("Invalid content ui state.");
        }
    }

    public JPopupMenu getPopupMenu() {
        return tabbedContentManager.getPopupMenu();
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        tabbedContentManager.setPopupMenu(popupMenu);
    }

    public void updateUI() {
        tabbedContentManager.updateUI();
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

        final JTabbedContentManager tabbedContentManager = new JTabbedContentManager();

        tabbedContentManager.addTabListener(new TabListener() {
            public void tabEventFired(TabEvent event) {
                Content content = contentManager.getContentByComponent(event.getContentManager().getComponentAt(event.getOverTabIndex()));
                switch (event.getActionId()) {
                    case ON_CLOSE:
                        try {
                            fireContentUIRemoving(getContentUI(content));
                            contentManager.removeContent(content);
                        } catch (Exception ignore) {
                        }
                        break;
                    case ON_DETACH:
                        content.setDetached(true);
                        fireContentUIDetached(getContentUI(content));
                        break;
                }
            }
        });
        tabbedContentManager.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (!valueAdjusting && !contentValueAdjusting) {
                    Component selectedComponent = tabbedContentManager.getSelectedComponent();
                    if (selectedComponent == null)
                        return;
                    BackContentUI newSelected = (BackContentUI) contentManager.getContentByComponent(selectedComponent);

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

        this.tabbedContentManager = tabbedContentManager;
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
            propertyChangeSupport.addPropertyChangeListener("popupMenu", new PopupMenuListener());
            propertyChangeSupport.addPropertyChangeListener("title", new TitleListener());
            propertyChangeSupport.addPropertyChangeListener("toolTipText", new ToolTipTextListener());
            propertyChangeSupport.addPropertyChangeListener("detached", new DetachedListener());
            propertyChangeSupport.addPropertyChangeListener("selected", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    //                System.out.println("SELECTED " + evt.getNewValue());
                }
            });
        }
        contentManagerUIListeners = new EventListenerList();
    }

    protected void addUIForContent(Content content) {
        if (!showAlwaysTab && tabbedContentManager.getTabCount() == 0 && (contentValueAdjusting || toolWindowManager.getMainContent() == null)) {
            detachedContentUIMap.put(content, new ContentPage(content, tabbedContentManager, null));
            toolWindowManager.setMainContent(content.getComponent());
            lastSelected = (BackContentUI) content;
        } else {
            if (!showAlwaysTab && tabbedContentManager.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContentByComponent(toolWindowManager.getMainContent()));
                valueAdjusting = false;
            }

            addTab(content);
            toolWindowManager.setMainContent(tabbedContentManager);

            if (!tabbedContentManager.isEnabledAt(tabbedContentManager.getSelectedIndex()))
                tabbedContentManager.setSelectedIndex(tabbedContentManager.getTabCount() - 1);
        }
    }

    protected void addTab(Content content) {
        tabbedContentManager.addTab(content.getTitle(),
                                    content.getIcon(),
                                    content.getComponent(),
                                    content.getToolTipText(),
                                    detachedContentUIMap.remove(content));

        int index = tabbedContentManager.getTabCount() - 1;
        tabbedContentManager.getContentPage(index).setContent(content);
        tabbedContentManager.setDisabledIconAt(index, content.getDisabledIcon());
        tabbedContentManager.setPopupMenuAt(index, content.getPopupMenu());
        int mnemonic = content.getMnemonic();
        if (mnemonic != -1)
            tabbedContentManager.setMnemonicAt(index, mnemonic);
        if (content.getForeground() != null)
            tabbedContentManager.setForegroundAt(index, content.getForeground());
    }

    protected void fireContentUIRemoving(ContentUI contentUI) {
        ContentManagerUIEvent event = new ContentManagerUIEvent(this, ContentManagerUIEvent.ActionId.CONTENTUI_REMOVING, contentUI);
        for (ContentManagerUIListener listener : contentManagerUIListeners.getListeners(ContentManagerUIListener.class)) {
            if (!listener.contentUIRemoving(event))
                throw new RuntimeException("Cannot remove Content.");
        }
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
                int index = tabbedContentManager.indexOfComponent(oldCmp);
                if (index != -1)
                    tabbedContentManager.setComponentAt(index, newCmp);
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
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setDisabledIconAt(index, (Icon) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setIconAt(index, (Icon) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    class MnemonicListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setMnemonicAt(index, (Integer) evt.getNewValue());
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
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setEnabledAt(index, (Boolean) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setForegroundAt(index, (Color) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    class PopupMenuListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setPopupMenuAt(index, (JPopupMenu) evt.getNewValue());
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
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1)
                    tabbedContentManager.setTitleAt(index, (String) evt.getNewValue());
                else if (toolWindowManager.getMainContent() != content.getComponent())
                    throw new IllegalStateException();
            }
        }
    }

    class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                int index = tabbedContentManager.indexOfComponent(content.getComponent());
                if (index != -1) {
                    String newToolTip = (String) evt.getNewValue();
                    if (newToolTip == null)
                        newToolTip = "";
                    tabbedContentManager.setToolTipTextAt(index, newToolTip);
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
                if (tabbedContentManager.getTabCount() != 0) {
                    TabbedContentUI contentUI = tabbedContentManager.getContentPage(
                            tabbedContentManager.indexOfComponent(content.getComponent())
                    );
                    detachedContentUIMap.put(content, contentUI);
                }   

                final JDialog dialog = new JDialog(parentFrame, false);
                dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

                Window parentWindow = SwingUtilities.windowForComponent(tabbedContentManager);
                Component component = content.getComponent();

                int tabIndex = tabbedContentManager.indexOfComponent(component);
                if (tabIndex != -1) {
                    tabbedContentManager.removeTabAt(tabIndex);
                } else {
                    if (tabbedContentManager.getParent() == null)
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

                if (WindowTransparencyManager.getInstance().isServiceAvailable()) {
                    WindowTransparencyListener windowTransparencyListener = new WindowTransparencyListener(
                            getContentUI(content), dialog
                    );
                    dialog.addWindowListener(windowTransparencyListener);
                    dialog.addWindowFocusListener(windowTransparencyListener);
                }

                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent event) {
                        Component component = dialog.getContentPane().getComponent(0);
                        BackContentUI content = (BackContentUI) contentManager.getContentByComponent(component);
                        content.fireSelected(false);
                        content.setDetached(false);
                    }
                });

                dialog.addWindowFocusListener(new WindowFocusListener() {
                    public void windowGainedFocus(WindowEvent e) {
                        if (!valueAdjusting && !contentValueAdjusting) {
                            BackContentUI newSelected = (BackContentUI) contentManager.getContentByComponent(
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
                tabbedContentManager.setSelectedIndex(tabbedContentManager.getTabCount() - 1);
            }
        }

    }

}
