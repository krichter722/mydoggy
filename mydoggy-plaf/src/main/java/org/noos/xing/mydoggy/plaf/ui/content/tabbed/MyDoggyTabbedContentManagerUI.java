package org.noos.xing.mydoggy.plaf.ui.content.tabbed;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.TabbedContentUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.support.PropertyChangeSupport;
import org.noos.xing.mydoggy.plaf.ui.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.content.ContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.content.ContentUI;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.component.JTabbedContentManager;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.component.TabEvent;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.component.TabListener;
import org.noos.xing.mydoggy.plaf.ui.transparency.TransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyTabbedContentManagerUI implements TabbedContentManagerUI, ContentManagerUI, PropertyChangeListener {
    private MyDoggyToolWindowManager toolWindowManager;
    private MyDoggyContentManager contentManager;

    private JTabbedContentManager tabbedContentManager;
    private boolean showAlwaysTab;

    private PropertyChangeSupport propertyChangeSupport;

    private ContentUI lastSelected;

    boolean valueAdjusting;
    boolean contentValueAdjusting;


    public MyDoggyTabbedContentManagerUI() {
        initComponents();
    }


    public boolean isShowAlwaysTab() {
        return showAlwaysTab;
    }

    public void setCloseable(boolean closeable) {
        tabbedContentManager.setCloseable(closeable);
    }

    public void setDetachable(boolean detachable) {
        tabbedContentManager.setDetachable(detachable);
    }

    public void setTabPlacement(TabPlacement tabPlacement) {
        if (tabPlacement == null)
            return;
        tabbedContentManager.setTabPlacement(tabPlacement.ordinal() + 1);
    }

    public TabPlacement getTabPlacement() {
        switch(tabbedContentManager.getTabPlacement()) {
            case SwingConstants.TOP :
                return TabPlacement.TOP;
            case SwingConstants.LEFT :
                return TabPlacement.LEFT;
            case SwingConstants.BOTTOM :
                return TabPlacement.BOTTOM;
            case SwingConstants.RIGHT :
                return TabPlacement.RIGHT;
        }
        throw new IllegalStateException("Invalid Tab Placement...");
    }

    public void setTabLayout(TabLayout tabLayout) {
        if (tabLayout == null)
            return;
        tabbedContentManager.setTabLayoutPolicy(tabLayout.ordinal());
    }

    public TabLayout getTabLayout() {
        switch(tabbedContentManager.getTabLayoutPolicy()) {
            case JTabbedPane.WRAP_TAB_LAYOUT:
                return TabLayout.WRAP;
            case JTabbedPane.SCROLL_TAB_LAYOUT :
                return TabLayout.SCROLL;
        }
        throw new IllegalStateException("Invalid Tab Layout...");
    }

    public void setShowAlwaysTab(boolean showAlwaysTab) {
        this.showAlwaysTab = showAlwaysTab;

        if (showAlwaysTab) {
            if (contentManager.getContentCount() == 1 && toolWindowManager.getMainContent() != tabbedContentManager && tabbedContentManager.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContent(toolWindowManager.getMainContent()));
                valueAdjusting = false;

                toolWindowManager.setMainContent(tabbedContentManager);
            }
        }
    }

    public TabbedContentUI getTabbedContentUI(Content content) {
        return tabbedContentManager.getContentPage(
                tabbedContentManager.indexOfComponent(content.getComponent()));
    }


    public void install(ToolWindowManager manager) {
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = (MyDoggyContentManager) manager.getContentManager();
        initListeners();

		setPopupMenu(contentManager.getPopupMenu());

        lastSelected = null;
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            addContent((ContentUI) content);
            contentValueAdjusting = false;
        }
        contentValueAdjusting = false;
    }

    public void unistall() {
        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            removeContent((ContentUI) content);
        }
        contentValueAdjusting = false;
    }

    public void addContent(ContentUI content) {
        addUIForContent(content);
        content.addUIPropertyChangeListener(this);
    }

    public void removeContent(ContentUI content) {
        if (content.isDetached())
            content.setDetached(false);

        int index = tabbedContentManager.indexOfComponent(content.getComponent());
        if (index != -1) {
            tabbedContentManager.removeTabAt(index);
            content.removePropertyChangeListener(this);
        } else if (toolWindowManager.getMainContent() != content.getComponent())
            throw new IllegalStateException("Invalid content ui state.");

        if (tabbedContentManager.getTabCount() == 0)
            toolWindowManager.resetMainContent();

        content.removeUIPropertyChangeListener(this);
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


    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChangeEvent(evt);
    }


    protected void initComponents() {
        final JTabbedContentManager tabbedContentManager = new JTabbedContentManager();

        tabbedContentManager.addTabListener(new TabListener() {
            public void tabEventFired(TabEvent event) {
                switch (event.getActionId()) {
                    case ON_CLOSE:
                        contentManager.removeContent(
                                event.getContentManager().getComponentAt(event.getOverTabIndex())
                        );
                        break;
                    case ON_DETACH:
                        contentManager.getContent(
                                event.getContentManager().getComponentAt(event.getOverTabIndex())
                        ).setDetached(true);
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
                    ContentUI newSelected = (ContentUI) contentManager.getContent(selectedComponent);

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
            propertyChangeSupport = new PropertyChangeSupport();
            propertyChangeSupport.addPropertyChangeListener("component", new ComponentListener());
            propertyChangeSupport.addPropertyChangeListener("disabledIcon", new DisabledIconListener());
            propertyChangeSupport.addPropertyChangeListener("icon", new IconListener());
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
    }

    protected void addUIForContent(Content content) {
        if (!showAlwaysTab && tabbedContentManager.getTabCount() == 0 && (contentValueAdjusting || toolWindowManager.getMainContent() == null)) {
            toolWindowManager.setMainContent(content.getComponent());
        } else {
            if (!showAlwaysTab && tabbedContentManager.getParent() == null) {
                valueAdjusting = true;
                addTab(contentManager.getContent(toolWindowManager.getMainContent()));
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
                                    content.getToolTipText());
        int index = tabbedContentManager.getTabCount() - 1;
        tabbedContentManager.setDisabledIconAt(index, content.getDisabledIcon());
        tabbedContentManager.setPopupMenuAt(index, content.getPopupMenu());
        tabbedContentManager.setForegroundAt(index, content.getForeground());
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

                if (TransparencyManager.getInstance().isServiceAvailable()) {
                    WindowTransparencyListener windowTransparencyListener = new WindowTransparencyListener(dialog);
                    dialog.addWindowListener(windowTransparencyListener);
                    dialog.addWindowFocusListener(windowTransparencyListener);
                }

                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent event) {
                        Component component = dialog.getContentPane().getComponent(0);
                        ContentUI content = (ContentUI) contentManager.getContent(component);
                        content.fireSelected(false);
                        content.setDetached(false);
                    }
                });

                dialog.addWindowFocusListener(new WindowFocusListener() {
                    public void windowGainedFocus(WindowEvent e) {
                        if (!valueAdjusting && !contentValueAdjusting) {
                            ContentUI newSelected = (ContentUI) contentManager.getContent(
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
