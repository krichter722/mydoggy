package org.noos.xing.mydoggy.plaf.ui.content;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ToolWindowManagerUI;
import org.noos.xing.mydoggy.plaf.ui.cmp.ContentDesktopManager;
import org.noos.xing.mydoggy.plaf.ui.cmp.DesktopContentFrame;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.ToFrontWindowFocusListener;
import org.noos.xing.mydoggy.plaf.ui.cmp.event.WindowTransparencyListener;
import org.noos.xing.mydoggy.plaf.ui.content.action.NextContentAction;
import org.noos.xing.mydoggy.plaf.ui.content.action.PreviousContentAction;
import org.noos.xing.mydoggy.plaf.ui.transparency.WindowTransparencyManager;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyDesktopContentManagerUI implements DesktopContentManagerUI, PlafContentManagerUI, PropertyChangeListener {
    protected MyDoggyToolWindowManager toolWindowManager;
    protected MyDoggyContentManager contentManager;

    protected JDesktopPane desktopPane;

    protected PropertyChangeSupport propertyChangeSupport;
    protected EventListenerList contentManagerUIListeners;

    protected PlafContentUI lastSelected;

    protected boolean valueAdjusting;
    protected boolean contentValueAdjusting;

    protected Map<Content, DesktopContentUI> detachedContentUIMap;

    protected int contentIndex = 0;

    protected JPopupMenu popupMenu;


    public MyDoggyDesktopContentManagerUI() {
    }


    public void setCloseable(boolean closeable) {
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.setClosable(closeable);
        }
    }

    public void setDetachable(boolean detachable) {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame internalFrame : frames) {
            DesktopContentFrame frame = (DesktopContentFrame) internalFrame;
            frame.setDetachable(detachable);
        }
    }

    public DesktopContentUI getContentUI(Content content) {
        if (content.isDetached()) {
            return detachedContentUIMap.get(content);
        } else
            return (DesktopContentUI) getFrameByComponent(content.getComponent());
    }


    public Container getContainer() {
        return desktopPane;
    }

    public void install(ToolWindowManager manager) {
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = (MyDoggyContentManager) manager.getContentManager();
        this.contentIndex = 0;

        initComponents();
        initListeners();
        setupActions();

        toolWindowManager.setMainContent(desktopPane);

        setPopupMenu(contentManager.getPopupMenu());

        contentValueAdjusting = true;
        for (Content content : contentManager.getContents()) {
            addContent((PlafContentUI) content);
        }
        contentValueAdjusting = false;

        if (contentManager.getContentCount() > 0) {
            contentManager.getContent(0).setSelected(true);
        }
    }

    public void unistall() {
        for (Content content : contentManager.getContents()) {
            removeContent((PlafContentUI) content);
        }
    }

    public void addContent(PlafContentUI content) {
        addUIForContent(content);
        content.addUIPropertyChangeListener(this);
    }

    public void removeContent(PlafContentUI content) {
        if (content.isDetached())
            content.setDetached(false);

        for (JInternalFrame internalFrame : desktopPane.getAllFrames()) {
            if (internalFrame.getContentPane().getComponent(0) == content.getComponent()) {
                desktopPane.remove(internalFrame);
                break;
            }
        }

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
            JInternalFrame internalFrame = getFrameByComponent(content.getComponent());
            if (internalFrame != null)
                try {
                    valueAdjusting = true;
                    internalFrame.setSelected(selected);
                    lastSelected = (PlafContentUI) content;
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
        if (desktopPane == null) {
            detachedContentUIMap = new Hashtable<Content, DesktopContentUI>();
            desktopPane = (JDesktopPane) toolWindowManager.getToolWindowManagerUI().createComponent(
                    ToolWindowManagerUI.DESKTOP_CONTENT_PANE, toolWindowManager
            );
        }
    }

    protected void initListeners() {
        if (propertyChangeSupport == null) {
            propertyChangeSupport = new PropertyChangeSupport(this);
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

            desktopPane.addMouseListener(new PopupMouseListener());
        }
        this.contentManagerUIListeners = new EventListenerList();
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

    protected void addUIForContent(Content content) {
        JInternalFrame internalFrame = (JInternalFrame) detachedContentUIMap.get(content);
        if (internalFrame == null) {
            internalFrame = new DesktopContentFrame(content, content.getTitle(), true, true, true, true);
            internalFrame.setFrameIcon(content.getIcon());

            internalFrame.getContentPane().add(content.getComponent());

            int contentX;
            int contentY;

            contentY = contentX = 10 + (contentIndex++ * 25);
            if (contentX > desktopPane.getWidth() - 320 || contentY > desktopPane.getHeight() - 200) {
                contentIndex = 0;
                contentY = contentX = 10;
            }

            internalFrame.setBounds(contentX, contentY, 320, 200);

            internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameClosed(InternalFrameEvent e) {
                    try {
                        Content content = contentManager.getContentByComponent(e.getInternalFrame().getContentPane().getComponent(0));
                        fireContentUIRemoving(getContentUI(content));
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
                                        lastSelected = (PlafContentUI) content;
                                    }
                                    ((PlafContentUI) content).fireSelected((Boolean) evt.getNewValue());
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

    class DisabledIconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
        }
    }

    class IconListener implements PropertyChangeListener {
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

    class EnabledListener implements PropertyChangeListener {
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

    class ForegroundListener implements PropertyChangeListener {
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

    class PopupMenuListener implements PropertyChangeListener {
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

    class TitleListener implements PropertyChangeListener {
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

    class ToolTipTextListener implements PropertyChangeListener {
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

                if (WindowTransparencyManager.getInstance().isServiceAvailable()) {
                    WindowTransparencyListener windowTransparencyListener = new WindowTransparencyListener(
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


    class PopupMouseListener extends MouseAdapter implements ActionListener {
        private JPopupMenu popupMenu;

        public PopupMouseListener() {
            popupMenu = new JPopupMenu();
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                popupMenu.removeAll();

                for (Content content : contentManager.getContents()) {
                    JMenu menu = new JMenu(content.getTitle());

                    JMenuItem detach = new JMenuItem("Detach");
                    detach.putClientProperty("content", content);
                    detach.setActionCommand("Detach");
                    detach.addActionListener(this);
                    detach.setEnabled(getContentUI(content).isDetachable() && !content.isDetached());

                    menu.add(detach);

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
            }
        }
    }

}
