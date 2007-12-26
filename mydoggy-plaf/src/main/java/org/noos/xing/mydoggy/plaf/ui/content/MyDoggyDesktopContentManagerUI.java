package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.PersistenceDelegate;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.DesktopContentFrame;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyDesktopContentManagerUI implements DesktopContentManagerUI, PlafContentManagerUI, PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected MyDoggyContentManager contentManager;
    protected ResourceManager resourceManager;

    protected JDesktopPane desktopPane;
    protected boolean closeable, detachable;
    protected boolean installed;

    protected PropertyChangeSupport internalPropertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;

    protected PlafContent lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, DesktopContentUI> detachedContentUIMap;

    protected int contentIndex = 0;

    protected JPopupMenu popupMenu;


    public MyDoggyDesktopContentManagerUI() {
        this.contentManagerUIListeners = new EventListenerList();
        this.closeable = this.detachable = true;
    }


    public void setCloseable(boolean closeable) {
        boolean old = this.closeable;
        this.closeable = closeable;

        if (desktopPane != null)
            for (JInternalFrame frame : desktopPane.getAllFrames()) {
                frame.setClosable(closeable);
            }

        fireContentManagerUIProperty("closeable", old, closeable);
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setDetachable(boolean detachable) {
        boolean old = this.detachable;
        this.detachable = detachable;

        if (desktopPane != null)
            for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
                DesktopContentFrame frame = (DesktopContentFrame) internalFrame;
                frame.setDetachable(detachable);
            }

        fireContentManagerUIProperty("detachable", old, detachable);
    }

    public boolean isDetachable() {
        return detachable;
    }

    public DesktopContentUI getContentUI(Content content) {
        if (content.isDetached()) {
            return detachedContentUIMap.get(content);
        } else
            return (DesktopContentUI) getFrameByComponent(content.getComponent());
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

        this.contentIndex = 0;

        if (oldContentManagerUI != null) {
            // Import properties from the old ContentManagerUI
            this.closeable = oldContentManagerUI.isCloseable();
            this.detachable = oldContentManagerUI.isDetachable();
        }
        // Import properties from the ContentManager
        setPopupMenu(contentManager.getPopupMenu());

        // Init Components
        initComponents();

        // Init Listeners
        initListeners();

        // Set the main content with the desktopPane
        toolWindowManager.setMainContent(desktopPane);

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
            // Import listeners from the old ContentManagerUI
            for (ContentManagerUIListener listener : oldContentManagerUI.getContentManagerUiListener()) {
                addContentManagerUIListener(listener);
            }
/*
            for (PropertyChangeListener listener : oldContentManagerUI.getPropertyChangeListeners()) {
                addPropertyChangeListener(listener);
            }
*/
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

        // Remove from desktopPane
        for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
            if (internalFrame.getContentPane().getComponent(0) == content.getComponent()) {
                desktopPane.remove(internalFrame);
                break;
            }
        }

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
        if (content.isDetached()) {
            // If the content is detached request the focus for owner window
            if (selected)
                // If the content is detached request the focus for owner window
                SwingUtil.requestFocus(
                        SwingUtilities.windowForComponent(content.getComponent())
                );
        } else {
            JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
            if (internalFrame != null)
                try {
                    valueAdjusting = true;

                    internalFrame.setSelected(selected);
                    lastSelected = (PlafContent) content;

                    valueAdjusting = false;
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            else
                throw new IllegalStateException("Invalid content ui state.");
        }
    }

    public void updateUI() {
        desktopPane.updateUI();
    }


    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        if (desktopPane == null) {
            /// Init just once

            detachedContentUIMap = new Hashtable<Content, DesktopContentUI>();
            desktopPane = (JDesktopPane) toolWindowManager.getResourceManager().createComponent(
                    MyDoggyKeySpace.DESKTOP_CONTENT_PANE, toolWindowManager
            );
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
            internalPropertyChangeSupport.addPropertyChangeListener("enabled", new EnabledListener());
            internalPropertyChangeSupport.addPropertyChangeListener("foreground", new ForegroundListener());
            internalPropertyChangeSupport.addPropertyChangeListener("popupMenu", new PopupMenuListener());
            internalPropertyChangeSupport.addPropertyChangeListener("title", new TitleListener());
            internalPropertyChangeSupport.addPropertyChangeListener("toolTipText", new ToolTipTextListener());
            internalPropertyChangeSupport.addPropertyChangeListener("detached", new DetachedListener());
            internalPropertyChangeSupport.addPropertyChangeListener("maximized", new MaximizedListener());

            desktopPane.addMouseListener(new PopupMouseListener());
        }
    }

    protected void setupActions() {
        // Setup actions
        SwingUtil.addKeyActionMapping(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, desktopPane,
                KeyStroke.getKeyStroke(39, InputEvent.ALT_MASK),
                "nextContent", new NextContentAction(toolWindowManager));
        SwingUtil.addKeyActionMapping(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, desktopPane,
                KeyStroke.getKeyStroke(37, InputEvent.ALT_MASK),
                "previousContent", new PreviousContentAction(toolWindowManager));
    }

    protected void addUIForContent(Content content, Object... constraints) {
        JInternalFrame internalFrame = (JInternalFrame) detachedContentUIMap.get(content);

        if (internalFrame == null) {
            internalFrame = new DesktopContentFrame(content, content.getTitle(), true, true, true, true);
            internalFrame.setFrameIcon(content.getIcon());
            internalFrame.setClosable(closeable);
            ((DesktopContentFrame) internalFrame).setDetachable(detachable);

            internalFrame.getContentPane().add(content.getComponent());

            // Parse constraints
            if (constraints.length > 0) {
                if (constraints[0] instanceof Point) {
                    Point location = (Point) constraints[0];

                    internalFrame.setBounds(location.x, location.y, 320, 200);
                } else if (constraints[0] instanceof Rectangle) {
                    internalFrame.setBounds((Rectangle) constraints[0]);
                } else
                    constraints = null;
            }

            if (constraints == null || constraints.length == 0) {
                int contentX, contentY;
                contentY = contentX = 10 + (contentIndex++ * 25);
                if (contentX > desktopPane.getWidth() - 320 || contentY > desktopPane.getHeight() - 200) {
                    contentIndex = 0;
                    contentY = contentX = 10;
                }
                internalFrame.setBounds(contentX, contentY, 320, 200);
            }


            internalFrame.addVetoableChangeListener(new VetoableChangeListener() {
                public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                    if (JInternalFrame.IS_CLOSED_PROPERTY.equals(evt.getPropertyName())) {
                        if (Boolean.TRUE.equals(evt.getNewValue())) {
                            Content content = contentManager.getContentByComponent(
                                    ((JInternalFrame) evt.getSource()).getContentPane().getComponent(0)
                            );
                            if (!fireContentUIRemoving(getContentUI(content)))
                                throw new PropertyVetoException("Cannot remove.", evt);
                        }
                    }
                }
            });
            internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosed(InternalFrameEvent e) {
                    try {
                        Content content = contentManager.getContentByComponent(e.getInternalFrame().getContentPane().getComponent(0));
                        contentManager.removeContent(content);
                    } catch (Exception ignore) {
                        ignore.printStackTrace();
                    }
                }
            });
            internalFrame.addPropertyChangeListener(JInternalFrame.IS_SELECTED_PROPERTY, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (!valueAdjusting && !contentValueAdjusting) {

                        Container container = ((JInternalFrame) evt.getSource()).getContentPane();
                        if (container.getComponentCount() > 0) {
                            Component cmp = container.getComponent(0);
                            for (Content content : contentManager.getContents()) {
                                if (content.getComponent() == cmp) {
                                    boolean value = (Boolean) evt.getNewValue();
                                    if (value) {
                                        if (lastSelected != null) {
                                            if (lastSelected.isDetached())
                                                lastSelected.fireSelected(false);
                                        }
                                        lastSelected = (PlafContent) content;
                                    }
                                    ((PlafContent) content).fireSelected((Boolean) evt.getNewValue());
                                    break;
                                }
                            }
                        }

                    }
                }
            });
        } else {
            internalFrame.getContentPane().add(content.getComponent());
        }

        desktopPane.add(internalFrame);
        internalFrame.show();
        internalFrame.toFront();

        if (content.isSelected())
            try {
                internalFrame.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }

    }

    protected JInternalFrame getFrameByComponent(Component component) {
        for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
            if (internalFrame.getContentPane().getComponent(0) == component)
                return internalFrame;
        }
        return null;
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
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null) {
                    Container container = internalFrame.getContentPane();
                    container.removeAll();
                    container.add((Component) evt.getNewValue());
                } else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class DisabledIconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    protected class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null)
                    internalFrame.setFrameIcon((Icon) evt.getNewValue());
                else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class EnabledListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null) {
                    internalFrame.setEnabled((Boolean) evt.getNewValue());
                } else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null)
                    internalFrame.setForeground((Color) evt.getNewValue());
                else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class PopupMenuListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null)
                    internalFrame.setComponentPopupMenu((JPopupMenu) evt.getNewValue());
                else
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
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null)
                    internalFrame.setTitle((String) evt.getNewValue());
                else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
                if (internalFrame != null) {
                    String newToolTip = (String) evt.getNewValue();
                    if (newToolTip == null)
                        newToolTip = "";

                    internalFrame.setToolTipText(newToolTip);
                } else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class MaximizedListener implements PropertyChangeListener {
        protected ByteArrayOutputStream tmpWorkspace;

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if ((Boolean) evt.getNewValue()) {
                toolWindowManager.getPersistenceDelegate().save(tmpWorkspace = new ByteArrayOutputStream());
                toolWindowManager.getToolWindowGroup().setVisible(false);
                try {
                    ((DesktopContentFrame) getContentUI(content)).setMaximum(true);
                } catch (PropertyVetoException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            } else {
                if (tmpWorkspace != null) {
                    toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                            PersistenceDelegate.MergePolicy.UNION);
                    try {
                        ((DesktopContentFrame) getContentUI(content)).setMaximum(false);
                    } catch (PropertyVetoException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    tmpWorkspace = null;
                }
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
                final JDialog dialog = new JDialog(resourceManager.getBoolean("dialog.owner.enabled", true) ? parentFrame : null,
                        false);
                dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

                Window parentWindow = SwingUtilities.windowForComponent(desktopPane);
                Component component = content.getComponent();

                JInternalFrame internalFrame = getFrameByComponent(component);
                if (internalFrame != null) {
                    desktopPane.remove(internalFrame);
                    detachedContentUIMap.put(content, (DesktopContentUI) internalFrame);
                } else
                    throw new IllegalStateException("Invalid Content : " + content);

                component.setPreferredSize(component.getSize());

                dialog.setTitle(content.getTitle());
                dialog.getContentPane().add(component);

                Point location = parentWindow.getLocation();
                location.x += 5;
                location.y += 5;
                dialog.setLocation(location);

                dialog.pack();

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
                                    getFrameByComponent(lastSelected.getComponent()).setSelected(false);
//                                    lastSelected.fireSelected(false);
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
                SwingUtil.repaint(desktopPane);
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


    protected class PopupMouseListener extends MouseAdapter implements ActionListener {
        protected JPopupMenu popupMenu;

        public PopupMouseListener() {
            popupMenu = new JPopupMenu();
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                popupMenu.removeAll();

                for (Content content : contentManager.getContents()) {
                    JMenu menu = new JMenu(content.getTitle());

                    JMenuItem detach = new JMenuItem(resourceManager.getString("@@tab.content.detach"));
                    detach.putClientProperty("content", content);
                    detach.setActionCommand("Detach");
                    detach.addActionListener(this);
                    detach.setEnabled(getContentUI(content).isDetachable() && !content.isDetached());
                    menu.add(detach);

                    JMenuItem maximize = new JMenuItem();
                    maximize.putClientProperty("content", content);
                    maximize.setActionCommand("Maximize");
                    maximize.setText(content.isMaximized() ?
                            resourceManager.getString("@@tabbed.page.restore") :
                            resourceManager.getString("@@tabbed.page.maximize")
                    );
                    maximize.addActionListener(this);
                    menu.add(maximize);

                    popupMenu.add(menu);
                }

                popupMenu.show(desktopPane, e.getX(), e.getY());
            }
        }


        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("Detach".equals(actionCommand)) {
                JComponent c = (JComponent) e.getSource();

                Content content = ((Content) c.getClientProperty("content"));
                content.setDetached(true);
                fireContentUIDetached(getContentUI(content));
            } else if ("Maximize".equals(actionCommand)) {
                JComponent c = (JComponent) e.getSource();

                Content content = ((Content) c.getClientProperty("content"));
                content.setMaximized(!content.isMaximized());
            }
        }
    }

}
