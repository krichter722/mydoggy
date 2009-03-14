package org.noos.xing.mydoggy.dj;

import org.noos.xing.mydoggy.*;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Jens
 */
public class WindowManager extends MyDoggyToolWindowManager implements ContentManagerUIListener
{
    private MainWindow mainWindow = null;

    public WindowManager(MainWindow mainWindow)
    {
        super();

        this.mainWindow = mainWindow;
        resourceManager.putProperty("dialog.owner.enabled", "true");

        //add content listener to set a frame icon
        ContentManagerUI contentUIManager = getContentManager().getContentManagerUI();

        contentUIManager.addContentManagerUIListener(this);

        ResourceManager rManager = this.getResourceManager();
        rManager.putProperty("drag.toolwindow.asTab", "false");
    }



    public void insertContentTab(String title, JComponent component)
    {
        ContentManager manager = this.getContentManager();

        Content content = manager.addContent(title, title, null, component);

        TabbedContentUI contentUI = (TabbedContentUI) content.getContentUI();

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
                    content.setSelected(true);
                }
                
                content.setSelected(true);
            }
        }
    }



    public void removeContentTab(Component component)
    {
        ContentManager manager = this.getContentManager();

        Content contents[] = manager.getContents();

        for (Content content : contents)
        {
            Component item = content.getComponent();

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
                ContentUI contentUI = contentManagerUI.getContentUI(content);

                if (content.isDetached() == true)
                {
                    //content.setComponent(componentToAdd);
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

        for(Window window : windows)
        {
            window.setIconImage(mainWindow.getIconImage());
        }
    }


    private void configureDetachability(TabbedContentUI contentUI, JComponent component)
    {
        if(component instanceof WebPanel)
        {
            configureAsUndetachable(contentUI);
        }
        else
        {
            configureAsDetachable(contentUI);
        }
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
}
