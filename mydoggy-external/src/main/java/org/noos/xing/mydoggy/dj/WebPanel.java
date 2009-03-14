package org.noos.xing.mydoggy.dj;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Jens
 */
public class WebPanel extends JPanel
{
    private Browser browser;



    /** Creates new form ForumPanel */
    public WebPanel()
    {
        setLayout(new BorderLayout());
        init();

        setVisible(true);
    }



    private void init()
    {
        browser = new Browser();
        browser.setLocationBarVisible(false);
        browser.setMenuBarVisible(false);

        browser.openUrl("http://www.google.com");

        add(browser.getBrowser(), BorderLayout.CENTER);
    }
}
