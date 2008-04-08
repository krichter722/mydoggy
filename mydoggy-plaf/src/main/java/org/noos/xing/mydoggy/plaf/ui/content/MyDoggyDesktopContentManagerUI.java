package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.PersistenceDelegate;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDialog;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentFrame;
import org.noos.xing.mydoggy.plaf.ui.cmp.DesktopContentFrame;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
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
public class MyDoggyDesktopContentManagerUI extends MyDoggyContentManagerUI implements DesktopContentManagerUI,
                                                                                       PlafContentManagerUI,
                                                                                       PropertyChangeListener {
    protected JDesktopPane desktopPane;
    protected Map<Content, DesktopContentUI> detachedContentUIMap;
    protected int contentIndex = 0;
    protected JPopupMenu popupMenu;


    public MyDoggyDesktopContentManagerUI() {
        setContentManagerUI(this);
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

    public void setMinimizable(boolean minimizable) {
        boolean old = this.minimizable;
        this.minimizable = minimizable;

        if (desktopPane != null)
            for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
                DesktopContentFrame frame = (DesktopContentFrame) internalFrame;
                frame.setMinimizable(minimizable);
            }

        fireContentManagerUIProperty("minimizable", old, minimizable);
    }

    public DesktopContentUI getContentUI(Content content) {
        if (content.isDetached() || content.isMinimzed()) {
            DesktopContentUI result = detachedContentUIMap.get(content);
            if (result == null)
                result = (DesktopContentUI) getFrameByContent(content);
            return result;
        } else
            return (DesktopContentUI) getFrameByContent(content);
    }


    public PlafContentManagerUI install(ContentManagerUI oldContentManagerUI, ToolWindowManager manager) {
        // Init managers
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = manager.getContentManager();
        this.resourceManager = toolWindowManager.getResourceManager();

        this.contentIndex = 0;

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
                // Remove component
                for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
                    if (internalFrame.getContentPane().getComponent(0) == content.getComponent()) {
                        valueAdjusting = true;
                        try {
                            internalFrame.removePropertyChangeListener(contentUIListener);
                            desktopPane.remove(internalFrame);

                            // TODO: clean internalFrame...
                        } finally {
                            valueAdjusting = false;
                        }
                        break;
                    }
                }

            }
        } finally {
            // Remove listeners
            content.removePlafPropertyChangeListener(this);

            // Clean desccriptor for minimization
            toolWindowManager.removeDockableDescriptor(content.getId());

            // Remove the contentUI part
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
        if (content.isMinimzed()) {
            content.setMinimzed(false);
            content.setSelected(true);
        } else if (content.isDetached()) {
            // If the content is detached request the focus for owner window
            if (selected)
                // If the content is detached request the focus for owner window
                SwingUtil.requestFocus(
                        SwingUtilities.windowForComponent(content.getComponent())
                );
        } else {
            JInternalFrame internalFrame = getFrameByContent(content);
            if (internalFrame != null) {
                try {
                    valueAdjusting = true;

                    internalFrame.setSelected(selected);
                    lastSelected = content == lastSelected ? null : content;

                    valueAdjusting = false;
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
            } else if (selected)
                throw new IllegalStateException("Invalid content ui state.");
            else if (content == lastSelected)
                lastSelected = null;
        }
    }

    public void updateUI() {
        desktopPane.updateUI();
    }

    public void selectNextContent(Content content) {
    }

    public void propertyChange(PropertyChangeEvent evt) {
        internalPropertyChangeSupport.firePropertyChange(evt);
    }


    protected void initComponents() {
        if (desktopPane == null) {
            /// Init just once

            detachedContentUIMap = new Hashtable<Content, DesktopContentUI>();
            desktopPane = (JDesktopPane) toolWindowManager.getResourceManager().createComponent(
                    MyDoggyKeySpace.DESKTOP_CONTENT_PANE, toolWindowManager.getContext()
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
            DetachedListener detachedListener = new DetachedListener();
            internalPropertyChangeSupport.addPropertyChangeListener("detached.dispose", detachedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("detached", detachedListener);
            MaximizedListener maximizedListener = new MaximizedListener();
            internalPropertyChangeSupport.addPropertyChangeListener("maximized.before", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("maximized", maximizedListener);
            internalPropertyChangeSupport.addPropertyChangeListener("minimized", new MinimizedListener());

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
            internalFrame = new DesktopContentFrame(this, content, content.getTitle());
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
                            if (!fireContentUIRemoving((ContentUI) evt.getSource()))
                                throw new PropertyVetoException("Cannot remove.", evt);
                        }
                    }
                }
            });
            internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosed(InternalFrameEvent e) {
                    try {
                        contentManager.removeContent(((DesktopContentFrame) e.getInternalFrame()).getContent());
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
                                                lastSelected.setSelected(false);
                                        }
                                        content.setSelected(true);
                                        lastSelected = content;
                                    } else
                                        content.setSelected(false);
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

    protected JInternalFrame getFrameByContent(Content content) {
        for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
            DesktopContentFrame frame = (DesktopContentFrame) internalFrame;
            if (frame.getContent() == content)
                return internalFrame;
        }
        return null;
    }


    protected class ComponentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimzed())
                return;

            if (content.isDetached()) {
                RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.windowForComponent(content.getComponent());
                Container container = rootPaneContainer.getContentPane();
                container.removeAll();
                container.add((Component) evt.getNewValue());
            } else {
                JInternalFrame internalFrame = getFrameByContent(content);
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

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByContent(content);
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

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByContent(content);
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

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByContent(content);
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

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByContent(content);
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

            if (content.isMinimzed())
                return;

            if (content.isDetached()) {
                SwingUtil.setWindowTitle(content.getComponent(), (String) evt.getNewValue());
            } else {
                JInternalFrame internalFrame = getFrameByContent(content);
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

            if (content.isMinimzed())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getFrameByContent(content);
                if (internalFrame != null) {
                    internalFrame.setToolTipText((String) evt.getNewValue());
                } else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        }
    }

    protected class MaximizedListener implements PropertyChangeListener {
        protected ByteArrayOutputStream tmpWorkspace;
        protected Component oldFucusOwner;

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if ("maximized.before".equals(evt.getPropertyName())) {
                if ((Boolean) evt.getNewValue()) {
                    toolWindowManager.getPersistenceDelegate().save(tmpWorkspace = new ByteArrayOutputStream());
                    toolWindowManager.getToolWindowGroup().setVisible(false);

                    oldFucusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    try {
                        ((DesktopContentFrame) getContentUI(content)).setMaximum(true);
                    } catch (PropertyVetoException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            } else {
                if (!(Boolean) evt.getNewValue()) {
                    if (tmpWorkspace != null) {
                        toolWindowManager.getPersistenceDelegate().merge(new ByteArrayInputStream(tmpWorkspace.toByteArray()),
                                                                         resourceManager.getObject(PersistenceDelegate.MergePolicy.class,
                                                                                                   PersistenceDelegate.MergePolicy.UNION));
                        try {
                            ((DesktopContentFrame) getContentUI(content)).setMaximum(false);
                        } catch (PropertyVetoException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                        tmpWorkspace = null;

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

    protected class MinimizedListener implements PropertyChangeListener {

        public MinimizedListener() {
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
                JInternalFrame internalFrame = getFrameByContent(content);

                if (internalFrame != null) {
                    desktopPane.remove(internalFrame);
                    detachedContentUIMap.put(content, (DesktopContentUI) internalFrame);
                } else
                    throw new IllegalStateException("Invalid Content : " + content);

                // Put on bar
                descriptor.setAvailable(true);

                SwingUtil.repaint(desktopPane);
            } else {
                DockableDescriptor descriptor = toolWindowManager.getDockableDescriptor(content.getId());

                // Remove from bar
                descriptor.setAvailable(false);

                addUIForContent(content);
                content.setSelected(true);
            }
        }

    }

    protected class DetachedListener implements PropertyChangeListener {
        protected Frame parentFrame;

        public DetachedListener() {
            parentFrame = (toolWindowManager.getParentComponent() instanceof Frame) ? (Frame) toolWindowManager.getParentComponent() : null;
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
                    ContentUI contentUI = getContentUI(content);

                    // Remove the internal frame
                    JInternalFrame internalFrame = getFrameByContent(content);

                    Rectangle inBounds;
                    if (internalFrame != null) {
                        inBounds = toolWindowManager.getBoundsToScreen(internalFrame.getBounds(),
                                                                       desktopPane);

                        desktopPane.remove(internalFrame);
                        detachedContentUIMap.put(content, (DesktopContentUI) internalFrame);
                    } else
                        throw new IllegalStateException("Invalid Content : " + content);

                    // Setup dialog
                    Window dialog;
                    if (contentUI.isAddToTaskBarWhenDetached()) {
                        dialog = new ContentFrame(resourceManager, content, contentUI,
                                                  parentFrame, inBounds);
                    } else {
                        dialog = new ContentDialog(resourceManager, content, contentUI,
                                                   parentFrame, inBounds);
                    }
                    dialog.addWindowFocusListener(new ContentDialogFocusListener(content));
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
                    detach.putClientProperty(Content.class, content);
                    detach.setActionCommand("Detach");
                    detach.addActionListener(this);
                    detach.setEnabled(getContentUI(content).isDetachable() && !content.isDetached());
                    menu.add(detach);

                    if (content.isMaximized() || content.isMinimzed()) {
                        JMenuItem maximize = new JMenuItem();
                        maximize.putClientProperty(Content.class, content);
                        maximize.setActionCommand(content.isMaximized() ? "Maximize" : "Minimize");
                        maximize.setText(resourceManager.getString("@@tabbed.page.restore"));
                        maximize.addActionListener(this);
                        menu.add(maximize);
                    } else {
                        JMenuItem maximize = new JMenuItem();
                        maximize.putClientProperty(Content.class, content);
                        maximize.setActionCommand("Maximize");
                        maximize.setText(resourceManager.getString("@@tabbed.page.maximize"));
                        maximize.addActionListener(this);
                        menu.add(maximize);

                        JMenuItem minimize = new JMenuItem();
                        minimize.putClientProperty(Content.class, content);
                        minimize.setActionCommand("Minimize");
                        minimize.setText(resourceManager.getString("@@tabbed.page.minimize"));
                        minimize.addActionListener(this);
                        minimize.setEnabled(getContentUI(content).isMinimizable() && !content.isMinimzed());
                        menu.add(minimize);
                    }

                    popupMenu.add(menu);
                }

                popupMenu.show(desktopPane, e.getX(), e.getY());
            }
        }


        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if ("Detach".equals(actionCommand)) {
                JComponent c = (JComponent) e.getSource();

                Content content = ((Content) c.getClientProperty(Content.class));
                content.setDetached(true);
                fireContentUIDetached(getContentUI(content));
            } else if ("Maximize".equals(actionCommand)) {
                JComponent c = (JComponent) e.getSource();

                Content content = ((Content) c.getClientProperty(Content.class));
                content.setMaximized(!content.isMaximized());
            } else if ("Minimize".equals(actionCommand)) {
                JComponent c = (JComponent) e.getSource();

                Content content = ((Content) c.getClientProperty(Content.class));
                content.setMinimzed(!content.isMinimzed());
            }
        }
    }

}
