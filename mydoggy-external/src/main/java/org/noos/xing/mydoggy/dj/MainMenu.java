/*
 * MainMenu.java
 *
 * Created 17. April 2007, 12:08
 *
 * by thorsten schloermann
 *
 */

package org.noos.xing.mydoggy.dj;

import javax.swing.*;

public class MainMenu extends JMenuBar
{
    private MainMenuActions mainMenuActions = null;

    /**
     * Creates a new instance of MainMenu
     */
    public MainMenu(MainMenuActions mainMenuActions)
    {
        this.mainMenuActions = mainMenuActions;
        add(buildToolsMenu());
        add(buildPanelOneMenu());
        add(buildPanelTwoMenu());
    }



    private JMenu buildToolsMenu()
    {
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem browserItem = new JMenuItem("Browser");
        browserItem.setActionCommand("openBrowser");
        browserItem.addActionListener(mainMenuActions);

        toolsMenu.add(browserItem);

        return toolsMenu;
    }


    
    private JMenu buildPanelOneMenu()
    {
        JMenu panelMenu = new JMenu("Panel One");

        JMenuItem panelItem = new JMenuItem("Open Panel");
        panelItem.setActionCommand("openPanelOne");
        panelItem.addActionListener(mainMenuActions);

        panelMenu.add(panelItem);

        return panelMenu;
    }


    
    private JMenu buildPanelTwoMenu()
    {
        JMenu panelMenu = new JMenu("Panel Two");

        JMenuItem panelItem = new JMenuItem("Open Panel");
        panelItem.setActionCommand("openPanelTwo");
        panelItem.addActionListener(mainMenuActions);

        panelMenu.add(panelItem);

        return panelMenu;
    }
}
