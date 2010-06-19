
package org.noos.xing.mydoggy.mydoggytest;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerEvent;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Jens
 */
public class WindowManager extends MyDoggyToolWindowManager implements ContentManagerUIListener
{
    public WindowManager()
    {
        super();
        resourceManager.putProperty("dialog.owner.enabled", "true");

        //add content listener to set a frame icon
        ContentManagerUI contentUIManager = getContentManager().getContentManagerUI();

        contentUIManager.addContentManagerUIListener(this);

        ResourceManager rManager = this.getResourceManager();
        rManager.putProperty("drag.toolwindow.asTab", "false");

        ContentManager manager = this.getContentManager();
        manager.addContentManagerListener(new ManagerListener());
    }



    public void insertContentTab(String title, JComponent component)
    {
        System.err.println("insertContentTab: " + component);

        ContentManager manager = this.getContentManager();

        //Content content = manager.addContent(title, title, null, component);
        Content content = manager.addContent(title, title, null, component);
        //content.addPropertyChangeListener(new Listener());

        TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();

        //contentUI.addPropertyChangeListener(new Listener());

        configureDetachability(contentUI, component);
    }



    public void insertContentTab(String title, Icon icon, JComponent component)
    {
        ContentManager manager = this.getContentManager();
        Content content = manager.addContent(title, title, icon, component);

        TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();
        // without the need of the cast

        configureDetachability(contentUI, component);
    }



    public void selectContentTab(JComponent component)
    {
        ContentManager manager = this.getContentManager();

        Content contents[] = manager.getContents();

        for (Content content : contents)
        {
            Component item = content.getComponent();

            if (item.equals(component))
            {
                if (content.isMinimized() == true)
                {
                    //content.setMinimized(false);
                    content.setMaximized(true);
                }
                else
                {
                    //content.setSelected(true);
                }

                //item.setVisible(true);
                content.setSelected(true);
            }
            else
            {
                //item.setVisible(false);
            }
        }
    }


    public void pinContentTab(JComponent component)
    {
        System.out.println("#### in pinContentTab");

        ContentManager manager = this.getContentManager();

        Content contents[] = manager.getContents();

        for (Content content : contents)
        {
            Component item = content.getComponent();

            if (item.equals(component))
            {
                TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();

                configureAsUntouchable(contentUI);
                selectContentTab(component);
            }
        }
    }


    public void unPinContentTab(JComponent component)
    {
        ContentManager manager = this.getContentManager();

        Content contents[] = manager.getContents();

        for (Content content : contents)
        {
            Component item = content.getComponent();

            if (item.equals(component))
            {
                TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();

                configureDetachability(contentUI, component);
                selectContentTab(component);
            }
        }
    }




    public void removeContentTab(Component component)
    {
        System.err.println("remove content Tab: " + component);

        ContentManager manager = this.getContentManager();

        Content contents[] = manager.getContents();

        for (Content content : contents)
        {
            Component item = content.getComponent();

            System.err.println("item: " + item);

            if (item.equals(component))
            {
                manager.removeContent(content);
            }
        }
    }



    public void substituteTabContent(JComponent componentToRemove, JComponent componentToAdd)
    {
        ContentManager manager = this.getContentManager();
        ContentManagerUI contentManagerUI = manager.getContentManagerUI();

        Content contents[] = manager.getContents();

        for (Content content : contents)
        {
            Component item = content.getComponent();

            if (item.equals(componentToRemove))
            {
                if(componentToAdd.getName() == null)
                {
                    componentToAdd.setName(componentToRemove.getName());
                }

                if (content.isDetached() == true)
                {
                    content.setComponent(componentToAdd);
                }
                else
                {
                    content.setComponent(componentToAdd);
                }
            }
        }
    }



    public boolean contentUIRemoving(ContentManagerUIEvent arg0)
    {
        return true;
    }



    public void contentUIDetached(ContentManagerUIEvent arg0)
    {
        Window windows[] = Frame.getWindows();

        for (Window window : windows)
        {
            //window.setIconImage();
        }
    }




    private void configureDetachability(TabbedContentUI contentUI, JComponent component)
    {
//        if (component instanceof NotDetachableInterface)
//        {
//            configureAsUndetachable(contentUI);
//        }
//        else
//        {
            configureAsDetachable(contentUI);
//        }
    }



    private void configureAsUndetachable(TabbedContentUI contentUI)
    {
        contentUI.setAlwaysOnTop(false);
        contentUI.setAddToTaskBarWhenDetached(true);
        contentUI.setCloseable(true);
        contentUI.setDetachable(false);
        contentUI.setMinimizable(false);
        contentUI.setMaximizable(false);
        contentUI.setTransparentMode(false);
    }



    private void configureAsDetachable(TabbedContentUI contentUI)
    {
        contentUI.setAlwaysOnTop(false);
        contentUI.setAddToTaskBarWhenDetached(true);
        contentUI.setCloseable(true);
        contentUI.setDetachable(true);
        contentUI.setMinimizable(false);
        contentUI.setMaximizable(false);
        contentUI.setTransparentMode(false);
    }



    private void configureAsUntouchable(TabbedContentUI contentUI)
    {
        contentUI.setAlwaysOnTop(false);
        contentUI.setAddToTaskBarWhenDetached(false);
        contentUI.setCloseable(false);
        contentUI.setDetachable(false);
        contentUI.setMinimizable(false);
        contentUI.setMaximizable(false);
        contentUI.setTransparentMode(false);
    }
}



class Listener implements PropertyChangeListener
{
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        System.out.println("### property changed: " + evt.getPropertyName());

        System.out.println("### value: " + evt.getNewValue());
        System.out.println("### source: " + evt.getSource());
    }
}


class ManagerListener implements ContentManagerListener
{
    @Override
    public void contentAdded(ContentManagerEvent arg0)
    {
        //System.out.println("content added: " + arg0.getContent().getComponent().getName());
        //Main.getStatusBarManager().enableByName(arg0.getContent().getComponent().getName());
    }



    @Override
    public void contentRemoved(ContentManagerEvent arg0)
    {
        //System.out.println("content removed: " + arg0.getContent().getComponent().getName());
        //Main.getStatusBarManager().disableByName(arg0.getContent().getComponent().getName());
    }



    @Override
    public void contentSelected(ContentManagerEvent arg0)
    {
        String name = arg0.getContent().getComponent().getName();

        if(name == null || name.isEmpty())
        {
            return;
        }

        //EventBus.publish(new ModuleSelectionEvent(arg0.getContent().getComponent().getName()));
        //System.out.println("content selected: " + arg0.getContent().getComponent().getName());
        //Main.getStatusBarManager().enableByName(arg0.getContent().getComponent().getName());
    }
}
