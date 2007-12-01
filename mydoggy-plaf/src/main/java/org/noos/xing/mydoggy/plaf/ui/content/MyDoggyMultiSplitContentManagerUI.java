package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.JTabbedContentPane;
import org.noos.xing.mydoggy.plaf.ui.cmp.MultiSplitTabbedContentContainer;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.TabbedContentPaneEvent;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.InputEvent;
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
public class MyDoggyMultiSplitContentManagerUI implements MultiSplitContentManagerUI, PlafContentManagerUI, PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected MyDoggyContentManager contentManager;
    protected ResourceManager resourceManager;

    protected MultiSplitContainer multiSplitContainer;
    protected boolean closeable, detachable;        // TODO: think of this...
    protected boolean installed;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;

    protected PlafContent lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, MultiSplitContentUI> contentUIMap;
    protected Map<Content, MultiSplitContentUI> detachedContentUIMap;

    protected int contentIndex = 0;

    protected JPopupMenu popupMenu;


    public MyDoggyMultiSplitContentManagerUI() {
        this.contentManagerUIListeners = new EventListenerList();
        this.contentUIMap = new Hashtable<Content, MultiSplitContentUI>();
        this.closeable = this.detachable = true;
    }


    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setCloseable(closeable);
        }
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
        for (ContentUI contentUI : contentUIMap.values()) {
            contentUI.setDetachable(detachable);
        }
    }

    public MultiSplitContentUI getContentUI(Content content) {
        return contentUIMap.get(content);
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
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = (MyDoggyContentManager) manager.getContentManager();
        this.resourceManager = toolWindowManager.getResourceManager();
        this.contentIndex = 0;

        initComponents();
        initListeners();
        setupActions();

        toolWindowManager.setMainContent(multiSplitContainer);

        setPopupMenu(contentManager.getPopupMenu());

        contentValueAdjusting = true;
        Content selectedContent = null;
        for (Content content : contentManager.getContents()) {
            if (content.isSelected())
                selectedContent = content;
            addContent((PlafContent) content);
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
        for (Content content : contentManager.getContents()) {
            removeContent((PlafContent) content);
        }
        this.installed = false;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void addContent(PlafContent content, Object... constraints) {
        addUIForContent(content, constraints);
        content.addUIPropertyChangeListener(this);
    }

    public void removeContent(PlafContent content) {
        if (content.isDetached())
            content.setDetached(false);

        multiSplitContainer.removeDockable(content);

        content.removeUIPropertyChangeListener(this);
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
        if (content.isDetached()) {
            SwingUtil.requestFocus(
                    SwingUtilities.windowForComponent(content.getComponent())
            );
        } else {
            for (Component c : multiSplitContainer.getTabbedComponents()) {
                if (c instanceof JTabbedContentPane) {
                    JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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

    public void updateUI() {
        multiSplitContainer.updateUI();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        if (multiSplitContainer == null) {
            detachedContentUIMap = new Hashtable<Content, MultiSplitContentUI>();
            multiSplitContainer = new MultiSplitContainer(toolWindowManager);
        }
    }

    protected void initListeners() {
        if (internalPropertyChangeSupport == null) {
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
        MultiSplitContentUI contentUI = new MyDoggyMultiSplitContentUI(content);
        contentUIMap.put(content, contentUI);
        contentUI.setCloseable(closeable);
        contentUI.setDetachable(detachable);

        if (constraints == null || constraints.length == 0) {
            multiSplitContainer.addDockable(content,
                                            null, content.getComponent(),
                                            null,
                                            -1, AggregationPosition.DEFAULT);
        } else {
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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
                        JTabbedContentPane tabbedContentPane = ((JTabbedContentPane)c);
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

    protected class DetachedListener implements PropertyChangeListener {
        protected Frame parentFrame;

        public DetachedListener() {
            parentFrame = (toolWindowManager.getAnchestor() instanceof Frame) ? (Frame) toolWindowManager.getAnchestor() : null;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();
            boolean oldValue = (Boolean) evt.getOldValue();
            boolean newValue = (Boolean) evt.getNewValue();

            if (!oldValue && newValue) {
                MultiSplitContentUI contentUI = getContentUI(content);
                detachedContentUIMap.put(content, contentUI);

                final JDialog dialog = new JDialog(resourceManager.getBoolean("dialog.owner.enabled", true) ?  parentFrame : null,
                                                   false);
                dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

                Window parentWindow = SwingUtilities.windowForComponent(multiSplitContainer);
                Component component = content.getComponent();

                multiSplitContainer.removeDockable(content);

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
                content.setSelected(true);
            }
        }

    }

    protected class MultiSplitContainer extends MultiSplitTabbedContentContainer {

        public MultiSplitContainer(MyDoggyToolWindowManager toolWindowManager) {
            super(toolWindowManager);
        }


        public void setRootComponent(Component component) {
            super.setRootComponent(component);    
        }


        protected Container getComponentWrapper(Dockable dockable, Component component) {
            final JTabbedContentPane tabbedPane = (JTabbedContentPane) super.getComponentWrapper(dockable, component);
            tabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
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
            });
            tabbedPane.addTabbedContentPaneListener(new TabbedContentPaneListener() {
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

            return tabbedPane;
        }

    }

}