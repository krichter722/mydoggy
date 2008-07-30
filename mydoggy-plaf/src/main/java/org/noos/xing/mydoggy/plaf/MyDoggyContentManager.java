package org.noos.xing.mydoggy.plaf;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.plaf.ui.content.PlafContentManagerUI;
import org.noos.xing.mydoggy.plaf.ui.util.DockableManager2ContentManagerWrapper;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Angelo De Caro (angelo.decaro@gmail.com)
 */
public class MyDoggyContentManager extends PropertyChangeEventSource implements ContentManager {
    protected MyDoggyToolWindowManager toolWindowManager;

    protected List<Content> contents;
    protected Map<Object, Content> contentMap;
    protected Map<Object, Content> aliases;
    protected PlafContentManagerUI plafContentManagerUI;

    protected EventListenerList listeners;
    protected boolean enabled;


    protected MyDoggyContentManager(MyDoggyToolWindowManager windowManager) {
        this.toolWindowManager = windowManager;
        this.contents = new ArrayList<Content>();
        this.contentMap = new HashMap<Object, Content>();
        this.aliases = new HashMap<Object, Content>();
        this.listeners = new EventListenerList();
        this.enabled = true;
    }


    public void setContentManagerUI(ContentManagerUI contentManagerUI) {
        if (!(contentManagerUI instanceof PlafContentManagerUI))
            throw new IllegalArgumentException("ContentManagerUI type not supported. See Plaf prescription.");

        if (this.plafContentManagerUI == contentManagerUI)
            return;

        if (this.plafContentManagerUI != null)
            this.plafContentManagerUI.uninstall();

        PlafContentManagerUI newContentManagerUI = (PlafContentManagerUI) contentManagerUI;
        PlafContentManagerUI old = this.plafContentManagerUI;
        this.plafContentManagerUI = newContentManagerUI;
        newContentManagerUI.install((ContentManagerUI) old, toolWindowManager);
    }

    public ContentManagerUI getContentManagerUI() {
        return (ContentManagerUI) plafContentManagerUI;
    }

    public int getContentCount() {
        return contents.size();
    }

    public Content addContent(String id, String title, Icon icon, Component component) {
        return addContent(id, title, icon, component, null);
    }

    public Content addContent(String id, String title, Icon icon, Component component, String tip) {
        return addContentInternal(id, title, icon, component, tip, null);
    }

    public Content addContent(String id, String title, Icon icon, Component component, String tip, Object... constraints) {
        return addContentInternal(id, title, icon, component, tip, null, constraints);
    }

    public Content addContent(Dockable dockable) {
        if (dockable instanceof ToolWindow) {
            toolWindowManager.removeIfDockableDelegator(dockable);

            ((MyDoggyToolWindow) dockable).setTypeInternal(ToolWindowType.EXTERN);
            Content content = addContentInternal(dockable.getId(),
                                                 dockable.getTitle(),
                                                 dockable.getIcon(),
                                                 dockable.getComponent(),
                                                 null,
                                                 (ToolWindow) dockable);
            return content;
        } else
            throw new IllegalArgumentException("Dockable not yet supported");
    }

    public void addAlias(Content content, Object alias) {
        if (contentMap.containsKey(alias))
            throw new IllegalArgumentException("There is a content whose id is the passed alias. Cannot add that alias.");
        aliases.put(alias, content);
    }

    public boolean removeContent(Content content) {
        if (content == null)
            throw new IllegalArgumentException("Content cannot be null.");

        // Deactivate content
        content.setFlashing(false);
        content.setMaximized(false);
        content.setSelected(false);

        // Remove content from ui manager
        plafContentManagerUI.removeContent((MyDoggyContent) content);

        // Remove from manager
        boolean result = contents.remove(content);
        if (result) {
            try {
                // Clean internal map
                contentMap.remove(content.getId());

                // Fire remove event
                fireContentRemoved(content);

                // Choose next content
                plafContentManagerUI.selectNextContent(content);

                // Restore the delegator
                if (content.getDockableDelegator() != null) {
                    Dockable delegator = content.getDockableDelegator();
                    if (delegator instanceof ToolWindow) {
                        ToolWindow toolWindow = (ToolWindow) delegator;
                        toolWindow.setType(ToolWindowType.DOCKED);  // TODO (-): Remember old type
                    }
                }
            } finally {
                // clean the content
                ((MyDoggyContent) content).cleanup();
            }
        }

        return result;
    }

    public boolean removeContent(int index) {
        Content content = contents.get(index);
        return removeContent(content);
    }

    public void removeAllContents() {
        for (int i = 0, size = getContentCount(); i < size; i++)
            removeContent(0);
    }

    public Content getContent(int index) {
        return contents.get(index);
    }

    public Content getContent(Object key) {
        Content content = contentMap.get(key);
        if (content == null)
            content = aliases.get(key);
        return content;
    }

    public Object[] getAliases(Content content) {
        List<Object> result = new ArrayList<Object>();
        for (Map.Entry<Object, Content> entry : aliases.entrySet()) {
            if (entry.getValue() == content)
                result.add(entry.getKey());
        }
        return result.toArray();
    }

    public Content getContentByComponent(Component component) {
        for (Content content : contents) {
            if (content.getComponent() == component)
                return content;
        }
        throw new IllegalArgumentException("Cannot found content for component. [component : " + component + ']');
    }

    public Content getSelectedContent() {
        for (Content content : contents) {
            if (content.isSelected())
                return content;
        }
        return null;
    }

    public Content getNextContent() {
        if (contents.size() == 0)
            return null;

        if (getSelectedContent() == null)
            return contents.get(0);

        int index = contents.indexOf(getSelectedContent()) + 1;
        int startIndex = index;
        do {
            if (index >= contents.size())
                index = 0;
            Content content = getContent(index);
            if (content.isEnabled())
                return content;
            index++;
        } while (index != startIndex);

        return null;
    }

    public Content getPreviousContent() {
        if (contents.size() == 0)
            return null;

        if (getSelectedContent() == null)
            return contents.get(0);

        int index = contents.indexOf(getSelectedContent()) - 1;
        int startIndex = index;
        do {
            if (index < 0)
                index = contents.size() - 1;
            Content content = getContent(index);
            if (content.isEnabled())
                return content;
            index--;
        } while (index != startIndex);

        return null;
    }

    public Content[] getContents() {
        return contents.toArray(new Content[contents.size()]);
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        plafContentManagerUI.setPopupMenu(popupMenu);
    }

    public JPopupMenu getPopupMenu() {
        if (plafContentManagerUI != null)
            return plafContentManagerUI.getPopupMenu();
        return null;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;

        boolean old = this.enabled;
        this.enabled = enabled;

        firePropertyChangeEvent("enabled", old, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void addContentManagerListener(ContentManagerListener listener) {
        listeners.add(ContentManagerListener.class, listener);
    }

    public void removeContentManagerListener(ContentManagerListener listener) {
        listeners.remove(ContentManagerListener.class, listener);
    }

    public ContentManagerListener[] getContentManagerListeners() {
        return listeners.getListeners(ContentManagerListener.class);
    }

    public void addDockableManagerListener(DockableManagerListener listener) {
        addContentManagerListener(new DockableManager2ContentManagerWrapper(listener));
    }

    public void removeDockableManagerListener(DockableManagerListener listener) {
        for (ContentManagerListener managerListener : getContentManagerListeners()) {
            if (managerListener instanceof DockableManager2ContentManagerWrapper) {
                if (((DockableManager2ContentManagerWrapper) managerListener).getListener() == listener) {
                    removeContentManagerListener(managerListener);
                }
            }
        }
    }

    public DockableManagerListener[] getDockableManagerListeners() {
        List<DockableManagerListener> listeners = new ArrayList<DockableManagerListener>();
        for (ContentManagerListener managerListener : getContentManagerListeners()) {
            if (managerListener instanceof DockableManager2ContentManagerWrapper) {
                listeners.add(((DockableManager2ContentManagerWrapper) managerListener).getListener());
            }
        }
        return listeners.toArray(new DockableManagerListener[listeners.size()]);
    }


    public void updateUI() {
        for (Content content : contents) {
            SwingUtilities.updateComponentTreeUI(content.getComponent());
            if (content.getPopupMenu() != null)
                SwingUtilities.updateComponentTreeUI(content.getPopupMenu());
        }

        if (getPopupMenu() != null)
            SwingUtilities.updateComponentTreeUI(getPopupMenu());

        plafContentManagerUI.updateUI();
    }

    public PlafContentManagerUI getPlafContentManagerUI() {
        return plafContentManagerUI;
    }


    protected void checkEnabled() {
        if (!isEnabled())
            throw new IllegalStateException("ContentManager is not enabled!. Enable it before call this method.");
    }

    protected Content addContentInternal(String id, String title, Icon icon, Component component, String tip,
                                         ToolWindow toolWindow, Object... constraints) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null.");
        if (component == null)
            throw new IllegalArgumentException("Component cannot be null.");

        if (toolWindowManager.getDockable(id) != null)
            throw new IllegalArgumentException("Cannot register content with passed id. An already registered dockable exists. [id : " + id + "]");

        MyDoggyContent content = new MyDoggyContent(this, id, title, icon, component, tip, toolWindow);
        content.addPlafPropertyChangeListener("selected", new SelectedContentPropertyChangeListener());
        content.addPlafPropertyChangeListener("maximized.before", new MaximizedBeforePropertyChangeListener());

        contents.add(content);
        contentMap.put(id, content);

        plafContentManagerUI.addContent(content, constraints);

        fireContentAdded(content);

        return content;
    }

    protected void fireContentAdded(Content content) {
        ContentManagerEvent event = new ContentManagerEvent(this, ContentManagerEvent.ActionId.CONTENT_ADDED, content);
        for (ContentManagerListener listener : listeners.getListeners(ContentManagerListener.class)) {
            listener.contentAdded(event);
        }
    }

    protected void fireContentRemoved(Content content) {
        ContentManagerEvent event = new ContentManagerEvent(this, ContentManagerEvent.ActionId.CONTENT_REMOVED, content);
        for (ContentManagerListener listener : listeners.getListeners(ContentManagerListener.class)) {
            listener.contentRemoved(event);
        }
    }

    protected void fireContentSelected(Content content) {
        ContentManagerEvent event = new ContentManagerEvent(this, ContentManagerEvent.ActionId.CONTENT_SELECTED, content);
        for (ContentManagerListener listener : listeners.getListeners(ContentManagerListener.class)) {
            listener.contentSelected(event);
        }
    }


    protected class SelectedContentPropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() instanceof Content;

            if (Boolean.TRUE.equals(evt.getNewValue()))
                fireContentSelected((Content) evt.getSource());
        }
    }

    protected class MaximizedBeforePropertyChangeListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {
            assert evt.getSource() instanceof Content;
            Content source = (Content) evt.getSource();

            if (Boolean.TRUE.equals(evt.getNewValue())) {
                for (Content content : getContents()) {
                    if (content.isMaximized() && content != source) {
                        content.setMaximized(false);
                        return;
                    }
                }
            }

        }
    }
}
