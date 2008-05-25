package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.DockableDescriptor;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDialog;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentFrame;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyDesktopContentManagerUI extends MyDoggyContentManagerUI<DesktopContentUI> implements DesktopContentManagerUI,
                                                                                                         PlafContentManagerUI {
    protected JDesktopPane desktopPane;
    protected int contentIndex = 0;
    protected JPopupMenu popupMenu;


    public MyDoggyDesktopContentManagerUI() {
        setContentManagerUI(this);
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
        lastSelected = null;
        Content selectedContent = null;

        contentValueAdjusting = true;
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
            for (Content content : contentManager.getContents()) {
                removeContent((PlafContent) content);
            }
            contentValueAdjusting = false;

            // Now you can consider this manager uninstalled
            this.installed = false;
        } finally {
            uninstalling = false;
        }
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public synchronized void setSelected(Content content, boolean selected) {
        if (selected) {
            if (lastSelected != null)
               lastSelected.setSelected(false);

            if (content.isMinimized()) {
                content.setMinimized(false);
                content.setSelected(true);
            } else if (content.isDetached()) {
                // If the content is detached request the focus for owner window
                SwingUtil.requestFocus(
                        SwingUtilities.windowForComponent(content.getComponent())
                );
            } else {
                JInternalFrame internalFrame = getInternalFrame(content);
                if (internalFrame != null) {
                    try {
                        valueAdjusting = true;

                        internalFrame.setSelected(selected);
                        lastSelected = content == lastSelected ? null : content;

                        valueAdjusting = false;
                    } catch (PropertyVetoException e) {
                        e.printStackTrace();
                    }
                } else
                    throw new IllegalStateException("Invalid content ui state.");
            }
        } else {
            if (content == lastSelected)
                lastSelected = null;
        }

    }

    public void updateUI() {
        desktopPane.updateUI();
    }

    public void selectNextContent(Content content) {
    }


    protected void initComponents() {
        if (desktopPane == null) {
            /// Init just once
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

    protected Object addUIForContent(final Content content, Object... constraints) {
        DesktopContentUI desktopContentUI = contentUIMap.get(content);
        JInternalFrame internalFrame;

        if (desktopContentUI == null) {
            MyDoggyDesktopContentUI myDoggyDesktopContentUI = new MyDoggyDesktopContentUI(contentManager,
                                                                                          this,
                                                                                          content);

            internalFrame = myDoggyDesktopContentUI.getInternalFrame();

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

            contentUIMap.put(content, myDoggyDesktopContentUI);
        } else {
            internalFrame = ((MyDoggyDesktopContentUI) desktopContentUI).getInternalFrame();
            internalFrame.getContentPane().add(content.getComponent());
        }

        desktopPane.add(internalFrame);
        contentValueAdjusting = true;
        try {
            internalFrame.show();
            internalFrame.toFront();
        } finally {
            contentValueAdjusting = false;
        }

        if (content.isSelected()) {
            try {
                internalFrame.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected void removeUIForContent(Content content) {
        // Remove component
        MyDoggyDesktopContentUI desktopContentUI = (MyDoggyDesktopContentUI) content.getContentUI();
        JInternalFrame internalFrame = desktopContentUI.getInternalFrame();

        valueAdjusting = true;
        try {
            internalFrame.removePropertyChangeListener(contentUIListener);
            desktopPane.remove(internalFrame);
            desktopContentUI.cleanup();
        } finally {
            valueAdjusting = false;
        }
    }

    protected JInternalFrame getInternalFrame(Content content) {
        return ((MyDoggyDesktopContentUI) content.getContentUI()).getInternalFrame();
    }


    protected class ComponentListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (content.isMinimized())
                return;

            if (content.isDetached()) {
                RootPaneContainer rootPaneContainer = (RootPaneContainer) SwingUtilities.windowForComponent(content.getComponent());
                Container container = rootPaneContainer.getContentPane();
                container.removeAll();
                container.add((Component) evt.getNewValue());
            } else {
                JInternalFrame internalFrame = getInternalFrame(content);
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

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getInternalFrame(content);
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

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getInternalFrame(content);
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

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getInternalFrame(content);
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

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getInternalFrame(content);
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

            if (content.isMinimized())
                return;

            if (content.isDetached()) {
                SwingUtil.setWindowTitle(content.getComponent(), (String) evt.getNewValue());
            } else {
                JInternalFrame internalFrame = getInternalFrame(content);
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

            if (content.isMinimized())
                return;

            if (!content.isDetached()) {
                JInternalFrame internalFrame = getInternalFrame(content);
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
                        ((MyDoggyDesktopContentUI) getContentUI(content)).getInternalFrame().setMaximum(true);
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
                            ((MyDoggyDesktopContentUI) getContentUI(content)).getInternalFrame().setMaximum(false);
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
                JInternalFrame internalFrame = getInternalFrame(content);

                if (internalFrame != null) {
                    desktopPane.remove(internalFrame);
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

        public DetachedListener() {
        }

        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if ("detached.dispose".equals(evt.getPropertyName())) {
                Window window = SwingUtilities.windowForComponent(content.getComponent());
                window.setVisible(false);
                window.dispose();
            } else {
                if ((Boolean) evt.getNewValue()) {
                    ContentUI contentUI = getContentUI(content);

                    // Remove the internal frame
                    JInternalFrame internalFrame = getInternalFrame(content);

                    Rectangle inBounds;
                    if (internalFrame != null) {
                        inBounds = toolWindowManager.getBoundsToScreen(internalFrame.getBounds(),
                                                                       desktopPane);

                        desktopPane.remove(internalFrame);
                    } else
                        throw new IllegalStateException("Invalid Content : " + content);

                    // Setup dialog
                    Frame parentFrame = (toolWindowManager.getWindowAnchestor() instanceof Frame) ? (Frame) toolWindowManager.getWindowAnchestor() : null;

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
                } else {
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
            if (SwingUtilities.isRightMouseButton(e) && isPopupMenuEnabled()) {                
                popupMenu.removeAll();

                for (Content content : contentManager.getContents()) {
                    JMenu menu = new JMenu(content.getTitle());

                    // TODO: use new isMaximizable...
                    JMenuItem detach = new JMenuItem(resourceManager.getString("@@tab.content.detach"));
                    detach.putClientProperty(Content.class, content);
                    detach.setActionCommand("Detach");
                    detach.addActionListener(this);
                    detach.setEnabled(getContentUI(content).isDetachable() && !content.isDetached());
                    menu.add(detach);

                    if (content.isMaximized() || content.isMinimized()) {
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
                        minimize.setEnabled(getContentUI(content).isMinimizable() && !content.isMinimized());
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
                content.setMinimized(!content.isMinimized());
            }
        }
    }

}
