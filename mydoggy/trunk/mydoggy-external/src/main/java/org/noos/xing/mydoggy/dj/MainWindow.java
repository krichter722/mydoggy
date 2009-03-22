package org.noos.xing.mydoggy.dj;

import org.noos.xing.mydoggy.ContentManagerUIListener;
import org.noos.xing.mydoggy.TabbedContentManagerUI;
import org.noos.xing.mydoggy.event.ContentManagerUIEvent;
import org.noos.xing.mydoggy.plaf.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Jens
 */
public class MainWindow extends JFrame
{
    private TabController tabController;

    private WindowManager windowManager = null;

    private MainMenu mainMenu;

    public MainWindow()
    {
        windowManager = new WindowManager(this);
        tabController = new TabController(this);
        mainMenu      = new MainMenu(new MainMenuActions(tabController));

        setJMenuBar(mainMenu);
        
        //setupToolwindow();
        setupContentManagerUI();

        getContentPane().add((JComponent) windowManager, BorderLayout.CENTER);

        // get dimension of screen
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        // set mainWindow to screen size
        setSize(d.width, d.height);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(800,600);
        SwingUtil.centrePositionOnScreen(this);
        
        setVisible(true);
    }



    protected void setupContentManagerUI()
    {
        // By default a TabbedContentManagerUI is installed.
        TabbedContentManagerUI contentManagerUI = (TabbedContentManagerUI) windowManager.getContentManager().getContentManagerUI();

        contentManagerUI.setShowAlwaysTab(true);
        contentManagerUI.setTabPlacement(TabbedContentManagerUI.TabPlacement.TOP);
        contentManagerUI.setTabLayout(TabbedContentManagerUI.TabLayout.WRAP);
        contentManagerUI.setDetachable(true);

        contentManagerUI.addContentManagerUIListener(new ContentManagerUIListener()
        {
            public boolean contentUIRemoving(ContentManagerUIEvent event)
            {
                Component component = event.getContentUI().getContent().getComponent();

                if (component != null)
                {
                    tabController.removeTab((JComponent) component);
                }

                return true;
            }



            public void contentUIDetached(ContentManagerUIEvent event)
            {
            }
        });
    }



    /**
     * @return the windowManager
     */
    public WindowManager getWindowManager()
    {
        return windowManager;
    }



    /**
     * @param windowManager the windowManager to set
     */
    public void setWindowManager(WindowManager windowManager)
    {
        this.windowManager = windowManager;
    }
}
