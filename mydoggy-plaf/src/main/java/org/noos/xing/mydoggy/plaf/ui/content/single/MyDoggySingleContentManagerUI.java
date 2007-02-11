package org.noos.xing.mydoggy.plaf.ui.content.single;

import org.noos.xing.mydoggy.Content;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.ContentUI;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyContentManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.content.BackContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.content.BackContentUI;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.component.JTabbedContentManager;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.component.TabEvent;
import org.noos.xing.mydoggy.plaf.ui.content.tabbed.component.TabListener;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 * @todo
 */
public class MyDoggySingleContentManagerUI implements ContentManagerUI, BackContentManagerUI, PropertyChangeListener {
    private MyDoggyToolWindowManager toolWindowManager;
    private MyDoggyContentManager contentManager;

    private PropertyChangeSupport propertyChangeSupport;

    private BackContentUI lastSelected;

    boolean valueAdjusting;
    boolean contentValueAdjusting;


    public MyDoggySingleContentManagerUI() {
        initComponents();
    }


    public void setCloseable(boolean closeable) {
    }

    public void setDetachable(boolean detachable) {
    }

	public ContentUI getContentUI(Content content) {
		return null;
	}


    public void install(ToolWindowManager manager) {
        this.toolWindowManager = (MyDoggyToolWindowManager) manager;
        this.contentManager = (MyDoggyContentManager) manager.getContentManager();
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
        }
    }

    public JPopupMenu getPopupMenu() {
        return null;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
    }

    public void updateUI() {
    }


    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
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
                    BackContentUI newSelected = (BackContentUI) contentManager.getContent(selectedComponent);

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
            propertyChangeSupport.addPropertyChangeListener("selected", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    //                System.out.println("SELECTED " + evt.getNewValue());
                }
            });
        }
    }

    protected void addUIForContent(Content content) {
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
            }
        }
    }

    class DisabledIconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
            }
        }
    }

    class IconListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
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
            }
        }
    }

    class ForegroundListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
            }
        }
    }

    class PopupMenuListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
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
            }
        }
    }

    class ToolTipTextListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            Content content = (Content) evt.getSource();

            if (!content.isDetached()) {
            }
        }
    }

}
