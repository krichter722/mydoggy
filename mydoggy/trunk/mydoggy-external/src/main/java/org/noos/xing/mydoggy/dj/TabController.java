package org.noos.xing.mydoggy.dj;

import javax.swing.*;
import java.util.ArrayList;

/**
 *
 * @author Jens
 */
public class TabController
{
    private MainWindow mainWindow;

    private ArrayList<JComponent> openPanels = new ArrayList<JComponent>();



    /**
     * Creates a new instance of TabController
     */
    public TabController(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
    }



    public ArrayList<JComponent> getOpenPanels()
    {
        return openPanels;
    }



    public void setOpenPanels(ArrayList<JComponent> openPanels)
    {
        this.openPanels = openPanels;
    }



    public void insertTab(String title, JComponent component)
    {
        getOpenPanels().add(component); // register Panel

        ((WindowManager) mainWindow.getWindowManager()).insertContentTab(title, component);
    }



    public void insertTab(String title, Icon icon, JComponent component)
    {
        getOpenPanels().add(component); // register Panel

        ((WindowManager) mainWindow.getWindowManager()).insertContentTab(title, icon, component);
    }



    public void selectTab(JComponent component)
    {
        ((WindowManager) mainWindow.getWindowManager()).selectContentTab(component);
    }



    public void selectTab(String panelName)
    {
        for (JComponent component : getOpenPanels())
        {
            String name = component.getName();

            if (panelName.equals(name))
            {
                ((WindowManager) mainWindow.getWindowManager()).selectContentTab(component);
                break;
            }
        }
    }



    public void removeTab(JComponent component)
    {
        // remove the component from the list of open panels
        getOpenPanels().remove(component);
    }



    public void replaceTabContent(String panelName, JComponent componentToAdd)
    {
        for (JComponent componentToRemove : getOpenPanels())
        {
            String name = componentToRemove.getName();

            if (panelName.equals(name))
            {
                ((WindowManager) mainWindow.getWindowManager()).substituteTabContent(componentToRemove, componentToAdd);
                getOpenPanels().remove(componentToRemove);
                getOpenPanels().add(componentToAdd);
                break;
            }
        }
    }
}
