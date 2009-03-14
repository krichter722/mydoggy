/*
 * MainMenuActions.java
 *
 * Created 17. April 2007, 14:48
 *
 * by thorsten schloermann
 *
 */

package org.noos.xing.mydoggy.dj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuActions implements ActionListener
{
    private TabController tabController;

    //outbound
    JPanel outboundPanel = null;

    private WebPanel webPanel = null;

    private JPanel panelOne = null;
    private JPanel panelTwo = null;

    /** Creates a new instance of MainMenuActions */
    public MainMenuActions(TabController tabController)
    {
        this.tabController = tabController;
    }



    public void actionPerformed(ActionEvent e)
    {
        String actionCmd = e.getActionCommand();

        if (actionCmd.equals("openBrowser"))
        {
            startBrowser();
        }
        else if (actionCmd.equals("openPanelOne"))
        {
            startPanelOne();
        }
        else if (actionCmd.equals("openPanelTwo"))
        {
            startPanelTwo();
        }
    }



    private void startBrowser()
    {
        if (webPanel == null)
        {
            webPanel = new WebPanel();
            webPanel.setName("tools.webPanel");

            tabController.insertTab("Browser", webPanel);
            tabController.selectTab(webPanel);
        }
        else
        {
            tabController.selectTab("tools.webPanel");
        }
    }


    private void startPanelOne()
    {
        if (panelOne == null)
        {
            panelOne = new JPanel();
            panelOne.setLayout(new BorderLayout());
            panelOne.add(new JButton("Test"), BorderLayout.CENTER);
            panelOne.setName("panel.panelOne");

            tabController.insertTab("Panel One", panelOne);
            tabController.selectTab(panelOne);
        }
        else
        {
            tabController.selectTab("panel.panelOne");
        }
    }


    private void startPanelTwo()
    {
        if (panelTwo == null)
        {
            panelTwo = new JPanel();
            panelTwo.setLayout(new BorderLayout());
            panelTwo.add(new JButton("Test"), BorderLayout.CENTER);
            panelTwo.setName("panel.panelTwo");

            tabController.insertTab("Panel Two", panelTwo);
            tabController.selectTab(panelTwo);
        }
        else
        {
            tabController.selectTab("panel.panelTwo");
        }
    }
}